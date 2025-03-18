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
package org.kie.kogito.process.management;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.jbpm.ruleflow.core.Metadata;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.WorkflowProcess;
import org.kie.kogito.Application;
import org.kie.kogito.Model;
import org.kie.kogito.internal.process.runtime.KogitoWorkflowProcess;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessError;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.ProcessInstanceExecutionException;
import org.kie.kogito.process.Processes;
import org.kie.kogito.process.WorkItem;
import org.kie.kogito.process.impl.AbstractProcess;
import org.kie.kogito.services.uow.UnitOfWorkExecutor;

import com.fasterxml.jackson.databind.JsonNode;

public abstract class BaseProcessInstanceManagementResource<T> implements ProcessInstanceManagement<T> {

    private static final String PROCESS_REQUIRED = "Process id must be given";
    private static final String PROCESS_AND_INSTANCE_REQUIRED = "Process id and Process instance id must be given";
    private static final String PROCESS_NOT_FOUND = "Process with id %s not found";
    private static final String PROCESS_INSTANCE_NOT_FOUND = "Process instance with id %s not found";
    private static final String PROCESS_INSTANCE_NOT_IN_ERROR = "Process instance with id %s is not in error state";

    private Supplier<Processes> processes;

    private Application application;

    public BaseProcessInstanceManagementResource(Processes processes, Application application) {
        this(() -> processes, application);
    }

    public BaseProcessInstanceManagementResource(Supplier<Processes> processes, Application application) {
        this.processes = processes;
        this.application = application;
    }

    public T doGetProcesses() {
        return buildOkResponse(processes.get().processIds());
    }

    public T doGetProcessInfo(String processId) {
        return executeOnProcess(processId, process -> {
            Map<String, Object> data = new HashMap<>();
            data.put("id", process.id());
            data.put("name", process.name());
            data.put("type", process.type());
            data.put("version", process.version());
            if (process instanceof Supplier) {
                org.kie.api.definition.process.Process processDefinition = ((Supplier<org.kie.api.definition.process.Process>) process).get();
                Map<String, Object> metadata = processDefinition.getMetaData();
                String description = (String) metadata.get(Metadata.DESCRIPTION);
                if (description != null) {
                    data.put("description", description);
                }
                List<String> annotations = (List<String>) metadata.get(Metadata.ANNOTATIONS);
                if (annotations != null) {
                    data.put("annotations", annotations);
                }
                if (processDefinition instanceof WorkflowProcess) {
                    WorkflowProcess workflowProcess = (WorkflowProcess) processDefinition;
                    workflowProcess.getInputValidator().flatMap(v -> v.schema(JsonNode.class)).ifPresent(s -> data.put("inputSchema", s));
                    workflowProcess.getOutputValidator().flatMap(v -> v.schema(JsonNode.class)).ifPresent(s -> data.put("outputSchema", s));
                }
            }
            return buildOkResponse(data);
        });
    }

    public T doGetProcessNodes(String processId) {
        return executeOnProcess(processId, process -> {
            List<org.kie.api.definition.process.Node> nodes = ((KogitoWorkflowProcess) ((AbstractProcess<?>) process).get()).getNodesRecursively();
            List<Map<String, Object>> list = nodes.stream().map(n -> {
                Map<String, Object> data = new HashMap<>();
                data.put("id", n.getId().toExternalFormat());
                data.put("uniqueId", ((Node) n).getUniqueId());
                data.put("nodeDefinitionId", n.getUniqueId());
                data.put("metadata", n.getMetaData());
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

    public T doMigrateInstance(String processId, ProcessMigrationSpec migrationSpec, String processInstanceId) {
        try {
            Process<? extends Model> process = processes.get().processById(processId);
            process.instances().migrateProcessInstances(migrationSpec.getTargetProcessId(), migrationSpec.getTargetProcessVersion(), processInstanceId);
            Map<String, Object> message = new HashMap<>();
            message.put("message", processInstanceId + " instance migrated");
            message.put("processInstanceId", processInstanceId);
            return buildOkResponse(message);
        } catch (Exception e) {
            return badRequestResponse(e.getMessage());
        }
    }

    public T doMigrateAllInstances(String processId, ProcessMigrationSpec migrationSpec) {
        try {
            Process<? extends Model> process = processes.get().processById(processId);
            long numberOfProcessInstanceMigrated = process.instances().migrateAll(migrationSpec.getTargetProcessId(), migrationSpec.getTargetProcessVersion());
            Map<String, Object> message = new HashMap<>();
            message.put("message", "All intances migrated");
            message.put("numberOfProcessInstanceMigrated", numberOfProcessInstanceMigrated);
            return buildOkResponse(message);
        } catch (Exception e) {
            return badRequestResponse(e.getMessage());
        }
    }

    public T doGetWorkItemsInProcessInstance(String processId, String processInstanceId) {

        return executeOnProcessInstance(processId, processInstanceId, processInstance -> {
            // use special security policy to bypass auth check as this is management operation
            List<WorkItem> workItems = processInstance.workItems();
            return buildOkResponse(workItems);
        });
    }

    public T doRetriggerInstanceInError(String processId, String processInstanceId) {

        return executeOnInstanceInError(processId, processInstanceId, processInstance -> {
            processInstance.error().get().retrigger();

            if (processInstance.status() == ProcessInstance.STATE_ERROR) {
                throw ProcessInstanceExecutionException.fromError(processInstance);
            } else {
                return buildOkResponse(processInstance.variables());
            }
        });
    }

    public T doSkipInstanceInError(String processId, String processInstanceId) {

        return executeOnInstanceInError(processId, processInstanceId, processInstance -> {
            processInstance.error().get().skip();

            if (processInstance.status() == ProcessInstance.STATE_ERROR) {
                throw ProcessInstanceExecutionException.fromError(processInstance);
            } else {
                return buildOkResponse(processInstance.variables());
            }
        });
    }

    public T doTriggerNodeInstanceId(String processId, String processInstanceId, String nodeId) {

        return executeOnProcessInstance(processId, processInstanceId, processInstance -> {
            processInstance.triggerNode(nodeId);

            if (processInstance.status() == ProcessInstance.STATE_ERROR) {
                throw ProcessInstanceExecutionException.fromError(processInstance);
            } else {
                return buildOkResponse(processInstance.variables());
            }
        });
    }

    public T doRetriggerNodeInstanceId(String processId, String processInstanceId, String nodeInstanceId) {

        return executeOnProcessInstance(processId, processInstanceId, processInstance -> {
            processInstance.retriggerNodeInstance(nodeInstanceId);

            if (processInstance.status() == ProcessInstance.STATE_ERROR) {
                throw ProcessInstanceExecutionException.fromError(processInstance);
            } else {
                return buildOkResponse(processInstance.variables());
            }
        });
    }

    public T doCancelNodeInstanceId(String processId, String processInstanceId, String nodeInstanceId) {

        return executeOnProcessInstance(processId, processInstanceId, processInstance -> {
            processInstance.cancelNodeInstance(nodeInstanceId);

            if (processInstance.status() == ProcessInstance.STATE_ERROR) {
                throw ProcessInstanceExecutionException.fromError(processInstance);
            } else {
                return buildOkResponse(processInstance.variables());
            }
        });
    }

    public T doCancelProcessInstanceId(String processId, String processInstanceId) {

        return executeOnProcessInstance(processId, processInstanceId, processInstance -> {
            processInstance.abort();

            if (processInstance.status() == ProcessInstance.STATE_ERROR) {
                throw ProcessInstanceExecutionException.fromError(processInstance);
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

        Process<?> process = processes.get().processById(processId);
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

        Process<?> process = processes.get().processById(processId);
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

        Process<?> process = processes.get().processById(processId);
        if (process == null) {
            return notFoundResponse(String.format(PROCESS_NOT_FOUND, processId));
        }
        return supplier.apply(process);
    }

    protected abstract <R> T buildOkResponse(R body);

    protected abstract T badRequestResponse(String message);

    protected abstract T notFoundResponse(String message);
}
