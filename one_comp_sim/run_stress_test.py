#!/usr/bin/env python
"""Comprehensive storage stress test.

Runs five micro-scenarios that collectively exercise every depletion
and processing path in ProcApplication + RepoStorage:

Scenario A (all-proc):       passive_rate [0,1] → proc_forced, invalid proc, unproc
Scenario B (all-nonproc):    passive_rate [1,0] → static_cloud, static_forced
Scenario C (mixed + stale):  passive_rate [1,1], delay > freshness → stale processing, overtime
Scenario D (fast-proc):      delay << fresh_period → fresh_count (processed within freshness)
Scenario E (shelf-fail):     short proc_shelf + comp=True → unproc, punproc, unprocessed

Each scenario uses 1 EDR (tiny 5 MB storage) + 3 mobiles flooding it.
"""

import sys
import os

# Ensure the package is importable
sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

from one_comp_sim.config.schema import SimulationConfig
from one_comp_sim.core.scenario import SimScenario
from one_comp_sim.core.sim_clock import SimClock


def make_config(
    name: str,
    passive_rate: list,
    compress_rate: list,
    delay: float,
    fresh_period: float,
    proc_shelf: float,
    nonproc_shelf: float,
    core_count: int = 1,
    max_storage_time: float = 15.0,
    end_time: float = 120.0,
    interval: float = 0.8,
    storage_size: int = 5_000_000,
    min_storage: float = 0.10,
    max_storage: float = 0.50,
) -> dict:
    """Build a config dict for a stress scenario."""
    return {
        "scenario": {
            "name": name,
            "world_size": [100, 100],
            "end_time": end_time,
            "update_interval": 0.1,
            "simulate_connections": True,
            "warmup": 0,
            "repo_offset": [10, 10],
        },
        "interfaces": {
            "radio": {
                "type": "SimpleBroadcastInterface",
                "transmit_speed": 10_000_000,
                "transmit_range": 200,
                "scan_interval": 0.5,
            }
        },
        "node_groups": [
            {
                "group_id": "r",
                "count": 1,
                "role": "edr",
                "movement": "RepoStationaryMovement",
                "router": "RepoDirectDeliveryRouter",
                "buffer_size": 10_000_000,
                "msg_ttl": 300,
                "interfaces": ["radio"],
                "storage": {
                    "enabled": True,
                    "processing": True,
                    "size": storage_size,
                    "compression_rate": 2.0,
                },
                "applications": ["procApp"],
            },
            {
                "group_id": "m",
                "count": 3,
                "role": "ped",
                "movement": "RepoStationaryMovement",
                "router": "RepoDirectDeliveryRouter",
                "buffer_size": 50_000_000,
                "msg_ttl": 300,
                "interfaces": ["radio"],
                "applications": ["genApp"],
            },
        ],
        "applications": {
            "procApp": {
                "type": "ProcApplication",
                "passive": False,
                "storage_mode": True,
                "max_storage_time": max_storage_time,
                "max_storage": max_storage,
                "min_storage": min_storage,
                "depletion_rate": 500_000,
                "cloud_rate": 300_000,
                "core_count": core_count,
            },
            "genApp": {
                "type": "ProcGenApplication",
                "interval": interval,
                "delay": delay,
                "fresh_period": fresh_period,
                "proc_shelf": proc_shelf,
                "nonproc_shelf": nonproc_shelf,
                "passive": False,
                "passive_rate": passive_rate,
                "compress_rate": compress_rate,
                "message_size": 500_000,
                "destination_name": "r",
                "seed": 42,
            },
        },
        "reports": {
            "output_dir": "stress_reports",
            "granularity": 10.0,
            "warmup": 0,
            "metrics": [],
        },
    }


def run_scenario(label: str, cfg_dict: dict) -> dict:
    """Build and run a scenario, return the EDR's metrics."""
    cfg = SimulationConfig.model_validate(cfg_dict)
    scenario = SimScenario(cfg)
    world = scenario.build()
    world.run(cfg.scenario.end_time)

    # Find the single EDR
    edr = None
    for h in world.hosts:
        if h.has_storage and h.storage is not None:
            edr = h
            break

    if edr is None:
        return {"error": "No EDR found"}

    s = edr.storage
    m = s.metrics
    d = s.depletion

    # Get ProcApplication state
    pa = None
    for app in edr.router.get_all_applications():
        if hasattr(app, 'storage_mode'):
            pa = app
            break

    return {
        "label": label,
        "sim_time": SimClock.get_time(),
        # Compartments
        "static_msgs": len(s.static_messages),
        "static_size": s.static_size,
        "process_msgs": len(s.process_messages),
        "process_size": s.process_size,
        "processed_msgs": len(s.processed_messages),
        "processed_size": s.processed_messages_size,
        # Intake
        "total_received": m.total_received_messages,
        "total_received_size": m.total_received_size,
        # Processing
        "fresh_count": m.fresh_count,
        "stale_count": m.stale_count,
        # Quality
        "satisfied": m.satisfied_count,
        "unsatisfied": m.unsatisfied_count,
        "overtime": m.overtime_count,
        "unprocessed": m.unprocessed_count,
        # Cloud depletion
        "proc_cloud": d.get_count("proc_cloud"),
        "static_cloud": d.get_count("static_cloud"),
        "unproc": d.get_count("unproc"),
        "punproc": d.get_count("punproc"),
        # Forced depletion
        "proc_forced": d.get_count("proc_forced"),
        "static_forced": d.get_count("static_forced"),
        # Storage time
        "stor_time_count": m.stor_time_count,
        "stor_time_avg": m.get_avg_storage_time(),
        "stor_time_max": m.stor_time_max,
        # App state
        "storage_mode_final": pa.storage_mode if pa else None,
        # Mobile check
        "mobile_remaining": sum(
            h.router.get_nrof_messages()
            for h in world.hosts if not h.has_storage
        ),
    }


def print_result(r: dict) -> None:
    """Pretty-print one scenario's results."""
    print(f"\n{'=' * 65}")
    print(f"  {r['label']}")
    print(f"  sim_time={r['sim_time']:.0f}s, received={r['total_received']} msgs "
          f"({r['total_received_size']/1e6:.1f} MB)")
    print(f"{'=' * 65}")
    print(f"  Final state: static={r['static_msgs']}, process={r['process_msgs']}, "
          f"processed={r['processed_msgs']}")
    print(f"  Processing:  fresh={r['fresh_count']}, stale={r['stale_count']}")
    print(f"  Quality:     satisfied={r['satisfied']}, unsatisfied={r['unsatisfied']}, "
          f"overtime={r['overtime']}, unprocessed={r['unprocessed']}")
    print(f"  Cloud depl:  proc_cloud={r['proc_cloud']}, static_cloud={r['static_cloud']}, "
          f"unproc={r['unproc']}, punproc={r['punproc']}")
    print(f"  Forced depl: proc_forced={r['proc_forced']}, static_forced={r['static_forced']}")
    print(f"  Stor time:   {r['stor_time_count']} samples, avg={r['stor_time_avg']:.1f}s, "
          f"max={r['stor_time_max']:.1f}s")
    print(f"  stor_mode:   {r['storage_mode_final']}, mobile_buf={r['mobile_remaining']}")


def check_path(results: list, path_name: str, key: str) -> bool:
    """Check if at least one scenario triggered a depletion path."""
    for r in results:
        if r.get(key, 0) > 0:
            return True
    return False


# =========================================================================
# SCENARIOS
# =========================================================================

scenarios = [
    # A: all-proc → proc forced depletion, invalid proc, unprocessed paths
    (
        "A: ALL-PROC (proc_forced, invalid_proc, unprocessed)",
        make_config(
            name="stress_A_all_proc",
            passive_rate=[0, 1],     # 100% proc messages
            compress_rate=[1, 1],    # 50% compress, 50% delete
            delay=4.0,              # long processing → queuing → shelf expiry
            fresh_period=2.0,        # very short freshness
            proc_shelf=6.0,          # shelf life
            nonproc_shelf=10.0,      # unused (no nonproc)
            core_count=1,            # severe bottleneck
            max_storage_time=15.0,
            end_time=120.0,
            interval=0.8,
        ),
    ),
    # B: all-nonproc → static cloud depletion, static forced, stale static
    (
        "B: ALL-NONPROC (static_cloud, static_forced, stale static)",
        make_config(
            name="stress_B_all_nonproc",
            passive_rate=[1, 0],     # 100% nonproc messages
            compress_rate=[1, 1],    # 50% compress, 50% delete
            delay=0.1,              # unused (no proc)
            fresh_period=10.0,       # unused
            proc_shelf=10.0,         # unused
            nonproc_shelf=5.0,       # short → stale quickly
            core_count=1,
            max_storage_time=10.0,   # storage mode timeout
            end_time=120.0,
            interval=0.8,
        ),
    ),
    # C: mixed + stale processing → fresh/stale balance, overtime, all paths
    (
        "C: MIXED + STALE (stale processing, overtime, balanced)",
        make_config(
            name="stress_C_mixed",
            passive_rate=[1, 2],     # 33% nonproc, 67% proc
            compress_rate=[1, 1],    # 50% compress, 50% delete
            delay=5.0,              # delay > freshness → stale processing
            fresh_period=1.5,        # very short → most processing is stale
            proc_shelf=8.0,          # longer shelf so they do get processed
            nonproc_shelf=6.0,       # moderate
            core_count=1,            # bottleneck
            max_storage_time=20.0,
            end_time=120.0,
            interval=0.8,
        ),
    ),
    # D: fast processing → messages processed within freshness → fresh_count
    (
        "D: FAST-PROC (fresh_count, low delay)",
        make_config(
            name="stress_D_fast_proc",
            passive_rate=[0, 1],     # 100% proc messages
            compress_rate=[1, 1],    # 50% compress, 50% delete
            delay=0.1,              # very fast processing!
            fresh_period=30.0,       # very long freshness → all fresh
            proc_shelf=60.0,         # long shelf → nothing expires
            nonproc_shelf=60.0,      # unused
            core_count=4,            # plenty of cores → no queuing
            max_storage_time=15.0,
            end_time=60.0,           # shorter run, just need to confirm fresh
            interval=1.5,            # slower generation so processing keeps up
            storage_size=10_000_000, # 10 MB so processed compartment has room
            min_storage=0.01,        # low threshold → cloud depletion fires
            max_storage=0.90,        # high threshold → no forced depletion
        ),
    ),
    # E: shelf-fail with comp=True → unproc, punproc, unprocessed
    # Key: proc_shelf very short + large queue → shelf expires before processing
    # comp=True (compress_rate=[1,0] = 100% compress) → compressed unprocessed
    # Larger storage so is_processed_full() doesn't block the shelf-fail path
    (
        "E: SHELF-FAIL (unproc, punproc, unprocessed)",
        make_config(
            name="stress_E_shelf_fail",
            passive_rate=[0, 1],     # 100% proc messages
            compress_rate=[1, 0],    # 100% compress (comp=True always)
            delay=3.0,              # moderate processing delay
            fresh_period=1.0,        # very short freshness
            proc_shelf=2.0,          # VERY short shelf → expires in queue
            nonproc_shelf=10.0,      # unused
            core_count=1,            # bottleneck → queue builds up
            max_storage_time=15.0,
            end_time=120.0,
            interval=0.8,
            storage_size=20_000_000, # 20 MB → bigger processed capacity
            min_storage=0.05,        # low threshold → cloud depletion fires easily
            max_storage=0.40,        # force depletion at 40%
        ),
    ),
]

# =========================================================================
# RUN
# =========================================================================

results = []
for label, cfg_dict in scenarios:
    print(f"\n>>> Running: {label}")
    try:
        r = run_scenario(label, cfg_dict)
        results.append(r)
        print_result(r)
    except Exception as e:
        import traceback
        traceback.print_exc()
        print(f"  FAILED: {e}")
        results.append({"label": label, "error": str(e)})

# =========================================================================
# COVERAGE CHECK
# =========================================================================

print("\n" + "=" * 65)
print("  DEPLETION PATH COVERAGE SUMMARY")
print("=" * 65)

paths = [
    ("proc_cloud   (processed → cloud)",      "proc_cloud"),
    ("static_cloud (stale static → cloud)",    "static_cloud"),
    ("unproc       (unprocessed → cloud)",     "unproc"),
    ("punproc      (depleted unprocessed)",     "punproc"),
    ("proc_forced  (proc overflow)",           "proc_forced"),
    ("static_forced(static overflow)",         "static_forced"),
    ("fresh_count  (processed within fresh)",  "fresh_count"),
    ("stale_count  (processed past fresh)",    "stale_count"),
    ("satisfied    (within shelf life)",       "satisfied"),
    ("unsatisfied  (forced out early)",        "unsatisfied"),
    ("overtime     (past shelf life)",         "overtime"),
    ("unprocessed  (couldn't process)",        "unprocessed"),
]

all_pass = True
for desc, key in paths:
    triggered = check_path(results, desc, key)
    status = "PASS" if triggered else "MISS"
    if not triggered:
        all_pass = False
    # Find which scenarios triggered it
    which = [r["label"].split(":")[0].strip() for r in results if r.get(key, 0) > 0]
    which_str = ", ".join(which) if which else "none"
    print(f"  [{status}] {desc:45s}  triggered by: {which_str}")

print()
if all_pass:
    print("  ALL DEPLETION PATHS EXERCISED SUCCESSFULLY")
else:
    print("  WARNING: Some paths not triggered (see MISS above)")
print()
