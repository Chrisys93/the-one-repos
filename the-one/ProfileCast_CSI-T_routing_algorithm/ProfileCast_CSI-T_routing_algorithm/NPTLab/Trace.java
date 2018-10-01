//Copyright Andrea Campanella, NPTLab, Public university of Milan, 2014
package NPTLab;

import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;

/**
 * Class that saves the traces of a user containing laction, day and duration of the visit 
 * @author Andrea Campanella, NPTLab, Public university of Milan, 2014
 */

public class Trace {
	
	private DoubleMatrix2D Trace = null;
	private int SimulationDays = 0; 
	private Map<Integer,Integer> IdColumnMap = new HashMap<Integer,Integer>();
	private Map<Integer,Integer> IdColumnMapInverse = new HashMap<Integer,Integer>();
	
	/**
	 * Creates a trace for a node based on his Set of locations and the nuber of simulation days 
	 * @param csu The CoordSetUser element for the node, 
	 * @param simulationDays The number os simylation days the node had created thei traces for 
	 */
	public Trace(CoordSetUser csu, int simulationDays){
		SimulationDays = simulationDays;
		int locationNumbers = csu.numberOfLocations(); // number of location for the user
		int c = 0;
		// if node does not have trace it creates it
		if(Trace == null){
		Trace = new DenseDoubleMatrix2D(SimulationDays+1,locationNumbers);
		Trace.assign(0.0);
		}
		
		//adds CoordWrapper to trace and writes a map that keep trace of wich column has wich id.
		Set<CoordWrapperUserMap> userSet = csu.getSet(); //returns the set of CoordWrapperUserMap that contains the maps 
		for(CoordWrapperUserMap cwum : userSet){
			int locId = cwum.getCw().getID();
			Map<Integer,Double> coordMap = cwum.getMap(); // return the map of the user containing the values of location and time spent
			for(Map.Entry<Integer,Double> entry : coordMap.entrySet()){
				int day = entry.getKey();
				double time = entry.getValue();
				Trace.set(day, c , time ); //sets the values in the trace of the user.
				IdColumnMap.put(c,locId); // map with column ad key and id as value
				IdColumnMapInverse.put(locId, c); //map with idCoord as key and column as value
			}
		c++;
		}
		this.normalize();
	}
	
	/**
	 * Normalizes the trace
	 */
	private void normalize(){
		for(int i = 0; i<Trace.rows(); i++){
			double[] row = (Trace.viewRow(i)).toArray();
			double min = getMin(row);
			double max = getMax(row);
			if(min==0 && max==0 ){
				break;
			}
			 for (int j = 0; j<row.length ;j++){
				 double normalized = normalizeValue(row[j], min, max);
				 Trace.setQuick(i,j,normalized);
			 }
			
		}
	}
	
	/**
	 * Gets the minimum value from an array of double
	 * @param ArrayToNormalize The array containing the values the must be normalized
	 * @return dobule, minimum value for the array 
	 */
	private static double getMin(double[] ArrayToNormalize){
		Set<Double> b = new HashSet<Double>();
		for(int i=0; i<ArrayToNormalize.length; i++){
			b.add(ArrayToNormalize[i]);
		}
        return Collections.min(b);
	}
	
	/**
	 * Gets the maximum value from an array of double
	 * @param ArrayToNormalize The array containing the values the must be normalized
	 * @return dobule, maximum value for the array 
	 */
	private static double getMax(double[] ArrayToNormalize){
		Set<Double> b = new HashSet<Double>();
		for(int i=0; i<ArrayToNormalize.length; i++){
			b.add(ArrayToNormalize[i]);
		}
        return Collections.max(b);
	}
	
	/**
	 * Normalizes a value in 0-1 boundary in and array form the min and the max in the array itself. 
	 * @param Value value that must be normalized from 0 to 1 
	 * @param Min the minimum value in the array
	 * @param Max the maximum value in the array.
	 */
	private static double normalizeValue(double Value, double Min, double Max){
		return (Value-Min)/(Max-Min);
	}
	
	/**
	 * Returns the  Trace for the node
	 * @return cern.colt.lang.DoubleMatrix2D
	 */
	public DoubleMatrix2D returnTrace(){
		return Trace;
	}
	/**
	 * Returns the  map with column ad key and id as value
	 * @return Map<Integer,Integer>
	 */
	public Map<Integer,Integer> returnCombinationMap(){
		return IdColumnMap;
	}
	
	/**
	 * Returns the map with idCoord as key and column as value
	 * @return Map<Integer,Integer>
	 */
	public Map<Integer,Integer> returnInverseCombinationMap(){
		return IdColumnMapInverse;
	}
	
	/**
	 * Method for printing
	 * @return string
	 */
	public String toString(){
		return Trace.toString();
	}

}
