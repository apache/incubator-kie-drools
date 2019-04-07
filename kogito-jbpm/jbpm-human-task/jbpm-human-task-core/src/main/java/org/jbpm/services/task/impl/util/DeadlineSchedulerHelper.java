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

package org.jbpm.services.task.impl.util;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.jbpm.services.task.commands.TaskContext;
import org.kie.api.runtime.Environment;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.PeopleAssignments;
import org.kie.internal.task.api.TaskDeadlinesService;
import org.kie.internal.task.api.TaskDeadlinesService.DeadlineType;
import org.kie.internal.task.api.TaskPersistenceContext;
import org.kie.internal.task.api.model.Deadline;
import org.kie.internal.task.api.model.Deadlines;
import org.kie.internal.task.api.model.InternalTask;


public class DeadlineSchedulerHelper {


    public static void rescheduleDeadlinesForTask(final InternalTask task, TaskContext taskContext, DeadlineType ...types) {
        Environment environment = taskContext.getTaskContentService().getMarshallerContext(task).getEnvironment();
        TaskPersistenceContext persistenceContext = taskContext.getPersistenceContext();
        taskContext.loadTaskVariables(task);
        PeopleAssignments peopleAssignments = task.getPeopleAssignments();
        List<OrganizationalEntity> businessAdministrators = peopleAssignments.getBusinessAdministrators();
        List<DeadlineType> deadlineTypes = Arrays.asList(types);
        
        Deadlines deadlines = HumanTaskHandlerHelper.setDeadlines(task.getTaskData().getTaskInputVariables(), businessAdministrators, environment);
        if(deadlineTypes.contains(DeadlineType.START)) {
            for(Deadline deadline : deadlines.getStartDeadlines()) {
                task.getDeadlines().getStartDeadlines().add(deadline);
                persistenceContext.persistDeadline(deadline);
            }
        }
        if(deadlineTypes.contains(DeadlineType.END)) {
            for(Deadline deadline : deadlines.getEndDeadlines()) {
                task.getDeadlines().getEndDeadlines().add(deadline);
                persistenceContext.persistDeadline(deadline);
            }
        }
        persistenceContext.updateTask(task);
        scheduleDeadlinesForTask(task, taskContext, types);
    }

    public static void scheduleDeadlinesForTask(final InternalTask task, TaskContext taskContext, DeadlineType ...types) {
        TaskDeadlinesService deadlineService = taskContext.getTaskDeadlinesService();
        final long now = System.currentTimeMillis();
        List<DeadlineType> deadlineTypes = Arrays.asList(types);
        Deadlines deadlines = task.getDeadlines();

        if (deadlines != null) {
            final List<? extends Deadline> startDeadlines = deadlines.getStartDeadlines();

            if (startDeadlines != null && deadlineTypes.contains(DeadlineType.START)) {
                scheduleDeadlines(startDeadlines, now, task.getId(), DeadlineType.START, deadlineService);
            }

            final List<? extends Deadline> endDeadlines = deadlines.getEndDeadlines();

            if (endDeadlines != null && deadlineTypes.contains(DeadlineType.END)) {
                scheduleDeadlines(endDeadlines, now, task.getId(), DeadlineType.END, deadlineService);
            }
        }
    }

    public static void scheduleDeadlines(final List<? extends Deadline> deadlines, final long now, 
            final long taskId, DeadlineType type, TaskDeadlinesService deadlineService) {
        for (Deadline deadline : deadlines) {
            if (!deadline.isEscalated()) {
                // only escalate when true - typically this would only be true
                // if the user is requested that the notification should never be escalated
                Date date = deadline.getDate();
                deadlineService.schedule(taskId, deadline.getId(), date.getTime() - now, type);
            }
        }
    }


}
