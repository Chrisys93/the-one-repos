//Copyright Andrea Campanella, NPTLab, Public university of Milan, 2014
package NPTLab;

import core.Message;
import java.util.Map;
import java.util.TreeMap;

/**
 * Class that contains a map to mantain the one to one realtionship between a Message and his MessageWrapper 
 * implemented with a singleton pattern
 * @author Andrea Campanella, NPTLab, Public university of Milan, 2014
 */

public class MessageWrapperMap {
	private static MessageWrapperMap MapInstance;
	private Map<Message, MessageWrapper> MessageWrapperMap = null;
	
	/**
	 * Private constructor that creates the map
	 */
	private MessageWrapperMap() {
		MessageWrapperMap = new TreeMap<Message, MessageWrapper>();
	}
	
	/**
	 * Returns the instance of the map. if the map doesn't exist the method creates it.
	 * @return Map<Message,MessageWrapper>
	 */
	public static MessageWrapperMap getInstance() {
		if (null == MapInstance) {
			MapInstance = new MessageWrapperMap();
		}
		return MapInstance;
	}
	
	/**
	 * Adds to the map an entrance matching a Message and his MessageWrapper from the Map or a new one if it didn't exist.
	 * sets the destination mobility trace and the similarity.
	 * @param m Message the message
	 * @param MessageDestTrace the destination mobility trace choosen for this message.
	 */
	public void set(Message m , Trace MessageDestTrace){
		if(MessageWrapperMap.containsKey(m)){
			MessageWrapper MW = MessageWrapperMap.get(m);
			MW.setTrace(MessageDestTrace);
			MW.setSimilarity(CompareTraces.getSimilarity(m,null));
			MessageWrapperMap.put(m,MW);
		} else {
			MessageWrapper MW = new MessageWrapper(m);
			MW.setTrace(MessageDestTrace);
			MW.setSimilarity(CompareTraces.getSimilarity(m,null));
			MessageWrapperMap.put(m,MW);
		}
	}
	
	/**
	 * Returns the Map<Message, MessageWrapper> of the singleton instance of the MEssageWrapperMap
	 */
	public Map<Message, MessageWrapper> returnMap(){
		return MessageWrapperMap;
	}
}
