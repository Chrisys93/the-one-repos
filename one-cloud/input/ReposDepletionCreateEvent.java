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
public class ReposDepletionCreateEvent extends ReposDepletionEvent {
	
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
	public ReposDepletionCreateEvent(ArrayList <String> hosts, long lowerLimit, long higherLimit, int msgNo, double time) {
		super(hosts, lowerLimit, higherLimit, msgNo, time);
	}

	
	/**
	 * Creates the message this event represents. 
	 */
	@Override
	public void processEvent(World world) {
		ArrayList<DTNHost> deplHosts = world.getNodestoDeplete(this.hosts);
		//System.out.println("The lower limit is: "+this.lowerLimit);
		//System.out.println("The higher limit is: "+this.higherLimit);
		//System.out.println("The message number is: "+this.msgNo);
		
		for (int i = 0; i<deplHosts.size(); i++) {
			DTNHost currNode = deplHosts.get(i);
			//System.out.println("The storage used in node "+currNode.name.toString()+" is: "+currNode.getStorageSystem().getStoredMessagesSize());
			if (currNode.getStorageSystem().getStaticMessagesSize()>this.lowerLimit) {
				if (currNode.getStorageSystem().getNrofMessages() > this.msgNo){
					for (int u = 0; u<this.msgNo; u++) {
						Message temp = currNode.getStorageSystem().getStoredMessages().get(u);
						//System.out.println("The message to be deleted is "+this.msgNo+" from host "+currNode.name.toString());
						currNode.getStorageSystem().addToDeplStaticMessages(temp);
						currNode.getStorageSystem().deleteStaticMessage(temp.getId());
						if (currNode.getStorageSystem().getStaticMessagesSize()>=this.higherLimit) {
							System.out.println("Host "+ currNode + " is full. Consider increasing depletion" + 
									" rate, increasing storage capacity, or improving storage distribution" +
									" of each repository/storage-capable node");
						}
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
