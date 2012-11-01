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
import org.jbpm.eventmessaging.EventKey;
import org.jbpm.eventmessaging.EventResponseHandler;
import org.jbpm.task.Attachment;
import org.jbpm.task.Comment;
import org.jbpm.task.Content;
import org.jbpm.task.OrganizationalEntity;
import org.jbpm.task.Task;
import org.jbpm.task.service.ContentData;
import org.jbpm.task.service.FaultData;
import org.jbpm.task.service.TaskClientHandler.AddAttachmentResponseHandler;
import org.jbpm.task.service.TaskClientHandler.AddCommentResponseHandler;
import org.jbpm.task.service.TaskClientHandler.AddTaskResponseHandler;
import org.jbpm.task.service.TaskClientHandler.DeleteAttachmentResponseHandler;
import org.jbpm.task.service.TaskClientHandler.DeleteCommentResponseHandler;
import org.jbpm.task.service.TaskClientHandler.GetContentResponseHandler;
import org.jbpm.task.service.TaskClientHandler.GetTaskResponseHandler;
import org.jbpm.task.service.TaskClientHandler.QueryGenericResponseHandler;
import org.jbpm.task.service.TaskClientHandler.SetDocumentResponseHandler;
import org.jbpm.task.service.TaskClientHandler.TaskOperationResponseHandler;
import org.jbpm.task.service.TaskClientHandler.TaskSummaryResponseHandler;
import org.jbpm.task.service.responsehandlers.BlockingTaskSummaryResponseHandler;

public interface AsyncTaskService {

    void activate(long taskId, String userId, TaskOperationResponseHandler responseHandler);

    void addAttachment(long taskId, Attachment attachment, Content content, AddAttachmentResponseHandler responseHandler);

    void addComment(long taskId, Comment comment, AddCommentResponseHandler responseHandler);

    void addTask(Task task, ContentData content, AddTaskResponseHandler responseHandler);

    void claim(long taskId, String userId, TaskOperationResponseHandler responseHandler);

    @Deprecated
    void claim(long taskId, String userId, List<String> groupIds, TaskOperationResponseHandler responseHandler);
    
    void claimNextAvailable( String userId, String language, TaskOperationResponseHandler responseHandler);

    @Deprecated
    void claimNextAvailable( String userId, List<String> groupIds, String language, TaskOperationResponseHandler responseHandler);
    
    void complete(long taskId, String userId, ContentData outputData, TaskOperationResponseHandler responseHandler);

    void completeWithResults(long taskId, String userId, Object results, TaskOperationResponseHandler responseHandler);
    
    boolean connect();

    boolean connect(String address, int port);

    void delegate(long taskId, String userId, String targetUserId, TaskOperationResponseHandler responseHandler);

    void deleteAttachment(long taskId, long attachmentId, long contentId, DeleteAttachmentResponseHandler responseHandler);

    void deleteComment(long taskId, long commentId, DeleteCommentResponseHandler responseHandler);

    void deleteFault(long taskId, String userId, TaskOperationResponseHandler responseHandler);

    void deleteOutput(long taskId, String userId, TaskOperationResponseHandler responseHandler);

    void disconnect() throws Exception;
    
    void exit(long taskId, String userId, TaskOperationResponseHandler responseHandler);

    void fail(long taskId, String userId, FaultData faultData, TaskOperationResponseHandler responseHandler);

    void forward(long taskId, String userId, String targetEntityId, TaskOperationResponseHandler responseHandler);

    void getContent(long contentId, GetContentResponseHandler responseHandler);

    void getSubTasksAssignedAsPotentialOwner(long parentId, String userId, String language, TaskSummaryResponseHandler responseHandler);

    void getSubTasksByParent(long parentId, TaskSummaryResponseHandler responseHandler);

    void getTask(long taskId, GetTaskResponseHandler responseHandler);

    void getTaskByWorkItemId(long workItemId, GetTaskResponseHandler responseHandler);

    void getTasksAssignedAsBusinessAdministrator(String userId, String language, TaskSummaryResponseHandler responseHandler);

    void getTasksAssignedAsExcludedOwner(String userId, String language, TaskSummaryResponseHandler responseHandler);

    void getTasksAssignedAsPotentialOwner(String userId, String language, TaskSummaryResponseHandler responseHandler);

    @Deprecated
    void getTasksAssignedAsPotentialOwner(String userId, List<String> groupIds, String language, TaskSummaryResponseHandler responseHandler);

    @Deprecated
    void getTasksAssignedAsPotentialOwner(String userId, List<String> groupIds, String language, int firstResult, int maxResult, TaskSummaryResponseHandler responseHandler);

    void getTasksAssignedAsRecipient(String userId, String language, TaskSummaryResponseHandler responseHandler);

    void getTasksAssignedAsTaskInitiator(String userId, String language, TaskSummaryResponseHandler responseHandler);

    void getTasksAssignedAsTaskStakeholder(String userId, String language, TaskSummaryResponseHandler responseHandler);

    void getTasksOwned(String userId, String language, TaskSummaryResponseHandler responseHandler);
    
    void getTasksOwned(String userId, List<Status> status, String language, TaskSummaryResponseHandler responseHandler);

    void nominate(long taskId, String userId, List<OrganizationalEntity> potentialOwners, TaskOperationResponseHandler responseHandler);

    /**
     * This method allows the user to exercise the query of his/her choice. 
     * This method will be deleted in future versions. 
     * </p>
     * Only select queries are currently supported, for obvious reasons. 
     * 
     * @param qlString The query string. 
     * @param size     Maximum number of results to return.
     * @param offset   The offset from the beginning of the result list determining the first result. 
     * 
     * @return         The result of the query. 
     */
    @Deprecated
    void query(String qlString, Integer size, Integer offset, QueryGenericResponseHandler responseHandler);

    void register(long taskId, String userId, TaskOperationResponseHandler responseHandler);

    /**
     * This method registers for an event key a handler to notify the task life cycle events 
     * @param key
     * @param remove The remove boolean is used to remove the handler after firing a notification
     * @param responseHandler 
     */
    void registerForEvent(EventKey key, boolean remove, EventResponseHandler responseHandler);
    
    /**
     * This method removes all the handlers associated with an EventKey
     * @param key 
     */
    void unregisterForEvent(EventKey key);

    void release(long taskId, String userId, TaskOperationResponseHandler responseHandler);

    void remove(long taskId, String userId, TaskOperationResponseHandler responseHandler);

    void resume(long taskId, String userId, TaskOperationResponseHandler responseHandler);

    void setDocumentContent(long taskId, Content content, SetDocumentResponseHandler responseHandler);

    void setFault(long taskId, String userId, FaultData fault, TaskOperationResponseHandler responseHandler);

    void setOutput(long taskId, String userId, ContentData outputContentData, TaskOperationResponseHandler responseHandler);

    void setPriority(long taskId, String userId, int priority, TaskOperationResponseHandler responseHandler);

    void skip(long taskId, String userId, TaskOperationResponseHandler responseHandler);

    void start(long taskId, String userId, TaskOperationResponseHandler responseHandler);

    void stop(long taskId, String userId, TaskOperationResponseHandler responseHandler);

    void suspend(long taskId, String userId, TaskOperationResponseHandler responseHandler);

    public void getTasksAssignedAsPotentialOwnerByStatus(String userId, List<Status> status, String language, BlockingTaskSummaryResponseHandler responseHandler);
    
    @Deprecated
    public void getTasksAssignedAsPotentialOwnerByStatusByGroup(String userId, List<String> groupIds, List<Status> status, String language, BlockingTaskSummaryResponseHandler responseHandler);

    public void getTasksByStatusByProcessId(long processInstanceId, List<Status> status, String language, TaskSummaryResponseHandler responseHandler);

    public void getTasksByStatusByProcessIdByTaskName(long processInstanceId, List<Status> status, String taskName, String language, TaskSummaryResponseHandler responseHandler);
}
