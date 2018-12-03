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
			if (con.getOtherNode(this.getHost()).name.contains("r")) {
				if(!this.getHost().name.contains("r")) {
					if (startTransfer(m, con) == RCV_OK) {
						return t;
					}
				}	
			}
		}
		
		return null;
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
		System.out.println(this.getHost().name + " has storage capability " + this.getHost().hasStorageCapability());
		System.out.println("And other host of connection: "+con.getOtherNode(this.getHost()).name + " has storage capability " + con.getOtherNode(this.getHost()).hasStorageCapability());
		//Message msg = this.messageTransferred(con.getMessage().getId(), this.getHost());
		//if (this.getHost().name.contains("r")){
		//	System.out.println("This host is "+ this.getHost() +" has storage capability: " + this.getHost().hasStorageCapability());
		//}
		if (con.getOtherNode(this.getHost()).hasStorageCapability()) {
			
			for (Application app : getApplications(con.getMessage().getAppID())) {
				// Note that the order of applications is significant
				// since the next one gets the output of the previous.
				System.out.println("app.handle gets executed here");
				Message outgoing = app.handle(con.getMessage(), con.getOtherNode(this.getHost()));
			}
			
		}
		
	}
	
	@Override
	public RepoDirectDeliveryRouter replicate() {
		return new RepoDirectDeliveryRouter(this);
	}
}
