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
package org.kie.kogito.quarkus;

import java.net.URL;

import org.junit.jupiter.api.Test;

import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.http.ContentType;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.parser.core.models.ParseOptions;
import io.swagger.v3.parser.core.models.SwaggerParseResult;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

@QuarkusIntegrationTest
public class EndpointsIT {

    @TestHTTPResource("/")
    URL rootUrl;

    @Test
    public void endpointTest() {
        String url = rootUrl.toString() + "/q/openapi";
        ParseOptions parseOptions = new ParseOptions();
        parseOptions.setResolve(true);
        SwaggerParseResult result = new OpenAPIV3Parser().readLocation(url, null, parseOptions);

        assertThat(result.getMessages()).isEmpty();

        OpenAPI openAPI = result.getOpenAPI();
        assertThat(openAPI.getPaths()).isNotEmpty();
    }

    @Test
    public void testGeneratedRestEndpoint() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .body("{\"p\": {\"name\": \"Paul\"}}")
                .post("/dmnModel")
                .then()
                .statusCode(200)
                .body("d.Hello", not(emptyOrNullString()));
    }

    @Test
    public void testGeneratedResource() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/refs/a.json")
                .then()
                .statusCode(200)
                .body("definitions.tAddress.type", is("object"));

    }

}
