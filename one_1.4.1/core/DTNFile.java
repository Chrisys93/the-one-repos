package core;

import java.util.ArrayList;
import java.util.List;

/** 
 * @author Daniele Bonaldo, University of Padua
 */
public class DTNFile implements Comparable<DTNFile> {
	
	/** Name of the file */
	private String filename;
	/** Hash of the file */
	private String hash;
	/** Size of the file (bytes) */
	private int length;
			
	public DTNFile(String filename, int size) {
		this(filename, hashFromFilename(filename), size);
	}	
	

	public DTNFile(String filename, String hash, int size) {
		this.filename = filename;
		this.hash = hash;
		this.length = size;
	}
	
	
	public static String hashFromFilename(String filenameStr){
		return ((Integer)filenameStr.hashCode()).toString();
	}



	/**
	 * Returns the size of the file (in bytes)
	 * @return the size of the file
	 */
	public int getSize() {
		return this.length;
	}
	
	/**
	 * Returns the filename of the file
	 * @return the filename of the file
	 */
	public String getFilename() {
		return this.filename;
	}
	
	/**
	 * Returns the hash of the file
	 * @return the hash of the file
	 */
	public String getHash() {
		return this.hash;
	}
	
	/**
	 * Returns a string representation of the file
	 * @return a string representation of the file
	 */
	public String toString () {
		return filename;
	}

	@Override
	public int compareTo(DTNFile f) {
		return getHash().compareTo(f.getHash());
	}


	
	

}
