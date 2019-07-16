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

	private boolean cloudEmptyLoop = true;
	private boolean deplEmptyLoop = true;
	private boolean passive = false;
	private double	max_stor;
	private double	min_stor;
	private long 	depl_rate;
	private long 	cloud_lim;
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
		this.procEndTimes = a.getProcEndTimes();
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
	
			//System.out.println("handle is accessed on host: " + host);

			host.getStorageSystem().addToStoredMessages(msg);
			this.getProcMin();
			
			/**
			 * TODO:
			 * Add "processed" flag and "send to customer" - separate BW association - need to
			 * change processMessage(msg) to do this in RepoStorage, to change the message "destination" and 
			 * not to delete the message from storage.
			 */	
			processNewestValidMessage(host);
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
		 * Processing older messages, that could not be processed as soon as
		 * accepted, for any reason. Processing is a separate process, inside
		 * the EDRs and does not interfere with compression or offloading.
		 *
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

		processOldestValidMessage(host);

		/**
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
!!!!!!!!!!!MAKE SURE "PROCESS" MESSAGES ARE DELETED EITHER AFTER USE OR IN DUE TIME!!!!!!!!!!!!!!!!!!!!!!!!!
		 * 
		 */
		this.updateCloudBW(host);
		this.deplCloud(host);
		
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

		//this.updateDeplBW(host);
		//this.deplStorage(host);

	}
	
	public void getProcMin() {
		double curTime = SimClock.getTime();
		this.procMin = 0;
		this.procMinI = 0;
		for (int i = 0; i<this.procEndTimes.size()-1; i++) {
			if (i == 0)
				if(this.procEndTimes.get(i)<this.procEndTimes.get(this.procEndTimes.size()-1)) {
					this.procMin = (double)this.procEndTimes.get(i);
					this.procMinI = i;
				}
				else {
					this.procMin = (double)this.procEndTimes.get(this.procEndTimes.size()-1);
					this.procMinI = this.procEndTimes.size()-1;
				}
			else if (this.procEndTimes.get(i)<this.procMin) {
				this.procMin = (double)this.procEndTimes.get(i);
				this.procMinI = i;
			}
		}
		if (this.procMin == 0)
			this.procMin = curTime;
	}
	
	public boolean processOldestValidMessage(DTNHost host) {
		boolean ans = false;
		if (host.getStorageSystem().getOldestValidProcessMessage() != null && !host.getStorageSystem().isProcessedFull()) {
			double curTime = SimClock.getTime();
			this.getProcMin();
			
			Message tempp = host.getStorageSystem().getOldestValidProcessMessage();
			double delayed = (double)tempp.getProperty("delay");
			double temppFresh = (double)tempp.getProperty("freshPer");
			double temppShelf = (double)tempp.getProperty("shelfLife");
			
			if(this.procMin-curTime+delayed <= temppShelf) {
				
				if (this.procMin-curTime+delayed <= tempp.getReceiveTime() - temppFresh + curTime) {
					if (!host.getStorageSystem().isProcessedFull()) {
						tempp.addProperty("Fresh", true);
						this.processMessage(host, tempp);
						this.procEndTimes.set(this.procMinI, this.procMin + delayed);
						ans = true;
					}
				}
					
				else if (this.procMin-curTime+delayed <= tempp.getReceiveTime() - temppShelf + curTime && 
						tempp.getProperty("Fresh") == null) {
					if (!host.getStorageSystem().isProcessedFull()) {
						tempp.addProperty("Fresh", false);
						this.processMessage(host, tempp);
						this.procEndTimes.set(this.procMinI, this.procMin + delayed);
						ans = true;
					}
				}
				ans = true;
			}
			
			else {
				if(tempp.getProperty("comp") != null) {
					if(!(Boolean)tempp.getProperty("comp")) {
						host.getStorageSystem().deleteMessage(tempp.getId());
					}
					else {
						Message m = this.compressMessage(host, tempp);
						host.getStorageSystem().addToDeplUnProcMessages(m.getId());
					}
				}
			}
		}
		return ans;
	}
	

	
	public boolean processNewestValidMessage(DTNHost host) {
		boolean ans = false;
		if (host.getStorageSystem().getNewestProcessMessage() != null && !host.getStorageSystem().isProcessedFull()) {
			double curTime = SimClock.getTime();
			this.getProcMin();
			
			Message tempp = host.getStorageSystem().getNewestProcessMessage();
			double delayed = (double)tempp.getProperty("delay");
			double temppFresh = (double)tempp.getProperty("freshPer");
			double temppShelf = (double)tempp.getProperty("shelfLife");
			
			if(this.procMin-curTime+delayed <= temppShelf) {
	
				if (this.procMin-curTime+delayed <= temppFresh - (curTime-tempp.getReceiveTime())) {
					if (!host.getStorageSystem().isProcessedFull()) {
						tempp.addProperty("Fresh", true);
						this.processMessage(host, tempp);
						this.procEndTimes.set(this.procMinI, this.procMin + delayed);
						ans = true;
					}
				}
					
				else if (this.procMin-curTime+delayed <= temppShelf - (curTime-tempp.getReceiveTime()) && 
						tempp.getProperty("Fresh") == null) {
					if (!host.getStorageSystem().isProcessedFull()) {
						tempp.addProperty("Fresh", false);
						this.processMessage(host, tempp);
						this.procEndTimes.set(this.procMinI, this.procMin + delayed);
						ans = true;
					}
				}
			}
			
			else {
				if(tempp.getProperty("comp") != null) {
					if(!(Boolean)tempp.getProperty("comp")) {
						host.getStorageSystem().deleteMessage(tempp.getId());
					}
					else {
						Message m = this.compressMessage(host, tempp);
						host.getStorageSystem().addToDeplUnProcMessages(m.getId());
					}
				}
			}
		}
		return ans;
	}
	
	public boolean processMessage(DTNHost host, Message procMessage) {
		host.getStorageSystem().deleteProcMessage(procMessage.getId());
		int initsize = procMessage.getSize();
		int processedsize = (int) (initsize/(2*host.getStorageSystem().getCompressionRate()));
		Message processedMessage = new Message(procMessage.getFrom(), procMessage.getTo(), procMessage.getId(), processedsize);
		processedMessage.copyFrom(procMessage);
		processedMessage.setReceiveTime(procMessage.getReceiveTime());
		processedMessage.updateProperty("type", "processed");
		procMessage.updateProperty("type", "nonproc");
		host.getStorageSystem().addToStoredMessages(processedMessage);
		
		if((Boolean)procMessage.getProperty("comp"))
			host.getStorageSystem().addToStoredMessages(procMessage);
		
		return true;
	}
	
	public Message compressMessage(DTNHost host, Message compMessage) {
		if ((Boolean)compMessage.getProperty("comp")) {
			int initsize = compMessage.getSize();
			int processedsize = (int) (initsize/ host.getStorageSystem().getCompressionRate());
			Message compressedMessage = new Message(compMessage.getFrom(), compMessage.getTo(), compMessage.getId(), processedsize);
			compressedMessage.copyFrom(compMessage);
			compressedMessage.setReceiveTime(compMessage.getReceiveTime());
			compressedMessage.updateProperty("comp", null);
			//compressedMessage.updateProperty("type", "nonproc");
			host.getStorageSystem().deleteMessage(compMessage.getId());
			//host.getStorageSystem().addToStoredMessages(compressedMessage);
			return compressedMessage;
		}
		else
			return null;
	}
	
	
	public void updateCloudBW(DTNHost host) {
		this.cloudBW = host.getStorageSystem().getDepletedCloudProcMessagesBW(false) + 
				host.getStorageSystem().getDepletedUnProcMessagesBW(false) + 
				host.getStorageSystem().getDepletedCloudStaticMessagesBW(false);
	}
	
	public void updateDeplBW(DTNHost host) {
	this.deplBW = host.getStorageSystem().getDepletedProcMessagesBW(false) + 
			host.getStorageSystem().getDepletedUnProcMessagesBW(false) + 
			host.getStorageSystem().getDepletedStaticMessagesBW(false);
	}
	
	public void deplCloud(DTNHost host) {
		double curTime = SimClock.getTime();
		if (host.getStorageSystem().getProcessedMessagesSize()+
				host.getStorageSystem().getStaleStaticMessagesSize() > 
				(long)(host.getStorageSystem().getTotalStorageSpace()*this.min_stor)){
			this.cloudEmptyLoop = true;
			for (int i = 0; this.cloudBW<this.cloud_lim && this.cloudEmptyLoop && i<50; i++) {
				/* 
				 * Oldest processed message is depleted (as a FIFO type of storage,
				 * and a new message for processing is processed
				 */

				if (!host.getStorageSystem().isProcessedEmpty()) {
					this.processedDepletion(host);
				}
				
				/* Oldest unprocessed message is depleted (as a FIFO type of storage) */
				else if (host.getStorageSystem().getOldestStaleStaticMessage() != null){
					oldestSatisfiedStaticDepletion(host);
				}
				/*else if(host.getStorageSystem().getOldestDeplUnProcMessage() != null) {
					oldestUnProcDepletion(host);
				}*/
				else {
					this.cloudEmptyLoop = false;
					//System.out.println("Depletion is at: "+ this.cloudBW);
				}
				//Revise:
				this.updateCloudBW(host);
				//System.out.println("Depletion is at: "+ deplBW);
				this.lastDepl = curTime;
				/*System.out.println("this.cloudBW is at " + 
						this.cloudBW +
						" this.cloud_lim is at " + 
						this.cloud_lim +
						" host.getStorageSystem().getTotalStorageSpace()*this.min_stor equal "+
						(long)(host.getStorageSystem().getTotalStorageSpace()*this.min_stor)+
						" Total space is "+host.getStorageSystem().getTotalStorageSpace());*/
				//System.out.println("Depleted static messages: "+ sdepleted);
			}
		}
		else if (host.getStorageSystem().getProcessedMessagesSize()+
				host.getStorageSystem().getStaleStaticMessagesSize() > 
				(long)(host.getStorageSystem().getTotalStorageSpace()*this.max_stor)){
			this.cloudEmptyLoop = true;
			for (int i = 0; this.cloudBW<this.cloud_lim && this.cloudEmptyLoop && i<50; i++) {
				
				/* Oldest unprocessed message is depleted (as a FIFO type of storage) */

				if (host.getStorageSystem().getOldestStaleStaticMessage() != null){
					oldestSatisfiedStaticDepletion(host);
				}
				
				/*
				 * Oldest unprocessed messages should be given priority for depletion 
				 * at a certain point.
				 */
				
				/*else if(host.getStorageSystem().getOldestDeplUnProcMessage() != null) {
					oldestUnProcDepletion(host);
				}*/
				
				/* 
				 * Oldest processed message is depleted (as a FIFO type of storage,
				 * and a new message for processing is processed
				 */
				else if (!host.getStorageSystem().isProcessedEmpty()) {
					this.processedDepletion(host);
				}
				else {
					this.cloudEmptyLoop = false;
					//System.out.println("Depletion is at: "+ this.cloudBW);
				}
				//Revise:
				this.updateCloudBW(host);
				//System.out.println("Depletion is at: "+ deplBW);
				this.lastDepl = curTime;
				/*System.out.println("this.cloudBW is at " + 
						this.cloudBW +
						" this.cloud_lim is at " + 
						this.cloud_lim +
						" host.getStorageSystem().getTotalStorageSpace()*this.min_stor equal "+
						(long)(host.getStorageSystem().getTotalStorageSpace()*this.min_stor)+
						" Total space is "+host.getStorageSystem().getTotalStorageSpace());*/
				//System.out.println("Depleted static messages: "+ sdepleted);
			}
		}
	}

	
	
	public void  deplStorage(DTNHost host) {
		double curTime = SimClock.getTime();
		if(host.getStorageSystem().getProcMessagesSize() + 
				host.getStorageSystem().getStaticMessagesSize() > 
				host.getStorageSystem().getTotalStorageSpace()*this.max_stor) {
			this.deplEmptyLoop = true;
			for (int i = 0; this.deplBW<this.depl_rate && 
				this.deplEmptyLoop && i<50 || this.cloudBW<this.cloud_lim; i++) {
				
				/*if(host.getStorageSystem().getOldestDeplUnProcMessage() != null) {
					oldestUnProcDepletion(host);
				}
				/* Oldest unprocessed message is depleted (as a FIFO type of storage) */
				
				/*else*/ if (host.getStorageSystem().getOldestStaleStaticMessage() != null){
					oldestSatisfiedStaticDepletion(host);
				}
				
				else if(host.getStorageSystem().getOldestInvalidProcessMessage() != null) {
					oldestInvalidProcDepletion(host);
				}
				else if (host.getStorageSystem().getOldestStaticMessage() != null){
					Message temp = host.getStorageSystem().getOldestStaticMessage();
					host.getStorageSystem().deleteMessage(temp.getId());
					if(temp.getProperty("comp") != null) {
						if((Boolean)temp.getProperty("comp")) {
							Message ctemp = this.compressMessage(host, temp);
							host.getStorageSystem().deleteMessage(ctemp.getId());
							double storTime = curTime - ctemp.getReceiveTime();
							ctemp.addProperty("storTime", storTime);
							ctemp.addProperty("satisfied", false);
							ctemp.addProperty("overtime", true);
							host.getStorageSystem().addToDeplStaticMessages(ctemp);
						}
					}
					else if(temp.getProperty("comp") == null) {
						double storTime = curTime - temp.getReceiveTime();
						temp.addProperty("storTime", storTime);
						temp.addProperty("satisfied", true);
						if (storTime == (double)temp.getProperty("shelfLife")) {
							temp.addProperty("overtime", false);
						}
						else if (storTime > (double)temp.getProperty("shelfLife")) {
							temp.addProperty("overtime", true);
						}
						
						if (((String)temp.getProperty("type")).equalsIgnoreCase("unprocessed"))
							host.getStorageSystem().addToDepletedUnProcMessages(temp);
						else
							host.getStorageSystem().addToDeplStaticMessages(temp);
					}
				}
				else if (host.getStorageSystem().getNewestProcessMessage() != null){
					Message temp = host.getStorageSystem().getNewestProcessMessage();
					host.getStorageSystem().deleteMessage(temp.getId());
					if(temp.getProperty("comp") != null) {
						if((Boolean)temp.getProperty("comp")) {
							Message ctemp = this.compressMessage(host, temp);
							host.getStorageSystem().deleteMessage(ctemp.getId());
							double storTime = curTime - ctemp.getReceiveTime();
							ctemp.addProperty("storTime", storTime);
							ctemp.addProperty("satisfied", false);
							ctemp.addProperty("overtime", true);
							host.getStorageSystem().addToDeplProcMessages(ctemp);
						}
					}
					else if(temp.getProperty("comp") == null) {
						double storTime = curTime - temp.getReceiveTime();
						temp.addProperty("storTime", storTime);
						temp.addProperty("satisfied", true);
						if (storTime == (double)temp.getProperty("shelfLife")) {
							temp.addProperty("overtime", false);
						}
						else if (storTime > (double)temp.getProperty("shelfLife")) {
							temp.addProperty("overtime", true);
						}
						
						if (((String)temp.getProperty("type")).equalsIgnoreCase("unprocessed"))
							host.getStorageSystem().addToDepletedUnProcMessages(temp);
						else
							host.getStorageSystem().addToDeplStaticMessages(temp);
					}
				}
	
				else {
					this.deplEmptyLoop = false;
					//System.out.println("Depletion is at: "+ this.deplBW);
				}
				this.updateDeplBW(host);
				this.updateCloudBW(host);
				//Revise:
				this.lastDepl = curTime;
			}
		}
	}
	
	public void processedDepletion(DTNHost host) {
		double curTime = SimClock.getTime();
		if (host.getStorageSystem().getOldestFreshMessage() != null) {
			
			Message temp = host.getStorageSystem().getOldestFreshMessage();
			boolean report = false;
			host.getStorageSystem().deleteProcessedMessage(temp.getId(), report);
			/**
			 * Make sure here that the added message to the cloud depletion
			 * tracking is also tracked by whether it's Fresh or Stale.
			 */
			double storTime = curTime - temp.getReceiveTime();
			temp.addProperty("storTime", storTime);
			//temp.addProperty("satisfied", true);
			if (storTime == (double)temp.getProperty("shelfLife")) {
				temp.addProperty("overtime", false);
			}
			else if (storTime > (double)temp.getProperty("shelfLife")) {
				temp.addProperty("overtime", true);
			}
			report = true;
			host.getStorageSystem().addToStoredMessages(temp);
			host.getStorageSystem().deleteProcessedMessage(temp.getId(), report);
		}
		else if (host.getStorageSystem().getOldestShelfMessage() != null) {
			
			Message temp = host.getStorageSystem().getOldestShelfMessage();
			boolean report = false;
			host.getStorageSystem().deleteProcessedMessage(temp.getId(), report);
			/**
			 * Make sure here that the added message to the cloud depletion
			 * tracking is also tracked by whether it's Fresh or Stale.
			 */
			double storTime = curTime - temp.getReceiveTime();
			temp.addProperty("storTime", storTime);
			//temp.addProperty("satisfied", false);
			if (storTime == (double)temp.getProperty("shelfLife")) {
				temp.addProperty("overtime", false);
			}
			else if (storTime > (double)temp.getProperty("shelfLife")) {
				temp.addProperty("overtime", true);
			}
			report = true;
			host.getStorageSystem().addToStoredMessages(temp);
			host.getStorageSystem().deleteProcessedMessage(temp.getId(), report);
		}
	}
	
	public void oldestSatisfiedStaticDepletion(DTNHost host) {
		double curTime = SimClock.getTime();
		Message temp = host.getStorageSystem().getOldestStaleStaticMessage();
		host.getStorageSystem().deleteMessage(temp.getId());
		if(temp.getProperty("comp") != null) {
			if((Boolean)temp.getProperty("comp")) {
				Message ctemp = this.compressMessage(host, temp);
				host.getStorageSystem().deleteMessage(ctemp.getId());
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
			}
		}
		else if(temp.getProperty("comp") == null) {
			double storTime = curTime - temp.getReceiveTime();
			temp.addProperty("storTime", storTime);
			temp.addProperty("satisfied", true);
			if (storTime == (double)temp.getProperty("shelfLife")) {
				temp.addProperty("overtime", false);
			}
			else if (storTime > (double)temp.getProperty("shelfLife")) {
				temp.addProperty("overtime", true);
			}
			
			if ((String)temp.getProperty("type") == "unprocessed")
				host.getStorageSystem().addToDepletedUnProcMessages(temp);
			else
				host.getStorageSystem().addToCloudDeplStaticMessages(temp);
		}
	}
	
	public void oldestInvalidProcDepletion(DTNHost host){
		double curTime = SimClock.getTime();
		Message temp = host.getStorageSystem().getOldestInvalidProcessMessage();
		host.getStorageSystem().deleteMessage(temp.getId());
		if(temp.getProperty("comp") != null) {
			if((Boolean)temp.getProperty("comp")) {
				Message ctemp = this.compressMessage(host, temp);
				host.getStorageSystem().deleteMessage(ctemp.getId());
				double storTime = curTime - ctemp.getReceiveTime();
				ctemp.addProperty("storTime", storTime);
				ctemp.addProperty("satisfied", true);
				if (storTime == (double)ctemp.getProperty("shelfLife")) {
					ctemp.addProperty("overtime", false);
				}
				else if (storTime > (double)ctemp.getProperty("shelfLife")) {
					ctemp.addProperty("overtime", true);
				}
				host.getStorageSystem().addToDeplProcMessages(ctemp);
			}
		}
		else if(temp.getProperty("comp") == null) {
			double storTime = curTime - temp.getReceiveTime();
			temp.addProperty("storTime", storTime);
			temp.addProperty("satisfied", true);
			if (storTime == (double)temp.getProperty("shelfLife")) {
				temp.addProperty("overtime", false);
			}
			else if (storTime > (double)temp.getProperty("shelfLife")) {
				temp.addProperty("overtime", true);
			}
			
			if ((String)temp.getProperty("type") == "unprocessed")
				host.getStorageSystem().addToDepletedUnProcMessages(temp);
			else
				host.getStorageSystem().addToDeplProcMessages(temp);
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
	public ArrayList <Double> getProcEndTimes() {
		return procEndTimes;
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
