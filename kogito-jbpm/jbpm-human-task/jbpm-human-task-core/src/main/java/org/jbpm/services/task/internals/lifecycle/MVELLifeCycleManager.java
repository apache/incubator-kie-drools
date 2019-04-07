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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.services.task.internals.lifecycle;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.core.util.MVELSafeHelper;
import org.jbpm.persistence.api.integration.EventManagerProvider;
import org.jbpm.process.instance.impl.NoOpExecutionErrorHandler;
import org.jbpm.services.task.assignment.AssignmentService;
import org.jbpm.services.task.assignment.AssignmentServiceProvider;
import org.jbpm.services.task.events.TaskEventSupport;
import org.jbpm.services.task.exception.PermissionDeniedException;
import org.jbpm.services.task.utils.ContentMarshallerHelper;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.task.model.Content;
import org.kie.api.task.model.Group;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.PeopleAssignments;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.TaskData;
import org.kie.api.task.model.User;
import org.kie.internal.runtime.error.ExecutionErrorHandler;
import org.kie.internal.runtime.error.ExecutionErrorManager;
import org.kie.internal.task.api.TaskContentService;
import org.kie.internal.task.api.TaskContext;
import org.kie.internal.task.api.TaskModelProvider;
import org.kie.internal.task.api.TaskPersistenceContext;
import org.kie.internal.task.api.model.FaultData;
import org.kie.internal.task.api.model.InternalContent;
import org.kie.internal.task.api.model.InternalPeopleAssignments;
import org.kie.internal.task.api.model.InternalTaskData;
import org.kie.internal.task.api.model.Operation;
import org.kie.internal.task.exception.TaskException;
import org.mvel2.MVEL;
import org.mvel2.ParserConfiguration;
import org.mvel2.ParserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 */

public class MVELLifeCycleManager implements LifeCycleManager {
    
    private static final Logger logger = LoggerFactory.getLogger(MVELLifeCycleManager.class);

    private TaskContext context;
    
    private TaskPersistenceContext persistenceContext;

    private TaskContentService taskContentService;
    private TaskEventSupport taskEventSupport;
    private static Map<Operation, List<OperationCommand>> operations = initMVELOperations();

    public MVELLifeCycleManager() {
    }
    
    public MVELLifeCycleManager(TaskContext context, TaskPersistenceContext persistenceContext, TaskContentService contentService,
    		TaskEventSupport taskEventSupport) {
    	this.context = context;
    	this.persistenceContext = persistenceContext;
    	this.taskContentService = contentService;
    	this.taskEventSupport = taskEventSupport;
    	
    }

    public void setPersistenceContext(TaskPersistenceContext persistenceContext) {
        this.persistenceContext = persistenceContext;
    }
    
    public void setTaskEventSupport(TaskEventSupport taskEventSupport) {
        this.taskEventSupport = taskEventSupport;
    }

    public void setTaskContentService(TaskContentService taskContentService) {
        this.taskContentService = taskContentService;
    }
    

    void evalCommand(final Operation operation, final List<OperationCommand> commands, final Task task,
            final User user, final OrganizationalEntity targetEntity,
            List<String> groupIds, OrganizationalEntity...entities) throws PermissionDeniedException {

        boolean statusMatched = false;
        final TaskData taskData = task.getTaskData();
        for (OperationCommand command : commands) {
            // first find out if we have a matching status
            if (command.getStatus() != null) {
                for (Status status : command.getStatus()) {
                    if (task.getTaskData().getStatus() == status) {
                        statusMatched = true;
                        // next find out if the user can execute this doOperation
                        if (!isAllowed(command, task, user, groupIds)) {
                            String errorMessage = "User '" + user + "' does not have permissions to execute operation '" + operation + "' on task id " + task.getId();

                            throw new PermissionDeniedException(errorMessage);
                        }

                        commands(command, task, user, targetEntity, entities);
                    } else {
                        logger.debug("No match on status for task {} :status {}  != {}", task.getId(), task.getTaskData().getStatus(), status);
                    }
                }
            }

            if (command.getPreviousStatus() != null) {
                for (Status status : command.getPreviousStatus()) {
                    if (taskData.getPreviousStatus() == status) {
                        statusMatched = true;

                        // next find out if the user can execute this doOperation
                        if (!isAllowed(command, task, user, groupIds)) {
                            String errorMessage = "User '" + user + "' does not have permissions to execute operation '" + operation + "' on task id " + task.getId();
                            throw new PermissionDeniedException(errorMessage);
                        }

                        commands(command, task, user, targetEntity, entities);
                    } else {
                        logger.debug("No match on previous status for task {} :status {}  != {}", task.getId(), task.getTaskData().getStatus(), status);
                    }
                }
            }
            

            if (!command.isGroupTargetEntityAllowed() && targetEntity instanceof Group) {
                String errorMessage = "User '" + user + "' was unable to execute operation '" + operation + "' on task id " + task.getId() + " due to 'target entity cannot be group'";
                throw new PermissionDeniedException(errorMessage); 
            }
        }
        if (!statusMatched) {
            String errorMessage = "User '" + user + "' was unable to execute operation '" + operation + "' on task id " + task.getId() + " due to a no 'current status' match";
            throw new PermissionDeniedException(errorMessage);
        }
        

    }

    private boolean isAllowed(final OperationCommand command, final Task task, final User user,
            List<String> groupIds) {

        boolean operationAllowed = false;
        boolean isExcludedOwner =  ((InternalPeopleAssignments) task.getPeopleAssignments()).getExcludedOwners().contains(user);
        
        for (Allowed allowed : command.getAllowed()) {
            if (operationAllowed) {
                break;
            }
            switch (allowed) {
                case Owner: {
                    operationAllowed = !isExcludedOwner && (task.getTaskData().getActualOwner() != null && task.getTaskData().getActualOwner().equals(user));
                    break;
                }
                case Initiator: {
                    operationAllowed = (
                    		!isExcludedOwner &&
                            task.getTaskData().getCreatedBy() != null
                            && (task.getTaskData().getCreatedBy().equals(user)
                            || groupIds != null && groupIds.contains(task.getTaskData().getCreatedBy().getId())));
                    break;
                }
                case PotentialOwner: {
                	operationAllowed = !isExcludedOwner && isAllowed(user, groupIds, (List<OrganizationalEntity>) task.getPeopleAssignments().getPotentialOwners());
                    break;
                }
                case BusinessAdministrator: {
                    operationAllowed = isAllowed(user, groupIds, (List<OrganizationalEntity>) task.getPeopleAssignments().getBusinessAdministrators());
                    break;
                }
                case TaskStakeholders: {
                    operationAllowed = !isExcludedOwner && isAllowed(user, groupIds, (List<OrganizationalEntity>) ((InternalPeopleAssignments) task.getPeopleAssignments()).getTaskStakeholders());
                    break;
                }
                case Anyone: {
                    operationAllowed = !isExcludedOwner;
                    break;
                }
            }
        }

        if (operationAllowed && command.isUserIsExplicitPotentialOwner()) {
            // if user has rights to execute the command, make sure user is explicitly specified (not as a group)
            operationAllowed = task.getPeopleAssignments().getPotentialOwners().contains(user);
        }

        if (operationAllowed && command.isSkipable()) {
            operationAllowed = task.getTaskData().isSkipable();
        }

        return operationAllowed;
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
    
    private void commands(final OperationCommand command, final Task task, final User user,
            final OrganizationalEntity targetEntity, OrganizationalEntity...entities) {


        final PeopleAssignments people = task.getPeopleAssignments();
        final InternalTaskData taskData = (InternalTaskData) task.getTaskData();
        
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
                    // @TODO: Ical stuff
                    // Task was reserved so owner should get icals
//                    SendIcal.getInstance().sendIcalForTask(task, service.getUserinfo());

                    break;
                }
                case Nominate: {
                	if (entities != null && entities.length > 0) {
                		List<OrganizationalEntity> potentialOwners = new ArrayList<OrganizationalEntity>(Arrays.asList(entities));
                		((InternalPeopleAssignments) task.getPeopleAssignments()).setPotentialOwners(potentialOwners);
                		assignOwnerAndStatus((InternalTaskData) task.getTaskData(), potentialOwners);                    	
                    }
                    break;
                }
            }
        }


    }
    

    public void taskOperation(final Operation operation, final long taskId, final String userId,
            final String targetEntityId, final Map<String, Object> data,
            List<String> groupIds, OrganizationalEntity...entities) throws TaskException {

        try {
            final List<OperationCommand> commands = operations.get(operation);

            Task task = persistenceContext.findTask(taskId);
            
            if (task == null) {
            	String errorMessage = "Task '" + taskId + "' not found";
                throw new PermissionDeniedException(errorMessage);
            }
            
            String deploymentId = (String) context.get(EnvironmentName.DEPLOYMENT_ID);
            if (deploymentId != null && !deploymentId.equals(task.getTaskData().getDeploymentId())) {
                throw new IllegalStateException("Task instance " + task.getId() + " is owned by another deployment expected " +
                        task.getTaskData().getDeploymentId() + " found " + deploymentId);
            }
            // automatically load task variables on each operation if the event manager is activated
            if (EventManagerProvider.getInstance().isActive()) {
                taskContentService.loadTaskVariables(task);
            }

            User user = persistenceContext.findUser(userId);
            OrganizationalEntity targetEntity = null;
            if (targetEntityId != null && !targetEntityId.equals("")) {
                targetEntity = persistenceContext.findOrgEntity(targetEntityId);
            }
            getExecutionErrorHandler().processing(task);
            switch (operation) {    
                case Activate: {
                	taskEventSupport.fireBeforeTaskActivated(task, context);
                    break;
                }
                case Claim: {
                	taskEventSupport.fireBeforeTaskClaimed(task, context);
                    break;
                }
                case Complete: {
                	taskEventSupport.fireBeforeTaskCompleted(task, context);
                    break;
                }
                case Delegate: {
                	taskEventSupport.fireBeforeTaskDelegated(task, context);
                    break;
                }
                case Exit: {
                	taskEventSupport.fireBeforeTaskExited(task, context);
                    break;
                }

                case Fail: {
                    if (data != null) {
                        FaultData faultData = ContentMarshallerHelper.marshalFault(task, data, null);
                        Content content = TaskModelProvider.getFactory().newContent();
                        ((InternalContent)content).setContent(faultData.getContent());
                        persistenceContext.persistContent(content);
                        persistenceContext.setFaultToTask(content, faultData, task);
                    }
                    taskEventSupport.fireBeforeTaskFailed(task, context);
                    break;
                }
                case Forward: {
                    taskEventSupport.fireBeforeTaskForwarded(task, context);
                    if (task.getPeopleAssignments().getPotentialOwners().stream().anyMatch(oe -> oe instanceof Group)) {
                        // if potential owners contains a group, operation should not be allowed
                        throw new PermissionDeniedException("Task forward operation not allowed for task with group assignment");
                    }
                    break;
                }
                case Nominate: {
                	taskEventSupport.fireBeforeTaskNominated(task, context);
                    break;
                }
                case Release: {
                	taskEventSupport.fireBeforeTaskReleased(task, context);
                    break;
                }
                case Resume: {
                	taskEventSupport.fireBeforeTaskResumed(task, context);
                    break;
                }
                case Skip: {
                	taskEventSupport.fireBeforeTaskSkipped(task, context);
                    break;
                }
                case Start: {
                	taskEventSupport.fireBeforeTaskStarted(task, context);
                    break;
                }
                case Stop: {
                	taskEventSupport.fireBeforeTaskStopped(task, context);
                    break;
                }
                case Suspend: {
                	taskEventSupport.fireBeforeTaskSuspended(task, context);
                    break;
                }

            }
            
            evalCommand(operation, commands, task, user, targetEntity, groupIds, entities);
            
            persistenceContext.updateTask(task);

            switch (operation) {
                case Activate: {
                	taskEventSupport.fireAfterTaskActivated(task, context);
                    break;
                }
                case Claim: {
                	taskEventSupport.fireAfterTaskClaimed(task, context);
                    break;
                }
                case Complete: {
                    if (data != null) {
                        
                        taskContentService.addOutputContent(taskId, data);
                        
                    }
                    taskEventSupport.fireAfterTaskCompleted(task, context);
                    break;
                }
                case Delegate: {                	
                    // This is a really bad hack to execut the correct behavior
                    ((InternalTaskData) task.getTaskData()).setStatus(Status.Reserved);
                    taskEventSupport.fireAfterTaskDelegated(task, context);
                    break;
                }
                case Exit: {
                	taskEventSupport.fireAfterTaskExited(task, context);
                    break;
                }
                case Fail: {
                	taskEventSupport.fireAfterTaskFailed(task, context);
                    break;
                }
                case Forward: {
                    invokeAssignmentService(task, context, userId);
                	taskEventSupport.fireAfterTaskForwarded(task, context);
                    break;
                }   
                case Nominate: {
                    invokeAssignmentService(task, context, userId);
                	taskEventSupport.fireAfterTaskNominated(task, context);
                    break;
                }
                case Release: {
                    invokeAssignmentService(task, context, userId);
                	taskEventSupport.fireAfterTaskReleased(task, context);
                    break;
                }
                case Resume: {
                	taskEventSupport.fireAfterTaskResumed(task, context);
                    break;
                }
                case Start: {
                	taskEventSupport.fireAfterTaskStarted(task, context);
                    break;
                }
                case Skip: {
                	taskEventSupport.fireAfterTaskSkipped(task, context);
                    break;
                }
                case Stop: {
                	taskEventSupport.fireAfterTaskStopped(task, context);
                    break;
                }    
                case Suspend: {
                	taskEventSupport.fireAfterTaskSuspended(task, context);
                    break;
                }
                
            }
            
            getExecutionErrorHandler().processed(task);
        } catch (RuntimeException re) {
            throw re;
        }


    }

    protected void invokeAssignmentService(Task taskImpl, TaskContext context, String excludedUser) {
        // use assignment service to directly assign actual owner if enabled
        AssignmentService assignmentService = AssignmentServiceProvider.get();
        if (assignmentService.isEnabled()) {
            assignmentService.assignTask(taskImpl, context, excludedUser);
        }
    }
    
    public static Map<Operation, List<OperationCommand>> initMVELOperations() {

        Map<String, Object> vars = new HashMap<String, Object>();

        // Search operations-dsl.mvel, if necessary using superclass if TaskService is subclassed
        InputStream is = null;
        // for (Class<?> c = getClass(); c != null; c = c.getSuperclass()) {
        is = MVELLifeCycleManager.class.getResourceAsStream("/operations-dsl.mvel");
//            if (is != null) {
//                break;
//            }
        //}
        if (is == null) {
            throw new RuntimeException("Unable To initialise TaskService, could not find Operations DSL");
        }
        Reader reader = new InputStreamReader(is);
        try {
            return (Map<Operation, List<OperationCommand>>) eval(toString(reader), vars);
        } catch (IOException e) {
            throw new RuntimeException("Unable To initialise TaskService, could not load Operations DSL");
        }


    }

    public static String toString(Reader reader) throws IOException {
        int charValue;
        StringBuffer sb = new StringBuffer(1024);
        while ((charValue = reader.read()) != -1) {
            sb.append((char) charValue);
        }
        return sb.toString();
    }

    public static Object eval(Reader reader) {
        try {
            return eval(toString(reader), null);
        } catch (IOException e) {
            throw new RuntimeException("Exception Thrown", e);
        }
    }

    public static Object eval(Reader reader, Map<String, Object> vars) {
        try {
            return eval(toString(reader), vars);
        } catch (IOException e) {
            throw new RuntimeException("Exception Thrown", e);
        }
    }

    public static Object eval(String str, Map<String, Object> vars) {
        ParserConfiguration pconf = new ParserConfiguration();
        pconf.addPackageImport("org.kie.internal.task.api.model");
        pconf.addPackageImport("org.jbpm.services.task");
        pconf.addPackageImport("org.jbpm.services.task.impl.model");
        pconf.addPackageImport("org.jbpm.services.task.query");
        pconf.addPackageImport("org.jbpm.services.task.internals.lifecycle");

        pconf.addImport(Status.class);
        pconf.addImport(Allowed.class);
        pconf.addPackageImport("java.util");

        ParserContext context = new ParserContext(pconf);
        Serializable s = MVEL.compileExpression(str.trim(), context);

        if (vars != null) {
            return MVELSafeHelper.getEvaluator().executeExpression(s, vars);
        } else {
            return MVELSafeHelper.getEvaluator().executeExpression(s);
        }
    }

    
    /**
     * This method will potentially assign the actual owner of this TaskData and set the status
     * of the data.
     * <li>If there is only 1 potential owner, and it is a <code>User</code>, that will become the actual
     * owner of the TaskData and the status will be set to <code>Status.Reserved</code>.</li>
     * <li>f there is only 1 potential owner, and it is a <code>Group</code>,  no owner will be assigned
     * and the status will be set to <code>Status.Ready</code>.</li>
     * <li>If there are more than 1 potential owners, the status will be set to <code>Status.Ready</code>.</li>
     * <li>otherwise, the task data will be unchanged</li>
     *
     * @param taskdata - task data
     * @param potentialOwners - list of potential owners
     * @return current status of task data
     */
    public static Status assignOwnerAndStatus(InternalTaskData taskData, List<OrganizationalEntity> potentialOwners) {
        if (taskData.getStatus() != Status.Created) {
            throw new PermissionDeniedException("Can only assign task owner if status is Created!");
        }

        Status assignedStatus = null;

        if (potentialOwners.size() == 1) {
            // if there is a single potential owner, assign and set status to Reserved
            OrganizationalEntity potentialOwner = potentialOwners.get(0);
            // if there is a single potential user owner, assign and set status to Reserved
            if (potentialOwner instanceof User) {
                taskData.setActualOwner((User) potentialOwner);
                assignedStatus = Status.Reserved;
            }
            //If there is a group set as potentialOwners, set the status to Ready ??
            if (potentialOwner instanceof Group) {
                assignedStatus = Status.Ready;
            }
        } else if (potentialOwners.size() > 1) {
            // multiple potential owners, so set to Ready so one can claim.
            assignedStatus = Status.Ready;
        } else {
            //@TODO we have no potential owners
        }

        if (assignedStatus != null) {
            taskData.setStatus(assignedStatus);
        } else {
            // status wasn't assigned, so just return the currrent status
            assignedStatus = taskData.getStatus();
        }

        return assignedStatus;
    }
    
    protected ExecutionErrorHandler getExecutionErrorHandler() {
        ExecutionErrorManager errorManager = (ExecutionErrorManager) ((org.jbpm.services.task.commands.TaskContext) context).get(EnvironmentName.EXEC_ERROR_MANAGER);
        if (errorManager == null) {
            return new NoOpExecutionErrorHandler();
        }
        return errorManager.getHandler();
    }
}
