/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.services.task.lifecycle.listeners;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.event.Reception;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;

import org.jboss.seam.transaction.Transactional;
import org.jbpm.services.task.events.AfterTaskActivatedEvent;
import org.jbpm.services.task.events.AfterTaskAddedEvent;
import org.jbpm.services.task.events.AfterTaskClaimedEvent;
import org.jbpm.services.task.events.AfterTaskCompletedEvent;
import org.jbpm.services.task.events.AfterTaskExitedEvent;
import org.jbpm.services.task.events.AfterTaskFailedEvent;
import org.jbpm.services.task.events.AfterTaskStartedEvent;
import org.jbpm.services.task.events.AfterTaskStoppedEvent;
import org.jbpm.services.task.impl.model.TaskEventImpl;
import org.jbpm.shared.services.api.JbpmServicesPersistenceManager;
import org.kie.api.task.model.Task;
import org.kie.internal.task.api.model.TaskEvent;

/**
 *
 */

@Alternative @ApplicationScoped
public class JPATaskLifeCycleEventListener implements TaskLifeCycleEventListener{

    @Inject 
    private JbpmServicesPersistenceManager pm;
    
    public JPATaskLifeCycleEventListener() {
        
    }

    public void setPm(JbpmServicesPersistenceManager pm) {
        this.pm = pm;
    }
    
    @Transactional
    public void afterTaskStartedEvent(@Observes(notifyObserver= Reception.IF_EXISTS) @AfterTaskStartedEvent Task ti) {
        pm.persist(new TaskEventImpl(ti.getId(), TaskEvent.TaskEventType.STARTED, ti.getTaskData().getActualOwner()));
    }

    public void afterTaskActivatedEvent(@Observes(notifyObserver= Reception.IF_EXISTS) @AfterTaskActivatedEvent Task ti) {
        pm.persist(new TaskEventImpl(ti.getId(), TaskEvent.TaskEventType.ACTIVATED, ti.getTaskData().getActualOwner()));
    }

    public void afterTaskClaimedEvent(@Observes(notifyObserver= Reception.IF_EXISTS) @AfterTaskClaimedEvent Task ti) {
        pm.persist(new TaskEventImpl(ti.getId(), TaskEvent.TaskEventType.CLAIMED, ti.getTaskData().getActualOwner()));
    }

    public void afterTaskSkippedEvent(Task ti) {
        pm.persist(new TaskEventImpl(ti.getId(), TaskEvent.TaskEventType.SKIPPED, ti.getTaskData().getActualOwner()));
    }

    public void afterTaskStoppedEvent(@Observes(notifyObserver= Reception.IF_EXISTS) @AfterTaskStoppedEvent Task ti ) {
        pm.persist(new TaskEventImpl(ti.getId(), TaskEvent.TaskEventType.STOPPED, ti.getTaskData().getActualOwner()));
    }

    public void afterTaskCompletedEvent(@Observes(notifyObserver= Reception.IF_EXISTS) @AfterTaskCompletedEvent Task ti) {
        pm.persist(new TaskEventImpl(ti.getId(), TaskEvent.TaskEventType.COMPLETED, ti.getTaskData().getActualOwner()));
    }

    public void afterTaskFailedEvent(@Observes(notifyObserver= Reception.IF_EXISTS) @AfterTaskFailedEvent Task ti) {
        pm.persist(new TaskEventImpl(ti.getId(), TaskEvent.TaskEventType.FAILED, ti.getTaskData().getActualOwner()));
    }

    public void afterTaskAddedEvent(@Observes(notifyObserver= Reception.IF_EXISTS) @AfterTaskAddedEvent Task ti) {
        pm.persist(new TaskEventImpl(ti.getId(), TaskEvent.TaskEventType.ADDED, ti.getTaskData().getActualOwner()));
    }

    public void afterTaskExitedEvent(@Observes(notifyObserver= Reception.IF_EXISTS) @AfterTaskExitedEvent Task ti) {
        pm.persist(new TaskEventImpl(ti.getId(), TaskEvent.TaskEventType.EXITED, ti.getTaskData().getActualOwner()));
    }
    
}
