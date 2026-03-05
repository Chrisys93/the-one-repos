"""SEND-style context-aware label tracking for messages and EDR storage.

Labels provide a lightweight taxonomy that the management plane uses to make
placement and replication decisions.  Each message carries a ``labels: set[str]``
(defined in :class:`~core.message.Message`), and the :class:`LabelManager`
provides helpers to assign, query, and aggregate label information.
"""

from __future__ import annotations

from dataclasses import dataclass, field
from typing import TYPE_CHECKING, Any, Optional

if TYPE_CHECKING:
    from ..core.message import Message
    from ..storage.repo_storage import RepoStorage


@dataclass
class Label:
    """A single descriptive label that can be attached to a message.

    Attributes
    ----------
    name:
        Short human-readable label identifier (e.g. ``"temperature"``).
    category:
        The taxonomy category this label belongs to (e.g. ``"sensor_type"``).
    value:
        Optional payload associated with the label.
    timestamp:
        Optional simulation time at which the label was assigned.
    """

    name: str
    category: str
    value: Any = None
    timestamp: Optional[float] = None


class LabelManager:
    """Registry and query interface for SEND context-aware labels.

    The label manager maintains a *taxonomy* -- a mapping from category names
    to sets of known label names -- and exposes helpers that operate on
    :class:`~core.message.Message` instances.
    """

    def __init__(self) -> None:
        self._taxonomy: dict[str, set[str]] = {}

    # ------------------------------------------------------------------
    # Taxonomy management
    # ------------------------------------------------------------------

    def register_label(self, category: str, name: str) -> None:
        """Register *name* under *category* in the taxonomy."""
        self._taxonomy.setdefault(category, set()).add(name)

    @property
    def taxonomy(self) -> dict[str, set[str]]:
        """Return the current taxonomy (read-only view)."""
        return dict(self._taxonomy)

    # ------------------------------------------------------------------
    # Per-message operations
    # ------------------------------------------------------------------

    def assign_label(self, msg: Message, label: Label) -> None:
        """Add *label*'s name to *msg*'s label set."""
        msg.labels.add(label.name)

    def match(self, msg: Message, query: set[str]) -> bool:
        """Return ``True`` if *msg* carries at least one label in *query*."""
        return bool(msg.labels & query)

    # ------------------------------------------------------------------
    # Aggregate queries
    # ------------------------------------------------------------------

    def get_popularity(self, storage: RepoStorage, label: str) -> int:
        """Count messages in *storage* that carry *label*.

        Scans all three compartments (static, process, processed).
        """
        count: int = 0
        for compartment in (
            storage.static_messages,
            storage.process_messages,
            storage.processed_messages,
        ):
            for msg in compartment:
                if label in msg.labels:
                    count += 1
        return count
