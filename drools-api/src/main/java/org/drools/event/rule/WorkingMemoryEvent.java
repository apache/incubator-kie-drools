package org.drools.event.rule;

import org.drools.event.KnowledgeRuntimeEvent;
import org.drools.runtime.rule.PropagationContext;
import org.drools.runtime.rule.WorkingMemory;

public interface WorkingMemoryEvent extends KnowledgeRuntimeEvent {        
    public PropagationContext getPropagationContext();
}
