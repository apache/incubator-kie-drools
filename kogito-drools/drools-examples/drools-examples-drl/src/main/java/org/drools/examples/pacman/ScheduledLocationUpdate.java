package org.drools.examples.pacman;

public class ScheduledLocationUpdate {
	private Location location;
	private int row;
	private int col;
	private int tock;
	
    public ScheduledLocationUpdate(Location location,
                    int row,
                    int col,
                    int tock) {
        this.location = location;
        this.row = row;
        this.col = col;
        this.tock = tock;
    }

    public Location getLocation() {
        return location;
    }

    public void setRow(int row) {
        this.row = row;
    }
    
    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }	

    public void setCol(int col) {
        this.col = col;
    }

    public int getTock() {
        return tock;
    }

    public void setTock(int tock) {
        this.tock = tock;
    }

    @Override
    public String toString() {
	    return "ScheduledLocationUpdate " + location.getCharacter() + " " + row + ":" + col;
	}
}
