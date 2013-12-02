/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.services.task.impl;

import java.util.List;

import org.jbpm.services.task.utils.ClassUtil;
import org.kie.internal.task.api.TaskEventsService;
import org.kie.internal.task.api.TaskPersistenceContext;
import org.kie.internal.task.api.model.TaskEvent;

/**
 *
 */
public class TaskEventsServiceImpl implements TaskEventsService {

    private TaskPersistenceContext persistenceContext;
    
    public TaskEventsServiceImpl() {
    	
    }
    
    public TaskEventsServiceImpl(TaskPersistenceContext persistenceContext) {
    	this.persistenceContext = persistenceContext;
    }
    
    public List<TaskEvent> getTaskEventsById(long taskId) {
        return  persistenceContext.queryStringWithParametersInTransaction("select te from TaskEvent te where te.taskId =:taskId ", 
        		persistenceContext.addParametersToMap("taskId", taskId),
        		ClassUtil.<List<TaskEvent>>castClass(List.class));
    }

    public void removeTaskEventsById(long taskId) {
        List<TaskEvent> taskEventsById = getTaskEventsById(taskId);
        for (TaskEvent e : taskEventsById) {
        	persistenceContext.remove(e);
        }
    }
}
