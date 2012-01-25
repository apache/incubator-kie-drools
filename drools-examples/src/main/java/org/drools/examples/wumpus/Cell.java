package org.drools.examples.wumpus;

public class Cell {
    private int row;
    private int col;
    
    private boolean hidden;

    public Cell(int row,
                int col) {
        this.row = row;
        this.col = col;
        this.hidden = true;
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

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    @Override
    public String toString() {
        return "Cell [row=" + row + ", col=" + col + "]";
    }
        

}
