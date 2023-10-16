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
package org.kie.kogito.persistence.postgresql.reporting;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityResult;
import javax.persistence.FieldResult;
import javax.persistence.Id;
import javax.persistence.SqlResultSetMapping;
import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.persistence.api.Storage;
import org.kie.kogito.persistence.postgresql.PostgresStorageService;
import org.kie.kogito.persistence.postgresql.model.CacheEntityRepository;
import org.kie.kogito.testcontainers.quarkus.PostgreSqlQuarkusTestResource;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@QuarkusTest
@QuarkusTestResource(PostgreSqlQuarkusTestResource.class)
class ComplexHierarchicalTypeMappingIT {

    private static final String CACHE_NAME = "ComplexHierarchicalType";

    private static final String SQL = "SELECT " +
            "ROW_NUMBER() OVER (ORDER BY name, key) as id, " +
            "name, " +
            "key, " +
            "root, " +
            "nestedBasicMappedField, " +
            "nestedComplexCollectionMappedField1, " +
            "nestedComplexCollectionMappedSubField1 " +
            "FROM " +
            "ComplexHierarchicalTypeExtract";

    @Inject
    PostgresStorageService storageService;

    @Inject
    CacheEntityRepository repository;

    @BeforeEach
    @Transactional
    public void setup() {
        storageService.getCache(CACHE_NAME).clear();
    }

    @Test
    @Transactional
    void testComplexHierarchicalTypeMapping() {
        final ComplexHierarchicalType cht1 = new ComplexHierarchicalType("root1",
                new BasicType(1, 2L, "A"),
                List.of(new ComplexHierarchicalType("Aa",
                        new BasicType(null, null, "field3a"),
                        List.of(new ComplexHierarchicalType("Ba",
                                new BasicType(null, null, "field3a-1"),
                                null),
                                new ComplexHierarchicalType("Bb",
                                        new BasicType(null, null, "field3a-2"),
                                        null))),
                        new ComplexHierarchicalType("Ab",
                                new BasicType(null, null, "field3b"),
                                List.of(new ComplexHierarchicalType("Ca", null, null)))));

        final Storage<String, ComplexHierarchicalType> cache = storageService.getCache(CACHE_NAME, ComplexHierarchicalType.class);
        cache.put("key1", cht1);

        @SuppressWarnings("unchecked")
        final List<ComplexHierarchicalTypeExtractRow> results = repository
                .getEntityManager()
                .createNativeQuery(SQL, "ComplexHierarchicalTypeMapping")
                .getResultList();

        assertThat(results).hasSize(3);
        final ComplexHierarchicalTypeExtractRow row0 = results.get(0);
        assertEquals("key1", row0.key);
        assertEquals(CACHE_NAME, row0.name);
        assertEquals("root1", row0.root);
        assertEquals("A", row0.nestedBasicMappedField);
        assertEquals("Aa", row0.nestedComplexCollectionMappedField1);
        assertEquals("Ba", row0.nestedComplexCollectionMappedSubField1);

        final ComplexHierarchicalTypeExtractRow row1 = results.get(1);
        assertEquals("key1", row1.key);
        assertEquals(CACHE_NAME, row1.name);
        assertEquals("root1", row1.root);
        assertEquals("A", row1.nestedBasicMappedField);
        assertEquals("Aa", row1.nestedComplexCollectionMappedField1);
        assertEquals("Bb", row1.nestedComplexCollectionMappedSubField1);

        final ComplexHierarchicalTypeExtractRow row2 = results.get(2);
        assertEquals("key1", row2.key);
        assertEquals(CACHE_NAME, row2.name);
        assertEquals("root1", row2.root);
        assertEquals("A", row2.nestedBasicMappedField);
        assertEquals("Ab", row2.nestedComplexCollectionMappedField1);
        assertEquals("Ca", row2.nestedComplexCollectionMappedSubField1);
    }

    @Test
    @Transactional
    void testComplexHierarchicalTypeMappingDelete() {
        final ComplexHierarchicalType cht1 = new ComplexHierarchicalType("root1",
                new BasicType(1, 2L, "A"),
                List.of(new ComplexHierarchicalType("Aa",
                        new BasicType(null, null, "field3a"),
                        List.of(new ComplexHierarchicalType("Aaa", null, null)))));
        final ComplexHierarchicalType cht2 = new ComplexHierarchicalType("root2",
                new BasicType(3, 4L, "B"),
                List.of(new ComplexHierarchicalType("Ba",
                        new BasicType(null, null, "field3b"),
                        List.of(new ComplexHierarchicalType("Baa", null, null)))));

        final Storage<String, ComplexHierarchicalType> cache = storageService.getCache(CACHE_NAME, ComplexHierarchicalType.class);
        cache.put("key1", cht1);
        cache.put("key2", cht2);

        assertResultSize(2);

        cache.remove("key1");

        assertResultSize(1);

        cache.remove("key2");

        assertResultSize(0);
    }

    @Test
    @Transactional
    void testComplexHierarchicalTypeMappingUpdate() {
        final Storage<String, ComplexHierarchicalType> cache = storageService.getCache(CACHE_NAME, ComplexHierarchicalType.class);
        cache.put("key1", new ComplexHierarchicalType("root1",
                new BasicType(1, 2L, "A"),
                List.of(new ComplexHierarchicalType("root1a",
                        new BasicType(1, 2L, "field3a"),
                        null),
                        new ComplexHierarchicalType("root1b",
                                new BasicType(3, 4L, "field3b"),
                                null))));
        assertResultSize(2);

        //Clear JPA cache to ensure we fetch queries from the database
        repository.getEntityManager().clear();

        cache.put("key1", new ComplexHierarchicalType("root1-1",
                new BasicType(1, 2L, "A-1"),
                null));

        assertResultSize(1);

        @SuppressWarnings("unchecked")
        final List<ComplexHierarchicalTypeExtractRow> results = repository
                .getEntityManager()
                .createNativeQuery(SQL, "ComplexHierarchicalTypeMapping")
                .getResultList();

        assertThat(results).hasSize(1);
        final ComplexHierarchicalTypeExtractRow row0 = results.get(0);
        assertEquals("key1", row0.key);
        assertEquals(CACHE_NAME, row0.name);
        assertEquals("root1-1", row0.root);
        assertEquals("A-1", row0.nestedBasicMappedField);
        assertNull(row0.nestedComplexCollectionMappedSubField1);
    }

    @SuppressWarnings("unchecked")
    private void assertResultSize(final int expected) {
        final List<BasicTypeMappingIT.BasicTypeExtractRow> results = repository
                .getEntityManager()
                .createNativeQuery(SQL, "ComplexHierarchicalTypeMapping")
                .getResultList();
        assertThat(results).hasSize(expected);
    }

    /**
     * Class to demonstrate "typed" access to the mapped result set
     */
    @Entity
    @SqlResultSetMapping(
            name = "ComplexHierarchicalTypeMapping",
            entities = {
                    @EntityResult(
                            entityClass = ComplexHierarchicalTypeExtractRow.class,
                            fields = { @FieldResult(name = "id", column = "id"),
                                    @FieldResult(name = "key", column = "key"),
                                    @FieldResult(name = "name", column = "name"),
                                    @FieldResult(name = "root", column = "root"),
                                    @FieldResult(name = "nestedBasicMappedField", column = "nestedBasicMappedField"),
                                    @FieldResult(name = "nestedComplexCollectionMappedField1", column = "nestedComplexCollectionMappedField1"),
                                    @FieldResult(name = "nestedComplexCollectionMappedSubField1", column = "nestedComplexCollectionMappedSubField1")
                            })
            })
    public static class ComplexHierarchicalTypeExtractRow {

        @Id
        @Column(nullable = false)
        @SuppressWarnings("unused")
        private int id;

        @Column(nullable = false)
        private String key;

        @Column
        private String name;

        @Column(nullable = false)
        private String root;

        @Column
        private String nestedBasicMappedField;

        @Column
        private String nestedComplexCollectionMappedField1;

        @Column
        private String nestedComplexCollectionMappedSubField1;
    }
}
