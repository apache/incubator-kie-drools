/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.task.management.service;

import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.jbpm.process.instance.impl.humantask.HumanTaskHelper;
import org.jbpm.process.instance.impl.humantask.HumanTaskWorkItemImpl;
import org.kie.kogito.internal.process.runtime.KogitoWorkItem;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessConfig;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.ProcessInstanceNotFoundException;
import org.kie.kogito.process.Processes;
import org.kie.kogito.process.workitem.HumanTaskWorkItem;
import org.kie.kogito.process.workitem.Policy;
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
            Policy<?>... policies) {
        ProcessInstance<?> pi = getProcessInstance(processId, processInstanceId, taskId);
        KogitoWorkItem workItem = UnitOfWorkExecutor.executeInUnitOfWork(processConfig.unitOfWorkManager(),
                () -> pi.updateWorkItem(taskId,
                        wi -> {
                            HumanTaskWorkItemImpl humanTask = HumanTaskHelper.asHumanTask(wi);
                            setField(humanTask::setAdminGroups, taskInfo::getAdminGroups, shouldReplace);
                            setField(humanTask::setAdminUsers, taskInfo::getAdminUsers, shouldReplace);
                            setField(humanTask::setExcludedUsers, taskInfo::getExcludedUsers, shouldReplace);
                            setField(humanTask::setPotentialUsers, taskInfo::getPotentialUsers, shouldReplace);
                            setField(humanTask::setPotentialGroups, taskInfo::getPotentialGroups, shouldReplace);
                            setField(humanTask::setTaskPriority, taskInfo::getPriority, shouldReplace);
                            setField(humanTask::setTaskDescription, taskInfo::getDescription, shouldReplace);
                            setMap(humanTask::setParameters, humanTask::setParameter, taskInfo.getInputParams(),
                                    shouldReplace);
                            return wi;
                        }, policies));
        return convert((HumanTaskWorkItem) workItem);
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

    private <T> boolean setField(Consumer<T> consumer, Supplier<T> supplier, boolean shouldReplace) {
        T value = supplier.get();
        boolean result = shouldReplace || value != null;
        if (result) {
            consumer.accept(value);
        }
        return result;
    }

    @Override
    public TaskInfo getTask(String processId, String processInstanceId, String taskId, Policy<?>... policies) {
        return convert(HumanTaskHelper.findTask(getProcessInstance(processId, processInstanceId, taskId), taskId,
                policies));
    }

    private TaskInfo convert(HumanTaskWorkItem humanTask) {
        return new TaskInfo(humanTask.getTaskDescription(), humanTask.getTaskPriority(), humanTask.getPotentialUsers(),
                humanTask.getPotentialGroups(), humanTask.getExcludedUsers(), humanTask.getAdminUsers(),
                humanTask.getAdminGroups(), humanTask.getParameters());
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
