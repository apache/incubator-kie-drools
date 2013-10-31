package org.drools.impl.adapters;

import org.drools.event.rule.WorkingMemoryEventListener;
import org.drools.runtime.KnowledgeRuntime;
import org.drools.runtime.rule.FactHandle;
import org.drools.runtime.rule.PropagationContext;
import org.kie.api.event.rule.ObjectDeletedEvent;
import org.kie.api.event.rule.ObjectInsertedEvent;
import org.kie.api.event.rule.ObjectUpdatedEvent;

public class WorkingMemoryEventListenerAdapter implements org.kie.api.event.rule.RuleRuntimeEventListener {

    private final WorkingMemoryEventListener delegate;

    public WorkingMemoryEventListenerAdapter(WorkingMemoryEventListener delegate) {
        this.delegate = delegate;
    }

    public void objectInserted(final ObjectInsertedEvent event) {
        delegate.objectInserted(new org.drools.event.rule.ObjectInsertedEvent() {
            public FactHandle getFactHandle() {
                return new FactHandleAdapter(event.getFactHandle());
            }

            public Object getObject() {
                return event.getObject();
            }

            public PropagationContext getPropagationContext() {
                throw new UnsupportedOperationException(".getPropagationContext -> TODO");
            }

            public KnowledgeRuntime getKnowledgeRuntime() {
                return new KnowledgeRuntimeAdapter((org.kie.internal.runtime.KnowledgeRuntime)event.getKieRuntime());
            }
        });
    }

    public void objectUpdated(ObjectUpdatedEvent event) {
        throw new UnsupportedOperationException("org.drools.impl.adapters.WorkingMemoryEventListenerAdapter.objectUpdated -> TODO");

    }

    public void objectDeleted(ObjectDeletedEvent event) {
        throw new UnsupportedOperationException("org.drools.impl.adapters.WorkingMemoryEventListenerAdapter.objectDeleted -> TODO");

    }
}
