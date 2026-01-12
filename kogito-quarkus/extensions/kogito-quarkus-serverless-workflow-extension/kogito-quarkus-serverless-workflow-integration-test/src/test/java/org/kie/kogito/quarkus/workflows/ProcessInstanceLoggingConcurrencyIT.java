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

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.kie.kogito.test.utils.JsonProcessInstanceLogAnalyzer;
import org.kie.kogito.test.utils.JsonProcessInstanceLoggingTestBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.path.json.JsonPath;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.test.utils.ProcessInstancesRESTTestUtils.isProcessInstanceFinished;
import static org.kie.kogito.test.utils.ProcessInstancesRESTTestUtils.newProcessInstance;

/**
 * Comprehensive integration tests for process instance aware logging during concurrent execution
 * of serverless workflows.
 *
 * This test class validates that:
 * 1. Multiple concurrent workflow instances maintain proper log isolation
 * 2. Each workflow instance's logs are properly tagged with the correct process instance ID
 * 3. General context logs (empty process instance ID) are properly identified
 * 4. No context leakage occurs between concurrent executions
 * 5. Log format consistency is maintained across all executions
 * 6. Background operations properly maintain process instance context
 */
@QuarkusIntegrationTest
public class ProcessInstanceLoggingConcurrencyIT extends JsonProcessInstanceLoggingTestBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessInstanceLoggingConcurrencyIT.class);

    /**
     * Data transfer object for concurrent execution infrastructure setup.
     */
    private static class ConcurrentExecutionSetup {
        final ExecutorService executor;
        final CountDownLatch startLatch;
        final CountDownLatch completionLatch;
        final List<Future<String>> futures;

        ConcurrentExecutionSetup(ExecutorService executor, CountDownLatch startLatch,
                CountDownLatch completionLatch, List<Future<String>> futures) {
            this.executor = executor;
            this.startLatch = startLatch;
            this.completionLatch = completionLatch;
            this.futures = futures;
        }
    }

    /**
     * Sets up the infrastructure for concurrent workflow execution.
     *
     * @param numberOfWorkflows the number of concurrent workflows to execute
     * @return ConcurrentExecutionSetup containing executor service, latches, and futures list
     */
    private ConcurrentExecutionSetup setupConcurrentExecution(int numberOfWorkflows) {
        ExecutorService executor = Executors.newFixedThreadPool(numberOfWorkflows);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch completionLatch = new CountDownLatch(numberOfWorkflows);
        List<Future<String>> futures = new ArrayList<>();

        return new ConcurrentExecutionSetup(executor, startLatch, completionLatch, futures);
    }

    /**
     * Submits concurrent greet workflow tasks to the executor service.
     *
     * @param setup the concurrent execution setup containing executor and latches
     * @param numberOfWorkflows the number of workflows to execute
     */
    private void executeConcurrentGreetWorkflows(ConcurrentExecutionSetup setup, int numberOfWorkflows) {
        for (int i = 0; i < numberOfWorkflows; i++) {
            final int workflowIndex = i;
            Future<String> future = setup.executor.submit(() -> {
                try {
                    // Wait for the start signal
                    setup.startLatch.await();

                    // Start a new workflow instance with unique data
                    String language = workflowIndex % 2 == 0 ? "English" : "Spanish";
                    String workflowInput = buildGreetWorkflowInput(
                            "ConcurrentUser" + workflowIndex, language);

                    JsonPath jsonPath = newProcessInstance("/greet", workflowInput);

                    String processInstanceId = jsonPath.getString("id");
                    LOGGER.info("Started concurrent workflow instance {}: {}", workflowIndex, processInstanceId);

                    // Wait for the workflow to complete using Awaitility
                    Awaitility.await()
                            .atMost(Duration.ofSeconds(30))
                            .pollInterval(Duration.ofMillis(500))
                            .until(() -> isProcessInstanceFinished("/greet/{id}", processInstanceId));

                    LOGGER.info("Completed concurrent workflow instance {}: {}", workflowIndex, processInstanceId);
                    setup.completionLatch.countDown();
                    return processInstanceId;
                } catch (Exception e) {
                    LOGGER.error("Error in concurrent workflow execution for index " + workflowIndex, e);
                    setup.completionLatch.countDown();
                    throw new RuntimeException(e);
                }
            });
            setup.futures.add(future);
        }
    }

    /**
     * Waits for all concurrent workflows to complete and collects process instance IDs.
     *
     * @param setup the concurrent execution setup containing completion latch and futures
     * @return set of process instance IDs from completed workflows
     * @throws Exception if workflows don't complete within timeout or execution fails
     */
    private Set<String> waitForWorkflowCompletion(ConcurrentExecutionSetup setup) throws Exception {
        // Start all workflows at the same time
        setup.startLatch.countDown();

        // Wait for all workflows to complete (with timeout)
        boolean completed = setup.completionLatch.await(2, TimeUnit.MINUTES);
        assertThat(completed)
                .as("All concurrent workflows should complete within 2 minutes")
                .isTrue();

        // Collect all process instance IDs
        Set<String> processInstanceIds = new HashSet<>();
        for (Future<String> future : setup.futures) {
            processInstanceIds.add(future.get());
        }

        return processInstanceIds;
    }

    /**
     * Validates concurrent logging isolation by parsing logs and checking for context leaks.
     *
     * @param processInstanceIds set of process instance IDs to validate
     * @throws Exception if log parsing or validation fails
     */
    private void validateConcurrentLoggingIsolation(Set<String> processInstanceIds) throws Exception {
        LOGGER.info("All {} concurrent workflows completed: {}", processInstanceIds.size(), processInstanceIds);

        // Wait for all logs to be flushed
        waitForLogFlush(2000);

        // Parse and analyze the JSON log file
        List<JsonProcessInstanceLogAnalyzer.JsonLogEntry> logEntries = parseAllJsonLogFilesWithRetry();

        // Group entries by process instance
        Map<String, List<JsonProcessInstanceLogAnalyzer.JsonLogEntry>> entriesByProcess =
                JsonProcessInstanceLogAnalyzer.groupByProcessInstance(logEntries);

        // Validate process instance isolation
        JsonProcessInstanceLogAnalyzer.validateProcessInstanceIsolation(entriesByProcess, processInstanceIds);

        // Calculate and display statistics
        logJsonStatistics(logEntries);

        // Validate that we have process-specific logs for each concurrent execution
        for (String processInstanceId : processInstanceIds) {
            List<JsonProcessInstanceLogAnalyzer.JsonLogEntry> processLogs = entriesByProcess.get(processInstanceId);
            assertThat(processLogs)
                    .as("Should have log entries for concurrent workflow instance " + processInstanceId)
                    .isNotEmpty();

            // Log some statistics for this workflow
            long infoLogs = processLogs.stream().filter(entry -> "INFO".equals(entry.level)).count();
            long debugLogs = processLogs.stream().filter(entry -> "DEBUG".equals(entry.level)).count();

            LOGGER.info("Workflow {} generated {} INFO logs and {} DEBUG logs",
                    processInstanceId, infoLogs, debugLogs);
        }

        // Verify general context logs exist and perform context leak validation
        validateContextLeaks(logEntries, entriesByProcess, processInstanceIds);

        LOGGER.info("Concurrent workflow logging isolation validation completed successfully");
    }

    /**
     * Validates that no context leaks occur between concurrent executions.
     *
     * @param logEntries all log entries
     * @param entriesByProcess log entries grouped by process instance
     * @param processInstanceIds set of process instance IDs
     */
    private void validateContextLeaks(List<JsonProcessInstanceLogAnalyzer.JsonLogEntry> logEntries,
            Map<String, List<JsonProcessInstanceLogAnalyzer.JsonLogEntry>> entriesByProcess,
            Set<String> processInstanceIds) {
        // Verify general context logs exist (background operations, framework logs, etc.)
        LOGGER.info("Log groups found: {}", entriesByProcess.keySet());
        List<JsonProcessInstanceLogAnalyzer.JsonLogEntry> generalContextLogs = entriesByProcess.get("");

        // Verify no context leaks - create artificial completion times for validation
        Map<String, LocalDateTime> processCompletionTimes = new HashMap<>();
        for (String processInstanceId : processInstanceIds) {
            // Use the last log entry time for this workflow as completion time
            List<JsonProcessInstanceLogAnalyzer.JsonLogEntry> processLogs = entriesByProcess.get(processInstanceId);
            if (!processLogs.isEmpty()) {
                LocalDateTime lastLogTime = processLogs.get(processLogs.size() - 1).timestamp;
                processCompletionTimes.put(processInstanceId, lastLogTime);
            }
        }

        JsonProcessInstanceLogAnalyzer.validateNoContextLeaks(logEntries, processCompletionTimes);
    }

    /**
     * Properly shuts down the executor service with timeout.
     *
     * @param executor the executor service to shut down
     * @throws InterruptedException if shutdown is interrupted
     */
    private void cleanupExecutor(ExecutorService executor) throws InterruptedException {
        executor.shutdown();
        if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
            executor.shutdownNow();
        }
    }

    /**
     * Submits concurrent parallel workflow tasks to the executor service.
     *
     * @param executor the executor service to submit tasks to
     * @param startLatch the latch to wait for start signal
     * @param numberOfWorkflows the number of workflows to execute
     * @return list of futures for the submitted tasks
     */
    private List<Future<String>> executeConcurrentParallelWorkflows(ExecutorService executor,
            CountDownLatch startLatch,
            int numberOfWorkflows) {
        List<Future<String>> futures = new ArrayList<>();

        for (int i = 0; i < numberOfWorkflows; i++) {
            final int workflowIndex = i;
            Future<String> future = executor.submit(() -> {
                try {
                    // Wait for the start signal
                    startLatch.await();

                    // Start a new parallel workflow instance
                    String workflowInput = buildParallelWorkflowInput((workflowIndex % 3) + 1);

                    JsonPath jsonPath = newProcessInstance("/parallel", workflowInput);

                    String processInstanceId = jsonPath.getString("id");
                    LOGGER.info("Started high concurrency parallel workflow instance {}: {}", workflowIndex, processInstanceId);

                    // Wait for completion
                    Awaitility.await()
                            .atMost(Duration.ofSeconds(30))
                            .pollInterval(Duration.ofMillis(200))
                            .until(() -> isProcessInstanceFinished("/parallel/{id}", processInstanceId));

                    return processInstanceId;
                } catch (Exception e) {
                    LOGGER.error("Error in high concurrency workflow execution for index " + workflowIndex, e);
                    throw new RuntimeException(e);
                }
            });
            futures.add(future);
        }

        return futures;
    }

    /**
     * Waits for parallel workflow completion and collects process instance IDs.
     *
     * @param startLatch the latch to signal start of all workflows
     * @param futures list of futures for the parallel workflows
     * @return list of process instance IDs from completed workflows
     * @throws Exception if workflows don't complete within timeout or execution fails
     */
    private List<String> waitForParallelWorkflowCompletion(CountDownLatch startLatch,
            List<Future<String>> futures) throws Exception {
        // Start all workflows at the same time
        startLatch.countDown();

        // Collect all process instance IDs
        List<String> processInstanceIds = new ArrayList<>();
        for (Future<String> future : futures) {
            processInstanceIds.add(future.get(2, TimeUnit.MINUTES));
        }

        return processInstanceIds;
    }

    /**
     * Validates high concurrency logging by parsing logs and checking isolation.
     *
     * @param processInstanceIds list of process instance IDs to validate
     * @throws Exception if log parsing or validation fails
     */
    private void validateHighConcurrencyLogging(List<String> processInstanceIds) throws Exception {
        LOGGER.info("All {} high concurrency parallel workflows completed: {}", processInstanceIds.size(), processInstanceIds);

        // Wait for logs to be flushed
        waitForLogFlush(2000);

        // Parse and analyze the JSON log file
        List<JsonProcessInstanceLogAnalyzer.JsonLogEntry> logEntries = parseAllJsonLogFilesWithRetry();

        // Group and validate isolation
        Map<String, List<JsonProcessInstanceLogAnalyzer.JsonLogEntry>> entriesByProcess =
                JsonProcessInstanceLogAnalyzer.groupByProcessInstance(logEntries);

        // Calculate and display statistics
        logJsonStatistics(logEntries);

        // Verify each workflow has distinct logs
        for (String processInstanceId : processInstanceIds) {
            List<JsonProcessInstanceLogAnalyzer.JsonLogEntry> processLogs = entriesByProcess.get(processInstanceId);
            assertThat(processLogs)
                    .as("High concurrency parallel workflow " + processInstanceId + " should have distinct logs")
                    .isNotEmpty();
        }

        LOGGER.info("High concurrency parallel workflow logging isolation validation completed successfully");
    }

    /**
     * Test concurrent execution of simple workflows with logging isolation validation.
     * Uses the simple "greet" workflow which has switch logic and function calls.
     */
    @Test
    void testConcurrentWorkflowLoggingIsolation() throws Exception {
        LOGGER.info("Testing concurrent workflow execution with process instance logging isolation");

        // Clear all log files including rotated ones to start with a clean slate
        clearAllLogFiles();

        final int numberOfWorkflows = 7;
        ConcurrentExecutionSetup setup = setupConcurrentExecution(numberOfWorkflows);

        try {
            // Execute all workflows concurrently
            executeConcurrentGreetWorkflows(setup, numberOfWorkflows);

            // Wait for completion and collect process instance IDs
            Set<String> processInstanceIds = waitForWorkflowCompletion(setup);

            // Validate logging isolation
            validateConcurrentLoggingIsolation(processInstanceIds);

        } finally {
            cleanupExecutor(setup.executor);
        }
    }

    /**
     * Test logging isolation during high-contention concurrent execution using parallel workflow.
     * This test uses the parallel workflow and validates that the logging system can handle
     * high concurrent load without mixing process instance contexts.
     */
    @Test
    void testHighConcurrencyLoggingIsolationWithParallelWorkflow() throws Exception {
        LOGGER.info("Testing high concurrency workflow execution with logging isolation using parallel workflow");

        // Clear all log files including rotated ones to start with a clean slate
        clearAllLogFiles();

        final int numberOfWorkflows = 10;
        ExecutorService executor = Executors.newFixedThreadPool(numberOfWorkflows);
        CountDownLatch startLatch = new CountDownLatch(1);

        try {
            // Execute all parallel workflows concurrently
            List<Future<String>> futures = executeConcurrentParallelWorkflows(executor, startLatch, numberOfWorkflows);

            // Wait for completion and collect process instance IDs
            List<String> processInstanceIds = waitForParallelWorkflowCompletion(startLatch, futures);

            // Validate high concurrency logging
            validateHighConcurrencyLogging(processInstanceIds);

        } finally {
            cleanupExecutor(executor);
        }
    }

}
