"""Inter-EDR replication policies and transfer stubs.

When the management plane decides that messages should be replicated between
Edge Data Repositories, a :class:`ReplicationPolicy` determines *which*
messages qualify and :class:`InterEDRTransfer` initiates the actual data
movement.
"""

from __future__ import annotations

import logging
from abc import ABC, abstractmethod
from typing import TYPE_CHECKING, Any

if TYPE_CHECKING:
    from ..core.message import Message

log = logging.getLogger(__name__)


# ---------------------------------------------------------------------------
# Replication policy interface
# ---------------------------------------------------------------------------

class ReplicationPolicy(ABC):
    """Decide whether a specific message should be replicated to a target EDR."""

    @abstractmethod
    def should_replicate(
        self,
        msg: Message,
        source: Any,
        target: Any,
    ) -> bool:
        """Return ``True`` if *msg* should be copied from *source* to *target*.

        Parameters
        ----------
        msg:
            The candidate message.
        source:
            The EDR host (or its :class:`~storage.repo_storage.RepoStorage`)
            currently holding the message.
        target:
            The EDR host (or its storage) being considered as a replica
            destination.
        """
        ...


# ---------------------------------------------------------------------------
# Concrete policy: storage-aware replication
# ---------------------------------------------------------------------------

class StorageAwareReplication(ReplicationPolicy):
    """Replicate when the source is near-full and the target has free space.

    Parameters
    ----------
    source_threshold:
        Minimum storage utilisation (0.0 -- 1.0) on the source before
        replication is considered.
    target_free_fraction:
        Minimum fraction of free space (0.0 -- 1.0) the target must have.
    """

    def __init__(
        self,
        source_threshold: float = 0.85,
        target_free_fraction: float = 0.30,
    ) -> None:
        self.source_threshold: float = source_threshold
        self.target_free_fraction: float = target_free_fraction

    def should_replicate(
        self,
        msg: Message,
        source: Any,
        target: Any,
    ) -> bool:
        """Return ``True`` when source is full and target has room."""
        source_storage: Any = getattr(source, "storage", source)
        target_storage: Any = getattr(target, "storage", target)

        # Source utilisation check.
        src_cap: int = getattr(source_storage, "total_capacity", 0)
        if src_cap <= 0:
            return False
        src_used: int = (
            getattr(source_storage, "static_size", 0)
            + getattr(source_storage, "process_size", 0)
        )
        if src_used / src_cap < self.source_threshold:
            return False

        # Target free space check.
        tgt_cap: int = getattr(target_storage, "total_capacity", 0)
        if tgt_cap <= 0:
            return False
        tgt_free: int = getattr(target_storage, "get_free_space", lambda: 0)()
        if tgt_free / tgt_cap < self.target_free_fraction:
            return False

        # Ensure the message actually fits.
        if msg.size > tgt_free:
            return False

        return True


# ---------------------------------------------------------------------------
# Inter-EDR transfer stub
# ---------------------------------------------------------------------------

class InterEDRTransfer:
    """Stub that would perform the actual inter-EDR data movement.

    In a full implementation this would coordinate with the routing layer and
    connection manager to schedule transfers over real (or simulated) links.
    """

    def initiate_transfer(
        self,
        source: Any,
        target: Any,
        messages: list[Message],
    ) -> None:
        """Initiate replication of *messages* from *source* to *target*.

        .. note::

            Stub implementation -- logs the intent but does not move data.
        """
        source_name: str = getattr(source, "name", str(source))
        target_name: str = getattr(target, "name", str(target))
        total_bytes: int = sum(m.size for m in messages)
        log.info(
            "InterEDRTransfer: %d messages (%d bytes) %s -> %s [stub]",
            len(messages),
            total_bytes,
            source_name,
            target_name,
        )
