"""Distance-dependent capacity network interface with variable bit-rate connections.

Port of Java ``DistanceCapacityInterface.java``.  The transmit speed is
linearly interpolated from a list of speed tiers based on the fractional
distance between the two endpoints relative to the transmit range.
Connections are modelled as :class:`VBRConnection` instances whose speed is
recalculated every simulation tick.
"""

from __future__ import annotations

from typing import Optional

from ..core.connection import Connection, VBRConnection
from ..core.network_interface import NetworkInterface


class DistanceCapacityInterface(NetworkInterface):
    """Network interface whose capacity degrades with distance.

    Parameters
    ----------
    interface_type:
        Short identifier for the interface technology.
    transmit_range:
        Maximum radio range in metres.
    transmit_speed:
        Nominal transmit speed in bytes per second (used as a fallback;
        the actual speed is derived from *transmit_speeds*).
    transmit_speeds:
        Ordered list of speed tiers (bytes/s) from closest to farthest.
        The first value is the speed at distance 0; the last is the speed
        at the edge of ``transmit_range``.  Intermediate distances are
        linearly interpolated between adjacent entries.
    scan_interval:
        Interval between active scans in seconds (0.0 = continuous).
    """

    def __init__(
        self,
        interface_type: str = "DistanceCapacityInterface",
        transmit_range: float = 0.0,
        transmit_speed: int = 0,
        transmit_speeds: list[int] | None = None,
        scan_interval: float = 0.0,
    ) -> None:
        super().__init__(
            interface_type=interface_type,
            transmit_range=transmit_range,
            transmit_speed=transmit_speed,
            scan_interval=scan_interval,
        )
        self.transmit_speeds: list[int] = (
            list(transmit_speeds) if transmit_speeds is not None else []
        )

    # -- Speed interpolation -------------------------------------------------

    def get_transmit_speed(self, other: Optional[NetworkInterface] = None) -> int:
        """Return interpolated speed in bytes/s based on distance to *other*.

        If *other* is ``None`` or no speed tiers are configured, the base
        ``transmit_speed`` is returned.
        """
        if other is None or not self.transmit_speeds:
            return self.transmit_speed

        distance: float = other.get_location().distance(self.get_location())

        if distance >= self.transmit_range:
            return 0

        # Map the distance to a fractional index into the speed-tier list
        fraction_index = (
            (distance / self.transmit_range) * (len(self.transmit_speeds) - 1)
        )
        index = int(fraction_index)
        decimal = fraction_index - index

        # Guard against index == last element (distance exactly at range boundary)
        if index >= len(self.transmit_speeds) - 1:
            return self.transmit_speeds[-1]

        speed = (
            self.transmit_speeds[index] * (1.0 - decimal)
            + self.transmit_speeds[index + 1] * decimal
        )
        return int(speed)

    # -- Abstract implementations --------------------------------------------

    def replicate(self) -> DistanceCapacityInterface:
        """Return a copy of this interface with a new address."""
        clone = DistanceCapacityInterface(
            interface_type=self.interface_type,
            transmit_range=self.transmit_range,
            transmit_speed=self.transmit_speed,
            transmit_speeds=list(self.transmit_speeds),
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
            con: Connection = VBRConnection(
                self.host, self, other.host, other
            )
            self._connect(con, other)

    def update(self) -> None:
        """Recalculate speeds, tear down out-of-range, create new connections."""
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

        # Recalculate VBR speeds on all existing connections
        for con in self.connections:
            con.update()

    def create_connection(self, other: NetworkInterface) -> None:
        """Create a VBR connection to *other* without range / activity checks."""
        if not self.is_connected(other) and self is not other:
            con: Connection = VBRConnection(
                self.host, self, other.host, other
            )
            self._connect(con, other)

    # -- Display -------------------------------------------------------------

    def __str__(self) -> str:
        return f"DistanceCapacityInterface {super().__str__()}"
