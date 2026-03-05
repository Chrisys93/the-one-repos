"""2-D coordinate used for node positions and movement."""

from __future__ import annotations

import math
from dataclasses import dataclass


@dataclass(frozen=True)
class Coord:
    """Immutable 2-D coordinate."""
    x: float
    y: float

    def distance(self, other: Coord) -> float:
        """Euclidean distance to *other*."""
        dx = self.x - other.x
        dy = self.y - other.y
        return math.hypot(dx, dy)

    def translate(self, dx: float, dy: float) -> Coord:
        """Return a new ``Coord`` offset by *(dx, dy)*."""
        return Coord(self.x + dx, self.y + dy)

    def clone(self) -> Coord:
        return Coord(self.x, self.y)

    def __str__(self) -> str:
        return f"({self.x:.2f}, {self.y:.2f})"
