//Copyright Andrea Campanella, NPTLab, Public university of Milan, 2014
package report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import NPTLab.DTNHWMap;
import NPTLab.SettingsValuesSingleton;
import core.DTNHost;
import core.Message;
import core.MessageListener;
import core.Settings;
import core.SimClock;


/**
 * Report for generating different kind of total statistics about message
 * relaying performance. Messages that were created during the warm up period
 * are ignored. Some of the statistic aknowleged are: numero of messages dropped, removed, relayed, created, delivered,
 * latency, number of hops.
 * <P><strong>Note:</strong> if some statistics could not be created (e.g.
 * overhead ratio if no messages were delivered) "NaN" is reported for
 * double values and zero for integer median(s).
 * @author Andrea Campanella, NPTLab, Public university of Milan, 2014
 */
public class MessageStatsProfileCastReport extends Report implements MessageListener {
	private  Map<String, Double> creationTimes;
	private  List<Double> latencies;
	private  List<Integer> hopCounts;
	private  List<Double> msgBufferTime;
	
	private  int nrofDropped;
	private  int nrofRemoved;
	private  int nrofStarted;
	private  int nrofAborted;
	private  int nrofRelayed;
	private  int nrofCreated;
	private  int nrofTotalMessages;
	private  int nrofDeliveredtoGroup;
	private  int nrofDuplicates;
	private  int nrofDeliveredtoTarget;
	public static final String GROUP_NS = "Group";
	public static final String EVENTS1_NS = "Events1";
	
	/**
	 * Constructor.
	 */
	public MessageStatsProfileCastReport() {
		init();
	}

	@Override
	protected void init() {
		super.init();
		this.creationTimes = new HashMap<String, Double>();
		this.latencies = new ArrayList<Double>();
		this.msgBufferTime = new ArrayList<Double>();
		this.hopCounts = new ArrayList<Integer>();
		
		this.nrofDropped = 0;
		this.nrofRemoved = 0;
		this.nrofStarted = 0;
		this.nrofAborted = 0;
		this.nrofRelayed = 0;
		this.nrofCreated = 0;
		this.nrofTotalMessages = 0;
		this.nrofDeliveredtoGroup = 0;
		this.nrofDeliveredtoTarget = 0;
	}

	
	public void messageDeleted(Message m, DTNHost where, boolean dropped) {
		if (isWarmupID(m.getId())) {
			return;
		}
		
		if (dropped) {
			this.nrofDropped++;
		}
		else {
			this.nrofRemoved++;
		}
		
		this.msgBufferTime.add(getSimTime() - m.getReceiveTime());
	}

	
	public void messageTransferAborted(Message m, DTNHost from, DTNHost to) {
		if (isWarmupID(m.getId())) {
			return;
		}
		
		this.nrofAborted++;
	}

	
	public void messageTransferred(Message m, DTNHost from, DTNHost to,
			boolean finalTarget) {
		if (isWarmupID(m.getId())) {
		return;
		}

	nrofRelayed++;
	
	if (((DTNHWMap.getInstance().returnMap()).get(to).similarityMap.get(m) > SettingsValuesSingleton.getGroupSpreadThreshold())) {
		latencies.add(SimClock.getTime() - 
			creationTimes.get(m.getId()) );
		nrofDeliveredtoGroup++;
		hopCounts.add(m.getHops().size() - 1);
	} 
	if (finalTarget){
	nrofDeliveredtoTarget++;
	hopCounts.add(m.getHops().size() - 1);
	}
}


	public void newMessage(Message m) {
		if (isWarmup()) {
			addWarmupID(m.getId());
			return;
		}
		
		this.creationTimes.put(m.getId(), getSimTime());
		this.nrofCreated++;
		this.nrofTotalMessages++;
	}
	
	
	public void messageTransferStarted(Message m, DTNHost from, DTNHost to) {
		if (isWarmupID(m.getId())) {
			return;
		}

		this.nrofStarted++;
	}
	
	

	@Override
	public void done() {
		write("Message stats for scenario " + getScenarioName() + 
				"\nsim_time: " + format(getSimTime()));
		double deliveryProbGroup = 0; // delivery probability
		double deliveryProbTarget = 0;
		double deliveryProbGroupNoDuplicates = 0;
		double overHead = Double.NaN;	// overhead ratio
		
		if (this.nrofCreated > 0) {
			deliveryProbGroup = (1.0 * this.nrofDeliveredtoGroup) / this.nrofCreated;
			deliveryProbTarget = (1.0 * this.nrofDeliveredtoTarget) / this.nrofCreated;
			deliveryProbGroupNoDuplicates = (1.0 * this.nrofDeliveredtoGroup-this.nrofDuplicates) / this.nrofCreated;
		}
		if (this.nrofDeliveredtoGroup > 0) {
			overHead = (1.0 * (this.nrofRelayed - this.nrofDeliveredtoGroup)) /
				this.nrofDeliveredtoGroup;
		}
		Settings s = new Settings(GROUP_NS);
		Settings s1 = new Settings(EVENTS1_NS);
		
		String statsText = "created: " + this.nrofCreated + 
			"\ntotal Messages (with duplicates from group_spread):" + this.nrofTotalMessages +
			"\ntried exchanges: " + this.nrofStarted + 
			"\ndone exchanges: " + this.nrofRelayed +
			"\naborted: " + this.nrofAborted +
			"\ndropped: " + this.nrofDropped +
			"\nremoved: " + this.nrofRemoved +
			"\ndelivered to Group: " + this.nrofDeliveredtoGroup +
			"\ndelivered to Target: " + this.nrofDeliveredtoTarget +
			"\ndelivery_prob_toGroup: " + format(deliveryProbGroup) +
			"\ndelivery_prob_toGroup_no_duplicates: " + format(deliveryProbGroupNoDuplicates) +
			"\ndelivery_prob_toTarget: " + format(deliveryProbTarget) +
			"\noverhead_ratio: " + format(overHead) + 
			"\nlatency_avg: " + getAverage(this.latencies) +
			"\nlatency_med: " + getMedian(this.latencies) + 
			"\nhopcount_avg: " + getIntAverage(this.hopCounts) +
			"\nhopcount_med: " + getIntMedian(this.hopCounts) + 
			"\nbuffertime_avg: " + getAverage(this.msgBufferTime) +
			"\nbuffertime_med: " + getMedian(this.msgBufferTime) +
			"\ngroupSpread threshold " + SettingsValuesSingleton.getGroupSpreadThreshold() +
			"\nWait_Time: " + s.getSetting("waitTime") + "sec" +
			"\nMessage Created Every: " + s1.getSetting("interval") + "sec" +
			"\nMessage TTL:" + s.getSetting("msgTtl") + "min"
			;
		
		write(statsText);
		super.done();
	}
	
}
