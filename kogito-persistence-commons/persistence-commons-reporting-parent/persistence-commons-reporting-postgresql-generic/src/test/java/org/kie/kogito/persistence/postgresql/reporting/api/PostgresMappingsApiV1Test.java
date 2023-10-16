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
package org.kie.kogito.persistence.postgresql.reporting.api;

import java.util.Collection;
import java.util.List;

import javax.ws.rs.core.Response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.persistence.postgresql.reporting.database.GenericPostgresDatabaseManagerImpl;
import org.kie.kogito.persistence.postgresql.reporting.model.JsonType;
import org.kie.kogito.persistence.postgresql.reporting.model.PostgresField;
import org.kie.kogito.persistence.postgresql.reporting.model.PostgresJsonField;
import org.kie.kogito.persistence.postgresql.reporting.model.PostgresMapping;
import org.kie.kogito.persistence.postgresql.reporting.model.PostgresMappingDefinition;
import org.kie.kogito.persistence.postgresql.reporting.model.PostgresMappingDefinitions;
import org.kie.kogito.persistence.postgresql.reporting.model.PostgresPartitionField;
import org.kie.kogito.persistence.postgresql.reporting.service.PostgresMappingServiceImpl;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostgresMappingsApiV1Test {

    @Mock
    private PostgresMappingServiceImpl mappingService;

    @Mock
    private GenericPostgresDatabaseManagerImpl databaseManager;

    private PostgresMappingsApiV1 service;

    @BeforeEach
    public void setup() {
        this.service = new PostgresMappingsApiV1(mappingService, databaseManager);
    }

    @Test
    void testGetAllMappingDefinitions() {
        final PostgresMappingDefinition definition = new PostgresMappingDefinition("mappingId",
                "sourceTableName",
                "sourceTableJsonFieldName",
                List.of(new PostgresField("key")),
                List.of(new PostgresPartitionField("sourceTablePartitionFieldName", "sourceTablePartitionName")),
                "targetTableName",
                List.of(new PostgresMapping("sourceJsonPath",
                        new PostgresJsonField("targetFieldName",
                                JsonType.STRING))));
        when(mappingService.getAllMappingDefinitions()).thenReturn(List.of(definition));

        final Response response = service.getAllMappingDefinitions();

        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertTrue(response.getEntity() instanceof PostgresMappingDefinitions);

        final PostgresMappingDefinitions responseDefinitions = (PostgresMappingDefinitions) response.getEntity();
        final Collection<PostgresMappingDefinition> responseDefinitionsCollection = responseDefinitions.getMappingDefinitions();
        assertEquals(1, responseDefinitionsCollection.size());

        final PostgresMappingDefinition responseDefinition = responseDefinitionsCollection.iterator().next();
        assertEquals(definition, responseDefinition);
    }

    @Test
    void testGetMappingDefinitionByIdWhenFound() {
        final PostgresMappingDefinition definition = new PostgresMappingDefinition("mappingId",
                "sourceTableName",
                "sourceTableJsonFieldName",
                List.of(new PostgresField("key")),
                List.of(new PostgresPartitionField("sourceTablePartitionFieldName", "sourceTablePartitionName")),
                "targetTableName",
                List.of(new PostgresMapping("sourceJsonPath",
                        new PostgresJsonField("targetFieldName",
                                JsonType.STRING))));
        when(mappingService.getMappingDefinitionById(anyString())).thenReturn(definition);

        final Response response = service.getMappingDefinitionById("mappingId");

        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertTrue(response.getEntity() instanceof PostgresMappingDefinition);

        final PostgresMappingDefinition responseDefinition = (PostgresMappingDefinition) response.getEntity();
        assertEquals(definition, responseDefinition);
    }

    @Test
    void testGetMappingDefinitionByIdWhenNotFoundNull() {
        when(mappingService.getMappingDefinitionById(anyString())).thenReturn(null);

        final Response response = service.getMappingDefinitionById("mappingId");
        assertNotNull(response);
        assertEquals(400, response.getStatus());
    }

    @Test
    void testGetMappingDefinitionByIdWhenNotFoundException() {
        when(mappingService.getMappingDefinitionById(anyString())).thenThrow(new IllegalArgumentException());

        final Response response = service.getMappingDefinitionById("mappingId");
        assertNotNull(response);
        assertEquals(400, response.getStatus());
    }

    @Test
    void testCreateMappingDefinition() {
        final PostgresMappingDefinition definition = new PostgresMappingDefinition("mappingId",
                "sourceTableName",
                "sourceTableJsonFieldName",
                List.of(new PostgresField("key")),
                List.of(new PostgresPartitionField("sourceTablePartitionFieldName", "sourceTablePartitionName")),
                "targetTableName",
                List.of(new PostgresMapping("sourceJsonPath",
                        new PostgresJsonField("targetFieldName",
                                JsonType.STRING))));

        final Response response = service.createMappingDefinition(definition);
        assertNotNull(response);
        assertEquals(200, response.getStatus());

        verify(mappingService).saveMappingDefinition(definition);
        verify(databaseManager).createArtifacts(definition);
    }

    @Test
    void testDeleteMappingDefinitionByIdWhenFound() {
        final PostgresMappingDefinition definition = new PostgresMappingDefinition("mappingId",
                "sourceTableName",
                "sourceTableJsonFieldName",
                List.of(new PostgresField("key")),
                List.of(new PostgresPartitionField("sourceTablePartitionFieldName", "sourceTablePartitionName")),
                "targetTableName",
                List.of(new PostgresMapping("sourceJsonPath",
                        new PostgresJsonField("targetFieldName",
                                JsonType.STRING))));

        when(mappingService.deleteMappingDefinitionById("mappingId")).thenReturn(definition);

        final Response response = service.deleteMappingDefinitionById("mappingId");
        assertNotNull(response);
        assertEquals(200, response.getStatus());

        verify(mappingService).deleteMappingDefinitionById("mappingId");
        verify(databaseManager).destroyArtifacts(definition);
    }

    @Test
    void testDeleteMappingDefinitionByIdWhenNotFoundNull() {
        when(mappingService.deleteMappingDefinitionById(anyString())).thenReturn(null);

        final Response response = service.deleteMappingDefinitionById("mappingId");
        assertNotNull(response);
        assertEquals(400, response.getStatus());

        verify(mappingService).deleteMappingDefinitionById("mappingId");
    }

    @Test
    void testDeleteMappingDefinitionByIdWhenNotFoundException() {
        when(mappingService.deleteMappingDefinitionById(anyString())).thenThrow(new IllegalArgumentException());

        final Response response = service.deleteMappingDefinitionById("mappingId");
        assertNotNull(response);
        assertEquals(400, response.getStatus());

        verify(mappingService).deleteMappingDefinitionById("mappingId");
    }

}
