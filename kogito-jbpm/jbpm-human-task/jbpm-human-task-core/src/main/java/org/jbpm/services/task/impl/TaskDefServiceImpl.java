/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.services.task.impl;

import java.util.List;

import org.jbpm.services.task.utils.ClassUtil;
import org.kie.internal.task.api.TaskDefService;
import org.kie.internal.task.api.TaskPersistenceContext;
import org.kie.internal.task.api.model.TaskDef;

/**
 *
 */
public class TaskDefServiceImpl implements TaskDefService{
    

    private TaskPersistenceContext persistenceContext;

	public TaskDefServiceImpl() {
    }
	
	public TaskDefServiceImpl(TaskPersistenceContext persistenceContext) {
		this.persistenceContext = persistenceContext;
	}
	
    public void setPersistenceContext(TaskPersistenceContext persistenceContext) {
		this.persistenceContext = persistenceContext;
	}

    public void deployTaskDef(TaskDef def) {
    	persistenceContext.persist(def);    
    }

    public List<TaskDef> getAllTaskDef(String filter) {
        List<TaskDef> resultList = persistenceContext.queryStringInTransaction("select td from TaskDef td",
        		ClassUtil.<List<TaskDef>>castClass(List.class)); 
        return resultList;
    }

    public TaskDef getTaskDefById(String name) {
        //TODO: FIX LOGIC
        
        List<TaskDef> resultList = persistenceContext.queryStringWithParametersInTransaction("select td from TaskDef td where td.name = :name", 
        		persistenceContext.addParametersToMap("name", name),
        		ClassUtil.<List<TaskDef>>castClass(List.class));
                                 
        
        if(resultList.size() > 0){
            return resultList.get(0);
        }
        return null;
        
    }
    
    public void undeployTaskDef(String name) {
        TaskDef taskDef = getTaskDefById(name);
        persistenceContext.remove(taskDef);    
    }
    
}
