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
package org.kie.kogito.addons.quarkus.data.index.it;

import org.junit.jupiter.api.Test;
import org.kie.kogito.index.test.quarkus.http.DataIndexInfinispanHttpQuarkusTestResource;
import org.kie.kogito.index.test.quarkus.http.KogitoServiceRandomPortQuarkusHttpTestResource;
import org.kie.kogito.test.quarkus.QuarkusTestProperty;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.kie.kogito.index.test.Constants.KOGITO_DATA_INDEX_SERVICE_URL;

@QuarkusIntegrationTest
@QuarkusTestResource(DataIndexInfinispanHttpQuarkusTestResource.class)
@QuarkusTestResource(value = KogitoServiceRandomPortQuarkusHttpTestResource.class)
class InfinispanQuarkusAddonDataIndexPersistenceIT {

    static {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @QuarkusTestProperty(name = KOGITO_DATA_INDEX_SERVICE_URL)
    String dataIndex;

    @Test
    void testDataIndexAddon() {
        given().contentType(ContentType.JSON)
                .baseUri(dataIndex)
                .body("{ \"query\" : \"{ ProcessDefinitions{ id, name, version, endpoint, addons, source, nodes { id, name, type, uniqueId, metadata { UniqueId } } } }\" }")
                .when().post("/graphql")
                .then().log().ifValidationFails().statusCode(200)
                .body("data.ProcessDefinitions[0].id", is("hello"))
                .body("data.ProcessDefinitions[0].name", is("hello"))
                .body("data.ProcessDefinitions[0].version", is("1.0"))
                .body("data.ProcessDefinitions[0].endpoint", is(not(emptyOrNullString())))
                .body("data.ProcessDefinitions[0].addons", hasItem("infinispan-persistence"))
                .body("data.ProcessDefinitions[0].source", is(not(emptyOrNullString())))
                .body("data.ProcessDefinitions[0].nodes.size()", is(2))
                .body("data.ProcessDefinitions[0].nodes[0].id", is("1"))
                .body("data.ProcessDefinitions[0].nodes[0].name", is("End"))
                .body("data.ProcessDefinitions[0].nodes[0].type", is("EndNode"))
                .body("data.ProcessDefinitions[0].nodes[0].uniqueId", is("1"))
                .body("data.ProcessDefinitions[0].nodes[0].metadata.UniqueId", is("_B3241ACF-97BE-443B-A49F-964AB3DD006C"));

        given().contentType(ContentType.JSON)
                .baseUri(dataIndex)
                .body("{ \"query\" : \"{ProcessInstances{ id } }\" }")
                .when().post("/graphql")
                .then().statusCode(200)
                .body("data.ProcessInstances.size()", is(greaterThanOrEqualTo(0)));

        String processInstanceId = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .post("/hello")
                .then()
                .statusCode(201)
                .body("id", is(notNullValue()))
                .extract().path("id");

        given().contentType(ContentType.JSON)
                .baseUri(dataIndex)
                .body("{ \"query\" : \"{ProcessInstances(where: { id: {equal: \\\"" + processInstanceId + "\\\"}}){ id, state } }\" }")
                .when().post("/graphql")
                .then().statusCode(200)
                .body("data.ProcessInstances.size()", is(1))
                .body("data.ProcessInstances[0].id", is(processInstanceId))
                .body("data.ProcessInstances[0].state", is("COMPLETED"));
    }

    @Test
    void testGraphQL() {
        given().contentType(ContentType.HTML)
                .when().get("/graphql")
                .then().statusCode(404);
    }

    @Test
    void testGraphQLUI() {
        given().contentType(ContentType.HTML)
                .when().get("/q/graphql-ui/")
                .then().statusCode(404);
    }

}
