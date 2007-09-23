package org.drools.reteoo;

import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.spi.PropagationContext;

public class EmptyObjectSinkAdapter
    implements
    ObjectSinkPropagator {

    private static final long serialVersionUID = -631743913176779720L;

    private static final EmptyObjectSinkAdapter instance = new EmptyObjectSinkAdapter();
    
    private static final ObjectSink[] SINK_LIST = new ObjectSink[0];

    public static EmptyObjectSinkAdapter getInstance() {
        return instance;
    }

    private EmptyObjectSinkAdapter() {
    }

    public void propagateAssertObject(final InternalFactHandle handle,
                                      final PropagationContext context,
                                      final InternalWorkingMemory workingMemory) {

    }

    public void propagateRetractObject(final InternalFactHandle handle,
                                       final PropagationContext context,
                                       final InternalWorkingMemory workingMemory,
                                       final boolean useHash) {
    }

    public ObjectSink[] getSinks() {
        return SINK_LIST;
    }
    
    public int size() {
        return 0;
    }

}
