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
package org.kie.kogito.persistence.reporting.bootstrap;

import java.util.Objects;

import org.kie.kogito.event.cloudevents.utils.CloudEventUtils;
import org.kie.kogito.persistence.reporting.database.DatabaseManager;
import org.kie.kogito.persistence.reporting.database.SchemaGenerationAction;
import org.kie.kogito.persistence.reporting.database.sqlbuilders.Context;
import org.kie.kogito.persistence.reporting.model.Field;
import org.kie.kogito.persistence.reporting.model.JsonField;
import org.kie.kogito.persistence.reporting.model.Mapping;
import org.kie.kogito.persistence.reporting.model.MappingDefinition;
import org.kie.kogito.persistence.reporting.model.MappingDefinitions;
import org.kie.kogito.persistence.reporting.model.PartitionField;
import org.kie.kogito.persistence.reporting.service.MappingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class BaseStartupHandler<T, F extends Field, P extends PartitionField, J extends JsonField<T>, M extends Mapping<T, J>, D extends MappingDefinition<T, F, P, J, M>, S extends MappingDefinitions<T, F, P, J, M, D>, C extends Context<T, F, P, J, M>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseStartupHandler.class);

    private BootstrapLoader<T, F, P, J, M, D, S> loader;
    private DatabaseManager<T, F, P, J, M, D, C> databaseManager;
    private MappingService<T, F, P, J, M, D> mappingService;
    private SchemaGenerationAction action;

    protected BaseStartupHandler() {
        //CDI proxy
    }

    protected BaseStartupHandler(final BootstrapLoader<T, F, P, J, M, D, S> loader,
            final DatabaseManager<T, F, P, J, M, D, C> databaseManager,
            final MappingService<T, F, P, J, M, D> mappingService,
            final SchemaGenerationAction action) {
        this.loader = Objects.requireNonNull(loader);
        this.mappingService = Objects.requireNonNull(mappingService);
        this.databaseManager = Objects.requireNonNull(databaseManager);
        this.action = Objects.requireNonNull(action);
    }

    protected void onStartup() {
        switch (action) {
            case NONE:
                LOGGER.info("Database Schema Action is 'NONE'. Exiting.");
                break;
            case DROP:
                LOGGER.info("Database Schema Action is 'DROP'. Destroying existing database artifacts...");
                mappingService.getAllMappingDefinitions().forEach(databaseManager::destroyArtifacts);
                break;
            case DROP_AND_CREATE:
                LOGGER.info("Database Schema Action is 'DROP-AND-CREATE'. Destroying existing database artifacts...");
                mappingService.getAllMappingDefinitions().forEach(databaseManager::destroyArtifacts);
            case CREATE:
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
}
