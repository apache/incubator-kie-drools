/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.process.management;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.kie.api.definition.process.Node;
import org.kie.api.definition.process.WorkflowProcess;
import org.kie.kogito.Application;
import org.kie.kogito.auth.SecurityPolicy;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessError;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.ProcessInstanceExecutionException;
import org.kie.kogito.process.Processes;
import org.kie.kogito.process.WorkItem;
import org.kie.kogito.process.impl.AbstractProcess;
import org.kie.kogito.services.uow.UnitOfWorkExecutor;

import static org.jbpm.ruleflow.core.Metadata.UNIQUE_ID;

public abstract class BaseProcessInstanceManagementResource<T> implements ProcessInstanceManagement<T> {

    private static final String PROCESS_REQUIRED = "Process id must be given";
    private static final String PROCESS_AND_INSTANCE_REQUIRED = "Process id and Process instance id must be given";
    private static final String PROCESS_NOT_FOUND = "Process with id %s not found";
    private static final String PROCESS_INSTANCE_NOT_FOUND = "Process instance with id %s not found";
    private static final String PROCESS_INSTANCE_NOT_IN_ERROR = "Process instance with id %s is not in error state";

    private Processes processes;

    private Application application;

    public BaseProcessInstanceManagementResource(Processes processes, Application application) {
        this.processes = processes;
        this.application = application;
    }

    public T doGetProcessNodes(String processId) {
        return executeOnProcess(processId, process -> {
            List<Node> nodes = ((WorkflowProcess) ((AbstractProcess<?>) process).process()).getNodesRecursively();
            List<Map<String, Object>> list = nodes.stream().map(n -> {
                Map<String, Object> data = new HashMap<>();
                data.put("id", n.getId());
                data.put("uniqueId", ((org.jbpm.workflow.core.Node) n).getUniqueId());
                data.put("nodeDefinitionId", n.getMetaData().get(UNIQUE_ID));
                data.put("type", n.getClass().getSimpleName());
                data.put("name", n.getName());
                return data;
            }).collect(Collectors.toList());
            return buildOkResponse(list);
        });
    }

    public T doGetInstanceInError(String processId, String processInstanceId) {

        return executeOnInstanceInError(processId, processInstanceId, processInstance -> {
            ProcessError error = processInstance.error().get();

            Map<String, String> data = new HashMap<>();
            data.put("id", processInstance.id());
            data.put("failedNodeId", error.failedNodeId());
            data.put("message", error.errorMessage());

            return buildOkResponse(data);
        });
    }

    public T doGetWorkItemsInProcessInstance(String processId, String processInstanceId) {

        return executeOnProcessInstance(processId, processInstanceId, processInstance -> {
            // use special security policy to bypass auth check as this is management operation
            List<WorkItem> workItems = processInstance.workItems(new SecurityPolicy(null) {
            });

            return buildOkResponse(workItems);
        });
    }

    public T doRetriggerInstanceInError(String processId, String processInstanceId) {

        return executeOnInstanceInError(processId, processInstanceId, processInstance -> {
            processInstance.error().get().retrigger();

            if (processInstance.status() == ProcessInstance.STATE_ERROR) {
                throw new ProcessInstanceExecutionException(processInstance.id(), processInstance.error().get().failedNodeId(), processInstance.error().get().errorMessage());
            } else {
                return buildOkResponse(processInstance.variables());
            }
        });
    }

    public T doSkipInstanceInError(String processId, String processInstanceId) {

        return executeOnInstanceInError(processId, processInstanceId, processInstance -> {
            processInstance.error().get().skip();

            if (processInstance.status() == ProcessInstance.STATE_ERROR) {
                throw new ProcessInstanceExecutionException(processInstance.id(), processInstance.error().get().failedNodeId(), processInstance.error().get().errorMessage());
            } else {
                return buildOkResponse(processInstance.variables());
            }
        });
    }

    public T doTriggerNodeInstanceId(String processId, String processInstanceId, String nodeId) {

        return executeOnProcessInstance(processId, processInstanceId, processInstance -> {
            processInstance.triggerNode(nodeId);

            if (processInstance.status() == ProcessInstance.STATE_ERROR) {
                throw new ProcessInstanceExecutionException(processInstance.id(), processInstance.error().get().failedNodeId(), processInstance.error().get().errorMessage());
            } else {
                return buildOkResponse(processInstance.variables());
            }
        });
    }

    public T doRetriggerNodeInstanceId(String processId, String processInstanceId, String nodeInstanceId) {

        return executeOnProcessInstance(processId, processInstanceId, processInstance -> {
            processInstance.retriggerNodeInstance(nodeInstanceId);

            if (processInstance.status() == ProcessInstance.STATE_ERROR) {
                throw new ProcessInstanceExecutionException(processInstance.id(), processInstance.error().get().failedNodeId(), processInstance.error().get().errorMessage());
            } else {
                return buildOkResponse(processInstance.variables());
            }
        });
    }

    public T doCancelNodeInstanceId(String processId, String processInstanceId, String nodeInstanceId) {

        return executeOnProcessInstance(processId, processInstanceId, processInstance -> {
            processInstance.cancelNodeInstance(nodeInstanceId);

            if (processInstance.status() == ProcessInstance.STATE_ERROR) {
                throw new ProcessInstanceExecutionException(processInstance.id(), processInstance.error().get().failedNodeId(), processInstance.error().get().errorMessage());
            } else {
                return buildOkResponse(processInstance.variables());
            }
        });
    }

    public T doCancelProcessInstanceId(String processId, String processInstanceId) {

        return executeOnProcessInstance(processId, processInstanceId, processInstance -> {
            processInstance.abort();

            if (processInstance.status() == ProcessInstance.STATE_ERROR) {
                throw new ProcessInstanceExecutionException(processInstance.id(), processInstance.error().get().failedNodeId(), processInstance.error().get().errorMessage());
            } else {
                return buildOkResponse(processInstance.variables());
            }
        });
    }

    /*
     * Helper methods
     */
    private T executeOnInstanceInError(String processId, String processInstanceId, Function<ProcessInstance<?>, T> supplier) {
        if (processId == null || processInstanceId == null) {
            return badRequestResponse(PROCESS_AND_INSTANCE_REQUIRED);
        }

        Process<?> process = processes.processById(processId);
        if (process == null) {
            return notFoundResponse(String.format(PROCESS_NOT_FOUND, processId));
        }

        return UnitOfWorkExecutor.executeInUnitOfWork(application.unitOfWorkManager(), () -> {
            Optional<? extends ProcessInstance<?>> processInstanceFound = process.instances().findById(processInstanceId);
            if (processInstanceFound.isPresent()) {
                ProcessInstance<?> processInstance = processInstanceFound.get();

                if (processInstance.error().isPresent()) {
                    return supplier.apply(processInstance);
                } else {
                    return badRequestResponse(String.format(PROCESS_INSTANCE_NOT_IN_ERROR, processInstanceId));
                }
            } else {
                return notFoundResponse(String.format(PROCESS_INSTANCE_NOT_FOUND, processInstanceId));
            }
        });
    }

    private T executeOnProcessInstance(String processId, String processInstanceId, Function<ProcessInstance<?>, T> supplier) {
        if (processId == null || processInstanceId == null) {
            return badRequestResponse(PROCESS_AND_INSTANCE_REQUIRED);
        }

        Process<?> process = processes.processById(processId);
        if (process == null) {
            return notFoundResponse(String.format(PROCESS_NOT_FOUND, processId));
        }
        return UnitOfWorkExecutor.executeInUnitOfWork(application.unitOfWorkManager(), () -> {
            Optional<? extends ProcessInstance<?>> processInstanceFound = process.instances().findById(processInstanceId);
            if (processInstanceFound.isPresent()) {
                ProcessInstance<?> processInstance = processInstanceFound.get();

                return supplier.apply(processInstance);
            } else {
                return notFoundResponse(String.format(PROCESS_INSTANCE_NOT_FOUND, processInstanceId));
            }
        });
    }

    private T executeOnProcess(String processId, Function<Process<?>, T> supplier) {
        if (processId == null) {
            return badRequestResponse(PROCESS_REQUIRED);
        }

        Process<?> process = processes.processById(processId);
        if (process == null) {
            return notFoundResponse(String.format(PROCESS_NOT_FOUND, processId));
        }
        return supplier.apply(process);
    }

    protected abstract <R> T buildOkResponse(R body);

    protected abstract T badRequestResponse(String message);

    protected abstract T notFoundResponse(String message);
}
