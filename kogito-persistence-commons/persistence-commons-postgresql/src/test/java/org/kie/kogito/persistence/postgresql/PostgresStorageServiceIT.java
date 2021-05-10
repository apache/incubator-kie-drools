/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.persistence.postgresql;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.kie.kogito.persistence.api.Storage;
import org.kie.kogito.persistence.postgresql.model.CacheEntity;
import org.kie.kogito.persistence.postgresql.model.CacheEntityRepository;
import org.kie.kogito.persistence.postgresql.model.CacheId;
import org.kie.kogito.testcontainers.quarkus.PostgreSqlQuarkusTestResource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.persistence.postgresql.ProcessInstanceModel.newModel;

@QuarkusTest
@QuarkusTestResource(PostgreSqlQuarkusTestResource.class)
class PostgresStorageServiceIT {

    @Inject
    PostgresStorageService storageService;

    @Inject
    CacheEntityRepository repository;

    @Inject
    ObjectMapper mapper;

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

}
