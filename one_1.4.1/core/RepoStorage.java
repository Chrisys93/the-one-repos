package core;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.ArrayList;

import core.Settings;
import core.SimClock;

/** 
 * @author Daniele Bonaldo, University of Padua
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
	private long depletedStaticMessages;
	private long oldDepletedStaticMessagesSize;
	private long depletedStaticMessagesSize;
	private double totalReceivedMessages;
	private double totalReceivedMessagesSize;

	protected ArrayList<Message> staticMessages;
	protected ArrayList<Message> processMessages;
	protected ArrayList<Message> processedMessages;
	protected ArrayList<Message> storedMessages;

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
		this.depletedStaticMessages = 0;
		this.oldDepletedStaticMessagesSize = 0;
		this.depletedStaticMessagesSize = 0;
		if (this.getHost().hasProcessingCapability()){
			//this.processSize = processSize;
			this.compressionRate = compressionRate;
			double processedRatio = this.compressionRate*2;
			this.processedSize = (long)(this.storageSize/processedRatio);
		}
	}

	public long getTotalStorageSpace() {
		return this.storageSize;
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
			if (this.getOldestStaticMessage() != null) {
				this.deleteStaticMessage(this.getOldestStaticMessage().getId());
				this.nrofDeletedMessages++;
			}
			else if(!this.isProcessedFull()){
				this.processMessage(this.getOldestProcessMessage());
			}
			else {
				this.deleteProcMessage(this.getOldestProcessMessage().getId());
				this.nrofDeletedMessages++;
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
	public void addToDeplProcMessages(Message sm) {
		if (sm != null) {
			if (((String) sm.getProperty("type")).equalsIgnoreCase("proc")) {
				this.depletedProcMessages++;
				this.depletedProcMessagesSize += sm.getSize();
				this.processMessages.remove(sm);
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
	
	public long getNrofDepletedStaticMessages() {		
		return this.depletedStaticMessages;
	}
	
	public double getOverallMeanIncomingMesssageNo() {
		return (this.totalReceivedMessages/SimClock.getTime());
	}
	
	public double getOverallMeanIncomingSpeed() {
		return (this.totalReceivedMessagesSize/SimClock.getTime());
	}
	
	public long getDepletedProcMessagesBW() {
		long procBW = this.depletedProcMessagesSize - this.oldDepletedProcMessagesSize;
		this.oldDepletedProcMessagesSize = this.depletedProcMessagesSize;
		return (procBW);
	}
	
	public long getDepletedStaticMessagesBW() {
		long statBW = this.depletedStaticMessagesSize - this.oldDepletedStaticMessagesSize;
		this.oldDepletedStaticMessagesSize = this.depletedStaticMessagesSize;
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
		if (usedProcessed >= this.processedSize - 1000000){
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
	
	/*public Message getOldestProcStaticMessage(){
		Message oldest = null;
		for (Message m : this.staticMessages) {
			
			if (((String) m.getProperty("type")).equalsIgnoreCase("proc")) {
				if (oldest == null) {
					oldest = m;
				}
				else if (oldest.getReceiveTime() > m.getReceiveTime()) {
					oldest = m;
				}
			}
		}	
		return oldest;
	}

	public void deleteMessagesForSpace(boolean deleteAll){
		if (this.isStorageFull() && deleteAll == false){
			for (int i=0; i<1000; i++){
				Message oldest = this.getOldestStaticMessage();
				if (this.isProcessingFull()) {
					String mId = oldest.getId();
					this.deleteStaticMessage(mId);
					this.nrofDeletedMessages++;
				}
				else {
					this.processMessages.add(oldest);
					if (!this.isProcessedFull()) {
						this.processMessage(this.getOldestProcessMessage());
					}
				}
				//System.out.println("There is " + this.getStaticMessagesSize() + " storage used in "+this.getHost().name);
				//String mId = oldest.getId(); 
				//this.deleteStaticMessage(mId);
				//this.nrofDeletedMessages++;
			}
		}
		else if (deleteAll == true){
			this.staticMessages.clear();
		};
	}*/
	

		
	
	/**
	 * Returns a String presentation of this file system
	 * @return A String presentation of this file system
	 */
	/*public String toString() {
		return getClass().getSimpleName() + " of " + 
			this.getHost().toString() + " with " + getNrofFiles() 
			+ " files";
	}*/


}
