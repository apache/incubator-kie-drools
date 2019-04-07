/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
