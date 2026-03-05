"""Placement strategies for the SEND management plane.

The management plane periodically collects per-EDR statistics and feeds them
to a :class:`PlacementStrategy` implementation which decides where messages,
replicas, and processing functions should live.  Four concrete strategies are
provided, from the simple :class:`GeneralPurposePlacement` to the
:class:`HybridPlacement` that blends multiple heuristics with configurable
weights.
"""

from __future__ import annotations

from abc import ABC, abstractmethod
from dataclasses import dataclass, field
from typing import Optional


# ---------------------------------------------------------------------------
# Data transfer objects
# ---------------------------------------------------------------------------

@dataclass
class EDRStats:
    """Snapshot of a single EDR host's resource utilisation.

    Attributes
    ----------
    host_name:
        Human-readable host identifier.
    storage_utilization:
        Fraction of storage capacity currently used (0.0 -- 1.0).
    processing_utilization:
        Fraction of processing capacity currently used (0.0 -- 1.0).
    incoming_rate:
        Mean bytes/sec of incoming messages at the time of collection.
    depletion_rate:
        Mean bytes/sec being depleted (cloud + local + inter-EDR).
    message_type_distribution:
        Count of messages per :class:`~core.types.MessageType` value.
    label_distribution:
        Count of messages per label name present on the host.
    """

    host_name: str
    storage_utilization: float
    processing_utilization: float
    incoming_rate: float
    depletion_rate: float
    message_type_distribution: dict[str, int] = field(default_factory=dict)
    label_distribution: dict[str, int] = field(default_factory=dict)


@dataclass
class ManagementAction:
    """A single management-plane decision to be applied to the network.

    Attributes
    ----------
    action_type:
        One of ``"replicate"``, ``"migrate"``, or ``"adjust_capacity"``.
    source_edr:
        Name of the EDR host on which the action originates.
    target_edr:
        Name of the destination EDR host (``None`` for capacity adjustments).
    parameters:
        Strategy-specific key-value pairs (e.g. label filters, byte budgets).
    """

    action_type: str  # "replicate" | "migrate" | "adjust_capacity"
    source_edr: str
    target_edr: Optional[str] = None
    parameters: dict = field(default_factory=dict)

    def __post_init__(self) -> None:
        allowed: set[str] = {"replicate", "migrate", "adjust_capacity"}
        if self.action_type not in allowed:
            raise ValueError(
                f"action_type must be one of {allowed!r}, got {self.action_type!r}"
            )


# ---------------------------------------------------------------------------
# Abstract strategy
# ---------------------------------------------------------------------------

class PlacementStrategy(ABC):
    """Interface for management-plane placement decision logic."""

    @abstractmethod
    def decide(
        self,
        stats: dict[str, EDRStats],
        topology: dict,
    ) -> list[ManagementAction]:
        """Produce a list of placement actions given current EDR stats.

        Parameters
        ----------
        stats:
            Mapping of host name to :class:`EDRStats` snapshot.
        topology:
            Adjacency or connectivity information for the EDR network.
            The format is strategy-dependent (e.g. ``{host: [neighbours]}``).

        Returns
        -------
        list[ManagementAction]
            Ordered list of actions to execute.
        """
        ...


# ---------------------------------------------------------------------------
# Concrete strategies
# ---------------------------------------------------------------------------

class GeneralPurposePlacement(PlacementStrategy):
    """Place replicas on the EDR with the most free storage.

    When any EDR exceeds *high_threshold* utilisation, the strategy emits a
    ``"replicate"`` action targeting the EDR with the lowest utilisation.
    """

    def __init__(
        self,
        high_threshold: float = 0.85,
        low_threshold: float = 0.30,
    ) -> None:
        self.high_threshold: float = high_threshold
        self.low_threshold: float = low_threshold

    def decide(
        self,
        stats: dict[str, EDRStats],
        topology: dict,
    ) -> list[ManagementAction]:
        actions: list[ManagementAction] = []
        if not stats:
            return actions

        # Identify the EDR with the lowest storage utilisation.
        least_loaded: str = min(
            stats, key=lambda h: stats[h].storage_utilization
        )

        for host_name, s in stats.items():
            if s.storage_utilization >= self.high_threshold:
                if stats[least_loaded].storage_utilization < self.low_threshold:
                    actions.append(
                        ManagementAction(
                            action_type="replicate",
                            source_edr=host_name,
                            target_edr=least_loaded,
                            parameters={
                                "reason": "storage_overflow",
                                "source_util": s.storage_utilization,
                                "target_util": stats[least_loaded].storage_utilization,
                            },
                        )
                    )
        return actions


class PopularityBasedPlacement(PlacementStrategy):
    """Place replicas near EDRs with the highest request rate for popular labels.

    If a label has a high request count on one EDR, the strategy suggests
    replicating the relevant messages to neighbouring EDRs that lack them.
    """

    def __init__(self, popularity_threshold: int = 50) -> None:
        self.popularity_threshold: int = popularity_threshold

    def decide(
        self,
        stats: dict[str, EDRStats],
        topology: dict,
    ) -> list[ManagementAction]:
        actions: list[ManagementAction] = []
        if not stats:
            return actions

        # Build a global popularity ranking across all EDRs.
        global_popularity: dict[str, int] = {}
        for s in stats.values():
            for label, count in s.label_distribution.items():
                global_popularity[label] = (
                    global_popularity.get(label, 0) + count
                )

        # Find popular labels.
        popular_labels: set[str] = {
            label
            for label, count in global_popularity.items()
            if count >= self.popularity_threshold
        }

        if not popular_labels:
            return actions

        # For each EDR with a high incoming rate and popular labels, suggest
        # replicating toward a neighbour that carries fewer copies.
        neighbours: dict[str, list[str]] = {
            h: topology.get(h, []) for h in stats
        }

        for host_name, s in stats.items():
            host_popular = popular_labels & set(s.label_distribution.keys())
            if not host_popular:
                continue
            for neighbour in neighbours.get(host_name, []):
                if neighbour not in stats:
                    continue
                n_labels = set(stats[neighbour].label_distribution.keys())
                missing = host_popular - n_labels
                if missing:
                    actions.append(
                        ManagementAction(
                            action_type="replicate",
                            source_edr=host_name,
                            target_edr=neighbour,
                            parameters={
                                "reason": "popularity",
                                "labels": sorted(missing),
                            },
                        )
                    )
        return actions


class FunctionBasedPlacement(PlacementStrategy):
    """Place data near processing functions to reduce transfer latency.

    When an EDR has high processing utilisation but low storage utilisation,
    the strategy suggests migrating processing-queue messages *to* it from
    overloaded peers.
    """

    def __init__(
        self,
        processing_high: float = 0.80,
        storage_low: float = 0.40,
    ) -> None:
        self.processing_high: float = processing_high
        self.storage_low: float = storage_low

    def decide(
        self,
        stats: dict[str, EDRStats],
        topology: dict,
    ) -> list[ManagementAction]:
        actions: list[ManagementAction] = []
        if not stats:
            return actions

        # Find EDRs with spare storage that could absorb processing workloads.
        receivers: list[str] = [
            h
            for h, s in stats.items()
            if s.storage_utilization < self.storage_low
        ]

        for host_name, s in stats.items():
            if s.processing_utilization < self.processing_high:
                continue
            for target in receivers:
                if target == host_name:
                    continue
                actions.append(
                    ManagementAction(
                        action_type="migrate",
                        source_edr=host_name,
                        target_edr=target,
                        parameters={
                            "reason": "function_colocation",
                            "source_proc_util": s.processing_utilization,
                            "target_stor_util": stats[target].storage_utilization,
                        },
                    )
                )
                break  # one target per source is enough
        return actions


class HybridPlacement(PlacementStrategy):
    """Combine multiple placement strategies with configurable weights.

    Each sub-strategy is called independently and the resulting actions are
    scored by their strategy weight.  The final action list is sorted by
    descending score so higher-weighted strategies' actions appear first.

    Parameters
    ----------
    strategies:
        Mapping of :class:`PlacementStrategy` instances to their weight
        (float, higher = more important).
    """

    def __init__(
        self,
        strategies: Optional[dict[PlacementStrategy, float]] = None,
    ) -> None:
        if strategies is None:
            strategies = {
                GeneralPurposePlacement(): 1.0,
                PopularityBasedPlacement(): 0.5,
                FunctionBasedPlacement(): 0.5,
            }
        self.strategies: dict[PlacementStrategy, float] = strategies

    def decide(
        self,
        stats: dict[str, EDRStats],
        topology: dict,
    ) -> list[ManagementAction]:
        scored: list[tuple[float, ManagementAction]] = []

        for strategy, weight in self.strategies.items():
            for action in strategy.decide(stats, topology):
                action.parameters.setdefault("_weight", weight)
                scored.append((weight, action))

        # Stable sort, highest weight first.
        scored.sort(key=lambda pair: pair[0], reverse=True)
        return [action for _, action in scored]
