package org.droolsjbpm.services.impl.event.listeners;

import org.droolsjbpm.services.impl.event.NodeInstanceLeftEvent;
import org.droolsjbpm.services.impl.event.NodeInstanceTriggeredEvent;
import org.jboss.seam.transaction.Transactional;
import org.kie.api.event.process.ProcessCompletedEvent;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.event.process.ProcessNodeLeftEvent;
import org.kie.api.event.process.ProcessNodeTriggeredEvent;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.api.event.process.ProcessVariableChangedEvent;
import org.kie.runtime.ObjectFilter;
import org.kie.runtime.process.WorkflowProcessInstance;
import org.kie.runtime.rule.FactHandle;

import javax.enterprise.context.ApplicationScoped;
import java.util.Collection;
import org.droolsjbpm.services.impl.event.ProcessInstanceCompletedEvent;
import org.droolsjbpm.services.impl.event.ProcessInstanceStartedEvent;

import org.kie.runtime.KieRuntime;
import org.kie.runtime.KieSession;

@ApplicationScoped // This should be something like DomainScoped
@Transactional
public class CDIRuleAwareProcessEventListener implements ProcessEventListener {
    

    public void beforeProcessStarted(ProcessStartedEvent event) {        
        FactHandle handle = getProcessInstanceFactHandle(event.getProcessInstance().getId(), event.getKieRuntime());
        if (handle != null) {
            event.getKieRuntime().update(handle, event.getProcessInstance());
        } else {
            event.getKieRuntime().insert(event.getProcessInstance());
          
        }
    }

    public void afterProcessStarted(ProcessStartedEvent event) {
        event.getKieRuntime().getEntryPoint("process-events").insert(new ProcessInstanceStartedEvent(event));
        ((KieSession) event.getKieRuntime()).fireAllRules();
    }

    public void beforeProcessCompleted(ProcessCompletedEvent event) {
        event.getKieRuntime().getEntryPoint("process-events").insert(new ProcessInstanceCompletedEvent(event));
        ((KieSession) event.getKieRuntime()).fireAllRules();
    }

    public void afterProcessCompleted(ProcessCompletedEvent event) {
        FactHandle handle = getProcessInstanceFactHandle(event.getProcessInstance().getId(), event.getKieRuntime());
        
        if (handle != null) {
            event.getKieRuntime().retract(handle);
        }
    }

    public void beforeNodeTriggered(ProcessNodeTriggeredEvent event) {
        // do nothing
        event.getKieRuntime().getEntryPoint("process-events").insert(new NodeInstanceTriggeredEvent(event));
        ((KieSession) event.getKieRuntime()).fireAllRules();
        
    }

    public void afterNodeTriggered(ProcessNodeTriggeredEvent event) {
        // do nothing
    }

    public void beforeNodeLeft(ProcessNodeLeftEvent event) {
        event.getKieRuntime().getEntryPoint("process-events").insert(new NodeInstanceLeftEvent(event));
        ((KieSession) event.getKieRuntime()).fireAllRules();
    }

    public void afterNodeLeft(ProcessNodeLeftEvent event) {
        // do nothing
    }

    public void beforeVariableChanged(ProcessVariableChangedEvent event) {
        // do nothing
    }

    public void afterVariableChanged(ProcessVariableChangedEvent event) {
        FactHandle handle = getProcessInstanceFactHandle(event.getProcessInstance().getId(), event.getKieRuntime());
        
        if (handle != null) {
            event.getKieRuntime().update(handle, event.getProcessInstance());
        } else {
            event.getKieRuntime().insert(event.getProcessInstance());
          
        }
    }

    protected FactHandle getProcessInstanceFactHandle(final Long processInstanceId, KieRuntime kruntime) {
        

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
            return handle;
        }
        return null;
    }
}
