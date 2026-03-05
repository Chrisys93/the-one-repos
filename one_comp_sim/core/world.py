"""World: main simulation loop.

Port of Java World.java -- time-stepped engine where every host is updated
every interval.
"""

from __future__ import annotations

import random
import time as wall_time
from dataclasses import dataclass, field
from typing import TYPE_CHECKING, Any, Optional

from .events import EventQueue, ScheduledUpdatesQueue
from .sim_clock import SimClock

if TYPE_CHECKING:
    from .host import DTNHost
    from ..reporting.collector import MetricsCollector


@dataclass
class SimulationResult:
    """Structured output from a simulation run."""
    scenario_name: str = ""
    sim_time: float = 0.0
    wall_time_seconds: float = 0.0
    summary: dict[str, Any] = field(default_factory=dict)
    time_series: Optional[dict[str, list]] = None
    config_used: dict = field(default_factory=dict)
    warnings: list[str] = field(default_factory=list)


class World:
    """The simulation world -- manages hosts, connections, and the time loop."""

    def __init__(
        self,
        hosts: list[DTNHost],
        size_x: int = 4500,
        size_y: int = 3400,
        update_interval: float = 0.1,
        simulate_connections: bool = True,
        randomize_updates: bool = True,
        end_time: float = 10800.0,
        warmup_time: float = 0.0,
    ) -> None:
        self.hosts = hosts
        self.size_x = size_x
        self.size_y = size_y
        self.update_interval = update_interval
        self.simulate_connections = simulate_connections
        self.end_time = end_time
        self._randomize_updates = randomize_updates
        self._cancelled = False

        # Event queues
        self.event_queues: list[EventQueue] = []
        self.scheduled_updates = ScheduledUpdatesQueue()

        # Update listeners (reports, management plane, etc.)
        self.update_listeners: list[Any] = []

        # Metrics collector (optional)
        self.metrics_collector: Optional[MetricsCollector] = None

        # Management plane (optional, set by SimScenario)
        self.management_plane: Any = None

        # K8s integration hooks (optional, set by SimScenario)
        self.k8s_mapper: Any = None
        self.k8s_hook: Any = None
        self.k8s_namespace_router: Any = None
        self.k8s_cluster: Any = None

        # AI hooks (optional, set by SimScenario)
        self.recommender: Any = None
        self.dataset_registry: Any = None
        self.architecture_registry: Any = None

        # Clock
        self._clock = SimClock.get_instance()

        # Back-reference so hosts can find their world
        for h in hosts:
            h._world = self

        # Warmup
        if warmup_time > 0:
            self.warmup_movement(warmup_time)

    def warmup_movement(self, warmup_time: float) -> None:
        """Pre-simulation movement warmup."""
        steps = int(warmup_time / self.update_interval)
        for _ in range(steps):
            for host in self.hosts:
                host.move(self.update_interval)

    def update(self) -> None:
        """One simulation step."""
        cur_time = SimClock.get_time()

        # 1. Process external events due before next interval
        next_time = cur_time + self.update_interval
        for eq in self.event_queues:
            while eq.next_events_time() <= next_time:
                event = eq.next_event()
                if event is not None:
                    event.process_event(self)

        while self.scheduled_updates.next_events_time() <= next_time:
            event = self.scheduled_updates.next_event()
            if event is not None:
                event.process_event(self)

        # 2. Move hosts
        update_order = list(range(len(self.hosts)))
        if self._randomize_updates:
            random.shuffle(update_order)

        for idx in update_order:
            self.hosts[idx].move(self.update_interval)

        # 3. Update hosts (connections + routing + applications)
        for idx in update_order:
            self.hosts[idx].update(self.simulate_connections)

        # 4. Notify update listeners (reports)
        for listener in self.update_listeners:
            if hasattr(listener, "updated"):
                listener.updated(self.hosts)

        if self.metrics_collector is not None:
            self.metrics_collector.updated(self.hosts)

        # Advance clock
        self._clock.advance(self.update_interval)

    def run(self, end_time: float | None = None) -> SimulationResult:
        """Run simulation to completion."""
        if end_time is not None:
            self.end_time = end_time

        start_wall = wall_time.time()

        while SimClock.get_time() < self.end_time and not self._cancelled:
            self.update()

        elapsed = wall_time.time() - start_wall

        # Build summary
        summary: dict[str, Any] = {}
        for host in self.hosts:
            if host.storage is not None:
                summary[host.name] = host.storage.metrics.snapshot()

        result = SimulationResult(
            sim_time=SimClock.get_time(),
            wall_time_seconds=elapsed,
            summary=summary,
        )

        # Attach time series if collector available
        if self.metrics_collector is not None:
            result.time_series = self.metrics_collector.get_all_metrics()

        # Finalize reports
        for listener in self.update_listeners:
            if hasattr(listener, "done"):
                listener.done()

        return result

    def step(self) -> bool:
        """Single step for interactive/tool use. Returns False when done."""
        if SimClock.get_time() >= self.end_time or self._cancelled:
            return False
        self.update()
        return True

    def cancel(self) -> None:
        self._cancelled = True

    def get_host_by_name(self, prefix: str, requester: Any = None) -> Optional[DTNHost]:
        """Find a host whose name contains the given prefix."""
        for host in self.hosts:
            if prefix in host.name and host != requester:
                return host
        return None

    # Aliases for application compatibility
    get_node_by_name = get_host_by_name

    def get_hosts(self) -> list:
        return self.hosts
