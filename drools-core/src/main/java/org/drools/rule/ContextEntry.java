package org.drools.rule;

import java.io.Externalizable;

import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.reteoo.LeftTuple;

public interface ContextEntry
    extends
    Externalizable {

    public ContextEntry getNext();

    public void setNext(ContextEntry entry);

    public void updateFromTuple(InternalWorkingMemory workingMemory,
                                LeftTuple tuple);

    public void updateFromFactHandle(InternalWorkingMemory workingMemory,
                                     InternalFactHandle handle);

    public void resetTuple();

    public void resetFactHandle();

}
