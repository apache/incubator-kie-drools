package org.drools.spi;

import java.io.Serializable;

import org.drools.WorkingMemory;

public interface Salience extends Serializable {    
    public int getValue(final Tuple tuple,
                        final WorkingMemory workingMemory);
}
