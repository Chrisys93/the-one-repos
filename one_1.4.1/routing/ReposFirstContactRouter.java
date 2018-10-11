/* 
 * Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details. 
 */
package routing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import core.Connection;
import core.Message;
import core.Settings;
import core.DTNHost;
import core.SettingsError;
import core.RepoStorage;



/**
 * First contact router which uses only a single copy of the message 
 * (or fragments) and forwards it to the first available contact.
 */
public class ReposFirstContactRouter extends ActiveRouter {

	/**
	 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	 * Insert storage setting for storage 
	 * capacity.
	 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	 */

	
	/** Message storage size -setting id ({@value}). Integer value in bytes.*/
	public static final String STORE_SIZE_S = "storageSize";

	/** size of the storage space */
	private long storageSize;

	/** hosts to be used as sides of connections */
	private DTNHost to;
	private DTNHost from;

	/** host to be used in methods */
	private DTNHost dtnHost;

	/** arraylist needed for storing messages */
	private ArrayList<Message> storedMessages;

	/** value to keep track of used storage */
	private long usedStorage;
	
	/**
	 * Constructor. Creates a new message router based on the settings in
	 * the given Settings object.
	 * @param s The settings object
	 */
	public ReposFirstContactRouter(Settings s) {
		super(s);
		this.storageSize = 100000000; //defaults to 100M buffer
		this.storedMessages = new ArrayList<Message>();
		this.usedStorage = 0;
		
		/** \/ This \/ needs to be solved in the router part! */
		if (s.contains(STORE_SIZE_S)) {
			this.storageSize = this.parseLong(STORE_SIZE_S);
		}
		
	}
	
	/**
	 * Copy constructor.
	 * @param r The router prototype where setting values are copied from
	 */
	protected ReposFirstContactRouter(ReposFirstContactRouter r) {
		super(r);
	}
	
	@Override
	protected int checkReceiving(Message m) {
		int recvCheck = super.checkReceiving(m); 
		
		if (recvCheck == RCV_OK) {
			/* don't accept a message that has already traversed this node */
			if (m.getHops().contains(getHost())) {
				recvCheck = DENIED_OLD;
			}
		}
		
		return recvCheck;
	}
			
	@Override
	public void update() {
		super.update();
		
		/* get all connections and all messages (getConnections and 
		 * getMessagesForConnected()) 
		 * and isolate the CONNECTED repos. May need to modify 
		 * getMessagesForConnected to only work with repos
		 */
		
		
		/* if there are no messages for connected (repos),
		 * create message for connected (repos)
		 */
		

		if (isTransferring() || !canStartTransfer()) {
			return; 
		}
		
		//if (exchangeDeliverableMessages() != null) {
		//	return; 
		//}

		/* modify the following\|/ to something like tryAllMessages(), with
		 * the arguments being only connections to repos and sending
		 * all the messages
		 */
		
		
		List<Connection> connections = new ArrayList<Connection>();
		List<Message> messages = new ArrayList<Message>(super.getMessageCollection());
		for (Connection con : getConnections()) {
			int i = 0;
			super.sortByQueueMode(messages);
			DTNHost to = con.getOtherNode(getHost());
			if (to.name.toString().contains("r")){
				if (tryAllMessages(con, messages) != null){
					connections.add(con);
					i++;
					return;
				}
				/*else { m_new = Message(getHost(), to, messages.size(), 1000000)
					if (tryAllMessages(con, messages) != null){
						connections.add(con);
						i++;						
						return;
					}*/
				return;					
			}
			return;
		}
			
		tryMessagesToConnections(messages, connections);
		
		//tryAllMessagesToAllConnections();
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
	public long getTotalStorageSpace() {
		return this.storageSize;
	}

	public ArrayList <Message> addMessageToStorageSpace(Connection con) {
		/** get message collection in storage, 
		 * from dtnHost and the number of messages,
		 * multiply and obtain total amount of space used,
		 * then find free space via difference.
		 */
		DTNHost to = this.getHost();
		DTNHost from = con.getOtherNode(to);
		if (!this.isStorageFull()){
			to.getStorageSystem().addToStoredMessages(con.getMessage());
			// account for message space taken in storage now
			this.storedMessages.add(con.getMessage());
		}
		else {
			deleteMessagesForSpace(to, false);
			to.getStorageSystem().addToStoredMessages(con.getMessage());
			// account for message space taken in storage now
			this.storedMessages.add(con.getMessage());
		}
		return storedMessages;
	}

	public long getFreeStorageSpace(DTNHost dtnHost) {
		for (int i=0; i<this.storedMessages.size(); i++){
			Message temp = this.storedMessages.get(i);
			this.usedStorage += temp.getSize();
		}
		long freeStorage = this.getTotalStorageSpace() - usedStorage;
		return freeStorage;
	}

	public boolean isStorageFull() {
		long freeStorage = this.getFreeStorageSpace(this.getHost());
		if (freeStorage < 900000){
			return true;
		}
		else{
			return false;
		}
	}

	public void deleteMessagesForSpace(DTNHost dtnHost, boolean deleteAll){
		if (this.isStorageFull() && deleteAll == false){
			for (int i=0; i<100; i++){
				String messageId = this.getOldestMessage(true).getId();
				this.dtnHost.getStorageSystem().deleteStoredMessage(messageId);
			}
		}
		else if (deleteAll == true){
			dtnHost.getStorageSystem().clearAllStoredMessages();
		}
	}

	@Override
	protected Message getOldestMessage(boolean excludeMsgBeingSent) {
		Collection<Message> messages = this.storedMessages;
		Message oldest = null;
		for (Message m : messages) {
			
			if (excludeMsgBeingSent && isSending(m.getId())) {
				continue; // skip the message(s) that router is sending
			}
			
			if (oldest == null ) {
				oldest = m;
			}
			else if (oldest.getReceiveTime() > m.getReceiveTime()) {
				oldest = m;
			}
		}
		
		return oldest;
	}
	
	@Override
	protected void transferDone(Connection con) {
		/* don't leave a copy for the sender */
		//this.deleteMessage(con.getMessage().getId(), false);
		/* this is where the start of the storage part could be implemented*/
		DTNHost from = con.getOtherNode(to);
		DTNHost to = this.getHost();	
		if (to.hasStorageCapability()) {
			this.addMessageToStorageSpace(con);	
		}
	}
		
	@Override
	public ReposFirstContactRouter replicate() {
		return new ReposFirstContactRouter(this);
	}

	/** \/ This \/ needs to be solved in the router part!
	 * 		And could be needed for the MessageCreateEvent,
	 *		as well. 
	 */
	private long parseLong(String value) {
		long number;
		long multiplier = 1;
		
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
			number = (long) (Double.parseDouble(value) * multiplier);
		} catch (NumberFormatException e) {
			throw new SettingsError("Invalid numeric setting '" + value + 
					"' for fileSize\n" + e.getMessage());
		}
		return number;
	}

}
