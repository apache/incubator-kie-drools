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
package org.jbpm.task.impl;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jboss.seam.transaction.Transactional;
import org.jbpm.task.Status;
import org.jbpm.task.api.TaskQueryService;
import org.jbpm.task.api.TaskStatisticsService;
import org.jbpm.task.query.TaskSummary;

/**
 *
 */
@Transactional
@ApplicationScoped
public class TaskStatisticsServiceImpl implements TaskStatisticsService{
    @Inject 
    private TaskQueryService queryService;
    
    public int getCompletedTaskByUserId(String userId) {
        List<Status> statuses = new ArrayList<Status>();
        statuses.add(Status.Completed);
        List<TaskSummary> tasksCompleted = queryService.getTasksAssignedAsPotentialOwnerByStatus(userId, statuses, "en-UK");
        return tasksCompleted.size();
    }

    public int getPendingTaskByUserId(String userId) {
        List<TaskSummary> tasksAssigned = queryService.getTasksAssignedAsPotentialOwner(userId, "en-UK");
        return tasksAssigned.size();
    }
    
}
