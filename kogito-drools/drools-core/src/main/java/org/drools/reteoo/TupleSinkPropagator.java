package org.drools.reteoo;

import java.util.List;
import java.util.Map;

import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.spi.PropagationContext;
import org.drools.util.LinkedList;

public interface TupleSinkPropagator {
    public void propagateAssertTuple(ReteTuple tuple,
                                     InternalFactHandle handle,
                                     TupleMatch tupleMatch,
                                     PropagationContext context,
                                     InternalWorkingMemory workingMemory);

    public void propagateAssertTuple(ReteTuple tuple,
                                     PropagationContext context,
                                     InternalWorkingMemory workingMemory);

    /**
     * Propagates a new tuple adding the given fact handle to the tuple 
     * before propagating.
     * 
     * @param tuple The base tuple for propagation
     * @param handle The handle to add to the tuple when propagating
     * @param context
     * @param workingMemory
     */
    public void propagateAssertTuple(final ReteTuple tuple,
                                     final InternalFactHandle handle,
                                     final PropagationContext context,
                                     final InternalWorkingMemory workingMemory);

    public LinkedList createAndAssertTuple(InternalFactHandle handle,
                                           PropagationContext context,
                                           InternalWorkingMemory workingMemory);

    public TupleSink[] getSinks();

    public void propagateNewTupleSink(TupleMatch tupleMatch,
                                      PropagationContext context,
                                      InternalWorkingMemory workingMemory);

    public void propagateNewTupleSink(InternalFactHandle handle,
                                      LinkedList list,
                                      PropagationContext context,
                                      InternalWorkingMemory workingMemory);

    public void propagateNewTupleSink(ReteTuple tuple,
                                      PropagationContext context,
                                      InternalWorkingMemory workingMemory);

    public List getPropagatedTuples(final Map memory,
                                    final InternalWorkingMemory workingMemory,
                                    final TupleSink sink);

    public int size();

}
