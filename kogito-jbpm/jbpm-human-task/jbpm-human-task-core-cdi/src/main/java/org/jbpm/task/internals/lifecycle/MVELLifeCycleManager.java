/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.task.internals.lifecycle;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import org.jboss.seam.transaction.Transactional;
import org.jbpm.task.Content;
import org.jbpm.task.ContentData;
import org.jbpm.task.FaultData;
import org.jbpm.task.Group;
import org.jbpm.task.Operation;
import org.jbpm.task.OrganizationalEntity;
import org.jbpm.task.PeopleAssignments;
import org.jbpm.task.Status;
import org.jbpm.task.Task;
import org.jbpm.task.TaskData;
import org.jbpm.task.User;
import org.jbpm.task.annotations.Internal;
import org.jbpm.task.annotations.Local;
import org.jbpm.task.api.TaskDefService;
import org.jbpm.task.api.TaskIdentityService;
import org.jbpm.task.api.TaskQueryService;

import org.jbpm.task.events.AfterTaskCompletedEvent;
import org.jbpm.task.events.AfterTaskStartedEvent;
import org.jbpm.task.events.BeforeTaskClaimedEvent;
import org.jbpm.task.events.BeforeTaskCompletedEvent;
import org.jbpm.task.events.BeforeTaskFailedEvent;
import org.jbpm.task.events.BeforeTaskSkippedEvent;
import org.jbpm.task.events.BeforeTaskStartedEvent;
import org.jbpm.task.exception.PermissionDeniedException;
import org.jbpm.task.exception.TaskException;
import org.jbpm.task.lifecycle.listeners.TaskLifeCycleEventListener;
import org.jbpm.task.utils.ContentMarshallerHelper;
import org.mvel2.MVEL;
import org.mvel2.ParserConfiguration;
import org.mvel2.ParserContext;

/**
 *
 * @author salaboy
 */
@Mvel
@Transactional
public class MVELLifeCycleManager implements LifeCycleManager {

    private @Inject
    Logger log;
    private @Inject
    EntityManager em;
    private @Inject
    @Local
    TaskDefService taskDefService;
    private @Inject
    @Local
    TaskQueryService taskQueryService;
    private @Inject
    @Local
    TaskIdentityService taskIdentityService;
    private Map<Operation, List<OperationCommand>> operations;
    private @Inject
    Event<Task> taskEvents;
    private @Inject
    @Internal
    TaskLifeCycleEventListener eventListener;

    public MVELLifeCycleManager() {
    }

    public MVELLifeCycleManager(TaskDefService taskDefService, TaskQueryService taskQueryService, TaskIdentityService taskIdentityService, TaskLifeCycleEventListener eventListener) {
        this.taskDefService = taskDefService;
        this.taskQueryService = taskQueryService;
        this.taskIdentityService = taskIdentityService;
        this.eventListener = eventListener;
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
                        System.out.println("No match on status for task " + task.getId() + ": status " + task.getTaskData().getStatus() + " != " + status);
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
                        System.out.println("No match on previous status for task " + task.getId() + ": status " + task.getTaskData().getStatus() + " != " + status);
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
        //@TODO: Include TaskDef Lookup
//        if(task.getTaskType() != null && !task.getTaskType().equals("")){
//            TaskDef taskDef = taskDefService.getTaskDefById(task.getTaskType()); 
//        }
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
                    operationAllowed = isAllowed(user, groupIds, task.getPeopleAssignments().getPotentialOwners());
                    break;
                }
                case BusinessAdministrator: {
                    operationAllowed = isAllowed(user, groupIds, task.getPeopleAssignments().getBusinessAdministrators());
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

        //@TODO: Include TaskDef Lookup
//        if(task.getTaskType() != null && !task.getTaskType().equals("")){
//            TaskDef taskDef = taskDefService.getTaskDefById(task.getTaskType()); 
//        }


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
//                    SendIcal.getInstance().sendIcalForTask(task, service.getUserinfo());
//
//                    // trigger event support
//                    service.getEventSupport().fireTaskClaimed(task.getId(),
//                            task.getTaskData().getActualOwner().getId());
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

            Task task = taskQueryService.getTaskInstanceById(taskId);
            User user = taskIdentityService.getUserById(userId);
            OrganizationalEntity targetEntity = null;
            if (targetEntityId != null && !targetEntityId.equals("")) {
                targetEntity = taskIdentityService.getOrganizationalEntityById(targetEntityId);
            }

            switch (operation) {
                case Start: {
                    taskEvents.select(new AnnotationLiteral<BeforeTaskStartedEvent>() {
                    }).fire(task);
                    break;
                }
                case Claim: {
//                    taskClaimOperation(task);

                    taskEvents.select(new AnnotationLiteral<BeforeTaskClaimedEvent>() {
                    }).fire(task);
                    break;
                }
                case Complete: {
//                    taskCompleteOperation(task, data);


                    taskEvents.select(new AnnotationLiteral<BeforeTaskCompletedEvent>() {
                    }).fire(task);
                    break;
                }
                case Fail: {
//                    taskFailOperation(task, data);
                    if (data != null) {
                        
                        FaultData faultData = ContentMarshallerHelper.marshalFault(data, null);
                        Content content = new Content();
                        content.setContent(faultData.getContent());
                        em.persist(content);
                        task.getTaskData().setFault(content.getId(), faultData);


                    }
                    taskEvents.select(new AnnotationLiteral<BeforeTaskFailedEvent>() {
                    }).fire(task);
                    break;
                }
                case Skip: {
//                    taskSkipOperation(task, userId);
                    taskEvents.select(new AnnotationLiteral<BeforeTaskSkippedEvent>() {
                    }).fire(task);
                    break;
                }
                case Remove: {
//                	taskRemoveOperation(task, user);
                    break;
                }
                case Register: {
//                	taskRegisterOperation(task, user);
                    break;
                }
            }

            evalCommand(operation, commands, task, user, targetEntity, groupIds);

            switch (operation) {
                case Start: {
                    taskEvents.select(new AnnotationLiteral<AfterTaskStartedEvent>() {
                    }).fire(task);
                    break;
                }
                case Forward: {

                    break;
                }
                case Release: {

                    break;
                }
                case Stop: {

                    break;
                }
                case Claim: {

                    break;
                }
                case Complete: {

                    if (data != null) {

                        ContentData result = ContentMarshallerHelper.marshal((Object) data, null);


                        Content content = new Content();
                        content.setContent(result.getContent());


                        em.persist(content);


                        //THIS SHOULD BE AVAILABLE BECAUSE OF THE EXTENDED PERSISTENCE CONTEXT
                        // PROVIDED BY SEAM PERSISTENCE

                        task.getTaskData().setOutput(content.getId(), result);
                        //task.setOutputId(content.getId());


                    }

                    taskEvents.select(new AnnotationLiteral<AfterTaskCompletedEvent>() {
                    }).fire(task);
                    break;
                }
                case Fail: {
//                postTaskFailOperation(task);
                    break;
                }
                case Skip: {
//                postTaskSkipOperation(task, userId);
                    break;
                }
                case Exit: {
//                postTaskExitOperation(task, userId);
                    break;
                }
            }

            
//            tpm.endTransaction(transactionOwner);

        } catch (RuntimeException re) {

            // We may not be the tx owner -- but something has gone wrong.
            // ..which is why we make ourselves owner, and roll the tx back. 
//            boolean takeOverTransaction = true;
            //tpm.rollBackTransaction(takeOverTransaction);

//            doOperationInTransaction(new TransactedOperation() {
//                public void doOperation() {
//                    task.getTaskData().setStatus(Status.Error);
//                }
//            });

            throw re;
        }


    }

    @PostConstruct
    public void initMVELOperations() {

        Map<String, Object> vars = new HashMap<String, Object>();

        // Search operations-dsl.mvel, if necessary using superclass if TaskService is subclassed
        InputStream is = null;
        // for (Class<?> c = getClass(); c != null; c = c.getSuperclass()) {
        is = getClass().getResourceAsStream("/operations-dsl.mvel");
//            if (is != null) {
//                break;
//            }
        //}
        if (is == null) {
            throw new RuntimeException("Unable To initialise TaskService, could not find Operations DSL");
        }
        Reader reader = new InputStreamReader(is);
        try {
            operations = (Map<Operation, List<OperationCommand>>) eval(toString(reader), vars);
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
        pconf.addPackageImport("org.jbpm.task");
//    	pconf.addPackageImport("org.jbpm.task.service");
        pconf.addPackageImport("org.jbpm.task.query");
        pconf.addPackageImport("org.jbpm.task.internals.lifecycle");

        pconf.addImport(Status.class);
        pconf.addImport(Allowed.class);
        pconf.addPackageImport("java.util");

//    	for(String entry : getInputs().keySet()){
//    		pconf.addImport(entry, getInputs().get(entry));
//        }
        ParserContext context = new ParserContext(pconf);
        Serializable s = MVEL.compileExpression(str.trim(), context);

        if (vars != null) {
            return MVEL.executeExpression(s, vars);
        } else {
            return MVEL.executeExpression(s);
        }
    }
    
     public void nominate(long taskId, String userId, List<OrganizationalEntity> potentialOwners) {
        final Task task = em.find(Task.class, taskId);
        final User user = em.find(User.class, userId);
        if (isAllowed(user, null, task.getPeopleAssignments().getBusinessAdministrators())) {


            task.getTaskData().assignOwnerAndStatus(potentialOwners);
            if (task.getTaskData().getStatus() == Status.Ready) {
                task.getPeopleAssignments().setPotentialOwners(potentialOwners);
            }

        } else {
            throw new PermissionDeniedException("User " + userId + " is not allowed to perform Nominate on Task " + taskId);
        }
    }
}
