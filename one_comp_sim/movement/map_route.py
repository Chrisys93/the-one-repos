"""MapRouteMovement: movement along fixed routes (e.g., bus routes).

Port of Java MapRouteMovement.java.
Nodes follow a predefined route from a WKT file, looping back and forth.
"""

from __future__ import annotations

import re
from typing import Optional

from ..core.coord import Coord
from .base import MovementModel
from .map_data.path import Path


class MapRouteMovement(MovementModel):
    """Follow a fixed route loaded from a WKT file."""

    def __init__(
        self,
        route_file: str,
        route_type: int = 1,
        min_speed: float = 4.0,
        max_speed: float = 10.0,
        min_wait: float = 10.0,
        max_wait: float = 30.0,
        max_x: int = 4500,
        max_y: int = 3400,
        rng_seed: int = 0,
    ) -> None:
        super().__init__(
            min_speed=min_speed, max_speed=max_speed,
            min_wait=min_wait, max_wait=max_wait,
            max_x=max_x, max_y=max_y, rng_seed=rng_seed,
        )
        self.route_file = route_file
        self.route_type = route_type
        self._route_coords: list[Coord] = []
        self._route_index: int = 0
        self._direction: int = 1  # 1 = forward, -1 = backward
        self._load_route()

    def _load_route(self) -> None:
        try:
            with open(self.route_file, "r") as f:
                content = f.read()
        except FileNotFoundError:
            return

        pattern = re.compile(r"LINESTRING\s*\(([\d\s.,\-]+)\)", re.IGNORECASE)
        for match in pattern.finditer(content):
            for pair in match.group(1).split(","):
                parts = pair.strip().split()
                if len(parts) >= 2:
                    self._route_coords.append(
                        Coord(float(parts[0]), float(parts[1]))
                    )

    def get_initial_location(self) -> Coord:
        if not self._route_coords:
            return self.get_random_coord()
        # Start at a random point along the route
        self._route_index = self._rng.randint(0, len(self._route_coords) - 1)
        loc = self._route_coords[self._route_index]
        self._last_waypoint = loc
        return loc

    def get_path(self) -> Optional[Path]:
        if not self._route_coords:
            return None

        # Advance to next stop on the route
        next_idx = self._route_index + self._direction
        if next_idx >= len(self._route_coords):
            if self.route_type == 1:
                # Circular: loop back
                self._direction = -1
                next_idx = self._route_index + self._direction
            else:
                # Restart from beginning
                next_idx = 0
        elif next_idx < 0:
            self._direction = 1
            next_idx = self._route_index + self._direction

        if next_idx < 0 or next_idx >= len(self._route_coords):
            return None

        self._route_index = next_idx
        dest = self._route_coords[next_idx]
        speed = self.get_random_speed()
        self._last_waypoint = dest
        return Path(coords=[dest], speed=speed)

    def replicate(self) -> MapRouteMovement:
        return MapRouteMovement(
            route_file=self.route_file,
            route_type=self.route_type,
            min_speed=self.min_speed, max_speed=self.max_speed,
            min_wait=self.min_wait, max_wait=self.max_wait,
            max_x=self.max_x, max_y=self.max_y,
            rng_seed=self._rng.randint(0, 2**31),
        )
