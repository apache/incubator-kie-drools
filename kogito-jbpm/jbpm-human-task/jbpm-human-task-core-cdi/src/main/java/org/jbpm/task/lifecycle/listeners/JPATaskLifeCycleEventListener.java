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
    
}
