"""Abstract base class for all simulator applications.

Mirrors the Java ``core.Application`` class: every concrete application must
implement :meth:`handle`, :meth:`update`, and :meth:`replicate`.
"""

from __future__ import annotations

from abc import ABC, abstractmethod
from typing import Any, Optional


class Application(ABC):
    """Base class for node-hosted applications.

    Applications receive every incoming message via :meth:`handle` and are
    called once per simulation tick via :meth:`update`.
    """

    app_id: str = ""

    @abstractmethod
    def handle(self, msg: Any, host: Any) -> Optional[Any]:
        """Process an incoming message.

        Parameters
        ----------
        msg:
            The :class:`~core.message.Message` received by the host's router.
        host:
            The :class:`DTNHost` this application instance is attached to.

        Returns
        -------
        Optional[Any]
            The (possibly modified) message to continue forwarding, or
            ``None`` to signal the router to drop it.
        """
        ...

    @abstractmethod
    def update(self, host: Any) -> None:
        """Called every simulation cycle.

        Parameters
        ----------
        host:
            The :class:`DTNHost` this application instance is attached to.
        """
        ...

    @abstractmethod
    def replicate(self) -> Application:
        """Return a deep copy of this application instance.

        Used when the application is cloned onto each node in a group.
        """
        ...

    def set_app_id(self, app_id: str) -> None:
        """Set the application ID.

        Should only be called once when the application is created.
        """
        self.app_id = app_id

    def get_app_id(self) -> str:
        """Return the application ID."""
        return self.app_id
