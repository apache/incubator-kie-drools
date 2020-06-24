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

import java.text.ParseException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.kie.kogito.trusty.service.ITrustyService;
import org.kie.kogito.trusty.service.responses.ExecutionsResponse;
import org.kie.kogito.trusty.storage.api.model.Execution;
import org.kie.kogito.trusty.storage.api.model.ExecutionTypeEnum;
import org.mockito.Mockito;

import static io.restassured.RestAssured.given;
import static org.mockito.ArgumentMatchers.any;

@QuarkusTest
class ExecutionsApiV1Test {

    @InjectMock
    ITrustyService executionService;

    @Test
    void givenRequestWithoutLimitAndOffsetParametersWhenExecutionEndpointIsCalledThenTheDefaultValuesAreCorrect() {
        Mockito.when(executionService.getExecutionHeaders(any(OffsetDateTime.class), any(OffsetDateTime.class), any(Integer.class), any(Integer.class), any(String.class))).thenReturn(new ArrayList<>());
        ExecutionsResponse response = given().contentType(ContentType.JSON).when().get("/v1/executions?from=2000-01-01T00:00:00Z&to=2021-01-01T00:00:00Z").as(ExecutionsResponse.class);

        Assertions.assertEquals(100, response.getLimit());
        Assertions.assertEquals(0, response.getOffset());
        Assertions.assertEquals(0, response.getHeaders().size());
    }

    @Test
    void givenARequestWithoutTimeRangeParametersWhenExecutionEndpointIsCalledThenTheDefaultValuesAreUsed() {
        given().when().get("/v1/executions").then().statusCode(200);
        given().when().get("/v1/executions?from=2000-01-01T00:00:00Z").then().statusCode(200);
        given().when().get("/v1/executions?to=2000-01-01T00:00:00Z").then().statusCode(200);
    }

    @Test
    void givenARequestWithoutTimeZoneInformationWhenExecutionEndpointIsCalledThenBadRequestIsReturned() {
        given().when().get("/v1/executions?to=2000-01-01T00:00:00&from=2000-01-01T00:00:00Z").then().statusCode(400);
    }

    @Test
    void givenARequestWithInvalidPaginationParametersWhenExecutionEndpointIsCalledThenBadRequestIsReturned() {
        given().when().get("/v1/executions?from=2000-01-01T00:00:00Z&to=2021-01-01T00:00:00Z&offset=-1").then().statusCode(400);
        given().when().get("/v1/executions?from=2000-01-01T00:00:00Z&to=2021-01-01T00:00:00Z&limit=-1").then().statusCode(400);
    }

    @Test
    void givenARequestWitMalformedTimeRangeWhenExecutionEndpointIsCalledThenBadRequestIsReturned() {
        given().contentType(ContentType.JSON).when().get("/v1/executions?from=2000-01-01&to=2021-01-01T00:00:00Z").then().statusCode(400);
        given().contentType(ContentType.JSON).when().get("/v1/executions?from=2000-01-01T00:00:00Z&to=2021-01-01").then().statusCode(400);
        given().contentType(ContentType.JSON).when().get("/v1/executions?from=2000-01-01T00:00:00&to=2021-01-01T00:00:00Z").then().statusCode(400);
        given().contentType(ContentType.JSON).when().get("/v1/executions?from=2000-01-01T00:00:00Z&to=2021-01-01T00:00:00").then().statusCode(400);
    }

    @Test
    void givenARequestWhenExecutionEndpointIsCalledThenTheExecutionHeaderIsReturned() throws ParseException {
        Execution execution = new Execution("test1",
                                            OffsetDateTime.parse("2020-01-01T00:00:00Z", DateTimeFormatter.ISO_OFFSET_DATE_TIME).toInstant().toEpochMilli(),
                                            true, "name", "model", ExecutionTypeEnum.DECISION);
        Mockito.when(executionService.getExecutionHeaders(any(OffsetDateTime.class), any(OffsetDateTime.class), any(Integer.class), any(Integer.class), any(String.class))).thenReturn(List.of(execution));

        ExecutionsResponse response = given().contentType(ContentType.JSON).when().get("/v1/executions?from=2000-01-01T00:00:00Z&to=2021-01-01T00:00:00Z").as(ExecutionsResponse.class);

        Assertions.assertEquals(1, response.getHeaders().size());
    }
}