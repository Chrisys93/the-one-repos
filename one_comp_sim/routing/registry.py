"""Router registry: maps configuration names to router classes."""

from __future__ import annotations

from typing import Type

from .base import MessageRouter


_REGISTRY: dict[str, Type[MessageRouter]] = {}


def register(name: str, cls: Type[MessageRouter]) -> None:
    _REGISTRY[name] = cls


def get(name: str) -> Type[MessageRouter]:
    if name not in _REGISTRY:
        raise KeyError(f"Unknown router: {name!r}. Available: {list_available()}")
    return _REGISTRY[name]


def list_available() -> list[str]:
    return list(_REGISTRY.keys())


def _register_defaults() -> None:
    from .direct_delivery import RepoDirectDeliveryRouter
    from .first_contact import ReposFirstContactRouter

    register("RepoDirectDeliveryRouter", RepoDirectDeliveryRouter)
    register("ReposFirstContactRouter", ReposFirstContactRouter)


_register_defaults()
