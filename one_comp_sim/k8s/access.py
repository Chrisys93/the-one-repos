"""Access management abstractions for Kubernetes-style RBAC.

:class:`AccessPolicy` defines namespace- and label-based access levels, and
:class:`AccessManager` enforces them at simulation time and can produce a
Kubernetes RBAC manifest.
"""

from __future__ import annotations

from dataclasses import dataclass, field
from typing import Any


@dataclass
class AccessPolicy:
    """A single access control policy.

    Attributes
    ----------
    name:
        Human-readable policy identifier.
    level:
        Access level -- one of ``"secret"``, ``"machine"``, or ``"service"``.
    allowed_namespaces:
        Namespaces from which access is permitted.
    allowed_labels:
        Label key-value pairs that a host or message must carry.
    """

    name: str
    level: str  # "secret" | "machine" | "service"
    allowed_namespaces: list[str] = field(default_factory=list)
    allowed_labels: dict[str, str] = field(default_factory=dict)

    def __post_init__(self) -> None:
        allowed_levels: set[str] = {"secret", "machine", "service"}
        if self.level not in allowed_levels:
            raise ValueError(
                f"level must be one of {allowed_levels!r}, got {self.level!r}"
            )


class AccessManager:
    """Evaluate access policies against simulation hosts and messages."""

    def check_access(
        self,
        policy: AccessPolicy,
        host: Any,
        msg: Any,
    ) -> bool:
        """Return ``True`` if *host* is allowed to access *msg* under *policy*.

        Checks are performed in order:

        1. Namespace check -- the host's ``namespace`` attribute must be in
           ``policy.allowed_namespaces`` (skipped if the list is empty).
        2. Label check -- every key-value pair in ``policy.allowed_labels``
           must appear in the host's ``labels`` dict **or** the message's
           ``labels`` set (skipped if the dict is empty).

        Parameters
        ----------
        policy:
            The :class:`AccessPolicy` to evaluate.
        host:
            The DTNHost attempting to access the message.
        msg:
            The :class:`~core.message.Message` being accessed.
        """
        # 1. Namespace check.
        if policy.allowed_namespaces:
            host_ns: str = getattr(host, "namespace", "default")
            if host_ns not in policy.allowed_namespaces:
                return False

        # 2. Label check.
        if policy.allowed_labels:
            host_labels: dict[str, str] = getattr(host, "labels", {})
            msg_labels: set[str] = getattr(msg, "labels", set())
            for key, value in policy.allowed_labels.items():
                host_match: bool = host_labels.get(key) == value
                # For message labels we check if "key=value" is in the set.
                msg_match: bool = f"{key}={value}" in msg_labels or key in msg_labels
                if not (host_match or msg_match):
                    return False

        return True

    def create_rbac_manifest(
        self,
        policies: list[AccessPolicy],
    ) -> dict:
        """Produce a Kubernetes RBAC manifest dict from *policies*.

        Generates a ``ClusterRole`` and ``ClusterRoleBinding`` for each
        policy.  The resulting dict is suitable for serialisation to YAML.
        """
        items: list[dict] = []

        for policy in policies:
            # ClusterRole
            role_name: str = f"sim-{policy.name}-role"
            items.append(
                {
                    "apiVersion": "rbac.authorization.k8s.io/v1",
                    "kind": "ClusterRole",
                    "metadata": {
                        "name": role_name,
                        "labels": {
                            "sim/access-level": policy.level,
                        },
                    },
                    "rules": [
                        {
                            "apiGroups": [""],
                            "resources": ["pods", "services"],
                            "verbs": _verbs_for_level(policy.level),
                        }
                    ],
                }
            )

            # ClusterRoleBinding
            binding_name: str = f"sim-{policy.name}-binding"
            subjects: list[dict] = []
            for ns in policy.allowed_namespaces:
                subjects.append(
                    {
                        "kind": "ServiceAccount",
                        "name": "default",
                        "namespace": ns,
                    }
                )
            items.append(
                {
                    "apiVersion": "rbac.authorization.k8s.io/v1",
                    "kind": "ClusterRoleBinding",
                    "metadata": {"name": binding_name},
                    "roleRef": {
                        "apiGroup": "rbac.authorization.k8s.io",
                        "kind": "ClusterRole",
                        "name": role_name,
                    },
                    "subjects": subjects,
                }
            )

        return {"apiVersion": "v1", "kind": "List", "items": items}


# ---------------------------------------------------------------------------
# Helpers
# ---------------------------------------------------------------------------

def _verbs_for_level(level: str) -> list[str]:
    """Return the K8s API verbs appropriate for an access level."""
    if level == "secret":
        return ["get"]
    if level == "machine":
        return ["get", "list", "watch"]
    # "service"
    return ["get", "list", "watch", "create", "update", "delete"]
