package org.drools.base;

import org.drools.WorkingMemory;
import org.drools.spi.Salience;
import org.drools.spi.Tuple;

public class SalienceInteger
    implements
    Salience {
    
    /**
     * 
     */
    private static final long serialVersionUID = 400L;

    public static final Salience DEFAULT_SALIENCE = new SalienceInteger( 0 );
    
    private final int value;

    public SalienceInteger(int value) {
        this.value = value;
    }

    public int getValue(final Tuple tuple,
                        final WorkingMemory workingMemory) {
        return this.value;
    }
    
    public String toString() {
        return String.valueOf( this.value );
    }

}
