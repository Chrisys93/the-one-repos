//Copyright Andrea Campanella, NPTLab, Public university of Milan, 2014
package report;


import NPTLab.TraceNodeSet;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.linalg.SingularValueDecomposition;
import core.DTNHost;
import NPTLab.Trace;

//import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Class that wirtes in a file all the matrices obtained from SVD for each node.
 * @author Andrea Campanella, NPTLab, Public university of Milan, 2014
 *
 */

public class NodesSVDReport extends Report {
	
	private  SingularValueDecomposition SVDTrace = null;
	private  DoubleMatrix2D Trace = null;
	private  DoubleMatrix2D TraceTransposed = null;
	private  DenseDoubleMatrix2D CorrectU = null;
	private  DenseDoubleMatrix2D CorrectS = null;
	private  DenseDoubleMatrix2D CorrectV = null;
	public  double similarity = 0;
	private  boolean transpose = false; //true if the matrix has been trasposed
	private Map<DTNHost,Trace> TraceNodesMap = null;
	
	
	public void done() {
	TraceNodesMap = TraceNodeSet.returnTraceNodeMap();
	
	
	Set<DTNHost> hosts = TraceNodesMap.keySet();
	
	transpose = false;
	SVDTrace = null;
	Trace = null;
	TraceTransposed = null;
	
		for( DTNHost h : hosts){
		Trace = null;
		Trace = TraceNodesMap.get(h).returnTrace();
		
		
			if(Trace.rows()>=Trace.columns()){
				SVDTrace = new SingularValueDecomposition(Trace);
			} else{
				transpose= true;
				double[][] Trace1Array=Trace.toArray(); //return the trace Matrix as an array
				TraceTransposed = new DenseDoubleMatrix2D(Trace.columns(),Trace.rows()); //creates the new trasposed matrix
		        TraceTransposed.assign(transpose(Trace1Array)); //transposes the obtained array then assigns it to the new matrix
		        SVDTrace = new SingularValueDecomposition(TraceTransposed); //do SVD on the trasposed Matrix
			}
			
			write("Stats for scenario " + getScenarioName() + 
					"\nsim_time: " + format(getSimTime())+ "\n");
			
			if(transpose){
				
				double[][] UArray1=(SVDTrace.getU()).toArray();
				double[][] SArray1=(SVDTrace.getS()).toArray();
				double[][] VArray1=(SVDTrace.getV()).toArray();
				
				CorrectU = new DenseDoubleMatrix2D((SVDTrace.getV()).columns(),(SVDTrace.getV()).rows());
		        CorrectU.assign(transpose(VArray1));
				
				CorrectS = new DenseDoubleMatrix2D((SVDTrace.getU()).columns(),(SVDTrace.getS()).rows());
		        CorrectS.assign(transpose(SArray1));
				
				CorrectV = new DenseDoubleMatrix2D((SVDTrace.getU()).columns(),(SVDTrace.getU()).rows());
		        CorrectV.assign(transpose(UArray1));
		        write("Stats for scenario " + getScenarioName() + 
						"\nsim_time: " + format(getSimTime())+ "\n");
		        String statsText = "nodo:  " + h + "\n" +
		        		"Traccia: " + Trace + "\n" +
	        			" U: " + CorrectU + "\n" +
	        			" S: " + CorrectS + "\n" +
	        			" V: " + CorrectV + "\n" +
	        			"\n"
	        			;
	        	write(statsText);
			} else {
				String statsText = "nodo:  " + h + "\n" +
						"Traccia: " + Trace + "\n" +
	        			" U: " + SVDTrace.getU() + "\n" +
	        			" S: " + SVDTrace.getS() + "\n" +
	        			" V: " + SVDTrace.getV() + "\n" +
	        			"\n"
	        			;
	        	write(statsText);
			}
		}
	}
	
	private double[][] transpose(double[][] ArrayToTanspose){
		double[][] ArrayTransposed = new double[ArrayToTanspose[0].length][ArrayToTanspose.length];
		    for (int i = 0; i < ArrayToTanspose.length; i++){
		        for (int j = 0; j < ArrayToTanspose[0].length; j++){
		        	ArrayTransposed[j][i] = ArrayToTanspose[i][j];
		        }
		    }
	    return ArrayTransposed;
	}
}
