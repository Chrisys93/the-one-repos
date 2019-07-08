/* 
 * 2018 UCL
 * Author: Adrian-Cristian Nicolaescu
 */

package applications;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import core.Application;
import core.DTNHost;
import core.Message;
import core.Settings;
import core.SimClock;


/**
 * TODO: CHECK HOW BUFFER INTERFERES WITH MESSAGE RECEPTION INTO STORAGE!
 * (if buffer is full, repo might refuse connections and messages! - buffer 
 * should basically be avoided by most messages, and only used if absolutely
 * necessary!)
 */

/**
 * TODO:
 * SHOULD RETAIN DATA REGARDLESS OF WHETHER PROCESSED OR NOT.
 * IF PROCESSED AND STORAGE FULL, DELETE;
 * IF NON-PROCESSING AND STORAGE FULL, OFFLOAD TO CLOUD.
 */


/**
 * Processing application, implemented on each of the repositories, for
 * processing/storing and depleting all incoming messages, according to 
 * the scope and age of each message.
 */
public class ProcApplication extends Application {
	/** Run in passive mode - don't process messages, but store */
	public static final String PROC_PASSIVE = "passive";
	/** Percentage (below unity) of maximum storage occupied */
	public static final String MAX_STOR = "maxStorage";
	/** Depletion rate */
	public static final String DEPL_RATE = "depletionRate";
	/** Numbers of processing cores */
	public static final String PROC_NO = "coreNo";
	
	/** Application ID */
	public static final String APP_ID = "ProcApplication";
	
	// Private vars
	private double	lastProc = 0;
	private double 	lastDepl = 0;
	private double 	lastCheck = 0;
	private double 	procMin = 0;	
	
	private boolean passive = false;
	private long	max_stor = (long) 0.9;
	private long	min_stor = (long) 0.1;
	private long 	depl_rate = 0;
	private long	deplBW = 0;
	private long	servBW = 0;
	private long	cloudBW = 0;	
	private int		proc_rate = 4;
	private int		procMinI = 0;
	//private int 	processedSize = (int) (procSize*proc_ratio);
	protected ArrayList<Double> procEndTimes = new ArrayList<Double>();
	
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
		if (s.contains(MAX_STOR)){
			this.max_stor = s.getLong(MAX_STOR);
		}
		if (s.contains(PROC_NO)){
			int m = s.getInt(PROC_NO);
			for (int n=0; n<m; n++)
			{
				this.procEndTimes.add((double) 0);
			} 
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
		this.lastDepl = a.getLastDepl();
		this.passive = a.isPassive();
		this.depl_rate = a.getDeplRate();
		this.proc_rate = a.getProcRate();
		for (int n=0; n<4; n++)
		{
			this.procEndTimes.add((double) 0);
		}
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
	
			//System.out.println("handle is accessed on host: " + host);

			host.getStorageSystem().addToStoredMessages(msg);
			
			this.getProcMin();
			
			
			/**
			 * TODO:
			 * Add "processed" flag and "send to customer" - separate BW association - need to
			 * change processMessage(msg) to do this in RepoStorage, to change the message "destination" and 
			 * not to delete the message from storage.
			 */	
			if (type.equalsIgnoreCase("proc")) {				
				if ((!host.getStorageSystem().isProcessingEmpty()) && (!host.getStorageSystem().isProcessedFull())) {
					double delayed = (double)msg.getProperty("delay");
					if (curTime - this.procMin >= delayed) {
						host.getStorageSystem().processMessage(msg);
						tempp.addProperty("Fresh", true);
						this.procEndTimes.set(this.procMinI, this.procMin + delayed);
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
		
		this.getProcMin();
		
		/**
		 * Processing older messages, that could not be processed as soon as
		 * accepted, for any reason.
		 */
		
		this.getProcMin();
		while (host.getStorageSystem().getOldestProcessMessage() != null) {
			
			this.getProcMin();
			
			Message tempp = host.getStorageSystem().getOldestValidProcessMessage();
			double delayed = (double)tempp.getProperty("delay");
			double temppFresh = (double)tempp.getProperty("freshPer");
			double temppShelf = (double)tempp.getProperty("shelfLife");
			
			if(curTime-this.procMin+(double)host.getStorageSystem().getOldestValidProcessMessage().getProperty("delay") <= 
				(double)host.getStorageSystem().getOldestValidProcessMessage().getProperty("shelfLife")) {
	
				if (curTime-this.procMin+delayed <= temppFresh - (curTime-tempp.getReceiveTime())) {
					if ((!host.getStorageSystem().isProcessingEmpty()) && (!host.getStorageSystem().isProcessedFull())) {
						if (curTime - this.procMin >= delayed) {
							host.getStorageSystem().processMessage(tempp);
							tempp.addProperty("Fresh", true);
							this.procEndTimes.set(this.procMinI, this.procMin + delayed);
						}
					}
				}
			}
					
			else if (curTime-this.procMin+delayed <= temppShelf - (curTime-tempp.getReceiveTime())) {
			
			/**
			 * TODO:
			 * Add "processed" flag and "send to customer" - separate BW association - need to
			 * change processMessage(msg) to do this in RepoStorage, to change the message "destination" and 
			 * not to delete the message from storage.
			 * 
			 * The processMessage method of RepoStorage also needs to move the message with the (now) "processed"
			 * flag to ANOTHER space (yes, maybe make the cache dynamic as well). 
			 * 
			 *The "processed" flag could also maintain a processing count (as it's a key-value pair)...but this 
			 *is for later work.
			 */	
				if (!host.getStorageSystem().isProcessingEmpty() && !host.getStorageSystem().isProcessedFull()) {
					if (curTime - this.procMin >= delayed) {
						host.getStorageSystem().processMessage(tempp);
						tempp.addProperty("Fresh", false);
						this.procEndTimes.set(this.procMinI, this.procMin + delayed);
					}
				}
			
			}
			
			else {
				String mID = host.getStorageSystem().compressMessage(tempp);
				host.getStorageSystem().addToDeplUnProcMessages(mID);
			}
		}

		/**
		 * TODO:
		 * DEPLETION PART HERE
		 *
		 * Depletion has to be done in the following order, and ONLY WHEN STORAGE IS FULL; up to a certain lower limit!
		 * non-processing with shelf-life expired, non-processing with shelf-life, processing with shelf-life expired, 
		 * processing with shelf-life
		 * 
		 * A different depletion pipeline must be made, so that on each update, the processed messages are also sent through,
		 * but this should be limited by a specific namespace/interface limit per second...
		 * 
		 * With these, messages also have to be tagged (tags added HERE, at depletion) with: storage time, for shelf-life 
		 * correlations, in-time processing tags, for analytics, overtime boolean tags, shelf-life processing confirmation 
		 * boolean tags
		 * The other tags should be deleted AS THE MESSAGES ARE PROCESSED/COMPRESSED/DELETED!
		 */
		
		if (curTime - this.lastDepl >= 1) {
			this.deplBW = 0;
			this.servBW = 0;
			this.cloudBW = 0;
		}
		
		/**
		 * Here, the messages which are processed should be sent to their destinations (servBW),
		 * and the ones which have expired shelf-life tags should be sent to the cloud (cloudBW) 
		 */
		
		if((host.getStorageSystem().getProcessedMessagesSize() + host.getStorageSystem().getStaticMessagesSize()) > this.min_stor) {
			if (host.getStorageSystem().getOldestStaticMessage() != null){
				Message temp = host.getStorageSystem().getOldestStaticMessage();
				//System.out.println("The message to be deleted is "+this.msgNo+" from host "+host.name.toString());
				host.getStorageSystem().addToDeplStaticMessages(temp);
				host.getStorageSystem().deleteStaticMessage(temp.getId());
				//sdepleted += 1;
			}
			else if (!host.getStorageSystem().isProcessedEmpty()) {
					Message temp = host.getStorageSystem().getOldestProcessedMessage();
					host.getStorageSystem().deleteProcessedMessage(temp.getId());
					if (host.getStorageSystem().getOldestProcessMessage() != null && (!host.getStorageSystem().isProcessedFull())) {
						Message tempp = host.getStorageSystem().getNewestProcessMessage();
						double delayed = (double)tempp.getProperty("delay");
						if (curTime - this.lastProc >= delayed) {
							if (!host.getStorageSystem().isProcessingEmpty() && 
								!host.getStorageSystem().isProcessedFull() ) {
								host.getStorageSystem().processMessage(tempp);
							}
							this.lastProc = curTime;
						}
					}
					//System.out.println(curTime + ": The message was deleted at: "+host.name.toString());
					//pdepleted += 1;
			}
			//Revise:
			servBW = host.getStorageSystem().getDepletedProcMessagesBW(false) + host.getStorageSystem().getDepletedUnProcMessagesBW(false) + host.getStorageSystem().getDepletedStaticMessagesBW(false);
			cloudBW = host.getStorageSystem().getDepletedProcMessagesBW(false) + host.getStorageSystem().getDepletedUnProcMessagesBW(false) + host.getStorageSystem().getDepletedStaticMessagesBW(false);
			//System.out.println("Depletion is at: "+ deplBW);
			this.lastDepl = curTime;
			//System.out.println("Depleted processed messages: "+ pdepleted);
			//System.out.println("Depleted static messages: "+ sdepleted);
		}
		
		/**
		 * This can only be entered if forced depletion has to happen (there are no other options), 
		 * and all the messages are making the storage overflow.
		 */
		
		if (deplBW<this.depl_rate && ((host.getStorageSystem().getProcessedMessagesSize() + host.getStorageSystem().getStaticMessagesSize()) > this.depl_rate)) {
			/* 
			 * In order to make the system (kind of) fair, we want to make sure that it does not get overflowed 
			 * by static messages and processed messages are not depleted, past a point, and neither the other
			 * way around (having the cloud off-loading as a solution)
			 */
			if (host.getStorageSystem().getStaticMessagesSize() + host.getStorageSystem().getProcMessagesSize() >= (host.getStorageSystem().getTotalStorageSpace()*this.max_stor) ) {
				/* 
				 * Oldest processed message is depleted (as a FIFO type of storage,
				 * and a new message for processing is processed
				 */
				if (!host.getStorageSystem().isProcessedEmpty()) {
					Message temp = host.getStorageSystem().getOldestProcessedMessage();
					host.getStorageSystem().deleteProcessedMessage(temp.getId());
					if (host.getStorageSystem().getOldestProcessMessage() != null && (!host.getStorageSystem().isProcessedFull())) {
						Message tempp = host.getStorageSystem().getNewestProcessMessage();
						double delayed = (double)tempp.getProperty("delay");
						if (curTime - this.lastProc >= delayed) {
							if (!host.getStorageSystem().isProcessingEmpty() && 
								!host.getStorageSystem().isProcessedFull() ) {
								host.getStorageSystem().processMessage(tempp);
							}
							this.lastProc = curTime;
						}
					}
				}
				/* Oldest unprocessed message is depleted (as a FIFO type of storage) */
				else if (host.getStorageSystem().getOldestStaticMessage() != null){
					Message temp = host.getStorageSystem().getOldestStaticMessage();
					host.getStorageSystem().addToDeplStaticMessages(temp);
					host.getStorageSystem().deleteStaticMessage(temp.getId());
				}
				/* Message to be processed is offloaded to the cloud */
				else if(host.getStorageSystem().getProcessedMessagesSize() > (host.getStorageSystem().getTotalProcessedSpace() - 2000000)) {
					Message tempc = host.getStorageSystem().getNewestProcessMessage();
					host.getStorageSystem().addToDeplUnProcMessages(tempc);
				}
			}
			//Revise:
			deplBW = host.getStorageSystem().getDepletedProcMessagesBW(false) + host.getStorageSystem().getDepletedUnProcMessagesBW(false) + host.getStorageSystem().getDepletedStaticMessagesBW(false);
			//System.out.println("Depletion is at: "+ deplBW);
			this.lastDepl = curTime;
			//System.out.println("Depleted processed messages: "+ pdepleted);
			//System.out.println("Depleted static messages: "+ sdepleted);
		}
	}
	
	public void getProcMin() {
		for (int i = 0; i<this.procEndTimes.size()-2; i++)
			if (i == 0)
				if(this.procEndTimes.get(i)<this.procEndTimes.get(this.procEndTimes.size()-1)) {
					this.procMin = (double)this.procEndTimes.get(i);
					this.procMinI = i;
				}
			else if (this.procEndTimes.get(i)<this.procEndTimes.get(i+1)) {
				this.procMin = (double)this.procEndTimes.get(i);
				this.procMinI = i;
			}
			else {
				this.procMin = (double)this.procEndTimes.get(i+1);
				this.procMinI = i+1;
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
	 * @return n. of processing cores
	 */
	public int getProcRate() {
		return proc_rate;
	}

}
