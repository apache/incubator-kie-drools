/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.task.impl;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import org.jboss.seam.transaction.Transactional;
import org.jbpm.task.Task;
import org.jbpm.task.annotations.Local;
import org.jbpm.task.api.TaskAdminService;
import org.jbpm.task.query.TaskSummary;

/**
 *
 * @author salaboy
 */
@Local
@Named
@Transactional
public class TaskAdminServiceImpl implements TaskAdminService{

    @Inject 
    private EntityManager em;

    public TaskAdminServiceImpl() {
    }
    
    
    public List<TaskSummary> getActiveTasks() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<TaskSummary> getActiveTasks(Date since) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<TaskSummary> getCompletedTasks() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<TaskSummary> getCompletedTasks(Date since) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<TaskSummary> getCompletedTasksByProcessId(Long processId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int archiveTasks(List<TaskSummary> tasks) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<TaskSummary> getArchivedTasks() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int removeTasks(List<TaskSummary> tasks) {
        
        int count = 0;
        
        for(TaskSummary taskSummary : tasks){
            Task task = em.find(Task.class, taskSummary.getId());
            em.remove(task);
            count++;
        }
        
        return count;
    }
    
}
