//Copyright Andrea Campanella, NPTLab, Public university of Milan, 2014
package NPTLab;

import java.util.HashSet;
import java.util.Set;
import core.Coord;
/**
 * Class that contains a set of all the location visited wrapped with an univocal ID.
 * CoordWrapper element contains a Coord element and his univoque ID.
 */

public class CoordSet {
	
	public static Set<CoordWrapper> GlobalSet = new HashSet<CoordWrapper>(); /** Set of All the locations **/
	
	public CoordSet(){
		}
	/**
	 * Checks if the global set already contains a Coord
	 * @param CW The CoordWrapper element that is to be checkd
	 * @return true if cointained, else false
	 */
	public static boolean contains(CoordWrapper CW) {
		boolean b = false;
		Coord new_coord = CW.getCoord();
		for(CoordWrapper cw : GlobalSet){
			if(cw.getCoord().equals(new_coord)){
				b = true;
			}
		}
		return b;
	}
	
	/**
	 * Adds a CoordWrapper to the set if not already contained
	 * @param cw The CoordWrapper element that has to be added
	 * @return true if added correctly
	 */
	public static boolean addCw(CoordWrapper cw){
		if (!CoordSet.contains(cw)){
			GlobalSet.add(cw);
			return true;
		}
		return false;
	}
	
	/**
	 * Gets an Entry ID in the global set by the Coord Passed
	 * @param c Coord element used to get the Entry in the set
	 * @return ID of the element with the passed Coord
	 */
	public static int getEntryIdByCoord(Coord c){
		int Id = 0;
		for(CoordWrapper cw : GlobalSet ){
			if(c.equals(cw.getCoord())){
				return cw.getID();
			}
		}
		return Id;
	}
	
	/**
	 * Gets an CW in the global set by the Coord Passed
	 * @param c Coord element used to get the Entry in the set
	 * @return CoordWrapper element with given Coord
	 */
	private static CoordWrapper getCwByCoord(Coord c){
		CoordWrapper res = null;
		for( CoordWrapper cw : GlobalSet){
			if(cw.getCoord().equals(c)){;
			res = cw;
			break;
			}
		}
		return res;
	}
	
	/**
	 * Returns a new CW if the cw is not contained in global set or the CW itself if it's contained, all based on passed coordinates
	 * @param c Coord element used to create the CW or to return the already existent CW
	 * @return CoordWrapper element 
	 */
	public static CoordWrapper getCw(Coord c){
		CoordWrapper DummyCw = CoordWrapper.createDummyCoordWrap(c);
		CoordWrapper res = null; 
		if(CoordSet.contains(DummyCw)){
			res= CoordSet.getCwByCoord(c);
		} else {
			res = CoordWrapper.createCoordWrap(c);
			CoordSet.addCw(res);
		}
		return res;
			
	}
	
	/**
	 * Method for printing
	 * @return string
	 */
	public String toString(){
		return GlobalSet.toString();
	}
	
	
}
