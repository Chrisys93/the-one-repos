/* 
 * Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details. 
 */
package input;

/** 
 * @author Adrian-Cristian Nicolaescu, University College London
 */

/**
 * A message related external event
 */
public abstract class ReposMessageEvent extends ExternalEvent {
	/** address of the node the message is from */
	protected int fromAddr;
	/** address of the node the message is to */
	protected String toAddr;
	/** identifier of the message */
	protected String id;
	
	/**
	 * Creates a message  event
	 * @param from Where the message comes from
	 * @param to Who the message goes to 
	 * @param id ID of the message
	 * @param time Time when the message event occurs
	 */
	public ReposMessageEvent(int from, String to, String id, double time) {
		super(time);
		this.fromAddr = from;
		this.toAddr= to;
		this.id = id;
	}
	
	@Override
	public String toString() {
		return "MSG @" + this.time + " " + id;
	}
}
