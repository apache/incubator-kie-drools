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
package org.kie.kogito.services.registry;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.kie.kogito.Application;
import org.kie.kogito.event.EventBatch;
import org.kie.kogito.event.impl.ProcessEventBatch;
import org.kie.kogito.event.process.NodeDefinition;
import org.kie.kogito.event.process.ProcessDefinitionDataEvent;
import org.kie.kogito.event.process.ProcessDefinitionEventBody;
import org.kie.kogito.event.process.ProcessDefinitionEventBody.ProcessDefinitionEventBodyBuilder;
import org.kie.kogito.internal.utils.ConversionUtils;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.Processes;
import org.kie.kogito.source.files.SourceFile;
import org.kie.kogito.source.files.SourceFilesProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toSet;

public class ProcessDefinitionEventRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessDefinitionEventRegistry.class);

    private final Application app;
    private final String serviceUrl;
    private final Optional<SourceFilesProvider> sourceFilesProvider;

    public ProcessDefinitionEventRegistry(Application app, String serviceUrl) {
        this(app, serviceUrl, Optional.empty());
    }

    public ProcessDefinitionEventRegistry(Application app, String serviceUrl, Optional<SourceFilesProvider> sourceFilesProvider) {
        this.app = app;
        this.serviceUrl = serviceUrl;
        this.sourceFilesProvider = sourceFilesProvider;
    }

    public void register(Processes processes) {
        EventBatch eventBatch = new ProcessEventBatch();
        processes.processIds().stream()
                .map(processes::processById)
                .map(mapProcessDefinition(app.config().addons().availableAddons(), serviceUrl))
                .forEach(process -> {
                    LOGGER.debug("Registering process definition with id: {}", process.getId());
                    eventBatch.append(process);
                });
        LOGGER.debug("Publishing all processes definitions");
        app.unitOfWorkManager().eventManager().publish(eventBatch);
    }

    private Function<Process<?>, ProcessDefinitionDataEvent> mapProcessDefinition(Set<String> addons, String endpoint) {

        return p -> {
            Map<String, Object> metadata = Collections.emptyMap();
            if (p instanceof Supplier) {
                org.kie.api.definition.process.Process processDefinition = ((Supplier<org.kie.api.definition.process.Process>) p).get();
                if (processDefinition != null) {
                    metadata = processDefinition.getMetaData();
                }
            }
            Set<String> annotations = ((List<String>) metadata.getOrDefault("annotations", emptyList())).stream().collect(toSet());
            String description = (String) metadata.get("Description");

            ProcessDefinitionEventBodyBuilder builder = ProcessDefinitionEventBody.builder().setId(p.id())
                    .setName(p.name())
                    .setVersion(p.version())
                    .setType(p.type())
                    .setAddons(addons)
                    .setEndpoint(getEndpoint(endpoint, p))
                    .setNodes(getNodesDefinitions(p))
                    .setAnnotations(annotations)
                    .setDescription(description)
                    .setMetadata(metadata);
            sourceFilesProvider.flatMap(provider -> provider.getProcessSourceFile(p.id())).map(this::readSourceFile).ifPresentOrElse(builder::setSource,
                    () -> LOGGER.warn("Not source found for process id {}", p.id()));
            return new ProcessDefinitionDataEvent(builder.build());
        };
    }

    private String readSourceFile(SourceFile s) {
        try {
            return new String(s.readContents());
        } catch (IOException e) {
            LOGGER.warn("Error reading content for source file {}", s, e);
            return null;
        }
    }

    private static String getEndpoint(String endpoint, Process<?> p) {
        //sanitize process path in case of fqdn org.acme.ProcessExample -> ProcessExample
        String processPath = ConversionUtils.sanitizeToSimpleName(p.id());
        return endpoint + "/" + processPath;
    }

    private List<NodeDefinition> getNodesDefinitions(Process<?> p) {
        return p.findNodes(n -> true).stream()
                .map(node -> NodeDefinition.builder()
                        .setId(node.getId().toExternalFormat())
                        .setName(node.getName())
                        .setType(node.getClass().getSimpleName())
                        .setUniqueId(node.getUniqueId())
                        .setMetadata(node.getMetaData())
                        .build())
                .collect(Collectors.toList());
    }
}
