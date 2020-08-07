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

package org.kie.kogito.trusty.service.api;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.kie.kogito.trusty.service.TrustyService;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@QuarkusTest
public class ModelsApiV1Test {

    private static final String MODEL_DEFINITION = "definition";

    @InjectMock
    TrustyService trustyService;

    @Test
    void testGetModelById() {
        assertGetModelByIdCorrectResponse();
        assertBadRequestWithoutModel();
    }

    private void assertGetModelByIdCorrectResponse() {
        mockServiceWithModel();
        final Response response = get();
        final String definition = response.getBody().print();
        assertEquals(MODEL_DEFINITION, definition);
    }

    private void assertBadRequestWithoutModel() {
        mockServiceWithoutDecision();
        get().then().statusCode(400);
    }

    private Response get() {
        return given()
                .filter(new ResponseLoggingFilter())
                .contentType(ContentType.TEXT)
                .when()
                .get("/v1/models/name:namespace");
    }

    private void mockServiceWithModel() {
        when(trustyService.getModelById(anyString())).thenReturn(MODEL_DEFINITION);
    }

    private void mockServiceWithoutDecision() {
        when(trustyService.getModelById(anyString())).thenThrow(new IllegalArgumentException("Model does not exist."));
    }
}
