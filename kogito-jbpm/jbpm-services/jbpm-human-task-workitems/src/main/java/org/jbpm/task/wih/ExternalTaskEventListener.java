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
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.event.Reception;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;
import org.kie.runtime.KnowledgeRuntime;
import org.jbpm.task.Content;
import org.jbpm.task.Status;
import org.jbpm.task.Task;
import org.jbpm.task.annotations.External;
import org.jbpm.task.api.TaskServiceEntryPoint;
import org.jbpm.task.events.AfterTaskCompletedEvent;
import org.jbpm.task.events.AfterTaskFailedEvent;
import org.jbpm.task.events.AfterTaskSkippedEvent;
import org.jbpm.task.lifecycle.listeners.TaskLifeCycleEventListener;
import org.jbpm.task.utils.ContentMarshallerHelper;

/**
 *
 * @author salaboy
 */
@ApplicationScoped @External
public class ExternalTaskEventListener implements TaskLifeCycleEventListener {
    
    @Inject
    private TaskServiceEntryPoint taskService;
    
    private KnowledgeRuntime session;
    
    private ClassLoader classLoader;

    public ExternalTaskEventListener() {
    }

    public TaskServiceEntryPoint getTaskService() {
        return taskService;
    }

    public void setTaskService(TaskServiceEntryPoint taskService) {
        this.taskService = taskService;
    }

    public KnowledgeRuntime getSession() {
        return session;
    }

    public void setSession(KnowledgeRuntime session) {
        this.session = session;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public void processTaskState(Task task) {
        long workItemId = task.getTaskData().getWorkItemId();
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

    public void afterTaskSkippedEvent(@Observes(notifyObserver= Reception.IF_EXISTS) @AfterTaskSkippedEvent Task task) {
        processTaskState(task);
    }

    public void afterTaskStartedEvent(Task ti) {
        // DO NOTHING
    }

    public void afterTaskStoppedEvent(Task ti) {
        // DO NOTHING
    }

    public void afterTaskCompletedEvent(@Observes(notifyObserver= Reception.IF_EXISTS) @AfterTaskCompletedEvent Task task) {
         processTaskState(task);
    }

    public void afterTaskFailedEvent(@Observes(notifyObserver= Reception.IF_EXISTS) @AfterTaskFailedEvent Task task) {
        processTaskState(task);
    }

    public void afterTaskAddedEvent(Task ti) {
        // DO NOTHING
    }

    public void afterTaskExitedEvent(Task ti) {
       // DO NOTHING
    }
}
