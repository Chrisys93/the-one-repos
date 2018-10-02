//Copyright Andrea Campanella, NPTLab, Public university of Milan, 2014
package NPTLab;

import core.Message;
import core.DTNHost;
import java.util.SortedSet;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.TreeSet;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import cern.colt.matrix.DoubleMatrix1D;
//import cern.colt.matrix.impl.DenseDoubleMatrix1D;
import cern.colt.matrix.linalg.SingularValueDecomposition;
//import cern.colt.matrix.linalg.Algebra;
import java.util.Set;
import java.util.Iterator;
import org.ejml.data.D1Matrix64F;
import org.ejml.data.DenseMatrix64F;
import org.ejml.alg.dense.mult.VectorVectorMult;

/**
 * Class that computes the similarty between two given traces of two different users.
 */


public class CompareTraces {
	
	private static SingularValueDecomposition SVDTrace1 = null;
	private static SingularValueDecomposition SVDTrace2 = null;
	private static SortedSet<Integer> LocIdSet1 = null; /**set of Coord elemnts ( locations visited ) of first note sorted by ID**/
	private static SortedSet<Integer> LocIdSet2 = null; /**set of locations Coord elemnts ( locations visited ) of second note sorted by ID**/
	private static SortedSet<Integer> LocIdSetGeneral = null; /**set of locations Coord elemnts ( locations visited ) of both nodes sorted by ID**/
	private static DoubleMatrix2D Trace1 = null; /** trace of first node **/
	private static DoubleMatrix2D Trace1Transposed = null;
	private static DoubleMatrix2D Trace2 = null; /**trace of second node **/
	private static DoubleMatrix2D Trace2Transposed = null;
	private static DenseDoubleMatrix2D CorrectU1 = null;
	private static DenseDoubleMatrix2D CorrectS1 = null;
	private static DenseDoubleMatrix2D CorrectV1 = null;
	private static DenseDoubleMatrix2D CorrectU2 = null;
	private static DenseDoubleMatrix2D CorrectS2 = null;
	private static DenseDoubleMatrix2D CorrectV2 = null;
	private static Map<double[],Double> EigenAndWeightMapTargetNode= new HashMap<double[], Double>(); /** map of weights and corresponding eigen vectors first node**/
	private static Map<double[],Double> EigenAndWeightMapNewNode = new HashMap<double[], Double>(); /** maps of wights and correspondign eigen vectors second node**/
	public static double similarity = 0;
	private static boolean transpose1 = false; //true if the matrix has been trasposed
	private static boolean transpose2 = false;
	private static Map<Integer,Integer> CombinationMap1 = null;
	private static Map<Integer,Integer> CombinationMap2 = null;
	private static Map<String,Double> AllSimilaritiesMessages = new HashMap<String,Double>(); /** map of a couple node-message and their similarity**/
	private static Map<String,Double> AllSimilaritiesNodes = new HashMap<String,Double>();	/** map of a couple of nodes and their similarity**/
	
		
	/**
	 * Computes the similarty between two given traces.
	 * @param userTrace1 Trace of Node1
	 * @param userTrace2 Trace of the Node2
	 * @param SimulationDays duration of the period in wich the Trace were populated
	 * @param MessageOwnerSimilarity similarity between the owner of the message trace and the trace of the target node
	 * @param m Message wich trace is compared.
	 * @return boolean true if similarity>MessageOwnerSimilarity, so the message must be passed
	 */
	public static boolean compareTwoTracesMessage(Trace userTrace1, Trace userTrace2, int SimulationDays, double MessageOwnerSimilarity, Message m, DTNHost h){
		
		
		SVDTrace1 = null;
		SVDTrace2 = null;
		LocIdSet1 = null;
		LocIdSet2 = null;
		LocIdSetGeneral = null;
		Trace1 = null;
		Trace1Transposed = null;
		Trace2 = null;
		Trace2Transposed = null;
		CorrectU1 = null;
		CorrectS1 = null;
		CorrectV1 = null;
		CorrectU2 = null;
		CorrectS2 = null;
		CorrectV2 = null;
		EigenAndWeightMapTargetNode= new HashMap<double[], Double>();
		EigenAndWeightMapNewNode = new HashMap<double[], Double>();
		similarity = 0;
		transpose1 = false; /**true if the matrix has been trasposed**/
		transpose2 = false;
		CombinationMap1 = null;
		CombinationMap2 = null;
		
		
	
		LocIdSet1 = new TreeSet<Integer>();
		LocIdSet2 = new TreeSet<Integer>();
		LocIdSetGeneral = new TreeSet<Integer>();
		CombinationMap1 = userTrace1.returnInverseCombinationMap();
		CombinationMap2 = userTrace2.returnInverseCombinationMap();	
		LocIdSet1.addAll(CombinationMap1.keySet()); 
		
		LocIdSet2.addAll(CombinationMap2.keySet()); 
		
		LocIdSetGeneral.addAll(LocIdSet1); 
		
		LocIdSetGeneral.addAll(LocIdSet2);
		
		
		int locationNumber = LocIdSetGeneral.size(); // total number of locations visited by both the Nodes
		
		// allocation of the new matrices for both nodes with the number of rows equal to the total nuber of location visited by both Nodes and Rows = simulation days before routing 
		Trace1 = new DenseDoubleMatrix2D(SimulationDays+1,locationNumber); 
		Trace2 = new DenseDoubleMatrix2D(SimulationDays+1,locationNumber);
		
		
		//puts 0.0 in all cells
		Trace1.assign(0.0);
		Trace2.assign(0.0);
		
		int columnIndex1 = 0;
		int columnIndex2 = 0;
		
		
		for (int loc : LocIdSetGeneral){
			//allocation of the values of visited location for node1 matrix based oh his set of locations
			if (LocIdSet1.contains(loc)){
				int column = CombinationMap1.get(loc);
				DoubleMatrix1D columnMatrix =  userTrace1.returnTrace().viewColumn(column); //gets the correct comumn in the trace based on the map id/Coord
				double[] columnArray = columnMatrix.toArray(); 
				int row = 0;
				for (double value : columnArray){
					Trace1.set(row, columnIndex1, value); // puts the correct values in the new Trace that will be used for comparison.
					row++;
				}
				columnIndex1++;
				
			} else {
				columnIndex1++; // if the location is not in the set of the nod simply leaves the comlumn to all 0.0 and proceeds
			}
			
			//same procedure senn for node1 is used for population of matrix for node 2
			if (LocIdSet2.contains(loc)){
				int column = CombinationMap2.get(loc);
				DoubleMatrix1D columnMatrix =  userTrace2.returnTrace().viewColumn(column);
				double[] columnArray = columnMatrix.toArray(); 
				int row = 0;
				for (double value : columnArray){
					Trace2.set(row, columnIndex2, value);
					row++;
				}columnIndex2++;
				
			} else {
				columnIndex2++;
			}
		}
		

		// compute singular value decompostion (SVD) , if matrices are rows<columns, first transposes the matrix, then does SVD, then re transpose the results and saves them
		transpose1 = false; //true if the matrix has been trasposed
		transpose2 = false; 
		
		if(Trace1.rows()>=Trace1.columns()){
			SVDTrace1 = new SingularValueDecomposition(Trace1);
		} else{
			transpose1= true;
			
			double[][] Trace1Array=Trace1.toArray(); //return the trace Matrix as an array
			Trace1Transposed = new DenseDoubleMatrix2D(locationNumber,SimulationDays+1); //creates the new trasposed matrix
	        Trace1Transposed.assign(CompareTraces.transpose(Trace1Array)); //transposes the obtained array then assigns it to the new matrix
	        SVDTrace1 = new SingularValueDecomposition(Trace1Transposed); //do SVD on the trasposed Matrix
		}
		
		//Same procedure for second matrix
		if(Trace2.rows()>=Trace2.columns()){
			SVDTrace2 = new SingularValueDecomposition(Trace2);
		} else{
			transpose2= true;
			double[][] Trace2Array=Trace2.toArray();//returns the trace Matrix as an array
	        Trace2Transposed = new DenseDoubleMatrix2D(locationNumber,SimulationDays+1);//creates the new trasposed matrix
	        Trace2Transposed.assign(CompareTraces.transpose(Trace2Array)); //transposes the obtained array then assigns it to the new matrix
	        SVDTrace2 = new SingularValueDecomposition(Trace2Transposed);//does SVD on the trasposed Matrix
		}
		
		
		//check if matrices has been transposed, in which case it transposes the results of SVD computation and saves them in new matrices
		
		if(transpose1){
			
			double[][] UArray1=(SVDTrace1.getU()).toArray();
			double[][] SArray1=(SVDTrace1.getS()).toArray();
			double[][] VArray1=(SVDTrace1.getV()).toArray();
			
			CorrectU1 = new DenseDoubleMatrix2D((SVDTrace1.getV()).columns(),(SVDTrace1.getV()).rows());
	        CorrectU1.assign(CompareTraces.transpose(VArray1));
			
			CorrectS1 = new DenseDoubleMatrix2D((SVDTrace1.getU()).columns(),(SVDTrace1.getS()).rows());
	        CorrectS1.assign(CompareTraces.transpose(SArray1));
			
			CorrectV1 = new DenseDoubleMatrix2D((SVDTrace1.getU()).columns(),(SVDTrace1.getU()).rows());
	        CorrectV1.assign(CompareTraces.transpose(UArray1));
		}
		
		if(transpose2){
			
			double[][] UArray2=(SVDTrace2.getU()).toArray();
			double[][] SArray2=(SVDTrace2.getS()).toArray();
			double[][] VArray2=(SVDTrace2.getV()).toArray();
			
			CorrectU2 = new DenseDoubleMatrix2D((SVDTrace2.getV()).columns(),(SVDTrace2.getV()).rows());
	        CorrectU2.assign(CompareTraces.transpose(VArray2));
			
			CorrectS2 = new DenseDoubleMatrix2D((SVDTrace2.getS()).columns(),(SVDTrace2.getS()).rows());
	        CorrectS2.assign(CompareTraces.transpose(SArray2));
			
			CorrectV2 = new DenseDoubleMatrix2D((SVDTrace2.getU()).columns(),(SVDTrace2.getU()).rows());
	        CorrectV2.assign(CompareTraces.transpose(UArray2));// assign the values of the matrix U to the matrix V and vice-versa because Of the transposition of SVD results
	        //inverts the matrices, if normally SVD(A)=U.S.V instead SVD(Atransposed) = V.S.U
		} 
		
		
		//compute similarity
		
		//obtain eigen vecotors and weights for Node1, if transpose = true, use directly SVD matrices, else use the corrected one.
		if(transpose1){
			EigenAndWeightMapTargetNode = CompareTraces.createEigenAndWeightMap(CorrectS1, CorrectV1);
		} else {
			EigenAndWeightMapTargetNode = CompareTraces.createEigenAndWeightMap(SVDTrace1.getS(),SVDTrace1.getV());
		}
		
		
		//obtain eigen vecotors and weights for Node2, if transpose = true, use directly SVD matrices, else use the corrected one.
		if(transpose2){
			EigenAndWeightMapNewNode= CompareTraces.createEigenAndWeightMap(CorrectS2, CorrectV2);
		} else {
			EigenAndWeightMapNewNode= CompareTraces.createEigenAndWeightMap(SVDTrace2.getS(),SVDTrace2.getV());
		}
		
		//compute the similarity between the two Traces for Node1 and Node2
		Double TotalWeight1=0.0;
		Double TotalWeight2=0.0;
		Set<Map.Entry<double[], Double>> EigenSet1 = EigenAndWeightMapNewNode.entrySet();
		Set<Map.Entry<double[], Double>> EigenSet2 = EigenAndWeightMapTargetNode.entrySet();
		Iterator<Map.Entry<double[], Double>> ite1 = EigenSet1.iterator();
		Iterator<Map.Entry<double[], Double>> ite2 = EigenSet2.iterator();
		Set<double[]> Values1 = EigenAndWeightMapNewNode.keySet();
		Set<double[]> Values2 = EigenAndWeightMapTargetNode.keySet();
		Iterator<double[]> iteV1 = Values1.iterator();
		Iterator<double[]> iteV2 = Values2.iterator();
		
		int firstCounter= EigenAndWeightMapNewNode.size();
		int secondCounter= EigenAndWeightMapTargetNode.size();
		
		similarity=0.0;
		while(firstCounter!=0){
			Map.Entry<double[], Double> FirstEntry = ite1.next(); // get first entry of the map for node 1
			double firstWeight = (Double) FirstEntry.getValue(); // get value ( weight ) for node 1
			double[] firstArray = iteV1.next(); //get eigen vector
			D1Matrix64F firstMatrix = new DenseMatrix64F(1, firstArray.length); //create first array for inner product
			firstMatrix.setData(firstArray); //set the data of the first eigen vector in the array for inner product
			TotalWeight2=0.0;
			ite2 = EigenSet2.iterator(); //restarts the iterators and the valuesused in the second for
			iteV2 = Values2.iterator();
			int secondCounterCopy = secondCounter;
			while(secondCounterCopy!=0){
				Map.Entry<double[], Double> SecondEntry = ite2.next();// get first entry of the map for node 2
				double secondWeight = (Double) SecondEntry.getValue();// get value ( weight ) for node 2
				double[] secondArray = iteV2.next();//get eigen vector
				D1Matrix64F secondMatrix = new DenseMatrix64F(1, secondArray.length); //create second array for inner product
				secondMatrix.setData(secondArray);//set the data of the second eigen vector in the array for inner product
				double mult_vettori = 0.0;
				for (int i = 0; i<firstArray.length; i++){
						mult_vettori = mult_vettori + firstArray[i]*secondArray[i];
				} 
				similarity = similarity + firstWeight*secondWeight*(Math.abs(VectorVectorMult.innerProd(firstMatrix,secondMatrix))); // compute the partial similarity and sums to the total already computed.
				TotalWeight2 = TotalWeight2 + secondWeight; // updates total weight 2
				secondCounterCopy--; // updates counter
			}
			TotalWeight1 = TotalWeight1+firstWeight;// updates total weight 1
			firstCounter--; // updates counter
		}
		
		AllSimilaritiesMessages.put(m+","+h,similarity);
		//if the computed similarity is greater then the one the owner of the message has with the target return true so the message is passed, else false and the message stays on the same node
		if(similarity>=MessageOwnerSimilarity){
			return true;
		} else {
			return false;
		}
		
	}
	
public static void compareTwoTracesNodes(Trace userTrace1, Trace userTrace2, int SimulationDays, DTNHost h1, DTNHost h2){
		
		
		SVDTrace1 = null;
		SVDTrace2 = null;
		LocIdSet1 = null;
		LocIdSet2 = null;
		LocIdSetGeneral = null;
		Trace1 = null;
		Trace1Transposed = null;
		Trace2 = null;
		Trace2Transposed = null;
		CorrectU1 = null;
		CorrectS1 = null;
		CorrectV1 = null;
		CorrectU2 = null;
		CorrectS2 = null;
		CorrectV2 = null;
		EigenAndWeightMapTargetNode= new HashMap<double[], Double>();
		EigenAndWeightMapNewNode = new HashMap<double[], Double>();
		similarity = 0;
		transpose1 = false; //true if the matrix has been trasposed
		transpose2 = false;
		CombinationMap1 = null;
		CombinationMap2 = null;
		
		LocIdSet1 = new TreeSet<Integer>();
		LocIdSet2 = new TreeSet<Integer>();
		LocIdSetGeneral = new TreeSet<Integer>();
		CombinationMap1 = userTrace1.returnInverseCombinationMap();
		CombinationMap2 = userTrace2.returnInverseCombinationMap();	
		LocIdSet1.addAll(CombinationMap1.keySet()); //set of Coord elemnts ( locations visited ) of first note sorted by ID 
		
		LocIdSet2.addAll(CombinationMap2.keySet()); //set of locations Coord elemnts ( locations visited ) of second note sorted by ID
		
		LocIdSetGeneral.addAll(LocIdSet1); //set of locations Coord elemnts ( locations visited ) of both nodes sorted by ID
		
		LocIdSetGeneral.addAll(LocIdSet2);
		
		
		int locationNumber = LocIdSetGeneral.size(); // total number of locations visited by both the Nodes
		
		// allocation of the new matrices for both nodes with the number of rows equal to the total nuber of location visited by both Nodes and Rows = simulation days before routing 
		Trace1 = new DenseDoubleMatrix2D(SimulationDays+1,locationNumber); 
		Trace2 = new DenseDoubleMatrix2D(SimulationDays+1,locationNumber);
		
		
		//puts 0.0 in all cells
		Trace1.assign(0.0);
		Trace2.assign(0.0);
		
		int columnIndex1 = 0;
		int columnIndex2 = 0;
		
		
		for (int loc : LocIdSetGeneral){
			//allocation of the values of visited location for node1 matrix based oh his set of locations
			if (LocIdSet1.contains(loc)){
				int column = CombinationMap1.get(loc);
				DoubleMatrix1D columnMatrix =  userTrace1.returnTrace().viewColumn(column); //gets the correct comumn in the trace based on the map id/Coord
				double[] columnArray = columnMatrix.toArray(); 
				int row = 0;
				for (double value : columnArray){
					Trace1.set(row, columnIndex1, value); // puts the correct values in the new Trace that will be used for comparison.
					row++;
				}
				columnIndex1++;
				
			} else {
				columnIndex1++; // if the location is not in the set of the nod simply leaves the comlumn to all 0.0 and proceeds
			}
			
			//same procedure senn for node1 is used for population of matrix for node 2
			if (LocIdSet2.contains(loc)){
				int column = CombinationMap2.get(loc);
				DoubleMatrix1D columnMatrix =  userTrace2.returnTrace().viewColumn(column);
				double[] columnArray = columnMatrix.toArray(); 
				int row = 0;
				for (double value : columnArray){
					Trace2.set(row, columnIndex2, value);
					row++;
				}columnIndex2++;
				
			} else {
				columnIndex2++;
			}
		}
		
		// compute singular value decompostion (SVD) , if matrices are rows<columns, first transposes the matrix, then does SVD, then re transpose the results and saves them
		transpose1 = false; //true if the matrix has been trasposed
		transpose2 = false; 
		
		if(Trace1.rows()>=Trace1.columns()){
			SVDTrace1 = new SingularValueDecomposition(Trace1);
		} else{
			transpose1= true;
			
			double[][] Trace1Array=Trace1.toArray(); //return the trace Matrix as an array
			Trace1Transposed = new DenseDoubleMatrix2D(locationNumber,SimulationDays+1); //creates the new trasposed matrix
	        Trace1Transposed.assign(CompareTraces.transpose(Trace1Array)); //transposes the obtained array then assigns it to the new matrix
	        SVDTrace1 = new SingularValueDecomposition(Trace1Transposed); //do SVD on the trasposed Matrix
		}
		
		//Same procedure for second matrix
		if(Trace2.rows()>=Trace2.columns()){
			SVDTrace2 = new SingularValueDecomposition(Trace2);
		} else{
			transpose2= true;
			double[][] Trace2Array=Trace2.toArray();//return the trace Matrix as an array
		    Trace2Transposed = new DenseDoubleMatrix2D(locationNumber,SimulationDays+1);//creates the new trasposed matrix
	        Trace2Transposed.assign(CompareTraces.transpose(Trace2Array)); //transposes the obtained array then assigns it to the new matrix
	        SVDTrace2 = new SingularValueDecomposition(Trace2Transposed);//do SVD on the trasposed Matrix
		}
		
		
		//check if matrices has been transposed, in which case it transposes the results of SVD computation and saves them in new matrices
		
		if(transpose1){
			
			double[][] UArray1=(SVDTrace1.getU()).toArray();
			double[][] SArray1=(SVDTrace1.getS()).toArray();
			double[][] VArray1=(SVDTrace1.getV()).toArray();
			
			CorrectU1 = new DenseDoubleMatrix2D((SVDTrace1.getV()).columns(),(SVDTrace1.getV()).rows());
	        CorrectU1.assign(CompareTraces.transpose(VArray1));
			
			CorrectS1 = new DenseDoubleMatrix2D((SVDTrace1.getU()).columns(),(SVDTrace1.getS()).rows());
	        CorrectS1.assign(CompareTraces.transpose(SArray1));
			
			CorrectV1 = new DenseDoubleMatrix2D((SVDTrace1.getU()).columns(),(SVDTrace1.getU()).rows());
	        CorrectV1.assign(CompareTraces.transpose(UArray1));
		}
		
		if(transpose2){
			
			double[][] UArray2=(SVDTrace2.getU()).toArray();
			double[][] SArray2=(SVDTrace2.getS()).toArray();
			double[][] VArray2=(SVDTrace2.getV()).toArray();
			
			CorrectU2 = new DenseDoubleMatrix2D((SVDTrace2.getV()).columns(),(SVDTrace2.getV()).rows());
	        CorrectU2.assign(CompareTraces.transpose(VArray2));
			
			CorrectS2 = new DenseDoubleMatrix2D((SVDTrace2.getS()).columns(),(SVDTrace2.getS()).rows());
	        CorrectS2.assign(CompareTraces.transpose(SArray2));
			
			CorrectV2 = new DenseDoubleMatrix2D((SVDTrace2.getU()).columns(),(SVDTrace2.getU()).rows());
	        CorrectV2.assign(CompareTraces.transpose(UArray2));// assign the values of the matrix U to the matrix V and vice-versa because Of the transposition of SVD results
	        //inverts the matrices, if normally SVD(A)=U.S.V instead SVD(Atransposed) = V.S.U
		} 
		
		
		//compute similarity
		
		//obtain eigen vecotors and weights for Node1, if transpose = true, use directly SVD matrices, else use the corrected one.
		if(transpose1){
			EigenAndWeightMapTargetNode = CompareTraces.createEigenAndWeightMap(CorrectS1, CorrectV1);
		} else {
			EigenAndWeightMapTargetNode = CompareTraces.createEigenAndWeightMap(SVDTrace1.getS(),SVDTrace1.getV());
		}
		
		
		//obtain eigen vecotors and weights for Node2, if transpose = true, use directly SVD matrices, else use the corrected one.
		if(transpose2){
			EigenAndWeightMapNewNode= CompareTraces.createEigenAndWeightMap(CorrectS2, CorrectV2);
		} else {
			EigenAndWeightMapNewNode= CompareTraces.createEigenAndWeightMap(SVDTrace2.getS(),SVDTrace2.getV());
		}
		
		
		//compute the similarity between the two Traces for Node1 and Node2
		Double TotalWeight1=0.0;
		Double TotalWeight2=0.0;
		Set<Map.Entry<double[], Double>> EigenSet1 = EigenAndWeightMapNewNode.entrySet();
		Set<Map.Entry<double[], Double>> EigenSet2 = EigenAndWeightMapTargetNode.entrySet();
		Iterator<Map.Entry<double[], Double>> ite1 = EigenSet1.iterator();
		Iterator<Map.Entry<double[], Double>> ite2 = EigenSet2.iterator();
		Set<double[]> Values1 = EigenAndWeightMapNewNode.keySet();
		Set<double[]> Values2 = EigenAndWeightMapTargetNode.keySet();
		Iterator<double[]> iteV1 = Values1.iterator();
		Iterator<double[]> iteV2 = Values2.iterator();
		
		int firstCounter= EigenAndWeightMapNewNode.size();
		int secondCounter= EigenAndWeightMapTargetNode.size();
		
		similarity=0.0;
		while(firstCounter!=0){
			Map.Entry<double[], Double> FirstEntry = ite1.next(); // get first entry of the map for node 1
			double firstWeight = (Double) FirstEntry.getValue(); // get value ( weight ) for node 1
			double[] firstArray = iteV1.next(); //get eigen vector
			D1Matrix64F firstMatrix = new DenseMatrix64F(1, firstArray.length); //create first array for inner product
			firstMatrix.setData(firstArray); //set the data of the first eigen vector in the array for inner product
			TotalWeight2=0.0;
			ite2 = EigenSet2.iterator(); //restarts the iterators and the valuesused in the second for
			iteV2 = Values2.iterator();
			int secondCounterCopy = secondCounter;
			while(secondCounterCopy!=0){
				Map.Entry<double[], Double> SecondEntry = ite2.next();// get first entry of the map for node 2
				double secondWeight = (Double) SecondEntry.getValue();// get value ( weight ) for node 2
				double[] secondArray = iteV2.next();//get eigen vector
				D1Matrix64F secondMatrix = new DenseMatrix64F(1, secondArray.length); //create second array for inner product
				secondMatrix.setData(secondArray);//set the data of the second eigen vector in the array for inner product
				double mult_vettori = 0.0;
				for (int i = 0; i<firstArray.length; i++){
						mult_vettori = mult_vettori + firstArray[i]*secondArray[i];
				} 
				similarity = similarity + firstWeight*secondWeight*(Math.abs(VectorVectorMult.innerProd(firstMatrix,secondMatrix))); // compute the partial similarity and sums to the total already computed.
				TotalWeight2 = TotalWeight2 + secondWeight; // updates total weight 2
				secondCounterCopy--; // updates counter
			}
			TotalWeight1 = TotalWeight1+firstWeight;// updates total weight 1
			firstCounter--; // updates counter
		}
		AllSimilaritiesNodes.put(Integer.toString(h2.getAddress())+","+Integer.toString(h1.getAddress()),similarity);
		//if the computed similarity is greater then the one the owner of the message has with the target return true so the message is passed, else false and the message stays on the same node
		
		
	}
	
	/**
	 * Transposes a matrix.
	 * @param ArrayToTanspose matrix to transpose
	 * @return double[][]  the matrix in input transposed
	 */
	private static double[][] transpose(double[][] ArrayToTanspose){
		double[][] ArrayTransposed = new double[ArrayToTanspose[0].length][ArrayToTanspose.length];
		    for (int i = 0; i < ArrayToTanspose.length; i++){
		        for (int j = 0; j < ArrayToTanspose[0].length; j++){
		        	ArrayTransposed[j][i] = ArrayToTanspose[i][j];
		        }
		    }
		return ArrayTransposed;
	}
	
	/**
	 * Takes the eigen-vectors and computing their weight, saving the results in a LinkedMap
	 * @param S DoubleMatrix2D S calculated by SVD 
	 * @param V DoubleMatrix2D V calculated by SVD 
	 * @return LinkedHashMap<double[],Double>
	 */
	private static LinkedHashMap<double[],Double> createEigenAndWeightMap(DoubleMatrix2D S, DoubleMatrix2D V){
		LinkedHashMap<double[],Double> EigenAndWeightMap= new LinkedHashMap<double[], Double>();
		double EigenSum= 0;
		//compute the sum of all values on the diagonal of S
		for (int i = 0; i<V.rows(); i++){
			EigenSum = EigenSum + Math.pow(S.get(i, i),2); //computes the sum of the values in the considered single eigen vector
		}
		double EigenValue = 0;
		double[] Vector = null;
		double weight = 0;
		double weightsum=0;
		// computes the wight then saves the eigen vector and its corresponding weight in a LinkedMap, so they are in a known order
		for (int i = 0; i<V.rows(); i++){
			if(transpose1){ //TODO check this modification ?? sempre view rows ? dovrebbe essere sempre cos“
				Vector = (V.viewRow(i)).toArray();
			} else {
				Vector = (V.viewColumn(i)).toArray();
			}
			EigenValue = S.get(i, i);
			weight = (Math.pow(EigenValue,2))/EigenSum; //computes the weight of the considered eigen vector 
			EigenAndWeightMap.put(Vector,weight); //saves them in the map.
			weightsum=weightsum+weight;
		}
		return EigenAndWeightMap; //return the eigen vectors - value map.
	}
	/**
	 * Returns the last computed similarity
	 * @param m Message which similarity is to be returned
	 * @param h the DTN wich the similarty is refferring to
	 * @return double Similarity
	 */
	public static double getSimilarity(Message m, DTNHost h){
		return AllSimilaritiesMessages.get(m+","+h);
	}
	
	/**
	 * Returns the map of all nodes similarities
	 * @return Map<String,Double>
	 */
	public static Map<String,Double> getSimilarityMapNodes(){
		return AllSimilaritiesNodes;
	}
	
}
