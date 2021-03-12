/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.explainability.rest;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.kie.kogito.explainability.api.ExplainabilityRequestDto;
import org.kie.kogito.explainability.api.ExplainabilityResultDto;
import org.kie.kogito.explainability.api.ModelIdentifierDto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
class ExplainabilityApiV1Test {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String executionId = "test";
    private static final String serviceUrl = "http://localhost:8080";

    @Test
    void testEndpointWithRequest() throws JsonProcessingException {
        ModelIdentifierDto modelIdentifierDto = new ModelIdentifierDto("dmn", "namespace:name");

        String body = MAPPER.writeValueAsString(new ExplainabilityRequestDto(executionId, serviceUrl, modelIdentifierDto, Collections.emptyMap(), Collections.emptyMap()));

        ExplainabilityResultDto result = given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/v1/explain")
                .as(ExplainabilityResultDto.class);

        assertEquals(executionId, result.getExecutionId());
    }

    @Test
    void testEndpointWithBadRequests() throws JsonProcessingException {
        ExplainabilityRequestDto[] badRequests = new ExplainabilityRequestDto[] {
                new ExplainabilityRequestDto(null, serviceUrl, new ModelIdentifierDto("test", "test"), Collections.emptyMap(), Collections.emptyMap()),
                new ExplainabilityRequestDto(executionId, serviceUrl, new ModelIdentifierDto("", "test"), Collections.emptyMap(), Collections.emptyMap()),
                new ExplainabilityRequestDto(executionId, serviceUrl, new ModelIdentifierDto("test", ""), Collections.emptyMap(), Collections.emptyMap()),
                new ExplainabilityRequestDto(executionId, serviceUrl, null, Collections.emptyMap(), Collections.emptyMap()),
                new ExplainabilityRequestDto(executionId, "", new ModelIdentifierDto("test", "test"), Collections.emptyMap(), Collections.emptyMap()),
                new ExplainabilityRequestDto(executionId, null, new ModelIdentifierDto("test", "test"), Collections.emptyMap(), Collections.emptyMap()),
                new ExplainabilityRequestDto(null, null, null, Collections.emptyMap(), Collections.emptyMap()),
        };

        for (int i = 0; i < badRequests.length; i++) {
            String body = MAPPER.writeValueAsString(badRequests[i]);

            given()
                    .contentType(ContentType.JSON)
                    .body(body)
                    .when()
                    .post("/v1/explain")
                    .then()
                    .statusCode(400);
        }
    }
}
