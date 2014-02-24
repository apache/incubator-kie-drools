package org.drools.impl.adapters;

import org.drools.event.rule.ObjectRetractedEvent;
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
                throw new UnsupportedOperationException("This operation is no longer supported");
            }

            public KnowledgeRuntime getKnowledgeRuntime() {
                return new KnowledgeRuntimeAdapter((org.kie.internal.runtime.KnowledgeRuntime)event.getKieRuntime());
            }
        });
    }

    public void objectUpdated(final ObjectUpdatedEvent event) {
        delegate.objectUpdated(new org.drools.event.rule.ObjectUpdatedEvent() {
            @Override
            public FactHandle getFactHandle() {
                return new FactHandleAdapter(event.getFactHandle());
            }

            @Override
            public Object getOldObject() {
                return event.getOldObject();
            }

            @Override
            public Object getObject() {
                return event.getObject();
            }

            @Override
            public PropagationContext getPropagationContext() {
                throw new UnsupportedOperationException("This operation is no longer supported");
            }

            @Override
            public KnowledgeRuntime getKnowledgeRuntime() {
                return new KnowledgeRuntimeAdapter((org.kie.internal.runtime.KnowledgeRuntime)event.getKieRuntime());
            }
        });
    }

    public void objectDeleted(final ObjectDeletedEvent event) {
        delegate.objectRetracted(new ObjectRetractedEvent() {
            @Override
            public FactHandle getFactHandle() {
                return new FactHandleAdapter(event.getFactHandle());
            }

            @Override
            public Object getOldObject() {
                return event.getOldObject();
            }

            @Override
            public PropagationContext getPropagationContext() {
                throw new UnsupportedOperationException("This operation is no longer supported");

            }

            @Override
            public KnowledgeRuntime getKnowledgeRuntime() {
                return new KnowledgeRuntimeAdapter((org.kie.internal.runtime.KnowledgeRuntime)event.getKieRuntime());
            }
        });
    }
}
