/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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
package org.kie.internal.task.api;

import java.util.Date;
import java.util.List;

import org.kie.api.task.model.TaskSummary;


/**
 * The Task Admintration Service is intended to provide
 *  administrative functions such as:
 *    - Remove and Archive Tasks

 */
public interface TaskAdminService {

    public List<TaskSummary> getActiveTasks();

    public List<TaskSummary> getActiveTasks(Date since);

    public List<TaskSummary> getCompletedTasks();

    public List<TaskSummary> getCompletedTasks(Date since);

    public List<TaskSummary> getCompletedTasksByProcessId(Long processId);

    public int archiveTasks(List<TaskSummary> tasks);

    public List<TaskSummary> getArchivedTasks();

    public int removeTasks(List<TaskSummary> tasks);
    
    public int removeAllTasks();
}
