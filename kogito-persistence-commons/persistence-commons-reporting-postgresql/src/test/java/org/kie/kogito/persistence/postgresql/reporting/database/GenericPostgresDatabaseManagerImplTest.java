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
package org.kie.kogito.persistence.postgresql.reporting.database;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.persistence.postgresql.model.CacheEntityRepository;
import org.kie.kogito.persistence.postgresql.reporting.database.sqlbuilders.IndexesSqlBuilderImpl;
import org.kie.kogito.persistence.postgresql.reporting.database.sqlbuilders.PostgresContext;
import org.kie.kogito.persistence.postgresql.reporting.database.sqlbuilders.TableSqlBuilderImpl;
import org.kie.kogito.persistence.postgresql.reporting.database.sqlbuilders.TriggerDeleteSqlBuilderImpl;
import org.kie.kogito.persistence.postgresql.reporting.database.sqlbuilders.TriggerInsertSqlBuilderImpl;
import org.kie.kogito.persistence.postgresql.reporting.model.JsonType;
import org.kie.kogito.persistence.postgresql.reporting.model.PostgresField;
import org.kie.kogito.persistence.postgresql.reporting.model.PostgresMapping;
import org.kie.kogito.persistence.postgresql.reporting.model.PostgresMappingDefinition;
import org.kie.kogito.persistence.postgresql.reporting.model.paths.PostgresTerminalPathSegment;
import org.kie.kogito.persistence.reporting.model.paths.JoinPathSegment;
import org.kie.kogito.persistence.reporting.model.paths.PathSegment;
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
class GenericPostgresDatabaseManagerImplTest {

    private static final PostgresMappingDefinition DEFINITION = new PostgresMappingDefinition("mappingId",
            "sourceTableName",
            "sourceTableJsonFieldName",
            List.of(new PostgresField("id", JsonType.STRING)),
            "targetTableName",
            List.of(new PostgresMapping("sourceJsonPath",
                    new PostgresField("field1",
                            JsonType.STRING))));

    @Mock
    private CacheEntityRepository repository;

    @Mock
    private EntityManager entityManager;

    @Mock
    private Query query;

    @Mock
    private IndexesSqlBuilderImpl indexesSqlBuilder;

    @Mock
    private TableSqlBuilderImpl tableSqlBuilder;

    @Mock
    private TriggerDeleteSqlBuilderImpl triggerDeleteSqlBuilder;

    @Mock
    private TriggerInsertSqlBuilderImpl triggerInsertSqlBuilder;

    @Captor
    private ArgumentCaptor<PostgresContext> contextArgumentCaptor;

    private GenericPostgresDatabaseManagerImpl manager;

    @BeforeEach
    public void setup() {
        this.manager = new GenericPostgresDatabaseManagerImpl(repository,
                indexesSqlBuilder,
                tableSqlBuilder,
                triggerDeleteSqlBuilder,
                triggerInsertSqlBuilder);
    }

    @Test
    void testGetEntityManager_Processes() {
        when(repository.getEntityManager()).thenReturn(entityManager);

        assertEquals(entityManager,
                manager.getEntityManager("does-not-matter"));
    }

    @Test
    void testCreateArtifacts_Indexes() {
        when(repository.getEntityManager()).thenReturn(entityManager);
        when(entityManager.createNativeQuery(any())).thenReturn(query);

        manager.createArtifacts(DEFINITION);

        verify(indexesSqlBuilder).createTableIndexesSql(contextArgumentCaptor.capture());
        assertPostgresContext(contextArgumentCaptor.getValue());
    }

    @Test
    void testCreateArtifacts_Tables() {
        when(repository.getEntityManager()).thenReturn(entityManager);
        when(entityManager.createNativeQuery(any())).thenReturn(query);

        manager.createArtifacts(DEFINITION);

        verify(tableSqlBuilder).createTableSql(contextArgumentCaptor.capture());
        assertPostgresContext(contextArgumentCaptor.getValue());
    }

    @Test
    void testCreateArtifacts_InsertTrigger() {
        when(repository.getEntityManager()).thenReturn(entityManager);
        when(entityManager.createNativeQuery(any())).thenReturn(query);

        manager.createArtifacts(DEFINITION);

        verify(triggerInsertSqlBuilder).createInsertTriggerSql(contextArgumentCaptor.capture());
        assertPostgresContext(contextArgumentCaptor.getValue());
    }

    @Test
    void testCreateArtifacts_InsertTriggerFunction() {
        when(repository.getEntityManager()).thenReturn(entityManager);
        when(entityManager.createNativeQuery(any())).thenReturn(query);

        manager.createArtifacts(DEFINITION);

        verify(triggerInsertSqlBuilder).createInsertTriggerFunctionSql(contextArgumentCaptor.capture());
        assertPostgresContext(contextArgumentCaptor.getValue());
    }

    @Test
    void testCreateArtifacts_DeleteTrigger() {
        when(repository.getEntityManager()).thenReturn(entityManager);
        when(entityManager.createNativeQuery(any())).thenReturn(query);

        manager.createArtifacts(DEFINITION);

        verify(triggerDeleteSqlBuilder).createDeleteTriggerSql(contextArgumentCaptor.capture());
        assertPostgresContext(contextArgumentCaptor.getValue());
    }

    @Test
    void testCreateArtifacts_DeleteTriggerFunction() {
        when(repository.getEntityManager()).thenReturn(entityManager);
        when(entityManager.createNativeQuery(any())).thenReturn(query);

        manager.createArtifacts(DEFINITION);

        verify(triggerDeleteSqlBuilder).createDeleteTriggerFunctionSql(contextArgumentCaptor.capture());
        assertPostgresContext(contextArgumentCaptor.getValue());
    }

    @Test
    void testDestroyArtifacts_Indexes() {
        when(repository.getEntityManager()).thenReturn(entityManager);
        when(entityManager.createNativeQuery(any())).thenReturn(query);

        manager.destroyArtifacts(DEFINITION);

        verify(indexesSqlBuilder).dropTableIndexesSql(contextArgumentCaptor.capture());
        assertPostgresContext(contextArgumentCaptor.getValue());
    }

    @Test
    void testDestroyArtifacts_Tables() {
        when(repository.getEntityManager()).thenReturn(entityManager);
        when(entityManager.createNativeQuery(any())).thenReturn(query);

        manager.destroyArtifacts(DEFINITION);

        verify(tableSqlBuilder).dropTableSql(contextArgumentCaptor.capture());
        assertPostgresContext(contextArgumentCaptor.getValue());
    }

    @Test
    void testDestroyArtifacts_InsertTrigger() {
        when(repository.getEntityManager()).thenReturn(entityManager);
        when(entityManager.createNativeQuery(any())).thenReturn(query);

        manager.destroyArtifacts(DEFINITION);

        verify(triggerInsertSqlBuilder).dropInsertTriggerSql(contextArgumentCaptor.capture());
        assertPostgresContext(contextArgumentCaptor.getValue());
    }

    @Test
    void testDestroyArtifacts_InsertTriggerFunction() {
        when(repository.getEntityManager()).thenReturn(entityManager);
        when(entityManager.createNativeQuery(any())).thenReturn(query);

        manager.destroyArtifacts(DEFINITION);

        verify(triggerInsertSqlBuilder).dropInsertTriggerFunctionSql(contextArgumentCaptor.capture());
        assertPostgresContext(contextArgumentCaptor.getValue());
    }

    @Test
    void testDestroyArtifacts_DeleteTrigger() {
        when(repository.getEntityManager()).thenReturn(entityManager);
        when(entityManager.createNativeQuery(any())).thenReturn(query);

        manager.destroyArtifacts(DEFINITION);

        verify(triggerDeleteSqlBuilder).dropDeleteTriggerSql(contextArgumentCaptor.capture());
        assertPostgresContext(contextArgumentCaptor.getValue());
    }

    @Test
    void testDestroyArtifacts_DeleteTriggerFunction() {
        when(repository.getEntityManager()).thenReturn(entityManager);
        when(entityManager.createNativeQuery(any())).thenReturn(query);

        manager.destroyArtifacts(DEFINITION);

        verify(triggerDeleteSqlBuilder).dropDeleteTriggerFunctionSql(contextArgumentCaptor.capture());
        assertPostgresContext(contextArgumentCaptor.getValue());
    }

    private void assertPostgresContext(final PostgresContext context) {
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
        final PostgresMapping mapping = new PostgresMapping("field1",
                new PostgresField("targetFieldName",
                        JsonType.STRING));

        final List<PathSegment> segments = manager.parsePathSegments(List.of(mapping));
        assertNotNull(segments);
        assertEquals(1, segments.size());

        final PathSegment segment1a = segments.get(0);
        assertNull(segment1a.getParent());
        assertTrue(segment1a.getChildren().isEmpty());
        assertEquals("field1", segment1a.getSegment());
        assertTrue(segment1a instanceof PostgresTerminalPathSegment);

        final PostgresTerminalPathSegment terminal1a = (PostgresTerminalPathSegment) segment1a;
        assertEquals(mapping, terminal1a.getMapping());
    }

    @Test
    void testParsePathSegments_OneMapping_TwoSegments() {
        // $ ---> field1[1a] ---> field2[1b]
        final PostgresMapping mapping = new PostgresMapping("field1.field2",
                new PostgresField("targetFieldName",
                        JsonType.STRING));

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
        assertTrue(segment1b instanceof PostgresTerminalPathSegment);

        final PostgresTerminalPathSegment terminal1b = (PostgresTerminalPathSegment) segment1b;
        assertEquals(mapping, terminal1b.getMapping());
    }

    @Test
    void testParsePathSegments_TwoMappings_MultipleSegments_NoneShared() {
        // $ ---> field1[1a] ---> field2[1b]
        //   \
        //    +-> field3[2a]
        final PostgresMapping mapping1 = new PostgresMapping("field1.field2",
                new PostgresField("targetFieldName1",
                        JsonType.STRING));
        final PostgresMapping mapping2 = new PostgresMapping("field3",
                new PostgresField("targetFieldName2",
                        JsonType.STRING));

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
        assertTrue(segment1b instanceof PostgresTerminalPathSegment);

        final PostgresTerminalPathSegment terminal1b = (PostgresTerminalPathSegment) segment1b;
        assertEquals(mapping1, terminal1b.getMapping());

        final PathSegment segment2a = segments.get(1);
        assertNull(segment2a.getParent());
        assertTrue(segment2a.getChildren().isEmpty());
        assertEquals("field3", segment2a.getSegment());
        assertTrue(segment2a instanceof PostgresTerminalPathSegment);

        final PostgresTerminalPathSegment terminal2a = (PostgresTerminalPathSegment) segment2a;
        assertEquals(mapping2, terminal2a.getMapping());
    }

    @Test
    void testParsePathSegments_TwoMappings_MultipleSegments_Shared() {
        // $ ---> field1[1a] ---> field2[1b]
        //                   \
        //                    +-> field3[2b]
        final PostgresMapping mapping1 = new PostgresMapping("field1.field2",
                new PostgresField("targetFieldName1",
                        JsonType.STRING));
        final PostgresMapping mapping2 = new PostgresMapping("field1.field3",
                new PostgresField("targetFieldName2",
                        JsonType.STRING));

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
        assertTrue(segment1b instanceof PostgresTerminalPathSegment);

        final PostgresTerminalPathSegment terminal1b = (PostgresTerminalPathSegment) segment1b;
        assertEquals(mapping1, terminal1b.getMapping());

        final PathSegment segment2b = segment1a.getChildren().get(1);
        assertEquals(segment1a, segment2b.getParent());
        assertTrue(segment2b.getChildren().isEmpty());
        assertEquals("field3", segment2b.getSegment());
        assertTrue(segment2b instanceof PostgresTerminalPathSegment);

        final PostgresTerminalPathSegment terminal2b = (PostgresTerminalPathSegment) segment2b;
        assertEquals(mapping2, terminal2b.getMapping());
    }

    @Test
    void testParsePathSegments_TwoMappings_MultipleSegments_Shared_WithIntermediate() {
        // $ ---> field1[1a] ---> field2[1b]
        //                   \
        //                    +-> field3[2b] ---> field4[2c]
        final PostgresMapping mapping1 = new PostgresMapping("field1.field2",
                new PostgresField("targetFieldName1",
                        JsonType.STRING));
        final PostgresMapping mapping2 = new PostgresMapping("field1.field3.field4",
                new PostgresField("targetFieldName2",
                        JsonType.STRING));

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
        assertTrue(segment1b instanceof PostgresTerminalPathSegment);

        final PostgresTerminalPathSegment terminal1b = (PostgresTerminalPathSegment) segment1b;
        assertEquals(mapping1, terminal1b.getMapping());

        final PathSegment segment2b = segment1a.getChildren().get(1);
        assertEquals(segment1a, segment2b.getParent());
        assertEquals(1, segment2b.getChildren().size());
        assertEquals("field3", segment2b.getSegment());

        final PathSegment segment2c = segment2b.getChildren().get(0);
        assertEquals(segment2b, segment2c.getParent());
        assertTrue(segment2c.getChildren().isEmpty());
        assertEquals("field4", segment2c.getSegment());
        assertTrue(segment2c instanceof PostgresTerminalPathSegment);

        final PostgresTerminalPathSegment terminal2c = (PostgresTerminalPathSegment) segment2c;
        assertEquals(mapping2, terminal2c.getMapping());
    }

    @Test
    void testParsePathSegments_OneMapping_OneSegment_WithJoin() {
        // $ ---> field1[1a] ---> field2[1b]
        final PostgresMapping mapping = new PostgresMapping("field1[].field2",
                new PostgresField("targetFieldName",
                        JsonType.STRING));

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
        assertTrue(segment1b instanceof PostgresTerminalPathSegment);

        final PostgresTerminalPathSegment terminal1b = (PostgresTerminalPathSegment) segment1b;
        assertEquals(mapping, terminal1b.getMapping());
    }
}
