package org.drools.base;

import org.drools.common.InternalWorkingMemory;
import org.drools.reteoo.LeftTuple;
import org.drools.spi.PropagationContext;

public interface QueryResultCollector {
    public void add(final LeftTuple tuple,
                    final PropagationContext context,
                    final InternalWorkingMemory workingMemory);
}
