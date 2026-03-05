"""Event system: queues, external events, scheduled updates.

Port of Java EventQueue, ExternalEvent, and input/* event classes.
"""

from __future__ import annotations

from abc import ABC, abstractmethod
from dataclasses import dataclass, field
from typing import TYPE_CHECKING, Any, Optional

if TYPE_CHECKING:
    from .host import DTNHost


class ExternalEvent(ABC):
    """Base class for external simulation events."""

    def __init__(self, time: float) -> None:
        self.time = time

    @abstractmethod
    def process_event(self, world: Any) -> None: ...

    def __lt__(self, other: ExternalEvent) -> bool:
        return self.time < other.time


class EventQueue(ABC):
    """Abstract event queue."""

    @abstractmethod
    def next_events_time(self) -> float: ...

    @abstractmethod
    def next_event(self) -> Optional[ExternalEvent]: ...


class ScheduledUpdatesQueue(EventQueue):
    """Queue for scheduled events, sorted by time."""

    def __init__(self) -> None:
        self._events: list[ExternalEvent] = []

    def add_event(self, event: ExternalEvent) -> None:
        self._events.append(event)
        self._events.sort()

    def next_events_time(self) -> float:
        if not self._events:
            return float("inf")
        return self._events[0].time

    def next_event(self) -> Optional[ExternalEvent]:
        if not self._events:
            return None
        return self._events.pop(0)

    def is_empty(self) -> bool:
        return len(self._events) == 0


# ---------------------------------------------------------------------------
# Concrete event types
# ---------------------------------------------------------------------------


class MessageCreateEvent(ExternalEvent):
    """Create a message between two hosts at a given time."""

    def __init__(
        self,
        time: float,
        from_addr: int,
        to_addr: int,
        msg_id: str,
        size: int,
    ) -> None:
        super().__init__(time)
        self.from_addr = from_addr
        self.to_addr = to_addr
        self.msg_id = msg_id
        self.size = size

    def process_event(self, world: Any) -> None:
        from .message import Message

        hosts = world.hosts
        from_host = None
        to_host = None
        for h in hosts:
            if h.address == self.from_addr:
                from_host = h
            if h.address == self.to_addr:
                to_host = h
        if from_host and to_host:
            msg = Message(
                id=self.msg_id,
                from_host=from_host,
                to_host=to_host,
                size=self.size,
            )
            from_host.create_new_message(msg)


class MessageDeleteEvent(ExternalEvent):
    """Delete a message from a host at a given time."""

    def __init__(self, time: float, host_addr: int, msg_id: str) -> None:
        super().__init__(time)
        self.host_addr = host_addr
        self.msg_id = msg_id

    def process_event(self, world: Any) -> None:
        for h in world.hosts:
            if h.address == self.host_addr:
                h.router.delete_message(self.msg_id, drop=True)
                break


class ConnectionEvent(ExternalEvent):
    """Force a connection up/down between two hosts."""

    def __init__(
        self, time: float, host1_addr: int, host2_addr: int, is_up: bool
    ) -> None:
        super().__init__(time)
        self.host1_addr = host1_addr
        self.host2_addr = host2_addr
        self.is_up = is_up

    def process_event(self, world: Any) -> None:
        pass  # Connections are managed by interface updates
