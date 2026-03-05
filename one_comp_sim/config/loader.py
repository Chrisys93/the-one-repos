"""Configuration file loaders: YAML (new) and legacy .txt (original ONE format).

Public API
----------
* :func:`load_config` -- load a YAML configuration and validate it.
* :func:`expand_sweeps` -- generate one :class:`SimulationConfig` per sweep
  combination.
* :func:`load_legacy_config` -- parse the original ONE key=value ``.txt``
  format into a validated :class:`SimulationConfig`.
"""

from __future__ import annotations

import copy
import itertools
import re
from pathlib import Path
from typing import Any

import yaml

from .schema import (
    AIConfig,
    ApplicationConfig,
    InterfaceConfig,
    K8sConfig,
    ManagementConfig,
    MapConfig,
    NodeGroupConfig,
    ReportConfig,
    ScenarioConfig,
    SimulationConfig,
    StorageConfig,
    SweepConfig,
)
from ..core.types import NodeRole, parse_size


# ===================================================================
# YAML loader
# ===================================================================

def load_config(path: str) -> SimulationConfig:
    """Load a YAML configuration file and return a validated model.

    Parameters
    ----------
    path:
        Filesystem path to a ``.yaml`` / ``.yml`` file.

    Returns
    -------
    SimulationConfig
        Fully validated configuration object.
    """
    raw = Path(path).read_text(encoding="utf-8")
    data: dict[str, Any] = yaml.safe_load(raw)
    return SimulationConfig.model_validate(data)


# ===================================================================
# Sweep expansion
# ===================================================================

def expand_sweeps(config: SimulationConfig) -> list[SimulationConfig]:
    """Generate one configuration per combination of sweep values.

    If no sweeps are defined the original configuration is returned inside a
    single-element list.

    Parameters
    ----------
    config:
        A :class:`SimulationConfig` whose ``sweeps`` field may contain one or
        more :class:`SweepConfig` entries.

    Returns
    -------
    list[SimulationConfig]
        One configuration per combination, with the swept parameter replaced.
    """
    if not config.sweeps:
        return [config]

    axes: list[list[tuple[str, Any]]] = []
    for sweep in config.sweeps:
        axes.append([(sweep.parameter, v) for v in sweep.values])

    configs: list[SimulationConfig] = []
    for combo in itertools.product(*axes):
        cfg = config.model_copy(deep=True)
        data = cfg.model_dump()
        for param, value in combo:
            _set_nested(data, param, value)
        # Re-validate after parameter injection
        configs.append(SimulationConfig.model_validate(data))
    return configs


def _set_nested(data: dict[str, Any], dotted_key: str, value: Any) -> None:
    """Set a value in a nested dict using a dotted key path.

    Supports list indexing via numeric path segments (e.g.
    ``node_groups.0.count``).
    """
    parts = dotted_key.split(".")
    obj: Any = data
    for part in parts[:-1]:
        if isinstance(obj, list):
            obj = obj[int(part)]
        else:
            obj = obj[part]
    final = parts[-1]
    if isinstance(obj, list):
        obj[int(final)] = value
    else:
        obj[final] = value


# ===================================================================
# Legacy .txt loader
# ===================================================================

# Regex for the [v1; v2; v3] sweep syntax used in the original ONE settings.
_SWEEP_RE = re.compile(r"^\[(.+)\]$")


def load_legacy_config(path: str) -> SimulationConfig:
    """Parse an original ONE ``.txt`` key=value configuration file.

    The parser handles:

    * ``Key = Value`` lines (``#`` comments, blank lines ignored)
    * Dotted namespaces: ``Group1.x``, ``procApp.x``, ``Scenario.x``, etc.
    * Size suffixes: ``k``, ``M``, ``G`` (delegated to :func:`core.types.parse_size`)
    * Sweep syntax: ``[v1; v2; v3]`` produces a :class:`SweepConfig`

    Parameters
    ----------
    path:
        Filesystem path to a legacy settings file.

    Returns
    -------
    SimulationConfig
        Validated configuration object.
    """
    raw_lines = Path(path).read_text(encoding="utf-8").splitlines()

    props: dict[str, str] = {}
    for line in raw_lines:
        line = line.strip()
        if not line or line.startswith("#"):
            continue
        if "=" not in line:
            continue
        key, _, val = line.partition("=")
        props[key.strip()] = val.strip()

    # ---- Scenario ----------------------------------------------------------
    scenario = _build_scenario(props)

    # ---- Interfaces --------------------------------------------------------
    interfaces = _build_interfaces(props)

    # ---- Applications ------------------------------------------------------
    applications = _build_applications(props)

    # ---- Node groups -------------------------------------------------------
    nrof_groups = int(props.get("Scenario.nrofHostGroups", "0"))
    node_groups = _build_node_groups(props, nrof_groups)

    # ---- Maps --------------------------------------------------------------
    maps = _build_maps(props)

    # ---- Reports -----------------------------------------------------------
    reports = _build_reports(props)

    # ---- Sweeps (collected during value parsing) ---------------------------
    sweeps = _collect_sweeps(props)

    return SimulationConfig(
        scenario=scenario,
        interfaces=interfaces,
        applications=applications,
        node_groups=node_groups,
        maps=maps,
        reports=reports,
        sweeps=sweeps,
    )


# -------------------------------------------------------------------
# Legacy sub-builders
# -------------------------------------------------------------------

_GROUP_ID_TO_ROLE: dict[str, NodeRole] = {
    "r": NodeRole.EDR,
    "p": NodeRole.PEDESTRIAN,
    "c": NodeRole.CAR,
    "b": NodeRole.BUS,
    "io": NodeRole.IOT_OFFICE,
    "ih": NodeRole.IOT_HOME,
}


def _role_from_group_id(gid: str) -> NodeRole:
    """Best-effort mapping from a legacy group-id prefix to a :class:`NodeRole`."""
    for prefix, role in _GROUP_ID_TO_ROLE.items():
        if gid.startswith(prefix):
            return role
    return NodeRole.PEDESTRIAN


def _build_scenario(props: dict[str, str]) -> ScenarioConfig:
    ws_raw = props.get("MovementModel.worldSize", "4500, 3400")
    world_size = [int(float(x.strip())) for x in ws_raw.split(",")]

    return ScenarioConfig(
        name=props.get("Scenario.name", "default_scenario"),
        end_time=float(props.get("Scenario.endTime", "10800")),
        update_interval=float(props.get("Scenario.updateInterval", "0.1")),
        simulate_connections=_parse_bool(props.get("Scenario.simulateConnections", "true")),
        simulate_storage=_parse_bool(props.get("Scenario.simulateStorage", "false")),
        world_size=world_size,
        warmup=float(props.get("MovementModel.warmup", "0")),
    )


def _build_interfaces(props: dict[str, str]) -> dict[str, InterfaceConfig]:
    """Discover interface definitions from ``<name>.type`` keys."""
    interfaces: dict[str, InterfaceConfig] = {}
    seen: set[str] = set()
    for key in props:
        if key.endswith(".type") and "." in key:
            prefix = key.rsplit(".", 1)[0]
            # Skip non-interface prefixes (applications, groups, etc.)
            if prefix.startswith(("Group", "Scenario", "Report", "Events",
                                  "MovementModel", "MapBasedMovement",
                                  "Optimization", "GUI", "ProphetRouter",
                                  "SprayAndWaitRouter")):
                continue
            itype = props[key]
            # Heuristic: interfaces have *Interface in type or transmitRange
            if "Interface" in itype or f"{prefix}.transmitRange" in props:
                if prefix in seen:
                    continue
                seen.add(prefix)

                tr_raw = props.get(f"{prefix}.transmitRange", "0")
                ts_raw = props.get(f"{prefix}.transmitSpeed", "0")
                tspeeds_raw = props.get(f"{prefix}.transmitSpeeds", "")
                si_raw = props.get(f"{prefix}.scanInterval", "0")

                transmit_speeds: list[int] = []
                if tspeeds_raw:
                    transmit_speeds = [
                        int(parse_size(s.strip()))
                        for s in tspeeds_raw.split(",")
                        if s.strip()
                    ]

                interfaces[prefix] = InterfaceConfig(
                    type=itype,
                    transmit_speed=int(parse_size(ts_raw)),
                    transmit_speeds=transmit_speeds,
                    transmit_range=float(tr_raw),
                    scan_interval=float(si_raw),
                )
    return interfaces


def _build_applications(props: dict[str, str]) -> dict[str, ApplicationConfig]:
    """Discover application definitions from ``<name>.type`` keys.

    Application keys are those whose ``.type`` value names a known application
    class (ProcApplication, ProcGenApplication, PingApplication, etc.).
    """
    _APP_TYPES = {"ProcApplication", "ProcGenApplication", "PingApplication"}
    apps: dict[str, ApplicationConfig] = {}
    seen: set[str] = set()
    for key, value in props.items():
        if key.endswith(".type") and value in _APP_TYPES:
            prefix = key.rsplit(".", 1)[0]
            if prefix in seen:
                continue
            seen.add(prefix)
            extras: dict[str, Any] = {}
            for k, v in props.items():
                if k.startswith(f"{prefix}.") and k != key:
                    field = k[len(prefix) + 1:]
                    extras[field] = _coerce_legacy_value(v)
            apps[prefix] = ApplicationConfig(type=value, **extras)
    return apps


def _build_node_groups(
    props: dict[str, str],
    nrof_groups: int,
) -> list[NodeGroupConfig]:
    """Build node group configs from ``Group`` and ``GroupN`` keys."""
    groups: list[NodeGroupConfig] = []

    # Collect base Group.* defaults
    base: dict[str, str] = {}
    for key, value in props.items():
        if key.startswith("Group."):
            field = key[len("Group."):]
            base[field] = value

    for i in range(1, nrof_groups + 1):
        prefix = f"Group{i}"
        merged: dict[str, str] = dict(base)
        for key, value in props.items():
            if key.startswith(f"{prefix}."):
                field = key[len(prefix) + 1:]
                merged[field] = value

        gid = merged.get("groupID", f"g{i}")
        role = _role_from_group_id(gid)
        count = int(merged.get("nrofHosts", merged.get("nrofhosts", "40")))
        movement = merged.get("movementModel", "ShortestPathMapBasedMovement")
        router = merged.get("router", "DirectDeliveryRouter")
        buffer_size = int(parse_size(merged.get("bufferSize", "100M")))

        speed_raw = merged.get("speed", "1.0, 1.5")
        speed = [float(x.strip()) for x in speed_raw.split(",")]

        wait_raw = merged.get("waitTime", "0, 120")
        wait_time = [float(x.strip()) for x in wait_raw.split(",")]

        msg_ttl = int(merged.get("msgTtl", "300"))

        # Interfaces
        nrof_ifaces = int(merged.get("nrofInterfaces", "1"))
        ifaces: list[str] = []
        for j in range(1, nrof_ifaces + 1):
            iface_name = merged.get(f"interface{j}", "")
            if iface_name:
                ifaces.append(iface_name)

        # Storage
        storage: StorageConfig | None = None
        if _parse_bool(merged.get("storageCapability", "false")):
            storage = StorageConfig(
                enabled=True,
                processing=_parse_bool(merged.get("processingCapability", "false")),
                size=int(parse_size(merged.get("storageSize", "100G"))),
                compression_rate=float(merged.get("compressionRate", "1.0")),
            )

        # Applications
        nrof_apps = int(merged.get("nrofApplications", "0"))
        app_names: list[str] = []
        for j in range(1, nrof_apps + 1):
            app_name = merged.get(f"application{j}", "")
            if app_name:
                app_names.append(app_name)

        ok_maps_raw = merged.get("okMaps", "")
        ok_maps: list[int] = []
        if ok_maps_raw:
            ok_maps = [int(x.strip()) for x in ok_maps_raw.split(",") if x.strip()]

        route_file: str | None = merged.get("routeFile")
        route_type: int | None = None
        if "routeType" in merged:
            route_type = int(merged["routeType"])

        groups.append(NodeGroupConfig(
            group_id=gid,
            role=role,
            count=count,
            movement=movement,
            router=router,
            buffer_size=buffer_size,
            speed=speed,
            wait_time=wait_time,
            msg_ttl=msg_ttl,
            interfaces=ifaces,
            storage=storage,
            applications=app_names,
            ok_maps=ok_maps,
            route_file=route_file,
            route_type=route_type,
        ))

    return groups


def _build_maps(props: dict[str, str]) -> MapConfig | None:
    nrof = int(props.get("MapBasedMovement.nrofMapFiles", "0"))
    if nrof == 0:
        return None
    files: list[str] = []
    for i in range(1, nrof + 1):
        f = props.get(f"MapBasedMovement.mapFile{i}", "")
        if f:
            files.append(f)
    return MapConfig(files=files)


def _build_reports(props: dict[str, str]) -> ReportConfig | None:
    nrof = int(props.get("Report.nrofReports", "0"))
    if nrof == 0:
        return None

    metrics: list[str] = []
    for i in range(1, nrof + 1):
        m = props.get(f"Report.report{i}", "")
        if m:
            metrics.append(m)

    return ReportConfig(
        output_dir=props.get("Report.reportDir", "reports/"),
        warmup=int(float(props.get("Report.warmup", "0"))),
        end_time=float(props["Report.endTime"]) if "Report.endTime" in props else None,
        granularity=float(props.get("Report.granularity", "1")),
        metrics=metrics,
    )


def _collect_sweeps(props: dict[str, str]) -> list[SweepConfig]:
    """Detect ``[v1; v2; v3]`` sweep syntax in property values."""
    sweeps: list[SweepConfig] = []
    for key, value in props.items():
        m = _SWEEP_RE.match(value)
        if m:
            raw_values = [v.strip() for v in m.group(1).split(";")]
            parsed = [_coerce_legacy_value(v) for v in raw_values]
            sweeps.append(SweepConfig(parameter=key, values=parsed))
    return sweeps


# -------------------------------------------------------------------
# Helpers
# -------------------------------------------------------------------

def _parse_bool(value: str) -> bool:
    return value.strip().lower() in ("true", "1", "yes")


def _coerce_legacy_value(raw: str) -> Any:
    """Best-effort coercion of a legacy string value to a Python type."""
    raw = raw.strip()

    # Boolean
    if raw.lower() in ("true", "false"):
        return raw.lower() == "true"

    # Size with suffix
    for suffix in ("k", "K", "M", "G", "T"):
        if raw.endswith(suffix):
            try:
                return int(parse_size(raw))
            except (ValueError, TypeError):
                pass

    # Comma-separated integers (e.g. "1,2")
    if "," in raw:
        parts = [p.strip() for p in raw.split(",")]
        try:
            return [int(p) for p in parts]
        except ValueError:
            try:
                return [float(p) for p in parts]
            except ValueError:
                return raw

    # Numeric
    try:
        return int(raw)
    except ValueError:
        pass
    try:
        return float(raw)
    except ValueError:
        pass

    return raw
