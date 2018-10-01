package core;

import java.util.Collection;
import java.util.HashMap;

/** 
 * @author Daniele Bonaldo, University of Padua
 */
public class DTNFileSystem {
	
	private HashMap<String, DTNFile> files;
	
	/** Host where this file system belongs to */
	private DTNHost host;
	
	
	
	/**
	 * Adds a file to the file system
	 * @param f The file to add
	 * @return true if the file is added correctly
	 */
	public boolean addToFiles(DTNFile f) {
		this.files.put(f.getHash(), f);
		return true;
	}
	
	/**
	 * Returns a file by hash.
	 * @param hash hash of the file
	 * @return The file
	 */
	public DTNFile getFile(String hash) {
		return this.files.get(hash);
	}
	
	/**
	 * Returns a reference to the files of this file system in collection.
	 * @return a reference to the files of this file system in collection.
	 */
	public Collection<DTNFile> getFileCollection() {
		return this.files.values();
	}
	
	/**
	 * Returns the number of files this file system has
	 * @return How many files this file system has
	 */
	public int getNrofFiles() {
		return this.files.size();
	}
	
	/**
	 * Returns the host this file system is in
	 * @return The host object
	 */
	protected DTNHost getHost() {
		return this.host;
	}
	
	/**
	 * Check if this file system has a file searched by hash.
	 * @param hash hash of the file
	 * @return true if this file system has the file
	 */
	public boolean hasFile(String hash) {
		return this.files.containsKey(hash);
	}
	
	/**
	 * Returns a String presentation of this file system
	 * @return A String presentation of this file system
	 */
	public String toString() {
		return getClass().getSimpleName() + " of " + 
			this.getHost().toString() + " with " + getNrofFiles() 
			+ " files";
	}

	public void init(DTNHost dtnHost) {
		this.files = new HashMap<String, DTNFile>();
		this.host = dtnHost;
	}


}
