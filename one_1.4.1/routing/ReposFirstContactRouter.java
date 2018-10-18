/* 
 * Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details. 
 */
package routing;

import java.lang.*;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import core.Connection;
import core.Message;
import core.MessageListener;
import core.Settings;
import core.DTNHost;
import core.SettingsError;
import core.RepoStorage;
import core.SimError;
import core.SimClock;
import core.Application;
import core.Tuple;



/**
 * First contact router which uses only a single copy of the message 
 * (or fragments) and forwards it to the first available contact.
 */
public class ReposFirstContactRouter extends ActiveRouter {

	/** Message storage size -setting id ({@value}). Integer value in bytes.*/
	//public static final String STORE_SIZE_S = "storageSize";


	/** hosts to be used as sides of connections */
	private DTNHost to;
	private DTNHost from;

	//private long storageSize;

	/** arraylist needed for storing messages */
	//protected ArrayList<Message> storedMessages;
	
	/**
	 * Constructor. Creates a new message router based on the settings in
	 * the given Settings object.
	 * @param s The settings object
	 */
	public ReposFirstContactRouter(Settings s) {
		super(s);
	}

	/**
	 * Copy constructor.
	 * @param r The router prototype where setting values are copied from
	 */
	protected ReposFirstContactRouter(ReposFirstContactRouter r) {
		super(r);
	}

	@Override
	public void init(DTNHost host, List<MessageListener> mListeners) {
		super.init(host, mListeners);
		//this.storedMessages = new ArrayList<Message>();
		//this.usedStorage = 0;
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
		
		
		/* if there are no messages for connected (repos),
		 * create message for connected (repos)
		 */
		

		if (isTransferring() || !canStartTransfer()) {
			return; 
		}
		
		if (exchangeDeliverableMessages() != null) {
			return; 
		}

		/* modify the following\|/ to something like tryAllMessages(), with
		 * the arguments being only connections to repos and sending
		 * all the messages
		 */

		
		
		/* get all connections and all messages (getConnections and 
		 * getMessagesForConnected()) 
		 * and isolate the CONNECTED repos. May need to modify 
		 * getMessagesForConnected to only work with repos
		 */

		/*List<Connection> connections = new ArrayList<Connection>();
		List<Message> messages = new ArrayList<Message>(super.getMessageCollection());
		for (Connection con : this.getHost().getConnections()) {
			super.sortByQueueMode(messages);
			DTNHost to = con.getOtherNode(this.getHost());
			if(con.isUp() && con.getMessage() != null){
				System.out.println("The next message is destined to " + con.getMessage().getTo() + " coming from " +con.getMessage().getFrom()+ " and this host is " + this.getHost());			
				Message temp = con.getMessage();
				if (temp.getTo() == to && to.name.toString().contains("r") && tryAllMessages(con, messages) != null){
					connections.add(con);
				}
			}
		}
			
		tryMessagesToConnections(messages, connections);*/
	}
	
	/**
	 * Returns a list of message-connections tuples of the messages whose
	 * recipient is some host that we're connected to at the moment.
	 * @return a list of message-connections tuples
	 */
	/*@Override
	protected List<Tuple<Message, Connection>> getMessagesForConnected() {
		if (getNrofMessages() == 0 || getConnections().size() == 0) {
	*/		/* no messages -> empty list */
	/*		return new ArrayList<Tuple<Message, Connection>>(0); 
		}

		List<Tuple<Message, Connection>> forTuples = 
			new ArrayList<Tuple<Message, Connection>>();
		for (Message m : getMessageCollection()) {
			for (Connection con : getConnections()) {
				DTNHost to = con.getOtherNode(this.getHost());
				System.out.println("The next message is destined to " + m.getTo() + " coming from " +m.getFrom()+ " and this host is " + this.getHost());
				if (m.getTo() == to && to.name.toString().contains("r")) {
					forTuples.add(new Tuple<Message, Connection>(m,con));
				}
			}
		}
		
		return forTuples;
	}*/

	

	public void addMessageToStorageSpace(Connection con) {
		/** get message collection in storage, 
		 * from dtnHost and the number of messages,
		 * multiply and obtain total amount of space used,
		 * then find free space via difference.
		 */
		long freeStorage = this.getHost().getStorageSystem().getFreeStorageSpace();

		System.out.println("There is "+freeStorage+" free storage space");
		
		if (!this.getHost().getStorageSystem().isStorageFull()){
			this.getHost().getStorageSystem().addToStoredMessages(con.getMessage());
			System.out.println("Message has been added to storage, with no problem");
		}
		else {
			System.out.println("The current host is: " + this.getHost());
			this.getHost().getStorageSystem().deleteMessagesForSpace(false);
			//if(con.ifUp()){
				this.getHost().getStorageSystem().addToStoredMessages(con.getMessage());
			//}			
			System.out.println("Message has been added to storage, by deleting other messages");
		}
	}

	/**
	 * This method should be called (on the receiving host) after a message
	 * was successfully transferred. The transferred message is put to the
	 * message buffer unless this host is the final recipient of the message.
	 * @param id Id of the transferred message
	 * @param from Host the message was from (previous hop)
	 * @return The message that this host received
	 */
	@Override
	public Message messageTransferred(String id, DTNHost from) {
		Message incoming = removeFromIncomingBuffer(id, from);
		boolean isFinalRecipient;
		boolean isFirstDelivery; // is this first delivered instance of the msg
		
		
		if (incoming == null) {
			throw new SimError("No message with ID " + id + " in the incoming "+
					"buffer of " + this.getHost());
		}
		
		incoming.setReceiveTime(SimClock.getTime());
		
		// Pass the message to the application (if any) and get outgoing message
		Message outgoing = incoming;
		for (Application app : getApplications(incoming.getAppID())) {
			// Note that the order of applications is significant
			// since the next one gets the output of the previous.
			outgoing = app.handle(outgoing, this.getHost());
			if (outgoing == null) break; // Some app wanted to drop the message
		}
		
		Message aMessage = (outgoing==null)?(incoming):(outgoing);
		// If the application re-targets the message (changes 'to')
		// then the message is not considered as 'delivered' to this host.
		isFinalRecipient = aMessage.getTo() == this.getHost();
		isFirstDelivery = isFinalRecipient &&
		!isDeliveredMessage(aMessage);

		if (!isFinalRecipient && outgoing!=null) {
			// not the final recipient and app doesn't want to drop the message
			// -> put to buffer
			addToMessages(aMessage, false);
		}
		else if (isFirstDelivery) {
			this.deliveredMessages.put(id, aMessage);
			if (this.getHost().hasStorageCapability()) {
				for (Connection con:this.getHost().getConnections()){
					if (con.getOtherNode(this.getHost()) == from){
						System.out.println("Repo " + this.getHost() + " isDeliveredMessage " +isDeliveredMessage(aMessage));
						this.addMessageToStorageSpace(con);
						//System.out.println("Message added to storage");	
					}
				}
			}
		}
		
		for (MessageListener ml : this.mListeners) {
			ml.messageTransferred(aMessage, from, this.getHost(),
					isFirstDelivery);
		}
		
		return aMessage;
	}
	
	@Override
	protected void transferDone(Connection con) {
		/* this is where the start of the storage part could be implemented
		 * Nope, it's not! Use isDeliveredMessage instead, to check if the
		 * message of the connection con was delivered (to this host) and if yes,
		 * THEN add the message to storage.
		 * Soooo it seems like this literally only gets called when the node is 
		 * NOT the target!
		 */
		boolean isFinalRepoRecipient = con.getMessage().getTo() == this.getHost();
		boolean isFirstRepoDelivery = isFinalRepoRecipient && !isDeliveredMessage(con.getMessage());
		DTNHost from = con.getOtherNode(this.getHost());
		DTNHost to = this.getHost();
		if (this.getHost().hasStorageCapability()) {
			
			//System.out.println("Repo " + this.getHost() + " isDeliveredMessage " +isDeliveredMessage(con.getMessage()));
			if (!isDeliveredMessage(con.getMessage())){
				this.addMessageToStorageSpace(con);
				//System.out.println("Message added to storage");	
			}		
		}
		//System.out.println("Transfer done " + this.getHost().name);
		else if (super.getMessage(con.getMessage().getId()) != null){		
			/* don't leave a copy for the sender */
			this.deleteMessage(con.getMessage().getId(), false);
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
	/*private long parseLong(String value) {
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
	}*/

}
