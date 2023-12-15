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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.persistence.api.Storage;
import org.kie.kogito.persistence.postgresql.PostgresStorageService;
import org.kie.kogito.persistence.postgresql.model.CacheEntityRepository;
import org.kie.kogito.testcontainers.quarkus.PostgreSqlQuarkusTestResource;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;

import jakarta.inject.Inject;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityResult;
import jakarta.persistence.FieldResult;
import jakarta.persistence.Id;
import jakarta.persistence.SqlResultSetMapping;
import jakarta.transaction.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
@QuarkusTestResource(PostgreSqlQuarkusTestResource.class)
class HierarchicalTypeMappingIT {

    private static final String CACHE_NAME = "HierarchicalType";

    private static final String SQL = "SELECT " +
            "ROW_NUMBER() OVER (ORDER BY name, key) as id, " +
            "name, " +
            "key, " +
            "root, " +
            "nestedBasicMappedField, " +
            "nestedBasicCollectionMappedField " +
            "FROM " +
            "HierarchicalTypeExtract";

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
    void testHierarchicalTypeMapping() {
        final HierarchicalType ht1 = new HierarchicalType("root1",
                new BasicType(1, 2L, "A"),
                List.of(new BasicType(3, 4L, "Aa"), new BasicType(5, 6L, "Ab")));
        final HierarchicalType ht2 = new HierarchicalType("root2",
                new BasicType(7, 8L, "B"),
                List.of(new BasicType(9, 10L, "Ba")));

        final Storage<String, HierarchicalType> cache = storageService.getCache(CACHE_NAME, HierarchicalType.class);
        cache.put("key1", ht1);
        cache.put("key2", ht2);

        @SuppressWarnings("unchecked")
        final List<HierarchicalTypeExtractRow> results = repository
                .getEntityManager()
                .createNativeQuery(SQL, "HierarchicalTypeMapping")
                .getResultList();

        assertThat(results).hasSize(3);
        final HierarchicalTypeExtractRow row0 = results.get(0);
        assertEquals("key1", row0.key);
        assertEquals(CACHE_NAME, row0.name);
        assertEquals("root1", row0.root);
        assertEquals("A", row0.nestedBasicMappedField);
        assertEquals("Aa", row0.nestedBasicCollectionMappedField);

        final HierarchicalTypeExtractRow row1 = results.get(1);
        assertEquals("key1", row1.key);
        assertEquals(CACHE_NAME, row1.name);
        assertEquals("root1", row1.root);
        assertEquals("A", row1.nestedBasicMappedField);
        assertEquals("Ab", row1.nestedBasicCollectionMappedField);

        final HierarchicalTypeExtractRow row2 = results.get(2);
        assertEquals("key2", row2.key);
        assertEquals(CACHE_NAME, row2.name);
        assertEquals("root2", row2.root);
        assertEquals("B", row2.nestedBasicMappedField);
        assertEquals("Ba", row2.nestedBasicCollectionMappedField);
    }

    @Test
    @Transactional
    void testHierarchicalTypeMappingDelete() {
        final HierarchicalType ht1 = new HierarchicalType("root1",
                new BasicType(1, 2L, "A"),
                List.of(new BasicType(3, 4L, "Aa"), new BasicType(5, 6L, "Ab")));
        final HierarchicalType ht2 = new HierarchicalType("root2",
                new BasicType(7, 8L, "B"),
                List.of(new BasicType(9, 10L, "Ba")));

        final Storage<String, HierarchicalType> cache = storageService.getCache(CACHE_NAME, HierarchicalType.class);
        cache.put("key1", ht1);
        cache.put("key2", ht2);

        assertResultSize(3);

        cache.remove("key1");

        assertResultSize(1);

        cache.remove("key2");

        assertResultSize(0);
    }

    @Test
    @Transactional
    void testHierarchicalTypeMappingUpdate() {
        final Storage<String, HierarchicalType> cache = storageService.getCache(CACHE_NAME, HierarchicalType.class);
        cache.put("key1", new HierarchicalType("root1",
                new BasicType(1, 2L, "A"),
                List.of(new BasicType(3, 4L, "Aa"), new BasicType(5, 6L, "Ab"))));
        assertResultSize(2);

        //Clear JPA cache to ensure we fetch queries from the database
        repository.getEntityManager().clear();

        cache.put("key1", new HierarchicalType("root2",
                new BasicType(1, 2L, "A2"),
                List.of(new BasicType(3, 4L, "Aa2"), new BasicType(5, 6L, "Ab2"))));
        assertResultSize(2);

        @SuppressWarnings("unchecked")
        final List<HierarchicalTypeExtractRow> results = repository
                .getEntityManager()
                .createNativeQuery(SQL, "HierarchicalTypeMapping")
                .getResultList();

        assertThat(results).hasSize(2);
        final HierarchicalTypeExtractRow row0 = results.get(0);
        assertEquals("key1", row0.key);
        assertEquals(CACHE_NAME, row0.name);
        assertEquals("A2", row0.nestedBasicMappedField);
        assertEquals("Aa2", row0.nestedBasicCollectionMappedField);

        final HierarchicalTypeExtractRow row1 = results.get(1);
        assertEquals("key1", row1.key);
        assertEquals(CACHE_NAME, row1.name);
        assertEquals("A2", row1.nestedBasicMappedField);
        assertEquals("Ab2", row1.nestedBasicCollectionMappedField);
    }

    @SuppressWarnings("unchecked")
    private void assertResultSize(final int expected) {
        final List<HierarchicalTypeExtractRow> results = repository
                .getEntityManager()
                .createNativeQuery(SQL, "HierarchicalTypeMapping")
                .getResultList();
        assertThat(results).hasSize(expected);
    }

    /**
     * Class to demonstrate "typed" access to the mapped result set
     */
    @Entity
    @SqlResultSetMapping(
            name = "HierarchicalTypeMapping",
            entities = {
                    @EntityResult(
                            entityClass = HierarchicalTypeExtractRow.class,
                            fields = { @FieldResult(name = "id", column = "id"),
                                    @FieldResult(name = "key", column = "key"),
                                    @FieldResult(name = "name", column = "name"),
                                    @FieldResult(name = "root", column = "root"),
                                    @FieldResult(name = "nestedBasicMappedField", column = "nestedBasicMappedField"),
                                    @FieldResult(name = "nestedBasicCollectionMappedField", column = "nestedBasicCollectionMappedField") })
            })
    public static class HierarchicalTypeExtractRow {

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
        private String nestedBasicCollectionMappedField;
    }
}
