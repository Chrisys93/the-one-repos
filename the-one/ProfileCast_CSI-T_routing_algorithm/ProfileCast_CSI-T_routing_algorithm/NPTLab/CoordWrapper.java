//Copyright Andrea Campanella, NPTLab, Public university of Milan, 2014
package NPTLab;

import core.Coord;
/**
 * Class that wraps th class Coord in an object that adds an univoque ID to a couple of Coordinates (x,y)
 */

public class CoordWrapper {
	static int old_id = 0;
	int ID = 0;
	Coord cW;
	
	static CoordWrapper dummyCw = new CoordWrapper (new Coord(0,0), -1);
	
	//empty Constructor
	public CoordWrapper(){}
	
	//constructor based only on the coord, chooses id 
	private CoordWrapper(Coord c){
		cW = c.clone();
		ID = old_id++;
	}
	/** 
	 * constructor based on coord and is, sets the id to the passed int
	 * @param c Coord on which the wrapper is to be addes
	 * @param id the id assigned to the coord.
	 */
	private CoordWrapper(Coord c, int id){
		cW = c.clone();
		ID = id;
	}
	
	/**
	 * Method to Create CW with coord calling the private constructor
	 * @param c Coord to be wrapped in the elment adding an ID
	 * @return CoordWrapper
	 */
	public static CoordWrapper createCoordWrap(Coord c){
		CoordWrapper CoordWrap = new CoordWrapper(c); 
		return CoordWrap;
	}
	/**
	 * Method to create a DummyCW with coord calling the private constructor
	 * @param c Coord to be wrapped in the elment adding an ID
	 * @return and object of CoordWrapperClasse
	 */
	public static CoordWrapper createDummyCoordWrap(Coord c){
		dummyCw.cW.setLocation(c);
		return dummyCw;
	}
	
	/**
	 * Method to the Coord cotnained in the CoordWrapper element
	 * @return Coord
	 */
	public Coord getCoord(){
		return cW;
	}
	
	/**
	 * Returns the univocal ID of the CoordWrapper element
	 * @return int
	 */
	public int getID(){
		return ID;
	}
	
	/**
	 * Method for printing
	 * @return string
	 */
	public String toString(){
		return "(" + cW.getX() + "," + cW.getY() + ") ID:" + ID;
	}
}
