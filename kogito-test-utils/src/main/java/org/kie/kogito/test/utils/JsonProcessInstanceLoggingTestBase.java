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
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.awaitility.Awaitility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Base class for JSON-based process instance logging tests.
 * Extends the existing ProcessInstanceLoggingTestBase to provide JSON-specific functionality
 * while maintaining compatibility with existing infrastructure.
 */
public abstract class JsonProcessInstanceLoggingTestBase extends ProcessInstanceLoggingTestBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonProcessInstanceLoggingTestBase.class);

    /**
     * Parse all JSON log files (including rotated ones) and combine results.
     *
     * @return Combined JSON log entries from all files, sorted by timestamp
     * @throws IOException if file operations fail
     */
    protected List<JsonProcessInstanceLogAnalyzer.JsonLogEntry> parseAllJsonLogFiles() throws IOException {
        List<Path> logFiles = getAllLogFiles();
        List<JsonProcessInstanceLogAnalyzer.JsonLogEntry> allEntries = new ArrayList<>();

        LOGGER.info("Parsing {} JSON log files for comprehensive analysis", logFiles.size());

        for (Path logFile : logFiles) {
            parseJsonLogFile(logFile, allEntries);
        }

        // Sort all entries by timestamp to ensure chronological order
        allEntries.sort((e1, e2) -> e1.timestamp.compareTo(e2.timestamp));

        LOGGER.info("Combined {} JSON log entries from {} files", allEntries.size(), logFiles.size());

        if (allEntries.isEmpty()) {
            throw new IOException("No JSON log entries found in any log files: " + logFiles);
        }

        // Validate combined results
        JsonProcessInstanceLogAnalyzer.validateJsonStructure(allEntries);

        return allEntries;
    }

    private static void parseJsonLogFile(Path logFile, List<JsonProcessInstanceLogAnalyzer.JsonLogEntry> allEntries) {
        try {
            LOGGER.info("Parsing JSON log file: {}", logFile);
            List<JsonProcessInstanceLogAnalyzer.JsonLogEntry> entries =
                    JsonProcessInstanceLogAnalyzer.parseJsonLogFile(logFile);
            allEntries.addAll(entries);
            LOGGER.debug("Added {} entries from {}", entries.size(), logFile.getFileName());
        } catch (IOException e) {
            LOGGER.warn("Failed to parse JSON log file {}: {}", logFile, e.getMessage());
            // Continue with other files rather than failing completely
        }
    }

    /**
     * Calculate and log JSON statistics for debugging.
     *
     * @param entries JSON log entries to analyze
     */
    protected void logJsonStatistics(List<JsonProcessInstanceLogAnalyzer.JsonLogEntry> entries) {
        JsonProcessInstanceLogAnalyzer.JsonLogStatistics stats =
                JsonProcessInstanceLogAnalyzer.calculateJsonStatistics(entries);
        LOGGER.info("JSON log analysis: {}", stats);
    }

    /**
     * Validate that each process instance has distinct JSON log entries using all available log files.
     *
     * @param processInstanceIds Process instance IDs to validate
     * @throws IOException if file operations fail
     */
    protected void validateProcessInstanceJsonLogsRobust(String... processInstanceIds) throws IOException {
        List<JsonProcessInstanceLogAnalyzer.JsonLogEntry> entries = parseAllJsonLogFilesWithRetry();

        Map<String, List<JsonProcessInstanceLogAnalyzer.JsonLogEntry>> entriesByProcess =
                JsonProcessInstanceLogAnalyzer.groupByProcessInstance(entries);

        JsonProcessInstanceLogAnalyzer.validateProcessInstanceIsolation(
                entriesByProcess, Set.of(processInstanceIds));

        logJsonStatistics(entries);

        // Verify that all process instances generated logs with their respective IDs
        for (String processInstanceId : processInstanceIds) {
            List<JsonProcessInstanceLogAnalyzer.JsonLogEntry> processLogs = entriesByProcess.get(processInstanceId);
            assertThat(processLogs)
                    .as("Process instance " + processInstanceId + " should have dedicated JSON log entries")
                    .isNotEmpty();

            // Validate MDC fields
            validateJsonMdcFields(processLogs, processInstanceId);
        }

        LOGGER.info("Process instance JSON logging validation completed for {} process instances (rotation-aware)", processInstanceIds.length);
    }

    /**
     * Validate MDC fields in JSON log entries for a specific process instance.
     */
    private void validateJsonMdcFields(List<JsonProcessInstanceLogAnalyzer.JsonLogEntry> processLogs, String processInstanceId) {
        for (JsonProcessInstanceLogAnalyzer.JsonLogEntry entry : processLogs) {
            assertThat(entry.mdc)
                    .as("JSON log entry should have MDC fields")
                    .isNotNull();

            assertThat(entry.getProcessInstanceId())
                    .as("JSON log entry should have processInstanceId in MDC")
                    .isEqualTo(processInstanceId);
        }
    }

    /**
     * Parse all JSON log files with retry logic and rotation handling for CI environments.
     *
     * @return Parsed JSON log entries from all available log files
     * @throws IOException if file operations fail after retries
     */
    protected List<JsonProcessInstanceLogAnalyzer.JsonLogEntry> parseAllJsonLogFilesWithRetry() throws IOException {
        int maxRetries = isRunningInCI() ? 3 : 1;
        IOException lastException = null;

        for (int retry = 0; retry < maxRetries; retry++) {
            try {
                List<JsonProcessInstanceLogAnalyzer.JsonLogEntry> entries = parseAllJsonLogFiles(retry, maxRetries);
                if (entries != null)
                    return entries;

            } catch (IOException e) {
                lastException = e;
                LOGGER.warn("Failed to parse JSON log files (attempt {}/{}): {}", retry + 1, maxRetries, e.getMessage());
                if (retry < maxRetries - 1) {
                    Awaitility.await()
                            .pollDelay(Duration.ofMillis(2000))
                            .until(() -> true);
                }
            }
        }

        throw new IOException("Failed to parse JSON log files after " + maxRetries + " attempts", lastException);
    }

    private List<JsonProcessInstanceLogAnalyzer.JsonLogEntry> parseAllJsonLogFiles(int retry, int maxRetries) throws IOException {
        List<JsonProcessInstanceLogAnalyzer.JsonLogEntry> entries = parseAllJsonLogFiles();

        if (entries.isEmpty() && retry < maxRetries - 1) {
            LOGGER.warn("No JSON log entries found in any files, retrying... (attempt {}/{})", retry + 1, maxRetries);
            Awaitility.await()
                    .pollDelay(Duration.ofMillis(2000))
                    .until(() -> true);
            return null;
        }

        return entries;
    }

    /**
     * Validate concurrent JSON logging isolation.
     *
     * @param processInstanceIds Set of process instance IDs from concurrent execution
     * @param minimumOverlapRatio Minimum overlap ratio for pattern consistency
     * @throws IOException if file operations fail
     */
    protected void validateConcurrentJsonLoggingIsolation(Set<String> processInstanceIds, double minimumOverlapRatio) throws IOException {
        List<JsonProcessInstanceLogAnalyzer.JsonLogEntry> entries = parseAllJsonLogFilesWithRetry();

        Map<String, List<JsonProcessInstanceLogAnalyzer.JsonLogEntry>> entriesByProcess =
                JsonProcessInstanceLogAnalyzer.groupByProcessInstance(entries);

        // Validate isolation
        JsonProcessInstanceLogAnalyzer.validateProcessInstanceIsolation(entriesByProcess, processInstanceIds);

        // Validate consistent patterns
        JsonProcessInstanceLogAnalyzer.validateConsistentJsonLogPatterns(entriesByProcess, processInstanceIds, minimumOverlapRatio);

        // Validate JSON structure
        JsonProcessInstanceLogAnalyzer.validateJsonStructure(entries);

        logJsonStatistics(entries);

        LOGGER.info("Concurrent JSON logging isolation validation completed for {} process instances", processInstanceIds.size());
    }

}
