/**
 * dLife v1.0 implementation by Waldir Moreira (waldir.junior@ulusofona.pt).
 * This class, as it is, implements dLife. 
 * This code was done based on DistributedBubbleRap.java implementation, thus it inherits some functions, methods and classes
 * Despite the fact this class is under the community folder, it does not use any community parameters. 
 *
 * Copyright 2010 by University of Pittsburgh
 * Copyright 2012 SITI, Universidade Lusófona
 * 
 * Dlife is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Dlife is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Dlife.  If not, see <http://www.gnu.org/licenses/>.
 */

package routing.community;

import java.util.*;

import core.*;
import routing.DecisionEngineRouter;
import routing.MessageRouter;
import routing.RoutingDecisionEngine;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class Dlife 
				implements RoutingDecisionEngine, CommunityDetectionEngine
{
	/** Community Detection Algorithm to employ -setting id {@value} */
	public static final String COMMUNITY_ALG_SETTING = "communityDetectAlg";
	/** Centrality Computation Algorithm to employ -setting id {@value} */
	public static final String CENTRALITY_ALG_SETTING = "centralityAlg";
	
	protected Map<DTNHost, Double> startTimestamps;
	protected Map<DTNHost, List<Duration>> connHistory;
	
	protected CommunityDetection community;
	protected Centrality centrality;
	
	private  Map<DTNHost, Map<DTNHost, Double>> _weights;
	private  Map<DTNHost, Double> _importances;

	
	/**
	 * Constructs a Dlife Decision Engine based upon the settings
	 * defined in the Settings object parameter. The class looks for the class
	 * names of the community detection and centrality algorithms that should be
	 * employed used to perform the routing.
	 * 
	 * @param s Settings to configure the object
	 */
	public Dlife(Settings s)
	{
		this._weights = new HashMap<DTNHost, Map<DTNHost, Double>>();
		this._importances = new HashMap<DTNHost, Double>();
		
		if(s.contains(COMMUNITY_ALG_SETTING))
			this.community = (CommunityDetection) 
				s.createIntializedObject(s.getSetting(COMMUNITY_ALG_SETTING));
		else
			this.community = new SimpleCommunityDetection(s);
		
		if(s.contains(CENTRALITY_ALG_SETTING))
			this.centrality = (Centrality) 
				s.createIntializedObject(s.getSetting(CENTRALITY_ALG_SETTING));
		else
			this.centrality = new SWindowCentrality(s);
		

	}
	
	/**
	 * Constructs a Dlife Decision Engine from the argument 
	 * prototype. 
	 * 
	 * @param proto Prototype Dlife upon which to base this object
	 */
	public Dlife(Dlife proto)
	{
		this._weights = new HashMap<DTNHost, Map<DTNHost, Double>>();
		this._importances = new HashMap<DTNHost, Double>();
		this.community = proto.community.replicate();
		this.centrality = proto.centrality.replicate();
		startTimestamps = new HashMap<DTNHost, Double>();
		connHistory = new HashMap<DTNHost, List<Duration>>();
		
	}

	public void connectionUp(DTNHost thisHost, DTNHost peer){}

	/**
	 * Starts timing the duration of this new connection and informs the community
	 * detection object that a new connection was formed.
	 * 
	 * @see routing.RoutingDecisionEngine#doExchangeForNewConnection(core.Connection, core.DTNHost)
	 */
	public void doExchangeForNewConnection(Connection con, DTNHost peer)
	{
		DTNHost myHost = con.getOtherNode(peer);
		Dlife de = this.getOtherDecisionEngine(peer);
		
		this.startTimestamps.put(peer, SimClock.getTime());
		de.startTimestamps.put(myHost, SimClock.getTime());
		
		this.community.newConnection(myHost, peer, de.community);

	}
	
	public void connectionDown(DTNHost thisHost, DTNHost peer)
	{
		double time = startTimestamps.get(peer);
		double etime = SimClock.getTime();
		
		// Find or create the connection history list
		List<Duration> history;
		if(!connHistory.containsKey(peer))
		{
			history = new LinkedList<Duration>();
			connHistory.put(peer, history);
		}
		else
			history = connHistory.get(peer);
		
		// add this connection to the list
		if(etime - time > 0)
			history.add(new Duration(time, etime));
		
		CommunityDetection peerCD = this.getOtherDecisionEngine(peer).community;
		
		// inform the community detection object that a connection was lost.
		// The object might need the whole connection history at this point.
		community.connectionLost(thisHost, peer, peerCD, history);
		
		startTimestamps.remove(peer);

	}


	
	public boolean newMessage(Message m)
	{
		return true; // Always keep and attempt to forward a created message
	}

	public boolean isFinalDest(Message m, DTNHost aHost)
	{
		return m.getTo() == aHost; // Unicast Routing
	}

	public boolean shouldSaveReceivedMessage(Message m, DTNHost thisHost)
	{
		return m.getTo() != thisHost;
	}

	public boolean shouldSendMessageToHost(Message m, DTNHost otherHost, DTNHost thisHost)
	{
		if(m.getTo() == otherHost) return true; // trivial to deliver to final dest
		
		/*
		 * Here is where we decide when to forward along a message. 
		 */
		DTNHost dest = m.getTo();
		
		_weights=DecisionEngineRouter.weightsCopy;
		_importances=DecisionEngineRouter.importCopy;
		
				

		if(checkMessage(m, otherHost)) return false; 
		
		else if(_weights.containsKey(thisHost) && _weights.containsKey(otherHost)){ 	
			Map<DTNHost,Double> tempListThis = new HashMap<DTNHost,Double>();
			Map<DTNHost,Double> tempListOther = new HashMap<DTNHost,Double>();
			
			double ThisWeightToDest = 0.0;
			double OtherWeightToDest = 0.0;
			
			tempListThis=_weights.get(thisHost);
			tempListOther=_weights.get(otherHost);

			
				Set<DTNHost> hostset= tempListThis.keySet();
					Iterator<DTNHost> hostIterator=hostset.iterator();
				
					if(tempListThis.size()!=0){
				
						while(hostIterator.hasNext()){            
							DTNHost currenthost = hostIterator.next();
							if(currenthost==dest){
								ThisWeightToDest=tempListThis.get(currenthost);	
							}							
						}
				} 
					
				
				Set<DTNHost> hostset1= tempListOther.keySet();
				Iterator<DTNHost> hostIterator1=hostset1.iterator();
			
				if(tempListOther.size()!=0){
					while(hostIterator1.hasNext()){            
						DTNHost currenthost = hostIterator1.next();
						if(currenthost==dest){
							OtherWeightToDest=tempListOther.get(currenthost);	
						}
					}
				}

					
				if(OtherWeightToDest>ThisWeightToDest){
					return true; //other node has better weight
				}		
		}
		else if(_importances.get(thisHost) != null && _importances.get(otherHost) != null){
			if(_importances.get(otherHost) > _importances.get(thisHost)){
				return true;
			}
		}
		return false; 
	}
	
	//ADDED Checks whether a nodes has a weight to a specific destination
	public boolean checkWeightToDest(Map<DTNHost,Double> weightList, DTNHost dest){
		if(weightList.get(dest)!=null){
		Set<DTNHost> hostset= weightList.keySet();
		Iterator<DTNHost> hostIterator=hostset.iterator();
			if(weightList.size()!=0){
				while(hostIterator.hasNext()){
					DTNHost currenthost = hostIterator.next();
					if(currenthost==dest)
						return true;
					}
				} 
		}
		return false;	
	}
	
	//ADDED Checks whether a nodes has already a given message
	public boolean checkMessage (Message m, DTNHost otherHost){
		List<Message> teste = new ArrayList<Message>();
		teste.addAll(otherHost.getMessageCollection());				
		Object [] array = teste.toArray();
		String a = m.toString();
		for (int i = 0; i < array.length; i++){
			if(array[i].toString()==a){
				return true;
			}
						
		}
		return false;
	}

	public boolean shouldDeleteSentMessage(Message m, DTNHost otherHost, DTNHost thisHost)
	{
		Map<DTNHost,Double> tempListThis = new HashMap<DTNHost,Double>();	
		_weights=DecisionEngineRouter.weightsCopy;
			
		tempListThis=_weights.get(thisHost);
		if(tempListThis==null)
			return false;
		return !tempListThis.containsKey(m.getTo());
	}

	public boolean shouldDeleteOldMessage(Message m, DTNHost hostReportingOld)
	{
		return false;
	}

	public RoutingDecisionEngine replicate()
	{
		return new Dlife(this);
	}
	
	protected boolean commumesWithHost(DTNHost h)
	{
		return community.isHostInCommunity(h);
	}
	
	protected double getLocalCentrality()
	{
		return this.centrality.getLocalCentrality(connHistory, community);
	}
	
	protected double getGlobalCentrality()
	{
		return this.centrality.getGlobalCentrality(connHistory);
	}

	private Dlife getOtherDecisionEngine(DTNHost h)
	{
		MessageRouter otherRouter = h.getRouter();
		assert otherRouter instanceof DecisionEngineRouter : "This router only works " + 
		" with other routers of same type";
		
		return (Dlife) ((DecisionEngineRouter)otherRouter).getDecisionEngine();
	}

	public Set<DTNHost> getLocalCommunity() {return this.community.getLocalCommunity();}
}
