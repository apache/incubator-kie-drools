package org.drools.reteoo;

import java.io.Serializable;
import java.io.Externalizable;

import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.spi.PropagationContext;

public interface ObjectSinkPropagator
    extends
    Externalizable {
    public void propagateAssertObject(InternalFactHandle factHandle,
                                      PropagationContext context,
                                      InternalWorkingMemory workingMemory);

    public ObjectSink[] getSinks();

    public int size();
}
