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

package org.jbpm.services.task.events;

import java.util.Date;
import java.util.EventObject;

import org.kie.api.task.model.Task;
import org.kie.internal.task.api.TaskContext;
import org.kie.internal.task.api.TaskEvent;

public class TaskEventImpl extends EventObject implements TaskEvent {

    private static final long serialVersionUID = -3579310906511209132L;

    private Task task;
    private transient TaskContext taskContext;
    private final Date eventDate;

    public TaskEventImpl(Task task, TaskContext context) {
        super(task);
        this.task = task;
        this.taskContext = context;
        this.eventDate = new Date();
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public TaskContext getTaskContext() {
        return taskContext;
    }

    public void setTaskContext(TaskContext context) {
        this.taskContext = context;
    }

    @Override
    public Date getEventDate() {
        return this.eventDate;
    }

}
