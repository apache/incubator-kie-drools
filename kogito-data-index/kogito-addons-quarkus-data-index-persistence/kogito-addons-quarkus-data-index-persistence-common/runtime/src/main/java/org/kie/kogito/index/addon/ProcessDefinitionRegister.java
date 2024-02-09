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
package org.kie.kogito.index.addon;

import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.kie.kogito.Application;
import org.kie.kogito.index.api.KogitoRuntimeClient;
import org.kie.kogito.index.model.ProcessDefinition;
import org.kie.kogito.index.model.ProcessDefinitionKey;
import org.kie.kogito.index.service.DataIndexServiceException;
import org.kie.kogito.index.storage.DataIndexStorageService;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.Processes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.runtime.StartupEvent;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.Instance;

import static java.lang.String.format;

@ApplicationScoped
public class ProcessDefinitionRegister {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessDefinitionRegister.class);

    @ConfigProperty(name = "kogito.service.url")
    Optional<String> kogitoServiceUrl;

    void startup(@Observes StartupEvent event, Instance<Processes> processesInstance, Application app, DataIndexStorageService storage, KogitoRuntimeClient client) {
        if (processesInstance.isResolvable()) {
            Processes processes = processesInstance.get();
            processes.processIds().stream()
                    .map(processes::processById)
                    .map(mapProcessDefinition(app.config().addons().availableAddons(), kogitoServiceUrl.orElse(null), client))
                    .forEach(process -> {
                        LOGGER.debug("Registering process definition with id: {}", process.getId());
                        storage.getProcessDefinitionStorage().put(new ProcessDefinitionKey(process.getId(), process.getVersion()), process);
                    });
        } else {
            LOGGER.info("No process definitions to register.");
        }
    }

    private Function<Process<?>, ProcessDefinition> mapProcessDefinition(Set<String> addons, String endpoint, KogitoRuntimeClient client) {
        return p -> {
            ProcessDefinition pd = new ProcessDefinition();
            pd.setId(p.id());
            pd.setName(p.name());
            pd.setVersion(p.version());
            pd.setType(pd.getType());
            pd.setAddons(addons);
            // See ProcessInstanceEventBatch.buildSource
            pd.setEndpoint(endpoint + "/" + (p.id().contains(".") ? p.id().substring(p.id().lastIndexOf('.') + 1) : p.id()));
            try {
                String content = client.getProcessDefinitionSourceFileContent(null, p.id()).get();
                pd.setSource(content);
            } catch (InterruptedException e) {
                LOGGER.warn("Interrupted thread while registering process definition with id: {}", p.id(), e);
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                throw new DataIndexServiceException(format("Failed to register process definition with id: %s", p.id()), e);
            }
            try {
                pd.setNodes(client.getProcessDefinitionNodes(null, p.id()).get());
            } catch (InterruptedException e) {
                LOGGER.warn("Interrupted thread while registering process definition with id: {}", p.id(), e);
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                throw new DataIndexServiceException(format("Failed to register process definition with id: %s", p.id()), e);
            }
            return pd;
        };
    }

}
