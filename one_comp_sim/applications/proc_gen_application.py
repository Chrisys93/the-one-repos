"""Message generation application -- faithful Python 3 port of Java ProcGenApplication.java.

Generates messages at a configurable interval, tagging them with the
appropriate processing type (nonproc/proc), compression/deletion flags,
freshness periods, and shelf-life values.  Messages are addressed to a
connected repository host identified by a name prefix.

The counter-based tag rotation logic faithfully replicates the original Java
implementation so that message mixes remain identical for the same seed and
configuration.
"""

from __future__ import annotations

import random
from typing import Any, Optional

from .base import Application
from ..core.message import Message
from ..core.sim_clock import SimClock


# -----------------------------------------------------------------------
# Constants matching Java static final fields
# -----------------------------------------------------------------------

APP_ID: str = "ProcGenApplication"


class ProcGenApplication(Application):
    """Port of ``applications.ProcGenApplication`` (435-line Java class).

    Generates store-process-deplete workload messages on mobile nodes.
    """

    # ------------------------------------------------------------------ init

    def __init__(
        self,
        *,
        interval: float = 5.0,
        delay: float = 0.1,
        fresh_period: float = 1.0,
        proc_shelf: float = 2.0,
        nonproc_shelf: float = 3.0,
        passive: bool = False,
        passive_rate: tuple[int, int] = (1, 1),
        compress_rate: tuple[int, int] = (1, 1),
        message_size: int = 1_000_000,
        destination_name: str = "r",
        seed: int = 0,
    ) -> None:
        # -- Configuration ---------------------------------------------------
        self.interval: float = interval
        self.delay: float = delay
        self.fresh_period: float = fresh_period
        self.proc_shelf: float = proc_shelf
        self.nonproc_shelf: float = nonproc_shelf
        self.passive: bool = passive
        self.passive_rate: tuple[int, int] = passive_rate   # (nonproc, proc)
        self.compress_rate: tuple[int, int] = compress_rate  # (comp, del)
        self.message_size: int = message_size
        self.destination_name: str = destination_name
        self.seed: int = seed

        # -- Runtime state ---------------------------------------------------
        self._last_msg: float = 0.0           # Java: lastMsg
        self._nonproc_counter: int = 0        # Java: noPassive
        self._proc_counter: int = 0           # Java: noProc
        self._comp_counter: int = 0           # Java: noComp
        self._del_counter: int = 0            # Java: noDel
        self._rng: random.Random = random.Random(self.seed)

        self.set_app_id(APP_ID)

    # ---------------------------------------------------------------- copy

    @classmethod
    def _from_copy(cls, other: ProcGenApplication) -> ProcGenApplication:
        """Copy-constructor equivalent (Java ``ProcGenApplication(ProcGenApplication a)``)."""
        inst = cls.__new__(cls)
        inst.interval = other.interval
        inst.delay = other.delay
        inst.fresh_period = other.fresh_period
        inst.proc_shelf = other.proc_shelf
        inst.nonproc_shelf = other.nonproc_shelf
        inst.passive = other.passive
        inst.passive_rate = other.passive_rate
        inst.compress_rate = other.compress_rate
        inst.message_size = other.message_size
        inst.destination_name = other.destination_name
        inst.seed = other.seed

        inst._last_msg = other._last_msg
        inst._nonproc_counter = 0
        inst._proc_counter = 0
        inst._comp_counter = 0
        inst._del_counter = 0
        inst._rng = random.Random(inst.seed)
        inst.app_id = other.app_id
        return inst

    def replicate(self) -> ProcGenApplication:
        """Return a deep copy (Java: ``replicate()``)."""
        return ProcGenApplication._from_copy(self)

    # ============================================================ handle

    def handle(self, msg: Any, host: Any) -> Optional[Any]:
        """No-op -- generator apps do not process incoming messages.

        Java: ``handle(Message msg, DTNHost host)``
        """
        return msg

    # ============================================================ update

    def update(self, host: Any) -> None:
        """Generate a message if the interval has elapsed.

        Java: ``update(DTNHost host)``

        In *passive* mode only ``nonproc`` messages are generated.  In
        *active* mode the counter-based rotation assigns ``nonproc``/``proc``
        type tags and ``comp``/``del`` compression tags using the ratios
        from the configuration.
        """
        cur_time: float = SimClock.get_time()

        if self.passive:
            if cur_time - self._last_msg >= self.interval:
                dest = self._find_destination(host)
                msg_id = f"nonproc{SimClock.get_int_time()}-{host.get_address()}"
                m = Message(
                    id=msg_id,
                    from_host=host,
                    to_host=dest,
                    size=self.message_size,
                )
                m.add_property("type", "nonproc")
                m.app_id = "ProcApplication"
                m.labels.add("nonproc")
                host.create_new_message(m)
                self._last_msg = cur_time

        elif cur_time - self._last_msg >= self.interval:
            dest = self._find_destination(host)
            msg_id = (
                f"{SimClock.get_int_time()}-{host.get_address()}{dest}"
            )
            m = Message(
                id=msg_id,
                from_host=host,
                to_host=dest,
                size=self.message_size,
            )

            # ---- nonproc / proc tag rotation (Java: passive_rate counters)
            if self._nonproc_counter < self.passive_rate[0]:
                m.add_property("type", "nonproc")
                m.add_property("shelfLife", self.nonproc_shelf)
                m.app_id = "ProcApplication"
                m.labels.add("nonproc")
                self._nonproc_counter += 1
            elif self._proc_counter < self.passive_rate[1]:
                m.add_property("type", "proc")
                m.add_property("delay", self.delay)
                m.add_property("freshPer", self.fresh_period)
                m.add_property("shelfLife", self.proc_shelf)
                m.app_id = "ProcApplication"
                m.labels.add("proc")
                self._proc_counter += 1

            if (self._nonproc_counter >= self.passive_rate[0]
                    and self._proc_counter >= self.passive_rate[1]):
                self._nonproc_counter = 0
                self._proc_counter = 0

            # ---- comp / del tag rotation (Java: comp_rate counters)
            if self._comp_counter < self.compress_rate[0]:
                m.add_property("comp", True)
                m.app_id = "ProcApplication"
                m.labels.add("compress")
                self._comp_counter += 1
            elif self._del_counter < self.compress_rate[1]:
                m.add_property("comp", False)
                m.app_id = "ProcApplication"
                m.labels.add("delete")
                self._del_counter += 1

            if (self._comp_counter >= self.compress_rate[0]
                    and self._del_counter >= self.compress_rate[1]):
                self._comp_counter = 0
                self._del_counter = 0

            host.create_new_message(m)
            self._last_msg = cur_time

    # ============================================================ destination lookup

    def _find_destination(self, host: Any) -> Any:
        """Locate a connected repository host by the configured destination name.

        Java: ``connectedRepoHost(DTNHost host)``

        Falls back to a random repo host from the world if no directly
        connected match is found.
        """
        # Try the World helper first (mirrors Java: w.getNodeByName)
        try:
            world = host.get_world()
        except AttributeError:
            # DTNHost may not yet expose get_world() -- degrade gracefully
            return host

        node = world.get_node_by_name(self.destination_name, host)
        if node is not None and node is not host:
            return node

        # Fallback: collect all repo hosts and pick one at random
        destinations = [
            h for h in world.get_hosts()
            if hasattr(h, "name") and self.destination_name in h.name
        ]
        if destinations:
            return destinations[self._rng.randint(0, len(destinations) - 1)]

        return host

    # ============================================================ accessors

    @property
    def last_msg(self) -> float:
        return self._last_msg

    @last_msg.setter
    def last_msg(self, value: float) -> None:
        self._last_msg = value
