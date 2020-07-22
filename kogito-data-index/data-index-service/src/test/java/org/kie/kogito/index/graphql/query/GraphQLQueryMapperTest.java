/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.index.graphql.query;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import graphql.schema.GraphQLScalarType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.index.DataIndexStorageService;
import org.kie.kogito.index.graphql.GraphQLScalarTypeProducer;
import org.kie.kogito.index.graphql.GraphQLSchemaManager;
import org.kie.kogito.persistence.api.query.AttributeFilter;
import org.kie.kogito.persistence.api.query.FilterCondition;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.persistence.api.query.FilterCondition.AND;
import static org.kie.kogito.persistence.api.query.FilterCondition.BETWEEN;
import static org.kie.kogito.persistence.api.query.FilterCondition.EQUAL;
import static org.kie.kogito.persistence.api.query.QueryFilterFactory.contains;
import static org.kie.kogito.persistence.api.query.QueryFilterFactory.containsAll;
import static org.kie.kogito.persistence.api.query.QueryFilterFactory.containsAny;
import static org.kie.kogito.persistence.api.query.QueryFilterFactory.equalTo;
import static org.kie.kogito.persistence.api.query.QueryFilterFactory.in;
import static org.kie.kogito.persistence.api.query.QueryFilterFactory.isNull;
import static org.kie.kogito.persistence.api.query.QueryFilterFactory.notNull;

@ExtendWith(MockitoExtension.class)
public class GraphQLQueryMapperTest {

    @InjectMocks
    GraphQLSchemaManager manager;

    @Mock
    DataIndexStorageService cacheService;

    @Spy
    GraphQLScalarType qlDateTimeScalarType = new GraphQLScalarTypeProducer().dateTimeScalar();

    GraphQLQueryParser processInstanceParser;

    private static void assertAttributeFilter(String attribute, FilterCondition condition, AttributeFilter filter, String... values) {
        assertThat(filter.getCondition()).isEqualTo(condition);
        assertThat(filter.getAttribute()).isEqualTo(attribute);
        if (values.length == 1) {
            assertThat(filter.getValue()).isEqualTo(values[0]);
        } else {
            assertThat(filter.getValue()).asList().containsExactly(values);
        }
    }

    @BeforeEach
    public void setup() {
        manager.setup();
        processInstanceParser = GraphQLQueryParserRegistry.get().getParser("ProcessInstanceArgument");
    }

    @Test
    public void testQuery() {
        Map<String, Object> where = new HashMap<>();
        Map<String, Object> id = new HashMap();
        Map<String, Object> idValues = new HashMap();
        idValues.put("in", asList("adasdasd", "bla"));
        idValues.put("isNull", false);
        id.put("id", idValues);
        Map<String, Object> state = new HashMap();
        state.put("state", singletonMap("equal", 1));
        where.put("and", asList(id, state));

        List<AttributeFilter<?>> filters = processInstanceParser.apply(where);

        assertThat(filters).hasSize(1);
        AttributeFilter<?> filter = filters.get(0);
        assertThat(filter.getCondition()).isEqualTo(AND);
        assertThat(filter.getValue()).asList().hasSize(3).containsExactly(
                in("id", asList("adasdasd", "bla")),
                notNull("id"),
                equalTo("state", 1)
        );
    }

    @Test
    public void testDatesQuery() {
        Map<String, Object> where = new HashMap<>();
        where.put("start", singletonMap("equal", "2019-01-01"));
        Map<String, Object> between = new HashMap<>();
        between.put("from", "2019-01-01");
        between.put("to", "2020-01-01");
        where.put("end", singletonMap("between", between));

        List<AttributeFilter<?>> filters = processInstanceParser.apply(where);

        assertThat(filters).hasSize(2);
        assertAttributeFilter("start", EQUAL, filters.get(0), "2019-01-01");
        assertAttributeFilter("end", BETWEEN, filters.get(1), "2019-01-01", "2020-01-01");
    }

    @Test
    public void testNodesQuery() {
        Map<String, Object> where = new HashMap<>();
        where.put("nodes", singletonMap("name", singletonMap("equal", "StartNode")));

        List<AttributeFilter<?>> filters = processInstanceParser.apply(where);

        assertThat(filters).hasSize(1);
        assertAttributeFilter("nodes.name", EQUAL, filters.get(0), "StartNode");
    }

    @Test
    public void testOrQuery() {
        Map<String, Object> where = new HashMap<>();
        Map<String, Object> id = new HashMap();
        Map<String, Object> idValues = new HashMap();
        idValues.put("in", asList("travels"));
        id.put("processName", idValues);
        Map<String, Object> state = new HashMap();
        state.put("rootProcessId", singletonMap("isNull", true));
        Map<String, Object> roles = new HashMap();
        roles.put("contains", "admin");
        roles.put("containsAll", asList("kogito", "admin"));
        roles.put("containsAny", asList("admin", "kogito"));
        where.put("or", asList(id, state, singletonMap("roles", roles)));

        List<AttributeFilter<?>> filters = processInstanceParser.apply(where);

        assertThat(filters).hasSize(1).first().extracting(f -> f.getValue()).asList().containsExactly(
                in("processName", asList("travels")),
                isNull("rootProcessId"),
                contains("roles", "admin"),
                containsAll("roles", asList("kogito", "admin")),
                containsAny("roles", asList("admin", "kogito"))
        );
    }
}
