"""Simple broadcast network interface with constant bit-rate connections.

Port of Java ``SimpleBroadcastInterface.java``.  Provides a fixed transmit
speed and range; connections are modelled as :class:`CBRConnection` instances.
"""

from __future__ import annotations

from ..core.connection import CBRConnection, Connection
from ..core.network_interface import NetworkInterface


class SimpleBroadcastInterface(NetworkInterface):
    """A network interface that provides a constant bit-rate service.

    Only one transmission can be active per connection at a time.

    Parameters
    ----------
    interface_type:
        Short identifier for the interface technology.
    transmit_range:
        Maximum radio range in metres.
    transmit_speed:
        Fixed transmit speed in bytes per second.
    scan_interval:
        Interval between active scans in seconds (0.0 = continuous).
    """

    def __init__(
        self,
        interface_type: str = "SimpleBroadcastInterface",
        transmit_range: float = 0.0,
        transmit_speed: int = 0,
        scan_interval: float = 0.0,
    ) -> None:
        super().__init__(
            interface_type=interface_type,
            transmit_range=transmit_range,
            transmit_speed=transmit_speed,
            scan_interval=scan_interval,
        )

    # -- Abstract implementations --------------------------------------------

    def replicate(self) -> SimpleBroadcastInterface:
        """Return a copy of this interface with a new address."""
        clone = SimpleBroadcastInterface(
            interface_type=self.interface_type,
            transmit_range=self.transmit_range,
            transmit_speed=self.transmit_speed,
            scan_interval=self._scan_interval,
        )
        clone.host = self.host
        clone._connection_listeners = self._connection_listeners
        return clone

    def connect(self, other: NetworkInterface) -> None:
        """Connect to *other* if scanning, in range, active, and not already connected."""
        if (
            self.is_scanning()
            and other.host is not None
            and other.host.is_active()
            and self.is_within_range(other)
            and not self.is_connected(other)
            and self is not other
        ):
            # Connection speed is the slower of the two interfaces
            con_speed = min(
                other.get_transmit_speed(self),
                self.transmit_speed,
            )
            con: Connection = CBRConnection(
                self.host, self, other.host, other, float(con_speed)
            )
            self._connect(con, other)

    def update(self) -> None:
        """Tear down out-of-range connections and create new ones."""
        if self.optimizer is None:
            from .connectivity_grid import ConnectivityGrid
            self.optimizer = ConnectivityGrid.get_or_create(
                key=hash(self.interface_type),
                range_value=self.transmit_range,
            )
            self.optimizer.add_interface(self)

        # Update our location in the spatial index
        self.optimizer.update_location(self)

        # Tear down connections that are now out of range
        i = 0
        while i < len(self.connections):
            con = self.connections[i]
            other_iface = con.get_other_interface(self)

            assert con.is_up, f"Connection {con} was down!"

            if not self.is_within_range(other_iface):
                self._disconnect(con, other_iface)
                del self.connections[i]
            else:
                i += 1

        # Discover and connect to new neighbours
        near_interfaces = self.optimizer.get_near_interfaces(self)
        for iface in near_interfaces:
            self.connect(iface)

    def create_connection(self, other: NetworkInterface) -> None:
        """Create a CBR connection to *other* without range / activity checks."""
        if not self.is_connected(other) and self is not other:
            con_speed = min(
                other.get_transmit_speed(self),
                self.transmit_speed,
            )
            con: Connection = CBRConnection(
                self.host, self, other.host, other, float(con_speed)
            )
            self._connect(con, other)

    # -- Display -------------------------------------------------------------

    def __str__(self) -> str:
        return f"SimpleBroadcastInterface {super().__str__()}"
