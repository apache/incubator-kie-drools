/*
 * Copyright 2013 JBoss Inc
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
package org.kie.api.task;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.kie.api.runtime.CommandExecutor;
import org.kie.api.task.model.Attachment;
import org.kie.api.task.model.Comment;
import org.kie.api.task.model.Content;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.TaskSummary;

/**
 * The Task Service Entry Point serves as 
 *  facade of all the other services, providing a single entry point
 *  to access to all the services
 */
public interface TaskService extends CommandExecutor {
    
    void activate(long taskId, String userId);

    void claim(long taskId, String userId);

    void claimNextAvailable(String userId, String language);

    void complete(long taskId, String userId, Map<String, Object> data);

    void delegate(long taskId, String userId, String targetUserId);

    void exit(long taskId, String userId);

    void fail(long taskId, String userId, Map<String, Object> faultData);

    void forward(long taskId, String userId, String targetEntityId);

    Task getTaskByWorkItemId(long workItemId);

    Task getTaskById(long taskId);

    List<TaskSummary> getTasksAssignedAsBusinessAdministrator(String userId, String language);

    List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId, String language);

    @Deprecated
    List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId, List<String> groupIds, String language, int firstResult, int maxResults);

    List<TaskSummary> getTasksAssignedAsPotentialOwnerByStatus(String userId, List<Status> status, String language);

    List<TaskSummary> getTasksOwned(String userId, String language);

    List<TaskSummary> getTasksOwnedByStatus(String userId, List<Status> status, String language);

    List<TaskSummary> getTasksByStatusByProcessInstanceId(long processInstanceId, List<Status> status, String language);
    
    List<TaskSummary> getTasksAssignedAsPotentialOwnerByProcessId(String userId, String processId);

    List<Long> getTasksByProcessInstanceId(long processInstanceId);
    
    long addTask(Task task, Map<String, Object> params);

    void release(long taskId, String userId);

    void resume(long taskId, String userId);

    void skip(long taskId, String userId);

    void start(long taskId, String userId);

    void stop(long taskId, String userId);

    void suspend(long taskId, String userId);

    void nominate(long taskId, String userId, List<OrganizationalEntity> potentialOwners);

    Content getContentById(long contentId);

    Attachment getAttachmentById(long attachId);

    Map<String, Object> getTaskContent(long taskId);
    
    /**
     * This method will be removed in jBPM 7.x because of new methods that better implement this functionality.
     * </p>
     * This method queries using the given arguments.
     *  
     * @param userId Optional parameter: the task user id
     * @param workItemIds Optional parameter: a list of work item ids
     * @param taskIds Optional parameter: a list of task ids
     * @param procInstIds Optional parameter: a list of task ids
     * @param busAdmins Optional parameter: a list of business administrator ids
     * @param potOwners Optional parameter: a list of potential owners
     * @param taskOwners Optional parameter: a list of task owners
     * @param status Optional parameter: a list of status's
     * @param union Required: whether the query should be a union or intersection of the criteria
     * @return a List of {@link TaskSummary} instances that fit the critieria given
     */
    @Deprecated
    List<TaskSummary> getTasksByVariousFields( String userId, List<Long> workItemIds, List<Long> taskIds, List<Long> procInstIds, 
            List<String> busAdmins, List<String> potOwners, List<String> taskOwners, 
            List<Status> status, List<String> language, boolean union);
    
    /**
     * This method will be removed in jBPM 7.x because of new methods that better implement this functionality.
     * </p>
     * Using this method is not recommended. 
     * 
     * @see {@link #getTasksByVariousFields(String, List, List, List, List, List, List, List, boolean)}
     */
    @Deprecated
    List<TaskSummary> getTasksByVariousFields( String userId, Map <String, List<?>> parameters, boolean union);
    
    Long addComment(long taskId, Comment comment);
    
    Long addComment(long taskId, String addedByUserId, String commentText);

    void deleteComment(long taskId, long commentId);

    List<Comment> getAllCommentsByTaskId(long taskId);

    Comment getCommentById(long commentId);        
    
    void setExpirationDate(long taskId, Date date);
}