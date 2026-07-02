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
package org.kie.kogito.index.graphql.query;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.persistence.api.query.AttributeFilter;
import org.kie.kogito.persistence.api.query.FilterCondition;

import graphql.Scalars;
import graphql.schema.GraphQLInputObjectField;
import graphql.schema.GraphQLInputObjectType;
import graphql.schema.GraphQLList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.index.json.JsonUtils.jsonFilter;
import static org.kie.kogito.persistence.api.query.QueryFilterFactory.*;

public class GraphQLQueryMapperTest {

    private GraphQLQueryMapper mapper;

    @BeforeEach
    void setup() {
        mapper = new GraphQLQueryMapper();
    }

    @Test
    void testJsonMapperEqual() {
        assertThat(mapper.mapJsonArgument("variables").apply(Map.of("workflowdata", Map.of("number", Map.of("equal", 1))))).containsExactly(
                jsonFilter(equalTo("variables.workflowdata.number", 1)));
    }

    @Test
    void testJsonMapperGreater() {
        assertThat(mapper.mapJsonArgument("variables").apply(Map.of("workflowdata", Map.of("number", Map.of("greaterThan", 1))))).containsExactly(
                jsonFilter(greaterThan("variables.workflowdata.number", 1)));
    }

    @Test
    void testJsonMapperLess() {
        assertThat(mapper.mapJsonArgument("variables").apply(Map.of("workflowdata", Map.of("number", Map.of("lessThan", 1))))).containsExactly(
                jsonFilter(lessThan("variables.workflowdata.number", 1)));
    }

    @Test
    void testJsonMapperGreaterEqual() {
        assertThat(mapper.mapJsonArgument("variables").apply(Map.of("workflowdata", Map.of("number", Map.of("greaterThanEqual", 1))))).containsExactly(
                jsonFilter(greaterThanEqual("variables.workflowdata.number", 1)));
    }

    @Test
    void testJsonMapperLessEqual() {
        assertThat(mapper.mapJsonArgument("variables").apply(Map.of("workflowdata", Map.of("number", Map.of("lessThanEqual", 1))))).containsExactly(
                jsonFilter(lessThanEqual("variables.workflowdata.number", 1)));
    }

    @Test
    void testJsonMapperBetween() {
        assertThat(mapper.mapJsonArgument("variables").apply(Map.of("workflowdata", Map.of("number", Map.of("between", Map.of("from", 1, "to", 3)))))).containsExactly(
                jsonFilter(between("variables.workflowdata.number", 1, 3)));
    }

    @Test
    void testJsonMapperIn() {
        assertThat(mapper.mapJsonArgument("variables").apply(Map.of("workflowdata", Map.of("number", Map.of("in", List.of(1, 3)))))).containsExactly(
                jsonFilter(in("variables.workflowdata.number", Arrays.asList(1, 3))));
    }

    @Test
    void testJsonMapperContains() {
        assertThat(mapper.mapJsonArgument("variables").apply(Map.of("workflowdata", Map.of("number", Map.of("contains", 1))))).containsExactly(
                jsonFilter(contains("variables.workflowdata.number", 1)));
    }

    @Test
    void testJsonMapperContainsAny() {
        assertThat(mapper.mapJsonArgument("variables").apply(Map.of("workflowdata", Map.of("number", Map.of("containsAny", List.of(1, 2, 3)))))).containsExactly(
                jsonFilter(containsAny("variables.workflowdata.number", List.of(1, 2, 3))));
    }

    @Test
    void testJsonMapperContainsAll() {
        assertThat(mapper.mapJsonArgument("variables").apply(Map.of("workflowdata", Map.of("number", Map.of("containsAll", List.of(1, 2, 3)))))).containsExactly(
                jsonFilter(containsAll("variables.workflowdata.number", List.of(1, 2, 3))));
    }

    @Test
    void testJsonMapperLike() {
        assertThat(mapper.mapJsonArgument("variables").apply(Map.of("workflowdata", Map.of("number", Map.of("like", "kk"))))).containsExactly(
                jsonFilter(like("variables.workflowdata.number", "kk")));
    }

    @Test
    void testJsonMapperNull() {
        assertThat(mapper.mapJsonArgument("variables").apply(Map.of("workflowdata", Map.of("number", Map.of("isNull", true))))).containsExactly(
                jsonFilter(isNull("variables.workflowdata.number")));
    }

    @Test
    void testJsonMapperNotNull() {
        assertThat(mapper.mapJsonArgument("variables").apply(Map.of("workflowdata", Map.of("number", Map.of("isNull", false))))).containsExactly(
                jsonFilter(notNull("variables.workflowdata.number")));
    }

    // ========== Array Argument Tests ==========

    @Test
    void testArrayArgumentContains() {
        // Create the input type and parser
        GraphQLInputObjectType inputType = createProcessInstanceArgumentWithNodes();
        var parser = mapper.apply(inputType);

        // Test: nodes: { contains: { name: { equal: "StartProcess" } } }
        var filters = parser.apply(Map.of("nodes", Map.of("contains", Map.of("name", Map.of("equal", "StartProcess")))));

        assertThat(filters).hasSize(1);
        AttributeFilter<?> filter = filters.get(0);
        assertThat(filter.getAttribute()).isEqualTo("nodes.name");
        assertThat(filter.getCondition()).isEqualTo(FilterCondition.EQUAL);
        assertThat(filter.getValue()).isEqualTo("StartProcess");
    }

    @Test
    void testArrayArgumentContainsAll() {
        GraphQLInputObjectType inputType = createProcessInstanceArgumentWithNodes();
        var parser = mapper.apply(inputType);

        // Test: nodes: { containsAll: [{ name: { equal: "Start" } }, { name: { equal: "End" } }] }
        var filters = parser.apply(Map.of("nodes", Map.of("containsAll", List.of(
                Map.of("name", Map.of("equal", "Start")),
                Map.of("name", Map.of("equal", "End"))))));

        assertThat(filters).hasSize(1);
        AttributeFilter<?> filter = filters.get(0);
        assertThat(filter.getCondition()).isEqualTo(FilterCondition.AND);

        @SuppressWarnings("unchecked")
        List<AttributeFilter<?>> andFilters = (List<AttributeFilter<?>>) filter.getValue();
        assertThat(andFilters).hasSize(2);
        assertThat(andFilters.get(0).getAttribute()).isEqualTo("nodes.name");
        assertThat(andFilters.get(0).getValue()).isEqualTo("Start");
        assertThat(andFilters.get(1).getAttribute()).isEqualTo("nodes.name");
        assertThat(andFilters.get(1).getValue()).isEqualTo("End");
    }

    @Test
    void testArrayArgumentContainsAny() {
        GraphQLInputObjectType inputType = createProcessInstanceArgumentWithNodes();
        var parser = mapper.apply(inputType);

        // Test: nodes: { containsAny: [{ name: { equal: "Start" } }, { name: { equal: "End" } }] }
        var filters = parser.apply(Map.of("nodes", Map.of("containsAny", List.of(
                Map.of("name", Map.of("equal", "Start")),
                Map.of("name", Map.of("equal", "End"))))));

        assertThat(filters).hasSize(1);
        AttributeFilter<?> filter = filters.get(0);
        assertThat(filter.getCondition()).isEqualTo(FilterCondition.OR);

        @SuppressWarnings("unchecked")
        List<AttributeFilter<?>> orFilters = (List<AttributeFilter<?>>) filter.getValue();
        assertThat(orFilters).hasSize(2);
        assertThat(orFilters.get(0).getAttribute()).isEqualTo("nodes.name");
        assertThat(orFilters.get(1).getAttribute()).isEqualTo("nodes.name");
    }

    @Test
    void testArrayArgumentIsNull() {
        GraphQLInputObjectType inputType = createProcessInstanceArgumentWithNodes();
        var parser = mapper.apply(inputType);

        // Test: nodes: { isNull: true }
        var filters = parser.apply(Map.of("nodes", Map.of("isNull", true)));

        assertThat(filters).hasSize(1);
        AttributeFilter<?> filter = filters.get(0);
        assertThat(filter.getAttribute()).isEqualTo("nodes");
        assertThat(filter.getCondition()).isEqualTo(FilterCondition.IS_NULL);
    }

    @Test
    void testArrayArgumentIsNotNull() {
        GraphQLInputObjectType inputType = createProcessInstanceArgumentWithNodes();
        var parser = mapper.apply(inputType);

        // Test: nodes: { isNull: false }
        var filters = parser.apply(Map.of("nodes", Map.of("isNull", false)));

        assertThat(filters).hasSize(1);
        AttributeFilter<?> filter = filters.get(0);
        assertThat(filter.getAttribute()).isEqualTo("nodes");
        assertThat(filter.getCondition()).isEqualTo(FilterCondition.NOT_NULL);
    }

    @Test
    void testArrayArgumentWithNot() {
        GraphQLInputObjectType inputType = createProcessInstanceArgumentWithNodes();
        var parser = mapper.apply(inputType);

        // Test: nodes: { not: { contains: { name: { equal: "StartProcess" } } } }
        var filters = parser.apply(Map.of("nodes", Map.of("not", Map.of("contains", Map.of("name", Map.of("equal", "StartProcess"))))));

        assertThat(filters).hasSize(1);
        AttributeFilter<?> filter = filters.get(0);
        assertThat(filter.getCondition()).isEqualTo(FilterCondition.NOT);

        AttributeFilter<?> notFilter = (AttributeFilter<?>) filter.getValue();
        assertThat(notFilter.getAttribute()).isEqualTo("nodes.name");
        assertThat(notFilter.getValue()).isEqualTo("StartProcess");
    }

    @Test
    void testArrayArgumentWithNotContainsAll() {
        GraphQLInputObjectType inputType = createProcessInstanceArgumentWithNodes();
        var parser = mapper.apply(inputType);

        // Test: nodes: { not: { containsAll: [{ name: { equal: "Start" } }, { name: { equal: "End" } }] } }
        var filters = parser.apply(Map.of("nodes", Map.of("not", Map.of("containsAll", List.of(
                Map.of("name", Map.of("equal", "Start")),
                Map.of("name", Map.of("equal", "End")))))));

        assertThat(filters).hasSize(1);
        AttributeFilter<?> filter = filters.get(0);
        assertThat(filter.getCondition()).isEqualTo(FilterCondition.NOT);

        AttributeFilter<?> notFilter = (AttributeFilter<?>) filter.getValue();
        assertThat(notFilter.getCondition()).isEqualTo(FilterCondition.AND);
    }

    @Test
    void testArrayArgumentBackwardCompatibility() {
        GraphQLInputObjectType inputType = createProcessInstanceArgumentWithNodes();
        var parser = mapper.apply(inputType);

        // Test: nodes: { name: { equal: "StartProcess" } }
        // Should work like 'contains' for backward compatibility
        var filters = parser.apply(Map.of("nodes", Map.of("name", Map.of("equal", "StartProcess"))));

        assertThat(filters).hasSize(1);
        AttributeFilter<?> filter = filters.get(0);
        assertThat(filter.getAttribute()).isEqualTo("nodes.name");
        assertThat(filter.getCondition()).isEqualTo(FilterCondition.EQUAL);
        assertThat(filter.getValue()).isEqualTo("StartProcess");
    }

    @Test
    void testArrayArgumentWithAndOperator() {
        GraphQLInputObjectType inputType = createProcessInstanceArgumentWithNodes();
        var parser = mapper.apply(inputType);

        // Test: nodes: { and: [{ contains: { name: { equal: "Start" } } }, { contains: { type: { equal: "HumanTask" } } }] }
        var filters = parser.apply(Map.of("nodes", Map.of("and", List.of(
                Map.of("contains", Map.of("name", Map.of("equal", "Start"))),
                Map.of("contains", Map.of("type", Map.of("equal", "HumanTask")))))));

        assertThat(filters).hasSize(1);
        AttributeFilter<?> filter = filters.get(0);
        assertThat(filter.getCondition()).isEqualTo(FilterCondition.AND);

        @SuppressWarnings("unchecked")
        List<AttributeFilter<?>> andFilters = (List<AttributeFilter<?>>) filter.getValue();
        assertThat(andFilters).hasSize(2);
        assertThat(andFilters.get(0).getAttribute()).isEqualTo("nodes.name");
        assertThat(andFilters.get(1).getAttribute()).isEqualTo("nodes.type");
    }

    @Test
    void testArrayArgumentWithOrOperator() {
        GraphQLInputObjectType inputType = createProcessInstanceArgumentWithNodes();
        var parser = mapper.apply(inputType);

        // Test: nodes: { or: [{ contains: { name: { equal: "Start" } } }, { contains: { name: { equal: "End" } } }] }
        var filters = parser.apply(Map.of("nodes", Map.of("or", List.of(
                Map.of("contains", Map.of("name", Map.of("equal", "Start"))),
                Map.of("contains", Map.of("name", Map.of("equal", "End")))))));

        assertThat(filters).hasSize(1);
        AttributeFilter<?> filter = filters.get(0);
        assertThat(filter.getCondition()).isEqualTo(FilterCondition.OR);

        @SuppressWarnings("unchecked")
        List<AttributeFilter<?>> orFilters = (List<AttributeFilter<?>>) filter.getValue();
        assertThat(orFilters).hasSize(2);
        assertThat(orFilters.get(0).getAttribute()).isEqualTo("nodes.name");
        assertThat(orFilters.get(1).getAttribute()).isEqualTo("nodes.name");
    }

    @Test
    void testArrayArgumentComplexQuery() {
        GraphQLInputObjectType inputType = createProcessInstanceArgumentWithNodes();
        var parser = mapper.apply(inputType);

        // Test: nodes: { and: [{ contains: { name: { equal: "HumanTask" } } }, { not: { contains: { type: { equal: "EndEvent" } } } }] }
        var filters = parser.apply(Map.of("nodes", Map.of("and", List.of(
                Map.of("contains", Map.of("name", Map.of("equal", "HumanTask"))),
                Map.of("not", Map.of("contains", Map.of("type", Map.of("equal", "EndEvent"))))))));

        assertThat(filters).hasSize(1);
        AttributeFilter<?> filter = filters.get(0);
        assertThat(filter.getCondition()).isEqualTo(FilterCondition.AND);

        @SuppressWarnings("unchecked")
        List<AttributeFilter<?>> andFilters = (List<AttributeFilter<?>>) filter.getValue();
        assertThat(andFilters).hasSize(2);
        assertThat(andFilters.get(0).getAttribute()).isEqualTo("nodes.name");
        assertThat(andFilters.get(1).getCondition()).isEqualTo(FilterCondition.NOT);
    }

    // Helper method to create ProcessInstanceArgument with NodeInstanceArrayArgument
    private GraphQLInputObjectType createProcessInstanceArgumentWithNodes() {
        // Create StringArgument type
        GraphQLInputObjectType stringArgument = GraphQLInputObjectType.newInputObject()
                .name("StringArgument")
                .field(GraphQLInputObjectField.newInputObjectField()
                        .name("equal")
                        .type(Scalars.GraphQLString)
                        .build())
                .build();

        // Create NodeInstanceArgument type
        GraphQLInputObjectType nodeInstanceArgument = GraphQLInputObjectType.newInputObject()
                .name("NodeInstanceArgument")
                .field(GraphQLInputObjectField.newInputObjectField()
                        .name("name")
                        .type(stringArgument)
                        .build())
                .field(GraphQLInputObjectField.newInputObjectField()
                        .name("type")
                        .type(stringArgument)
                        .build())
                .build();

        // Create NodeInstanceArrayArgument type (note the "ArrayArgument" suffix)
        GraphQLInputObjectType nodeInstanceArrayArgument = GraphQLInputObjectType.newInputObject()
                .name("NodeInstanceArrayArgument")
                .field(GraphQLInputObjectField.newInputObjectField()
                        .name("contains")
                        .type(nodeInstanceArgument)
                        .build())
                .field(GraphQLInputObjectField.newInputObjectField()
                        .name("containsAll")
                        .type(new GraphQLList(nodeInstanceArgument))
                        .build())
                .field(GraphQLInputObjectField.newInputObjectField()
                        .name("containsAny")
                        .type(new GraphQLList(nodeInstanceArgument))
                        .build())
                .field(GraphQLInputObjectField.newInputObjectField()
                        .name("isNull")
                        .type(Scalars.GraphQLBoolean)
                        .build())
                .field(GraphQLInputObjectField.newInputObjectField()
                        .name("and")
                        .type(new GraphQLList(GraphQLInputObjectType.newInputObject()
                                .name("NodeInstanceArrayArgumentRecursive")
                                .build()))
                        .build())
                .field(GraphQLInputObjectField.newInputObjectField()
                        .name("or")
                        .type(new GraphQLList(GraphQLInputObjectType.newInputObject()
                                .name("NodeInstanceArrayArgumentRecursive2")
                                .build()))
                        .build())
                .field(GraphQLInputObjectField.newInputObjectField()
                        .name("not")
                        .type(GraphQLInputObjectType.newInputObject()
                                .name("NodeInstanceArrayArgumentRecursive3")
                                .build())
                        .build())
                .build();

        // Create ProcessInstanceArgument type with nodes field
        return GraphQLInputObjectType.newInputObject()
                .name("ProcessInstanceArgument")
                .field(GraphQLInputObjectField.newInputObjectField()
                        .name("nodes")
                        .type(nodeInstanceArrayArgument)
                        .build())
                .build();
    }
}
