//Copyright Andrea Campanella, NPTLab, Public university of Milan, 2014
package routing;

import java.util.ArrayList;
import java.util.List;

import NPTLab.CompareTraces;
import NPTLab.DTNHWMap;
import NPTLab.MessageWrapperMap;
import NPTLab.SettingsValuesSingleton;
import NPTLab.Trace;
import NPTLab.TraceNodeSet;
import core.Connection;
import core.DTNHost;
import core.DTNSim;
import core.Message;
import core.Settings;
import core.SimClock;
import core.SimError;

/**
 * <P>
 * CSI: T message router that delivers messages based on the similiraty two nodes and a message. If the similarity of the destination node with the Target Profile
 *  of the message is bigger than the one of the sender the message is exchanged. under the GroupSpread Threshold there is one copy of the
 * message. iF a node similarity to the message destination trace is over the GroupSpread thresold he doesn't delete the message 
 * after having realyed. 
 * The CSI: T algorith is descibed in Hsu,Dutta,Helmy - A paradigm for behavior-oriented profile-cast services in mobile networks @ Ad Hoc Networks 10, 2011 
 * This router also <B>ignores message size and all messages are delivered 
 * immediately</B>.</P><P>
 * <B>Note:</B> This router module also bypasses ActiveRouter.update()
 * @author Andrea Campanella, NPTLab, Public university of Milan, 2014
 */
public class ProfileCastRouter extends ActiveRouter {
	
	/** List of all routers in this node group */
	private static List<ProfileCastRouter> allRouters;
	/** ProfileCast router settings namespace ({@value})*/
	public static final String PROFILECAST_ROUTER_NS = "ProfileCast";
	public static final String DAYS = "matrixCreationDays";
	

	static {
		DTNSim.registerForReset(ProfileCastRouter.class.getCanonicalName());
		reset();
	}
	
	/**
	 * Constructor. Creates a new message router based on the settings in
	 * the given Settings object.
	 * @param s The settings object
	 */
	public ProfileCastRouter(Settings s) {
		super(s);
	}
	
	/**
	 * Copy constructor.
	 * @param r The router prototype where setting values are copied from
	 */
	protected ProfileCastRouter(ProfileCastRouter r) {
		super(r);
		allRouters.add(this);
	}
	
	/**
	 * Tries to deliver the message when the connection is up beetween two nodes. first gets all the messages that the
	 * reciver does not have form the set of the sender, then transfers the ones where this host has > similarity that the sender. 
	 * If the message hs expired its TTL it's deleted.
	 * @param con The connection that has changed between two nodes
	 */
	@Override 
	public void changedConnection(Connection con) {
		if(convertSecondsToDays(SimClock.getTime())>=SettingsValuesSingleton.getMatrixCreationDays()+SettingsValuesSingleton.getComputeMatricesDays()){
			if (con.isUp()) {
				DTNHost peer = con.getOtherNode(getHost());
				List<Message> newMessages = new ArrayList<Message>();
				for (Message m : peer.getMessageCollection()) {
					if (!this.hasMessage(m.getId())) {
						newMessages.add(m);
					}
				}
				for (Message m : newMessages) {
					/* try to start transfer from peer */
					if (con.startTransfer(peer, m) == RCV_OK && this.compareTrace(peer, m)) {
						con.finalizeTransfer(); /* and finalize it right away */
					} 
					if(this.checkReceiving(m)== DENIED_TTL) {
						peer.deleteMessage(m.getId() , true);
					}
				
				}
			}
		}
	}
	/**
	 * Computes the similarity between the possible reciver Node's trace and the message's trace, compares it to the owner's 
	 * similarity to the target Trace in the message and return true, if reciver's similarity > sender similarity.
	 * reciver does not have form the set of the sender, then transfers the ones where this host has > similarity that the sender
	 * @param From DTNHost the owner of the message
	 * @param m Message the message to be eventually transferred
	 * @return b Boolean true if the message must be transferred
	 */
	private boolean compareTrace(DTNHost From, Message m){ 
		boolean b = false;
		if(convertSecondsToDays(SimClock.getTime())>=SettingsValuesSingleton.getMatrixCreationDays()+SettingsValuesSingleton.getComputeMatricesDays()){
				b = CompareTraces.compareTwoTracesMessage((MessageWrapperMap.getInstance().returnMap()).get(m).getTrace(), ((DTNHWMap.getInstance().returnMap()).get(getHost())).returnTrace(), SettingsValuesSingleton.getMatrixCreationDays(), ((DTNHWMap.getInstance().returnMap()).get(From)).getSimilarity(m), m, getHost());
				(DTNHWMap.getInstance().returnMap()).get(getHost()).add(m, CompareTraces.getSimilarity(m,getHost()));
			}
		return b;
	}
	
	/**
	 * Creates a message, sets a random target trace to reach and computes the target trace similarity. adds the message and 
	 * his similarity to the the similarity map of the node that create the message.
	 * @param m Message form the super.createNewMessage()
	 * @return Boolean true if the message is created correctly else false.
	 */
	public boolean createNewMessage(Message m) {
		if(convertSecondsToDays(SimClock.getTime())>=SettingsValuesSingleton.getMatrixCreationDays()+SettingsValuesSingleton.getComputeMatricesDays() & SimClock.getTime()<=SettingsValuesSingleton.getStopMessageCreationTime()){
			boolean ok = super.createNewMessage(m);
			
			if (!ok) {
				throw new SimError("Can't create message " + m);
			}
			Trace MessageDestTrace = TraceNodeSet.getRandomTrace();
			CompareTraces.compareTwoTracesMessage(MessageDestTrace, MessageDestTrace, SettingsValuesSingleton.getMatrixCreationDays(), 0.0,m, null); //0.0 in quanto crea nuovo messaggio e non c'� owner imilarity
			MessageWrapperMap.getInstance().set(m,MessageDestTrace);
			CompareTraces.compareTwoTracesMessage(((DTNHWMap.getInstance().returnMap()).get(getHost())).returnTrace(),MessageDestTrace,SettingsValuesSingleton.getMatrixCreationDays(), 0.0,m,getHost());//messaggio con colui che l'ha creato con destinatario
			(DTNHWMap.getInstance().returnMap()).get(getHost()).add(m, CompareTraces.getSimilarity(m,getHost()));
			return true;
		} else {
			return false;
		}
		
	}
	
	/**
	 * Removes the message with the given ID from this router, if the router
	 * has that message; otherwise does nothing. If the router was transferring
	 * the message, the transfer is aborted.
	 * @param id ID of the message to be removed
	 */
	public void removeDeliveredMessage(String id) {
		if (this.hasMessage(id)) {
			for (Connection c : this.sendingConnections) {
				/* if sending the message-to-be-removed, cancel transfer */
				if (c.getMessage().getId().equals(id)) {
					c.abortTransfer();
				}
			}
			this.deleteMessage(id, false);			
		}
	}
	
	@Override 
	/**
	 * When a message is transferred this method deletes it from the sender if it's similarity is inferior 
	 * to the GroupSpreadThreshold and when the message has arrived to his target.
	 * @param id String id of the transferred Message
	 * @param from DTNHOst, the sender fo the message.
	 * @return m Message an object form the messageTransferred method from Message router
	 */
	public Message messageTransferred(String id, DTNHost from) {
		Message m = null;
		if(convertSecondsToDays(SimClock.getTime())>=SettingsValuesSingleton.getMatrixCreationDays()+SettingsValuesSingleton.getComputeMatricesDays()){
			m = super.messageTransferred(id, from); 
			ProfileCastRouter sender = (ProfileCastRouter) from.getRouter();
			if (((DTNHWMap.getInstance().returnMap()).get(getHost())).similarityMap.get(m)>SettingsValuesSingleton.getGroupSpreadThreshold()){
			} else {
				sender.removeDeliveredMessage(id);
			}
			
		}
		return m;
	}
	
	
	/**
	 * When the transfer of a message can start, if the recipient has already seen the message, 
	 * has seen it or has delivered it, doesn't start the transfer else it.
	 * If the message has expired its TTL it's deleted.
	 * Else gives the ok to start the transfer.
	 * 
	 * @param m Message the messagge that can start to be transferred
	 */
	protected int checkReceiving(Message m) {
		if ( isIncomingMessage(m.getId()) || hasMessage(m.getId()) || 
				isDeliveredMessage(m) ){
			return DENIED_OLD; // already seen this message -> reject it
		}
		if (m.getTtl() <= 0 && m.getTo() != getHost()) {
			/* TTL has expired and this host is not the final recipient */
			return DENIED_TTL; 
		}
		
		return RCV_OK;
	}
	
	@Override
	/**
	 * When the transfer of a message is done, if the recipient was the target recipient the message is deleted from the sender
	 * @param con Connection the connection on which the message was transferred
	 */
	protected void transferDone(Connection con) {
		Message m = con.getMessage();
		
		if (m == null) {
			core.Debug.p("Null message for con " + con);
			return;
		}	
	}
	
	/**
	 * Nothing to do; all transfers are started only when new connections
	 * are created or new messages are created or received, and transfers
	 * are finalized immediately
	 */
	@Override
	public void update() {
		
	}
	/**
	 * Converts a double number of seconds in it's corresponding number of days
	 * @param seconds Double the time in seconds the simulation is at.
	 */
	private static int convertSecondsToDays(double seconds){
		int days = (int) (seconds / (60*60*24));
		return days;
	}
	
	@Override
	public ProfileCastRouter replicate() {
		return new ProfileCastRouter(this);
	}
	
	/**
	 * Resets the static router list
	 */
	public static void reset() {
		allRouters = new ArrayList<ProfileCastRouter>();
	}

}