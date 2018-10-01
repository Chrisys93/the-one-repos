//Copyright Andrea Campanella, NPTLab, Public university of Milan, 2014
package NPTLab;

import java.util.StringTokenizer;
import java.util.Scanner;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.HashMap;

/** Main class to analyze the .txt ouput file created by DeliverdMessagesProfileCastReport and obtain another file with 
 * the number of duplicates of the messages and the unique ones.
 * 
 * @author Andrea Campanella, NPTLab, Public university of Milan, 2014
 *
 */

public class DeliveredMessagesProfileCastReportAnalyzer {
	public static void main (String [] args) throws IOException{
		String FilePath = "/Users/Andrea/Documents/eclipse_workspace/Time Varying Bipartite Graph Movement Model for ONE simulator/reports/definitivo_new/0.60_short1_doubleMSGReport/ProfileCast_DeliveredMessagesProfileCastReport.txt";
		Map<String,Integer> MessagesMap = new HashMap<String,Integer>();
		int TotalCopies = 0;
		int TotalMessages = 0;
		Scanner sc = new Scanner(new File(FilePath)); 
		String outFileName = "/Users/Andrea/Documents/eclipse_workspace/Time Varying Bipartite Graph Movement Model for ONE simulator/reports/definitivo_new/0.60_short1_doubleMSGReport/Delivered&Copies.txt";
		PrintWriter out = null ;
		out = new PrintWriter(new FileWriter(outFileName));;
		
		
		sc.nextLine();
		while(sc.hasNextLine()){
			StringTokenizer stk = new StringTokenizer(sc.nextLine());
			if(stk.hasMoreElements()){
				String firstEl = stk.nextElement().toString();
				if(!(firstEl.startsWith(">"))){
					String MessageIdentifier = stk.nextElement().toString();
					if(MessagesMap.containsKey(MessageIdentifier)){
						int value = MessagesMap.get(MessageIdentifier) +1;
						MessagesMap.put(MessageIdentifier,value);
						
					} else {
					MessagesMap.put(MessageIdentifier,1);
					}
				}
			}
		}
		for ( String s: MessagesMap.keySet()){
				int copies = MessagesMap.get(s);
	        	String statsText = "messaggio:  " + s + 
	        			" copie: " + copies +
	        			"\n"
	        			;
	        	out.write(statsText);
	        	if (copies > 1){
	        		TotalCopies = TotalCopies + MessagesMap.get(s);
	        		TotalMessages= TotalMessages + MessagesMap.get(s);
	        	} else {
	        		TotalMessages = TotalMessages+1;
	        	}
			}
			System.out.println("TotalMessagesDelivered: " + TotalMessages + "copie" + TotalCopies);
			out.write("Copie: " + TotalCopies + "\n" + "TotaleMessaggiConsegnati: " + TotalMessages );
			out.close();
	}

}
