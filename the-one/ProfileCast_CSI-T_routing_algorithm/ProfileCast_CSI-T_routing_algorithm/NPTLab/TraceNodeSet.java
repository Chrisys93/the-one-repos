//Copyright Andrea Campanella, NPTLab, Public university of Milan, 2014
package NPTLab;

import java.util.Map;
import core.DTNHost;
import java.util.HashMap;
import java.util.Random;


/**
 * Class that contains a map of all the nodes with their mobility trace.
 * @author Andrea Campanella, NPTLab, Public university of Milan, 2014
 */
public class TraceNodeSet {
	/** List of all the Hosts with their mobility traces*/
	public static Map<DTNHost,Trace> TraceNodeMap = new HashMap<DTNHost, Trace>();
	private static Random r = new Random();
	
	/**
	 * Add to the complete set an Host and his mobility Trace.
	 * @param host DTNHost the host 
	 * @param NodeTrace Trace the mobility trace of the host
	 */
	public static void add(DTNHost host, Trace NodeTrace){
		TraceNodeMap.put(host, NodeTrace);
	}
	
	/**
	 * Picks a random trace from the set
	 * @return Trace 
	 */
	public static Trace getRandomTrace(){
		int temp = r.nextInt(TraceNodeMap.size());
		Object[] ObjectAsKeys = TraceNodeMap.keySet().toArray();
        Object key = ObjectAsKeys[temp];
        MessageWrapper.setDestNode((DTNHost)key);
        return TraceNodeMap.get(key);
	}
	
	public static Map<DTNHost,Trace> returnTraceNodeMap(){
		return TraceNodeMap;
	}

}
