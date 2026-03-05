"""Shared enumerations, type aliases, and constants for the simulator."""

from __future__ import annotations

from enum import Enum, auto
from typing import Union

# ---------------------------------------------------------------------------
# Type aliases (compatible with Python 3.8+)
# ---------------------------------------------------------------------------

SimTime = float       # Seconds since simulation start
ByteCount = int       # Size in bytes
BWRate = int          # Bytes per second
NodeAddress = int     # Unique integer address per node

# ---------------------------------------------------------------------------
# Enumerations
# ---------------------------------------------------------------------------


class MessageType(str, Enum):
    """Types a message can carry through the store-process-send pipeline."""
    NONPROC = "nonproc"
    PROC = "proc"
    PROCESSED = "processed"
    UNPROCESSED = "unprocessed"


class NodeRole(str, Enum):
    """Functional role of a network node."""
    EDR = "edr"            # Edge Data Repository (stationary, "r" nodes)
    PEDESTRIAN = "ped"     # Mobile pedestrian ("p" nodes)
    CAR = "car"            # Mobile car ("c" nodes)
    BUS = "bus"            # Mobile bus on fixed route ("b" nodes)
    IOT_OFFICE = "iot_office"
    IOT_HOME = "iot_home"


class DepletionTarget(str, Enum):
    """Where a depleted message is sent."""
    CLOUD = "cloud"
    LOCAL = "local"        # Local forced depletion (drop)
    CUSTOMER = "customer"  # Deliver to requesting customer
    INTER_EDR = "inter_edr"  # SEND: forward to another EDR


class PlacementStrategyType(str, Enum):
    """SEND management plane placement strategies."""
    GENERAL_PURPOSE = "general_purpose"
    POPULARITY_BASED = "popularity_based"
    FUNCTION_BASED = "function_based"
    HYBRID = "hybrid"


# ---------------------------------------------------------------------------
# Constants
# ---------------------------------------------------------------------------

INFINITE_TTL: int = -1
DEFAULT_UPDATE_INTERVAL: float = 0.1
DEFAULT_WARMUP: float = 1000.0
DEFAULT_MESSAGE_TTL: int = 300  # minutes

# Size multiplier helpers (matching original Java parseLong)
SIZE_UNITS: dict[str, int] = {
    "k": 1_000,
    "K": 1_000,
    "M": 1_000_000,
    "G": 1_000_000_000,
    "T": 1_000_000_000_000,
}


def parse_size(value: Union[str, int, float]) -> int:
    """Parse size strings like '5G', '500k', '20M' into byte counts."""
    if isinstance(value, (int, float)):
        return int(value)
    value = str(value).strip()
    for suffix, multiplier in SIZE_UNITS.items():
        if value.endswith(suffix):
            return int(float(value[:-1]) * multiplier)
    return int(float(value))
