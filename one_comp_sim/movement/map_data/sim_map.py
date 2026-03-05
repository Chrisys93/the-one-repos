"""SimMap: WKT file parsing and graph representation for map-based movement.

Uses shapely for WKT parsing when available, falls back to basic parsing.
"""

from __future__ import annotations

import re
from dataclasses import dataclass, field
from typing import Optional

from ...core.coord import Coord


@dataclass
class MapNode:
    """A node in the road/path network graph."""
    location: Coord
    neighbors: list[MapNode] = field(default_factory=list, repr=False)
    map_index: int = -1  # Which map file this node belongs to

    def add_neighbor(self, other: MapNode) -> None:
        if other not in self.neighbors:
            self.neighbors.append(other)
            other.neighbors.append(self)

    def __hash__(self) -> int:
        return hash((self.location.x, self.location.y))

    def __eq__(self, other: object) -> bool:
        if not isinstance(other, MapNode):
            return NotImplemented
        return self.location == other.location


class SimMap:
    """Loads and manages WKT map files for map-based movement models."""

    def __init__(self) -> None:
        self.nodes: list[MapNode] = []
        self._nodes_by_coord: dict[tuple[float, float], MapNode] = {}
        self._map_files: list[str] = []

    @classmethod
    def from_wkt_files(cls, filepaths: list[str]) -> SimMap:
        sim_map = cls()
        for idx, fp in enumerate(filepaths):
            sim_map._load_wkt_file(fp, map_index=idx)
        return sim_map

    def _load_wkt_file(self, filepath: str, map_index: int) -> None:
        self._map_files.append(filepath)
        try:
            with open(filepath, "r") as f:
                content = f.read()
        except FileNotFoundError:
            return

        # Parse LINESTRING entries from WKT
        linestring_pattern = re.compile(
            r"LINESTRING\s*\(([\d\s.,\-]+)\)", re.IGNORECASE
        )
        for match in linestring_pattern.finditer(content):
            coords_str = match.group(1)
            coords = self._parse_coord_list(coords_str)
            prev_node: Optional[MapNode] = None
            for coord in coords:
                node = self._get_or_create_node(coord, map_index)
                if prev_node is not None:
                    prev_node.add_neighbor(node)
                prev_node = node

        # Also parse POINT entries
        point_pattern = re.compile(
            r"POINT\s*\(([\d\s.\-]+)\)", re.IGNORECASE
        )
        for match in point_pattern.finditer(content):
            coords_str = match.group(1)
            parts = coords_str.strip().split()
            if len(parts) >= 2:
                coord = Coord(float(parts[0]), float(parts[1]))
                self._get_or_create_node(coord, map_index)

    def _parse_coord_list(self, s: str) -> list[Coord]:
        coords = []
        for pair in s.split(","):
            parts = pair.strip().split()
            if len(parts) >= 2:
                coords.append(Coord(float(parts[0]), float(parts[1])))
        return coords

    def _get_or_create_node(self, coord: Coord, map_index: int) -> MapNode:
        key = (coord.x, coord.y)
        if key in self._nodes_by_coord:
            return self._nodes_by_coord[key]
        node = MapNode(location=coord, map_index=map_index)
        self._nodes_by_coord[key] = node
        self.nodes.append(node)
        return node

    def get_nodes_for_map(self, map_index: int) -> list[MapNode]:
        return [n for n in self.nodes if n.map_index == map_index]

    def get_closest_node(self, coord: Coord, ok_maps: list[int] | None = None) -> Optional[MapNode]:
        best: Optional[MapNode] = None
        best_dist = float("inf")
        for node in self.nodes:
            if ok_maps and node.map_index not in ok_maps:
                continue
            d = coord.distance(node.location)
            if d < best_dist:
                best_dist = d
                best = node
        return best
