package org.drools.event.rule;

import org.drools.event.KnowledgeRuntimeEvent;
import org.drools.runtime.rule.PropagationContext;

public interface WorkingMemoryEvent
    extends
    KnowledgeRuntimeEvent {
    public PropagationContext getPropagationContext();
}
