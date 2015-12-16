/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

import org.drools.event.process.ProcessEventListener;
import org.drools.event.rule.WorkingMemoryEventListener;
import org.drools.runtime.KnowledgeRuntime;
import org.drools.runtime.process.NodeInstance;
import org.drools.runtime.process.ProcessInstance;
import org.drools.runtime.rule.FactHandle;
import org.drools.runtime.rule.PropagationContext;
import org.kie.api.event.process.ProcessCompletedEvent;
import org.kie.api.event.process.ProcessNodeLeftEvent;
import org.kie.api.event.process.ProcessNodeTriggeredEvent;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.api.event.process.ProcessVariableChangedEvent;
import org.kie.api.event.rule.ObjectDeletedEvent;
import org.kie.api.event.rule.ObjectInsertedEvent;
import org.kie.api.event.rule.ObjectUpdatedEvent;

public class ProcessEventListenerAdapter implements org.kie.api.event.process.ProcessEventListener {

    private final ProcessEventListener delegate;

    public ProcessEventListenerAdapter(ProcessEventListener delegate) {
        this.delegate = delegate;
    }

	public void beforeProcessStarted(final ProcessStartedEvent event) {
        delegate.beforeProcessStarted(new org.drools.event.process.ProcessStartedEvent() {
			public ProcessInstance getProcessInstance() {
				return new ProcessInstanceAdapter(event.getProcessInstance());
			}
            public KnowledgeRuntime getKnowledgeRuntime() {
                return new KnowledgeRuntimeAdapter((org.kie.internal.runtime.KnowledgeRuntime) event.getKieRuntime());
            }
        });
	}

	public void afterProcessStarted(final ProcessStartedEvent event) {
        delegate.afterProcessStarted(new org.drools.event.process.ProcessStartedEvent() {
			public ProcessInstance getProcessInstance() {
				return new ProcessInstanceAdapter(event.getProcessInstance());
			}
            public KnowledgeRuntime getKnowledgeRuntime() {
                return new KnowledgeRuntimeAdapter((org.kie.internal.runtime.KnowledgeRuntime) event.getKieRuntime());
            }
        });
	}

	public void beforeProcessCompleted(final ProcessCompletedEvent event) {
        delegate.beforeProcessCompleted(new org.drools.event.process.ProcessCompletedEvent() {
			public ProcessInstance getProcessInstance() {
				return new ProcessInstanceAdapter(event.getProcessInstance());
			}
            public KnowledgeRuntime getKnowledgeRuntime() {
                return new KnowledgeRuntimeAdapter((org.kie.internal.runtime.KnowledgeRuntime) event.getKieRuntime());
            }
        });
	}

	public void afterProcessCompleted(final ProcessCompletedEvent event) {
        delegate.afterProcessCompleted(new org.drools.event.process.ProcessCompletedEvent() {
			public ProcessInstance getProcessInstance() {
				return new ProcessInstanceAdapter(event.getProcessInstance());
			}
            public KnowledgeRuntime getKnowledgeRuntime() {
                return new KnowledgeRuntimeAdapter((org.kie.internal.runtime.KnowledgeRuntime) event.getKieRuntime());
            }
        });
	}

	public void beforeNodeTriggered(final ProcessNodeTriggeredEvent event) {
        delegate.beforeNodeTriggered(new org.drools.event.process.ProcessNodeTriggeredEvent() {
			public ProcessInstance getProcessInstance() {
				return new ProcessInstanceAdapter(event.getProcessInstance());
			}
            public KnowledgeRuntime getKnowledgeRuntime() {
                return new KnowledgeRuntimeAdapter((org.kie.internal.runtime.KnowledgeRuntime) event.getKieRuntime());
            }
			public NodeInstance getNodeInstance() {
				return new NodeInstanceAdapter(event.getNodeInstance());
			}
        });
	}

	@Override
	public void afterNodeTriggered(final ProcessNodeTriggeredEvent event) {
        delegate.afterNodeTriggered(new org.drools.event.process.ProcessNodeTriggeredEvent() {
			public ProcessInstance getProcessInstance() {
				return new ProcessInstanceAdapter(event.getProcessInstance());
			}
            public KnowledgeRuntime getKnowledgeRuntime() {
                return new KnowledgeRuntimeAdapter((org.kie.internal.runtime.KnowledgeRuntime) event.getKieRuntime());
            }
			public NodeInstance getNodeInstance() {
				return new NodeInstanceAdapter(event.getNodeInstance());
			}
        });
	}

	@Override
	public void beforeNodeLeft(final ProcessNodeLeftEvent event) {
        delegate.beforeNodeLeft(new org.drools.event.process.ProcessNodeLeftEvent() {
			public ProcessInstance getProcessInstance() {
				return new ProcessInstanceAdapter(event.getProcessInstance());
			}
            public KnowledgeRuntime getKnowledgeRuntime() {
                return new KnowledgeRuntimeAdapter((org.kie.internal.runtime.KnowledgeRuntime) event.getKieRuntime());
            }
			public NodeInstance getNodeInstance() {
				return new NodeInstanceAdapter(event.getNodeInstance());
			}
        });
	}

	@Override
	public void afterNodeLeft(final ProcessNodeLeftEvent event) {
        delegate.afterNodeLeft(new org.drools.event.process.ProcessNodeLeftEvent() {
			public ProcessInstance getProcessInstance() {
				return new ProcessInstanceAdapter(event.getProcessInstance());
			}
            public KnowledgeRuntime getKnowledgeRuntime() {
                return new KnowledgeRuntimeAdapter((org.kie.internal.runtime.KnowledgeRuntime) event.getKieRuntime());
            }
			public NodeInstance getNodeInstance() {
				return new NodeInstanceAdapter(event.getNodeInstance());
			}
        });
    }

	@Override
	public void beforeVariableChanged(final ProcessVariableChangedEvent event) {
        delegate.beforeVariableChanged(new org.drools.event.process.ProcessVariableChangedEvent() {
			public ProcessInstance getProcessInstance() {
				return new ProcessInstanceAdapter(event.getProcessInstance());
			}
            public KnowledgeRuntime getKnowledgeRuntime() {
                return new KnowledgeRuntimeAdapter((org.kie.internal.runtime.KnowledgeRuntime) event.getKieRuntime());
            }
			public String getVariableId() {
				return event.getVariableId();
			}
			public String getVariableInstanceId() {
				return event.getVariableInstanceId();
			}
			public Object getOldValue() {
				return event.getOldValue();
			}
			public Object getNewValue() {
				return event.getNewValue();
			}
        });
	}

	@Override
	public void afterVariableChanged(final ProcessVariableChangedEvent event) {
        delegate.afterVariableChanged(new org.drools.event.process.ProcessVariableChangedEvent() {
			public ProcessInstance getProcessInstance() {
				return new ProcessInstanceAdapter(event.getProcessInstance());
			}
            public KnowledgeRuntime getKnowledgeRuntime() {
                return new KnowledgeRuntimeAdapter((org.kie.internal.runtime.KnowledgeRuntime) event.getKieRuntime());
            }
			public String getVariableId() {
				return event.getVariableId();
			}
			public String getVariableInstanceId() {
				return event.getVariableInstanceId();
			}
			public Object getOldValue() {
				return event.getOldValue();
			}
			public Object getNewValue() {
				return event.getNewValue();
			}
        });
	}

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ProcessEventListenerAdapter && delegate.equals(((ProcessEventListenerAdapter)obj).delegate);
    }
}
