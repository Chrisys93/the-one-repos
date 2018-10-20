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
 * Router that will deliver messages only to the final recipient.
 */
public class ReposDirectDeliveryRouter extends ActiveRouter {

	public ReposDirectDeliveryRouter(Settings s) {
		super(s);
	}
	
	protected ReposDirectDeliveryRouter(ReposDirectDeliveryRouter r) {
		super(r);
	}

	@Override
	public void init(DTNHost host, List<MessageListener> mListeners) {
		super.init(host, mListeners);
	}
	
	@Override
	public void update() {
		super.update();
		if (isTransferring() || !canStartTransfer()) {
			return; // can't start a new transfer
		}
		
		// Try only the messages that can be delivered to final recipient
		if (exchangeDeliverableMessages() != null) {
			return; // started a transfer
		}
		
		//List<Connection> connections = new ArrayList<Connection>();
		List<Message> messages = new ArrayList<Message>(super.getMessageCollection());
		for (Connection con : this.getHost().getConnections()) {
			super.sortByQueueMode(messages);
			DTNHost from = con.getOtherNode(this.getHost());
			if(con.isUp() && con.getMessage() != null){		
				Message temp = con.getMessage();
				if (messageTransferred(temp.getId(), from) != null){
					//System.out.println("Message " + temp.getId() + " was successfully stored in " + this.getHost());
				}
			}
		}
	}

	public void addMessageToStorageSpace(Connection con) {
		/** get message collection in storage, 
		 * from dtnHost and the number of messages,
		 * multiply and obtain total amount of space used,
		 * then find free space via difference.
		 */
		long freeStorage = this.getHost().getStorageSystem().getFreeStorageSpace();

		//System.out.println("There is "+freeStorage+" free storage space");
		
		if (!this.getHost().getStorageSystem().isStorageFull()){
			this.getHost().getStorageSystem().addToStoredMessages(con.getMessage());
			//System.out.println("Message has been added to storage, with no problem");
		}
		else {
			//System.out.println("The current host is: " + this.getHost());
			this.getHost().getStorageSystem().deleteMessagesForSpace(false);
			//if(con.ifUp()){
				this.getHost().getStorageSystem().addToStoredMessages(con.getMessage());
			//}			
			//System.out.println("Message has been added to storage, by deleting other messages");
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
		try {
			System.setOut(new PrintStream(new FileOutputStream("logupdate.txt")));
		} catch(Exception e) {}
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
	public ReposDirectDeliveryRouter replicate() {
		return new ReposDirectDeliveryRouter(this);
	}
}
