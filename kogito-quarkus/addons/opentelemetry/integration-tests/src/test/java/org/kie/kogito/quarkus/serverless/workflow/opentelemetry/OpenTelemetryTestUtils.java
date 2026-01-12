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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.sdk.trace.data.EventData;
import io.opentelemetry.sdk.trace.data.SpanData;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;

import jakarta.ws.rs.core.HttpHeaders;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.quarkus.serverless.workflow.opentelemetry.ExternalServiceMock.SUCCESSFUL_QUERY;
import static org.kie.kogito.quarkus.serverless.workflow.opentelemetry.SonataFlowOtelAttributes.*;
import static org.kie.kogito.quarkus.serverless.workflow.opentelemetry.TokenPropagationExternalServicesMock.AUTHORIZATION_TOKEN;
import static org.kie.kogito.quarkus.serverless.workflow.opentelemetry.TokenPropagationExternalServicesMock.SERVICE3_AUTHORIZATION_TOKEN;
import static org.kie.kogito.quarkus.serverless.workflow.opentelemetry.TokenPropagationExternalServicesMock.SERVICE3_HEADER_TO_PROPAGATE;
import static org.kie.kogito.quarkus.serverless.workflow.opentelemetry.TokenPropagationExternalServicesMock.SERVICE4_AUTHORIZATION_TOKEN;
import static org.kie.kogito.quarkus.serverless.workflow.opentelemetry.TokenPropagationExternalServicesMock.SERVICE4_HEADER_TO_PROPAGATE;

/**
 * Utility class for OpenTelemetry integration tests.
 * Provides reusable helper methods for workflow execution, span validation,
 * and event verification across all OpenTelemetry test classes.
 */
public final class OpenTelemetryTestUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(OpenTelemetryTestUtils.class);

    private OpenTelemetryTestUtils() {
        // Prevent instantiation
    }

    /**
     * Builds process input JSON for workflows that accept a query parameter.
     *
     * @param query the query string to include in the process input
     * @return JSON string representing the process input
     */
    public static String buildProcessInput(String query) {
        return "{\"query\": \"" + query + "\"}";
    }

    /**
     * Builds greet workflow request body with name and language.
     *
     * @param name the name to greet
     * @param language the language for greeting (English or Spanish)
     * @return JSON string representing the greet workflow input
     */
    public static String buildGreetBody(String name, String language) {
        return """
                {
                    "name": "%s",
                    "language": "%s"
                }
                """.formatted(name, language);
    }

    /**
     * Base helper method for executing workflow requests with full header customization.
     *
     * @param endpoint the workflow endpoint to call
     * @param body the request body as JSON string
     * @param headers custom headers to include in the request (can be null)
     * @param expectedStatus the expected HTTP status code
     * @return ValidatableResponse for further assertions
     */
    public static ValidatableResponse executeWorkflow(String endpoint, String body,
            Map<String, String> headers,
            int expectedStatus) {
        RequestSpecification request = given().contentType(ContentType.JSON).body(body);

        if (headers != null) {
            request = request.headers(headers);
        }

        return request.when()
                .post(endpoint)
                .then()
                .statusCode(expectedStatus);
    }

    /**
     * Execute workflow with no custom headers.
     *
     * @param endpoint the workflow endpoint to call
     * @param body the request body as JSON string
     * @param expectedStatus the expected HTTP status code
     * @return ValidatableResponse for further assertions
     */
    public static ValidatableResponse executeWorkflow(String endpoint, String body, int expectedStatus) {
        return executeWorkflow(endpoint, body, null, expectedStatus);
    }

    /**
     * Execute workflow with transaction ID header only.
     *
     * @param endpoint the workflow endpoint to call
     * @param body the request body as JSON string
     * @param transactionId the transaction ID to set in X-TRANSACTION-ID header
     * @param expectedStatus the expected HTTP status code
     * @return ValidatableResponse for further assertions
     */
    public static ValidatableResponse executeWorkflowWithTxn(String endpoint, String body,
            String transactionId, int expectedStatus) {
        Map<String, String> headers = new HashMap<>();
        headers.put("X-TRANSACTION-ID", transactionId);
        return executeWorkflow(endpoint, body, headers, expectedStatus);
    }

    /**
     * Execute workflow with transaction ID and tracker headers.
     *
     * @param endpoint the workflow endpoint to call
     * @param body the request body as JSON string
     * @param transactionId the transaction ID to set in X-TRANSACTION-ID header
     * @param customerId the customer ID to set in X-TRACKER-CUSTOMER-ID header
     * @param session the session to set in X-TRACKER-SESSION header
     * @param expectedStatus the expected HTTP status code
     * @return ValidatableResponse for further assertions
     */
    public static ValidatableResponse executeWorkflowWithTrackers(String endpoint, String body,
            String transactionId,
            String customerId, String session,
            int expectedStatus) {
        Map<String, String> headers = new HashMap<>();
        headers.put("X-TRANSACTION-ID", transactionId);
        if (customerId != null) {
            headers.put("X-TRACKER-CUSTOMER-ID", customerId);
        }
        if (session != null) {
            headers.put("X-TRACKER-SESSION", session);
        }
        return executeWorkflow(endpoint, body, headers, expectedStatus);
    }

    /**
     * Execute token propagation workflow with standard authorization headers.
     *
     * @param transactionId the transaction ID to set in X-TRANSACTION-ID header
     * @param expectedStatus the expected HTTP status code
     * @return ValidatableResponse for further assertions
     */
    public static ValidatableResponse executeTokenPropagationWorkflow(String transactionId, int expectedStatus) {
        String processInput = buildProcessInput(SUCCESSFUL_QUERY);
        Map<String, String> headers = new HashMap<>();
        headers.put("X-TRANSACTION-ID", transactionId);
        headers.put(HttpHeaders.AUTHORIZATION, AUTHORIZATION_TOKEN);
        headers.put(SERVICE3_HEADER_TO_PROPAGATE, SERVICE3_AUTHORIZATION_TOKEN);
        headers.put(SERVICE4_HEADER_TO_PROPAGATE, SERVICE4_AUTHORIZATION_TOKEN);

        return executeWorkflow("/token_propagation", processInput, headers, expectedStatus);
    }

    /**
     * Filters spans to include only workflow node spans (excluding HTTP instrumentation).
     *
     * @param spans list of all spans
     * @return filtered list containing only workflow spans
     */
    public static List<SpanData> filterWorkflowSpans(List<SpanData> spans) {
        return spans.stream()
                .filter(span -> span.getName().startsWith("sonataflow.process"))
                .collect(Collectors.toList());
    }

    /**
     * Filters spans by transaction ID with detailed logging for debugging.
     *
     * @param spans list of all spans
     * @param transactionId the transaction ID to filter by
     * @return filtered list containing only spans with the specified transaction ID
     */
    public static List<SpanData> filterSpansByTransactionId(List<SpanData> spans, String transactionId) {
        LOGGER.info("Filtering {} spans for transaction ID: '{}'", spans.size(), transactionId);

        Set<String> foundTransactionIds = spans.stream()
                .map(span -> span.getAttributes().get(SONATAFLOW_TRANSACTION_ID))
                .filter(txnId -> txnId != null)
                .collect(Collectors.toSet());

        LOGGER.info("Found transaction IDs in spans: {}", foundTransactionIds);

        long spansWithoutTxnId = spans.stream()
                .filter(span -> span.getAttributes().get(SONATAFLOW_TRANSACTION_ID) == null)
                .count();

        if (spansWithoutTxnId > 0) {
            LOGGER.warn("{} spans have NO transaction ID attribute", spansWithoutTxnId);
        }

        spans.stream().limit(3).forEach(span -> {
            String spanTxnId = span.getAttributes().get(SONATAFLOW_TRANSACTION_ID);
            String spanProcessId = span.getAttributes().get(SONATAFLOW_PROCESS_ID);
            String spanStateName = span.getAttributes().get(SONATAFLOW_WORKFLOW_STATE);
            LOGGER.info("Sample span: {} | Process: {} | State: {} | TxnId: '{}'",
                    span.getName(), spanProcessId, spanStateName, spanTxnId);
        });

        List<SpanData> filteredSpans = spans.stream()
                .filter(span -> {
                    String txnId = span.getAttributes().get(SONATAFLOW_TRANSACTION_ID);
                    boolean matches = transactionId.equals(txnId);
                    if (!matches && txnId != null) {
                        LOGGER.debug("Span '{}' has txnId '{}' (expected '{}')",
                                span.getName(), txnId, transactionId);
                    }
                    return matches;
                })
                .collect(Collectors.toList());

        LOGGER.info("Filtered result: {} spans match transaction ID '{}'",
                filteredSpans.size(), transactionId);

        if (filteredSpans.isEmpty() && !spans.isEmpty()) {
            LOGGER.error("*** CRITICAL: No spans found with transaction ID '{}' ***", transactionId);
            LOGGER.error("Available transaction IDs: {}", foundTransactionIds);
        }

        return filteredSpans;
    }

    /**
     * Collects all events by name from a list of spans.
     *
     * @param spans list of spans to search
     * @param eventName the event name to collect
     * @return list of all events with the specified name
     */
    public static List<EventData> collectEventsByName(List<SpanData> spans, String eventName) {
        return spans.stream()
                .flatMap(span -> span.getEvents().stream())
                .filter(event -> eventName.equals(event.getName()))
                .collect(Collectors.toList());
    }

    /**
     * Extracts unique trace IDs from spans.
     *
     * @param spans list of spans
     * @return set of unique trace IDs
     */
    public static Set<String> extractTraceIds(List<SpanData> spans) {
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
    public static Set<String> extractProcessInstanceIds(List<SpanData> spans) {
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
    public static Set<String> extractStateNames(List<SpanData> spans) {
        return spans.stream()
                .map(span -> span.getAttributes().get(SONATAFLOW_WORKFLOW_STATE))
                .filter(stateName -> stateName != null)
                .collect(Collectors.toSet());
    }

    /**
     * Validates transaction ID and tracker attributes on a span.
     *
     * @param span the span to validate
     * @param expectedTransactionId the expected transaction ID
     * @param expectedCustomerId the expected customer ID (can be null)
     * @param expectedSession the expected session (can be null)
     */
    public static void validateTransactionAndTrackerAttributes(SpanData span, String expectedTransactionId,
            String expectedCustomerId, String expectedSession) {
        assertThat(span.getAttributes().get(SONATAFLOW_TRANSACTION_ID))
                .isEqualTo(expectedTransactionId);
        if (expectedCustomerId != null) {
            assertThat(span.getAttributes().get(AttributeKey.stringKey(TrackerAttributes.createTrackerAttributeKey("customer.id"))))
                    .isEqualTo(expectedCustomerId);
        }
        if (expectedSession != null) {
            assertThat(span.getAttributes().get(AttributeKey.stringKey(TrackerAttributes.createTrackerAttributeKey("session"))))
                    .isEqualTo(expectedSession);
        }
    }

    /**
     * Validates that all spans share the same trace ID.
     *
     * @param spans list of spans to validate
     */
    public static void validateSharedTraceId(List<SpanData> spans) {
        assertThat(spans).isNotEmpty();
        String traceId = spans.get(0).getTraceId();
        spans.forEach(span -> {
            assertThat(span.getTraceId()).isEqualTo(traceId);
        });
    }

}
