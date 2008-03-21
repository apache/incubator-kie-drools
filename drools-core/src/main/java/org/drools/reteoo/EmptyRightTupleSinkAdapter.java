package org.drools.reteoo;

import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.spi.PropagationContext;

import java.io.ObjectOutput;
import java.io.IOException;
import java.io.ObjectInput;

public class EmptyRightTupleSinkAdapter
    implements
    RightTupleSinkPropagator {

    private static final long serialVersionUID = -631743913176779720L;

    private static final EmptyRightTupleSinkAdapter instance = new EmptyRightTupleSinkAdapter();

    private static final RightTupleSink[] SINK_LIST = new RightTupleSink[0];

    public static EmptyRightTupleSinkAdapter getInstance() {
        return instance;
    }

    public EmptyRightTupleSinkAdapter() {
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    }

    public void writeExternal(ObjectOutput out) throws IOException {
    }

    public void propagateAssertFact(final InternalFactHandle factHandle,
                                      final PropagationContext context,
                                      final InternalWorkingMemory workingMemory) {

    }

    public void propagateRetractObject(final InternalFactHandle handle,
                                       final PropagationContext context,
                                       final InternalWorkingMemory workingMemory,
                                       final boolean useHash) {
    }

    public RightTupleSink[] getSinks() {
        return SINK_LIST;
    }

    public int size() {
        return 0;
    }

    public boolean equals(Object obj) {
        return obj instanceof EmptyRightTupleSinkAdapter;
    }

}
