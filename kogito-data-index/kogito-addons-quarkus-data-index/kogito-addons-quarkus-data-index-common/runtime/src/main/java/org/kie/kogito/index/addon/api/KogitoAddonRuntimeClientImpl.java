/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.index.addon.api;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.eclipse.microprofile.context.ManagedExecutor;
import org.kie.kogito.addon.source.files.SourceFilesProvider;
import org.kie.kogito.index.api.KogitoRuntimeClient;
import org.kie.kogito.index.model.Job;
import org.kie.kogito.index.model.Node;
import org.kie.kogito.index.model.ProcessInstance;
import org.kie.kogito.index.model.UserTaskInstance;
import org.kie.kogito.index.service.DataIndexServiceException;
import org.kie.kogito.internal.process.runtime.KogitoWorkflowProcess;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.Processes;
import org.kie.kogito.process.impl.AbstractProcess;
import org.kie.kogito.svg.ProcessSvgService;

import static org.jbpm.ruleflow.core.Metadata.UNIQUE_ID;

@ApplicationScoped
public class KogitoAddonRuntimeClientImpl implements KogitoRuntimeClient {

    private ProcessSvgService processSvgService;

    private SourceFilesProvider sourceFilesProvider;

    private Processes processes;

    @Inject
    public KogitoAddonRuntimeClientImpl(Instance<ProcessSvgService> processSvgService,
            SourceFilesProvider sourceFilesProvider,
            Instance<Processes> processesInstance) {
        this.processSvgService = processSvgService.isResolvable() ? processSvgService.get() : null;
        this.sourceFilesProvider = sourceFilesProvider;
        this.processes = processesInstance.isResolvable() ? processesInstance.get() : null;
    }

    @Inject
    ManagedExecutor managedExecutor;

    static <T> CompletableFuture<T> throwUnsupportedException() {
        return CompletableFuture.failedFuture(new UnsupportedOperationException());
    }

    @Override
    public CompletableFuture<String> abortProcessInstance(String serviceURL, ProcessInstance processInstance) {
        return throwUnsupportedException();
    }

    @Override
    public CompletableFuture<String> retryProcessInstance(String serviceURL, ProcessInstance processInstance) {
        return throwUnsupportedException();
    }

    @Override
    public CompletableFuture<String> skipProcessInstance(String serviceURL, ProcessInstance processInstance) {
        return throwUnsupportedException();
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
    public CompletableFuture<String> getProcessInstanceSourceFileContent(String serviceURL, ProcessInstance processInstance) {
        return CompletableFuture.supplyAsync(() -> sourceFilesProvider.getProcessSourceFile(processInstance.getProcessId())
                .map(sourceFile -> {
                    try {
                        return sourceFile.readContents();
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                })
                .map(String::new)
                .orElseThrow(() -> new DataIndexServiceException("Source file not found for the specified process ID: " + processInstance.getProcessId())), managedExecutor);
    }

    @Override
    public CompletableFuture<List<Node>> getProcessInstanceNodeDefinitions(String serviceURL, ProcessInstance processInstance) {
        Process<?> process = processes != null ? processes.processById(processInstance.getProcessId()) : null;
        if (process == null) {
            return CompletableFuture.completedFuture(null);
        } else {
            List<org.kie.api.definition.process.Node> nodes = ((KogitoWorkflowProcess) ((AbstractProcess<?>) process).get()).getNodesRecursively();
            List<Node> list = nodes.stream().map(n -> {
                Node data = new Node();
                data.setId(String.valueOf(n.getId()));
                data.setNodeId(((org.jbpm.workflow.core.Node) n).getUniqueId());
                data.setDefinitionId((String) n.getMetaData().get(UNIQUE_ID));
                data.setType(n.getClass().getSimpleName());
                data.setName(n.getName());
                return data;
            }).collect(Collectors.toList());
            return CompletableFuture.completedFuture(list);
        }
    }

    @Override
    public CompletableFuture<String> triggerNodeInstance(String serviceURL, ProcessInstance processInstance, String nodeDefinitionId) {
        return throwUnsupportedException();
    }

    @Override
    public CompletableFuture<String> retriggerNodeInstance(String serviceURL, ProcessInstance processInstance, String nodeInstanceId) {
        return throwUnsupportedException();
    }

    @Override
    public CompletableFuture<String> cancelNodeInstance(String serviceURL, ProcessInstance processInstance, String nodeInstanceId) {
        return throwUnsupportedException();
    }

    @Override
    public CompletableFuture<String> cancelJob(String serviceURL, Job job) {
        return throwUnsupportedException();
    }

    @Override
    public CompletableFuture<String> rescheduleJob(String serviceURL, Job job, String newJobData) {
        return throwUnsupportedException();
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
}
