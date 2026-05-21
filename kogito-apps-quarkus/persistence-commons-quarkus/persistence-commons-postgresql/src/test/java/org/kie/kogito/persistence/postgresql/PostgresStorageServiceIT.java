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
package org.kie.kogito.persistence.postgresql;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.persistence.api.Storage;
import org.kie.kogito.persistence.api.query.Query;
import org.kie.kogito.persistence.api.query.QueryFilterFactory;
import org.kie.kogito.persistence.api.query.SortDirection;
import org.kie.kogito.persistence.postgresql.model.CacheEntity;
import org.kie.kogito.persistence.postgresql.model.CacheEntityRepository;
import org.kie.kogito.persistence.postgresql.model.CacheId;
import org.kie.kogito.testcontainers.quarkus.PostgreSqlQuarkusTestResource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.persistence.postgresql.ProcessInstanceModel.newModel;

@QuarkusTest
@QuarkusTestResource(PostgreSqlQuarkusTestResource.class)
class PostgresStorageServiceIT {

    static class StructuredType {

        private Integer field1;
        private Long field2;
        private String field3;

        StructuredType() {
        }

        StructuredType(final Integer field1, final Long field2, final String field3) {
            this.field1 = field1;
            this.field2 = field2;
            this.field3 = field3;
        }

        public Integer getField1() {
            return field1;
        }

        public Long getField2() {
            return field2;
        }

        public String getField3() {
            return field3;
        }

        public static StructuredTypeBuilder builder() {
            return new StructuredTypeBuilder();
        }

        private static class StructuredTypeBuilder {

            private Integer field1;
            private Long field2;
            private String field3;

            public StructuredTypeBuilder withField1(final Integer value) {
                this.field1 = value;
                return this;
            }

            public StructuredTypeBuilder withField2(final Long value) {
                this.field2 = value;
                return this;
            }

            public StructuredTypeBuilder withField3(final String value) {
                this.field3 = value;
                return this;
            }

            public StructuredType build() {
                return new StructuredType(field1, field2, field3);
            }
        }
    }

    @Inject
    PostgresStorageService storageService;

    @Inject
    CacheEntityRepository repository;

    @Inject
    ObjectMapper mapper;

    @BeforeEach
    @Transactional
    public void setup() {
        storageService.getCache("test").clear();
        storageService.getCache("queries").clear();
    }

    @Test
    @Transactional
    void testCacheByName() {
        String cacheName = "test";
        String key = "akey";
        String value = "avalue";

        Storage<String, String> cache = storageService.getCache(cacheName);
        assertThat(cache.put(key, value)).isEqualTo(value);
        CacheEntity entity = repository.findById(new CacheId(cacheName, key));

        assertThat(entity).isNotNull();
        assertThat(entity.getKey()).isEqualTo(key);
        assertThat(entity.getName()).isEqualTo(cacheName);
        assertThat(entity.getValue().get("value").asText()).isEqualTo(value);

        assertThat(cache.containsKey(key)).isTrue();
        assertThat(cache.entries()).hasSize(1);

        value = "newValue";
        assertThat(cache.put(key, value)).isEqualTo(value);

        entity = repository.findById(new CacheId(cacheName, key));

        assertThat(entity).isNotNull();
        assertThat(entity.getKey()).isEqualTo(key);
        assertThat(entity.getName()).isEqualTo(cacheName);
        assertThat(entity.getValue().get("value").asText()).isEqualTo(value);

        cache.remove(key);

        assertThat(cache.containsKey(key)).isFalse();
        assertThat(cache.entries()).isEmpty();

        entity = repository.findById(new CacheId(cacheName, key));

        assertThat(entity).isNull();
    }

    @Test
    @Transactional
    void testCacheByNameAndType() {
        String cacheName = "pi";
        ProcessInstanceModel value = newModel();
        String key = value.getId();

        Storage<String, ProcessInstanceModel> cache = storageService.getCache(cacheName, ProcessInstanceModel.class);
        assertThat(cache.put(key, value)).usingRecursiveComparison().isEqualTo(value);
        CacheEntity entity = repository.findById(new CacheId(cacheName, key));

        assertThat(entity).isNotNull();
        assertThat(entity.getKey()).isEqualTo(key);
        assertThat(entity.getName()).isEqualTo(cacheName);
        assertThat(entity.getValue()).isEqualTo(mapper.valueToTree(value));

        assertThat(cache.containsKey(key)).isTrue();
        assertThat(cache.entries()).hasSize(1);

        value = newModel(key);
        assertThat(cache.put(key, value)).isEqualTo(value);

        entity = repository.findById(new CacheId(cacheName, key));

        assertThat(entity).isNotNull();
        assertThat(entity.getKey()).isEqualTo(key);
        assertThat(entity.getName()).isEqualTo(cacheName);
        assertThat(entity.getValue()).isEqualTo(mapper.valueToTree(value));

        cache.remove(key);

        assertThat(cache.containsKey(key)).isFalse();
        assertThat(cache.entries()).hasSize(0);

        entity = repository.findById(new CacheId(cacheName, key));

        assertThat(entity).isNull();
    }

    @Test
    @Transactional
    void testCacheByNameAndJsonType() {
        String cacheName = "json";
        ProcessInstanceModel pi = newModel();
        ObjectNode value = mapper.valueToTree(pi);
        String key = pi.getId();

        Storage<String, ObjectNode> cache = storageService
                .getCache(cacheName, ObjectNode.class, ProcessInstanceModel.class.getCanonicalName());
        assertThat(cache.put(key, value)).isEqualTo(value);
        CacheEntity entity = repository.findById(new CacheId(cacheName, key));

        assertThat(entity).isNotNull();
        assertThat(entity.getKey()).isEqualTo(key);
        assertThat(entity.getName()).isEqualTo(cacheName);
        assertThat(entity.getValue()).isEqualTo(value);

        assertThat(cache.containsKey(key)).isTrue();
        assertThat(cache.entries()).hasSize(1);

        value = mapper.valueToTree(newModel(key));
        assertThat(cache.put(key, value)).isEqualTo(value);

        entity = repository.findById(new CacheId(cacheName, key));

        assertThat(entity).isNotNull();
        assertThat(entity.getKey()).isEqualTo(key);
        assertThat(entity.getName()).isEqualTo(cacheName);
        assertThat(entity.getValue()).isEqualTo(value);

        cache.remove(key);

        assertThat(cache.containsKey(key)).isFalse();
        assertThat(cache.entries()).hasSize(0);

        entity = repository.findById(new CacheId(cacheName, key));

        assertThat(entity).isNull();
    }

    @Test
    @Transactional
    void testQuery_Equal() {
        String cacheName = "queries";

        Storage<String, StructuredType> cache = storageService.getCache(cacheName, StructuredType.class);
        cache.put("key1", StructuredType.builder().withField1(1).withField3("A").build());
        cache.put("key2", StructuredType.builder().withField1(2).withField3("B").build());

        Query<StructuredType> query = cache.query();
        query.filter(List.of(QueryFilterFactory.equalTo("field1", 1)));

        List<StructuredType> results = query.execute();
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getField1()).isEqualTo(1);
        assertThat(results.get(0).getField3()).isEqualTo("A");
    }

    @Test
    @Transactional
    void testQuery_Integers() {
        String cacheName = "queries";

        Storage<String, StructuredType> cache = storageService.getCache(cacheName, StructuredType.class);
        cache.put("key1", StructuredType.builder().withField1(1).build());
        cache.put("key2", StructuredType.builder().withField1(2).build());

        Query<StructuredType> query = cache.query();
        query.filter(List.of(QueryFilterFactory.equalTo("field1", 1)));

        List<StructuredType> results = query.execute();
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getField1()).isEqualTo(1);
    }

    @Test
    @Transactional
    void testQuery_Longs() {
        String cacheName = "queries";

        Storage<String, StructuredType> cache = storageService.getCache(cacheName, StructuredType.class);
        cache.put("key1", StructuredType.builder().withField2(1L).build());
        cache.put("key2", StructuredType.builder().withField2(2L).build());

        Query<StructuredType> query = cache.query();
        query.filter(List.of(QueryFilterFactory.equalTo("field2", 1)));

        List<StructuredType> results = query.execute();
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getField2()).isEqualTo(1);
    }

    @Test
    @Transactional
    void testQuery_Strings() {
        String cacheName = "queries";

        Storage<String, StructuredType> cache = storageService.getCache(cacheName, StructuredType.class);
        cache.put("key1", StructuredType.builder().withField3("A").build());
        cache.put("key2", StructuredType.builder().withField3("B").build());

        Query<StructuredType> query = cache.query();
        query.filter(List.of(QueryFilterFactory.equalTo("field3", "A")));

        List<StructuredType> results = query.execute();
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getField3()).isEqualTo("A");
    }

    @Test
    @Transactional
    void testQuery_GreaterThan() {
        String cacheName = "queries";

        Storage<String, StructuredType> cache = storageService.getCache(cacheName, StructuredType.class);
        cache.put("key1", StructuredType.builder().withField1(1).withField3("A").build());
        cache.put("key2", StructuredType.builder().withField1(2).withField3("B").build());

        Query<StructuredType> query = cache.query();
        query.filter(List.of(QueryFilterFactory.greaterThan("field1", 1)));

        List<StructuredType> results = query.execute();
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getField1()).isEqualTo(2);
        assertThat(results.get(0).getField3()).isEqualTo("B");
    }

    @Test
    @Transactional
    void testQuery_GreaterThanEqual() {
        String cacheName = "queries";

        Storage<String, StructuredType> cache = storageService.getCache(cacheName, StructuredType.class);
        cache.put("key1", StructuredType.builder().withField1(1).withField3("A").build());
        cache.put("key2", StructuredType.builder().withField1(2).withField3("B").build());

        Query<StructuredType> query = cache.query();
        query.filter(List.of(QueryFilterFactory.greaterThanEqual("field1", 1)));

        List<StructuredType> results = query.execute();
        assertThat(results).hasSize(2);
        assertThat(results.get(0).getField1()).isEqualTo(1);
        assertThat(results.get(0).getField3()).isEqualTo("A");
        assertThat(results.get(1).getField1()).isEqualTo(2);
        assertThat(results.get(1).getField3()).isEqualTo("B");
    }

    @Test
    @Transactional
    void testQuery_LessThan() {
        String cacheName = "queries";

        Storage<String, StructuredType> cache = storageService.getCache(cacheName, StructuredType.class);
        cache.put("key1", StructuredType.builder().withField1(1).withField3("A").build());
        cache.put("key2", StructuredType.builder().withField1(2).withField3("B").build());

        Query<StructuredType> query = cache.query();
        query.filter(List.of(QueryFilterFactory.lessThan("field1", 2)));

        List<StructuredType> results = query.execute();
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getField1()).isEqualTo(1);
        assertThat(results.get(0).getField3()).isEqualTo("A");
    }

    @Test
    @Transactional
    void testQuery_LessThanEqual() {
        String cacheName = "queries";

        Storage<String, StructuredType> cache = storageService.getCache(cacheName, StructuredType.class);
        cache.put("key1", StructuredType.builder().withField1(1).withField3("A").build());
        cache.put("key2", StructuredType.builder().withField1(2).withField3("B").build());

        Query<StructuredType> query = cache.query();
        query.filter(List.of(QueryFilterFactory.lessThanEqual("field1", 2)));

        List<StructuredType> results = query.execute();
        assertThat(results).hasSize(2);
        assertThat(results.get(0).getField1()).isEqualTo(1);
        assertThat(results.get(0).getField3()).isEqualTo("A");
        assertThat(results.get(1).getField1()).isEqualTo(2);
        assertThat(results.get(1).getField3()).isEqualTo("B");
    }

    @Test
    @Transactional
    void testQuery_Like() {
        String cacheName = "queries";

        Storage<String, StructuredType> cache = storageService.getCache(cacheName, StructuredType.class);
        cache.put("key1", StructuredType.builder().withField1(1).withField3("AAA").build());
        cache.put("key2", StructuredType.builder().withField1(2).withField3("BAA").build());

        Query<StructuredType> query = cache.query();
        query.filter(List.of(QueryFilterFactory.like("field3", "*AA")));

        List<StructuredType> results = query.execute();
        assertThat(results).hasSize(2);
        assertThat(results.get(0).getField1()).isEqualTo(1);
        assertThat(results.get(0).getField3()).isEqualTo("AAA");
        assertThat(results.get(1).getField1()).isEqualTo(2);
        assertThat(results.get(1).getField3()).isEqualTo("BAA");
    }

    @Test
    @Transactional
    void testQuery_In() {
        String cacheName = "queries";

        Storage<String, StructuredType> cache = storageService.getCache(cacheName, StructuredType.class);
        cache.put("key1", StructuredType.builder().withField1(1).withField3("AAA").build());
        cache.put("key2", StructuredType.builder().withField1(2).withField3("BAA").build());

        Query<StructuredType> query = cache.query();
        query.filter(List.of(QueryFilterFactory.in("field3", List.of("AAA", "BAA"))));

        List<StructuredType> results = query.execute();
        assertThat(results).hasSize(2);
        assertThat(results.get(0).getField1()).isEqualTo(1);
        assertThat(results.get(0).getField3()).isEqualTo("AAA");
        assertThat(results.get(1).getField1()).isEqualTo(2);
        assertThat(results.get(1).getField3()).isEqualTo("BAA");
    }

    @Test
    @Transactional
    void testQuery_IsNull() {
        String cacheName = "queries";

        Storage<String, StructuredType> cache = storageService.getCache(cacheName, StructuredType.class);
        cache.put("key1", StructuredType.builder().withField1(1).build());
        cache.put("key2", StructuredType.builder().withField1(2).withField3("B").build());

        Query<StructuredType> query = cache.query();
        query.filter(List.of(QueryFilterFactory.isNull("field3")));

        List<StructuredType> results = query.execute();
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getField1()).isEqualTo(1);
    }

    @Test
    @Transactional
    void testQuery_IsNotNull() {
        String cacheName = "queries";

        Storage<String, StructuredType> cache = storageService.getCache(cacheName, StructuredType.class);
        cache.put("key1", StructuredType.builder().withField1(1).build());
        cache.put("key2", StructuredType.builder().withField1(2).withField3("B").build());

        Query<StructuredType> query = cache.query();
        query.filter(List.of(QueryFilterFactory.notNull("field3")));

        List<StructuredType> results = query.execute();
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getField1()).isEqualTo(2);
        assertThat(results.get(0).getField3()).isEqualTo("B");
    }

    @Test
    @Transactional
    void testQuery_Contains() {
        String cacheName = "queries";

        Storage<String, StructuredType> cache = storageService.getCache(cacheName, StructuredType.class);
        cache.put("key1", StructuredType.builder().withField1(1).withField3("A").build());
        cache.put("key2", StructuredType.builder().withField1(2).withField3("B").build());

        Query<StructuredType> query = cache.query();
        query.filter(List.of(QueryFilterFactory.contains("field3", "A")));

        List<StructuredType> results = query.execute();
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getField1()).isEqualTo(1);
        assertThat(results.get(0).getField3()).isEqualTo("A");
    }

    @Test
    @Transactional
    void testQuery_ContainsAll() {
        String cacheName = "queries";

        Storage<String, StructuredType> cache = storageService.getCache(cacheName, StructuredType.class);
        cache.put("key1", StructuredType.builder().withField1(1).withField3("A").build());
        cache.put("key2", StructuredType.builder().withField1(2).withField3("B").build());

        Query<StructuredType> query = cache.query();
        query.filter(List.of(QueryFilterFactory.containsAll("field3", List.of("A"))));

        List<StructuredType> results = query.execute();
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getField1()).isEqualTo(1);
        assertThat(results.get(0).getField3()).isEqualTo("A");
    }

    @Test
    @Transactional
    void testQuery_ContainsAny() {
        String cacheName = "queries";

        Storage<String, StructuredType> cache = storageService.getCache(cacheName, StructuredType.class);
        cache.put("key1", StructuredType.builder().withField1(1).withField3("A").build());
        cache.put("key2", StructuredType.builder().withField1(2).withField3("B").build());

        Query<StructuredType> query = cache.query();
        query.filter(List.of(QueryFilterFactory.containsAny("field3", List.of("A", "B"))));

        List<StructuredType> results = query.execute();
        assertThat(results).hasSize(2);
        assertThat(results.get(0).getField1()).isEqualTo(1);
        assertThat(results.get(0).getField3()).isEqualTo("A");
        assertThat(results.get(1).getField1()).isEqualTo(2);
        assertThat(results.get(1).getField3()).isEqualTo("B");
    }

    @Test
    @Transactional
    void testQuery_Or() {
        String cacheName = "queries";

        Storage<String, StructuredType> cache = storageService.getCache(cacheName, StructuredType.class);
        cache.put("key1", StructuredType.builder().withField1(1).withField3("A").build());
        cache.put("key2", StructuredType.builder().withField1(2).withField3("B").build());

        Query<StructuredType> query = cache.query();
        query.filter(List.of(QueryFilterFactory.or(
                List.of(QueryFilterFactory.equalTo("field1", 1),
                        QueryFilterFactory.equalTo("field3", "B")))));

        List<StructuredType> results = query.execute();
        assertThat(results).hasSize(2);
        assertThat(results.get(0).getField1()).isEqualTo(1);
        assertThat(results.get(0).getField3()).isEqualTo("A");
        assertThat(results.get(1).getField1()).isEqualTo(2);
        assertThat(results.get(1).getField3()).isEqualTo("B");
    }

    @Test
    @Transactional
    void testQuery_And() {
        String cacheName = "queries";

        Storage<String, StructuredType> cache = storageService.getCache(cacheName, StructuredType.class);
        cache.put("key1", StructuredType.builder().withField1(1).withField3("A").build());
        cache.put("key2", StructuredType.builder().withField1(2).withField3("B").build());

        Query<StructuredType> query = cache.query();
        query.filter(List.of(QueryFilterFactory.and(
                List.of(QueryFilterFactory.equalTo("field1", 1),
                        QueryFilterFactory.equalTo("field3", "A")))));

        List<StructuredType> results = query.execute();
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getField1()).isEqualTo(1);
        assertThat(results.get(0).getField3()).isEqualTo("A");
    }

    @Test
    @Transactional
    void testQuery_Between() {
        String cacheName = "queries";

        Storage<String, StructuredType> cache = storageService.getCache(cacheName, StructuredType.class);
        cache.put("key1", StructuredType.builder().withField1(1).withField3("A").build());
        cache.put("key2", StructuredType.builder().withField1(2).withField3("B").build());
        cache.put("key3", StructuredType.builder().withField1(3).withField3("C").build());

        Query<StructuredType> query = cache.query();
        query.filter(List.of(QueryFilterFactory.between("field1", 2, 3)));

        List<StructuredType> results = query.execute();
        assertThat(results).hasSize(2);
        assertThat(results.get(0).getField1()).isEqualTo(2);
        assertThat(results.get(0).getField3()).isEqualTo("B");
        assertThat(results.get(1).getField1()).isEqualTo(3);
        assertThat(results.get(1).getField3()).isEqualTo("C");
    }

    @Test
    @Transactional
    void testQuery_Not() {
        String cacheName = "queries";

        Storage<String, StructuredType> cache = storageService.getCache(cacheName, StructuredType.class);
        cache.put("key1", StructuredType.builder().withField1(1).withField3("A").build());
        cache.put("key2", StructuredType.builder().withField1(2).withField3("B").build());

        Query<StructuredType> query = cache.query();
        query.filter(List.of(QueryFilterFactory.not(QueryFilterFactory.equalTo("field1", 2))));

        List<StructuredType> results = query.execute();
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getField1()).isEqualTo(1);
        assertThat(results.get(0).getField3()).isEqualTo("A");
    }

    @Test
    @Transactional
    void testQuery_OrderByString() {
        String cacheName = "queries";

        Storage<String, StructuredType> cache = storageService.getCache(cacheName, StructuredType.class);
        cache.put("key1", StructuredType.builder().withField1(1).withField3("A").build());
        cache.put("key2", StructuredType.builder().withField1(2).withField3("B").build());

        Query<StructuredType> query = cache.query();
        query.sort(List.of(QueryFilterFactory.orderBy("field3", SortDirection.DESC)));

        List<StructuredType> results = query.execute();
        assertThat(results).hasSize(2);
        assertThat(results.get(0).getField1()).isEqualTo(2);
        assertThat(results.get(0).getField3()).isEqualTo("B");
        assertThat(results.get(1).getField1()).isEqualTo(1);
        assertThat(results.get(1).getField3()).isEqualTo("A");
    }

    @Test
    @Transactional
    void testQuery_OrderByNumeric() {
        String cacheName = "queries";

        Storage<String, StructuredType> cache = storageService.getCache(cacheName, StructuredType.class);
        cache.put("key1", StructuredType.builder().withField1(1).withField3("A").build());
        cache.put("key2", StructuredType.builder().withField1(2).withField3("B").build());

        Query<StructuredType> query = cache.query();
        query.sort(List.of(QueryFilterFactory.orderBy("field1", SortDirection.DESC)));

        List<StructuredType> results = query.execute();
        assertThat(results).hasSize(2);
        assertThat(results.get(0).getField1()).isEqualTo(2);
        assertThat(results.get(0).getField3()).isEqualTo("B");
        assertThat(results.get(1).getField1()).isEqualTo(1);
        assertThat(results.get(1).getField3()).isEqualTo("A");
    }

}
