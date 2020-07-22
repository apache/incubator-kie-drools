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

package org.kie.kogito.persistence.mongodb.query;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.bson.conversions.Bson;
import org.kie.kogito.persistence.api.query.AttributeFilter;
import org.kie.kogito.persistence.api.query.AttributeSort;
import org.kie.kogito.persistence.api.query.Query;
import org.kie.kogito.persistence.api.query.SortDirection;
import org.kie.kogito.persistence.mongodb.model.MongoEntityMapper;

import static com.mongodb.client.model.Sorts.ascending;
import static com.mongodb.client.model.Sorts.descending;
import static com.mongodb.client.model.Sorts.orderBy;
import static java.util.stream.Collectors.toList;

public class MongoQuery<V, E> implements Query<V> {

    Integer limit;
    Integer offset;
    List<AttributeFilter<?>> filters;
    List<AttributeSort> sortBy;

    MongoEntityMapper<V, E> mongoEntityMapper;

    MongoCollection<E> mongoCollection;

    public MongoQuery(MongoCollection<E> mongoCollection, MongoEntityMapper<V, E> mongoEntityMapper) {
        this.mongoCollection = mongoCollection;
        this.mongoEntityMapper = mongoEntityMapper;
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
        this.sortBy = sortBy;
        return this;
    }

    @Override
    public List<V> execute() {
        MongoCollection<E> collection = this.mongoCollection;
        Optional<Bson> query = QueryUtils.generateQuery(this.filters, mongoEntityMapper::convertToMongoAttribute);
        Optional<Bson> sort = this.generateSort();

        FindIterable<E> find = query.map(collection::find).orElseGet(collection::find);
        find = sort.map(find::sort).orElse(find);
        find = Optional.ofNullable(this.offset).map(find::skip).orElse(find);
        find = Optional.ofNullable(this.limit).map(find::limit).orElse(find);

        List<V> list = new LinkedList<>();
        try (MongoCursor<E> cursor = find.iterator()) {
            while (cursor.hasNext()) {
                E e = cursor.next();
                list.add(mongoEntityMapper.mapToModel(e));
            }
        }
        return list;
    }

    private Optional<Bson> generateSort() {
        return Optional.ofNullable(this.sortBy).map(sbList -> orderBy(sbList.stream().map(
                sb -> SortDirection.ASC.equals(sb.getSort()) ?
                        ascending(mongoEntityMapper.convertToMongoAttribute(sb.getAttribute())) :
                        descending(mongoEntityMapper.convertToMongoAttribute(sb.getAttribute())))
                                                                              .collect(toList()))
        );
    }
}
