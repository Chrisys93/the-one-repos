"""Export utilities for simulation results.

Provides helpers to convert :class:`~ai.tool_interface.SimulationResult` data
into formats suitable for RAG pipelines, ML training datasets, and pandas
DataFrames.
"""

from __future__ import annotations

import json
import os
from dataclasses import asdict
from typing import Any


def export_for_rag(
    results: list[Any],
    output_dir: str,
    chunk_size: int = 1000,
) -> list[str]:
    """Write *results* as chunked JSON files with metadata for RAG ingestion.

    Each chunk contains up to *chunk_size* result entries serialised as a
    JSON array.  A ``_metadata.json`` sidecar is written alongside each chunk
    containing the chunk index and entry count.

    Parameters
    ----------
    results:
        List of :class:`~ai.tool_interface.SimulationResult` (or plain dicts).
    output_dir:
        Directory in which chunk files are written.  Created if it does not
        exist.
    chunk_size:
        Maximum number of result entries per chunk file.

    Returns
    -------
    list[str]
        Paths of the chunk files that were written.
    """
    os.makedirs(output_dir, exist_ok=True)

    serialisable: list[dict] = _to_dicts(results)
    written: list[str] = []

    for idx in range(0, max(1, len(serialisable)), chunk_size):
        chunk: list[dict] = serialisable[idx : idx + chunk_size]
        chunk_index: int = idx // chunk_size
        chunk_path: str = os.path.join(output_dir, f"chunk_{chunk_index:04d}.json")
        meta_path: str = os.path.join(output_dir, f"chunk_{chunk_index:04d}_metadata.json")

        with open(chunk_path, "w", encoding="utf-8") as fh:
            json.dump(chunk, fh, indent=2, default=str)

        with open(meta_path, "w", encoding="utf-8") as fh:
            json.dump(
                {
                    "chunk_index": chunk_index,
                    "entry_count": len(chunk),
                    "source": "one_comp_sim",
                },
                fh,
                indent=2,
            )

        written.append(chunk_path)

    return written


def export_dataset(
    results: list[Any],
    format: str = "json",
) -> str:
    """Serialise *results* as an ML-ready dataset string.

    Parameters
    ----------
    results:
        List of :class:`~ai.tool_interface.SimulationResult` (or plain dicts).
    format:
        Output format.  Currently only ``"json"`` and ``"jsonl"`` are
        supported.

    Returns
    -------
    str
        The serialised dataset.

    Raises
    ------
    ValueError
        If *format* is not supported.
    """
    serialisable: list[dict] = _to_dicts(results)

    if format == "json":
        return json.dumps(serialisable, indent=2, default=str)

    if format == "jsonl":
        lines: list[str] = [
            json.dumps(entry, default=str) for entry in serialisable
        ]
        return "\n".join(lines) + "\n"

    raise ValueError(
        f"Unsupported format {format!r}; use 'json' or 'jsonl'."
    )


def results_to_dataframe(results: list[Any]) -> dict[str, list]:
    """Convert *results* to a column-oriented dict suitable for ``pandas.DataFrame``.

    Each top-level key in the result dicts becomes a column.  Nested dicts
    (e.g. ``summary``) are flattened with a ``"."`` separator.

    Parameters
    ----------
    results:
        List of :class:`~ai.tool_interface.SimulationResult` (or plain dicts).

    Returns
    -------
    dict[str, list]
        Column-name -> list-of-values mapping.  Pass directly to
        ``pandas.DataFrame(results_to_dataframe(results))``.
    """
    serialisable: list[dict] = _to_dicts(results)
    flat_rows: list[dict[str, Any]] = [_flatten(d) for d in serialisable]

    # Collect all column names in insertion order.
    all_keys: list[str] = []
    seen: set[str] = set()
    for row in flat_rows:
        for key in row:
            if key not in seen:
                all_keys.append(key)
                seen.add(key)

    columns: dict[str, list] = {k: [] for k in all_keys}
    for row in flat_rows:
        for key in all_keys:
            columns[key].append(row.get(key))

    return columns


# ---------------------------------------------------------------------------
# Internal helpers
# ---------------------------------------------------------------------------

def _to_dicts(results: list[Any]) -> list[dict]:
    """Normalise a list of results (dataclass or dict) to plain dicts."""
    out: list[dict] = []
    for r in results:
        if isinstance(r, dict):
            out.append(r)
        elif hasattr(r, "__dataclass_fields__"):
            out.append(asdict(r))
        else:
            out.append({"value": r})
    return out


def _flatten(d: dict, prefix: str = "") -> dict[str, Any]:
    """Flatten a nested dict using ``"."`` as the key separator."""
    items: dict[str, Any] = {}
    for key, value in d.items():
        full_key: str = f"{prefix}.{key}" if prefix else key
        if isinstance(value, dict):
            items.update(_flatten(value, full_key))
        else:
            items[full_key] = value
    return items
