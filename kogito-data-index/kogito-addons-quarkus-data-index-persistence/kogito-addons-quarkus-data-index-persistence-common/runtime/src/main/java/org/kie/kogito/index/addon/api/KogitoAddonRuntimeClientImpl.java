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
package org.kie.kogito.index.addon.api;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.eclipse.microprofile.context.ManagedExecutor;
import org.kie.kogito.Application;
import org.kie.kogito.addon.source.files.SourceFilesProvider;
import org.kie.kogito.index.api.KogitoRuntimeClient;
import org.kie.kogito.index.api.KogitoRuntimeCommonClient;
import org.kie.kogito.index.model.Node;
import org.kie.kogito.index.model.ProcessInstance;
import org.kie.kogito.index.model.UserTaskInstance;
import org.kie.kogito.index.service.DataIndexServiceException;
import org.kie.kogito.internal.process.runtime.KogitoWorkflowProcess;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstanceExecutionException;
import org.kie.kogito.process.Processes;
import org.kie.kogito.process.impl.AbstractProcess;
import org.kie.kogito.services.uow.UnitOfWorkExecutor;
import org.kie.kogito.svg.ProcessSvgService;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;

import static java.util.stream.Collectors.toMap;

@ApplicationScoped
public class KogitoAddonRuntimeClientImpl extends KogitoRuntimeCommonClient implements KogitoRuntimeClient {

    private static String SUCCESSFULLY_OPERATION_MESSAGE = "Successfully performed: %s";

    private ProcessSvgService processSvgService;

    private SourceFilesProvider sourceFilesProvider;

    private Processes processes;

    private Application application;

    @Inject
    public KogitoAddonRuntimeClientImpl(Instance<ProcessSvgService> processSvgService,
            SourceFilesProvider sourceFilesProvider,
            Instance<Processes> processesInstance,
            Instance<Application> application) {
        this.processSvgService = processSvgService.isResolvable() ? processSvgService.get() : null;
        this.sourceFilesProvider = sourceFilesProvider;
        this.processes = processesInstance.isResolvable() ? processesInstance.get() : null;
        this.application = application.isResolvable() ? application.get() : null;
    }

    @Inject
    ManagedExecutor managedExecutor;

    static <T> CompletableFuture<T> throwUnsupportedException() {
        return CompletableFuture.failedFuture(new UnsupportedOperationException("Unsupported operation using Data Index addon"));
    }

    @Override
    public CompletableFuture<String> abortProcessInstance(String serviceURL, ProcessInstance processInstance) {
        return CompletableFuture.completedFuture(executeOnProcessInstance(processInstance.getProcessId(), processInstance.getId(), pInstance -> {
            pInstance.abort();

            if (pInstance.status() == org.kie.kogito.process.ProcessInstance.STATE_ERROR) {
                throw new ProcessInstanceExecutionException(pInstance.id(), pInstance.error().get().failedNodeId(), pInstance.error().get().errorMessage());
            } else {
                return String.format(SUCCESSFULLY_OPERATION_MESSAGE, "ABORT ProcessInstance with id: " + processInstance.getId());
            }
        }));
    }

    @Override
    public CompletableFuture<String> retryProcessInstance(String serviceURL, ProcessInstance processInstance) {
        return CompletableFuture.completedFuture(executeOnProcessInstance(processInstance.getProcessId(), processInstance.getId(), pInstance -> {
            pInstance.error().get().retrigger();

            if (pInstance.status() == org.kie.kogito.process.ProcessInstance.STATE_ERROR) {
                throw new ProcessInstanceExecutionException(pInstance.id(), pInstance.error().get().failedNodeId(), pInstance.error().get().errorMessage());
            } else {
                return String.format(SUCCESSFULLY_OPERATION_MESSAGE, "RETRY ProcessInstance in error with id: " + processInstance.getId());
            }
        }));
    }

    @Override
    public CompletableFuture<String> skipProcessInstance(String serviceURL, ProcessInstance processInstance) {
        return CompletableFuture.completedFuture(executeOnProcessInstance(processInstance.getProcessId(), processInstance.getId(), pInstance -> {
            pInstance.error().get().skip();

            if (pInstance.status() == org.kie.kogito.process.ProcessInstance.STATE_ERROR) {
                throw new ProcessInstanceExecutionException(pInstance.id(), pInstance.error().get().failedNodeId(), pInstance.error().get().errorMessage());
            } else {
                return String.format(SUCCESSFULLY_OPERATION_MESSAGE, "SKIP ProcessInstance in error with id: " + processInstance.getId());
            }
        }));
    }

    @Override
    public CompletableFuture<String> updateProcessInstanceVariables(String serviceURL, ProcessInstance processInstance, String variables) {
        return throwUnsupportedException();
    }

    @Override
    public CompletableFuture<String> getProcessInstanceDiagram(String serviceURL, ProcessInstance processInstance) {
        if (processSvgService == null) {
            return CompletableFuture.completedFuture(null);
        } else {
            return CompletableFuture.supplyAsync(() -> processSvgService.getProcessInstanceSvg(processInstance.getProcessId(), processInstance.getId(), null).orElse(null), managedExecutor);
        }
    }

    @Override
    public CompletableFuture<String> getProcessDefinitionSourceFileContent(String serviceURL, String processId) {
        return CompletableFuture.supplyAsync(() -> sourceFilesProvider.getProcessSourceFile(processId)
                .map(sourceFile -> {
                    try {
                        return sourceFile.readContents();
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                })
                .map(String::new)
                .orElseThrow(() -> new DataIndexServiceException("Source file not found for the specified process ID: " + processId)), managedExecutor);
    }

    @Override
    public CompletableFuture<List<Node>> getProcessDefinitionNodes(String serviceURL, String processId) {
        Process<?> process = processes != null ? processes.processById(processId) : null;
        if (process == null) {
            return CompletableFuture.completedFuture(null);
        } else {
            List<org.kie.api.definition.process.Node> nodes = ((KogitoWorkflowProcess) ((AbstractProcess<?>) process).get()).getNodesRecursively();
            List<Node> list = nodes.stream().map(n -> {
                Node data = new Node();
                data.setId(String.valueOf(n.getId()));
                data.setUniqueId(((org.jbpm.workflow.core.Node) n).getUniqueId());
                data.setMetadata(n.getMetaData() == null ? null : mapMetadata(n));
                data.setType(n.getClass().getSimpleName());
                data.setName(n.getName());
                return data;
            }).collect(Collectors.toList());
            return CompletableFuture.completedFuture(list);
        }
    }

    private static Map<String, String> mapMetadata(org.kie.api.definition.process.Node n) {
        return n.getMetaData().entrySet().stream().filter(e -> e.getValue() != null).collect(toMap(Map.Entry::getKey, e -> e.getValue().toString()));
    }

    @Override
    public CompletableFuture<String> triggerNodeInstance(String serviceURL, ProcessInstance processInstance, String nodeDefinitionId) {
        return CompletableFuture.completedFuture(executeOnProcessInstance(processInstance.getProcessId(), processInstance.getId(), pInstance -> {
            pInstance.triggerNode(nodeDefinitionId);

            if (pInstance.status() == org.kie.kogito.process.ProcessInstance.STATE_ERROR) {
                throw new ProcessInstanceExecutionException(pInstance.id(), pInstance.error().get().failedNodeId(), pInstance.error().get().errorMessage());
            } else {
                return String.format(SUCCESSFULLY_OPERATION_MESSAGE,
                        "TRIGGER Node " + nodeDefinitionId + "from ProcessInstance with id: " + processInstance.getId());
            }
        }));
    }

    @Override
    public CompletableFuture<String> retriggerNodeInstance(String serviceURL, ProcessInstance processInstance, String nodeInstanceId) {
        return CompletableFuture.completedFuture(executeOnProcessInstance(processInstance.getProcessId(), processInstance.getId(), pInstance -> {
            pInstance.retriggerNodeInstance(nodeInstanceId);

            if (pInstance.status() == org.kie.kogito.process.ProcessInstance.STATE_ERROR) {
                throw new ProcessInstanceExecutionException(pInstance.id(), pInstance.error().get().failedNodeId(), pInstance.error().get().errorMessage());
            } else {
                return String.format(SUCCESSFULLY_OPERATION_MESSAGE,
                        "RETRIGGER Node instance " + nodeInstanceId + "from ProcessInstance with id: " + processInstance.getId());
            }
        }));
    }

    @Override
    public CompletableFuture<String> cancelNodeInstance(String serviceURL, ProcessInstance processInstance, String nodeInstanceId) {
        return CompletableFuture.completedFuture(executeOnProcessInstance(processInstance.getProcessId(), processInstance.getId(), pInstance -> {
            pInstance.cancelNodeInstance(nodeInstanceId);

            if (pInstance.status() == org.kie.kogito.process.ProcessInstance.STATE_ERROR) {
                throw new ProcessInstanceExecutionException(pInstance.id(), pInstance.error().get().failedNodeId(), pInstance.error().get().errorMessage());
            } else {
                return String.format(SUCCESSFULLY_OPERATION_MESSAGE,
                        "CANCEL Node instance " + nodeInstanceId + "from ProcessInstance with id: " + processInstance.getId());
            }
        }));
    }

    @Override
    public CompletableFuture<String> getUserTaskSchema(String serviceURL, UserTaskInstance userTaskInstance, String user, List<String> groups) {
        return throwUnsupportedException();
    }

    @Override
    public CompletableFuture<String> updateUserTaskInstance(String serviceURL, UserTaskInstance userTaskInstance, String user, List<String> groups, Map taskInfo) {
        return throwUnsupportedException();
    }

    @Override
    public CompletableFuture<String> createUserTaskInstanceComment(String serviceURL, UserTaskInstance userTaskInstance, String user, List<String> groups, String commentInfo) {
        return throwUnsupportedException();
    }

    @Override
    public CompletableFuture<String> createUserTaskInstanceAttachment(String serviceURL, UserTaskInstance userTaskInstance, String user, List<String> groups, String name, String uri) {
        return throwUnsupportedException();
    }

    @Override
    public CompletableFuture<String> updateUserTaskInstanceComment(String serviceURL, UserTaskInstance userTaskInstance, String user, List<String> groups, String commentId, String commentInfo) {
        return throwUnsupportedException();
    }

    @Override
    public CompletableFuture<String> deleteUserTaskInstanceComment(String serviceURL, UserTaskInstance userTaskInstance, String user, List<String> groups, String commentId) {
        return throwUnsupportedException();
    }

    @Override
    public CompletableFuture<String> updateUserTaskInstanceAttachment(String serviceURL, UserTaskInstance userTaskInstance, String user, List<String> groups, String attachmentId, String name,
            String uri) {
        return throwUnsupportedException();
    }

    @Override
    public CompletableFuture<String> deleteUserTaskInstanceAttachment(String serviceURL, UserTaskInstance userTaskInstance, String user, List<String> groups, String attachmentId) {
        return throwUnsupportedException();
    }

    private String executeOnProcessInstance(String processId, String processInstanceId, Function<org.kie.kogito.process.ProcessInstance<?>, String> supplier) {

        Process<?> process = processes != null ? processes.processById(processId) : null;

        if (process == null) {
            throw new DataIndexServiceException(String.format("Unable to find Process instance with id %s to perform the operation requested", processInstanceId));
        }
        return UnitOfWorkExecutor.executeInUnitOfWork(application.unitOfWorkManager(), () -> {
            Optional<? extends org.kie.kogito.process.ProcessInstance<?>> processInstanceFound = process.instances().findById(processInstanceId);
            if (processInstanceFound.isPresent()) {
                org.kie.kogito.process.ProcessInstance<?> processInstance = processInstanceFound.get();
                return supplier.apply(processInstance);
            } else {
                throw new DataIndexServiceException(String.format("Process instance with id %s doesn't allow the operation requested", processInstanceId));
            }
        });
    }
}
