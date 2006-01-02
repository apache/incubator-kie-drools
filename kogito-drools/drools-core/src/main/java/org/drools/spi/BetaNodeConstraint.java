package org.drools.spi;

import org.drools.FactHandle;
import org.drools.rule.Declaration;

public interface BetaNodeConstraint extends Constraint {
    public boolean isAllowed(Object object,
                             FactHandle handle,
                             Tuple tuple);
    
    Declaration[] getRequiredDeclarations();
    
}
