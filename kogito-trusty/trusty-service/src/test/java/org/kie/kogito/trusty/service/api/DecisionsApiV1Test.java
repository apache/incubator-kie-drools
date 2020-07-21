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

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.kie.kogito.tracing.decision.event.common.MessageLevel;
import org.kie.kogito.trusty.service.ITrustyService;
import org.kie.kogito.trusty.service.responses.DecisionOutcomeResponse;
import org.kie.kogito.trusty.service.responses.DecisionOutcomesResponse;
import org.kie.kogito.trusty.service.responses.DecisionStructuredInputsResponse;
import org.kie.kogito.trusty.service.responses.ExecutionHeaderResponse;
import org.kie.kogito.trusty.storage.api.model.Decision;
import org.kie.kogito.trusty.storage.api.model.DecisionOutcome;
import org.kie.kogito.trusty.storage.api.model.ExecutionTypeEnum;
import org.kie.kogito.trusty.storage.api.model.Message;
import org.kie.kogito.trusty.storage.api.model.MessageExceptionField;
import org.kie.kogito.trusty.storage.api.model.TypedValue;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@QuarkusTest
public class DecisionsApiV1Test {

    private static final String TEST_EXECUTION_ID = "executionId";
    private static final String TEST_OUTCOME_ID = "FirstOutcome";

    @InjectMock
    ITrustyService executionService;

    @Test
    void testGetExecutionById() throws Exception {
        assertGetExecutionByIdCorrectResponse(ListStatus.FULL, ListStatus.FULL);
        assertGetExecutionByIdCorrectResponse(ListStatus.FULL, ListStatus.EMPTY);
        assertGetExecutionByIdCorrectResponse(ListStatus.FULL, ListStatus.NULL);
        assertGetExecutionByIdCorrectResponse(ListStatus.EMPTY, ListStatus.FULL);
        assertGetExecutionByIdCorrectResponse(ListStatus.EMPTY, ListStatus.EMPTY);
        assertGetExecutionByIdCorrectResponse(ListStatus.EMPTY, ListStatus.NULL);
        assertGetExecutionByIdCorrectResponse(ListStatus.NULL, ListStatus.FULL);
        assertGetExecutionByIdCorrectResponse(ListStatus.NULL, ListStatus.EMPTY);
        assertGetExecutionByIdCorrectResponse(ListStatus.NULL, ListStatus.NULL);
        assertBadRequestWithoutDecision("");
    }

    @Test
    void testGetStructuredInputs() throws Exception {
        assertGetStructuredInputsCorrectFullResponse(ListStatus.FULL, ListStatus.FULL);
        assertGetStructuredInputsCorrectFullResponse(ListStatus.FULL, ListStatus.EMPTY);
        assertGetStructuredInputsCorrectFullResponse(ListStatus.FULL, ListStatus.NULL);
        assertGetStructuredInputsCorrectEmptyResponse(ListStatus.EMPTY, ListStatus.FULL);
        assertGetStructuredInputsCorrectEmptyResponse(ListStatus.EMPTY, ListStatus.EMPTY);
        assertGetStructuredInputsCorrectEmptyResponse(ListStatus.EMPTY, ListStatus.NULL);
        assertBadRequestWithDecision("/structuredInputs", ListStatus.NULL, ListStatus.FULL);
        assertBadRequestWithDecision("/structuredInputs", ListStatus.NULL, ListStatus.EMPTY);
        assertBadRequestWithDecision("/structuredInputs", ListStatus.NULL, ListStatus.NULL);
        assertBadRequestWithoutDecision("/structuredInputs");
    }

    @Test
    void testGetOutcomes() throws Exception {
        assertGetOutcomesCorrectFullResponse(ListStatus.FULL, ListStatus.FULL);
        assertGetOutcomesCorrectEmptyResponse(ListStatus.FULL, ListStatus.EMPTY);
        assertGetOutcomesCorrectNullResponse(ListStatus.FULL, ListStatus.NULL);
        assertGetOutcomesCorrectFullResponse(ListStatus.EMPTY, ListStatus.FULL);
        assertGetOutcomesCorrectEmptyResponse(ListStatus.EMPTY, ListStatus.EMPTY);
        assertGetOutcomesCorrectNullResponse(ListStatus.EMPTY, ListStatus.NULL);
        assertGetOutcomesCorrectFullResponse(ListStatus.NULL, ListStatus.FULL);
        assertGetOutcomesCorrectEmptyResponse(ListStatus.NULL, ListStatus.EMPTY);
        assertGetOutcomesCorrectNullResponse(ListStatus.NULL, ListStatus.NULL);
        assertBadRequestWithoutDecision("/structuredInputs");
    }

    @Test
    void testGetOutcomeById() throws Exception {
        assertGetOutcomeByIdCorrectResponse(ListStatus.FULL, ListStatus.FULL);
        assertBadRequestWithDecision("/outcomes/" + TEST_OUTCOME_ID, ListStatus.FULL, ListStatus.EMPTY);
        assertBadRequestWithDecision("/outcomes/" + TEST_OUTCOME_ID, ListStatus.FULL, ListStatus.NULL);
        assertGetOutcomeByIdCorrectResponse(ListStatus.EMPTY, ListStatus.FULL);
        assertBadRequestWithDecision("/outcomes/" + TEST_OUTCOME_ID, ListStatus.EMPTY, ListStatus.EMPTY);
        assertBadRequestWithDecision("/outcomes/" + TEST_OUTCOME_ID, ListStatus.EMPTY, ListStatus.NULL);
        assertGetOutcomeByIdCorrectResponse(ListStatus.NULL, ListStatus.FULL);
        assertBadRequestWithDecision("/outcomes/" + TEST_OUTCOME_ID, ListStatus.NULL, ListStatus.EMPTY);
        assertBadRequestWithDecision("/outcomes/" + TEST_OUTCOME_ID, ListStatus.NULL, ListStatus.NULL);
        assertBadRequestWithoutDecision("/structuredInputs");
    }

    private void assertBadRequestWithDecision(String path, ListStatus inputsStatus, ListStatus outcomesStatus) throws Exception {
        mockServiceWithDecision(inputsStatus, outcomesStatus);
        get(path).then().statusCode(400);
    }

    private void assertBadRequestWithoutDecision(String path) {
        mockServiceWithoutDecision();
        get(path).then().statusCode(400);
    }

    private void assertGetExecutionByIdCorrectResponse(ListStatus inputsStatus, ListStatus outcomesStatus) throws Exception {
        mockServiceWithDecision(inputsStatus, outcomesStatus);
        ExecutionHeaderResponse response = get().as(ExecutionHeaderResponse.class);
        assertEquals(TEST_EXECUTION_ID, response.getExecutionId());
        assertTrue(response.hasSucceeded());
    }

    private void assertGetOutcomeByIdCorrectResponse(ListStatus inputsStatus, ListStatus outcomesStatus) throws Exception {
        mockServiceWithDecision(inputsStatus, outcomesStatus);
        DecisionOutcomeResponse response = get("/outcomes/" + TEST_OUTCOME_ID).as(DecisionOutcomeResponse.class);
        assertEquals(TEST_OUTCOME_ID, response.getOutcomeId());
    }

    private void assertGetOutcomesCorrectEmptyResponse(ListStatus inputsStatus, ListStatus outcomesStatus) throws Exception {
        mockServiceWithDecision(inputsStatus, outcomesStatus);
        DecisionOutcomesResponse response = get("/outcomes").as(DecisionOutcomesResponse.class);
        assertSame(0, response.getOutcomes().size());
    }

    private void assertGetOutcomesCorrectFullResponse(ListStatus inputsStatus, ListStatus outcomesStatus) throws Exception {
        mockServiceWithDecision(inputsStatus, outcomesStatus);
        DecisionOutcomesResponse response = get("/outcomes").as(DecisionOutcomesResponse.class);
        assertSame(1, response.getOutcomes().size());
        assertTrue(response.getOutcomes().stream().anyMatch(o -> "ONE".equals(o.getOutcomeName())));
    }

    private void assertGetOutcomesCorrectNullResponse(ListStatus inputsStatus, ListStatus outcomesStatus) throws Exception {
        mockServiceWithDecision(inputsStatus, outcomesStatus);
        DecisionOutcomesResponse response = get("/outcomes").as(DecisionOutcomesResponse.class);
        assertNull(response.getOutcomes());
    }

    private void assertGetStructuredInputsCorrectEmptyResponse(ListStatus inputsStatus, ListStatus outcomesStatus) throws Exception {
        mockServiceWithDecision(inputsStatus, outcomesStatus);
        DecisionStructuredInputsResponse response = get("/structuredInputs").as(DecisionStructuredInputsResponse.class);
        assertSame(0, response.getInputs().size());
    }

    private void assertGetStructuredInputsCorrectFullResponse(ListStatus inputsStatus, ListStatus outcomesStatus) throws Exception {
        mockServiceWithDecision(inputsStatus, outcomesStatus);
        DecisionStructuredInputsResponse response = get("/structuredInputs").as(DecisionStructuredInputsResponse.class);
        assertSame(2, response.getInputs().size());
        assertTrue(response.getInputs().stream().anyMatch(i -> "first".equals(i.getName())));
        assertTrue(response.getInputs().stream().anyMatch(i -> "second".equals(i.getName())));
    }

    private Decision buildValidDecision(ListStatus inputsStatus, ListStatus outcomesStatus) throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        Decision decision = new Decision();
        decision.setExecutionId(TEST_EXECUTION_ID);
        decision.setExecutionTimestamp(1591692950000L);
        decision.setExecutionType(ExecutionTypeEnum.DECISION);
        decision.setExecutedModelName("testModel");
        decision.setSuccess(true);

        switch (inputsStatus) {
            case EMPTY:
                decision.setInputs(List.of());
                break;

            case FULL:
                decision.setInputs(List.of(
                        new TypedValue("first", "FirstInput", mapper.readTree("\"Hello\"")),
                        new TypedValue("second", "SecondInput", mapper.readTree("12345"))
                ));
        }

        switch (outcomesStatus) {
            case EMPTY:
                decision.setOutcomes(List.of());
                break;

            case FULL:
                decision.setOutcomes(List.of(
                        new DecisionOutcome(
                                TEST_OUTCOME_ID, "ONE", "SUCCEEDED",
                                new TypedValue("result", "ResType", mapper.readTree("\"The First Outcome\"")),
                                List.of(),
                                List.of(new Message(
                                        MessageLevel.WARNING, "INTERNAL", "TEST", "testSrc", "Test message",
                                        new MessageExceptionField("TestException", "Test exception message",
                                                new MessageExceptionField("TestExceptionCause", "Test exception cause message", null)
                                        )
                                ))
                        )
                ));
        }

        return decision;
    }

    private Response get() {
        return get("");
    }

    private Response get(String endpoint) {
        return given()
                .filter(new ResponseLoggingFilter())
                .contentType(ContentType.JSON)
                .when()
                .get("/v1/executions/decisions/" + TEST_EXECUTION_ID + endpoint);
    }

    private void mockServiceWithDecision(ListStatus inputsStatus, ListStatus outcomesStatus) throws Exception {
        when(executionService.getDecisionById(eq(TEST_EXECUTION_ID))).thenReturn(buildValidDecision(inputsStatus, outcomesStatus));
    }

    private void mockServiceWithoutDecision() {
        when(executionService.getDecisionById(anyString())).thenThrow(new IllegalArgumentException("Execution does not exist."));
    }

    private enum ListStatus {
        FULL,
        EMPTY,
        NULL
    }
}
