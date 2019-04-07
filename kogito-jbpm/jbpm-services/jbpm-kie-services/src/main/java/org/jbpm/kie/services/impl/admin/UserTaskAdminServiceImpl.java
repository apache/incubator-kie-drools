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

package org.jbpm.kie.services.impl.admin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.kie.services.impl.admin.commands.AddPeopleAssignmentsCommand;
import org.jbpm.kie.services.impl.admin.commands.AddTaskInputsCommand;
import org.jbpm.kie.services.impl.admin.commands.CancelTaskDeadlineCommand;
import org.jbpm.kie.services.impl.admin.commands.ListTaskNotificationsCommand;
import org.jbpm.kie.services.impl.admin.commands.ListTaskReassignmentsCommand;
import org.jbpm.kie.services.impl.admin.commands.RemovePeopleAssignmentsCommand;
import org.jbpm.kie.services.impl.admin.commands.RemoveTaskDataCommand;
import org.jbpm.kie.services.impl.admin.commands.ScheduleTaskDeadlineCommand;
import org.jbpm.runtime.manager.impl.AbstractRuntimeManager;
import org.jbpm.services.api.RuntimeDataService;
import org.jbpm.services.api.TaskNotFoundException;
import org.jbpm.services.api.UserTaskService;
import org.jbpm.services.api.admin.ExecutionErrorNotFoundException;
import org.jbpm.services.api.admin.TaskNotification;
import org.jbpm.services.api.admin.TaskReassignment;
import org.jbpm.services.api.admin.UserTaskAdminService;
import org.jbpm.services.api.model.UserTaskInstanceDesc;
import org.jbpm.services.api.service.ServiceRegistry;
import org.jbpm.shared.services.impl.QueryManager;
import org.jbpm.shared.services.impl.TransactionalCommandService;
import org.jbpm.shared.services.impl.commands.QueryNameCommand;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.query.QueryContext;
import org.kie.api.task.model.I18NText;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.internal.identity.IdentityProvider;
import org.kie.internal.runtime.error.ExecutionError;
import org.kie.internal.runtime.manager.RuntimeManagerRegistry;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;
import org.kie.internal.task.api.TaskDeadlinesService.DeadlineType;
import org.kie.internal.task.api.TaskModelProvider;
import org.kie.internal.task.api.model.Deadline;
import org.kie.internal.task.api.model.EmailNotification;
import org.kie.internal.task.api.model.EmailNotificationHeader;
import org.kie.internal.task.api.model.Escalation;
import org.kie.internal.task.api.model.InternalI18NText;
import org.kie.internal.task.api.model.Language;
import org.kie.internal.task.api.model.Notification;
import org.kie.internal.task.api.model.Reassignment;


public class UserTaskAdminServiceImpl implements UserTaskAdminService {
    
    
    public static final int POT_OWNER = 0;
    public static final int EXCL_OWNER = 1;
    public static final int ADMIN = 2;

    private UserTaskService userTaskService;    
    private RuntimeDataService runtimeDataService;
    private IdentityProvider identityProvider;
    
    private TransactionalCommandService commandService;
    
    
    public UserTaskAdminServiceImpl() {
        ServiceRegistry.get().register(UserTaskAdminService.class.getSimpleName(), this);
    }
    
    public void setUserTaskService(UserTaskService userTaskService) {
        this.userTaskService = userTaskService;
    }
    
    public void setRuntimeDataService(RuntimeDataService runtimeDataService) {
        this.runtimeDataService = runtimeDataService;
    }    
    
    public void setIdentityProvider(IdentityProvider identityProvider) {
        this.identityProvider = identityProvider;
    }

    public void setCommandService(TransactionalCommandService commandService) {
        this.commandService = commandService;
    }
    
    @Override
    public void addPotentialOwners(long taskId, boolean removeExisting, OrganizationalEntity... orgEntities) throws TaskNotFoundException {
        addPotentialOwners(null, taskId, removeExisting, orgEntities);
    }
    
    @Override
    public void addPotentialOwners(String deploymentId, long taskId, boolean removeExisting, OrganizationalEntity... orgEntities) throws TaskNotFoundException {
        addPeopleAssignment(deploymentId, taskId, removeExisting, POT_OWNER, orgEntities);
    }

    @Override
    public void addExcludedOwners(long taskId, boolean removeExisting, OrganizationalEntity... orgEntities) throws TaskNotFoundException {
        addExcludedOwners(null, taskId, removeExisting, orgEntities);
    }
    
    @Override
    public void addExcludedOwners(String deploymentId, long taskId, boolean removeExisting, OrganizationalEntity... orgEntities) throws TaskNotFoundException {
        addPeopleAssignment(deploymentId, taskId, removeExisting, EXCL_OWNER, orgEntities);
    }
    
    @Override
    public void addBusinessAdmins(long taskId, boolean removeExisting, OrganizationalEntity... orgEntities) throws TaskNotFoundException {
        addBusinessAdmins(null, taskId, removeExisting, orgEntities);
    }

    @Override
    public void addBusinessAdmins(String deploymentId, long taskId, boolean removeExisting, OrganizationalEntity... orgEntities) throws TaskNotFoundException {
        addPeopleAssignment(deploymentId, taskId, removeExisting, ADMIN, orgEntities);
    }
    
    @Override
    public void removePotentialOwners(long taskId, OrganizationalEntity... orgEntities) throws TaskNotFoundException {
        removePotentialOwners(null, taskId, orgEntities);
    }

    @Override
    public void removePotentialOwners(String deploymentId, long taskId, OrganizationalEntity... orgEntities) throws TaskNotFoundException {
        removePeopleAssignment(deploymentId, taskId, POT_OWNER, orgEntities);
    }
    
    @Override
    public void removeExcludedOwners(long taskId, OrganizationalEntity... orgEntities) throws TaskNotFoundException {
        removeExcludedOwners(null, taskId, orgEntities);
    }

    @Override
    public void removeExcludedOwners(String deploymentId, long taskId, OrganizationalEntity... orgEntities) throws TaskNotFoundException {
        removePeopleAssignment(deploymentId, taskId, EXCL_OWNER, orgEntities);
    }

    @Override
    public void removeBusinessAdmins(long taskId, OrganizationalEntity... orgEntities) throws TaskNotFoundException, IllegalStateException {
        removeBusinessAdmins(null, taskId, orgEntities);
    }
    
    @Override
    public void removeBusinessAdmins(String deploymentId, long taskId, OrganizationalEntity... orgEntities) throws TaskNotFoundException, IllegalStateException {
        removePeopleAssignment(deploymentId, taskId, ADMIN, orgEntities);
    }
    
    @Override
    public void addTaskInput(long taskId, String name, Object value) throws TaskNotFoundException {
        addTaskInput(null, taskId, name, value);
    }

    @Override
    public void addTaskInput(String deploymentId, long taskId, String name, Object value) throws TaskNotFoundException {
        Map<String, Object> data = new HashMap<>();
        data.put(name, value);
        addTaskInputs(deploymentId, taskId, data);
    }
    
    @Override
    public void addTaskInputs(long taskId, Map<String, Object> data) throws TaskNotFoundException {
        addTaskInputs(null, taskId, data);
    }

    @Override
    public void addTaskInputs(String deploymentId, long taskId, Map<String, Object> data) throws TaskNotFoundException {
        UserTaskInstanceDesc task = runtimeDataService.getTaskById(taskId);
        validateTask(deploymentId, taskId, task);

        userTaskService.execute(task.getDeploymentId(), ProcessInstanceIdContext.get(task.getProcessInstanceId()), 
                new AddTaskInputsCommand(identityProvider.getName(), taskId, data));
    }

    @Override
    public void removeTaskInputs(long taskId, String... name) throws TaskNotFoundException {
        removeTaskInputs(null, taskId, name);
    }
    
    @Override
    public void removeTaskInputs(String deploymentId, long taskId, String... name) throws TaskNotFoundException {
        UserTaskInstanceDesc task = runtimeDataService.getTaskById(taskId);
        validateTask(deploymentId, taskId, task);

        userTaskService.execute(task.getDeploymentId(), ProcessInstanceIdContext.get(task.getProcessInstanceId()), 
                new RemoveTaskDataCommand(identityProvider.getName(), taskId, Arrays.asList(name), true));

    }
    
    @Override
    public void removeTaskOutputs(long taskId, String... name) throws TaskNotFoundException {
        removeTaskOutputs(null, taskId, name);
    }
    
    @Override
    public void removeTaskOutputs(String deploymentId, long taskId, String... name) throws TaskNotFoundException {
        UserTaskInstanceDesc task = runtimeDataService.getTaskById(taskId);
        validateTask(deploymentId, taskId, task);

        userTaskService.execute(task.getDeploymentId(), ProcessInstanceIdContext.get(task.getProcessInstanceId()), 
                new RemoveTaskDataCommand(identityProvider.getName(), taskId, Arrays.asList(name), false));

    }
    
    @Override
    public Long reassignWhenNotStarted(long taskId, String timeExpression, OrganizationalEntity... orgEntities) throws TaskNotFoundException {
        return reassignWhenNotStarted(null, taskId, timeExpression, orgEntities);
    }

    @Override
    public Long reassignWhenNotStarted(String deploymentId, long taskId, String timeExpression, OrganizationalEntity... orgEntities) throws TaskNotFoundException {
        return reassign(deploymentId, taskId, timeExpression, DeadlineType.START, orgEntities);
    }

    @Override
    public Long reassignWhenNotCompleted(long taskId, String timeExpression, OrganizationalEntity... orgEntities) throws TaskNotFoundException {
        return reassignWhenNotCompleted(null, taskId, timeExpression, orgEntities);
    }
    
    @Override
    public Long reassignWhenNotCompleted(String deploymentId, long taskId, String timeExpression, OrganizationalEntity... orgEntities) throws TaskNotFoundException {
        return reassign(deploymentId, taskId, timeExpression, DeadlineType.END, orgEntities);

    }

    @Override
    public Long notifyWhenNotStarted(long taskId, String timeExpression, Notification notification) throws TaskNotFoundException {
        return notifyWhenNotStarted(null, taskId, timeExpression, notification);
    }
    
    @Override
    public Long notifyWhenNotStarted(String deploymentId, long taskId, String timeExpression, Notification notification) throws TaskNotFoundException {
        return notify(deploymentId, taskId, timeExpression, DeadlineType.START, notification);

    }
    
    @Override
    public Long notifyWhenNotCompleted(long taskId, String timeExpression, Notification notification) throws TaskNotFoundException {
        return notifyWhenNotCompleted(null, taskId, timeExpression, notification);
    }

    @Override
    public Long notifyWhenNotCompleted(String deploymentId, long taskId, String timeExpression, Notification notification) throws TaskNotFoundException {
        return notify(deploymentId, taskId, timeExpression, DeadlineType.END, notification);

    }
    
    @Override
    public Collection<TaskReassignment> getTaskReassignments(long taskId, boolean activeOnly) throws TaskNotFoundException {
        return getTaskReassignments(null, taskId, activeOnly);
    }
    
    @Override
    public Collection<TaskReassignment> getTaskReassignments(String deploymentId, long taskId, boolean activeOnly) throws TaskNotFoundException {
        UserTaskInstanceDesc task = runtimeDataService.getTaskById(taskId);
        validateTask(deploymentId, taskId, task);

        return userTaskService.execute(task.getDeploymentId(), ProcessInstanceIdContext.get(task.getProcessInstanceId()), 
                new ListTaskReassignmentsCommand(identityProvider.getName(), taskId, activeOnly));
    }

    @Override
    public Collection<TaskNotification> getTaskNotifications(long taskId, boolean activeOnly) throws TaskNotFoundException {
        return getTaskNotifications(null, taskId, activeOnly);
    }
    
    @Override
    public Collection<TaskNotification> getTaskNotifications(String deploymentId, long taskId, boolean activeOnly) throws TaskNotFoundException {
        UserTaskInstanceDesc task = runtimeDataService.getTaskById(taskId);
        validateTask(deploymentId, taskId, task);

        return userTaskService.execute(task.getDeploymentId(), ProcessInstanceIdContext.get(task.getProcessInstanceId()), 
                new ListTaskNotificationsCommand(identityProvider.getName(), taskId, activeOnly));
    }
    
    @Override
    public void cancelNotification(long taskId, long notificationId) throws TaskNotFoundException {
        cancelNotification(null, taskId, notificationId);
    }

    @Override
    public void cancelNotification(String deploymentId, long taskId, long notificationId) throws TaskNotFoundException {
        UserTaskInstanceDesc task = runtimeDataService.getTaskById(taskId);
        validateTask(deploymentId, taskId, task);

        userTaskService.execute(task.getDeploymentId(), ProcessInstanceIdContext.get(task.getProcessInstanceId()), 
                new CancelTaskDeadlineCommand(identityProvider.getName(), taskId, notificationId));
    }

    @Override
    public void cancelReassignment(long taskId, long reassignmentId) throws TaskNotFoundException {
        cancelReassignment(null, taskId, reassignmentId);
    }
    
    @Override
    public void cancelReassignment(String deploymentId, long taskId, long reassignmentId) throws TaskNotFoundException {
        UserTaskInstanceDesc task = runtimeDataService.getTaskById(taskId);
        validateTask(deploymentId, taskId, task);

        userTaskService.execute(task.getDeploymentId(), ProcessInstanceIdContext.get(task.getProcessInstanceId()), 
                new CancelTaskDeadlineCommand(identityProvider.getName(), taskId, reassignmentId));
    }
    
    @Override
    public EmailNotification buildEmailNotification(String subjectStr, List<OrganizationalEntity> recipients, String bodyStr, String fromStr, String replyToStr) {
        EmailNotification emailNotification = TaskModelProvider.getFactory().newEmialNotification();
        
        Map<Language, EmailNotificationHeader> emailHeaders = new HashMap<Language, EmailNotificationHeader>();
        List<I18NText> subjects = new ArrayList<I18NText>();
        List<I18NText> names = new ArrayList<I18NText>();
        
        String locale = "en-UK";

        EmailNotificationHeader emailHeader = TaskModelProvider.getFactory().newEmailNotificationHeader();
        emailHeader.setBody(bodyStr);
        emailHeader.setFrom(fromStr);
        emailHeader.setReplyTo(replyToStr);
        emailHeader.setLanguage(locale);
        emailHeader.setSubject(subjectStr);

        Language lang = TaskModelProvider.getFactory().newLanguage();
        lang.setMapkey(locale);
        emailHeaders.put(lang, emailHeader);

        I18NText subject = TaskModelProvider.getFactory().newI18NText();
        ((InternalI18NText) subject).setLanguage(locale);
        ((InternalI18NText) subject).setText(emailHeader.getSubject());;
        
        subjects.add(subject);
        names.add(subject);
        
        emailNotification.setEmailHeaders(emailHeaders);
        emailNotification.setNames(names);
        emailNotification.setRecipients(new ArrayList<>(recipients));
        emailNotification.setSubjects(subjects);
        
        return emailNotification;
    }
    
    /*
     * Error handling related
     */
    
    @Override
    public List<ExecutionError> getErrors(boolean includeAcknowledged, QueryContext queryContext) {
        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("ack", getAckMode(includeAcknowledged));
        applyQueryContext(params, queryContext);        
        
        List<ExecutionError> execErrors = commandService.execute(new QueryNameCommand<List<ExecutionError>>("getTaskErrors",params));
        return execErrors;
    }
    
    @Override
    public List<ExecutionError> getErrorsByTaskId(long taskId, boolean includeAcknowledged, QueryContext queryContext) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("taskId", taskId);
        params.put("ack", getAckMode(includeAcknowledged));
        applyQueryContext(params, queryContext);        
        
        List<ExecutionError> execErrors = commandService.execute(new QueryNameCommand<List<ExecutionError>>("getErrorsByTaskId",params));        
        return execErrors;
    }

    @Override
    public List<ExecutionError> getErrorsByTaskName(String taskName, boolean includeAcknowledged, QueryContext queryContext) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("taskName", taskName);
        params.put("ack", getAckMode(includeAcknowledged));
        applyQueryContext(params, queryContext);        
        
        List<ExecutionError> execErrors = commandService.execute(new QueryNameCommand<List<ExecutionError>>("getErrorsByTaskName",params));
        return execErrors;
    }

    @Override
    public List<ExecutionError> getErrorsByTaskName(String processId, String taskName, boolean includeAcknowledged, QueryContext queryContext) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("processId", processId);
        params.put("taskName", taskName);
        params.put("ack", getAckMode(includeAcknowledged));
        applyQueryContext(params, queryContext);   
        
        List<ExecutionError> execErrors = commandService.execute(new QueryNameCommand<List<ExecutionError>>("getErrorsByTaskNameProcessId",params));

        return execErrors;
    }

    @Override
    public List<ExecutionError> getErrorsByTaskName(String deploymentId, String processId, String taskName, boolean includeAcknowledged, QueryContext queryContext) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("deploymentId", deploymentId);
        params.put("processId", processId);
        params.put("taskName", taskName);
        params.put("ack", getAckMode(includeAcknowledged));
        applyQueryContext(params, queryContext);  
        
        List<ExecutionError> execErrors = commandService.execute(new QueryNameCommand<List<ExecutionError>>("getErrorsByTaskNameProcessIdDeploymentId",params));

        return execErrors;
    }

    @Override
    public void acknowledgeError(String... errorId) throws ExecutionErrorNotFoundException {
        
        for (String error : errorId) {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("errorId", error);               
            params.put("ack", new Short("0"));
            List<ExecutionError> execErrors = commandService.execute(new QueryNameCommand<List<ExecutionError>>("getErrorById",params));
            
            if (execErrors.isEmpty()) {
                throw new ExecutionErrorNotFoundException("No execution error found for id " + errorId);
            }
            
            ExecutionError errorInstance = execErrors.get(0);
            RuntimeManager runtimeManager = RuntimeManagerRegistry.get().getManager(errorInstance.getDeploymentId());
            if (runtimeManager != null) {
                ((AbstractRuntimeManager) runtimeManager).getExecutionErrorManager().getStorage().acknowledge(identityProvider.getName(), errorInstance.getErrorId());
            }
        }
    }
    
    
    @Override
    public ExecutionError getError(String errorId) throws ExecutionErrorNotFoundException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("errorId", errorId);               
        List<ExecutionError> execErrors = commandService.execute(new QueryNameCommand<List<ExecutionError>>("getErrorByIdSkipAckCheck",params));
        
        if (execErrors.isEmpty()) {
            throw new ExecutionErrorNotFoundException("No execution error found for id " + errorId);
        }
        ExecutionError error = execErrors.get(0);
        return error;
    }
    
    /*
     * Internal methods
     */
    
    
    protected void addPeopleAssignment(String deploymentId, long taskId, boolean removeExisting, int type, OrganizationalEntity... orgEntities) throws TaskNotFoundException {
        UserTaskInstanceDesc task = runtimeDataService.getTaskById(taskId);
        validateTask(deploymentId, taskId, task);

        userTaskService.execute(task.getDeploymentId(), ProcessInstanceIdContext.get(task.getProcessInstanceId()), 
                new AddPeopleAssignmentsCommand(identityProvider.getName(), taskId, type, orgEntities, removeExisting));
    }
    
    protected void removePeopleAssignment(String deploymentId, long taskId, int type, OrganizationalEntity... orgEntities) throws TaskNotFoundException {
        UserTaskInstanceDesc task = runtimeDataService.getTaskById(taskId);
        validateTask(deploymentId, taskId, task);

        userTaskService.execute(task.getDeploymentId(), ProcessInstanceIdContext.get(task.getProcessInstanceId()), 
                new RemovePeopleAssignmentsCommand(identityProvider.getName(), taskId, type, orgEntities));
    }
    
    protected Long reassign(String deploymentId, long taskId, String timeExpression, DeadlineType type, OrganizationalEntity... orgEntities) throws TaskNotFoundException {
        UserTaskInstanceDesc task = runtimeDataService.getTaskById(taskId);
        validateTask(deploymentId, taskId, task);

        if(timeExpression == null || timeExpression.isEmpty()) {
            throw new IllegalArgumentException("Invalid time expression");
        }

        if(orgEntities == null || orgEntities.length <= 0) {
            throw new IllegalArgumentException("Invalid org entity");
        }

        List<Escalation> escalations = new ArrayList<Escalation>();
        Deadline taskDeadline = TaskModelProvider.getFactory().newDeadline();                
        taskDeadline.setEscalations(escalations);        
        
        Escalation escalation = TaskModelProvider.getFactory().newEscalation();
        escalations.add(escalation);        
        escalation.setName("Admin reassignment for task " + taskId);
        
        List<Reassignment> reassignments = new ArrayList<Reassignment>();
        Reassignment reassignment = TaskModelProvider.getFactory().newReassignment();
        reassignment.setPotentialOwners(new ArrayList<>(Arrays.asList(orgEntities)));
        
        reassignments.add(reassignment);
        
        escalation.setReassignments(reassignments);        

        return userTaskService.execute(task.getDeploymentId(), ProcessInstanceIdContext.get(task.getProcessInstanceId()), 
                new ScheduleTaskDeadlineCommand(identityProvider.getName(), taskId, type, taskDeadline, timeExpression));
    }
    
    protected Long notify(String deploymentId, long taskId, String timeExpression, DeadlineType type, Notification notification) throws TaskNotFoundException {
        UserTaskInstanceDesc task = runtimeDataService.getTaskById(taskId);
        validateTask(deploymentId, taskId, task);
        
        List<Escalation> escalations = new ArrayList<Escalation>();
        Deadline taskDeadline = TaskModelProvider.getFactory().newDeadline();                
        taskDeadline.setEscalations(escalations);        
        
        Escalation escalation = TaskModelProvider.getFactory().newEscalation();
        escalations.add(escalation);        
        escalation.setName("Admin notification for task " + taskId);
        
        List<Notification> notifications = new ArrayList<Notification>();
        notifications.add(notification);
        
        escalation.setNotifications(notifications);        

        return userTaskService.execute(task.getDeploymentId(), ProcessInstanceIdContext.get(task.getProcessInstanceId()), 
                new ScheduleTaskDeadlineCommand(identityProvider.getName(), taskId, type, taskDeadline, timeExpression));
    }

    protected void applyQueryContext(Map<String, Object> params, QueryContext queryContext) {
        if (queryContext != null) {
            params.put("firstResult", queryContext.getOffset());
            params.put("maxResults", queryContext.getCount());

            if (queryContext.getOrderBy() != null && !queryContext.getOrderBy().isEmpty()) {
                params.put(QueryManager.ORDER_BY_KEY, queryContext.getOrderBy());

                if (queryContext.isAscending()) {
                    params.put(QueryManager.ASCENDING_KEY, "true");
                } else {
                    params.put(QueryManager.DESCENDING_KEY, "true");
                }
            }
        }
    }

    protected List<Short> getAckMode(boolean includeAcknowledged) {
        List<Short> ackMode = new ArrayList<>();
        ackMode.add(new Short("0"));
        if (includeAcknowledged) {
            ackMode.add(new Short("1"));
        }
        
        return ackMode;
    }
    
    protected void validateTask(String deploymentId, Long taskId, UserTaskInstanceDesc task) {
        if (task == null) {
            throw new TaskNotFoundException("Task with id " + taskId + " was not found");
        }
        
        if (deploymentId != null && !task.getDeploymentId().equals(deploymentId)) {
            throw new TaskNotFoundException("Task with id " + taskId + " is not associated with " + deploymentId);
        }
    }
}
