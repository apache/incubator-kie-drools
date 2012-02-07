package org.drools.examples.wumpus;

public class Thing {
    private int row;
    private int col;
    

    public Thing(int row,
                int col) {
        this.row = row;
        this.col = col;
    }
    
    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }    
}
