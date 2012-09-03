/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.task.lifecycle.listeners;

import javax.enterprise.event.Observes;
import javax.enterprise.event.Reception;
import org.jbpm.task.Task;
import org.jbpm.task.annotations.Internal;
import org.jbpm.task.events.AfterTaskActivatedEvent;
import org.jbpm.task.events.AfterTaskAddedEvent;
import org.jbpm.task.events.AfterTaskClaimedEvent;
import org.jbpm.task.events.AfterTaskCompletedEvent;
import org.jbpm.task.events.AfterTaskFailedEvent;
import org.jbpm.task.events.AfterTaskSkippedEvent;
import org.jbpm.task.events.AfterTaskStartedEvent;
import org.jbpm.task.events.AfterTaskStoppedEvent;


/**
 *
 */
@Internal
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
