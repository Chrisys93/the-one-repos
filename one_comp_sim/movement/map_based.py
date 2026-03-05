"""Map-based movement models using WKT road/path networks.

Port of Java ShortestPathMapBasedMovement.java + MapBasedMovement.java.
"""

from __future__ import annotations

from typing import TYPE_CHECKING, Optional

from ..core.coord import Coord
from .base import MovementModel
from .map_data.dijkstra import dijkstra
from .map_data.path import Path
from .map_data.sim_map import MapNode, SimMap

if TYPE_CHECKING:
    pass


class MapBasedMovement(MovementModel):
    """Movement along map road segments."""

    def __init__(
        self,
        sim_map: SimMap,
        ok_maps: list[int] | None = None,
        min_speed: float = 0.5,
        max_speed: float = 1.5,
        min_wait: float = 0.0,
        max_wait: float = 120.0,
        max_x: int = 4500,
        max_y: int = 3400,
        rng_seed: int = 0,
    ) -> None:
        super().__init__(
            min_speed=min_speed, max_speed=max_speed,
            min_wait=min_wait, max_wait=max_wait,
            max_x=max_x, max_y=max_y, rng_seed=rng_seed,
        )
        self.sim_map = sim_map
        self.ok_maps = ok_maps
        self._current_node: Optional[MapNode] = None

    def get_initial_location(self) -> Coord:
        candidates = self.sim_map.nodes
        if self.ok_maps:
            candidates = [n for n in candidates if n.map_index in self.ok_maps]
        if not candidates:
            return self.get_random_coord()
        node = self._rng.choice(candidates)
        self._current_node = node
        self._last_waypoint = node.location
        return node.location

    def get_path(self) -> Optional[Path]:
        if self._current_node is None:
            return None
        # Pick a random neighbor and walk there
        if not self._current_node.neighbors:
            return None
        next_node = self._rng.choice(self._current_node.neighbors)
        speed = self.get_random_speed()
        p = Path(coords=[next_node.location], speed=speed)
        self._current_node = next_node
        self._last_waypoint = next_node.location
        return p

    def replicate(self) -> MapBasedMovement:
        return MapBasedMovement(
            sim_map=self.sim_map, ok_maps=self.ok_maps,
            min_speed=self.min_speed, max_speed=self.max_speed,
            min_wait=self.min_wait, max_wait=self.max_wait,
            max_x=self.max_x, max_y=self.max_y,
            rng_seed=self._rng.randint(0, 2**31),
        )


class ShortestPathMapBasedMovement(MapBasedMovement):
    """Movement using Dijkstra shortest paths between random map nodes."""

    def get_path(self) -> Optional[Path]:
        if self._current_node is None:
            return None

        # Pick a random destination node
        candidates = self.sim_map.nodes
        if self.ok_maps:
            candidates = [n for n in candidates if n.map_index in self.ok_maps]
        if not candidates:
            return None

        dest_node = self._rng.choice(candidates)
        if dest_node == self._current_node:
            return None

        # Find shortest path
        node_path = dijkstra(self._current_node, dest_node)
        if node_path is None or len(node_path) < 2:
            return None

        speed = self.get_random_speed()
        p = Path(speed=speed)
        for node in node_path[1:]:  # Skip current position
            p.add_waypoint(node.location)

        self._current_node = dest_node
        self._last_waypoint = dest_node.location
        return p

    def replicate(self) -> ShortestPathMapBasedMovement:
        return ShortestPathMapBasedMovement(
            sim_map=self.sim_map, ok_maps=self.ok_maps,
            min_speed=self.min_speed, max_speed=self.max_speed,
            min_wait=self.min_wait, max_wait=self.max_wait,
            max_x=self.max_x, max_y=self.max_y,
            rng_seed=self._rng.randint(0, 2**31),
        )
