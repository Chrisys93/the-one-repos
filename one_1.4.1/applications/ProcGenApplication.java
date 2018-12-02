/* 
 * Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details. 
 */

package applications;

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
	/** Run in passive mode - don't process messages, but store */
	public static final String PROC_PASSIVE = "passive";
	/** Run in passive mode - don't process messages, but store */
	public static final String PROC_PASSIVE_RATE = "passiveRate";
	/** Proc generation interval */
	public static final String PROC_INTERVAL = "interval";
	/** Processing delay */
	public static final String PROC_DELAY = "delay";
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
	private double	interval = 5;
	private double	delay = 1;
	private boolean passive = false;
	private double[] passive_rate = {1, 1};
	private int		seed = 0;
	private String	destination = "r";
	private int		procSize=1000000;
	private Random	rng;
	private int		noPassive=0;
	private int		noProc=0;
	
	/** 
	 * Creates a new proc application with the given settings.
	 * 
	 * @param s	Settings to use for initializing the application.
	 */
	public ProcGenApplication(Settings s) {
		if (s.contains(PROC_PASSIVE)){
			this.passive = s.getBoolean(PROC_PASSIVE);
		}
		if (s.contains(PROC_PASSIVE_RATE)){
			this.passive_rate = s.getCsvDoubles(PROC_PASSIVE_RATE, 2);
		}
		if (s.contains(PROC_INTERVAL)){
			this.interval = s.getDouble(PROC_INTERVAL);
		}
		if (s.contains(PROC_DELAY)){
			this.delay = s.getDouble(PROC_DELAY);
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
		this.interval = a.getInterval();
		this.passive = a.isPassive();
		this.destination = a.getDestination();
		this.seed = a.getSeed();
		this.procSize = a.getProcSize();
		this.rng = new Random(this.seed);
		this.noPassive =0;
		this.passive_rate = a.getPassiveRate();
		this.delay = 1;
	}

	/** 
	 * Draws a connected repository host from the destination range
	 * 
	 * @return host
	 */
	private DTNHost connectedRepoHost(DTNHost host) {
		World w = SimScenario.getInstance().getWorld();
		return w.getNodeByName(this.destination, host);
	}
	
	@Override
	public Application replicate() {
		return new ProcGenApplication(this);
	}

	/** 
	 * Sends a proc packet if this is an active application instance.
	 * 
	 * @param host to which the application instance is attached
	 */
	@Override
	public void update(DTNHost host) {
		double curTime = SimClock.getTime();
		if (this.passive) {
			if (curTime - this.lastProc >= this.interval) {
				// Time to send a new proc
				Message m = new Message(host, connectedRepoHost(host), "nonproc" +
					SimClock.getIntTime() + "-" + host.getAddress(),
					getProcSize());
				m.addProperty("type", "nonproc");
				m.setAppID(APP_ID);
				host.createNewMessage(m);
				
				// Call listeners
				super.sendEventToListeners("SentProc", null, host);
				
				this.lastProc = curTime;
			}
		}
		else if (curTime - this.lastProc >= this.interval) {
			// Time to send a new proc
			if (this.noProc<(this.passive_rate[1])) {
				Message m = new Message(host, connectedRepoHost(host), "proc" +
						SimClock.getIntTime() + "-" + host.getAddress(),
						getProcSize());
				m.addProperty("type", "proc");
				m.addProperty("delay", this.delay);
				m.setAppID(APP_ID);
				host.createNewMessage(m);
				this.noProc++;
			}
			else if (this.noPassive<(this.passive_rate[0])) {

				Message m = new Message(host, connectedRepoHost(host), "nonproc" +
						SimClock.getIntTime() + "-" + host.getAddress(),
						getProcSize());
				m.addProperty("type", "nonproc");
				m.setAppID(APP_ID);
				host.createNewMessage(m);
				noPassive++;
			}
			else {
				this.noPassive = 0;
				this.noProc = 0;
			}
			
			// Call listeners
			super.sendEventToListeners("SentProc", null, host);
			
			this.lastProc = curTime;
		}
	}

	/**
	 * @return the lastProc
	 */
	public double getLastProc() {
		return lastProc;
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
	 * @param interval the interval to set
	 */
	public void setInterval(double interval) {
		this.interval = interval;
	}
	
	/**
	 * Return passive v for Processing rate
	 * @return
	 */
	private double[] getPassiveRate() {
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
	 * @param no. of previous passive messages
	 */
	public int getNoPassive() {
		return noPassive;
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

	@Override
	public Message handle(Message msg, DTNHost host) {
		// TODO Auto-generated method stub
		return null;
	}

}
