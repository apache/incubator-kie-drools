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
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.opentelemetry.sdk.trace.data.SpanData;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.kie.kogito.quarkus.serverless.workflow.opentelemetry.OpenTelemetryTestUtils.*;
import static org.kie.kogito.quarkus.serverless.workflow.opentelemetry.SonataFlowOtelAttributes.*;

/**
 * Integration tests for OpenTelemetry token propagation and subflow tracing.
 * Tests focus on validating token propagation across service calls and
 * proper trace correlation between main workflows and subflows.
 */
@QuarkusIntegrationTest
@QuarkusTestResource(OtlpMockTestResource.class)
@QuarkusTestResource(TokenPropagationExternalServicesMock.class)
@QuarkusTestResource(KeycloakServiceMock.class)
public class OpenTelemetryTokenPropagationIT {

    private static final Logger LOGGER = LoggerFactory.getLogger(OpenTelemetryTokenPropagationIT.class);

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
     * Comprehensive test for token propagation workflow with subflow tracing validation.
     * This test validates proper trace correlation, parent-child relationships,
     * and transaction ID propagation across main workflow and subflow boundaries.
     * <p>
     * Validates:
     * - Token propagation across external service calls
     * - Main workflow and subflow span creation
     * - Trace ID consistency between main workflow and subflow
     * - Process instance isolation (different process instance IDs)
     * - Transaction ID propagation to all spans (main + subflow)
     * - Span durations are positive
     * - No error status in successful execution
     */
    @Test
    void shouldValidateTokenPropagationWithSubflowTracing() {
        executeTokenPropagationWorkflow("token-propagation-subflow-test-txn", 201);

        await().atMost(Duration.ofSeconds(25)).untilAsserted(() -> {
            List<SpanData> spans = OtlpMockTestResource.getSpans();
            List<SpanData> workflowSpans = filterWorkflowSpans(spans);

            assertThat(workflowSpans)
                    .withFailMessage("Workflow spans should be present for token propagation with subflow")
                    .isNotEmpty();

            validateMainAndSubflowBehaviour(workflowSpans, "token-propagation-subflow-test-txn");
        });
    }

    /**
     * Test to verify that subflow spans maintain flat hierarchy with main workflow spans.
     * When a workflow invokes a subflow, all spans (main workflow + subflow) should be
     * flat siblings under the same root span, not nested hierarchically.
     *
     * This validates:
     * - Main workflow spans have flat hierarchy (all siblings)
     * - Subflow spans are also flat siblings with the same parent as main workflow spans
     * - Subflow spans are NOT children of the ExecuteSubflow state span
     * - The flat hierarchy is maintained correctly across workflow boundaries
     */
    @Test
    void shouldMaintainFlatHierarchyForSubflowNodes() {
        executeTokenPropagationWorkflow("workflow-subflow-flat-hierarchy-test-txn", 201);

        await().atMost(Duration.ofSeconds(25)).untilAsserted(() -> {
            List<SpanData> spans = OtlpMockTestResource.getSpans();
            List<SpanData> workflowSpans = filterWorkflowSpans(spans);

            assertThat(workflowSpans)
                    .withFailMessage("Workflow spans should be present")
                    .isNotEmpty();

            List<SpanData> currentTestSpans = filterSpansByTransactionId(workflowSpans,
                    "workflow-subflow-flat-hierarchy-test-txn");

            List<SpanData> mainWorkflowSpans = filterMainWorkflowSpans(currentTestSpans, "token_propagation");
            List<SpanData> subflowSpans = filterSubflowSpans(currentTestSpans, "tokenPropagationSubflow");

            assertThat(mainWorkflowSpans)
                    .withFailMessage("Main workflow spans should be present")
                    .isNotEmpty();
            assertThat(subflowSpans)
                    .withFailMessage("Subflow spans should be present")
                    .isNotEmpty();

            Set<String> allParentSpanIds = Stream.concat(
                    mainWorkflowSpans.stream(),
                    subflowSpans.stream())
                    .map(SpanData::getParentSpanId)
                    .filter(id -> id != null && !id.isEmpty())
                    .collect(Collectors.toSet());

            assertThat(allParentSpanIds)
                    .withFailMessage("All workflow spans (main + subflow) should have same parent (flat hierarchy). Found: " + allParentSpanIds)
                    .hasSize(1);

            SpanData executeSubflowSpan = findSpanByStateName(mainWorkflowSpans, "ExecuteSubflow");
            String executeSubflowSpanId = executeSubflowSpan.getSpanId();

            long subflowSpansWithExecuteSubflowAsParent = subflowSpans.stream()
                    .filter(span -> executeSubflowSpanId.equals(span.getParentSpanId()))
                    .count();

            assertThat(subflowSpansWithExecuteSubflowAsParent)
                    .withFailMessage("Subflow spans should NOT be children of ExecuteSubflow - should be flat siblings. Found %d hierarchical spans", subflowSpansWithExecuteSubflowAsParent)
                    .isEqualTo(0);
        });
    }

    // Private helper methods moved from OpenTelemetryTestUtils for TokenPropagation-specific validation

    /**
     * Validates main workflow and subflow behavior including trace correlation,
     * process instance isolation, parent-child relationships, timing, and execution.
     *
     * @param workflowSpans all workflow spans to validate
     * @param transactionId the transaction ID for filtering
     */
    private static void validateMainAndSubflowBehaviour(List<SpanData> workflowSpans, String transactionId) {
        List<SpanData> currentTestSpans = OpenTelemetryTestUtils.filterSpansByTransactionId(workflowSpans, transactionId);

        List<SpanData> mainWorkflowSpans = filterMainWorkflowSpans(currentTestSpans, "token_propagation");
        List<SpanData> subflowSpans = filterSubflowSpans(currentTestSpans, "tokenPropagationSubflow");

        assertThat(mainWorkflowSpans)
                .withFailMessage("Main workflow spans should be present for token_propagation")
                .isNotEmpty();
        assertThat(subflowSpans)
                .withFailMessage("Subflow spans should be present for tokenPropagationSubflow")
                .isNotEmpty();

        validateTraceIdConsistency(mainWorkflowSpans, subflowSpans);
        validateProcessInstanceIsolation(mainWorkflowSpans, subflowSpans);

        SpanData executeSubflowSpan = validateParentChildSpanRelationship(mainWorkflowSpans, subflowSpans);
        validateSpansTimingAndSequence(mainWorkflowSpans, subflowSpans, executeSubflowSpan);
        validateSubflowNodeExecution(subflowSpans);

        validateTransactionIdPropagation(mainWorkflowSpans, transactionId);
        validateTransactionIdPropagation(subflowSpans, transactionId);
        validateSpanDurations(currentTestSpans);
        validateNoErrorStatus(currentTestSpans);
    }

    /**
     * Filters spans to include only those from the main workflow (by process ID).
     *
     * @param spans list of all spans
     * @param processId the process ID to filter by
     * @return filtered list containing only spans from the specified process
     */
    private static List<SpanData> filterMainWorkflowSpans(List<SpanData> spans, String processId) {
        return spans.stream()
                .filter(span -> {
                    String spanProcessId = span.getAttributes().get(SONATAFLOW_PROCESS_ID);
                    return processId.equals(spanProcessId);
                })
                .collect(Collectors.toList());
    }

    /**
     * Filters spans to include only those from a subflow (by process ID).
     *
     * @param spans list of all spans
     * @param subflowProcessId the subflow process ID to filter by
     * @return filtered list containing only spans from the specified subflow
     */
    private static List<SpanData> filterSubflowSpans(List<SpanData> spans, String subflowProcessId) {
        return filterMainWorkflowSpans(spans, subflowProcessId);
    }

    /**
     * Validates trace ID consistency across main and subflow spans.
     *
     * @param mainWorkflowSpans spans from the main workflow
     * @param subflowSpans spans from the subflow
     */
    private static void validateTraceIdConsistency(List<SpanData> mainWorkflowSpans, List<SpanData> subflowSpans) {
        Set<String> mainTraceIds = extractTraceIds(mainWorkflowSpans);
        Set<String> subflowTraceIds = extractTraceIds(subflowSpans);

        assertThat(mainTraceIds)
                .withFailMessage("All main workflow spans should share the same trace ID")
                .hasSize(1);
        assertThat(subflowTraceIds)
                .withFailMessage("All subflow spans should share the same trace ID")
                .hasSize(1);
        assertThat(mainTraceIds.iterator().next())
                .withFailMessage("Main workflow and subflow should share the same trace ID for correlation")
                .isEqualTo(subflowTraceIds.iterator().next());
    }

    /**
     * Validates process instance isolation between main and subflow.
     *
     * @param mainWorkflowSpans spans from the main workflow
     * @param subflowSpans spans from the subflow
     */
    private static void validateProcessInstanceIsolation(List<SpanData> mainWorkflowSpans, List<SpanData> subflowSpans) {
        Set<String> mainProcessInstanceIds = extractProcessInstanceIds(mainWorkflowSpans);
        Set<String> subflowProcessInstanceIds = extractProcessInstanceIds(subflowSpans);

        assertThat(mainProcessInstanceIds)
                .withFailMessage("All main workflow spans should share the same process instance ID")
                .hasSize(1);
        assertThat(subflowProcessInstanceIds)
                .withFailMessage("All subflow spans should share the same process instance ID")
                .hasSize(1);
        assertThat(mainProcessInstanceIds.iterator().next())
                .withFailMessage("Main workflow and subflow must have different process instance IDs")
                .isNotEqualTo(subflowProcessInstanceIds.iterator().next());
    }

    /**
     * Validates flat span hierarchy between main workflow and subflow.
     * All spans (main workflow + subflow) should share the same parent (flat hierarchy).
     *
     * @param mainWorkflowSpans spans from the main workflow
     * @param subflowSpans spans from the subflow
     * @return the ExecuteSubflow span
     */
    private static SpanData validateParentChildSpanRelationship(List<SpanData> mainWorkflowSpans, List<SpanData> subflowSpans) {
        SpanData executeSubflowSpan = findSpanByStateName(mainWorkflowSpans, "ExecuteSubflow");

        Set<String> allParentIds = Stream.concat(
                mainWorkflowSpans.stream(),
                subflowSpans.stream())
                .map(SpanData::getParentSpanId)
                .filter(id -> id != null && !id.isEmpty())
                .collect(Collectors.toSet());

        assertThat(allParentIds)
                .withFailMessage("All spans should share the same parent (flat hierarchy)")
                .hasSize(1);

        return executeSubflowSpan;
    }

    /**
     * Validates timing and sequence of spans across main workflow and subflow.
     *
     * @param mainWorkflowSpans spans from the main workflow
     * @param subflowSpans spans from the subflow
     * @param executeSubflowSpan the ExecuteSubflow span
     */
    private static void validateSpansTimingAndSequence(List<SpanData> mainWorkflowSpans, List<SpanData> subflowSpans, SpanData executeSubflowSpan) {
        validateSpanTiming(mainWorkflowSpans);
        validateSpanTiming(subflowSpans);

        List<SpanData> sortedSubflowSpans = subflowSpans.stream()
                .sorted(Comparator.comparingLong(SpanData::getStartEpochNanos))
                .toList();
        SpanData firstSubflowSpan = sortedSubflowSpans.get(0);
        assertThat(firstSubflowSpan.getStartEpochNanos())
                .withFailMessage("First subflow span should start after ExecuteSubflow span")
                .isGreaterThanOrEqualTo(executeSubflowSpan.getStartEpochNanos());
    }

    /**
     * Validates that subflow states are executed.
     *
     * @param subflowSpans spans from the subflow
     */
    private static void validateSubflowNodeExecution(List<SpanData> subflowSpans) {
        Set<String> actualSubflowStates = extractStateNames(subflowSpans);

        assertThat(actualSubflowStates)
                .withFailMessage("Subflow should have at least one state executed")
                .isNotEmpty();
    }

    /**
     * Validates span timing and sequence for a list of spans.
     *
     * @param spans list of spans to validate
     */
    private static void validateSpanTiming(List<SpanData> spans) {
        List<SpanData> sortedSpans = spans.stream()
                .sorted(Comparator.comparingLong(SpanData::getStartEpochNanos))
                .toList();

        for (int i = 1; i < sortedSpans.size(); i++) {
            SpanData previousSpan = sortedSpans.get(i - 1);
            SpanData currentSpan = sortedSpans.get(i);
            assertThat(currentSpan.getStartEpochNanos())
                    .withFailMessage("Span %s should start after or at the same time as previous span %s",
                            currentSpan.getAttributes().get(SONATAFLOW_WORKFLOW_STATE),
                            previousSpan.getAttributes().get(SONATAFLOW_WORKFLOW_STATE))
                    .isGreaterThanOrEqualTo(previousSpan.getStartEpochNanos());
        }
    }

    /**
     * Validates that all spans have positive duration.
     *
     * @param spans list of spans to validate
     */
    private static void validateSpanDurations(List<SpanData> spans) {
        spans.forEach(span -> {
            long duration = span.getEndEpochNanos() - span.getStartEpochNanos();
            assertThat(duration)
                    .withFailMessage("Span %s should have positive duration",
                            span.getAttributes().get(SONATAFLOW_WORKFLOW_STATE))
                    .isGreaterThan(0);
        });
    }

    /**
     * Validates that no spans have error status.
     *
     * @param spans list of spans to validate
     */
    private static void validateNoErrorStatus(List<SpanData> spans) {
        spans.forEach(span -> {
            assertThat(span.getStatus().getStatusCode())
                    .withFailMessage("Span %s should not have error status for successful workflow execution",
                            span.getAttributes().get(SONATAFLOW_WORKFLOW_STATE))
                    .isNotEqualTo(io.opentelemetry.api.trace.StatusCode.ERROR);
        });
    }

    /**
     * Validates transaction ID propagation to all spans.
     *
     * @param spans list of spans to validate
     * @param expectedTransactionId the expected transaction ID
     */
    private static void validateTransactionIdPropagation(List<SpanData> spans, String expectedTransactionId) {
        spans.forEach(span -> {
            String txnId = span.getAttributes().get(SONATAFLOW_TRANSACTION_ID);
            assertThat(txnId)
                    .withFailMessage("Span should have transaction ID")
                    .isEqualTo(expectedTransactionId);
        });
    }

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
     * Extracts unique trace IDs from spans.
     *
     * @param spans list of spans
     * @return set of unique trace IDs
     */
    private static Set<String> extractTraceIds(List<SpanData> spans) {
        return spans.stream()
                .map(SpanData::getTraceId)
                .collect(Collectors.toSet());
    }

    /**
     * Extracts unique process instance IDs from spans.
     *
     * @param spans list of spans
     * @return set of unique process instance IDs
     */
    private static Set<String> extractProcessInstanceIds(List<SpanData> spans) {
        return spans.stream()
                .map(span -> span.getAttributes().get(SONATAFLOW_PROCESS_INSTANCE_ID))
                .collect(Collectors.toSet());
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
}
