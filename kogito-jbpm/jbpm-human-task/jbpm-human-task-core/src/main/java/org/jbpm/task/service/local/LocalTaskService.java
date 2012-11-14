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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.kie.runtime.Environment;

import org.jbpm.eventmessaging.EventKey;
import org.jbpm.eventmessaging.EventResponseHandler;
import org.jbpm.eventmessaging.EventTriggerTransport;
import org.jbpm.eventmessaging.Payload;
import org.jbpm.task.Attachment;
import org.jbpm.task.Comment;
import org.jbpm.task.Content;
import org.jbpm.task.OrganizationalEntity;
import org.jbpm.task.Status;
import org.jbpm.task.Task;
import org.jbpm.task.TaskService;
import org.jbpm.task.event.TaskEventListener;
import org.jbpm.task.query.TaskSummary;
import org.jbpm.task.service.ContentData;
import org.jbpm.task.service.FaultData;
import org.jbpm.task.service.Operation;
import org.jbpm.task.service.TaskServiceSession;
import org.jbpm.task.utils.ContentMarshallerHelper;

/**
 *
 *
 */
public class LocalTaskService implements TaskService {

    private org.jbpm.task.service.TaskService service;
    private TaskServiceSession session;
    private Environment environment;

    public LocalTaskService(org.jbpm.task.service.TaskService taskService) {
        this.service = taskService;
        this.session = service.createSession();
    }

    public void activate(long taskId, String userId) {
        session.taskOperation(Operation.Activate, taskId, userId, null, null, null);
    }

    public void addAttachment(long taskId, Attachment attachment, Content content) {
        session.addAttachment(taskId, attachment, content);
    }

    public void addComment(long taskId, Comment comment) {
        session.addComment(taskId, comment);
    }

    public void addTask(Task task, ContentData content) {
        session.addTask(task, content);
    }

    public void claim(long taskId, String userId) {
        session.taskOperation(Operation.Claim, taskId, userId, null, null, null);
    }

    @Deprecated
    public void claim(long taskId, String userId, List<String> groupIds) {
        session.taskOperation(Operation.Claim, taskId, userId, null, null, groupIds);
    }

    public void complete(long taskId, String userId, ContentData outputData) {
        session.taskOperation(Operation.Complete, taskId, userId, null, outputData, null);
    }

    public void completeWithResults(long taskId, String userId, Object results) {
        ContentData contentData = null;
        if (results != null) {
            contentData = ContentMarshallerHelper.marshal(results, this.environment);
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
        session.taskOperation(Operation.Delegate, taskId, userId, targetUserId, null, null);
    }

    public void deleteAttachment(long taskId, long attachmentId, long contentId) {
        session.deleteAttachment(taskId, attachmentId, contentId);
    }

    public void deleteComment(long taskId, long commentId) {
        session.deleteComment(taskId, commentId);
    }

    public void deleteFault(long taskId, String userId) {
        session.deleteFault(taskId, userId);
    }

    public void deleteOutput(long taskId, String userId) {
        session.deleteOutput(taskId, userId);
    }

    public void disconnect() throws Exception {
        // do nothing 
    }

    public void dispose() {
        session.dispose();
    }

    public void exit(long taskId, String userId) {
        session.taskOperation(Operation.Exit, taskId, userId, null, null, null);
    }

    public void fail(long taskId, String userId, FaultData faultData) {
        session.taskOperation(Operation.Fail, taskId, userId, null, faultData, null);
    }

    public void forward(long taskId, String userId, String targetEntityId) {
        session.taskOperation(Operation.Forward, taskId, userId, targetEntityId, null, null);
    }

    public Content getContent(long contentId) {
        return session.getContent(contentId);
    }

    public List<TaskSummary> getSubTasksAssignedAsPotentialOwner(long parentId, String userId, String language) {
        return session.getSubTasksAssignedAsPotentialOwner(parentId, userId, language);
    }

    public List<TaskSummary> getSubTasksByParent(long parentId) {
        return session.getSubTasksByParent(parentId, null);
    }

    public Task getTask(long taskId) {
        return session.getTask(taskId);
    }

    public Task getTaskByWorkItemId(long workItemId) {
        return session.getTaskByWorkItemId(workItemId);
    }

    public List<TaskSummary> getTasksAssignedAsBusinessAdministrator(String userId, String language) {
        return session.getTasksAssignedAsBusinessAdministrator(userId, language);
    }

    public List<TaskSummary> getTasksAssignedAsExcludedOwner(String userId, String language) {
        return session.getTasksAssignedAsExcludedOwner(userId, language);
    }

    public List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId, String language) {
        return session.getTasksAssignedAsPotentialOwner(userId, language);
    }

    public List<TaskSummary> getTasksAssignedAsPotentialOwnerByStatus(String userId, List<Status> status, String language) {
        return session.getTasksAssignedAsPotentialOwnerByStatus(userId, status, language);
    }
    @Deprecated
    public List<TaskSummary> getTasksAssignedAsPotentialOwnerByStatusByGroup(String userId, List<String> groupIds, List<Status> status, String language) {
        return session.getTasksAssignedAsPotentialOwnerByStatusByGroup(userId, groupIds, status, language);
    }
    @Deprecated
    public List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId, List<String> groupIds, String language) {
        return session.getTasksAssignedAsPotentialOwner(userId, groupIds, language);
    }
    @Deprecated
    public List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId, List<String> groupIds, String language, int firstResult, int maxResult) {
        return session.getTasksAssignedAsPotentialOwner(userId, groupIds, language, firstResult, maxResult);
    }

    public List<TaskSummary> getTasksAssignedAsRecipient(String userId, String language) {
        return session.getTasksAssignedAsRecipient(userId, language);
    }

    public List<TaskSummary> getTasksAssignedAsTaskInitiator(String userId, String language) {
        return session.getTasksAssignedAsTaskInitiator(userId, language);
    }

    public List<TaskSummary> getTasksAssignedAsTaskStakeholder(String userId, String language) {
        return session.getTasksAssignedAsTaskStakeholder(userId, language);
    }

    public List<TaskSummary> getTasksByStatusByProcessId(long processInstanceId, List<Status> status, String language) {
        return session.getTasksByStatusByProcessId(processInstanceId, status, language);
    }

    public List<TaskSummary> getTasksByStatusByProcessIdByTaskName(long processInstanceId, List<Status> status, String taskName, String language) {
        return session.getTasksByStatusByProcessIdByTaskName(processInstanceId, status, taskName, language);
    }

    public List<TaskSummary> getTasksOwned(String userId, String language) {
        return session.getTasksOwned(userId, language);
    }

    public List<TaskSummary> getTasksOwned(String userId, List<Status> status, String language) {
        return session.getTasksOwned(userId, status, language);
    }

    public void nominate(long taskId, String userId, List<OrganizationalEntity> potentialOwners) {
        session.nominateTask(taskId, userId, potentialOwners);
    }

    /**
     * This method allows the user to exercise the query of his/her choice. This
     * method will be deleted in future versions. </p> Only select queries are
     * currently supported, for obvious reasons.
     *
     * @param qlString The query string.
     * @param size Maximum number of results to return.
     * @param offset The offset from the beginning of the result list
     * determining the first result.
     *
     * @return The result of the query.
     */
    @Deprecated
    public List<?> query(String qlString, Integer size, Integer offset) {
        return session.query(qlString, size, offset);
    }

    public void register(long taskId, String userId) {
        session.taskOperation(Operation.Register, taskId, userId, null, null, null);
    }

    public void registerForEvent(EventKey key, boolean remove, EventResponseHandler responseHandler) {
        SimpleEventTransport transport = new SimpleEventTransport(responseHandler, remove);
        service.getEventKeys().register(key, transport);
        
    }

    public void unregisterForEvent(EventKey key) {
        service.getEventKeys().removeKey(key);
    }
    
    public void release(long taskId, String userId) {
        session.taskOperation(Operation.Release, taskId, userId, null, null, null);
    }

    public void remove(long taskId, String userId) {
        session.taskOperation(Operation.Remove, taskId, userId, null, null, null);
    }

    public void resume(long taskId, String userId) {
        session.taskOperation(Operation.Resume, taskId, userId, null, null, null);
    }

    public void setDocumentContent(long taskId, Content content) {
        session.setDocumentContent(taskId, content);
    }

    public void setFault(long taskId, String userId, FaultData fault) {
        session.setFault(taskId, userId, fault);
    }

    public void setOutput(long taskId, String userId, ContentData outputContentData) {
        session.setOutput(taskId, userId, outputContentData);
    }

    public void setPriority(long taskId, String userId, int priority) {
        session.setPriority(taskId, userId, priority);
    }

    public void skip(long taskId, String userId) {
        session.taskOperation(Operation.Skip, taskId, userId, null, null, null);
    }

    public void start(long taskId, String userId) {
        session.taskOperation(Operation.Start, taskId, userId, null, null, null);
    }

    public void stop(long taskId, String userId) {
        session.taskOperation(Operation.Stop, taskId, userId, null, null, null);
    }

    public void suspend(long taskId, String userId) {
        session.taskOperation(Operation.Suspend, taskId, userId, null, null, null);
    }

    public void claimNextAvailable(String userId, String language) {
        session.claimNextAvailable(userId, language);

    }
    @Deprecated
    public void claimNextAvailable(String userId, List<String> groupIds, String language) {
        session.claimNextAvailable(userId, groupIds, language);
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    

    private static class SimpleEventTransport implements EventTriggerTransport {

        private boolean remove;
        private EventResponseHandler responseHandler;

        public SimpleEventTransport(EventResponseHandler responseHandler, boolean remove) {
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
    
    public void addEventListener(final TaskEventListener listener) {
        service.addEventListener(listener);
    }
}
