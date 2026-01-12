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
package org.kie.kogito.quarkus.serverless.workflow.opentelemetry;

import java.time.Duration;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.sdk.trace.data.EventData;
import io.opentelemetry.sdk.trace.data.SpanData;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.kie.kogito.quarkus.serverless.workflow.opentelemetry.OpenTelemetryTestUtils.*;
import static org.kie.kogito.quarkus.serverless.workflow.opentelemetry.SonataFlowOtelAttributes.*;

/**
 * Integration tests for OpenTelemetry process lifecycle events.
 * Tests focus on process.instance.start, process.instance.complete, and process.instance.error events.
 */
@QuarkusIntegrationTest
@QuarkusTestResource(OtlpMockTestResource.class)
@QuarkusTestResource(TokenPropagationExternalServicesMock.class)
@QuarkusTestResource(KeycloakServiceMock.class)
public class OpenTelemetryProcessEventsIT {

    @BeforeEach
    public void cleanup() throws InterruptedException {
        if (OtlpMockTestResource.isCollectorRunning()) {
            OtlpMockTestResource.clearRequests();
        }
        TokenPropagationExternalServicesMock.getInstance().resetRequests();

        for (int i = 0; i < 5; i++) {
            Thread.sleep(200);
            if (OtlpMockTestResource.isCollectorRunning()) {
                OtlpMockTestResource.clearRequests();
                if (OtlpMockTestResource.getSpanCount() == 0) {
                    break;
                }
            }
        }
    }

    /**
     * Test that process.instance.start event is added to the first state span.
     * <p>
     * Validates:
     * - process.instance.start event exists on first workflow state span
     * - Event has required attributes: process.instance.id, trigger, reference.id
     * - Event is properly timestamped
     */
    @Test
    void shouldAddProcessStartEventToFirstState() {
        executeWorkflowWithTxn("/greet", buildGreetBody("ProcessStartTest", "English"),
                "process-start-test-txn-123", 201);

        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
            List<SpanData> spans = OtlpMockTestResource.getSpans();
            List<SpanData> workflowSpans = filterWorkflowSpans(spans);

            assertThat(workflowSpans).hasSizeGreaterThanOrEqualTo(3);

            SpanData firstStateSpan = findSpanByStateName(workflowSpans, "ChooseOnLanguage");
            EventData startEvent = findEventByName(firstStateSpan, "process.instance.start");

            String processInstanceId = firstStateSpan.getAttributes().get(SONATAFLOW_PROCESS_INSTANCE_ID);
            validateProcessStartEvent(startEvent, processInstanceId, "http", "process-start-test-txn-123");
        });
    }

    /**
     * Test that process.instance.complete event is added to the final state span.
     * <p>
     * Validates:
     * - process.instance.complete event exists on final workflow state span
     * - Event has required attributes: process.instance.id, duration.ms, outcome
     * - Event is properly timestamped
     * - Duration is positive
     */
    @Test
    void shouldAddProcessCompleteEventToFinalState() {
        executeWorkflowWithTxn("/greet", buildGreetBody("ProcessCompleteTest", "Spanish"),
                "process-complete-test-txn-456", 201);

        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
            List<SpanData> spans = OtlpMockTestResource.getSpans();
            List<SpanData> workflowSpans = filterWorkflowSpans(spans);

            assertThat(workflowSpans).hasSizeGreaterThanOrEqualTo(3);

            SpanData finalStateSpan = findSpanByStateName(workflowSpans, "GreetPerson");
            EventData completeEvent = findEventByName(finalStateSpan, "process.instance.complete");

            String processInstanceId = finalStateSpan.getAttributes().get(SONATAFLOW_PROCESS_INSTANCE_ID);
            validateProcessCompleteEvent(completeEvent, processInstanceId, "COMPLETED");
        });
    }

    /**
     * Test that process.instance.error event is added when workflow encounters an error.
     */
    @Test
    void shouldAddProcessErrorEventWhenProcessFails() {
        executeWorkflowWithTxn("/uncaughterror", "{\"number\": 1}",
                "process-error-test-txn-789", 500);
        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
            List<SpanData> spans = OtlpMockTestResource.getSpans();
            List<SpanData> workflowSpans = filterWorkflowSpans(spans);

            assertThat(workflowSpans).isNotEmpty();

            boolean errorEventFound = workflowSpans.stream()
                    .flatMap(span -> span.getEvents().stream())
                    .anyMatch(event -> "process.instance.error".equals(event.getName()));

            assertThat(errorEventFound)
                    .withFailMessage("process.instance.error event not found in error workflow execution")
                    .isTrue();

            workflowSpans.stream()
                    .flatMap(span -> span.getEvents().stream())
                    .filter(event -> "process.instance.error".equals(event.getName()))
                    .findFirst()
                    .ifPresent(errorEvent -> {
                        Attributes eventAttributes = errorEvent.getAttributes();

                        assertThat(eventAttributes.get(PROCESS_INSTANCE_ID))
                                .isNotNull();
                        assertThat(eventAttributes.get(AttributeKey.stringKey("error.message")))
                                .isNotNull();
                        assertThat(eventAttributes.get(AttributeKey.stringKey("error.type")))
                                .isNotNull();

                        assertThat(errorEvent.getEpochNanos()).isGreaterThan(0);
                    });
        });
    }

    // Private helper methods moved from OpenTelemetryTestUtils for ProcessEventsIT-specific validation

    /**
     * Finds a span by state name.
     *
     * @param spans list of spans to search
     * @param stateName the state name to find
     * @return the span with the specified state name
     * @throws AssertionError if span is not found
     */
    private static SpanData findSpanByStateName(List<SpanData> spans, String stateName) {
        return spans.stream()
                .filter(span -> {
                    String spanStateName = span.getAttributes().get(SONATAFLOW_WORKFLOW_STATE);
                    return stateName.equals(spanStateName);
                })
                .findFirst()
                .orElseThrow(() -> new AssertionError("Span with state name '" + stateName + "' not found"));
    }

    /**
     * Finds the first event by name from a span.
     *
     * @param span the span to search
     * @param eventName the event name to find
     * @return the event with the specified name
     * @throws AssertionError if event is not found
     */
    private static EventData findEventByName(SpanData span, String eventName) {
        return span.getEvents().stream()
                .filter(event -> eventName.equals(event.getName()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Event '" + eventName + "' not found on span"));
    }

    /**
     * Validates process.instance.start event attributes.
     *
     * @param startEvent the start event to validate
     * @param expectedProcessInstanceId the expected process instance ID
     * @param expectedTrigger the expected trigger type
     * @param expectedReferenceId the expected reference ID
     */
    private static void validateProcessStartEvent(EventData startEvent, String expectedProcessInstanceId,
            String expectedTrigger, String expectedReferenceId) {
        Attributes eventAttributes = startEvent.getAttributes();
        assertThat(eventAttributes.get(PROCESS_INSTANCE_ID))
                .isEqualTo(expectedProcessInstanceId);
        assertThat(eventAttributes.get(TRIGGER))
                .isEqualTo(expectedTrigger);
        assertThat(eventAttributes.get(REFERENCE_ID))
                .isEqualTo(expectedReferenceId);
        assertThat(startEvent.getEpochNanos()).isGreaterThan(0);
    }

    /**
     * Validates process.instance.complete event attributes.
     *
     * @param completeEvent the complete event to validate
     * @param expectedProcessInstanceId the expected process instance ID
     * @param expectedOutcome the expected outcome
     */
    private static void validateProcessCompleteEvent(EventData completeEvent, String expectedProcessInstanceId,
            String expectedOutcome) {
        Attributes eventAttributes = completeEvent.getAttributes();
        assertThat(eventAttributes.get(PROCESS_INSTANCE_ID))
                .isEqualTo(expectedProcessInstanceId);
        assertThat(eventAttributes.get(OUTCOME))
                .isEqualTo(expectedOutcome);
        Long durationMs = eventAttributes.get(DURATION_MS);
        assertThat(durationMs).isNotNull();
        assertThat(durationMs).isGreaterThan(0L);
        assertThat(completeEvent.getEpochNanos()).isGreaterThan(0);
    }
}
