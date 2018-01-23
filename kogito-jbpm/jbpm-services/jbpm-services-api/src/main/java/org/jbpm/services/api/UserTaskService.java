/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.services.api;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.jbpm.services.api.model.UserTaskInstanceDesc;
import org.kie.api.command.Command;
import org.kie.api.runtime.manager.Context;
import org.kie.api.task.model.Attachment;
import org.kie.api.task.model.Comment;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.Task;

public interface UserTaskService {
	
	// user task life cycle operation

	/**
	 * Activate the task, i.e. set the task to status Ready.
	 * 
	 * @param taskId
	 * @param userId
	 * @throws TaskNotFoundException in case task with given id was not found
	 */
	void activate(Long taskId, String userId);
	
	/**
     * Activate the task, i.e. set the task to status Ready.
     * 
     * @param deploymentId
     * @param taskId
     * @param userId
     * @throws TaskNotFoundException in case task with given id was not found or is not associated with given deployment id
     */
	void activate(String deploymentId, Long taskId, String userId);

	/**
	 * Claim responsibility for a task, i.e. set the task to status Reserved
	 * 
	 * @param taskId
	 * @param userId
	 * @throws TaskNotFoundException in case task with given id was not found
	 */
	void claim(Long taskId, String userId);
	
	/**
     * Claim responsibility for a task, i.e. set the task to status Reserved
     * 
     * @param deploymentId
     * @param taskId
     * @param userId
     * @throws TaskNotFoundException in case task with given id was not found or is not associated with given deployment id
     */
    void claim(String deploymentId, Long taskId, String userId);


    /**
     * Complete a task with the given data
     * 
     * @param taskId
     * @param userId
     * @param data
     * @throws TaskNotFoundException in case task with given id was not found
     */
    void complete(Long taskId, String userId, Map<String, Object> params);
    
    /**
     * Complete a task with the given data
     * 
     * @param deploymentId
     * @param taskId
     * @param userId
     * @param data
     * @throws TaskNotFoundException in case task with given id was not found or is not associated with given deployment id
     */
    void complete(String deploymentId, Long taskId, String userId, Map<String, Object> params);


    /**
     * Complete a task with the given data. If needed, the task is automatically claimed and/or started.
     * 
     * @param taskId
     * @param userId
     * @param data
     * @throws TaskNotFoundException in case task with given id was not found
     */
    void completeAutoProgress(Long taskId, String userId, Map<String, Object> params);
    
    /**
     * Complete a task with the given data. If needed, the task is automatically claimed and/or started.
     * 
     * @param deploymentId
     * @param taskId
     * @param userId
     * @param data
     * @throws TaskNotFoundException in case task with given id was not found or is not associated with given deployment id
     */
    void completeAutoProgress(String deploymentId, Long taskId, String userId, Map<String, Object> params);
	

	/**
	 * 
	 * Delegate a task from userId to targetUserId
	 * 
	 * @param taskId
	 * @param userId
	 * @param targetUserId
	 * @throws TaskNotFoundException in case task with given id was not found
	 */
	void delegate(Long taskId, String userId, String targetUserId);
	
	/**
     * 
     * Delegate a task from userId to targetUserId
     * 
     * @param deploymentId
     * @param taskId
     * @param userId
     * @param targetUserId
     * @throws TaskNotFoundException in case task with given id was not found or is not associated with given deployment id
     */
    void delegate(String deploymentId, Long taskId, String userId, String targetUserId);

	/**
	 * Requesting application is no longer interested in the task output
	 * 
	 * 
	 * @param taskId
	 * @param userId
	 * @throws TaskNotFoundException in case task with given id was not found
	 */
	void exit(Long taskId, String userId);
	
	/**
     * Requesting application is no longer interested in the task output
     * 
     * @param deploymentId
     * @param taskId
     * @param userId
     * @throws TaskNotFoundException in case task with given id was not found or is not associated with given deployment id
     */
    void exit(String deploymentId, Long taskId, String userId);

	/**
	 * Actual owner completes the execution of the task raising a fault. The
	 * fault illegalOperationFault is returned if the task interface defines no
	 * faults. If fault name or fault data is not set the operation returns
	 * illegalArgumentFault.
	 * 
	 * 
	 * @param taskId
	 * @param userId
	 * @param faultData
	 * @throws TaskNotFoundException in case task with given id was not found
	 */
	void fail(Long taskId, String userId, Map<String, Object> faultData);
	
	/**
     * Actual owner completes the execution of the task raising a fault. The
     * fault illegalOperationFault is returned if the task interface defines no
     * faults. If fault name or fault data is not set the operation returns
     * illegalArgumentFault.
     * 
     * @param deploymentId 
     * @param taskId
     * @param userId
     * @param faultData
     * @throws TaskNotFoundException in case task with given id was not found or is not associated with given deployment id
     */
    void fail(String deploymentId, Long taskId, String userId, Map<String, Object> faultData);

	/**
	 * Forward the task to another organization entity. The caller has to
	 * specify the receiving organizational entity. Potential owners can only
	 * forward a task while the task is in the Ready state. For details on
	 * forwarding human tasks refer to section 4.7.3 in WS-HumanTask_v1.pdf
	 * 
	 * 
	 * @param taskId
	 * @param userId
	 * @param targetEntityId
	 * @throws TaskNotFoundException in case task with given id was not found
	 */
	void forward(Long taskId, String userId, String targetEntityId);
	
	/**
     * Forward the task to another organization entity. The caller has to
     * specify the receiving organizational entity. Potential owners can only
     * forward a task while the task is in the Ready state. For details on
     * forwarding human tasks refer to section 4.7.3 in WS-HumanTask_v1.pdf
     * 
     * @param deploymentId 
     * @param taskId
     * @param userId
     * @param targetEntityId
     * @throws TaskNotFoundException in case task with given id was not found or is not associated with given deployment id
     */
    void forward(String deploymentId, Long taskId, String userId, String targetEntityId);

	/**
	 * Release a previously claimed task
	 * 
	 * @param taskId
	 * @param userId
	 * @throws TaskNotFoundException in case task with given id was not found
	 */
	void release(Long taskId, String userId);
	
	/**
     * Release a previously claimed task
     * 
     * @param deploymentId
     * @param taskId
     * @param userId
     * @throws TaskNotFoundException in case task with given id was not found or is not associated with given deployment id
     */
    void release(String deploymentId, Long taskId, String userId);
	

	/**
	 * Resume a previously suspended task
	 * 
	 * @param taskId
	 * @param userId
	 * @throws TaskNotFoundException in case task with given id was not found
	 */
	void resume(Long taskId, String userId);
	
	/**
     * Resume a previously suspended task
     * 
     * @param deploymentId
     * @param taskId
     * @param userId
     * @throws TaskNotFoundException in case task with given id was not found or is not associated with given deployment id
     */
    void resume(String deploymentId, Long taskId, String userId);

	/**
	 * Skip a claimed task
	 * 
	 * @param taskId
	 * @param userId
	 * @throws TaskNotFoundException in case task with given id was not found
	 */
	void skip(Long taskId, String userId);
	
	/**
     * Skip a claimed task
     * 
     * @param deploymentId
     * @param taskId
     * @param userId
     * @throws TaskNotFoundException in case task with given id was not found or is not associated with given deployment id
     */
    void skip(String deploymentId, Long taskId, String userId);

	/**
	 * Start the execution of the task, i.e. set the task to status InProgress.
	 * 
	 * @param taskId
	 * @param userId
	 * @throws TaskNotFoundException in case task with given id was not found
	 */
	void start(Long taskId, String userId);
	
	/**
     * Start the execution of the task, i.e. set the task to status InProgress.
     * 
     * @param deploymentId
     * @param taskId
     * @param userId
     * @throws TaskNotFoundException in case task with given id was not found or is not associated with given deployment id
     */
    void start(String deploymentId, Long taskId, String userId);
	

	/**
	 * Cancel/stop the processing of the task. The task returns to the Reserved
	 * state.
	 * 
	 * 
	 * @param taskId
	 * @param userId
	 * @throws TaskNotFoundException in case task with given id was not found
	 */
	void stop(Long taskId, String userId);
	
	/**
     * Cancel/stop the processing of the task. The task returns to the Reserved
     * state.
     * 
     * @param deploymentId
     * @param taskId
     * @param userId
     * @throws TaskNotFoundException in case task with given id was not found or is not associated with given deployment id
     */
    void stop(String deploymentId, Long taskId, String userId);

	/**
	 * Suspend a claimed task.
	 * 
	 * @param taskId
	 * @param userId
	 * @throws TaskNotFoundException in case task with given id was not found
	 */
	void suspend(Long taskId, String userId);
	
	/**
     * Suspend a claimed task.
     * 
     * @param deploymentId
     * @param taskId
     * @param userId
     * @throws TaskNotFoundException in case task with given id was not found or is not associated with given deployment id
     */
    void suspend(String deploymentId, Long taskId, String userId);

	/**
	 * Nominate a task to be handled by potentialOwners
	 * 
	 * @param taskId
	 * @param userId
	 * @param potentialOwners
	 * @throws TaskNotFoundException in case task with given id was not found
	 */
	void nominate(Long taskId, String userId, List<OrganizationalEntity> potentialOwners);
	
	/**
     * Nominate a task to be handled by potentialOwners
     * 
     * @param deploymentId
     * @param taskId
     * @param userId
     * @param potentialOwners
     * @throws TaskNotFoundException in case task with given id was not found or is not associated with given deployment id
     */
    void nominate(String deploymentId, Long taskId, String userId, List<OrganizationalEntity> potentialOwners);
	
	// user task attributes operations
	
	/**
	 * Sets priority of a task to given value
	 * @param taskId
	 * @param priority
	 * @throws TaskNotFoundException in case task with given id was not found
	 */
    void setPriority(Long taskId, int priority);
    
    /**
     * Sets priority of a task to given value
     * 
     * @param deploymentId
     * @param taskId
     * @param priority
     * @throws TaskNotFoundException in case task with given id was not found or is not associated with given deployment id
     */
    void setPriority(String deploymentId, Long taskId, int priority);

    /**
     * Sets expiration date of a task to given value
     * @param taskId
     * @param date
     * @throws TaskNotFoundException in case task with given id was not found
     */
    void setExpirationDate(Long taskId, Date date);
    
    /**
     * Sets expiration date of a task to given value
     * 
     * @param deploymentId
     * @param taskId
     * @param date
     * @throws TaskNotFoundException in case task with given id was not found or is not associated with given deployment id
     */
    void setExpirationDate(String deploymentId, Long taskId, Date date);

    /**
     * Sets skipable property of a task to given value
     * @param taskId
     * @param skipable
     * @throws TaskNotFoundException in case task with given id was not found
     */
    void setSkipable(Long taskId, boolean skipable);
    
    /**
     * Sets skipable property of a task to given value
     * 
     * @param deploymentId
     * @param taskId
     * @param skipable
     * @throws TaskNotFoundException in case task with given id was not found or is not associated with given deployment id
     */
    void setSkipable(String deploymentId, Long taskId, boolean skipable);
    
    /**
     * Sets name of the task
     * @param taskId
     * @param name
     * @throws TaskNotFoundException in case task with given id was not found
     */
    void setName(Long taskId, String name);
    
    /**
     * Sets name of the task
     * 
     * @param deploymentId
     * @param taskId
     * @param name
     * @throws TaskNotFoundException in case task with given id was not found or is not associated with given deployment id
     */
    void setName(String deploymentId, Long taskId, String name);
    
    /**
     * Sets description of the task
     * @param taskId
     * @param description
     * @throws TaskNotFoundException in case task with given id was not found
     */
    void setDescription(Long taskId, String description);
    
    /**
     * Sets description of the task
     * 
     * @param deploymentId
     * @param taskId
     * @param description
     * @throws TaskNotFoundException in case task with given id was not found or is not associated with given deployment id
     */
    void setDescription(String deploymentId, Long taskId, String description);

    /**
     * Updates user task properties and data inputs and outputs. Allowed properties to be updated are:
     * <ul>
     *  <li>name</li>
     *  <li>description</li>
     *  <li>priority</li>
     *  <li>expiration date</li>
     *  <li>formName</li>    
     * </ul>
     * 
     * @param userId user id who is going to perform the update
     * @param userTask user task with properties to be updated
     * @param inputData map of input variables to be added/replaced on a task
     * @param outputData map of output variables to be added/replaced on a task
     * @throws TaskNotFoundException in case task id was not given or task was not found with given id
     */
    void updateTask(Long taskId, String userId, UserTaskInstanceDesc userTask, Map<String, Object> inputData, Map<String, Object> outputData);
    
    /**
     * Updates user task properties and data inputs and outputs. Allowed properties to be updated are:
     * <ul>
     *  <li>name</li>
     *  <li>description</li>
     *  <li>priority</li>
     *  <li>expiration date</li>
     *  <li>formName</li>    
     * </ul>
     * 
     * @param deploymentId
     * @param userId user id who is going to perform the update
     * @param userTask user task with properties to be updated
     * @param inputData map of input variables to be added/replaced on a task
     * @param outputData map of output variables to be added/replaced on a task
     * @throws TaskNotFoundException in case task id was not given or task was not found with given id or is not associated with given deployment id
     */
    void updateTask(String deploymentId, Long taskId, String userId, UserTaskInstanceDesc userTask, Map<String, Object> inputData, Map<String, Object> outputData);
	
	// user task instance content operations
	
    /**
     * Saves gives values as content of a task, applies to task output as input cannot be altered
     * @param taskId
     * @param values
     * @return
     * @throws TaskNotFoundException in case task with given id was not found
     */
    Long saveContent(Long taskId, Map<String, Object> values);
    
    /**
     * Saves gives values as content of a task, applies to task output as input cannot be altered
     * 
     * @param deploymentId
     * @param taskId
     * @param values
     * @return
     * @throws TaskNotFoundException in case task with given id was not found or is not associated with given deployment id
     */
    Long saveContent(String deploymentId, Long taskId, Map<String, Object> values);

    /**
     * Returns task output data currently stored, might be null of no data is stored
     * @param taskId
     * @return
     * @throws TaskNotFoundException in case task with given id was not found
     */
    Map<String, Object> getTaskOutputContentByTaskId(Long taskId);
    
    /**
     * Returns task output data currently stored, might be null of no data is stored
     * 
     * @param deploymentId
     * @param taskId
     * @return
     * @throws TaskNotFoundException in case task with given id was not found or is not associated with given deployment id
     */
    Map<String, Object> getTaskOutputContentByTaskId(String deploymentId, Long taskId);

    /**
     * Returns task input data of a task
     * @param taskId
     * @return
     * @throws TaskNotFoundException in case task with given id was not found
     */
    Map<String, Object> getTaskInputContentByTaskId(Long taskId);
    
    /**
     * Returns task input data of a task
     * 
     * @param deploymentId
     * @param taskId
     * @return
     * @throws TaskNotFoundException in case task with given id was not found or is not associated with given deployment id
     */
    Map<String, Object> getTaskInputContentByTaskId(String deploymentId, Long taskId);
    
    /**
     * Deletes content given by <code>contentId</code> from given task
     * @param taskId
     * @param contentId
     * @throws TaskNotFoundException in case task with given id was not found
     */
    void deleteContent(Long taskId, Long contentId);
    
    /**
     * Deletes content given by <code>contentId</code> from given task
     * 
     * @param deploymentId
     * @param taskId
     * @param contentId
     * @throws TaskNotFoundException in case task with given id was not found or is not associated with given deployment id
     */
    void deleteContent(String deploymentId, Long taskId, Long contentId);
    
    // user task comments operations

    /**
     * Add comment to a task
     * @param taskId
     * @param text
     * @param addedBy
     * @param addedOn
     * @return
     * @throws TaskNotFoundException in case task with given id was not found
     */
    Long addComment(Long taskId, String text, String addedBy, Date addedOn);
    
    /**
     * Add comment to a task
     * 
     * @param deploymentId
     * @param taskId
     * @param text
     * @param addedBy
     * @param addedOn
     * @return
     * @throws TaskNotFoundException in case task with given id was not found or is not associated with given deployment id
     */
    Long addComment(String deploymentId, Long taskId, String text, String addedBy, Date addedOn);

    /**
     * Deletes comment from a task
     * @param taskId
     * @param commentId
     * @throws TaskNotFoundException in case task with given id was not found
     */
    void deleteComment(Long taskId, Long commentId);
    
    /**
     * Deletes comment from a task
     * 
     * @param deploymentId
     * @param taskId
     * @param commentId
     * @throws TaskNotFoundException in case task with given id was not found or is not associated with given deployment id
     */
    void deleteComment(String deploymentId, Long taskId, Long commentId);

    /**
     * Get comments for a task
     * @param taskId
     * @return
     * @throws TaskNotFoundException in case task with given id was not found
     */
    List<Comment> getCommentsByTaskId(Long taskId);
    
    /**
     * Get comments for a task
     * 
     * @param deploymentId
     * @param taskId
     * @return
     * @throws TaskNotFoundException in case task with given id was not found or is not associated with given deployment id
     */
    List<Comment> getCommentsByTaskId(String deploymentId, Long taskId);

    /**
     * Get comment by identifier
     * @param commentId
     * @return
     * @throws TaskNotFoundException in case task with given id was not found
     */
    Comment getCommentById(Long taskId, Long commentId);
    
    /**
     * Get comment by identifier
     * 
     * @param deploymentId
     * @param commentId
     * @return
     * @throws TaskNotFoundException in case task with given id was not found or is not associated with given deployment id
     */
    Comment getCommentById(String deploymentId, Long taskId, Long commentId);
    
    // user task attachment operations
    
    /**
     * Add attachment to a task
     * @param taskId
     * @param userId
     * @param attachment
     * @return
     * @throws TaskNotFoundException in case task with given id was not found
     */
    Long addAttachment(Long taskId, String userId, String name, Object attachment);
    
    /**
     * Add attachment to a task
     * 
     * @param deploymentId
     * @param taskId
     * @param userId
     * @param attachment
     * @return
     * @throws TaskNotFoundException in case task with given id was not found or is not associated with given deployment id
     */
    Long addAttachment(String deploymentId, Long taskId, String userId, String name, Object attachment);
    
    /**
     * Delete attachment from a task
     * @param taskId
     * @param attachmentId
     * @throws TaskNotFoundException in case task with given id was not found
     */
    void deleteAttachment(Long taskId, Long attachmentId);
    
    /**
     * Delete attachment from a task
     * 
     * @param deploymentId
     * @param taskId
     * @param attachmentId
     * @throws TaskNotFoundException in case task with given id was not found or is not associated with given deployment id
     */
    void deleteAttachment(String deploymentId, Long taskId, Long attachmentId);
    
	/**
	 * Get attachment by identifier
	 * @param attachmentId
	 * @return
	 * @throws TaskNotFoundException in case task with given id was not found
	 */
	Attachment getAttachmentById(Long taskId, Long attachmentId);
	
	/**
     * Get attachment by identifier
     * 
     * @param deploymentId
     * @param attachmentId
     * @return
     * @throws TaskNotFoundException in case task with given id was not found or is not associated with given deployment id
     */
    Attachment getAttachmentById(String deploymentId, Long taskId, Long attachmentId);
	
	/**
	 * Get attachment's content by identifier
	 * @param attachmentId
	 * @return
	 * @throws TaskNotFoundException in case task with given id was not found
	 */
	Object getAttachmentContentById(Long taskId, Long attachmentId);
	
	/**
     * Get attachment's content by identifier
     * 
     * @param deploymentId
     * @param attachmentId
     * @return
     * @throws TaskNotFoundException in case task with given id was not found or is not associated with given deployment id
     */
    Object getAttachmentContentById(String deploymentId, Long taskId, Long attachmentId);
	
	/**
	 * Get attachments for a task
	 * @param taskId
	 * @return
	 * @throws TaskNotFoundException in case task with given id was not found
	 */
	List<Attachment> getAttachmentsByTaskId(Long taskId);
	
	/**
     * Get attachments for a task
     * 
     * @param deploymentId
     * @param taskId
     * @return
     * @throws TaskNotFoundException in case task with given id was not found or is not associated with given deployment id
     */
    List<Attachment> getAttachmentsByTaskId(String deploymentId, Long taskId);
	
	/**
	 * Returns task instance
	 * @param taskId
	 * @return
	 * @throws TaskNotFoundException in case task with given id was not found
	 */
	Task getTask(Long taskId);
	
	/**
     * Returns task instance
     * 
     * @param deploymentId
     * @param taskId
     * @return
     * @throws TaskNotFoundException in case task with given id was not found or is not associated with given deployment id
     */
    Task getTask(String deploymentId, Long taskId);
	
	
	/**
     * Executes provided command on the underlying command executor
     * @param deploymentId identifier of the deployment that engine should be used for execution
     * @param command actual command for execution
     * @return results of command execution
     */
    public <T> T execute(String deploymentId, Command<T> command);
    
	/**
     * Executes provided command on the underlying command executor
     * @param deploymentId identifier of the deployment that engine should be used for execution
     * @param context context implementation to be used for getting runtime engine
     * @param command actual command for execution
     * @return results of command execution
     */
    public <T> T execute(String deploymentId, Context<?> context, Command<T> command);

}
