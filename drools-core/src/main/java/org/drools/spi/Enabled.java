package org.drools.spi;

import java.io.Serializable;

import org.drools.WorkingMemory;

public interface Enabled extends Serializable {    
    public boolean getValue(final Tuple tuple,
                            final WorkingMemory workingMemory);
}
