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
package org.kie.kogito.persistence.postgresql.reporting.database.sqlbuilders;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.persistence.api.Storage;
import org.kie.kogito.persistence.postgresql.PostgresStorageService;
import org.kie.kogito.persistence.postgresql.model.CacheEntityRepository;
import org.kie.kogito.persistence.postgresql.reporting.database.GenericPostgresDatabaseManagerImpl;
import org.kie.kogito.persistence.postgresql.reporting.model.JsonType;
import org.kie.kogito.persistence.postgresql.reporting.model.PostgresField;
import org.kie.kogito.persistence.postgresql.reporting.model.PostgresJsonField;
import org.kie.kogito.persistence.postgresql.reporting.model.PostgresMapping;
import org.kie.kogito.persistence.postgresql.reporting.model.PostgresMappingDefinition;
import org.kie.kogito.persistence.postgresql.reporting.model.PostgresPartitionField;
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

@QuarkusTest
@QuarkusTestResource(PostgreSqlQuarkusTestResource.class)
class PostgresApplyMappingSqlBuilderIT {

    private static final String CACHE_NAME = "DynamicType";

    private static final String SQL = "SELECT " +
            "ROW_NUMBER() OVER (ORDER BY name, key) as id, " +
            "name, " +
            "key, " +
            "field1MappedField, " +
            "field2MappedField " +
            "FROM " +
            "DynamicTypeExtract";

    @Inject
    PostgresStorageService storageService;

    @Inject
    CacheEntityRepository repository;

    @Inject
    GenericPostgresDatabaseManagerImpl databaseManager;

    @BeforeEach
    @Transactional
    public void setup() {
        storageService.getCache(CACHE_NAME).clear();
    }

    @Test
    @Transactional
    void testApplyMappingToExistingData() {
        final Storage<String, DynamicType> cache = storageService.getCache(CACHE_NAME, DynamicType.class);
        cache.put("key1", new DynamicType("A", 1));
        cache.put("key2", new DynamicType("B", 1));

        // Dynamically create mapping for DynamicType
        databaseManager.createArtifacts(new PostgresMappingDefinition("dynamicMappingId",
                "kogito_data_cache",
                "json_value",
                List.of(new PostgresField("key")),
                List.of(new PostgresPartitionField("name", CACHE_NAME)),
                "DynamicTypeExtract",
                List.of(new PostgresMapping("field1",
                        new PostgresJsonField("field1MappedField", JsonType.STRING)),
                        new PostgresMapping("field2",
                                new PostgresJsonField("field2MappedField", JsonType.NUMBER)))));
        // We should now have two records in the extract table
        assertResultSize(2);

        // Add a third to verify we keep getting new records as expected
        cache.put("key3", new DynamicType("C", 3));
        assertResultSize(3);
    }

    @SuppressWarnings("unchecked")
    private void assertResultSize(final int expected) {
        final List<DynamicTypeExtractRow> results = repository
                .getEntityManager()
                .createNativeQuery(SQL, "DynamicTypeExtractMapping")
                .getResultList();
        assertThat(results).hasSize(expected);
    }

    @Entity
    @SqlResultSetMapping(
            name = "DynamicTypeExtractMapping",
            entities = {
                    @EntityResult(
                            entityClass = DynamicTypeExtractRow.class,
                            fields = { @FieldResult(name = "id", column = "id"),
                                    @FieldResult(name = "key", column = "key"),
                                    @FieldResult(name = "name", column = "name"),
                                    @FieldResult(name = "field1MappedField", column = "field1MappedField"),
                                    @FieldResult(name = "field2MappedField", column = "field2MappedField") })
            })
    public static class DynamicTypeExtractRow {

        @Id
        @Column(nullable = false)
        @SuppressWarnings("unused")
        private int id;

        @Column(nullable = false)
        private String key;

        @Column(nullable = false)
        private String name;

        @Column
        private String field1MappedField;

        @Column
        private Integer field2MappedField;
    }
}
