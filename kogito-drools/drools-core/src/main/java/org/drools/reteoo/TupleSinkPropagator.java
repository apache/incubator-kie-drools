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
    
    public List getPropagatedTuples(final Map memory,
                                    final InternalWorkingMemory workingMemory,
                                    final TupleSink sink);

}
