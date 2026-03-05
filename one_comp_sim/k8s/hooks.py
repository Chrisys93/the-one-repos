"""gRPC / API hook interfaces for Kubernetes integration.

:class:`K8sHookInterface` defines an async interface for managing K8s resources
from within the simulator.  Two implementations are provided:

* :class:`LoggingK8sHook` -- records all calls in-memory (for testing /
  offline analysis).
* :class:`GrpcK8sHook` -- stub that raises ``NotImplementedError`` to
  indicate where real gRPC calls would be placed.
"""

from __future__ import annotations

from abc import ABC, abstractmethod
from dataclasses import dataclass, field
from typing import Any

from .models import K8sNamespace, K8sPod
from .routing_rules import RoutingRule


# ---------------------------------------------------------------------------
# Abstract interface
# ---------------------------------------------------------------------------

class K8sHookInterface(ABC):
    """Async interface for Kubernetes resource management from the simulator."""

    @abstractmethod
    async def create_namespace(self, ns: K8sNamespace) -> None:
        """Create a namespace in the target cluster."""
        ...

    @abstractmethod
    async def apply_routing_rule(self, rule: RoutingRule) -> None:
        """Apply a routing / network-policy rule to the cluster."""
        ...

    @abstractmethod
    async def update_pod_resources(self, pod: K8sPod) -> None:
        """Update the resource allocation for an existing pod."""
        ...

    @abstractmethod
    async def delete_pod(self, pod_name: str) -> None:
        """Delete a pod by name."""
        ...

    @abstractmethod
    async def get_cluster_status(self) -> dict:
        """Return a dict summarising the current cluster state."""
        ...


# ---------------------------------------------------------------------------
# Logging implementation (in-memory, no external calls)
# ---------------------------------------------------------------------------

@dataclass
class LoggingK8sHook(K8sHookInterface):
    """Default hook that records all operations in an in-memory log.

    Useful for testing and offline analysis -- no network calls are made.

    Attributes
    ----------
    log:
        Ordered list of ``(operation, payload)`` tuples recorded by the hook.
    """

    log: list[tuple[str, Any]] = field(default_factory=list)

    async def create_namespace(self, ns: K8sNamespace) -> None:
        self.log.append(("create_namespace", ns))

    async def apply_routing_rule(self, rule: RoutingRule) -> None:
        self.log.append(("apply_routing_rule", rule))

    async def update_pod_resources(self, pod: K8sPod) -> None:
        self.log.append(("update_pod_resources", pod))

    async def delete_pod(self, pod_name: str) -> None:
        self.log.append(("delete_pod", pod_name))

    async def get_cluster_status(self) -> dict:
        self.log.append(("get_cluster_status", None))
        return {
            "status": "simulated",
            "operations_recorded": len(self.log),
        }


# ---------------------------------------------------------------------------
# gRPC stub implementation
# ---------------------------------------------------------------------------

class GrpcK8sHook(K8sHookInterface):
    """Stub for real gRPC-based Kubernetes integration.

    Every method raises :class:`NotImplementedError` with a message indicating
    which gRPC call should be wired in.  To implement a real hook:

    1. Initialise a gRPC channel in ``__init__``.
    2. Replace each method body with the appropriate stub call.
    """

    async def create_namespace(self, ns: K8sNamespace) -> None:
        raise NotImplementedError(
            f"GrpcK8sHook.create_namespace: wire gRPC CoreV1 "
            f"CreateNamespace for {ns.name!r}"
        )

    async def apply_routing_rule(self, rule: RoutingRule) -> None:
        raise NotImplementedError(
            f"GrpcK8sHook.apply_routing_rule: wire gRPC NetworkingV1 "
            f"CreateNetworkPolicy for {rule.source_namespace!r} -> "
            f"{rule.target_namespace!r}"
        )

    async def update_pod_resources(self, pod: K8sPod) -> None:
        raise NotImplementedError(
            f"GrpcK8sHook.update_pod_resources: wire gRPC CoreV1 "
            f"PatchPod for {pod.name!r}"
        )

    async def delete_pod(self, pod_name: str) -> None:
        raise NotImplementedError(
            f"GrpcK8sHook.delete_pod: wire gRPC CoreV1 "
            f"DeletePod for {pod_name!r}"
        )

    async def get_cluster_status(self) -> dict:
        raise NotImplementedError(
            "GrpcK8sHook.get_cluster_status: wire gRPC CoreV1 "
            "ListNodes / ListPods to gather cluster status"
        )
