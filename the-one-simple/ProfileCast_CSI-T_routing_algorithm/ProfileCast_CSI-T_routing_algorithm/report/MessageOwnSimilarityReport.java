//Copyright Andrea Campanella, NPTLab, Public university of Milan, 2014
package report;

import core.Message;
import NPTLab.MessageWrapper;
import NPTLab.MessageWrapperMap;
import java.util.Map;

/**
 * Saves and writes to a file all the messages with their own similarity.
* @author Andrea Campanella, NPTLab, Public university of Milan, 2014
 */
public class MessageOwnSimilarityReport extends Report{
	
	public void done() {
		Map<Message,MessageWrapper> OwnSimilarityMap = MessageWrapperMap.getInstance().returnMap();
		write("Message own similarity" + getScenarioName() + 
				"\nsim_time: " + format(getSimTime()));
		for ( Message m: OwnSimilarityMap.keySet()){
        	String statsText = "messaggio:  " + m + 
        			" similairty con se' stesso: " + OwnSimilarityMap.get(m).getSimilarity() + 
        			"\n"
        			;
        	write(statsText);
        }
		
	}
	

}
