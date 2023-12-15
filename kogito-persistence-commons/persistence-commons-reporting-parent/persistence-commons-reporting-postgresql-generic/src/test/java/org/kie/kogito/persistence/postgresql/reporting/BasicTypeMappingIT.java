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
class BasicTypeMappingIT {

    private static final String CACHE_NAME = "BasicType";

    private static final String SQL = "SELECT " +
            "ROW_NUMBER() OVER (ORDER BY name, key) as id, " +
            "name, " +
            "key, " +
            "field1MappedField, " +
            "field2MappedField " +
            "FROM " +
            "BasicTypeExtract";

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
    void testBasicTypeMapping() {
        final Storage<String, BasicType> cache = storageService.getCache(CACHE_NAME, BasicType.class);
        cache.put("key1", new BasicType(1, 2L, "A"));
        cache.put("key2", new BasicType(3, 4L, "B"));

        @SuppressWarnings("unchecked")
        final List<BasicTypeExtractRow> results = repository
                .getEntityManager()
                .createNativeQuery(SQL, "BasicTypeExtractMapping")
                .getResultList();

        assertThat(results).hasSize(2);
        final BasicTypeExtractRow row0 = results.get(0);
        assertEquals("key1", row0.key);
        assertEquals(CACHE_NAME, row0.name);
        assertEquals(1, row0.field1MappedField);
        assertEquals("A", row0.field2MappedField);

        final BasicTypeExtractRow row1 = results.get(1);
        assertEquals("key2", row1.key);
        assertEquals(CACHE_NAME, row1.name);
        assertEquals(3, row1.field1MappedField);
        assertEquals("B", row1.field2MappedField);
    }

    @Test
    @Transactional
    void testBasicTypeMappingDelete() {
        final Storage<String, BasicType> cache = storageService.getCache(CACHE_NAME, BasicType.class);
        cache.put("key1", new BasicType(1, 2L, "A"));
        cache.put("key2", new BasicType(3, 4L, "B"));

        assertResultSize(2);

        cache.remove("key1");

        assertResultSize(1);

        cache.remove("key2");

        assertResultSize(0);
    }

    @Test
    @Transactional
    void testBasicTypeMappingUpdate() {
        final Storage<String, BasicType> cache = storageService.getCache(CACHE_NAME, BasicType.class);
        cache.put("key1", new BasicType(1, 2L, "A"));
        cache.put("key2", new BasicType(2, 4L, "B"));
        cache.put("key3", new BasicType(3, 6L, "C"));
        assertResultSize(3);

        //Clear JPA cache to ensure we fetch queries from the database
        repository.getEntityManager().clear();

        cache.put("key2", new BasicType(22, 8L, "BBB"));
        assertResultSize(3);

        @SuppressWarnings("unchecked")
        final List<BasicTypeExtractRow> results = repository
                .getEntityManager()
                .createNativeQuery(SQL, "BasicTypeExtractMapping")
                .getResultList();

        assertThat(results).hasSize(3);
        final BasicTypeExtractRow row0 = results.get(0);
        assertEquals("key1", row0.key);
        assertEquals(CACHE_NAME, row0.name);
        assertEquals(1, row0.field1MappedField);
        assertEquals("A", row0.field2MappedField);

        final BasicTypeExtractRow row1 = results.get(1);
        assertEquals("key2", row1.key);
        assertEquals(CACHE_NAME, row1.name);
        assertEquals(22, row1.field1MappedField);
        assertEquals("BBB", row1.field2MappedField);

        final BasicTypeExtractRow row2 = results.get(2);
        assertEquals("key3", row2.key);
        assertEquals(CACHE_NAME, row2.name);
        assertEquals(3, row2.field1MappedField);
        assertEquals("C", row2.field2MappedField);
    }

    @SuppressWarnings("unchecked")
    private void assertResultSize(final int expected) {
        final List<BasicTypeExtractRow> results = repository
                .getEntityManager()
                .createNativeQuery(SQL, "BasicTypeExtractMapping")
                .getResultList();
        assertThat(results).hasSize(expected);
    }

    /**
     * Class to demonstrate "typed" access to the mapped result set
     */
    @Entity
    @SqlResultSetMapping(
            name = "BasicTypeExtractMapping",
            entities = {
                    @EntityResult(
                            entityClass = BasicTypeExtractRow.class,
                            fields = { @FieldResult(name = "id", column = "id"),
                                    @FieldResult(name = "key", column = "key"),
                                    @FieldResult(name = "name", column = "name"),
                                    @FieldResult(name = "field1MappedField", column = "field1MappedField"),
                                    @FieldResult(name = "field2MappedField", column = "field2MappedField") })
            })
    public static class BasicTypeExtractRow {

        @Id
        @Column(nullable = false)
        @SuppressWarnings("unused")
        private int id;

        @Column(nullable = false)
        private String key;

        @Column(nullable = false)
        private String name;

        @Column
        private Integer field1MappedField;

        @Column
        private String field2MappedField;
    }
}
