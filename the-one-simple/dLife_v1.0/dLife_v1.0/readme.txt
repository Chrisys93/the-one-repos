This file describes what should be added to DTNSim.java and SimClock.java so dLife v1.0 can work with both TECD and TECDi utility functions as well as its community version, dLifeComm. 

It also describes the SlotTimeCheck.java which provides the idea of time slots required for calculations performed by TECD/TECDi.

NOTE: this implementation considered the Bubble Rap implemented by PJ Dillon which were adapted to run Dlife.java and DlifeComm.java. Thus, you will find some files in this bundle which you may already have. I recommend you back up similar files and then follow the intructions below if you already have the Bubble Rap on your simulator.
 
In the Sample file you can find a file for running Dlife/DlifeComm. It simulates 150 nodes with ShortestPathMapBasedMovement and load comes from an external file. These file should be placed together where one.sh is being called.
Implemented by Waldir Moreira (waldir.junior@ulusofona.pt).

Changes

i) core.DTNSim

public static void main(String[] args) {
	// TECD **********************************
		ArrayList<Long> arl= new ArrayList<Long>();
		/** 24 slots of 1 hour **/
		arl.add((long)3600);  // 1
		arl.add((long)7200);  // 2
		arl.add((long)10800); // 3
		arl.add((long)14400); // 4
		arl.add((long)18000); // 5
		arl.add((long)21600); // 6
		arl.add((long)25200); // 7
		arl.add((long)28800); // 8
		arl.add((long)32400); // 9
		arl.add((long)36000); // 10
		arl.add((long)39600); // 11
		arl.add((long)43200); // 12
		arl.add((long)46800); // 13
		arl.add((long)50400); // 14
		arl.add((long)54000); // 15
		arl.add((long)57600); // 16
		arl.add((long)61200); // 17
		arl.add((long)64800); // 18
		arl.add((long)68400); // 19
		arl.add((long)72000); // 20
		arl.add((long)75600); // 21
		arl.add((long)79200); // 22
		arl.add((long)82800); // 23
		arl.add((long)86400); // 24
		SlotTimeCheck g = new SlotTimeCheck(arl);
	// TECD **********************************

================================

ii) core.SimClock

public void setTime(double time) {
	clockTime = time;
	SlotTimeCheck.update(time); // TECD: Everytime the simulation time changes the update-method of SlotTimeCheck is called to check if a end of a Slot is already reached.   
}

public static void reset() {
	clockTime = 0;
	SlotTimeCheck.currentday=1; // Restart day count in case of running multiple runs 
}

================================

iii) Add new class SlotTimeCheck to core

SlotTimeCheck is the class to manage the SlotSystem. 
Depending on if a slot changed (end of a slot) it initiates metric calculations on all hosts in the simulation.

================================

iv) Add DecisionEngineRouter, RoutingDecisionEngine and MessageRouter to routing

================================

v) Add Dlife, DlifeComm, Duration, CommunityDetectionEngine, CommunityDetection, KCliqueCommunityDetection, SimpleCommunityDetection, Centrality, CWindowCentrality, SWindowCentrality to routing.community

================================

vi) Add CommunityDetectionReport to report

