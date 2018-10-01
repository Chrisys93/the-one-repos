//Copyright Andrea Campanella, NPTLab, Public university of Milan, 2014
package report;
import core.DTNHost;
import NPTLab.DTNHWMap;
import NPTLab.DTNHostWrapper;
import core.Coord;
import core.MovementListener;
//import core.Settings;
import core.SimClock;
import NPTLab.SettingsValuesSingleton;

/**
 * Class that gets all the informations on the mobility of a user by implementing the MovementListener.
 * @author Andrea Campanella, NPTLab, Public university of Milan, 2014
 */

public class CreateNodeTrace extends Report implements MovementListener{
	
	private boolean TraceCreated = false;
	
	/**
	 * When the method is called it saves in the DTNHWMap the destination visited, the day and the time.
	 * @param host DTNHost the moving host
	 * @param destination Coord the coordinates of the location where the host is going
	 * @param speed Double the speed of the movement, unused in this implementation
	 */
	@Override
	public void newDestination(DTNHost host, Coord destination, double speed) {
		int day = convertSecondsToDays(SimClock.getTime());
		if(SettingsValuesSingleton.getMatrixCreationDays()>=day){
			double time = host.getMovementModel().returnWaitTime();
			DTNHWMap.getInstance().add(host,destination, day, time);
		} else if (TraceCreated == false){ 
			DTNHostWrapper DTNHW = DTNHWMap.getInstance().returnDTNHostWrapper(host);
			DTNHW.createTrace(SettingsValuesSingleton.getMatrixCreationDays());
		}
	}
	/**
	 * When the node is assigned it's first location this method saves in the DTNHWMap the first location visited 
	 * the day and the time.
	 * @param host DTNHost the moving host
	 * @param location Coord the coordinates of the location where the host is put
	 */
	@Override
	public void initialLocation(DTNHost host, Coord location) {
		int day = convertSecondsToDays(SimClock.getTime());
		if(SettingsValuesSingleton.getMatrixCreationDays()>=day){
			double time = host.getMovementModel().returnWaitTime();
			DTNHWMap.getInstance().add(host,location, day, time);
		}
		
	}
	
	/**
	 * Converts a double number of seconds in it's corresponding number of days
	 * @param seconds Double the time in seconds the simulation is at.
	 */
	private static int convertSecondsToDays(double seconds){
		int days = (int) (seconds / (60*60*24));
		return days;
	}
}
