/* 
 * Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details. 
 */
package report;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import core.DTNHost;
import core.Message;
import core.Settings;
import core.UpdateListener;
import core.SimScenario;


/**
 * Message location report. Reports the location (coordinates) of messages.
 * The messages that are reported and the reporting interval can be configured.
 */
public class RepoDepletedStorageBWReport extends Report implements UpdateListener {
	/** Reporting granularity -setting id ({@value}). 
	 * Defines the interval how often (seconds) a new snapshot of message 
	 * locations is created */
	public static final String GRANULARITY = "granularity";
	/** Reported messages -setting id ({@value}). 
	 * Defines the IDs of the messages that are reported 
	 * (comma separated list)*/
	public static final String REPORTED_MESSAGES = "messages";
	/** value of the granularity setting */
	protected final int granularity;
	/** time of last update*/
	protected double lastUpdate; 
	/** Identifiers of the message which are reported */
	protected HashSet<String> reportedMessages;
	/** Need to internally define simScenario*/
	private SimScenario simScenario;
	
	/** 
	 * TODO:
	 * \/ For these two, I need to create separate reports \/ 
	 */
	/** Array list for storing deleted messages from repos */
	private ArrayList<Long> deletedMessageNos;
	/** Array list for storing overall mean incoming speeds of repos */	
	private ArrayList<Double> overallMeanRepoSpeeds;
	
	
	/**
	 * Constructor. Reads the settings and initializes the report module.
	 */
	public RepoDepletedStorageBWReport() {
		Settings settings = getSettings();
		this.lastUpdate = 0;	
		this.granularity = settings.getInt(GRANULARITY);
		
		//The old way to determine how many messages are reported (all by default)
		if (settings.contains(REPORTED_MESSAGES)) {
			this.reportedMessages = new HashSet<String>();
			for (String msgId : settings.getCsvSetting(REPORTED_MESSAGES)) {
				this.reportedMessages.add(msgId);
			}
		} else {
			this.reportedMessages = null; /* all messages */
		}
		
		init();
	}

	/**
	 * Creates a new snapshot of the message locations if "granularity" 
	 * seconds have passed since the last snapshot. 
	 * @param hosts All the hosts in the world
	 */
	public void updated(List<DTNHost> hosts) {
		double simTime = getSimTime();
		/* creates a snapshot once every granularity seconds */
		if (simTime - lastUpdate >= granularity) {
			createSnapshot(hosts);
			this.lastUpdate = simTime - simTime % granularity;
		}
	}
	
	// Had to re-insert this, as it was not working beforehand.
	/**
	 * Returns true if the given message is tracked by the report
	 * @param m The message
	 * @return True if the message is tracked, false if not
	 */
	protected boolean isTracked(Message m) {
		return (this.reportedMessages == null ||
				this.reportedMessages.contains(m.getId()));
	}	
	
	
	/**
	 * Creates a snapshot of message locations 
	 * @param hosts The list of hosts in the world
	 */
	protected void createSnapshot(List<DTNHost> hosts) {
		boolean isFirstMessage;
		String reportLine;
		reportLine = (int)getSimTime() + " ";
		for (DTNHost host : hosts) {
			isFirstMessage = true;
			String hostname = host.name.toString();
			if (hostname.contains("r") ){
				double totalStoredMsgs =  host.getStorageSystem().getDepletedStaticMessagesBW(true);
				reportLine += " " + totalStoredMsgs;
				isFirstMessage = false;
			}
			
		}
		if (reportLine.length() > 0) {
		write(reportLine); /* write coordinate and message IDs */
		}
	}
	 
}
