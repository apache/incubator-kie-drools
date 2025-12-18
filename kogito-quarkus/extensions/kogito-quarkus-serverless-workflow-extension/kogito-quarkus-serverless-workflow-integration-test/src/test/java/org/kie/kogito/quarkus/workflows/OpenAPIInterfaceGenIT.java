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
package org.kie.kogito.quarkus.workflows;

import java.net.URL;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Handle integration tests for OpenAPI Spec Interface generation for SW projects.
 */
@QuarkusIntegrationTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OpenAPIInterfaceGenIT {
    @TestHTTPResource
    URL baseUrl;
    private JsonPath jp;

    @BeforeAll
    void init() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        String openapi = given()
                .baseUri(baseUrl.toString())
                .accept(ContentType.JSON)
                .when()
                .get("/q/openapi?format=json")
                .then()
                .statusCode(200)
                .extract()
                .asString();

        jp = new JsonPath(openapi);
    }

    @Test
    void verifyOperationIdIsGeneratedByDefault() {
        assertThat(jp.getString("paths.'/helloworld'.get.operationId"), is("getAllProcessInstances_helloworld"));
    }

    @ParameterizedTest
    @ValueSource(strings = { "helloworld", "expression", "greet", "long-call", "squareService" })
    void verifySchemaAndTagsForWorkflows(String processId) {
        Map<String, Object> schemas = jp.getMap("components.schemas");
        assertTrue(schemas.containsKey("Expression Input"));
        assertTrue(schemas.containsKey("Expression Output"));

        Map<String, Object> inputSchema = jp.getMap("components.schemas['Expression Input']");
        assertNotNull(inputSchema, "Input schema should exist for processId: " + processId);
        assertTrue(inputSchema.containsKey("type"), "Input schema should contain 'type' field for processId: " + processId);

        List<String> postTags = jp.getList("paths.'/" + processId + "'.post.tags");
        assertTrue(postTags.contains("Process - " + processId));

        Map<String, Object> getSchema = jp.getMap(
                "paths.'/" + processId + "'.post.responses.'201'.content.'application/json'.schema");

        assertNotNull(getSchema, "Schema must not be null");
    }
}
