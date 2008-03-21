package org.drools.reteoo;

import java.io.Serializable;
import java.io.Externalizable;

import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.spi.PropagationContext;

public interface RightTupleSinkPropagator
    extends
    Externalizable {
    public void propagateAssertFact(InternalFactHandle factHandle,
                                          PropagationContext context,
                                          InternalWorkingMemory workingMemory);

    public RightTupleSink[] getSinks();

    public int size();
}
