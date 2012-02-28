package org.drools.games.wumpus;


public class Gold extends Thing {

    public Gold(int row,
                int col) {
        super( row, col );
    }

    @Override
    public String toString() {
        return "Gold [row=" + getRow() + ", col=" + getCol() + "]";
    }
    
    
}
