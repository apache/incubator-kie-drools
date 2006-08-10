package org.drools.spi;

import java.io.Serializable;

import org.drools.WorkingMemory;
import org.drools.rule.Declaration;

public interface Restriction extends Serializable {
    Declaration[] getRequiredDeclarations();
    
    public boolean isAllowed(Object object,
                             Tuple tuple,
                             WorkingMemory workingMemory);
}
