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
package org.jbpm.services.task.wih;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.jbpm.services.task.exception.PermissionDeniedException;
import org.jbpm.services.task.utils.OnErrorAction;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.api.task.model.Group;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.Task;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;
import org.kie.internal.task.api.InternalTaskService;
import org.kie.internal.task.exception.TaskException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LocalHTWorkItemHandler extends AbstractHTWorkItemHandler {

    private static final Logger logger = LoggerFactory.getLogger(LocalHTWorkItemHandler.class);
    private RuntimeManager runtimeManager;        
    
    public RuntimeManager getRuntimeManager() {
        return runtimeManager;
    }

    public void setRuntimeManager(RuntimeManager runtimeManager) {
        this.runtimeManager = runtimeManager;
    }
   
    public LocalHTWorkItemHandler() {
    }
   
    @Override
    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
        
        RuntimeEngine runtime = runtimeManager.getRuntimeEngine(ProcessInstanceIdContext.get(workItem.getProcessInstanceId()));
        KieSession ksessionById = runtime.getKieSession();
        
        Task task = createTaskBasedOnWorkItemParams(ksessionById, workItem);
//        ContentData content = createTaskContentBasedOnWorkItemParams(ksessionById, workItem);
        Map<String, Object> content = createTaskDataBasedOnWorkItemParams(ksessionById, workItem);
        try {
            long taskId = ((InternalTaskService) runtime.getTaskService()).addTask(task, content);
            if (isAutoClaim(ksessionById, workItem, task)) {
            	try {
            		runtime.getTaskService().claim(taskId, (String) workItem.getParameter("SwimlaneActorId"));
            	} catch (PermissionDeniedException e) {
            		logger.warn("User {} is not allowed to auto claim task due to permission violation", workItem.getParameter("SwimlaneActorId"));
            	}
            }
        } catch (Exception e) {
            if (action.equals(OnErrorAction.ABORT)) {
                manager.abortWorkItem(workItem.getId());
            } else if (action.equals(OnErrorAction.RETHROW)) {
                if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                } else {
                    throw new RuntimeException(e);
                }
            } else if (action.equals(OnErrorAction.LOG)) {
                StringBuilder logMsg = new StringBuilder();
                logMsg.append(new Date()).append(": Error when creating task on task server for work item id ").append(workItem.getId());
                logMsg.append(". Error reported by task server: ").append(e.getMessage());
                logger.error(logMsg.toString(), e);
                // rethrow to cancel processing if the exception is not recoverable                
                if (!(e instanceof TaskException) || ((e instanceof TaskException) && !((TaskException) e).isRecoverable())) {
                	if (e instanceof RuntimeException) {
                        throw (RuntimeException) e;
                    } else {
                        throw new RuntimeException(e);
                    }
                }
            }
        } 
    }

    @Override
    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
        RuntimeEngine runtime = runtimeManager.getRuntimeEngine(ProcessInstanceIdContext.get(workItem.getProcessInstanceId()));
        Task task = runtime.getTaskService().getTaskByWorkItemId(workItem.getId());
        if (task != null) {
            try {
                String adminUser = ADMIN_USER;
                
                List<OrganizationalEntity> businessAdmins = task.getPeopleAssignments().getBusinessAdministrators();
                for (OrganizationalEntity admin : businessAdmins) {
                    if (admin instanceof Group) {
                        continue;
                    }
                    
                    if (!admin.getId().equals(ADMIN_USER)) {
                        adminUser = admin.getId();
                        break;
                    }
                }
                logger.debug("Task {} is going to be exited by {} who is business admin", task.getId(), adminUser);
                runtime.getTaskService().exit(task.getId(), adminUser);
            } catch (PermissionDeniedException e) {
                logger.info(e.getMessage());
            }
        }
        
    }
    
}
