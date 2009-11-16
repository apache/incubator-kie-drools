package org.drools.examples.pacman;

public class Location {
	private Character character;
	private int row;
	private int col;
	
    public Location(Character character,
                    int row,
                    int col) {
        this.character = character;
        this.row = row;
        this.col = col;
    }

    public Character getCharacter() {
        return character;
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

    @Override
    public String toString() {
	    return "Location " + character + " " + row + ":" + col;
	}
}
