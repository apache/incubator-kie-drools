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
package org.kie.kogito.trusty.service.common.messaging.incoming;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.json.JSONException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.kie.dmn.api.core.DMNDecisionResult.DecisionEvaluationStatus;
import org.kie.kogito.tracing.event.message.Message;
import org.kie.kogito.tracing.event.message.MessageCategory;
import org.kie.kogito.tracing.event.message.MessageLevel;
import org.kie.kogito.tracing.event.message.models.DecisionMessage;
import org.kie.kogito.tracing.event.trace.TraceEvent;
import org.kie.kogito.tracing.event.trace.TraceOutputValue;
import org.kie.kogito.trusty.service.common.TrustyServiceTestUtils;
import org.kie.kogito.trusty.storage.api.model.decision.Decision;
import org.skyscreamer.jsonassert.JSONAssert;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TraceEventConverterTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static void doTest(TraceEvent traceEvent, Decision expectedDecision) throws JsonProcessingException,
            JSONException {
        Decision actualDecision = TraceEventConverter.toDecision(traceEvent, TrustyServiceTestUtils.CLOUDEVENT_SOURCE, TrustyServiceTestUtils.CLOUDEVENT_SERVICE);
        JSONAssert.assertEquals(MAPPER.writeValueAsString(expectedDecision),
                MAPPER.writeValueAsString(actualDecision), false);
    }

    private static TraceOutputValue buildTraceOutputValue(DecisionEvaluationStatus status, boolean withErrorMessage) {
        String id = UUID.randomUUID().toString();
        List<Message> messages = withErrorMessage
                ? List.of(new DecisionMessage(MessageLevel.ERROR, MessageCategory.INTERNAL, "TEST", id, "Error " +
                        "message", null, null))
                : Collections.emptyList();
        return new TraceOutputValue(id, "Output", status.name(), null, null, messages);
    }

    @Test
    void testCorrectTraceEvent() throws JsonProcessingException, JSONException {
        doTest(TrustyServiceTestUtils.buildCorrectTraceEvent(TrustyServiceTestUtils.CORRECT_CLOUDEVENT_ID),
                TrustyServiceTestUtils.buildCorrectDecision(TrustyServiceTestUtils.CORRECT_CLOUDEVENT_ID));
    }

    @Test
    @Disabled("https://issues.redhat.com/browse/KOGITO-4318 - This test is broken by design, should be changed.")
    void testTraceEventWithError() throws JsonProcessingException, JSONException {
        doTest(TrustyServiceTestUtils.buildTraceEventWithErrors(), TrustyServiceTestUtils.buildDecisionWithErrors());
    }

    @Test
    void testTraceEventWithNullFields() throws JsonProcessingException, JSONException {
        doTest(TrustyServiceTestUtils.buildTraceEventWithNullFields(),
                TrustyServiceTestUtils.buildDecisionWithNullFields());
    }

    @Test
    void testDecisionHasSucceeded() {
        assertFalse(TraceEventConverter.decisionHasSucceeded(
                null), "Decision must be failed if input list is null");

        assertTrue(TraceEventConverter.decisionHasSucceeded(
                Collections.emptyList()), "Decision must be succeeeded if input list is empty");

        assertTrue(TraceEventConverter.decisionHasSucceeded(List.of(
                buildTraceOutputValue(DecisionEvaluationStatus.SUCCEEDED, false),
                buildTraceOutputValue(DecisionEvaluationStatus.SKIPPED, false),
                buildTraceOutputValue(DecisionEvaluationStatus.NOT_EVALUATED, false))), "Decision must be succeeded " +
                        "if there are no outputs with 'FAILED' status or containing error messages");

        assertFalse(TraceEventConverter.decisionHasSucceeded(List.of(
                buildTraceOutputValue(DecisionEvaluationStatus.SUCCEEDED, false),
                buildTraceOutputValue(DecisionEvaluationStatus.FAILED, false))), "Decision must be failed if at least" +
                        " one output has 'FAILED' status");

        assertFalse(TraceEventConverter.decisionHasSucceeded(List.of(
                buildTraceOutputValue(DecisionEvaluationStatus.SUCCEEDED, false),
                buildTraceOutputValue(DecisionEvaluationStatus.SKIPPED, true))), "Decision must be failed if at least" +
                        " one output contains error messages");

        assertFalse(TraceEventConverter.decisionHasSucceeded(List.of(
                buildTraceOutputValue(DecisionEvaluationStatus.SUCCEEDED, false),
                buildTraceOutputValue(DecisionEvaluationStatus.NOT_EVALUATED, true))), "Decision must be failed if at" +
                        " least one output contains error messages");

        assertFalse(TraceEventConverter.decisionHasSucceeded(List.of(
                buildTraceOutputValue(DecisionEvaluationStatus.SKIPPED, true),
                buildTraceOutputValue(DecisionEvaluationStatus.FAILED, false))), "Decision must be failed if at least" +
                        " one output has 'FAILED' status or contains error messages");

        assertFalse(TraceEventConverter.decisionHasSucceeded(List.of(
                buildTraceOutputValue(DecisionEvaluationStatus.NOT_EVALUATED, true),
                buildTraceOutputValue(DecisionEvaluationStatus.FAILED, true))), "Decision must be failed if at least " +
                        "one output has 'FAILED' status or contains error messages");
    }
}
