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
package org.kie.kogito.trusty.service.messaging.incoming;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.kie.dmn.api.core.DMNDecisionResult.DecisionEvaluationStatus;
import org.kie.kogito.tracing.decision.event.message.Message;
import org.kie.kogito.tracing.decision.event.message.MessageCategory;
import org.kie.kogito.tracing.decision.event.message.MessageLevel;
import org.kie.kogito.tracing.decision.event.trace.TraceEvent;
import org.kie.kogito.tracing.decision.event.trace.TraceOutputValue;
import org.kie.kogito.trusty.storage.api.model.Decision;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.kie.kogito.trusty.service.TrustyServiceTestUtils.CLOUDEVENT_SOURCE;
import static org.kie.kogito.trusty.service.TrustyServiceTestUtils.CORRECT_CLOUDEVENT_ID;
import static org.kie.kogito.trusty.service.TrustyServiceTestUtils.buildCorrectDecision;
import static org.kie.kogito.trusty.service.TrustyServiceTestUtils.buildCorrectTraceEvent;
import static org.kie.kogito.trusty.service.TrustyServiceTestUtils.buildDecisionWithErrors;
import static org.kie.kogito.trusty.service.TrustyServiceTestUtils.buildDecisionWithNullFields;
import static org.kie.kogito.trusty.service.TrustyServiceTestUtils.buildTraceEventWithErrors;
import static org.kie.kogito.trusty.service.TrustyServiceTestUtils.buildTraceEventWithNullFields;

class TraceEventConverterTest {

    private static void doTest(TraceEvent traceEvent, Decision expectedDecision) {
        Decision actualDecision = TraceEventConverter.toDecision(traceEvent, CLOUDEVENT_SOURCE);
        TraceEventTestUtils.assertDecision(expectedDecision, actualDecision);
    }

    @Test
    void testCorrectTraceEvent() {
        doTest(buildCorrectTraceEvent(CORRECT_CLOUDEVENT_ID), buildCorrectDecision(CORRECT_CLOUDEVENT_ID));
    }

    @Test
    void testTraceEventWithError() {
        doTest(buildTraceEventWithErrors(), buildDecisionWithErrors());
    }

    @Test
    void testTraceEventWithNullFields() {
        doTest(buildTraceEventWithNullFields(), buildDecisionWithNullFields());
    }

    @Test
    void testDecisionHasSucceeded() {
        assertFalse(TraceEventConverter.decisionHasSucceeded(
                null
        ), "Decision must be failed if input list is null");

        assertTrue(TraceEventConverter.decisionHasSucceeded(
                Collections.emptyList()
        ), "Decision must be succeeeded if input list is empty");

        assertTrue(TraceEventConverter.decisionHasSucceeded(List.of(
                buildTraceOutputValue(DecisionEvaluationStatus.SUCCEEDED, false),
                buildTraceOutputValue(DecisionEvaluationStatus.SKIPPED, false),
                buildTraceOutputValue(DecisionEvaluationStatus.NOT_EVALUATED, false)
        )), "Decision must be succeeded if there are no outputs with 'FAILED' status or containing error messages");

        assertFalse(TraceEventConverter.decisionHasSucceeded(List.of(
                buildTraceOutputValue(DecisionEvaluationStatus.SUCCEEDED, false),
                buildTraceOutputValue(DecisionEvaluationStatus.FAILED, false)
        )), "Decision must be failed if at least one output has 'FAILED' status");

        assertFalse(TraceEventConverter.decisionHasSucceeded(List.of(
                buildTraceOutputValue(DecisionEvaluationStatus.SUCCEEDED, false),
                buildTraceOutputValue(DecisionEvaluationStatus.SKIPPED, true)
        )), "Decision must be failed if at least one output contains error messages");

        assertFalse(TraceEventConverter.decisionHasSucceeded(List.of(
                buildTraceOutputValue(DecisionEvaluationStatus.SUCCEEDED, false),
                buildTraceOutputValue(DecisionEvaluationStatus.NOT_EVALUATED, true)
        )), "Decision must be failed if at least one output contains error messages");

        assertFalse(TraceEventConverter.decisionHasSucceeded(List.of(
                buildTraceOutputValue(DecisionEvaluationStatus.SKIPPED, true),
                buildTraceOutputValue(DecisionEvaluationStatus.FAILED, false)
        )), "Decision must be failed if at least one output has 'FAILED' status or contains error messages");

        assertFalse(TraceEventConverter.decisionHasSucceeded(List.of(
                buildTraceOutputValue(DecisionEvaluationStatus.NOT_EVALUATED, true),
                buildTraceOutputValue(DecisionEvaluationStatus.FAILED, true)
        )), "Decision must be failed if at least one output has 'FAILED' status or contains error messages");
    }

    private static TraceOutputValue buildTraceOutputValue(DecisionEvaluationStatus status, boolean withErrorMessage) {
        String id = UUID.randomUUID().toString();
        List<Message> messages = withErrorMessage
            ? List.of(new Message(MessageLevel.ERROR, MessageCategory.INTERNAL, "TEST", id, "Error message", null, null))
            : Collections.emptyList();
        return new TraceOutputValue(id, "Output", status.name(), null, null, messages);
    }
}
