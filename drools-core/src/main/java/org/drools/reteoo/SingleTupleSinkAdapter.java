package org.drools.reteoo;

import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.spi.PropagationContext;

import java.io.ObjectInput;
import java.io.IOException;
import java.io.ObjectOutput;

public class SingleTupleSinkAdapter
    implements
    LeftTupleSinkPropagator {
    private LeftTupleSink sink;

    public SingleTupleSinkAdapter() {
        
    }

    public SingleTupleSinkAdapter(final LeftTupleSink sink) {
        this.sink = sink;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        sink   = (LeftTupleSink)in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(sink);
    }

    public void propagateAssertLeftTuple(final LeftTuple tuple,
                                     final InternalFactHandle handle,
                                     final PropagationContext context,
                                     final InternalWorkingMemory workingMemory) {
        this.sink.assertLeftTuple( new LeftTuple( tuple,
                                              handle ),
                               context,
                               workingMemory );
    }

    public void propagateAssertLeftTuple(final LeftTuple tuple,
                                     final PropagationContext context,
                                     final InternalWorkingMemory workingMemory) {
        this.sink.assertLeftTuple( new LeftTuple( tuple ),
                               context,
                               workingMemory );
    }

    public void propagateRetractLeftTuple(final LeftTuple tuple,
                                      final InternalFactHandle handle,
                                      final PropagationContext context,
                                      final InternalWorkingMemory workingMemory) {
        this.sink.retractLeftTuple( new LeftTuple( tuple,
                                               handle ),
                                context,
                                workingMemory );
    }

    public void propagateRetractLeftTuple(final LeftTuple tuple,
                                      final PropagationContext context,
                                      final InternalWorkingMemory workingMemory) {
        this.sink.retractLeftTuple( new LeftTuple( tuple ),
                                context,
                                workingMemory );
    }

    public void createAndPropagateAssertLeftTuple(final InternalFactHandle handle,
                                              final PropagationContext context,
                                              final InternalWorkingMemory workingMemory) {
        this.sink.assertLeftTuple( new LeftTuple( handle ),
                               context,
                               workingMemory );
    }

    public void createAndPropagateRetractLeftTuple(final InternalFactHandle handle,
                                               final PropagationContext context,
                                               final InternalWorkingMemory workingMemory) {
        this.sink.retractLeftTuple( new LeftTuple( handle ),
                                context,
                                workingMemory );
    }

    public LeftTupleSink[] getSinks() {
        return new LeftTupleSink[]{this.sink};
    }

    public int size() {
        return (this.sink != null) ? 1 : 0;
    }
}
