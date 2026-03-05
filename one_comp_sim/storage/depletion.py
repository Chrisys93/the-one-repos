"""Per-category bandwidth tracking for the six depletion paths.

Each depletion path in the Java original (processed->cloud, static->cloud,
processed->forced, static->forced, unprocessed, p-unprocessed) maintained a
cumulative-size counter and a "last reported" snapshot so that the BW *since
the last report* could be computed.  ``DepletionTracker`` wraps all six into
one unified object.
"""

from __future__ import annotations

from dataclasses import dataclass


# ---------------------------------------------------------------------------
# Internal per-category state
# ---------------------------------------------------------------------------

@dataclass
class _DepletionCategory:
    """Counters for a single depletion path."""

    count: int = 0
    cumulative_size: int = 0
    _last_reported_size: int = 0


# ---------------------------------------------------------------------------
# The six canonical category names
# ---------------------------------------------------------------------------

_CATEGORY_NAMES: frozenset[str] = frozenset(
    {
        "proc_cloud",       # processed msgs depleted to cloud
        "static_cloud",     # static (nonproc) msgs depleted to cloud
        "proc_forced",      # processed msgs force-depleted locally
        "static_forced",    # static (nonproc) msgs force-depleted locally
        "unproc",           # unprocessed msgs (could-not-process path)
        "punproc",          # "p-unprocessed" msgs (already-depleted unproc)
    }
)


# ---------------------------------------------------------------------------
# Public tracker
# ---------------------------------------------------------------------------

class DepletionTracker:
    """Unified bandwidth tracker for the six depletion categories.

    Mirrors the Java fields ``depletedProcMessagesSize``,
    ``depletedCloudProcMessagesSize``, ``depletedStaticMessagesSize``,
    ``depletedCloudStaticMessagesSize``, ``depletedUnProcMessagesSize``,
    and ``depletedPUnProcMessagesSize`` together with their
    ``old*`` snapshot counterparts used for BW computation.
    """

    def __init__(self) -> None:
        self._categories: dict[str, _DepletionCategory] = {
            name: _DepletionCategory() for name in _CATEGORY_NAMES
        }

    # -- internal helpers -----------------------------------------------------

    def _get(self, category_name: str) -> _DepletionCategory:
        cat = self._categories.get(category_name)
        if cat is None:
            raise ValueError(
                f"Unknown depletion category {category_name!r}. "
                f"Valid names: {sorted(_CATEGORY_NAMES)}"
            )
        return cat

    # -- public API -----------------------------------------------------------

    def record(self, category_name: str, msg_size: int) -> None:
        """Record one depleted message of *msg_size* bytes in *category_name*."""
        cat = self._get(category_name)
        cat.count += 1
        cat.cumulative_size += msg_size

    def get_bw(self, category_name: str, reporting: bool = False) -> int:
        """Return bytes depleted since the last reporting snapshot.

        If *reporting* is ``True``, the internal snapshot is advanced so that
        the next call sees only the delta accumulated after this point.  This
        matches the ``old*`` / ``current`` pattern in the Java getters.
        """
        cat = self._get(category_name)
        bw = cat.cumulative_size - cat._last_reported_size
        if reporting:
            cat._last_reported_size = cat.cumulative_size
        return bw

    def get_count(self, category_name: str) -> int:
        """Return the total number of messages depleted through *category_name*."""
        return self._get(category_name).count

    def get_cumulative_size(self, category_name: str) -> int:
        """Return total bytes ever depleted through *category_name*."""
        return self._get(category_name).cumulative_size

    @property
    def category_names(self) -> frozenset[str]:
        """The set of valid category names."""
        return _CATEGORY_NAMES
