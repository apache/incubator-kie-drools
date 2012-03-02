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
package org.jbpm.task;

import java.util.List;
import java.util.Map;

import org.jbpm.eventmessaging.EventKey;
import org.jbpm.eventmessaging.EventResponseHandler;
import org.jbpm.task.query.TaskSummary;
import org.jbpm.task.service.ContentData;
import org.jbpm.task.service.FaultData;

/**
 *
 *
 */
public interface TaskService {

    void activate(long taskId, String userId);

    void addAttachment(long taskId, Attachment attachment, Content content);

    void addComment(long taskId, Comment comment);

    void addTask(Task task, ContentData content);

    void claim(long taskId, String userId);

    void claim(long taskId, String userId, List<String> groupIds);

    void complete(long taskId, String userId, ContentData outputData);

    void completeWithResults(long taskId, String userId, Object results);
    
    boolean connect();

    boolean connect(String address, int port);

    void delegate(long taskId, String userId, String targetUserId);

    void deleteAttachment(long taskId, long attachmentId, long contentId);

    void deleteComment(long taskId, long commentId);

    void deleteFault(long taskId, String userId);

    void deleteOutput(long taskId, String userId);

    void disconnect() throws Exception;

    void fail(long taskId, String userId, FaultData faultData);

    void forward(long taskId, String userId, String targetEntityId);

    Content getContent(long contentId);

    List<TaskSummary> getSubTasksAssignedAsPotentialOwner(long parentId, String userId, String language);

    List<TaskSummary> getSubTasksByParent(long parentId);

    Task getTask(long taskId);

    Task getTaskByWorkItemId(long workItemId);

    List<TaskSummary> getTasksAssignedAsBusinessAdministrator(String userId, String language);

    List<TaskSummary> getTasksAssignedAsExcludedOwner(String userId, String language);

    List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId, String language);

    List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId, List<String> groupIds, String language);

    List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId, List<String> groupIds, String language, int firstResult, int maxResult);
    
    List<TaskSummary> getTasksAssignedAsRecipient(String userId, String language);

    List<TaskSummary> getTasksAssignedAsTaskInitiator(String userId, String language);

    List<TaskSummary> getTasksAssignedAsTaskStakeholder(String userId, String language);

    List<TaskSummary>  getTasksOwned(String userId, String language);

    void nominate(long taskId, String userId, List<OrganizationalEntity> potentialOwners);

    List<?> query(String qlString, Integer size, Integer offset);

    void register(long taskId, String userId);

    void registerForEvent(EventKey key, boolean remove, EventResponseHandler responseHandler);

    void release(long taskId, String userId);

    void remove(long taskId, String userId);

    void resume(long taskId, String userId);

    void setDocumentContent(long taskId, Content content);

    void setFault(long taskId, String userId, FaultData fault);

    void setOutput(long taskId, String userId, ContentData outputContentData);

    void setPriority(long taskId, String userId, int priority);

    void skip(long taskId, String userId);

    void start(long taskId, String userId);

    void stop(long taskId, String userId);

    void suspend(long taskId, String userId);
    
}
