"""Registry for tracking and comparing infrastructure configurations.

:class:`ArchitectureRegistry` stores versioned :class:`ArchitectureEntry`
records so that callers (including LLM agents) can discover, compare, and
select deployment architectures for the edge simulator.
"""

from __future__ import annotations

from dataclasses import dataclass, field
from typing import Optional


@dataclass
class ArchitectureEntry:
    """Metadata record for a single infrastructure architecture.

    Attributes
    ----------
    name:
        Architecture identifier (e.g. ``"3-edr-bus-network"``).
    version:
        Semantic version string (e.g. ``"1.0.0"``).
    description:
        Free-text description.
    config:
        Full configuration dict used to set up this architecture.
    topology_summary:
        High-level topology metrics (e.g. node count, link count).
    performance_summary:
        Benchmark / simulation performance metrics.
    created_at:
        ISO-8601 timestamp of creation.
    """

    name: str
    version: str
    description: str
    config: dict = field(default_factory=dict)
    topology_summary: dict = field(default_factory=dict)
    performance_summary: dict = field(default_factory=dict)
    created_at: str = ""


class ArchitectureRegistry:
    """In-memory versioned registry for :class:`ArchitectureEntry` records."""

    def __init__(self) -> None:
        self._entries: list[ArchitectureEntry] = []

    # ------------------------------------------------------------------
    # Registration
    # ------------------------------------------------------------------

    def register(self, entry: ArchitectureEntry) -> None:
        """Add *entry* to the registry."""
        self._entries.append(entry)

    # ------------------------------------------------------------------
    # Queries
    # ------------------------------------------------------------------

    def get_latest(self, name: str) -> Optional[ArchitectureEntry]:
        """Return the latest version of architecture *name*, or ``None``.

        "Latest" is determined by insertion order (last registered wins).
        """
        matches: list[ArchitectureEntry] = [
            e for e in self._entries if e.name == name
        ]
        return matches[-1] if matches else None

    def list_versions(self, name: str) -> list[ArchitectureEntry]:
        """Return all registered versions of architecture *name*, in order."""
        return [e for e in self._entries if e.name == name]

    # ------------------------------------------------------------------
    # Comparison
    # ------------------------------------------------------------------

    def compare(self, name1: str, name2: str) -> dict:
        """Compare the latest versions of two architectures.

        Returns a dict with keys ``"left"``, ``"right"``, and ``"diff"``
        where ``"diff"`` highlights topology and performance differences.
        """
        left: Optional[ArchitectureEntry] = self.get_latest(name1)
        right: Optional[ArchitectureEntry] = self.get_latest(name2)

        result: dict = {
            "left": _entry_summary(left),
            "right": _entry_summary(right),
            "diff": {},
        }

        if left is None or right is None:
            result["diff"]["error"] = (
                "Cannot compare: one or both architectures not found."
            )
            return result

        # Topology diff.
        topo_diff: dict = {}
        all_topo_keys: set[str] = set(left.topology_summary) | set(
            right.topology_summary
        )
        for key in sorted(all_topo_keys):
            lv = left.topology_summary.get(key)
            rv = right.topology_summary.get(key)
            if lv != rv:
                topo_diff[key] = {"left": lv, "right": rv}

        # Performance diff.
        perf_diff: dict = {}
        all_perf_keys: set[str] = set(left.performance_summary) | set(
            right.performance_summary
        )
        for key in sorted(all_perf_keys):
            lv = left.performance_summary.get(key)
            rv = right.performance_summary.get(key)
            if lv != rv:
                perf_diff[key] = {"left": lv, "right": rv}

        result["diff"]["topology"] = topo_diff
        result["diff"]["performance"] = perf_diff
        return result


# ---------------------------------------------------------------------------
# Helpers
# ---------------------------------------------------------------------------

def _entry_summary(entry: Optional[ArchitectureEntry]) -> dict:
    """Return a compact summary dict for an entry (or empty if ``None``)."""
    if entry is None:
        return {}
    return {
        "name": entry.name,
        "version": entry.version,
        "description": entry.description,
        "created_at": entry.created_at,
    }
