/* 
 * Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details. 
 */
package input;

import java.util.Random;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import core.Settings;
import core.SettingsError;

/**
 * Message creation -external events generator. Creates uniformly distributed
 * message creation patterns whose message size and inter-message intervals can
 * be configured.
 */
public class ReposStorageConstantProcDepletion implements EventQueue {
	/** Message deletion interval range -setting id ({@value}). Can be either a 
	 * single value or a range (min, max) of uniformly distributed 
	 * random values. Defines the inter-message creation interval (seconds). */
	public static final String DEPLETION_INTERVAL_S = "depletionInterval";
	/** Message amount to be deleted each time the event occurs*/
	public static final String MSG_AMOUNT_S = "processedMsgNo";

	/* The lower bound is inclusive and upper bound exclusive. */
	public static final String HOST_NAMES_S = "procDepletionHostnames";
	
	/** Time of the next event (simulated seconds) */
	protected double nextEventsTime = 0;
	/** Range of host addresses that can be senders or receivers */
	//protected int[] hostRange = {0, 0};
	/** Range of host addresses that can be senders or receivers */
	protected ArrayList<String> hostNames;
	/** Host group names that can be receivers */
	//protected String toHostName = null;
	/** Range of host addresses that can be receivers */
	//protected int[] toHostRange = null;
	/** Prefix for the messages */
	protected String idPrefix;
	/** Interval between messages (min, max) */
	private int[] dplInterval;
	/** Number of messages to be depleted on each event */
	protected int processedMsgNo;

	/** Random number generator for this Class */
	protected Random rng;
	
	/**
	 * Constructor, initializes the interval between events, 
	 * and the size of messages generated, as well as number 
	 * of hosts in the network.
	 * @param s Settings for this generator.
	 */
	public ReposStorageConstantProcDepletion(Settings s){
		this.dplInterval = s.getCsvInts(DEPLETION_INTERVAL_S);
		//this.hostRange = s.getCsvInts(HOST_RANGE_S, 2);
		this.hostNames = new ArrayList<String>();
		//for (int i=0; i<HOST_NAMES_S.trim().length(); i++){
		//	this.hostNames.add(Character.toString(HOST_NAMES_S.charAt(i)));
		//}
		this.hostNames = s.getCsvSettingtoList(HOST_NAMES_S);

		/** Need to select the hosts by name instead, for opportunistic transfers. */
		if (s.contains(MSG_AMOUNT_S)) {
			this.processedMsgNo = s.getInt(MSG_AMOUNT_S);;
		}
		else {
			this.processedMsgNo = 1;
		}

		if (this.dplInterval.length == 1) {
			this.dplInterval = new int[] {this.dplInterval[0], 
					this.dplInterval[0]};
		}
		else {
			s.assertValidRange(this.dplInterval, DEPLETION_INTERVAL_S);
		}

		//s.assertValidRange(this.hostRange, HOST_RANGE_S);
		

		/*
		 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		 * might actually need "hostRange" anyway, to determine which host in 
		 * the whole group is creating a message, in order to send to a repository
		 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		 */
		/*if (this.hostRange[1] - this.hostRange[0] < 2) {
			if (this.toHostName == null) {
				throw new SettingsError("Host range must contain at least one " 
						+ "letter unless toHostRange is defined");
			}
			else if (this.hostNames.contains(toHostName)) {
				// XXX: teemuk: Since (X,X) == (X,X+1) in drawHostAddress()
				// there's still a boundary condition that can cause an
				// infinite loop.
				throw new SettingsError("If to and from host ranges contain" + 
						" only one host, they can't be the equal");
			}
		}*/
		/* ^^^Considering just commenting this whole if statement^^^*/
		
		/* calculate the first event's time */
		this.nextEventsTime = dplInterval[0] + 
			(dplInterval[0] == dplInterval[1] ? 0 : 
			rng.nextInt(dplInterval[1] - dplInterval[0]));
	}
	
	
	/**
	 * Draws a random host address from the configured address range
	 * @param hostRange The range of hosts
	 * @return A random host address
	 */
	/*protected int drawHostAddress(int hostRange[]) {
		if (hostRange[1] == hostRange[0]) {
			return hostRange[0];
		}
		return hostRange[0] + rng.nextInt(hostRange[1] - hostRange[0]);
	}*/
	
	/**
	 * Generates a (random) time difference between two events
	 * @return the time difference
	 */
	protected int drawNextEventTimeDiff() {
		int timeDiff = dplInterval[0] == dplInterval[1] ? 0 : 
			rng.nextInt(dplInterval[1] - dplInterval[0]);
		return dplInterval[0] + timeDiff;
	}
	
	/**
	 * Check hosts address that is different from the "from"
	 * address
	 * @param hostRange The range of hosts
	 * @param from the "from" address
	 * @return a destination address from the range, but different from "from"
	 */
	/*protected int drawToAddress(int hostRange[], int from) {
		int to;
		do {
			to = this.toHostRange != null ? drawHostAddress(this.toHostRange):
				drawHostAddress(this.hostRange); 
		} while (from==to);
		
		return to;
	}
	*/
	/** 
	 * Returns the next message creation event
	 * @see input.EventQueue#nextEvent()
	 */
	/*TODO: This \/ needs to be sorted out.*/
	public ExternalEvent nextEvent() {
		//try {
		//	System.setOut(new PrintStream(new FileOutputStream("logevent.txt")));
		//} catch(Exception e) {}
		int processedMsgNo;
		int interval;
		ArrayList <String> hosts;
		
		
		/* Get nodes for starting depletion of storage */
		hosts = this.hostNames;
		processedMsgNo = this.processedMsgNo;
		//System.out.println("The lower limit is: "+this.lowerLimit);
		//System.out.println("The higher limit is: "+this.higherLimit);
		//System.out.println("The higher limit is: "+this.msgNo);
		//System.out.println("to = " + to);
		interval = drawNextEventTimeDiff();
		
		/* Create event and advance to next event */
		ReposProcDepletionCreateEvent mce = new ReposProcDepletionCreateEvent(hosts, processedMsgNo, this.nextEventsTime);
		this.nextEventsTime += interval;	
		
		//if (this.msgTime != null && this.nextEventsTime > this.msgTime[1]) {
			/* next event would be later than the end time */
			//this.nextEventsTime = Double.MAX_VALUE;
		//}
		
		return mce;
	}

	/**
	 * Returns next message creation event's time
	 * @see input.EventQueue#nextEventsTime()
	 */
	public double nextEventsTime() {
		return this.nextEventsTime;
	}	
}
