/* 
 * 2018 UCL
 * Author: Adrian-Cristian Nicolaescu
 */

package applications;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import core.Application;
import core.DTNHost;
import core.Message;
import core.Settings;
import core.SimClock;


/**
 * Processing application, implemented on each of the repositories, for
 * processing/storing and depleting all incoming messages, according to 
 * the scope and age of each message.
 */
public class ProcApplication extends Application {
	/** Run in passive mode - don't process messages, but store */
	public static final String PROC_PASSIVE = "passive";
	/** Maximum depletion bandwidth */
	public static final String DEPL_RATE = "depletionRate";
	/** Number of processing threads */
	public static final String PROC_NO = "coreNo";
	/** Maximum period of time (minimum update frequency) allocated to subscriber for data request */
	public static final String DEPL_REQ = "maxReq";
	
	/** Application ID */
	public static final String APP_ID = "ProcApplication";
	
	// Private vars
	private double	lastProc = 0;
	private double 	lastDepl = 0;
	private double 	lastCheck = 0;
	private boolean passive = false;
	private long 	depl_rate = 0;
	private double	maxReq = 1;
	private int		proc_rate = 4;
	//private int 	processedSize = (int) (procSize*proc_ratio);

	private Random rng;
	
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
			this.depl_rate = s.getLong(DEPL_RATE);
		}
		if (s.contains(DEPL_REQ)){
			this.maxReq = s.getDouble(DEPL_REQ);
		}
		if (s.contains(PROC_NO)){
			this.proc_rate = s.getInt(PROC_NO);
		}
		
		//this.rng = new Random(depl_rate);
		
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
		this.lastDepl = a.getLastDepl();
		this.passive = a.isPassive();
		this.depl_rate = a.getDeplRate();
		this.proc_rate = a.getProcRate();
		this.maxReq = a.getDeplReq();
		//this.processedSize = a.getProcessedSize();
	}
	
	@Override
	public Application replicate() {
		return new ProcApplication(this);
	}
	
	/**
	 * TODO:
	 * Make sure to check that all the storages and checks are updated
	 * everywhere, where needed.
	 */
	
	/** 
	 * Handles an incoming message and stores it. If the message is a proc message,
	 * it processes it.
	 * 
	 * @param msg	message received by the router
	 * @param host	host to which the application instance is attached
	 */
	@Override
	public Message handle(Message msg, DTNHost host) {
		if (host.hasStorageCapability()){
			double curTime = SimClock.getTime();
			String type = (String) msg.getProperty("type");
			msg.updateProperty("received", curTime);
	
			//System.out.println("handle is accessed on host: " + host);

			host.getStorageSystem().addToStoredMessages(msg);
			
			if (type.equalsIgnoreCase("proc")) {				
				if ((!host.getStorageSystem().isProcessingEmpty()) && 
					(!host.getStorageSystem().isProcessedFull())) {
					double delayed = (double)msg.getProperty("delay");
					if (curTime - this.lastProc >= delayed) {
						host.getStorageSystem().processMessage(msg);
						host.getStorageSystem().addToCachedMessagesNo(1);
					}
				}
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
		
		/**
		 * TODO:
		 * Review this whole update process, for freshness, priority and 
		 * processing/non-processing precedence
		 */

		//System.out.println("processor update is accessed");
		double curTime = SimClock.getTime();
		
		/**
		 * Processing older messages, that could not be processed as soon as
		 * accepted, for any reason.
		 */
		if (host.getStorageSystem().getOldestProcessMessage(true) != null && 
			(!host.getStorageSystem().isProcessedFull())) {
			
			/**
			 * NOTE: It can be safely assumed that, when information is sent from the
			 * 		Edge and received, the producer app provides the freshest information
			 * 		available.
			 */
			
			for (int i = host.getStorageSystem().getFullCachedMessagesNo(); i<this.proc_rate && 
				!host.getStorageSystem().isProcessingEmpty() && 
				!host.getStorageSystem().isProcessedFull() &&
				host.getStorageSystem().getOldestProcessMessage(true) != null;i++) {
				
				Message tempp = host.getStorageSystem().getOldestProcessMessage(true);
				double delayed = (double)tempp.getProperty("delay");
				
				if (curTime - this.lastProc >= delayed) {
					host.getStorageSystem().processMessage(tempp);
				}
				/*else if (curTime - this.lastProc >= (double)host.getStorageSystem().getNewestProcessMessage().getProperty("delay")){
					tempp = host.getStorageSystem().getNewestProcessMessage();
					host.getStorageSystem().processMessage(tempp);
				}*/
				else {
					break;
				}
			}
		}
		
		/**
		 * Depleting processed messages; and if there are none, deplete stored messages
		 */
		
		/* TODO:
		 * All of this needs to be modified.
		 */
		//double cst = new rng.nextDouble();
		double request = ThreadLocalRandom.current().nextDouble(0.1, this.maxReq);
		//System.out.println("Request is: "+request+" and maxReq is "+this.maxReq+ " and proc rate "+this.proc_rate);
		if (curTime - this.lastDepl >= request) {
			
			long deplBW = 0;
			while (deplBW<this.depl_rate && 
					host.getStorageSystem().getProcessedMessagesSize() + host.getStorageSystem().getStaticMessagesSize() + host.getStorageSystem().getProcMessagesSize() > this.depl_rate &&
					!host.getStorageSystem().isProcessedEmpty() && !host.getStorageSystem().isStorageEmpty()) {
				/* 
				 * In order to make the system (kind of) fair, we want to make sure that it does not get overflowed 
				 * by static messages and processed messages are not depleted, past a point, and neither the other
				 * way around (having the cloud off-loading as a solution)
				 */
				if (host.getStorageSystem().getStaticMessagesSize() < (host.getStorageSystem().getTotalStorageSpace()/1.25)) {
					/* 
					 * Oldest processed message is depleted (as a FIFO type of storage,
					 * and a new message for processing is processed
					 */
					if (!host.getStorageSystem().isProcessedEmpty()) {
						
						Message temp = host.getStorageSystem().getOldestProcessedMessage();
						host.getStorageSystem().deleteProcessedMessage(temp.getId());
						
						Message tempp = host.getStorageSystem().getNewestProcessMessage();
						double delayed = (double)tempp.getProperty("delay");
						double received = (double)tempp.getProperty("received");
						double freshness = (double)tempp.getProperty("freshness");
						
						if (host.getStorageSystem().getOldestProcessMessage(true) != null && 
							(!host.getStorageSystem().isProcessedFull())) {
							if (curTime - this.lastProc >= delayed && curTime -  received < freshness) {
								if (!host.getStorageSystem().isProcessingEmpty() && 
									!host.getStorageSystem().isProcessedFull() ) {
									host.getStorageSystem().processMessage(tempp);
									host.getStorageSystem().addToCachedMessagesNo(1);
								}
							}
						}
						//pdepleted += 1;
						//System.out.println(curTime + ": The message was deleted at: "+host.name.toString());
					}
					/* Oldest unprocessed message is depleted (as a FIFO type of storage) */
					else if (host.getStorageSystem().getOldestStaticMessage(true) != null){
						Message temp = host.getStorageSystem().getOldestStaticMessage(true);
						host.getStorageSystem().addToDeplStaticMessages(temp);
						host.getStorageSystem().deleteStaticMessage(temp.getId());
					}
					else if (host.getStorageSystem().getOldestStaticMessage(false) != null){
						Message temp = host.getStorageSystem().getOldestStaticMessage(false);
						host.getStorageSystem().addToDeplUnProcMessages(temp);
					}
					/* Message to be processed is offloaded to the cloud */
					else {
						Message tempc = host.getStorageSystem().getNewestProcessMessage();
						host.getStorageSystem().addToDeplUnProcMessages(tempc);
						System.out.println("Request is: "+request+" and maxReq is "+this.maxReq+ " and current BW: "+deplBW);
					}
				}
				else {
					if (host.getStorageSystem().getOldestStaticMessage(true) != null){
						Message temp = host.getStorageSystem().getOldestStaticMessage(true);
						//System.out.println("The message to be deleted is "+this.msgNo+" from host "+host.name.toString());
						host.getStorageSystem().addToDeplStaticMessages(temp);
						host.getStorageSystem().deleteStaticMessage(temp.getId());
						//sdepleted += 1;
					}
					else if (host.getStorageSystem().getOldestStaticMessage(false) != null){
						Message temp = host.getStorageSystem().getOldestStaticMessage(false);
						//System.out.println("The message to be deleted is "+this.msgNo+" from host "+host.name.toString());
						host.getStorageSystem().addToDeplUnProcMessages(temp);
						host.getStorageSystem().deleteStaticMessage(temp.getId());
						//sdepleted += 1;
					}
					else if (!host.getStorageSystem().isProcessedEmpty()) {
						
							Message temp = host.getStorageSystem().getOldestProcessedMessage();
							host.getStorageSystem().deleteProcessedMessage(temp.getId());
							
							if (host.getStorageSystem().getOldestProcessMessage(true) != null && 
								(!host.getStorageSystem().isProcessedFull())) {
								Message tempp = host.getStorageSystem().getNewestProcessMessage();
								double delayed = (double)tempp.getProperty("delay");
								if (curTime - this.lastProc >= delayed) {
									if (!host.getStorageSystem().isProcessingEmpty() && 
										!host.getStorageSystem().isProcessedFull() ) {
										host.getStorageSystem().processMessage(tempp);
										host.getStorageSystem().addToCachedMessagesNo(1);
									}
								}
							}
							//System.out.println(curTime + ": The message was deleted at: "+host.name.toString());
							//pdepleted += 1;
					}
				}
				deplBW = host.getStorageSystem().getDepletedProcMessagesBW(false, true) + 
						host.getStorageSystem().getDepletedUnProcMessagesBW(false, true) + 
						host.getStorageSystem().getDepletedPUnProcMessagesBW(false, true) + 
						host.getStorageSystem().getDepletedStaticMessagesBW(false, true);
				//System.out.println("Depletion is at: "+ deplBW);
			}
			deplBW = host.getStorageSystem().getDepletedProcMessagesBW(true, true) + 
					host.getStorageSystem().getDepletedUnProcMessagesBW(true, true) + 
					host.getStorageSystem().getDepletedPUnProcMessagesBW(true, true) + 
					host.getStorageSystem().getDepletedStaticMessagesBW(true, true);
			this.lastDepl = curTime;
			//System.out.println("Depleted processed messages: "+ pdepleted);
			//System.out.println("Depleted static messages: "+ sdepleted);
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
	 * @return the lastProc
	 */
	public double getLastDepl() {
		return lastDepl;
	}
	
	/**
	 * @return the lastProc
	 */
	public void setLastDepl(double lastDepl) {
		this.lastDepl = lastDepl;
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
	 * @return depletion rate
	 */
	public long getDeplRate() {
		return depl_rate;
	}

	/**
	 * @return depletion rate
	 */
	public double getDeplReq() {
		return maxReq;
	}

	/**
	 * @return n. of processing cores
	 */
	public int getProcRate() {
		return proc_rate;
	}

}
