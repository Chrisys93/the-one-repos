//Copyright Andrea Campanella, NPTLab, Public university of Milan, 2014
package NPTLab;

import core.DTNHost;
import core.Coord;
import core.Message;
import java.util.Map;
import java.util.TreeMap;

/**
 * Class that Wraps DTNHost and add the method and the objects needed by the ProfileCast routing algorithm.
 * @author Andrea Campanella, NPTLab, Public university of Milan, 2014
 */

public class DTNHostWrapper {
	
	private CoordSetUser csu = null; /** set containing all the coordinates visited by this user**/
	private Trace UserTrace = null; /** trace of the node**/
	private DTNHost Host = null;
	public Map<Message, Double> similarityMap = new TreeMap<Message, Double>();/** map containing all the similarities beetween this node and the messages he encountered **/
	
	public DTNHostWrapper (DTNHost h){
		Host = h;
		csu = new CoordSetUser();
	}
	
	/**
	 * Method that adds to the set of coordinates for the host, the new coord element, the day and the time spent at 
	 * the given location
	 * @param c Coord the location
	 * @param day Int, the day in wich the location was visited
	 * @param time Double the time spent at the given location
	 */
	public void add(Coord c, int day, double time ) {
		csu.add(c, day , time);
	}
	
	/**
	 * Creates the Trace for a specific host and adds the host and its new trace to the GLobal Map of Traces
	 * @param simulationDays int the number of days in which were saved location and time spent
	 */
	public void createTrace(int simulationDays){
		UserTrace = new Trace(csu, simulationDays);
		TraceNodeSet.add(Host,UserTrace);
	}
			
	/**
	 * Returns the trace for the specific DTNHostWrapper
	 */
	public Trace returnTrace(){
		return UserTrace;
	}
	
	/**
	 * Adds to the message with its similarity to this DTNHostWrapper to its map of similarities.
	 * @param m Message the message to be addes
	 * @param similarity double the computed similarity with the trace of the message.
	 */
	public void add(Message m, double similarity){
		similarityMap.put(m,similarity);
	} 
	
	/**
	 * Returns the similarity for the passed Message
	 * @param m the message in which similarity we are interested
	 * @return double Similarity
	 */
	public double getSimilarity(Message m){
		return similarityMap.get(m);
	}
	
	/**
	 * Returns the similarity map of this DTNHostWrapper
	 * @return Map<Message,Double> 
	 */
	public Map<Message,Double> returnSimilarityMap(){
		return similarityMap;
	}
	
			
			

}
