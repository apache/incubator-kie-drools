/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.services.task.audit;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.seam.transaction.Transactional;
import org.jbpm.services.task.events.AfterTaskActivatedEvent;
import org.jbpm.services.task.events.AfterTaskAddedEvent;
import org.jbpm.services.task.events.AfterTaskClaimedEvent;
import org.jbpm.services.task.events.AfterTaskCompletedEvent;
import org.jbpm.services.task.events.AfterTaskExitedEvent;
import org.jbpm.services.task.events.AfterTaskFailedEvent;
import org.jbpm.services.task.events.AfterTaskSkippedEvent;
import org.jbpm.services.task.events.AfterTaskStartedEvent;
import org.jbpm.services.task.events.AfterTaskStoppedEvent;
import org.jbpm.services.task.lifecycle.listeners.TaskLifeCycleEventListener;
import org.jbpm.shared.services.api.JbpmServicesPersistenceManager;
import org.kie.api.task.model.Task;
import org.kie.internal.task.api.model.TaskEvent;

/**
 *
 */

@ApplicationScoped
public class JPATaskLifeCycleEventListener implements TaskLifeCycleEventListener{

    @Inject 
    private JbpmServicesPersistenceManager pm;
    
    public JPATaskLifeCycleEventListener() {
    }

    public void setPm(JbpmServicesPersistenceManager pm) {
        this.pm = pm;
    }
    
    @Transactional
    public void afterTaskStartedEvent(@Observes @AfterTaskStartedEvent Task ti) {
        pm.persist(new TaskEventImpl(ti.getId(), TaskEvent.TaskEventType.STARTED, ti.getTaskData().getActualOwner()));
    }

    public void afterTaskActivatedEvent(@Observes @AfterTaskActivatedEvent Task ti) {
        pm.persist(new TaskEventImpl(ti.getId(), TaskEvent.TaskEventType.ACTIVATED, ti.getTaskData().getActualOwner()));
    }

    public void afterTaskClaimedEvent(@Observes @AfterTaskClaimedEvent Task ti) {
        pm.persist(new TaskEventImpl(ti.getId(), TaskEvent.TaskEventType.CLAIMED, ti.getTaskData().getActualOwner()));
    }

    public void afterTaskSkippedEvent(@Observes @AfterTaskSkippedEvent Task ti) {
        pm.persist(new TaskEventImpl(ti.getId(), TaskEvent.TaskEventType.SKIPPED, ti.getTaskData().getActualOwner()));
    }

    public void afterTaskStoppedEvent(@Observes @AfterTaskStoppedEvent Task ti ) {
        pm.persist(new TaskEventImpl(ti.getId(), TaskEvent.TaskEventType.STOPPED, ti.getTaskData().getActualOwner()));
    }

    public void afterTaskCompletedEvent(@Observes @AfterTaskCompletedEvent Task ti) {
        pm.persist(new TaskEventImpl(ti.getId(), TaskEvent.TaskEventType.COMPLETED, ti.getTaskData().getActualOwner()));
    }

    public void afterTaskFailedEvent(@Observes @AfterTaskFailedEvent Task ti) {
        pm.persist(new TaskEventImpl(ti.getId(), TaskEvent.TaskEventType.FAILED, ti.getTaskData().getActualOwner()));
    }

    public void afterTaskAddedEvent(@Observes @AfterTaskAddedEvent Task ti) {
        pm.persist(new TaskEventImpl(ti.getId(), TaskEvent.TaskEventType.ADDED, ti.getTaskData().getActualOwner()));
    }

    public void afterTaskExitedEvent(@Observes @AfterTaskExitedEvent Task ti) {
        pm.persist(new TaskEventImpl(ti.getId(), TaskEvent.TaskEventType.EXITED, ti.getTaskData().getActualOwner()));
    }
    
}
