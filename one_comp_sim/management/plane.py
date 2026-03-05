"""SEND-style centralised management plane.

The :class:`ManagementPlane` periodically collects per-EDR statistics,
feeds them to a :class:`~management.placement.PlacementStrategy`, and
applies the resulting :class:`~management.placement.ManagementAction`
list to the simulated world.
"""

from __future__ import annotations

import logging
from typing import Any

from ..core.sim_clock import SimClock
from .labels import LabelManager
from .placement import (
    EDRStats,
    ManagementAction,
    PlacementStrategy,
    GeneralPurposePlacement,
    PopularityBasedPlacement,
    FunctionBasedPlacement,
    HybridPlacement,
)
from .replication import StorageAwareReplication, InterEDRTransfer

log = logging.getLogger(__name__)

# ---------------------------------------------------------------------------
# Strategy factory
# ---------------------------------------------------------------------------

_STRATEGY_REGISTRY: dict[str, type[PlacementStrategy]] = {
    "general_purpose": GeneralPurposePlacement,
    "popularity_based": PopularityBasedPlacement,
    "function_based": FunctionBasedPlacement,
    "hybrid": HybridPlacement,
}


def create_placement_strategy(name: str) -> PlacementStrategy:
    """Instantiate a :class:`PlacementStrategy` by its config name."""
    cls = _STRATEGY_REGISTRY.get(name)
    if cls is None:
        log.warning(
            "Unknown placement strategy %r, falling back to general_purpose", name
        )
        cls = GeneralPurposePlacement
    return cls()


class ManagementPlane:
    """Centralised controller for SEND placement and replication.

    Parameters
    ----------
    stats_interval:
        How often (in simulation seconds) the plane collects stats and
        makes decisions.
    placement_strategy:
        The :class:`PlacementStrategy` implementation used to produce
        actions each collection interval.
    label_manager:
        Shared :class:`LabelManager` instance for taxonomy look-ups.
    """

    def __init__(
        self,
        stats_interval: float,
        placement_strategy: PlacementStrategy,
        label_manager: LabelManager,
    ) -> None:
        self.stats_interval: float = stats_interval
        self.placement_strategy: PlacementStrategy = placement_strategy
        self.label_manager: LabelManager = label_manager
        self._edr_stats: dict[str, EDRStats] = {}
        self._last_update: float = 0.0
        self._hosts: list[Any] = []
        self._transfer: InterEDRTransfer = InterEDRTransfer()
        self._replication_policy: StorageAwareReplication = StorageAwareReplication()

    # ------------------------------------------------------------------
    # Statistics collection
    # ------------------------------------------------------------------

    def collect_stats(self, hosts: list[Any]) -> None:
        """Gather per-EDR stats from every host in *hosts*.

        Each host is expected to expose:

        * ``name: str``
        * ``storage: RepoStorage`` (with ``total_capacity``, ``static_size``,
          ``process_size``, ``processed_messages_size``, and compartment lists)
        * ``get_overall_mean_incoming_speed() -> float`` (optional)

        Hosts that do not have a ``storage`` attribute are silently skipped.
        """
        self._edr_stats.clear()

        for host in hosts:
            storage: Any = getattr(host, "storage", None)
            if storage is None:
                continue

            host_name: str = getattr(host, "name", str(host))
            total_cap: int = getattr(storage, "total_capacity", 1)
            used: int = (
                getattr(storage, "static_size", 0)
                + getattr(storage, "process_size", 0)
                + getattr(storage, "processed_messages_size", 0)
            )
            storage_util: float = used / total_cap if total_cap > 0 else 0.0

            # Processing utilisation: derive from processing queue vs capacity.
            proc_cap: int = getattr(storage, "processed_capacity", 1) or 1
            proc_used: int = getattr(storage, "process_size", 0)
            processing_util: float = proc_used / proc_cap if proc_cap > 0 else 0.0

            incoming_rate: float = 0.0
            get_speed = getattr(storage, "get_overall_mean_incoming_speed", None)
            if get_speed is not None:
                incoming_rate = get_speed()

            depletion_rate: float = 0.0
            depletion_tracker: Any = getattr(storage, "depletion", None)
            if depletion_tracker is not None:
                for channel in (
                    "proc_cloud",
                    "static_cloud",
                    "proc_forced",
                    "static_forced",
                ):
                    get_bw = getattr(depletion_tracker, "get_bw", None)
                    if get_bw is not None:
                        depletion_rate += get_bw(channel)

            # Message-type distribution
            msg_type_dist: dict[str, int] = {}
            for compartment_name in (
                "static_messages",
                "process_messages",
                "processed_messages",
            ):
                compartment: list[Any] = getattr(storage, compartment_name, [])
                for msg in compartment:
                    mt: str = getattr(msg, "message_type", "unknown")
                    if hasattr(mt, "value"):
                        mt = mt.value
                    msg_type_dist[mt] = msg_type_dist.get(mt, 0) + 1

            # Label distribution
            label_dist: dict[str, int] = {}
            for compartment_name in (
                "static_messages",
                "process_messages",
                "processed_messages",
            ):
                compartment = getattr(storage, compartment_name, [])
                for msg in compartment:
                    for lbl in getattr(msg, "labels", set()):
                        label_dist[lbl] = label_dist.get(lbl, 0) + 1

            self._edr_stats[host_name] = EDRStats(
                host_name=host_name,
                storage_utilization=storage_util,
                processing_utilization=processing_util,
                incoming_rate=incoming_rate,
                depletion_rate=depletion_rate,
                message_type_distribution=msg_type_dist,
                label_distribution=label_dist,
            )

    # ------------------------------------------------------------------
    # Decision making
    # ------------------------------------------------------------------

    def make_decisions(self) -> list[ManagementAction]:
        """Invoke the placement strategy on collected stats.

        The topology is derived from the stat keys (flat fully-connected
        assumption for now -- concrete topology hooks would replace this).
        """
        if not self._edr_stats:
            return []

        # Build a flat fully-connected topology as a placeholder.
        all_hosts: list[str] = list(self._edr_stats.keys())
        topology: dict[str, list[str]] = {
            h: [o for o in all_hosts if o != h] for h in all_hosts
        }

        return self.placement_strategy.decide(self._edr_stats, topology)

    # ------------------------------------------------------------------
    # Action application (stub)
    # ------------------------------------------------------------------

    def apply_actions(
        self,
        world: Any,
        actions: list[ManagementAction],
    ) -> None:
        """Apply *actions* to the simulated world.

        Executes replicate/migrate/rebalance actions using the
        :class:`StorageAwareReplication` policy and :class:`InterEDRTransfer`.
        """
        if not self._hosts:
            for action in actions:
                log.info(
                    "ManagementPlane action (no hosts): %s %s -> %s  params=%s",
                    action.action_type, action.source_edr,
                    action.target_edr, action.parameters,
                )
            return

        host_map: dict[str, Any] = {
            getattr(h, "name", str(h)): h for h in self._hosts
        }

        for action in actions:
            source = host_map.get(action.source_edr)
            target = host_map.get(action.target_edr)

            if source is None or target is None:
                log.warning(
                    "ManagementPlane: cannot resolve hosts for %s -> %s",
                    action.source_edr, action.target_edr,
                )
                continue

            log.info(
                "ManagementPlane action: %s from %s -> %s  params=%s",
                action.action_type, action.source_edr,
                action.target_edr, action.parameters,
            )

            if action.action_type in ("replicate", "migrate", "rebalance"):
                source_storage = getattr(source, "storage", None)
                if source_storage is None:
                    continue
                all_msgs = (
                    list(source_storage.static_messages)
                    + list(source_storage.process_messages)
                    + list(source_storage.processed_messages)
                )
                candidates = [
                    m for m in all_msgs
                    if self._replication_policy.should_replicate(m, source, target)
                ]
                if candidates:
                    self._transfer.initiate_transfer(source, target, candidates)

    # ------------------------------------------------------------------
    # Periodic update
    # ------------------------------------------------------------------

    def updated(self, hosts: list[Any]) -> None:
        """World listener interface -- called every simulation tick.

        Delegates to :meth:`update` with the current simulation time.
        """
        self.update(hosts, SimClock.get_time())

    def update(self, hosts: list[Any], sim_time: float) -> None:
        """Called every simulation tick; acts only at *stats_interval* boundaries.

        Parameters
        ----------
        hosts:
            All DTNHost instances in the simulation.
        sim_time:
            Current simulation time in seconds.
        """
        if sim_time - self._last_update < self.stats_interval:
            return

        self._last_update = sim_time
        self._hosts = hosts
        self.collect_stats(hosts)
        actions: list[ManagementAction] = self.make_decisions()
        if actions:
            self.apply_actions(None, actions)
