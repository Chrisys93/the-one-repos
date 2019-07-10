/* 
 * Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details. 
 */
package input;

import java.util.ArrayList;

import core.DTNHost;
import core.Message;
import core.World;

/** 
 * @author Adrian-Cristian Nicolaescu, University College London
 */

/**
 * External event for creating a message.
 */
public class ReposProcDepletionCreateEvent extends ReposProcDepletionEvent {
	
	/**
	 * Creates a message creation event with a optional response request
	 * @param from The creator of the message
	 * @param to Where the message is destined to
	 * @param id ID of the message
	 * @param size Size of the message
	 * @param responseSize Size of the requested response message or 0 if
	 * no response is requested
	 * @param time Time, when the message is created
	 */
	public ReposProcDepletionCreateEvent(ArrayList <String> hosts, int processedMsgNo, double time) {
		super(hosts, processedMsgNo, time);
	}

	
	/**
	 * Creates the message this event represents. 
	 */
	@Override
	public void processEvent(World world) {
		ArrayList<DTNHost> deplHosts = world.getNodestoDeplete(this.hosts);
		//System.out.println("The message number is: "+this.processedMsgNo);
		
		for (int i = 0; i<deplHosts.size(); i++) {
			DTNHost currNode = deplHosts.get(i);
			//System.out.println("The storage used in node "+currNode.name.toString()+" is: "+currNode.getStorageSystem().getStoredMessagesSize());
			if (currNode.getStorageSystem().getNrofMessages() > this.processedMsgNo){
				for (int u = 0; u<this.processedMsgNo; u++) {
					if (!currNode.getStorageSystem().isProcessingEmpty()) {
						if (currNode.getStorageSystem().getOldestProcessMessage() != null) {
							Message temp = currNode.getStorageSystem().getOldestProcessMessage();
							//System.out.println("The message to be deleted is "+this.msgNo+" from host "+currNode.name.toString());
							currNode.getStorageSystem().processMessage(temp);
							while (currNode.getStorageSystem().hasMessage(temp.getId()) != null) {}
							currNode.getStorageSystem().deleteProcessedMessage(temp.getId());
						}
					}
					else {
						Message temp = currNode.getStorageSystem().getOldestStaticMessage();
						//System.out.println("The message to be deleted is "+this.msgNo+" from host "+currNode.name.toString());
						currNode.getStorageSystem().addToDeplStaticMessages(temp);
						currNode.getStorageSystem().deleteStaticMessage(temp.getId());
					}
				}
			}
		}
	}
	
	//@Override
	//public String toString() {
		//return super.toString() + " [" + fromAddr + "->" + toAddr + "] " +
		//"size:" + size + " CREATE";
	//}
}
