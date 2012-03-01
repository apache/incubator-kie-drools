package org.drools.games.wumpus;

import org.drools.definition.type.PropertyReactive;

@PropertyReactive
public class Pit extends Thing {

    public Pit(int row,
                int col) {
        super(row, col);
    }

    @Override
    public String toString() {
        return "Pitt [row=" + getRow() + ", col=" + getCol() + "]";
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + getCol();
        result = prime * result + getRow();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        Pit other = (Pit) obj;
        if ( getCol() != other.getCol() ) return false;
        if ( getRow() != other.getRow() ) return false;
        return true;
    }

}
