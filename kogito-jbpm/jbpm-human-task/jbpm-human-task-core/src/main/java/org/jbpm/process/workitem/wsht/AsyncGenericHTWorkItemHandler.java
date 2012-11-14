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
package org.jbpm.process.workitem.wsht;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.kie.runtime.KnowledgeRuntime;
import org.kie.runtime.StatefulKnowledgeSession;
import org.kie.runtime.process.WorkItem;
import org.kie.runtime.process.WorkItemManager;
import org.jbpm.eventmessaging.EventResponseHandler;
import org.jbpm.eventmessaging.Payload;
import org.jbpm.task.AsyncTaskService;
import org.jbpm.task.Content;
import org.jbpm.task.Status;
import org.jbpm.task.Task;
import org.jbpm.task.event.TaskEventKey;
import org.jbpm.task.event.entity.TaskCompletedEvent;
import org.jbpm.task.event.entity.TaskEvent;
import org.jbpm.task.event.entity.TaskFailedEvent;
import org.jbpm.task.event.entity.TaskSkippedEvent;
import org.jbpm.task.service.ContentData;
import org.jbpm.task.service.TaskClientHandler;
import org.jbpm.task.service.TaskClientHandler.GetContentResponseHandler;
import org.jbpm.task.service.TaskClientHandler.GetTaskResponseHandler;
import org.jbpm.task.service.responsehandlers.AbstractBaseResponseHandler;
import org.jbpm.task.utils.ContentMarshallerHelper;
import org.jbpm.task.utils.OnErrorAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AsyncGenericHTWorkItemHandler extends AbstractHTWorkItemHandler {

    private static final Logger logger = LoggerFactory.getLogger(GenericHTWorkItemHandler.class);
    private AsyncTaskService client;
    private String ipAddress;
    private int port;
    private boolean local = false;
    private boolean connected = false;
    private ClassLoader classLoader;
    private boolean owningSessionOnly = false;

    public AsyncGenericHTWorkItemHandler(KnowledgeRuntime session, OnErrorAction action, ClassLoader classLoader) {
        super(session, action);
        this.classLoader = classLoader;
    }

    public AsyncGenericHTWorkItemHandler(KnowledgeRuntime session, OnErrorAction action) {
        super(session, action);
    }

    public AsyncGenericHTWorkItemHandler(KnowledgeRuntime session) {
        super(session);
    }
    
    public AsyncGenericHTWorkItemHandler(KnowledgeRuntime session, boolean owningSessionOnly) {
        super(session);
        this.owningSessionOnly = owningSessionOnly;
    }

    public AsyncTaskService getClient() {
        return client;
    }

    public void setClient(AsyncTaskService client) {
        this.client = client;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setLocal(boolean local) {
        this.local = local;
    }

    public boolean isConnected() {
        return connected;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public boolean isLocal() {
        return local;
    }

    public int getPort() {
        return port;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    private void registerTaskEvents() {
        TaskCompletedHandler eventResponseHandler = new TaskCompletedHandler(client, classLoader);
        TaskEventKey key = new TaskEventKey(TaskCompletedEvent.class, -1);
        client.registerForEvent(key, false, eventResponseHandler);
        eventHandlers.put(key, eventResponseHandler);
        key = new TaskEventKey(TaskFailedEvent.class, -1);
        client.registerForEvent(key, false, eventResponseHandler);
        eventHandlers.put(key, eventResponseHandler);
        key = new TaskEventKey(TaskSkippedEvent.class, -1);
        client.registerForEvent(key, false, eventResponseHandler);
        eventHandlers.put(key, eventResponseHandler);
    }

    public void connect() {
        if (client == null) {
            throw new IllegalStateException("You must set the Task Service Client to the work item to work");
        }
        if (ipAddress == null || ipAddress.equals("") || port <= 0) {
            throw new IllegalStateException("You must set the IP and Port to the work item to work");
        }
        if (client != null) {
            if (!connected) {
                connected = client.connect(ipAddress, port);
                if (!connected) {
                    throw new IllegalArgumentException("Could not connect task client");
                }
                registerTaskEvents();
            }
        }
    }

    public void dispose() throws Exception {
        for (TaskEventKey key : eventHandlers.keySet()) {
            client.unregisterForEvent(key);
        }
        eventHandlers.clear();
        if (client != null) {
            client.disconnect();
        }

    }

    @Override
    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
        Task task = createTaskBasedOnWorkItemParams(workItem);
        ContentData content = createTaskContentBasedOnWorkItemParams(workItem);
        connect();
        client.addTask(task, content, new TaskAddedHandler(workItem.getId()));


    }

    @Override
    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
        GetTaskResponseHandler abortTaskResponseHandler = new AbortTaskResponseHandler(client);
        client.getTaskByWorkItemId(workItem.getId(), abortTaskResponseHandler);
    }
    
    public boolean isOwningSessionOnly() {
        return owningSessionOnly;
    }

    public void setOwningSessionOnly(boolean owningSessionOnly) {
        this.owningSessionOnly = owningSessionOnly;
    }

    private class TaskAddedHandler extends AbstractBaseResponseHandler implements TaskClientHandler.AddTaskResponseHandler {

        private long workItemId;

        public TaskAddedHandler(long workItemId) {
            this.workItemId = workItemId;
        }

        public void execute(long taskId) {
        }

        @Override
        public synchronized void setError(RuntimeException error) {
            super.setError(error);

            if (action.equals(OnErrorAction.ABORT)) {
                session.getWorkItemManager().abortWorkItem(workItemId);

            } else if (action.equals(OnErrorAction.RETHROW)) {
                throw getError();

            } else if (action.equals(OnErrorAction.LOG)) {
                StringBuffer logMsg = new StringBuffer();
                logMsg.append(new Date() + ": Error when creating task on task server for work item id " + workItemId);
                logMsg.append(". Error reported by task server: " + getError().getMessage());
                logger.error(logMsg.toString(), getError());
            }
        }
    }

    private class TaskCompletedHandler extends AbstractBaseResponseHandler implements EventResponseHandler {

        private AsyncTaskService client;
        private ClassLoader classLoader;

        public TaskCompletedHandler(AsyncTaskService client, ClassLoader classLoader) {

            this.client = client;
            this.classLoader = classLoader;

        }

        public void execute(Payload payload) {
            TaskEvent event = (TaskEvent) payload.get();
            
            if (owningSessionOnly && (session instanceof StatefulKnowledgeSession)) {
                if (((StatefulKnowledgeSession) session).getId() != event.getSessionId()) {
                    return;
                }
            }
            
            long taskId = event.getTaskId();
            TaskClientHandler.GetTaskResponseHandler getTaskResponseHandler =
                    new GetCompletedTaskResponseHandler(client, classLoader);
            client.getTask(taskId, getTaskResponseHandler);
        }

        public boolean isRemove() {
            return false;
        }
    }

    private class GetCompletedTaskResponseHandler extends AbstractBaseResponseHandler implements GetTaskResponseHandler {

        private AsyncTaskService client;
        private ClassLoader classLoader;

        public GetCompletedTaskResponseHandler(AsyncTaskService client, ClassLoader classLoader) {
            this.client = client;
            this.classLoader = classLoader;
        }

        public void execute(Task task) {
            long workItemId = task.getTaskData().getWorkItemId();
            if (task.getTaskData().getStatus() == Status.Completed) {
                String userId = task.getTaskData().getActualOwner().getId();
                Map<String, Object> results = new HashMap<String, Object>();
                results.put("ActorId", userId);
                long contentId = task.getTaskData().getOutputContentId();
                if (contentId != -1) {
                    TaskClientHandler.GetContentResponseHandler getContentResponseHandler =
                            new GetResultContentResponseHandler(task, results, classLoader);
                    client.getContent(contentId, getContentResponseHandler);
                } else {
                    session.getWorkItemManager().completeWorkItem(workItemId, results);
                }
            } else {
                session.getWorkItemManager().abortWorkItem(workItemId);
            }
        }
    }

    private class GetResultContentResponseHandler extends AbstractBaseResponseHandler implements GetContentResponseHandler {

        private Task task;
        private Map<String, Object> results;
        private ClassLoader classLoader;

        public GetResultContentResponseHandler(Task task, Map<String, Object> results, ClassLoader classLoader) {
            this.task = task;
            this.results = results;
            this.classLoader = classLoader;
        }

        public void execute(Content content) {
            Object result = ContentMarshallerHelper.unmarshall(content.getContent(), session.getEnvironment(), classLoader);
            results.put("Result", result);
            if (result instanceof Map) {
                @SuppressWarnings("rawtypes")
                Map<?, ?> map = (Map) result;
                for (Map.Entry<?, ?> entry : map.entrySet()) {
                    if (entry.getKey() instanceof String) {
                        results.put((String) entry.getKey(), entry.getValue());
                    }
                }
            }
            session.getWorkItemManager().completeWorkItem(task.getTaskData().getWorkItemId(), results);
        }
    }

    private class AbortTaskResponseHandler extends AbstractBaseResponseHandler implements GetTaskResponseHandler {

        private AsyncTaskService client;

        public AbortTaskResponseHandler(AsyncTaskService client) {
            this.client = client;
        }

        public void execute(Task task) {
            if (task != null) {
                client.exit(task.getId(), "Administrator", null);
            }
        }
    }
}
