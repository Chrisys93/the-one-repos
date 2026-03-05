"""Default configuration values used when keys are absent from user config.

These constants mirror the hard-coded defaults scattered throughout the Java
ONE simulator and its SEND/EDR extensions.
"""

from __future__ import annotations

# ---------------------------------------------------------------------------
# Scenario defaults
# ---------------------------------------------------------------------------

DEFAULT_SCENARIO_NAME: str = "default_scenario"
DEFAULT_END_TIME: float = 10_800.0        # 3 hours in seconds
DEFAULT_UPDATE_INTERVAL: float = 0.1      # seconds between update ticks
DEFAULT_WARMUP: float = 1_000.0           # seconds of movement warm-up
DEFAULT_WORLD_SIZE: list[int] = [4500, 3400]
DEFAULT_REPO_OFFSET: list[float] = [500.0, 200.0]

# ---------------------------------------------------------------------------
# Node group defaults
# ---------------------------------------------------------------------------

DEFAULT_BUFFER_SIZE: int = 100_000_000          # 100 MB
DEFAULT_SPEED: list[float] = [1.0, 1.5]        # m/s  (walking)
DEFAULT_WAIT_TIME: list[float] = [0.0, 120.0]  # seconds
DEFAULT_MSG_TTL: int = 300                       # minutes (5 hours)

# ---------------------------------------------------------------------------
# Storage defaults
# ---------------------------------------------------------------------------

DEFAULT_STORAGE_SIZE: int = 100_000_000_000     # 100 GB
DEFAULT_COMPRESSION_RATE: float = 1.0

# ---------------------------------------------------------------------------
# Processing / ProcApplication defaults
# ---------------------------------------------------------------------------

DEFAULT_PASSIVE: bool = False
DEFAULT_STORAGE_MODE: bool = False
DEFAULT_MAX_STORAGE_TIME: float = 2_000.0       # seconds
DEFAULT_MAX_STORAGE_RATIO: float = 0.95
DEFAULT_MIN_STORAGE_RATIO: float = 0.001
DEFAULT_DEPLETION_RATE: int = 20_000_000        # 20 MB/s
DEFAULT_CLOUD_RATE: int = 10_000_000            # 10 MB/s
DEFAULT_CORE_COUNT: int = 4

# ---------------------------------------------------------------------------
# ProcGenApplication defaults
# ---------------------------------------------------------------------------

DEFAULT_GEN_INTERVAL: float = 5.0               # seconds between messages
DEFAULT_GEN_DELAY: float = 0.1                   # processing delay tag
DEFAULT_GEN_FRESH_PERIOD: float = 1.0            # seconds
DEFAULT_GEN_PROC_SHELF: float = 2.0              # seconds
DEFAULT_GEN_NONPROC_SHELF: float = 3.0           # seconds
DEFAULT_GEN_PASSIVE_RATE: tuple[int, int] = (1, 1)
DEFAULT_GEN_COMPRESS_RATE: tuple[int, int] = (1, 1)
DEFAULT_GEN_MESSAGE_SIZE: int = 1_000_000        # 1 MB
DEFAULT_GEN_DESTINATION_NAME: str = "r"
DEFAULT_GEN_SEED: int = 0

# ---------------------------------------------------------------------------
# Report defaults
# ---------------------------------------------------------------------------

DEFAULT_REPORT_OUTPUT_DIR: str = "reports/"
DEFAULT_REPORT_WARMUP: int = 0
DEFAULT_REPORT_GRANULARITY: float = 1.0
DEFAULT_REPORT_FORMAT: str = "text"

# ---------------------------------------------------------------------------
# Management / K8s / AI extension defaults
# ---------------------------------------------------------------------------

DEFAULT_MANAGEMENT_STATS_INTERVAL: float = 60.0
DEFAULT_PLACEMENT_STRATEGY: str = "general_purpose"
DEFAULT_K8S_NAMESPACE_PREFIX: str = "edr-"
DEFAULT_AI_EXPORT_FORMAT: str = "json"

# ---------------------------------------------------------------------------
# Optimisation defaults (matching Java World class)
# ---------------------------------------------------------------------------

DEFAULT_CELL_SIZE_MULT: int = 5
DEFAULT_RANDOMIZE_UPDATE_ORDER: bool = True
