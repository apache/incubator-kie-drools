package org.drools.spi;

import org.drools.FactHandle;
import org.drools.rule.Declaration;

public interface Constraint
{        
    public Declaration[] getRequiredDeclarations();    
        
    public boolean isAllowed(Object object, 
                             FactHandle handle,
                             Tuple tuple);        
}
