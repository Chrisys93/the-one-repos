/* 
 * Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details. 
 */

package applications;
import core.Application;
import core.DTNHost;
import core.Message;
import core.Settings;
import core.SimClock;

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
public class ProcApplication extends Application {
	/** Run in passive mode - don't process messages, but store */
	public static final String PROC_PASSIVE = "passive";
	/** Run in passive mode - don't process messages, but store */
	public static final String DEPL_RATE = "depletionRate";
	
	/** Application ID */
	public static final String APP_ID = "ProcApplication";
	
	// Private vars
	private double	lastProc = 0;
	private boolean passive = false;
	private double 	depl_rate = 1;
	//private int 	processedSize = (int) (procSize*proc_ratio);
	private int		noDepl = 0;
	private int		noProc = 0;
	
	/** 
	 * Creates a new proc application with the given settings.
	 * 
	 * @param s	Settings to use for initializing the application.
	 */
	public ProcApplication(Settings s) {
		if (s.contains(PROC_PASSIVE)){
			this.passive = s.getBoolean(PROC_PASSIVE);
		}
		if (s.contains(DEPL_RATE)){
			this.depl_rate = s.getDouble(DEPL_RATE);
		}
		super.setAppID(APP_ID);
	}
	
	/** 
	 * Copy-constructor
	 * 
	 * @param a
	 */
	public ProcApplication(ProcApplication a) {
		super(a);
		this.lastProc = a.getLastProc();
		this.passive = a.isPassive();
		this.noDepl = a.getNoDepl();
		this.noProc = a.getNoProc();
		this.depl_rate = a.getDeplRate();
		//this.processedSize = a.getProcessedSize();
	}
	
	@Override
	public Application replicate() {
		return new ProcApplication(this);
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
		if (host.hasStorageCapability()){
			double curTime = SimClock.getTime();
			String type = (String) msg.getProperty("type");
			double delay = (double)msg.getProperty("delay");
	
			System.out.println("handle is accessed on host: " + host);
			//System.out.println("There is "+freeStorage+" free storage space");
			
			if (!host.getStorageSystem().isStorageFull()){
				host.getStorageSystem().addToStoredMessages(msg);
				System.out.println("Message has been added to storage, with no problem");
			}
			else {
				//System.out.println("The current host is: " + host);
				host.getStorageSystem().deleteMessagesForSpace(false);
				host.getStorageSystem().addToStoredMessages(msg);
				System.out.println("Message has been added to storage, by deleting other messages");
			}
			
			/**
			 * TODO:
			 * PROCESSING PART HERE, with processing rate and delay:
			 * messages are processed at a certain rate/sec, obtaining messages according 
			 * to processMessage() method, in RepoStorage
			 */
			if (type.equalsIgnoreCase("proc")) {
				if (!host.getStorageSystem().isProcessedFull()) {
					host.getStorageSystem().processMessage(msg);
				}
				
				if (!host.getStorageSystem().isProcessingEmpty()) {
					if (curTime - this.lastProc >= delay) {
						if (type.equalsIgnoreCase("proc")){
							host.getStorageSystem().processMessage(msg);
							while (!host.getStorageSystem().hasMessage(msg.getId())) {}
						}
						this.lastProc = curTime;
						this.noProc++;
					}
					//System.out.println("The message to be deleted is "+this.msgNo+" from host "+host.name.toString());
				}
			}
			
			else if (type.equalsIgnoreCase("nonproc")) {
				//System.out.println("The message to be deleted is "+this.msgNo+" from host "+host.name.toString());
				host.getStorageSystem().addToStoredMessages(msg);
			}
		}
		return msg;
	}

	/** 
	 * Sends a proc packet if this is an active application instance.
	 * 
	 * @param host to which the application instance is attached
	 */
	@Override
	public void update(DTNHost host) {

		//System.out.println("processor update is accessed");

		/**
		 * TODO:
		 * DEPLETION PART HERE, with depletion rate:
		 * messages are depleted at a certain rate/sec, with priority
		 * being given to processed messages, and if not, unprocessed messages
		 * being depleted instead
		 */
		double curTime = SimClock.getTime();
		
		/**
		 * Processing older messages, that could not be processed as soon as
		 * accepted, for any reason.
		 */
		if (!host.getStorageSystem().isProcessingEmpty()) {
			if (host.getStorageSystem().getOldestProcessMessage() != null) {
				Message temp = host.getStorageSystem().getOldestProcessMessage();
				String temptype = (String)temp.getProperty("type");
				double delay = (double)temp.getProperty("delay");
				if (curTime - this.lastProc >= delay) {
					if (temptype.equalsIgnoreCase("proc")){
						host.getStorageSystem().processMessage(temp);
						while (!host.getStorageSystem().hasMessage(temp.getId())) {}
					}
					
					this.lastProc = curTime;
				}
				//System.out.println("The message to be deleted is "+this.msgNo+" from host "+host.name.toString());
				
			}
			if (!host.getStorageSystem().isProcessingFull()) {					
				Message tempstored = host.getStorageSystem().getOldestProcStoredMessage();
				host.getStorageSystem().addToStoredMessages(tempstored);
			}
		}
		
		/**
		 * Depleting processed messages; and if there are none, deplete stored messages
		 */
		
		for (int noDepl = 0; noDepl<this.depl_rate; noDepl++) {
			if (host.getStorageSystem().getOldestProcessedMessage() != null) {
				Message temp = host.getStorageSystem().getOldestProcessedMessage();
				host.getStorageSystem().deleteProcessedMessage(temp.getId());
				//System.out.println("The message to be deleted is "+this.msgNo+" from host "+host.name.toString());
					
				if (!host.getStorageSystem().isProcessingFull()) {					
					Message tempstored = host.getStorageSystem().getOldestProcStoredMessage();
					host.getStorageSystem().addToStoredMessages(tempstored);
				}
			}
			else if (host.getStorageSystem().getOldestStoredMessage() != null){
				Message temp = host.getStorageSystem().getOldestStoredMessage();
				//System.out.println("The message to be deleted is "+this.msgNo+" from host "+host.name.toString());
				host.getStorageSystem().addToDeplStoredMessages(temp);
				host.getStorageSystem().deleteStoredMessage(temp.getId());
			}
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
	public int getNoDepl() {
		return noDepl;
	}

	/**
	 * @param no. of previous passive messages
	 */
	public int getNoProc() {
		return noProc;
	}

	/**
	 * @param no. of previous passive messages
	 */
	public double getDeplRate() {
		return depl_rate;
	}

}
