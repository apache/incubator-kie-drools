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
package org.kie.kogito.persistence.reporting.database;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.persistence.reporting.model.paths.JoinPathSegment;
import org.kie.kogito.persistence.reporting.model.paths.PathSegment;
import org.kie.kogito.persistence.reporting.model.paths.TerminalPathSegment;
import org.kie.kogito.persistence.reporting.test.TestTypes.TestApplyMappingSqlBuilder;
import org.kie.kogito.persistence.reporting.test.TestTypes.TestContext;
import org.kie.kogito.persistence.reporting.test.TestTypes.TestField;
import org.kie.kogito.persistence.reporting.test.TestTypes.TestIndexesSqlBuilder;
import org.kie.kogito.persistence.reporting.test.TestTypes.TestJsonField;
import org.kie.kogito.persistence.reporting.test.TestTypes.TestMapping;
import org.kie.kogito.persistence.reporting.test.TestTypes.TestMappingDefinition;
import org.kie.kogito.persistence.reporting.test.TestTypes.TestPartitionField;
import org.kie.kogito.persistence.reporting.test.TestTypes.TestTableSqlBuilder;
import org.kie.kogito.persistence.reporting.test.TestTypes.TestTriggerDeleteSqlBuilder;
import org.kie.kogito.persistence.reporting.test.TestTypes.TestTriggerInsertSqlBuilder;
import org.kie.kogito.persistence.reporting.test.TestTypesImpl.TestContextImpl;
import org.kie.kogito.persistence.reporting.test.TestTypesImpl.TestFieldImpl;
import org.kie.kogito.persistence.reporting.test.TestTypesImpl.TestJsonFieldImpl;
import org.kie.kogito.persistence.reporting.test.TestTypesImpl.TestMappingDefinitionImpl;
import org.kie.kogito.persistence.reporting.test.TestTypesImpl.TestMappingImpl;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BaseDatabaseManagerImplTest {

    private static final TestMappingDefinition DEFINITION = new TestMappingDefinitionImpl("mappingId",
            "sourceTableName",
            "sourceTableJsonFieldName",
            List.of(new TestFieldImpl("id")),
            Collections.emptyList(),
            "targetTableName",
            List.of(new TestMappingImpl("sourceJsonPath",
                    new TestJsonFieldImpl("field1",
                            String.class))));

    @Mock
    private EntityManager entityManager;

    @Mock
    private Query query;

    @Mock
    private TestIndexesSqlBuilder indexesSqlBuilder;

    @Mock
    private TestTableSqlBuilder tableSqlBuilder;

    @Mock
    private TestTriggerDeleteSqlBuilder triggerDeleteSqlBuilder;

    @Mock
    private TestTriggerInsertSqlBuilder triggerInsertSqlBuilder;

    @Mock
    private TestApplyMappingSqlBuilder applyMappingSqlBuilder;

    @Captor
    private ArgumentCaptor<TestContextImpl> contextArgumentCaptor;

    private class TestBasePostgresDatabaseManagerImpl extends BaseDatabaseManagerImpl<Object, TestField, TestPartitionField, TestJsonField, TestMapping, TestMappingDefinition, TestContext> {

        TestBasePostgresDatabaseManagerImpl(final TestIndexesSqlBuilder indexesSqlBuilder,
                final TestTableSqlBuilder tableSqlBuilder,
                final TestTriggerDeleteSqlBuilder triggerDeleteSqlBuilder,
                final TestTriggerInsertSqlBuilder triggerInsertSqlBuilder,
                final TestApplyMappingSqlBuilder applyMappingSqlBuilder) {
            super(indexesSqlBuilder,
                    tableSqlBuilder,
                    triggerDeleteSqlBuilder,
                    triggerInsertSqlBuilder,
                    applyMappingSqlBuilder);
        }

        @Override
        protected EntityManager getEntityManager(final String sourceTableName) {
            return entityManager;
        }

        @Override
        protected TerminalPathSegment<Object, TestJsonField, TestMapping> buildTerminalPathSegment(final String segment,
                final PathSegment parent,
                final TestMapping mapping) {
            return new TerminalPathSegment<>(segment,
                    parent,
                    mapping);
        }

        @Override
        public TestContext createContext(final TestMappingDefinition definition) {
            return new TestContextImpl(definition.getMappingId(),
                    definition.getSourceTableName(),
                    definition.getSourceTableJsonFieldName(),
                    definition.getSourceTableIdentityFields(),
                    definition.getSourceTablePartitionFields(),
                    definition.getTargetTableName(),
                    definition.getFieldMappings(),
                    parsePathSegments(definition.getFieldMappings()),
                    getSourceTableFieldTypes(definition.getSourceTableName()));
        }

        @Override
        protected Map<String, String> getSourceTableFieldTypes(String sourceTableName) {
            return Collections.emptyMap();
        }
    }

    private TestBasePostgresDatabaseManagerImpl manager;

    @BeforeEach
    public void setup() {
        this.manager = new TestBasePostgresDatabaseManagerImpl(indexesSqlBuilder,
                tableSqlBuilder,
                triggerDeleteSqlBuilder,
                triggerInsertSqlBuilder,
                applyMappingSqlBuilder) {
        };
    }

    @Test
    void testCreateArtifacts_Indexes() {
        when(entityManager.createNativeQuery(any())).thenReturn(query);

        manager.createArtifacts(DEFINITION);

        verify(indexesSqlBuilder).createTableIndexesSql(contextArgumentCaptor.capture());
        assertPostgresContext(contextArgumentCaptor.getValue());
    }

    @Test
    void testCreateArtifacts_Tables() {
        when(entityManager.createNativeQuery(any())).thenReturn(query);

        manager.createArtifacts(DEFINITION);

        verify(tableSqlBuilder).createTableSql(contextArgumentCaptor.capture());
        assertPostgresContext(contextArgumentCaptor.getValue());
    }

    @Test
    void testCreateArtifacts_InsertTrigger() {
        when(entityManager.createNativeQuery(any())).thenReturn(query);

        manager.createArtifacts(DEFINITION);

        verify(triggerInsertSqlBuilder).createInsertTriggerSql(contextArgumentCaptor.capture());
        assertPostgresContext(contextArgumentCaptor.getValue());
    }

    @Test
    void testCreateArtifacts_InsertTriggerFunction() {
        when(entityManager.createNativeQuery(any())).thenReturn(query);

        manager.createArtifacts(DEFINITION);

        verify(triggerInsertSqlBuilder).createInsertTriggerFunctionSql(contextArgumentCaptor.capture());
        assertPostgresContext(contextArgumentCaptor.getValue());
    }

    @Test
    void testCreateArtifacts_DeleteTrigger() {
        when(entityManager.createNativeQuery(any())).thenReturn(query);

        manager.createArtifacts(DEFINITION);

        verify(triggerDeleteSqlBuilder).createDeleteTriggerSql(contextArgumentCaptor.capture());
        assertPostgresContext(contextArgumentCaptor.getValue());
    }

    @Test
    void testCreateArtifacts_DeleteTriggerFunction() {
        when(entityManager.createNativeQuery(any())).thenReturn(query);

        manager.createArtifacts(DEFINITION);

        verify(triggerDeleteSqlBuilder).createDeleteTriggerFunctionSql(contextArgumentCaptor.capture());
        assertPostgresContext(contextArgumentCaptor.getValue());
    }

    @Test
    void testCreateArtifacts_ApplyMapping() {
        when(entityManager.createNativeQuery(any())).thenReturn(query);

        manager.createArtifacts(DEFINITION);

        verify(applyMappingSqlBuilder).apply(contextArgumentCaptor.capture());
        assertPostgresContext(contextArgumentCaptor.getValue());
    }

    @Test
    void testDestroyArtifacts_Indexes() {
        when(entityManager.createNativeQuery(any())).thenReturn(query);

        manager.destroyArtifacts(DEFINITION);

        verify(indexesSqlBuilder).dropTableIndexesSql(contextArgumentCaptor.capture());
        assertPostgresContext(contextArgumentCaptor.getValue());
    }

    @Test
    void testDestroyArtifacts_Tables() {
        when(entityManager.createNativeQuery(any())).thenReturn(query);

        manager.destroyArtifacts(DEFINITION);

        verify(tableSqlBuilder).dropTableSql(contextArgumentCaptor.capture());
        assertPostgresContext(contextArgumentCaptor.getValue());
    }

    @Test
    void testDestroyArtifacts_InsertTrigger() {
        when(entityManager.createNativeQuery(any())).thenReturn(query);

        manager.destroyArtifacts(DEFINITION);

        verify(triggerInsertSqlBuilder).dropInsertTriggerSql(contextArgumentCaptor.capture());
        assertPostgresContext(contextArgumentCaptor.getValue());
    }

    @Test
    void testDestroyArtifacts_InsertTriggerFunction() {
        when(entityManager.createNativeQuery(any())).thenReturn(query);

        manager.destroyArtifacts(DEFINITION);

        verify(triggerInsertSqlBuilder).dropInsertTriggerFunctionSql(contextArgumentCaptor.capture());
        assertPostgresContext(contextArgumentCaptor.getValue());
    }

    @Test
    void testDestroyArtifacts_DeleteTrigger() {
        when(entityManager.createNativeQuery(any())).thenReturn(query);

        manager.destroyArtifacts(DEFINITION);

        verify(triggerDeleteSqlBuilder).dropDeleteTriggerSql(contextArgumentCaptor.capture());
        assertPostgresContext(contextArgumentCaptor.getValue());
    }

    @Test
    void testDestroyArtifacts_DeleteTriggerFunction() {
        when(entityManager.createNativeQuery(any())).thenReturn(query);

        manager.destroyArtifacts(DEFINITION);

        verify(triggerDeleteSqlBuilder).dropDeleteTriggerFunctionSql(contextArgumentCaptor.capture());
        assertPostgresContext(contextArgumentCaptor.getValue());
    }

    private void assertPostgresContext(final TestContext context) {
        assertEquals(context.getMappingId(), DEFINITION.getMappingId());
        assertEquals(context.getSourceTableName(), DEFINITION.getSourceTableName());
        assertEquals(context.getSourceTableIdentityFields(), DEFINITION.getSourceTableIdentityFields());
        assertEquals(context.getSourceTablePartitionFields(), DEFINITION.getSourceTablePartitionFields());
        assertEquals(context.getSourceTableJsonFieldName(), DEFINITION.getSourceTableJsonFieldName());
        assertEquals(context.getTargetTableName(), DEFINITION.getTargetTableName());
        assertEquals(context.getFieldMappings(), DEFINITION.getFieldMappings());
    }

    @Test
    void testParsePathSegments_OneMapping_OneSegment() {
        // $ ---> field1[1a]
        final TestMapping mapping = new TestMappingImpl("field1",
                new TestJsonFieldImpl("targetFieldName",
                        String.class));

        final List<PathSegment> segments = manager.parsePathSegments(List.of(mapping));
        assertNotNull(segments);
        assertEquals(1, segments.size());

        final PathSegment segment1a = segments.get(0);
        assertNull(segment1a.getParent());
        assertTrue(segment1a.getChildren().isEmpty());
        assertEquals("field1", segment1a.getSegment());
        assertTrue(segment1a instanceof TerminalPathSegment<?, ?, ?>);

        final TerminalPathSegment<?, ?, ?> terminal1a = (TerminalPathSegment<?, ?, ?>) segment1a;
        assertEquals(mapping, terminal1a.getMapping());
    }

    @Test
    void testParsePathSegments_OneMapping_TwoSegments() {
        // $ ---> field1[1a] ---> field2[1b]
        final TestMapping mapping = new TestMappingImpl("field1.field2",
                new TestJsonFieldImpl("targetFieldName",
                        String.class));

        final List<PathSegment> segments = manager.parsePathSegments(List.of(mapping));
        assertNotNull(segments);
        assertEquals(1, segments.size());

        final PathSegment segment1a = segments.get(0);
        assertNull(segment1a.getParent());
        assertEquals(1, segment1a.getChildren().size());
        assertEquals("field1", segment1a.getSegment());

        final PathSegment segment1b = segment1a.getChildren().get(0);
        assertEquals(segment1a, segment1b.getParent());
        assertTrue(segment1b.getChildren().isEmpty());
        assertEquals("field2", segment1b.getSegment());
        assertTrue(segment1b instanceof TerminalPathSegment<?, ?, ?>);

        final TerminalPathSegment<?, ?, ?> terminal1b = (TerminalPathSegment<?, ?, ?>) segment1b;
        assertEquals(mapping, terminal1b.getMapping());
    }

    @Test
    void testParsePathSegments_TwoMappings_MultipleSegments_NoneShared() {
        // $ ---> field1[1a] ---> field2[1b]
        //   \
        //    +-> field3[2a]
        final TestMapping mapping1 = new TestMappingImpl("field1.field2",
                new TestJsonFieldImpl("targetFieldName1",
                        String.class));
        final TestMapping mapping2 = new TestMappingImpl("field3",
                new TestJsonFieldImpl("targetFieldName2",
                        String.class));

        final List<PathSegment> segments = manager.parsePathSegments(List.of(mapping1, mapping2));
        assertNotNull(segments);
        assertEquals(2, segments.size());

        final PathSegment segment1a = segments.get(0);
        assertNull(segment1a.getParent());
        assertEquals(1, segment1a.getChildren().size());
        assertEquals("field1", segment1a.getSegment());

        final PathSegment segment1b = segment1a.getChildren().get(0);
        assertEquals(segment1a, segment1b.getParent());
        assertTrue(segment1b.getChildren().isEmpty());
        assertEquals("field2", segment1b.getSegment());
        assertTrue(segment1b instanceof TerminalPathSegment<?, ?, ?>);

        final TerminalPathSegment<?, ?, ?> terminal1b = (TerminalPathSegment<?, ?, ?>) segment1b;
        assertEquals(mapping1, terminal1b.getMapping());

        final PathSegment segment2a = segments.get(1);
        assertNull(segment2a.getParent());
        assertTrue(segment2a.getChildren().isEmpty());
        assertEquals("field3", segment2a.getSegment());
        assertTrue(segment2a instanceof TerminalPathSegment<?, ?, ?>);

        final TerminalPathSegment<?, ?, ?> terminal2a = (TerminalPathSegment<?, ?, ?>) segment2a;
        assertEquals(mapping2, terminal2a.getMapping());
    }

    @Test
    void testParsePathSegments_TwoMappings_MultipleSegments_Shared() {
        // $ ---> field1[1a] ---> field2[1b]
        //                   \
        //                    +-> field3[2b]
        final TestMapping mapping1 = new TestMappingImpl("field1.field2",
                new TestJsonFieldImpl("targetFieldName1",
                        String.class));
        final TestMapping mapping2 = new TestMappingImpl("field1.field3",
                new TestJsonFieldImpl("targetFieldName2",
                        String.class));

        final List<PathSegment> segments = manager.parsePathSegments(List.of(mapping1, mapping2));
        assertNotNull(segments);
        assertEquals(1, segments.size());

        final PathSegment segment1a = segments.get(0);
        assertNull(segment1a.getParent());
        assertEquals(2, segment1a.getChildren().size());
        assertEquals("field1", segment1a.getSegment());

        final PathSegment segment1b = segment1a.getChildren().get(0);
        assertEquals(segment1a, segment1b.getParent());
        assertTrue(segment1b.getChildren().isEmpty());
        assertEquals("field2", segment1b.getSegment());
        assertTrue(segment1b instanceof TerminalPathSegment<?, ?, ?>);

        final TerminalPathSegment<?, ?, ?> terminal1b = (TerminalPathSegment<?, ?, ?>) segment1b;
        assertEquals(mapping1, terminal1b.getMapping());

        final PathSegment segment2b = segment1a.getChildren().get(1);
        assertEquals(segment1a, segment2b.getParent());
        assertTrue(segment2b.getChildren().isEmpty());
        assertEquals("field3", segment2b.getSegment());
        assertTrue(segment2b instanceof TerminalPathSegment<?, ?, ?>);

        final TerminalPathSegment<?, ?, ?> terminal2b = (TerminalPathSegment<?, ?, ?>) segment2b;
        assertEquals(mapping2, terminal2b.getMapping());
    }

    @Test
    void testParsePathSegments_TwoMappings_MultipleSegments_Shared_WithIntermediate() {
        // $ ---> field1[1a] ---> field2[1b]
        //                   \
        //                    +-> field3[2b] ---> field4[2c]
        final TestMapping mapping1 = new TestMappingImpl("field1.field2",
                new TestJsonFieldImpl("targetFieldName1",
                        String.class));
        final TestMapping mapping2 = new TestMappingImpl("field1.field3.field4",
                new TestJsonFieldImpl("targetFieldName2",
                        String.class));

        final List<PathSegment> segments = manager.parsePathSegments(List.of(mapping1, mapping2));
        assertNotNull(segments);
        assertEquals(1, segments.size());

        final PathSegment segment1a = segments.get(0);
        assertNull(segment1a.getParent());
        assertEquals(2, segment1a.getChildren().size());
        assertEquals("field1", segment1a.getSegment());

        final PathSegment segment1b = segment1a.getChildren().get(0);
        assertEquals(segment1a, segment1b.getParent());
        assertTrue(segment1b.getChildren().isEmpty());
        assertEquals("field2", segment1b.getSegment());
        assertTrue(segment1b instanceof TerminalPathSegment<?, ?, ?>);

        final TerminalPathSegment<?, ?, ?> terminal1b = (TerminalPathSegment<?, ?, ?>) segment1b;
        assertEquals(mapping1, terminal1b.getMapping());

        final PathSegment segment2b = segment1a.getChildren().get(1);
        assertEquals(segment1a, segment2b.getParent());
        assertEquals(1, segment2b.getChildren().size());
        assertEquals("field3", segment2b.getSegment());

        final PathSegment segment2c = segment2b.getChildren().get(0);
        assertEquals(segment2b, segment2c.getParent());
        assertTrue(segment2c.getChildren().isEmpty());
        assertEquals("field4", segment2c.getSegment());
        assertTrue(segment2c instanceof TerminalPathSegment<?, ?, ?>);

        final TerminalPathSegment<?, ?, ?> terminal2c = (TerminalPathSegment<?, ?, ?>) segment2c;
        assertEquals(mapping2, terminal2c.getMapping());
    }

    @Test
    void testParsePathSegments_OneMapping_OneSegment_WithJoin() {
        // $ ---> field1[1a] ---> field2[1b]
        final TestMapping mapping = new TestMappingImpl("field1[].field2",
                new TestJsonFieldImpl("targetFieldName",
                        String.class));

        final List<PathSegment> segments = manager.parsePathSegments(List.of(mapping));
        assertNotNull(segments);
        assertEquals(1, segments.size());

        final PathSegment segment1a = segments.get(0);
        assertNull(segment1a.getParent());
        assertEquals(1, segment1a.getChildren().size());
        assertEquals("field1[]", segment1a.getSegment());
        assertTrue(segment1a instanceof JoinPathSegment);

        final JoinPathSegment join1a = (JoinPathSegment) segment1a;
        assertEquals("g0", join1a.getGroupName());

        final PathSegment segment1b = segment1a.getChildren().get(0);
        assertEquals(segment1a, segment1b.getParent());
        assertTrue(segment1b.getChildren().isEmpty());
        assertEquals("field2", segment1b.getSegment());
        assertTrue(segment1b instanceof TerminalPathSegment<?, ?, ?>);

        final TerminalPathSegment<?, ?, ?> terminal1b = (TerminalPathSegment<?, ?, ?>) segment1b;
        assertEquals(mapping, terminal1b.getMapping());
    }
}
