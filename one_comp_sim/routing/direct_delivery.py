"""RepoDirectDeliveryRouter: delivers messages only to EDR ('r') nodes.

Port of Java RepoDirectDeliveryRouter.java.
Non-repo nodes send only to connected repo nodes.
Repos never forward to mobile/edge devices, but can forward to other repos
when allow_inter_edr is True (for SEND management plane mode).
"""

from __future__ import annotations

from typing import TYPE_CHECKING, Any, Optional

from .base import ActiveRouter

if TYPE_CHECKING:
    from ..core.connection import Connection
    from ..core.message import Message


class RepoDirectDeliveryRouter(ActiveRouter):
    """Direct delivery to EDR repositories only."""

    def __init__(
        self,
        buffer_size: int = 100_000_000,
        msg_ttl: int = 300,
        allow_inter_edr: bool = False,
    ) -> None:
        super().__init__(buffer_size, msg_ttl)
        self.allow_inter_edr = allow_inter_edr
        self._total_buffer_deleted: int = 0

    def replicate(self) -> RepoDirectDeliveryRouter:
        r = RepoDirectDeliveryRouter(
            buffer_size=self.buffer_size,
            msg_ttl=self.msg_ttl,
            allow_inter_edr=self.allow_inter_edr,
        )
        return r

    def update(self) -> None:
        super().update()
        if self.is_transferring() or not self.can_start_transfer():
            return
        self._try_all_messages_to_repos()

    def _try_all_messages_to_repos(self) -> Optional[Connection]:
        """Non-repo nodes send to repo nodes; repos only forward to other repos
        when inter-EDR mode is enabled."""
        is_repo = "r" in (self.host.name if self.host else "")

        if is_repo and not self.allow_inter_edr:
            return None  # Repos don't forward in default mode

        connections: list[Connection] = []
        for con in self.get_connections():
            other = con.get_other_node(self.host)
            other_name = other.name if other else ""

            if is_repo:
                # Inter-EDR: only forward to other repos
                if "r" in other_name and other_name != self.host.name:
                    connections.append(con)
            else:
                # Mobile nodes: only deliver to repos
                if "r" in other_name:
                    connections.append(con)

        if not connections or self.get_nrof_messages() == 0:
            return None

        messages = self.sort_by_queue_mode(self.get_message_collection())
        return self.try_messages_to_connections(messages, connections)

    def make_room_for_message(self, size: int) -> bool:
        if size > self.buffer_size:
            return False
        free = self.get_free_buffer_size()
        while free < size:
            m = self.get_oldest_message(exclude_sending=True)
            if m is None:
                return False
            self.delete_message(m.id, drop=True)
            free += m.size
            self._total_buffer_deleted += 1
        return True

    def transfer_done(self, con: Connection) -> None:
        """On transfer complete: trigger app.handle on receiving EDR,
        then delete sender's copy."""
        other = con.get_other_node(self.host)
        msg = con.msg_on_fly

        if other is not None and msg is not None:
            # Dispatch to applications on the RECEIVING node
            handled = False
            if hasattr(other, 'router'):
                apps = other.router.get_applications(msg.app_id or "")
                for app in apps:
                    app.handle(msg, other)
                    handled = True

            # Fallback: no matching app, store directly
            if not handled and hasattr(other, "has_storage") and other.has_storage:
                if other.storage is not None:
                    other.storage.add_message(msg)

        # Remove sender's copy
        if msg is not None:
            self.delete_message(msg.id, drop=False)
        con.msg_on_fly = None

    def get_buffer_deleted_messages_size(self) -> int:
        return self._total_buffer_deleted
