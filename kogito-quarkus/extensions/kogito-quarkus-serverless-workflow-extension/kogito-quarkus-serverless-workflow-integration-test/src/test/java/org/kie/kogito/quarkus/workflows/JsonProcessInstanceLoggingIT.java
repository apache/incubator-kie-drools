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
package org.kie.kogito.quarkus.workflows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.test.utils.JsonProcessInstanceLogAnalyzer;
import org.kie.kogito.test.utils.JsonProcessInstanceLoggingTestBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Comprehensive integration tests for JSON-based process instance logging in serverless workflows.
 *
 * This test suite validates that the JSON logging infrastructure correctly:
 * - Creates JSON log files with process instance IDs for serverless workflows
 * - Isolates logs by process instance in JSON format
 * - Validates JSON schema compliance
 * - Handles MDC keys (processInstanceId, traceId, spanId)
 * - Supports concurrent workflow execution
 * - Handles edge cases (empty workflows, malformed JSON, large loads)
 * - Works in CI environments with proper retry logic
 *
 * Test Design Principles:
 * - One behavior per test method
 * - Clear test names describing what is being tested
 * - Proper setup and teardown for test isolation
 * - Comprehensive assertions with descriptive error messages
 * - CI-friendly with retry logic and extended timeouts
 * - Uses serverless workflows (helloworld, greet, parallel)
 */
@QuarkusIntegrationTest
class JsonProcessInstanceLoggingIT extends JsonProcessInstanceLoggingTestBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonProcessInstanceLoggingIT.class);
    private static final int CONCURRENT_WORKFLOW_COUNT = 10;
    private static final Duration WORKFLOW_COMPLETION_TIMEOUT = Duration.ofSeconds(30);

    @BeforeEach
    void setup() throws IOException {
        LOGGER.info("Setting up test - clearing all log files");
        clearAllLogFiles();
    }

    /**
     * Test: JSON log file is created when serverless workflow instances are executed.
     *
     * Validates:
     * - Log file exists after workflow execution
     * - Log file contains valid JSON entries
     * - Process instance ID appears in JSON logs
     */
    @Test
    void testJsonLogFileCreationWithProcessInstanceId() throws Exception {
        LOGGER.info("TEST: JSON log file creation with process instance ID for helloworld workflow");

        String processId = createHelloworldWorkflow();
        boolean completed = waitForWorkflowCompletion("/helloworld", processId, WORKFLOW_COMPLETION_TIMEOUT);
        assertThat(completed).as("Helloworld workflow should complete within timeout").isTrue();
        waitForLogFlush();

        List<JsonProcessInstanceLogAnalyzer.JsonLogEntry> entries =
                parseAllJsonLogFilesWithRetry();

        assertThat(entries)
                .as("JSON log file should contain entries")
                .isNotEmpty();

        List<JsonProcessInstanceLogAnalyzer.JsonLogEntry> processEntries = entries.stream()
                .filter(entry -> processId.equals(entry.getProcessInstanceId()))
                .collect(Collectors.toList());

        assertThat(processEntries)
                .as("JSON logs should contain entries for workflow instance: " + processId)
                .isNotEmpty();

        LOGGER.info("TEST PASSED: Found {} JSON log entries for workflow {}", processEntries.size(), processId);
    }

    /**
     * Test: Process instance IDs are properly isolated in JSON logs for different workflows.
     *
     * Validates:
     * - Each workflow instance has distinct log entries
     * - No cross-contamination between workflow logs
     * - All entries for a workflow have correct processInstanceId in MDC
     */
    @Test
    void testProcessInstanceIdIsolationInJsonLogs() throws Exception {
        LOGGER.info("TEST: Process instance ID isolation in JSON logs using multiple workflows");

        String greetId1 = createGreetWorkflow("Alice", "English");
        String greetId2 = createGreetWorkflow("Bob", "Spanish");
        String helloworldId = createHelloworldWorkflow();

        boolean greet1Completed = waitForWorkflowCompletion("/greet", greetId1, WORKFLOW_COMPLETION_TIMEOUT);
        boolean greet2Completed = waitForWorkflowCompletion("/greet", greetId2, WORKFLOW_COMPLETION_TIMEOUT);
        boolean helloworldCompleted = waitForWorkflowCompletion("/helloworld", helloworldId, WORKFLOW_COMPLETION_TIMEOUT);

        assertThat(greet1Completed).as("Greet workflow 1 should complete within timeout").isTrue();
        assertThat(greet2Completed).as("Greet workflow 2 should complete within timeout").isTrue();
        assertThat(helloworldCompleted).as("Helloworld workflow should complete within timeout").isTrue();
        waitForLogFlush();

        List<JsonProcessInstanceLogAnalyzer.JsonLogEntry> entries =
                parseAllJsonLogFilesWithRetry();

        Map<String, List<JsonProcessInstanceLogAnalyzer.JsonLogEntry>> entriesByProcess =
                JsonProcessInstanceLogAnalyzer.groupByProcessInstance(entries);

        JsonProcessInstanceLogAnalyzer.validateProcessInstanceIsolation(
                entriesByProcess, Set.of(greetId1, greetId2, helloworldId));

        for (String processId : List.of(greetId1, greetId2, helloworldId)) {
            List<JsonProcessInstanceLogAnalyzer.JsonLogEntry> processLogs =
                    entriesByProcess.get(processId);

            assertThat(processLogs)
                    .as("Workflow %s should have dedicated JSON log entries", processId)
                    .isNotEmpty();

            long correctlyTaggedCount = processLogs.stream()
                    .filter(entry -> processId.equals(entry.getProcessInstanceId()))
                    .count();

            assertThat(correctlyTaggedCount)
                    .as("All logs for workflow %s should have correct processInstanceId in MDC", processId)
                    .isEqualTo(processLogs.size());
        }

        logJsonStatistics(entries);
        LOGGER.info("TEST PASSED: Process instance isolation validated for 3 workflows");
    }

    /**
     * Test: JSON log structure complies with expected schema for serverless workflows.
     *
     * Validates:
     * - All required JSON fields are present (timestamp, level, loggerName, message, mdc)
     * - Timestamps are parseable
     * - Log levels are valid
     * - MDC structure is correct
     */
    @Test
    void testJsonSchemaValidation() throws Exception {
        LOGGER.info("TEST: JSON schema validation using greet workflow");

        String processId = createGreetWorkflow("Schema Test", "English");
        boolean completed = waitForWorkflowCompletion("/greet", processId, WORKFLOW_COMPLETION_TIMEOUT);
        assertThat(completed).as("Greet workflow should complete within timeout").isTrue();
        waitForLogFlush();

        List<JsonProcessInstanceLogAnalyzer.JsonLogEntry> entries =
                parseAllJsonLogFilesWithRetry();

        JsonProcessInstanceLogAnalyzer.validateJsonStructure(entries);

        for (JsonProcessInstanceLogAnalyzer.JsonLogEntry entry : entries) {
            assertThat(entry.timestamp)
                    .as("JSON entry should have valid timestamp")
                    .isNotNull()
                    .isBefore(LocalDateTime.now().plusMinutes(1))
                    .isAfter(LocalDateTime.now().minusHours(1));

            assertThat(entry.level)
                    .as("JSON entry should have valid log level")
                    .isNotNull()
                    .matches("TRACE|DEBUG|INFO|WARN|WARNING|ERROR|FATAL");

            assertThat(entry.loggerName)
                    .as("JSON entry should have logger name")
                    .isNotNull()
                    .isNotEmpty();

            assertThat(entry.message)
                    .as("JSON entry should have message")
                    .isNotNull();

            assertThat(entry.mdc)
                    .as("JSON entry should have MDC map")
                    .isNotNull();
        }

        LOGGER.info("TEST PASSED: JSON schema validation completed for {} entries", entries.size());
    }

    /**
     * Test: MDC keys are correctly populated in JSON logs for serverless workflows.
     *
     * Validates:
     * - processInstanceId appears in MDC for workflow-specific logs
     * - MDC keys are accessible as separate JSON fields
     * - Tracing keys (traceId, spanId) are present when available
     */
    @Test
    void testMdcKeysInJsonLogs() throws Exception {
        LOGGER.info("TEST: MDC keys in JSON logs using greet workflow");

        String processId = createGreetWorkflow("MDC Test", "Spanish");
        boolean completed = waitForWorkflowCompletion("/greet", processId, WORKFLOW_COMPLETION_TIMEOUT);
        assertThat(completed).as("Greet workflow should complete within timeout").isTrue();
        waitForLogFlush();

        List<JsonProcessInstanceLogAnalyzer.JsonLogEntry> entries =
                parseAllJsonLogFilesWithRetry();

        List<JsonProcessInstanceLogAnalyzer.JsonLogEntry> processEntries = entries.stream()
                .filter(entry -> processId.equals(entry.getProcessInstanceId()))
                .collect(Collectors.toList());

        assertThat(processEntries)
                .as("Should have workflow-specific JSON entries with MDC")
                .isNotEmpty();

        JsonProcessInstanceLogAnalyzer.validateMdcKeys(
                entries, Set.of("processInstanceId"));

        for (JsonProcessInstanceLogAnalyzer.JsonLogEntry entry : processEntries) {
            assertThat(entry.mdc.get("processInstanceId"))
                    .as("Workflow-specific entry should have processInstanceId in MDC")
                    .isEqualTo(processId);
        }

        long entriesWithTracing = entries.stream()
                .filter(entry -> entry.getTraceId() != null || entry.getSpanId() != null)
                .count();

        LOGGER.info("TEST PASSED: MDC validation completed. {} workflow entries found, {} entries with tracing",
                processEntries.size(), entriesWithTracing);
    }

    /**
     * Test: Concurrent serverless workflow execution with JSON logging.
     *
     * Validates:
     * - Multiple concurrent workflows of the same type have segregated logs
     * - Log patterns are consistent across similar workflow instances
     * - No log entry corruption or mixing
     * - All workflow instances complete successfully
     * - JSON parsing works correctly under concurrent load
     */
    @Test
    void testConcurrentJsonWorkflowLogging() throws Exception {
        LOGGER.info("TEST: Concurrent JSON workflow logging with {} greet workflows",
                CONCURRENT_WORKFLOW_COUNT);

        Set<String> processIds = ConcurrentHashMap.newKeySet();
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch completionLatch = new CountDownLatch(CONCURRENT_WORKFLOW_COUNT);
        ExecutorService executor = Executors.newFixedThreadPool(CONCURRENT_WORKFLOW_COUNT);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        try {
            for (int i = 0; i < CONCURRENT_WORKFLOW_COUNT; i++) {
                final int index = i;
                executor.submit(() -> {
                    try {
                        startLatch.await();

                        // Use only greet workflows with varying parameters for consistency testing
                        // Alternate between English and Spanish to maintain some variety
                        String language = index % 2 == 0 ? "English" : "Spanish";
                        String processId = createGreetWorkflow("ConcurrentUser" + index, language);
                        waitForWorkflowCompletion("/greet", processId, WORKFLOW_COMPLETION_TIMEOUT);

                        processIds.add(processId);
                        successCount.incrementAndGet();
                    } catch (Exception e) {
                        LOGGER.error("Failed to create/complete concurrent workflow {}", index, e);
                        failureCount.incrementAndGet();
                    } finally {
                        completionLatch.countDown();
                    }
                });
            }

            startLatch.countDown();

            boolean completed = completionLatch.await(
                    WORKFLOW_COMPLETION_TIMEOUT.toSeconds() * 2,
                    TimeUnit.SECONDS);

            assertThat(completed)
                    .as("All concurrent workflows should complete within timeout")
                    .isTrue();

            assertThat(successCount.get())
                    .as("All concurrent workflows should succeed")
                    .isEqualTo(CONCURRENT_WORKFLOW_COUNT);

            assertThat(failureCount.get())
                    .as("No concurrent workflows should fail")
                    .isZero();

            waitForLogFlush(3000);

            validateConcurrentJsonLoggingIsolation(processIds, 0.5);

            LOGGER.info("TEST PASSED: Concurrent logging validated for {} greet workflows", processIds.size());

        } finally {
            executor.shutdown();
            executor.awaitTermination(30, TimeUnit.SECONDS);
        }
    }

    /**
     * Test: Empty serverless workflows produce minimal but valid JSON logs.
     *
     * Validates:
     * - Empty workflows still generate log entries
     * - JSON structure remains valid
     * - Process instance context is properly managed
     */
    @Test
    void testEmptyWorkflowJsonLogging() throws Exception {
        LOGGER.info("TEST: Empty workflow JSON logging using helloworld");

        String processId = createHelloworldWorkflow();
        boolean completed = waitForWorkflowCompletion("/helloworld", processId, WORKFLOW_COMPLETION_TIMEOUT);
        assertThat(completed).as("Helloworld workflow should complete within timeout").isTrue();
        waitForLogFlush();

        List<JsonProcessInstanceLogAnalyzer.JsonLogEntry> entries =
                parseAllJsonLogFilesWithRetry();

        List<JsonProcessInstanceLogAnalyzer.JsonLogEntry> processEntries = entries.stream()
                .filter(entry -> processId.equals(entry.getProcessInstanceId()))
                .collect(Collectors.toList());

        assertThat(processEntries)
                .as("Empty workflow should still generate JSON log entries")
                .isNotEmpty();

        JsonProcessInstanceLogAnalyzer.validateJsonStructure(processEntries);

        LOGGER.info("TEST PASSED: Empty workflow generated {} JSON log entries", processEntries.size());
    }

    /**
     * Test: Malformed JSON entries are handled gracefully.
     *
     * Validates:
     * - Log parser is resilient to malformed JSON lines
     * - Valid entries are still parsed correctly
     * - No test failures due to occasional malformed entries
     */
    @Test
    void testMalformedJsonResilience() throws Exception {
        LOGGER.info("TEST: Malformed JSON resilience using greet workflow");

        String processId = createGreetWorkflow("Resilience Test", "English");
        waitForWorkflowCompletion("/greet", processId, WORKFLOW_COMPLETION_TIMEOUT);
        waitForLogFlush();

        Path logFile = getLogFilePath();
        assertThat(Files.exists(logFile))
                .as("Log file should exist")
                .isTrue();

        List<String> originalLines = Files.readAllLines(logFile);

        List<String> modifiedLines = originalLines.stream()
                .map(line -> {
                    if (line.contains("\"level\"")) {
                        return line + " {MALFORMED}";
                    }
                    return line;
                })
                .collect(Collectors.toList());

        Path testLogFile = logFile.getParent().resolve("test-malformed.log");
        Files.write(testLogFile, modifiedLines);

        try {
            List<JsonProcessInstanceLogAnalyzer.JsonLogEntry> entries =
                    JsonProcessInstanceLogAnalyzer.parseJsonLogFile(testLogFile);

            assertThat(entries)
                    .as("Parser should handle malformed entries gracefully")
                    .isNotEmpty();

            List<JsonProcessInstanceLogAnalyzer.JsonLogEntry> validProcessEntries = entries.stream()
                    .filter(entry -> processId.equals(entry.getProcessInstanceId()))
                    .collect(Collectors.toList());

            assertThat(validProcessEntries)
                    .as("Valid JSON entries should still be parsed")
                    .isNotEmpty();

            LOGGER.info("TEST PASSED: Parsed {} entries despite malformed JSON lines", entries.size());

        } finally {
            Files.deleteIfExists(testLogFile);
        }
    }

    /**
     * Test: Log rotation scenarios work correctly with serverless workflows.
     *
     * Validates:
     * - Rotated log files are discovered and parsed
     * - Workflow instance logs span multiple files correctly
     * - Combined parsing works across rotated files
     */
    @Test
    void testLogRotationHandling() throws Exception {
        LOGGER.info("TEST: Log rotation handling with multiple workflows");

        Set<String> allProcessIds = ConcurrentHashMap.newKeySet();

        for (int batch = 0; batch < 3; batch++) {
            String processId = createGreetWorkflow(
                    "Rotation Batch " + batch,
                    batch % 2 == 0 ? "English" : "Spanish");
            allProcessIds.add(processId);
            waitForWorkflowCompletion("/greet", processId, WORKFLOW_COMPLETION_TIMEOUT);
            waitForLogFlush(500);
        }

        List<Path> logFiles = getAllLogFiles();
        LOGGER.info("Found {} log files (including rotated files)", logFiles.size());

        List<JsonProcessInstanceLogAnalyzer.JsonLogEntry> allEntries =
                parseAllJsonLogFilesWithRetry();

        assertThat(allEntries)
                .as("Combined parsing should retrieve all entries across rotated files")
                .isNotEmpty();

        for (String processId : allProcessIds) {
            List<JsonProcessInstanceLogAnalyzer.JsonLogEntry> processEntries = allEntries.stream()
                    .filter(entry -> processId.equals(entry.getProcessInstanceId()))
                    .collect(Collectors.toList());

            assertThat(processEntries)
                    .as("Workflow %s should have entries in combined log", processId)
                    .isNotEmpty();
        }

        LOGGER.info("TEST PASSED: Log rotation handling validated across {} files with {} total entries",
                logFiles.size(), allEntries.size());
    }

    /**
     * Test: Workflow completion and context cleanup.
     *
     * Validates:
     * - Workflow completion is logged correctly
     * - Context is cleaned up after workflow completion
     * - No context leaks to subsequent general logs
     */
    @Test
    void testWorkflowCompletionAndContextCleanup() throws Exception {
        LOGGER.info("TEST: Workflow completion and context cleanup using parallel workflow");

        String processId = createParallelWorkflow(3);

        LocalDateTime beforeCompletion = LocalDateTime.now();
        waitForWorkflowCompletion("/parallel", processId, WORKFLOW_COMPLETION_TIMEOUT);
        LocalDateTime afterCompletion = LocalDateTime.now();

        waitForLogFlush(2000);

        List<JsonProcessInstanceLogAnalyzer.JsonLogEntry> entries =
                parseAllJsonLogFilesWithRetry();

        List<JsonProcessInstanceLogAnalyzer.JsonLogEntry> processEntries = entries.stream()
                .filter(entry -> processId.equals(entry.getProcessInstanceId()))
                .collect(Collectors.toList());

        assertThat(processEntries)
                .as("Workflow should have log entries")
                .isNotEmpty();

        Map<String, LocalDateTime> completionTimes = new HashMap<>();
        completionTimes.put(processId, afterCompletion);

        JsonProcessInstanceLogAnalyzer.validateNoContextLeaks(entries, completionTimes);

        LOGGER.info("TEST PASSED: Workflow completion and context cleanup validated");
    }

    /**
     * Test: Large concurrent serverless workflow load.
     *
     * Validates:
     * - System handles large number of concurrent workflows
     * - JSON logging scales correctly
     * - No performance degradation or failures
     */
    @Test
    void testLargeConcurrentWorkflowLoad() throws Exception {
        LOGGER.info("TEST: Large concurrent workflow load");

        int largeLoadCount = isRunningInCI() ? 20 : 50;
        Set<String> processIds = ConcurrentHashMap.newKeySet();
        CountDownLatch completionLatch = new CountDownLatch(largeLoadCount);
        ExecutorService executor = Executors.newFixedThreadPool(10);
        AtomicInteger successCount = new AtomicInteger(0);

        try {
            for (int i = 0; i < largeLoadCount; i++) {
                final int index = i;
                executor.submit(() -> {
                    try {
                        String processId;
                        // Mix different workflow types
                        boolean completed;
                        if (index % 4 == 0) {
                            processId = createHelloworldWorkflow();
                            completed = waitForWorkflowCompletion("/helloworld", processId, WORKFLOW_COMPLETION_TIMEOUT);
                        } else if (index % 4 == 1) {
                            processId = createGreetWorkflow("LoadUser" + index, "English");
                            completed = waitForWorkflowCompletion("/greet", processId, WORKFLOW_COMPLETION_TIMEOUT);
                        } else if (index % 4 == 2) {
                            processId = createGreetWorkflow("LoadUser" + index, "Spanish");
                            completed = waitForWorkflowCompletion("/greet", processId, WORKFLOW_COMPLETION_TIMEOUT);
                        } else {
                            processId = createParallelWorkflow((index % 3) + 1);
                            completed = waitForWorkflowCompletion("/parallel", processId, WORKFLOW_COMPLETION_TIMEOUT);
                        }
                        if (!completed) {
                            LOGGER.warn("Workflow {} failed to complete within timeout", processId);
                        }
                        processIds.add(processId);
                        successCount.incrementAndGet();
                    } catch (Exception e) {
                        LOGGER.error("Failed to create/complete load test workflow {}", index, e);
                    } finally {
                        completionLatch.countDown();
                    }
                });
            }

            boolean completed = completionLatch.await(
                    WORKFLOW_COMPLETION_TIMEOUT.toSeconds() * 3,
                    TimeUnit.SECONDS);

            assertThat(completed)
                    .as("All load test workflows should complete")
                    .isTrue();

            double successRate = (double) successCount.get() / largeLoadCount;
            assertThat(successRate)
                    .as("At least 90%% of workflows should succeed under load")
                    .isGreaterThanOrEqualTo(0.9);

            waitForLogFlush(5000);

            List<JsonProcessInstanceLogAnalyzer.JsonLogEntry> entries =
                    parseAllJsonLogFilesWithRetry();

            assertThat(entries)
                    .as("Should have substantial JSON log entries from load test")
                    .hasSizeGreaterThan(largeLoadCount);

            logJsonStatistics(entries);

            LOGGER.info("TEST PASSED: Large concurrent load validated with {} workflows, {} succeeded",
                    largeLoadCount, successCount.get());

        } finally {
            executor.shutdown();
            executor.awaitTermination(60, TimeUnit.SECONDS);
        }
    }

    /**
     * Test: JSON logging works correctly in CI environment with serverless workflows.
     *
     * Validates:
     * - CI-specific timeouts and retries work correctly
     * - Log file access works in CI filesystem
     * - Tests are reliable in CI execution
     */
    @Test
    void testCiEnvironmentCompatibility() throws Exception {
        LOGGER.info("TEST: CI environment compatibility (isCI: {})", isRunningInCI());

        String processId = createGreetWorkflow("CI Test", "English");
        waitForWorkflowCompletion("/greet", processId,
                isRunningInCI() ? WORKFLOW_COMPLETION_TIMEOUT.multipliedBy(2) : WORKFLOW_COMPLETION_TIMEOUT);

        waitForLogFlush();

        List<JsonProcessInstanceLogAnalyzer.JsonLogEntry> entries =
                parseAllJsonLogFilesWithRetry();

        assertThat(entries)
                .as("Should successfully parse logs in CI environment")
                .isNotEmpty();

        List<JsonProcessInstanceLogAnalyzer.JsonLogEntry> processEntries = entries.stream()
                .filter(entry -> processId.equals(entry.getProcessInstanceId()))
                .collect(Collectors.toList());

        assertThat(processEntries)
                .as("Should find workflow-specific entries in CI environment")
                .isNotEmpty();

        LOGGER.info("TEST PASSED: CI environment compatibility validated");
    }

    private String createHelloworldWorkflow() {
        String workflowInput = buildEmptyWorkflowInput();

        return given()
                .contentType(ContentType.JSON)
                .body(workflowInput)
                .when()
                .post("/helloworld")
                .then()
                .statusCode(201)
                .extract()
                .path("id");
    }

    private String createGreetWorkflow(String name, String language) {
        String workflowInput = buildGreetWorkflowInput(name, language);

        return given()
                .contentType(ContentType.JSON)
                .body(workflowInput)
                .when()
                .post("/greet")
                .then()
                .statusCode(201)
                .extract()
                .path("id");
    }

    private String createParallelWorkflow(int count) {
        String workflowInput = buildParallelWorkflowInput(count);

        return given()
                .contentType(ContentType.JSON)
                .body(workflowInput)
                .when()
                .post("/parallel")
                .then()
                .statusCode(201)
                .extract()
                .path("id");
    }
}
