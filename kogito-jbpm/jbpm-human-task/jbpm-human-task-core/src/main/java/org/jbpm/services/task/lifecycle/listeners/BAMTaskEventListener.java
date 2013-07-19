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
import org.jbpm.services.task.events.*;
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
 *     <li>Completed</li>
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
 *     <li>Exited - Exited</li>
 *     <li>Failed - Error</li>
 *     <li>Error - Error</li>
 *     <li>Obsolete - Error</li>
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
        createOrUpdateTask(ti, new BAMTaskWorker() {
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
        createOrUpdateTask(ti);
    }

    public void afterTaskClaimedEvent(@Observes(notifyObserver = Reception.ALWAYS) @AfterTaskClaimedEvent Task ti) {
        createOrUpdateTask(ti);
    }

    public void afterTaskCompletedEvent(@Observes(notifyObserver = Reception.ALWAYS) @AfterTaskCompletedEvent Task ti) {

        createOrUpdateTask(ti, new BAMTaskWorker() {
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

    public void afterTaskAddedEvent(@Observes(notifyObserver = Reception.ALWAYS) @AfterTaskAddedEvent Task ti) {
        createOrUpdateTask(ti);
    }

    /**
     * When a task is skipped, the status for dashbuilder integration task must be Exited.
     *
     * @param ti The task.
     */
    public void afterTaskSkippedEvent(Task ti) {
        createOrUpdateTask(ti, Status.Exited);
    }

    /**
     * When a task is stopped, the status for dashbuilder integration task must be Exited.
     *
     * @param ti The task.
     */
    public void afterTaskStoppedEvent(@Observes(notifyObserver = Reception.ALWAYS) @AfterTaskStoppedEvent Task ti) {
        createOrUpdateTask(ti, Status.Exited);
    }

    /**
     * When a task is failed, the status for dashbuilder integration task must be Exited.
     *
     * @param ti The task.
     */
    public void afterTaskFailedEvent(@Observes(notifyObserver = Reception.ALWAYS) @AfterTaskFailedEvent Task ti) {
        createOrUpdateTask(ti, Status.Error);
    }

    public void afterTaskExitedEvent(@Observes(notifyObserver = Reception.ALWAYS) @AfterTaskExitedEvent Task ti) {
        createOrUpdateTask(ti, Status.Exited);
    }

    public void afterTaskReleasedEvent(@Observes(notifyObserver = Reception.ALWAYS) @AfterTaskReleasedEvent Task ti) {
        createOrUpdateTask(ti);
    }

    public void afterTaskDelegatedEvent(@Observes(notifyObserver = Reception.ALWAYS) @AfterTaskDelegatedEvent Task ti) {
        createOrUpdateTask(ti);
    }

    public void afterTaskForwaredEvent(@Observes(notifyObserver = Reception.ALWAYS) @AfterTaskForwardedEvent Task ti) {
        createOrUpdateTask(ti);
    }

    public void afterTaskNomiatedEvent(@Observes(notifyObserver = Reception.ALWAYS) @AfterTaskNominatedEvent Task ti) {
        createOrUpdateTask(ti);
    }

    public void afterTaskResumedEvent(@Observes(notifyObserver = Reception.ALWAYS) @AfterTaskResumedEvent Task ti) {
        createOrUpdateTask(ti);
    }

    public void afterTaskSuspendedEvent(@Observes(notifyObserver = Reception.ALWAYS) @AfterTaskSuspendedEvent Task ti) {
        createOrUpdateTask(ti);
    }

    /**
     * Creates or updates a bam task summary instance.
     *
     * @param ti The source task
     * @param worker Perform additional operations to the bam task summary instance.
     * @return The created or updated bam task summary instance.
     */
    protected BAMTaskSummaryImpl createOrUpdateTask(Task ti, BAMTaskWorker worker) {
        return createOrUpdateTask(ti, null, worker);
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
     * Interface for performing additional operations to a <code>org.jbpm.services.task.impl.model.BAMTaskSummaryImpl</code> instance.
     */
    protected interface BAMTaskWorker {
        BAMTaskSummaryImpl createTask(BAMTaskSummaryImpl bamTask, Task task);
        BAMTaskSummaryImpl updateTask(BAMTaskSummaryImpl bamTask, Task task);
    }
}
