"""RepoStationaryMovement: stationary placement for EDR nodes.

Port of Java RepoStationaryMovement.java + SimScenario grid placement.
EDRs are placed on a sqrt(N) x sqrt(N) grid within the world area.
"""

from __future__ import annotations

import math
from typing import Optional

from ..core.coord import Coord
from .base import MovementModel
from .map_data.path import Path


class RepoStationaryMovement(MovementModel):
    """Stationary movement model -- nodes don't move after initial placement."""

    _placement_index: int = 0  # Class-level counter for grid placement
    _total_count: int = 0
    _grid_cols: int = 0
    _grid_rows: int = 0
    _x_offset: float = 500.0
    _y_offset: float = 200.0

    def __init__(
        self,
        max_x: int = 4500,
        max_y: int = 3400,
        x_offset: float = 500.0,
        y_offset: float = 200.0,
        total_count: int = 80,
        rng_seed: int = 0,
    ) -> None:
        super().__init__(
            min_speed=0, max_speed=0, min_wait=0, max_wait=0,
            max_x=max_x, max_y=max_y, rng_seed=rng_seed,
        )
        self._x_offset = x_offset
        self._y_offset = y_offset
        self._total_count = total_count
        self._assigned_location: Optional[Coord] = None

    @classmethod
    def reset_placement(cls) -> None:
        cls._placement_index = 0

    def get_initial_location(self) -> Coord:
        if self._assigned_location is not None:
            return self._assigned_location

        n = self._total_count
        cols = int(math.ceil(math.sqrt(n)))
        rows = int(math.ceil(n / cols))

        idx = RepoStationaryMovement._placement_index
        RepoStationaryMovement._placement_index += 1

        col = idx % cols
        row = idx // cols

        # Space evenly within world bounds minus offsets
        usable_x = self.max_x - 2 * self._x_offset
        usable_y = self.max_y - 2 * self._y_offset

        x = self._x_offset + (col + 0.5) * usable_x / max(cols, 1)
        y = self._y_offset + (row + 0.5) * usable_y / max(rows, 1)

        self._assigned_location = Coord(x, y)
        return self._assigned_location

    def get_path(self) -> Optional[Path]:
        return None  # Stationary -- never moves

    def replicate(self) -> RepoStationaryMovement:
        return RepoStationaryMovement(
            max_x=self.max_x, max_y=self.max_y,
            x_offset=self._x_offset, y_offset=self._y_offset,
            total_count=self._total_count,
            rng_seed=self._rng.randint(0, 2**31),
        )
