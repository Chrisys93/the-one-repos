/* 
 * 2018 UCL
 * Author: Adrian-Cristian Nicolaescu
 */
package core;

import java.util.Collection;
import java.util.HashMap;
import java.util.ArrayList;

import core.Settings;
import core.SimClock;

import applications.ProcApplication;

/** 
 * @author Adrian-Cristian Nicolaescu, University College London
 */
public class RepoStorage {
	
	/** 
	 * Like in the DTNFileSystem, but here the 
	 * objects to be used and stored are messages 
	 * and this is a more complex system, with a collection 
	 * of messages and a certain capacity
	 */
	//private HashMap<String, Messages> staticMessages;

	/** Host where this file system belongs to */
	private DTNHost host;
	private double compressionRate;

	/** size of the storage space */
	private long storageSize;
	private long processSize;
	private long staticSize;
	private long processedSize;
	private long nrofDeletedMessages;
	private long depletedProcMessages;
	private long oldDepletedProcMessagesSize;
	private long depletedProcMessagesSize;
	private long depletedCloudProcMessages;
	private long depletedCloudProcMessagesSize;
	private long oldDepletedCloudProcMessagesSize;
	private long depletedUnProcMessages;
	private long depletedUnProcMessagesSize;
	private long oldDepletedUnProcMessagesSize;
	private long depletedPUnProcMessages;
	private long depletedPUnProcMessagesSize;
	private long oldDepletedPUnProcMessagesSize;
	private long depletedStaticMessages;
	private long depletedStaticMessagesSize;
	private long oldDepletedStaticMessagesSize;
	private long depletedCloudStaticMessages;
	private long oldDepletedCloudStaticMessagesSize;
	private long depletedCloudStaticMessagesSize;
	private int  mStorTimeNo;
	private int  mFresh;
	private int  mStale;
	private int  mOvertime;
	private int  mSatisfied;
	private int  mUnSatisfied;
	private int  mUnProcessed;
	private double mStorTimeAvg;
	private double mStorTimeMax;
	private double totalReceivedMessages;
	private double totalReceivedMessagesSize;
	private int cachedMessages;

	protected ArrayList<Message> staticMessages;
	protected ArrayList<Message> processMessages;
	protected ArrayList<Message> processedMessages;
	protected ArrayList<Message> storedMessages;
	
	private ProcApplication procApp;

	/** value to keep track of used storage */
	//protected long usedStorage;

	protected Collection<Message> messages;

	public void init(DTNHost dtnHost, long storageSize, double compressionRate) {
		this.host = dtnHost;
		//this.messages = new Collection<Message>();
		this.staticMessages = new ArrayList<Message>();
		this.processMessages = new ArrayList<Message>();
		this.processedMessages = new ArrayList<Message>();
		this.storedMessages = new ArrayList<Message>();
		this.storageSize = storageSize;
		this.processSize = 0;
		this.staticSize = 0;
		this.processedSize = 0;
		this.mFresh = 0;
		this.mStale = 0;
		this.mOvertime = 0;
		this.mSatisfied = 0;
		this.mUnSatisfied = 0;
		this.mUnProcessed = 0;
		this.mStorTimeNo = 0;
		this.mStorTimeAvg = 0;
		this.mStorTimeMax = 0;
		this.nrofDeletedMessages = 0;
		this.totalReceivedMessages = 0;
		this.totalReceivedMessagesSize = 0;
		this.depletedProcMessages = 0;
		this.oldDepletedProcMessagesSize = 0;
		this.depletedProcMessagesSize = 0;
		this.depletedCloudProcMessages = 0;
		this.oldDepletedCloudProcMessagesSize = 0;
		this.depletedCloudProcMessagesSize = 0;
		this.depletedUnProcMessages = 0;
		this.depletedUnProcMessagesSize = 0;
		this.depletedPUnProcMessages = 0;
		this.depletedPUnProcMessagesSize = 0;
		this.oldDepletedUnProcMessagesSize = 0;
		this.depletedStaticMessages = 0;
		this.depletedStaticMessagesSize = 0;
		this.oldDepletedStaticMessagesSize = 0;
		this.depletedCloudStaticMessages = 0;
		this.oldDepletedCloudStaticMessagesSize = 0;
		this.depletedCloudStaticMessagesSize = 0;
		this.cachedMessages = 0;
		if (this.getHost().hasProcessingCapability()){
			//this.processSize = processSize;
			this.compressionRate = compressionRate;
			double processedRatio = this.compressionRate*2;
			this.processedSize = (long)(this.storageSize/processedRatio);
		}
	}
	
	/**
	 * Returns total storage size of repo
	 * @return total storage size
	 */
	public long getTotalStorageSpace() {
		return this.storageSize;
	}
	
	/**
	 * Returns total processed message storage size of repo
	 * @return total processed message storage size
	 */
	public long getTotalProcessedSpace() {
		return this.processedSize;
	}
	
	/** Create message collection stored return method */
	
	public Collection<Message> getStoredMessagesCollection() {
		this.messages = this.staticMessages;
		this.messages.addAll(this.processMessages);
		return this.messages;
	}

	/** Create message ArrayList stored return method */
	
	public ArrayList<Message> getStoredMessages() {
		this.storedMessages = this.staticMessages;
		this.storedMessages.addAll(this.processMessages);
		return this.storedMessages;
	}

	/** Create message ArrayList stored return method */
	
	public ArrayList<Message> getProcessedMessages() {
		return this.processedMessages;
	}

	/** Create message ArrayList stored return method */
	
	public ArrayList<Message> getProcessMessages() {
		return this.processMessages;
	}

	/** Create message ArrayList stored return method */
	
	public ArrayList<Message> getStaticMessages() {
		return this.staticMessages;
	}
	
	/**
	 * Adds a message to the storage system and/or to the processing pipeline
	 * if the the node has processing capability and the processing storage is 
	 * not full and/or process the oldest message in processing storage, unless 
	 * the processed messages storage is full.
	 * @param sm The message to add
	 * @return true if the message is added correctly
	 */			
	public void addToStoredMessages(Message sm) {
		double curTime = SimClock.getTime();
		if (sm != null) {
			if (((String) sm.getProperty("type")).equalsIgnoreCase("nonproc")) {
				this.staticMessages.add(sm);
				this.staticSize += sm.getSize();
			}
			else if (((String) sm.getProperty("type")).equalsIgnoreCase("proc")) {
				this.processMessages.add(sm);
				this.processSize += sm.getSize();				
			}
			else if (((String) sm.getProperty("type")).equalsIgnoreCase("processed")) {
				this.processedMessages.add(sm);	
			}
			this.totalReceivedMessages++;
			this.totalReceivedMessagesSize += sm.getSize();
			/* add space used in the storage space */
			//System.out.println("There is " + this.getStaticMessagesSize() + " storage used");
		}
		if ((this.staticSize + this.processSize) >= this.storageSize) {
			for (Application app : this.getHost().getRouter().getApplications("ProcApplication")) {
				this.procApp = (ProcApplication) app;
				//System.out.println("App ID is: "+ this.procApp.getAppID());
			}

			procApp.updateDeplBW(this.getHost());
			procApp.deplStorage(this.getHost());
			
			/*if(!this.isProcessedFull() && this.cachedMessages < procApp.getProcRate()){
				this.procApp.processOldestValidMessage(this.getHost());
			}
			else if (this.getOldestStaticMessage() != null) {
				Message temp = this.getOldestStaticMessage();
				if((Boolean)temp.getProperty("comp") != null && (Boolean)temp.getProperty("comp") == false) {
					this.deleteMessage(temp.getId());
				}
				else if ((Boolean)temp.getProperty("comp") != null) {
					String tempc = this.compressMessage(temp);
					Message ctemp = this.getStaticMessage(tempc);
					double storTime = curTime - ctemp.getReceiveTime();
					ctemp.addProperty("storTime", storTime);
					ctemp.addProperty("satisfied", false);
					ctemp.addProperty("overtime", true);
					this.addToDeplStaticMessages(ctemp);
					this.deleteMessage(tempc);
				}
			}
			else {
				Message temp = this.getNewestProcessMessage();
				if((Boolean)temp.getProperty("comp") != null && (Boolean)temp.getProperty("comp") == false) {
					this.deleteMessage(temp.getId());
				}
				else if ((Boolean)temp.getProperty("comp") != null) {
					String tempc = this.compressMessage(temp);
					Message ctemp = this.getStaticMessage(tempc);
					double storTime = curTime - ctemp.getReceiveTime();
					ctemp.addProperty("storTime", storTime);
					ctemp.addProperty("satisfied", false);
					ctemp.addProperty("overtime", true);
					this.addToDeplProcMessages(ctemp);
					this.deleteMessage(tempc);
				}
			}*/
		}
	}
	
	/**
	 * Adds a message to the depleted stored messages
	 * @param sm The message to add
	 * @return true if the message is added correctly
	 */			
	public void addToDeplStaticMessages(Message sm) {
		if (sm != null) {
			this.depletedStaticMessages++;
			this.depletedStaticMessagesSize += sm.getSize();
			if ((Boolean)sm.getProperty("overtime"))
				this.mOvertime ++;
			if ((String)sm.getProperty("type") == "unprocessed")
				this.mUnProcessed ++;
			if ((Boolean)sm.getProperty("satisfied"))
				this.mSatisfied ++;
			else
				this.mUnSatisfied ++;
			if(sm.getProperty("storTime") != null) {
				this.mStorTimeNo ++;
				this.mStorTimeAvg = (double)sm.getProperty("storTime")/this.mStorTimeNo;
				if (this.mStorTimeMax < (double)sm.getProperty("storTime"))
					this.mStorTimeMax = (double)sm.getProperty("storTime");
			}
			else {
				double curTime = SimClock.getTime();
				sm.addProperty("storTime", curTime - sm.getReceiveTime());
				this.mStorTimeNo ++;
				this.mStorTimeAvg = (double)sm.getProperty("storTime")/this.mStorTimeNo;
				if (this.mStorTimeMax < (double)sm.getProperty("storTime"))
					this.mStorTimeMax = (double)sm.getProperty("storTime");
			}
		}
	}
	
	/**
	 * Adds a message to the depleted processing messages
	 * @param sm The message to add
	 * @return true if the message is added correctly
	 */			
	public void addToDeplProcMessages(Message sm) {
		if (sm != null) {
			this.depletedProcMessages++;
			this.depletedProcMessagesSize += sm.getSize();
			if ((Boolean)sm.getProperty("overtime"))
				this.mOvertime ++;
			this.mUnSatisfied ++;
			if ((String)sm.getProperty("type") == "unprocessed")
				this.mUnProcessed ++;
			if(sm.getProperty("storTime") != null) {
				this.mStorTimeNo ++;
				this.mStorTimeAvg = (double)sm.getProperty("storTime")/this.mStorTimeNo;
				if (this.mStorTimeMax < (double)sm.getProperty("storTime"))
					this.mStorTimeMax = (double)sm.getProperty("storTime");
			}
			else {
				double curTime = SimClock.getTime();
				sm.addProperty("storTime", curTime - sm.getReceiveTime());
				this.mStorTimeNo ++;
				this.mStorTimeAvg = (double)sm.getProperty("storTime")/this.mStorTimeNo;
				if (this.mStorTimeMax < (double)sm.getProperty("storTime"))
					this.mStorTimeMax = (double)sm.getProperty("storTime");
			}
		}
	}
	
	/**
	 * Adds a message to the depleted stored messages
	 * @param sm The message to add
	 * @return true if the message is added correctly
	 */			
	public void addToCloudDeplStaticMessages(Message sm) {
		if (sm != null) {
			this.depletedCloudStaticMessages++;
			this.depletedCloudStaticMessagesSize += sm.getSize();
			if ((Boolean)sm.getProperty("overtime"))
				this.mOvertime ++;
			if ((String)sm.getProperty("type") == "unprocessed")
				this.mUnProcessed ++;
			if ((Boolean)sm.getProperty("satisfied"))
				this.mSatisfied ++;
			else
				this.mUnSatisfied ++;
			if(sm.getProperty("storTime") != null) {
				this.mStorTimeNo ++;
				this.mStorTimeAvg = (double)sm.getProperty("storTime")/this.mStorTimeNo;
				if (this.mStorTimeMax < (double)sm.getProperty("storTime"))
					this.mStorTimeMax = (double)sm.getProperty("storTime");
			}
			else {
				double curTime = SimClock.getTime();
				sm.addProperty("storTime", curTime - sm.getReceiveTime());
				this.mStorTimeNo ++;
				this.mStorTimeAvg = (double)sm.getProperty("storTime")/this.mStorTimeNo;
				if (this.mStorTimeMax < (double)sm.getProperty("storTime"))
					this.mStorTimeMax = (double)sm.getProperty("storTime");
			}
		}
	}
	
	/**
	 * Adds a message there is not enough space for in the repository
	 * to the "depleted processed" messages, even though this message 
	 * would be sent towards the cloud for processing instead. The 
	 * tradeoff for this problem is accounted for by increasing BW 
	 * of depletion. This is a last-resort solution.
	 * @param sm The message to add
	 * @return true if the message is added correctly
	 */			
	public void addToDeplUnProcMessages(String smID) {
		if (this.hasMessage(smID) != null ) {
			Message sm = this.hasMessage(smID);
			sm.updateProperty("type", "unprocessed");
		}
	}
	
	/**
	 * Adds a message to the depleted stored messages
	 * @param sm The message to add
	 * @return true if the message is added correctly
	 */			
	public void addToDepletedUnProcMessages(Message sm) {
		if (sm != null) {
			this.depletedUnProcMessages++;
			this.depletedUnProcMessagesSize += sm.getSize();
			if ((Boolean)sm.getProperty("overtime"))
				this.mOvertime ++;
			if ((String)sm.getProperty("type") == "unprocessed")
				this.mUnProcessed ++;
			if ((Boolean)sm.getProperty("satisfied"))
				this.mSatisfied ++;
			else
				this.mUnSatisfied ++;
			if(sm.getProperty("storTime") != null) {
				this.mStorTimeNo ++;
				this.mStorTimeAvg = (double)sm.getProperty("storTime")/this.mStorTimeNo;
				if (this.mStorTimeMax < (double)sm.getProperty("storTime"))
					this.mStorTimeMax = (double)sm.getProperty("storTime");
			}
			else {
				double curTime = SimClock.getTime();
				sm.addProperty("storTime", curTime - sm.getReceiveTime());
				this.mStorTimeNo ++;
				this.mStorTimeAvg = (double)sm.getProperty("storTime")/this.mStorTimeNo;
				if (this.mStorTimeMax < (double)sm.getProperty("storTime"))
					this.mStorTimeMax = (double)sm.getProperty("storTime");
			}
		}
	}
	
	/**
	 * Returns a stored message by ID.
	 * @param MessageId ID of the file
	 * @return The message
	 */
	public Message getStaticMessage(String MessageId) {
		Message staticMessage = null;
		for (Message temp : this.staticMessages){
			if (temp.getId() == MessageId){
				int i = this.staticMessages.indexOf(temp);
				staticMessage = this.staticMessages.get(i);
			}
		}
		return staticMessage;
	}
	
	/**
	 * Returns a processed message by ID.
	 * @param MessageId ID of the file
	 * @return The message
	 */
	public Message getProcessedMessage(String MessageId) {
		Message processedMessage = null;
		for (Message temp : this.processedMessages){
			if (temp.getId() == MessageId){
				int i = this.processedMessages.indexOf(temp);
				processedMessage = this.processedMessages.get(i);
			}
		}
		return processedMessage;
	}
	
	/**
	 * Returns a processing message by ID.
	 * @param MessageId ID of the file
	 * @return The message
	 */
	public Message getProcessMessage(String MessageId) {
		Message processMessage = null;
		for (Message temp : this.processMessages){
			if (temp.getId() == MessageId){
				int i = this.processMessages.indexOf(temp);
				processMessage = this.processMessages.get(i);
			}
		}
		return processMessage;
	}
	
	/**
	 * Returns the number of messages having storage times registered
	 * @return How many files this file system has
	 */
	public int getStorTimeNo() {
		return this.mStorTimeNo;
	}
	
	/**
	 * Returns the number of messages having storage times registered
	 * @return How many files this file system has
	 */
	public double getStorTimeAvg() {
		return this.mStorTimeAvg;
	}
	
	/**
	 * Returns the number of messages having storage times registered
	 * @return How many files this file system has
	 */
	public double getStorTimeMax() {
		return this.mStorTimeMax;
	}
	
	/**
	 * Returns the number of files this file system has
	 * @return How many files this file system has
	 */
	public int getNrofMessages() {
		return this.staticMessages.size();
	}
	
	/**
	 * Returns the number of files this file system has
	 * @return How many files this file system has
	 */
	public int getNrofProcessMessages() {
		return this.processMessages.size();
	}
	
	/**
	 * Returns the number of files this file system has
	 * @return How many files this file system has
	 */
	public int getNrofProcessedMessages() {
		return this.processedMessages.size();
	}

	/**
	 * Returns the total size of stored messages in this storage system
	 * @return The size of the used storage in this storage system
	 */
	public long getStaticMessagesSize() {
		return this.staticSize;
	}
	
	/**
	 * Returns the total size of messages that have stayed in storage over
	 * their shelf lives.
	 * @return The size of the used storage for valid, satisfied shelf-life
	 * messages in this storage system
	 */
	public long getStaleStaticMessagesSize(){
		double curTime = SimClock.getTime();
		long size = 0;
		for (Message m : this.staticMessages) {
			if(m.getProperty("shelfLife")!=null && 
				((double) m.getProperty("shelfLife")) >= curTime - m.getReceiveTime()) {
				size += m.getSize();
			}
		}
		return size;
	}

	/**
	 * Returns the total size of messages to be processed in this storage system
	 * @return The size of the used processing storage in this storage system
	 */
	public long getProcMessagesSize() {
		return this.processSize;
	}

	/**
	 * Returns the total size of messages to be processed in this storage system
	 * @return The size of the used processing storage in this storage system
	 */
	public long getProcessedMessagesSize() {
		long processedUsed = 0;
		for (Message msg:this.processedMessages) {
			processedUsed += msg.getSize();
		}
		return processedUsed;
	}
	
	/**
	 * Returns processed messages as a result of new messages being added.
	 * This is called every application update. 
	 * @return The number of messages processed as a result of full storage
	 */
	public int getFullCachedMessagesNo() {
		int proc = this.cachedMessages;
		this.cachedMessages = 0;
		return proc;
	}
		
	
	/**
	 * Returns the host this repo storage system is in
	 * @return The host object
	 */
	protected DTNHost getHost() {
		return this.host;
	}
	
	/**
	 * Check if this storage system has a message searched by Id.
	 * @param hash hash of the file
	 * @return true if this file system has the file
	 */
	public Message hasMessage(String MessageId) {
		Message answer = null;

		for (int j=0; j<this.processedMessages.size(); j++){
			if(this.processedMessages.get(j).getId() == MessageId){
				answer =  this.processedMessages.get(j);
			}
		}
		for(int i=0; i<this.staticMessages.size(); i++){
			if(this.staticMessages.get(i).getId() == MessageId){
				answer =  this.staticMessages.get(i);
			}
		}
		for(int i=0; i<this.processMessages.size(); i++){
			if(this.processMessages.get(i).getId() == MessageId){
				answer =  this.processMessages.get(i);
			}
		}
		return answer;
	}

	/**
	 * Method for deleting specific stored message
	 * @param MessageId ID of message to be deleted
	 * @return successful deletion status
	 */
	public boolean deleteStaticMessage(String MessageId){
		for(int i=0; i<staticMessages.size(); i++){
			if(staticMessages.get(i).getId() == MessageId){
				this.staticSize -= staticMessages.get(i).getSize();
				this.staticMessages.remove(i);
				return true;
			}
		}
		return false;
	}

	/**
	 * Method for deleting specific message to be processed
	 * @param MessageId ID of message to be deleted
	 * @return successful deletion status
	 */
	public boolean deleteProcMessage(String MessageId){
		for(int i=0; i<processMessages.size(); i++){
			if(processMessages.get(i).getId() == MessageId){
				this.processSize -= processMessages.get(i).getSize();
				this.processMessages.remove(i);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Method for deleting specific message from storage
	 * @param MessageId ID of message to be deleted
	 * @return successful deletion status
	 */
	public boolean deleteMessage(String MessageId){
		Message m = this.hasMessage(MessageId);
		if(m != null){
			if (((String)m.getProperty("type")).equalsIgnoreCase("proc") && deleteProcMessage(MessageId)) {
				return true;
			}
			else if (((String)m.getProperty("type")).equalsIgnoreCase("nonproc") && deleteStaticMessage(MessageId)) {
				return true;
			}
			else if (((String)m.getProperty("type")).equalsIgnoreCase("unprocessed") && deleteProcMessage(MessageId)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Method for deleting specific processed message
	 * @param MessageId ID of message to be deleted
	 * @return successful deletion status
	 */
	public boolean deleteProcessedMessage(String MessageId, boolean report){
		/* 
		 * To be used in event, for deleting processed messages 
		 * after "sending"/depleting them.
		 */
		for(int i=0; i<processedMessages.size(); i++){
			if(processedMessages.get(i).getId() == MessageId){
				this.depletedCloudProcMessages++;
				this.depletedCloudProcMessagesSize += this.processedMessages.get(i).getSize();
				if(report) {
					if(this.processedMessages.get(i).getProperty("overtime")!=null) {
						if ((Boolean)this.processedMessages.get(i).getProperty("overtime"))
							this.mOvertime ++;
					}
					if(this.processedMessages.get(i).getProperty("satisfied")!=null) {
						if ((Boolean)this.processedMessages.get(i).getProperty("satisfied"))
							this.mSatisfied ++;
						else
							this.mUnSatisfied ++;
					}
					if(this.processedMessages.get(i).getProperty("Fresh")!=null) {
						if ((Boolean)processedMessages.get(i).getProperty("Fresh"))
							this.mFresh++;
						else if (!(Boolean)processedMessages.get(i).getProperty("Fresh"))
							this.mStale++;
					}
				}
				this.processedMessages.remove(i);
				return true;
			}
		}
		return false;
	}
	
	public long getNrofDeletedMessages() {
		return this.nrofDeletedMessages;
	}
	
	public long getNrofDepletedCloudProcMessages() {
		return this.depletedCloudProcMessages;
	}
	
	public int getNrofFreshMessages() {
		return this.mFresh;
	}
	
	public int getNrofStaleMessages() {
		return this.mStale;
	}
	
	public int getNrofSatisfiedMessages() {
		return this.mSatisfied;
	}
	
	public int getNrofUnSatisfiedMessages() {
		return this.mUnSatisfied;
	}
	
	public int getNrofOvertimeMessages() {
		return this.mOvertime;
	}
	
	public int getNrofUnProcessedMessages() {
		return this.mUnProcessed;
	}
	
	public long getNrofDepletedUnProcMessages() {
		return this.depletedUnProcMessages;
	}
	
	public long getNrofDepletedPUnProcMessages() {
		return this.depletedPUnProcMessages;
	}
	
	public long getNrofDepletedStaticMessages() {		
		return this.depletedStaticMessages;
	}
	
	public long getNrofDepletedCloudStaticMessages() {		
		return this.depletedCloudStaticMessages;
	}
	
	public double getOverallMeanIncomingMesssageNo() {
		return (this.totalReceivedMessages/SimClock.getTime());
	}
	
	public double getOverallMeanIncomingSpeed() {
		return (this.totalReceivedMessagesSize/SimClock.getTime());
	}
	
	/**
	 * Method that returns depletion BW used for processed messages.
	 * @param reporting Whether the function is used for reporting, 
	 * as a final method of the update, or for checking BW usage.
	 * @return the processed depletion BW used upstream
	 */
	public long getDepletedCloudProcMessagesBW(boolean reporting) {
		long procBW = this.depletedCloudProcMessagesSize - this.oldDepletedCloudProcMessagesSize;
		if (reporting) {
			this.oldDepletedCloudProcMessagesSize = this.depletedCloudProcMessagesSize;
		}
		return (procBW);
	}
	
	/**
	 * Method that returns depletion BW used in off-loading static messages to the cloud.
	 * @param reporting Whether the function is used for reporting, 
	 * as a final method of the update, or for checking BW usage.
	 * @return the static depletion BW used upstream
	 */
	public long getDepletedUnProcMessagesBW(boolean reporting) {
		long procBW = this.depletedUnProcMessagesSize - this.oldDepletedUnProcMessagesSize;
		if (reporting) {
			this.oldDepletedUnProcMessagesSize = this.depletedUnProcMessagesSize;
		}
		return (procBW);
	}
	
	/**
	 * Method that returns depletion BW used in off-loading unprocessed messages to the cloud.
	 * @param reporting Whether the function is used for reporting, 
	 * as a final method of the update, or for checking BW usage.
	 * @return the unprocessed depletion BW used upstream
	 */
	public long getDepletedPUnProcMessagesBW(boolean reporting) {
		long procBW = this.depletedPUnProcMessagesSize - this.oldDepletedPUnProcMessagesSize;
		if (reporting) {
			this.oldDepletedPUnProcMessagesSize = this.depletedPUnProcMessagesSize;
		}
		return (procBW);
	}
	
	/**
	 * Method that returns depletion BW used for non-processing messages.
	 * @param reporting Whether the function is used for reporting, 
	 * as a final method of the update, or for checking BW usage.
	 * @return the non-processing depletion BW used upstream
	 */
	public long getDepletedCloudStaticMessagesBW(boolean reporting) {
		long statBW = this.depletedCloudStaticMessagesSize - this.oldDepletedCloudStaticMessagesSize;
		if (reporting) {
			this.oldDepletedCloudStaticMessagesSize = this.depletedCloudStaticMessagesSize;
		}
		return (statBW);
	}
	
	/**
	 * Method that returns depletion BW used for processed messages.
	 * @param reporting Whether the function is used for reporting, 
	 * as a final method of the update, or for checking BW usage.
	 * @return the processed depletion BW used upstream
	 */
	public long getDepletedProcMessagesBW(boolean reporting) {
		long procBW = this.depletedProcMessagesSize - this.oldDepletedProcMessagesSize;
		if (reporting) {
			this.oldDepletedProcMessagesSize = this.depletedProcMessagesSize;
		}
		return (procBW);
	}
	
	/**
	 * Method that returns depletion BW used for non-processing messages.
	 * @param reporting Whether the function is used for reporting, 
	 * as a final method of the update, or for checking BW usage.
	 * @return the non-processing depletion BW used upstream
	 */
	public long getDepletedStaticMessagesBW(boolean reporting) {
		long statBW = this.depletedStaticMessagesSize - this.oldDepletedStaticMessagesSize;
		if (reporting) {
			this.oldDepletedStaticMessagesSize = this.depletedStaticMessagesSize;
		}
		return (statBW);
	}
	
	public boolean clearAllStaticMessages(){
		this.staticMessages.clear();
		return true;
	}

	public long getFreeStorageSpace() {
		long usedStorage = this.getStaticMessagesSize();
		//System.out.println("There is " + usedStorage + " storage used in "+this.getHost());
		long freeStorage = this.storageSize - usedStorage;
		//System.out.println("There is "+freeStorage+" free storage space in "+this.getHost());
		return freeStorage;
	}

	/*public boolean isStorageFull() {
		long usedStorage = this.getStaticMessagesSize();
		if (usedStorage >= this.storageSize - 100000000){
			return true;
		}
		else{
			return false;
		}
	}

	public boolean isProcessingFull() {
		long usedProc = this.getProcMessagesSize();
		if (usedProc >= this.processSize - 2000000){
			//System.out.println("There is enough storage space: " + freeStorage);
			return true;
		}
		else{
			//System.out.println("There is not enough storage space: " + freeStorage);
			return false;
		}
	}*/

	public boolean isProcessingEmpty() {
		//try {
		//	System.setOut(new PrintStream(new FileOutputStream("log.txt")));
		//} catch(Exception e) {}
		if (this.processSize <= 2000000){
			//System.out.println("There is enough storage space: " + freeStorage);
			return true;
		}
		else{
			//System.out.println("There is not enough storage space: " + freeStorage);
			return false;
		}
	}

	public boolean isProcessedFull() {
		long usedProcessed = this.getProcessedMessagesSize();
		//try {
		//	System.setOut(new PrintStream(new FileOutputStream("log.txt")));
		//} catch(Exception e) {}
		if (usedProcessed >= this.processedSize - 500000){
			//System.out.println("There is enough storage space: " + freeStorage);
			return true;
		}
		else{
			//System.out.println("There is not enough storage space: " + freeStorage);
			return false;
		}
	}

	public boolean isProcessedEmpty() {
		long usedProc = this.getProcessedMessagesSize();
		//try {
		//	System.setOut(new PrintStream(new FileOutputStream("log.txt")));
		//} catch(Exception e) {}
		if (usedProc <= 2000000){
			//System.out.println("There is enough storage space: " + freeStorage);
			return true;
		}
		else{
			//System.out.println("There is not enough storage space: " + freeStorage);
			return false;
		}
	}
	
	public Message getOldestProcessMessage(){
		Message oldest = null;
		for (Message m : this.processMessages) {
			
			if (oldest == null ) {
				oldest = m;
			}
			else if (oldest.getReceiveTime() > m.getReceiveTime()) {
				oldest = m;
			}
		}
		return oldest;
	}
	
	public Message getOldestValidProcessMessage(){
		Message oldest = null;
		for (Message m : this.processMessages) {
			
			if (oldest == null) {
				
				if(m.getProperty("type")!=null) {
					if(!((String) m.getProperty("type")).equalsIgnoreCase("unprocessed")&& 
						!((String) m.getProperty("type")).equalsIgnoreCase("processed")) {
						oldest = m;
					}
				}
			}
			else if (oldest.getReceiveTime() > m.getReceiveTime()) {
					
				if(m.getProperty("type")!=null) {
					if(!((String) m.getProperty("type")).equalsIgnoreCase("unprocessed")&& 
						!((String) m.getProperty("type")).equalsIgnoreCase("processed")) {
						oldest = m;
					}
				}
			}
		}
		return oldest;
	}
	
	public Message getOldestInvalidProcessMessage(){
		double curTime = SimClock.getTime();
		Message oldest = null;
		for (Message m : this.processMessages) {
			
			if (oldest == null) {
				
				if(m.getProperty("type")!=null && m.getProperty("shelfLife")!=null) {
					if(((String) m.getProperty("type")).equalsIgnoreCase("proc") && 
					((double) m.getProperty("shelfLife")) <= curTime - m.getReceiveTime()) {
						oldest = m;
					}
				}
			}
			else if (oldest.getReceiveTime() > m.getReceiveTime()) {
				
				if(m.getProperty("type")!=null && m.getProperty("shelfLife")!=null) {
					if(((String) m.getProperty("type")).equalsIgnoreCase("proc") && 
					((double) m.getProperty("shelfLife")) <= curTime - m.getReceiveTime()) {
						oldest = m;
					}
				}
			}
		}
		return oldest;
	}
	
	public Message getOldestDeplUnProcMessage(){
		Message oldest = null;
		for (Message m : this.staticMessages) {
			
			if (oldest == null) {
				
				if(m.getProperty("type")!=null) {
					if(((String) m.getProperty("type")).equalsIgnoreCase("unprocessed")) {
						oldest = m;
					}
				}
			}
			else if (oldest.getReceiveTime() > m.getReceiveTime()) {
				
				if(m.getProperty("type")!=null) {
					if(((String) m.getProperty("type")).equalsIgnoreCase("unprocessed")) {
						oldest = m;
					}
				}
			}
		}
		return oldest;
	}
	

	
	public Message getNewestProcessMessage(){
		Message newest = null;
		for (Message m : this.processMessages) {
			
			if (newest == null) {
				
				if(m.getProperty("type")!=null) {
					if(((String) m.getProperty("type")).equalsIgnoreCase("proc")) {
						newest = m;
					}
				}
			}
			else if (newest.getReceiveTime() < m.getReceiveTime()) {
				
				if(m.getProperty("type")!=null) {
					if(((String) m.getProperty("type")).equalsIgnoreCase("proc")) {
						newest = m;
					}
				}
			}
		}
		return newest;
	}
	
	public Message getOldestProcessedMessage(){
		Message oldest = null;
		for (Message m : this.processedMessages) {
			
			if (oldest == null ) {
				oldest = m;
			}
			else if (oldest.getReceiveTime() > m.getReceiveTime()) {
				oldest = m;
			}
		}
		return oldest;
	}
	
	public Message getOldestFreshMessage(){
		Message oldest = null;
		for (Message m : this.processedMessages) {
			
			if (oldest == null) {
				
				if(m.getProperty("Fresh")!=null) {
					if(((Boolean) m.getProperty("Fresh"))) {
						oldest = m;
					}
				}
			}
			else if (oldest.getReceiveTime() > m.getReceiveTime()) {
				
				if(m.getProperty("Fresh")!=null) {
					if(((Boolean) m.getProperty("Fresh"))) {
						oldest = m;
					}
				}
			}
		}
		return oldest;
	}
	
	public Message getOldestShelfMessage(){
		Message oldest = null;
		for (Message m : this.processedMessages) {
			
			if (oldest == null) {
				
				if(m.getProperty("Fresh")!=null) {
					if(!((Boolean) m.getProperty("Fresh"))) {
						oldest = m;
					}
				}
			}
			else if (oldest.getReceiveTime() > m.getReceiveTime()) {
				
				if(m.getProperty("Fresh")!=null) {
					if(!((Boolean) m.getProperty("Fresh"))) {
						oldest = m;
					}
				}
			}
		}
		return oldest;
	}
	
	public Message getOldestStaticMessage(){
		Message oldest = null;
		for (Message m : this.staticMessages) {
			
			if (oldest == null ) {
				oldest = m;
			}
			else if (oldest.getReceiveTime() > m.getReceiveTime()) {
				oldest = m;
			}
		}
		return oldest;
	}
	
	public Message getOldestStaleStaticMessage(){
		double curTime = SimClock.getTime();
		Message oldest = null;
		for (Message m : this.staticMessages) {
			
			if (oldest == null) {
				
				if(m.getProperty("shelfLife")!=null) {
					if(((double) m.getProperty("shelfLife")) <= curTime - m.getReceiveTime()) {
						oldest = m;
					}
				}
			}
			else if (oldest.getReceiveTime() > m.getReceiveTime()) {
				
				if(m.getProperty("shelfLife")!=null) {
					if(((double) m.getProperty("shelfLife")) <= curTime - m.getReceiveTime()) {
						oldest = m;
					}
				}
			}
		}
		return oldest;
	}

	public double getCompressionRate() {
		return this.compressionRate;
	}

}
