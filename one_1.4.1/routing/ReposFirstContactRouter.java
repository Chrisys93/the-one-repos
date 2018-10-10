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
	
	@Override
	protected void transferDone(Connection con) {
		/* don't leave a copy for the sender */
		//this.deleteMessage(con.getMessage().getId(), false);
		/* this is where the start of the storage part could be implemented*/
	}
		
	@Override
	public ReposFirstContactRouter replicate() {
		return new ReposFirstContactRouter(this);
	}

}
