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
package org.kie.kogito.test.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static org.awaitility.Awaitility.await;

/**
 * Base class for process instance logging tests providing common functionality.
 * This class eliminates code duplication between different logging test implementations.
 */
public abstract class ProcessInstanceLoggingTestBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessInstanceLoggingTestBase.class);
    public static final int MAX_ROTATED_LOG_FILE = 10;

    /**
     * Get the configured log file path.
     *
     * @return Path to the Quarkus log file
     */
    protected Path getLogFilePath() {
        String logPath = System.getProperty("quarkus.log.file.path", "target/quarkus.log");
        return Paths.get(logPath);
    }

    /**
     * Get all log files including rotated ones in chronological order (newest first).
     * Handles Quarkus default rotation: quarkus.log, quarkus.log.1, quarkus.log.2, etc.
     *
     * @return List of log file paths sorted by modification time (newest first)
     * @throws IOException if directory access fails
     */
    protected List<Path> getAllLogFiles() throws IOException {
        Path logFile = getLogFilePath();
        Path logDir = logFile.getParent();
        String baseName = logFile.getFileName().toString();

        List<Path> logFiles = new ArrayList<>();

        // Add main log file if it exists
        if (Files.exists(logFile)) {
            logFiles.add(logFile);
        }

        // Look for rotated files: baseName.1, baseName.2, etc.
        for (int i = 1; i <= MAX_ROTATED_LOG_FILE; i++) { // Check up to 10 rotated files (more than default max-backup-index=5)
            Path rotatedFile = logDir.resolve(baseName + "." + i);
            if (Files.exists(rotatedFile)) {
                logFiles.add(rotatedFile);
            }
        }

        // Sort by last modified time (newest first) to ensure we read logs in chronological order
        logFiles.sort((p1, p2) -> {
            try {
                return Files.getLastModifiedTime(p2).compareTo(Files.getLastModifiedTime(p1));
            } catch (IOException e) {
                return 0; // Keep original order if we can't compare
            }
        });

        LOGGER.debug("Found {} log files: {}", logFiles.size(), logFiles);
        return logFiles;
    }

    /**
     * Clear a specific log file to start with a clean slate.
     *
     * Warning: If your test uses getAllLogFiles() or parseAllLogFiles() methods later,
     * consider using clearAllLogFiles() instead to avoid inconsistent results from
     * rotated log files.
     *
     * @param logFile Path to the log file
     * @throws IOException if file operations fail
     */
    protected void clearLogFile(Path logFile) throws IOException {
        if (Files.exists(logFile)) {
            Files.write(logFile, new byte[0]);
        }
    }

    /**
     * Clear all log files including rotated ones to start with a clean slate.
     * This ensures we don't have stale logs from previous test runs affecting current test.
     *
     * This is the recommended method for test setup when using getAllLogFiles() or
     * parseAllLogFiles() methods to ensure consistent results.
     *
     * @throws IOException if file operations fail
     */
    protected void clearAllLogFiles() throws IOException {
        try {
            List<Path> logFiles = getAllLogFiles();
            for (Path logFile : logFiles) {
                if (Files.exists(logFile)) {
                    Files.write(logFile, new byte[0]);
                    LOGGER.debug("Cleared log file: {}", logFile);
                }
            }
            LOGGER.info("Cleared {} log files", logFiles.size());
        } catch (IOException e) {
            LOGGER.warn("Failed to clear some log files: {}", e.getMessage());
            // Don't fail the test, just warn
        }
    }

    /**
     * Wait for logs to be flushed to disk.
     *
     * @throws InterruptedException if thread is interrupted
     */
    protected void waitForLogFlush() throws InterruptedException {
        // Increase default wait time for CI environments
        long waitTime = isRunningInCI() ? 3000 : 1000;
        Thread.sleep(waitTime);
    }

    /**
     * Wait for logs to be flushed to disk with custom duration.
     *
     * @param millis milliseconds to wait
     * @throws InterruptedException if thread is interrupted
     */
    protected void waitForLogFlush(long millis) throws InterruptedException {
        // In CI environments, use at least the provided time or 2 seconds, whichever is higher
        long actualWaitTime = isRunningInCI() ? Math.max(millis, 2000) : millis;
        Thread.sleep(actualWaitTime);
    }

    /**
     * Detect if running in CI environment based on common CI environment variables.
     * 
     * @return true if running in CI, false otherwise
     */
    protected boolean isRunningInCI() {
        return System.getenv("CI") != null ||
                System.getenv("JENKINS_URL") != null ||
                System.getenv("GITHUB_ACTIONS") != null ||
                System.getenv("TRAVIS") != null ||
                System.getenv("CIRCLECI") != null ||
                System.getProperty("ci.environment") != null;
    }

    /**
     * Wait for workflow completion using polling.
     *
     * @param workflowPath REST path for the workflow
     * @param processInstanceId Process instance ID
     * @param timeout Maximum time to wait
     * @return true if workflow completed successfully, false if timeout was reached
     */
    protected boolean waitForWorkflowCompletion(String workflowPath, String processInstanceId, Duration timeout) {
        // Extend timeout for CI environments
        Duration actualTimeout = isRunningInCI() ? timeout.multipliedBy(2) : timeout;

        try {
            await()
                    .atMost(actualTimeout)
                    .pollInterval(Duration.ofMillis(500))
                    .until(() -> {
                        try {
                            int statusCode = given()
                                    .contentType(ContentType.JSON)
                                    .accept(ContentType.JSON)
                                    .when()
                                    .get(workflowPath + "/" + processInstanceId)
                                    .then()
                                    .extract()
                                    .statusCode();
                            return statusCode == 404; // Completed and cleaned up
                        } catch (Exception e) {
                            LOGGER.debug("Exception while checking workflow completion: {}", e.getMessage());
                            return false;
                        }
                    });
            return true; // Workflow completed successfully
        } catch (org.awaitility.core.ConditionTimeoutException e) {
            LOGGER.warn("Timeout reached while waiting for workflow completion. ProcessInstanceId: {}", processInstanceId);
            return false; // Timeout was reached
        }
    }

    /**
     * Build input for greet workflow.
     *
     * @param name The name parameter
     * @param language The language parameter
     * @return JSON input string
     */
    protected String buildGreetWorkflowInput(String name, String language) {
        return String.format("{\"name\": \"%s\", \"language\": \"%s\"}", name, language);
    }

    /**
     * Build input for parallel workflow.
     *
     * @param numCompleted The numCompleted parameter
     * @return JSON input string
     */
    protected String buildParallelWorkflowInput(int numCompleted) {
        return String.format("{\"numCompleted\": %d}", numCompleted);
    }

    /**
     * Build empty input for simple workflows like helloworld.
     *
     * @return Empty JSON object string
     */
    protected String buildEmptyWorkflowInput() {
        return "{}";
    }

    /**
     * Build input for token exchange workflow.
     *
     * @param query The query parameter
     * @return JSON input string
     */
    protected String buildTokenExchangeWorkflowInput(String query) {
        return "{\"workflowdata\": {\"query\": \"" + query + "\"} }";
    }
}
