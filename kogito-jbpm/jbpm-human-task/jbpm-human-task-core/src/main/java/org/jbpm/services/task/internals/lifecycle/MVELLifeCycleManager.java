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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.core.util.MVELSafeHelper;
import org.jbpm.services.task.events.TaskEventSupport;
import org.jbpm.services.task.exception.PermissionDeniedException;
import org.jbpm.services.task.utils.ContentMarshallerHelper;
import org.kie.api.task.model.Content;
import org.kie.api.task.model.Group;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.PeopleAssignments;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.TaskData;
import org.kie.api.task.model.User;
import org.kie.internal.task.api.TaskContentService;
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

    private TaskPersistenceContext persistenceContext;

    private TaskContentService taskContentService;
    private TaskEventSupport taskEventSupport;
    private static Map<Operation, List<OperationCommand>> operations = initMVELOperations();

    public MVELLifeCycleManager() {
    }
    
    public MVELLifeCycleManager(TaskPersistenceContext persistenceContext, TaskContentService contentService,
    		TaskEventSupport taskEventSupport) {
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
            List<String> groupIds) throws PermissionDeniedException {

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
                            String errorMessage = "User '" + user + "' does not have permissions to execution operation '" + operation + "' on task id " + task.getId();

                            throw new PermissionDeniedException(errorMessage);
                        }

                        commands(command, task, user, targetEntity);
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
                            String errorMessage = "User '" + user + "' does not have permissions to execution operation '" + operation + "' on task id " + task.getId();
                            throw new PermissionDeniedException(errorMessage);
                        }

                        commands(command, task, user, targetEntity);
                    } else {
                        logger.debug("No match on previous status for task {} :status {}  != {}", task.getId(), task.getTaskData().getStatus(), status);
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


        boolean operationAllowed = false;
        for (Allowed allowed : command.getAllowed()) {
            if (operationAllowed) {
                break;
            }
            switch (allowed) {
                case Owner: {
                    operationAllowed = (task.getTaskData().getActualOwner() != null && task.getTaskData().getActualOwner().equals(user));
                    break;
                }
                case Initiator: {
                    operationAllowed = (task.getTaskData().getCreatedBy() != null
                            && (task.getTaskData().getCreatedBy().equals(user))
                            || (groupIds != null && groupIds.contains(task.getTaskData().getCreatedBy().getId())));
                    break;
                }
                case PotentialOwner: {
                    operationAllowed = isAllowed(user, groupIds, (List<OrganizationalEntity>) task.getPeopleAssignments().getPotentialOwners());
                    break;
                }
                case BusinessAdministrator: {
                    operationAllowed = isAllowed(user, groupIds, (List<OrganizationalEntity>) task.getPeopleAssignments().getBusinessAdministrators());
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
            final OrganizationalEntity targetEntity) {


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
            }
        }


    }

    public void taskOperation(final Operation operation, final long taskId, final String userId,
            final String targetEntityId, final Map<String, Object> data,
            List<String> groupIds) throws TaskException {

        try {
            final List<OperationCommand> commands = operations.get(operation);

            Task task = persistenceContext.findTask(taskId);
            if (task == null) {
            	String errorMessage = "Task '" + taskId + "' not found";
                throw new PermissionDeniedException(errorMessage);
            }
            User user = persistenceContext.findUser(userId);
            OrganizationalEntity targetEntity = null;
            if (targetEntityId != null && !targetEntityId.equals("")) {
                targetEntity = persistenceContext.findOrgEntity(targetEntityId);
            }

            switch (operation) {    
                case Activate: {
                	taskEventSupport.fireBeforeTaskActivated(task, persistenceContext);
                    break;
                }
                case Claim: {
                	taskEventSupport.fireBeforeTaskClaimed(task, persistenceContext);
                    break;
                }
                case Complete: {
                	taskEventSupport.fireBeforeTaskCompleted(task, persistenceContext);
                    break;
                }
                case Delegate: {
                	taskEventSupport.fireBeforeTaskDelegated(task, persistenceContext);
                    break;
                }
                case Exit: {
                	taskEventSupport.fireBeforeTaskExited(task, persistenceContext);
                    break;
                }

                case Fail: {
                    if (data != null) {

                        FaultData faultData = ContentMarshallerHelper.marshalFault(data, null);
                        Content content = TaskModelProvider.getFactory().newContent();
                        ((InternalContent)content).setContent(faultData.getContent());
                        persistenceContext.persistContent(content);
                        ((InternalTaskData) task.getTaskData()).setFault(content.getId(), faultData);


                    }
                    taskEventSupport.fireBeforeTaskFailed(task, persistenceContext);
                    break;
                }
                case Forward: {
                	taskEventSupport.fireBeforeTaskForwarded(task, persistenceContext);
                    break;
                }
                case Release: {
                	taskEventSupport.fireBeforeTaskReleased(task, persistenceContext);
                    break;
                }
                case Resume: {
                	taskEventSupport.fireBeforeTaskResumed(task, persistenceContext);
                    break;
                }
                case Skip: {
                	taskEventSupport.fireBeforeTaskSkipped(task, persistenceContext);
                    break;
                }
                case Start: {
                	taskEventSupport.fireBeforeTaskStarted(task, persistenceContext);
                    break;
                }
                case Stop: {
                	taskEventSupport.fireBeforeTaskStopped(task, persistenceContext);
                    break;
                }
                case Suspend: {
                	taskEventSupport.fireBeforeTaskSuspended(task, persistenceContext);
                    break;
                }

            }
            
            evalCommand(operation, commands, task, user, targetEntity, groupIds);

            switch (operation) {
                case Activate: {
                	taskEventSupport.fireAfterTaskActivated(task, persistenceContext);
                    break;
                }
                case Claim: {
                	taskEventSupport.fireAfterTaskClaimed(task, persistenceContext);
                    break;
                }
                case Complete: {
                    if (data != null) {
                        
                        taskContentService.addContent(taskId, data);
                        
                    }

                    taskEventSupport.fireAfterTaskCompleted(task, persistenceContext);
                    break;
                }
                case Delegate: {
                	taskEventSupport.fireAfterTaskDelegated(task, persistenceContext);
                    // This is a really bad hack to execut the correct behavior
                    ((InternalTaskData) task.getTaskData()).setStatus(Status.Reserved);
                }
                case Exit: {
                	taskEventSupport.fireAfterTaskExited(task, persistenceContext);
                    break;
                }
                case Fail: {
                	taskEventSupport.fireAfterTaskFailed(task, persistenceContext);
                    break;
                }
                case Forward: {
                	taskEventSupport.fireAfterTaskForwarded(task, persistenceContext);
                    break;
                }   
                case Release: {
                	taskEventSupport.fireAfterTaskReleased(task, persistenceContext);
                    break;
                }
                case Resume: {
                	taskEventSupport.fireAfterTaskResumed(task, persistenceContext);
                    break;
                }
                case Start: {
                	taskEventSupport.fireAfterTaskStarted(task, persistenceContext);
                    break;
                }
                case Skip: {
                	taskEventSupport.fireAfterTaskSkipped(task, persistenceContext);
                    break;
                }
                case Stop: {
                	taskEventSupport.fireAfterTaskStopped(task, persistenceContext);
                    break;
                }    
                case Suspend: {
                	taskEventSupport.fireAfterTaskSuspended(task, persistenceContext);
                    break;
                }
            }
        } catch (RuntimeException re) {
            throw re;
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

    public void nominate(long taskId, String userId, List<OrganizationalEntity> potentialOwners) {
        final Task task = persistenceContext.findTask(taskId);
        final User user = persistenceContext.findUser(userId);
        if (isAllowed(user, null, (List<OrganizationalEntity>) task.getPeopleAssignments().getBusinessAdministrators())) {


            ((InternalTaskData) task.getTaskData()).assignOwnerAndStatus(potentialOwners);
            if (task.getTaskData().getStatus() == Status.Ready) {
                ((InternalPeopleAssignments) task.getPeopleAssignments()).setPotentialOwners(potentialOwners);
            }

        } else {
            throw new PermissionDeniedException("User " + userId + " is not allowed to perform Nominate on Task " + taskId);
        }
    }
}
