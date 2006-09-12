package org.drools.reteoo;

import org.drools.common.InternalWorkingMemory;
import org.drools.spi.PropagationContext;

public interface TupleMatchChildren {
    public void add(ReteTuple tuple);

    public void propagateRetractTuple(final PropagationContext context,
                                      final InternalWorkingMemory workingMemory);

    public void propagateModifyTuple(final PropagationContext context,
                                     final InternalWorkingMemory workingMemory);
    
    public ReteTuple getTupleForSink(TupleSink sink);
}
