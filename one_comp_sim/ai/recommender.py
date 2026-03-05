"""Recommendation engine for simulation-informed infrastructure decisions.

:class:`Recommender` defines the abstract interface; :class:`BaselineRecommender`
provides a rule-based implementation that maps simulation results to
placement, processing-config, and Kubernetes resource suggestions.
"""

from __future__ import annotations

from abc import ABC, abstractmethod
from typing import Any


class Recommender(ABC):
    """Abstract recommendation interface.

    Implementations consume :class:`~ai.tool_interface.SimulationResult`
    objects (or their ``summary`` dicts) and produce actionable suggestions.
    """

    @abstractmethod
    def suggest_placement(
        self,
        results: list[dict[str, Any]],
        constraints: dict[str, Any],
    ) -> dict:
        """Suggest EDR placement changes based on simulation results.

        Parameters
        ----------
        results:
            List of summary dicts from completed simulation runs.
        constraints:
            Budget / SLA constraints (e.g. max node count, latency target).
        """
        ...

    @abstractmethod
    def suggest_processing_config(
        self,
        results: list[dict[str, Any]],
    ) -> dict:
        """Suggest processing pipeline adjustments."""
        ...

    @abstractmethod
    def suggest_k8s_resources(
        self,
        results: list[dict[str, Any]],
    ) -> dict:
        """Suggest Kubernetes resource requests/limits."""
        ...


class BaselineRecommender(Recommender):
    """Rule-based recommender using simple threshold heuristics.

    All thresholds are configurable at construction time.
    """

    def __init__(
        self,
        storage_high: float = 0.80,
        storage_low: float = 0.30,
        overtime_threshold: float = 0.10,
        freshness_threshold: float = 0.90,
        cpu_target_util: float = 0.60,
        memory_target_util: float = 0.70,
    ) -> None:
        self.storage_high: float = storage_high
        self.storage_low: float = storage_low
        self.overtime_threshold: float = overtime_threshold
        self.freshness_threshold: float = freshness_threshold
        self.cpu_target_util: float = cpu_target_util
        self.memory_target_util: float = memory_target_util

    # ------------------------------------------------------------------
    # Placement
    # ------------------------------------------------------------------

    def suggest_placement(
        self,
        results: list[dict[str, Any]],
        constraints: dict[str, Any],
    ) -> dict:
        """Based on average storage utilisation, recommend adding/removing EDRs.

        Logic:

        * If mean utilisation > ``storage_high`` --> suggest adding EDRs.
        * If mean utilisation < ``storage_low``  --> suggest removing EDRs.
        * Otherwise                              --> no change.
        """
        if not results:
            return {"action": "no_data", "reason": "No simulation results provided."}

        avg_util: float = _mean(
            [r.get("avg_storage_utilization", 0.0) for r in results]
        )
        max_nodes: int = constraints.get("max_nodes", 100)

        if avg_util > self.storage_high:
            suggested: int = min(max_nodes, int(avg_util / self.storage_high) + 1)
            return {
                "action": "add_edrs",
                "additional_edrs": suggested,
                "reason": (
                    f"Average storage utilisation ({avg_util:.2f}) exceeds "
                    f"threshold ({self.storage_high:.2f})."
                ),
            }
        if avg_util < self.storage_low:
            return {
                "action": "remove_edrs",
                "reason": (
                    f"Average storage utilisation ({avg_util:.2f}) is below "
                    f"threshold ({self.storage_low:.2f}); resources are "
                    f"under-utilised."
                ),
            }
        return {
            "action": "no_change",
            "reason": (
                f"Average storage utilisation ({avg_util:.2f}) is within "
                f"acceptable range."
            ),
        }

    # ------------------------------------------------------------------
    # Processing config
    # ------------------------------------------------------------------

    def suggest_processing_config(
        self,
        results: list[dict[str, Any]],
    ) -> dict:
        """Adjust processing parameters based on overtime and freshness ratios."""
        if not results:
            return {"action": "no_data", "reason": "No simulation results provided."}

        avg_overtime: float = _mean(
            [r.get("overtime_ratio", 0.0) for r in results]
        )
        avg_freshness: float = _mean(
            [r.get("freshness_ratio", 1.0) for r in results]
        )

        suggestions: dict[str, Any] = {"adjustments": []}

        if avg_overtime > self.overtime_threshold:
            suggestions["adjustments"].append(
                {
                    "parameter": "processing_capacity",
                    "action": "increase",
                    "reason": (
                        f"Overtime ratio ({avg_overtime:.2f}) exceeds "
                        f"{self.overtime_threshold:.2f}."
                    ),
                }
            )

        if avg_freshness < self.freshness_threshold:
            suggestions["adjustments"].append(
                {
                    "parameter": "freshness_period",
                    "action": "increase",
                    "reason": (
                        f"Freshness ratio ({avg_freshness:.2f}) is below "
                        f"target ({self.freshness_threshold:.2f})."
                    ),
                }
            )

        if not suggestions["adjustments"]:
            suggestions["adjustments"].append(
                {
                    "parameter": "none",
                    "action": "no_change",
                    "reason": "Processing metrics are within acceptable bounds.",
                }
            )

        return suggestions

    # ------------------------------------------------------------------
    # Kubernetes resources
    # ------------------------------------------------------------------

    def suggest_k8s_resources(
        self,
        results: list[dict[str, Any]],
    ) -> dict:
        """Suggest K8s resource requests/limits based on utilisation percentages.

        Uses ``avg_storage_utilization`` as a proxy for overall resource
        pressure.
        """
        if not results:
            return {"action": "no_data", "reason": "No simulation results provided."}

        avg_util: float = _mean(
            [r.get("avg_storage_utilization", 0.0) for r in results]
        )

        # Scale CPU and memory recommendations relative to target utilisation.
        cpu_scale: float = avg_util / self.cpu_target_util if self.cpu_target_util > 0 else 1.0
        mem_scale: float = avg_util / self.memory_target_util if self.memory_target_util > 0 else 1.0

        base_cpu_m: int = 500
        base_mem_bytes: int = 512 * 1024 * 1024  # 512 MiB

        return {
            "cpu_millicores": max(100, int(base_cpu_m * cpu_scale)),
            "memory_bytes": max(128 * 1024 * 1024, int(base_mem_bytes * mem_scale)),
            "reason": (
                f"Scaled from base (500m CPU, 512Mi memory) using observed "
                f"utilisation ({avg_util:.2f}) vs targets "
                f"(CPU {self.cpu_target_util:.2f}, mem {self.memory_target_util:.2f})."
            ),
        }


# ---------------------------------------------------------------------------
# Helpers
# ---------------------------------------------------------------------------

def _mean(values: list[float]) -> float:
    """Return the arithmetic mean, or 0.0 for an empty list."""
    if not values:
        return 0.0
    return sum(values) / len(values)
