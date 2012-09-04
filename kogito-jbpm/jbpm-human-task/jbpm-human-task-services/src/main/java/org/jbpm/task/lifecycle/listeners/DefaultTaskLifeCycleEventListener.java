/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.task.lifecycle.listeners;

import javax.enterprise.event.Observes;
import javax.enterprise.event.Reception;
import javax.enterprise.inject.Alternative;
import javax.inject.Singleton;
import org.jbpm.task.Task;
import org.jbpm.task.annotations.External;
import org.jbpm.task.events.AfterTaskActivatedEvent;
import org.jbpm.task.events.AfterTaskAddedEvent;
import org.jbpm.task.events.AfterTaskClaimedEvent;
import org.jbpm.task.events.AfterTaskCompletedEvent;
import org.jbpm.task.events.AfterTaskExitedEvent;
import org.jbpm.task.events.AfterTaskFailedEvent;
import org.jbpm.task.events.AfterTaskSkippedEvent;
import org.jbpm.task.events.AfterTaskStartedEvent;
import org.jbpm.task.events.AfterTaskStoppedEvent;


/**
 *
 */

@Singleton @External
public class DefaultTaskLifeCycleEventListener implements TaskLifeCycleEventListener{

    public DefaultTaskLifeCycleEventListener() {
        
    }

    public void afterTaskStartedEvent(@Observes(notifyObserver= Reception.IF_EXISTS) @AfterTaskStartedEvent Task ti) {
        
    }

    public void afterTaskActivatedEvent(@Observes(notifyObserver= Reception.IF_EXISTS) @AfterTaskActivatedEvent Task ti) {
        
    }

    public void afterTaskClaimedEvent(@Observes(notifyObserver= Reception.IF_EXISTS) @AfterTaskClaimedEvent Task ti) {
        
    }

    public void afterTaskSkippedEvent(@Observes(notifyObserver= Reception.IF_EXISTS) @AfterTaskSkippedEvent Task ti) {
        
    }

    public void afterTaskStoppedEvent(@Observes(notifyObserver= Reception.IF_EXISTS) @AfterTaskStoppedEvent Task ti) {
        
    }

    public void afterTaskCompletedEvent(@Observes(notifyObserver= Reception.IF_EXISTS) @AfterTaskCompletedEvent Task ti) {
        
    }

    public void afterTaskFailedEvent(@Observes(notifyObserver= Reception.IF_EXISTS) @AfterTaskFailedEvent Task ti) {
        
    }

    public void afterTaskAddedEvent(@Observes(notifyObserver= Reception.IF_EXISTS) @AfterTaskAddedEvent Task ti) {
        
    }

    public void afterTaskExitedEvent(@Observes(notifyObserver= Reception.IF_EXISTS) @AfterTaskExitedEvent Task ti) {
        
    }
    
}
