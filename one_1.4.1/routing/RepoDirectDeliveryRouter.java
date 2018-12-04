/* 
 * Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details. 
 */
package routing;

import java.util.List;

import core.Application;
import core.Connection;
import core.Message;
import core.Settings;
import core.SimClock;
import core.Tuple;

/**
 * Router that will deliver messages only to the final recipient.
 */
public class RepoDirectDeliveryRouter extends ActiveRouter {


	/** hosts to be used as sides of connections */
	private long totalBufferDeletedMessages;
	
	public RepoDirectDeliveryRouter(Settings s) {
		super(s);
	}
	
	protected RepoDirectDeliveryRouter(RepoDirectDeliveryRouter r) {
		super(r);
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
	}
	
	@Override
	protected Tuple<Message, Connection> tryMessagesForConnected(
			List<Tuple<Message, Connection>> tuples) {
		if (tuples.size() == 0) {
			return null;
		}
		
		for (Tuple<Message, Connection> t : tuples) {
			Message m = t.getKey();
			Connection con = t.getValue();
			//if (con.getOtherNode(this.getHost()).name.contains("r")) {
				//if(!this.getHost().name.contains("r")) {
					if (startTransfer(m, con) == RCV_OK) {
						return t;
					}
				//}	
			//}
		}
		
		return null;
	}
	
	/** 
	 * Removes messages from the buffer (oldest first) until
	 * there's enough space for the new message.
	 * @param size Size of the new message 
	 * transferred, the transfer is aborted before message is removed
	 * @return True if enough space could be freed, false if not
	 */
	@Override
	protected boolean makeRoomForMessage(int size){
		if (size > this.getBufferSize()) {
			return false; // message too big for the buffer
		}
			
		int freeBuffer = this.getFreeBufferSize();
		/* delete messages from the buffer until there's enough space */
		while (freeBuffer < size) {
			Message m = getOldestMessage(true); // don't remove msgs being sent

			if (m == null) {
				return false; // couldn't remove any more messages
			}			
			
			/* delete message from the buffer as "drop" */
			deleteMessage(m.getId(), true);
			freeBuffer += m.getSize();
			this.totalBufferDeletedMessages ++;
		}
		
		return true;
	}
	
	@Override
	protected void transferDone(Connection con) {
		/* don't leave a copy for the sender */
		//this.deleteMessage(con.getMessage().getId(), false);
		/* this is where the start of the storage part could be implemented
		 * Nope, it's not! Use isDeliveredMessage instead, to check if the
		 * message of the connection con was delivered (to this host) and if yes,
		 * THEN add the message to storage.
		 * Soooo it seems like this literally only gets called when the node is 
		 * NOT the target!
		 */
		//boolean isFinalRepoRecipient = con.getMessage().getTo() == this.getHost();
		//System.out.println("The next message is destined to " + con.getMessage().getTo() + " and this host is " + this.getHost());
		//for (Application app : getApplications(con.getMessage().getAppID())) {
		//	System.out.println("app.handle of "+ app.getAppID() +" gets executed here");
		//}
		//System.out.println(this.getHost().name + " has storage capability " + this.getHost().hasStorageCapability());
		//System.out.println("And other host of connection: "+con.getOtherNode(this.getHost()).name + " has storage capability " + con.getOtherNode(this.getHost()).hasStorageCapability());
		//Message msg = this.messageTransferred(con.getMessage().getId(), this.getHost());
		//if (this.getHost().name.contains("r")){
		//	System.out.println("This host is "+ this.getHost() +" has storage capability: " + this.getHost().hasStorageCapability());
		//}
		if (con.getOtherNode(this.getHost()).hasStorageCapability()) {
			
			for (Application app : getApplications(con.getMessage().getAppID())) {
				// Note that the order of applications is significant
				// since the next one gets the output of the previous.
				//System.out.println("app.handle gets executed here: " + con.getMessage().getAppID());
				Message outgoing = app.handle(con.getMessage(), con.getOtherNode(this.getHost()));
			}
			
		}
		
	}
	
	@Override
	public long getBufferDeletedMessagesSize() {
		return this.totalBufferDeletedMessages;
	}
	
	@Override
	public RepoDirectDeliveryRouter replicate() {
		return new RepoDirectDeliveryRouter(this);
	}
}
