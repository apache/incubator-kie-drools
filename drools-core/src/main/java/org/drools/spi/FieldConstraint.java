package org.drools.spi;

import org.drools.FactHandle;
import org.drools.WorkingMemory;
import org.drools.rule.Declaration;

public interface FieldConstraint extends Constraint {
    public boolean isAllowed(FactHandle handle,
                             Tuple tuple,
                             WorkingMemory workingMemory);
    
    Declaration[] getRequiredDeclarations();
}
