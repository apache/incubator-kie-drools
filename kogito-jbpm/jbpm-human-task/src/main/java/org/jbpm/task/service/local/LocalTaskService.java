/*
 * Copyright 2011 JBoss by Red Hat.
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
package org.jbpm.task.service.local;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.drools.runtime.process.WorkItemManager;
import org.jbpm.eventmessaging.EventKey;
import org.jbpm.eventmessaging.EventTriggerTransport;
import org.jbpm.eventmessaging.Payload;
import org.jbpm.task.Attachment;
import org.jbpm.task.Comment;
import org.jbpm.task.Content;
import org.jbpm.task.OrganizationalEntity;
import org.jbpm.task.Status;
import org.jbpm.task.Task;
import org.jbpm.task.query.TaskSummary;
import org.jbpm.task.service.ContentData;
import org.jbpm.task.service.FaultData;
import org.jbpm.task.service.Operation;
import org.jbpm.task.TaskService;
import org.jbpm.task.event.TaskCompletedEvent;
import org.jbpm.task.event.TaskFailedEvent;
import org.jbpm.task.event.TaskSkippedEvent;
import org.jbpm.task.service.TaskServiceSession;

/**
 *
 * @author salaboy
 */
public class LocalTaskService implements TaskService {

    private TaskServiceSession taskServiceSession;

    public LocalTaskService(TaskServiceSession taskServiceSession) {
        this.taskServiceSession = taskServiceSession;
    }

    public void activate(long taskId, String userId) {
        taskServiceSession.taskOperation(Operation.Activate, taskId, userId, null, null, null);
    }

    public void addAttachment(long taskId, Attachment attachment, Content content) {
        taskServiceSession.addAttachment(taskId, attachment, content);
    }

    public void addComment(long taskId, Comment comment) {
        taskServiceSession.addComment(taskId, comment);
    }

    public void addTask(Task task, ContentData content) {
        taskServiceSession.addTask(task, content);
    }

    public void claim(long taskId, String userId) {
        taskServiceSession.taskOperation(Operation.Claim, taskId, userId, null, null, null);
    }

    public void claim(long taskId, String userId, List<String> groupIds) {
        taskServiceSession.taskOperation(Operation.Claim, taskId, userId, null, null, groupIds);
    }

    public void complete(long taskId, String userId, ContentData outputData) {
        taskServiceSession.taskOperation(Operation.Complete, taskId, userId, null, outputData, null);
    }

    public boolean connect() {
        //do nothing
        return true;
    }

    public boolean connect(String address, int port) {
        //do nothing
        return true;
    }

    public void delegate(long taskId, String userId, String targetUserId) {
        taskServiceSession.taskOperation(Operation.Delegate, taskId, userId, targetUserId, null, null);
    }

    public void deleteAttachment(long taskId, long attachmentId, long contentId) {
        taskServiceSession.deleteAttachment(taskId, attachmentId, contentId);
    }

    public void deleteComment(long taskId, long commentId) {
        taskServiceSession.deleteComment(taskId, commentId);
    }

    public void deleteFault(long taskId, String userId) {
        taskServiceSession.deleteFault(taskId, userId);
    }

    public void deleteOutput(long taskId, String userId) {
        taskServiceSession.deleteOutput(taskId, userId);
    }

    public void disconnect() throws Exception {
        // do nothing 
    }

    public void fail(long taskId, String userId, FaultData faultData) {
        taskServiceSession.taskOperation(Operation.Fail, taskId, userId, null, faultData, null);
    }

    public void forward(long taskId, String userId, String targetEntityId) {
        taskServiceSession.taskOperation(Operation.Forward, taskId, userId, targetEntityId, null, null);
    }

    public Content getContent(long contentId) {
        return taskServiceSession.getContent(contentId);
    }

    public List<TaskSummary> getSubTasksAssignedAsPotentialOwner(long parentId, String userId, String language) {
        return taskServiceSession.getSubTasksAssignedAsPotentialOwner(parentId, userId, language);
    }

    public List<TaskSummary> getSubTasksByParent(long parentId) {
        return taskServiceSession.getSubTasksByParent(parentId, null);
    }

    public Task getTask(long taskId) {
        return taskServiceSession.getTask(taskId);
    }

    public Task getTaskByWorkItemId(long workItemId) {
        return taskServiceSession.getTaskByWorkItemId(workItemId);
    }

    public List<TaskSummary> getTasksAssignedAsBusinessAdministrator(String userId, String language) {
        return taskServiceSession.getTasksAssignedAsBusinessAdministrator(userId, language);
    }

    public List<TaskSummary> getTasksAssignedAsExcludedOwner(String userId, String language) {
        return taskServiceSession.getTasksAssignedAsExcludedOwner(userId, language);
    }

    public List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId, String language) {
        return taskServiceSession.getTasksAssignedAsPotentialOwner(userId, language);
    }

    public List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId, List<String> groupIds, String language) {
        return taskServiceSession.getTasksAssignedAsPotentialOwner(userId, groupIds, language);
    }

    public List<TaskSummary> getTasksAssignedAsRecipient(String userId, String language) {
        return taskServiceSession.getTasksAssignedAsRecipient(userId, language);
    }

    public List<TaskSummary> getTasksAssignedAsTaskInitiator(String userId, String language) {
        return taskServiceSession.getTasksAssignedAsTaskInitiator(userId, language);
    }

    public List<TaskSummary> getTasksAssignedAsTaskStakeholder(String userId, String language) {
        return taskServiceSession.getTasksAssignedAsTaskStakeholder(userId, language);
    }

    public List<TaskSummary> getTasksOwned(String userId, String language) {
        return taskServiceSession.getTasksOwned(userId, language);
    }

    public void nominate(long taskId, String userId, List<OrganizationalEntity> potentialOwners) {
        taskServiceSession.nominateTask(taskId, userId, potentialOwners);
    }

    public List<?> query(String qlString, Integer size, Integer offset) {
        return taskServiceSession.query(qlString, size, offset);
    }

    public void register(long taskId, String userId) {
        taskServiceSession.taskOperation(Operation.Register, taskId, userId, null, null, null);
    }

    public void registerForEvent(EventKey key, boolean remove, WorkItemManager manager) {
        SimpleEventTransport transport = new SimpleEventTransport(taskServiceSession, manager, remove);
        taskServiceSession.getService().getEventKeys().register(key, transport);
    }

    public void release(long taskId, String userId) {
        taskServiceSession.taskOperation(Operation.Release, taskId, userId, null, null, null);
    }

    public void remove(long taskId, String userId) {
        taskServiceSession.taskOperation(Operation.Remove, taskId, userId, null, null, null);
    }

    public void resume(long taskId, String userId) {
        taskServiceSession.taskOperation(Operation.Resume, taskId, userId, null, null, null);
    }

    public void setDocumentContent(long taskId, Content content) {
        taskServiceSession.setDocumentContent(taskId, content);
    }

    public void setFault(long taskId, String userId, FaultData fault) {
        taskServiceSession.setFault(taskId, userId, fault);
    }

    public void setOutput(long taskId, String userId, ContentData outputContentData) {
        taskServiceSession.setOutput(taskId, userId, outputContentData);
    }

    public void setPriority(long taskId, String userId, int priority) {
        taskServiceSession.setPriority(taskId, userId, priority);
    }

    public void skip(long taskId, String userId) {
        taskServiceSession.taskOperation(Operation.Skip, taskId, userId, null, null, null);
    }

    public void start(long taskId, String userId) {
        taskServiceSession.taskOperation(Operation.Start, taskId, userId, null, null, null);
    }

    public void stop(long taskId, String userId) {
        taskServiceSession.taskOperation(Operation.Stop, taskId, userId, null, null, null);
    }

    public void suspend(long taskId, String userId) {
        taskServiceSession.taskOperation(Operation.Suspend, taskId, userId, null, null, null);
    }

    private static class SimpleEventTransport implements EventTriggerTransport {

        private boolean remove;
        private WorkItemManager manager;
        private TaskServiceSession session;

        public SimpleEventTransport(TaskServiceSession session, WorkItemManager manager, boolean remove) {
            this.session = session;
            this.manager = manager;
            this.remove = remove;
        }

        public void trigger(Payload payload) {
            if (payload.get() instanceof TaskFailedEvent) {
                Task task = session.getTask(((TaskFailedEvent) payload.get()).getTaskId());
                manager.abortWorkItem(task.getTaskData().getWorkItemId());
                return;
            }
            if (payload.get() instanceof TaskSkippedEvent) {
                Task task = session.getTask(((TaskSkippedEvent) payload.get()).getTaskId());
                manager.abortWorkItem(task.getTaskData().getWorkItemId());
                return;
            }
            if (payload.get() instanceof TaskCompletedEvent) {
                Task task = session.getTask(((TaskCompletedEvent) payload.get()).getTaskId());

                task.getTaskData().setStatus(Status.Completed);
                String userId = task.getTaskData().getActualOwner().getId();
                Map<String, Object> results = new HashMap<String, Object>();
                results.put("ActorId", userId);
                long contentId = task.getTaskData().getOutputContentId();
                if (contentId != -1) {
                    Content content = session.getContent(contentId);
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
                        manager.completeWorkItem(task.getTaskData().getWorkItemId(), results);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                } else {
                    manager.completeWorkItem(task.getTaskData().getWorkItemId(), results);
                }

                return;
            }

        }

        public boolean isRemove() {
            return remove;
        }
    }
}
