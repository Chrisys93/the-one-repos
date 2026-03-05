"""Registry for discovering and managing simulation datasets.

:class:`DatasetRegistry` allows callers to register, search, and export
metadata about datasets produced by simulation runs.
"""

from __future__ import annotations

import json
from dataclasses import asdict, dataclass, field
from typing import Optional


@dataclass
class DatasetEntry:
    """Metadata record for a single simulation dataset.

    Attributes
    ----------
    name:
        Short human-readable identifier.
    description:
        Free-text description of the dataset's contents and purpose.
    scenario_name:
        Name of the scenario that produced the dataset.
    labels:
        Free-form tags for search and filtering.
    format:
        Serialisation format (e.g. ``"json"``, ``"csv"``, ``"parquet"``).
    path:
        Filesystem path (or URI) where the dataset is stored.
    created_at:
        ISO-8601 timestamp of creation.
    metadata:
        Arbitrary extra key-value metadata.
    """

    name: str
    description: str
    scenario_name: str
    labels: set[str] = field(default_factory=set)
    format: str = "json"
    path: str = ""
    created_at: str = ""
    metadata: dict = field(default_factory=dict)


class DatasetRegistry:
    """In-memory registry for :class:`DatasetEntry` records."""

    def __init__(self) -> None:
        self._entries: list[DatasetEntry] = []

    # ------------------------------------------------------------------
    # Registration
    # ------------------------------------------------------------------

    def register(self, entry: DatasetEntry) -> None:
        """Add *entry* to the registry."""
        self._entries.append(entry)

    # ------------------------------------------------------------------
    # Queries
    # ------------------------------------------------------------------

    def find_by_label(self, label: str) -> list[DatasetEntry]:
        """Return all entries whose ``labels`` set contains *label*."""
        return [e for e in self._entries if label in e.labels]

    def find_by_scenario(self, name: str) -> list[DatasetEntry]:
        """Return all entries whose ``scenario_name`` matches *name*."""
        return [e for e in self._entries if e.scenario_name == name]

    def list_all(self) -> list[DatasetEntry]:
        """Return a shallow copy of every registered entry."""
        return list(self._entries)

    # ------------------------------------------------------------------
    # Serialisation
    # ------------------------------------------------------------------

    def to_json(self) -> str:
        """Serialise the full registry to a JSON string.

        ``set`` fields are converted to sorted lists for JSON compatibility.
        """
        serialisable: list[dict] = []
        for entry in self._entries:
            d: dict = asdict(entry)
            # Convert sets to sorted lists.
            d["labels"] = sorted(d.get("labels", []))
            serialisable.append(d)
        return json.dumps(serialisable, indent=2)
