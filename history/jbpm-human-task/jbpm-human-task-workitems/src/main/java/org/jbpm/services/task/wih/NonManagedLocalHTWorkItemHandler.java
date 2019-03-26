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

import org.jbpm.services.task.exception.PermissionDeniedException;
import org.jbpm.services.task.utils.OnErrorAction;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.api.task.TaskLifeCycleEventListener;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.Group;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.Task;
import org.kie.internal.task.api.EventService;
import org.kie.internal.task.api.InternalTaskService;
import org.kie.internal.task.api.model.ContentData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * LocalHumanTaskHandler that is intended to be used when RuntimeManager is not used, most likely
 * in backward compatible cases where simply providing:
 * <ul>
 *  <li><code>KieSession</code></li>
 *  <li><code>TaskService</code></li>
 * </ul>
 * is usual case. It will ensure that task listener will be registered on task service.
 * <br/>
 * Important notes are that this handler instance should have independent <code>TaskService</code>
 * instances as it's <code>close</code> method will clear task listeners on the task service.
 * <br/>
 * This is not suited for CDI environments and thus it's Veto'ed.
 * <br/>
 * Can be bootstrapped in two ways:
 * <ul>
 *  <li/>by constructor and providing both KieSession and TaskService</li>
 *  <li/>by no arg constructor and then use setters for KieSession and TaskService, 
 *  after using setters call to init method is required</li>
 * </ul>
 *
 */
public class NonManagedLocalHTWorkItemHandler extends AbstractHTWorkItemHandler {

    private static final Logger logger = LoggerFactory.getLogger(NonManagedLocalHTWorkItemHandler.class);
    private KieSession ksession;
    private TaskService taskService;
    private TaskLifeCycleEventListener listener;
    private boolean initialized = false;
   
    public NonManagedLocalHTWorkItemHandler() {
    }
    
    public NonManagedLocalHTWorkItemHandler(KieSession ksession, TaskService taskService) {
        this.ksession = ksession;
        this.taskService = taskService;
        init();
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void init() {
        if(!initialized) {
            
            listener = new NonManagedTaskEventListener(this.ksession, this.taskService);
            if (taskService instanceof EventService) {
                ((EventService)taskService).registerTaskEventListener(listener);
            }
            initialized = true;
        }
    }
    
    @SuppressWarnings({ "rawtypes" })
    public void close() {
        if (taskService instanceof EventService) {
            ((EventService)taskService).clearTaskEventListeners();
        }
    }
   
    @Override
    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
        
        Task task = createTaskBasedOnWorkItemParams(ksession, workItem);
        ContentData content = createTaskContentBasedOnWorkItemParams(ksession, workItem);
        try {
            long taskId = ((InternalTaskService) taskService).addTask(task, content);
            if (isAutoClaim(ksession, workItem, task)) {
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
                taskService.exit(task.getId(), adminUser);
            } catch (PermissionDeniedException e) {
                logger.info(e.getMessage());
            }
        }
        
    }

    public KieSession getKsession() {
        return ksession;
    }

    public void setKsession(KieSession ksession) {
        this.ksession = ksession;
    }

    public TaskService getTaskService() {
        return taskService;
    }

    public void setTaskService(TaskService taskService) {
        this.taskService = taskService;
    }
}
