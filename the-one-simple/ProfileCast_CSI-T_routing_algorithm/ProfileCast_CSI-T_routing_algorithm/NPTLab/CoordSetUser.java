//Copyright Andrea Campanella, NPTLab, Public university of Milan, 2014
package NPTLab;

import core.Coord;
import java.util.HashSet;
import java.util.Set;

/**
 * Class that contains in a set, personal for every user, every Coord a user visited paired with his map of locations and time per each location
 */

public class CoordSetUser extends CoordSet{
	
	private Set<CoordWrapperUserMap> CoordMapSet; 
	
	/**
	 * Public constuctor that assigns the Set
	 */
	public CoordSetUser(){
		CoordMapSet = new HashSet<CoordWrapperUserMap>();
		}
	
	
	/**
	 * Checks if the set contaiins a given Coord Element
	 * @param c Coord to check
	 * @return boolean true if contained, else false
	 */
	public boolean containsCoord(Coord c) {
		if(CoordMapSet.size() > 0){
			for (CoordWrapperUserMap UtilityCwum : CoordMapSet) {
				Coord new_coord = UtilityCwum.getCw().getCoord();
				if(new_coord.equals(c)){
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Adds a Coord, a day and a time for the day to the set if cointainted setDayndTime else it creates 
		a CoordWrapperUserMap object and letting that class handle the rest
	 * @param c Coord 
	 * @param Day day in wich the user visits the location
	 * @param Time time the user stay in a location
	 */
	public void add(Coord c , Integer Day, Double Time){
		CoordWrapperUserMap cwum = null; 
		CoordWrapper cwNew = CoordSet.getCw(c);
		if(!this.containsCoord(c)){
			cwum = CoordWrapperUserMap.createCoordWrapUserMap(cwNew);
			cwum.addLocationDayTime(Day, Time);
			CoordMapSet.add(cwum);
		} else {
			cwum = this.getCwumByCw(cwNew);
			cwum.addLocationDayTime(Day, Time);
		}
	}
	
	/**
	 * Returns the CoordWrapperUserMap object that contains the givent CoordWrap element
	 * @param cw CoordWrapper the object i whose map we are intrested
	 * @return CoordWrapperUserMap
	 */
	public CoordWrapperUserMap getCwumByCw(CoordWrapper cw){
		CoordWrapperUserMap LastCwum = null;
		Coord new_coord = cw.getCoord();
		for (CoordWrapperUserMap cwum : CoordMapSet){
			if(cwum.getCw().getCoord().equals(new_coord)){
				LastCwum = cwum;
				return LastCwum;
			}
		}
		return LastCwum;
	}
	
	/**
	 * Returns the number of locationa user visited ( size of his set of locations )
	 * @return int
	 */
	public int numberOfLocations(){
		return CoordMapSet.size();
	}
	
	/**
	 * Returns the (x,y) Coord object contained in a CoordWrapperUserMap
	 * @param cwum CoordWrapperUserMap
	 * @return Coord
	 */
	public Coord getCoord(CoordWrapperUserMap cwum){
		return cwum.cw.getCoord();
	}
	
	/**
	 * Returns the Set of CoordWrapperUserMap
	 * @return Set<CoordWrapperUserMap
	 */
	public Set<CoordWrapperUserMap> getSet(){
		return CoordMapSet;
	}
	
	/**
	 * Method for printing
	 * @return string
	 */
	public String toString(){
		return CoordMapSet.toString();
	}
	
	
	
	

}
