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

import java.util.List;

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
import org.kie.api.runtime.Environment;

/**
 *
 *
 */
public class LocalTaskService implements TaskService {

    private org.jbpm.task.service.TaskService service;
    private Environment environment;

    public LocalTaskService(org.jbpm.task.service.TaskService taskService) {
        this.service = taskService;
    }

    public void activate(long taskId, String userId) {
        TaskServiceSession session = service.createSession();
        session.taskOperation(Operation.Activate, taskId, userId, null, null, null);
        session.dispose();
    }

    public void addAttachment(long taskId, Attachment attachment, Content content) {
        TaskServiceSession session = service.createSession();
        session.addAttachment(taskId, attachment, content);
        session.dispose();
    }

    public void addComment(long taskId, Comment comment) {
        TaskServiceSession session = service.createSession();
        session.addComment(taskId, comment);
        session.dispose();
    }

    public void addTask(Task task, ContentData content) {
        TaskServiceSession session = service.createSession();
        session.addTask(task, content);
        session.dispose();
    }

    public void claim(long taskId, String userId) {
        TaskServiceSession session = service.createSession();
        session.taskOperation(Operation.Claim, taskId, userId, null, null, null);
        session.dispose();
    }

    @Deprecated
    public void claim(long taskId, String userId, List<String> groupIds) {
        TaskServiceSession session = service.createSession();
        session.taskOperation(Operation.Claim, taskId, userId, null, null, groupIds);
        session.dispose();
    }

    public void complete(long taskId, String userId, ContentData outputData) {
        TaskServiceSession session = service.createSession();
        session.taskOperation(Operation.Complete, taskId, userId, null, outputData, null);
        session.dispose();
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
        TaskServiceSession session = service.createSession();
        session.taskOperation(Operation.Delegate, taskId, userId, targetUserId, null, null);
        session.dispose();
    }

    public void deleteAttachment(long taskId, long attachmentId, long contentId) {
        TaskServiceSession session = service.createSession();
        session.deleteAttachment(taskId, attachmentId, contentId);
        session.dispose();
    }

    public void deleteComment(long taskId, long commentId) {
        TaskServiceSession session = service.createSession();
        session.deleteComment(taskId, commentId);
        session.dispose();
    }

    public void deleteFault(long taskId, String userId) {
        TaskServiceSession session = service.createSession();
        session.deleteFault(taskId, userId);
        session.dispose();
    }

    public void deleteOutput(long taskId, String userId) {
        TaskServiceSession session = service.createSession();
        session.deleteOutput(taskId, userId);
        session.dispose();
    }

    public void disconnect() throws Exception {
        
    }

    public void dispose() {
        
    }

    public void exit(long taskId, String userId) {
        TaskServiceSession session = service.createSession();
        session.taskOperation(Operation.Exit, taskId, userId, null, null, null);
        session.dispose();
    }

    public void fail(long taskId, String userId, FaultData faultData) {
        TaskServiceSession session = service.createSession();
        session.taskOperation(Operation.Fail, taskId, userId, null, faultData, null);
        session.dispose();
    }

    public void forward(long taskId, String userId, String targetEntityId) {
        TaskServiceSession session = service.createSession();
        session.taskOperation(Operation.Forward, taskId, userId, targetEntityId, null, null);
        session.dispose();
    }

    public Content getContent(long contentId) {
        TaskServiceSession session = service.createSession();
        Content content = session.getContent(contentId);
        session.dispose();
        
        return content;
    }

    public List<TaskSummary> getSubTasksAssignedAsPotentialOwner(long parentId, String userId, String language) {
        TaskServiceSession session = service.createSession();
        List<TaskSummary> result = session.getSubTasksAssignedAsPotentialOwner(parentId, userId, language);
        session.dispose();
        
        return result;
    }

    public List<TaskSummary> getSubTasksByParent(long parentId) {
        TaskServiceSession session = service.createSession();
        List<TaskSummary> result = session.getSubTasksByParent(parentId, null);
        session.dispose();
        
        return result;
    }

    public Task getTask(long taskId) {
        TaskServiceSession session = service.createSession();
        Task result = session.getTask(taskId);
        loadLazyFields(result);
        session.dispose();
        
        return result;
    }

    public Task getTaskByWorkItemId(long workItemId) {
        TaskServiceSession session = service.createSession();
        Task result = session.getTaskByWorkItemId(workItemId);
        loadLazyFields(result);
        session.dispose();
        
        return result;
    }
    
    public List<Long> getTasksByProcessInstanceId(long processInstanceId) {
    	TaskServiceSession session = service.createSession();
        List<Long> result = session.getTasksByProcessInstanceId(processInstanceId);
        session.dispose();
        return result;
    }

    public List<TaskSummary> getTasksAssignedAsBusinessAdministrator(String userId, String language) {
        TaskServiceSession session = service.createSession();
        List<TaskSummary> result = session.getTasksAssignedAsBusinessAdministrator(userId, language);
        session.dispose();
        
        return result;
    }

    public List<TaskSummary> getTasksAssignedAsExcludedOwner(String userId, String language) {
        TaskServiceSession session = service.createSession();
        List<TaskSummary> result = session.getTasksAssignedAsExcludedOwner(userId, language);
        session.dispose();
        
        return result;
    }

    public List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId, String language) {
        TaskServiceSession session = service.createSession();
        List<TaskSummary> result = session.getTasksAssignedAsPotentialOwner(userId, language);
        session.dispose();
        
        return result;
    }

    public List<TaskSummary> getTasksAssignedAsPotentialOwnerByStatus(String userId, List<Status> status, String language) {
        TaskServiceSession session = service.createSession();
        List<TaskSummary> result = session.getTasksAssignedAsPotentialOwnerByStatus(userId, status, language);
        session.dispose();
        
        return result;
    }
    @Deprecated
    public List<TaskSummary> getTasksAssignedAsPotentialOwnerByStatusByGroup(String userId, List<String> groupIds, List<Status> status, String language) {
        TaskServiceSession session = service.createSession();
        List<TaskSummary> result = session.getTasksAssignedAsPotentialOwnerByStatusByGroup(userId, groupIds, status, language);
        session.dispose();
        
        return result;
    }
    @Deprecated
    public List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId, List<String> groupIds, String language) {
        TaskServiceSession session = service.createSession();
        List<TaskSummary> result = session.getTasksAssignedAsPotentialOwner(userId, groupIds, language);
        session.dispose();
        
        return result;
    }
    @Deprecated
    public List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId, List<String> groupIds, String language, int firstResult, int maxResult) {
        TaskServiceSession session = service.createSession();
        List<TaskSummary> result = session.getTasksAssignedAsPotentialOwner(userId, groupIds, language, firstResult, maxResult);
        session.dispose();
        
        return result;
    }

    public List<TaskSummary> getTasksAssignedAsRecipient(String userId, String language) {
        TaskServiceSession session = service.createSession();
        List<TaskSummary> result = session.getTasksAssignedAsRecipient(userId, language);
        session.dispose();
        
        return result;
    }

    public List<TaskSummary> getTasksAssignedAsTaskInitiator(String userId, String language) {
        TaskServiceSession session = service.createSession();
        List<TaskSummary> result = session.getTasksAssignedAsTaskInitiator(userId, language);
        session.dispose();
        
        return result;
    }

    public List<TaskSummary> getTasksAssignedAsTaskStakeholder(String userId, String language) {
        TaskServiceSession session = service.createSession();
        List<TaskSummary> result = session.getTasksAssignedAsTaskStakeholder(userId, language);
        session.dispose();
        
        return result;
    }

    public List<TaskSummary> getTasksByStatusByProcessId(long processInstanceId, List<Status> status, String language) {
        TaskServiceSession session = service.createSession();
        List<TaskSummary> result = session.getTasksByStatusByProcessId(processInstanceId, status, language);
        session.dispose();
        
        return result;
    }

    public List<TaskSummary> getTasksByStatusByProcessIdByTaskName(long processInstanceId, List<Status> status, String taskName, String language) {
        TaskServiceSession session = service.createSession();
        List<TaskSummary> result = session.getTasksByStatusByProcessIdByTaskName(processInstanceId, status, taskName, language);
        session.dispose();
        
        return result;
    }

    public List<TaskSummary> getTasksOwned(String userId, String language) {
        TaskServiceSession session = service.createSession();
        List<TaskSummary> result = session.getTasksOwned(userId, language);
        session.dispose();
        
        return result;
    }

    public List<TaskSummary> getTasksOwned(String userId, List<Status> status, String language) {
        TaskServiceSession session = service.createSession();
        List<TaskSummary> result = session.getTasksOwned(userId, status, language);
        session.dispose();
        
        return result;
    }

    public void nominate(long taskId, String userId, List<OrganizationalEntity> potentialOwners) {
        TaskServiceSession session = service.createSession();
        session.nominateTask(taskId, userId, potentialOwners);
        session.dispose();
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
        TaskServiceSession session = service.createSession();
        List<?> result = session.query(qlString, size, offset);
        session.dispose();
        
        return result;
    }

    public void register(long taskId, String userId) {
        TaskServiceSession session = service.createSession();
        session.taskOperation(Operation.Register, taskId, userId, null, null, null);
        session.dispose();
    }

    public void registerForEvent(EventKey key, boolean remove, EventResponseHandler responseHandler) {
        SimpleEventTransport transport = new SimpleEventTransport(responseHandler, remove);
        service.getEventKeys().register(key, transport);
        
    }

    public void unregisterForEvent(EventKey key) {
        service.getEventKeys().removeKey(key);
    }
    
    public void release(long taskId, String userId) {
        TaskServiceSession session = service.createSession();
        session.taskOperation(Operation.Release, taskId, userId, null, null, null);
        session.dispose();
    }

    public void remove(long taskId, String userId) {
        TaskServiceSession session = service.createSession();
        session.taskOperation(Operation.Remove, taskId, userId, null, null, null);
        session.dispose();
    }

    public void resume(long taskId, String userId) {
        TaskServiceSession session = service.createSession();
        session.taskOperation(Operation.Resume, taskId, userId, null, null, null);
        session.dispose();
    }

    public void setDocumentContent(long taskId, Content content) {
        TaskServiceSession session = service.createSession();
        session.setDocumentContent(taskId, content);
        session.dispose();
    }

    public void setFault(long taskId, String userId, FaultData fault) {
        TaskServiceSession session = service.createSession();
        session.setFault(taskId, userId, fault);
        session.dispose();
    }

    public void setOutput(long taskId, String userId, ContentData outputContentData) {
        TaskServiceSession session = service.createSession();
        session.setOutput(taskId, userId, outputContentData);
        session.dispose();
    }

    public void setPriority(long taskId, String userId, int priority) {
        TaskServiceSession session = service.createSession();
        session.setPriority(taskId, userId, priority);
        session.dispose();
    }

    public void skip(long taskId, String userId) {
        TaskServiceSession session = service.createSession();
        session.taskOperation(Operation.Skip, taskId, userId, null, null, null);
        session.dispose();
    }

    public void start(long taskId, String userId) {
        TaskServiceSession session = service.createSession();
        session.taskOperation(Operation.Start, taskId, userId, null, null, null);
        session.dispose();
    }

    public void stop(long taskId, String userId) {
        TaskServiceSession session = service.createSession();
        session.taskOperation(Operation.Stop, taskId, userId, null, null, null);
        session.dispose();
    }

    public void suspend(long taskId, String userId) {
        TaskServiceSession session = service.createSession();
        session.taskOperation(Operation.Suspend, taskId, userId, null, null, null);
        session.dispose();
    }

    public void claimNextAvailable(String userId, String language) {
        TaskServiceSession session = service.createSession();
        session.claimNextAvailable(userId, language);
        session.dispose();
    }
    @Deprecated
    public void claimNextAvailable(String userId, List<String> groupIds, String language) {
        TaskServiceSession session = service.createSession();
        session.claimNextAvailable(userId, groupIds, language);
        session.dispose();
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
    
    private void loadLazyFields(Task task) {
        task.getPeopleAssignments().getBusinessAdministrators().size();
        task.getPeopleAssignments().getPotentialOwners().size();
        task.getPeopleAssignments().getRecipients().size();
        task.getPeopleAssignments().getExcludedOwners().size();
        task.getPeopleAssignments().getTaskStakeholders().size();
        task.getDeadlines().getStartDeadlines().size();
        task.getDeadlines().getEndDeadlines().size();
        task.getDelegation().getDelegates().size();
        task.getTaskData().getAttachments().size();
        task.getTaskData().getComments().size();
        task.getDescriptions().size();
        task.getNames().size();
        task.getSubjects().size();
        task.getSubTaskStrategies().size();
        
    }
}
