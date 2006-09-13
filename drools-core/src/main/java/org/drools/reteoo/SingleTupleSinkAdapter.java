package org.drools.reteoo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.spi.PropagationContext;
import org.drools.util.LinkedList;
import org.drools.util.LinkedListObjectWrapper;

public class SingleTupleSinkAdapter
    implements
    TupleSinkPropagator {
    private TupleSink sink;

    public SingleTupleSinkAdapter(TupleSink sink) {
        this.sink = sink;
    }

    public void propagateAssertTuple(ReteTuple tuple,
                                     InternalFactHandle handle,
                                     TupleMatch tupleMatch,
                                     PropagationContext context,
                                     InternalWorkingMemory workingMemory) {
        ReteTuple joined = new ReteTuple( tuple,
                                          handle,
                                          sink );
        tupleMatch.addJoinedTuple( joined );
        joined.assertTuple( context,
                            workingMemory );
    }

    public LinkedList createAndAssertTuple(InternalFactHandle handle,
                                     PropagationContext context,
                                     InternalWorkingMemory workingMemory) {
        final LinkedList list = new LinkedList();
        ReteTuple tuple = new ReteTuple( handle,
                                         sink );
        list.add( new LinkedListObjectWrapper( tuple ) );
        tuple.assertTuple( context,
                           workingMemory );
        return list;
    }

    public void propagateAssertTuple(ReteTuple tuple,
                                     PropagationContext context,
                                     InternalWorkingMemory workingMemory) {
        final ReteTuple child = new ReteTuple( tuple,
                                               sink );
        // no TupleMatch so instead add as a linked tuple
        tuple.addLinkedTuple( new LinkedListObjectWrapper( child ) );
        child.assertTuple( context,
                           workingMemory );
    }

    public TupleSink[] getSinks() {
        return new TupleSink[]{this.sink};
    }

    public void propagateNewTupleSink(TupleMatch tupleMatch,
                                      PropagationContext context,
                                      InternalWorkingMemory workingMemory) {
        // do nothing, as we have no new tuple sinks
        throw new RuntimeException( "This is a bug you cannot update new data through this single sink adapter" );
    }

    public void propagateNewTupleSink(InternalFactHandle handle,
                                      LinkedList list,
                                      PropagationContext context,
                                      InternalWorkingMemory workingMemory) {
        // do nothing, as we have no new tuple sinks
        throw new RuntimeException( "This is a bug you cannot update new data through this single sink adapter" );

    }

    public List getPropagatedTuples(final Map memory,
                                    final InternalWorkingMemory workingMemory,
                                    final TupleSink sink) {
        final List propagatedTuples = new ArrayList( memory.size() );

        for ( final Iterator it = memory.values().iterator(); it.hasNext(); ) {
            final LinkedList tuples = (LinkedList) it.next();
            propagatedTuples.add( ((LinkedListObjectWrapper) tuples.getFirst()).getObject() );
        }

        return propagatedTuples;
    }
    
    public int size() {
        return 1;
    }

}
