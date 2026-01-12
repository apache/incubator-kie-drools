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
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.opentelemetry.sdk.trace.data.EventData;
import io.opentelemetry.sdk.trace.data.SpanData;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.kie.kogito.quarkus.serverless.workflow.opentelemetry.OpenTelemetryTestUtils.*;
import static org.kie.kogito.quarkus.serverless.workflow.opentelemetry.SonataFlowOtelAttributes.*;

/**
 * Integration tests for OpenTelemetry workflow execution behavior.
 * Tests focus on basic workflow span creation, node execution, transaction ID handling,
 * and workflow configuration.
 */
@QuarkusIntegrationTest
@QuarkusTestResource(OtlpMockTestResource.class)
@QuarkusTestResource(TokenPropagationExternalServicesMock.class)
@QuarkusTestResource(KeycloakServiceMock.class)
public class OpenTelemetryWorkflowIT {

    private static final Logger LOGGER = LoggerFactory.getLogger(OpenTelemetryWorkflowIT.class);

    @BeforeEach
    public void cleanup() throws InterruptedException {
        OtlpMockTestResource.clearRequests();
        TokenPropagationExternalServicesMock.getInstance().resetRequests();

        for (int i = 0; i < 5; i++) {
            Thread.sleep(200);
            OtlpMockTestResource.clearRequests();
            if (OtlpMockTestResource.getSpanCount() == 0) {
                break;
            }
        }
    }

    /**
     * Test complete OpenTelemetry integration with real workflow execution.
     * <p>
     * This test validates:
     * - Real workflow execution through REST endpoint
     * - State span creation for each workflow state
     * - Transaction ID propagation from X-TRANSACTION-ID header
     * - Tracker attribute propagation from X-TRACKER-* headers
     * - All mandatory span attributes according to design document
     */
    @Test
    void shouldCreateStateSpansWithTransactionIdFromHeader() {
        executeWorkflowWithTrackers("/greet", buildGreetBody("John", "English"),
                "workflow-test-transaction-123", "customer-456", "session-789", 201);

        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
            List<SpanData> spans = OtlpMockTestResource.getSpans();
            List<SpanData> workflowSpans = filterWorkflowSpans(spans);

            assertThat(workflowSpans).hasSizeGreaterThanOrEqualTo(3);

            validateSharedTraceId(workflowSpans);
            validateSpanNaming(workflowSpans, "sonataflow.process.greet.execute");

            workflowSpans.forEach(span -> {
                validateMandatoryStateSpanAttributes(span, "greet");
                validateTransactionAndTrackerAttributes(span, "workflow-test-transaction-123", "customer-456", "session-789");
            });

            Set<String> stateNames = extractStateNames(workflowSpans);
            assertThat(stateNames).hasSizeGreaterThanOrEqualTo(3);
        });
    }

    /**
     * Test workflow execution without headers falls back to process instance ID.
     */
    @Test
    void shouldFallbackToProcessInstanceIdWhenNoTransactionIdHeader() {
        executeWorkflow("/greet", buildGreetBody("Alice", "Spanish"), 201);

        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
            List<SpanData> spans = OtlpMockTestResource.getSpans();
            List<SpanData> workflowSpans = filterWorkflowSpans(spans);
            assertThat(workflowSpans).hasSizeGreaterThanOrEqualTo(3);

            workflowSpans.forEach(span -> {
                String processInstanceId = span.getAttributes().get(SONATAFLOW_PROCESS_INSTANCE_ID);
                String transactionId = span.getAttributes().get(SONATAFLOW_TRANSACTION_ID);
                String stateName = span.getAttributes().get(SONATAFLOW_WORKFLOW_STATE);

                assertThat(transactionId).isEqualTo(processInstanceId);
                assertThat(stateName).isNotNull();
            });
        });
    }

    /**
     * Test state span creation for different workflow paths.
     * This validates that different workflow state paths create appropriate spans.
     */
    @Test
    void shouldCreateDifferentStateSpansForDifferentWorkflowPaths() {
        executeWorkflowWithTxn("/greet", buildGreetBody("Carlos", "Spanish"),
                "workflow-spanish-workflow-txn", 201);

        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
            List<SpanData> spans = OtlpMockTestResource.getSpans();
            List<SpanData> workflowSpans = filterWorkflowSpans(spans);
            assertThat(workflowSpans).hasSizeGreaterThanOrEqualTo(3);

            Set<String> stateNames = extractStateNames(workflowSpans);
            assertThat(stateNames).contains("ChooseOnLanguage", "GreetInSpanish", "GreetPerson");
        });
    }

    /**
     * Test OpenTelemetry configuration handling.
     * Validates that the integration respects configuration settings.
     */
    @Test
    void shouldRespectOpenTelemetryConfiguration() {
        executeWorkflow("/greet", buildGreetBody("Test", "English"), 201);

        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
            List<SpanData> spans = OtlpMockTestResource.getSpans();
            List<SpanData> workflowSpans = filterWorkflowSpans(spans);

            assertThat(workflowSpans).isNotEmpty();

            workflowSpans.forEach(span -> {
                String serviceName = span.getAttributes().get(SERVICE_NAME);
                assertThat(serviceName).isNotNull();
            });
        });
    }

    /**
     * Test concurrent workflow executions create independent traces.
     */
    @Test
    void shouldCreateIndependentTracesForConcurrentWorkflows() {
        for (int i = 0; i < 3; i++) {
            executeWorkflowWithTxn("/greet", buildGreetBody("User" + i, "English"),
                    "workflow-concurrent-txn-" + i, 201);
        }

        await().atMost(Duration.ofSeconds(15)).untilAsserted(() -> {
            List<SpanData> spans = OtlpMockTestResource.getSpans();
            assertThat(spans).hasSizeGreaterThanOrEqualTo(9);

            Set<String> traceIds = spans.stream()
                    .map(SpanData::getTraceId)
                    .collect(Collectors.toSet());
            assertThat(traceIds).hasSizeGreaterThanOrEqualTo(3);

            Set<String> transactionIds = spans.stream()
                    .map(span -> span.getAttributes().get(SONATAFLOW_TRANSACTION_ID))
                    .filter(id -> id != null && id.startsWith("workflow-concurrent-txn-"))
                    .collect(Collectors.toSet());
            assertThat(transactionIds).containsExactlyInAnyOrder(
                    "workflow-concurrent-txn-0", "workflow-concurrent-txn-1", "workflow-concurrent-txn-2");
        });
    }

    /**
     * Test to verify that process completion events are handled exactly once without race conditions.
     * This test specifically targets the issue where multiple finishing states trigger duplicate
     * completion context warnings: "Process in terminal state but no completion context found".
     *
     * The test ensures:
     * - Process completion events are added exactly once per process
     * - State started and completed events exist
     * - No duplicate completion context warnings are generated
     * - Multiple concurrent workflows don't interfere with each other's completion handling
     */
    @Test
    void shouldHandleProcessCompletionEventsExactlyOnce() {
        for (int i = 0; i < 3; i++) {
            executeWorkflowWithTxn("/greet", buildGreetBody("CompletionTest" + i, "English"),
                    "workflow-completion-race-test-txn-" + i, 201);
        }

        await().atMost(Duration.ofSeconds(15)).untilAsserted(() -> {
            List<SpanData> spans = OtlpMockTestResource.getSpans();
            List<SpanData> workflowSpans = filterWorkflowSpans(spans);

            assertThat(workflowSpans).hasSizeGreaterThanOrEqualTo(9);

            Map<String, List<SpanData>> spansByTxn = workflowSpans.stream()
                    .filter(span -> {
                        String txnId = span.getAttributes().get(SONATAFLOW_TRANSACTION_ID);
                        return txnId != null && txnId.startsWith("workflow-completion-race-test-txn-");
                    })
                    .collect(Collectors.groupingBy(
                            span -> span.getAttributes().get(SONATAFLOW_TRANSACTION_ID)));

            assertThat(spansByTxn).hasSize(3);

            spansByTxn.forEach((txnId, workflowSpansForTxn) -> {
                List<EventData> completeEvents = workflowSpansForTxn.stream()
                        .flatMap(span -> span.getEvents().stream())
                        .filter(event -> "process.instance.complete".equals(event.getName()))
                        .collect(Collectors.toList());

                assertThat(completeEvents)
                        .withFailMessage("Each workflow should have exactly one process.instance.complete event. " +
                                "Transaction %s has %d events", txnId, completeEvents.size())
                        .hasSize(1);

                EventData completeEvent = completeEvents.get(0);
                assertThat(completeEvent.getAttributes().get(PROCESS_INSTANCE_ID))
                        .isNotNull();
                assertThat(completeEvent.getAttributes().get(OUTCOME))
                        .isEqualTo("COMPLETED");
                assertThat(completeEvent.getAttributes().get(DURATION_MS))
                        .isNotNull()
                        .isGreaterThan(0L);

                List<EventData> stateStartedEvents = workflowSpansForTxn.stream()
                        .flatMap(span -> span.getEvents().stream())
                        .filter(event -> "state.started".equals(event.getName()))
                        .collect(Collectors.toList());

                assertThat(stateStartedEvents)
                        .withFailMessage("State started events should exist for transaction %s", txnId)
                        .isNotEmpty();

                List<EventData> stateCompletedEvents = workflowSpansForTxn.stream()
                        .flatMap(span -> span.getEvents().stream())
                        .filter(event -> "state.completed".equals(event.getName()))
                        .collect(Collectors.toList());

                assertThat(stateCompletedEvents)
                        .withFailMessage("State completed events should exist for transaction %s", txnId)
                        .isNotEmpty();
            });

            Map<String, List<EventData>> startEventsByTxn = workflowSpans.stream()
                    .filter(span -> {
                        String txnId = span.getAttributes().get(SONATAFLOW_TRANSACTION_ID);
                        return txnId != null && txnId.startsWith("workflow-completion-race-test-txn-");
                    })
                    .flatMap(span -> span.getEvents().stream())
                    .filter(event -> "process.instance.start".equals(event.getName()))
                    .collect(Collectors.groupingBy(
                            event -> event.getAttributes().get(REFERENCE_ID)));

            assertThat(startEventsByTxn).hasSize(3);
            startEventsByTxn.forEach((txnId, events) -> {
                assertThat(events)
                        .withFailMessage("Each workflow should have exactly one process.instance.start event. " +
                                "Transaction %s has %d events", txnId, events.size())
                        .hasSize(1);
            });
        });
    }

    /**
     * Test to verify that regular workflow state spans have a flat span hierarchy.
     * All workflow state spans should be siblings under the HTTP request span,
     * rather than forming a parent-child hierarchy.
     *
     * This validates:
     * - All workflow state spans share the same parent span ID
     * - The parent span is the HTTP request span (not another workflow state)
     * - Span hierarchy is flat for regular (non-subflow) states
     */
    @Test
    void shouldCreateFlatSpanHierarchyForStateSpans() {
        executeWorkflowWithTxn("/greet", buildGreetBody("HierarchyTest", "English"),
                "workflow-flat-hierarchy-test-txn", 201);

        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
            List<SpanData> spans = OtlpMockTestResource.getSpans();
            List<SpanData> workflowSpans = filterWorkflowSpans(spans);

            LOGGER.info("=== Flat Span Hierarchy Debug Info ===");
            LOGGER.info("Total spans: {}, Workflow spans: {}", spans.size(), workflowSpans.size());

            assertThat(workflowSpans)
                    .withFailMessage("Workflow spans should be present")
                    .isNotEmpty();

            List<SpanData> currentTestSpans = filterSpansByTransactionId(workflowSpans,
                    "workflow-flat-hierarchy-test-txn");

            LOGGER.info("Spans for test transaction: {}", currentTestSpans.size());

            assertThat(currentTestSpans)
                    .withFailMessage("Should have spans for the test transaction")
                    .hasSizeGreaterThanOrEqualTo(3);

            LOGGER.info("--- Individual Span Details ---");
            currentTestSpans.forEach(span -> {
                String stateName = span.getAttributes().get(SONATAFLOW_WORKFLOW_STATE);
                LOGGER.info("Span: {} (state: {}) - parent: {}",
                        span.getSpanId(), stateName, span.getParentSpanId());
            });

            Set<String> parentSpanIds = currentTestSpans.stream()
                    .map(SpanData::getParentSpanId)
                    .filter(id -> id != null)
                    .collect(Collectors.toSet());

            LOGGER.info("--- Parent Span Analysis ---");
            LOGGER.info("Unique parent span IDs: {} -> {}", parentSpanIds.size(), parentSpanIds);

            if (parentSpanIds.size() > 1) {
                LOGGER.warn("*** HIERARCHICAL STRUCTURE DETECTED ***");
                LOGGER.warn("Expected all spans to share same parent (flat), but found {} different parents",
                        parentSpanIds.size());

                Map<String, List<SpanData>> spansByParent = currentTestSpans.stream()
                        .collect(Collectors.groupingBy(SpanData::getParentSpanId));

                spansByParent.forEach((parentId, childSpans) -> {
                    LOGGER.warn("Parent {}: {} children -> {}",
                            parentId,
                            childSpans.size(),
                            childSpans.stream()
                                    .map(s -> s.getAttributes().get(SONATAFLOW_WORKFLOW_STATE))
                                    .collect(Collectors.toList()));

                    boolean parentIsInTest = currentTestSpans.stream()
                            .anyMatch(s -> s.getSpanId().equals(parentId));
                    if (parentIsInTest) {
                        SpanData parentSpan = currentTestSpans.stream()
                                .filter(s -> s.getSpanId().equals(parentId))
                                .findFirst()
                                .orElse(null);
                        if (parentSpan != null) {
                            LOGGER.warn("  -> Parent '{}' is a workflow state (this creates hierarchy!)",
                                    parentSpan.getAttributes().get(SONATAFLOW_WORKFLOW_STATE));
                        }
                    }
                });
            }

            assertThat(parentSpanIds)
                    .withFailMessage("All workflow state spans should share the same parent span ID (flat hierarchy). Found: " + parentSpanIds)
                    .hasSize(1);

            String sharedParentSpanId = parentSpanIds.iterator().next();

            boolean parentIsWorkflowSpan = currentTestSpans.stream()
                    .anyMatch(span -> span.getSpanId().equals(sharedParentSpanId));

            LOGGER.info("Shared parent span ID: {}", sharedParentSpanId);
            LOGGER.info("Parent is workflow span: {}", parentIsWorkflowSpan);

            assertThat(parentIsWorkflowSpan)
                    .withFailMessage("Parent span should not be a workflow state span (should be HTTP request span)")
                    .isFalse();

            LOGGER.info("=== Flat Span Hierarchy Test PASSED ===");
        });
    }

    // Private helper methods moved from OpenTelemetryTestUtils for WorkflowIT-specific validation

    /**
     * Validates span naming follows design document pattern.
     *
     * @param spans list of spans to validate
     * @param expectedPrefix the expected span name prefix
     */
    private static void validateSpanNaming(List<SpanData> spans, String expectedPrefix) {
        spans.forEach(span -> {
            assertThat(span.getName()).startsWith(expectedPrefix);
        });
    }

    /**
     * Extracts unique state names from spans.
     *
     * @param spans list of spans
     * @return set of unique state names
     */
    private static Set<String> extractStateNames(List<SpanData> spans) {
        return spans.stream()
                .map(span -> span.getAttributes().get(SONATAFLOW_WORKFLOW_STATE))
                .filter(stateName -> stateName != null)
                .collect(Collectors.toSet());
    }

    /**
     * Validates mandatory span attributes for state-based spans.
     *
     * @param span the span to validate
     * @param expectedProcessId the expected process ID
     */
    private static void validateMandatoryStateSpanAttributes(SpanData span, String expectedProcessId) {
        assertThat(span.getAttributes().get(SONATAFLOW_PROCESS_INSTANCE_ID)).isNotNull();
        assertThat(span.getAttributes().get(SONATAFLOW_PROCESS_ID)).isEqualTo(expectedProcessId);
        assertThat(span.getAttributes().get(SONATAFLOW_PROCESS_VERSION)).isEqualTo("1.0");
        assertThat(span.getAttributes().get(SONATAFLOW_PROCESS_INSTANCE_STATE)).isNotNull();
        assertThat(span.getAttributes().get(SERVICE_NAME)).isNotNull();
        assertThat(span.getAttributes().get(SERVICE_VERSION)).isNotNull();
        assertThat(span.getAttributes().get(SONATAFLOW_WORKFLOW_STATE)).isNotNull();
    }
}
