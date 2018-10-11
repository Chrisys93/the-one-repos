package core;

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
	private int storageSize;

	private String MessageId;

	private ArrayList<Message> storedMessages;

	private Collection<Message> messages;

	public void init(DTNHost dtnHost) {
		this.host = dtnHost;
		this.storedMessages = new ArrayList <Message>();
		//this.messages = new Collection<Message>;
	}
	
	/** Create message collection stored return method */
	
	private Collection<Message> getStoredMessagesCollection() {
		Collection<Message> messages = storedMessages;
		return this.messages;
	}
	
	/**
	 * Adds a file to the file system
	 * @param f The file to add
	 * @return true if the file is added correctly
	 */
	public boolean addToStoredMessages(Message sm) {
		this.storedMessages.add(sm);
		/* add space used in the storage space */
		return true;
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
	 * Returns a String presentation of this file system
	 * @return A String presentation of this file system
	 */
	/*public String toString() {
		return getClass().getSimpleName() + " of " + 
			this.getHost().toString() + " with " + getNrofFiles() 
			+ " files";
	}*/


}
