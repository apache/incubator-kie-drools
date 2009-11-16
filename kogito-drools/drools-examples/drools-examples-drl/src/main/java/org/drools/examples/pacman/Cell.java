package org.drools.examples.pacman;

public class Cell {
	private int row;
	private int col;
	
	public Cell(int row, int col) {
		super();
		this.row = row;
		this.col = col;
	}
	
	public int getRow() {
		return row;
	}
	
	public int getCol() {
		return col;
	}	
	
	@Override
    public String toString() {
	    return "Cell " + row + ":" + col;
	}
}
