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
package org.kie.kogito.index.service.graphql.query;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.kogito.index.api.DefaultDateTimeCoercing;
import org.kie.kogito.index.graphql.GraphQLScalarTypeProducer;
import org.kie.kogito.index.graphql.query.GraphQLQueryParser;
import org.kie.kogito.index.graphql.query.GraphQLQueryParserRegistry;
import org.kie.kogito.index.service.graphql.GraphQLSchemaManagerImpl;
import org.kie.kogito.index.storage.DataIndexStorageService;
import org.kie.kogito.persistence.api.query.AttributeFilter;
import org.kie.kogito.persistence.api.query.FilterCondition;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import graphql.schema.GraphQLScalarType;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
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
    GraphQLSchemaManagerImpl manager;

    @Mock
    DataIndexStorageService cacheService;

    @Spy
    GraphQLScalarType qlDateTimeScalarType = new GraphQLScalarTypeProducer(new DefaultDateTimeCoercing()).dateTimeScalar();

    GraphQLQueryParser processInstanceParser;
    GraphQLQueryParser jobParser;

    private static void assertAttributeFilter(String attribute, FilterCondition condition, AttributeFilter filter, String... values) {
        assertThat(filter.getCondition()).isEqualTo(condition);
        assertThat(filter.getAttribute()).isEqualTo(attribute);
        if (values.length == 1) {
            assertThat(filter.getValue()).isEqualTo(values[0]);
        } else {
            assertThat(filter.getValue()).asList().containsExactly(values);
        }
    }

    static Stream<Map<String, Object>> processInstanceProvider() {
        return Stream.of(
                //String mapper
                singletonMap("businessKey", null),
                singletonMap("businessKey", emptyMap()),
                singletonMap("businessKey", singletonMap("like", null)),
                singletonMap("businessKey", singletonMap("in", emptyList())),
                //String Array mapper
                singletonMap("roles", null),
                singletonMap("roles", emptyMap()),
                singletonMap("roles", singletonMap("contains", null)),
                singletonMap("roles", singletonMap("containsAll", emptyList())),
                // Date mapper
                singletonMap("start", null),
                singletonMap("start", emptyMap()),
                singletonMap("start", singletonMap("equal", null)),
                singletonMap("start", singletonMap("between", null)),
                singletonMap("start", singletonMap("between", emptyMap())),
                //Enum mapper
                singletonMap("state", null),
                singletonMap("state", singletonMap("equal", null)));
    }

    static Stream<Map<String, Object>> jobProvider() {
        return Stream.of(
                //Numeric mapper
                singletonMap("priority", null),
                singletonMap("priority", emptyMap()),
                singletonMap("priority", singletonMap("equal", null)),
                singletonMap("priority", singletonMap("between", null)),
                singletonMap("priority", singletonMap("between", emptyMap())),
                //Id mapper
                singletonMap("id", null),
                singletonMap("id", emptyMap()),
                singletonMap("id", singletonMap("equal", null)),
                singletonMap("id", singletonMap("in", emptyList())));
    }

    @BeforeEach
    public void setup() {
        manager.setup();
        processInstanceParser = GraphQLQueryParserRegistry.get().getParser("ProcessInstanceArgument");
        jobParser = GraphQLQueryParserRegistry.get().getParser("JobArgument");
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
                equalTo("state", 1));
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
                containsAny("roles", asList("admin", "kogito")));
    }

    @ParameterizedTest
    @MethodSource("processInstanceProvider")
    void testProcessInstanceNullQuery(Map<String, Object> where) {
        List<AttributeFilter<?>> filters = processInstanceParser.apply(where);
        assertThat(filters).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("jobProvider")
    void testJobNullQuery(Map<String, Object> where) {
        List<AttributeFilter<?>> filters = jobParser.apply(where);
        assertThat(filters).isEmpty();
    }
}
