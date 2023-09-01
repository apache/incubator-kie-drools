package org.drools.games.wumpus;

public class Arrow {

    private int row;
    private int col;  
    private Direction direction;

    public Arrow(int row,
                 int col,
                 Direction direction) {
        this.row = row;
        this.col = col;
        this.direction = direction;
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

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    @Override
    public String toString() {
        return "Arrow [row=" + row + ", col=" + col + ", direction=" + direction + "]";
    } 
}
