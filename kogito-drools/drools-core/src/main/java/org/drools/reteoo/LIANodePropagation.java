package org.drools.reteoo;

import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.spi.PropagationContext;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectInput;

public class LIANodePropagation
    implements
    Externalizable {
    private LeftInputAdapterNode node;
    private InternalFactHandle   handle;
    private PropagationContext   context;
    private boolean leftTupleMemoryEnabled;
    
    public LIANodePropagation() {
        // constructor needed for serialisation
    }

    public LIANodePropagation(final LeftInputAdapterNode node,
                              final InternalFactHandle handle,
                              final PropagationContext context) {
        super();
        this.node = node;
        this.handle = handle;
        this.context = context;
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        node = (LeftInputAdapterNode) in.readObject();
        handle = (InternalFactHandle) in.readObject();
        context = (PropagationContext) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( node );
        out.writeObject( handle );
        out.writeObject( context );
    }

    public void doPropagation(InternalWorkingMemory workingMemory) {
        node.getSinkPropagator().createAndPropagateAssertLeftTuple( handle,
                                                                    context,
                                                                    workingMemory,
                                                                    false  );
    }

}
