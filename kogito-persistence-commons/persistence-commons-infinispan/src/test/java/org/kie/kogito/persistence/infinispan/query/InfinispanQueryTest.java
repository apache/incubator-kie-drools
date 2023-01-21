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
package org.kie.kogito.persistence.infinispan.query;

import java.util.List;
import java.util.stream.Stream;

import org.infinispan.query.dsl.Query;
import org.infinispan.query.dsl.QueryFactory;
import org.infinispan.query.dsl.QueryResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.kogito.persistence.api.query.AttributeFilter;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.kie.kogito.persistence.api.query.QueryFilterFactory.and;
import static org.kie.kogito.persistence.api.query.QueryFilterFactory.between;
import static org.kie.kogito.persistence.api.query.QueryFilterFactory.contains;
import static org.kie.kogito.persistence.api.query.QueryFilterFactory.containsAll;
import static org.kie.kogito.persistence.api.query.QueryFilterFactory.containsAny;
import static org.kie.kogito.persistence.api.query.QueryFilterFactory.equalTo;
import static org.kie.kogito.persistence.api.query.QueryFilterFactory.greaterThan;
import static org.kie.kogito.persistence.api.query.QueryFilterFactory.greaterThanEqual;
import static org.kie.kogito.persistence.api.query.QueryFilterFactory.in;
import static org.kie.kogito.persistence.api.query.QueryFilterFactory.isNull;
import static org.kie.kogito.persistence.api.query.QueryFilterFactory.lessThan;
import static org.kie.kogito.persistence.api.query.QueryFilterFactory.lessThanEqual;
import static org.kie.kogito.persistence.api.query.QueryFilterFactory.like;
import static org.kie.kogito.persistence.api.query.QueryFilterFactory.not;
import static org.kie.kogito.persistence.api.query.QueryFilterFactory.notNull;
import static org.kie.kogito.persistence.api.query.QueryFilterFactory.or;
import static org.kie.kogito.persistence.api.query.QueryFilterFactory.orderBy;
import static org.kie.kogito.persistence.api.query.SortDirection.ASC;
import static org.kie.kogito.persistence.api.query.SortDirection.DESC;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InfinispanQueryTest {

    private static final String rootType = "org.kie.kogito.index.model.ProcessInstance";

    @Mock
    QueryFactory factory;

    @Mock
    Query mockQuery;

    @Mock
    QueryResult queryResult;

    private static Stream<Arguments> provideFilters() {
        return Stream.of(
                Arguments.of(
                        asList(like("name", "test%")),
                        "FROM org.kie.kogito.index.model.ProcessInstance o WHERE o.name like 'test%'"),
                Arguments.of(
                        asList(in("id", asList("8035b580-6ae4-4aa8-9ec0-e18e19809e0b", "a1e139d5-4e77-48c9-84ae-34578e904e5a"))),
                        "FROM org.kie.kogito.index.model.ProcessInstance o WHERE o.id in ('8035b580-6ae4-4aa8-9ec0-e18e19809e0b', 'a1e139d5-4e77-48c9-84ae-34578e904e5a')"),
                Arguments.of(
                        asList(equalTo("id", "8035b580-6ae4-4aa8-9ec0-e18e19809e0b")),
                        "FROM org.kie.kogito.index.model.ProcessInstance o WHERE o.id = '8035b580-6ae4-4aa8-9ec0-e18e19809e0b'"),
                Arguments.of(
                        asList(contains("name", "test")),
                        "FROM org.kie.kogito.index.model.ProcessInstance o WHERE o.name = 'test'"),
                Arguments.of(
                        asList(containsAll("name", asList("name1", "name2"))),
                        "FROM org.kie.kogito.index.model.ProcessInstance o WHERE o.name = 'name1' and o.name = 'name2'"),
                Arguments.of(
                        asList(containsAny("name", asList("name1", "name2"))),
                        "FROM org.kie.kogito.index.model.ProcessInstance o WHERE o.name = 'name1' or o.name = 'name2'"),
                Arguments.of(
                        asList(isNull("name")),
                        "FROM org.kie.kogito.index.model.ProcessInstance o WHERE o.name is null"),
                Arguments.of(
                        asList(notNull("name")),
                        "FROM org.kie.kogito.index.model.ProcessInstance o WHERE o.name is not null"),
                Arguments.of(
                        asList(between("start", "2019-01-01", "2020-01-01")),
                        "FROM org.kie.kogito.index.model.ProcessInstance o WHERE o.start between '2019-01-01' and '2020-01-01'"),
                Arguments.of(
                        asList(greaterThan("priority", 1)),
                        "FROM org.kie.kogito.index.model.ProcessInstance o WHERE o.priority > 1"),
                Arguments.of(
                        asList(greaterThanEqual("priority", 1)),
                        "FROM org.kie.kogito.index.model.ProcessInstance o WHERE o.priority >= 1"),
                Arguments.of(
                        asList(lessThan("priority", 1)),
                        "FROM org.kie.kogito.index.model.ProcessInstance o WHERE o.priority < 1"),
                Arguments.of(
                        asList(lessThanEqual("priority", 1)),
                        "FROM org.kie.kogito.index.model.ProcessInstance o WHERE o.priority <= 1"),
                Arguments.of(
                        asList(and(asList(lessThanEqual("priority", 1), greaterThan("priority", 1)))),
                        "FROM org.kie.kogito.index.model.ProcessInstance o WHERE (o.priority <= 1 and o.priority > 1)"),
                Arguments.of(
                        asList(or(asList(lessThanEqual("priority", 1), greaterThan("priority", 1)))),
                        "FROM org.kie.kogito.index.model.ProcessInstance o WHERE (o.priority <= 1 or o.priority > 1)"),
                Arguments.of(
                        asList(and(asList(notNull("name"), contains("name", "test"))), or(asList(lessThanEqual("priority", 1), greaterThan("priority", 1)))),
                        "FROM org.kie.kogito.index.model.ProcessInstance o WHERE (o.name is not null and o.name = 'test') and (o.priority <= 1 or o.priority > 1)"),
                Arguments.of(
                        asList(or(asList(isNull("name"), contains("name", "test"))),
                                and(asList(between("start", "2019-01-01", "2020-01-01"), or(asList(lessThanEqual("priority", 1), greaterThan("priority", 1)))))),
                        "FROM org.kie.kogito.index.model.ProcessInstance o WHERE (o.name is null or o.name = 'test') and (o.start between '2019-01-01' and '2020-01-01' and (o.priority <= 1 or o.priority > 1))"),
                Arguments.of(
                        asList(not(equalTo("priority", 1))),
                        "FROM org.kie.kogito.index.model.ProcessInstance o WHERE not o.priority = 1"));
    }

    @BeforeEach
    public void setup() {
        when(factory.create(any())).thenReturn(mockQuery);
        when(mockQuery.execute()).thenReturn(queryResult);
    }

    @Test
    void testNoParameters() {
        InfinispanQuery query = new InfinispanQuery(factory, rootType);

        query.execute();

        verify(factory).create("FROM org.kie.kogito.index.model.ProcessInstance o");
        verify(queryResult).list();
    }

    @Test
    void testEmptyParameters() {
        InfinispanQuery query = new InfinispanQuery(factory, rootType);
        query.filter(emptyList());
        query.sort(emptyList());

        query.execute();

        verify(factory).create("FROM org.kie.kogito.index.model.ProcessInstance o");
        verify(queryResult).list();
    }

    @Test
    void testPagination() {
        InfinispanQuery query = new InfinispanQuery(factory, rootType);
        query.limit(10);
        query.offset(0);

        query.execute();

        verify(factory).create("FROM org.kie.kogito.index.model.ProcessInstance o");
        verify(mockQuery).startOffset(0);
        verify(mockQuery).maxResults(10);
        verify(queryResult).list();
    }

    @Test
    void testOrderBy() {
        InfinispanQuery query = new InfinispanQuery(factory, rootType);
        query.sort(asList(orderBy("name", DESC), orderBy("date", ASC)));

        query.execute();

        verify(factory).create("FROM org.kie.kogito.index.model.ProcessInstance o ORDER BY o.name DESC, o.date ASC");
        verify(queryResult).list();
    }

    @ParameterizedTest
    @MethodSource("provideFilters")
    void assertQueryFilters(List<AttributeFilter<?>> filters, String queryString) {
        InfinispanQuery query = new InfinispanQuery(factory, rootType);
        query.filter(filters);

        query.execute();

        verify(factory).create(queryString);
        verify(queryResult).list();
    }
}
