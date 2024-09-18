/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.kogito.task.management.service;

import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.kie.kogito.internal.process.workitem.KogitoWorkItem;
import org.kie.kogito.internal.process.workitem.Policy;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessConfig;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.ProcessInstanceNotFoundException;
import org.kie.kogito.process.Processes;
import org.kie.kogito.process.WorkItem;
import org.kie.kogito.process.workitems.InternalKogitoWorkItem;
import org.kie.kogito.services.uow.UnitOfWorkExecutor;

public class TaskManagementService implements TaskManagementOperations {

    private Processes processes;
    private ProcessConfig processConfig;

    public TaskManagementService(Processes processes, ProcessConfig processConfig) {
        this.processes = processes;
        this.processConfig = processConfig;
    }

    @Override
    public TaskInfo updateTask(String processId,
            String processInstanceId,
            String taskId,
            TaskInfo taskInfo,
            boolean shouldReplace,
            Policy... policies) {
        ProcessInstance<?> pi = getProcessInstance(processId, processInstanceId, taskId);
        KogitoWorkItem workItem = UnitOfWorkExecutor.executeInUnitOfWork(processConfig.unitOfWorkManager(),
                () -> pi.updateWorkItem(taskId,
                        wi -> {
                            InternalKogitoWorkItem task = (InternalKogitoWorkItem) wi;
                            setMap(task::setParameters, task::setParameter, taskInfo.getInputParams(), shouldReplace);
                            return wi;
                        }, policies));
        return convert(workItem);
    }

    private void setMap(Consumer<Map<String, Object>> allConsumer,
            BiConsumer<String, Object> entryConsumer,
            Map<String, Object> params,
            boolean shouldReplace) {
        if (params != null) {
            if (shouldReplace) {
                allConsumer.accept(params);
            } else {
                for (Entry<String, Object> entry : params.entrySet()) {
                    entryConsumer.accept(entry.getKey(), entry.getValue());
                }
            }
        }
    }

    @Override
    public TaskInfo getTask(String processId, String processInstanceId, String taskId, Policy... policies) {
        WorkItem workItem = getProcessInstance(processId, processInstanceId, taskId).workItem(taskId, policies);
        return convert(workItem);
    }

    private TaskInfo convert(WorkItem workItem) {
        return new TaskInfo(
                (String) workItem.getParameters().get("Description"),
                (String) workItem.getParameters().get("Priority"),
                toSet(workItem.getParameters().get("ActorId")),
                toSet(workItem.getParameters().get("GroupId")),
                toSet(workItem.getParameters().get("ExcludedUsersId")),
                toSet(workItem.getParameters().get("BusinessAdministratorId")),
                toSet(workItem.getParameters().get("BusinessGroupsId")),
                workItem.getParameters());
    }

    private TaskInfo convert(KogitoWorkItem workItem) {
        return new TaskInfo(
                (String) workItem.getParameter("Description"),
                (String) workItem.getParameter("Priority"),
                toSet(workItem.getParameter("ActorId")),
                toSet(workItem.getParameter("GroupId")),
                toSet(workItem.getParameter("ExcludedUsersId")),
                toSet(workItem.getParameter("BusinessAdministratorId")),
                toSet(workItem.getParameter("BusinessGroupsId")),
                workItem.getParameters());
    }

    private Set<String> toSet(Object value) {
        if (value == null) {
            return Collections.emptySet();
        }
        if (value instanceof String string) {
            return Set.of(string.split(","));
        }
        return Collections.emptySet();
    }

    private ProcessInstance<?> getProcessInstance(String processId, String processInstanceId, String taskId) {
        if (processId == null) {
            throw new IllegalArgumentException("Process id must be given");
        }
        if (processInstanceId == null) {
            throw new IllegalArgumentException("Process instance id must be given");
        }
        if (taskId == null) {
            throw new IllegalArgumentException("Task id must be given");
        }
        Process<?> process = processes.processById(processId);
        if (process == null) {
            throw new IllegalArgumentException(String.format("Process with id %s not found", processId));
        }
        return process.instances().findById(processInstanceId).orElseThrow(
                () -> new ProcessInstanceNotFoundException(processInstanceId));
    }

}
