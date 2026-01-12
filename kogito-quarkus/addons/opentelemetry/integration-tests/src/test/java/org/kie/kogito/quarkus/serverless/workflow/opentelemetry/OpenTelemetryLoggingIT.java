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
 * Integration tests for OpenTelemetry logging integration.
 * Tests focus on log message capture as span events, log level filtering,
 * correlation with process instances, and external library log capture.
 */
@QuarkusIntegrationTest
@QuarkusTestResource(OtlpMockTestResource.class)
@QuarkusTestResource(TokenPropagationExternalServicesMock.class)
@QuarkusTestResource(KeycloakServiceMock.class)
public class OpenTelemetryLoggingIT {

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
     * Test that log messages during workflow execution are captured as span events.
     * <p>
     * Validates:
     * - Log messages are captured as "log.message" events on spans
     * - Event has required attributes: level, logger, message, thread.name, thread.id
     * - Log events are associated with the correct workflow execution spans
     */
    @Test
    void shouldCaptureLogMessagesAsSpanEvents() {
        executeWorkflowWithTxn("/greet", buildGreetBody("LogTest", "English"),
                "log-capture-test-txn-123", 201);

        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
            List<SpanData> spans = OtlpMockTestResource.getSpans();
            List<SpanData> workflowSpans = filterWorkflowSpans(spans);

            assertThat(workflowSpans).isNotEmpty();

            List<EventData> allLogEvents = collectEventsByName(workflowSpans, "log.message");
            EventData logEvent = allLogEvents.get(0);

            validateLogEventAttributes(logEvent);
        });
    }

    /**
     * Test that only logs at configured level and above are captured.
     * <p>
     * Validates:
     * - Only logs at INFO level and above are captured (default configuration)
     * - DEBUG and TRACE logs are filtered out
     * - Log level values in events match standard levels: ERROR, WARN, INFO
     */
    @Test
    void shouldOnlyCaptureConfiguredLogLevels() {
        executeWorkflowWithTxn("/greet", buildGreetBody("LogLevelTest", "Spanish"),
                "log-level-filter-test-txn-456", 201);

        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
            List<SpanData> spans = OtlpMockTestResource.getSpans();
            List<SpanData> workflowSpans = filterWorkflowSpans(spans);

            assertThat(workflowSpans).isNotEmpty();

            List<EventData> allLogEvents = collectEventsByName(workflowSpans, "log.message");

            Set<String> logLevels = allLogEvents.stream()
                    .map(event -> event.getAttributes().get(LOG_LEVEL))
                    .collect(Collectors.toSet());

            assertThat(logLevels)
                    .withFailMessage("Log events should only contain INFO and above, found: " + logLevels)
                    .allMatch(level -> level.equals("INFO") || level.equals("WARN") || level.equals("ERROR"));
        });
    }

    /**
     * Test that logs appear in spans associated with the correct process instance.
     * <p>
     * Validates:
     * - Logs are correlated with the correct workflow execution using transaction ID
     * - Multiple concurrent workflows don't mix their logs
     * - Log events share the same trace ID as their workflow execution
     */
    @Test
    void shouldCorrelateLogsWithProcessInstance() {
        executeWorkflowWithTxn("/greet", buildGreetBody("EnglishUser", "English"),
                "log-correlation-test-txn-english", 201);

        executeWorkflowWithTxn("/greet", buildGreetBody("SpanishUser", "Spanish"),
                "log-correlation-test-txn-spanish", 201);

        await().atMost(Duration.ofSeconds(15)).untilAsserted(() -> {
            List<SpanData> spans = OtlpMockTestResource.getSpans();
            List<SpanData> workflowSpans = filterWorkflowSpans(spans);

            List<SpanData> englishSpans = filterSpansByTransactionId(workflowSpans, "log-correlation-test-txn-english");
            List<SpanData> spanishSpans = filterSpansByTransactionId(workflowSpans, "log-correlation-test-txn-spanish");

            assertThat(englishSpans).isNotEmpty();
            assertThat(spanishSpans).isNotEmpty();

            String englishTraceId = englishSpans.get(0).getTraceId();
            String spanishTraceId = spanishSpans.get(0).getTraceId();

            assertThat(englishTraceId).isNotEqualTo(spanishTraceId);

            List<EventData> spanishLogEvents = collectEventsByName(spanishSpans, "log.message");

            assertThat(spanishLogEvents)
                    .withFailMessage("log.message events not found in Spanish workflow")
                    .isNotEmpty();

            validateSharedTraceId(englishSpans);
            validateSharedTraceId(spanishSpans);
        });
    }

    /**
     * Anti-regression test to ensure log events are not duplicated in spans.
     * This test guards against the issue where the same log handler was registered
     * on both root and Kogito loggers, causing duplicate span events.
     */
    @Test
    void shouldNotDuplicateLogEvents() {
        executeWorkflowWithTxn("/greet", buildGreetBody("DuplicateTest", "English"),
                "log-no-duplicate-test-txn", 201);

        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
            List<SpanData> spans = OtlpMockTestResource.getSpans();
            assertThat(spans).isNotEmpty();

            List<EventData> allLogEvents = spans.stream()
                    .flatMap(span -> span.getEvents().stream())
                    .filter(event -> event.getName().equals("log.message"))
                    .collect(Collectors.toList());

            assertThat(allLogEvents).isNotEmpty();

            Map<String, Long> messageCounts = allLogEvents.stream()
                    .map(event -> event.getAttributes().get(LOG_MESSAGE))
                    .filter(message -> message != null && !message.isEmpty())
                    .collect(Collectors.groupingBy(
                            java.util.function.Function.identity(),
                            Collectors.counting()));

            messageCounts.forEach((message, count) -> {
                assertThat(count)
                        .as("Log message should not be excessively duplicated: '%s' (count: %d)", message, count)
                        .isLessThanOrEqualTo(2L);
            });

            for (int i = 1; i < allLogEvents.size(); i++) {
                EventData prev = allLogEvents.get(i - 1);
                EventData curr = allLogEvents.get(i);

                String prevMessage = prev.getAttributes().get(LOG_MESSAGE);
                String currMessage = curr.getAttributes().get(LOG_MESSAGE);

                if (prevMessage != null && prevMessage.equals(currMessage)) {
                    long prevTime = prev.getEpochNanos();
                    long currTime = curr.getEpochNanos();

                    assertThat(Math.abs(currTime - prevTime))
                            .as("Consecutive identical log messages should not occur within 1ms: '%s'", prevMessage)
                            .isGreaterThan(1_000_000L);
                }
            }
        });
    }

    /**
     * Test that external library logs are captured when using root-only logger registration.
     * This validates the user's concern: "if we remove the root logger, logs may be missed:
     * for instance if the user is enabling DEBUG log for apache http, then we would miss those logs"
     * <p>
     * Validates:
     * - External library logs (e.g., Apache HTTP) are captured via root logger inheritance
     * - Root-only registration provides comprehensive log coverage including external libraries
     * - No loss of external library log visibility with the duplicate handler fix
     */
    @Test
    void shouldCaptureExternalLibraryLogs() {
        executeTokenPropagationWorkflow("log-external-library-log-test-txn", 201);

        await().atMost(Duration.ofSeconds(30)).untilAsserted(() -> {
            List<SpanData> spans = OtlpMockTestResource.getSpans();
            List<SpanData> workflowSpans = filterWorkflowSpans(spans);

            assertThat(workflowSpans).isNotEmpty();

            List<EventData> allLogEvents = collectEventsByName(workflowSpans, "log.message");

            assertThat(allLogEvents).isNotEmpty();

            List<EventData> externalLibraryEvents = allLogEvents.stream()
                    .filter(event -> {
                        String logger = event.getAttributes().get(LOG_LOGGER);
                        return logger != null && (logger.contains("apache") ||
                                logger.contains("http") ||
                                logger.contains("quarkus") ||
                                logger.contains("netty") ||
                                logger.contains("undertow") ||
                                !logger.startsWith("org.kie.kogito"));
                    })
                    .collect(Collectors.toList());

            assertThat(externalLibraryEvents)
                    .withFailMessage("External library logs should be captured via root logger inheritance. " +
                            "This validates that removing duplicate kogito logger registration " +
                            "doesn't lose external library log coverage.")
                    .isNotEmpty();

            externalLibraryEvents.forEach(event -> {
                Attributes eventAttributes = event.getAttributes();
                assertThat(eventAttributes.get(LOG_LEVEL))
                        .isNotNull();
                assertThat(eventAttributes.get(LOG_LOGGER))
                        .isNotNull();
                assertThat(eventAttributes.get(LOG_MESSAGE))
                        .isNotNull();
            });
        });
    }

    /**
     * Test that log messages during token propagation workflow are captured as span events.
     * This test focuses only on the log capture aspects of token propagation, validating
     * that logs are properly associated with workflow spans during complex workflows.
     * <p>
     * Validates:
     * - Log messages are captured as "log.message" events on spans
     * - Event has required attributes: level, logger, message, thread.name, thread.id
     * - Log events are associated with the correct workflow execution spans
     */
    @Test
    void shouldCaptureLogMessagesAsSpanEventsWithTokenPropagation() {
        executeTokenPropagationWorkflow("log-capture-test-token-propagation-txn-123", 201);

        await().atMost(Duration.ofSeconds(25)).untilAsserted(() -> {
            List<SpanData> spans = OtlpMockTestResource.getSpans();
            List<SpanData> workflowSpans = filterWorkflowSpans(spans);

            assertThat(workflowSpans).isNotEmpty();

            List<EventData> allLogEvents = collectEventsByName(workflowSpans, "log.message");
            assertThat(allLogEvents).isNotEmpty();

            EventData logEvent = allLogEvents.get(0);
            validateLogEventAttributes(logEvent);
        });
    }

    // Private helper methods moved from OpenTelemetryTestUtils for LoggingIT-specific validation

    /**
     * Validates log.message event attributes.
     *
     * @param logEvent the log event to validate
     */
    private static void validateLogEventAttributes(EventData logEvent) {
        Attributes logAttributes = logEvent.getAttributes();
        assertThat(logAttributes.get(LOG_LEVEL))
                .withFailMessage("log.message event missing 'level' attribute")
                .isNotNull();
        assertThat(logAttributes.get(LOG_LOGGER))
                .withFailMessage("log.message event missing 'logger' attribute")
                .isNotNull();
        assertThat(logAttributes.get(LOG_MESSAGE))
                .withFailMessage("log.message event missing 'message' attribute")
                .isNotNull();
        assertThat(logAttributes.get(LOG_THREAD_NAME))
                .withFailMessage("log.message event missing 'thread.name' attribute")
                .isNotNull();
        assertThat(logAttributes.get(LOG_THREAD_ID))
                .withFailMessage("log.message event missing 'thread.id' attribute")
                .isNotNull();
        assertThat(logEvent.getEpochNanos()).isGreaterThan(0);
    }
}
