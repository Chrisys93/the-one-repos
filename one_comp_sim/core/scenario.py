"""SimScenario: build the simulation world from configuration.

Port of Java SimScenario.java -- creates hosts, assigns interfaces, routers,
movement models, and applications based on config.
"""

from __future__ import annotations

from typing import TYPE_CHECKING, Any, Optional

from .host import DTNHost, reset_address_counter
from .sim_clock import SimClock
from .types import NodeRole
from .world import World
from ..movement.stationary import RepoStationaryMovement

if TYPE_CHECKING:
    from ..config.schema import SimulationConfig


class SimScenario:
    """Builds the World from a SimulationConfig."""

    def __init__(self, config: SimulationConfig) -> None:
        self.config = config

    def build(self) -> World:
        """Construct the complete simulation world."""
        # Reset state
        reset_address_counter()
        SimClock.reset()
        RepoStationaryMovement.reset_placement()
        from ..interfaces.connectivity_grid import reset_grids
        reset_grids()

        sc = self.config.scenario
        hosts: list[DTNHost] = []

        # Load map if configured
        sim_map = None
        if self.config.maps and self.config.maps.files:
            from ..movement.map_data.sim_map import SimMap
            sim_map = SimMap.from_wkt_files(self.config.maps.files)

        # Create hosts for each node group
        host_counter: dict[str, int] = {}
        for group_cfg in self.config.node_groups:
            gid = group_cfg.group_id
            host_counter.setdefault(gid, 0)

            for i in range(group_cfg.count):
                idx = host_counter[gid]
                host_counter[gid] += 1
                name = f"{gid}{idx}"

                # Create movement model
                movement = self._create_movement(group_cfg, sim_map, sc)

                # Create router
                router = self._create_router(group_cfg)

                # Create interfaces
                interfaces = self._create_interfaces(group_cfg)

                # Determine capabilities
                has_storage = False
                has_processing = False
                storage_size = 0
                compression_rate = 1.0
                if group_cfg.storage:
                    has_storage = group_cfg.storage.enabled
                    has_processing = group_cfg.storage.processing
                    storage_size = group_cfg.storage.size
                    compression_rate = group_cfg.storage.compression_rate

                host = DTNHost(
                    name=name,
                    group_id=gid,
                    role=group_cfg.role,
                    router=router,
                    movement=movement,
                    interfaces=interfaces,
                    has_storage=has_storage,
                    has_processing=has_processing,
                    storage_size=storage_size,
                    compression_rate=compression_rate,
                    msg_ttl=group_cfg.msg_ttl,
                )

                # Initialize storage if capable
                if has_storage:
                    from ..storage.repo_storage import RepoStorage
                    host.storage = RepoStorage(
                        host=host,
                        total_capacity=storage_size,
                        compression_rate=compression_rate,
                    )

                # Set initial location
                host.location = movement.get_initial_location()

                # Attach applications
                for app_name in group_cfg.applications:
                    app = self._create_application(app_name)
                    if app is not None:
                        router.add_application(app)

                hosts.append(host)

        # Build world
        world = World(
            hosts=hosts,
            size_x=sc.world_size[0],
            size_y=sc.world_size[1],
            update_interval=sc.update_interval,
            simulate_connections=sc.simulate_connections,
            end_time=sc.end_time,
            warmup_time=sc.warmup,
        )

        # Set up metrics collector
        if self.config.reports:
            from ..reporting.collector import MetricsCollector
            collector = MetricsCollector(
                granularity=self.config.reports.granularity
            )
            world.metrics_collector = collector

            # Create report instances
            for metric in self.config.reports.metrics:
                from ..reporting.repo_reports import RepoReport
                report = RepoReport(
                    metric=metric,
                    output_path=f"{self.config.reports.output_dir}/{metric}.txt",
                    granularity=self.config.reports.granularity,
                    warmup_time=self.config.reports.warmup,
                )
                report.init()
                world.update_listeners.append(report)

        # Initialize management plane if enabled
        if self.config.management and self.config.management.enabled:
            from ..management.plane import ManagementPlane, create_placement_strategy
            from ..management.labels import LabelManager
            strategy = create_placement_strategy(
                self.config.management.placement_strategy
            )
            label_manager = LabelManager()
            # Register default taxonomy labels
            for cat, names in (
                ("message_type", ("nonproc", "proc", "processed")),
                ("compression", ("compress", "delete")),
            ):
                for n in names:
                    label_manager.register_label(cat, n)
            mgmt = ManagementPlane(
                stats_interval=self.config.management.stats_interval,
                placement_strategy=strategy,
                label_manager=label_manager,
            )
            world.management_plane = mgmt
            world.update_listeners.append(mgmt)

        # Initialize K8s integration if enabled
        if self.config.k8s and self.config.k8s.enabled:
            from ..k8s.mapper import K8sMapper
            from ..k8s.hooks import LoggingK8sHook
            from ..k8s.routing_rules import NamespaceRouter
            mapper = K8sMapper()
            k8s_hook = LoggingK8sHook()
            ns_router = NamespaceRouter()
            # Map topology and populate host metadata
            cluster = mapper.generate_topology(world)
            for host in hosts:
                host.namespace = (
                    f"{self.config.k8s.namespace_prefix}{host.group_id}"
                )
                if host.has_storage:
                    host.k8s_metadata = {
                        "pod": mapper.map_edr_to_pod(host),
                        "cluster_name": self.config.k8s.cluster_name,
                        "namespace": host.namespace,
                    }
            world.k8s_mapper = mapper
            world.k8s_hook = k8s_hook
            world.k8s_namespace_router = ns_router
            world.k8s_cluster = cluster

        # Initialize AI hooks if enabled
        if self.config.ai and self.config.ai.enabled:
            from ..ai.recommender import BaselineRecommender
            from ..ai.dataset_registry import DatasetRegistry
            from ..ai.architecture_registry import ArchitectureRegistry
            world.recommender = BaselineRecommender()
            world.dataset_registry = DatasetRegistry()
            world.architecture_registry = ArchitectureRegistry()

        return world

    def _create_movement(self, group_cfg: Any, sim_map: Any, sc: Any) -> Any:
        from ..movement.base import MovementModel
        from ..movement.stationary import RepoStationaryMovement
        from ..movement.map_route import MapRouteMovement

        name = group_cfg.movement
        world_size = sc.world_size
        repo_offset = sc.repo_offset if hasattr(sc, "repo_offset") else [500, 200]

        if name == "RepoStationaryMovement":
            return RepoStationaryMovement(
                max_x=world_size[0], max_y=world_size[1],
                x_offset=repo_offset[0], y_offset=repo_offset[1],
                total_count=group_cfg.count,
            )
        elif name == "MapRouteMovement":
            return MapRouteMovement(
                route_file=group_cfg.route_file or "",
                route_type=group_cfg.route_type or 1,
                min_speed=group_cfg.speed[0] if group_cfg.speed else 4.0,
                max_speed=group_cfg.speed[1] if len(group_cfg.speed) > 1 else 10.0,
                min_wait=group_cfg.wait_time[0] if group_cfg.wait_time else 10.0,
                max_wait=group_cfg.wait_time[1] if len(group_cfg.wait_time) > 1 else 30.0,
                max_x=world_size[0], max_y=world_size[1],
            )
        elif name in ("ShortestPathMapBasedMovement", "MapBasedMovement"):
            from ..movement.map_based import ShortestPathMapBasedMovement
            return ShortestPathMapBasedMovement(
                sim_map=sim_map if sim_map else _empty_map(),
                ok_maps=group_cfg.ok_maps or None,
                min_speed=group_cfg.speed[0] if group_cfg.speed else 0.5,
                max_speed=group_cfg.speed[1] if len(group_cfg.speed) > 1 else 1.5,
                min_wait=group_cfg.wait_time[0] if group_cfg.wait_time else 0.0,
                max_wait=group_cfg.wait_time[1] if len(group_cfg.wait_time) > 1 else 120.0,
                max_x=world_size[0], max_y=world_size[1],
            )
        else:
            # Fallback: random waypoint
            from ..movement.map_based import MapBasedMovement
            return MapBasedMovement(
                sim_map=sim_map if sim_map else _empty_map(),
                min_speed=group_cfg.speed[0] if group_cfg.speed else 0.5,
                max_speed=group_cfg.speed[1] if len(group_cfg.speed) > 1 else 1.5,
                max_x=world_size[0], max_y=world_size[1],
            )

    def _create_router(self, group_cfg: Any) -> Any:
        from ..routing.direct_delivery import RepoDirectDeliveryRouter
        from ..routing.first_contact import ReposFirstContactRouter

        name = group_cfg.router
        buf = group_cfg.buffer_size
        ttl = group_cfg.msg_ttl

        if name == "ReposFirstContactRouter":
            return ReposFirstContactRouter(buffer_size=buf, msg_ttl=ttl)
        else:
            return RepoDirectDeliveryRouter(buffer_size=buf, msg_ttl=ttl)

    def _create_interfaces(self, group_cfg: Any) -> list:
        interfaces = []
        for iface_name in group_cfg.interfaces:
            iface_cfg = self.config.interfaces.get(iface_name)
            if iface_cfg is None:
                continue
            iface = self._build_interface(iface_cfg)
            if iface is not None:
                interfaces.append(iface)
        return interfaces

    def _build_interface(self, cfg: Any) -> Any:
        if cfg.type == "DistanceCapacityInterface":
            from ..interfaces.distance_capacity import DistanceCapacityInterface
            return DistanceCapacityInterface(
                transmit_speeds=cfg.transmit_speeds,
                transmit_range=cfg.transmit_range,
            )
        else:
            from ..interfaces.simple_broadcast import SimpleBroadcastInterface
            return SimpleBroadcastInterface(
                transmit_speed=cfg.transmit_speed,
                transmit_range=cfg.transmit_range,
            )

    def _create_application(self, app_name: str) -> Any:
        app_cfg = self.config.applications.get(app_name)
        if app_cfg is None:
            return None

        app_type = app_cfg.type

        if app_type == "ProcApplication":
            from ..applications.proc_application import ProcApplication
            return ProcApplication(
                passive=getattr(app_cfg, "passive", False),
                storage_mode=getattr(app_cfg, "storage_mode", False),
                max_storage_time=getattr(app_cfg, "max_storage_time", 2000.0),
                max_storage_ratio=getattr(app_cfg, "max_storage", 0.95),
                min_storage_ratio=getattr(app_cfg, "min_storage", 0.001),
                depletion_rate=getattr(app_cfg, "depletion_rate", 20_000_000),
                cloud_rate=getattr(app_cfg, "cloud_rate", 10_000_000),
                core_count=getattr(app_cfg, "core_count", 10),
            )
        elif app_type == "ProcGenApplication":
            from ..applications.proc_gen_application import ProcGenApplication
            pr = getattr(app_cfg, "passive_rate", [1, 0])
            if isinstance(pr, list) and len(pr) >= 2:
                passive_rate = (pr[0], pr[1])
            else:
                passive_rate = (1, 0)
            cr = getattr(app_cfg, "compress_rate", [1, 0])
            if isinstance(cr, list) and len(cr) >= 2:
                compress_rate = (cr[0], cr[1])
            else:
                compress_rate = (1, 0)
            return ProcGenApplication(
                interval=getattr(app_cfg, "interval", 2.0),
                delay=getattr(app_cfg, "delay", 0.1),
                fresh_period=getattr(app_cfg, "fresh_period", 1.0),
                proc_shelf=getattr(app_cfg, "proc_shelf", 2.0),
                nonproc_shelf=getattr(app_cfg, "nonproc_shelf", 3.0),
                passive=getattr(app_cfg, "passive", False),
                passive_rate=passive_rate,
                compress_rate=compress_rate,
                message_size=getattr(app_cfg, "message_size", 1_000_000),
                destination_name=getattr(app_cfg, "destination_name", "r"),
                seed=getattr(app_cfg, "seed", 0),
            )
        return None


def _empty_map() -> Any:
    from ..movement.map_data.sim_map import SimMap
    return SimMap()
