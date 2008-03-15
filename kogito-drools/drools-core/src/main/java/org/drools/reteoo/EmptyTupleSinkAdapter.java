package org.drools.reteoo;

import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.spi.PropagationContext;

import java.io.ObjectOutput;
import java.io.IOException;
import java.io.ObjectInput;

public class EmptyTupleSinkAdapter
    implements
    TupleSinkPropagator {

    private static final EmptyTupleSinkAdapter instance = new EmptyTupleSinkAdapter();

    public static final EmptyTupleSinkAdapter getInstance() {
        return instance;
    }

    public EmptyTupleSinkAdapter() {
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    }

    public void writeExternal(ObjectOutput out) throws IOException {
    }
    
    public void propagateAssertTuple(final ReteTuple tuple,
                                     final InternalFactHandle handle,
                                     final PropagationContext context,
                                     final InternalWorkingMemory workingMemory) {
    }

    public void propagateAssertTuple(final ReteTuple tuple,
                                     final PropagationContext context,
                                     final InternalWorkingMemory workingMemory) {
    }

    public void propagateRetractTuple(final ReteTuple tuple,
                                      final InternalFactHandle handle,
                                      final PropagationContext context,
                                      final InternalWorkingMemory workingMemory) {
    }

    public void propagateRetractTuple(final ReteTuple tuple,
                                      final PropagationContext context,
                                      final InternalWorkingMemory workingMemory) {
    }

    public void createAndPropagateAssertTuple(final InternalFactHandle handle,
                                              final PropagationContext context,
                                              final InternalWorkingMemory workingMemory) {
    }

    public void createAndPropagateRetractTuple(final InternalFactHandle handle,
                                               final PropagationContext context,
                                               final InternalWorkingMemory workingMemory) {
    }

    public TupleSink[] getSinks() {
        return new TupleSink[]{};
    }

    public int size() {
        return 0;
    }
}
