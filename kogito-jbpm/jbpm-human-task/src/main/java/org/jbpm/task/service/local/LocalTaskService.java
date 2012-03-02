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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

import org.jbpm.eventmessaging.EventKey;
import org.jbpm.eventmessaging.EventResponseHandler;
import org.jbpm.eventmessaging.EventTriggerTransport;
import org.jbpm.eventmessaging.Payload;
import org.jbpm.task.AccessType;
import org.jbpm.task.Attachment;
import org.jbpm.task.Comment;
import org.jbpm.task.Content;
import org.jbpm.task.OrganizationalEntity;
import org.jbpm.task.Task;
import org.jbpm.task.TaskService;
import org.jbpm.task.query.TaskSummary;
import org.jbpm.task.service.ContentData;
import org.jbpm.task.service.FaultData;
import org.jbpm.task.service.Operation;
import org.jbpm.task.service.persistence.TaskServiceSession;

/**
 *
 * *
 */
public class LocalTaskService implements TaskService {

    private org.jbpm.task.service.TaskService service;

    public LocalTaskService(org.jbpm.task.service.TaskService taskService) {
        this.service = taskService;
    }

    private TaskServiceSession getSession() { 
        return service.createSession();
    }
    
    public void activate(long taskId, String userId) {
        getSession().taskOperation(Operation.Activate, taskId, userId, null, null, null);
    }

    public void addAttachment(long taskId, Attachment attachment, Content content) {
        getSession().addAttachment(taskId, attachment, content);
    }

    public void addComment(long taskId, Comment comment) {
        getSession().addComment(taskId, comment);
    }

    public void addTask(Task task, ContentData content) {
        getSession().addTask(task, content);
    }

    public void claim(long taskId, String userId) {
        getSession().taskOperation(Operation.Claim, taskId, userId, null, null, null);
    }

    public void claim(long taskId, String userId, List<String> groupIds) {
        getSession().taskOperation(Operation.Claim, taskId, userId, null, null, groupIds);
    }

    public void complete(long taskId, String userId, ContentData outputData) {
        getSession().taskOperation(Operation.Complete, taskId, userId, null, outputData, null);
    }

    public void completeWithResults(long taskId, String userId, Object results) {
        ContentData contentData = null;
        if (results != null) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out;
            try {
                out = new ObjectOutputStream(bos);
                out.writeObject(results);
                out.close();
                contentData = new ContentData();
                contentData.setContent(bos.toByteArray());
                contentData.setAccessType(AccessType.Inline);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        complete(taskId, userId, contentData);
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
        getSession().taskOperation(Operation.Delegate, taskId, userId, targetUserId, null, null);
    }

    public void deleteAttachment(long taskId, long attachmentId, long contentId) {
        getSession().deleteAttachment(taskId, attachmentId, contentId);
    }

    public void deleteComment(long taskId, long commentId) {
        getSession().deleteComment(taskId, commentId);
    }

    public void deleteFault(long taskId, String userId) {
        getSession().deleteFault(taskId, userId);
    }

    public void deleteOutput(long taskId, String userId) {
        getSession().deleteOutput(taskId, userId);
    }

    public void disconnect() throws Exception {
        // do nothing 
    }

    public void fail(long taskId, String userId, FaultData faultData) {
        getSession().taskOperation(Operation.Fail, taskId, userId, null, faultData, null);
    }

    public void forward(long taskId, String userId, String targetEntityId) {
        getSession().taskOperation(Operation.Forward, taskId, userId, targetEntityId, null, null);
    }

    public Content getContent(long contentId) {
        return getSession().getContent(contentId);
    }

    public List<TaskSummary> getSubTasksAssignedAsPotentialOwner(long parentId, String userId, String language) {
        return getSession().getSubTasksAssignedAsPotentialOwner(parentId, userId, language);
    }

    public List<TaskSummary> getSubTasksByParent(long parentId) {
        return getSession().getSubTasksByParent(parentId, null);
    }

    public Task getTask(long taskId) {
        return getSession().getTask(taskId);
    }

    public Task getTaskByWorkItemId(long workItemId) {
        return getSession().getTaskByWorkItemId(workItemId);
    }

    public List<TaskSummary> getTasksAssignedAsBusinessAdministrator(String userId, String language) {
        return getSession().getTasksAssignedAsBusinessAdministrator(userId, language);
    }

    public List<TaskSummary> getTasksAssignedAsExcludedOwner(String userId, String language) {
        return getSession().getTasksAssignedAsExcludedOwner(userId, language);
    }

    public List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId, String language) {
        return getSession().getTasksAssignedAsPotentialOwner(userId, language);
    }

    public List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId, List<String> groupIds, String language) {
        return getSession().getTasksAssignedAsPotentialOwner(userId, groupIds, language);
    }

    public List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId, List<String> groupIds, String language, int firstResult, int maxResult) {
        return getSession().getTasksAssignedAsPotentialOwner(userId, groupIds, language, firstResult, maxResult);
    }

    public List<TaskSummary> getTasksAssignedAsRecipient(String userId, String language) {
        return getSession().getTasksAssignedAsRecipient(userId, language);
    }

    public List<TaskSummary> getTasksAssignedAsTaskInitiator(String userId, String language) {
        return getSession().getTasksAssignedAsTaskInitiator(userId, language);
    }

    public List<TaskSummary> getTasksAssignedAsTaskStakeholder(String userId, String language) {
        return getSession().getTasksAssignedAsTaskStakeholder(userId, language);
    }

    public List<TaskSummary> getTasksOwned(String userId, String language) {
        return getSession().getTasksOwned(userId, language);
    }

    public void nominate(long taskId, String userId, List<OrganizationalEntity> potentialOwners) {
        getSession().nominateTask(taskId, userId, potentialOwners);
    }

    public List<?> query(String qlString, Integer size, Integer offset) {
        return getSession().query(qlString, size, offset);
    }

    public void register(long taskId, String userId) {
        getSession().taskOperation(Operation.Register, taskId, userId, null, null, null);
    }

    public void registerForEvent(EventKey key, boolean remove, EventResponseHandler responseHandler) {
        SimpleEventTransport transport = new SimpleEventTransport(getSession(), responseHandler, remove);
        service.getEventKeys().register(key, transport);
    }

    public void release(long taskId, String userId) {
        getSession().taskOperation(Operation.Release, taskId, userId, null, null, null);
    }

    public void remove(long taskId, String userId) {
        getSession().taskOperation(Operation.Remove, taskId, userId, null, null, null);
    }

    public void resume(long taskId, String userId) {
        getSession().taskOperation(Operation.Resume, taskId, userId, null, null, null);
    }

    public void setDocumentContent(long taskId, Content content) {
        getSession().setDocumentContent(taskId, content);
    }

    public void setFault(long taskId, String userId, FaultData fault) {
        getSession().setFault(taskId, userId, fault);
    }

    public void setOutput(long taskId, String userId, ContentData outputContentData) {
        getSession().setOutput(taskId, userId, outputContentData);
    }

    public void setPriority(long taskId, String userId, int priority) {
        getSession().setPriority(taskId, userId, priority);
    }

    public void skip(long taskId, String userId) {
        getSession().taskOperation(Operation.Skip, taskId, userId, null, null, null);
    }

    public void start(long taskId, String userId) {
        getSession().taskOperation(Operation.Start, taskId, userId, null, null, null);
    }

    public void stop(long taskId, String userId) {
        getSession().taskOperation(Operation.Stop, taskId, userId, null, null, null);
    }

    public void suspend(long taskId, String userId) {
        getSession().taskOperation(Operation.Suspend, taskId, userId, null, null, null);
    }

    private static class SimpleEventTransport implements EventTriggerTransport {

        private boolean remove;
        private EventResponseHandler responseHandler;

        public SimpleEventTransport(TaskServiceSession session, EventResponseHandler responseHandler, boolean remove) {
            this.responseHandler = responseHandler;
            this.remove = remove;
        }

        public void trigger(Payload payload) {
            responseHandler.execute(payload);
        }

        public boolean isRemove() {
            return remove;
        }
    }
    
}
