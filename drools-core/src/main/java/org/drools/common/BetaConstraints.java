package org.drools.common;

import java.io.Serializable;

import org.drools.RuleBaseConfiguration;
import org.drools.reteoo.BetaMemory;
import org.drools.reteoo.ReteTuple;
import org.drools.rule.ContextEntry;
import org.drools.util.LinkedList;

public interface BetaConstraints
    extends
    Serializable {

    public ContextEntry[] createContext();

    public void updateFromTuple(ContextEntry[] context,
                                InternalWorkingMemory workingMemory,
                                ReteTuple tuple);

    public void updateFromFactHandle(ContextEntry[] context,
                                     InternalWorkingMemory workingMemory,
                                     InternalFactHandle handle);

    public boolean isAllowedCachedLeft(ContextEntry[] context,
                                       InternalFactHandle handle);

    public boolean isAllowedCachedRight(ContextEntry[] context,
                                        ReteTuple tuple);

    public LinkedList getConstraints();

    public boolean isIndexed();

    public int getIndexCount();

    public boolean isEmpty();

    public BetaMemory createBetaMemory(final RuleBaseConfiguration config);

    public void resetTuple(final ContextEntry[] context);

    public void resetFactHandle(final ContextEntry[] context);

}