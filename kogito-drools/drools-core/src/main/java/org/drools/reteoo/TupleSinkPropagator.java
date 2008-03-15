package org.drools.reteoo;

import java.io.Externalizable;

import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.spi.PropagationContext;

public interface TupleSinkPropagator
    extends
    Externalizable {
    public void propagateAssertTuple(ReteTuple tuple,
                                     InternalFactHandle handle,
                                     PropagationContext context,
                                     InternalWorkingMemory workingMemory);

    public void propagateAssertTuple(ReteTuple tuple,
                                     PropagationContext context,
                                     InternalWorkingMemory workingMemory);

    public void propagateRetractTuple(ReteTuple tuple,
                                      InternalFactHandle handle,
                                      PropagationContext context,
                                      InternalWorkingMemory workingMemory);

    public void propagateRetractTuple(ReteTuple tuple,
                                      PropagationContext context,
                                      InternalWorkingMemory workingMemory);

    public void createAndPropagateAssertTuple(InternalFactHandle handle,
                                              PropagationContext context,
                                              InternalWorkingMemory workingMemory);

    public void createAndPropagateRetractTuple(InternalFactHandle handle,
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
