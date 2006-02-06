package org.drools.spi;

import org.drools.FactHandle;
import org.drools.WorkingMemory;
import org.drools.rule.Declaration;

public class MockConstraint
    implements
    FieldConstraint {
    
    public Declaration[] declarations;
    
    public boolean isAllowed = true;

    public boolean isAllowed(FactHandle handle,
                             Tuple tuple,
                             WorkingMemory workingMemory) {
        return this.isAllowed;
    }

    public Declaration[] getRequiredDeclarations() {
        return declarations;
    }

}
