"""LLM-callable simulator tool interface.

:class:`SimulatorTool` exposes the simulator as a structured tool that can be
invoked by an LLM (e.g. via function-calling / tool-use).  It accepts a
:class:`SimulationRequest`, runs (or stubs) the simulation, and returns a
:class:`SimulationResult` with summary metrics and optional time-series data.
"""

from __future__ import annotations

import datetime
import time
from dataclasses import dataclass, field
from typing import Any, Optional


@dataclass
class SimulationRequest:
    """Parameters for a single simulation invocation.

    Attributes
    ----------
    config:
        Full scenario configuration dict (same schema accepted by the config
        loader).
    run_count:
        Number of independent replications to execute.
    metrics_of_interest:
        Subset of metric names to include in the result summary.  An empty
        list means "return all available metrics".
    return_time_series:
        If ``True``, include per-tick metric values in the result.
    max_sim_time:
        Optional cap on simulation time (seconds).
    """

    config: dict
    run_count: int = 1
    metrics_of_interest: list[str] = field(default_factory=list)
    return_time_series: bool = False
    max_sim_time: Optional[float] = None


@dataclass
class SimulationResult:
    """Structured result returned after one simulation run.

    Attributes
    ----------
    scenario_name:
        Name of the scenario that was executed.
    sim_time:
        Simulation time at which the run ended (seconds).
    wall_time:
        Wall-clock duration of the run (seconds).
    summary:
        Flat dict of summary metrics (e.g. delivery ratio, storage fill).
    time_series:
        Optional per-metric time-series data (metric name -> list of values).
    config_used:
        The resolved configuration dict that was used.
    warnings:
        Any non-fatal warnings produced during the run.
    """

    scenario_name: str
    sim_time: float
    wall_time: float
    summary: dict[str, Any]
    time_series: Optional[dict[str, list]] = None
    config_used: dict = field(default_factory=dict)
    warnings: list[str] = field(default_factory=list)


class SimulatorTool:
    """LLM-callable interface to the edge network simulator.

    Loads configuration, builds the simulation world, runs it, and returns
    structured results.  Optionally integrates with :class:`~ai.recommender.Recommender`,
    :class:`~ai.dataset_registry.DatasetRegistry`, and
    :class:`~ai.architecture_registry.ArchitectureRegistry` when provided.
    """

    def __init__(
        self,
        recommender: Any = None,
        dataset_registry: Any = None,
        architecture_registry: Any = None,
    ) -> None:
        self.recommender = recommender
        self.dataset_registry = dataset_registry
        self.architecture_registry = architecture_registry

    # ------------------------------------------------------------------
    # Single run
    # ------------------------------------------------------------------

    def run(self, request: SimulationRequest) -> SimulationResult:
        """Execute a single simulation run.

        Loads the configuration, builds the simulation world, runs it,
        and returns structured results.
        """
        start: float = time.monotonic()
        warnings: list[str] = []

        scenario_name: str = request.config.get("scenario", {}).get("name", "unnamed")

        try:
            from one_comp_sim.config.schema import SimulationConfig
            from one_comp_sim.core.scenario import SimScenario

            config = SimulationConfig.model_validate(request.config)
            scenario = SimScenario(config)
            world = scenario.build()

            # Pick up AI hooks from world if not already set
            if self.recommender is None:
                self.recommender = getattr(world, "recommender", None)
            if self.dataset_registry is None:
                self.dataset_registry = getattr(world, "dataset_registry", None)
            if self.architecture_registry is None:
                self.architecture_registry = getattr(
                    world, "architecture_registry", None
                )

            end_time = request.max_sim_time or config.scenario.end_time
            world_result = world.run(end_time=end_time)

            summary = world_result.summary
            ts = world_result.time_series if request.return_time_series else None

            # Filter to requested metrics if specified
            if request.metrics_of_interest and isinstance(summary, dict):
                summary = {
                    k: v for k, v in summary.items()
                    if k in request.metrics_of_interest
                }

            # Generate recommendations if recommender is wired
            if self.recommender is not None:
                try:
                    summary["_recommendations"] = {
                        "placement": self.recommender.suggest_placement(
                            [summary], {}
                        ),
                        "processing": self.recommender.suggest_processing_config(
                            [summary]
                        ),
                        "k8s_resources": self.recommender.suggest_k8s_resources(
                            [summary]
                        ),
                    }
                except Exception as rec_exc:
                    warnings.append(f"Recommender failed: {rec_exc}")

            wall: float = time.monotonic() - start

            result = SimulationResult(
                scenario_name=scenario_name,
                sim_time=world_result.sim_time,
                wall_time=wall,
                summary=summary,
                time_series=ts,
                config_used=dict(request.config),
                warnings=warnings,
            )

            # Auto-register in dataset registry
            if self.dataset_registry is not None:
                try:
                    from .dataset_registry import DatasetEntry
                    self.dataset_registry.register(DatasetEntry(
                        name=f"run_{scenario_name}",
                        description=f"Simulation run of {scenario_name}",
                        scenario_name=scenario_name,
                        labels=set(summary.keys()) if isinstance(summary, dict) else set(),
                        format="json",
                        created_at=datetime.datetime.now(
                            datetime.timezone.utc
                        ).isoformat(),
                        metadata={"sim_time": world_result.sim_time, "wall_time": wall},
                    ))
                except Exception as reg_exc:
                    warnings.append(f"Dataset registry failed: {reg_exc}")

            # Auto-register in architecture registry
            if self.architecture_registry is not None:
                try:
                    from .architecture_registry import ArchitectureEntry
                    node_groups = request.config.get("node_groups", [])
                    self.architecture_registry.register(ArchitectureEntry(
                        name=scenario_name,
                        version="1.0.0",
                        description=f"Architecture for {scenario_name}",
                        config=dict(request.config),
                        topology_summary={
                            "node_groups": len(node_groups),
                            "total_nodes": sum(
                                g.get("count", 0) for g in node_groups
                            ),
                        },
                        performance_summary=dict(summary) if isinstance(summary, dict) else {},
                        created_at=datetime.datetime.now(
                            datetime.timezone.utc
                        ).isoformat(),
                    ))
                except Exception as arch_exc:
                    warnings.append(f"Architecture registry failed: {arch_exc}")

            return result

        except Exception as exc:
            warnings.append(f"Simulation failed: {exc}")
            wall = time.monotonic() - start
            return SimulationResult(
                scenario_name=scenario_name,
                sim_time=0.0,
                wall_time=wall,
                summary={},
                config_used=dict(request.config),
                warnings=warnings,
            )

    # ------------------------------------------------------------------
    # Parameter sweep
    # ------------------------------------------------------------------

    def run_sweep(self, request: SimulationRequest) -> list[SimulationResult]:
        """Execute *request.run_count* independent runs and return all results.

        Each run uses the same configuration.  In production, seeds or
        stochastic parameters would vary per run.
        """
        results: list[SimulationResult] = []
        for i in range(request.run_count):
            result: SimulationResult = self.run(request)
            result.warnings.append(f"Sweep run {i + 1} of {request.run_count}")
            results.append(result)
        return results

    # ------------------------------------------------------------------
    # Schema / introspection
    # ------------------------------------------------------------------

    def get_schema(self) -> dict:
        """Return a JSON-Schema-style description of :class:`SimulationRequest`.

        Intended for LLM tool-use registration.
        """
        return {
            "name": "run_simulation",
            "description": (
                "Run an edge network simulation with the given configuration "
                "and return summary metrics."
            ),
            "parameters": {
                "type": "object",
                "properties": {
                    "config": {
                        "type": "object",
                        "description": "Scenario configuration dict.",
                    },
                    "run_count": {
                        "type": "integer",
                        "description": "Number of independent runs.",
                        "default": 1,
                    },
                    "metrics_of_interest": {
                        "type": "array",
                        "items": {"type": "string"},
                        "description": (
                            "Metric names to include.  Empty = all."
                        ),
                        "default": [],
                    },
                    "return_time_series": {
                        "type": "boolean",
                        "description": "Include per-tick time-series data.",
                        "default": False,
                    },
                    "max_sim_time": {
                        "type": ["number", "null"],
                        "description": (
                            "Cap on simulation time in seconds.  null = use "
                            "config value."
                        ),
                        "default": None,
                    },
                },
                "required": ["config"],
            },
        }

    def describe_metrics(self) -> dict[str, str]:
        """Return human-readable descriptions for all available metrics."""
        return {
            "delivery_ratio": (
                "Fraction of created messages that were successfully delivered."
            ),
            "avg_storage_utilization": (
                "Mean storage utilisation across all EDR hosts (0.0--1.0)."
            ),
            "overtime_ratio": (
                "Fraction of depleted messages that exceeded their shelf-life."
            ),
            "freshness_ratio": (
                "Fraction of depleted messages still within freshness period."
            ),
            "total_messages_created": (
                "Total number of messages generated during the simulation."
            ),
            "total_messages_delivered": (
                "Total number of messages successfully delivered."
            ),
        }
