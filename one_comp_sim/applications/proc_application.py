"""Processing application -- faithful Python 3 port of Java ProcApplication.java.

This is the core store-process-deplete logic running on each Edge Data
Repository (EDR).  Every incoming message is stored; processing messages are
scheduled onto virtual cores with freshness/shelf-life awareness; processed
and stale messages are depleted to the cloud or dropped according to a
multi-threshold bandwidth-limited pipeline.

The control flow mirrors the original Java implementation line-for-line so
that simulation results remain comparable.  Variable names have been made
more Pythonic but a mapping comment references the original Java field where
clarity is needed.
"""

from __future__ import annotations

from typing import Any, Optional

from .base import Application
from ..core.message import Message
from ..core.sim_clock import SimClock


# -----------------------------------------------------------------------
# Constants matching Java static final fields
# -----------------------------------------------------------------------

APP_ID: str = "ProcApplication"

# Maximum iterations per depletion loop (prevents runaway in a single tick)
_MAX_DEPL_ITERATIONS: int = 50


class ProcApplication(Application):
    """Port of ``applications.ProcApplication`` (930-line Java class).

    Implements the store / process / deplete pipeline on an EDR node.
    """

    # ------------------------------------------------------------------ init

    def __init__(
        self,
        *,
        passive: bool = False,
        storage_mode: bool = False,
        max_storage_time: float = 2_000.0,
        max_storage_ratio: float = 0.95,
        min_storage_ratio: float = 0.001,
        depletion_rate: int = 0,
        cloud_rate: int = 0,
        core_count: int = 4,
    ) -> None:
        # -- Configuration (Java: Settings constructor) ----------------------
        self.passive: bool = passive
        self.storage_mode: bool = storage_mode          # Java: storMode
        self.max_storage_time: float = max_storage_time  # Java: maxStorTime
        self.max_storage_ratio: float = max_storage_ratio  # Java: max_stor
        self.min_storage_ratio: float = min_storage_ratio  # Java: min_stor
        self.depletion_rate: int = depletion_rate        # Java: depl_rate
        self.cloud_rate: int = cloud_rate                # Java: cloud_lim

        # Virtual processing cores -- each entry is the sim-time at which
        # the core becomes free.  Java: procEndTimes (ArrayList<Double>)
        self._core_end_times: list[float] = [0.0] * core_count

        # -- Runtime state ---------------------------------------------------
        self._depl_bw: int = 0          # Java: deplBW
        self._cloud_bw: int = 0         # Java: cloudBW
        self._cloud_empty_loop: bool = True   # Java: cloudEmptyLoop
        self._depl_empty_loop: bool = True    # Java: deplEmptyLoop
        self._last_proc: float = 0.0         # Java: lastProc
        self._last_depl: float = 0.0         # Java: lastDepl
        self._last_cloud_static_upload: float = 0.0  # Java: lastCloudStaticUpload

        # Core selection cache (Java: procMin / procMinI)
        self._proc_min: float = 0.0
        self._proc_min_i: int = 0

        self.set_app_id(APP_ID)

    # ---------------------------------------------------------------- copy

    @classmethod
    def _from_copy(cls, other: ProcApplication) -> ProcApplication:
        """Copy-constructor equivalent (Java ``ProcApplication(ProcApplication a)``)."""
        inst = cls.__new__(cls)
        inst.passive = other.passive
        inst.storage_mode = other.storage_mode
        inst.max_storage_time = other.max_storage_time if other.storage_mode else 0.0
        inst.max_storage_ratio = other.max_storage_ratio
        inst.min_storage_ratio = other.min_storage_ratio
        inst.depletion_rate = other.depletion_rate
        inst.cloud_rate = other.cloud_rate
        inst._core_end_times = list(other._core_end_times)
        inst._depl_bw = 0
        inst._cloud_bw = 0
        inst._cloud_empty_loop = True
        inst._depl_empty_loop = True
        inst._last_proc = other._last_proc
        inst._last_depl = other._last_depl
        inst._last_cloud_static_upload = 0.0
        inst._proc_min = 0.0
        inst._proc_min_i = 0
        inst.app_id = other.app_id
        return inst

    def replicate(self) -> ProcApplication:
        """Return a deep copy (Java: ``replicate()``)."""
        return ProcApplication._from_copy(self)

    # ============================================================ handle

    def handle(self, msg: Any, host: Any) -> Optional[Any]:
        """Store the incoming message and try to process the newest valid one.

        Java: ``handle(Message msg, DTNHost host)``
        """
        if host.has_storage_capability():
            host.get_storage_system().add_to_stored_messages(msg)
            self._process_newest_valid(host)
        return msg

    # ============================================================ update

    def update(self, host: Any) -> None:
        """Per-tick processing and depletion pipeline.

        Java: ``update(DTNHost host)``

        1. Process oldest valid message (back-fill cores).
        2. Cloud depletion (two threshold bands, BW-limited).
        3. Forced storage depletion (overflow protection).
        """
        self._process_oldest_valid(host)

        self._update_cloud_bw(host)
        self._deplete_cloud(host)

        self._update_depl_bw(host)
        self._deplete_storage(host)

    # ============================================================ core scheduling

    def _get_earliest_core(self) -> tuple[int, float]:
        """Find the core with the minimum end time.

        Java: ``getProcMin()`` -- note the original has an off-by-one loop
        (``i < size()-1``) which is faithfully replicated here.

        Returns
        -------
        tuple[int, float]
            ``(core_index, end_time)``
        """
        cur_time: float = SimClock.get_time()
        n = len(self._core_end_times)
        if n == 0:
            self._proc_min = cur_time
            self._proc_min_i = 0
            return (0, cur_time)

        # Java loop: for (int i = 0; i < this.procEndTimes.size()-1; i++)
        best_i: int = 0
        best_v: float = 0.0

        for i in range(n - 1):
            if i == 0:
                if self._core_end_times[0] < self._core_end_times[n - 1]:
                    best_v = self._core_end_times[0]
                    best_i = 0
                else:
                    best_v = self._core_end_times[n - 1]
                    best_i = n - 1
            else:
                if self._core_end_times[i] < best_v:
                    best_v = self._core_end_times[i]
                    best_i = i

        if best_v == 0.0:
            best_v = cur_time

        self._proc_min = best_v
        self._proc_min_i = best_i
        return (best_i, best_v)

    # ============================================================ valid message processing

    def _process_oldest_valid(self, host: Any) -> bool:
        """Java: ``processOldestValidMessage(DTNHost host)``."""
        storage = host.get_storage_system()
        tempp = storage.get_oldest_valid_process_message()
        if tempp is None or storage.is_processed_full():
            return False

        cur_time: float = SimClock.get_time()
        delayed: float = float(tempp.get_property("delay"))
        tempp_shelf: float = float(tempp.get_property("shelfLife"))
        self._get_earliest_core()

        if self._proc_min - cur_time + delayed <= tempp_shelf:
            if not storage.is_processed_full():
                self._process_oldest_message(host, tempp)
            return True
        else:
            comp = tempp.get_property("comp")
            if comp is not None:
                if not bool(comp):
                    storage.delete_message(tempp.id)
                else:
                    m = self._compress_message(host, tempp)
                    if m is not None:
                        storage.add_to_depl_unproc_messages(m)
            return False

    def _process_newest_valid(self, host: Any) -> bool:
        """Java: ``processNewestValidMessage(DTNHost host)``."""
        storage = host.get_storage_system()
        tempp = storage.get_newest_process_message()
        if tempp is None or storage.is_processed_full():
            return False

        cur_time: float = SimClock.get_time()
        delayed: float = float(tempp.get_property("delay"))
        tempp_shelf: float = float(tempp.get_property("shelfLife"))
        self._get_earliest_core()

        if self._proc_min - cur_time + delayed <= tempp_shelf:
            if not storage.is_processed_full():
                self._process_newest_message(host, tempp)
            return True
        else:
            comp = tempp.get_property("comp")
            if comp is not None:
                if not bool(comp):
                    storage.delete_message(tempp.id)
                else:
                    m = self._compress_message(host, tempp)
                    if m is not None:
                        storage.add_to_depl_unproc_messages(m)
            return False

    # ============================================================ message scheduling on cores

    def _process_oldest_message(self, host: Any, proc_message: Any) -> bool:
        """Schedule *proc_message* on the earliest core -- oldest-first variant.

        Java: ``processOldestMessage(DTNHost host, Message procMessage)``
        """
        cur_time: float = SimClock.get_time()
        storage = host.get_storage_system()
        ans: bool = False

        delayed: float = float(proc_message.get_property("delay"))
        fresh_per: float = float(proc_message.get_property("freshPer"))
        shelf_life: float = float(proc_message.get_property("shelfLife"))

        # Fresh window check (Java: first if)
        if (self._proc_min - cur_time + delayed
                <= fresh_per - (cur_time - proc_message.time_received)
                and proc_message.get_property("Fresh") is None):
            storage.delete_proc_message(proc_message.id)
            proc_message.add_property("Fresh", True)
            self._core_end_times[self._proc_min_i] = self._proc_min + delayed
            proc_message.add_property("procTime", self._proc_min + delayed)
            storage.add_to_stored_messages(proc_message)
            ans = True

        # Shelf-life window check (Java: else if)
        elif (self._proc_min - cur_time + delayed
                <= shelf_life - (cur_time - proc_message.time_received)
                and proc_message.get_property("Fresh") is None):
            storage.delete_proc_message(proc_message.id)
            proc_message.add_property("Fresh", False)
            self._core_end_times[self._proc_min_i] = self._proc_min + delayed
            proc_message.add_property("procTime", self._proc_min + delayed)
            storage.add_to_stored_messages(proc_message)
            ans = True

        # Process ready messages from the queue (Java: unconditional block)
        oldest_fresh = storage.get_oldest_queue_fresh_message()
        if oldest_fresh is not None:
            self._process_message(host, oldest_fresh)
        else:
            oldest_shelf = storage.get_oldest_queue_shelf_message()
            if oldest_shelf is not None:
                self._process_message(host, oldest_shelf)

        return ans

    def _process_newest_message(self, host: Any, proc_message: Any) -> bool:
        """Schedule *proc_message* on the earliest core -- newest-first variant.

        Java: ``processNewestMessage(DTNHost host, Message procMessage)``
        """
        cur_time: float = SimClock.get_time()
        storage = host.get_storage_system()
        ans: bool = False

        delayed: float = float(proc_message.get_property("delay"))
        fresh_per: float = float(proc_message.get_property("freshPer"))
        shelf_life: float = float(proc_message.get_property("shelfLife"))

        # Fresh window check
        if (self._proc_min - cur_time + delayed
                <= fresh_per - (cur_time - proc_message.time_received)):
            storage.delete_proc_message(proc_message.id)
            proc_message.add_property("Fresh", True)
            self._core_end_times[self._proc_min_i] = self._proc_min + delayed
            proc_message.add_property("procTime", self._proc_min + delayed)
            storage.add_to_stored_messages(proc_message)

        # Shelf-life window check
        elif (self._proc_min - cur_time + delayed
                <= shelf_life - (cur_time - proc_message.time_received)
                and proc_message.get_property("Fresh") is None):
            storage.delete_proc_message(proc_message.id)
            proc_message.add_property("Fresh", False)
            self._core_end_times[self._proc_min_i] = self._proc_min + delayed
            proc_message.add_property("procTime", self._proc_min + delayed)
            storage.add_to_stored_messages(proc_message)

        # Process ready messages from the queue
        proc_time_val = proc_message.get_property("procTime")
        if proc_time_val is not None and float(proc_time_val) <= cur_time:
            self._process_message(host, proc_message)
            ans = True
        else:
            newest_fresh = storage.get_newest_queue_fresh_message()
            if newest_fresh is not None:
                self._process_message(host, newest_fresh)
                ans = True
            else:
                newest_shelf = storage.get_newest_queue_shelf_message()
                if newest_shelf is not None:
                    self._process_message(host, newest_shelf)
                    ans = True

        return ans

    # ============================================================ process / compress

    def _process_message(self, host: Any, proc_message: Any) -> None:
        """Create a processed copy and optionally retain the compressed original.

        Java: ``processMessage(DTNHost host, Message procMessage)``
        """
        storage = host.get_storage_system()
        storage.delete_proc_message(proc_message.id)

        init_size: int = proc_message.size
        compression_rate: float = storage.get_compression_rate()
        processed_size: int = int(init_size / (2 * compression_rate))

        processed_msg = Message(
            id=proc_message.id,
            from_host=proc_message.from_host,
            to_host=proc_message.to_host,
            size=processed_size,
        )
        processed_msg.copy_from(proc_message)
        processed_msg.time_received = proc_message.time_received
        processed_msg.update_property("type", "processed")
        processed_msg.update_property("procTime", None)

        # Revert original to nonproc for potential re-storage
        proc_message.update_property("type", "nonproc")

        storage.add_to_stored_messages(processed_msg)

        comp = proc_message.get_property("comp")
        if comp is not None and bool(comp):
            storage.add_to_stored_messages(proc_message)

    def _compress_message(self, host: Any, comp_message: Any) -> Optional[Any]:
        """Compress a message (halve size by compression rate).

        Java: ``compressMessage(DTNHost host, Message compMessage)``

        Returns
        -------
        Optional[Message]
            Compressed message, or ``None`` if not compression-eligible.
        """
        comp = comp_message.get_property("comp")
        if comp is not None and bool(comp):
            storage = host.get_storage_system()
            init_size: int = comp_message.size
            compressed_size: int = int(init_size / storage.get_compression_rate())
            compressed_msg = Message(
                id=comp_message.id,
                from_host=comp_message.from_host,
                to_host=comp_message.to_host,
                size=compressed_size,
            )
            compressed_msg.copy_from(comp_message)
            compressed_msg.time_received = comp_message.time_received
            compressed_msg.update_property("comp", None)
            storage.delete_message(comp_message.id)
            return compressed_msg
        return None

    # ============================================================ bandwidth tracking

    def _update_cloud_bw(self, host: Any) -> None:
        """Sum current cloud depletion bandwidth.

        Java: ``updateCloudBW(DTNHost host)``

        The Java BW getters used read-and-reset semantics: each call
        returns the delta since the previous call and advances the
        snapshot.  We pass ``reporting=True`` to replicate this.
        """
        storage = host.get_storage_system()
        self._cloud_bw = (
            storage.get_depleted_cloud_proc_messages_bw(True)
            + storage.get_depleted_unproc_messages_bw(True)
            + storage.get_depleted_cloud_static_messages_bw(True)
        )

    def _update_depl_bw(self, host: Any) -> None:
        """Sum current forced-depletion bandwidth.

        Java: ``updateDeplBW(DTNHost host)``

        Same read-and-reset semantics as ``_update_cloud_bw``.
        """
        storage = host.get_storage_system()
        self._depl_bw = (
            storage.get_depleted_proc_messages_bw(True)
            + storage.get_depleted_unproc_messages_bw(True)
            + storage.get_depleted_static_messages_bw(True)
        )

    # ============================================================ cloud depletion

    def _deplete_cloud(self, host: Any) -> None:
        """Two-threshold cloud depletion pipeline.

        Java: ``deplCloud(DTNHost host)``

        *First threshold* (``> min_storage_ratio``): priority order is
        processed -> unproc -> stale static.

        *Second threshold* (``> max_storage_ratio``): priority order is
        stale static -> unproc -> processed.
        """
        cur_time: float = SimClock.get_time()
        storage = host.get_storage_system()

        processed_plus_stale: int = (
            storage.get_processed_messages_size()
            + storage.get_stale_static_messages_size()
        )
        total: int = storage.get_total_storage_space()

        # ---- First threshold: > min_storage_ratio --------------------------
        if processed_plus_stale > int(total * self.min_storage_ratio):
            self._cloud_empty_loop = True

            for _ in range(_MAX_DEPL_ITERATIONS):
                if self._cloud_bw >= self.cloud_rate or not self._cloud_empty_loop:
                    break

                if not storage.is_processed_empty():
                    self._processed_depletion(host)
                elif storage.get_oldest_depl_unproc_message() is not None:
                    self._oldest_unproc_depletion(host)
                elif (not self.storage_mode
                      and storage.get_oldest_stale_static_message() is not None):
                    self._oldest_satisfied_static_depletion(host)
                elif (self.storage_mode
                      and cur_time - self._last_cloud_static_upload >= self.max_storage_time):
                    self.storage_mode = False
                else:
                    self._cloud_empty_loop = False

                self._update_cloud_bw(host)
                self._last_depl = cur_time

        # ---- Second threshold: > max_storage_ratio -------------------------
        elif processed_plus_stale > int(total * self.max_storage_ratio):
            self._cloud_empty_loop = True

            for _ in range(_MAX_DEPL_ITERATIONS):
                if self._cloud_bw >= self.cloud_rate or not self._cloud_empty_loop:
                    break

                if (storage.get_oldest_stale_static_message() is not None
                        and self._cloud_bw < self.cloud_rate):
                    self._oldest_satisfied_static_depletion(host)
                elif storage.get_oldest_depl_unproc_message() is not None:
                    self._oldest_unproc_depletion(host)
                elif not storage.is_processed_empty():
                    self._processed_depletion(host)
                else:
                    self._cloud_empty_loop = False

                self._update_cloud_bw(host)
                self._last_depl = cur_time

    # ============================================================ forced storage depletion

    def _deplete_storage(self, host: Any) -> None:
        """Forced depletion when storage exceeds *max_storage_ratio*.

        Java: ``deplStorage(DTNHost host)``

        Priority: unproc -> stale static -> invalid proc -> oldest static ->
        newest proc.
        """
        cur_time: float = SimClock.get_time()
        storage = host.get_storage_system()

        used: int = storage.get_proc_messages_size() + storage.get_static_messages_size()
        capacity: float = storage.get_total_storage_space() * self.max_storage_ratio

        if used <= capacity:
            return

        self._depl_empty_loop = True
        for _ in range(_MAX_DEPL_ITERATIONS):
            if self._depl_bw >= self.depletion_rate or not self._depl_empty_loop:
                break

            if storage.get_oldest_depl_unproc_message() is not None:
                self._oldest_unproc_depletion(host)

            elif storage.get_oldest_stale_static_message() is not None:
                self._oldest_satisfied_static_depletion(host)

            elif storage.get_oldest_invalid_process_message() is not None:
                self._oldest_invalid_proc_depletion(host)

            elif storage.get_oldest_static_message() is not None:
                # Java: oldest static message block (complex inline logic)
                temp = storage.get_oldest_static_message()
                storage.delete_message(temp.id)
                comp = temp.get_property("comp")
                if comp is not None:
                    if bool(comp):
                        ctemp = self._compress_message(host, temp)
                        if ctemp is not None:
                            storage.delete_message(ctemp.id)
                            stor_time = cur_time - ctemp.time_received
                            ctemp.add_property("storTime", stor_time)
                            ctemp.add_property("satisfied", False)
                            ctemp.add_property("overtime", False)
                            storage.add_to_depl_static_messages(ctemp)
                elif comp is None:
                    stor_time = cur_time - temp.time_received
                    temp.add_property("storTime", stor_time)
                    temp.add_property("satisfied", False)
                    temp.add_property("overtime", False)
                    msg_type = str(temp.get_property("type")).lower()
                    if msg_type == "unprocessed":
                        storage.add_to_depleted_unproc_messages(temp)
                    else:
                        storage.add_to_depl_static_messages(temp)

            elif storage.get_newest_process_message() is not None:
                # Java: newest proc message block
                temp = storage.get_newest_process_message()
                storage.delete_message(temp.id)
                comp = temp.get_property("comp")
                if comp is not None:
                    if bool(comp):
                        ctemp = self._compress_message(host, temp)
                        if ctemp is not None:
                            storage.delete_message(ctemp.id)
                            stor_time = cur_time - ctemp.time_received
                            ctemp.add_property("storTime", stor_time)
                            ctemp.add_property("satisfied", False)
                            storage.add_to_depl_proc_messages(ctemp)
                elif comp is None:
                    stor_time = cur_time - temp.time_received
                    temp.add_property("storTime", stor_time)
                    temp.add_property("satisfied", True)
                    shelf = float(temp.get_property("shelfLife"))
                    if stor_time <= shelf + 1:
                        temp.add_property("overtime", False)
                    elif stor_time > shelf + 1:
                        temp.add_property("overtime", True)
                    msg_type = str(temp.get_property("type")).lower()
                    if msg_type == "unprocessed":
                        storage.add_to_depleted_unproc_messages(temp)
                    else:
                        storage.add_to_depl_static_messages(temp)

            else:
                self._depl_empty_loop = False

            self._update_depl_bw(host)
            self._update_cloud_bw(host)
            self._last_depl = cur_time

    # ============================================================ depletion sub-routines

    def _processed_depletion(self, host: Any) -> None:
        """Deplete oldest fresh, then oldest stale processed messages.

        Java: ``processedDepletion(DTNHost host)``
        """
        storage = host.get_storage_system()

        oldest_fresh = storage.get_oldest_fresh_message()
        if oldest_fresh is not None:
            if oldest_fresh.get_property("procTime") is None:
                report = False
                storage.delete_processed_message(oldest_fresh.id, report)
                report = True
                storage.add_to_stored_messages(oldest_fresh)
                storage.delete_processed_message(oldest_fresh.id, report)
                return

        oldest_shelf = storage.get_oldest_shelf_message()
        if oldest_shelf is not None:
            if oldest_shelf.get_property("procTime") is None:
                report = False
                storage.delete_processed_message(oldest_shelf.id, report)
                report = True
                storage.add_to_stored_messages(oldest_shelf)
                storage.delete_processed_message(oldest_shelf.id, report)

    def _oldest_satisfied_static_depletion(self, host: Any) -> None:
        """Deplete the oldest stale (shelf-life-expired) static message.

        Java: ``oldestSatisfiedStaticDepletion(DTNHost host)``
        """
        cur_time: float = SimClock.get_time()
        storage = host.get_storage_system()
        temp = storage.get_oldest_stale_static_message()
        if temp is None:
            return
        storage.delete_message(temp.id)

        comp = temp.get_property("comp")
        if comp is not None:
            if bool(comp):
                ctemp = self._compress_message(host, temp)
                if ctemp is not None:
                    storage.delete_message(ctemp.id)
                    stor_time = cur_time - ctemp.time_received
                    ctemp.add_property("storTime", stor_time)
                    ctemp.add_property("satisfied", True)
                    shelf = float(ctemp.get_property("shelfLife"))
                    if stor_time <= shelf + 1:
                        ctemp.add_property("overtime", False)
                    elif stor_time > shelf + 1:
                        ctemp.add_property("overtime", True)
                    storage.add_to_cloud_depl_static_messages(ctemp)
        elif comp is None:
            stor_time = cur_time - temp.time_received
            temp.add_property("storTime", stor_time)
            temp.add_property("satisfied", True)
            shelf = float(temp.get_property("shelfLife"))
            if stor_time <= shelf + 1:
                temp.add_property("overtime", False)
            elif stor_time > shelf + 1:
                temp.add_property("overtime", True)
            storage.add_to_cloud_depl_static_messages(temp)

        self._last_cloud_static_upload = cur_time

    def _oldest_invalid_proc_depletion(self, host: Any) -> None:
        """Deplete the oldest process message whose shelf life has expired.

        Java: ``oldestInvalidProcDepletion(DTNHost host)``
        """
        cur_time: float = SimClock.get_time()
        storage = host.get_storage_system()
        temp = storage.get_oldest_invalid_process_message()
        if temp is None:
            return
        storage.delete_message(temp.id)

        comp = temp.get_property("comp")
        if comp is not None:
            if bool(comp):
                ctemp = self._compress_message(host, temp)
                if ctemp is not None:
                    storage.delete_message(ctemp.id)
                    stor_time = cur_time - ctemp.time_received
                    ctemp.add_property("storTime", stor_time)
                    shelf = float(ctemp.get_property("shelfLife"))
                    if stor_time <= shelf + 1:
                        ctemp.add_property("overtime", False)
                    elif stor_time > shelf + 1:
                        ctemp.add_property("overtime", True)
                    storage.add_to_depl_proc_messages(ctemp)
        elif comp is None:
            stor_time = cur_time - temp.time_received
            temp.add_property("storTime", stor_time)
            temp.add_property("satisfied", False)
            shelf = float(temp.get_property("shelfLife"))
            if stor_time <= shelf + 1:
                temp.add_property("overtime", False)
            elif stor_time > shelf + 1:
                temp.add_property("overtime", True)
            storage.add_to_depl_proc_messages(temp)

    def _oldest_unproc_depletion(self, host: Any) -> None:
        """Deplete the oldest unprocessed message from static storage.

        Java: ``oldestUnProcDepletion(DTNHost host)``
        """
        storage = host.get_storage_system()
        temp = storage.get_oldest_depl_unproc_message()
        if temp is None:
            return
        storage.delete_message(temp.id)
        storage.add_to_depleted_unproc_messages(temp)

    # ============================================================ accessors

    @property
    def last_proc(self) -> float:
        return self._last_proc

    @last_proc.setter
    def last_proc(self, value: float) -> None:
        self._last_proc = value

    @property
    def last_depl(self) -> float:
        return self._last_depl

    @last_depl.setter
    def last_depl(self, value: float) -> None:
        self._last_depl = value

    @property
    def core_end_times(self) -> list[float]:
        return self._core_end_times
