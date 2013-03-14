/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.task.impl;

import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.jboss.seam.transaction.Transactional;
import org.jbpm.shared.services.api.JbpmServicesPersistenceManager;
import org.jbpm.task.TaskDef;
import org.jbpm.task.api.TaskDefService;

/**
 *
 */

@Transactional
@ApplicationScoped
public class TaskDefServiceImpl implements TaskDefService{
    
    @Inject 
    private JbpmServicesPersistenceManager pm;

    public TaskDefServiceImpl() {
    }

    public void deployTaskDef(TaskDef def) {
        pm.persist(def);    
    }

    public List<TaskDef> getAllTaskDef(String filter) {
        List<TaskDef> resultList = (List<TaskDef>) pm.queryStringInTransaction("select td from TaskDef td"); 
        return resultList;
    }

    public TaskDef getTaskDefById(String name) {
        //TODO: FIX LOGIC
        
        List<TaskDef> resultList =  (List<TaskDef>)pm.queryStringWithParametersInTransaction("select td from TaskDef td where td.name = :name", pm.addParametersToMap("name", name));
                                 
        
        if(resultList.size() > 0){
            return resultList.get(0);
        }
        return null;
        
    }
    
    public void undeployTaskDef(String name) {
        TaskDef taskDef = getTaskDefById(name);
        pm.remove(taskDef);    
    }
    
}
