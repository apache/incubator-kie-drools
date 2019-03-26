/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.services.task.audit.impl.model;

import java.util.ArrayList;
import java.util.List;

public class AuditTaskData {

    private AuditTaskImpl auditTask;

    private List<TaskEventImpl> taskEvents = new ArrayList<>();

    private List<TaskVariableImpl> taskInputs;

    private List<TaskVariableImpl> taskOutputs;

    public AuditTaskData(AuditTaskImpl auditTask) {
        this.auditTask = auditTask;
    }
    
    public AuditTaskData(AuditTaskImpl auditTask, TaskEventImpl taskEvent) {
        this.auditTask = auditTask;
        this.taskEvents.add(taskEvent);
    }

    public AuditTaskData(AuditTaskImpl auditTask, List<TaskEventImpl> taskEvents) {
        this.auditTask = auditTask;
        this.taskEvents = taskEvents;
    }

    public AuditTaskData(AuditTaskImpl auditTask, List<TaskEventImpl> taskEvents, List<TaskVariableImpl> taskInputs, List<TaskVariableImpl> taskOutputs) {
        this.auditTask = auditTask;
        this.taskEvents = taskEvents;
        this.taskInputs = taskInputs;
        this.taskOutputs = taskOutputs;
    }

    public AuditTaskImpl getAuditTask() {
        return auditTask;
    }

    public void setAuditTask(AuditTaskImpl auditTask) {
        this.auditTask = auditTask;
    }

    public List<TaskEventImpl> getTaskEvents() {
        return taskEvents;
    }

    public void setTaskEvents(List<TaskEventImpl> taskEvents) {
        this.taskEvents = taskEvents;
    }

    public List<TaskVariableImpl> getTaskInputs() {
        return taskInputs;
    }

    public void setTaskInputs(List<TaskVariableImpl> taskInputs) {
        this.taskInputs = taskInputs;
    }

    public List<TaskVariableImpl> getTaskOutputs() {
        return taskOutputs;
    }

    public void setTaskOutputs(List<TaskVariableImpl> taskOutputs) {
        this.taskOutputs = taskOutputs;
    }
    
    public void addTaskEvent(TaskEventImpl taskEvent) {
        this.taskEvents.add(taskEvent);
    }
    
}
