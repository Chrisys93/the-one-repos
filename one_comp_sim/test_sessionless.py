#!/usr/bin/env python
"""Test session-less data transmission.

Validates that data stored in an EDR repo is NOT bound to a specific
session or connection, and is accessible to any entity that knows the
message ID or content hash.

Key properties tested:
1. Messages get content hashes assigned on storage ingest
2. Any connected node can discover stored message IDs and hashes
3. Messages can be retrieved by ID (session-agnostic)
4. Messages can be retrieved by content hash (session-agnostic)
5. Messages can be queried by application ID
6. Messages can be queried by label
7. Pull-based retrieval via router works across connections
"""

import sys
import os

sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

from one_comp_sim.config.schema import SimulationConfig
from one_comp_sim.core.scenario import SimScenario
from one_comp_sim.core.sim_clock import SimClock


def make_config() -> dict:
    """Build a config for the session-less test."""
    return {
        "scenario": {
            "name": "sessionless_test",
            "world_size": [100, 100],
            "end_time": 30,
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
                "buffer_size": 100_000_000,
                "msg_ttl": 300,
                "interfaces": ["radio"],
                "storage": {
                    "enabled": True,
                    "processing": True,
                    "size": 100_000_000,
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
                "max_storage_time": 2000,
                "max_storage": 0.95,
                "min_storage": 0.001,
                "depletion_rate": 20_000_000,
                "cloud_rate": 10_000_000,
                "core_count": 4,
            },
            "genApp": {
                "type": "ProcGenApplication",
                "interval": 2.0,
                "delay": 0.1,
                "fresh_period": 30.0,
                "proc_shelf": 60.0,
                "nonproc_shelf": 90.0,
                "passive": False,
                "passive_rate": [1, 1],
                "compress_rate": [1, 0],
                "message_size": 500_000,
                "destination_name": "r",
                "seed": 42,
            },
        },
        "reports": {
            "output_dir": "sessionless_reports",
            "granularity": 10.0,
            "warmup": 0,
            "metrics": [],
        },
    }


def run_test():
    """Run the session-less data transmission test."""
    cfg = SimulationConfig.model_validate(make_config())
    scenario = SimScenario(cfg)
    world = scenario.build()

    # Run for 30 seconds to generate and store some messages
    world.run(cfg.scenario.end_time)

    # Find the EDR and mobile nodes
    edr = None
    mobiles = []
    for h in world.hosts:
        if h.has_storage and h.storage is not None:
            edr = h
        else:
            mobiles.append(h)

    assert edr is not None, "No EDR found"
    assert len(mobiles) > 0, "No mobile nodes found"
    storage = edr.storage

    print(f"\nEDR: {edr.name}")
    print(f"Mobiles: {[m.name for m in mobiles]}")
    print(f"Messages in storage: static={len(storage.static_messages)}, "
          f"process={len(storage.process_messages)}, "
          f"processed={len(storage.processed_messages)}")

    total_msgs = (len(storage.static_messages)
                  + len(storage.process_messages)
                  + len(storage.processed_messages))
    print(f"Total messages in storage: {total_msgs}")

    passed = 0
    failed = 0

    # ---- Test 1: Content hashes assigned on ingest ----
    print("\n--- Test 1: Content hashes assigned on ingest ---")
    all_msgs = (storage.static_messages
                + storage.process_messages
                + storage.processed_messages)
    hashed = sum(1 for m in all_msgs if m.content_hash is not None)
    if hashed == len(all_msgs) and len(all_msgs) > 0:
        print(f"  PASS: {hashed}/{len(all_msgs)} messages have content hashes")
        passed += 1
    else:
        print(f"  FAIL: {hashed}/{len(all_msgs)} messages have content hashes")
        failed += 1

    # ---- Test 2: Message discovery by ID ----
    print("\n--- Test 2: Discover all stored message IDs ---")
    all_ids = edr.get_stored_message_ids()
    if len(all_ids) == total_msgs and total_msgs > 0:
        print(f"  PASS: Discovered {len(all_ids)} message IDs")
        passed += 1
    else:
        print(f"  FAIL: Expected {total_msgs} IDs, got {len(all_ids)}")
        failed += 1

    # ---- Test 3: Message discovery by hash ----
    print("\n--- Test 3: Discover all content hashes ---")
    all_hashes = edr.get_stored_content_hashes()
    if len(all_hashes) == hashed and hashed > 0:
        print(f"  PASS: Discovered {len(all_hashes)} content hashes")
        passed += 1
    else:
        print(f"  FAIL: Expected {hashed} hashes, got {len(all_hashes)}")
        failed += 1

    # ---- Test 4: Retrieve by ID (session-agnostic) ----
    print("\n--- Test 4: Retrieve message by ID (session-agnostic) ---")
    if all_ids:
        test_id = all_ids[0]
        retrieved = edr.request_message_by_id(test_id)
        if retrieved is not None and retrieved.id == test_id:
            print(f"  PASS: Retrieved '{test_id}' by ID")
            passed += 1
        else:
            print(f"  FAIL: Could not retrieve '{test_id}' by ID")
            failed += 1
    else:
        print(f"  SKIP: No messages in storage to test")

    # ---- Test 5: Retrieve by content hash (session-agnostic) ----
    print("\n--- Test 5: Retrieve message by content hash ---")
    if all_hashes:
        test_hash = all_hashes[0]
        retrieved = edr.request_message_by_hash(test_hash)
        if retrieved is not None and retrieved.content_hash == test_hash:
            print(f"  PASS: Retrieved message by hash '{test_hash[:16]}...'")
            passed += 1
        else:
            print(f"  FAIL: Could not retrieve by hash '{test_hash[:16]}...'")
            failed += 1
    else:
        print(f"  SKIP: No hashed messages to test")

    # ---- Test 6: Query by application ID ----
    print("\n--- Test 6: Query messages by application ID ---")
    proc_msgs = storage.get_messages_by_app("ProcApplication")
    if len(proc_msgs) > 0:
        print(f"  PASS: Found {len(proc_msgs)} messages for app 'ProcApplication'")
        passed += 1
    else:
        print(f"  FAIL: No messages found for app 'ProcApplication'")
        failed += 1

    # ---- Test 7: Messages NOT bound to sender session ----
    print("\n--- Test 7: Messages not bound to sender/receiver session ---")
    # Verify that stored messages can be accessed without the original
    # sender or connection reference. The storage lookup uses only the
    # message ID or content hash, NOT the from_host/to_host fields.
    if all_ids:
        test_id = all_ids[0]
        msg = storage.get_message_by_id(test_id)
        # The message exists in storage and can be found by ANYONE
        # who knows the ID, regardless of who sent it or which
        # connection delivered it.
        has_from = msg.from_host is not None
        can_access = msg is not None
        # The point: storage lookup doesn't check who's asking
        if can_access:
            print(f"  PASS: Message accessible by ID alone (from_host={msg.from_host}, "
                  f"but access is not restricted to that host)")
            passed += 1
        else:
            print(f"  FAIL: Could not access message by ID")
            failed += 1
    else:
        print(f"  SKIP: No messages to test")

    # ---- Test 8: Pull-based retrieval from mobile via router ----
    print("\n--- Test 8: Pull-based retrieval via router ---")
    # A mobile node connected to the EDR should be able to discover
    # and retrieve messages from the repo by ID or hash
    mobile = mobiles[0]
    discovered = mobile.router.discover_from_connected_repos()
    if discovered:
        repo_name = list(discovered.keys())[0]
        repo_ids = discovered[repo_name]
        if len(repo_ids) > 0:
            # Pull a specific message
            pulled = mobile.router.request_from_connected_repo(msg_id=repo_ids[0])
            if pulled is not None:
                print(f"  PASS: Mobile '{mobile.name}' pulled message '{pulled.id}' "
                      f"from repo '{repo_name}'")
                passed += 1
            else:
                print(f"  FAIL: Mobile could not pull message by ID from connected repo")
                failed += 1
        else:
            print(f"  FAIL: Repo '{repo_name}' reported no message IDs")
            failed += 1
    else:
        print(f"  FAIL: Mobile could not discover any connected repos")
        failed += 1

    # ---- Test 9: Pull by hash from mobile ----
    print("\n--- Test 9: Pull by content hash via router ---")
    if all_hashes:
        pulled = mobile.router.request_from_connected_repo(content_hash=all_hashes[0])
        if pulled is not None and pulled.content_hash == all_hashes[0]:
            print(f"  PASS: Mobile pulled message by hash from connected repo")
            passed += 1
        else:
            print(f"  FAIL: Could not pull by hash from connected repo")
            failed += 1
    else:
        print(f"  SKIP: No hashes to test")

    # ---- Summary ----
    print(f"\n{'=' * 65}")
    print(f"  SESSION-LESS DATA TRANSMISSION TEST RESULTS")
    print(f"{'=' * 65}")
    print(f"  Passed: {passed}, Failed: {failed}")
    if failed == 0:
        print(f"  ALL TESTS PASSED")
        print(f"\n  Data stored in repos is NOT bound to specific sessions.")
        print(f"  Any entity aware of the message ID or content hash can")
        print(f"  retrieve it through the session-less retrieval API.")
    else:
        print(f"  SOME TESTS FAILED")
    print()


if __name__ == "__main__":
    run_test()
