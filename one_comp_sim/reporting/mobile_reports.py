"""Mobile node reports: deleted messages, message locations."""

from __future__ import annotations

from typing import Any, Optional

from ..core.sim_clock import SimClock
from .base import Report


class MobileDeletedMessagesReport(Report):
    """Report tracking messages dropped from mobile node buffers."""

    def __init__(
        self,
        output_path: Optional[str] = None,
        granularity: float = 1.0,
        warmup_time: int = 0,
    ) -> None:
        super().__init__(output_path=output_path, warmup_time=warmup_time)
        self.granularity = granularity
        self._last_report: float = 0.0

    def updated(self, hosts: list[Any]) -> None:
        cur_time = SimClock.get_time()
        if self.is_warmup(cur_time):
            return
        if cur_time - self._last_report < self.granularity:
            return
        self._last_report = cur_time

        parts = [f"{cur_time:.1f}"]
        for host in hosts:
            if hasattr(host, "has_storage") and not host.has_storage:
                parts.append(
                    str(host.router.get_buffer_deleted_messages_size())
                )
        self.write(" ".join(parts))


class MobileMessageLocationReport(Report):
    """Report tracking messages currently in mobile node buffers."""

    def __init__(
        self,
        output_path: Optional[str] = None,
        granularity: float = 1.0,
        warmup_time: int = 0,
    ) -> None:
        super().__init__(output_path=output_path, warmup_time=warmup_time)
        self.granularity = granularity
        self._last_report: float = 0.0

    def updated(self, hosts: list[Any]) -> None:
        cur_time = SimClock.get_time()
        if self.is_warmup(cur_time):
            return
        if cur_time - self._last_report < self.granularity:
            return
        self._last_report = cur_time

        parts = [f"{cur_time:.1f}"]
        for host in hosts:
            if hasattr(host, "has_storage") and not host.has_storage:
                parts.append(str(host.router.get_nrof_messages()))
        self.write(" ".join(parts))
