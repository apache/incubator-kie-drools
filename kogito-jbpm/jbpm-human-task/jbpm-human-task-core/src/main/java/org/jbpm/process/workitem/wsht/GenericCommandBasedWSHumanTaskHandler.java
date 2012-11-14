/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.jbpm.process.workitem.wsht;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kie.runtime.KnowledgeRuntime;
import org.kie.runtime.StatefulKnowledgeSession;
import org.kie.runtime.process.WorkItem;
import org.kie.runtime.process.WorkItemHandler;
import org.kie.runtime.process.WorkItemManager;
import org.jbpm.eventmessaging.EventResponseHandler;
import org.jbpm.eventmessaging.Payload;
import org.jbpm.task.AccessType;
import org.jbpm.task.Content;
import org.jbpm.task.I18NText;
import org.jbpm.task.OrganizationalEntity;
import org.jbpm.task.PeopleAssignments;
import org.jbpm.task.Status;
import org.jbpm.task.SubTasksStrategy;
import org.jbpm.task.SubTasksStrategyFactory;
import org.jbpm.task.Task;
import org.jbpm.task.TaskData;
import org.jbpm.task.event.TaskEventKey;
import org.jbpm.task.event.entity.TaskCompletedEvent;
import org.jbpm.task.event.entity.TaskEvent;
import org.jbpm.task.event.entity.TaskFailedEvent;
import org.jbpm.task.event.entity.TaskSkippedEvent;
import org.jbpm.task.service.ContentData;
import org.jbpm.task.service.TaskClient;
import org.jbpm.task.service.TaskClientHandler.AddTaskResponseHandler;
import org.jbpm.task.service.TaskClientHandler.GetContentResponseHandler;
import org.jbpm.task.service.TaskClientHandler.GetTaskResponseHandler;
import org.jbpm.task.service.responsehandlers.AbstractBaseResponseHandler;
import org.jbpm.task.utils.OnErrorAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public class GenericCommandBasedWSHumanTaskHandler implements WorkItemHandler {

    private static final Logger logger = LoggerFactory.getLogger(GenericCommandBasedWSHumanTaskHandler.class);
    private String ipAddress;
    private int port;
    private TaskClient client;
    private KnowledgeRuntime session;
    private OnErrorAction action;

    public GenericCommandBasedWSHumanTaskHandler() {
        this.session = null;
        this.action = OnErrorAction.LOG;
    }

    public GenericCommandBasedWSHumanTaskHandler(KnowledgeRuntime session) {
        this.session = session;
        this.action = OnErrorAction.LOG;
    }

    public GenericCommandBasedWSHumanTaskHandler(KnowledgeRuntime session, OnErrorAction action) {
        this.session = session;
        this.action = action;
    }

    public void setSession(KnowledgeRuntime session) {
        this.session = session;
    }

    public void setConnection(String ipAddress, int port) {
        this.ipAddress = ipAddress;
        this.port = port;
    }

    public void setClient(TaskClient client) {
        this.client = client;
    }

    

    private void registerTaskEventHandlers() {
        TaskEventKey key = new TaskEventKey(TaskCompletedEvent.class, -1);
        TaskCompletedHandler eventResponseHandler = new TaskCompletedHandler();
        client.registerForEvent(key, false, eventResponseHandler);
        key = new TaskEventKey(TaskFailedEvent.class, -1);
        client.registerForEvent(key, false, eventResponseHandler);
        key = new TaskEventKey(TaskSkippedEvent.class, -1);
        client.registerForEvent(key, false, eventResponseHandler);
    }

    public void setAction(OnErrorAction action) {
        this.action = action;
    }

    public void connect() {
        if (client == null) {
            throw new IllegalStateException("You must provide a client connector for the Client using the setClient() method");
        }
        if(ipAddress == null || ipAddress.equals("") || port == 0){
            throw new IllegalStateException("You must provide connection settings such as ip and port for the Client using the setConnection() method");
        }
        boolean connected = client.connect(ipAddress, port);
        if (!connected) {
            throw new IllegalArgumentException("Could not connect task client");
        }
        registerTaskEventHandlers();


    }

    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
        connect();
        Task task = new Task();
        String locale = (String) workItem.getParameter("Locale");
        if (locale == null) {
            locale = "en-UK";
        }
        String taskName = (String) workItem.getParameter("TaskName");
        if (taskName != null) {
            List<I18NText> names = new ArrayList<I18NText>();
            names.add(new I18NText(locale, taskName));
            task.setNames(names);
        }
        String comment = (String) workItem.getParameter("Comment");
        if (comment == null) {
            comment = "";
        }
        List<I18NText> descriptions = new ArrayList<I18NText>();
        descriptions.add(new I18NText(locale, comment));
        task.setDescriptions(descriptions);
        List<I18NText> subjects = new ArrayList<I18NText>();
        subjects.add(new I18NText(locale, comment));
        task.setSubjects(subjects);

        String priorityString = (String) workItem.getParameter("Priority");
        int priority = 0;
        if (priorityString != null) {
            try {
                priority = new Integer(priorityString);
            } catch (NumberFormatException e) {
                // do nothing
            }
        }
        task.setPriority(priority);

        TaskData taskData = new TaskData();
        taskData.setWorkItemId(workItem.getId());
        taskData.setProcessInstanceId(workItem.getProcessInstanceId());
        if (session != null && session.getProcessInstance(workItem.getProcessInstanceId()) != null) {
            taskData.setProcessId(session.getProcessInstance(workItem.getProcessInstanceId()).getProcess().getId());
        }
        if (session != null && (session instanceof StatefulKnowledgeSession)) {
            taskData.setProcessSessionId(((StatefulKnowledgeSession) session).getId());
        }
        taskData.setSkipable(!"false".equals(workItem.getParameter("Skippable")));
        //Sub Task Data
        Long parentId = (Long) workItem.getParameter("ParentId");
        if (parentId != null) {
            taskData.setParentId(parentId);
        }

        String subTaskStrategiesCommaSeparated = (String) workItem.getParameter("SubTaskStrategies");
        if (subTaskStrategiesCommaSeparated != null && !subTaskStrategiesCommaSeparated.equals("")) {
            String[] subTaskStrategies = subTaskStrategiesCommaSeparated.split(",");
            List<SubTasksStrategy> strategies = new ArrayList<SubTasksStrategy>();
            for (String subTaskStrategyString : subTaskStrategies) {
                SubTasksStrategy subTaskStrategy = SubTasksStrategyFactory.newStrategy(subTaskStrategyString);
                strategies.add(subTaskStrategy);
            }
            task.setSubTaskStrategies(strategies);
        }

        task.setTaskData(taskData);

        PeopleAssignmentHelper peopleAssignmentHelper = new PeopleAssignmentHelper();
        peopleAssignmentHelper.handlePeopleAssignments(workItem, task, taskData);
        
        ContentData content = null;
        Object contentObject = workItem.getParameter("Content");
        
        if (contentObject != null) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out;
            try {
                out = new ObjectOutputStream(bos);
                out.writeObject(contentObject);
                out.close();
                content = new ContentData();
                content.setContent(bos.toByteArray());
                content.setAccessType(AccessType.Inline);
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        } // If the content is not set we will automatically copy all the input objects into 
        // the task content
        else {
            contentObject = workItem.getParameters();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out;
            try {
                out = new ObjectOutputStream(bos);
                out.writeObject(contentObject);
                out.close();
                content = new ContentData();
                content.setContent(bos.toByteArray());
                content.setAccessType(AccessType.Inline);
                content.setType("java.util.map");
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
        
        PeopleAssignments peopleAssignments = task.getPeopleAssignments();
        List<OrganizationalEntity> businessAdministrators = peopleAssignments.getBusinessAdministrators();
        
        task.setDeadlines(HumanTaskHandlerHelper.setDeadlines(workItem, businessAdministrators, session.getEnvironment()));

        client.addTask(task, content, new TaskAddedHandler(workItem.getId()));

    }

    public void dispose() throws Exception {
        if (client != null) {
            client.disconnect();
        }
    }

    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
        GetTaskResponseHandler abortTaskResponseHandler = new AbortTaskResponseHandler();
        client.getTaskByWorkItemId(workItem.getId(), abortTaskResponseHandler);
    }

    private class TaskAddedHandler extends AbstractBaseResponseHandler implements AddTaskResponseHandler {

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

        public void execute(Payload payload) {
            TaskEvent event = (TaskEvent) payload.get();
            long taskId = event.getTaskId();
            GetTaskResponseHandler getTaskResponseHandler =
                    new GetCompletedTaskResponseHandler();
            client.getTask(taskId, getTaskResponseHandler);
        }

        public boolean isRemove() {
            return false;
        }
    }

    private class GetCompletedTaskResponseHandler extends AbstractBaseResponseHandler implements GetTaskResponseHandler {

        public void execute(Task task) {
            long workItemId = task.getTaskData().getWorkItemId();
            if (task.getTaskData().getStatus() == Status.Completed) {
                String userId = task.getTaskData().getActualOwner().getId();
                Map<String, Object> results = new HashMap<String, Object>();
                results.put("ActorId", userId);
                long contentId = task.getTaskData().getOutputContentId();
                if (contentId != -1) {
                    GetContentResponseHandler getContentResponseHandler =
                            new GetResultContentResponseHandler(task, results);
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

        public GetResultContentResponseHandler(Task task, Map<String, Object> results) {
            this.task = task;
            this.results = results;
        }

        public void execute(Content content) {
            ByteArrayInputStream bis = new ByteArrayInputStream(content.getContent());
            ObjectInputStream in;
            try {
                in = new ObjectInputStream(bis);
                Object result = in.readObject();
                in.close();
                results.put("Result", result);
                if (result instanceof Map) {
                    Map<?, ?> map = (Map) result;
                    for (Map.Entry<?, ?> entry : map.entrySet()) {
                        if (entry.getKey() instanceof String) {
                            results.put((String) entry.getKey(), entry.getValue());
                        }
                    }
                }
                session.getWorkItemManager().completeWorkItem(task.getTaskData().getWorkItemId(), results);
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            } catch (ClassNotFoundException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    private class AbortTaskResponseHandler extends AbstractBaseResponseHandler implements GetTaskResponseHandler {

        public void execute(Task task) {
            if (task != null) {
                client.exit(task.getId(), "Administrator", null);
            }
        }
    }
}
