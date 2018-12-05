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
			if (startTransfer(m, con) == RCV_OK) {
				return t;
			}
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
		
		if (con.getOtherNode(this.getHost()).hasStorageCapability()) {
			
			for (Application app : getApplications(con.getMessage().getAppID())) {
				Message outgoing = app.handle(con.getMessage(), con.getOtherNode(this.getHost()));
			}
			
		}
		/* don't leave a copy for the sender */
		this.deleteMessage(con.getMessage().getId(), false);
		
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
