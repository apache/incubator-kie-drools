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
package org.kie.kogito.persistence.redis;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.kie.kogito.persistence.api.query.AttributeFilter;
import org.kie.kogito.persistence.api.query.AttributeSort;
import org.kie.kogito.persistence.api.query.Query;
import org.kie.kogito.persistence.api.query.SortDirection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.redisearch.Client;
import io.redisearch.SearchResult;

import static org.kie.kogito.persistence.redis.Constants.RAW_OBJECT_FIELD;

public class RedisQuery<V> implements Query<V> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisQuery.class);

    Integer limit;
    Integer offset;
    List<AttributeFilter<?>> filters;
    AttributeSort sortBy;
    String indexName;

    private Class<V> type;

    private Client redisClient;

    public RedisQuery(Client redisClient, String indexName, Class<V> type) {
        this.redisClient = redisClient;
        this.indexName = indexName;
        this.type = type;
    }

    @Override
    public Query<V> limit(Integer limit) {
        this.limit = limit;
        return this;
    }

    @Override
    public Query<V> offset(Integer offset) {
        this.offset = offset;
        return this;
    }

    @Override
    public Query<V> filter(List<AttributeFilter<?>> filters) {
        this.filters = filters;
        return this;
    }

    @Override
    public Query<V> sort(List<AttributeSort> sortBy) {
        if (!sortBy.isEmpty()) {
            if (sortBy.size() > 1) { // TODO: implement backend side sorting on multiple attributes https://issues.redhat.com/browse/KOGITO-4072
                throw new UnsupportedOperationException("Multiple sorting attributes not implemented yet.");
            }
            this.sortBy = sortBy.get(0);
        }
        return this;
    }

    @Override
    public List<V> execute() {
        io.redisearch.Query query = new io.redisearch.Query(RedisQueryFactory.buildQueryBody(indexName, filters));

        setQueryLimitAndOffset(query);

        if (sortBy != null) {
            query.setSortBy(sortBy.getAttribute(), SortDirection.ASC.equals(sortBy.getSort()));
        }

        RedisQueryFactory.addFilters(query, filters);
        SearchResult search = redisClient.search(query);
        LOGGER.debug(String.format("%d documets have been found for the query.", search.totalResults));

        return search.docs.stream().map(x -> {
            try {
                return JsonUtils.getMapper().readValue((String) x.get(RAW_OBJECT_FIELD), type);
            } catch (IOException e) {
                throw new RuntimeException("Could not deserialize a retrieved object.", e);
            }
        }).collect(Collectors.toList());
    }

    private void setQueryLimitAndOffset(io.redisearch.Query query) {
        if (limit != null && offset == null) {
            LOGGER.warn("Limit was specified in Redis query but not the offset. Limit is ignored.");
            return;
        }
        if (limit == null && offset != null) {
            LOGGER.warn("Offset was specified in Redis query but not the limit. Offset is ignored.");
            return;
        }
        if (limit != null && offset != null) {
            query.limit(offset, limit);
        }
    }
}
