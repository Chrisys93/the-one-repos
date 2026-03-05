"""Visualization wrappers for simulation output.

Provides matplotlib-based charts for common metrics.  Plotly is used
when available and explicitly requested via *interactive=True*.
"""

from __future__ import annotations

from typing import Any, Optional


def plot_storage_fill(
    time_series: dict[str, list],
    host_names: Optional[list[str]] = None,
    title: str = "Storage Fill Over Time",
    output_path: Optional[str] = None,
    interactive: bool = False,
) -> Any:
    """Line chart of storage fill (stored messages) over time.

    Parameters
    ----------
    time_series:
        Dict from ``MetricsCollector.get_all_metrics()`` -- expects keys
        like ``"stored_messages"``.
    host_names:
        Subset of hosts to plot.  ``None`` plots all.
    title:
        Chart title.
    output_path:
        If provided, save the figure to this path instead of showing.
    interactive:
        Use Plotly if ``True`` and available; otherwise matplotlib.

    Returns
    -------
    The figure object (matplotlib Figure or plotly Figure).
    """
    metric = time_series.get("stored_messages", [])
    return _line_chart(
        metric, host_names=host_names, title=title,
        ylabel="Stored Messages", output_path=output_path,
        interactive=interactive,
    )


def plot_bandwidth_usage(
    time_series: dict[str, list],
    bw_metrics: Optional[list[str]] = None,
    title: str = "Bandwidth Usage Over Time",
    output_path: Optional[str] = None,
    interactive: bool = False,
) -> Any:
    """Stacked or multi-line chart of depletion bandwidth categories."""
    if bw_metrics is None:
        bw_metrics = [
            "depl_proc_bw", "depl_unproc_bw",
            "depl_storage_bw", "depl_cloud_storage_bw",
        ]
    datasets = {k: time_series.get(k, []) for k in bw_metrics if k in time_series}
    return _multi_line_chart(
        datasets, title=title, ylabel="Bandwidth (B/s)",
        output_path=output_path, interactive=interactive,
    )


def plot_satisfaction(
    time_series: dict[str, list],
    title: str = "Satisfaction Ratio Over Time",
    output_path: Optional[str] = None,
    interactive: bool = False,
) -> Any:
    """Line chart of satisfaction metric over time."""
    metric = time_series.get("satisfaction", [])
    return _line_chart(
        metric, title=title, ylabel="Satisfaction Ratio",
        output_path=output_path, interactive=interactive,
    )


def plot_freshness(
    time_series: dict[str, list],
    title: str = "Freshness Ratio Over Time",
    output_path: Optional[str] = None,
    interactive: bool = False,
) -> Any:
    """Line chart of freshness metric over time."""
    metric = time_series.get("freshness", [])
    return _line_chart(
        metric, title=title, ylabel="Freshness Ratio",
        output_path=output_path, interactive=interactive,
    )


def plot_overtime(
    time_series: dict[str, list],
    title: str = "Overtime Messages Over Time",
    output_path: Optional[str] = None,
    interactive: bool = False,
) -> Any:
    """Line chart of overtime message count over time."""
    metric = time_series.get("overtime", [])
    return _line_chart(
        metric, title=title, ylabel="Overtime Messages",
        output_path=output_path, interactive=interactive,
    )


def plot_host_summary_bar(
    summary: dict[str, dict[str, Any]],
    metric_key: str = "total_stored",
    title: Optional[str] = None,
    output_path: Optional[str] = None,
    interactive: bool = False,
) -> Any:
    """Bar chart comparing a single metric across hosts.

    Parameters
    ----------
    summary:
        Per-host summary dicts from ``SimulationResult.summary``.
    metric_key:
        Key to extract from each host dict.
    """
    if title is None:
        title = f"{metric_key} by Host"

    hosts = sorted(summary.keys())
    values = [summary[h].get(metric_key, 0) if isinstance(summary[h], dict) else 0 for h in hosts]

    if interactive:
        return _plotly_bar(hosts, values, title, metric_key, output_path)
    return _mpl_bar(hosts, values, title, metric_key, output_path)


# ---------------------------------------------------------------------------
# Internal helpers
# ---------------------------------------------------------------------------

def _line_chart(
    data: list,
    host_names: Optional[list[str]] = None,
    title: str = "",
    ylabel: str = "",
    output_path: Optional[str] = None,
    interactive: bool = False,
) -> Any:
    """Generic line chart for time-series data.

    *data* is a list of ``(time, {host: value, ...})`` tuples.
    """
    if interactive:
        return _plotly_line(data, host_names, title, ylabel, output_path)
    return _mpl_line(data, host_names, title, ylabel, output_path)


def _multi_line_chart(
    datasets: dict[str, list],
    title: str = "",
    ylabel: str = "",
    output_path: Optional[str] = None,
    interactive: bool = False,
) -> Any:
    """Multi-series line chart (one line per metric)."""
    if interactive:
        return _plotly_multi_line(datasets, title, ylabel, output_path)
    return _mpl_multi_line(datasets, title, ylabel, output_path)


# ---------------------------------------------------------------------------
# Matplotlib backends
# ---------------------------------------------------------------------------

def _mpl_line(
    data: list,
    host_names: Optional[list[str]],
    title: str,
    ylabel: str,
    output_path: Optional[str],
) -> Any:
    import matplotlib.pyplot as plt

    fig, ax = plt.subplots(figsize=(12, 6))
    if not data:
        ax.set_title(title)
        return fig

    # Extract hosts from first data point
    if isinstance(data[0], (list, tuple)) and len(data[0]) >= 2:
        times = [d[0] for d in data]
        all_hosts = sorted(data[0][1].keys()) if isinstance(data[0][1], dict) else []
        hosts = host_names or all_hosts
        for h in hosts:
            vals = [d[1].get(h, 0) if isinstance(d[1], dict) else 0 for d in data]
            ax.plot(times, vals, label=h)
    else:
        ax.plot(range(len(data)), data)

    ax.set_title(title)
    ax.set_xlabel("Time (s)")
    ax.set_ylabel(ylabel)
    if len(ax.get_lines()) > 1:
        ax.legend(fontsize="small", ncol=2)
    fig.tight_layout()

    if output_path:
        fig.savefig(output_path, dpi=150)
    return fig


def _mpl_multi_line(
    datasets: dict[str, list],
    title: str,
    ylabel: str,
    output_path: Optional[str],
) -> Any:
    import matplotlib.pyplot as plt

    fig, ax = plt.subplots(figsize=(12, 6))
    for name, data in datasets.items():
        if not data:
            continue
        if isinstance(data[0], (list, tuple)) and len(data[0]) >= 2:
            times = [d[0] for d in data]
            # Aggregate across hosts
            vals = [
                sum(d[1].values()) if isinstance(d[1], dict) else d[1]
                for d in data
            ]
        else:
            times = list(range(len(data)))
            vals = data
        ax.plot(times, vals, label=name)

    ax.set_title(title)
    ax.set_xlabel("Time (s)")
    ax.set_ylabel(ylabel)
    ax.legend(fontsize="small")
    fig.tight_layout()

    if output_path:
        fig.savefig(output_path, dpi=150)
    return fig


def _mpl_bar(
    hosts: list[str],
    values: list,
    title: str,
    ylabel: str,
    output_path: Optional[str],
) -> Any:
    import matplotlib.pyplot as plt

    fig, ax = plt.subplots(figsize=(max(8, len(hosts) * 0.4), 6))
    ax.bar(range(len(hosts)), values)
    ax.set_xticks(range(len(hosts)))
    ax.set_xticklabels(hosts, rotation=45, ha="right", fontsize=7)
    ax.set_title(title)
    ax.set_ylabel(ylabel)
    fig.tight_layout()

    if output_path:
        fig.savefig(output_path, dpi=150)
    return fig


# ---------------------------------------------------------------------------
# Plotly backends (optional)
# ---------------------------------------------------------------------------

def _plotly_line(
    data: list,
    host_names: Optional[list[str]],
    title: str,
    ylabel: str,
    output_path: Optional[str],
) -> Any:
    try:
        import plotly.graph_objects as go
    except ImportError:
        return _mpl_line(data, host_names, title, ylabel, output_path)

    fig = go.Figure()
    if data and isinstance(data[0], (list, tuple)) and len(data[0]) >= 2:
        times = [d[0] for d in data]
        all_hosts = sorted(data[0][1].keys()) if isinstance(data[0][1], dict) else []
        hosts = host_names or all_hosts
        for h in hosts:
            vals = [d[1].get(h, 0) if isinstance(d[1], dict) else 0 for d in data]
            fig.add_trace(go.Scatter(x=times, y=vals, name=h, mode="lines"))

    fig.update_layout(title=title, xaxis_title="Time (s)", yaxis_title=ylabel)
    if output_path:
        fig.write_html(output_path)
    return fig


def _plotly_multi_line(
    datasets: dict[str, list],
    title: str,
    ylabel: str,
    output_path: Optional[str],
) -> Any:
    try:
        import plotly.graph_objects as go
    except ImportError:
        return _mpl_multi_line(datasets, title, ylabel, output_path)

    fig = go.Figure()
    for name, data in datasets.items():
        if not data:
            continue
        if isinstance(data[0], (list, tuple)):
            times = [d[0] for d in data]
            vals = [
                sum(d[1].values()) if isinstance(d[1], dict) else d[1]
                for d in data
            ]
        else:
            times = list(range(len(data)))
            vals = data
        fig.add_trace(go.Scatter(x=times, y=vals, name=name, mode="lines"))

    fig.update_layout(title=title, xaxis_title="Time (s)", yaxis_title=ylabel)
    if output_path:
        fig.write_html(output_path)
    return fig


def _plotly_bar(
    hosts: list[str],
    values: list,
    title: str,
    ylabel: str,
    output_path: Optional[str],
) -> Any:
    try:
        import plotly.graph_objects as go
    except ImportError:
        return _mpl_bar(hosts, values, title, ylabel, output_path)

    fig = go.Figure(data=[go.Bar(x=hosts, y=values)])
    fig.update_layout(title=title, yaxis_title=ylabel)
    if output_path:
        fig.write_html(output_path)
    return fig
