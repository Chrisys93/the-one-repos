"""Map simulation entities to Kubernetes objects.

:class:`K8sMapper` translates the in-memory simulation world (EDR hosts,
groups, topology) into :mod:`k8s.models` dataclasses and can produce a
Kubernetes YAML deployment manifest.
"""

from __future__ import annotations

import textwrap
from typing import Any

from .models import (
    K8sCluster,
    K8sNamespace,
    K8sPod,
    K8sResources,
    K8sService,
)


class K8sMapper:
    """Translate simulation hosts and groups into Kubernetes objects."""

    # ------------------------------------------------------------------
    # Per-host mapping
    # ------------------------------------------------------------------

    def map_edr_to_pod(self, host: Any) -> K8sPod:
        """Create a :class:`K8sPod` that represents *host*.

        The host is expected to expose:

        * ``name: str``
        * ``address: int``  (used as part of the pod name)
        * ``storage`` with ``total_capacity`` (bytes)

        Reasonable defaults are applied when attributes are missing.
        """
        host_name: str = getattr(host, "name", "unknown")
        address: int = getattr(host, "address", 0)
        storage: Any = getattr(host, "storage", None)
        storage_bytes: int = (
            getattr(storage, "total_capacity", 0) if storage else 0
        )

        return K8sPod(
            name=f"edr-{host_name}-{address}",
            namespace="default",
            node_name=host_name,
            labels={
                "app": "edr",
                "edr-host": host_name,
                "sim-address": str(address),
            },
            resources=K8sResources(
                cpu_millicores=500,
                memory_bytes=512 * 1024 * 1024,  # 512 MiB default
                storage_bytes=storage_bytes,
            ),
        )

    # ------------------------------------------------------------------
    # Group mapping
    # ------------------------------------------------------------------

    def create_namespace_for_group(
        self,
        group_id: str,
        hosts: list[Any],
    ) -> K8sNamespace:
        """Create a :class:`K8sNamespace` for a simulation host group.

        Parameters
        ----------
        group_id:
            Identifier for the group (e.g. ``"edr-group-1"``).
        hosts:
            List of hosts belonging to the group.
        """
        return K8sNamespace(
            name=f"sim-{group_id}",
            labels={
                "sim-group": group_id,
                "host-count": str(len(hosts)),
            },
        )

    # ------------------------------------------------------------------
    # Full topology mapping
    # ------------------------------------------------------------------

    def generate_topology(self, world: Any) -> K8sCluster:
        """Produce a :class:`K8sCluster` representing the full simulation.

        *world* is expected to expose:

        * ``hosts: list``  -- all DTNHost instances
        * ``scenario_name: str`` (optional, used as cluster name)

        Each host becomes a pod in the ``default`` namespace, and a
        corresponding :class:`K8sService` is created for it.
        """
        cluster_name: str = getattr(world, "scenario_name", "sim-cluster")
        hosts: list[Any] = getattr(world, "hosts", [])

        namespaces: list[K8sNamespace] = [
            K8sNamespace(name="default", labels={"role": "simulation"})
        ]

        pods: list[K8sPod] = []
        services: list[K8sService] = []

        for host in hosts:
            pod: K8sPod = self.map_edr_to_pod(host)
            pods.append(pod)
            services.append(
                K8sService(
                    name=f"svc-{pod.name}",
                    namespace=pod.namespace,
                    selector={"edr-host": pod.node_name},
                    port=8080,
                    service_type="ClusterIP",
                )
            )

        return K8sCluster(
            name=cluster_name,
            namespaces=namespaces,
            pods=pods,
            services=services,
        )

    # ------------------------------------------------------------------
    # YAML generation
    # ------------------------------------------------------------------

    def generate_deployment_yaml(self, cluster: K8sCluster) -> str:
        """Produce a multi-document Kubernetes YAML manifest for *cluster*.

        The manifest contains ``Namespace``, ``Pod``, and ``Service``
        resources separated by ``---`` document markers.
        """
        documents: list[str] = []

        # Namespaces
        for ns in cluster.namespaces:
            labels_yaml: str = _dict_to_yaml_inline(ns.labels)
            documents.append(textwrap.dedent(f"""\
                apiVersion: v1
                kind: Namespace
                metadata:
                  name: {ns.name}
                  labels: {labels_yaml}"""))

        # Pods
        for pod in cluster.pods:
            labels_yaml = _dict_to_yaml_inline(pod.labels)
            documents.append(textwrap.dedent(f"""\
                apiVersion: v1
                kind: Pod
                metadata:
                  name: {pod.name}
                  namespace: {pod.namespace}
                  labels: {labels_yaml}
                spec:
                  nodeName: {pod.node_name}
                  containers:
                    - name: edr
                      image: edr-simulator:latest
                      resources:
                        requests:
                          cpu: "{pod.resources.cpu_millicores}m"
                          memory: "{pod.resources.memory_bytes}"
                        limits:
                          cpu: "{pod.resources.cpu_millicores}m"
                          memory: "{pod.resources.memory_bytes}"
                      volumeMounts:
                        - name: edr-storage
                          mountPath: /data
                  volumes:
                    - name: edr-storage
                      emptyDir:
                        sizeLimit: "{pod.resources.storage_bytes}" """))

        # Services
        for svc in cluster.services:
            selector_yaml: str = _dict_to_yaml_inline(svc.selector)
            documents.append(textwrap.dedent(f"""\
                apiVersion: v1
                kind: Service
                metadata:
                  name: {svc.name}
                  namespace: {svc.namespace}
                spec:
                  type: {svc.service_type}
                  selector: {selector_yaml}
                  ports:
                    - port: {svc.port}
                      targetPort: {svc.port}"""))

        return "\n---\n".join(documents) + "\n"


# ---------------------------------------------------------------------------
# Helpers
# ---------------------------------------------------------------------------

def _dict_to_yaml_inline(d: dict[str, str]) -> str:
    """Convert a flat string dict to an inline YAML mapping, e.g. ``{a: b, c: d}``."""
    if not d:
        return "{}"
    pairs: list[str] = [f"{k}: {v}" for k, v in d.items()]
    return "{" + ", ".join(pairs) + "}"
