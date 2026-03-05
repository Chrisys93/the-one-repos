"""Application registry -- maps string names to application classes.

The registry is the single point of indirection used by the configuration
loader and scenario builder to instantiate concrete :class:`Application`
subclasses by name.

Pre-registered applications
---------------------------
* ``ProcApplication``    -- :class:`~applications.proc_application.ProcApplication`
* ``ProcGenApplication`` -- :class:`~applications.proc_gen_application.ProcGenApplication`
"""

from __future__ import annotations

from typing import Type

from .base import Application
from .proc_application import ProcApplication
from .proc_gen_application import ProcGenApplication


class ApplicationRegistry:
    """Maps human-readable names to :class:`Application` subclasses."""

    def __init__(self) -> None:
        self._registry: dict[str, Type[Application]] = {}

    def register(self, name: str, cls: Type[Application]) -> None:
        """Register an application class under *name*.

        Parameters
        ----------
        name:
            The configuration-level name (e.g. ``"ProcApplication"``).
        cls:
            The concrete :class:`Application` subclass.
        """
        self._registry[name] = cls

    def get(self, name: str) -> Type[Application]:
        """Return the class registered under *name*.

        Raises
        ------
        KeyError
            If *name* has not been registered.
        """
        try:
            return self._registry[name]
        except KeyError:
            available = ", ".join(sorted(self._registry)) or "(none)"
            raise KeyError(
                f"No application registered under {name!r}. "
                f"Available: {available}"
            ) from None

    def list_available(self) -> list[str]:
        """Return a sorted list of registered application names."""
        return sorted(self._registry)


# -----------------------------------------------------------------------
# Module-level singleton with pre-registered applications
# -----------------------------------------------------------------------

registry = ApplicationRegistry()
registry.register("ProcApplication", ProcApplication)
registry.register("ProcGenApplication", ProcGenApplication)
