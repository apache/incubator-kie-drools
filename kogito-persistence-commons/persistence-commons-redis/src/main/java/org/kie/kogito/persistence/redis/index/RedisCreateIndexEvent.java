/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.persistence.redis.index;

import java.util.ArrayList;
import java.util.List;

import io.redisearch.Schema;

public class RedisCreateIndexEvent {

    private final String indexName;
    private final List<Schema.Field> fields = new ArrayList<>();

    public RedisCreateIndexEvent(String indexName) {
        this.indexName = indexName;
    }

    public RedisCreateIndexEvent withField(Schema.Field field) {
        this.fields.add(field);
        return this;
    }

    public String getIndexName() {
        return indexName;
    }

    public List<Schema.Field> getFields() {
        return fields;
    }
}
