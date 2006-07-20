package org.drools.spi;

import java.io.Serializable;

import org.drools.WorkingMemory;
import org.drools.common.InternalFactHandle;
import org.drools.rule.Declaration;

public interface Restriction extends Serializable {
    Declaration[] getRequiredDeclarations();
    
    public boolean isAllowed(Object object,
                             InternalFactHandle handle,
                             Tuple tuple,
                             WorkingMemory workingMemory);
}
