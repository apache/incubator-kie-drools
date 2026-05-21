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
package org.kie.kogito.persistence.postgresql.reporting.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.kie.kogito.persistence.api.Storage;
import org.kie.kogito.persistence.postgresql.PostgresStorageService;
import org.kie.kogito.persistence.postgresql.reporting.model.JsonType;
import org.kie.kogito.persistence.postgresql.reporting.model.PostgresField;
import org.kie.kogito.persistence.postgresql.reporting.model.PostgresJsonField;
import org.kie.kogito.persistence.postgresql.reporting.model.PostgresMapping;
import org.kie.kogito.persistence.postgresql.reporting.model.PostgresMappingDefinition;
import org.kie.kogito.persistence.postgresql.reporting.model.PostgresPartitionField;
import org.kie.kogito.persistence.reporting.service.MappingService;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class PostgresMappingServiceImpl implements MappingService<JsonType, PostgresField, PostgresPartitionField, PostgresJsonField, PostgresMapping, PostgresMappingDefinition> {

    static final String CACHE_NAME = "MappingDefinitions";

    private final PostgresStorageService storageService;

    @Inject
    public PostgresMappingServiceImpl(final PostgresStorageService storageService) {
        this.storageService = Objects.requireNonNull(storageService);
    }

    @Override
    public List<PostgresMappingDefinition> getAllMappingDefinitions() {
        final List<PostgresMappingDefinition> mappingDefinitions = new ArrayList<>(storageService.getCache(CACHE_NAME, PostgresMappingDefinition.class).entries().values());
        return Collections.unmodifiableList(mappingDefinitions);
    }

    @Override
    public PostgresMappingDefinition getMappingDefinitionById(final String mappingId) {
        final Storage<String, PostgresMappingDefinition> storage = storageService.getCache(CACHE_NAME, PostgresMappingDefinition.class);
        if (!storage.containsKey(mappingId)) {
            throw new IllegalArgumentException(String.format("A MappingDefinition with ID '%s' cannot be found in the storage.", mappingId));
        }
        return storage.get(mappingId);
    }

    @Override
    @Transactional
    public void saveMappingDefinition(final PostgresMappingDefinition definition) {
        final String mappingId = definition.getMappingId();
        final Storage<String, PostgresMappingDefinition> storage = storageService.getCache(CACHE_NAME, PostgresMappingDefinition.class);
        if (storage.containsKey(mappingId)) {
            throw new IllegalArgumentException(String.format("A MappingDefinition with ID '%s' is already present in the storage.", mappingId));
        }
        storage.put(mappingId, definition);
    }

    @Override
    @Transactional
    public PostgresMappingDefinition deleteMappingDefinitionById(final String mappingId) {
        final Storage<String, PostgresMappingDefinition> storage = storageService.getCache(CACHE_NAME, PostgresMappingDefinition.class);
        if (!storage.containsKey(mappingId)) {
            throw new IllegalArgumentException(String.format("A MappingDefinition with ID '%s' cannot be found in the storage.", mappingId));
        }
        return storage.remove(mappingId);
    }
}
