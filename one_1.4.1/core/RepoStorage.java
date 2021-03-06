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
	private long depletedProcMessagesSize;
	private long oldDepletedProcMessagesSize;
	private long depletedUnProcMessages;
	private long depletedUnProcMessagesSize;
	private long oldDepletedUnProcMessagesSize;
	private long depletedPUnProcMessages;
	private long depletedPUnProcMessagesSize;
	private long oldDepletedPUnProcMessagesSize;
	private long depletedStaticMessages;
	private long oldDepletedStaticMessagesSize;
	private long depletedStaticMessagesSize;
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
		this.nrofDeletedMessages = 0;
		this.totalReceivedMessages = 0;
		this.totalReceivedMessagesSize = 0;
		this.depletedProcMessages = 0;
		this.oldDepletedProcMessagesSize = 0;
		this.depletedProcMessagesSize = 0;
		this.depletedUnProcMessages = 0;
		this.depletedUnProcMessagesSize = 0;
		this.depletedPUnProcMessages = 0;
		this.depletedPUnProcMessagesSize = 0;
		this.oldDepletedUnProcMessagesSize = 0;
		this.depletedStaticMessages = 0;
		this.oldDepletedStaticMessagesSize = 0;
		this.depletedStaticMessagesSize = 0;
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
		if (sm != null) {
			if (((String) sm.getProperty("type")).equalsIgnoreCase("nonproc")) {
				this.staticMessages.add(sm);
				this.staticSize += sm.getSize();
			}
			else if (((String) sm.getProperty("type")).equalsIgnoreCase("proc")) {
				this.processMessages.add(sm);
				this.processSize += sm.getSize();				
			}
			this.totalReceivedMessages++;
			this.totalReceivedMessagesSize += sm.getSize();
			/* add space used in the storage space */
			//System.out.println("There is " + this.getStaticMessagesSize() + " storage used");
		}
		if ((this.staticSize + this.processSize) >= this.storageSize) {
			for (Application app : this.getHost().getRouter().getApplications("ProcApplication")) {
				this.procApp = (ProcApplication) app;
			}
			if(!this.isProcessedFull() && this.cachedMessages < procApp.getProcRate()){
				this.processMessage(this.getOldestProcessMessage());
				this.cachedMessages ++;
			}
			else if (this.getOldestStaticMessage() != null) {
				this.addToDeplUnProcMessages(this.getOldestStaticMessage());
			}
			else {
				this.addToDeplUnProcMessages(this.getNewestProcessMessage());
			}
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
	public void addToDeplUnProcMessages(Message sm) {
		if (sm != null) {
			if (((String) sm.getProperty("type")).equalsIgnoreCase("proc")) {
				this.depletedPUnProcMessages++;
				this.depletedPUnProcMessagesSize += sm.getSize();
				this.deleteProcMessage(sm.getId());
			}
			else if (((String) sm.getProperty("type")).equalsIgnoreCase("nonproc")) {
				this.depletedUnProcMessages++;
				this.depletedUnProcMessagesSize += sm.getSize();
				this.deleteStaticMessage(sm.getId());
			}
		}
	}
	
	/**
	 * Returns a stored message by ID.
	 * @param MessageId ID of the file
	 * @return The message
	 */
	public Message getStaticMessage(String MessageId) {
		Message staticMessage = this.staticMessages.get(0);
		for (Message temp : staticMessages){
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
		Message processedMessage = this.processedMessages.get(0);
		for (Message temp : processedMessages){
			if (temp.getId() == MessageId){
				int i = this.processedMessages.indexOf(temp);
				processedMessage = this.processedMessages.get(i);
			}
		}
		return processedMessage;
	}
	
	public boolean processMessage(Message procMessage) {
		int initsize = procMessage.getSize();
		int processedsize = (int) (initsize/this.compressionRate);
		Message processedMessage = new Message(procMessage.getFrom(), procMessage.getTo(), procMessage.getId(), processedsize);
		this.processedMessages.add(processedMessage);
		this.deleteProcMessage(procMessage.getId());
		return true;
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
	public boolean hasMessage(String MessageId) {
		boolean answer = false;

		for (int j=0; j<this.processedMessages.size(); j++){
			if(this.processedMessages.get(j).getId() == MessageId){
				answer =  true;
				return answer;
			}
		}
		for(int i=0; i<this.staticMessages.size(); i++){
			if(this.staticMessages.get(i).getId() == MessageId){
				answer =  true;
			}
			else{
				answer =  false;
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
	 * Method for deleting specific processed message
	 * @param MessageId ID of message to be deleted
	 * @return successful deletion status
	 */
	public boolean deleteProcessedMessage(String MessageId){
		/* 
		 * To be used in event, for deleting processed messages 
		 * after "sending"/depleting them.
		 */
		for(int i=0; i<processedMessages.size(); i++){
			if(processedMessages.get(i).getId() == MessageId){
				this.depletedProcMessages++;
				this.depletedProcMessagesSize += this.processedMessages.get(i).getSize();
				this.processedMessages.remove(i);
				return true;
			}
		}
		return false;
	}
	
	public long getNrofDeletedMessages() {
		return this.nrofDeletedMessages;
	}
	
	public long getNrofDepletedProcMessages() {
		return this.depletedProcMessages;
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
	public long getDepletedProcMessagesBW(boolean reporting) {
		long procBW = this.depletedProcMessagesSize - this.oldDepletedProcMessagesSize;
		if (reporting) {
			this.oldDepletedProcMessagesSize = this.depletedProcMessagesSize;
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
	

	
	public Message getNewestProcessMessage(){
		Message newest = null;
		for (Message m : this.processMessages) {
			
			if (newest == null ) {
				newest = m;
			}
			else if (newest.getReceiveTime() < m.getReceiveTime()) {
				newest = m;
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

}
