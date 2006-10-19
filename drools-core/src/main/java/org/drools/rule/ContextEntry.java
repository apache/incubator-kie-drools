package org.drools.rule;

import org.drools.common.InternalFactHandle;
import org.drools.reteoo.ReteTuple;

public interface ContextEntry {

    public ContextEntry getNext();

    public void setNext(ContextEntry entry);

    public void updateFromTuple(ReteTuple tuple);

    public void updateFromFactHandle(InternalFactHandle handle);

}
