"""Network message -- the fundamental data unit carried between DTN hosts.

Port of Java ``Message.java`` to idiomatic Python 3 with typed first-class
fields replacing the original untyped ``Map<String, Object>`` properties bag.

Design decisions
~~~~~~~~~~~~~~~~
* A module-level ``_next_unique_id`` counter (protected by a
  ``contextvars.ContextVar`` so concurrent simulations get independent
  sequences) replaces the Java ``static int nextUniqueId``.
* SEND-specific attributes (labels, content_hash, freshness/shelf-life,
  processing delay, pipeline state, compression eligibility) are promoted
  to explicit typed fields rather than being stuffed into a generic
  properties dict.  A ``properties`` dict is still available for backward
  compatibility with third-party code that expects it.
* ``replicate()`` produces a *deep copy* with a new ``unique_id``, matching
  the Java semantics used during message transfer.
"""

from __future__ import annotations

import contextvars
import hashlib
import sys
from dataclasses import dataclass, field
from typing import Any, Optional

from .sim_clock import SimClock
from .types import INFINITE_TTL, MessageType

# ---------------------------------------------------------------------------
# Module-level unique-id counter (context-aware for concurrent simulations)
# ---------------------------------------------------------------------------

_next_unique_id: contextvars.ContextVar[int] = contextvars.ContextVar(
    "_next_unique_id", default=0
)


def _allocate_unique_id() -> int:
    """Return the next unique message id and increment the counter."""
    uid = _next_unique_id.get()
    _next_unique_id.set(uid + 1)
    return uid


def reset_message_id_counter() -> None:
    """Reset the unique-id counter for the current context (used between runs)."""
    _next_unique_id.set(0)


# ---------------------------------------------------------------------------
# Message dataclass
# ---------------------------------------------------------------------------

@dataclass
class Message:
    """A message created at a node or passed between nodes.

    Parameters
    ----------
    id:
        Human-readable identifier (must be unique *per logical message* but is
        shared across replicates, just like the Java original).
    from_host, to_host:
        Origin and destination DTN hosts.  Typed as ``Any`` for now to avoid
        circular imports with the not-yet-ported ``DTNHost`` class.
    size:
        Payload size in bytes.
    """

    # -- Core identity -------------------------------------------------------
    id: str
    from_host: Any
    to_host: Any
    size: int

    # -- Auto-assigned -------------------------------------------------------
    unique_id: int = field(init=False)

    # -- Timing --------------------------------------------------------------
    time_created: float = field(init=False)
    time_received: float = field(init=False)
    ttl: int = field(default=INFINITE_TTL)  # minutes; -1 = infinite

    # -- Message type --------------------------------------------------------
    message_type: MessageType = field(default=MessageType.NONPROC)

    # -- SEND attributes -----------------------------------------------------
    labels: set[str] = field(default_factory=set)
    content_hash: Optional[str] = None
    freshness_period: Optional[float] = None   # seconds
    shelf_life: Optional[float] = None         # seconds
    processing_delay: Optional[float] = None   # seconds

    # -- Pipeline state ------------------------------------------------------
    fresh: Optional[bool] = None
    proc_time: Optional[float] = None   # seconds spent processing
    stor_time: Optional[float] = None   # seconds spent in storage
    satisfied: Optional[bool] = None
    overtime: Optional[bool] = None

    # -- Compression ---------------------------------------------------------
    compression_eligible: Optional[bool] = None

    # -- Path tracking -------------------------------------------------------
    path: list[Any] = field(default_factory=list)
    app_id: Optional[str] = None

    # -- Request / response --------------------------------------------------
    response_size: int = 0
    request_msg: Optional[Message] = None

    # -- Generic properties (backward compat) --------------------------------
    properties: dict[str, Any] = field(default_factory=dict)

    # ------------------------------------------------------------------ init
    def __post_init__(self) -> None:
        self.unique_id = _allocate_unique_id()
        self.time_created = SimClock.get_time()
        self.time_received = self.time_created
        self.add_node_on_path(self.from_host)

    # ---------------------------------------------------------------- public
    # -- Path helpers --------------------------------------------------------

    def add_node_on_path(self, host: Any) -> None:
        """Append *host* to the list of nodes this message has traversed."""
        self.path.append(host)

    def get_hops(self) -> list[Any]:
        """Return the list of hosts this message has passed through."""
        return self.path

    def get_hop_count(self) -> int:
        """Return the number of hops (edges) this message has traversed."""
        return max(0, len(self.path) - 1)

    # -- TTL helpers ---------------------------------------------------------

    def get_ttl(self) -> int:
        """Return remaining TTL in minutes, or ``sys.maxsize`` if infinite.

        The value can be negative if the TTL has already expired.
        """
        if self.ttl == INFINITE_TTL:
            return sys.maxsize
        elapsed_minutes = (SimClock.get_time() - self.time_created) / 60.0
        return int(self.ttl - elapsed_minutes)

    def is_expired(self) -> bool:
        """Return ``True`` if the message's TTL has been exceeded."""
        if self.ttl == INFINITE_TTL:
            return False
        elapsed = SimClock.get_time() - self.time_created
        return elapsed > self.ttl * 60.0

    # -- SEND freshness / shelf-life -----------------------------------------

    def is_within_freshness(self) -> bool:
        """Return ``True`` if the message is still within its freshness period.

        If no freshness period has been set the message is considered fresh.
        """
        if self.freshness_period is None:
            return True
        elapsed = SimClock.get_time() - self.time_created
        return elapsed <= self.freshness_period

    def is_within_shelf_life(self) -> bool:
        """Return ``True`` if the message is still within its shelf life.

        If no shelf life has been set the message is considered valid.
        """
        if self.shelf_life is None:
            return True
        elapsed = SimClock.get_time() - self.time_created
        return elapsed <= self.shelf_life

    # -- Content hashing -----------------------------------------------------

    def compute_content_hash(self) -> str:
        """Compute and store a SHA-256 content hash from the message id and size.

        This is a lightweight placeholder; real deployments would hash the
        payload bytes.
        """
        h = hashlib.sha256(f"{self.id}:{self.size}".encode()).hexdigest()
        self.content_hash = h
        return h

    # -- Request / response helpers ------------------------------------------

    def is_response(self) -> bool:
        """Return ``True`` if this message is a response to another message."""
        return self.request_msg is not None

    # -- Generic properties (backward compat) --------------------------------

    def add_property(self, key: str, value: Any) -> None:
        """Add a property, raising ``ValueError`` if *key* already exists."""
        if key in self.properties:
            raise ValueError(
                f"Message {self} already contains a value for key {key!r}"
            )
        self.properties[key] = value

    def get_property(self, key: str) -> Any:
        """Return the property for *key*, or ``None`` if absent."""
        return self.properties.get(key)

    def update_property(self, key: str, value: Any) -> None:
        """Set a property regardless of whether *key* already exists."""
        self.properties[key] = value

    def set_property(self, key: str, value: Any) -> None:
        """Alias for :meth:`update_property` (upsert semantics)."""
        self.properties[key] = value

    # -- Replication / copying -----------------------------------------------

    def replicate(self) -> Message:
        """Create a deep copy with a *new* ``unique_id``.

        Mirrors Java ``Message.replicate()``: the clone shares the same
        logical ``id`` but has its own ``unique_id`` and a copied ``path``.
        """
        replica = Message(
            id=self.id,
            from_host=self.from_host,
            to_host=self.to_host,
            size=self.size,
        )
        replica.copy_from(self)
        return replica

    def copy_from(self, other: Message) -> None:
        """Deep-copy mutable state from *other* into this message.

        Called by :meth:`replicate` after the constructor has already assigned
        a fresh ``unique_id``.
        """
        self.path = list(other.path)
        self.time_created = other.time_created
        self.response_size = other.response_size
        self.request_msg = other.request_msg
        self.ttl = other.ttl
        self.app_id = other.app_id
        self.message_type = other.message_type

        # SEND attributes
        self.labels = set(other.labels)
        self.content_hash = other.content_hash
        self.freshness_period = other.freshness_period
        self.shelf_life = other.shelf_life
        self.processing_delay = other.processing_delay

        # Pipeline state
        self.fresh = other.fresh
        self.proc_time = other.proc_time
        self.stor_time = other.stor_time
        self.satisfied = other.satisfied
        self.overtime = other.overtime

        # Compression
        self.compression_eligible = other.compression_eligible

        # Generic properties -- shallow copy (values should be immutable)
        self.properties = dict(other.properties)

    # -- Comparison / display ------------------------------------------------

    def __lt__(self, other: object) -> bool:
        if not isinstance(other, Message):
            return NotImplemented
        return self.id < other.id

    def __str__(self) -> str:
        return self.id

    def __repr__(self) -> str:
        return (
            f"Message(id={self.id!r}, from={self.from_host}, "
            f"to={self.to_host}, size={self.size})"
        )
