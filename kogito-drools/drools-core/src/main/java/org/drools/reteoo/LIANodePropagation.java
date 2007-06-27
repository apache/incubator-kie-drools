package org.drools.reteoo;

import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.spi.PropagationContext;

public class LIANodePropagation {
    private final LeftInputAdapterNode node;
    private final InternalFactHandle handle;
    private final PropagationContext context;
    
    public LIANodePropagation(final LeftInputAdapterNode node,
                              final InternalFactHandle handle,
                              final PropagationContext context ) {
        super();
        this.node = node;
        this.handle = handle;
        this.context = context;
    }
    
    public void doPropagation(InternalWorkingMemory workingMemory) {
        node.getSinkPropagator().createAndPropagateAssertTuple( handle, context, workingMemory );
    }
    
    
}
