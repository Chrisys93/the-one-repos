"""ReposFirstContactRouter: first-contact routing variant for repo delivery.

Port of Java ReposFirstContactRouter.java.
Similar to direct delivery but uses first-available-contact selection among
repo nodes, with storage-aware transfer handling.
"""

from __future__ import annotations

from typing import TYPE_CHECKING, Any, Optional

from .base import ActiveRouter

if TYPE_CHECKING:
    from ..core.connection import Connection
    from ..core.message import Message


class ReposFirstContactRouter(ActiveRouter):
    """First-contact delivery to EDR repositories."""

    def __init__(
        self,
        buffer_size: int = 100_000_000,
        msg_ttl: int = 300,
        allow_inter_edr: bool = False,
    ) -> None:
        super().__init__(buffer_size, msg_ttl)
        self.allow_inter_edr = allow_inter_edr
        self._total_buffer_deleted: int = 0

    def replicate(self) -> ReposFirstContactRouter:
        return ReposFirstContactRouter(
            buffer_size=self.buffer_size,
            msg_ttl=self.msg_ttl,
            allow_inter_edr=self.allow_inter_edr,
        )

    def check_receiving(self, msg: Message) -> int:
        """Reject messages that have already traversed this node."""
        if self.host in msg.path:
            return -1  # DENIED_OLD
        return 0  # RCV_OK

    def update(self) -> None:
        super().update()
        if self.is_transferring() or not self.can_start_transfer():
            return
        self._try_first_contact_to_repos()

    def _try_first_contact_to_repos(self) -> None:
        """Try to deliver messages to the first available repo connection."""
        is_repo = "r" in (self.host.name if self.host else "")

        if is_repo and not self.allow_inter_edr:
            return

        messages = self.sort_by_queue_mode(self.get_message_collection())

        for con in self.get_connections():
            other = con.get_other_node(self.host)
            other_name = other.name if other else ""

            if is_repo:
                if not ("r" in other_name and other_name != self.host.name):
                    continue
            else:
                if "r" not in other_name:
                    continue

            if not con.is_ready_for_transfer():
                continue

            # Try all messages to this first available repo connection
            for msg in messages:
                retval = self.start_transfer(msg, con)
                if retval == 0:
                    return  # First contact made

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
        """Storage-aware transfer completion.
        Invoke app.handle() on the receiving EDR, then delete sender's copy."""
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
