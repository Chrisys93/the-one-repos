"""Parameterized RepoReport -- replaces 15+ separate Java report classes.

Each report instance is configured with a metric name and extracts the
corresponding data from the MetricsCollector.
"""

from __future__ import annotations

from typing import Any, Optional

from ..core.sim_clock import SimClock
from .base import Report


class RepoReport(Report):
    """Parameterized report class that writes time-series for one metric."""

    def __init__(
        self,
        metric: str,
        output_path: Optional[str] = None,
        granularity: float = 1.0,
        warmup_time: int = 0,
    ) -> None:
        super().__init__(
            output_path=output_path,
            interval=granularity,
            warmup_time=warmup_time,
        )
        self.metric = metric
        self.granularity = granularity
        self._last_report: float = 0.0

    def updated(self, hosts: list[Any]) -> None:
        cur_time = SimClock.get_time()
        if self.is_warmup(cur_time):
            return
        if cur_time - self._last_report < self.granularity:
            return
        self._last_report = cur_time

        from .collector import METRIC_EXTRACTORS

        extractor = METRIC_EXTRACTORS.get(self.metric)
        if extractor is None:
            return

        parts = [f"{cur_time:.1f}"]
        for host in hosts:
            if not hasattr(host, "storage") or host.storage is None:
                if self.metric == "mobile_deleted":
                    parts.append(
                        str(host.router.get_buffer_deleted_messages_size())
                    )
                continue
            try:
                val = extractor(host)
                if isinstance(val, tuple):
                    parts.append(" ".join(str(v) for v in val))
                else:
                    parts.append(str(val))
            except (AttributeError, TypeError, ZeroDivisionError):
                parts.append("0")

        self.write(" ".join(parts))
