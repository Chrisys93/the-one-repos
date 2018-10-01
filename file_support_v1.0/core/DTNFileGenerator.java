package core;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Vector;
import java.io.File;
import java.util.regex.Pattern;

/** 
 * @author Daniele Bonaldo, University of Padua
 */
public class DTNFileGenerator {
	
	/** namespace for files generation settings ({@value})*/
	private static final String FILES_NS = "FilesGenerator";
	/** file containing files definition ({@value})*/
	public static final String FILES_FILE_S = "filepath";
	/** file generator's rng seed -setting id ({@value})*/
	public static final String RNG_SEED = "rngSeed";

	private SimScenario simScenario;
	private List<DTNFileCreationRequest> requests;
	private List<DTNFile> generatedFiles;
	private Scanner scanner;
	
	private Random rng; 

	public DTNFileGenerator(SimScenario simScenario) {
		this.simScenario = simScenario;
		this.requests = new ArrayList<DTNFileCreationRequest>();
		this.generatedFiles = new ArrayList<DTNFile>();
		reset();
	}
	

	public void readFiles() {
		Settings s = new Settings(FILES_NS);		
		String filePath = s.getSetting(FILES_FILE_S);
		
		try {
			this.scanner = new Scanner(new File(filePath));
		} catch (FileNotFoundException e) {
			throw new SimError(e.getMessage(),e);
		}
		
		int filesRequestRead = 0;
		// skip empty and comment lines
		Pattern skipPattern = Pattern.compile("(#.*)|(^\\s*$)");
		
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			Scanner lineScan = new Scanner(line);
			if (skipPattern.matcher(line).matches()) {
				// skip empty and comment lines
				continue;
			}
			String fileName;
			int fileSize;
			int nrOfCopies;
			String mode;
			
			try {
				DTNFileCreationRequest request = null;
				mode = lineScan.next();
				fileName = lineScan.next();
				fileSize = parseInt(lineScan.next());
				if(mode.equals("H")){
					request = new DTNFileCreationRequest(fileName, fileSize, 0, mode);
					while(lineScan.hasNext()){
						request.addHost(lineScan.nextInt());
					}						
				}
				else{
					nrOfCopies = lineScan.nextInt();
					request = new DTNFileCreationRequest(fileName, fileSize, nrOfCopies, mode);
					if(mode.equals("G")){
						while(lineScan.hasNext()){
							request.addGroup(lineScan.nextInt());
						}						
					}
				}
					
				requests.add(request);
				// discard the newline in the end
				if (lineScan.hasNextLine()) {
					lineScan.nextLine(); 
				}
				filesRequestRead++;				
			} catch (Exception e) {
				e.printStackTrace();
				throw new SimError("Can't parse file creation request " + 
						(filesRequestRead+1) + " from '" + line + "'", e);				
			}	
						
		}
		this.scanner.close();
		
	}


	public void generateFiles() {
		List<DTNHost> hosts = simScenario.hosts;
		
		int nrOfHostsWithFile=0;
		for(DTNHost host:hosts){
			if(host.hasFileCapability()){
				nrOfHostsWithFile++;
			}
		}
		for(int i=0; i<requests.size(); i++){
			DTNFileCreationRequest request = requests.get(i);
			String fileName = request.fileName;
			int fileSize = request.fileSize;
			int nrOfCopies = request.nrOfCopies;
			if(nrOfCopies > nrOfHostsWithFile && !request.mode.equals("P")){
				nrOfCopies = nrOfHostsWithFile;
			}
			
			String mode = request.mode;
			DTNFile tempFile = new DTNFile(fileName,fileSize);	
			if(!generatedFiles.contains(tempFile)){
				generatedFiles.add(tempFile);
			}
			
			/* Uniform distribution to All nodes with file capability */
			if(mode.equals("A")){
				while(nrOfCopies > 0){					
					int hostID = rng.nextInt(hosts.size());
					DTNHost tempHost = hosts.get(hostID);
					if(tempHost.hasFileCapability() && 
							!tempHost.getFileSystem().hasFile(tempFile.getHash())){
						tempHost.getFileSystem().addToFiles(tempFile);
						nrOfCopies--;
					}				
				}
				
			}
			
			/* Uniform distribution only to specified Groups */
			if(mode.equals("G")){
				while(nrOfCopies > 0){
					int hostID = rng.nextInt(hosts.size());
					DTNHost tempHost = hosts.get(hostID);
					
					if(tempHost.hasFileCapability() && 
							isValidGroup(tempHost, request) &&
							!tempHost.getFileSystem().hasFile(tempFile.getHash())){
						tempHost.getFileSystem().addToFiles(tempFile);
						nrOfCopies--;
					}				
				}
			}
			
			/* Distribution only to specified Hosts */
			if(mode.equals("H")){
				for(Integer addr: request.hostsAddress){
					DTNHost tempHost = hosts.get(addr);
					if(!tempHost.hasFileCapability()){
						throw new SimError("DTNHost " + tempHost + " has not fileCapability, requested by" +
								" file generation request");
					}
					if(!tempHost.getFileSystem().hasFile(tempFile.getHash())){
						tempHost.getFileSystem().addToFiles(tempFile);
					}
				}
			}
			
			/* Distribution with a specified Percent of hosts */
			if(mode.equals("P")){
				/* in this case nrOfCopies is the percentage of hosts which have the file */
				if(nrOfCopies > 100){
					nrOfCopies = 100;
				}
				nrOfCopies = nrOfHostsWithFile / 100 * nrOfCopies;
				System.err.println(nrOfCopies);
				while(nrOfCopies > 0){					
					int hostID = rng.nextInt(hosts.size());
					DTNHost tempHost = hosts.get(hostID);
					if(tempHost.hasFileCapability() && 
							!tempHost.getFileSystem().hasFile(tempFile.getHash())){
						tempHost.getFileSystem().addToFiles(tempFile);
						nrOfCopies--;
					}				
				}
			}

		}			

	}
	
	
	private boolean isValidGroup(DTNHost tempHost, DTNFileCreationRequest request) {
		int groupID = tempHost.getGroupId(simScenario.groupSizes);
		if(request.groups[groupID-1]){
			return true;
		}
		return false;
	}


	/**
	 * Resets all static fields to default values
	 */
	private void reset() {
		Settings s = new Settings(FILES_NS);
		if (s.contains(RNG_SEED)) {
			int seed = s.getInt(RNG_SEED);
			rng = new Random(seed);
		}
		else {
			rng = new Random(0);
		}
	}
	
	public DTNFile getFile(String fileHash){
		for(DTNFile file:generatedFiles){
			if(file.getHash().equals(fileHash)){
				return file;
			}
		}
		return null;
	}
	
	/**
	 * Parses a int value from a String valued setting. Supports
	 * kilo (k), mega (M) and giga (G) suffixes.
	 * @param value String value to parse
	 * @param setting The setting where this value was from (for error msgs)
	 * @return The value as a double
	 * @throws SettingsError if the value wasn't a numeric value 
	 * (or the suffix wasn't recognized)
	 */
	private int parseInt(String value) {
		int number;
		int multiplier = 1;
		
		if (value.endsWith("k")) {
			multiplier = 1000;
		}
		else if (value.endsWith("M")) {
			multiplier = 1000000;
		}
		else if (value.endsWith("G")) {
			multiplier = 1000000000;
		}
		
		if (multiplier > 1) { // take the suffix away before parsing
			value = value.substring(0,value.length()-1);
		}
		
		try {
			number = (int) (Double.parseDouble(value) * multiplier);
		} catch (NumberFormatException e) {
			throw new SettingsError("Invalid numeric setting '" + value + 
					"' for fileSize\n" + e.getMessage());
		}
		return number;
	}
	
	private class DTNFileCreationRequest implements Comparable<DTNFileCreationRequest>{
		String fileName;
		int fileSize;
		int nrOfCopies;
		String mode;
		boolean[] groups;
		Vector<Integer> hostsAddress;
		
		public DTNFileCreationRequest(String fileName, int fileSize,
				int nrOfCopies, String mode) {
			super();
			this.fileName = fileName;
			this.fileSize = fileSize;
			this.nrOfCopies = nrOfCopies;
			this.mode = mode;
			hostsAddress = new Vector<Integer>();
			groups = new boolean[simScenario.nrofGroups];
			for(int i=0; i<simScenario.nrofGroups; i++){
				groups[i] = false;
			}
		}
		
		public void addHost(int addr){
			hostsAddress.add(addr);
		}
		
		public void addGroup(int i){
			groups[i-1] = true;
		}

		@Override
		public int compareTo(DTNFileCreationRequest o) {
			return this.fileName.compareTo(o.fileName);
		}
		
	}

}
