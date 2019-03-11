//Copyright Andrea Campanella, NPTLab, Public university of Milan, 2014
package report;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.StringTokenizer;

import NPTLab.CompareTraces;
import core.ConnectionListener;
import core.DTNHost;
import core.SimScenario;
/**
 * Class that saves in a file all the connections between two hosts, if two of them have already seen ad encounter another
 * the visit countes gets a +1
 * @author Copyright Andrea Campanella, NPTLab, Public university of Milan, 2014
 */


public class ConnectivityListReport extends Report implements ConnectionListener {

	private List<String> ConnectivityList = null; 
	private Map<String,Integer> ConnectivityMap = null;
	private Map<String,Double> SimilarityMap = null;
	
		public ConnectivityListReport() {
			init();
			ConnectivityList = new ArrayList<String>();
		}
		
		@Override
		public void hostsConnected(DTNHost host1, DTNHost host2) {
			String host1Address = Integer.toString(host1.getAddress());
			String host2Address = Integer.toString(host2.getAddress());
			ConnectivityList.add(host1Address + "," + host2Address);
		}

		@Override
		public void hostsDisconnected(DTNHost host1, DTNHost host2) {
			//do nothing because it's not in our interest when two host disconnect
			
		}

		@Override
		public void done() {
			ConnectivityMap = new HashMap<String,Integer>();
			SimilarityMap = CompareTraces.getSimilarityMapNodes();
			List<DTNHost> Hosts = SimScenario.getInstance().getHosts();
			for ( int i = 0; i<Hosts.size(); i++){
				String host1 = Integer.toString(Hosts.get(i).getAddress());
				for( int j = 0; j<Hosts.size(); j++){
					String host2 = Integer.toString(Hosts.get(j).getAddress());
					if (!host1.equals(host2)){
						ConnectivityMap.put(host1+","+host2, 0);
					}
				}
			}
			for( int i = 0; i<ConnectivityList.size(); i++){
				String NodeCouple = ConnectivityList.get(i); 
				if(ConnectivityMap.containsKey(NodeCouple)){
					int encounters = ConnectivityMap.get(NodeCouple) + 1;
					ConnectivityMap.put(NodeCouple, encounters);
				} 
			}
				for( int i = 0; i<ConnectivityList.size(); i++){
					String NodeCouple2 = ConnectivityList.get(i); 
					StringTokenizer stk = new StringTokenizer(NodeCouple2, ",");
					String node2 = stk.nextElement().toString();
					String node1 = stk.nextElement().toString();
					String NodeCoupleInverse = node1 + "," + node2;
					if(ConnectivityMap.containsKey(NodeCoupleInverse)){
						int encounters = ConnectivityMap.get(NodeCoupleInverse) + 1;
						ConnectivityMap.put(NodeCoupleInverse, encounters);
					}
			}
			write("Stats for scenario " + getScenarioName() + 
					"\nsim_time: " + format(getSimTime()));
			for ( String nodes: ConnectivityMap.keySet()){
	        	String statsText = "nodi:  " + nodes + 
	        			" encounters: " + ConnectivityMap.get(nodes) + 
	        			" similarity: " + SimilarityMap.get(nodes) +
	        			"\n"
	        			;
	        	write(statsText);
	        }
			
			
			
			super.done();
		}
}
