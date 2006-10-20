package org.drools.reteoo;

import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.spi.PropagationContext;

public class SingleTupleSinkAdapter
    implements
    TupleSinkPropagator {
    private TupleSink sink;

    public SingleTupleSinkAdapter(final TupleSink sink) {
        this.sink = sink;
    }

    public void propagateAssertTuple(final ReteTuple tuple,
                                     final InternalFactHandle handle,
                                     final PropagationContext context,
                                     final InternalWorkingMemory workingMemory) {
        this.sink.assertTuple( new ReteTuple( tuple,
                                              handle ),
                               context,
                               workingMemory );
    }

    public void propagateAssertTuple(final ReteTuple tuple,
                                     final PropagationContext context,
                                     final InternalWorkingMemory workingMemory) {
        this.sink.assertTuple( new ReteTuple( tuple ),
                               context,
                               workingMemory );
    }

    public void propagateRetractTuple(final ReteTuple tuple,
                                      final InternalFactHandle handle,
                                      final PropagationContext context,
                                      final InternalWorkingMemory workingMemory) {
        this.sink.retractTuple( new ReteTuple( tuple,
                                               handle ),
                                context,
                                workingMemory );
    }

    public void propagateRetractTuple(final ReteTuple tuple,
                                      final PropagationContext context,
                                      final InternalWorkingMemory workingMemory) {
        this.sink.retractTuple( new ReteTuple( tuple ),
                                context,
                                workingMemory );
    }

    public ReteTuple createAndPropagateAssertTuple(final InternalFactHandle handle,
                                                   final PropagationContext context,
                                                   final InternalWorkingMemory workingMemory) {
        // This is the root fact, so we don't need to clone it.
        final ReteTuple tuple = new ReteTuple( handle );
        this.sink.assertTuple( tuple,
                               context,
                               workingMemory );
        return tuple;
    }

    public void createAndPropagateRetractTuple(final ReteTuple tuple,
                                               final PropagationContext context,
                                               final InternalWorkingMemory workingMemory) {
        this.sink.retractTuple( tuple,
                                context,
                                workingMemory );
    }

    public TupleSink[] getSinks() {
        return new TupleSink[]{this.sink};
    }

    public int size() {
        return (this.sink != null) ? 1 : 0;
    }
}
