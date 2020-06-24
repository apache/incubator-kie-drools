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
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.kie.kogito.trusty.service.ITrustyService;
import org.kie.kogito.trusty.service.responses.ExecutionHeaderResponse;
import org.kie.kogito.trusty.storage.api.model.Decision;
import org.mockito.Mockito;

import static io.restassured.RestAssured.given;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@QuarkusTest
public class DecisionsApiV1Test {

    @InjectMock
    ITrustyService executionService;

    @Test
    void givenAValidRequestWhenExecutionEndpointIsCalledThenTheDefaultValuesAreCorrect() {
        Decision decision = new Decision();
        decision.setExecutionId("executionId");
        decision.setExecutionTimestamp(1591692950000L);
        when(executionService.getDecisionById(eq("executionId"))).thenReturn(decision);

        ExecutionHeaderResponse response = given().contentType(ContentType.JSON).when().get("/v1/executions/decisions/executionId").as(ExecutionHeaderResponse.class);

        Assertions.assertEquals("executionId", response.getExecutionId());
    }

    @Test
    void givenAnInvalidRequestWhenExecutionEndpointIsCalledDThenBadRequestIsReturned() {
        when(executionService.getDecisionById(eq("executionId"))).thenThrow(new IllegalArgumentException("Execution does not exist."));

        given().contentType(ContentType.JSON).when().get("/v1/executions/decisions/executionId").then().statusCode(400);
    }
}
