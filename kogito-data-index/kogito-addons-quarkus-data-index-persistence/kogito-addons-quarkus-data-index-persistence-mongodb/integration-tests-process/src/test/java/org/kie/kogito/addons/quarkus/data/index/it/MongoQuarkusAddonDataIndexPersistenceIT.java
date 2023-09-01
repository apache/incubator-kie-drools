/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.addons.quarkus.data.index.it;

import org.junit.jupiter.api.Test;
import org.kie.kogito.index.test.quarkus.http.DataIndexMongoDBHttpQuarkusTestResource;
import org.kie.kogito.test.quarkus.QuarkusTestProperty;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.kie.kogito.index.test.Constants.KOGITO_DATA_INDEX_SERVICE_URL;

@QuarkusIntegrationTest
@QuarkusTestResource(DataIndexMongoDBHttpQuarkusTestResource.class)
class MongoQuarkusAddonDataIndexPersistenceIT {

    static {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @QuarkusTestProperty(name = KOGITO_DATA_INDEX_SERVICE_URL)
    String dataIndex;

    @Test
    void testDataIndexAddon() {
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
