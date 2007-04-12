package org.drools.common;

import java.io.Serializable;

import org.drools.reteoo.BetaMemory;
import org.drools.reteoo.ReteTuple;
import org.drools.util.LinkedList;

public interface BetaConstraints
    extends
    Serializable {

    public void updateFromTuple(InternalWorkingMemory workingMemory,
                                ReteTuple tuple);

    public void updateFromFactHandle(InternalWorkingMemory workingMemory,
                                     InternalFactHandle handle);

    public boolean isAllowedCachedLeft(Object object);

    public boolean isAllowedCachedRight(ReteTuple tuple);

    public LinkedList getConstraints();

    public boolean isIndexed();

    public boolean isEmpty();

    public BetaMemory createBetaMemory();
}