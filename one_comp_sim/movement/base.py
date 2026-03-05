"""MovementModel ABC -- base class for all movement models.

Port of Java MovementModel.java.
"""

from __future__ import annotations

import random
from abc import ABC, abstractmethod
from typing import TYPE_CHECKING, Optional

from ..core.coord import Coord
from .map_data.path import Path

if TYPE_CHECKING:
    pass


class MovementModel(ABC):
    """Abstract base for node movement models."""

    def __init__(
        self,
        min_speed: float = 0.5,
        max_speed: float = 1.5,
        min_wait: float = 0.0,
        max_wait: float = 120.0,
        max_x: int = 4500,
        max_y: int = 3400,
        rng_seed: int = 0,
    ) -> None:
        self.min_speed = min_speed
        self.max_speed = max_speed
        self.min_wait = min_wait
        self.max_wait = max_wait
        self.max_x = max_x
        self.max_y = max_y
        self._rng = random.Random(rng_seed)
        self._wait_until: float = 0.0
        self._last_waypoint: Optional[Coord] = None
        self.host: object = None  # Set by DTNHost

    @abstractmethod
    def get_path(self) -> Optional[Path]:
        """Return the next movement path, or None if waiting."""
        ...

    @abstractmethod
    def get_initial_location(self) -> Coord:
        """Return the starting location for this node."""
        ...

    @abstractmethod
    def replicate(self) -> MovementModel:
        """Create a copy for a new host."""
        ...

    def is_active(self) -> bool:
        return True

    def get_random_speed(self) -> float:
        if self.min_speed >= self.max_speed:
            return self.min_speed
        return self._rng.uniform(self.min_speed, self.max_speed)

    def get_random_wait_time(self) -> float:
        if self.min_wait >= self.max_wait:
            return self.min_wait
        return self._rng.uniform(self.min_wait, self.max_wait)

    def get_random_coord(self) -> Coord:
        return Coord(
            self._rng.uniform(0, self.max_x),
            self._rng.uniform(0, self.max_y),
        )
