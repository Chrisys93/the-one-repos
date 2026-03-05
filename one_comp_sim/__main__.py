"""CLI entry-point: python -m one_comp_sim.

Commands
--------
run       Run a simulation from a YAML config file.
sweep     Run parameter sweeps from a YAML config file.
convert   Convert a legacy .txt config to YAML.
"""

from __future__ import annotations

import argparse
import json
import sys
import time
from pathlib import Path
from typing import Any


def _cmd_run(args: argparse.Namespace) -> None:
    """Execute a single simulation run."""
    from one_comp_sim.config.loader import load_config, load_legacy_config
    from one_comp_sim.core.scenario import SimScenario

    config_path = args.config
    if config_path.endswith(".txt"):
        config = load_legacy_config(config_path)
    else:
        config = load_config(config_path)

    print(f"Building scenario from {config_path} ...")
    scenario = SimScenario(config)
    world = scenario.build()

    end_time = args.end_time or config.scenario.end_time
    print(f"Running simulation for {end_time:.0f}s ...")

    start = time.time()
    result = world.run(end_time=end_time)
    elapsed = time.time() - start

    print(f"Simulation complete: {result.sim_time:.1f}s sim-time in {elapsed:.2f}s wall-time")

    # Print summary
    if result.summary:
        print(f"\nHost summaries ({len(result.summary)} hosts with storage):")
        for host_name, metrics in sorted(result.summary.items()):
            if isinstance(metrics, dict):
                parts = [f"{k}={v}" for k, v in list(metrics.items())[:6]]
                print(f"  {host_name}: {', '.join(parts)}")
            else:
                print(f"  {host_name}: {metrics}")

    # Export results if requested
    if args.output:
        _export_result(result, args.output)
        print(f"\nResults written to {args.output}")


def _cmd_sweep(args: argparse.Namespace) -> None:
    """Execute parameter sweep runs."""
    from one_comp_sim.config.loader import load_config, expand_sweeps
    from one_comp_sim.core.scenario import SimScenario

    config = load_config(args.config)
    configs = expand_sweeps(config)

    print(f"Running {len(configs)} sweep configurations ...")
    all_results = []

    for i, cfg in enumerate(configs):
        print(f"\n--- Sweep {i + 1}/{len(configs)} ---")
        scenario = SimScenario(cfg)
        world = scenario.build()
        result = world.run()
        all_results.append({
            "sweep_index": i,
            "sim_time": result.sim_time,
            "wall_time": result.wall_time_seconds,
            "summary": result.summary,
        })
        print(f"  Completed: {result.sim_time:.1f}s sim in {result.wall_time_seconds:.2f}s")

    if args.output:
        with open(args.output, "w") as f:
            json.dump(all_results, f, indent=2, default=str)
        print(f"\nSweep results written to {args.output}")


def _cmd_convert(args: argparse.Namespace) -> None:
    """Convert a legacy .txt config to YAML."""
    import yaml
    from one_comp_sim.config.loader import load_legacy_config

    config = load_legacy_config(args.input)
    output = args.output or args.input.replace(".txt", ".yaml")

    yaml_data = config.model_dump(exclude_none=True, exclude_defaults=True)

    with open(output, "w") as f:
        yaml.dump(yaml_data, f, default_flow_style=False, sort_keys=False)

    print(f"Converted {args.input} -> {output}")


def _export_result(result: Any, path: str) -> None:
    """Export a SimulationResult to JSON."""
    data = {
        "scenario_name": result.scenario_name,
        "sim_time": result.sim_time,
        "wall_time_seconds": result.wall_time_seconds,
        "summary": result.summary,
    }
    if result.time_series is not None:
        data["time_series"] = result.time_series
    if result.warnings:
        data["warnings"] = result.warnings

    with open(path, "w") as f:
        json.dump(data, f, indent=2, default=str)


def main() -> None:
    parser = argparse.ArgumentParser(
        prog="one_comp_sim",
        description="Edge Data Repository Network Simulator",
    )
    sub = parser.add_subparsers(dest="command", help="Available commands")

    # run
    p_run = sub.add_parser("run", help="Run a simulation")
    p_run.add_argument("config", help="Path to YAML or legacy .txt config")
    p_run.add_argument("--end-time", type=float, default=None, help="Override simulation end time")
    p_run.add_argument("--output", "-o", help="Export results to JSON file")

    # sweep
    p_sweep = sub.add_parser("sweep", help="Run parameter sweeps")
    p_sweep.add_argument("config", help="Path to YAML config with sweep definitions")
    p_sweep.add_argument("--output", "-o", help="Export sweep results to JSON")

    # convert
    p_conv = sub.add_parser("convert", help="Convert legacy .txt config to YAML")
    p_conv.add_argument("input", help="Path to legacy .txt config")
    p_conv.add_argument("--output", "-o", help="Output YAML path")

    args = parser.parse_args()

    if args.command == "run":
        _cmd_run(args)
    elif args.command == "sweep":
        _cmd_sweep(args)
    elif args.command == "convert":
        _cmd_convert(args)
    else:
        parser.print_help()
        sys.exit(1)


if __name__ == "__main__":
    main()
