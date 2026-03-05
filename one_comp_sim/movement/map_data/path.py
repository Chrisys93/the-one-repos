"""Path dataclass for movement waypoints."""

from __future__ import annotations

from dataclasses import dataclass, field

from ...core.coord import Coord


@dataclass
class Path:
    """A sequence of waypoints that a node traverses at a given speed."""
    coords: list[Coord] = field(default_factory=list)
    speed: float = 1.0

    def add_waypoint(self, c: Coord) -> None:
        self.coords.append(c)

    def get_next_waypoint(self) -> Coord | None:
        if not self.coords:
            return None
        return self.coords[0]

    def pop_waypoint(self) -> Coord | None:
        if not self.coords:
            return None
        return self.coords.pop(0)

    def has_next(self) -> bool:
        return len(self.coords) > 0

    def __len__(self) -> int:
        return len(self.coords)
