"""Namespace-aware routing rules and Kubernetes NetworkPolicy generation.

:class:`RoutingRule` defines which message types may flow between namespaces,
and :class:`NamespaceRouter` enforces those rules at simulation time.
"""

from __future__ import annotations

from dataclasses import dataclass, field
from typing import Any


@dataclass
class RoutingRule:
    """A single namespace-to-namespace routing permission.

    Attributes
    ----------
    source_namespace:
        Namespace from which the message originates.
    target_namespace:
        Namespace to which the message is being routed.
    allowed_message_types:
        List of :class:`~core.types.MessageType` value strings that are
        permitted to cross this namespace boundary.
    priority:
        Higher-priority rules are evaluated first; the first matching rule
        wins.
    """

    source_namespace: str
    target_namespace: str
    allowed_message_types: list[str] = field(default_factory=list)
    priority: int = 0


class NamespaceRouter:
    """Evaluate :class:`RoutingRule` lists against a message transfer."""

    def apply_rules(
        self,
        rules: list[RoutingRule],
        msg: Any,
        source_host: Any,
        target_host: Any,
    ) -> bool:
        """Return ``True`` if *msg* is allowed to travel from *source_host* to *target_host*.

        Rules are evaluated in descending priority order.  The first rule
        whose source/target namespaces match the hosts' namespaces determines
        the outcome.  If the message type is in the rule's allowed list (or
        the list is empty, meaning "allow all"), the transfer is permitted.

        If no rule matches, the transfer is **denied** by default.

        Parameters
        ----------
        rules:
            Ordered list of :class:`RoutingRule` instances.
        msg:
            A :class:`~core.message.Message` instance.
        source_host:
            The sending DTNHost (expected to have a ``namespace: str`` attr).
        target_host:
            The receiving DTNHost.
        """
        src_ns: str = getattr(source_host, "namespace", "default")
        tgt_ns: str = getattr(target_host, "namespace", "default")

        msg_type: str = ""
        mt = getattr(msg, "message_type", None)
        if mt is not None:
            msg_type = mt.value if hasattr(mt, "value") else str(mt)

        sorted_rules: list[RoutingRule] = sorted(
            rules, key=lambda r: r.priority, reverse=True
        )

        for rule in sorted_rules:
            if rule.source_namespace != src_ns:
                continue
            if rule.target_namespace != tgt_ns:
                continue
            # Matching rule found.
            if not rule.allowed_message_types:
                return True  # empty list means allow all
            return msg_type in rule.allowed_message_types

        # Default deny.
        return False


def generate_network_policy(rules: list[RoutingRule]) -> dict:
    """Produce a Kubernetes ``NetworkPolicy`` manifest dict from *rules*.

    The resulting dict can be serialised to YAML and applied to a cluster.
    Each rule maps to an ingress entry on the target namespace, allowing
    traffic from the source namespace for the specified message types (stored
    as pod-selector labels).
    """
    ingress_entries: list[dict] = []

    for rule in rules:
        entry: dict = {
            "from": [
                {
                    "namespaceSelector": {
                        "matchLabels": {
                            "name": rule.source_namespace,
                        }
                    }
                }
            ],
        }
        if rule.allowed_message_types:
            entry["ports"] = [
                {"protocol": "TCP", "port": 8080}
            ]
            entry["metadata_annotations"] = {
                "sim/allowed-types": ",".join(rule.allowed_message_types),
            }
        ingress_entries.append(entry)

    return {
        "apiVersion": "networking.k8s.io/v1",
        "kind": "NetworkPolicy",
        "metadata": {
            "name": "sim-routing-policy",
            "namespace": "default",
        },
        "spec": {
            "podSelector": {},
            "policyTypes": ["Ingress"],
            "ingress": ingress_entries,
        },
    }
