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
import org.kie.kogito.index.test.quarkus.http.DataIndexPostgreSqlHttpQuarkusTestResource;
import org.kie.kogito.test.quarkus.QuarkusTestProperty;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.ResourceArg;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.kie.kogito.index.test.Constants.KOGITO_DATA_INDEX_SERVICE_URL;
import static org.kie.kogito.index.test.quarkus.http.DataIndexPostgreSqlHttpQuarkusTestResource.DATA_INDEX_MIGRATE_DB;

@QuarkusIntegrationTest
@QuarkusTestResource(value = DataIndexPostgreSqlHttpQuarkusTestResource.class, initArgs = { @ResourceArg(name = DATA_INDEX_MIGRATE_DB, value = "false") })
class PostgreSQLQuarkusAddonDataIndexPersistenceIT {

    static {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @QuarkusTestProperty(name = KOGITO_DATA_INDEX_SERVICE_URL)
    String dataIndex;

    @Test
    void testDataIndexAddon() {
        String source = given().contentType(ContentType.JSON)
                .baseUri(dataIndex)
                .body("{ \"query\" : \"{ ProcessDefinitions{ id, name, version, endpoint, addons, source, nodes { id, name, type, uniqueId, metadata { UniqueId } } } }\" }")
                .when().post("/graphql")
                .then().log().ifValidationFails().statusCode(200)
                .body("data.ProcessDefinitions[0].id", is("greet"))
                .body("data.ProcessDefinitions[0].name", is("Greeting workflow"))
                .body("data.ProcessDefinitions[0].version", is("1.0"))
                .body("data.ProcessDefinitions[0].endpoint", is("http://localhost:8080/greet"))
                .body("data.ProcessDefinitions[0].addons", hasItem("jdbc-persistence"))
                .body("data.ProcessDefinitions[0].source", is(not(emptyOrNullString())))
                .body("data.ProcessDefinitions[0].nodes.size()", is(12))
                .body("data.ProcessDefinitions[0].nodes[0].id", is("1"))
                .body("data.ProcessDefinitions[0].nodes[0].name", is("Start"))
                .body("data.ProcessDefinitions[0].nodes[0].type", is("StartNode"))
                .body("data.ProcessDefinitions[0].nodes[0].uniqueId", is("1"))
                .body("data.ProcessDefinitions[0].nodes[0].metadata.UniqueId", is("_jbpm-unique-0"))
                .extract().path("data.ProcessDefinitions[0].source");

        assertThat(JsonPath.from(source).getString("id")).isEqualTo("greet");
        assertThat(JsonPath.from(source).getString("version")).isEqualTo("1.0");

        given().contentType(ContentType.JSON)
                .baseUri(dataIndex)
                .body("{ \"query\" : \"{ProcessInstances{ id } }\" }")
                .when().post("/graphql")
                .then().statusCode(200)
                .body("data.ProcessInstances.size()", is(greaterThanOrEqualTo(0)));

        String processInstanceId = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body("{\"workflowdata\" : {\"name\" : \"John\", \"language\":\"English\"}}").when()
                .post("/greet")
                .then()
                .statusCode(201)
                .body("workflowdata.greeting", is("Hello from JSON Workflow,"))
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
