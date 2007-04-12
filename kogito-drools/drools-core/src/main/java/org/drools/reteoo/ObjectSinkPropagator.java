package org.drools.reteoo;

import java.io.Serializable;

import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.spi.PropagationContext;

public interface ObjectSinkPropagator
    extends
    Serializable {
    public void propagateAssertObject(InternalFactHandle handle,
                                      PropagationContext context,
                                      InternalWorkingMemory workingMemory);

    public void propagateRetractObject(InternalFactHandle handle,
                                       PropagationContext context,
                                       InternalWorkingMemory workingMemory,
                                       boolean useHash);

    public ObjectSink[] getSinks();
}
