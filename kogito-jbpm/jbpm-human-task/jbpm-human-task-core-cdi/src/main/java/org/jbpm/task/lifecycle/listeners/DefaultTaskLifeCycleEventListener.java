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
import org.jbpm.task.events.BeforeTaskActivatedEvent;
import org.jbpm.task.events.BeforeTaskClaimedEvent;
import org.jbpm.task.events.BeforeTaskSkippedEvent;
import org.jbpm.task.events.BeforeTaskStartedEvent;
import org.jbpm.task.events.BeforeTaskStoppedEvent;


/**
 *
 */

@Alternative @Singleton
public class DefaultTaskLifeCycleEventListener implements TaskLifeCycleEventListener{

    public DefaultTaskLifeCycleEventListener() {
        
    }

    public void afterTaskStartedEvent(@Observes(notifyObserver= Reception.IF_EXISTS) @BeforeTaskStartedEvent Task ti) {
        
    }

    public void afterTaskActivatedEvent(@Observes(notifyObserver= Reception.IF_EXISTS) @BeforeTaskActivatedEvent Task ti) {
        
    }

    public void afterTaskClaimedEvent(@Observes(notifyObserver= Reception.IF_EXISTS) @BeforeTaskClaimedEvent Task ti) {
        
    }

    public void afterTaskSkippedEvent(@Observes(notifyObserver= Reception.IF_EXISTS) @BeforeTaskSkippedEvent Task ti) {
        
    }

    public void afterTaskStoppedEvent(@Observes(notifyObserver= Reception.IF_EXISTS) @BeforeTaskStoppedEvent Task ti) {
        
    }
    
}
