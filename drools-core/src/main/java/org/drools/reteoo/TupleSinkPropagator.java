package org.drools.reteoo;

import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.spi.PropagationContext;
import org.drools.util.LinkedList;

public interface TupleSinkPropagator {
    public void propagateAssertTuple(ReteTuple tuple,
                                     InternalFactHandle handle,
                                     PropagationContext context,
                                     InternalWorkingMemory workingMemory);

    public void propagateAssertTuple(ReteTuple tuple,
                                     PropagationContext context,
                                     InternalWorkingMemory workingMemory);

    public LinkedList createAndPropagateAssertTupleWithMemory(InternalFactHandle handle,
                                                              PropagationContext context,
                                                              InternalWorkingMemory workingMemory);

    public void createAndPropagateAssertTuple(InternalFactHandle handle,
                                              PropagationContext context,
                                              InternalWorkingMemory workingMemory);

    public void createAndPropagateRetractTuple(ReteTuple tuple,
                                               PropagationContext context,
                                               InternalWorkingMemory workingMemory);

    public void createAndPropagateRetractTuple(LinkedList list,
                                               PropagationContext context,
                                               InternalWorkingMemory workingMemory);

    public void createAndPropagateModifyTuple(ReteTuple tuple,
                                              PropagationContext context,
                                              InternalWorkingMemory workingMemory);

    public void createAndPropagateModifyTuple(LinkedList list,
                                              PropagationContext context,
                                              InternalWorkingMemory workingMemory);

    public TupleSink[] getSinks();

    //    public void propagateNewTupleSink(TupleMatch tupleMatch,
    //                                      PropagationContext context,
    //                                      InternalWorkingMemory workingMemory);
    //
    //    public void propagateNewTupleSink(InternalFactHandle handle,
    //                                      LinkedList list,
    //                                      PropagationContext context,
    //                                      InternalWorkingMemory workingMemory);
    //
    //    public void propagateNewTupleSink(ReteTuple tuple,
    //                                      PropagationContext context,
    //                                      InternalWorkingMemory workingMemory);
    //
    //    public List getPropagatedTuples(final Map memory,
    //                                    final InternalWorkingMemory workingMemory,
    //                                    final TupleSink sink);

    public int size();

}
