/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.services.task.impl.factories;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jbpm.services.task.exception.IllegalTaskStateException;
import org.jbpm.services.task.impl.model.GroupImpl;
import org.jbpm.services.task.impl.model.TaskDataImpl;
import org.jbpm.services.task.impl.model.TaskImpl;
import org.jbpm.services.task.impl.model.UserImpl;
import org.jbpm.services.task.utils.MVELUtils;
import org.kie.internal.task.api.model.OrganizationalEntity;
import org.kie.internal.task.api.model.Status;
import org.kie.internal.task.api.model.Task;
import org.kie.internal.task.api.model.TaskData;
import org.kie.internal.task.api.model.TaskDef;

/**
 *
 */
public class TaskFactory {

    public static Task newTask(TaskDef taskDef) {
        Task task = new TaskImpl();
        TaskData taskData = new TaskDataImpl();
        taskData.initialize();
        task.setTaskData(taskData);
        initializeTask(taskDef, task);
        return task;
    }

    /**
     * This method contains the logic to initialize a Task using the defined
     * semantic in the WS-HT specification
     *
     * @param task
     */
    public static void initializeTask(TaskDef taskDef, Task task) {
        if (task.getTaskData().getStatus() != Status.Created) {
            throw new IllegalTaskStateException("We can only initialize tasks in the Created Status!");
        }

        Status assignedStatus = null;

//        if (taskDef.getPeopleAssignments().getPotentialOwners().size() == 1) {
//            // if there is a single potential owner, assign and set status to Reserved
//            OrganizationalEntity potentialOwner = taskDef.getPeopleAssignments().getPotentialOwners().get(0);
//            // if there is a single potential user owner, assign and set status to Reserved
//            if (potentialOwner instanceof User) {
//                task.getTaskData().setActualOwner((User) potentialOwner);
//
//                assignedStatus = Status.Reserved;
//            }
//            //If there is a group set as potentialOwners, set the status to Ready ??
//            if (potentialOwner instanceof Group) {
//
//                assignedStatus = Status.Ready;
//            }
//        } else if (taskDef.getPeopleAssignments().getPotentialOwners().size() > 1) {
//            // multiple potential owners, so set to Ready so one can claim.
//            assignedStatus = Status.Ready;
//        } else {
//            //@TODO we have no potential owners
//        }

        if (assignedStatus != null) {
            task.getTaskData().setStatus(assignedStatus);
        }



    }

    public static void initializeTask(Task task) {
        if (task.getTaskData().getStatus() != Status.Created) {
            throw new IllegalTaskStateException("We can only initialize tasks in the Created Status!");
        }

        Status assignedStatus = null;

        if (task.getPeopleAssignments() != null && task.getPeopleAssignments().getPotentialOwners() != null && task.getPeopleAssignments().getPotentialOwners().size() == 1) {
            // if there is a single potential owner, assign and set status to Reserved
            OrganizationalEntity potentialOwner = task.getPeopleAssignments().getPotentialOwners().get(0);
            // if there is a single potential user owner, assign and set status to Reserved
            if (potentialOwner instanceof UserImpl) {
                task.getTaskData().setActualOwner((UserImpl) potentialOwner);

                assignedStatus = Status.Reserved;
            }
            //If there is a group set as potentialOwners, set the status to Ready ??
            if (potentialOwner instanceof GroupImpl) {

                assignedStatus = Status.Ready;
            }
        } else if (task.getPeopleAssignments() != null && task.getPeopleAssignments().getPotentialOwners() != null && task.getPeopleAssignments().getPotentialOwners().size() > 1) {
            // multiple potential owners, so set to Ready so one can claim.
            assignedStatus = Status.Ready;
        } else {
            //@TODO: we have no potential owners
        }

        if (assignedStatus != null) {
            task.getTaskData().setStatus(assignedStatus);
        }

        if (task.getPeopleAssignments() != null && task.getPeopleAssignments().getBusinessAdministrators() != null) {
            List<OrganizationalEntity> businessAdmins = new ArrayList<OrganizationalEntity>();
            businessAdmins.add(new UserImpl("Administrator"));
            businessAdmins.addAll(task.getPeopleAssignments().getBusinessAdministrators());
            task.getPeopleAssignments().setBusinessAdministrators(businessAdmins);
        }

    }

    public static TaskImpl evalTask(Reader reader, Map<String, Object> vars, boolean initialize) {
        TaskImpl task = null;
        try {
            task = (TaskImpl) MVELUtils.eval(MVELUtils.toString(reader), vars);
            if (initialize) {
                initializeTask(task);
            }

        } catch (IOException ex) {
            Logger.getLogger(TaskFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
        return task;
    }

    public static TaskImpl evalTask(String taskString, Map<String, Object> vars, boolean initialize) {
        TaskImpl task = (TaskImpl) MVELUtils.eval(taskString, vars);
        if (initialize) {
            initializeTask(task);
        }
        return task;
    }

    public static TaskImpl evalTask(Reader reader) {
        return evalTask(reader, null);
    }

    public static TaskImpl evalTask(Reader reader, Map<String, Object> vars) {
        return evalTask(reader, vars, true);
    }
}
