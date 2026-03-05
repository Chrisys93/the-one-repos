"""Kubernetes resource abstractions as plain dataclasses.

These models mirror the core K8s object structure relevant to the SEND edge
simulator without depending on any external Kubernetes client library.
"""

from __future__ import annotations

from dataclasses import dataclass, field


@dataclass
class K8sResources:
    """Resource requests/limits for a single container or pod.

    Attributes
    ----------
    cpu_millicores:
        CPU allocation in millicores (1000 m = 1 vCPU).
    memory_bytes:
        Memory allocation in bytes.
    storage_bytes:
        Persistent storage allocation in bytes.
    """

    cpu_millicores: int
    memory_bytes: int
    storage_bytes: int


@dataclass
class K8sNamespace:
    """A Kubernetes namespace.

    Attributes
    ----------
    name:
        Namespace identifier (e.g. ``"edr-group-1"``).
    labels:
        Arbitrary key-value labels attached to the namespace.
    """

    name: str
    labels: dict[str, str] = field(default_factory=dict)


@dataclass
class K8sPod:
    """A Kubernetes pod (one or more containers sharing a network namespace).

    Attributes
    ----------
    name:
        Pod name.
    namespace:
        Namespace the pod belongs to.
    node_name:
        Cluster node on which the pod is scheduled.
    labels:
        Pod labels used for service selection and policy matching.
    resources:
        Aggregate resource allocation for all containers in the pod.
    status:
        Pod phase (default ``"Running"``).
    """

    name: str
    namespace: str
    node_name: str
    labels: dict[str, str] = field(default_factory=dict)
    resources: K8sResources = field(
        default_factory=lambda: K8sResources(
            cpu_millicores=0, memory_bytes=0, storage_bytes=0
        )
    )
    status: str = "Running"


@dataclass
class K8sService:
    """A Kubernetes Service that routes traffic to pods matching *selector*.

    Attributes
    ----------
    name:
        Service name.
    namespace:
        Namespace the service belongs to.
    selector:
        Label selector used to match target pods.
    port:
        Primary port exposed by the service.
    service_type:
        Kubernetes service type (default ``"ClusterIP"``).
    """

    name: str
    namespace: str
    selector: dict[str, str] = field(default_factory=dict)
    port: int = 80
    service_type: str = "ClusterIP"


@dataclass
class K8sCluster:
    """Aggregate representation of a Kubernetes cluster.

    Attributes
    ----------
    name:
        Cluster identifier.
    namespaces:
        All namespaces within the cluster.
    pods:
        All pods across namespaces.
    services:
        All services across namespaces.
    """

    name: str
    namespaces: list[K8sNamespace] = field(default_factory=list)
    pods: list[K8sPod] = field(default_factory=list)
    services: list[K8sService] = field(default_factory=list)
