package org.drools.event.rule;

import org.drools.runtime.rule.PropagationContext;
import org.drools.runtime.rule.WorkingMemory;

public interface WorkingMemoryEvent {
    
    /**
     * This method returns null after it is Serialized
     */    
    public WorkingMemory getWorkingMemory();
    
    public PropagationContext getPropagationContext();
}
