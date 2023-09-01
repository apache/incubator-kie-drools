package org.drools.mvel.compiler;

import java.io.Serializable;


public class Cell implements Serializable {
    public static final int LIVE = 1;
    public static final int DEAD = 2;

    int value = 0;
    int row;
    int col;

    int state;

    public Cell() {

    }

    public Cell(final int value) {
        this.value = value;
    }
    
    public Cell(int state,
                int row,
                int col) {
        super();
        this.state = state;
        this.row = row;
        this.col = col;
//        if( row == 1 && col == 1 ) {
//            value = 8;
//        } else if( row+col == 2 ) {
//            value = 3;
//        } else {
//            value = 5;
//        }
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

    public void setX(int x) {
        this.row = x;
    }

    public int getY() {
        return col;
    }

    public void setY(int y) {
        this.col = y;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
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
        return "Cell( ["+row+","+col+"] "+( (state==DEAD)?"DEAD":"LIVE") +" = "+value+" )";
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
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        Cell other = (Cell) obj;
        if ( row != other.row ) return false;
        if ( col != other.col ) return false;
        return true;
    }
    
    
}
