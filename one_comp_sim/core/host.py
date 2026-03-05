"""DTNHost: a network node with routing, movement, storage, and interfaces.

Port of Java DTNHost.java.
"""

from __future__ import annotations

from dataclasses import dataclass, field
from typing import TYPE_CHECKING, Any, Optional

from .bus import ModuleCommunicationBus
from .coord import Coord
from .sim_clock import SimClock
from .types import NodeRole

if TYPE_CHECKING:
    from .connection import Connection
    from .message import Message
    from ..movement.base import MovementModel
    from ..movement.map_data.path import Path
    from ..routing.base import MessageRouter
    from ..storage.repo_storage import RepoStorage


_next_address: int = 0


def _get_next_address() -> int:
    global _next_address
    addr = _next_address
    _next_address += 1
    return addr


def reset_address_counter() -> None:
    global _next_address
    _next_address = 0


class DTNHost:
    """A network node: ties together router, movement, interfaces, storage."""

    def __init__(
        self,
        name: str,
        group_id: str,
        role: NodeRole,
        router: MessageRouter,
        movement: MovementModel,
        interfaces: list[Any] | None = None,
        has_storage: bool = False,
        has_processing: bool = False,
        storage_size: int = 0,
        compression_rate: float = 1.0,
        msg_ttl: int = 300,
    ) -> None:
        self.address = _get_next_address()
        self.name = name
        self.group_id = group_id
        self.role = role

        # Components
        self.router = router
        self.router.host = self
        self.movement = movement
        self.movement.host = self
        self.interfaces: list[Any] = interfaces or []
        for iface in self.interfaces:
            iface.host = self
        self.com_bus = ModuleCommunicationBus()

        # Capabilities
        self.has_storage = has_storage
        self.has_processing = has_processing
        self.storage_size = storage_size
        self.compression_rate = compression_rate

        # Storage system (initialized externally if has_storage)
        self.storage: Optional[RepoStorage] = None

        # Location state
        self.location: Coord = Coord(0, 0)
        self._destination: Optional[Coord] = None
        self._speed: float = 0.0
        self._current_path: Optional[Path] = None
        self._wait_until: float = 0.0

        # K8s metadata hook (optional)
        self.k8s_metadata: Optional[dict[str, Any]] = None
        self.namespace: str = "default"

        # Applications are attached via router
        self.msg_ttl = msg_ttl

    # ---- convenience aliases (matching Java API) ---------------------------

    def has_storage_capability(self) -> bool:
        return self.has_storage

    def has_processing_capability(self) -> bool:
        return self.has_processing

    def get_storage_system(self) -> Optional[RepoStorage]:
        return self.storage

    # ---- session-less data retrieval API ------------------------------------
    # These methods allow any entity (connected node, application, management
    # plane) to access data stored in this node's repository by ID or content
    # hash, without needing a reference to the original delivery session or
    # connection.  This is the key enabler of session-less data transmission
    # in the store-process-send paradigm.

    def request_message_by_id(self, msg_id: str) -> Optional[Any]:
        """Retrieve a stored message by its logical ID.

        Returns the Message if found, else None.  The caller does not
        need to know which session or connection originally delivered
        the message.
        """
        if self.storage is not None:
            return self.storage.get_message_by_id(msg_id)
        return None

    def request_message_by_hash(self, content_hash: str) -> Optional[Any]:
        """Retrieve a stored message by its content hash.

        Returns the Message if found, else None.  Supports session-less
        access: any entity that knows the content hash can retrieve the
        data.
        """
        if self.storage is not None:
            return self.storage.get_message_by_hash(content_hash)
        return None

    def get_stored_message_ids(self) -> list[str]:
        """Return IDs of all messages currently in this node's storage.

        Supports discovery for session-less access.
        """
        if self.storage is not None:
            return self.storage.get_all_message_ids()
        return []

    def get_stored_content_hashes(self) -> list[str]:
        """Return content hashes of all stored messages.

        Supports hash-based discovery for session-less retrieval.
        """
        if self.storage is not None:
            return self.storage.get_all_content_hashes()
        return []

    # ---- message creation / reception ----------------------------------------

    def create_new_message(self, msg: Message) -> bool:
        return self.router.create_new_message(msg)

    def receive_message(self, msg: Message, from_host: Any) -> int:
        return self.router.receive_message(msg, from_host)

    # ---- location / activity -----------------------------------------------

    def get_location(self) -> Coord:
        return self.location

    def is_active(self) -> bool:
        return True

    # ---- connections -------------------------------------------------------

    def connection_up(self, con: Connection) -> None:
        """Called when a new connection is established."""
        self.router.changed_connection(con)

    def connection_down(self, con: Connection) -> None:
        """Called when a connection goes down."""
        self.router.changed_connection(con)

    def get_connections(self) -> list[Connection]:
        conns: list[Connection] = []
        for iface in self.interfaces:
            conns.extend(iface.connections)
        return conns

    # ---- movement ----------------------------------------------------------

    def move(self, time_increment: float) -> None:
        cur_time = SimClock.get_time()

        # If waiting, check if wait is over
        if self._wait_until > cur_time:
            return

        # If we have a destination, move towards it
        if self._current_path is not None and self._current_path.has_next():
            dest = self._current_path.get_next_waypoint()
            if dest is None:
                return

            dist_to_dest = self.location.distance(dest)
            move_dist = self._speed * time_increment

            if move_dist >= dist_to_dest:
                # Arrived at waypoint
                self.location = dest
                self._current_path.pop_waypoint()
                if not self._current_path.has_next():
                    # Path complete, wait
                    self._current_path = None
                    self._wait_until = cur_time + self.movement.get_random_wait_time()
            else:
                # Move towards destination
                dx = dest.x - self.location.x
                dy = dest.y - self.location.y
                fraction = move_dist / max(dist_to_dest, 1e-10)
                self.location = Coord(
                    self.location.x + dx * fraction,
                    self.location.y + dy * fraction,
                )
        else:
            # Get next path from movement model
            path = self.movement.get_path()
            if path is not None and path.has_next():
                self._current_path = path
                self._speed = path.speed
            else:
                self._wait_until = cur_time + self.movement.get_random_wait_time()

    def set_location(self, coord: Coord) -> None:
        self.location = coord
        for iface in self.interfaces:
            if hasattr(iface, "update_location"):
                iface.update_location()

    # ---- update (per simulation step) --------------------------------------

    def update(self, simulate_connections: bool) -> None:
        if simulate_connections:
            for iface in self.interfaces:
                iface.update()
        self.router.update()

    # ---- string representation ---------------------------------------------

    def __str__(self) -> str:
        return self.name

    def get_address(self) -> int:
        return self.address

    def get_world(self) -> Any:
        """Return the world this host belongs to (set by World after build)."""
        return getattr(self, '_world', None)

    def __repr__(self) -> str:
        return f"DTNHost({self.name!r}, role={self.role.value})"

    def __hash__(self) -> int:
        return self.address

    def __eq__(self, other: object) -> bool:
        if not isinstance(other, DTNHost):
            return NotImplemented
        return self.address == other.address
