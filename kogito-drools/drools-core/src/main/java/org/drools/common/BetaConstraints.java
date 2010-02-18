package org.drools.common;

import java.io.Externalizable;

import org.drools.RuleBaseConfiguration;
import org.drools.core.util.LinkedList;
import org.drools.reteoo.BetaMemory;
import org.drools.reteoo.LeftTuple;
import org.drools.rule.ContextEntry;

public interface BetaConstraints
    extends
    Externalizable {

    public ContextEntry[] createContext();

    public void updateFromTuple(ContextEntry[] context,
                                InternalWorkingMemory workingMemory,
                                LeftTuple tuple);

    public void updateFromFactHandle(ContextEntry[] context,
                                     InternalWorkingMemory workingMemory,
                                     InternalFactHandle handle);

    public boolean isAllowedCachedLeft(ContextEntry[] context,
                                       InternalFactHandle handle);

    public boolean isAllowedCachedRight(ContextEntry[] context,
                                        LeftTuple tuple);

    public LinkedList getConstraints();

    public boolean isIndexed();

    public int getIndexCount();

    public boolean isEmpty();

    public BetaMemory createBetaMemory(final RuleBaseConfiguration config);

    public void resetTuple(final ContextEntry[] context);

    public void resetFactHandle(final ContextEntry[] context);

}