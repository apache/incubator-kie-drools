/**
 * Copyright 2010 JBoss Inc
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

package org.jbpm.task.service;

import static org.jbpm.task.service.persistence.TaskPersistenceManager.addParametersToMap;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;

import org.drools.RuleBase;
import org.drools.StatefulSession;
import org.drools.core.util.StringUtils;
import org.jbpm.task.Attachment;
import org.jbpm.task.Comment;
import org.jbpm.task.Content;
import org.jbpm.task.Deadline;
import org.jbpm.task.Deadlines;
import org.jbpm.task.Escalation;
import org.jbpm.task.Group;
import org.jbpm.task.Notification;
import org.jbpm.task.OrganizationalEntity;
import org.jbpm.task.PeopleAssignments;
import org.jbpm.task.Reassignment;
import org.jbpm.task.Status;
import org.jbpm.task.SubTasksStrategy;
import org.jbpm.task.Task;
import org.jbpm.task.TaskData;
import org.jbpm.task.User;
import org.jbpm.task.identity.UserGroupCallback;
import org.jbpm.task.identity.UserGroupCallbackManager;
import org.jbpm.task.query.DeadlineSummary;
import org.jbpm.task.query.TaskSummary;
import org.jbpm.task.service.TaskService.ScheduledTaskDeadline;
import org.jbpm.task.service.persistence.TaskPersistenceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskServiceSession {

    private final TaskPersistenceManager tpm;
    private final TaskService service;
    
    private Map<String, RuleBase> ruleBases;
    private Map<String, Map<String, Object>> globals;
    private Map<String, Boolean> userGroupsMap = new HashMap<String, Boolean>();
    
    private static final Logger logger = LoggerFactory.getLogger(TaskServiceSession.class);

    public TaskServiceSession(final TaskService service, final TaskPersistenceManager tpm) {
        this.service = service;
        this.tpm = tpm;
    }
    
    public void dispose() {
        tpm.dispose();
        if( ruleBases != null ) { 
            ruleBases.clear();
            ruleBases = null;
        }
        if( globals != null ) { 
            globals.clear();
            globals = null;
        }
        if( userGroupsMap != null ) { 
            userGroupsMap.clear();
            userGroupsMap = null;
        }
    }

    public TaskPersistenceManager getTaskPersistenceManager() { 
        return tpm;
    }
    
    public org.jbpm.task.service.TaskService getService() {
        return service;
    }
    
    public void setRuleBase(final String type, final RuleBase ruleBase) {
        if (ruleBases == null) {
            ruleBases = new HashMap<String, RuleBase>();
        }
        ruleBases.put(type, ruleBase);
    }

    public void setGlobals(final String type, final Map<String, Object> globals) {
        if (this.globals == null) {
            this.globals = new HashMap<String, Map<String, Object>>();
        }
        this.globals.put(type, globals);
    }

    public void addUser(final User user) {
        if (!this.tpm.userExists(user.getId())) {
            persistInTransaction(user);
        } else {
            logger.warn("User " + user.getId() + " already exists in Task Server");
        }
    }

    public void addGroup(final Group group) {
        if (!this.tpm.groupExists(group.getId())) {
            persistInTransaction(group);
        } else {
            logger.warn("Group " + group.getId() + " already exists in Task Server");
        }
    }

    /**
     * Runs any custom rules against the specified Task and ContentData to ensure that the
     * task is allowed to be added. If the task cannot be added, a <code>CannotAddTaskException</code>
     * will be thrown.
     * @param task task that is being added
     * @param contentData content data for task
     * @throws CannotAddTaskException throw if the task is not allowed to be added
     */
    private void executeTaskAddRules(final Task task, final ContentData contentData)
        throws CannotAddTaskException
    {
        RuleBase ruleBase = ruleBases.get("addTask");
        if (ruleBase != null) {
            StatefulSession session = ruleBase.newStatefulSession();
            Map<String, Object> globals = this.globals.get("addTask");
            if (globals != null) {
                for (Map.Entry<String, Object> entry : globals.entrySet()) {
                    session.setGlobal(entry.getKey(), entry.getValue());
                }
            }
            TaskServiceRequest request = new TaskServiceRequest("addTask", null, null);
            session.setGlobal("request", request);
            session.insert(task);
            session.insert(contentData);
            session.fireAllRules();

            if (!request.isAllowed()) {
                StringBuilder error = new StringBuilder("Cannot add Task:\n");
                if (request.getReasons() != null) {
                    for (String reason : request.getReasons()) {
                        error.append( reason).append('\n');
                    }
                }

                throw new CannotAddTaskException(error.toString());
            }
        }
    }

    public void addTask(final Task task, final ContentData contentData)
        throws CannotAddTaskException {
        
        doCallbackOperationForPeopleAssignments(task.getPeopleAssignments());
        doCallbackOperationForTaskData(task.getTaskData());
        doCallbackOperationForTaskDeadlines(task.getDeadlines());
        
        final TaskData taskData = task.getTaskData();
        // initialize the task data
        Status currentStatus = taskData.initialize();

        if (ruleBases != null) {
            executeTaskAddRules(task, contentData);
        }

        // than assign the TaskData an owner and status based on the task assignments
        PeopleAssignments assignments = task.getPeopleAssignments();
        if (assignments != null) {
            List<OrganizationalEntity> potentialOwners = assignments.getPotentialOwners();
            currentStatus = taskData.assignOwnerAndStatus(potentialOwners);
        }
        
        doOperationInTransaction(new TransactedOperation() {
            public void doOperation() {
                tpm.saveEntity(task);

                if (contentData != null) {
                    Content content = new Content(contentData.getContent());
                    tpm.saveEntity(content);

                    task.getTaskData().setDocument(content.getId(), contentData);
                }
            }
        });

        // schedule after it's been persisted, otherwise the id's won't be assigned
        if (task.getDeadlines() != null) {
            scheduleTask(task);
        }
        if (currentStatus == Status.Ready) {
            // trigger event support
            String actualOwner = "";
            if(task.getTaskData().getActualOwner() != null){
                actualOwner = task.getTaskData().getActualOwner().getId();
            }
            service.getEventSupport().fireTaskCreated(task.getId(), actualOwner, task.getTaskData().getProcessSessionId());
        }
        
        if (currentStatus == Status.Reserved) {
            // Task was reserved so owner should get icals
            SendIcal.getInstance().sendIcalForTask(task, service.getUserinfo());

            // trigger event support
            service.getEventSupport().fireTaskClaimed(task.getId(), task.getTaskData().getActualOwner().getId(), task.getTaskData().getProcessSessionId());
        }
    }

    private void scheduleTask(final Task task) {
        final long now = System.currentTimeMillis();

        final List<Deadline> startDeadlines = task.getDeadlines().getStartDeadlines();

        if (startDeadlines != null) {
            scheduleDeadlines(startDeadlines, now, task.getId());
        }

        final List<Deadline> endDeadlines = task.getDeadlines().getEndDeadlines();

        if (endDeadlines != null) {
            scheduleDeadlines(endDeadlines, now, task.getId());
        }
    }

    public void scheduleUnescalatedDeadlines() { 
        long now = System.currentTimeMillis();
        for (DeadlineSummary summary : tpm.getUnescalatedDeadlines() ) { 
            ScheduledTaskDeadline deadline = new ScheduledTaskDeadline(summary.getTaskId(),
                                                                       summary.getDeadlineId(),
                                                                       this.service);
            long delay =  summary.getDate().getTime() - now;
            this.service.schedule(deadline, delay);
        }
    }
    
    private void scheduleDeadlines(final List<Deadline> deadlines, final long now, final long taskId) {
        for (Deadline deadline : deadlines) {
            if (!deadline.isEscalated()) {
                // only escalate when true - typically this would only be true
                // if the user is requested that the notification should never be escalated
                Date date = deadline.getDate();
                service.schedule(new ScheduledTaskDeadline(taskId, deadline.getId(), service), date.getTime() - now);
            }
        }
    }

    void evalCommand(final Operation operation, final List<OperationCommand> commands, final Task task,
                     final User user, final OrganizationalEntity targetEntity,
                     List<String> groupIds) throws PermissionDeniedException {

        final TaskData taskData = task.getTaskData();
        boolean statusMatched = false;

        for (OperationCommand command : commands) {
            // first find out if we have a matching status
            if (command.getStatus() != null) {
                for (Status status : command.getStatus()) {
                    if (taskData.getStatus() == status) {
                        statusMatched = true;
                        // next find out if the user can execute this doOperation
                        if (!isAllowed(command, task, user, groupIds)) { 
                            String errorMessage = "User '" + user + "' does not have permissions to execution operation '" + operation + "' on task id " + task.getId();

                            throw new PermissionDeniedException(errorMessage);
                        }

                        commands(command, task, user, targetEntity);
                    }
                    else { 
                        logger.debug( "No match on status for task " + task.getId() + ": status " + taskData.getStatus() + " != " + status);
                    }
                }
            }

            if (command.getPreviousStatus() != null) {
                for (Status status : command.getPreviousStatus()) {
                    if (taskData.getPreviousStatus() == status) {
                        statusMatched = true;

                        // next find out if the user can execute this doOperation
                        if (!isAllowed(command, task, user, groupIds)) {
                            String errorMessage = "User '" + user + "' does not have permissions to execution operation '" + operation + "' on task id " + task.getId();
                            throw new PermissionDeniedException(errorMessage);
                        }

                        commands(command, task, user, targetEntity);
                    }
                    else { 
                        logger.debug( "No match on previous status for task " + task.getId() + ": status " + taskData.getStatus() + " != " + status);
                    }
                }
            }
        }
        if (!statusMatched) {
            String errorMessage = "User '" + user + "' was unable to execution operation '" + operation + "' on task id " + task.getId() + " due to a no 'current status' match";
            throw new PermissionDeniedException(errorMessage);
        }
    }

    private boolean isAllowed(final OperationCommand command, final Task task, final User user,
    		                         List<String> groupIds) {
        final PeopleAssignments people = task.getPeopleAssignments();
        final TaskData taskData = task.getTaskData();
        
        boolean operationAllowed = false;
        for (Allowed allowed : command.getAllowed()) {
            if (operationAllowed) {
                break;
            }
            switch (allowed) {
                case Owner: {
                    operationAllowed = (taskData.getActualOwner() != null && taskData.getActualOwner().equals(user));
                    break;
                }
                case Initiator: {
                    operationAllowed = (taskData.getCreatedBy() != null && 
                		(taskData.getCreatedBy().equals(user)) 
                		 || (groupIds != null && groupIds.contains(taskData.getCreatedBy().getId())));
                    break;
                }
                case PotentialOwner: {
                    operationAllowed = isAllowed(user, groupIds, people.getPotentialOwners());
                    break;
                }
                case BusinessAdministrator: {
                    operationAllowed = isAllowed(user, groupIds, people.getBusinessAdministrators());
                    break;
                }
                case Anyone: {
                	operationAllowed = true;
                	break;
                }
            }
        }

        if (operationAllowed && command.isUserIsExplicitPotentialOwner()) {
            // if user has rights to execute the command, make sure user is explicitly specified (not as a group)
            operationAllowed = people.getPotentialOwners().contains(user);
        }

        if (operationAllowed && command.isSkippable()) {
            operationAllowed = taskData.isSkipable();
        }

        return operationAllowed;
    }

    private void commands(final OperationCommand command, final Task task, final User user,
                          final OrganizationalEntity targetEntity) {
        final PeopleAssignments people = task.getPeopleAssignments();
        final TaskData taskData = task.getTaskData();

        if (command.getNewStatus() != null) {
            taskData.setStatus(command.getNewStatus());
        } else if (command.isSetToPreviousStatus()) {
            taskData.setStatus(taskData.getPreviousStatus());
        }

        if (command.isAddTargetEntityToPotentialOwners() && !people.getPotentialOwners().contains(targetEntity)) {
            people.getPotentialOwners().add(targetEntity);
        }

        if (command.isRemoveUserFromPotentialOwners()) {
            people.getPotentialOwners().remove(user);
        }

        if (command.isSetNewOwnerToUser()) {
            taskData.setActualOwner(user);
        }

        if (command.isSetNewOwnerToNull()) {
            taskData.setActualOwner(null);
        }

        if (command.getExec() != null) {
            switch (command.getExec()) {
                case Claim: {
                    taskData.setActualOwner((User) targetEntity);
                    // Task was reserved so owner should get icals
                    SendIcal.getInstance().sendIcalForTask(task, service.getUserinfo());

                    // trigger event support
                    service.getEventSupport().fireTaskClaimed(task.getId(),
                            task.getTaskData().getActualOwner().getId(), task.getTaskData().getProcessSessionId());
                    break;
                }
            }
        }
    }

    public void taskOperation(final Operation operation, final long taskId, final String userId,
                              final String targetEntityId, final ContentData data,
                              List<String> groupIds) throws TaskException {
        OrganizationalEntity targetEntity = null;

        groupIds = doUserGroupCallbackOperation(userId, groupIds);
        doCallbackUserOperation(targetEntityId);
        if (targetEntityId != null) {
            targetEntity = getEntity(OrganizationalEntity.class, targetEntityId);
        }

        final Task task = getTask(taskId);
        User user = getEntity(User.class, userId);
        
        boolean transactionOwner = false;
        try {
            final List<OperationCommand> commands = service.getCommandsForOperation(operation);

            transactionOwner = tpm.beginTransaction();

            evalCommand(operation, commands, task, user, targetEntity, groupIds);

            switch (operation) {
                case Claim: {
                    taskClaimOperation(task);
                    break;
                }
                case Complete: {
                    taskCompleteOperation(task, data);
                    break;
                }
                case Fail: {
                    taskFailOperation(task, data);
                    break;
                }
                case Skip: {
                    taskSkipOperation(task, userId);
                    break;
                }
                case Remove: {
                	taskRemoveOperation(task, user);
                	break;
                }
                case Register: {
                	taskRegisterOperation(task, user);
                	break;
                }
            }
            
            tpm.endTransaction(transactionOwner);
            
        } catch (RuntimeException re) {
            
            // We may not be the tx owner -- but something has gone wrong.
            // ..which is why we make ourselves owner, and roll the tx back. 
            boolean takeOverTransaction = true;
            tpm.rollBackTransaction(takeOverTransaction);

            doOperationInTransaction(new TransactedOperation() {
                public void doOperation() {
                    task.getTaskData().setStatus(Status.Error);
                }
            });

            throw re;
        } 

        switch (operation) {
            case Start: {
                postTaskStartOperation(task);
                break;
            }
            case Forward: {
                postTaskForwardOperation(task);
                break;
            }
            case Release: {
                postTaskReleaseOperation(task);
                break;
            }
            case Stop: {
                postTaskStopOperation(task);
                break;
            }
            case Claim: {
                postTaskClaimOperation(task);
                break;
            }
            case Complete: {
                postTaskCompleteOperation(task);
                break;
            }
            case Fail: {
                postTaskFailOperation(task);
                break;
            }
            case Skip: {
                postTaskSkipOperation(task, userId);
                break;
            }
            case Exit: {
                postTaskExitOperation(task, userId);
                break;
            }
        }

    }

    private void taskClaimOperation(final Task task) {
        // Task was reserved so owner should get icals
        SendIcal.getInstance().sendIcalForTask(task, service.getUserinfo());
    }
    
    private void postTaskClaimOperation(final Task task) {
        // trigger event support
        service.getEventSupport().fireTaskClaimed(task.getId(), task.getTaskData().getActualOwner().getId(),
                task.getTaskData().getProcessSessionId());
    }
    
    private void postTaskStartOperation(final Task task) {
        // trigger event support
        service.getEventSupport().fireTaskStarted(task.getId(), task.getTaskData().getActualOwner().getId(),
                task.getTaskData().getProcessSessionId());
    }
    
    private void postTaskForwardOperation(final Task task) {
        // trigger event support
        String actualOwner = "";
        if(task.getTaskData().getActualOwner() != null){
            actualOwner = task.getTaskData().getActualOwner().getId();
        }
        service.getEventSupport().fireTaskForwarded(task.getId(), actualOwner,
                task.getTaskData().getProcessSessionId());
    }
    
    private void postTaskReleaseOperation(final Task task) {
        // trigger event support
        String actualOwner = "";
        if(task.getTaskData().getActualOwner() != null){
            actualOwner = task.getTaskData().getActualOwner().getId();
        }
        service.getEventSupport().fireTaskReleased(task.getId(), actualOwner,
                task.getTaskData().getProcessSessionId());
    }
    
    private void postTaskStopOperation(final Task task) {
        // trigger event support
        service.getEventSupport().fireTaskStopped(task.getId(), task.getTaskData().getActualOwner().getId(),
                task.getTaskData().getProcessSessionId());
    }

    private void taskCompleteOperation(final Task task, final ContentData data) {
        task.getTaskData().setCompletedOn(new Date());
        if (data != null) {
        	setOutput(task.getId(), task.getTaskData().getActualOwner().getId(), data);
        }
        checkSubTaskStrategy(task);
    }
    
    private void postTaskCompleteOperation(final Task task) {
        service.unschedule(task.getId());
        clearDeadlines(task);
        // trigger event support
        service.getEventSupport().fireTaskCompleted(task.getId(), task.getTaskData().getActualOwner().getId(),
                task.getTaskData().getProcessSessionId());
    }

    private void taskFailOperation(final Task task, final ContentData data) {
        // set fault data
        if (data != null) {
        	setFault(task.getId(), task.getTaskData().getActualOwner().getId(), (FaultData) data);
        }
    }
    
    private void postTaskFailOperation(final Task task) {
        service.unschedule(task.getId());
        clearDeadlines(task);
    	// trigger event support
        service.getEventSupport().fireTaskFailed(task.getId(), task.getTaskData().getActualOwner().getId(),
                task.getTaskData().getProcessSessionId());
    }

    private void taskSkipOperation(final Task task, final String userId) {
        checkSubTaskStrategy(task);
    }

    private void postTaskSkipOperation(final Task task, final String userId) {
        service.unschedule(task.getId());
        clearDeadlines(task);
        // trigger event support
        service.getEventSupport().fireTaskSkipped(task.getId(), userId, task.getTaskData().getProcessSessionId());
    }
    
    private void postTaskExitOperation(final Task task, final String userId) {
        service.unschedule(task.getId());
        clearDeadlines(task);
    }
    
    public Task getTask(final long taskId) {
        return getEntity(Task.class, taskId);
    }

    public Deadline getDeadline(final long deadlineId) { 
        return (Deadline) tpm.findEntity(Deadline.class, deadlineId);
    }
    
    public void setTaskStatus(final long taskId, Status status) { 
        tpm.setTaskStatusInTransaction(taskId, status);
    }
    
    public void addComment(final long taskId, final Comment comment) {
        final Task task = getTask(taskId);
        doCallbackOperationForComment(comment);
        
        doOperationInTransaction(new TransactedOperation() {
            public void doOperation() {
                task.getTaskData().addComment(comment);
            }
        });
    }

    public void addAttachment(final long taskId, final Attachment attachment, final Content content) {
        final Task task = getTask(taskId);
        doCallbackOperationForAttachment(attachment);

        doOperationInTransaction(new TransactedOperation() {
            public void doOperation() {
                tpm.saveEntity(content);
                attachment.setContent(content);
                task.getTaskData().addAttachment(attachment);
            }
        });
    }

    public void setDocumentContent(final long taskId, final Content content) {
        final Task task = getTask(taskId);

        doOperationInTransaction(new TransactedOperation() {
            public void doOperation() {
                tpm.saveEntity(content);

                task.getTaskData().setDocumentContentId(content.getId());
            }
        });
    }

    /**
     * This method should only be called from a ServerHandler or TaskService implementation. 
     * </p>
     * If you need a Content object (and are already running within a tx), then just use
     * tpm.findEntity(...). 
     * 
     * @param contentId The id of the Content object. 
     * @return The requested Content object. 
     */
    public Content getContent(final long contentId) {
        // The Content object contains a LOB which requires a tx in some db's
        
        final Content [] result = new Content[1];
        result[0] = null;
                
        doOperationInTransaction(new TransactedOperation() {
            public void doOperation() {
                result[0] = (Content) tpm.findEntity(Content.class, contentId);
            }
        });
        
        return result[0];
    }

    public void deleteAttachment(final long taskId, final long attachmentId, final long contentId) {
        // TODO I can't get this to work with HQL deleting the Attachment. 
        // Hibernate needs both the item removed from the collection and also the item deleted, 
        // so for now, we have to load the entire Task. 
        // I suspect that this is due to using the same EM which is caching things.
        final Task task = getTask(taskId);

        doOperationInTransaction(new TransactedOperation() {
            public void doOperation() {
                final Attachment removedAttachment = task.getTaskData().removeAttachment(attachmentId);

                if (removedAttachment != null) {
                    // need to do this otherwise it just removes the link id, without removing the attachment
                    tpm.deleteEntity(removedAttachment);
                }

                // we do this as HQL to avoid streaming in the entire HQL
                final String deleteContent = "delete from Content c where c.id = :id";
                Query query = tpm.createNewQuery(deleteContent);
                query.setParameter("id", contentId);
                query.executeUpdate();
            }
        });
    }

    public void deleteComment(final long taskId, final long commentId) {
        // @TODO I can't get this to work with HQL deleting the Comment. Hibernate needs both the item removed from the collection
        // and also the item deleted, so for now have to load the entire Task, I suspect that this is due to using the same EM which 
        // is caching things.
        final Task task = getTask(taskId);

        doOperationInTransaction(new TransactedOperation() {
            public void doOperation() {
                final Comment removedComment = task.getTaskData().removeComment(commentId);

                if (removedComment != null) {
                    // need to do this otherwise it just removes the link id, without removing the attachment
                    tpm.deleteEntity(removedComment);
                }
            }
        });
    }
    
    public void claimNextAvailable(final String userId, final String language) {
        doCallbackUserOperation(userId);
        List<Status> status = new ArrayList<Status>();
        status.add(Status.Ready);
        List<TaskSummary> queryTasks = getTasksAssignedAsPotentialOwnerByStatus(userId, status, language);
        if(queryTasks.size() > 0){
            taskOperation(Operation.Claim, queryTasks.get(0).getId(), userId, null, null, null );
        } else{
            logger.info(" No Task Available to Assign");
        }
    }

    @Deprecated 
    public void claimNextAvailable(final String userId, List<String> groupIds, final String language) {
        doCallbackUserOperation(userId);
        groupIds = doUserGroupCallbackOperation(userId, groupIds);
        List<Status> status = new ArrayList<Status>();
        status.add(Status.Ready);
        List<TaskSummary> queryTasks = getTasksAssignedAsPotentialOwnerByStatusByGroup(userId, groupIds, status, language);
        if(queryTasks.size() > 0){
            taskOperation(Operation.Claim, queryTasks.get(0).getId(), userId, null, null, groupIds );
        } else{
            logger.info(" No Task Available to Assign");
        }
    }

    @Deprecated 
    public List<TaskSummary> getTasksAssignedAsPotentialOwner(final String userId, final List<String> groupIds,
                                                              final String language) {
    	return getTasksAssignedAsPotentialOwner(userId, groupIds, language, -1, -1);
    }

    public Task getTaskByWorkItemId(final long workItemId) {
        HashMap<String, Object> params = addParametersToMap("workItemId", workItemId);
        Object taskObject = tpm.queryWithParametersInTransaction("TaskByWorkItemId", params, true);
        return (Task) taskObject;
    }

    public List<TaskSummary> getTasksOwned(final String userId, final String language) {
        doCallbackUserOperation(userId);
        return tpm.queryTasksWithUserIdAndLanguage("TasksOwned", userId, language);
    }
    
    public List<TaskSummary> getTasksOwned(final String userId, List<Status> status, final String language) {
        doCallbackUserOperation(userId);
        return tpm.queryTasksWithUserIdStatusAndLanguage("TasksOwnedWithParticularStatus", userId, status, language);
    }

    public List<TaskSummary> getTasksAssignedAsBusinessAdministrator(final String userId,
                                                                     final String language) {
        doCallbackUserOperation(userId);
        return tpm.queryTasksWithUserIdAndLanguage("TasksAssignedAsBusinessAdministrator", userId, language);
    }

    public List<TaskSummary> getTasksAssignedAsExcludedOwner(final String userId,
                                                             final String language) {
        doCallbackUserOperation(userId);
        return tpm.queryTasksWithUserIdAndLanguage("TasksAssignedAsExcludedOwner", userId, language);
    }

    /**
     * This is default method to get tasks assigned to <code>userId</code> based on user membership and direct assignment.
     * @param userId user id which tasks are assigned for
     * @param language preferred locale
     * @return list of tasks assigned to given user (direct or through group membership)
     */
    public List<TaskSummary> getTasksAssignedAsPotentialOwner(final String userId,
                                                              final String language) {
    	return getTasksAssignedAsPotentialOwner(userId, language, -1, -1);
    }
    

    @SuppressWarnings("unchecked")
	public List<TaskSummary> getTasksAssignedAsPotentialOwnerByStatus(String userId, List<Status> status, String language) {
        doCallbackUserOperation(userId);
        List<String> groupIds = doUserGroupCallbackOperation(userId, null);
        
        HashMap<String, Object> params = addParametersToMap(
                "userId", userId,
                "groupIds", groupIds,
                "language", language,
                "status", status);
        return (List<TaskSummary>) tpm.queryWithParametersInTransaction("TasksAssignedAsPotentialOwnerByStatusWithGroups", params);
    }
    
    @SuppressWarnings("unchecked")
    public List<TaskSummary> getTasksAssignedAsPotentialOwner(final String userId,
                                                              final String language, final int firstResult, int maxResults) {
        doCallbackUserOperation(userId);
        List<String> groupIds = doUserGroupCallbackOperation(userId, null);
        
        HashMap<String, Object> params = addParametersToMap(
                "userId", userId,
                "groupIds", groupIds,
                "language", language);
        if(maxResults != -1) {
            params.put(TaskPersistenceManager.FIRST_RESULT, firstResult);
            params.put(TaskPersistenceManager.MAX_RESULTS, maxResults);
        }

        return (List<TaskSummary>) tpm.queryWithParametersInTransaction("TasksAssignedAsPotentialOwnerWithGroups", params);
    }

    @Deprecated 
    @SuppressWarnings("unchecked")
    public List<TaskSummary> getTasksAssignedAsPotentialOwner(final String userId, List<String> groupIds,
                                                              final String language, final int firstResult, int maxResults) {
        doCallbackUserOperation(userId);
        groupIds = doUserGroupCallbackOperation(userId, groupIds);
        
        HashMap<String, Object> params = addParametersToMap(
                "userId", userId,
                "groupIds", groupIds,
                "language", language);
        if(maxResults != -1) {
            params.put(TaskPersistenceManager.FIRST_RESULT, firstResult);
            params.put(TaskPersistenceManager.MAX_RESULTS, maxResults);
        }

        return (List<TaskSummary>) tpm.queryWithParametersInTransaction("TasksAssignedAsPotentialOwnerWithGroups", params);
    }

    @SuppressWarnings("unchecked")
    public List<TaskSummary> getSubTasksAssignedAsPotentialOwner(final long parentId, final String userId,
                                                                 final String language) {
        doCallbackUserOperation(userId);
        List<String> groupIds = doUserGroupCallbackOperation(userId, null);
        Map<String, Object> params = addParametersToMap(
                "userId", userId,
                "groupIds", groupIds,
                "parentId", parentId,
                "language", language);
        
        return (List<TaskSummary>) tpm.queryWithParametersInTransaction("SubTasksAssignedAsPotentialOwner", params);
    }

    @Deprecated 
    @SuppressWarnings("unchecked")
    public List<TaskSummary> getTasksAssignedAsPotentialOwnerByGroup(final String groupId,
                                                                     final String language) {
        doCallbackGroupOperation(groupId);
        Map<String, Object> params = addParametersToMap(
                "groupId", groupId,
                "language", language);
        
        return (List<TaskSummary>) tpm.queryWithParametersInTransaction("TasksAssignedAsPotentialOwnerByGroup", params);
    }

    @SuppressWarnings("unchecked")
    public List<TaskSummary> getSubTasksByParent(final long parentId, final String language) {
        Map<String, Object> params = addParametersToMap(
                "parentId", parentId, 
                "language", language);
        
        return (List<TaskSummary>) tpm.queryWithParametersInTransaction("GetSubTasksByParentTaskId", params);
    }

    public List<TaskSummary> getTasksAssignedAsRecipient(final String userId,
                                                         final String language) {
        doCallbackUserOperation(userId);

        return tpm.queryTasksWithUserIdAndLanguage("TasksAssignedAsRecipient", userId, language);
    }

    public List<TaskSummary> getTasksAssignedAsTaskInitiator(final String userId,
                                                             final String language) {
        doCallbackUserOperation(userId);
        return tpm.queryTasksWithUserIdAndLanguage("TasksAssignedAsTaskInitiator", userId, language);
    }
    
    public List<TaskSummary> getTasksAssignedAsTaskStakeholder(final String userId,
                                                               final String language) {
        doCallbackUserOperation(userId);
        return tpm.queryTasksWithUserIdAndLanguage("TasksAssignedAsTaskStakeholder", userId, language);
    }
    
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
    public List<?> query(final String qlString, final Integer size, final Integer offset) {
        String regex = "(?i) *select .*";
        String badRegex = "(?i).*(delete|update) .*";
        if( ! qlString.matches(regex) || qlString.matches(badRegex) ) { 
            throw new UnsupportedOperationException("Only select queries are supported: '" + qlString + "'");
        }
        
    	final Query genericQuery = tpm.createNewQuery(qlString);
    	genericQuery.setMaxResults(size);
    	genericQuery.setFirstResult(offset);
    	return genericQuery.getResultList();
    }
    
    private void taskRemoveOperation(final Task task, final User user) {
		if (task.getPeopleAssignments().getRecipients().contains(user)) {
			task.getPeopleAssignments().getRecipients().remove(user);
		} else {
			throw new RuntimeException("Couldn't remove user " + user.getId() + " since it isn't a notification recipient");
		}
    }
    
    private void taskRegisterOperation(final Task task, final User user) {
		if (!task.getPeopleAssignments().getRecipients().contains(user)) {
			task.getPeopleAssignments().getRecipients().add(user);
		}
    }
    
    public void nominateTask(final long taskId, String userId, final List<OrganizationalEntity> potentialOwners) {
        doCallbackUserOperation(userId);
        doCallbackOperationForPotentialOwners(potentialOwners);
        
    	final Task task = getEntity(Task.class, taskId);
    	final User user = getEntity(User.class, userId);
    	if (isAllowed(user, null, task.getPeopleAssignments().getBusinessAdministrators())) {
	    	doOperationInTransaction(new TransactedOperation() {
				public void doOperation() {
					task.getTaskData().assignOwnerAndStatus(potentialOwners);
					if (task.getTaskData().getStatus() == Status.Ready) {
						task.getPeopleAssignments().setPotentialOwners(potentialOwners);
					}
				}
	    	});
    	} else {
    		throw new PermissionDeniedException("User " + userId + " is not allowed to perform Nominate on Task " + taskId);
    	}
    }
    
    private Task getTaskAndCheckTaskUserId(long taskId, String userId, String operation) { 
        Task task = getEntity(Task.class, taskId);
        if (!userId.equals(task.getTaskData().getActualOwner().getId())) {
            throw new RuntimeException(
                    "User " + userId 
                    + " is not the actual owner of the task " + taskId 
                    + " and can't perform " + operation);
        }
        return task;
    }

    public void setOutput(final long taskId, final String userId, final ContentData outputContentData) {
        final Task task = getTaskAndCheckTaskUserId(taskId, userId, "setOutput");
    	doOperationInTransaction(new TransactedOperation() {
    		public void doOperation() {
    	        Content content = new Content();
    	        content.setContent(outputContentData.getContent());
    	        tpm.saveEntity(content);
    	        task.getTaskData().setOutput(content.getId(), outputContentData);

    		}
    	});
    }
    
    public void setFault(final long taskId, final String userId, final FaultData faultContentData) {
        final Task task = getTaskAndCheckTaskUserId(taskId, userId, "setFault");
    	doOperationInTransaction(new TransactedOperation() {
    		public void doOperation() {
    	        Content content = new Content();
    	        content.setContent(faultContentData.getContent());
    	        tpm.saveEntity(content);
    	        task.getTaskData().setFault(content.getId(), faultContentData);
    	 
    		}
    	});
    }
    
    public void setPriority(final long taskId, final String userId, final int priority) {
    	doOperationInTransaction(new TransactedOperation() {
    		public void doOperation() {
    			Task task = getEntity(Task.class, taskId);
    			task.setPriority(priority);
    		}
    	});
    }
    
    public void deleteOutput(final long taskId, final String userId) {
        final Task task = getTaskAndCheckTaskUserId(taskId, userId, "deleteOutput");
    	doOperationInTransaction(new TransactedOperation() {
    		public void doOperation() {
    	        long contentId = task.getTaskData().getOutputContentId();
    	        Content content = (Content) tpm.findEntity(Content.class, contentId);
    	        ContentData data = new ContentData();
    	        tpm.deleteEntity(content);
    	        task.getTaskData().setOutput(0, data);
    		}
    	});
    }
    
    public void deleteFault(final long taskId, final String userId) {
        final Task task = getTask(taskId);
        if (! userId.equals(task.getTaskData().getActualOwner().getId())) {
            throw new RuntimeException("User " + userId + " is not the actual owner of the task " + taskId + " and can't perform deleteFault");
        }
    	doOperationInTransaction(new TransactedOperation() {
    		public void doOperation() {
    	        long contentId = task.getTaskData().getFaultContentId();
    	        Content content = (Content) tpm.findEntity(Content.class, contentId);
    	        FaultData data = new FaultData();
    	        tpm.deleteEntity(content);
    	        task.getTaskData().setFault(0, data);
    		}
    	});
    }
    

    private boolean isAllowed(final User user, final List<String> groupIds, final List<OrganizationalEntity> entities) {
        // for now just do a contains, I'll figure out group membership later.
        for (OrganizationalEntity entity : entities) {
            if (entity instanceof User && entity.equals(user)) {
                return true;
            }
            if (entity instanceof Group && groupIds != null && groupIds.contains(entity.getId())) {
                return true;
            }
        }
        return false;
    }

    private void checkSubTaskStrategy(final Task task) {
        for (SubTasksStrategy strategy : task.getSubTaskStrategies()) {
            strategy.execute(this, service, task);
        }

        final Task parentTask;
        if (task.getTaskData().getParentId() != -1) {
            parentTask = getTask(task.getTaskData().getParentId());
            for (SubTasksStrategy strategy : parentTask.getSubTaskStrategies()) {
                strategy.execute(this, service, parentTask);
            }
        }
    }

    /**
     * Returns the entity of the specified class by for the specified primaryKey.
     *
     * @param entityClass - class of entity to return
     * @param primaryKey  - key of entity
     * @return entity or <code>EntityNotFoundException</code> if the entity cannot be found
     * @throws EntityNotFoundException if entity not found
     */
    private <T> T getEntity(final Class<T> entityClass, final Object primaryKey) {
        
        final Object [] result = new Object[1];
        doOperationInTransaction(new TransactedOperation() {
            public void doOperation() {
                result[0] = tpm.findEntity(entityClass, primaryKey);
            }
        });
        
        final T entity = (T) result[0];
        if (entity == null) {
            throw new EntityNotFoundException("No " + entityClass.getSimpleName() + " with ID " + primaryKey + " was found!");
        }

        return entity;
    }
   
    /**
     * Persists the specified object within a new transaction. If there are any problems, the
     * transaction will be rolled back.
     *
     * @param object object to persists
     */
    private void persistInTransaction(final Object object) {
        doOperationInTransaction(new TransactedOperation() {
            public void doOperation() {
                tpm.saveEntity(object);
            }
        });
    }

    /**
     * Executes the specified operation within a transaction. Note that if there is a currently active
     * transaction, if will reuse it.
     * 
     * This logic is unfortunately duplicated in {@link TaskPersistenceManager#queryWithParametersInTransaction(String, Map)}. 
     * If you change the logic here, please make sure to change the logic there as well (and vice versa). 
     * 
     * @param operation operation to execute
     */
    public void doOperationInTransaction(final TransactedOperation operation) {

        boolean txOwner = false;
        boolean operationSuccessful = false;
        boolean txStarted = false;
        try {
            txOwner = tpm.beginTransaction();
            txStarted = true;
            
            operation.doOperation();
            operationSuccessful = true;
            
            tpm.endTransaction(txOwner);
        } catch(Exception e) {
            tpm.rollBackTransaction(txOwner);
            
            String message; 
            if( !txStarted ) { message = "Could not start transaction."; }
            else if( !operationSuccessful ) { message = "Operation failed"; }
            else { message = "Could not commit transaction"; }
            
            if (e instanceof TaskException) {
                throw (TaskException) e;
            } else {
                throw new RuntimeException(message, e);
            }
        }
        
    }

    /*
     * Deprecated query methods as they relied on given as argument groupids, recommended and default is to make use of callback 
     */
    @Deprecated 
    public List<TaskSummary> getTasksAssignedAsPotentialOwnerByStatusByGroup(String userId, List<String> groupIds, List<Status> status, String language) {
        doCallbackUserOperation(userId);
        HashMap<String, Object> params = addParametersToMap(
                                         "userId", userId,
                                         "groupIds", groupIds,
                                         "language", language,
                                         "status", status);
        return (List<TaskSummary>) tpm.queryWithParametersInTransaction("TasksAssignedAsPotentialOwnerByStatusWithGroups", params);
    }

    public List<TaskSummary> getTasksByStatusByProcessId(long processInstanceId, List<Status> status, String language){
        HashMap<String, Object> params = addParametersToMap(
                                         "processInstanceId", processInstanceId,
                                         "status", status,
                                         "language", language);
        List<TaskSummary> result = (List<TaskSummary>) tpm.queryWithParametersInTransaction("TasksByStatusByProcessId", params);
        return result;
    }

    public List<TaskSummary> getTasksByStatusByProcessIdByTaskName(long processInstanceId, List<Status> status, String taskName, String language){
        HashMap<String, Object> params = addParametersToMap(
                                         "processInstanceId", processInstanceId,
                                         "status", status,
                                         "taskName", taskName,
                                         "language", language);
        List<TaskSummary> result = (List<TaskSummary>) tpm.queryWithParametersInTransaction("TasksByStatusByProcessIdByTaskName", params);
        return result;
    }

    public interface TransactedOperation {
        void doOperation() throws Exception;
    }

    public void executeEscalatedDeadline(EscalatedDeadlineHandler escalatedDeadlineHandler, TaskService service, long taskId, long deadlineId) { 

        boolean txOwner = false;
        boolean operationSuccessful = false;
        boolean txStarted = false;
        try {
            txOwner = tpm.beginTransaction();
            txStarted = true;

            Task task = (Task) tpm.findEntity(Task.class, taskId);
            Deadline deadline = (Deadline) tpm.findEntity(Deadline.class, deadlineId);

            TaskData taskData = task.getTaskData();
            Content content = null;
            if ( taskData != null ) {
                content = (Content) tpm.findEntity(Content.class, taskData.getDocumentContentId() );
            }

            escalatedDeadlineHandler.executeEscalatedDeadline(task,
                    deadline,
                    content,
                    service);     

            operationSuccessful = true;
            tpm.endTransaction(txOwner);
        } catch(Exception e) {
            tpm.rollBackTransaction(txOwner);

            String message; 
            if( !txStarted ) { message = "Could not start transaction."; }
            else if( !operationSuccessful ) { message = "Operation failed"; }
            else { message = "Could not commit transaction"; }

            throw new RuntimeException(message, e);
        }
    }
    
    private List<String> doUserGroupCallbackOperation(String userId, List<String> groupIds) {
        if(UserGroupCallbackManager.getInstance().existsCallback()) {
            doCallbackUserOperation(userId);
            doCallbackGroupsOperation(userId, groupIds);
            List<String> allGroupIds = null;

            return UserGroupCallbackManager.getInstance().getCallback().getGroupsForUser(userId, groupIds, allGroupIds);
        } else {
            logger.debug("UserGroupCallback has not been registered.");
            return groupIds;
        }
    }
    
    private boolean doCallbackUserOperation(String userId) {
        if(UserGroupCallbackManager.getInstance().existsCallback()) {
            if(userId != null && UserGroupCallbackManager.getInstance().getCallback().existsUser(userId)) {
                addUserFromCallbackOperation(userId);
                return true;
            }
            return false;
        } else {
            logger.debug("UserGroupCallback has not been registered.");
            // returns true for backward compatibility
            return true;
        }
    }
    
    private boolean doCallbackGroupOperation(String groupId) {
        if(UserGroupCallbackManager.getInstance().existsCallback()) {
            if(groupId != null && UserGroupCallbackManager.getInstance().getCallback().existsGroup(groupId)) {
                addGroupFromCallbackOperation(groupId);
                return true;
            }
            return false;
        } else {
            logger.debug("UserGroupCallback has not been registered.");
            // returns true for backward compatibility
            return true;
        }
    }
    
    private void doCallbackOperationForTaskData(TaskData data) {
        if(UserGroupCallbackManager.getInstance().existsCallback() && data != null) {
            if(data.getActualOwner() != null) {
                boolean userExists = doCallbackUserOperation(data.getActualOwner().getId());
                if (!userExists) {
                	// remove it from the task to avoid foreign key constraint exception
                	data.setActualOwner(null);
                	data.setStatus(Status.Ready);
                }
            }
            
            if(data.getCreatedBy() != null) {
            	boolean userExists = doCallbackUserOperation(data.getCreatedBy().getId());
            	if (!userExists) {
                	// remove it from the task to avoid foreign key constraint exception
                	data.setCreatedBy(null);
                }
            }
        }
    }
    
    private void doCallbackOperationForPotentialOwners(List<OrganizationalEntity> potentialOwners) {
        if(UserGroupCallbackManager.getInstance().existsCallback() && potentialOwners != null) { 
        	List<OrganizationalEntity> nonExistingEntities = new ArrayList<OrganizationalEntity>();
        	
        	for(OrganizationalEntity orgEntity : potentialOwners) {
                if(orgEntity instanceof User) {
                    boolean userExists = doCallbackUserOperation(orgEntity.getId());
                    if (!userExists) {
                    	nonExistingEntities.add(orgEntity);
                    }
                }
                if(orgEntity instanceof Group) {
                	boolean groupExists = doCallbackGroupOperation(orgEntity.getId());
                    if (!groupExists) {
                    	nonExistingEntities.add(orgEntity);
                    }
                }
            }
            if (!nonExistingEntities.isEmpty()) {
            	potentialOwners.removeAll(nonExistingEntities);
            }
        }
    }
    
    private void doCallbackOperationForPeopleAssignments(PeopleAssignments assignments) {
        if(UserGroupCallbackManager.getInstance().existsCallback()) {
        	List<OrganizationalEntity> nonExistingEntities = new ArrayList<OrganizationalEntity>();
        	
            if(assignments != null) {
                List<OrganizationalEntity> businessAdmins = assignments.getBusinessAdministrators();
                if(businessAdmins != null) {
                    for(OrganizationalEntity admin : businessAdmins) {
                        if(admin instanceof User) {
                        	boolean userExists = doCallbackUserOperation(admin.getId());
                            if (!userExists) {
                            	nonExistingEntities.add(admin);
                            }
                        }
                        if(admin instanceof Group) {
                        	boolean groupExists = doCallbackGroupOperation(admin.getId());
                            if (!groupExists) {
                            	nonExistingEntities.add(admin);
                            }
                        }
                    }
                    
                    if (!nonExistingEntities.isEmpty()) {
                    	businessAdmins.removeAll(nonExistingEntities);
                    	nonExistingEntities.clear();
                    }
                } 

                if (businessAdmins == null || businessAdmins.isEmpty()){
                	// throw an exception as it should not be allowed to create task without administrator
                	throw new CannotAddTaskException("There are no known Business Administrators, task cannot be created according to WS-HT specification");
                }
                
                List<OrganizationalEntity> potentialOwners = assignments.getPotentialOwners();
                if(potentialOwners != null) {
                    for(OrganizationalEntity powner : potentialOwners) {
                        if(powner instanceof User) {
                        	boolean userExists = doCallbackUserOperation(powner.getId());
                            if (!userExists) {
                            	nonExistingEntities.add(powner);
                            }
                        }
                        if(powner instanceof Group) {
                        	boolean groupExists = doCallbackGroupOperation(powner.getId());
                            if (!groupExists) {
                            	nonExistingEntities.add(powner);
                            }
                        }
                    }
                    if (!nonExistingEntities.isEmpty()) {
                    	potentialOwners.removeAll(nonExistingEntities);
                    	nonExistingEntities.clear();
                    }
                }
                
                if(assignments.getTaskInitiator() != null && assignments.getTaskInitiator().getId() != null) {
                    doCallbackUserOperation(assignments.getTaskInitiator().getId());
                }
                
                List<OrganizationalEntity> excludedOwners = assignments.getExcludedOwners();
                if(excludedOwners != null) {
                    for(OrganizationalEntity exowner : excludedOwners) {
                        if(exowner instanceof User) {
                        	boolean userExists = doCallbackUserOperation(exowner.getId());
                            if (!userExists) {
                            	nonExistingEntities.add(exowner);
                            }
                        }
                        if(exowner instanceof Group) {
                        	boolean groupExists = doCallbackGroupOperation(exowner.getId());
                            if (!groupExists) {
                            	nonExistingEntities.add(exowner);
                            }
                        }
                    }
                    if (!nonExistingEntities.isEmpty()) {
                    	excludedOwners.removeAll(nonExistingEntities);
                    	nonExistingEntities.clear();
                    }
                }
                
                List<OrganizationalEntity> recipients = assignments.getRecipients();
                if(recipients != null) {
                    for(OrganizationalEntity recipient : recipients) {
                        if(recipient instanceof User) {
                        	boolean userExists = doCallbackUserOperation(recipient.getId());
                            if (!userExists) {
                            	nonExistingEntities.add(recipient);
                            }
                        }
                        if(recipient instanceof Group) {
                        	boolean groupExists = doCallbackGroupOperation(recipient.getId());
                            if (!groupExists) {
                            	nonExistingEntities.add(recipient);
                            }
                        }
                    }
                    if (!nonExistingEntities.isEmpty()) {
                    	recipients.removeAll(nonExistingEntities);
                    	nonExistingEntities.clear();
                    }
                }
                
                List<OrganizationalEntity> stakeholders = assignments.getTaskStakeholders();
                if(stakeholders != null) {
                    for(OrganizationalEntity stakeholder : stakeholders) {
                        if(stakeholder instanceof User) {
                        	boolean userExists = doCallbackUserOperation(stakeholder.getId());
                            if (!userExists) {
                            	nonExistingEntities.add(stakeholder);
                            }
                        }
                        if(stakeholder instanceof Group) {
                        	boolean groupExists = doCallbackGroupOperation(stakeholder.getId());
                            if (!groupExists) {
                            	nonExistingEntities.add(stakeholder);
                            }
                        }
                    }
                    if (!nonExistingEntities.isEmpty()) {
                    	stakeholders.removeAll(nonExistingEntities);
                    	nonExistingEntities.clear();
                    }
                }
            }
        }
        
    }
    
    private void doCallbackOperationForComment(Comment comment) {
        if(comment != null) {
            if(comment.getAddedBy() != null) {
                doCallbackUserOperation(comment.getAddedBy().getId());
            }
        }
    }
    
    private void doCallbackOperationForAttachment(Attachment attachment) {
        if(attachment != null) {
            if(attachment.getAttachedBy() != null) {
                doCallbackUserOperation(attachment.getAttachedBy().getId());
            }
        }
    }
    private void doCallbackOperationForTaskDeadlines(Deadlines deadlines) {
        if(deadlines != null) {
            if(deadlines.getStartDeadlines() != null) {
                List<Deadline> startDeadlines = deadlines.getStartDeadlines();
                for(Deadline startDeadline : startDeadlines) {
                    List<Escalation> escalations = startDeadline.getEscalations();
                    if(escalations != null) {
                        for(Escalation escalation : escalations) {
                            List<Notification> notifications = escalation.getNotifications();
                            List<Reassignment> ressignments = escalation.getReassignments();
                            if(notifications != null) {
                                for(Notification notification : notifications) {
                                    List<OrganizationalEntity> recipients = notification.getRecipients();
                                    if(recipients != null) {
                                        for(OrganizationalEntity recipient : recipients) {
                                            if(recipient instanceof User) {
                                                doCallbackUserOperation(recipient.getId());
                                            }
                                            if(recipient instanceof Group) {
                                                doCallbackGroupOperation(recipient.getId());
                                            }
                                        }
                                    }
                                    List<OrganizationalEntity> administrators = notification.getBusinessAdministrators();
                                    if(administrators != null) {
                                        for(OrganizationalEntity administrator : administrators) {
                                            if(administrator instanceof User) {
                                                doCallbackUserOperation(administrator.getId());
                                            }
                                            if(administrator instanceof Group) {
                                                doCallbackGroupOperation(administrator.getId());
                                            }
                                        }
                                    }
                                }
                            }
                            if(ressignments != null) {
                                for(Reassignment reassignment : ressignments) {
                                    List<OrganizationalEntity> potentialOwners = reassignment.getPotentialOwners();
                                    if(potentialOwners != null) {
                                        for(OrganizationalEntity potentialOwner : potentialOwners) {
                                            if(potentialOwner instanceof User) {
                                                doCallbackUserOperation(potentialOwner.getId());
                                            }
                                            if(potentialOwner instanceof Group) {
                                                doCallbackGroupOperation(potentialOwner.getId());
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            if(deadlines.getEndDeadlines() != null) {
                List<Deadline> endDeadlines = deadlines.getEndDeadlines();
                for(Deadline endDeadline : endDeadlines) {
                    List<Escalation> escalations = endDeadline.getEscalations();
                    if(escalations != null) {
                        for(Escalation escalation : escalations) {
                            List<Notification> notifications = escalation.getNotifications();
                            List<Reassignment> ressignments = escalation.getReassignments();
                            if(notifications != null) {
                                for(Notification notification : notifications) {
                                    List<OrganizationalEntity> recipients = notification.getRecipients();
                                    if(recipients != null) {
                                        for(OrganizationalEntity recipient : recipients) {
                                            if(recipient instanceof User) {
                                                doCallbackUserOperation(recipient.getId());
                                            }
                                            if(recipient instanceof Group) {
                                                doCallbackGroupOperation(recipient.getId());
                                            }
                                        }
                                    }
                                    List<OrganizationalEntity> administrators = notification.getBusinessAdministrators();
                                    if(administrators != null) {
                                        for(OrganizationalEntity administrator : administrators) {
                                            if(administrator instanceof User) {
                                                doCallbackUserOperation(administrator.getId());
                                            }
                                            if(administrator instanceof Group) {
                                                doCallbackGroupOperation(administrator.getId());
                                            }
                                        }
                                    }
                                }
                            }
                            if(ressignments != null) {
                                for(Reassignment reassignment : ressignments) {
                                    List<OrganizationalEntity> potentialOwners = reassignment.getPotentialOwners();
                                    if(potentialOwners != null) {
                                        for(OrganizationalEntity potentialOwner : potentialOwners) {
                                            if(potentialOwner instanceof User) {
                                                doCallbackUserOperation(potentialOwner.getId());
                                            }
                                            if(potentialOwner instanceof Group) {
                                                doCallbackGroupOperation(potentialOwner.getId());
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    
    private void doCallbackGroupsOperation(String userId, List<String> groupIds) { 
        if(UserGroupCallbackManager.getInstance().existsCallback()) {
            if(userId != null) {
                UserGroupCallback callback = UserGroupCallbackManager.getInstance().getCallback();
                if(groupIds != null && groupIds.size() > 0) {
                    
                	List<String> userGroups = callback.getGroupsForUser(userId, groupIds, null);
                    for(String groupId : groupIds) {
                        
                        if(callback.existsGroup(groupId) && userGroups != null && userGroups.contains(groupId)) {
                            addGroupFromCallbackOperation(groupId);
                        }
                    }
                } else {
                    if(!(userGroupsMap.containsKey(userId) && userGroupsMap.get(userId).booleanValue())) { 
                        List<String> userGroups = callback.getGroupsForUser(userId, null, null);
                        if(userGroups != null && userGroups.size() > 0) {
                            for(String group : userGroups) {
                                addGroupFromCallbackOperation(group);
                            }
                            userGroupsMap.put(userId, true);
                        }
                    }
                }
            } else {
                if(groupIds != null) {
                    for(String groupId : groupIds) {
                        addGroupFromCallbackOperation(groupId);
                    }
                }
            }
        } else {
            logger.debug("UserGroupCallback has not been registered.");
        }
    }
    
    private void addGroupFromCallbackOperation(String groupId) {
        try {
            boolean groupExists = tpm.findEntity(Group.class, groupId) != null;
            if( ! StringUtils.isEmpty(groupId) && ! groupExists ) {
                Group group = new Group(groupId);
                persistInTransaction(group);
            }
        } catch (Throwable t) {
            logger.debug("Trying to add group " + groupId + ", but it already exists. ");
        }
    }
    
    private void addUserFromCallbackOperation(String userId) { 
        try {
            boolean userExists = tpm.findEntity(User.class, userId) != null;
            if( ! StringUtils.isEmpty(userId) && ! userExists ) {
                User user = new User(userId);
                persistInTransaction(user);
            }
        } catch (Throwable t) {
            logger.debug("Unable to add user " + userId);
        }
    }
    
    private void clearDeadlines(final Task task) { 
        
        if (task.getDeadlines() == null) {
            return;
        }
        try {
            doOperationInTransaction(new TransactedOperation() {
                public void doOperation() {
                    Iterator<Deadline> it = null;
                    if (task.getDeadlines().getStartDeadlines() != null) {
                        it = task.getDeadlines().getStartDeadlines().iterator(); 
                        while (it.hasNext()) {
                            tpm.deleteEntity(it.next());
                            it.remove();
                        }
                    }
                    
                    if (task.getDeadlines().getEndDeadlines() != null) {
                        it = task.getDeadlines().getEndDeadlines().iterator(); 
                        while (it.hasNext()) {
                            tpm.deleteEntity(it.next());
                            it.remove();
                        }
                    }
                }
            });
        
        } catch (Throwable t) {
                logger.error("Unable to clear deadlines for task " + task.getId(), t);
        }
    }
    
  
}
