"""Spatial partitioning grid for efficient neighbour discovery.

Port of Java ``ConnectivityGrid.java``.  Instead of checking every
interface pair for proximity (O(n^2)), interfaces are placed in grid cells
and only interfaces in the same or adjacent cells are considered.

Design decisions
~~~~~~~~~~~~~~~~
* The Java version uses a dense 2-D array pre-allocated to world size.
  The Python port uses a **sparse** ``dict[(int, int), set]`` keyed by
  ``(cell_x, cell_y)`` so it works with arbitrary coordinate ranges without
  knowing the world dimensions up front.
* A class-level factory ``ConnectivityGrid.get_or_create()`` replaces the
  Java ``ConnectivityGridFactory`` static method.  Grids are keyed by an
  integer hash (typically ``hash(interface_type)``).
* The grid is not context-aware by default -- call :func:`reset_grids` to
  clear the global registry between simulation runs.
"""

from __future__ import annotations

import math
from typing import Any, Collection

from ..core.coord import Coord

# Forward reference; concrete type will be core.network_interface.NetworkInterface
# but we use Any here to avoid circular imports at module level.
InterfaceRef = Any


# ---------------------------------------------------------------------------
# Module-level grid registry
# ---------------------------------------------------------------------------

_grid_registry: dict[int, ConnectivityGrid] = {}


def reset_grids() -> None:
    """Clear the global grid registry (call between simulation runs)."""
    _grid_registry.clear()


# ---------------------------------------------------------------------------
# ConnectivityGrid
# ---------------------------------------------------------------------------

class ConnectivityGrid:
    """Overlay grid for cell-based neighbour lookups.

    Parameters
    ----------
    cell_size:
        Edge length of each grid cell.  Must be at least as large as the
        maximum interface transmit range so that two interfaces in adjacent
        cells can still be within radio distance.
    """

    # -- Factory -------------------------------------------------------------

    @classmethod
    def get_or_create(cls, key: int, range_value: float) -> ConnectivityGrid:
        """Return (or create) the grid for the given interface-type *key*.

        Parameters
        ----------
        key:
            Hash value that separates grids for different interface types.
        range_value:
            Transmit range of the interface; the cell size is set to
            ``ceil(range_value)``.
        """
        if key in _grid_registry:
            return _grid_registry[key]

        grid = cls(cell_size=int(math.ceil(range_value)))
        _grid_registry[key] = grid
        return grid

    # -- Construction --------------------------------------------------------

    def __init__(self, cell_size: int) -> None:
        if cell_size <= 0:
            raise ValueError(f"cell_size must be positive, got {cell_size}")
        self._cell_size: int = cell_size
        self._grid: dict[tuple[int, int], set[InterfaceRef]] = {}
        # Map from interface -> its current cell key
        self._interface_cells: dict[InterfaceRef, tuple[int, int]] = {}

    @property
    def cell_size(self) -> int:
        return self._cell_size

    # -- Interface management ------------------------------------------------

    def add_interface(self, ni: InterfaceRef) -> None:
        """Register *ni* in the grid at its current location."""
        cell_key = self._cell_key_from_coord(ni.get_location())
        self._grid.setdefault(cell_key, set()).add(ni)
        self._interface_cells[ni] = cell_key

    def remove_interface(self, ni: InterfaceRef) -> None:
        """Remove *ni* from the grid."""
        cell_key = self._interface_cells.pop(ni, None)
        if cell_key is not None:
            cell = self._grid.get(cell_key)
            if cell is not None:
                cell.discard(ni)

    def update_location(self, ni: InterfaceRef) -> None:
        """Move *ni* to the correct cell if its location has changed."""
        old_key = self._interface_cells.get(ni)
        new_key = self._cell_key_from_coord(ni.get_location())

        if new_key != old_key:
            # Remove from old cell
            if old_key is not None:
                old_cell = self._grid.get(old_key)
                if old_cell is not None:
                    old_cell.discard(ni)

            # Add to new cell
            self._grid.setdefault(new_key, set()).add(ni)
            self._interface_cells[ni] = new_key

    # -- Neighbour queries ---------------------------------------------------

    def get_near_interfaces(self, ni: InterfaceRef) -> Collection[InterfaceRef]:
        """Return all interfaces in the same or adjacent cells as *ni*.

        The returned collection *includes* *ni* itself (callers are expected
        to skip ``self`` comparisons).
        """
        if ni not in self._interface_cells:
            return []

        location: Coord = ni.get_location()
        cx = int(location.x / self._cell_size)
        cy = int(location.y / self._cell_size)

        result: list[InterfaceRef] = []
        for dx in (-1, 0, 1):
            for dy in (-1, 0, 1):
                cell = self._grid.get((cx + dx, cy + dy))
                if cell is not None:
                    result.extend(cell)
        return result

    def get_all_interfaces(self) -> Collection[InterfaceRef]:
        """Return every interface registered in this grid."""
        return list(self._interface_cells.keys())

    # -- Internal ------------------------------------------------------------

    def _cell_key_from_coord(self, coord: Coord) -> tuple[int, int]:
        """Map a world coordinate to a ``(cell_x, cell_y)`` key."""
        return (
            int(coord.x / self._cell_size),
            int(coord.y / self._cell_size),
        )

    # -- Display -------------------------------------------------------------

    def __str__(self) -> str:
        n_cells = sum(1 for c in self._grid.values() if c)
        return (
            f"ConnectivityGrid(cell_size={self._cell_size}, "
            f"interfaces={len(self._interface_cells)}, "
            f"active_cells={n_cells})"
        )

    def __repr__(self) -> str:
        return self.__str__()
