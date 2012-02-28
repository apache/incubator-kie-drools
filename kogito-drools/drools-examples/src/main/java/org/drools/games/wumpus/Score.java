package org.drools.games.wumpus;

import org.drools.definition.type.PropertySpecific;

@PropertySpecific
public class Score {
    private int value;

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
    
    
}
