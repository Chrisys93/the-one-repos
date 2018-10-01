//Copyright Andrea Campanella, NPTLab, Public university of Milan, 2014
package NPTLab;

import java.util.HashMap;

/**
 * Class that creates a map specific for the user for each location containing the time spent at each location in a given day.
 * @author Andrea Campanella, NPTLab, Public university of Milan, 2014
 */

public class CoordWrapperUserMap extends CoordWrapper{
	
	CoordWrapper cw;
	private HashMap<Integer,Double> LocationMap;
	
	/**
	 * Private constructor for CoordWrapperUserMap
	 * @param cwUser Element of CoordWrapper class
	 */
	private CoordWrapperUserMap(CoordWrapper cwUser){
		cw = cwUser;
		LocationMap = new HashMap<Integer,Double>();
	}
	
	/**
	 * Creates a CoordWrapUserMap
	 * @param cwUser Element of CoordWrapper class
	 * @return CoordWrapperUserMap
	 */
	public static CoordWrapperUserMap createCoordWrapUserMap(CoordWrapper cwUser){
		CoordWrapperUserMap CoordWrapUser = new CoordWrapperUserMap(cwUser);
		return CoordWrapUser;
	}
	
	/**
	 * Adds a location and his time if it's not already contained, if is already in the map adds the time for that day
	 * @param Day the day the user visited a specific location, Time time spent in that location
	 * @param Time the time spent at the given location that day
	 */
	public void addLocationDayTime(int Day, Double Time){
		if(!LocationMap.containsKey(Day)){
			LocationMap.put(Day,Time);
		} else {
			Double TemporaryTime = LocationMap.get(Day);
			Time = TemporaryTime + Time;
			LocationMap.put(Day, Time);
		}
		
	}
	
	/**
	 * Returns the CoordWrapper for this instance of the map
	 * @return CoordWrapper
	 */
	public CoordWrapper getCw(){
		return cw; 
	}
	
	/**
	 * Returns the Map
	 * @return Map
	 */
	public HashMap<Integer,Double> getMap(){
		return LocationMap;
	}
	
	/**
	 * Sets the CoordWrapper
	 * @param cwCorrectId CoordWrapper element with the correct ID
	 */
	public void setCw(CoordWrapper cwCorrectId){
		cw = cwCorrectId;
	}
	
	/**
	 * Method for printing
	 * @return string
	 */
	public String toString(){
		return "CoordWrap" + this.cw.toString() + " LocationMap" +  LocationMap.toString();
	}
}
