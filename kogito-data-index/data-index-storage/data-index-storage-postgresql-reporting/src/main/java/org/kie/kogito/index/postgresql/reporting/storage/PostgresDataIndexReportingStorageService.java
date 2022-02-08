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
package org.kie.kogito.index.postgresql.reporting.storage;

import java.util.Objects;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;

import org.kie.kogito.event.cloudevents.utils.CloudEventUtils;
import org.kie.kogito.index.postgresql.storage.JobEntityStorage;
import org.kie.kogito.index.postgresql.storage.PostgreSqlStorageService;
import org.kie.kogito.index.postgresql.storage.ProcessInstanceEntityStorage;
import org.kie.kogito.index.postgresql.storage.UserTaskInstanceEntityStorage;
import org.kie.kogito.persistence.postgresql.reporting.bootstrap.PostgresBootstrapLoaderImpl;
import org.kie.kogito.persistence.postgresql.reporting.service.PostgresMappingServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkus.arc.properties.IfBuildProperty;
import io.quarkus.runtime.StartupEvent;

import static org.kie.kogito.persistence.api.factory.Constants.PERSISTENCE_TYPE_PROPERTY;
import static org.kie.kogito.persistence.postgresql.Constants.POSTGRESQL_STORAGE;

@Alternative
@ApplicationScoped
@IfBuildProperty(name = PERSISTENCE_TYPE_PROPERTY, stringValue = POSTGRESQL_STORAGE)
public class PostgresDataIndexReportingStorageService extends PostgreSqlStorageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostgresDataIndexReportingStorageService.class);

    private PostgresBootstrapLoaderImpl loader;
    private PostgresDataIndexDatabaseManagerImpl databaseManager;
    private PostgresMappingServiceImpl mappingService;

    protected PostgresDataIndexReportingStorageService() {
        //CDI proxy
    }

    @Inject
    public PostgresDataIndexReportingStorageService(final ProcessInstanceEntityStorage processStorage,
            final JobEntityStorage jobStorage,
            final UserTaskInstanceEntityStorage taskStorage,
            final PostgresBootstrapLoaderImpl loader,
            final PostgresDataIndexDatabaseManagerImpl databaseManager,
            final PostgresMappingServiceImpl mappingService) {
        super(processStorage, jobStorage, taskStorage);
        this.loader = Objects.requireNonNull(loader);
        this.mappingService = Objects.requireNonNull(mappingService);
        this.databaseManager = Objects.requireNonNull(databaseManager);
    }

    void onStart(final @Observes StartupEvent event) {
        LOGGER.info("Loading bootstrap Mapping Definitions...");

        loader.load().ifPresent(mappingDefinitions -> {
            try {
                final ObjectMapper mapper = CloudEventUtils.Mapper.mapper();
                LOGGER.info(String.format("Bootstrap Mapping Definitions are:%n%s",
                        mapper.writerWithDefaultPrettyPrinter().writeValueAsString(mappingDefinitions)));
            } catch (JsonProcessingException jpe) {
                LOGGER.error(String.format("Failed to load bootstrap Mapping Definitions: %s",
                        jpe.getMessage()));
            }

            mappingDefinitions.getMappingDefinitions().forEach(definition -> {
                try {
                    mappingService.saveMappingDefinition(definition);
                    databaseManager.createArtifacts(definition);
                } catch (Exception e) {
                    LOGGER.error(String.format("Failed to process MappingDefinition '%s'%n%s",
                            definition.getMappingId(),
                            e.getMessage()));
                }
            });
        });
    }
}
