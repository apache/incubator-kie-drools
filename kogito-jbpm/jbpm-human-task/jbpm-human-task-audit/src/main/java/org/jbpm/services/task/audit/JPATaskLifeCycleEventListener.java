/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.services.task.audit;

import java.util.Date;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.seam.transaction.Transactional;
import org.jbpm.services.task.events.AfterTaskActivatedEvent;
import org.jbpm.services.task.events.AfterTaskAddedEvent;
import org.jbpm.services.task.events.AfterTaskClaimedEvent;
import org.jbpm.services.task.events.AfterTaskCompletedEvent;
import org.jbpm.services.task.events.AfterTaskDelegatedEvent;
import org.jbpm.services.task.events.AfterTaskExitedEvent;
import org.jbpm.services.task.events.AfterTaskFailedEvent;
import org.jbpm.services.task.events.AfterTaskForwardedEvent;
import org.jbpm.services.task.events.AfterTaskReleasedEvent;
import org.jbpm.services.task.events.AfterTaskResumedEvent;
import org.jbpm.services.task.events.AfterTaskSkippedEvent;
import org.jbpm.services.task.events.AfterTaskStartedEvent;
import org.jbpm.services.task.events.AfterTaskStoppedEvent;
import org.jbpm.services.task.events.AfterTaskSuspendedEvent;
import org.jbpm.services.task.lifecycle.listeners.TaskLifeCycleEventListener;
import org.jbpm.shared.services.api.JbpmServicesPersistenceManager;
import org.kie.api.task.model.Task;
import org.kie.internal.task.api.model.TaskEvent;

/**
 *
 */

@ApplicationScoped
@Transactional
public class JPATaskLifeCycleEventListener implements TaskLifeCycleEventListener{

    @Inject 
    private JbpmServicesPersistenceManager pm;
    
    public JPATaskLifeCycleEventListener() {
    }

    public void setPm(JbpmServicesPersistenceManager pm) {
        this.pm = pm;
    }

    @Override
    public void afterTaskStartedEvent(@Observes @AfterTaskStartedEvent Task ti) {
        String userId = "";
        if(ti.getTaskData().getActualOwner() != null){
            userId = ti.getTaskData().getActualOwner().getId();
        }
        pm.persist(new TaskEventImpl(ti.getId(), TaskEvent.TaskEventType.STARTED, userId, new Date()));
    }

    @Override
    public void afterTaskActivatedEvent(@Observes @AfterTaskActivatedEvent Task ti) {
        String userId = "";
        if(ti.getTaskData().getActualOwner() != null){
            userId = ti.getTaskData().getActualOwner().getId();
        }
        pm.persist(new TaskEventImpl(ti.getId(), TaskEvent.TaskEventType.ACTIVATED, userId, new Date()));
    }

    @Override
    public void afterTaskClaimedEvent(@Observes @AfterTaskClaimedEvent Task ti) {
        String userId = "";
        if(ti.getTaskData().getActualOwner() != null){
            userId = ti.getTaskData().getActualOwner().getId();
        }
        pm.persist(new TaskEventImpl(ti.getId(), TaskEvent.TaskEventType.CLAIMED, userId, new Date()));
    }

    @Override
    public void afterTaskSkippedEvent(@Observes @AfterTaskSkippedEvent Task ti) {
        String userId = "";
        if(ti.getTaskData().getActualOwner() != null){
            userId = ti.getTaskData().getActualOwner().getId();
        }
        pm.persist(new TaskEventImpl(ti.getId(), TaskEvent.TaskEventType.SKIPPED, userId, new Date()));
    }

    @Override
    public void afterTaskStoppedEvent(@Observes @AfterTaskStoppedEvent Task ti ) {
        String userId = "";
        if(ti.getTaskData().getActualOwner() != null){
            userId = ti.getTaskData().getActualOwner().getId();
        }
        pm.persist(new TaskEventImpl(ti.getId(), TaskEvent.TaskEventType.STOPPED, userId, new Date()));
    }

    @Override
    public void afterTaskCompletedEvent(@Observes @AfterTaskCompletedEvent Task ti) {
        String userId = "";
        if(ti.getTaskData().getActualOwner() != null){
            userId = ti.getTaskData().getActualOwner().getId();
        }
        pm.persist(new TaskEventImpl(ti.getId(), TaskEvent.TaskEventType.COMPLETED, userId, new Date()));
    }

    @Override
    public void afterTaskFailedEvent(@Observes @AfterTaskFailedEvent Task ti) {
        String userId = "";
        if(ti.getTaskData().getActualOwner() != null){
            userId = ti.getTaskData().getActualOwner().getId();
        }
        pm.persist(new TaskEventImpl(ti.getId(), TaskEvent.TaskEventType.FAILED, userId, new Date()));
    }

    @Override
    public void afterTaskAddedEvent(@Observes @AfterTaskAddedEvent Task ti) {
        String userId = "";
        if(ti.getTaskData().getActualOwner() != null){
            userId = ti.getTaskData().getActualOwner().getId();
        }
        
        pm.persist(new TaskEventImpl(ti.getId(), TaskEvent.TaskEventType.ADDED, userId , new Date()));
    }

    @Override
    public void afterTaskExitedEvent(@Observes @AfterTaskExitedEvent Task ti) {
        String userId = "";
        if(ti.getTaskData().getActualOwner() != null){
            userId = ti.getTaskData().getActualOwner().getId();
        }
        pm.persist(new TaskEventImpl(ti.getId(), TaskEvent.TaskEventType.EXITED, userId, new Date()));
    }

    @Override
    public void afterTaskReleasedEvent(@Observes @AfterTaskReleasedEvent Task ti) {
        String userId = "";
        if(ti.getTaskData().getActualOwner() != null){
            userId = ti.getTaskData().getActualOwner().getId();
        }
        pm.persist(new TaskEventImpl(ti.getId(), TaskEvent.TaskEventType.RELEASED, userId, new Date()));
    }

    @Override
    public void afterTaskResumedEvent(@Observes @AfterTaskResumedEvent Task ti) {
        String userId = "";
        if(ti.getTaskData().getActualOwner() != null){
            userId = ti.getTaskData().getActualOwner().getId();
        }
        pm.persist(new TaskEventImpl(ti.getId(), TaskEvent.TaskEventType.RESUMED, userId, new Date()));
    }

    @Override
    public void afterTaskSuspendedEvent(@Observes @AfterTaskSuspendedEvent Task ti) {
        String userId = "";
        if(ti.getTaskData().getActualOwner() != null){
            userId = ti.getTaskData().getActualOwner().getId();
        }
        pm.persist(new TaskEventImpl(ti.getId(), TaskEvent.TaskEventType.SUSPENDED, userId, new Date()));
    }

    @Override
    public void afterTaskForwardedEvent(@Observes @AfterTaskForwardedEvent Task ti) {
        String userId = "";
        if(ti.getTaskData().getActualOwner() != null){
            userId = ti.getTaskData().getActualOwner().getId();
        }
        pm.persist(new TaskEventImpl(ti.getId(), TaskEvent.TaskEventType.FORWARDED, userId, new Date()));
    }

    @Override
    public void afterTaskDelegatedEvent(@Observes @AfterTaskDelegatedEvent Task ti) {
        String userId = "";
        if(ti.getTaskData().getActualOwner() != null){
            userId = ti.getTaskData().getActualOwner().getId();
        }
        pm.persist(new TaskEventImpl(ti.getId(), TaskEvent.TaskEventType.DELEGATED, userId, new Date()));
    }
    
}
