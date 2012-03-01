package org.drools.games.wumpus;

import org.drools.definition.type.PropertyReactive;

@PropertyReactive
public class Score {
    private int value;

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
    
    
}
