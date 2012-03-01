package org.drools.games.wumpus;

import org.drools.definition.type.PropertyReactive;

@PropertyReactive
public class Wumpus extends Thing { 
    private boolean alive;

    public Wumpus(int row,
                  int col) {
        super( row, col );
        this.alive = true;
    } 

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    @Override
    public String toString() {
        return "Wumpus [row=" + getRow() + ", col=" + getCol() + ", alive=" + alive + "]";
    }


}
