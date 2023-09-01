package org.drools.testcoverage.common.model;

import java.io.Serializable;

public class Cell implements Serializable {

    public static final int LIVE = 1;
    private static final int DEAD = 2;

    private int value = 0;
    private int row;
    private int col;

    private int state;

    public Cell() {

    }

    public Cell(final int value) {
        this.value = value;
    }

    public Cell(final int state,
                final int row,
                final int col) {
        super();
        this.state = state;
        this.row = row;
        this.col = col;
    }

    public int getValue() {
        return this.value;
    }

    public void setValue(final int value) {
        this.value = value;
    }

    public int getX() {
        return row;
    }

    public void setX(final int x) {
        this.row = x;
    }

    public int getY() {
        return col;
    }

    public void setY(final int y) {
        this.col = y;
    }

    public int getState() {
        return state;
    }

    public void setState(final int state) {
        this.state = state;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    @Override
    public String toString() {
        return "Cell( [" + row + "," + col + "] " + ((state == DEAD) ? "DEAD" : "LIVE") + " = " + value + " )";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + row;
        result = prime * result + col;
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Cell other = (Cell) obj;
        if (row != other.row) {
            return false;
        }
        if (col != other.col) {
            return false;
        }
        return true;
    }
}
