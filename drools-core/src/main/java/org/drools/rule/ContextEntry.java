package org.drools.rule;

import java.io.Serializable;

import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.reteoo.ReteTuple;

public interface ContextEntry
    extends
    Serializable {

    public ContextEntry getNext();

    public void setNext(ContextEntry entry);

    public void updateFromTuple(InternalWorkingMemory workingMemory,
                                ReteTuple tuple);

    public void updateFromFactHandle(InternalWorkingMemory workingMemory,
                                     InternalFactHandle handle);
    
    public void resetTuple();
    
    public void resetFactHandle();
    
}
