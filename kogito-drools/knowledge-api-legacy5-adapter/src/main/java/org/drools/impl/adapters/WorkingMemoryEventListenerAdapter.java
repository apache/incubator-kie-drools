/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

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

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof WorkingMemoryEventListenerAdapter && delegate.equals(((WorkingMemoryEventListenerAdapter)obj).delegate);
    }
}
