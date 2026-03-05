"""Output format support: text, CSV, JSON, Parquet."""

from __future__ import annotations

import json
import os
from enum import Enum
from typing import Any


class OutputFormat(str, Enum):
    TEXT = "text"
    CSV = "csv"
    JSON = "json"
    PARQUET = "parquet"


def write_time_series(
    data: list[tuple[float, dict[str, Any]]],
    path: str,
    fmt: OutputFormat = OutputFormat.TEXT,
    metric_name: str = "",
) -> None:
    """Write time-series data to file in the specified format."""
    os.makedirs(os.path.dirname(path) or ".", exist_ok=True)

    if fmt == OutputFormat.TEXT:
        _write_text(data, path)
    elif fmt == OutputFormat.CSV:
        _write_csv(data, path)
    elif fmt == OutputFormat.JSON:
        _write_json(data, path, metric_name)
    elif fmt == OutputFormat.PARQUET:
        _write_parquet(data, path, metric_name)


def _write_text(data: list[tuple[float, dict[str, Any]]], path: str) -> None:
    with open(path, "w") as f:
        for time, values in data:
            parts = [f"{time:.1f}"]
            for host_name in sorted(values.keys()):
                val = values[host_name]
                if isinstance(val, tuple):
                    parts.extend(str(v) for v in val)
                else:
                    parts.append(str(val))
            f.write(" ".join(parts) + "\n")


def _write_csv(data: list[tuple[float, dict[str, Any]]], path: str) -> None:
    if not data:
        return
    hosts = sorted(data[0][1].keys()) if data else []
    with open(path, "w") as f:
        f.write("time," + ",".join(hosts) + "\n")
        for time, values in data:
            parts = [f"{time:.1f}"]
            for h in hosts:
                val = values.get(h, 0)
                if isinstance(val, tuple):
                    parts.append("|".join(str(v) for v in val))
                else:
                    parts.append(str(val))
            f.write(",".join(parts) + "\n")


def _write_json(
    data: list[tuple[float, dict[str, Any]]], path: str, metric_name: str
) -> None:
    records = []
    for time, values in data:
        records.append({"time": time, "metric": metric_name, "values": values})
    with open(path, "w") as f:
        json.dump(records, f, indent=2, default=str)


def _write_parquet(
    data: list[tuple[float, dict[str, Any]]], path: str, metric_name: str
) -> None:
    try:
        import pandas as pd

        if not data:
            return
        rows = []
        for time, values in data:
            for host, val in values.items():
                rows.append(
                    {"time": time, "metric": metric_name, "host": host, "value": val}
                )
        df = pd.DataFrame(rows)
        df.to_parquet(path, index=False)
    except ImportError:
        # Fall back to JSON if pyarrow/pandas not available
        _write_json(data, path.replace(".parquet", ".json"), metric_name)
