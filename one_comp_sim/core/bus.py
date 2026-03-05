"""Intra-node communication bus (key-value pub/sub between modules).

Mirrors the Java ``ModuleCommunicationBus``: routers, applications, and
movement models on the same host communicate via shared key-value pairs.
"""

from __future__ import annotations

from typing import Any, Callable


class ModuleCommunicationBus:
    """Simple key-value store with optional change subscriptions."""

    def __init__(self) -> None:
        self._store: dict[str, Any] = {}
        self._subscribers: dict[str, list[Callable[[str, Any], None]]] = {}

    def get(self, key: str, default: Any = None) -> Any:
        return self._store.get(key, default)

    def put(self, key: str, value: Any) -> None:
        self._store[key] = value
        for cb in self._subscribers.get(key, []):
            cb(key, value)

    def subscribe(self, key: str, callback: Callable[[str, Any], None]) -> None:
        self._subscribers.setdefault(key, []).append(callback)

    def contains(self, key: str) -> bool:
        return key in self._store
