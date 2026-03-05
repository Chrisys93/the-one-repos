"""Base router classes: MessageRouter ABC and ActiveRouter.

Port of Java MessageRouter.java and ActiveRouter.java.
"""

from __future__ import annotations

from abc import ABC, abstractmethod
from typing import TYPE_CHECKING, Any, Optional

from ..core.sim_clock import SimClock
from ..core.types import MessageType

if TYPE_CHECKING:
    from ..core.connection import Connection
    from ..core.message import Message


class MessageRouter(ABC):
    """Base message router with buffer management and application dispatch."""

    def __init__(
        self,
        buffer_size: int = 100_000_000,
        msg_ttl: int = 300,
    ) -> None:
        self.host: Any = None  # Set by DTNHost after construction
        self.buffer_size = buffer_size
        self.msg_ttl = msg_ttl
        self._messages: dict[str, Message] = {}
        self._delivered: dict[str, Message] = {}
        self._applications: dict[str, list[Any]] = {}  # app_id -> [Application]
        self._message_listeners: list[Any] = []

    # ---- abstract ----------------------------------------------------------

    @abstractmethod
    def replicate(self) -> MessageRouter: ...

    # ---- buffer management -------------------------------------------------

    def get_message_collection(self) -> list[Message]:
        return list(self._messages.values())

    def get_nrof_messages(self) -> int:
        return len(self._messages)

    def has_message(self, msg_id: str) -> bool:
        return msg_id in self._messages

    def get_message(self, msg_id: str) -> Optional[Message]:
        return self._messages.get(msg_id)

    def get_buffer_size(self) -> int:
        return self.buffer_size

    def get_free_buffer_size(self) -> int:
        used = sum(m.size for m in self._messages.values())
        return self.buffer_size - used

    def create_new_message(self, msg: Message) -> bool:
        self._messages[msg.id] = msg
        for listener in self._message_listeners:
            listener.new_message(msg)
        return True

    def receive_message(self, msg: Message, from_host: Any) -> int:
        self._messages[msg.id] = msg
        return 0  # RCV_OK

    def delete_message(self, msg_id: str, drop: bool = False) -> Optional[Message]:
        msg = self._messages.pop(msg_id, None)
        if msg is not None:
            for listener in self._message_listeners:
                listener.message_deleted(msg, self.host, drop)
        return msg

    def get_oldest_message(self, exclude_sending: bool = True) -> Optional[Message]:
        oldest: Optional[Message] = None
        for m in self._messages.values():
            if exclude_sending and self._is_sending(m.id):
                continue
            if oldest is None or m.time_received < oldest.time_received:
                oldest = m
        return oldest

    def _is_sending(self, msg_id: str) -> bool:
        return False  # Overridden by ActiveRouter

    # ---- applications ------------------------------------------------------

    def add_application(self, app: Any) -> None:
        aid = app.get_app_id()
        self._applications.setdefault(aid, []).append(app)

    def get_applications(self, app_id: str) -> list[Any]:
        return self._applications.get(app_id, [])

    def get_all_applications(self) -> list[Any]:
        apps = []
        for app_list in self._applications.values():
            apps.extend(app_list)
        return apps

    # ---- listeners ---------------------------------------------------------

    def add_message_listener(self, listener: Any) -> None:
        self._message_listeners.append(listener)

    # ---- connection change notification ------------------------------------

    def changed_connection(self, con: Any) -> None:
        """Notification that a connection went up or down."""
        pass  # Subclasses may override

    # ---- update ------------------------------------------------------------

    def update(self) -> None:
        for app in self.get_all_applications():
            app.update(self.host)

    def sort_by_queue_mode(self, messages: list[Message]) -> list[Message]:
        messages.sort(key=lambda m: m.time_received)
        return messages

    # ---- session-less data retrieval ----------------------------------------

    def request_from_connected_repo(
        self, msg_id: Optional[str] = None, content_hash: Optional[str] = None
    ) -> Optional[Message]:
        """Pull a message from a connected EDR repo by ID or content hash.

        This enables session-less data transmission: any node connected
        to an EDR can retrieve stored data without being the original
        sender or receiver, and without referencing the delivery session.

        Parameters
        ----------
        msg_id:
            Logical message ID to look up.
        content_hash:
            Content hash to look up (used if msg_id is None).

        Returns the first matching Message found across all connected
        repos, or None.
        """
        if msg_id is None and content_hash is None:
            return None

        for con in self.host.get_connections() if self.host else []:
            other = con.get_other_node(self.host)
            if other is None:
                continue
            # Only query repo nodes
            if not getattr(other, "has_storage", False):
                continue
            storage = getattr(other, "storage", None)
            if storage is None:
                continue

            if msg_id is not None:
                result = storage.get_message_by_id(msg_id)
                if result is not None:
                    return result
            elif content_hash is not None:
                result = storage.get_message_by_hash(content_hash)
                if result is not None:
                    return result
        return None

    def discover_from_connected_repos(self) -> dict[str, list[str]]:
        """Discover message IDs available at each connected EDR repo.

        Returns a dict mapping repo node name -> list of message IDs.
        Supports session-less data discovery.
        """
        result: dict[str, list[str]] = {}
        for con in self.host.get_connections() if self.host else []:
            other = con.get_other_node(self.host)
            if other is None:
                continue
            if not getattr(other, "has_storage", False):
                continue
            storage = getattr(other, "storage", None)
            if storage is None:
                continue
            result[other.name] = storage.get_all_message_ids()
        return result

    # ---- buffer deleted tracking -------------------------------------------

    def get_buffer_deleted_messages_size(self) -> int:
        return 0


class ActiveRouter(MessageRouter):
    """Router that actively manages transfers over connections."""

    def __init__(self, buffer_size: int = 100_000_000, msg_ttl: int = 300) -> None:
        super().__init__(buffer_size, msg_ttl)
        self._sending_connections: list[Connection] = []

    def update(self) -> None:
        # Finalize or abort completed transfers
        finished = []
        for con in self._sending_connections:
            if con.is_message_transferred():
                self.transfer_done(con)
                finished.append(con)
            elif not con.is_up:
                self.transfer_aborted(con)
                finished.append(con)
        for con in finished:
            if con in self._sending_connections:
                self._sending_connections.remove(con)

        # TTL check
        self._check_ttl()

        # Application updates
        super().update()

    def _check_ttl(self) -> None:
        cur_time = SimClock.get_time()
        to_delete = []
        for msg_id, msg in self._messages.items():
            if msg.ttl > 0 and (cur_time - msg.time_created) / 60.0 > msg.ttl:
                to_delete.append(msg_id)
        for mid in to_delete:
            self.delete_message(mid, drop=True)

    def is_transferring(self) -> bool:
        return len(self._sending_connections) > 0

    def can_start_transfer(self) -> bool:
        if self.get_nrof_messages() == 0:
            return False
        return not self.is_transferring()

    def _is_sending(self, msg_id: str) -> bool:
        for con in self._sending_connections:
            if con.msg_on_fly is not None and con.msg_on_fly.id == msg_id:
                return True
        return False

    def start_transfer(self, msg: Message, con: Connection) -> int:
        # K8s namespace routing rule check (if wired)
        other = con.get_other_node(self.host)
        if other is not None and self.host is not None:
            world = getattr(self.host, "_world", None)
            if world is not None:
                ns_router = getattr(world, "k8s_namespace_router", None)
                if ns_router is not None:
                    rules = getattr(world, "_k8s_routing_rules", [])
                    if rules and not ns_router.apply_rules(
                        rules, msg, self.host, other
                    ):
                        return -1  # Denied by K8s routing rules
        retval = con.start_transfer(self.host, msg)
        if retval == 0:  # RCV_OK
            self._sending_connections.append(con)
        return retval

    def make_room_for_message(self, size: int) -> bool:
        if size > self.buffer_size:
            return False
        free = self.get_free_buffer_size()
        while free < size:
            m = self.get_oldest_message(exclude_sending=True)
            if m is None:
                return False
            self.delete_message(m.id, drop=True)
            free += m.size
        return True

    def transfer_done(self, con: Connection) -> None:
        pass  # Overridden by specific routers

    def transfer_aborted(self, con: Connection) -> None:
        if con.msg_on_fly is not None:
            con.msg_on_fly = None

    def try_messages_to_connections(
        self, messages: list[Message], connections: list[Connection]
    ) -> Optional[Connection]:
        for msg in messages:
            for con in connections:
                if not con.is_ready_for_transfer():
                    continue
                retval = self.start_transfer(msg, con)
                if retval == 0:
                    return con
        return None

    def get_connections(self) -> list[Connection]:
        if self.host is None:
            return []
        conns: list[Connection] = []
        for iface in self.host.interfaces:
            conns.extend(iface.connections)
        return conns
