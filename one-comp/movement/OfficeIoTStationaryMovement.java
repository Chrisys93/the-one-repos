/* 
 * Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details. 
 */
package movement;

import input.WKTReader;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import movement.map.DijkstraPathFinder;
import movement.map.MapNode;
import movement.map.SimMap;
import core.Coord;
import core.ParetoRNG;
import core.Settings;
import core.SimClock;

/**
 * This class models movement at an office. If the node happens to be at some 
 * other location than the office, it first walks the shortest path to the 
 * office and then stays there until the end of the work day. A node has only 
 * works at one office.
 * 
 * @author Frans Ekman
 *
 */
public class OfficeIoTStationaryMovement extends MapBasedMovement{
	
	public static final String NR_OF_OFFICES_SETTING = "nrOfOffices";
	
	public static final String OFFICE_SIZE_SETTING = "officeSize";
	public static final String OFFICE_LOCATIONS_FILE_SETTING = 
		"officeLocationsFile";
	
	private static int nrOfOffices = 50;
	
	private ParetoRNG paretoRNG;
	
	private int distance;
	
	private List<Coord> allOffices;
	
	private Coord officeLocation;
	private Coord deskLocation;
	
	/**
	 * OfficeActivityMovement constructor
	 * @param settings
	 */
	public OfficeIoTStationaryMovement(Settings settings) {
		super(settings);

		//workDayLength = settings.getInt(WORK_DAY_LENGTH_SETTING);
		nrOfOffices = settings.getInt(NR_OF_OFFICES_SETTING);
		
		distance = settings.getInt(OFFICE_SIZE_SETTING);
		
		
		String officeLocationsFile = null;
		try {
			officeLocationsFile = settings.getSetting(
					OFFICE_LOCATIONS_FILE_SETTING);
		} catch (Throwable t) {
			// Do nothing;
		}
		
		if (officeLocationsFile == null) {
			MapNode[] mapNodes = (MapNode[])getMap().getNodes().
				toArray(new MapNode[0]);
			int officeIndex = rng.nextInt(mapNodes.length - 1) /
				(mapNodes.length/nrOfOffices);
			officeLocation = mapNodes[officeIndex].getLocation().clone();
		} else {
			try {
				allOffices = new LinkedList<Coord>();
				List<Coord> locationsRead = (new WKTReader()).
					readPoints(new File(officeLocationsFile));
				for (Coord coord : locationsRead) {
					SimMap map = getMap();
					Coord offset = map.getOffset();
					// mirror points if map data is mirrored
					if (map.isMirrored()) { 
						coord.setLocation(coord.getX(), -coord.getY()); 
					}
					coord.translate(offset.getX(), offset.getY());
					allOffices.add(coord);
				}
				officeLocation = allOffices.get(
						rng.nextInt(allOffices.size())).clone();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		this.deskLocation = getRandomCoorinateInsideOffice();
	}
	
	/**
	 * Copyconstructor
	 * @param proto
	 */
	public OfficeIoTStationaryMovement(OfficeIoTStationaryMovement proto) {
		super(proto);
		this.distance = proto.distance;
		
		if (proto.allOffices == null) {
			MapNode[] mapNodes = (MapNode[])getMap().getNodes().
				toArray(new MapNode[0]);
			int officeIndex = rng.nextInt(mapNodes.length - 1) / 
				(mapNodes.length/nrOfOffices);
			officeLocation = mapNodes[officeIndex].getLocation().clone();
		} else {
			this.allOffices = proto.allOffices;
			officeLocation = allOffices.get(
					rng.nextInt(allOffices.size())).clone();
		}
		
		this.deskLocation = getRandomCoorinateInsideOffice();
		this.paretoRNG = proto.paretoRNG;
	}
	
	public Coord getRandomCoorinateInsideOffice() {
		double x_coord = officeLocation.getX() + 
			(0.5 - rng.nextDouble()) * distance;
		if (x_coord > getMaxX()) {
			x_coord = getMaxX();
		} else if (x_coord < 0) {
			x_coord = 0;
		}
		double y_coord = officeLocation.getY() + 
			(0.5 - rng.nextDouble()) * distance;
		if (y_coord > getMaxY()) {
			y_coord = getMaxY();
		} else if (y_coord < 0) {
			y_coord = 0;
		}
		return new Coord(x_coord, y_coord);
	}
	
	@Override
	public Coord getInitialLocation() {
		
		this.deskLocation = getRandomCoorinateInsideOffice();

		return this.deskLocation;
	}

	@Override
	public Path getPath() {
		Path p = new Path(0);
		p.addWaypoint(deskLocation);
		return p;
	}
	
	@Override
	public double nextPathAvailable() {
		return Double.MAX_VALUE;	// no new paths available
	}
	
	@Override
	public MapBasedMovement replicate() {
		return new OfficeIoTStationaryMovement(this);
	}
	
	/**
	 * @return The location of the office
	 */
	public Coord getOfficeLocation() {
		return officeLocation.clone();
	}

}
