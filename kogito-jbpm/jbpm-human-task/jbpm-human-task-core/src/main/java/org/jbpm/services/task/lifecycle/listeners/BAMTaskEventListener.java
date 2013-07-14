/*
 * Copyright 2012 JBoss by Red Hat.
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
package org.jbpm.services.task.lifecycle.listeners;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.event.Reception;
import javax.inject.Inject;

import org.jboss.seam.transaction.Transactional;
import org.jbpm.services.task.events.AfterTaskActivatedEvent;
import org.jbpm.services.task.events.AfterTaskAddedEvent;
import org.jbpm.services.task.events.AfterTaskClaimedEvent;
import org.jbpm.services.task.events.AfterTaskCompletedEvent;
import org.jbpm.services.task.events.AfterTaskExitedEvent;
import org.jbpm.services.task.events.AfterTaskFailedEvent;
import org.jbpm.services.task.events.AfterTaskStartedEvent;
import org.jbpm.services.task.events.AfterTaskStoppedEvent;
import org.jbpm.services.task.impl.TaskServiceEntryPointImpl;
import org.jbpm.services.task.impl.model.BAMTaskSummaryImpl;
import org.jbpm.services.task.impl.model.GroupImpl;
import org.jbpm.services.task.impl.model.UserImpl;
import org.jbpm.shared.services.api.JbpmServicesPersistenceManager;
import org.kie.api.task.model.*;
import org.kie.internal.task.api.model.InternalPeopleAssignments;
import org.kie.internal.task.api.model.InternalTaskData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p></p>This listener implementation populates a table named BAMTASKCUMMARY in order to allow BAM module to query all tasks.</p>
 *
 * <p>The available status for a task instance are:</p>
 * @see org.kie.api.task.model.Status
 * <ul>
 *     <li>Created</li>
 *     <li>Ready</li>
 *     <li>Reserved</li>
 *     <li>InProgress</li>
 *     <li>Suspended</li>
 *     <li>Completed</li>                    org.hibernate.dialect.H2Dialect
 *     <li>Failed</li>
 *     <li>Error</li>
 *     <li>Exited</li>
 *     <li>Obsolete</li>
 * </ul>
 *
 * <p>The BAM module does not use all task predefined stauts, the following list shows the status for a jBPM task and the relationship with the BAM task status:</p>
 * <ul>
 *     <li>Kie Task status - BAM task status</li>
 *     <li>Created - Created</li>
 *     <li>Ready - Ready</li>
 *     <li>Reserved - Reserved</li>
 *     <li>InProgress - InProgress</li>
 *     <li>Suspended - Suspended</li>
 *     <li>Completed - Completed</li>
 *     <li>Failed - Obsolete</li>
 *     <li>Error - Obsolete</li>
 *     <li>Exited - Obsolete</li>
 *     <li>Obsolete - Obsolete</li>
 * </ul>
 */

@ApplicationScoped
@Transactional
public class BAMTaskEventListener implements TaskLifeCycleEventListener {

    /** Class logger. */
    private static final Logger logger = LoggerFactory.getLogger(BAMTaskEventListener.class);

    @Inject
    private JbpmServicesPersistenceManager pm;

    public BAMTaskEventListener() {
    }

    public void afterTaskStartedEvent(@Observes(notifyObserver = Reception.ALWAYS) @AfterTaskStartedEvent Task ti) {
        createOrUpdateTask(ti, Status.InProgress, new BAMTaskWorker() {
            @Override
            public BAMTaskSummaryImpl createTask(BAMTaskSummaryImpl bamTask, Task task) {
                bamTask.setStartDate(new Date());
                return bamTask;
            }

            @Override
            public BAMTaskSummaryImpl updateTask(BAMTaskSummaryImpl bamTask, Task task) {
                bamTask.setStartDate(new Date());
                return bamTask;
            }
        });
    }

    public void afterTaskActivatedEvent(@Observes(notifyObserver = Reception.ALWAYS) @AfterTaskActivatedEvent Task ti) {
        createOrUpdateTask(ti, Status.Ready);
    }

    public void afterTaskClaimedEvent(@Observes(notifyObserver = Reception.ALWAYS) @AfterTaskClaimedEvent Task ti) {
        createOrUpdateTask(ti, Status.Reserved);
    }

    public void afterTaskSkippedEvent(Task ti) {
        createOrUpdateTask(ti, Status.Obsolete);
    }

    public void afterTaskStoppedEvent(@Observes(notifyObserver = Reception.ALWAYS) @AfterTaskStoppedEvent Task ti) {
        createOrUpdateTask(ti, Status.Obsolete);
    }

    public void afterTaskCompletedEvent(@Observes(notifyObserver = Reception.ALWAYS) @AfterTaskCompletedEvent Task ti) {

        createOrUpdateTask(ti, Status.Completed, new BAMTaskWorker() {
            @Override
            public BAMTaskSummaryImpl createTask(BAMTaskSummaryImpl bamTask, Task task) {
                return bamTask;
            }

            @Override
            public BAMTaskSummaryImpl updateTask(BAMTaskSummaryImpl bamTask, Task task) {
                Date completedDate = new Date();
                bamTask.setEndDate(completedDate);
                bamTask.setDuration(completedDate.getTime() - bamTask.getStartDate().getTime());
                return bamTask;
            }
        });
    }

    public void afterTaskFailedEvent(@Observes(notifyObserver = Reception.ALWAYS) @AfterTaskFailedEvent Task ti) {
        createOrUpdateTask(ti, Status.Obsolete);
    }

    /**
     * When a task is added it can be reserved for a user or not.
     * If already reserved for a user when adding it,set reserved status.
     * Otherwise set created status.
     *
     * @param ti The task to add.
     */
    public void afterTaskAddedEvent(@Observes(notifyObserver = Reception.ALWAYS) @AfterTaskAddedEvent Task ti) {

        // Initialize task assigments and status.
        initializeTask(ti);

        // Set the task status.
        createOrUpdateTask(ti);
    }

    public void afterTaskExitedEvent(@Observes(notifyObserver = Reception.ALWAYS) @AfterTaskExitedEvent Task ti) {
        createOrUpdateTask(ti, Status.Obsolete);
    }

    /**
     * Creates or updates a bam task summary instance.
     *
     * @param ti The source task
     * @param newStatus The new state for the task.
     * @param worker Perform additional operations to the bam task summary instance.
     * @return The created or updated bam task summary instance.
     */
    protected BAMTaskSummaryImpl createOrUpdateTask(Task ti, Status newStatus, BAMTaskWorker worker) {
        BAMTaskSummaryImpl result = null;

        if (ti == null) {
            logger.error("The task instance does not exist.");
            return result;
        }

        Status status = newStatus != null ? newStatus : ti.getTaskData().getStatus();

        List<BAMTaskSummaryImpl> taskSummaries = (List<BAMTaskSummaryImpl>) pm.queryStringWithParametersInTransaction("select bts from BAMTaskSummaryImpl bts where bts.taskId=:taskId",
                pm.addParametersToMap("taskId", ti.getId()));
        if (taskSummaries.isEmpty()) {

            String actualOwner = "";
            if (ti.getTaskData().getActualOwner() != null) {
                actualOwner = ti.getTaskData().getActualOwner().getId();
            }

            result = new BAMTaskSummaryImpl(ti.getId(), ti.getNames().get(0).getText(), status.toString(), new Date(), actualOwner, ti.getTaskData().getProcessInstanceId());
            if (worker != null) worker.createTask(result, ti);
            pm.persist(result);
        } else if (taskSummaries.size() == 1) {

            result = taskSummaries.get(0);
            result.setStatus(status.toString());
            if (ti.getTaskData().getActualOwner() != null) {
                result.setUserId(ti.getTaskData().getActualOwner().getId());
            }
            if (worker != null) worker.updateTask(result, ti);
            pm.merge(result);

        } else {
            logger.warn("Something went wrong with the Task BAM Listener");
            throw new IllegalStateException("We cannot have more than one BAM Task Summary for the task id = " + ti.getId());
        }

        return result;
    }

    /**
     * Creates or updates a bam task summary instance.
     *
     * @param ti The source task
     * @return The created or updated bam task summary instance.
     */
    protected BAMTaskSummaryImpl createOrUpdateTask(Task ti) {
        return createOrUpdateTask(ti, null, null);
    }

    /**
     * Creates or updates a bam task summary instance.
     *
     * @param ti The source task
     * @param newStatus The new state for the task.
     * @return The created or updated bam task summary instance.
     */
    protected BAMTaskSummaryImpl createOrUpdateTask(Task ti, Status newStatus) {
        return createOrUpdateTask(ti, newStatus, null);
    }

    /**
     * Interface for performing additional operations to a <code>org.jbpm.services.task.impl.model.BAMTaskSummaryImpl</code> instance.
     */
    protected interface BAMTaskWorker {
        BAMTaskSummaryImpl createTask(BAMTaskSummaryImpl bamTask, Task task);
        BAMTaskSummaryImpl updateTask(BAMTaskSummaryImpl bamTask, Task task);
    }

    /**
     * Chekc the owners for a task and initialzes its assigments.
     *
     * TODO: Duplicate of org.jbpm.services.task.impl.TaskServiceEntryPointImpl#initializeTask.
     * TODO: Centralize this behaviour in a common task handler.
     *
     *
     * @param task The task.
     */
    protected void initializeTask(Task task){
        Status assignedStatus = null;

        if (task.getPeopleAssignments() != null && task.getPeopleAssignments().getPotentialOwners() != null && task.getPeopleAssignments().getPotentialOwners().size() == 1) {
            // if there is a single potential owner, assign and set status to Reserved
            OrganizationalEntity potentialOwner = task.getPeopleAssignments().getPotentialOwners().get(0);
            // if there is a single potential user owner, assign and set status to Reserved
            if (potentialOwner instanceof UserImpl) {
                ((InternalTaskData) task.getTaskData()).setActualOwner((UserImpl) potentialOwner);

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
            ((InternalTaskData) task.getTaskData()).setStatus(assignedStatus);
        }

        if (task.getPeopleAssignments() != null && task.getPeopleAssignments().getBusinessAdministrators() != null) {
            List<OrganizationalEntity> businessAdmins = new ArrayList<OrganizationalEntity>();
            businessAdmins.add(new UserImpl("Administrator"));
            businessAdmins.addAll(task.getPeopleAssignments().getBusinessAdministrators());
            ((InternalPeopleAssignments) task.getPeopleAssignments()).setBusinessAdministrators(businessAdmins);
        }

    }
}
