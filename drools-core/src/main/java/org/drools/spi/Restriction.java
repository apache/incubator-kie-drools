package org.drools.spi;

import java.io.Serializable;

import org.drools.WorkingMemory;
import org.drools.common.InternalWorkingMemory;
import org.drools.rule.ContextEntry;
import org.drools.rule.Declaration;

public interface Restriction extends Serializable {
    Declaration[] getRequiredDeclarations();
    
    public boolean isAllowed(Object object, InternalWorkingMemory workingMemoiry);
    
    public boolean isAllowed(ContextEntry context);
}
