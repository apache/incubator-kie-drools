package org.drools.spi;

import java.io.Serializable;

import org.drools.WorkingMemory;
import org.drools.common.InternalWorkingMemory;
import org.drools.reteoo.ReteTuple;
import org.drools.rule.ContextEntry;
import org.drools.rule.Declaration;
import org.drools.rule.VariableConstraint.VariableContextEntry;

public interface Restriction extends Serializable {
    Declaration[] getRequiredDeclarations();
    
    public boolean isAllowed(Extractor extractor, Object object, InternalWorkingMemory workingMemoiry);
    
    public boolean isAllowedCachedLeft(ContextEntry context, Object object);
    
    public boolean isAllowedCachedRight(ReteTuple tuple, ContextEntry context);
}
