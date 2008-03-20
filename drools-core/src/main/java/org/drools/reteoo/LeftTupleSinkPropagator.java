package org.drools.reteoo;

import java.io.Externalizable;

import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.spi.PropagationContext;

public interface LeftTupleSinkPropagator
    extends
    Externalizable {
    public void propagateAssertLeftTuple(LeftTuple leftTuple,
                                     InternalFactHandle handle,
                                     PropagationContext context,
                                     InternalWorkingMemory workingMemory);

    public void propagateAssertLeftTuple(LeftTuple leftTuple,
                                     PropagationContext context,
                                     InternalWorkingMemory workingMemory);

    public void propagateRetractLeftTuple(LeftTuple leftTuple,
                                      InternalFactHandle handle,
                                      PropagationContext context,
                                      InternalWorkingMemory workingMemory);

    public void propagateRetractLeftTuple(LeftTuple tuple,
                                      PropagationContext context,
                                      InternalWorkingMemory workingMemory);

    public void createAndPropagateAssertLeftTuple(InternalFactHandle handle,
                                              PropagationContext context,
                                              InternalWorkingMemory workingMemory);

    public void createAndPropagateRetractLeftTuple(InternalFactHandle handle,
                                               PropagationContext context,
                                               InternalWorkingMemory workingMemory);

    public LeftTupleSink[] getSinks();

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
