"""Core storage engine for an Edge Data Repository (EDR) node.

Faithful Python 3 port of the Java ``RepoStorage.java`` (one-comp / one-cloud
variants, ~1200 lines).  The three compartments, incremental size tracking,
depletion accounting, and every public method are preserved.  The 16+
``getOldest*/getNewest*`` methods are consolidated into a single
``query_messages()`` with composable filter predicates.

Design decisions vs. the Java original
---------------------------------------
* ``processedSize`` (a *capacity* limit in Java, not a running tally) is now
  named ``_processed_capacity`` to avoid confusion with the dynamically
  computed ``processed_messages_size`` property that sums current contents.
* Message-type routing uses the ``MessageType`` enum rather than raw string
  comparisons.
* The ``host`` back-reference is typed as ``Any`` (forward ref to ``DTNHost``)
  and only accessed through a thin ``_host`` property so that the storage
  module carries no hard import on the host layer.
"""

from __future__ import annotations

import logging
from typing import TYPE_CHECKING, Any, Optional

from ..core.sim_clock import SimClock
from ..core.types import MessageType

from .depletion import DepletionTracker
from .metrics import StorageMetrics

if TYPE_CHECKING:
    from ..core.message import Message

log = logging.getLogger(__name__)

# ---------------------------------------------------------------------------
# Compartment name constants (used as keys for query_messages)
# ---------------------------------------------------------------------------

STATIC: str = "static"
PROCESS: str = "process"
PROCESSED: str = "processed"

# Magic size thresholds carried over verbatim from Java.
_PROCESSED_FULL_MARGIN: int = 500_000       # bytes
_EMPTY_THRESHOLD: int = 2_000_000            # bytes


class RepoStorage:
    """Three-compartment message store with depletion accounting.

    Compartments
    ~~~~~~~~~~~~
    ``static_messages``
        Non-processing ("nonproc") and "unprocessed" data.  Tracked by
        ``_static_size``.
    ``process_messages``
        Messages awaiting processing ("proc").  Tracked by ``_process_size``.
    ``processed_messages``
        Compressed / processed messages ready for cloud depletion.  Their total
        size is computed on-the-fly (``processed_messages_size``).

    Parameters
    ----------
    host:
        The ``DTNHost`` that owns this storage (back-reference).
    total_capacity:
        Maximum storage in bytes (``storageSize`` in Java).
    compression_rate:
        Compression ratio used to derive ``_processed_capacity``.
    """

    # ------------------------------------------------------------------
    # Construction
    # ------------------------------------------------------------------

    def __init__(
        self,
        host: Any,
        total_capacity: int,
        compression_rate: float,
    ) -> None:
        self._host: Any = host
        self._total_capacity: int = total_capacity
        self._compression_rate: float = compression_rate

        # Compartments
        self.static_messages: list[Message] = []
        self.process_messages: list[Message] = []
        self.processed_messages: list[Message] = []

        # Incremental size tracking
        self._static_size: int = 0
        self._process_size: int = 0

        # Processed-compartment capacity (Java: processedSize)
        #   Java: processedSize = (long)(storageSize / (compressionRate * 2))
        if compression_rate > 0:
            self._processed_capacity: int = int(
                total_capacity // (compression_rate * 2)
            )
        else:
            self._processed_capacity = 0

        # Deleted-message bookkeeping (one-cloud variant)
        self._nrof_deleted_messages: int = 0
        self._deleted_messages_size: int = 0

        # Cached-messages counter (reset-on-read, Java getFullCachedMessagesNo)
        self._cached_messages: int = 0

        # Sub-components
        self.metrics: StorageMetrics = StorageMetrics()
        self.depletion: DepletionTracker = DepletionTracker()

    # ------------------------------------------------------------------
    # Host access
    # ------------------------------------------------------------------

    @property
    def host(self) -> Any:
        """The ``DTNHost`` that owns this storage."""
        return self._host

    # ------------------------------------------------------------------
    # Capacity / size properties
    # ------------------------------------------------------------------

    @property
    def total_capacity(self) -> int:
        """Total storage capacity in bytes (Java ``getTotalStorageSpace``)."""
        return self._total_capacity

    @property
    def processed_capacity(self) -> int:
        """Capacity reserved for processed messages (Java ``getTotalProcessedSpace``)."""
        return self._processed_capacity

    @property
    def static_size(self) -> int:
        """Bytes currently used by static (nonproc + unprocessed) messages."""
        return self._static_size

    @property
    def process_size(self) -> int:
        """Bytes currently used by messages awaiting processing."""
        return self._process_size

    @property
    def processed_messages_size(self) -> int:
        """Live-computed total bytes in the processed compartment.

        Java computed this on-the-fly in ``getProcessedMessagesSize()``.
        """
        return sum(m.size for m in self.processed_messages)

    @property
    def compression_rate(self) -> float:
        return self._compression_rate

    # ------------------------------------------------------------------
    # Compartment list accessors
    # ------------------------------------------------------------------

    def get_stored_messages(self) -> list[Message]:
        """Return static + process messages (new list each call).

        Note: The Java original mutated ``storedMessages`` in place, which was
        a bug-prone pattern.  We return a fresh concatenation.
        """
        return list(self.static_messages) + list(self.process_messages)

    # ------------------------------------------------------------------
    # Message counts
    # ------------------------------------------------------------------

    @property
    def nrof_static_messages(self) -> int:
        return len(self.static_messages)

    @property
    def nrof_process_messages(self) -> int:
        return len(self.process_messages)

    @property
    def nrof_processed_messages(self) -> int:
        return len(self.processed_messages)

    # ------------------------------------------------------------------
    # Deleted / cached counters
    # ------------------------------------------------------------------

    @property
    def nrof_deleted_messages(self) -> int:
        return self._nrof_deleted_messages

    @property
    def deleted_messages_size(self) -> int:
        return self._deleted_messages_size

    def get_full_cached_messages_no(self) -> int:
        """Return and reset the cached-messages counter (Java pattern)."""
        count = self._cached_messages
        self._cached_messages = 0
        return count

    # ------------------------------------------------------------------
    # Free-space / full / empty queries
    # ------------------------------------------------------------------

    def get_free_space(self) -> int:
        """Bytes of free storage (Java ``getFreeStorageSpace``).

        Java computed this as ``storageSize - staticSize``.
        """
        return self._total_capacity - self._static_size

    def is_processed_full(self) -> bool:
        """True when the processed compartment is at capacity.

        Java: ``usedProcessed >= processedSize - 500000``

        The margin is capped at 1/4 of processed capacity so that small
        storage sizes still behave correctly.
        """
        margin = min(_PROCESSED_FULL_MARGIN, max(1, self._processed_capacity // 4))
        return self.processed_messages_size >= (self._processed_capacity - margin)

    def is_processed_empty(self) -> bool:
        """True when processed contents are negligible.

        Java: ``usedProc <= 2000000``

        Threshold is capped at 1/4 of processed capacity so that small
        storage sizes still trigger cloud depletion.
        """
        threshold = min(_EMPTY_THRESHOLD, max(1, self._processed_capacity // 4))
        return self.processed_messages_size <= threshold

    def is_processing_empty(self) -> bool:
        """True when the processing queue is negligible.

        Java: ``processSize <= 2000000``

        Threshold is capped at 5% of total capacity for small storage.
        """
        threshold = min(_EMPTY_THRESHOLD, max(1, self._total_capacity // 20))
        return self._process_size <= threshold

    # ------------------------------------------------------------------
    # Stale-static size
    # ------------------------------------------------------------------

    def get_stale_static_size(self) -> int:
        """Total bytes of static messages whose shelf-life has been exceeded.

        Java ``getStaleStaticMessagesSize()``: counts messages where
        ``shelfLife >= curTime - receiveTime`` (i.e. the shelf-life window
        has *not* expired -- the name "stale" in the original is a misnomer
        kept for fidelity).
        """
        cur_time: float = SimClock.get_time()
        total: int = 0
        for m in self.static_messages:
            shelf_life: float | None = m.get_property("shelfLife")
            if shelf_life is not None and shelf_life >= cur_time - m.time_received:
                total += m.size
        return total

    # ------------------------------------------------------------------
    # Incoming-rate helpers
    # ------------------------------------------------------------------

    def get_overall_mean_incoming_speed(self) -> float:
        """Mean bytes/sec since simulation start (Java ``getOverallMeanIncomingSpeed``)."""
        t = SimClock.get_time()
        return self.metrics.get_overall_mean_incoming_speed(t)

    def get_overall_mean_incoming_message_no(self) -> float:
        """Mean messages/sec since simulation start."""
        t = SimClock.get_time()
        if t <= 0.0:
            return 0.0
        return self.metrics.total_received_messages / t

    # ------------------------------------------------------------------
    # Depletion BW convenience accessors
    # ------------------------------------------------------------------

    def get_depleted_proc_cloud_bw(self, reporting: bool = False) -> int:
        """BW for processed->cloud path (Java ``getDepletedCloudProcMessagesBW``)."""
        return self.depletion.get_bw("proc_cloud", reporting=reporting)

    def get_depleted_static_cloud_bw(self, reporting: bool = False) -> int:
        """BW for static->cloud path (Java ``getDepletedCloudStaticMessagesBW``)."""
        return self.depletion.get_bw("static_cloud", reporting=reporting)

    def get_depleted_proc_forced_bw(self, reporting: bool = False) -> int:
        """BW for forced proc depletion (Java ``getDepletedProcMessagesBW``)."""
        return self.depletion.get_bw("proc_forced", reporting=reporting)

    def get_depleted_static_forced_bw(self, reporting: bool = False) -> int:
        """BW for forced static depletion (Java ``getDepletedStaticMessagesBW``)."""
        return self.depletion.get_bw("static_forced", reporting=reporting)

    def get_depleted_unproc_bw(self, reporting: bool = False) -> int:
        """BW for unprocessed depletion (Java ``getDepletedUnProcMessagesBW``)."""
        return self.depletion.get_bw("unproc", reporting=reporting)

    def get_depleted_punproc_bw(self, reporting: bool = False) -> int:
        """BW for p-unprocessed depletion (Java ``getDepletedPUnProcMessagesBW``)."""
        return self.depletion.get_bw("punproc", reporting=reporting)

    # ------------------------------------------------------------------
    # Depletion count convenience accessors
    # ------------------------------------------------------------------

    @property
    def nrof_depleted_proc_cloud(self) -> int:
        return self.depletion.get_count("proc_cloud")

    @property
    def nrof_depleted_static_cloud(self) -> int:
        return self.depletion.get_count("static_cloud")

    @property
    def nrof_depleted_proc_forced(self) -> int:
        return self.depletion.get_count("proc_forced")

    @property
    def nrof_depleted_static_forced(self) -> int:
        return self.depletion.get_count("static_forced")

    @property
    def nrof_depleted_unproc(self) -> int:
        return self.depletion.get_count("unproc")

    @property
    def nrof_depleted_punproc(self) -> int:
        return self.depletion.get_count("punproc")

    # ------------------------------------------------------------------
    # add_message  (Java addToStoredMessages)
    # ------------------------------------------------------------------

    def add_message(self, msg: Message) -> None:
        """Route *msg* into the appropriate compartment and update accounting.

        Faithfully ports Java ``addToStoredMessages``:

        1. Inspect the message's ``type`` property to choose a compartment.
        2. Increment the corresponding size counter.
        3. Bump global reception counters.
        4. If combined static+process size >= total capacity, trigger the
           overflow callback on the host's processing application (the caller
           is responsible for wiring that up).
        """
        if msg is None:
            return

        # Ensure a content hash exists for session-less retrieval.
        # Any entity that knows the hash can later look up this message
        # without needing a reference to the original delivery session.
        if msg.content_hash is None:
            msg.compute_content_hash()

        msg_type_str: str = msg.get_property("type") or ""
        msg_type_str = msg_type_str.lower()

        if msg_type_str == MessageType.NONPROC.value:
            self.static_messages.append(msg)
            self._static_size += msg.size
        elif msg_type_str == MessageType.PROC.value:
            self.process_messages.append(msg)
            self._process_size += msg.size
        elif msg_type_str == MessageType.PROCESSED.value:
            self.processed_messages.append(msg)
        elif msg_type_str == MessageType.UNPROCESSED.value:
            self.static_messages.append(msg)
            self._static_size += msg.size

        self.metrics.record_message_received(msg.size)

        # Overflow check -- mirrors the Java ``if ((staticSize + processSize) >= storageSize)``
        if (self._static_size + self._process_size) >= self._total_capacity:
            self._trigger_overflow_depletion()

    def _trigger_overflow_depletion(self) -> None:
        """Invoke the host's processing-application depletion hooks.

        In Java this called ``procApp.updateDeplBW(host)`` then
        ``procApp.deplStorage(host)``.  The concrete wiring is host-layer
        responsibility; we call a well-known method if present.
        """
        host = self._host
        if host is None:
            return
        # The host is expected to expose an on_storage_overflow() callback
        # (or the caller can subclass / monkey-patch).
        callback = getattr(host, "on_storage_overflow", None)
        if callback is not None:
            callback(self)

    # ------------------------------------------------------------------
    # Depletion accounting  (addToDepl* family)
    # ------------------------------------------------------------------

    def _compute_stor_time(self, msg: Message) -> float:
        """Ensure *msg* has a ``storTime`` property and return its value.

        Matches the Java pattern: use existing ``storTime`` if present,
        otherwise compute ``curTime - receiveTime`` and write it back.
        """
        stor_time: float | None = msg.get_property("storTime")
        if stor_time is None:
            cur_time = SimClock.get_time()
            stor_time = cur_time - msg.time_received
            msg.set_property("storTime", stor_time)
        return stor_time

    def add_to_depl_static(self, msg: Message) -> None:
        """Record a forced-depleted static message (Java ``addToDeplStaticMessages``).

        Accounting: static_forced depletion counter, overtime, satisfied/
        unsatisfied, unprocessed sub-count, storage-time stats.
        """
        if msg is None:
            return

        self.depletion.record("static_forced", msg.size)

        overtime: bool = bool(msg.get_property("overtime"))
        satisfied: bool = bool(msg.get_property("satisfied"))
        msg_type_str: str = (msg.get_property("type") or "").lower()

        if msg_type_str == MessageType.UNPROCESSED.value:
            self.metrics.record_unprocessed()
            self.depletion.record("unproc", msg.size)

        stor_time = self._compute_stor_time(msg)
        self.metrics.record_depletion(
            msg,
            satisfied=satisfied,
            overtime=overtime,
            fresh=None,
            stor_time=stor_time,
        )

    def add_to_depl_proc(self, msg: Message) -> None:
        """Record a forced-depleted processing message (Java ``addToDeplProcMessages``).

        Always counts as *unsatisfied*.
        """
        if msg is None:
            return

        self.depletion.record("proc_forced", msg.size)

        overtime: bool = bool(msg.get_property("overtime"))
        msg_type_str: str = (msg.get_property("type") or "").lower()

        if msg_type_str == MessageType.UNPROCESSED.value:
            self.metrics.record_unprocessed()
            self.depletion.record("unproc", msg.size)

        stor_time = self._compute_stor_time(msg)
        # Forced proc depletion is always unsatisfied in Java
        self.metrics.record_depletion(
            msg,
            satisfied=False,
            overtime=overtime,
            fresh=None,
            stor_time=stor_time,
        )

    def add_to_cloud_depl_static(self, msg: Message) -> None:
        """Record a cloud-depleted static message (Java ``addToCloudDeplStaticMessages``)."""
        if msg is None:
            return

        self.depletion.record("static_cloud", msg.size)

        overtime: bool = bool(msg.get_property("overtime"))
        satisfied: bool = bool(msg.get_property("satisfied"))
        msg_type_str: str = (msg.get_property("type") or "").lower()

        if msg_type_str == MessageType.UNPROCESSED.value:
            self.metrics.record_unprocessed()
            self.depletion.record("unproc", msg.size)

        stor_time = self._compute_stor_time(msg)
        self.metrics.record_depletion(
            msg,
            satisfied=satisfied,
            overtime=overtime,
            fresh=None,
            stor_time=stor_time,
        )

    def add_to_depl_unproc(self, msg: Message) -> None:
        """Re-label a message as unprocessed and re-add it (Java ``addToDeplUnProcMessages``).

        This is the last-resort path: the message cannot be processed locally
        so it is relabelled ``unprocessed`` and re-inserted into storage to be
        depleted to the cloud later.
        """
        msg.set_property("type", MessageType.UNPROCESSED.value)
        self.add_message(msg)

    def add_to_depleted_unproc(self, msg: Message) -> None:
        """Record an already-depleted unprocessed message (Java ``addToDepletedUnProcMessages``)."""
        if msg is None:
            return
        msg_type_str: str = (msg.get_property("type") or "").lower()
        if msg_type_str == MessageType.UNPROCESSED.value:
            self.depletion.record("punproc", msg.size)
            self.metrics.record_unprocessed()

    # ------------------------------------------------------------------
    # has_message
    # ------------------------------------------------------------------

    def has_message(self, msg_id: str) -> Optional[Message]:
        """Search all three compartments for *msg_id*.

        Returns the ``Message`` if found, else ``None``.  Matches Java
        ``hasMessage`` which returns a ``Message`` (not boolean).

        This lookup is **session-agnostic**: any entity that knows the
        message ID can retrieve it, regardless of which connection or
        node originally delivered it.
        """
        for compartment in (
            self.processed_messages,
            self.static_messages,
            self.process_messages,
        ):
            for m in compartment:
                if m.id == msg_id:
                    return m
        return None

    # ------------------------------------------------------------------
    # Session-less retrieval (by ID, hash, app, label)
    # ------------------------------------------------------------------

    def get_message_by_id(self, msg_id: str) -> Optional[Message]:
        """Retrieve a message by its logical ID from any compartment.

        Alias for :meth:`has_message` with a clearer name for the
        session-less retrieval API.
        """
        return self.has_message(msg_id)

    def get_message_by_hash(self, content_hash: str) -> Optional[Message]:
        """Retrieve the first message matching *content_hash*.

        Enables session-less access: any entity that knows the content
        hash of a stored datum can retrieve it without needing to know
        which session or connection delivered it.
        """
        for compartment in (
            self.processed_messages,
            self.static_messages,
            self.process_messages,
        ):
            for m in compartment:
                if m.content_hash == content_hash:
                    return m
        return None

    def get_messages_by_app(self, app_id: str) -> list[Message]:
        """Return all messages in storage associated with *app_id*.

        Supports application-aware retrieval: entities aware of an
        application can discover all data it has stored.
        """
        result: list[Message] = []
        for compartment in (
            self.static_messages,
            self.process_messages,
            self.processed_messages,
        ):
            for m in compartment:
                if m.app_id == app_id:
                    result.append(m)
        return result

    def get_messages_by_label(self, label: str) -> list[Message]:
        """Return all messages carrying *label* in their label set.

        Supports SEND-style label-based discovery.
        """
        result: list[Message] = []
        for compartment in (
            self.static_messages,
            self.process_messages,
            self.processed_messages,
        ):
            for m in compartment:
                if label in m.labels:
                    result.append(m)
        return result

    def get_all_message_ids(self) -> list[str]:
        """Return the IDs of all messages currently in storage.

        Supports discovery: a connected entity can enumerate available
        data without prior knowledge of specific IDs.
        """
        ids: list[str] = []
        for compartment in (
            self.static_messages,
            self.process_messages,
            self.processed_messages,
        ):
            for m in compartment:
                ids.append(m.id)
        return ids

    def get_all_content_hashes(self) -> list[str]:
        """Return content hashes of all messages that have one.

        Supports hash-based discovery for session-less retrieval.
        """
        hashes: list[str] = []
        for compartment in (
            self.static_messages,
            self.process_messages,
            self.processed_messages,
        ):
            for m in compartment:
                if m.content_hash is not None:
                    hashes.append(m.content_hash)
        return hashes

    # ------------------------------------------------------------------
    # delete_* family
    # ------------------------------------------------------------------

    def delete_static_message(self, msg_id: str) -> bool:
        """Remove from static compartment, adjusting ``_static_size``."""
        for i, m in enumerate(self.static_messages):
            if m.id == msg_id:
                self._static_size -= m.size
                del self.static_messages[i]
                return True
        return False

    def delete_proc_message(self, msg_id: str) -> bool:
        """Remove from process compartment, adjusting ``_process_size``."""
        for i, m in enumerate(self.process_messages):
            if m.id == msg_id:
                self._process_size -= m.size
                del self.process_messages[i]
                return True
        return False

    def delete_message(self, msg_id: str) -> bool:
        """Remove a message from whichever compartment it is in.

        Ports Java ``deleteMessage``.  Looks up the message first, then
        dispatches to the appropriate compartment-specific delete.
        """
        m = self.has_message(msg_id)
        if m is None:
            return False

        msg_type_str: str = (m.get_property("type") or "").lower()

        if msg_type_str == MessageType.PROC.value:
            if self.delete_proc_message(msg_id):
                return True
        elif msg_type_str in (
            MessageType.NONPROC.value,
            MessageType.UNPROCESSED.value,
        ):
            if self.delete_static_message(msg_id):
                return True

        # one-cloud variant: track deletions on hosts without storage
        host = self._host
        if host is not None:
            has_storage = getattr(host, "has_storage_capability", None)
            if has_storage is not None and not has_storage():
                self._nrof_deleted_messages += 1
                self._deleted_messages_size += m.size

        return False

    def delete_processed_message(
        self, msg_id: str, report: bool = False
    ) -> bool:
        """Remove from processed compartment with optional metrics reporting.

        Ports Java ``deleteProcessedMessage``.  Always increments the
        ``proc_cloud`` depletion counters.  When *report* is ``True``, also
        records overtime / satisfied / fresh|stale flags on ``self.metrics``.
        """
        for i, m in enumerate(self.processed_messages):
            if m.id == msg_id:
                self.depletion.record("proc_cloud", m.size)

                if report:
                    overtime_prop = m.get_property("overtime")
                    overtime = bool(overtime_prop) if overtime_prop is not None else False

                    satisfied_prop = m.get_property("satisfied")
                    satisfied: bool | None = None
                    if satisfied_prop is not None:
                        satisfied = bool(satisfied_prop)

                    fresh_prop = m.get_property("Fresh")
                    fresh: bool | None = None
                    if fresh_prop is not None:
                        fresh = bool(fresh_prop)

                    # For delete_processed_message with report, Java records
                    # overtime, satisfied/unsatisfied, and fresh/stale but
                    # does NOT record storage time (no storTime block).
                    if overtime:
                        self.metrics.overtime_count += 1
                    if satisfied is True:
                        self.metrics.satisfied_count += 1
                    elif satisfied is False:
                        self.metrics.unsatisfied_count += 1
                    if fresh is True:
                        self.metrics.fresh_count += 1
                    elif fresh is False:
                        self.metrics.stale_count += 1

                del self.processed_messages[i]
                return True
        return False

    def clear_all_static_messages(self) -> bool:
        """Empty the static compartment (Java ``clearAllStaticMessages``)."""
        self.static_messages.clear()
        self._static_size = 0
        return True

    # ------------------------------------------------------------------
    # Unified message query  (replaces 16+ getOldest*/getNewest* methods)
    # ------------------------------------------------------------------

    def query_messages(
        self,
        compartment: str,
        oldest: bool = True,
        valid_only: bool = False,
        fresh_only: Optional[bool] = None,
        ready_only: bool = False,
        msg_type: Optional[MessageType] = None,
        exclude_types: Optional[set[MessageType]] = None,
    ) -> Optional[Message]:
        """Unified replacement for the 16+ Java ``getOldest*/getNewest*`` methods.

        Parameters
        ----------
        compartment:
            One of ``"static"``, ``"process"``, or ``"processed"``.
        oldest:
            ``True`` -> sort ascending by ``receive_time`` (oldest first).
            ``False`` -> sort descending (newest first).
        valid_only:
            When ``True``, exclude messages whose ``type`` is ``"unprocessed"``
            or ``"processed"`` *and* whose ``Fresh`` property is already set.
            Mirrors the Java ``getOldestValidProcessMessage`` filter.
        fresh_only:
            ``True`` -> only messages with ``Fresh == True``.
            ``False`` -> only messages with ``Fresh == False`` (shelf / stale).
            ``None`` -> no freshness filter.
        ready_only:
            When ``True``, include only messages where
            ``procTime <= SimClock.get_time()`` (processing has finished).
            Mirrors the Java ``getOldest/NewestQueueFresh/ShelfMessage`` filter.
        msg_type:
            If set, include only messages whose ``type`` property matches this
            ``MessageType`` value.
        exclude_types:
            If set, *exclude* messages whose ``type`` matches any member.

        Returns
        -------
        The first matching ``Message`` after sorting, or ``None`` if no
        messages pass all filters.
        """
        # Select source list
        if compartment == STATIC:
            source = self.static_messages
        elif compartment == PROCESS:
            source = self.process_messages
        elif compartment == PROCESSED:
            source = self.processed_messages
        else:
            raise ValueError(
                f"Unknown compartment {compartment!r}; "
                f"use {STATIC!r}, {PROCESS!r}, or {PROCESSED!r}."
            )

        cur_time: float = SimClock.get_time()
        candidates: list[Message] = []

        for m in source:
            m_type_raw: str | None = m.get_property("type")
            m_type: str = (m_type_raw or "").lower()
            m_fresh: bool | None = m.get_property("Fresh")
            m_shelf: float | None = m.get_property("shelfLife")
            m_proc_time: float | None = m.get_property("procTime")

            # --- valid_only filter (Java getOldestValidProcessMessage) ------
            # Requires type present, Fresh absent, type not in {unprocessed, processed}
            if valid_only:
                if m_type_raw is None:
                    continue
                if m_fresh is not None:
                    continue
                if m_type in (
                    MessageType.UNPROCESSED.value,
                    MessageType.PROCESSED.value,
                ):
                    continue

            # --- msg_type filter -------------------------------------------
            if msg_type is not None and m_type != msg_type.value:
                continue

            # --- exclude_types filter --------------------------------------
            if exclude_types is not None:
                if any(m_type == et.value for et in exclude_types):
                    continue

            # --- fresh_only filter -----------------------------------------
            if fresh_only is not None:
                if m_fresh is None:
                    continue
                if bool(m_fresh) != fresh_only:
                    continue

            # --- ready_only filter (procTime <= curTime) -------------------
            if ready_only:
                if m_fresh is None or m_proc_time is None:
                    continue
                if m_proc_time > cur_time:
                    continue

            # --- stale / invalid process filter (shelfLife expired) ---------
            # This is NOT applied automatically; callers can combine
            # msg_type + exclude_types to achieve the same effect.  However,
            # for the specific "getOldestInvalidProcessMessage" pattern we
            # expose a dedicated helper below.

            candidates.append(m)

        if not candidates:
            return None

        candidates.sort(
            key=lambda m: m.time_received,
            reverse=(not oldest),
        )
        return candidates[0]

    # ------------------------------------------------------------------
    # Convenience wrappers mapping old Java names to query_messages()
    # ------------------------------------------------------------------

    def get_oldest_process_message(self) -> Optional[Message]:
        """Java ``getOldestProcessMessage`` -- oldest in the process queue."""
        return self.query_messages(PROCESS, oldest=True)

    def get_oldest_valid_process_message(self) -> Optional[Message]:
        """Java ``getOldestValidProcessMessage``."""
        return self.query_messages(PROCESS, oldest=True, valid_only=True)

    def get_oldest_invalid_process_message(self) -> Optional[Message]:
        """Java ``getOldestInvalidProcessMessage`` -- oldest expired proc msg.

        Requires ``type == proc`` and ``shelfLife <= curTime - receiveTime``.
        This filter is too specialised for ``query_messages``'s generic
        predicates, so we implement it directly.
        """
        cur_time: float = SimClock.get_time()
        best: Optional[Message] = None
        for m in self.process_messages:
            m_type: str | None = m.get_property("type")
            m_shelf: float | None = m.get_property("shelfLife")
            if m_type is None or m_shelf is None:
                continue
            if m_type.lower() != MessageType.PROC.value:
                continue
            if m_shelf > cur_time - m.time_received:
                continue
            if best is None or m.time_received < best.time_received:
                best = m
        return best

    def get_oldest_depl_unproc_message(self) -> Optional[Message]:
        """Java ``getOldestDeplUnProcMessage`` -- oldest unprocessed in static."""
        return self.query_messages(
            STATIC, oldest=True, msg_type=MessageType.UNPROCESSED
        )

    def get_newest_process_message(self) -> Optional[Message]:
        """Java ``getNewestProcessMessage``.

        The Java original requires ``type != null && Fresh == null && type == "proc"``.
        Using ``valid_only=True`` enforces the ``Fresh == null`` constraint (plus
        excludes "unprocessed"/"processed" types, which is redundant given
        ``msg_type=PROC`` but harmless).
        """
        return self.query_messages(
            PROCESS,
            oldest=False,
            valid_only=True,
            msg_type=MessageType.PROC,
        )

    def get_oldest_processed_message(self) -> Optional[Message]:
        """Java ``getOldestProcessedMessage``."""
        return self.query_messages(PROCESSED, oldest=True)

    def get_oldest_fresh_message(self) -> Optional[Message]:
        """Java ``getOldestFreshMessage`` (processed, Fresh==True)."""
        return self.query_messages(
            PROCESSED,
            oldest=True,
            fresh_only=True,
            msg_type=MessageType.PROCESSED,
        )

    def get_newest_fresh_message(self) -> Optional[Message]:
        """Java ``getNewestFreshMessage``."""
        return self.query_messages(
            PROCESSED,
            oldest=False,
            fresh_only=True,
            msg_type=MessageType.PROCESSED,
        )

    def get_oldest_shelf_message(self) -> Optional[Message]:
        """Java ``getOldestShelfMessage`` (processed, Fresh==False)."""
        return self.query_messages(
            PROCESSED,
            oldest=True,
            fresh_only=False,
            msg_type=MessageType.PROCESSED,
        )

    def get_newest_shelf_message(self) -> Optional[Message]:
        """Java ``getNewestShelfMessage``."""
        return self.query_messages(
            PROCESSED,
            oldest=False,
            fresh_only=False,
            msg_type=MessageType.PROCESSED,
        )

    def get_oldest_queue_fresh_message(self) -> Optional[Message]:
        """Java ``getOldestQueueFreshMessage`` (process queue, ready, fresh)."""
        return self.query_messages(
            PROCESS, oldest=True, fresh_only=True, ready_only=True
        )

    def get_newest_queue_fresh_message(self) -> Optional[Message]:
        """Java ``getNewestQueueFreshMessage``."""
        return self.query_messages(
            PROCESS, oldest=False, fresh_only=True, ready_only=True
        )

    def get_oldest_queue_shelf_message(self) -> Optional[Message]:
        """Java ``getOldestQueueShelfMessage`` (process queue, ready, stale)."""
        return self.query_messages(
            PROCESS, oldest=True, fresh_only=False, ready_only=True
        )

    def get_newest_queue_shelf_message(self) -> Optional[Message]:
        """Java ``getNewestQueueShelfMessage``."""
        return self.query_messages(
            PROCESS, oldest=False, fresh_only=False, ready_only=True
        )

    def get_oldest_static_message(self) -> Optional[Message]:
        """Java ``getOldestStaticMessage``."""
        return self.query_messages(STATIC, oldest=True)

    def get_oldest_stale_static_message(self) -> Optional[Message]:
        """Java ``getOldestStaleStaticMessage`` -- oldest static past shelf-life.

        Requires ``shelfLife <= curTime - receiveTime``.  Specialised filter.
        """
        cur_time: float = SimClock.get_time()
        best: Optional[Message] = None
        for m in self.static_messages:
            shelf: float | None = m.get_property("shelfLife")
            if shelf is None:
                continue
            if shelf > cur_time - m.time_received:
                continue
            if best is None or m.time_received < best.time_received:
                best = m
        return best

    # ------------------------------------------------------------------
    # String representation
    # ------------------------------------------------------------------

    def __repr__(self) -> str:
        return (
            f"RepoStorage(capacity={self._total_capacity}, "
            f"static={self._static_size}, "
            f"process={self._process_size}, "
            f"processed={self.processed_messages_size}, "
            f"compression={self._compression_rate})"
        )

    # ------------------------------------------------------------------
    # Aliases for ProcApplication compatibility
    # ------------------------------------------------------------------

    def add_to_stored_messages(self, msg: Message) -> None:
        return self.add_message(msg)

    def add_to_depl_unproc_messages(self, msg: Message) -> None:
        return self.add_to_depl_unproc(msg)

    def add_to_depl_proc_messages(self, msg: Message) -> None:
        return self.add_to_depl_proc(msg)

    def add_to_depl_static_messages(self, msg: Message) -> None:
        return self.add_to_depl_static(msg)

    def add_to_cloud_depl_static_messages(self, msg: Message) -> None:
        return self.add_to_cloud_depl_static(msg)

    def add_to_depleted_unproc_messages(self, msg: Message) -> None:
        return self.add_to_depleted_unproc(msg)

    def get_depleted_cloud_proc_messages_bw(self, reporting: bool = False) -> int:
        return self.get_depleted_proc_cloud_bw(reporting)

    def get_depleted_cloud_static_messages_bw(self, reporting: bool = False) -> int:
        return self.get_depleted_static_cloud_bw(reporting)

    def get_depleted_proc_messages_bw(self, reporting: bool = False) -> int:
        return self.get_depleted_proc_forced_bw(reporting)

    def get_depleted_static_messages_bw(self, reporting: bool = False) -> int:
        return self.get_depleted_static_forced_bw(reporting)

    def get_depleted_unproc_messages_bw(self, reporting: bool = False) -> int:
        return self.get_depleted_unproc_bw(reporting)

    def get_compression_rate(self) -> float:
        return self._compression_rate

    def get_proc_messages_size(self) -> int:
        return self._process_size

    def get_processed_messages_size(self) -> int:
        return self.processed_messages_size

    def get_static_messages_size(self) -> int:
        return self._static_size

    def get_stale_static_messages_size(self) -> int:
        return self.get_stale_static_size()

    def get_total_storage_space(self) -> int:
        return self._total_capacity
