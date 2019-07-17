/* 
 * Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details. 
 */

package applications;

import java.util.ArrayList;
import java.util.Random;


import core.Application;
import core.DTNHost;
import core.Message;
import core.Settings;
import core.SimClock;
import core.SimScenario;
import core.World;

/**
 * Simple proc application to demonstrate the application support. The 
 * application can be configured to send procs with a fixed interval or to only
 * answer to procs it receives. When the application receives a proc it sends
 * a pong message in response.
 * 
 * The corresponding <code>ProcAppReporter</code> class can be used to record
 * information about the application behavior.
 * 
 * @see ProcAppReporter
 * @author teemuk
 */
public class ProcGenApplication extends Application {
	
	/**
	 * TODO:
	 * Add comp/deletion flag - think about periods/ratios for this, 
	 * as well - correlated with proc, or not? - comp = true, del = false;
	 * Add freshness period;
	 * Add shelf-life;
	 */
	
	/** Run in passive mode - don't process messages, but store */
	public static final String PROC_PASSIVE = "passive";
	/** Rate of non-processing - and - processing message generation */
	public static final String PROC_PASSIVE_RATE = "passiveRate";
	/** Rate of compression- and deletion-destined message generation */
	public static final String PROC_COMPRESS_RATE = "compressRate";
	/** Proc generation interval */
	public static final String PROC_INTERVAL = "interval";
	/** Processing delay */
	public static final String PROC_DELAY = "delay";
	/** Proc messages freshness period */
	public static final String FRESH_PERIOD = "freshPer";
	/** Proc messages shelf life period delay */
	public static final String PROC_SHELF = "procShelf";
	/** Non-Proc messages shelf life period delay */
	public static final String NONPROC_SHELF = "nonprocShelf";
	/** Destination address range - inclusive lower, exclusive upper */
	public static final String PROC_DEST_NAME = "destinationName";
	/** Seed for the app's random number generator */
	public static final String PROC_SEED = "seed";
	/** Size of message generated */
	public static final String PROC_MSG_SIZE = "procSize";
	
	/** Application ID */
	public static final String APP_ID = "ProcGenApplication";
	
	// Private vars
	private double	lastProc = 0;
	private double	lastMsg = 0;
	private double	interval = 5;
	private double	delay = 0.1;
	private double	fresh_period = 1;
	private double	proc_shelf = 1.5;
	private double	nonproc_shelf = 2;
	private boolean passive = false;
	private int[] 	passive_rate = {1, 1};
	private int[] 	comp_rate = {1, 1};
	private int		seed = 0;
	private int		noProc = 0;
	private int		noPassive = 0;
	private int		noComp = 0;
	private int		noDel = 0;
	private String	destination = "r";
	private int		procSize=1000000;
	private Random	rng;
	
	/** 
	 * Creates a new proc application with the given settings.
	 * 
	 * @param s	Settings to use for initializing the application.
	 */
	public ProcGenApplication(Settings s) {
		
		/**
		 * TODO:
		 * Remodel so that each second it creates a certain amount of messages,
		 * of a certain type instead.
		 */
		
		if (s.contains(PROC_PASSIVE)){
			this.passive = s.getBoolean(PROC_PASSIVE);
		}
		if (s.contains(PROC_PASSIVE_RATE)){
			this.passive_rate = s.getCsvInts(PROC_PASSIVE_RATE, 2);
		}
		if (s.contains(PROC_COMPRESS_RATE)){
			this.comp_rate = s.getCsvInts(PROC_COMPRESS_RATE, 2);
		}
		if (s.contains(PROC_INTERVAL)){
			this.interval = s.getDouble(PROC_INTERVAL);
		}
		if (s.contains(PROC_DELAY)){
			this.delay = s.getDouble(PROC_DELAY);
		}
		if (s.contains(FRESH_PERIOD)){
			this.fresh_period = s.getDouble(FRESH_PERIOD);
		}
		if (s.contains(PROC_SHELF)){
			this.proc_shelf = s.getDouble(PROC_SHELF);
		}
		if (s.contains(NONPROC_SHELF)){
			this.nonproc_shelf = s.getDouble(NONPROC_SHELF);
		}
		if (s.contains(PROC_SEED)){
			this.seed = s.getInt(PROC_SEED);
		}
		if (s.contains(PROC_MSG_SIZE)) {
			this.procSize = s.getInt(PROC_MSG_SIZE);
		}
		if (s.contains(PROC_DEST_NAME)){
			this.destination = s.getSetting(PROC_DEST_NAME);
		}
		this.noProc = 0;
		this.noPassive = 0;
		this.noComp = 0;
		this.noDel = 0;
		
		rng = new Random(this.seed);
		super.setAppID(APP_ID);
	}
	
	/** 
	 * Copy-constructor
	 * 
	 * @param a
	 */
	public ProcGenApplication(ProcGenApplication a) {
		super(a);
		this.lastProc = a.getLastProc();
		this.lastMsg = a.getLastMsg();
		this.interval = a.getInterval();
		this.passive = a.isPassive();
		this.destination = a.getDestination();
		this.seed = a.getSeed();
		this.procSize = a.getProcSize();
		this.rng = new Random(this.seed);
		this.passive_rate = a.getPassiveRate();
		this.delay = a.getDelay();
		this.fresh_period = a.getFreshPer();
		this.proc_shelf = a.getProcShelf();
		this.nonproc_shelf = a.getNonProcShelf();
		this.noProc = 0;
		this.noPassive = 0;
		this.noComp = 0;
		this.noDel = 0;
	}

	/** 
	 * Draws a connected repository host from the destination range
	 * 
	 * @return host
	 */
	private DTNHost connectedRepoHost(DTNHost host) {
		ArrayList<DTNHost> Destinations = new ArrayList<DTNHost>();
		World w = SimScenario.getInstance().getWorld();
		DTNHost node = host;
		if (w.getNodeByName(this.destination, host) != host) {
			node = w.getNodeByName(this.destination, host);
		}
		else {
			for (DTNHost dest: w.getHosts()) {
				if (dest.name.contains("r")) {
					Destinations.add(dest);
				}
			}
			node = Destinations.get(rng.nextInt(Destinations.size()));
		}
		return node;
	}
	
	@Override
	public Application replicate() {
		return new ProcGenApplication(this);
	}
	
	/** 
	 * Handles an incoming message. If the message is a proc message replies
	 * with a pong message. Generates events for proc and pong messages.
	 * 
	 * @param msg	message received by the router
	 * @param host	host to which the application instance is attached
	 */
	@Override
	public Message handle(Message msg, DTNHost host) {
		
		return msg;
	}

	/** 
	 * Sends a proc packet if this is an active application instance.
	 * 
	 * @param host to which the application instance is attached
	 */
	@Override
	public void update(DTNHost host) {
		double curTime = SimClock.getTime();
		
		/**
		 * TODO:
		 * Remodel so that each second it creates a certain amount of messages,
		 * of a certain type instead.
		 * 
		 * Every time it goes through a loop, it should increment (or reset) a
		 * counter, which counts up to the different amounts of messages
		 * (proc/non-proc, comp/del) specified in the sim settings.
		 * If a value is 0, then make sure it skips it, and creates the right 
		 * amount of messages of the other types.
		 * 
		 * To give a better understanding: Interval should be used for interval
		 * BETWEEN messages, and then the different counters can be used to change 
		 * the different tags of the messages.
		 */

		//System.out.println("generator update is accessed on: " + host);
		if (this.passive) {
			if (curTime - this.lastMsg >= this.interval) {
				// Time to send a new proc
				Message m = new Message(host, connectedRepoHost(host), "nonproc" +
					SimClock.getIntTime() + "-" + host.getAddress(),
					getProcSize());
				m.addProperty("type", "nonproc");
				m.setAppID("ProcApplication");
				host.createNewMessage(m);
				
				// Call listeners
				super.sendEventToListeners("SentProc", null, host);
				
				this.lastMsg = curTime;
			}
		}
		else if (curTime - this.lastMsg >= this.interval) {
			Message m = new Message(host, connectedRepoHost(host), SimClock.getIntTime() + "-" + host.getAddress() + connectedRepoHost(host), getProcSize());
			// Time to send a new proc
			/**
			 * BIG PROBLEM here:
			 * Need to find a way to reset the passive, proc,
			 * comp and del counters, while also assigning the
			 * appropriate tags, and not skipping messages!
			 */
			
			if (this.noPassive<this.passive_rate[0]) {
				m.addProperty("type", "nonproc");
				m.addProperty("shelfLife", this.nonproc_shelf);
				m.setAppID("ProcApplication");
				noPassive++;
			}
			else if (this.noProc<this.passive_rate[1]) {
				m.addProperty("type", "proc");
				m.addProperty("delay", this.delay);
				m.addProperty("freshPer", this.fresh_period);
				m.addProperty("shelfLife", this.proc_shelf);
				m.setAppID("ProcApplication");
				this.noProc++;
			}
			
			if (this.noPassive >= this.passive_rate[0] && this.noProc >= this.passive_rate[1]) {
				this.noPassive = 0;
				this.noProc = 0;
			}
			
			if (this.noComp<(this.comp_rate[0])) {
				m.addProperty("comp", true);
				m.setAppID("ProcApplication");
				noComp++;
			}
			else if (this.noDel<(this.comp_rate[1])) {
				m.addProperty("comp", false);
				m.setAppID("ProcApplication");
				this.noDel++;
			}
			
			if (this.noComp >= this.comp_rate[0] && this.noDel >= this.comp_rate[1]) {
				this.noComp = 0;
				this.noDel = 0;
			}
			
			host.createNewMessage(m);
			
			
			// Call listeners
			super.sendEventToListeners("SentProc", null, host);
			
			this.lastMsg = curTime;
		}
	}

	/**
	 * @return the lastProc
	 */
	public double getLastProc() {
		return lastProc;
	}

	/**
	 * @return the lastProc
	 */
	public double getLastMsg() {
		return lastMsg;
	}

	/**
	 * @param lastProc the lastProc to set
	 */
	public void setLastProc(double lastProc) {
		this.lastProc = lastProc;
	}

	/**
	 * @return the interval
	 */
	public double getInterval() {
		return interval;
	}

	/**
	 * @return the interval
	 */
	public double getDelay() {
		return delay;
	}

	/**
	 * @return the interval
	 */
	public double getFreshPer() {
		return fresh_period;
	}

	/**
	 * @return the interval
	 */
	public double getProcShelf() {
		return proc_shelf;
	}

	/**
	 * @return the interval
	 */
	public double getNonProcShelf() {
		return nonproc_shelf;
	}

	/**
	 * @param interval the interval to set
	 */
	public void setInterval(double interval) {
		this.interval = interval;
	}
	
	/**
	 * Return passive v for Processing rate
	 * @return
	 */
	private int[] getPassiveRate() {
		return passive_rate;
	}

	/**
	 * @return the passive
	 */
	public boolean isPassive() {
		return passive;
	}

	/**
	 * @param passive the passive to set
	 */
	public void setPassive(boolean passive) {
		this.passive = passive;
	}

	/**
	 * @return the destMin
	 */
	public String getDestination() {
		return destination;
	}

	/**
	 * @param destMin the destMin to set
	 */
	public void setDestMin(String destination) {
		this.destination = destination;
	}

	/**
	 * @return the seed
	 */
	public int getSeed() {
		return seed;
	}

	/**
	 * @param seed the seed to set
	 */
	public void setSeed(int seed) {
		this.seed = seed;
	}

	/**
	 * @return the procSize
	 */
	public int getProcSize() {
		return procSize;
	}

	/**
	 * @param procSize the procSize to set
	 */
	public void setProcSize(int procSize) {
		this.procSize = procSize;
	}

}
