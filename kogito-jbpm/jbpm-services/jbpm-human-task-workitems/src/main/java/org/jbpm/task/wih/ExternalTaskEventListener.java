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

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.event.Reception;
import javax.inject.Inject;

import org.jbpm.shared.services.impl.events.JbpmServicesEventListener;
import org.jbpm.task.annotations.External;
import org.jbpm.task.events.AfterTaskCompletedEvent;
import org.jbpm.task.events.AfterTaskFailedEvent;
import org.jbpm.task.events.AfterTaskSkippedEvent;
import org.jbpm.task.lifecycle.listeners.TaskLifeCycleEventListener;
import org.jbpm.task.utils.ContentMarshallerHelper;
import org.kie.api.runtime.KieSession;
import org.kie.internal.task.api.TaskService;
import org.kie.internal.task.api.model.Content;
import org.kie.internal.task.api.model.Status;
import org.kie.internal.task.api.model.Task;


/**
 *
 * @author salaboy
 */
@ApplicationScoped
@External
public class ExternalTaskEventListener extends JbpmServicesEventListener<Task>  implements TaskLifeCycleEventListener {

    @Inject
    private TaskService taskService;
    
    @Inject
    private Logger logger;
    
    private Map<Integer, KieSession> kruntimes = new HashMap<Integer,KieSession>();
    private Map<Integer, ClassLoader> classLoaders = new HashMap<Integer,ClassLoader>();
    

    public ExternalTaskEventListener() {
    }
    

    public TaskService getTaskService() {
        return taskService;
    }

    public void setTaskService(TaskService taskService) {
        this.taskService = taskService;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }


    public void addSession(KieSession session) {
        addSession(session, null);
    }
     
    public void addSession(KieSession session, ClassLoader classLoader) {
        kruntimes.put(session.getId(), session);
        classLoaders.put(session.getId(), classLoader);
    }


    public void processTaskState(Task task) {

        long workItemId = task.getTaskData().getWorkItemId();
        int processSessionId = task.getTaskData().getProcessSessionId();
        KieSession session = kruntimes.get(processSessionId);
        ClassLoader classLoader = classLoaders.get(processSessionId);
        if (task.getTaskData().getStatus() == Status.Completed) {
            String userId = task.getTaskData().getActualOwner().getId();
            Map<String, Object> results = new HashMap<String, Object>();
            results.put("ActorId", userId);
            long contentId = task.getTaskData().getOutputContentId();
            if (contentId != -1) {
                Content content = taskService.getContentById(contentId);
                Object result = ContentMarshallerHelper.unmarshall(content.getContent(), session.getEnvironment(), classLoader);
                results.put("Result", result);
                if (result instanceof Map) {
                    Map<?, ?> map = (Map<?, ?>) result;
                    for (Map.Entry<?, ?> entry : map.entrySet()) {
                        if (entry.getKey() instanceof String) {
                            results.put((String) entry.getKey(), entry.getValue());
                        }
                    }
                }

                session.getWorkItemManager().completeWorkItem(task.getTaskData().getWorkItemId(), results);
            } else {
                session.getWorkItemManager().completeWorkItem(workItemId, results);
            }
        } else {
            session.getWorkItemManager().abortWorkItem(workItemId);
        }
    }

    public void afterTaskActivatedEvent(Task ti) {
        // DO NOTHING
    }

    public void afterTaskClaimedEvent(Task ti) {
        // DO NOTHING
    }

    public void afterTaskSkippedEvent(@Observes(notifyObserver = Reception.IF_EXISTS) @AfterTaskSkippedEvent Task task) {
        processTaskState(task);
    }

    public void afterTaskStartedEvent(Task ti) {
        // DO NOTHING
    }

    public void afterTaskStoppedEvent(Task ti) {
        // DO NOTHING
    }

    public void afterTaskCompletedEvent(@Observes(notifyObserver = Reception.IF_EXISTS) @AfterTaskCompletedEvent Task task) {
        KieSession session = kruntimes.get(task.getTaskData().getProcessSessionId());
        if (session != null) {
            logger.log(Level.FINE, ">> I''ve recieved an event for a known session ({0})", task.getTaskData().getProcessSessionId());
            processTaskState(task);
        } else {
            logger.log(Level.SEVERE, "EE: I''ve recieved an event but the session is not known by this handler ( {0})", task.getTaskData().getProcessSessionId());
            
        }
    }

    public void afterTaskFailedEvent(@Observes(notifyObserver = Reception.IF_EXISTS) @AfterTaskFailedEvent Task task) {
        processTaskState(task);
    }

    public void afterTaskAddedEvent(Task ti) {
        
        // DO NOTHING
    }

    public void afterTaskExitedEvent(Task ti) {
        // DO NOTHING
    }
}
