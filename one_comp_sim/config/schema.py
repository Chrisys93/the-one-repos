"""Pydantic v2 models for YAML configuration validation.

Every section of the simulator YAML configuration maps to a model defined here.
The top-level :class:`SimulationConfig` aggregates all sub-models and is the
single object returned by :func:`config.loader.load_config`.
"""

from __future__ import annotations

from typing import Any, Dict, List, Optional

from pydantic import BaseModel, ConfigDict, Field

from ..core.types import NodeRole


# ---------------------------------------------------------------------------
# Interface
# ---------------------------------------------------------------------------

class InterfaceConfig(BaseModel):
    """Network interface definition (WiFi, Bluetooth, etc.)."""

    type: str
    transmit_speed: int = 0
    transmit_speeds: List[int] = Field(default_factory=list)
    transmit_range: float
    scan_interval: float = 0.0


# ---------------------------------------------------------------------------
# Storage
# ---------------------------------------------------------------------------

class StorageConfig(BaseModel):
    """Per-node-group repo storage settings."""

    enabled: bool = False
    processing: bool = False
    size: int = 100_000_000_000
    compression_rate: float = 1.0


# ---------------------------------------------------------------------------
# Application
# ---------------------------------------------------------------------------

class ApplicationConfig(BaseModel):
    """Application instance definition.

    Extra keyword arguments are forwarded to the concrete application class
    (e.g. ``interval``, ``delay``, ``passive``, etc.).
    """

    model_config = ConfigDict(extra="allow")

    type: str


# ---------------------------------------------------------------------------
# Node group
# ---------------------------------------------------------------------------

class NodeGroupConfig(BaseModel):
    """Configuration for a single host group."""

    group_id: str
    role: NodeRole
    count: int
    movement: str
    router: str
    buffer_size: int = 100_000_000
    speed: List[float] = Field(default_factory=lambda: [1.0, 1.5])
    wait_time: List[float] = Field(default_factory=lambda: [0, 120])
    msg_ttl: int = 300
    interfaces: List[str]
    storage: Optional[StorageConfig] = None
    applications: List[str] = Field(default_factory=list)
    ok_maps: List[int] = Field(default_factory=list)
    route_file: Optional[str] = None
    route_type: Optional[int] = None


# ---------------------------------------------------------------------------
# Maps
# ---------------------------------------------------------------------------

class MapConfig(BaseModel):
    """Map file references for map-based movement models."""

    files: List[str] = Field(default_factory=list)


# ---------------------------------------------------------------------------
# Sweep
# ---------------------------------------------------------------------------

class SweepConfig(BaseModel):
    """A single parameter sweep axis (generates one run per value)."""

    parameter: str
    values: List[Any]


# ---------------------------------------------------------------------------
# Reports
# ---------------------------------------------------------------------------

class ReportConfig(BaseModel):
    """Output and reporting configuration."""

    output_dir: str = "reports/"
    warmup: int = 0
    end_time: Optional[float] = None
    granularity: float = 1.0
    format: str = "text"
    metrics: List[str] = Field(default_factory=list)


# ---------------------------------------------------------------------------
# Management / K8s / AI extensions
# ---------------------------------------------------------------------------

class ManagementConfig(BaseModel):
    """SEND management plane settings."""

    enabled: bool = False
    stats_interval: float = 60.0
    placement_strategy: str = "general_purpose"


class K8sConfig(BaseModel):
    """Kubernetes integration settings."""

    enabled: bool = False
    cluster_name: str = ""
    namespace_prefix: str = "edr-"


class AIConfig(BaseModel):
    """AI/ML tool-interface export settings."""

    enabled: bool = False
    export_format: str = "json"


# ---------------------------------------------------------------------------
# Scenario
# ---------------------------------------------------------------------------

class ScenarioConfig(BaseModel):
    """Top-level scenario parameters."""

    name: str
    end_time: float
    update_interval: float = 0.1
    simulate_connections: bool = True
    simulate_storage: bool = True
    world_size: List[int]
    warmup: float = 0
    repo_offset: List[float] = Field(default_factory=lambda: [500, 200])


# ---------------------------------------------------------------------------
# Top-level simulation configuration
# ---------------------------------------------------------------------------

class SimulationConfig(BaseModel):
    """Root configuration object validated from YAML or legacy .txt files."""

    scenario: ScenarioConfig
    interfaces: Dict[str, InterfaceConfig]
    applications: Dict[str, ApplicationConfig]
    node_groups: List[NodeGroupConfig]
    maps: Optional[MapConfig] = None
    reports: Optional[ReportConfig] = None
    sweeps: List[SweepConfig] = Field(default_factory=list)
    management: Optional[ManagementConfig] = None
    k8s: Optional[K8sConfig] = None
    ai: Optional[AIConfig] = None
