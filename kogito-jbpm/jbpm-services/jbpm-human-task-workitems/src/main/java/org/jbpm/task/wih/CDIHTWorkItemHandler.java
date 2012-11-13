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
import javax.inject.Inject;


import org.jbpm.task.utils.OnErrorAction;
import org.jbpm.task.Task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.kie.runtime.process.WorkItem;
import org.kie.runtime.process.WorkItemManager;

import org.jbpm.task.ContentData;
import org.jbpm.task.annotations.External;
import org.jbpm.task.api.TaskServiceEntryPoint;
import org.jbpm.task.exception.PermissionDeniedException;
import org.jbpm.task.impl.factories.TaskFactory;


@ApplicationScoped
public class CDIHTWorkItemHandler extends AbstractHTWorkItemHandler {

    private static final Logger logger = LoggerFactory.getLogger(CDIHTWorkItemHandler.class);
    private ClassLoader classLoader;
    @Inject
    private TaskServiceEntryPoint taskService;

    @Inject @External
    private ExternalTaskEventListener listener;
    
    public CDIHTWorkItemHandler() {
    }
    
    public void init(){
        listener.setClassLoader(classLoader);
        listener.setSession(session);
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public TaskServiceEntryPoint getTaskService() {
        return taskService;
    }

    @Override
    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
        Task task = createTaskBasedOnWorkItemParams(workItem);
        TaskFactory.initializeTask(task);
        ContentData content = createTaskContentBasedOnWorkItemParams(workItem);
        try {
            taskService.addTask(task, content);
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
