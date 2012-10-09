/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.task.impl;

import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import org.jboss.seam.transaction.Transactional;
import org.jbpm.task.TaskEvent;
import org.jbpm.task.api.TaskEventsService;

/**
 *
 */
@Transactional
@ApplicationScoped
public class TaskEventsServiceImpl implements TaskEventsService {

    @Inject
    private EntityManager em;

    public List<TaskEvent> getTaskEventsById(long taskId) {
        return em.createQuery("select te from TaskEvent te where te.taskId =:taskId ").setParameter("taskId", taskId).getResultList();
    }

    public void removeTaskEventsById(long taskId) {
        List<TaskEvent> taskEventsById = getTaskEventsById(taskId);
        for (TaskEvent e : taskEventsById) {
            em.remove(e);
        }
    }
}
