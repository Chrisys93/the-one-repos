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
public class RepoUnProcessedReport extends Report implements UpdateListener {
	/** Reporting granularity -setting id ({@value}). 
	 * Defines the interval how often (seconds) a new snapshot of message 
	 * locations is created */
	public static final String GRANULARITY = "granularity";
	/** Reported messages -setting id ({@value}). 
	 * Defines the IDs of the messages that are reported 
	 * (comma separated list)*/
	public static final String REPORTED_MESSAGES = "messages";
	/** value of the granularity setting */
	//protected final int granularity;
	protected final double endTime;
	/** time of last update*/
	protected double lastUpdate; 
	/** Identifiers of the message which are reported */
	protected HashSet<String> reportedMessages;
	/** Need to internally define simScenario*/
	private SimScenario simScenario;
	
	private List<DTNHost> hosts;
	
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
	public RepoUnProcessedReport() {
		Settings settings = getSettings();
		this.lastUpdate = 0;	
		//this.granularity = settings.getInt(GRANULARITY);
		this.endTime = settings.getDouble(SimScenario.END_TIME_S);
		
		//The old way to determine how many messages are reported (all by default)
		if (settings.contains(REPORTED_MESSAGES)) {
			this.reportedMessages = new HashSet<String>();
			for (String msgId : settings.getCsvSetting(REPORTED_MESSAGES)) {
				this.reportedMessages.add(msgId);
			}
		} else {
			this.reportedMessages = null; /* all messages */
		}

		/*this.reportedMessages = new HashSet<String>();
		for (String msgId : settings.getCsvSetting(REPORTED_MESSAGES)) {
			this.reportedMessages.add(msgId);
		}*/
		
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
		if (simTime + 5 == this.endTime) {
			createSnapshot(hosts);
		}
		this.hosts = hosts;
	}
	
	
	/**
	 * Creates a snapshot of message locations 
	 * @param hosts The list of hosts in the world
	 */
	protected void createSnapshot(List<DTNHost> hosts) {
		String reportLine1;
		reportLine1 = "No. of unprocessed messages";
		for (DTNHost host : this.hosts) {
			String hostname = host.name.toString();
			if (hostname.contains("r") ){
				
				int NrofOvertimeMessages = host.getStorageSystem().getNrofOvertimeMessages();
				
				reportLine1 += " " + NrofOvertimeMessages;
			}			
		}
		if (reportLine1.length() > 0) {
		write(reportLine1); /* write coordinate and message IDs */
		}
	}
	
	@Override
	public void done() {
		String reportLine1;
		reportLine1 = "";
		for (DTNHost host : this.hosts) {
			String hostname = host.name.toString();
			if (hostname.contains("r") ){
				
				int NrofUnProcessedMessages = host.getStorageSystem().getNrofUnProcessedMessages();
				
				reportLine1 += " " + NrofUnProcessedMessages;
			}			
		}
		if (reportLine1.length() > 0) {
			write(reportLine1); /* write coordinate and message IDs */
		}
		super.done();
		
	}
	 
}
