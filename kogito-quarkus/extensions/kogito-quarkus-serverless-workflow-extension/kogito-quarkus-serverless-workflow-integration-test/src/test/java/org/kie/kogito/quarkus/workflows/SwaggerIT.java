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

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.http.ContentType;
import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@QuarkusIntegrationTest
class SwaggerIT {

    private static final Logger logger = LoggerFactory.getLogger(SwaggerIT.class);

    @Test
    void testSwagger() {
        String response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .get("/q/openapi")
                .then()
                .statusCode(200).extract().asString();
        logger.trace("Open API body {}", response);
        OpenAPI openAPI = new OpenAPIParser().readContents(response, null, null).getOpenAPI();
        Set<String> tags = new HashSet<>();

        for (PathItem path : openAPI.getPaths().values()) {
            checkOperation(path.getPost(), tags);
            checkOperation(path.getGet(), tags);
            checkOperation(path.getPatch(), tags);
            checkOperation(path.getDelete(), tags);
            checkOperation(path.getPut(), tags);
        }
        logger.debug("Tags collected {}", tags);
        assertThat(tags).contains("Process - expression");

    }

    private void checkOperation(Operation operation, Set<String> tags) {
        if (operation != null && operation.getTags() != null) {
            tags.addAll(operation.getTags());
        }
    }
}
