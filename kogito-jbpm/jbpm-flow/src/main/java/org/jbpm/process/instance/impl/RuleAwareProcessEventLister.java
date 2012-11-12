package org.jbpm.process.instance.impl;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import org.kie.event.process.ProcessCompletedEvent;
import org.kie.event.process.ProcessEventListener;
import org.kie.event.process.ProcessNodeLeftEvent;
import org.kie.event.process.ProcessNodeTriggeredEvent;
import org.kie.event.process.ProcessStartedEvent;
import org.kie.event.process.ProcessVariableChangedEvent;
import org.kie.runtime.KnowledgeRuntime;
import org.kie.runtime.ObjectFilter;
import org.kie.runtime.process.WorkflowProcessInstance;
import org.kie.runtime.rule.FactHandle;

public class RuleAwareProcessEventLister implements ProcessEventListener {
    
    private ConcurrentHashMap<Long, FactHandle> store = new ConcurrentHashMap<Long, FactHandle>();

    public void beforeProcessStarted(ProcessStartedEvent event) {
        
        FactHandle handle = event.getKnowledgeRuntime().insert(event.getProcessInstance());
        store.put(event.getProcessInstance().getId(), handle);
    }

    public void afterProcessStarted(ProcessStartedEvent event) {
        // do nothing
    }

    public void beforeProcessCompleted(ProcessCompletedEvent event) {
        // do nothing
    }

    public void afterProcessCompleted(ProcessCompletedEvent event) {
        FactHandle handle = getProcessInstanceFactHandle(event.getProcessInstance().getId(), event.getKnowledgeRuntime());
        
        if (handle != null) {
            event.getKnowledgeRuntime().retract(handle);
        }
    }

    public void beforeNodeTriggered(ProcessNodeTriggeredEvent event) {
        // do nothing
    }

    public void afterNodeTriggered(ProcessNodeTriggeredEvent event) {
        // do nothing
    }

    public void beforeNodeLeft(ProcessNodeLeftEvent event) {
        // do nothing
    }

    public void afterNodeLeft(ProcessNodeLeftEvent event) {
        // do nothing
    }

    public void beforeVariableChanged(ProcessVariableChangedEvent event) {
        // do nothing
    }

    public void afterVariableChanged(ProcessVariableChangedEvent event) {
        FactHandle handle = getProcessInstanceFactHandle(event.getProcessInstance().getId(), event.getKnowledgeRuntime());
        
        if (handle != null) {
            event.getKnowledgeRuntime().update(handle, event.getProcessInstance());
        } else {
            handle = event.getKnowledgeRuntime().insert(event.getProcessInstance());
            store.put(event.getProcessInstance().getId(), handle);
        }
    }

    protected FactHandle getProcessInstanceFactHandle(final Long processInstanceId, KnowledgeRuntime kruntime) {
        
        if (store.containsKey(processInstanceId)) {
            return store.get(processInstanceId);
        }
        
        //else try to search for it in the working memory
        Collection<FactHandle> factHandles = kruntime.getFactHandles(new ObjectFilter() {
            
            public boolean accept(Object object) {
                if (WorkflowProcessInstance.class.isAssignableFrom(object.getClass())) {
                    if (((WorkflowProcessInstance) object).getId() == processInstanceId) {
                        return true;
                    }
                }
                return false;
            }
        });
        
        if (factHandles != null && factHandles.size() > 0) {
            FactHandle handle = factHandles.iterator().next();
            // put it into store for faster access
            store.put(processInstanceId, handle);
            return handle;
        }
        return null;
    }
}
