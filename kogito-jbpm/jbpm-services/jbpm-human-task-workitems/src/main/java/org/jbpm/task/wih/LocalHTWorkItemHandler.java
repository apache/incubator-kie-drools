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
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.jboss.seam.transaction.Transactional;
import org.jbpm.shared.services.api.ServicesSessionManager;
import org.jbpm.shared.services.api.SessionManager;
import org.jbpm.task.annotations.External;
import org.jbpm.task.exception.PermissionDeniedException;
import org.jbpm.task.impl.TaskServiceEntryPointImpl;
import org.jbpm.task.impl.factories.TaskFactory;
import org.jbpm.task.utils.OnErrorAction;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.internal.task.api.TaskService;
import org.kie.internal.task.api.model.ContentData;
import org.kie.internal.task.api.model.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@ApplicationScoped
@Transactional
public class LocalHTWorkItemHandler extends AbstractHTWorkItemHandler {

    private static final Logger logger = LoggerFactory.getLogger(LocalHTWorkItemHandler.class);
    
    @Inject
    private TaskService taskService;

    @Inject @External
    private ExternalTaskEventListener listener;
    
    @Inject
    private Instance<SessionManager> sessionManagerInjected;
    
    private SessionManager sessionManager;

    private KieSession kieSessionLocal;
    
    public LocalHTWorkItemHandler() {
    }
    
    public void addSession(KieSession ksession){
        kieSessionLocal = ksession;
        addSession(ksession, null);
    }
    
    public void addSession(KieSession ksession, ClassLoader classLoader){
        if(listener != null){
            listener.addSession(ksession, classLoader);
        }
    }

    public void setSessionManager(ServicesSessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public void setTaskService(TaskService taskService) {
        this.taskService = taskService;
    }

    public void setTaskEventListener(ExternalTaskEventListener listener) {
        this.listener = listener;
        ((TaskServiceEntryPointImpl)taskService).registerTaskLifecycleEventListener(listener);
    }

    public TaskService getTaskService() {
        return taskService;
    }
    
    protected SessionManager getSessionManager() {
        if (this.sessionManager == null && sessionManagerInjected != null) {
            this.sessionManager = sessionManagerInjected.get();
        }
        
        return this.sessionManager;
    }

    @Override
    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
        KieSession ksessionById = null;
        if(getSessionManager() != null){
            int sessionId = getSessionManager().getSessionForProcessInstanceId(workItem.getProcessInstanceId());
            ksessionById = getSessionManager().getKsessionById(sessionId);
        }else{
            ksessionById = this.kieSessionLocal;
        }
        
        Task task = createTaskBasedOnWorkItemParams(ksessionById, workItem);
        TaskFactory.initializeTask(task);
        ContentData content = createTaskContentBasedOnWorkItemParams(ksessionById, workItem);
        try {
            long taskId = taskService.addTask(task, content);
            if (isAutoClaim(workItem, task)) {
                taskService.claim(taskId, (String) workItem.getParameter("SwimlaneActorId"));
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
        Task task = taskService.getTaskByWorkItemId(workItem.getId());
        if (task != null) {
            try {
                taskService.exit(task.getId(), "Administrator");
            } catch (PermissionDeniedException e) {
                logger.info(e.getMessage());
            }
        }
    }
    
}
