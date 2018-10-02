//Copyright Andrea Campanella, NPTLab, Public university of Milan, 2014
package NPTLab;

import core.DTNHost;
import core.Coord;
import java.util.Map;
import java.util.TreeMap;

/**
 * Class that contains a map to mantain the one to one realtionship between a DTNHost and his DTNHostWrapper 
 * implemented with a singleton pattern.
 * @author Andrea Campanella, NPTLab, Public university of Milan, 2014
 */

public class DTNHWMap {
	private static DTNHWMap MapInstance;
	private Map<DTNHost, DTNHostWrapper> DTNHWMap = null; /** Map that contains the corrispondence of a DTNHost with his Wrapper **/
	
	
	/**
	 * Private constructor that creates the map
	 */
	private DTNHWMap() {
		DTNHWMap = new TreeMap<DTNHost, DTNHostWrapper> ();
	}
	
	/**
	 * Returns the instance of the map. if the map doesn't exist the method creates it.
	 * @return Map<DTNHost, DTNHostWrapper>
	 */
	public static DTNHWMap getInstance() {
		if (null == MapInstance) {
			MapInstance = new DTNHWMap();
		}
		return MapInstance;
	}
	
	/**
	 * Adds to the map an entrance of Host and a DTNHostWrapper from the DTNHWMAP or a new one if it didn't exist.
	 * adds in both cases the destination, the day and the time specified.
	 * @param h DTNHost the moving host
	 * @param destination Coord the coordinates of the location where the host is going
	 * @param day int the day in which the user visited a specified location
	 * @param time double the time spent at a single location
	 */
	public void add(DTNHost h, Coord destination, int day, double time){
		if(DTNHWMap.containsKey(h)){
			DTNHostWrapper temp = DTNHWMap.get(h);
			temp.add(destination, day, time);
			DTNHWMap.put(h,temp);
		} else {
			DTNHostWrapper temp = new DTNHostWrapper(h);
			temp.add(destination, day, time);
			DTNHWMap.put(h,temp);
		}
	}
	
	/**
	 * Returns the Map<DTNHost, DTNHostWrapper> of the singleton instance of the DTNHWmap
	 */ 
	public Map<DTNHost, DTNHostWrapper> returnMap(){
		return DTNHWMap;
	}
	
	/**
	 * Returns the DTNHostWrapper for the given DTNHost h
	 * @return DTNHostWrapper
	 */
	public DTNHostWrapper returnDTNHostWrapper(DTNHost h){
		return DTNHWMap.get(h);
	}
}
