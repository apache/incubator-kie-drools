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

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.persistence.reporting.database.SchemaGenerationAction;
import org.kie.kogito.persistence.reporting.test.TestTypes.TestBootstrapLoader;
import org.kie.kogito.persistence.reporting.test.TestTypes.TestContext;
import org.kie.kogito.persistence.reporting.test.TestTypes.TestDatabaseManager;
import org.kie.kogito.persistence.reporting.test.TestTypes.TestField;
import org.kie.kogito.persistence.reporting.test.TestTypes.TestJsonField;
import org.kie.kogito.persistence.reporting.test.TestTypes.TestMapping;
import org.kie.kogito.persistence.reporting.test.TestTypes.TestMappingDefinition;
import org.kie.kogito.persistence.reporting.test.TestTypes.TestMappingDefinitions;
import org.kie.kogito.persistence.reporting.test.TestTypes.TestMappingService;
import org.kie.kogito.persistence.reporting.test.TestTypes.TestPartitionField;
import org.kie.kogito.persistence.reporting.test.TestTypesImpl;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BaseStartupHandlerTest {

    private static final TestMappingDefinition DEFINITION = new TestTypesImpl.TestMappingDefinitionImpl("mappingId",
            "sourceTableName",
            "sourceTableJsonFieldName",
            List.of(new TestTypesImpl.TestFieldImpl("id")),
            Collections.emptyList(),
            "targetTableName",
            List.of(new TestTypesImpl.TestMappingImpl("sourceJsonPath",
                    new TestTypesImpl.TestJsonFieldImpl("field1",
                            String.class))));

    @Mock
    private TestBootstrapLoader loader;

    @Mock
    private TestDatabaseManager databaseManager;

    @Mock
    private TestMappingService mappingService;

    private static class TestBaseStartupHandler
            extends BaseStartupHandler<Object, TestField, TestPartitionField, TestJsonField, TestMapping, TestMappingDefinition, TestMappingDefinitions, TestContext> {

        TestBaseStartupHandler(final TestBootstrapLoader loader,
                final TestDatabaseManager databaseManager,
                final TestMappingService mappingService,
                final SchemaGenerationAction action) {
            super(loader,
                    databaseManager,
                    mappingService,
                    action);
        }
    }

    @BeforeEach

    @Test
    void testStartup_NONE() {
        final TestBaseStartupHandler service = new TestBaseStartupHandler(loader,
                databaseManager,
                mappingService,
                SchemaGenerationAction.NONE);

        service.onStartup();

        verify(loader, never()).load();
        verify(databaseManager, never()).createArtifacts(any());
        verify(databaseManager, never()).destroyArtifacts(any());
        verify(mappingService, never()).saveMappingDefinition(any());
    }

    @Test
    void testStartup_DROP() {
        final TestBaseStartupHandler service = new TestBaseStartupHandler(loader,
                databaseManager,
                mappingService,
                SchemaGenerationAction.DROP);

        when(mappingService.getAllMappingDefinitions()).thenReturn(List.of(DEFINITION));

        service.onStartup();

        verify(mappingService).getAllMappingDefinitions();
        verify(databaseManager).destroyArtifacts(DEFINITION);

        verify(loader, never()).load();
        verify(databaseManager, never()).createArtifacts(any());
        verify(mappingService, never()).saveMappingDefinition(any());
    }

    @Test
    void testStartup_DROP_AND_CREATE() {
        final TestBaseStartupHandler service = new TestBaseStartupHandler(loader,
                databaseManager,
                mappingService,
                SchemaGenerationAction.DROP_AND_CREATE);

        when(loader.load()).thenReturn(Optional.of(new TestTypesImpl.TestMappingDefinitionsImpl(List.of(DEFINITION))));
        when(mappingService.getAllMappingDefinitions()).thenReturn(List.of(DEFINITION));

        service.onStartup();

        verify(mappingService).getAllMappingDefinitions();
        verify(databaseManager).destroyArtifacts(DEFINITION);

        verify(loader).load();
        verify(databaseManager).createArtifacts(DEFINITION);
        verify(mappingService).saveMappingDefinition(DEFINITION);
    }

    @Test
    void testStartup_CREATE() {
        final TestBaseStartupHandler service = new TestBaseStartupHandler(loader,
                databaseManager,
                mappingService,
                SchemaGenerationAction.CREATE);

        when(loader.load()).thenReturn(Optional.of(new TestTypesImpl.TestMappingDefinitionsImpl(List.of(DEFINITION))));

        service.onStartup();

        verify(mappingService, never()).getAllMappingDefinitions();
        verify(databaseManager, never()).destroyArtifacts(any());

        verify(loader).load();
        verify(databaseManager).createArtifacts(DEFINITION);
        verify(mappingService).saveMappingDefinition(DEFINITION);
    }

}
