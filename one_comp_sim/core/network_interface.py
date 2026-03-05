"""Abstract base class for network interfaces attached to DTN hosts.

Port of Java ``NetworkInterface.java``.  Concrete implementations live in
the ``interfaces`` package (e.g. ``SimpleBroadcastInterface``,
``DistanceCapacityInterface``).

Design decisions
~~~~~~~~~~~~~~~~
* ``NetworkInterface`` is an ABC -- subclasses **must** implement
  ``replicate()``, ``connect(other)``, ``update()``, and
  ``create_connection(other)``.
* Host and optimizer types are ``Any`` for now to avoid circular imports
  with the not-yet-ported ``DTNHost`` / ``ConnectivityGrid`` classes.
* A module-level context-aware counter provides unique interface addresses.
* Connection-listener notification is preserved from the Java original so
  that reporters can observe link up/down events.
"""

from __future__ import annotations

import contextvars
import random
from abc import ABC, abstractmethod
from typing import Any, Callable, Optional

from .connection import Connection
from .sim_clock import SimClock

# ---------------------------------------------------------------------------
# Module-level interface address counter (context-aware)
# ---------------------------------------------------------------------------

_next_address: contextvars.ContextVar[int] = contextvars.ContextVar(
    "_next_address", default=0
)

# Shared RNG for initial scan-time jitter (seeded for reproducibility)
_rng: random.Random = random.Random(0)


def _allocate_address() -> int:
    """Return the next unique interface address and increment the counter."""
    addr = _next_address.get()
    _next_address.set(addr + 1)
    return addr


def reset_interface_counter() -> None:
    """Reset address counter and RNG (call between simulation runs)."""
    _next_address.set(0)
    _rng.seed(0)


# ---------------------------------------------------------------------------
# Connection-event type codes
# ---------------------------------------------------------------------------

_CON_UP: int = 1
_CON_DOWN: int = 2

# ---------------------------------------------------------------------------
# ModuleCommunicationBus key constants
# ---------------------------------------------------------------------------

SCAN_INTERVAL_ID: str = "Network.scanInterval"
RANGE_ID: str = "Network.radioRange"
SPEED_ID: str = "Network.speed"

# Type alias for connection listener callbacks
ConnectionListenerCallback = Callable[..., None]


# ---------------------------------------------------------------------------
# Abstract NetworkInterface
# ---------------------------------------------------------------------------

class NetworkInterface(ABC):
    """Abstract base for all network interfaces attached to a DTN host.

    Parameters
    ----------
    interface_type:
        Short identifier for the interface technology (e.g. ``"btInterface"``).
    transmit_range:
        Maximum radio range in metres.
    transmit_speed:
        Base transmit speed in bytes per second.
    scan_interval:
        Interval between active scans in seconds.  ``0.0`` means the
        interface scans continuously.
    """

    def __init__(
        self,
        interface_type: str = "Default",
        transmit_range: float = 0.0,
        transmit_speed: int = 0,
        scan_interval: float = 0.0,
    ) -> None:
        self.interface_type: str = interface_type
        self.transmit_range: float = transmit_range
        self.transmit_speed: int = transmit_speed
        self.connections: list[Connection] = []
        self.host: Any = None
        self.optimizer: Any = None  # ConnectivityGrid or similar

        self._address: int = _allocate_address()
        self._scan_interval: float = scan_interval
        self._last_scan_time: float = _rng.random() * scan_interval
        self._connection_listeners: list[ConnectionListenerCallback] = []

    # -- Properties ----------------------------------------------------------

    @property
    def address(self) -> int:
        """Unique network-interface address."""
        return self._address

    @property
    def scan_interval(self) -> float:
        return self._scan_interval

    @scan_interval.setter
    def scan_interval(self, value: float) -> None:
        self._scan_interval = value

    # -- Abstract methods (subclasses MUST implement) ------------------------

    @abstractmethod
    def replicate(self) -> NetworkInterface:
        """Return a copy of this interface (new address, shared listeners)."""
        ...

    @abstractmethod
    def connect(self, other: NetworkInterface) -> None:
        """Attempt to connect to *other* (checking range, activity, etc.)."""
        ...

    @abstractmethod
    def update(self) -> None:
        """Update connection state: tear down out-of-range, create new."""
        ...

    @abstractmethod
    def create_connection(self, other: NetworkInterface) -> None:
        """Create a connection to *other* without range / activity checks."""
        ...

    # -- Host setup ----------------------------------------------------------

    def set_host(self, host: Any) -> None:
        """Bind this interface to a host and register with the comm bus."""
        self.host = host

    def set_connection_listeners(
        self, listeners: list[ConnectionListenerCallback]
    ) -> None:
        """Set the list of connection-event listeners."""
        self._connection_listeners = listeners

    # -- Location helper -----------------------------------------------------

    def get_location(self) -> Any:
        """Return the current location of the host (delegates to host)."""
        return self.host.get_location()

    # -- Transmit speed (overridable by subclasses like DistanceCapacity) ----

    def get_transmit_speed(self, other: Optional[NetworkInterface] = None) -> int:  # noqa: ARG002
        """Return the transmit speed in bytes/s toward *other*.

        The base implementation returns the fixed ``transmit_speed``;
        subclasses (e.g. ``DistanceCapacityInterface``) override this to
        account for distance.
        """
        return self.transmit_speed

    # -- Scanning ------------------------------------------------------------

    def is_scanning(self) -> bool:
        """Return ``True`` if the interface is currently in scanning mode."""
        sim_time = SimClock.get_time()

        if self._scan_interval > 0.0:
            if sim_time < self._last_scan_time:
                return False
            if sim_time > self._last_scan_time + self._scan_interval:
                self._last_scan_time = sim_time
                return True
            if sim_time != self._last_scan_time:
                return False

        # interval == 0 or still inside the current scan round
        return True

    # -- Range / connectivity queries ----------------------------------------

    def is_within_range(self, other: NetworkInterface) -> bool:
        """Return ``True`` if both interfaces are within mutual radio range."""
        smaller_range = min(self.transmit_range, other.transmit_range)
        distance = self.host.get_location().distance(other.host.get_location())
        return distance <= smaller_range

    def is_connected(self, other: NetworkInterface) -> bool:
        """Return ``True`` if this interface already has a connection to *other*."""
        for con in self.connections:
            if con.get_other_interface(self) is other:
                return True
        return False

    # -- Connection lifecycle helpers ----------------------------------------

    def _connect(self, con: Connection, other: NetworkInterface) -> None:
        """Wire up a bidirectional connection and notify listeners / hosts.

        This is the low-level helper called by concrete ``connect()``
        implementations after they have verified all preconditions.
        """
        self.connections.append(con)
        self._notify_connection_listeners(_CON_UP, other.host)

        other.connections.append(con)

        self.host.connection_up(con)
        other.host.connection_up(con)

    def _disconnect(self, con: Connection, other: NetworkInterface) -> None:
        """Tear down a bidirectional connection and notify listeners / hosts."""
        con.set_up_state(False)
        self._notify_connection_listeners(_CON_DOWN, other.host)

        if con in other.connections:
            other.connections.remove(con)
        else:
            raise RuntimeError(
                f"No connection {con} found in {other}"
            )

        self.host.connection_down(con)
        other.host.connection_down(con)

    def destroy_connection(self, other: NetworkInterface) -> None:
        """Remove any connection between this interface and *other*."""
        other_host = other.host
        i = 0
        while i < len(self.connections):
            con = self.connections[i]
            if con.get_other_node(self.host) is other_host:
                self._remove_connection_by_index(i, other)
                # Do not increment -- the list has shifted.
            else:
                i += 1

    def _remove_connection_by_index(
        self, index: int, other: NetworkInterface
    ) -> None:
        """Remove a connection by position in this interface's list."""
        con = self.connections[index]
        other_node = other.host
        con.set_up_state(False)
        self._notify_connection_listeners(_CON_DOWN, other_node)

        if con in other.connections:
            other.connections.remove(con)
        else:
            raise RuntimeError(
                f"No connection {con} found in {other_node}"
            )

        self.host.connection_down(con)
        other_node.connection_down(con)
        del self.connections[index]

    # -- Connection-listener notification ------------------------------------

    def _notify_connection_listeners(self, event_type: int, other_host: Any) -> None:
        for listener in self._connection_listeners:
            if event_type == _CON_UP:
                listener.hosts_connected(self.host, other_host)
            elif event_type == _CON_DOWN:
                listener.hosts_disconnected(self.host, other_host)

    # -- ModuleCommunicationBus callback -------------------------------------

    def module_value_changed(self, key: str, new_value: Any) -> None:
        """Handle changes published on the host's ``ModuleCommunicationBus``."""
        if key == SCAN_INTERVAL_ID:
            self._scan_interval = float(new_value)
        elif key == SPEED_ID:
            self.transmit_speed = int(new_value)
        elif key == RANGE_ID:
            self.transmit_range = float(new_value)
        else:
            raise RuntimeError(f"Unexpected combus key: {key!r}")

    # -- Validation ----------------------------------------------------------

    @staticmethod
    def _ensure_positive(value: float, name: str) -> None:
        if value < 0:
            raise ValueError(
                f"Negative value ({value}) not accepted for setting {name!r}"
            )

    # -- Display -------------------------------------------------------------

    def __str__(self) -> str:
        return (
            f"net interface {self._address} of {self.host}. "
            f"Connections: {self.connections}"
        )
