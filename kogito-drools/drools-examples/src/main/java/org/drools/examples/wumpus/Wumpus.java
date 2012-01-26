package org.drools.examples.wumpus;

public class Wumpus {

    private int row;
    private int col;   
    private boolean alive;

    public Wumpus(int row,
                  int col) {
        this.row = row;
        this.col = col;
        this.alive = true;
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

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    @Override
    public String toString() {
        return "Wumpus [row=" + row + ", col=" + col + ", alive=" + alive + "]";
    }


}
