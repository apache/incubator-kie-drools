package org.drools.reteoo;

import org.drools.common.InternalWorkingMemory;
import org.drools.spi.PropagationContext;
import org.drools.util.LinkedListNode;

public interface TupleMatch extends LinkedListNode {

    /**
     * Return the parent <code>ReteTuple</code>
     * 
     * @return the <code>ReteTuple</code>
     */
    public abstract ReteTuple getTuple();

    /**
     * Returns the referenced <code>ObjectMatches</code> which provides the 
     * <code>FactHandleImpl</code> the <code>ReteTuple</code> is joined with.
     * 
     * @return the <code>ObjectMatches</code>
     */
    public abstract ObjectMatches getObjectMatches();

    /**
     * Adds a resulting join to the <code>List</code>. A join is made for each <code>TupleSink</code>.
     * 
     * @param tuple
     */
    public abstract void addJoinedTuple(final ReteTuple tuple);

    public abstract void propagateRetractTuple(final PropagationContext context,
                                               final InternalWorkingMemory workingMemory);

    public abstract void propagateModifyTuple(final PropagationContext context,
                                              final InternalWorkingMemory workingMemory);

    public abstract ReteTuple getTupleForSink(TupleSink sink);

}