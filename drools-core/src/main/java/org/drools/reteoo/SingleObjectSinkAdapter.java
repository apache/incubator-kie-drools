package org.drools.reteoo;

import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.spi.PropagationContext;

public class SingleObjectSinkAdapter
    implements
    ObjectSinkPropagator {

    private static final long serialVersionUID = 873985743021L;
    
    private ObjectSink sink;

    public SingleObjectSinkAdapter(final ObjectSink sink) {
        this.sink = sink;
    }

    public void propagateAssertObject(final InternalFactHandle handle,
                                      final PropagationContext context,
                                      final InternalWorkingMemory workingMemory) {
        this.sink.assertObject( handle,
                                context,
                                workingMemory );

    }

    public void propagateRetractObject(final InternalFactHandle handle,
                                       final PropagationContext context,
                                       final InternalWorkingMemory workingMemory,
                                       final boolean useHash) {
        this.sink.retractObject( handle,
                                 context,
                                 workingMemory );

    }

    public ObjectSink[] getSinks() {
        return new ObjectSink[]{this.sink};
    }
    
    public int size() {
        return 1;
    }

}
