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
	/** Percentage (below unity) of minimum storage occupied before any depletion */
	public static final String MIN_STOR = "minStorage";
	/** Depletion rate */
	public static final String DEPL_RATE = "depletionRate";
	/** Cloud Max Update rate */
	public static final String CLOUD = "cloudRate";
	/** Numbers of processing cores */
	public static final String PROC_NO = "coreNo";
	
	/** Application ID */
	public static final String APP_ID = "ProcApplication";
	
	// Private vars
	private double	lastProc = 0;
	private double 	lastDepl = 0;
	private double 	procMin = 0;	
	
	private boolean passive = false;
	private double	max_stor = (long) 0.9;
	private double	min_stor = (long) 0.001;
	private long 	depl_rate = 0;
	private long 	cloud_lim = 0;
	private long	deplBW = 0;
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
		if (s.contains(CLOUD)){
			this.cloud_lim = s.getLong(CLOUD);
		}
		if (s.contains(MAX_STOR)){
			this.max_stor = s.getDouble(MAX_STOR);
		}
		if (s.contains(MIN_STOR)){
			this.min_stor = s.getDouble(MIN_STOR);
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
		this.lastProc 	= a.getLastProc();
		this.lastDepl 	= a.getLastDepl();
		this.passive 	= a.isPassive();
		this.depl_rate 	= a.getDeplRate();
		this.cloud_lim 	= a.getCloudLim();
		this.proc_rate 	= a.getProcRate();
		this.max_stor 	= a.getMaxStor();
		this.min_stor 	= a.getMinStor();
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
						msg.addProperty("Fresh", true);
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
		 * accepted, for any reason. Processing is a separate process, inside
		 * the EDRs and does not interfere with compression or offloading.
		 */
		
		this.getProcMin();
		while (host.getStorageSystem().getOldestProcessMessage() != null) {
			
			this.getProcMin();
			
			Message tempp = host.getStorageSystem().getOldestValidProcessMessage();
			double delayed = (double)tempp.getProperty("delay");
			double temppFresh = (double)tempp.getProperty("freshPer");
			double temppShelf = (double)tempp.getProperty("shelfLife");
			
			if(curTime-this.procMin+delayed <= temppShelf) {
	
				if (curTime-this.procMin+delayed <= temppFresh - (curTime-tempp.getReceiveTime())) {
					if ((!host.getStorageSystem().isProcessingEmpty()) && (!host.getStorageSystem().isProcessedFull())) {
						if (curTime - this.procMin >= delayed) {
							host.getStorageSystem().processMessage(tempp);
							tempp.addProperty("Fresh", true);
							this.procEndTimes.set(this.procMinI, this.procMin + delayed);
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
			}
			
			else {
				if((Boolean)tempp.getProperty("comp") == false) {
					host.getStorageSystem().deleteMessage(tempp.getId());
				}
				else {
					String mID = host.getStorageSystem().compressMessage(tempp);
					host.getStorageSystem().addToDeplUnProcMessages(mID);
				}
			}
		}

		/**
		 * TODO:
		 * DEPLETION PART HERE
		 *
		 * Depletion has to be done in the following order, and ONLY WHEN STORAGE IS FULL; up to a certain lower limit!
		 * non-processing with shelf-life expired, processing with shelf-life expired, non-processing with shelf-life, 
		 * processing with shelf-life
		 * 
		 * A different depletion pipeline must be made, so that on each update, the processed messages are also sent through,
		 * but this should be limited by a specific namespace/interface limit per second...
		 * 
		 * With these, messages also have to be tagged (tags added HERE, at depletion) with: storage time, for shelf-life 
		 * correlations, in-time processing tags, for analytics, overtime boolean tags, shelf-life processing confirmation 
		 * boolean tags
		 * The other tags should be deleted AS THE MESSAGES ARE PROCESSED/COMPRESSED/DELETED!
		 * 
		 */
		
		if (curTime - this.lastDepl >= 1) {
			this.deplBW = 0;
			this.cloudBW = 0;
		}
		
		/**
		 * Here, the messages which are processed should be sent to their destinations (supposing 
		 * that they are in the cloud, still going to account for them in cloudBW), and the ones 
		 * which have expired shelf-life tags should be sent to the cloud (cloudBW), no questions asked.
		 * 
		 * Content, in order of offloading preference - to customers: Fresh messages, Stale messages
		 * Content, in order of offloading preference - to cloud (all to be compressed): Stale unprocessed 
		 * messages, Stale non-processing messages
		 * 
		 * All added up to a maximum cloud BW (should be pretty high)
		 * 
		 * NEED TO SORT OUT HOW TO KEEP TRACK OF "storTime" TAGS IN RepoStorage!
		 * 
		 * Need to add compression delays
		 * 
		 */
		
		while(this.cloudBW<this.cloud_lim && host.getStorageSystem().getProcessedMessagesSize()+ 
				host.getStorageSystem().getProcMessagesSize() + host.getStorageSystem().getStaticMessagesSize() > 
				host.getStorageSystem().getTotalStorageSpace()*this.min_stor) {
			/* 
			 * Oldest processed message is depleted (as a FIFO type of storage,
			 * and a new message for processing is processed
			 */
			if (!host.getStorageSystem().isProcessedEmpty()) {
				if (host.getStorageSystem().getOldestFreshMessage() != null &&
					curTime - host.getStorageSystem().getOldestFreshMessage().getReceiveTime() >= 
					(double)host.getStorageSystem().getOldestFreshMessage().getProperty("shelfLife")) {
					Message temp = host.getStorageSystem().getOldestFreshMessage();
					/**
					 * Make sure here that the added message to the cloud depletion
					 * tracking is also tracked by whether it's Fresh or Stale.
					 */
					double storTime = curTime - temp.getReceiveTime();
					temp.addProperty("storTime", storTime);
					temp.addProperty("satisfied", true);
					if (storTime == (double)temp.getProperty("shelfLife")) {
						temp.addProperty("overtime", false);
					}
					else if (storTime > (double)temp.getProperty("shelfLife")) {
						temp.addProperty("overtime", true);
					}
					host.getStorageSystem().deleteProcessedMessage(temp.getId());
				}
				else if (host.getStorageSystem().getOldestShelfMessage() != null &&
						curTime - host.getStorageSystem().getOldestShelfMessage().getReceiveTime() >= 
						(double)host.getStorageSystem().getOldestShelfMessage().getProperty("shelfLife")) {
					
					Message temp = host.getStorageSystem().getOldestShelfMessage();
					/**
					 * Make sure here that the added message to the cloud depletion
					 * tracking is also tracked by whether it's Fresh or Stale.
					 */
					double storTime = curTime - temp.getReceiveTime();
					temp.addProperty("storTime", storTime);
					temp.addProperty("satisfied", false);
					if (storTime == (double)temp.getProperty("shelfLife")) {
						temp.addProperty("overtime", false);
					}
					else if (storTime > (double)temp.getProperty("shelfLife")) {
						temp.addProperty("overtime", true);
					}
					host.getStorageSystem().deleteProcessedMessage(temp.getId());
				}
			}
			
			/* Oldest unprocessed message is depleted (as a FIFO type of storage) */
			else if (host.getStorageSystem().getOldestStaleStaticMessage() != null){
				Message temp = host.getStorageSystem().getOldestStaleStaticMessage();
				if((Boolean)temp.getProperty("comp") == false) {
					host.getStorageSystem().deleteMessage(temp.getId());
				}
				else {
					String tempc = host.getStorageSystem().compressMessage(temp);
					Message ctemp = host.getStorageSystem().getStaticMessage(tempc);
					double storTime = curTime - ctemp.getReceiveTime();
					ctemp.addProperty("storTime", storTime);
					ctemp.addProperty("satisfied", true);
					if (storTime == (double)ctemp.getProperty("shelfLife")) {
						ctemp.addProperty("overtime", false);
					}
					else if (storTime > (double)ctemp.getProperty("shelfLife")) {
						ctemp.addProperty("overtime", true);
					}
					host.getStorageSystem().addToCloudDeplStaticMessages(ctemp);
					host.getStorageSystem().deleteMessage(tempc);
				}
			}
			//Revise:
			cloudBW = host.getStorageSystem().getDepletedCloudProcMessagesBW(false) + 
					host.getStorageSystem().getDepletedUnProcMessagesBW(false) + 
					host.getStorageSystem().getDepletedCloudStaticMessagesBW(false);
			//System.out.println("Depletion is at: "+ deplBW);
			this.lastDepl = curTime;
			//System.out.println("Depleted processed messages: "+ pdepleted);
			//System.out.println("Depleted static messages: "+ sdepleted);
		}
		
		/**
		 * This can only be entered if forced depletion has to happen (there are no other options), 
		 * and all the messages are making the storage overflow. No questions asked.
		 * 
		 * Content, in order of offloading preference - to cloud (all to be compressed): depletion-destined 
		 * unprocessed messages, stale unprocessed messages, Stale non-processing messages, if needed,
		 * oldest non-processing messages, regardless of satisfaction.
		 * 
		 * All added up to a maximum depletion BW (should be pretty low)
		 */
		
		while (this.deplBW<this.depl_rate && ((host.getStorageSystem().getProcessedMessagesSize() + 
				host.getStorageSystem().getStaticMessagesSize()) > host.getStorageSystem().getTotalStorageSpace()*this.max_stor)) {
			
			if(host.getStorageSystem().getOldestDeplUnProcMessage() != null) {
				Message temp = host.getStorageSystem().getOldestDeplUnProcMessage();
				double storTime = curTime - temp.getReceiveTime();
				temp.addProperty("storTime", storTime);
				if (storTime > (double)temp.getProperty("shelfLife")) {
					temp.addProperty("overtime", true);
				}
				else {
					temp.addProperty("overtime", false);
				}
				host.getStorageSystem().addToDepletedUnProcMessages(temp);
				host.getStorageSystem().deleteMessage(temp.getId());
			}
			
			else if(host.getStorageSystem().getOldestInvalidProcessMessage() != null) {
				Message temp = host.getStorageSystem().getOldestDeplUnProcMessage();
				String tempc = host.getStorageSystem().compressMessage(temp);
				Message ctemp = host.getStorageSystem().getStaticMessage(tempc);
				double storTime = curTime - ctemp.getReceiveTime();
				ctemp.addProperty("storTime", storTime);
				ctemp.addProperty("satisfied", true);
				if (storTime == (double)ctemp.getProperty("shelfLife")) {
					ctemp.addProperty("overtime", false);
				}
				else if (storTime > (double)ctemp.getProperty("shelfLife")) {
					ctemp.addProperty("overtime", true);
				}
				else {
					ctemp.addProperty("overtime", true);
				}
				host.getStorageSystem().addToCloudDeplStaticMessages(ctemp);
				host.getStorageSystem().deleteMessage(tempc);
			}
			/* Oldest unprocessed message is depleted (as a FIFO type of storage) */
			else if (host.getStorageSystem().getOldestStaleStaticMessage() != null){
				Message temp = host.getStorageSystem().getOldestStaleStaticMessage();
				if((Boolean)temp.getProperty("comp") == false) {
					host.getStorageSystem().deleteMessage(temp.getId());
				}
				else {
					String tempc = host.getStorageSystem().compressMessage(temp);
					Message ctemp = host.getStorageSystem().getStaticMessage(tempc);
					double storTime = curTime - ctemp.getReceiveTime();
					ctemp.addProperty("storTime", storTime);
					ctemp.addProperty("satisfied", true);
					if (storTime == (double)ctemp.getProperty("shelfLife")) {
						ctemp.addProperty("overtime", false);
					}
					else if (storTime > (double)ctemp.getProperty("shelfLife")) {
						ctemp.addProperty("overtime", true);
					}
					host.getStorageSystem().addToDeplStaticMessages(ctemp);
					host.getStorageSystem().deleteMessage(tempc);
				}
			}
			else if (host.getStorageSystem().getOldestStaticMessage() != null){
				Message temp = host.getStorageSystem().getOldestStaticMessage();
				if((Boolean)temp.getProperty("comp") == false) {
					host.getStorageSystem().deleteMessage(temp.getId());
				}
				else {
					String tempc = host.getStorageSystem().compressMessage(temp);
					Message ctemp = host.getStorageSystem().getStaticMessage(tempc);
					double storTime = curTime - ctemp.getReceiveTime();
					ctemp.addProperty("storTime", storTime);
					ctemp.addProperty("satisfied", false);
					ctemp.addProperty("overtime", true);
					host.getStorageSystem().addToDeplStaticMessages(ctemp);
					host.getStorageSystem().deleteMessage(tempc);
				}
			}
			else if (host.getStorageSystem().getNewestProcessMessage() != null){
				Message temp = host.getStorageSystem().getNewestProcessMessage();
				String tempc = host.getStorageSystem().compressMessage(temp);
				Message ctemp = host.getStorageSystem().getStaticMessage(tempc);
				double storTime = curTime - ctemp.getReceiveTime();
				ctemp.addProperty("storTime", storTime);
				ctemp.addProperty("satisfied", false);
				ctemp.addProperty("overtime", true);
				host.getStorageSystem().addToDeplProcMessages(ctemp);
				host.getStorageSystem().deleteMessage(tempc);
			}
			//Revise:
			deplBW = host.getStorageSystem().getDepletedProcMessagesBW(false) + 
					host.getStorageSystem().getDepletedUnProcMessagesBW(false) + 
					host.getStorageSystem().getDepletedStaticMessagesBW(false);
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
	public double getMaxStor() {
		return max_stor;
	}

	/**
	 * @return depletion rate
	 */
	public double getMinStor() {
		return min_stor;
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
	public long getCloudLim() {
		return cloud_lim;
	}

	/**
	 * @return n. of processing cores
	 */
	public int getProcRate() {
		return proc_rate;
	}

}
