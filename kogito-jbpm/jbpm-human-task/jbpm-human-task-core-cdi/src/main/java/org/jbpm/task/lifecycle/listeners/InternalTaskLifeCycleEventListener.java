/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.task.lifecycle.listeners;

import javax.enterprise.event.Observes;
import javax.enterprise.event.Reception;
import org.jbpm.task.Task;
import org.jbpm.task.annotations.Internal;
import org.jbpm.task.events.BeforeTaskActivatedEvent;
import org.jbpm.task.events.BeforeTaskClaimedEvent;
import org.jbpm.task.events.BeforeTaskSkippedEvent;
import org.jbpm.task.events.BeforeTaskStartedEvent;


/**
 *
 */
@Internal
public class InternalTaskLifeCycleEventListener implements TaskLifeCycleEventListener{

    public InternalTaskLifeCycleEventListener() {
    }

    public void afterTaskStartedEvent(@Observes(notifyObserver= Reception.ALWAYS) @BeforeTaskStartedEvent Task ti) {
    }

    public void afterTaskActivatedEvent(@Observes(notifyObserver= Reception.ALWAYS) @BeforeTaskActivatedEvent Task ti) {
        
    }

    public void afterTaskClaimedEvent(@Observes(notifyObserver= Reception.ALWAYS) @BeforeTaskClaimedEvent Task ti) {
        
    }

    public void afterTaskSkippedEvent(@Observes(notifyObserver= Reception.ALWAYS) @BeforeTaskSkippedEvent Task ti) {
        
    }

    public void afterTaskStoppedEvent(Task ti) {
        
    }
    
}
