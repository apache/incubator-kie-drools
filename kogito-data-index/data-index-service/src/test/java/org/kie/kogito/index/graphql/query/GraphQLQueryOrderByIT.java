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

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;

import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLInputObjectField;
import graphql.schema.GraphQLInputObjectType;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLType;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

import org.junit.jupiter.api.Test;
import org.kie.kogito.index.InfinispanServerTestResource;
import org.kie.kogito.index.graphql.GraphQLSchemaManager;
import org.kie.kogito.index.infinispan.protostream.ProtobufService;

import static io.restassured.RestAssured.given;
import static java.lang.String.format;
import static org.hamcrest.CoreMatchers.isA;
import static org.kie.kogito.index.TestUtils.getTravelsProtoBufferFile;

@QuarkusTest
@QuarkusTestResource(InfinispanServerTestResource.class)
public class GraphQLQueryOrderByIT {

    @Inject
    GraphQLSchemaManager manager;

    @Inject
    ProtobufService protobufService;

    @Test
    public void testProcessInstancesSort() {
        testSortBy("ProcessInstances");
    }

    @Test
    public void testProcessInstancesSortByMultipleFields() {
        given().contentType(ContentType.JSON).body("{ \"query\" : \"{ProcessInstances(orderBy: {start: ASC, processId: DESC}){ id } }\" }")
                .when().post("/graphql")
                .then().log().ifValidationFails().statusCode(200).body("data.ProcessInstances", isA(Collection.class));
    }

    @Test
    public void testUserTaskInstancesSort() {
        testSortBy("UserTaskInstances");
    }

    @Test
    public void testJobsSort() {
        testSortBy("Jobs");
    }

    @Test
    public void testTravelsSort() throws Exception {
        protobufService.registerProtoBufferType(getTravelsProtoBufferFile());

        testSortBy("Travels");
    }

    private void testSortBy(String root) {
        GraphQLObjectType queryType = manager.getGraphQLSchema().getQueryType();

        GraphQLFieldDefinition pi = queryType.getFieldDefinition(root);
        GraphQLArgument sortBy = pi.getArgument("orderBy");

        sortBy.getChildren().forEach(type -> {
            List<String> collect = type.getChildren().stream().flatMap(getAllTypes()).collect(Collectors.toList());
            collect.forEach(p -> {
                querySortBy(root, p, "ASC");
                querySortBy(root, p, "DESC");
            });
        });
    }

    private Function<GraphQLType, Stream<String>> getAllTypes() {
        return type -> type.getChildren().stream().flatMap(t -> {
            if (t instanceof GraphQLInputObjectType) {
                return getAllTypes().apply(t).map(s -> format("%s : { %s }", type.getName(), s));
            } else if (t instanceof GraphQLInputObjectField) {
                if (((GraphQLInputObjectField) t).getType() instanceof GraphQLInputObjectType) {
                    return getAllTypes().apply(((GraphQLInputObjectField) t).getType()).map(s -> format("%s : { %s }", t.getName(), s));
                }
                return Stream.of(format("%s : $sort", t.getName()));
            } else {
                return Stream.of(format("%s : $sort", type.getName()));
            }
        });
    }

    private void querySortBy(String root, String property, String sort) {
        String body = format("{ \"query\" : \"query ($sort: OrderBy) { %s( orderBy: { %s } ) { id } }\", \"variables\" : { \"sort\" : \"%s\" } }", root, property, sort);
        given().contentType(ContentType.JSON).body(body)
                .when().post("/graphql")
                .then().log().ifValidationFails().statusCode(200).body("data." + root, isA(Collection.class));
    }
}
