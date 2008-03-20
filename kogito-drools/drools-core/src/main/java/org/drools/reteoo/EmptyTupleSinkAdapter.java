package org.drools.reteoo;

import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.spi.PropagationContext;

import java.io.ObjectOutput;
import java.io.IOException;
import java.io.ObjectInput;

public class EmptyTupleSinkAdapter
    implements
    LeftTupleSinkPropagator {

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
    
    public void propagateAssertLeftTuple(final LeftTuple tuple,
                                     final InternalFactHandle handle,
                                     final PropagationContext context,
                                     final InternalWorkingMemory workingMemory) {
    }

    public void propagateAssertLeftTuple(final LeftTuple tuple,
                                     final PropagationContext context,
                                     final InternalWorkingMemory workingMemory) {
    }

    public void propagateRetractLeftTuple(final LeftTuple tuple,
                                      final InternalFactHandle handle,
                                      final PropagationContext context,
                                      final InternalWorkingMemory workingMemory) {
    }

    public void propagateRetractLeftTuple(final LeftTuple tuple,
                                      final PropagationContext context,
                                      final InternalWorkingMemory workingMemory) {
    }

    public void createAndPropagateAssertLeftTuple(final InternalFactHandle handle,
                                              final PropagationContext context,
                                              final InternalWorkingMemory workingMemory) {
    }

    public void createAndPropagateRetractLeftTuple(final InternalFactHandle handle,
                                               final PropagationContext context,
                                               final InternalWorkingMemory workingMemory) {
    }

    public LeftTupleSink[] getSinks() {
        return new LeftTupleSink[]{};
    }

    public int size() {
        return 0;
    }
}
