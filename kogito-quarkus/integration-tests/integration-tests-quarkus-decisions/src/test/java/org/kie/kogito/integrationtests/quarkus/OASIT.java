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
package org.kie.kogito.integrationtests.quarkus;

import java.net.URL;

import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.RestAssured;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.parser.core.models.ParseOptions;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.greaterThan;

@QuarkusIntegrationTest
class OASIT {

    static {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @TestHTTPResource("/")
    URL rootUrl;

    @Test
    public void testOASisValid() {
        String url = rootUrl.toString() + "/q/openapi"; // default location since Quarkus v1.10
        ParseOptions parseOptions = new ParseOptions();
        parseOptions.setResolve(true);
        SwaggerParseResult result = new OpenAPIV3Parser().readLocation(url, null, parseOptions);

        assertThat(result.getMessages()).isEmpty();

        OpenAPI openAPI = result.getOpenAPI();
        PathItem p1 = openAPI.getPaths().get("/basicAdd");
        assertThat(p1).isNotNull();
        assertThat(p1.getGet()).isNotNull();
        assertThat(p1.getPost()).isNotNull();
        PathItem p2 = openAPI.getPaths().get("/basicAdd/dmnresult");
        assertThat(p2).isNotNull();
        assertThat(p2.getPost()).isNotNull(); // only POST for ../dmnresult expected.
    }

    @Test
    public void testOASisSwaggerUICompatible() {
        String url = rootUrl.toString() + "/q/openapi"; // default location since Quarkus v1.10
        ParseOptions parseOptions = new ParseOptions();
        parseOptions.setResolve(false); // we want the actual OAS file (something ~like http://localhost:8080/q/openapi) as read by the Swagger UI on fetch.
        SwaggerParseResult result = new OpenAPIV3Parser().readLocation(url, null, parseOptions);
        assertThat(result.getMessages()).isEmpty();

        final String DMN_MODEL_NAME = "basicAdd";

        OpenAPI openAPI = result.getOpenAPI();
        PathItem p1 = openAPI.getPaths().get("/" + DMN_MODEL_NAME);
        assertThat(p1).isNotNull();
        assertThat(p1.getPost().getRequestBody().getContent().get("application/json").getSchema().get$ref()).startsWith("/basicAdd.json#");
        assertThat(p1.getPost().getResponses().getDefault().getContent().get("application/json").getSchema().get$ref()).startsWith("/basicAdd.json#");
    }

    @ParameterizedTest
    @ValueSource(strings = { "basicAdd",
            "DScoercion",
            "ElementAtIndex",
            "FaceMask",
            "Hospitals",
            "HospitalStatus",
            "java_function_context",
            "OneOfEachType",
            "StatusService" })
    public void testOASdmnDefinitions(String name) {
        RestAssured.given()
                .get("/" + name + ".json")
                .then()
                .statusCode(200)
                .body("definitions", aMapWithSize(greaterThan(0)));
    }
}
