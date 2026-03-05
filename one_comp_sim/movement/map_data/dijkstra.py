"""Dijkstra shortest-path finder for the map graph."""

from __future__ import annotations

import heapq
from typing import Optional

from .sim_map import MapNode


def dijkstra(start: MapNode, end: MapNode) -> Optional[list[MapNode]]:
    """Find shortest path from *start* to *end* using Dijkstra's algorithm.

    Returns the path as a list of MapNodes (including start and end),
    or None if no path exists.
    """
    dist: dict[MapNode, float] = {start: 0.0}
    prev: dict[MapNode, Optional[MapNode]] = {start: None}
    heap: list[tuple[float, int, MapNode]] = [(0.0, id(start), start)]
    visited: set[MapNode] = set()

    while heap:
        d, _, current = heapq.heappop(heap)
        if current in visited:
            continue
        visited.add(current)

        if current == end:
            # Reconstruct path
            path = []
            node: Optional[MapNode] = end
            while node is not None:
                path.append(node)
                node = prev.get(node)
            path.reverse()
            return path

        for neighbor in current.neighbors:
            if neighbor in visited:
                continue
            edge_dist = current.location.distance(neighbor.location)
            new_dist = d + edge_dist
            if new_dist < dist.get(neighbor, float("inf")):
                dist[neighbor] = new_dist
                prev[neighbor] = current
                heapq.heappush(heap, (new_dist, id(neighbor), neighbor))

    return None  # No path found
