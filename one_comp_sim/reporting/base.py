"""Report ABC -- base class for all report generators.

Port of Java Report.java.
"""

from __future__ import annotations

import os
from abc import ABC, abstractmethod
from typing import IO, Any, Optional


class Report(ABC):
    """Base report with interval-based output to file or stream."""

    def __init__(
        self,
        output_path: Optional[str] = None,
        interval: float = -1,
        warmup_time: int = 0,
        precision: int = 4,
    ) -> None:
        self.output_path = output_path
        self.interval = interval
        self.warmup_time = warmup_time
        self.precision = precision
        self._out: Optional[IO[str]] = None
        self._last_report_time: float = 0.0

    def init(self) -> None:
        if self.output_path:
            os.makedirs(os.path.dirname(self.output_path) or ".", exist_ok=True)
            self._out = open(self.output_path, "w")

    @abstractmethod
    def updated(self, hosts: list[Any]) -> None:
        """Called each simulation step with the list of all hosts."""
        ...

    def write(self, line: str) -> None:
        if self._out is not None:
            self._out.write(line + "\n")
            self._out.flush()

    def done(self) -> None:
        if self._out is not None:
            self._out.close()
            self._out = None

    def is_warmup(self, sim_time: float) -> bool:
        return sim_time < self.warmup_time
