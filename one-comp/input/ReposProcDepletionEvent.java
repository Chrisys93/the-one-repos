/* 
 * Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details. 
 */
package input;

import java.util.ArrayList;

/** 
 * @author Adrian-Cristian Nicolaescu, University College London
 */

/**
 * A message related external event
 */
public abstract class ReposProcDepletionEvent extends ExternalEvent {
	/** address of the node the message is from */
	protected ArrayList <String> hosts;
	/** identifier of the message */
	protected int processedMsgNo;
	
	/**
	 * Creates a message  event
	 * @param from Where the message comes from
	 * @param to Who the message goes to 
	 * @param id ID of the message
	 * @param time Time when the message event occurs
	 */
	public ReposProcDepletionEvent(ArrayList <String> hosts, int processedMsgNo, double time) {
		super(time);
		this.hosts = hosts;
		this.processedMsgNo = processedMsgNo;
		//System.out.println("The lower limit is: "+this.lowerLimit);
		//System.out.println("The higher limit is: "+this.higherLimit);
		//System.out.println("The higher limit is: "+this.msgNo);
	}
	
	/*@Override
	public String toString() {
		return "MSG @" + this.time + " " + id;
	}*/
}
