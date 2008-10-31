package org.drools.spi;

import java.io.Serializable;

import org.drools.WorkingMemory;
import org.drools.rule.Rule;

public interface Enabled extends Serializable {    
    public boolean getValue(final Tuple tuple,
                            final Rule rule,
                            final WorkingMemory workingMemory);
}
