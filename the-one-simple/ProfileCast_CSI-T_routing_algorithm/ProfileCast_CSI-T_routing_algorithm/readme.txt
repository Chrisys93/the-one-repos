This file describes what should be modified in the Movement Model used in conjunction with ProfileCast CSI:T in order to make it work.


To use ProfileCastRounter.java with your ONE simulator these steps must me completed:

1) Substitute in package core  the class DTNHost.java with the one provided in the core folder of ProfileCast_CSI-T_routing_algorithm.

2) Substitute in package movement the class MovementModel.java with the one provided in the movement folder of ProfileCast_CSI-T_routing_algorithm.

3) Add all the report classes in the report folder of ProfileCast_CSI-T_routing_algorithm to the report package in ONE

4) Create a new ONE package and name it NPTLab.

5) Add all the classes in the NPTLab folder of ProfileCast_CSI-T_routing_algorithm to the NPTLab you just created.

6) Add the file profile_cast_settings.txt from the settings folder of ProfileCast_CSI-T_routing_algorithm to the ONE project folder.

6) Substitute the default_settings.txt with default_settings.txt from the settings folder of ProfileCast_CSI-T_routing_algorithm.

7) Modify the settings to accomodate your needs of simulation and you’re ready to go.


To use the classes for ProfileCast CSI:T I had to add this method to DTNHost.java
public MovementModel getMovementModel(){
		return movement;
	}
this method return the movement model for the node.
In MovementModel.java I had to add 

public double returnWaitTime(){
		return WaitTime;
	}

in order to obtain the WaitTime in the next location for each Node.
This two modifications were necessary in order to create the Trace of the user.

In order to use ProfileCastRouter.java the runtime arguments of DTNSim.java must be:

Program arguments:
profile_cast_settings.txt
VM arguments:
-Xms1024M -Xmx2048M 

This last command gives more heap space to the Java Virtual machine in order to accomodate any needs of the program in terms of physical memory space.