"""Network connections between DTN nodes.

Port of Java ``Connection.java``, ``CBRConnection.java``, and
``VBRConnection.java`` into a single Python module with an abstract base
class and two concrete implementations.

Design decisions
~~~~~~~~~~~~~~~~
* ``Connection`` is an abstract base class (ABC).  Subclasses must implement
  ``start_transfer``, ``is_message_transferred``, ``get_speed``, and
  ``get_remaining_byte_count``.
* Host and interface types are ``Any`` to avoid circular imports with the
  not-yet-ported ``DTNHost`` / ``NetworkInterface`` classes.
* ``CBRConnection`` models a constant bit-rate link whose transfer-done time
  is computed once at the start of each transfer.
* ``VBRConnection`` models a variable bit-rate link whose speed is
  recalculated every simulation tick from the endpoint interfaces.
"""

from __future__ import annotations

from abc import ABC, abstractmethod
from typing import Any, Optional

from .sim_clock import SimClock


# ---------------------------------------------------------------------------
# Return codes mirroring MessageRouter constants (defined here to avoid
# a circular import with the not-yet-ported router module).
# ---------------------------------------------------------------------------
RCV_OK: int = 0


# ---------------------------------------------------------------------------
# Abstract Connection
# ---------------------------------------------------------------------------

class Connection(ABC):
    """Abstract bidirectional connection between two DTN nodes."""

    def __init__(
        self,
        from_node: Any,
        from_interface: Any,
        to_node: Any,
        to_interface: Any,
    ) -> None:
        self.from_node: Any = from_node
        self.from_interface: Any = from_interface
        self.to_node: Any = to_node
        self.to_interface: Any = to_interface

        self.is_up: bool = True
        self.msg_on_fly: Optional[Any] = None  # Optional[Message]
        self.msg_from_node: Optional[Any] = None
        self.bytes_transferred: float = 0.0

    # -- Abstract interface --------------------------------------------------

    @abstractmethod
    def start_transfer(self, from_host: Any, msg: Any) -> int:
        """Begin transferring *msg* from *from_host* over this connection.

        Returns a status code (``RCV_OK`` on success).
        """
        ...

    @abstractmethod
    def is_message_transferred(self) -> bool:
        """Return ``True`` when the current transfer is complete."""
        ...

    @abstractmethod
    def get_speed(self) -> float:
        """Return the current transfer speed in bytes per second."""
        ...

    @abstractmethod
    def get_remaining_byte_count(self) -> int:
        """Return bytes still to transfer, or 0 if idle / finished."""
        ...

    # -- Concrete helpers ----------------------------------------------------

    def update(self, dt: float = 0.0) -> None:  # noqa: ARG002 (dt unused in base)
        """Advance the connection state by *dt* seconds.

        Subclasses may override to recalculate variable speeds; the base
        implementation is intentionally a no-op (matching the Java original
        where ``Connection.update()`` has an empty body).
        """

    def is_ready_for_transfer(self) -> bool:
        """Return ``True`` if the link is up and no transfer is in progress."""
        return self.is_up and self.msg_on_fly is None

    def get_message(self) -> Optional[Any]:
        """Return the message currently being transferred, or ``None``."""
        return self.msg_on_fly

    def is_initiator(self, node: Any) -> bool:
        """Return ``True`` if *node* initiated this connection."""
        return node is self.from_node

    def set_up_state(self, state: bool) -> None:
        """Set the connection up/down state."""
        self.is_up = state

    def finalize_transfer(self) -> None:
        """Complete the current transfer, notifying the receiving node."""
        assert self.msg_on_fly is not None, f"Nothing to finalize in {self}"
        assert self.msg_from_node is not None, "msg_from_node is not set"

        self.bytes_transferred += self.msg_on_fly.size

        receiving_node = self.get_other_node(self.msg_from_node)
        receiving_node.message_transferred(self.msg_on_fly.id, self.msg_from_node)
        self._clear_msg_on_fly()

    def abort_transfer(self) -> None:
        """Abort the current transfer, notifying the receiving node."""
        assert self.msg_on_fly is not None, (
            f"No message to abort at {self.msg_from_node}"
        )
        bytes_remaining = self.get_remaining_byte_count()

        self.bytes_transferred += self.msg_on_fly.size - bytes_remaining

        receiving_node = self.get_other_node(self.msg_from_node)
        receiving_node.message_aborted(
            self.msg_on_fly.id, self.msg_from_node, bytes_remaining
        )
        self._clear_msg_on_fly()

    def get_total_bytes_transferred(self) -> float:
        """Return total bytes transferred across all transfers on this link."""
        if self.msg_on_fly is None:
            return self.bytes_transferred
        if self.is_message_transferred():
            return self.bytes_transferred + self.msg_on_fly.size
        return self.bytes_transferred + (
            self.msg_on_fly.size - self.get_remaining_byte_count()
        )

    def get_other_node(self, node: Any) -> Any:
        """Return the node at the *other* end of the connection."""
        if node is self.from_node:
            return self.to_node
        return self.from_node

    def get_other_interface(self, iface: Any) -> Any:
        """Return the interface at the *other* end of the connection."""
        if iface is self.from_interface:
            return self.to_interface
        return self.from_interface

    # -- Internal ------------------------------------------------------------

    def _clear_msg_on_fly(self) -> None:
        self.msg_on_fly = None
        self.msg_from_node = None

    # -- Display -------------------------------------------------------------

    def __str__(self) -> str:
        status = "up" if self.is_up else "down"
        base = f"{self.from_node}<->{self.to_node} ({self.get_speed():.0f}Bps) is {status}"
        if self.msg_on_fly is not None:
            base += f" transferring {self.msg_on_fly} from {self.msg_from_node}"
        return base


# ---------------------------------------------------------------------------
# CBRConnection -- constant bit rate
# ---------------------------------------------------------------------------

class CBRConnection(Connection):
    """Constant bit-rate connection.

    The transfer-done time is calculated once when a transfer begins and
    the remaining byte count is derived from the elapsed time.
    """

    def __init__(
        self,
        from_node: Any,
        from_interface: Any,
        to_node: Any,
        to_interface: Any,
        speed: float,
    ) -> None:
        super().__init__(from_node, from_interface, to_node, to_interface)
        self._speed: float = speed
        self._transfer_done_time: float = 0.0

    # -- Abstract implementations --------------------------------------------

    def start_transfer(self, from_host: Any, msg: Any) -> int:
        assert self.msg_on_fly is None, (
            f"Already transferring {self.msg_on_fly} from {self.msg_from_node} "
            f"to {self.get_other_node(self.msg_from_node)}. "
            f"Cannot start transfer of {msg} from {from_host}"
        )

        self.msg_from_node = from_host
        new_message = msg.replicate()
        ret_val = self.get_other_node(from_host).receive_message(new_message, from_host)

        if ret_val == RCV_OK:
            self.msg_on_fly = new_message
            self._transfer_done_time = (
                SimClock.get_time() + msg.size / self._speed
            )

        return ret_val

    def is_message_transferred(self) -> bool:
        return self.get_remaining_byte_count() == 0

    def get_speed(self) -> float:
        return self._speed

    def get_remaining_byte_count(self) -> int:
        if self.msg_on_fly is None:
            return 0
        remaining = int(
            (self._transfer_done_time - SimClock.get_time()) * self._speed
        )
        return max(remaining, 0)

    # -- Overrides -----------------------------------------------------------

    def abort_transfer(self) -> None:
        assert self.msg_on_fly is not None, (
            f"No message to abort at {self.msg_from_node}"
        )
        self.get_other_node(self.msg_from_node).message_aborted(
            self.msg_on_fly.id, self.msg_from_node, self.get_remaining_byte_count()
        )
        self._clear_msg_on_fly()
        self._transfer_done_time = 0.0

    @property
    def transfer_done_time(self) -> float:
        return self._transfer_done_time

    def __str__(self) -> str:
        status = "up" if self.is_up else "down"
        base = f"{self.from_node}<->{self.to_node} ({self._speed:.0f}Bps) is {status}"
        if self.msg_on_fly is not None:
            base += (
                f" transferring {self.msg_on_fly} from {self.msg_from_node}"
                f" until {self._transfer_done_time:.2f}"
            )
        return base


# ---------------------------------------------------------------------------
# VBRConnection -- variable bit rate
# ---------------------------------------------------------------------------

class VBRConnection(Connection):
    """Variable bit-rate connection.

    The speed is recalculated every tick based on the current
    ``get_transmit_speed`` values of both endpoint interfaces.  Bytes sent
    are accumulated incrementally.
    """

    def __init__(
        self,
        from_node: Any,
        from_interface: Any,
        to_node: Any,
        to_interface: Any,
    ) -> None:
        super().__init__(from_node, from_interface, to_node, to_interface)
        self._msg_size: int = 0
        self._msg_sent: int = 0
        self._current_speed: int = 0
        self._last_update: float = 0.0

    # -- Abstract implementations --------------------------------------------

    def start_transfer(self, from_host: Any, msg: Any) -> int:
        assert self.msg_on_fly is None, (
            f"Already transferring {self.msg_on_fly} from {self.msg_from_node} "
            f"to {self.get_other_node(self.msg_from_node)}. "
            f"Cannot start transfer of {msg} from {from_host}"
        )

        self.msg_from_node = from_host
        new_message = msg.replicate()
        ret_val = self.get_other_node(from_host).receive_message(new_message, from_host)

        if ret_val == RCV_OK:
            self.msg_on_fly = new_message
            self._msg_size = msg.size
            self._msg_sent = 0

        return ret_val

    def is_message_transferred(self) -> bool:
        return self._msg_sent >= self._msg_size

    def get_speed(self) -> float:
        return float(self._current_speed)

    def get_remaining_byte_count(self) -> int:
        remaining = self._msg_size - self._msg_sent
        return max(remaining, 0)

    # -- Overrides -----------------------------------------------------------

    def update(self, dt: float = 0.0) -> None:  # noqa: ARG002 (dt unused; we use wall-clock delta)
        """Recalculate speed from both endpoint interfaces and advance bytes sent."""
        from_speed: int = self.from_interface.get_transmit_speed(self.to_interface)
        to_speed: int = self.to_interface.get_transmit_speed(self.from_interface)

        self._current_speed = min(from_speed, to_speed)

        now = SimClock.get_time()
        self._msg_sent += int(self._current_speed * (now - self._last_update))
        self._last_update = now
