/* 
 * Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details. 
 */
package routing;

import core.Connection;
import core.Message;
import core.Settings;

/**
 * First contact router which uses only a single copy of the message 
 * (or fragments) and forwards it to the first available contact.
 */
public class ReposFirstContactRouter extends ActiveRouter {
	
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
		 * requestDeliverableMessages(?) or getMessagesForConnected(?))
		 * and isolate the CONNECTED repos.
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
		
		tryAllMessagesToAllConnections();
	}
	
	@Override
	protected void transferDone(Connection con) {
		/* don't leave a copy for the sender */
		this.deleteMessage(con.getMessage().getId(), false);
		/* this is where the start of the storage part could be implemented*/
	}
		
	@Override
	public FirstContactRouter replicate() {
		return new FirstContactRouter(this);
	}

}
