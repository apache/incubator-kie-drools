/*
 * Copyright 2012 JBoss by Red Hat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.task.wih;

import java.util.Date;

import javax.enterprise.context.ApplicationScoped;

import org.jboss.seam.transaction.Transactional;
import org.jbpm.task.exception.PermissionDeniedException;
import org.jbpm.task.impl.factories.TaskFactory;
import org.jbpm.task.utils.OnErrorAction;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.internal.runtime.manager.Runtime;
import org.kie.internal.runtime.manager.RuntimeManager;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;
import org.kie.internal.task.api.model.ContentData;
import org.kie.internal.task.api.model.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@ApplicationScoped
@Transactional
public class ManagedLocalHTWorkItemHandler extends AbstractHTWorkItemHandler {

    private static final Logger logger = LoggerFactory.getLogger(ManagedLocalHTWorkItemHandler.class);
    private RuntimeManager runtimeManager;
    
    public RuntimeManager getRuntimeManager() {
        return runtimeManager;
    }

    public void setRuntimeManager(RuntimeManager runtimeManager) {
        this.runtimeManager = runtimeManager;
    }
   
    public ManagedLocalHTWorkItemHandler() {
    }
   
    @Override
    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
        
        Runtime runtime = runtimeManager.getRuntime(ProcessInstanceIdContext.get(workItem.getProcessInstanceId()));
        KieSession ksessionById = runtime.getKieSession();
        
        Task task = createTaskBasedOnWorkItemParams(ksessionById, workItem);
        TaskFactory.initializeTask(task);
        ContentData content = createTaskContentBasedOnWorkItemParams(ksessionById, workItem);
        try {
            long taskId = runtime.getTaskService().addTask(task, content);
            if (isAutoClaim(workItem, task)) {
                runtime.getTaskService().claim(taskId, (String) workItem.getParameter("SwimlaneActorId"));
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
            }
        } 
    }

    @Override
    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
        Runtime runtime = runtimeManager.getRuntime(ProcessInstanceIdContext.get(workItem.getProcessInstanceId()));
        Task task = runtime.getTaskService().getTaskByWorkItemId(workItem.getId());
        if (task != null) {
            try {
                runtime.getTaskService().exit(task.getId(), "Administrator");
            } catch (PermissionDeniedException e) {
                logger.info(e.getMessage());
            }
        }
        
    }
    
}
