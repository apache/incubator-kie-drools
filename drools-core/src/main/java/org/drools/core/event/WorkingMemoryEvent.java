package org.drools.core.event;

import java.util.EventObject;

import org.drools.core.WorkingMemory;
import org.drools.core.common.PropagationContext;

public class WorkingMemoryEvent extends EventObject {
    private static final long        serialVersionUID = 510l;
    private final PropagationContext propagationContext;

    public WorkingMemoryEvent(final WorkingMemory workingMemory,
                              final PropagationContext propagationContext) {
        super( workingMemory );
        this.propagationContext = propagationContext;
    }

    public WorkingMemory getWorkingMemory() {
        return (WorkingMemory) getSource();
    }

    public PropagationContext getPropagationContext() {
        return this.propagationContext;
    }
}
