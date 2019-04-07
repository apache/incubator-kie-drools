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

package org.jbpm.services.api.admin;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.jbpm.services.api.TaskNotFoundException;
import org.kie.api.runtime.query.QueryContext;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.internal.runtime.error.ExecutionError;
import org.kie.internal.task.api.model.EmailNotification;
import org.kie.internal.task.api.model.Notification;

/**
 * Provides administrative operations on top of active tasks.
 * All operations should be performed by eligible user - in most of the case business administrator.
 */
public interface UserTaskAdminService {

    /**
     * Adds new potential owners for a given task. Can be instructed to remove existing potential owners
     * which means that the task will be released in case it was claimed already. 
     * Task must be in active state.
     * @param taskId unique task id
     * @param removeExisting specifies if existing potential owners should be removed 
     * @param orgEntities one or more potential owner to be added to a task
     * @throws TaskNotFoundException thrown when there is no task with given id
     */
    void addPotentialOwners(long taskId, boolean removeExisting, OrganizationalEntity ...orgEntities) throws TaskNotFoundException;
    
    /**
     * Adds new potential owners for a given task. Can be instructed to remove existing potential owners
     * which means that the task will be released in case it was claimed already. 
     * Task must be in active state.
     * 
     * @param deploymentId
     * @param taskId unique task id
     * @param removeExisting specifies if existing potential owners should be removed 
     * @param orgEntities one or more potential owner to be added to a task
     * @throws TaskNotFoundException thrown when there is no task with given id or is not associated with given deployment id
     */
    void addPotentialOwners(String deploymentId, long taskId, boolean removeExisting, OrganizationalEntity ...orgEntities) throws TaskNotFoundException;
    
    /**
     * Adds new excluded owners for a given task. Can be instructed to remove existing excluded owners. 
     * Task must be in active state.
     * @param taskId unique task id
     * @param removeExisting specifies if existing excluded owners should be removed 
     * @param orgEntities one or more excluded owner to be added to a task
     * @throws TaskNotFoundException thrown when there is no task with given id
     */
    void addExcludedOwners(long taskId,  boolean removeExisting, OrganizationalEntity ...orgEntities) throws TaskNotFoundException;
    
    /**
     * Adds new excluded owners for a given task. Can be instructed to remove existing excluded owners. 
     * Task must be in active state.
     * 
     * @param deploymentId
     * @param taskId unique task id
     * @param removeExisting specifies if existing excluded owners should be removed 
     * @param orgEntities one or more excluded owner to be added to a task
     * @throws TaskNotFoundException thrown when there is no task with given id or is not associated with given deployment id
     */
    void addExcludedOwners(String deploymentId, long taskId,  boolean removeExisting, OrganizationalEntity ...orgEntities) throws TaskNotFoundException;
    
    /**
     * Adds new business admin for a given task. Can be instructed to remove existing business admins. 
     * Task must be in active state.
     * @param taskId unique task id
     * @param removeExisting specifies if existing business admins should be removed 
     * @param orgEntities one or more business admin to be added to a task
     * @throws TaskNotFoundException thrown when there is no task with given id
     */
    void addBusinessAdmins(long taskId,  boolean removeExisting, OrganizationalEntity ...orgEntities) throws TaskNotFoundException;
    
    /**
     * Adds new business admin for a given task. Can be instructed to remove existing business admins. 
     * Task must be in active state.
     * 
     * @param deploymentId
     * @param taskId unique task id
     * @param removeExisting specifies if existing business admins should be removed 
     * @param orgEntities one or more business admin to be added to a task
     * @throws TaskNotFoundException thrown when there is no task with given id or is not associated with given deployment id
     */
    void addBusinessAdmins(String deploymentId, long taskId,  boolean removeExisting, OrganizationalEntity ...orgEntities) throws TaskNotFoundException;
    
    /**
     * Remove existing potential owners from given task. Task must be in active state.
     * @param taskId unique task id
     * @param orgEntities one or more potential owner to be removed from a task
     * @throws TaskNotFoundException thrown when there is no task with given id
     */
    void removePotentialOwners(long taskId, OrganizationalEntity ...orgEntities) throws TaskNotFoundException;
    
    /**
     * Remove existing potential owners from given task. Task must be in active state.
     * 
     * @param deploymentId
     * @param taskId unique task id
     * @param orgEntities one or more potential owner to be removed from a task
     * @throws TaskNotFoundException thrown when there is no task with given id or is not associated with given deployment id
     */
    void removePotentialOwners(String deploymentId, long taskId, OrganizationalEntity ...orgEntities) throws TaskNotFoundException;
    
    /**
     * Remove existing excluded owners from given task. Task must be in active state.
     * @param taskId unique task id
     * @param orgEntities one or more excluded owner to be removed from a task
     * @throws TaskNotFoundException thrown when there is no task with given id
     */
    void removeExcludedOwners(long taskId, OrganizationalEntity ...orgEntities) throws TaskNotFoundException;
    
    /**
     * Remove existing excluded owners from given task. Task must be in active state.
     * 
     * @param deploymentId
     * @param taskId unique task id
     * @param orgEntities one or more excluded owner to be removed from a task
     * @throws TaskNotFoundException thrown when there is no task with given id or is not associated with given deployment id
     */
    void removeExcludedOwners(String deploymentId, long taskId, OrganizationalEntity ...orgEntities) throws TaskNotFoundException;
    
    /**
     * Remove existing business admin from given task. Task must be in active state.
     * @param taskId unique task id
     * @param orgEntities one or more business admin to be removed from a task
     * @throws TaskNotFoundException thrown when there is no task with given id
     * @throws IllegalStateException in case there are no business admins left on a task - there must be at least one business admin
     */
    void removeBusinessAdmins(long taskId, OrganizationalEntity ...orgEntities) throws TaskNotFoundException, IllegalStateException;
    
    /**
     * Remove existing business admin from given task. Task must be in active state.
     * 
     * @param deploymentId
     * @param taskId unique task id
     * @param orgEntities one or more business admin to be removed from a task
     * @throws TaskNotFoundException thrown when there is no task with given id or is not associated with given deployment id
     * @throws IllegalStateException in case there are no business admins left on a task - there must be at least one business admin
     */
    void removeBusinessAdmins(String deploymentId, long taskId, OrganizationalEntity ...orgEntities) throws TaskNotFoundException, IllegalStateException;
    
    /**
     * Adds new item to task input variables.
     * @param taskId unique task id
     * @param name name of the input variable to be added
     * @param value value of the input variable
     * @throws TaskNotFoundException thrown when there is no task with given id
     */
    void addTaskInput(long taskId, String name, Object value) throws TaskNotFoundException;
    
    /**
     * Adds new item to task input variables.
     * 
     * @param deploymentId
     * @param taskId unique task id
     * @param name name of the input variable to be added
     * @param value value of the input variable
     * @throws TaskNotFoundException thrown when there is no task with given id or is not associated with given deployment id
     */
    void addTaskInput(String deploymentId, long taskId, String name, Object value) throws TaskNotFoundException;
    
    /**
     * Adds new items to task input variables.
     * @param taskId unique task id
     * @param data map of key (name of the variable) and value (value of the variable)
     * @throws TaskNotFoundException thrown when there is no task with given id
     */
    void addTaskInputs(long taskId, Map<String, Object> data) throws TaskNotFoundException;
    
    /**
     * Adds new items to task input variables.
     * 
     * @param deploymentId
     * @param taskId unique task id
     * @param data map of key (name of the variable) and value (value of the variable)
     * @throws TaskNotFoundException thrown when there is no task with given id or is not associated with given deployment id
     */
    void addTaskInputs(String deploymentId, long taskId, Map<String, Object> data) throws TaskNotFoundException;
    
    /**
     * Removes one or more input variables from a task.
     * @param taskId unique task id
     * @param name name(s) of the input variables to be removed
     * @throws TaskNotFoundException thrown when there is no task with given id
     */
    void removeTaskInputs(long taskId, String ...name) throws TaskNotFoundException;
    
    /**
     * Removes one or more input variables from a task.
     * 
     * @param deploymentId
     * @param taskId unique task id
     * @param name name(s) of the input variables to be removed
     * @throws TaskNotFoundException thrown when there is no task with given id or is not associated with given deployment id
     */
    void removeTaskInputs(String deploymentId, long taskId, String ...name) throws TaskNotFoundException;
    
    /**
     * Removes one or more output variables from a task.
     * @param taskId unique task id
     * @param name name(s) of the output variables to be removed
     * @throws TaskNotFoundException thrown when there is no task with given id
     */
    void removeTaskOutputs(long taskId, String... name) throws TaskNotFoundException;
    
    /**
     * Removes one or more output variables from a task.
     * 
     * @param deploymentId
     * @param taskId unique task id
     * @param name name(s) of the output variables to be removed
     * @throws TaskNotFoundException thrown when there is no task with given id or is not associated with given deployment id
     */
    void removeTaskOutputs(String deploymentId, long taskId, String... name) throws TaskNotFoundException;
    
    /**
     * Reassign automatically a task in case it was not started before time given as timeExpression elapses.
     * @param taskId unique task id
     * @param timeExpression time expression in duration format as 2s, 5h, 7d
     * @param orgEntities users/groups that will be reassigned to after conditions are met
     * @throws TaskNotFoundException thrown when there is no task with given id
     */
    Long reassignWhenNotStarted(long taskId, String timeExpression, OrganizationalEntity ...orgEntities) throws TaskNotFoundException;
    
    /**
     * Reassign automatically a task in case it was not started before time given as timeExpression elapses.
     * 
     * @param deploymentId
     * @param taskId unique task id
     * @param timeExpression time expression in duration format as 2s, 5h, 7d
     * @param orgEntities users/groups that will be reassigned to after conditions are met
     * @throws TaskNotFoundException thrown when there is no task with given id or is not associated with given deployment id
     */
    Long reassignWhenNotStarted(String deploymentId, long taskId, String timeExpression, OrganizationalEntity ...orgEntities) throws TaskNotFoundException;
    
    /**
     * Reassign automatically a task in case it was not completed before time given as timeExpression elapses.
     * @param taskId unique task id
     * @param timeExpression time expression in duration format as 2s, 5h, 7d
     * @param orgEntities users/groups that will be reassigned to after conditions are met
     * @throws TaskNotFoundException thrown when there is no task with given id
     */
    Long reassignWhenNotCompleted(long taskId, String timeExpression, OrganizationalEntity ...orgEntities) throws TaskNotFoundException;
    
    /**
     * Reassign automatically a task in case it was not completed before time given as timeExpression elapses.
     * 
     * @param deploymentId
     * @param taskId unique task id
     * @param timeExpression time expression in duration format as 2s, 5h, 7d
     * @param orgEntities users/groups that will be reassigned to after conditions are met
     * @throws TaskNotFoundException thrown when there is no task with given id or is not associated with given deployment id
     */
    Long reassignWhenNotCompleted(String deploymentId, long taskId, String timeExpression, OrganizationalEntity ...orgEntities) throws TaskNotFoundException;
    
    /**
     * Sends notification (of notification type) to given recipients in case task was not started 
     * before time given as timeExpression elapses.
     * @param taskId unique task id
     * @param timeExpression time expression in duration format as 2s, 5h, 7d
     * @param notification actual notification to be sent
     * @throws TaskNotFoundException thrown when there is no task with given id
     */
    Long notifyWhenNotStarted(long taskId, String timeExpression, Notification notification) throws TaskNotFoundException;
    
    /**
     * Sends notification (of notification type) to given recipients in case task was not started 
     * before time given as timeExpression elapses.
     * 
     * @param deploymentId
     * @param taskId unique task id
     * @param timeExpression time expression in duration format as 2s, 5h, 7d
     * @param notification actual notification to be sent
     * @throws TaskNotFoundException thrown when there is no task with given id or is not associated with given deployment id
     */
    Long notifyWhenNotStarted(String deploymentId, long taskId, String timeExpression, Notification notification) throws TaskNotFoundException;
    
    /**
     * Sends notification (of notification type) to given recipients in case task was not completed 
     * before time given as timeExpression elapses.
     * @param taskId unique task id
     * @param timeExpression time expression in duration format as 2s, 5h, 7d
     * @param notification actual notification to be sent
     * @throws TaskNotFoundException thrown when there is no task with given id
     */
    Long notifyWhenNotCompleted(long taskId, String timeExpression, Notification notification) throws TaskNotFoundException;
    
    /**
     * Sends notification (of notification type) to given recipients in case task was not completed 
     * before time given as timeExpression elapses.
     * 
     * @param deploymentId
     * @param taskId unique task id
     * @param timeExpression time expression in duration format as 2s, 5h, 7d
     * @param notification actual notification to be sent
     * @throws TaskNotFoundException thrown when there is no task with given id or is not associated with given deployment id
     */
    Long notifyWhenNotCompleted(String deploymentId, long taskId, String timeExpression, Notification notification) throws TaskNotFoundException;
    
    /**
     * Builds email notification based on given parameters that can be used as argument for notifyWhenNotStarted and notifyWhenNotCompleted methods
     * @param subjectStr email subject
     * @param recipients list of recipients to be included in the notification, must be at least one
     * @param bodyStr email body, can reference task variables
     * @param fromStr user who sends the email
     * @param replyToStr optional reply to address
     * @return completely configured email notification instance
     */
    EmailNotification buildEmailNotification(String subjectStr, List<OrganizationalEntity> recipients, String bodyStr, String fromStr, String replyToStr);
    
    /**
     * Returns task reassignments scheduled for given task
     * @param taskId unique task id
     * @param activeOnly determines if only active (not yet fired) should be returned
     * @return returns list of task reassignments for given task
     * @throws TaskNotFoundException thrown when there is no task with given id
     */
    Collection<TaskReassignment> getTaskReassignments(long taskId, boolean activeOnly) throws TaskNotFoundException;
    
    /**
     * Returns task reassignments scheduled for given task
     * 
     * @param deploymentId
     * @param taskId unique task id
     * @param activeOnly determines if only active (not yet fired) should be returned
     * @return returns list of task reassignments for given task
     * @throws TaskNotFoundException thrown when there is no task with given id or is not associated with given deployment id
     */
    Collection<TaskReassignment> getTaskReassignments(String deploymentId, long taskId, boolean activeOnly) throws TaskNotFoundException;
    
    /**
     * Returns task notifications scheduled for given task
     * @param taskId unique task id
     * @param activeOnly determines if only active (not yet fired) should be returned
     * @return returns list of task notifications for given task
     * @throws TaskNotFoundException thrown when there is no task with given id
     */
    Collection<TaskNotification> getTaskNotifications(long taskId, boolean activeOnly) throws TaskNotFoundException;
    
    /**
     * Returns task notifications scheduled for given task
     * 
     * @param deploymentId
     * @param taskId unique task id
     * @param activeOnly determines if only active (not yet fired) should be returned
     * @return returns list of task notifications for given task
     * @throws TaskNotFoundException thrown when there is no task with given id or is not associated with given deployment id
     */
    Collection<TaskNotification> getTaskNotifications(String deploymentId, long taskId, boolean activeOnly) throws TaskNotFoundException;
    
    /**
     * Cancels given notification on a task
     * @param taskId unique task id
     * @param notificationId unique notification id
     * @throws TaskNotFoundException thrown when there is no task with given id
     */
    void cancelNotification(long taskId, long notificationId) throws TaskNotFoundException;
    
    /**
     * Cancels given notification on a task
     * 
     * @param deploymentId
     * @param taskId unique task id
     * @param notificationId unique notification id
     * @throws TaskNotFoundException thrown when there is no task with given id or is not associated with given deployment id
     */
    void cancelNotification(String deploymentId, long taskId, long notificationId) throws TaskNotFoundException;
    
    /**
     * Cancels given reassignment on a task
     * @param taskId unique task id
     * @param reassignmentId unique reassignment id
     * @throws TaskNotFoundException thrown when there is no task with given id
     */
    void cancelReassignment(long taskId, long reassignmentId) throws TaskNotFoundException;
    
    /**
     * Cancels given reassignment on a task
     * 
     * @param deploymentId
     * @param taskId unique task id
     * @param reassignmentId unique reassignment id
     * @throws TaskNotFoundException thrown when there is no task with given id or is not associated with given deployment id
     */
    void cancelReassignment(String deploymentId, long taskId, long reassignmentId) throws TaskNotFoundException;
    
    /**
     * Returns execution errors for given task id
     * @param taskId unique task id
     * @param includeAcknowledged indicates whether to include acknowledged errors or not
     * @param queryContext control parameters for pagination 
     * @return list of found errors
     */
    List<ExecutionError> getErrorsByTaskId(long taskId, boolean includeAcknowledged, QueryContext queryContext);
    
    /**
     * Returns execution errors for given task name
     * @param taskName name of the task
     * @param includeAcknowledged indicates whether to include acknowledged errors or not
     * @param queryContext control parameters for pagination 
     * @return list of found errors
     */
    List<ExecutionError> getErrorsByTaskName(String taskName, boolean includeAcknowledged, QueryContext queryContext);
    
    /**
     * Returns execution errors for given task name and process id
     * @param processId process id of the process that task belongs to
     * @param taskName name of the task
     * @param includeAcknowledged indicates whether to include acknowledged errors or not
     * @param queryContext control parameters for pagination 
     * @return list of found errors
     */
    List<ExecutionError> getErrorsByTaskName(String processId, String taskName, boolean includeAcknowledged, QueryContext queryContext);
    
    /**
     * Returns execution errors for given task name, process id and deployment id
     * @param deploymentId deployment id that contains given process
     * @param processId process id of the process that task belongs to
     * @param taskName name of the task
     * @param includeAcknowledged indicates whether to include acknowledged errors or not
     * @param queryContext control parameters for pagination 
     * @return list of found errors
     */
    List<ExecutionError> getErrorsByTaskName(String deploymentId, String processId, String taskName, boolean includeAcknowledged, QueryContext queryContext);
    
    /**
     * Acknowledge given error that it was reviewed and understood
     * @param errorId unique id of the error
     * @throws ExecutionErrorNotFoundException thrown when there is no unacknowledged error with that id
     */
    void acknowledgeError(String... errorId) throws ExecutionErrorNotFoundException;
    
    /**
     * Returns execution error identified by given error id
     * @param errorId unique id of the error
     * @return returns execution error instance
     * @throws ExecutionErrorNotFoundException is thrown in case no error was found for given error id
     */
    ExecutionError getError(String errorId) throws ExecutionErrorNotFoundException;
    
    /**
     * Returns execution errors that are classified as task type errors
     * @param includeAcknowledged indicates whether to include acknowledged errors or not
     * @param queryContext control parameters for pagination
     * @return list of found errors
     */
    List<ExecutionError> getErrors(boolean includeAcknowledged, QueryContext queryContext);
}
