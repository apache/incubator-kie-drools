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
import org.jbpm.task.events.BeforeTaskStartedEvent;

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
    public void afterTaskStartedEvent(@Observes(notifyObserver= Reception.IF_EXISTS) @BeforeTaskStartedEvent Task ti) {
        em.persist(new TaskEvent(ti.getId(), TaskEvent.TaskEventType.STARTED, ti.getTaskData().getActualOwner()));
    }

    public void afterTaskActivatedEvent(Task ti) {
        em.persist(new TaskEvent(ti.getId(), TaskEvent.TaskEventType.ACTIVATED, ti.getTaskData().getActualOwner()));
    }

    public void afterTaskClaimedEvent(Task ti) {
        em.persist(new TaskEvent(ti.getId(), TaskEvent.TaskEventType.CLAIMED, ti.getTaskData().getActualOwner()));
    }

    public void afterTaskSkippedEvent(Task ti) {
        em.persist(new TaskEvent(ti.getId(), TaskEvent.TaskEventType.SKIPPED, ti.getTaskData().getActualOwner()));
    }

    public void afterTaskStoppedEvent(Task ti) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
