//Copyright Andrea Campanella, NPTLab, Public university of Milan, 2014
package NPTLab;


import core.Settings;
/**
 *  Class that gets from the settings file the value of the matrixCreationDays, that's the time dedicated
 *  to the creation of the the traces of the nodes
 *  @author Andrea Campanella, NPTLab, Public university of Milan, 2014
 */
public class SettingsValuesSingleton {

	private static int MatrixCreationDays = -1; /** number of days in which the matrices are created **/
	private static double GroupSpreadThreshold = -1; /** GroupSpread threshold over wich a nod is a intendend reciver **/
	//private static double DestinationThreshold = -1;
	private static int StopMessageCreationTime = -1; /** time to spot message creation in order to give a possibility to all the messages to reach their destination**/ 
	private static int ComputeMatricesDays = -1; /** number of days in which the matrices are computed **/
	public static final String PROFILECAST_ROUTER_NS = "ProfileCast";
	public static final String DAYS = "matrixCreationDays";
	public static final String GROUPSPREADTHRESHOLD = "groupSpreadThreshold";
	public static final String DESTINATIONTHRESHOLD = "destinationThreshold";
	public static final String STOPMESSAGECREATION = "stopMessageCreationTime";
	public static final String COMPUTEMATRICES = "computeMatricesDays";
	
	
	/**
	 * Gets and returns from the ProfileCast's settings file the value of the matrixCreationDays.
	 *  @return int MatrixCreationDays
	 */
	public static int getMatrixCreationDays(){
		if(MatrixCreationDays==-1){
			Settings settings = new Settings(PROFILECAST_ROUTER_NS);
			MatrixCreationDays = Integer.parseInt((settings.getSetting(DAYS)));
		} 
		return MatrixCreationDays;
	}
	
	/**
	 * Gets and returns from the ProfileCast's settings file the value of the GroupSpreadThreshold.
	 *  @return double GroupSpreadThreshold
	 */
	public static double getGroupSpreadThreshold(){
		if(GroupSpreadThreshold==-1){
			Settings settings = new Settings(PROFILECAST_ROUTER_NS);
			GroupSpreadThreshold = Double.parseDouble((settings.getSetting(GROUPSPREADTHRESHOLD)));
		} 
		return GroupSpreadThreshold;
	}
	
//	/*/**
//	 *  method that gets and returns from the ProfileCast's settings file the value of the DestinationThreshold.
//	 *  @return double DestinationThreshold
//	 */
//	public static double getDestinationThreshold(){
//		if(DestinationThreshold ==-1){
//			Settings settings = new Settings(PROFILECAST_ROUTER_NS);
//			DestinationThreshold = Double.parseDouble((settings.getSetting(DESTINATIONTHRESHOLD)));
//			System.out.println("DestinationThreshold test" + DestinationThreshold);
//		} 
//		return DestinationThreshold;
//	}
	
	/**
	 *  Gets and return from the ProfileCast's settings file the value of the StopMessageCreationTime.
	 *  @return int StopMessageCreationTime
	 */
	public static int getStopMessageCreationTime(){
		if(StopMessageCreationTime ==-1){
			Settings settings = new Settings(PROFILECAST_ROUTER_NS);
			StopMessageCreationTime = Integer.parseInt((settings.getSetting(STOPMESSAGECREATION)));
		} 
		return StopMessageCreationTime;
	}
	
	/**
	 * 	Gets and returns from the ProfileCast's settings file the value of the ComputeMatricesDays.
	 *  @return int ComputeMatricesDays
	 */
	public static int getComputeMatricesDays(){
		if(ComputeMatricesDays ==-1){
			Settings settings = new Settings(PROFILECAST_ROUTER_NS);
			ComputeMatricesDays = Integer.parseInt((settings.getSetting(COMPUTEMATRICES)));
		} 
		return ComputeMatricesDays;
	}

	
	
}
