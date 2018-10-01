//Copyright Andrea Campanella, NPTLab, Public university of Milan, 2014
package NPTLab;

import core.Message;
import core.DTNHost;


/**
 * Class that wraps a message with additional information useful for the ProfileCast(CSI: T) routing algorithm
 * @author Andrea Campanella, NPTLab, Public university of Milan, 2014
 */

public class MessageWrapper{
	
	private static DTNHost DestHost = null;
	private Message WrappedMessage = null;
	
	private Trace DestTrace = null;
	public double similarity = 0.0;
	
	/**
	 * Constructor that assigns the wrapped message to the instance of the class.
	 * @param m Message htat is wrapped
	 */
	public MessageWrapper(Message m){
		WrappedMessage = m;
	}
	
	public static void setDestNode(DTNHost host){
		DestHost = host;
	}
	
	/**
	 * Sets the Destination molbility trace for the message.
	 */
	public  void setTrace(Trace Trace){
		DestTrace = Trace;
	}
	
	/**
	 * Sets the similarity to be compared with the destination
	 */
	public void setSimilarity(double sim){
		similarity = sim;
	}
	
	/**
	 *  Returns the similarity for this message whit him.
	 *  @return double similarity
	 */
	public double  getSimilarity(){
		return similarity;
	}
	
	/**
	 *  Returns the Destination mobility trace for this message
	 *  @return Trace
	 */
	public Trace getTrace(){
		return DestTrace;
	}
}
