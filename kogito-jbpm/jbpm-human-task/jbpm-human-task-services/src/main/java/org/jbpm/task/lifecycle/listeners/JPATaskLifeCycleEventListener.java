/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.task.lifecycle.listeners;

import javax.enterprise.event.Observes;
import javax.enterprise.event.Reception;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import org.jboss.seam.transaction.Transactional;
import org.jbpm.task.Task;
import org.jbpm.task.TaskEvent;
import org.jbpm.task.events.AfterTaskActivatedEvent;
import org.jbpm.task.events.AfterTaskAddedEvent;
import org.jbpm.task.events.AfterTaskClaimedEvent;
import org.jbpm.task.events.AfterTaskCompletedEvent;
import org.jbpm.task.events.AfterTaskExitedEvent;
import org.jbpm.task.events.AfterTaskFailedEvent;
import org.jbpm.task.events.AfterTaskStartedEvent;
import org.jbpm.task.events.AfterTaskStoppedEvent;

/**
 *
 */

@Alternative @Singleton
public class JPATaskLifeCycleEventListener implements TaskLifeCycleEventListener{

    @Inject 
    private EntityManager em;
    
    public JPATaskLifeCycleEventListener() {
        
    }
    @Transactional
    public void afterTaskStartedEvent(@Observes(notifyObserver= Reception.IF_EXISTS) @AfterTaskStartedEvent Task ti) {
        em.persist(new TaskEvent(ti.getId(), TaskEvent.TaskEventType.STARTED, ti.getTaskData().getActualOwner()));
    }

    public void afterTaskActivatedEvent(@Observes(notifyObserver= Reception.IF_EXISTS) @AfterTaskActivatedEvent Task ti) {
        em.persist(new TaskEvent(ti.getId(), TaskEvent.TaskEventType.ACTIVATED, ti.getTaskData().getActualOwner()));
    }

    public void afterTaskClaimedEvent(@Observes(notifyObserver= Reception.IF_EXISTS) @AfterTaskClaimedEvent Task ti) {
        em.persist(new TaskEvent(ti.getId(), TaskEvent.TaskEventType.CLAIMED, ti.getTaskData().getActualOwner()));
    }

    public void afterTaskSkippedEvent(Task ti) {
        em.persist(new TaskEvent(ti.getId(), TaskEvent.TaskEventType.SKIPPED, ti.getTaskData().getActualOwner()));
    }

    public void afterTaskStoppedEvent(@Observes(notifyObserver= Reception.IF_EXISTS) @AfterTaskStoppedEvent Task ti ) {
        em.persist(new TaskEvent(ti.getId(), TaskEvent.TaskEventType.STOPPED, ti.getTaskData().getActualOwner()));
    }

    public void afterTaskCompletedEvent(@Observes(notifyObserver= Reception.IF_EXISTS) @AfterTaskCompletedEvent Task ti) {
        em.persist(new TaskEvent(ti.getId(), TaskEvent.TaskEventType.COMPLETED, ti.getTaskData().getActualOwner()));
    }

    public void afterTaskFailedEvent(@Observes(notifyObserver= Reception.IF_EXISTS) @AfterTaskFailedEvent Task ti) {
        em.persist(new TaskEvent(ti.getId(), TaskEvent.TaskEventType.FAILED, ti.getTaskData().getActualOwner()));
    }

    public void afterTaskAddedEvent(@Observes(notifyObserver= Reception.IF_EXISTS) @AfterTaskAddedEvent Task ti) {
        em.persist(new TaskEvent(ti.getId(), TaskEvent.TaskEventType.ADDED, ti.getTaskData().getActualOwner()));
    }

    public void afterTaskExitedEvent(@Observes(notifyObserver= Reception.IF_EXISTS) @AfterTaskExitedEvent Task ti) {
        em.persist(new TaskEvent(ti.getId(), TaskEvent.TaskEventType.EXITED, ti.getTaskData().getActualOwner()));
    }
    
}
