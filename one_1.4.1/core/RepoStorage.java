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
	//private HashMap<String, Messages> storedMessages;

	/** Host where this file system belongs to */
	private DTNHost host;
	private double compressionRate;

	/** size of the storage space */
	private long storageSize;
	private long processSize;
	private long processedSize;
	private long nrofDeletedMessages;
	private long depletedProcMessages;
	private long depletedProcMessagesSize;
	private long depletedStoredMessages;
	private long depletedStoredMessagesSize;
	private double totalReceivedMessages;

	private String MessageId;

	protected ArrayList<Message> storedMessages;
	protected ArrayList<Message> processMessages;
	protected ArrayList<Message> processedMessages;

	/** value to keep track of used storage */
	//protected long usedStorage;

	protected Collection<Message> messages;

	public void init(DTNHost dtnHost, long storageSize, long processSize, double compressionRate) {
		this.host = dtnHost;
		//this.messages = new Collection<Message>();
		this.storedMessages = new ArrayList<Message>();
		this.processMessages = new ArrayList<Message>();
		this.processedMessages = new ArrayList<Message>();
		this.storageSize = 0;
		this.processSize = 0;
		this.processedSize = 0;
		this.nrofDeletedMessages = 0;
		this.totalReceivedMessages = 0;
		this.depletedProcMessages = 0;
		this.depletedProcMessagesSize = 0;
		this.depletedStoredMessages = 0;
		this.depletedStoredMessagesSize = 0;
		this.compressionRate = 2;
		if (this.getHost().hasStorageCapability()){
			this.storageSize = storageSize;
		}
		if (this.getHost().hasProcessingCapability()){
			this.processSize = processSize;
			this.processedSize = (long)(processSize/this.compressionRate);
			this.compressionRate = compressionRate;
		}
	}

	public long getTotalStorageSpace() {
		return this.storageSize;
	}
	
	public long getTotalProcessStorageSpace() {
		return this.processSize;
	}
	
	/** Create message collection stored return method */
	
	public Collection<Message> getStoredMessagesCollection() {
		this.messages = storedMessages;
		return this.messages;
	}

	/** Create message ArrayList stored return method */
	
	public ArrayList<Message> getStoredMessages() {
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
	

	/**
	 * TODO: Add a function, to start processing messages as soon as they 
	 * enter processing storage (processStorage), by reducing their size at 
	 * the set compression rate, with a delay, and before it sends them off 
	 * (depletes the messages), it stores them in processedStorage.
	 */
	
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
				this.storedMessages.add(sm);
			}
			else if (this.getHost().hasProcessingCapability) {
				if (this.isProcessingFull()) {
					this.storedMessages.add(sm);
				}
				else if (((String) sm.getProperty("type")).equalsIgnoreCase("proc")) {
					this.processMessages.add(sm);
				}
			}
			this.totalReceivedMessages++;
			/* add space used in the storage space */
			//System.out.println("There is " + this.getStoredMessagesSize() + " storage used");
		}
	}
	
	/**
	 * Adds a message to the depleted stored messages
	 * @param sm The message to add
	 * @return true if the message is added correctly
	 */			
	public void addToDeplStoredMessages(Message sm) {
		if (sm != null) {
			this.depletedStoredMessages++;
			this.depletedStoredMessagesSize += sm.getSize();
		}
	}
	
	/**
	 * Returns a stored message by ID.
	 * @param MessageId ID of the file
	 * @return The message
	 */
	public Message getStoredMessage(String MessageId) {
		Message storedMessage = this.storedMessages.get(0);
		for (Message temp : storedMessages){
			if (temp.getId() == MessageId){
				int i = this.storedMessages.indexOf(temp);
				storedMessage = this.storedMessages.get(i);
			}
		}
		return storedMessage;
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
		processedMessage.addProperty("type", "processed");
		this.deleteProcMessage(procMessage.getId());
		return true;
	}
	
	/**
	 * Returns the number of files this file system has
	 * @return How many files this file system has
	 */
	public int getNrofMessages() {
		return this.storedMessages.size();
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
	public long getStoredMessagesSize() {
		long storedMessagesSize = 0;
		for (int i=0; i<this.storedMessages.size(); i++){
			Message temp = this.storedMessages.get(i);
			storedMessagesSize += temp.getSize();
		}
		return storedMessagesSize;
	}

	/**
	 * Returns the total size of messages to be processed in this storage system
	 * @return The size of the used processing storage in this storage system
	 */
	public long getProcMessagesSize() {
		long storedMessagesSize = 0;
		for (int i=0; i<this.processMessages.size(); i++){
			Message temp = this.processMessages.get(i);
			storedMessagesSize += temp.getSize();
		}
		return storedMessagesSize;
	}

	/**
	 * Returns the total size of messages to be processed in this storage system
	 * @return The size of the used processing storage in this storage system
	 */
	public long getProcessedMessagesSize() {
		long storedMessagesSize = 0;
		for (int i=0; i<this.processedMessages.size(); i++){
			Message temp = this.processedMessages.get(i);
			storedMessagesSize += temp.getSize();
		}
		return storedMessagesSize;
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
		for(int i=0; i<this.storedMessages.size(); i++){
			if(this.storedMessages.get(i).getId() == MessageId){
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
	public boolean deleteStoredMessage(String MessageId){
		for(int i=0; i<storedMessages.size(); i++){
			if(storedMessages.get(i).getId() == MessageId){
				this.storedMessages.remove(i);
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
	
	public long getNrofDepletedStoredMessages() {
		return this.depletedStoredMessages;
	}
	
	public double getOverallMeanIncomingSpeed() {
		return (this.totalReceivedMessages/SimClock.getTime());
	}
	
	public double getOverallDepletedProcMessagesBW() {
		return (this.depletedProcMessagesSize/SimClock.getTime());
	}
	
	public double getOverallDepletedStoredMessagesBW() {
		return (this.depletedStoredMessagesSize/SimClock.getTime());
	}
	
	public boolean clearAllStoredMessages(){
		this.storedMessages.clear();
		return true;
	}
		
	/**
	 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	 * TODO: Create methods for returning:
	 * free storage space;
	 * total storage space;
	 * add message to storage space
	 * TODO: Create methods for obtaining:
	 * deletion of messages from storage space;
	 * storage space full;
	 * free up storage if there is none left;
	 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	 */

	public long getFreeStorageSpace() {
		long usedStorage = this.getStoredMessagesSize();
		//System.out.println("There is " + usedStorage + " storage used in "+this.getHost());
		long freeStorage = this.storageSize - usedStorage;
		//System.out.println("There is "+freeStorage+" free storage space in "+this.getHost());
		return freeStorage;
	}

	public boolean isStorageFull() {
		long usedStorage = this.getStoredMessagesSize();
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
	}

	public boolean isProcessingEmpty() {
		long usedProc = this.getProcMessagesSize();
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

	public boolean isProcessedFull() {
		long usedProcessed = this.getProcessedMessagesSize();
		//try {
		//	System.setOut(new PrintStream(new FileOutputStream("log.txt")));
		//} catch(Exception e) {}
		if (usedProcessed >= this.processedSize - 100000000){
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
	
	public Message getOldestStoredMessage(){
		Message oldest = null;
		for (Message m : this.storedMessages) {
			
			if (oldest == null ) {
				oldest = m;
			}
			else if (oldest.getReceiveTime() > m.getReceiveTime()) {
				oldest = m;
			}
		}
		return oldest;
	}
	
	public Message getOldestProcStoredMessage(){
		Message oldest = null;
		for (Message m : this.storedMessages) {
			
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
				Message oldest = this.getOldestStoredMessage();
				if (this.isProcessingFull()) {
					String mId = oldest.getId();
					this.deleteStoredMessage(mId);
					this.nrofDeletedMessages++;
				}
				else {
					this.processMessages.add(oldest);
					if (!this.isProcessedFull()) {
						this.processMessage(this.getOldestProcessMessage());
					}
				}
				//System.out.println("There is " + this.getStoredMessagesSize() + " storage used in "+this.getHost().name);
				//String mId = oldest.getId(); 
				//this.deleteStoredMessage(mId);
				//this.nrofDeletedMessages++;
			}
		}
		else if (deleteAll == true){
			this.storedMessages.clear();
		};
	}
	

		
	
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
