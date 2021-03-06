/* 
 * Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details. 
 */
package core;

import input.EventQueue;
import input.EventQueueHandler;

import java.lang.Math.*;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import movement.MapBasedMovement;
import movement.MovementModel;
import movement.map.SimMap;
import routing.MessageRouter;

/**
 * A simulation scenario used for getting and storing the settings of a
 * simulation run.
 */
public class SimScenario implements Serializable {
	
	/** a way to get a hold of this... */	
	private static SimScenario myinstance=null;

	/** namespace of scenario settings ({@value})*/
	public static final String SCENARIO_NS = "Scenario";
	/** number of host groups -setting id ({@value})*/
	public static final String NROF_GROUPS_S = "nrofHostGroups";
	/** number of interface types -setting id ({@value})*/
	public static final String NROF_INTTYPES_S = "nrofInterfaceTypes";
	/** scenario name -setting id ({@value})*/
	public static final String NAME_S = "name";
	/** end time -setting id ({@value})*/
	public static final String END_TIME_S = "endTime";
	/** update interval -setting id ({@value})*/
	public static final String UP_INT_S = "updateInterval";
	/** simulate connections -setting id ({@value})*/
	public static final String SIM_CON_S = "simulateConnections";
	
	/** offset of repos to the right -setting id ({@value})*/
	public static final String REPO_XOFFSET = "repoXOffset";
	/** offset of repos to the top -setting id ({@value})*/
	public static final String REPO_YOFFSET = "repoYOffset";

	/** namespace for interface type settings ({@value}) */
	public static final String INTTYPE_NS = "Interface";
	/** interface type -setting id ({@value}) */
	public static final String INTTYPE_S = "type";
	/** interface name -setting id ({@value}) */
	public static final String INTNAME_S = "name";

	/** namespace for application type settings ({@value}) */
	public static final String APPTYPE_NS = "Application";
	/** application type -setting id ({@value}) */
	public static final String APPTYPE_S = "type";
	/** setting name for the number of applications */
	public static final String APPCOUNT_S = "nrofApplications";
	
	/** namespace for host group settings ({@value})*/
	public static final String GROUP_NS = "Group";
	/** group id -setting id ({@value})*/
	public static final String GROUP_ID_S = "groupID";
	/** number of hosts in the group -setting id ({@value})*/
	public static final String NROF_HOSTS_S = "nrofHosts";
	/** scanning interval -setting id ({@value})*/
	public static final String SCAN_INTERVAL_S = "scanInterval";
	/** movement model class -setting id ({@value})*/
	public static final String MOVEMENT_MODEL_S = "movementModel";
	/** router class -setting id ({@value})*/
	public static final String ROUTER_S = "router";
	/** number of interfaces in the group -setting id ({@value})*/
	public static final String NROF_INTERF_S = "nrofInterfaces";
	/** interface name in the group -setting id ({@value})*/
	public static final String INTERFACENAME_S = "interface";
	/** application name in the group -setting id ({@value})*/
	public static final String GAPPNAME_S = "application";

	/** host's file capability in the group -setting id ({@value})*/
	public static final String FILE_CAPABILITY_S = "fileCapability";
	/** simulate filesystems -setting id ({@value})*/
	public static final String SIM_FILES_S = "simulateFiles";

	/** host's storage capability in the group -setting id ({@value})*/
	public static final String STORAGE_CAPABILITY_S = "storageCapability";
	/** host's processing capability in the group -setting id ({@value})*/
	public static final String PROC_CAPABILITY_S = "processingCapability";
	/** simulate message storage in repos -setting id ({@value})*/
	public static final String SIM_STORE_S = "simulateStorage";

	/** package where to look for movement models */
	private static final String MM_PACKAGE = "movement.";
	/** package where to look for router classes */
	private static final String ROUTING_PACKAGE = "routing.";

	/** package where to look for interface classes */
	private static final String INTTYPE_PACKAGE = "interfaces.";
	
	/** package where to look for application classes */
	private static final String APP_PACKAGE = "applications.";

	/** Message storage size -setting id ({@value}). Integer value in bytes.*/
	public static final String STORE_SIZE_S = "storageSize";
	/** The processing nodes process and compress the messages with an average compression ratio */
	public static final String COMP_RAT_S = "compressionRate";

	
	/** The world instance */
	private World world;
	/** List of hosts in this simulation */
	protected List<DTNHost> hosts;
	/** Name of the simulation */
	private String name;
	/** number of host groups */
	int nrofGroups;
	/** number of hosts in every group */
	public int[] groupSizes;

	/** number of host groups with file capability*/
	public int nrofGroupsWithFiles;

	/** number of host groups with storage capability*/
	public int nrofGroupsWithStorage;

	/** number of host groups with storage capability*/
	public int nrofGroupsWithProcessing;

	/** Width of the world */
	private int worldSizeX;
	/** Height of the world */
	private int worldSizeY;
	
	/** Width division of the world  for repo coord allocation */
	private int worldSizeDivisionX;
	/** Height division of the world for repo coord allocation */
	private int worldSizeDivisionY;
	
	/** Width division of the world  for repo coord allocation */
	private double repo_xoffset;
	/** Height division of the world for repo coord allocation */
	private double repo_yoffset;
	
	

	/** Location for each repo when using RepoStationaryMovement */
	public static double[] simLocation = {0,0};
	
	/** Largest host's radio range */
	private double maxHostRange;
	/** Simulation end time */
	private double endTime;
	/** Update interval of sim time */
	private double updateInterval;
	/** External events queue */
	private EventQueueHandler eqHandler;
	/** Should connections between hosts be simulated */
	private boolean simulateConnections;

	/** Should filesystem in hosts be simulated */
	private boolean simulateFiles;

	/** Should repo storage in hosts be simulated */
	private boolean simulateRepos;


	/** Map used for host movement (if any) */
	private SimMap simMap;

	/** Global connection event listeners */
	private List<ConnectionListener> connectionListeners;
	/** Global message event listeners */
	private List<MessageListener> messageListeners;
	/** Global movement event listeners */
	private List<MovementListener> movementListeners;
	/** Global update event listeners */
	private List<UpdateListener> updateListeners;
	/** Global application event listeners */
	private List<ApplicationListener> appListeners;



	static {
		DTNSim.registerForReset(SimScenario.class.getCanonicalName());
		reset();
	}
	
	public static void reset() {
		myinstance = null;
	}

	/**
	 * Creates a scenario based on Settings object.
	 */
	protected SimScenario() {
		Settings s = new Settings(SCENARIO_NS);
		nrofGroups = s.getInt(NROF_GROUPS_S);

		this.name = s.valueFillString(s.getSetting(NAME_S));
		this.endTime = s.getDouble(END_TIME_S);
		this.updateInterval = s.getDouble(UP_INT_S);
		this.simulateConnections = s.getBoolean(SIM_CON_S);
		
		if (s.contains(SIM_FILES_S)) {
			this.simulateFiles = s.getBoolean(SIM_FILES_S);
		} else {
			this.simulateFiles = false;
		}
		
		if (s.contains(REPO_XOFFSET)) {
			this.repo_xoffset = s.getDouble(REPO_XOFFSET);
		} else {
			this.repo_xoffset = 500;
		}
		
		if (s.contains(REPO_YOFFSET)) {
			this.repo_yoffset = s.getDouble(REPO_YOFFSET);
		} else {
			this.repo_yoffset = 200;
		}

		if (s.contains(SIM_STORE_S)) {
			this.simulateRepos = s.getBoolean(SIM_STORE_S);
		} else {
			this.simulateRepos = false;
		}

		ensurePositiveValue(nrofGroups, NROF_GROUPS_S);
		ensurePositiveValue(endTime, END_TIME_S);
		ensurePositiveValue(updateInterval, UP_INT_S);

		this.simMap = null;
		this.maxHostRange = 1;

		this.connectionListeners = new ArrayList<ConnectionListener>();
		this.messageListeners = new ArrayList<MessageListener>();		
		this.movementListeners = new ArrayList<MovementListener>();
		this.updateListeners = new ArrayList<UpdateListener>();
		this.appListeners = new ArrayList<ApplicationListener>();
		this.eqHandler = new EventQueueHandler();

		/* TODO: check size from movement models */
		s.setNameSpace(MovementModel.MOVEMENT_MODEL_NS);
		int [] worldSize = s.getCsvInts(MovementModel.WORLD_SIZE, 2);
		this.worldSizeX = worldSize[0];
		this.worldSizeY = worldSize[1];
		
		createHosts();
		if(this.simulateRepos){
			addStorageToHosts();
			System.out.println("Repos are simulated");
		}		
		
		this.world = new World(hosts, worldSizeX, worldSizeY, updateInterval, 
				updateListeners, simulateConnections, 
				eqHandler.getEventQueues());
	}

	private void addStorageToHosts() {
		//try {
		//	System.setOut(new PrintStream(new FileOutputStream("loghosts.txt")));
		//} catch(Exception e) {System.out.println("Error");}
		/* !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		 * there should be smth here, like in the DTNFileGenerator, to initiate the
		 * message storage system and be initialized by the scenatrio initialization
		 * in the hosts, and that's what was missing!
		 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		 */
		for(DTNHost host:hosts){
			//System.out.println("Host " + host.name + " has "+host.hasStorageCapability()+  " storage");
			if (host.hasStorageCapability() && !host.hasProcessingCapability()){
				if (host.hasProcessingCapability()){
					host.setStorageSystem(host.getStorageSystem(), host.getStorageSystemSize(), host.getStorageSystemCompressionRate());
					//System.out.println("Host "+host.name+" has "+host.getStorageSystemSize()+ " storage");
					return;
				}
			}
			/*else if (!host.hasStorageCapability() && host.hasProcessingCapability()){
				host.setStorageSystem(host.getStorageSystem(), host.getStorageSystemSize(), host.getStorageSystemCompressionRate());
				//System.out.println("Host "+host.name+" has "+host.getStorageSystemSize()+ " storage");
				return;
			}*/
			//System.out.println("Host " + host.name + " has "+host.hasProcessingCapability()+  " processing storage");
		}
	}

	/**
	 * Returns the SimScenario instance and creates one if it doesn't exist yet
	 */
	public static SimScenario getInstance() {
		if (myinstance == null) {
			myinstance = new SimScenario();
		}
		return myinstance;
	}
	 

	/**
	 * Makes sure that a value is positive
	 * @param value Value to check
	 * @param settingName Name of the setting (for error's message)
	 * @throws SettingsError if the value was not positive
	 */
	private void ensurePositiveValue(double value, String settingName) {
		if (value < 0) {
			throw new SettingsError("Negative value (" + value + 
					") not accepted for setting " + settingName);
		}
	}

	/**
	 * Returns the name of the simulation run
	 * @return the name of the simulation run
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Returns true if connections should be simulated
	 * @return true if connections should be simulated (false if not)
	 */
	public boolean simulateConnections() {
		return this.simulateConnections;
	}
	
	/**
	 * Returns true if file systems should be simulated
	 * @return true if file systems should be simulated (false if not)
	 */
	public boolean simulateFiles() {
		return simulateFiles;
	}

	/**
	 * Returns true if storage systems should be simulated
	 * @return true if storage systems should be simulated (false if not)
	 */
	public boolean simulateRepos() {
		return simulateRepos;
	}
	
	/**
	 * Returns the width of the world
	 * @return the width of the world
	 */
	public int getWorldSizeX() {
		return this.worldSizeX;
	}

	/**
	 * Returns the height of the world
	 * @return the height of the world
	 */
	public int getWorldSizeY() {
		return worldSizeY;
	}

	/**
	 * Returns simulation's end time
	 * @return simulation's end time
	 */
	public double getEndTime() {
		return endTime;
	}

	/**
	 * Returns update interval (simulated seconds) of the simulation
	 * @return update interval (simulated seconds) of the simulation
	 */
	public double getUpdateInterval() {
		return updateInterval;
	}

	/**
	 * Returns how long range the hosts' radios have
	 * @return Range in meters
	 */
	public double getMaxHostRange() {
		return maxHostRange;
	}

	/**
	 * Returns the (external) event queue(s) of this scenario or null if there 
	 * aren't any
	 * @return External event queues in a list or null
	 */
	public List<EventQueue> getExternalEvents() {
		return this.eqHandler.getEventQueues();
	}

	/**
	 * Returns the SimMap this scenario uses, or null if scenario doesn't
	 * use any map
	 * @return SimMap or null if no map is used
	 */
	public SimMap getMap() {
		return this.simMap;
	}

	/**
	 * Adds a new connection listener for all nodes
	 * @param cl The listener
	 */
	public void addConnectionListener(ConnectionListener cl){
		this.connectionListeners.add(cl);
	}

	/**
	 * Adds a new message listener for all nodes
	 * @param ml The listener
	 */
	public void addMessageListener(MessageListener ml){
		this.messageListeners.add(ml);
	}

	/**
	 * Adds a new movement listener for all nodes
	 * @param ml The listener
	 */
	public void addMovementListener(MovementListener ml){
		this.movementListeners.add(ml);
	}

	/**
	 * Adds a new update listener for the world
	 * @param ul The listener
	 */
	public void addUpdateListener(UpdateListener ul) {
		this.updateListeners.add(ul);
	}

	/**
	 * Returns the list of registered update listeners
	 * @return the list of registered update listeners
	 */
	public List<UpdateListener> getUpdateListeners() {
		return this.updateListeners;
	}

	/** 
	 * Adds a new application event listener for all nodes.
	 * @param al The listener
	 */
	public void addApplicationListener(ApplicationListener al) {
		this.appListeners.add(al);
	}
	
	/**
	 * Returns the list of registered application event listeners
	 * @return the list of registered application event listeners
	 */
	public List<ApplicationListener> getApplicationListeners() {
		return this.appListeners;
	}
	
	/**
	 * Returns sizes of the various groups in the simulated world
	 * @return sizes of the various groups in the simulated world
	 */
	public int[] getGroupSizes(){
		return groupSizes;
	}
	
	/**
	 * Creates hosts for the scenario
	 */
	protected void createHosts() {
		//try {
		//	System.setOut(new PrintStream(new FileOutputStream("loghosts.txt")));
		//} catch(Exception e) {System.out.println("Error");}
		this.hosts = new ArrayList<DTNHost>();
		int lastGroupWithFiles = -1;
		int lastGroupWithStorage = -1;
		int lastGroupWithProcessing = -1;
		this.nrofGroupsWithFiles = 0;
		this.nrofGroupsWithStorage = 0;
		this.nrofGroupsWithProcessing = 0;
		this.groupSizes = new int[nrofGroups];

		for (int i=1; i<=nrofGroups; i++) {
			List<NetworkInterface> mmNetInterfaces = 
				new ArrayList<NetworkInterface>();
			Settings s = new Settings(GROUP_NS+i);
			s.setSecondaryNamespace(GROUP_NS);
			String gid = s.getSetting(GROUP_ID_S);
			int nrofHosts = s.getInt(NROF_HOSTS_S);
			int nrofInterfaces = s.getInt(NROF_INTERF_S);
			int appCount;
			boolean hasFileCapability;
			
			if (s.contains(FILE_CAPABILITY_S)) {
				hasFileCapability = s.getBoolean(FILE_CAPABILITY_S);
			} else {
				hasFileCapability = false;
			}
			
			if(hasFileCapability && i!=lastGroupWithFiles){
				lastGroupWithFiles = i;
			//	System.out.println("Group " + i + " has files");
				this.nrofGroupsWithFiles++;
			}

			boolean hasProcessingCapability;
			boolean hasStorageCapability;
			long storageSize = 0;
			if (s.contains(STORAGE_CAPABILITY_S)) {
				hasStorageCapability = s.getBoolean(STORAGE_CAPABILITY_S);
			//	System.out.println("has storage detected");
				/** \/ This \/ needs to be solved in the router part! */
				if (s.contains(STORE_SIZE_S)) {
					storageSize = s.getLong(STORE_SIZE_S);
				}
				else {
					storageSize = 100000000000L; //defaults to 100M storage
				}
			} else {
				hasStorageCapability = false;
			}
			if(s.contains(PROC_CAPABILITY_S)) {
				hasProcessingCapability = s.getBoolean(PROC_CAPABILITY_S);

				if (s.contains(STORE_SIZE_S)) {
					storageSize = s.getLong(STORE_SIZE_S);
				}
				else {
					storageSize = 100000000000L; //defaults to 100M storage
				}
			}
			else {
				hasProcessingCapability = false;
			}
			
			if((hasStorageCapability || hasProcessingCapability) && i!=lastGroupWithStorage){
				lastGroupWithStorage = i;
			//	System.out.println("Group " + i + " has storage");
				this.nrofGroupsWithStorage++;
			}
		
			
			double compressionRate = 1;
			if (s.contains(PROC_CAPABILITY_S)) {
				hasProcessingCapability = s.getBoolean(PROC_CAPABILITY_S);
				//System.out.println("has processing detected");
				compressionRate = s.getDouble(COMP_RAT_S);
			}
			else {
				hasProcessingCapability = false;
			}
			
			if(hasProcessingCapability && i!=lastGroupWithProcessing){
				lastGroupWithProcessing = i;
				System.out.println("Group " + i + " has processing");
				this.nrofGroupsWithProcessing++;
			}
			

			// creates prototypes of MessageRouter and MovementModel
			MovementModel mmProto = 
				(MovementModel)s.createIntializedObject(MM_PACKAGE + 
						s.getSetting(MOVEMENT_MODEL_S));
			MessageRouter mRouterProto = 
				(MessageRouter)s.createIntializedObject(ROUTING_PACKAGE + 
						s.getSetting(ROUTER_S));
			
			// checks that these values are positive (throws Error if not)
			ensurePositiveValue(nrofHosts, NROF_HOSTS_S);
			ensurePositiveValue(nrofInterfaces, NROF_INTERF_S);
			this.groupSizes[i-1] = nrofHosts;

			// setup interfaces
			for (int j=1;j<=nrofInterfaces;j++) {
				String Intname = s.getSetting(INTERFACENAME_S+j);
				Settings t = new Settings(Intname); 
				NetworkInterface mmInterface = 
					(NetworkInterface)t.createIntializedObject(INTTYPE_PACKAGE + 
							t.getSetting(INTTYPE_S));
				mmInterface.setClisteners(connectionListeners);
				mmNetInterfaces.add(mmInterface);
			}

			// setup applications
			if (s.contains(APPCOUNT_S)) {
				appCount = s.getInt(APPCOUNT_S);
			} else {
				appCount = 0;
			}
			for (int j=1; j<=appCount; j++) {
				String appname = null;
				Application protoApp = null;
				try {
					// Get name of the application for this group
					appname = s.getSetting(GAPPNAME_S+j);
					// Get settings for the given application
					Settings t = new Settings(appname);
					// Load an instance of the application
					protoApp = (Application)t.createIntializedObject(
							APP_PACKAGE + t.getSetting(APPTYPE_S));
					// Set application listeners
					protoApp.setAppListeners(this.appListeners);
					// Set the proto application in proto router
					//mRouterProto.setApplication(protoApp);
					mRouterProto.addApplication(protoApp);
				} catch (SettingsError se) {
					// Failed to create an application for this group
					System.err.println("Failed to setup an application: " + se);
					System.err.println("Caught at " + se.getStackTrace()[0]);
					System.exit(-1);
				}
			}

			
			if (mmProto instanceof MapBasedMovement) {
				this.simMap = ((MapBasedMovement)mmProto).getMap();
			}

			// creates hosts of ith group
			for (int j=0; j<nrofHosts; j++) {
				ModuleCommunicationBus comBus = new ModuleCommunicationBus();
				
				/*
				 * TODO:
				 * Introduce an if statement, if the RepoStationaryMovement is used,
				 * initialise position of DTNHosts depending in whether "r"'s or not,
				 * so that they are in a matrix.
				 */
				if (mmProto.toString().contains("RepoStationaryMovement")){
					this.worldSizeDivisionX = (int)Math.sqrt(nrofHosts);
					this.worldSizeDivisionY = (int)Math.sqrt(nrofHosts);
					SimScenario.simLocation[0] = repo_xoffset + (j % this.worldSizeDivisionX)*((this.worldSizeX-1150)/this.worldSizeDivisionX);
					SimScenario.simLocation[1] = repo_yoffset + (int)(j / worldSizeDivisionX)*((this.worldSizeY-1300)/this.worldSizeDivisionY);
					// creates prototypes of MessageRouter and MovementModel
					MovementModel mmPrototype = 
						(MovementModel)s.createIntializedObject(MM_PACKAGE + 
								s.getSetting(MOVEMENT_MODEL_S));
					// prototypes are given to new DTNHost which replicates
					// new instances of movement model and message router
					DTNHost host = new DTNHost(this.messageListeners, 
							this.movementListeners, gid, mmNetInterfaces, comBus, 
							mmPrototype, mRouterProto, hasStorageCapability, hasProcessingCapability, storageSize, compressionRate, simLocation);
					/*if (host.hasProcessingCapability()) {
						System.out.println("Host "+host.name+ " has " +host.getStorageSystemProcSize()+" processing storage");
					}
					else {
						System.out.println("Host "+host.name+"does not have processing capability");
					}*/
					hosts.add(host);
				}
				else {
				// prototypes are given to new DTNHost which replicates
				// new instances of movement model and message router
				DTNHost host = new DTNHost(this.messageListeners, 
						this.movementListeners, gid, mmNetInterfaces, comBus, 
						mmProto, mRouterProto, hasStorageCapability, hasProcessingCapability, storageSize, compressionRate, simLocation);
				hosts.add(host);
				}
			}
		}
	}

	/**
	 * Returns the list of nodes for this scenario.
	 * @return the list of nodes for this scenario.
	 */
	public List<DTNHost> getHosts() {
		return this.hosts;
	}
	
	/**
	 * Returns the World object of this scenario
	 * @return the World object
	 */
	public World getWorld() {
		return this.world;
	}

}
