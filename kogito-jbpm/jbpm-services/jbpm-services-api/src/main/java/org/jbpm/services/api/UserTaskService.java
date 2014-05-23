/*
 * Copyright 2014 JBoss by Red Hat.
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

package org.jbpm.services.api;

import java.util.Date;
import java.util.List;
import java.util.Map;

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
	 */
	void activate(Long taskId, String userId);

	/**
	 * Claim responsibility for a task, i.e. set the task to status Reserved
	 * 
	 * @param taskId
	 * @param userId
	 */
	void claim(Long taskId, String userId);


	/**
	 * Complete a task with the given data
	 * 
	 * @param taskId
	 * @param userId
	 * @param data
	 */
	void complete(Long taskId, String userId, Map<String, Object> params);
	

	/**
	 * 
	 * Delegate a task from userId to targetUserId
	 * 
	 * @param taskId
	 * @param userId
	 * @param targetUserId
	 */
	void delegate(Long taskId, String userId, String targetUserId);

	/**
	 * Requesting application is no longer interested in the task output
	 * 
	 * 
	 * @param taskId
	 * @param userId
	 */
	void exit(Long taskId, String userId);

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
	 */
	void fail(Long taskId, String userId, Map<String, Object> faultData);

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
	 */
	void forward(Long taskId, String userId, String targetEntityId);

	/**
	 * Release a previously claimed task
	 * 
	 * @param taskId
	 * @param userId
	 */
	void release(Long taskId, String userId);
	

	/**
	 * Resume a previously suspended task
	 * 
	 * @param taskId
	 * @param userId
	 */
	void resume(Long taskId, String userId);

	/**
	 * Skip a claimed task
	 * 
	 * @param taskId
	 * @param userId
	 */
	void skip(Long taskId, String userId);

	/**
	 * Start the execution of the task, i.e. set the task to status InProgress.
	 * 
	 * @param taskId
	 * @param userId
	 */
	void start(Long taskId, String userId);
	

	/**
	 * Cancel/stop the processing of the task. The task returns to the Reserved
	 * state.
	 * 
	 * 
	 * @param taskId
	 * @param userId
	 */
	void stop(Long taskId, String userId);

	/**
	 * Suspend a claimed task.
	 * 
	 * @param taskId
	 * @param userId
	 */
	void suspend(Long taskId, String userId);

	/**
	 * Nominate a task to be handled by potentialOwners
	 * 
	 * @param taskId
	 * @param userId
	 * @param potentialOwners
	 */
	void nominate(Long taskId, String userId, List<OrganizationalEntity> potentialOwners);
	
	// user task attributes operations
	
	/**
	 * Sets priority of a task to given value
	 * @param taskId
	 * @param priority
	 */
    void setPriority(Long taskId, int priority);

    /**
     * Sets expiration date of a task to given value
     * @param taskId
     * @param date
     */
    void setExpirationDate(Long taskId, Date date);

    /**
     * Sets skipable property of a task to given value
     * @param taskId
     * @param skipable
     */
    void setSkipable(Long taskId, boolean skipable);
    
    /**
     * Sets name of the task
     * @param taskId
     * @param name
     */
    void setName(Long taskId, String name);
    
    /**
     * Sets description of the task
     * @param taskId
     * @param description
     */
    void setDescription(Long taskId, String description);

	
	// user task instance content operations
	
    /**
     * Saves gives values as content of a task, applies to task output as input cannot be altered
     * @param taskId
     * @param values
     * @return
     */
    Long saveContent(Long taskId, Map<String, Object> values);

    /**
     * Returns task output data currently stored, might be null of no data is stored
     * @param taskId
     * @return
     */
    Map<String, Object> getTaskOutputContentByTaskId(Long taskId);

    /**
     * Returns task input data of a task
     * @param taskId
     * @return
     */
    Map<String, Object> getTaskInputContentByTaskId(Long taskId);
    
    /**
     * Deletes content given by <code>contentId</code> from given task
     * @param taskId
     * @param contentId
     */
    void deleteContent(Long taskId, Long contentId);
    
    // user task comments operations

    /**
     * Add comment to a task
     * @param taskId
     * @param text
     * @param addedBy
     * @param addedOn
     * @return
     */
    Long addComment(Long taskId, String text, String addedBy, Date addedOn);

    /**
     * Deletes comment from a task
     * @param taskId
     * @param commentId
     */
    void deleteComment(Long taskId, Long commentId);

    /**
     * Get comments for a task
     * @param taskId
     * @return
     */
    List<Comment> getCommentsByTaskId(Long taskId);

    /**
     * Get comment by identifier
     * @param commentId
     * @return
     */
    Comment getCommentById(Long taskId, Long commentId);
    
    // user task attachment operations
    
    /**
     * Add attachment to a task
     * @param taskId
     * @param userId
     * @param attachment
     * @return
     */
    Long addAttachment(Long taskId, String userId, Object attachment);
    
    /**
     * Delete attachment from a task
     * @param taskId
     * @param attachmentId
     */
    void deleteAttachment(Long taskId, Long attachmentId);
    
	/**
	 * Get attachment by identifier
	 * @param attachmentId
	 * @return
	 */
	Attachment getAttachmentById(Long taskId, Long attachmentId);
	
	/**
	 * Get attachments for a task
	 * @param taskId
	 * @return
	 */
	List<Attachment> getAttachmentsByTaskId(Long taskId);
	
	/**
	 * Returns task instance
	 * @param taskId
	 * @return
	 */
	Task getTask(Long taskId);
	
	
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
