package core;

import java.util.Collection;
import java.util.HashMap;
import java.util.ArrayList

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

	private String MessageId;

	private ArrayList<Message> storedMessages;

	private Collection<Message> messages;

	public void init(DTNHost dtnHost) {
		this.host = dtnHost;
		this.storedMessages = new ArrayList <Message> storedMessages;
		this.messages = new Collection<Message> messages;
	}
	
	/** Create message collection stored return method */
	
	private Collection<Message> getStoredMessagesCollection() {
		Collection<Message> messages = new Collection<Message>(storedMessages);
		return this.messages.values();
	}
	
	/**
	 * Adds a file to the file system
	 * @param f The file to add
	 * @return true if the file is added correctly
	 */
	public boolean addToStoredMessages(Message sm) {
		this.storedMessages.add(sm);
		return true;
	}
	
	/**
	 * Returns a file by hash.
	 * @param hash hash of the file
	 * @return The file
	 */
	public Message getStoredMessage(String MessageId) {
		return this.storedMessages.get(MessageId);
	}
	
	/**
	 * Returns the number of files this file system has
	 * @return How many files this file system has
	 */
	public int getNrofMessages() {
		return this.storedMessages.size();
	}

	/**
	 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	 * TODO: Create methods for returning:
	 * free storage space;
	 * total storage space;
	 * TODO: Create methods for obtaining:
	 * deletion of messages from storage space;
	 * storage space full;
	 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	 */
	
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
		for(int i=0; i<storedMessages.size(); i++)
			if(storedMessages.get(i).getId() == MessageId){
				return true;
			}
			else{
				return false;
			}
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

	private int parseInt(String value) {
		int number;
		int multiplier = 1;
		
		if (value.endsWith("k")) {
			multiplier = 1000;
		}
		else if (value.endsWith("M")) {
			multiplier = 1000000;
		}
		else if (value.endsWith("G")) {
			multiplier = 1000000000;
		}
		
		if (multiplier > 1) { // take the suffix away before parsing
			value = value.substring(0,value.length()-1);
		}
		
		try {
			number = (int) (Double.parseDouble(value) * multiplier);
		} catch (NumberFormatException e) {
			throw new SettingsError("Invalid numeric setting '" + value + 
					"' for fileSize\n" + e.getMessage());
		}
		return number;
}


}
