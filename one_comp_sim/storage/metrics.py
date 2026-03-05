"""Performance counters for an Edge Data Repository storage node.

Tracks message reception rates, depletion outcomes (satisfied / unsatisfied /
overtime / fresh / stale / unprocessed), and per-message storage-time
statistics.  A single ``StorageMetrics`` instance is owned by each
``RepoStorage``.
"""

from __future__ import annotations

from dataclasses import dataclass
from typing import TYPE_CHECKING, Any

from ..core.sim_clock import SimClock

if TYPE_CHECKING:
    from ..core.message import Message


@dataclass
class StorageMetrics:
    """All EDR performance counters, ported from the Java RepoStorage fields."""

    # -- reception counters ---------------------------------------------------

    total_received_messages: int = 0
    total_received_size: int = 0

    # -- depletion-outcome counters -------------------------------------------

    fresh_count: int = 0
    stale_count: int = 0
    overtime_count: int = 0
    satisfied_count: int = 0
    unsatisfied_count: int = 0
    unprocessed_count: int = 0

    # -- storage-time tracking ------------------------------------------------

    stor_time_sum: float = 0.0
    stor_time_max: float = 0.0
    stor_time_count: int = 0

    # -- helpers --------------------------------------------------------------

    def record_message_received(self, size: int) -> None:
        """Increment reception counters when a new message enters storage."""
        self.total_received_messages += 1
        self.total_received_size += size

    def record_depletion(
        self,
        msg: Message,
        satisfied: bool | None,
        overtime: bool,
        fresh: bool | None,
        stor_time: float,
    ) -> None:
        """Record the outcome of a single message depletion event.

        Parameters
        ----------
        msg:
            The message being depleted (used for property look-ups if needed).
        satisfied:
            ``True`` if the message's shelf-life / demand was met, ``False``
            otherwise, ``None`` if not applicable (e.g. forced proc depletion).
        overtime:
            ``True`` if the message exceeded its shelf-life window.
        fresh:
            ``True`` / ``False`` for fresh/stale, ``None`` if the freshness
            flag was not set on the message.
        stor_time:
            Time in seconds the message spent in storage.
        """
        if overtime:
            self.overtime_count += 1

        if satisfied is True:
            self.satisfied_count += 1
        elif satisfied is False:
            self.unsatisfied_count += 1

        if fresh is True:
            self.fresh_count += 1
        elif fresh is False:
            self.stale_count += 1

        # Storage-time bookkeeping
        self.stor_time_count += 1
        self.stor_time_sum += stor_time
        if stor_time > self.stor_time_max:
            self.stor_time_max = stor_time

    def record_unprocessed(self) -> None:
        """Increment the unprocessed depletion counter."""
        self.unprocessed_count += 1

    # -- derived queries ------------------------------------------------------

    def get_avg_storage_time(self) -> float:
        """Return the mean storage time, or 0.0 if no samples."""
        if self.stor_time_count == 0:
            return 0.0
        return self.stor_time_sum / self.stor_time_count

    def get_overall_mean_incoming_speed(self, sim_time: float) -> float:
        """Return mean bytes/sec of incoming messages over *sim_time*.

        Matches Java ``getOverallMeanIncomingSpeed()``.
        """
        if sim_time <= 0.0:
            return 0.0
        return self.total_received_size / sim_time

    def snapshot(self) -> dict[str, Any]:
        """Return a plain-dict copy of every counter (useful for reports)."""
        return {
            "total_received_messages": self.total_received_messages,
            "total_received_size": self.total_received_size,
            "fresh_count": self.fresh_count,
            "stale_count": self.stale_count,
            "overtime_count": self.overtime_count,
            "satisfied_count": self.satisfied_count,
            "unsatisfied_count": self.unsatisfied_count,
            "unprocessed_count": self.unprocessed_count,
            "stor_time_sum": self.stor_time_sum,
            "stor_time_max": self.stor_time_max,
            "stor_time_count": self.stor_time_count,
            "avg_storage_time": self.get_avg_storage_time(),
            "mean_incoming_speed": self.get_overall_mean_incoming_speed(
                SimClock.get_time()
            ),
        }
