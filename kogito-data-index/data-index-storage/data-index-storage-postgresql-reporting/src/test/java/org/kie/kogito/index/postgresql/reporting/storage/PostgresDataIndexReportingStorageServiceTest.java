/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.index.postgresql.storage.JobEntityStorage;
import org.kie.kogito.index.postgresql.storage.ProcessInstanceEntityStorage;
import org.kie.kogito.index.postgresql.storage.UserTaskInstanceEntityStorage;
import org.kie.kogito.persistence.postgresql.reporting.bootstrap.PostgresBootstrapLoaderImpl;
import org.kie.kogito.persistence.postgresql.reporting.model.JsonType;
import org.kie.kogito.persistence.postgresql.reporting.model.PostgresField;
import org.kie.kogito.persistence.postgresql.reporting.model.PostgresMapping;
import org.kie.kogito.persistence.postgresql.reporting.model.PostgresMappingDefinition;
import org.kie.kogito.persistence.postgresql.reporting.model.PostgresMappingDefinitions;
import org.kie.kogito.persistence.postgresql.reporting.service.PostgresMappingServiceImpl;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.quarkus.runtime.StartupEvent;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostgresDataIndexReportingStorageServiceTest {

    @Mock
    private ProcessInstanceEntityStorage processStorage;

    @Mock
    private JobEntityStorage jobStorage;

    @Mock
    private UserTaskInstanceEntityStorage taskStorage;

    @Mock
    private PostgresBootstrapLoaderImpl loader;

    @Mock
    private PostgresDataIndexDatabaseManagerImpl databaseManager;

    @Mock
    private PostgresMappingServiceImpl mappingService;

    @Mock
    private StartupEvent event;

    private PostgresDataIndexReportingStorageService service;

    @BeforeEach
    void setup() {
        service = new PostgresDataIndexReportingStorageService(processStorage,
                jobStorage,
                taskStorage,
                loader,
                databaseManager,
                mappingService);
    }

    @Test
    void testOnStartup() {
        final PostgresMappingDefinition definition = new PostgresMappingDefinition("different",
                "sourceTableName",
                "sourceTableJsonFieldName",
                List.of(new PostgresField("identifyField", JsonType.STRING)),
                "targetTableName",
                List.of(new PostgresMapping("sourceJsonPath", new PostgresField("mappedField", JsonType.STRING))));
        final Optional<PostgresMappingDefinitions> definitions = Optional.of(new PostgresMappingDefinitions(List.of(definition)));
        when(loader.load()).thenReturn(definitions);

        service.onStart(event);

        verify(loader).load();
        verify(databaseManager).createArtifacts(definition);
        verify(mappingService).saveMappingDefinition(definition);
    }

}
