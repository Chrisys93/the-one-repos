/**
 * DecisionEngineRouter.java was adapted to cope with Dlife and DlifeComm.
 * 
 * Copyright 2010 by University of Pittsburgh
 * Copyright 2012 SITI, Universidade Lusófona
 * 
 * Dlife and DlifeComm are free softwares: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Dlife and DlifeComm are distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Dlife and DlifeComm.  If not, see <http://www.gnu.org/licenses/>.
 *
 * 
 */

package routing;

import java.util.*;

import core.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.io.FileWriter;
import java.io.IOException;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import routing.community.CommunityDetection;

import core.Connection;
import core.DTNHost;
import core.Message;
import core.Settings;
import core.SimClock;
import core.SlotTimeCheck;
import core.Tuple;

public class DecisionEngineRouter extends ActiveRouter
{
	private ArrayList<Map<DTNHost,Double>> averageDurations; // every slot of averageDuration corresponds to a specific slot of a day containing the average duration for all the nodes that did set up a connection with THIS node  
	private Map<DTNHost,Double> startconnectiontime; // Map to save the starting times a connection of the several nodes ---- "-1" if there is no connection at the moment 
	private Map<DTNHost,Double> deltaT;    //Map to save the connectiontimes of the nodes for the current slot
	private Map<DTNHost,Double> deltaTforImportance;
	private Map<DTNHost,Double> importancemap; //Stores the importance of encountered nodes
	private double importance;
	public static Map<DTNHost, Double> weights;
	public static Map<DTNHost, Map<DTNHost, Double>> weightsCopy= new HashMap<DTNHost, Map<DTNHost, Double>>();
	public static Map<DTNHost, Double> importCopy= new HashMap<DTNHost, Double>();
	public static DecisionEngineRouter host;
	protected CommunityDetection community;
	int predCount=0;
	
	public static final String PUBSUB_NS = "DecisionEngineRouter";
	public static final String ENGINE_SETTING = "decisionEngine";
	public static final String TOMBSTONE_SETTING = "tombstones";
	public static final String CONNECTION_STATE_SETTING = "";
	
	protected boolean tombstoning;
	protected RoutingDecisionEngine decider;
	protected List<Tuple<Message, Connection>> outgoingMessages;
	
	protected Set<String> tombstones;
	
	/** how often TTL check (discarding old messages) is performed */
	 public static int TTL_CHECK_INTERVAL = 60;
	 /** sim time when the last TTL check was done */
	 private double lastTtlCheck;
	
	/** 
	 * Used to save state machine when new connections are made. See comment in
	 * changedConnection() 
	 */
	protected Map<Connection, Integer> conStates;
	
	
	
	public DecisionEngineRouter(Settings s)
	{
		super(s);
		
		Settings routeSettings = new Settings(PUBSUB_NS);
		
		outgoingMessages = new LinkedList<Tuple<Message, Connection>>();
		
		decider = (RoutingDecisionEngine)routeSettings.createIntializedObject(
				"routing." + routeSettings.getSetting(ENGINE_SETTING));
		
		if(routeSettings.contains(TOMBSTONE_SETTING))
			tombstoning = routeSettings.getBoolean(TOMBSTONE_SETTING);
		else
			tombstoning = false;
		
		if(tombstoning)
			tombstones = new HashSet<String>(10);
		conStates = new HashMap<Connection, Integer>(4);
		
		initWeights();
		
		averageDurations=new ArrayList<Map<DTNHost,Double>>(SlotTimeCheck.getnumberofslots());
		for(int i=0;i<SlotTimeCheck.getnumberofslots();i++){
			Map<DTNHost,Double> map=new HashMap<DTNHost,Double>();
			averageDurations.add(map);
		}

		startconnectiontime=new HashMap<DTNHost,Double>();
		deltaT=new HashMap<DTNHost,Double>();
		importancemap=new HashMap<DTNHost,Double>();
		importance=0;
	}

	public DecisionEngineRouter(DecisionEngineRouter r)
	{
		super(r);
		outgoingMessages = new LinkedList<Tuple<Message, Connection>>();
		decider = r.decider.replicate();
		tombstoning = r.tombstoning;
		
		if(this.tombstoning)
			tombstones = new HashSet<String>(10);
		conStates = new HashMap<Connection, Integer>(4);
		
		initWeights();

		averageDurations=new ArrayList<Map<DTNHost,Double>>(SlotTimeCheck.getnumberofslots());
		for(int i=0;i<SlotTimeCheck.getnumberofslots();i++){
			Map<DTNHost,Double> map=new HashMap<DTNHost,Double>();
			averageDurations.add(map);
		}
		startconnectiontime=new HashMap<DTNHost,Double>();
		deltaT=new HashMap<DTNHost,Double>();
		importancemap=new HashMap<DTNHost,Double>();
		importance=0;
	}

	@Override
	public MessageRouter replicate()
	{
		return new DecisionEngineRouter(this);
	}

	@Override
	 public boolean createNewMessage(Message m){
		if(decider.newMessage(m)){
			makeRoomForNewMessage(m.getSize());
			m.setTtl(this.msgTtl);
			addToMessages(m, true); 
			findConnectionsForNewMessage(m, getHost());
			return true;
		}
		return false;
	 }

	public void initWeights() {
		this.weights = new HashMap<DTNHost, Double>();
	}

	
	@Override
	public void changedConnection(Connection con)
	{
		DTNHost myHost = getHost();
		DTNHost otherNode = con.getOtherNode(myHost);
		DecisionEngineRouter otherRouter = (DecisionEngineRouter)otherNode.getRouter();
		if(con.isUp())
		{
			updatestartconnectiontime(otherNode);   
		    updateImportancemap(otherNode);

			decider.connectionUp(myHost, otherNode);
			
			if(shouldNotifyPeer(con))
			{
				this.doExchange(con, otherNode);
				otherRouter.didExchange(con);
			}
			
			/*
			 * Once we have new information computed for the peer, we figure out if
			 * there are any messages that should get sent to this peer.
			 */
			Collection<Message> msgs = getMessageCollection();
			for(Message m : msgs)
			{
				if(decider.shouldSendMessageToHost(m, otherNode, myHost))
					outgoingMessages.add(new Tuple<Message,Connection>(m, con));
			}
		}
		else 
		{

			updatedeltaT(otherNode);
			setstarttimeoff(otherNode);

			decider.connectionDown(myHost, otherNode);
			
			conStates.remove(con);
			
			/*
			 * If we  were trying to send message to this peer, we need to remove them
			 * from the outgoing List.
			 */
			for(Iterator<Tuple<Message,Connection>> i = outgoingMessages.iterator(); 
					i.hasNext();)
			{
				Tuple<Message, Connection> t = i.next();
				if(t.getValue() == con)
					i.remove();
			}
		}
	}
	
	private void updatestartconnectiontime(DTNHost host) {
		if(startconnectiontime.get(host)!=null){
			if(startconnectiontime.get(host)>=0){      //  method to check if everything runs all right --- TODO should be removed afterwards
				
			}
			else{
				startconnectiontime.put(host, SimClock.getTime());
			}
		}
		else{
		startconnectiontime.put(host, SimClock.getTime());
		}
// TODO erase later on:		weights.put(host, newValue);
	}
	private void updateImportancemap(DTNHost host){
		double importa=((DecisionEngineRouter)host.getRouter()).getImportance();
	    importancemap.put(host, importa);
	}
	
	
	private void updatedeltaT(DTNHost host){
		if(deltaT.get(host)!=null){
			deltaT.put(host, deltaT.get(host)+(SimClock.getTime()-startconnectiontime.get(host)));
		}
		else{
			if(startconnectiontime.get(host)==null){
//				System.out.println("porque");
			}
			else{
			deltaT.put(host,SimClock.getTime()-startconnectiontime.get(host));
			}
		}
		
	}
	
	private void setstarttimeoff(DTNHost host){
		startconnectiontime.put(host,-1.0);
		
		
	}
	public void calcdeltaTandAD(){
		Set<DTNHost> hostset= startconnectiontime.keySet();
		Iterator<DTNHost> hostIterator=hostset.iterator();
		Map<DTNHost,Double> tempList = new HashMap<DTNHost,Double>();
		while(hostIterator.hasNext()){             //TODO still have to check how the iterator reacts to changes in the Map 
			DTNHost currenthost = hostIterator.next();
			if(startconnectiontime.get(currenthost)>=0){
				updatedeltaT(currenthost);
				tempList.put(currenthost,SimClock.getTime()); //set the starttime on the beginning of the new slot
				
			}
		}  //at this point the startconnectiontime map is ready for the next slot: -times of existing connections set to the next slot  -others deleted
		startconnectiontime=tempList;
		updateAverageDuration();
		updateWeights();
		updateImportance();
		long currentday=SlotTimeCheck.getDay();
		int currentslot = SlotTimeCheck.getcurrentslot();
		weightsCopy.put(this.getHost(),weights);
	}
	
	
	private void updateAverageDuration(){
		long currentday=SlotTimeCheck.getDay();
		int currentslot = SlotTimeCheck.getcurrentslot();
		Map<DTNHost,Double> currentAverageDurationSlot=averageDurations.get(currentslot);
		Set hostsinDelta = deltaT.keySet();
		Iterator<DTNHost> deltaTHostIterator= hostsinDelta.iterator();
		while(deltaTHostIterator.hasNext()){
			DTNHost currenthost= deltaTHostIterator.next();
			double oldAD=0;
			if(currentAverageDurationSlot.get(currenthost)==null){
				oldAD=0;
			}
			else{
				oldAD=currentAverageDurationSlot.get(currenthost);
			}
			double newAD = (deltaT.get(currenthost)+(currentday-1)*oldAD);
			currentAverageDurationSlot.put(currenthost,newAD);

			
		}
		Set<DTNHost> s = currentAverageDurationSlot.keySet();
		Iterator<DTNHost> iter=s.iterator();
		double newvalue=0.0;
		while(iter.hasNext()){
			DTNHost dtnhost = iter.next();
			if(!hostsinDelta.contains(dtnhost)){
				newvalue= (currentday-1)*currentAverageDurationSlot.get(dtnhost)/currentday;
			}
			else newvalue= currentAverageDurationSlot.get(dtnhost)/currentday;
			currentAverageDurationSlot.put(dtnhost,newvalue);
		}
		deltaTforImportance=deltaT;
		deltaT=new HashMap<DTNHost,Double>();
		
	}

	private void updateWeights(){
		Map<DTNHost,Double>nextweight = new HashMap<DTNHost,Double>();
		int numberofslots = SlotTimeCheck.getnumberofslots();
		double denominator = SlotTimeCheck.getnumberofslots();
		int slotindex= SlotTimeCheck.getcurrentslot();
		for(int i=numberofslots;i>0;i--){
			if(averageDurations.get(slotindex)!=null){
				Map<DTNHost,Double> currentad=averageDurations.get(slotindex);
				Set<DTNHost> hosts= currentad.keySet();
				Iterator<DTNHost> hostIterator=hosts.iterator();
				while(hostIterator.hasNext()){
					DTNHost currentHost= hostIterator.next();
					double currAverageduration = currentad.get(currentHost);
					if(nextweight.get(currentHost)==null){
						nextweight.put(currentHost, 0.0);
					}
				nextweight.put(currentHost, nextweight.get(currentHost)+((SlotTimeCheck.getnumberofslots())/(denominator))*currAverageduration);
				}
				denominator++;
				slotindex=(slotindex+1);
				if(slotindex==24){
					slotindex=0;
					}
			}
		
		}
		this.weights=nextweight;
		
	}
	
	private void updateImportance(){
		int nrofneighbours=deltaTforImportance.size();
		double sumofweights=0;
		Set<DTNHost>set =deltaTforImportance.keySet();
		Iterator<DTNHost> it= set.iterator();
		while(it.hasNext()){
			DTNHost ho=it.next();
			sumofweights=sumofweights+weights.get(ho);
			
		}
		Set<DTNHost> deltahosts = deltaTforImportance.keySet();
		Iterator<DTNHost> iter= deltahosts.iterator();
		double newimportance=0;
		
		while(iter.hasNext()){
			DTNHost host=iter.next();
			newimportance = newimportance+(weights.get(host)*importancemap.get(host))/(nrofneighbours);
		}
		this.importance=0.2+0.8*newimportance;
		importCopy.put(this.getHost(), this.importance);
	}

	public double getImportance(){
		return importance;
	}
	
	public Map<DTNHost, Double> getWeights() {

			return this.weights;
		}
	
	protected void doExchange(Connection con, DTNHost otherHost)
	{
		conStates.put(con, 1);
		decider.doExchangeForNewConnection(con, otherHost);
	}
	
	/**
	 * Called by a peer DecisionEngineRouter to indicated that it already 
	 * performed an information exchange for the given connection.
	 * 
	 * @param con Connection on which the exchange was performed
	 */
	protected void didExchange(Connection con)
	{
		conStates.put(con, 1);
	}
	
	@Override
	protected int startTransfer(Message m, Connection con)
	{
		int retVal;
		
		if (!con.isReadyForTransfer()) {
			return TRY_LATER_BUSY;
		}
		
		retVal = con.startTransfer(getHost(), m);
		if (retVal == RCV_OK) { // started transfer
			addToSendingConnections(con);
		}
		else if(tombstoning && retVal == DENIED_DELIVERED)
		{
			this.deleteMessage(m.getId(), false);
			tombstones.add(m.getId());
		}
		else if (deleteDelivered && (retVal == DENIED_OLD || retVal == DENIED_DELIVERED) && 
				decider.shouldDeleteOldMessage(m, con.getOtherNode(getHost()))) {
			/* final recipient has already received the msg -> delete it */
			this.deleteMessage(m.getId(), false);
		}
		
		return retVal;
	}

	@Override
	 public int receiveMessage(Message m, DTNHost from){
		int recvCheck = checkReceiving(m); 
		if (recvCheck != RCV_OK) {
			return recvCheck;
		}
		if(isDeliveredMessage(m) || (tombstoning && tombstones.contains(m.getId())))
			return DENIED_DELIVERED; 
		
	 return super.receiveMessage(m, from);
	 }


	@Override
	public Message messageTransferred(String id, DTNHost from)
	{
		Message incoming = removeFromIncomingBuffer(id, from);
	
		if (incoming == null) {
			throw new SimError("No message with ID " + id + " in the incoming "+
					"buffer of " + getHost());
		}
		
		incoming.setReceiveTime(SimClock.getTime());
		
		Message outgoing = incoming;
		for (Application app : getApplications(incoming.getAppID())) {
			// Note that the order of applications is significant
			// since the next one gets the output of the previous.
			outgoing = app.handle(outgoing, getHost());
			if (outgoing == null) break; // Some app wanted to drop the message
		}
		
		Message aMessage = (outgoing==null)?(incoming):(outgoing);
		
		boolean isFinalRecipient = decider.isFinalDest(aMessage, getHost());
		boolean isFirstDelivery =  isFinalRecipient && 
			!isDeliveredMessage(aMessage);
		
		if (outgoing!=null && decider.shouldSaveReceivedMessage(aMessage, getHost())) 
		{
			// not the final recipient and app doesn't want to drop the message
			// -> put to buffer
			addToMessages(aMessage, false);
			
			// Determine any other connections to which to forward a message
			findConnectionsForNewMessage(aMessage, from);
		}
		
		if (isFirstDelivery)
		{
			this.deliveredMessages.put(id, aMessage);
		}
		
		for (MessageListener ml : this.mListeners) {
			ml.messageTransferred(aMessage, from, getHost(),
					isFirstDelivery);
		}
		
		return aMessage;
	}

	@Override
	protected void transferDone(Connection con)
	{
		Message transferred = this.getMessage(con.getMessage().getId());
		
		for(Iterator<Tuple<Message, Connection>> i = outgoingMessages.iterator(); 
		i.hasNext();)
		{
			Tuple<Message, Connection> t = i.next();
			if(t.getKey().getId().equals(transferred.getId()) && 
					t.getValue().equals(con))
			{
				i.remove();
				break;
			}
		}
		
		if(decider.shouldDeleteSentMessage(transferred, con.getOtherNode(getHost()), getHost()))
		{
			this.deleteMessage(transferred.getId(), false);
			
			
		}
	}

	@Override
	public void update(){
		super.update();

		/* time to do a TTL check and drop old messages? Only if not sending */
		if (SimClock.getTime() - lastTtlCheck >= TTL_CHECK_INTERVAL && 
				sendingConnections.size() == 0) {
			dropExpiredMessages();
			lastTtlCheck = SimClock.getTime();
		}

		if (!canStartTransfer() || isTransferring()) {
			return; // nothing to transfer or is currently transferring 
		} 

		tryMessagesForConnected(outgoingMessages); 

		for(Iterator<Tuple<Message, Connection>> i = outgoingMessages.iterator(); i.hasNext();){
			Tuple<Message, Connection> t = i.next();
			if(!this.hasMessage(t.getKey().getId())){
				i.remove();
			}
		}
	 }
	
	@Override
	public void deleteMessage(String id, boolean drop)
	{
		super.deleteMessage(id, drop);
		
		for(Iterator<Tuple<Message, Connection>> i = outgoingMessages.iterator(); 
		i.hasNext();)
		{
			Tuple<Message, Connection> t = i.next();
			if(t.getKey().getId().equals(id))
			{
				i.remove();
			}
		}
	}

	public RoutingDecisionEngine getDecisionEngine()
	{
		return this.decider;
	}

	protected boolean shouldNotifyPeer(Connection con)
	{
		Integer i = conStates.get(con);
		return i == null || i < 1;
	}
	
	protected void findConnectionsForNewMessage(Message m, DTNHost from)
	{
		for(Connection c : getConnections())
		{
			DTNHost other = c.getOtherNode(getHost());
			if(other != from && decider.shouldSendMessageToHost(m, other, getHost()))
			{
				outgoingMessages.add(new Tuple<Message, Connection>(m, c));
			}
		}
	}
}
