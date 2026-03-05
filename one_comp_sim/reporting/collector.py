"""MetricsCollector: centralized single-pass time-series collection.

Replaces 21+ separate Java report classes that each iterate over all hosts.
"""

from __future__ import annotations

from dataclasses import dataclass, field
from typing import Any, Callable, Optional, Protocol

from ..core.sim_clock import SimClock


class ReportConsumer(Protocol):
    """Interface for report output sinks."""
    def receive(self, time: float, metric: str, data: dict[str, Any]) -> None: ...


class MetricsCollector:
    """Collects all EDR metrics once per granularity interval."""

    def __init__(self, granularity: float = 1.0) -> None:
        self.granularity = granularity
        self._last_update: float = 0.0
        self._series: dict[str, list[tuple[float, dict[str, Any]]]] = {}
        self._consumers: list[ReportConsumer] = []

    def add_consumer(self, consumer: ReportConsumer) -> None:
        self._consumers.append(consumer)

    def updated(self, hosts: list[Any]) -> None:
        cur_time = SimClock.get_time()
        if cur_time - self._last_update < self.granularity:
            return
        self._last_update = cur_time

        # Single pass over all hosts, extract all metrics
        for metric_name, extractor in METRIC_EXTRACTORS.items():
            data: dict[str, Any] = {}
            for host in hosts:
                if not hasattr(host, "storage") or host.storage is None:
                    if metric_name == "mobile_deleted":
                        data[host.name] = host.router.get_buffer_deleted_messages_size()
                    continue
                try:
                    data[host.name] = extractor(host)
                except (AttributeError, TypeError, ZeroDivisionError):
                    pass

            if data:
                self._series.setdefault(metric_name, []).append((cur_time, data))
                for consumer in self._consumers:
                    consumer.receive(cur_time, metric_name, data)

    def get_time_series(self, metric: str) -> list[tuple[float, dict[str, Any]]]:
        return self._series.get(metric, [])

    def get_all_metrics(self) -> dict[str, list[tuple[float, dict[str, Any]]]]:
        return dict(self._series)


# ---------------------------------------------------------------------------
# Metric extractors -- one per report type from the Java codebase
# ---------------------------------------------------------------------------

def _safe_get(host: Any, attr_chain: str, default: Any = 0) -> Any:
    obj = host
    for attr in attr_chain.split("."):
        obj = getattr(obj, attr, None)
        if obj is None:
            return default
    return obj


METRIC_EXTRACTORS: dict[str, Callable[[Any], Any]] = {
    "incoming_speed": lambda h: h.storage.metrics.get_overall_mean_incoming_speed(
        SimClock.get_time()
    ),
    "stored_messages": lambda h: h.storage.static_size,
    "stored_messages_count": lambda h: len(h.storage.static_messages),
    "processing_messages": lambda h: h.storage.process_size,
    "processing_messages_count": lambda h: len(h.storage.process_messages),
    "processed_messages": lambda h: h.storage.processed_messages_size,
    "processed_messages_count": lambda h: len(h.storage.processed_messages),
    "all_messages": lambda h: h.storage.static_size + h.storage.process_size,
    "depl_proc_bw": lambda h: h.storage.depletion.get_bw("proc_cloud", reporting=True),
    "depl_unproc_bw": lambda h: h.storage.depletion.get_bw("unproc", reporting=True),
    "depl_storage_bw": lambda h: h.storage.depletion.get_bw(
        "static_forced", reporting=True
    ),
    "depl_cloud_storage_bw": lambda h: h.storage.depletion.get_bw(
        "static_cloud", reporting=True
    ),
    "satisfaction": lambda h: (
        h.storage.metrics.satisfied_count,
        h.storage.metrics.unsatisfied_count,
    ),
    "freshness": lambda h: (
        h.storage.metrics.fresh_count,
        h.storage.metrics.stale_count,
    ),
    "overtime": lambda h: h.storage.metrics.overtime_count,
    "unprocessed": lambda h: h.storage.metrics.unprocessed_count,
    "storage_times": lambda h: (
        h.storage.metrics.get_avg_storage_time(),
        h.storage.metrics.stor_time_max,
    ),
    "mobile_deleted": lambda h: h.router.get_buffer_deleted_messages_size(),
}
