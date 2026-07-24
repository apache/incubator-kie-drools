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
package org.kie.kogito.persistence.postgresql.reporting.bootstrap;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.persistence.postgresql.reporting.database.BasePostgresDatabaseManagerImpl;
import org.kie.kogito.persistence.postgresql.reporting.model.JsonType;
import org.kie.kogito.persistence.postgresql.reporting.model.PostgresField;
import org.kie.kogito.persistence.postgresql.reporting.model.PostgresJsonField;
import org.kie.kogito.persistence.postgresql.reporting.model.PostgresMapping;
import org.kie.kogito.persistence.postgresql.reporting.model.PostgresMappingDefinition;
import org.kie.kogito.persistence.postgresql.reporting.model.PostgresMappingDefinitions;
import org.kie.kogito.persistence.postgresql.reporting.model.PostgresPartitionField;
import org.kie.kogito.persistence.postgresql.reporting.service.PostgresMappingServiceImpl;
import org.kie.kogito.persistence.reporting.database.SchemaGenerationAction;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.quarkus.runtime.StartupEvent;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostgresStartupHandlerImplTest {

    @Mock
    private PostgresBootstrapLoaderImpl loader;

    @Mock
    private BasePostgresDatabaseManagerImpl databaseManager;

    @Mock
    private PostgresMappingServiceImpl mappingService;

    private PostgresStartupHandlerImpl service;

    @BeforeEach
    public void setup() {
        this.service = new PostgresStartupHandlerImpl(loader,
                databaseManager,
                mappingService,
                SchemaGenerationAction.DROP_AND_CREATE.getActionString());
    }

    @Test
    void testOnStart() {
        final PostgresMappingDefinition definition = new PostgresMappingDefinition("mappingId",
                "sourceTableName",
                "sourceTableJsonFieldName",
                List.of(new PostgresField("key")),
                List.of(new PostgresPartitionField("sourceTablePartitionFieldName", "sourceTablePartitionName")),
                "targetTableName",
                List.of(new PostgresMapping("sourceJsonPath",
                        new PostgresJsonField("targetFieldName",
                                JsonType.STRING))));
        final PostgresMappingDefinitions definitions = new PostgresMappingDefinitions(List.of(definition));
        when(loader.load()).thenReturn(Optional.of(definitions));

        service.onStartup(new StartupEvent());

        verify(mappingService).saveMappingDefinition(definition);
        verify(databaseManager).createArtifacts(definition);
    }
}
