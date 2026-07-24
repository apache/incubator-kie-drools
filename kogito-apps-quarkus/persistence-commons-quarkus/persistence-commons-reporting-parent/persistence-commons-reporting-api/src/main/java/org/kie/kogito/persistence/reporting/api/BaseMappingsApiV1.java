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
package org.kie.kogito.persistence.reporting.api;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.kie.kogito.persistence.reporting.database.DatabaseManager;
import org.kie.kogito.persistence.reporting.database.sqlbuilders.Context;
import org.kie.kogito.persistence.reporting.model.Field;
import org.kie.kogito.persistence.reporting.model.JsonField;
import org.kie.kogito.persistence.reporting.model.Mapping;
import org.kie.kogito.persistence.reporting.model.MappingDefinition;
import org.kie.kogito.persistence.reporting.model.MappingDefinitions;
import org.kie.kogito.persistence.reporting.model.PartitionField;
import org.kie.kogito.persistence.reporting.service.MappingService;

import jakarta.ws.rs.core.Response;

public abstract class BaseMappingsApiV1<T, F extends Field, P extends PartitionField, J extends JsonField<T>, M extends Mapping<T, J>, D extends MappingDefinition<T, F, P, J, M>, S extends MappingDefinitions<T, F, P, J, M, D>, C extends Context<T, F, P, J, M>> {

    private MappingService<T, F, P, J, M, D> mappingService;
    private DatabaseManager<T, F, P, J, M, D, C> databaseManager;

    protected BaseMappingsApiV1() {
        //CDI proxies
    }

    protected BaseMappingsApiV1(final MappingService<T, F, P, J, M, D> mappingService,
            final DatabaseManager<T, F, P, J, M, D, C> databaseManager) {
        this.mappingService = Objects.requireNonNull(mappingService);
        this.databaseManager = Objects.requireNonNull(databaseManager);
    }

    /**
     * Gets all Mapping Definitions.
     *
     * @return All Mapping Definitions.
     */
    protected Response getAllMappingDefinitions() {
        final List<D> result = mappingService.getAllMappingDefinitions();
        return Response.ok(buildMappingDefinitions(result)).build();
    }

    protected abstract S buildMappingDefinitions(final List<D> definitions);

    /**
     * Gets a Mapping Definition by ID.
     *
     * @param mappingId The Mapping Definition ID.
     * @return The Mapping Definition.
     */
    protected Response getMappingDefinitionById(final String mappingId) {
        return retrieveMappingDefinitionById(mappingId)
                .map(mapping -> Response.ok(mapping).build())
                .orElseGet(this::buildBadRequestResponse);
    }

    /**
     * Creates a new Mapping Definition.
     *
     * @return HTTP Response codes.
     */
    protected Response createMappingDefinition(final D definition) {
        mappingService.saveMappingDefinition(definition);
        databaseManager.createArtifacts(definition);
        return Response.ok().build();
    }

    /**
     * Deletes an existing Mapping Definition.
     *
     * @return HTTP Response codes.
     */
    protected Response deleteMappingDefinitionById(final String mappingId) {
        final Optional<D> definition = tryDeleteMappingDefinitionById(mappingId);
        definition.ifPresent(databaseManager::destroyArtifacts);
        return definition.map(d -> Response.ok().build()).orElseGet(this::buildBadRequestResponse);
    }

    private Optional<D> retrieveMappingDefinitionById(final String mappingId) {
        try {
            return Optional.ofNullable(mappingService.getMappingDefinitionById(mappingId));
        } catch (IllegalArgumentException ex) {
            return Optional.empty();
        }
    }

    private Optional<D> tryDeleteMappingDefinitionById(final String mappingId) {
        try {
            return Optional.ofNullable(mappingService.deleteMappingDefinitionById(mappingId));
        } catch (IllegalArgumentException ex) {
            return Optional.empty();
        }
    }

    private Response buildBadRequestResponse() {
        return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).build();
    }
}
