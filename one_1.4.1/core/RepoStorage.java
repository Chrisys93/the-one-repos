package core;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.ArrayList;

import core.Settings;

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

	/** size of the storage space */
	private long storageSize;

	private String MessageId;

	protected ArrayList<Message> storedMessages;

	/** value to keep track of used storage */
	//protected long usedStorage;

	protected Collection<Message> messages;

	private Message sm;

	public void init(DTNHost dtnHost, long storageSize) {
		this.host = dtnHost;
		//this.messages = new Collection<Message>();
		this.storedMessages = new ArrayList<Message>();
		this.storageSize = 0;
		if (this.getHost().hasStorageCapability()){
			this.storageSize = storageSize;
		}	
	}

	public long getTotalStorageSpace() {
		return this.storageSize;
	}
	
	/** Create message collection stored return method */
	
	public Collection<Message> getStoredMessagesCollection() {
		Collection<Message> messages = storedMessages;
		return this.messages;
	}

	/** Create message ArrayList stored return method */
	
	public ArrayList<Message> getStoredMessages() {
		return this.storedMessages;
	}
	
	/**
	 * Adds a message to the storage system
	 * @param sm The message to add
	 * @return true if the message is added correctly
	 */
	public void addToStoredMessages(Message sm) {
		this.storedMessages.add(sm);
		/* add space used in the storage space */
		System.out.println("There is " + this.getStoredMessagesSize() + " storage used");
	}
	
	/**
	 * Returns a file by hash.
	 * @param hash hash of the file
	 * @return The file
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
	 * Returns the number of files this file system has
	 * @return How many files this file system has
	 */
	public int getNrofMessages() {
		return this.storedMessages.size();
	}

	public long getStoredMessagesSize() {
		long storedMessagesSize = 0;
		for (int i=0; i<this.storedMessages.size(); i++){
			Message temp = this.storedMessages.get(i);
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
		for(int i=0; i<storedMessages.size(); i++){
			if(storedMessages.get(i).getId() == MessageId){
				answer =  true;
			}
			else{
				answer =  false;
			}
		}
		return answer;
	}
	
	public boolean deleteStoredMessage(String MessageId){
		boolean answer = false;
		for(int i=0; i<storedMessages.size(); i++){
			if(storedMessages.get(i).getId() == MessageId){
				this.storedMessages.remove(i);
				answer = true;
			}
			else{
				answer = false;
			}
		}
		return answer;
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
		System.out.println("There is " + usedStorage + " storage used");
		long freeStorage = this.storageSize - usedStorage;
		//System.out.println("There is "+freeStorage+" free storage space");
		return freeStorage;
	}

	public boolean isStorageFull() {
		long freeStorage = this.getFreeStorageSpace();
		try {
			System.setOut(new PrintStream(new FileOutputStream("log.txt")));
		} catch(Exception e) {}
		if (freeStorage >= 1100000){
			System.out.println("There is enough storage space: " + freeStorage);
			return false;
		}
		else{
			System.out.println("There is not enough storage space: " + freeStorage);
			return true;
		}
	}

	public void deleteMessagesForSpace(boolean deleteAll){
		if (this.isStorageFull() && deleteAll == false){
			for (int i=0; i<10; i++){
				Message oldest = null;
				for (Message m : this.storedMessages) {
					
					if (oldest == null ) {
						oldest = m;
					}
					else if (oldest.getReceiveTime() > m.getReceiveTime()) {
						oldest = m;
					}
				}
				String mId = oldest.getId();
				this.deleteStoredMessage(mId);
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
