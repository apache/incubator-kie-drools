/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.services.task.lifecycle.listeners;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.event.Reception;

import org.jbpm.services.task.annotations.Internal;
import org.jbpm.services.task.events.AfterTaskActivatedEvent;
import org.jbpm.services.task.events.AfterTaskAddedEvent;
import org.jbpm.services.task.events.AfterTaskClaimedEvent;
import org.jbpm.services.task.events.AfterTaskCompletedEvent;
import org.jbpm.services.task.events.AfterTaskFailedEvent;
import org.jbpm.services.task.events.AfterTaskSkippedEvent;
import org.jbpm.services.task.events.AfterTaskStartedEvent;
import org.jbpm.services.task.events.AfterTaskStoppedEvent;
import org.kie.internal.task.api.model.Task;


/**
 *
 */
@Internal
@ApplicationScoped
public class InternalTaskLifeCycleEventListener implements TaskLifeCycleEventListener{

    public InternalTaskLifeCycleEventListener() {
    }

    public void afterTaskStartedEvent(@Observes(notifyObserver= Reception.ALWAYS) @AfterTaskStartedEvent Task ti) {
    }

    public void afterTaskActivatedEvent(@Observes(notifyObserver= Reception.ALWAYS) @AfterTaskActivatedEvent Task ti) {
        
    }

    public void afterTaskClaimedEvent(@Observes(notifyObserver= Reception.ALWAYS) @AfterTaskClaimedEvent Task ti) {
        
    }

    public void afterTaskSkippedEvent(@Observes(notifyObserver= Reception.ALWAYS) @AfterTaskSkippedEvent Task ti) {
        
    }

    public void afterTaskStoppedEvent(@Observes(notifyObserver= Reception.ALWAYS) @AfterTaskStoppedEvent Task ti) {
        
    }

    public void afterTaskCompletedEvent(@Observes(notifyObserver= Reception.ALWAYS) @AfterTaskCompletedEvent Task ti) {
        
    }

    public void afterTaskFailedEvent(@Observes(notifyObserver= Reception.ALWAYS) @AfterTaskFailedEvent Task ti) {
        
    }

    public void afterTaskAddedEvent(@Observes(notifyObserver= Reception.ALWAYS) @AfterTaskAddedEvent Task ti) {
        
    }

    public void afterTaskExitedEvent(Task ti) {
        
    }
    
}
