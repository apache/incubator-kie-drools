package org.drools.common;

import org.drools.reteoo.BetaMemory;
import org.drools.reteoo.ObjectHashTable;
import org.drools.reteoo.ReteTuple;
import org.drools.util.LinkedList;
import org.drools.util.TupleHashTable;

public interface BetaConstraints {

    public void updateFromTuple(ReteTuple tuple);

    public void updateFromFactHandle(InternalFactHandle handle);

    public boolean isAllowedCachedLeft(Object object);

    public boolean isAllowedCachedRight(ReteTuple tuple);

    public LinkedList getConstraints();    
    
    public boolean isIndexed();
    
    public boolean isEmpty();
    
    public BetaMemory createBetaMemory();
}