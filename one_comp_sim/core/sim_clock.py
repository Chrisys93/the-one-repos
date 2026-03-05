"""Simulation clock -- tracks virtual time for the discrete-event loop.

Design: context-aware so multiple concurrent simulations (e.g. via the AI tool
interface) each have independent clocks.  A module-level convenience API
delegates to the *current* context's clock.
"""

from __future__ import annotations

import contextvars
from dataclasses import dataclass, field

_current_clock: contextvars.ContextVar["SimClock"] = contextvars.ContextVar(
    "_current_clock"
)


@dataclass
class SimClock:
    """Tracks simulation time in seconds."""

    _time: float = 0.0

    # ---- context management ------------------------------------------------

    def install(self) -> None:
        """Make this clock the current context's clock."""
        _current_clock.set(self)

    @classmethod
    def get_instance(cls) -> SimClock:
        """Return the clock for the current context, creating one if needed."""
        try:
            return _current_clock.get()
        except LookupError:
            clock = cls()
            clock.install()
            return clock

    # ---- time queries ------------------------------------------------------

    @classmethod
    def get_time(cls) -> float:
        return cls.get_instance()._time

    @classmethod
    def get_int_time(cls) -> int:
        return int(cls.get_instance()._time)

    # ---- time mutations ----------------------------------------------------

    def advance(self, dt: float) -> None:
        self._time += dt

    def set_time(self, t: float) -> None:
        self._time = t

    @classmethod
    def reset(cls) -> None:
        """Reset the current context's clock to 0."""
        cls.get_instance()._time = 0.0
