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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

/**
 * Utility class for analyzing process instance aware logging in JSON format.
 * Supports parsing JSON log format with MDC fields including processInstanceId.
 * This class replaces pipe-delimited format parsing for machine-consumable JSON logs.
 */
public class JsonProcessInstanceLogAnalyzer {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonProcessInstanceLogAnalyzer.class);

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    // Common timestamp patterns in JSON logs
    private static final DateTimeFormatter[] TIMESTAMP_FORMATTERS = {
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss,SSS"),
            DateTimeFormatter.ISO_LOCAL_DATE_TIME
    };

    /**
     * Represents a single JSON log entry with all its components.
     */
    public static class JsonLogEntry {
        public final LocalDateTime timestamp;
        public final String level;
        public final String loggerName;
        public final String message;
        public final Map<String, String> mdc;
        public final String threadName;
        public final String sequenceNumber;
        public final JsonNode rawJson;

        public JsonLogEntry(LocalDateTime timestamp, String level, String loggerName,
                String message, Map<String, String> mdc, String threadName,
                String sequenceNumber, JsonNode rawJson) {
            this.timestamp = timestamp;
            this.level = level != null ? level : "INFO";
            this.loggerName = loggerName != null ? loggerName : "unknown.logger";
            this.message = message != null ? message : "";
            this.mdc = mdc != null ? new HashMap<>(mdc) : new HashMap<>();
            this.threadName = threadName;
            this.sequenceNumber = sequenceNumber;
            this.rawJson = rawJson;
        }

        /**
         * Get the process instance ID from MDC.
         */
        public String getProcessInstanceId() {
            return mdc.get("processInstanceId");
        }

        /**
         * Check if this log entry has a process instance ID.
         */
        public boolean hasProcessInstance() {
            String processInstanceId = getProcessInstanceId();
            return processInstanceId != null && !processInstanceId.trim().isEmpty();
        }

        /**
         * Check if this log entry is general context (no process instance ID).
         */
        public boolean isGeneralContext() {
            return !hasProcessInstance();
        }

        /**
         * Get trace ID from MDC if available.
         */
        public String getTraceId() {
            return mdc.get("traceId");
        }

        /**
         * Get span ID from MDC if available.
         */
        public String getSpanId() {
            return mdc.get("spanId");
        }

        @Override
        public String toString() {
            return String.format("JsonLogEntry{timestamp=%s, level=%s, processInstanceId=%s, logger=%s, message=%s}",
                    timestamp, level, getProcessInstanceId(), loggerName, message);
        }
    }

    /**
     * Statistics about JSON log entries for analysis.
     */
    public static class JsonLogStatistics {
        public final long totalLogs;
        public final long processSpecificLogs;
        public final long generalContextLogs;
        public final Map<String, Long> logsByProcessInstance;
        public final Map<String, Long> logsByLevel;
        public final Map<String, Long> logsByLogger;
        public final long logsWithTracing;

        public JsonLogStatistics(List<JsonLogEntry> entries) {
            this.totalLogs = entries.size();
            this.processSpecificLogs = entries.stream().filter(JsonLogEntry::hasProcessInstance).count();
            this.generalContextLogs = entries.stream().filter(JsonLogEntry::isGeneralContext).count();
            this.logsByProcessInstance = entries.stream()
                    .collect(Collectors.groupingBy(
                            entry -> entry.hasProcessInstance() ? entry.getProcessInstanceId() : "",
                            Collectors.counting()));
            this.logsByLevel = entries.stream()
                    .collect(Collectors.groupingBy(entry -> entry.level, Collectors.counting()));
            this.logsByLogger = entries.stream()
                    .collect(Collectors.groupingBy(entry -> entry.loggerName, Collectors.counting()));
            this.logsWithTracing = entries.stream()
                    .filter(entry -> entry.getTraceId() != null)
                    .count();
        }

        @Override
        public String toString() {
            return String.format(
                    "JsonLogStatistics{total=%d, processSpecific=%d, general=%d, byProcess=%s, byLevel=%s, withTracing=%d}",
                    totalLogs, processSpecificLogs, generalContextLogs, logsByProcessInstance, logsByLevel, logsWithTracing);
        }
    }

    /**
     * Parse JSON log file with multiline support and resilient error handling.
     */
    public static List<JsonLogEntry> parseJsonLogFile(Path logFile) throws IOException {
        List<JsonLogEntry> entries = new ArrayList<>();
        AtomicInteger malformedLineCount = new AtomicInteger(0);
        AtomicInteger lineNumber = new AtomicInteger(0);

        try (Stream<String> lines = Files.lines(logFile)) {
            lines.forEach(line -> {
                lineNumber.incrementAndGet();

                if (line.trim().isEmpty()) {
                    return; // Skip empty lines
                }

                try {
                    JsonLogEntry entry = parseJsonLogLine(line, malformedLineCount, lineNumber.get());
                    entries.add(entry);
                } catch (Exception e) {
                    malformedLineCount.incrementAndGet();
                    LOGGER.error(
                            "Failed to parse JSON log line {}: {}",
                            lineNumber.get(), line.substring(0, Math.min(100, line.length())), e);
                }
            });
        }

        // Log statistics about parsing
        if (malformedLineCount.get() > 0) {
            LOGGER.warn("Encountered {} malformed/problematic lines out of {} total lines while parsing {}",
                    malformedLineCount.get(), lineNumber.get(), logFile.getFileName());
        }

        return entries;
    }

    /**
     * Parse a single JSON log line into a JsonLogEntry.
     */
    private static JsonLogEntry parseJsonLogLine(String line, AtomicInteger malformedLineCount, int lineNumber) {
        try {
            JsonNode jsonNode = OBJECT_MAPPER.readTree(line);

            // Extract timestamp
            LocalDateTime timestamp = parseTimestamp(jsonNode, malformedLineCount);

            // Extract standard fields
            String level = getTextValue(jsonNode, "level");
            String loggerName = getTextValue(jsonNode, "loggerName");
            String message = getTextValue(jsonNode, "message");
            String threadName = getTextValue(jsonNode, "thread");
            String sequenceNumber = getTextValue(jsonNode, "sequenceNumber");

            // Extract MDC fields
            Map<String, String> mdc = extractMdcFields(jsonNode);

            return new JsonLogEntry(timestamp, level, loggerName, message, mdc, threadName, sequenceNumber, jsonNode);

        } catch (JsonProcessingException e) {
            // Try fallback parsing for non-JSON lines
            return tryFallbackParsing(line, malformedLineCount);
        }
    }

    /**
     * Extract MDC fields from JSON log entry.
     */
    private static Map<String, String> extractMdcFields(JsonNode jsonNode) {
        Map<String, String> mdc = new HashMap<>();

        // Look for MDC in common field names
        JsonNode mdcNode = jsonNode.get("mdc");
        if (mdcNode == null) {
            mdcNode = jsonNode.get("MDC");
        }
        if (mdcNode == null) {
            mdcNode = jsonNode.get("context");
        }

        if (mdcNode != null && mdcNode.isObject()) {
            mdcNode.fields().forEachRemaining(entry -> {
                String key = entry.getKey();
                JsonNode value = entry.getValue();
                if (value.isTextual()) {
                    mdc.put(key, value.asText());
                } else if (!value.isNull()) {
                    mdc.put(key, value.toString());
                }
            });
        }

        // Also check for direct MDC fields at root level (some formats)
        String[] commonMdcFields = { "processInstanceId", "traceId", "spanId", "userId", "correlationId" };
        for (String field : commonMdcFields) {
            JsonNode fieldNode = jsonNode.get(field);
            if (fieldNode != null && fieldNode.isTextual()) {
                mdc.put(field, fieldNode.asText());
            }
        }

        return mdc;
    }

    /**
     * Parse timestamp from JSON node using multiple format attempts.
     */
    private static LocalDateTime parseTimestamp(JsonNode jsonNode, AtomicInteger malformedLineCount) {
        String timestampStr = getTextValue(jsonNode, "timestamp");
        if (timestampStr == null) {
            timestampStr = getTextValue(jsonNode, "@timestamp");
        }
        if (timestampStr == null) {
            timestampStr = getTextValue(jsonNode, "time");
        }

        if (timestampStr != null) {
            for (DateTimeFormatter formatter : TIMESTAMP_FORMATTERS) {
                try {
                    return LocalDateTime.parse(timestampStr, formatter);
                } catch (DateTimeParseException e) {
                    // Try next formatter
                }
            }
        }

        // Fallback to current time
        malformedLineCount.incrementAndGet();
        return LocalDateTime.now();
    }

    /**
     * Get text value from JSON node, handling null checks.
     */
    private static String getTextValue(JsonNode node, String fieldName) {
        JsonNode fieldNode = node.get(fieldName);
        return fieldNode != null && !fieldNode.isNull() ? fieldNode.asText() : null;
    }

    /**
     * Try fallback parsing for non-JSON lines (stack traces, etc.).
     */
    private static JsonLogEntry tryFallbackParsing(String line, AtomicInteger malformedLineCount) {
        // This could be a stack trace or multiline continuation
        // For now, create a simple entry
        malformedLineCount.incrementAndGet();

        Map<String, String> emptyMdc = new HashMap<>();
        return new JsonLogEntry(
                LocalDateTime.now(),
                "INFO",
                "unknown.logger",
                line,
                emptyMdc,
                null,
                null,
                null);
    }

    /**
     * Group log entries by process instance ID.
     */
    public static Map<String, List<JsonLogEntry>> groupByProcessInstance(List<JsonLogEntry> entries) {
        return entries.stream()
                .collect(Collectors.groupingBy(
                        entry -> entry.hasProcessInstance() ? entry.getProcessInstanceId() : ""));
    }

    /**
     * Calculate statistics for JSON log analysis.
     */
    public static JsonLogStatistics calculateJsonStatistics(List<JsonLogEntry> entries) {
        return new JsonLogStatistics(entries);
    }

    /**
     * Validate that process instances have distinct, non-overlapping log groups.
     */
    public static void validateProcessInstanceIsolation(
            Map<String, List<JsonLogEntry>> entriesByProcess,
            Set<String> expectedProcessInstanceIds) {

        // Validate each expected process has logs
        for (String processId : expectedProcessInstanceIds) {
            List<JsonLogEntry> processLogs = entriesByProcess.get(processId);

            assertThat(processLogs)
                    .as("Should have log entries for process instance " + processId)
                    .isNotEmpty();

            // Validate all entries for this process have correct ID
            long correctlyTagged = processLogs.stream()
                    .filter(entry -> processId.equals(entry.getProcessInstanceId()))
                    .count();

            assertThat(correctlyTagged)
                    .as("All logs for process " + processId + " should have correct process instance ID")
                    .isEqualTo(processLogs.size());
        }
    }

    /**
     * Validate that no context leaks occur - completed process IDs should not appear in later general logs.
     */
    public static void validateNoContextLeaks(List<JsonLogEntry> entries, Map<String, LocalDateTime> processCompletionTimes) {
        for (JsonLogEntry entry : entries) {
            if (entry.isGeneralContext()) {
                // Check if this general log message contains any completed process ID
                for (Map.Entry<String, LocalDateTime> completion : processCompletionTimes.entrySet()) {
                    String processId = completion.getKey();
                    LocalDateTime completionTime = completion.getValue();

                    if (entry.timestamp.isAfter(completionTime) &&
                            entry.message.contains(processId)) {

                        fail(String.format(
                                "Context leak detected: Process ID %s appears in general context log after completion. " +
                                        "Log entry at %s: %s",
                                processId, entry.timestamp, entry.message));
                    }
                }
            }
        }
    }

    /**
     * Validate JSON log structure and required fields.
     */
    public static void validateJsonStructure(List<JsonLogEntry> entries) {
        if (entries.isEmpty()) {
            LOGGER.warn("No JSON log entries found for validation");
            return;
        }

        int validationIssues = 0;
        for (JsonLogEntry entry : entries) {
            try {
                assertThat(entry.timestamp).as("Timestamp should not be null").isNotNull();
                assertThat(entry.level).as("Log level should not be null").isNotNull();
                assertThat(entry.loggerName).as("Logger should not be null").isNotNull();
                assertThat(entry.message).as("Message should not be null").isNotNull();
                assertThat(entry.mdc).as("MDC should not be null").isNotNull();

                // Validate log level
                if (!isValidLogLevel(entry.level)) {
                    validationIssues++;
                    LOGGER.warn("Invalid log level '{}' found in entry", entry.level);
                }
            } catch (AssertionError e) {
                validationIssues++;
                LOGGER.error("JSON log structure validation issue: {}", e.getMessage(), e);
            }
        }

        // Allow some validation issues but warn about them
        if (validationIssues > 0) {
            double errorRate = (double) validationIssues / entries.size();
            if (errorRate > 0.1) { // More than 10% validation issues
                throw new AssertionError(String.format(
                        "Too many JSON log structure validation issues: %d out of %d entries (%.1f%%) have issues",
                        validationIssues, entries.size(), errorRate * 100));
            } else {
                LOGGER.warn("JSON log structure validation completed with {} minor issues out of {} entries ({})",
                        validationIssues, entries.size(), errorRate * 100);
            }
        }
    }

    /**
     * Validate that required MDC keys are present.
     */
    public static void validateMdcKeys(List<JsonLogEntry> entries, Set<String> requiredKeys) {
        List<JsonLogEntry> processSpecificEntries = entries.stream()
                .filter(JsonLogEntry::hasProcessInstance)
                .collect(Collectors.toList());

        if (processSpecificEntries.isEmpty()) {
            LOGGER.warn("No process-specific entries found for MDC validation");
            return;
        }

        int missingKeyIssues = 0;
        for (JsonLogEntry entry : processSpecificEntries) {
            for (String requiredKey : requiredKeys) {
                if (!entry.mdc.containsKey(requiredKey) || entry.mdc.get(requiredKey) == null) {
                    missingKeyIssues++;
                    LOGGER.warn("Required MDC key '{}' missing in entry: {}",
                            requiredKey, entry);
                }
            }
        }

        if (missingKeyIssues > 0) {
            double errorRate = (double) missingKeyIssues / (processSpecificEntries.size() * requiredKeys.size());
            if (errorRate > 0.1) { // More than 10% missing keys
                throw new AssertionError(String.format(
                        "Too many missing MDC keys: %d out of %d expected key instances (%.1f%%) are missing",
                        missingKeyIssues, processSpecificEntries.size() * requiredKeys.size(), errorRate * 100));
            }
        }
    }

    /**
     * Validate consistent log patterns across different process instances.
     */
    public static void validateConsistentJsonLogPatterns(
            Map<String, List<JsonLogEntry>> entriesByProcess,
            Set<String> processInstanceIds,
            double minimumOverlapRatio) {

        if (processInstanceIds.size() < 2) {
            return; // Can't compare patterns with less than 2 processes
        }

        // Extract normalized log patterns for each process
        Map<String, List<String>> patternsByProcess = new HashMap<>();

        for (String processId : processInstanceIds) {
            List<JsonLogEntry> logs = entriesByProcess.get(processId);
            if (logs != null) {
                List<String> patterns = logs.stream()
                        .map(entry -> entry.loggerName + ":" + normalizeMessage(entry.message))
                        .collect(Collectors.toList());
                patternsByProcess.put(processId, patterns);
            }
        }

        // Compare each process pattern with the first one
        String firstProcessId = processInstanceIds.iterator().next();
        List<String> firstProcessPatterns = patternsByProcess.get(firstProcessId);

        if (firstProcessPatterns == null || firstProcessPatterns.isEmpty()) {
            return;
        }

        for (String processId : processInstanceIds) {
            if (processId.equals(firstProcessId)) {
                continue;
            }

            List<String> patterns = patternsByProcess.get(processId);
            if (patterns == null || patterns.isEmpty()) {
                continue;
            }

            // Calculate pattern overlap
            long matchingPatterns = patterns.stream()
                    .filter(firstProcessPatterns::contains)
                    .count();

            double overlapRatio = (double) matchingPatterns / Math.max(firstProcessPatterns.size(), patterns.size());

            assertThat(overlapRatio)
                    .as("Process %s should have similar log patterns to process %s (overlap: %.2f%%)",
                            processId, firstProcessId, overlapRatio * 100)
                    .isGreaterThanOrEqualTo(minimumOverlapRatio);
        }
    }

    /**
     * Check if log level is valid.
     */
    private static boolean isValidLogLevel(String level) {
        if (level == null || level.trim().isEmpty()) {
            return false;
        }
        String normalizedLevel = level.trim().toUpperCase();
        return normalizedLevel.matches("TRACE|DEBUG|INFO|WARN|WARNING|ERROR|FATAL|OFF|ALL") ||
                normalizedLevel.matches("FINE|FINER|FINEST|SEVERE|CONFIG"); // Java util.logging levels
    }

    /**
     * Normalize log message by removing process-specific values for pattern comparison.
     */
    private static String normalizeMessage(String message) {
        if (message == null) {
            return "";
        }

        return message
                // Replace UUIDs with placeholder
                .replaceAll("[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}", "<UUID>")
                // Replace timestamps with placeholder
                .replaceAll("\\d{4}-\\d{2}-\\d{2}[T ]\\d{2}:\\d{2}:\\d{2}[.,]\\d{3}", "<TIMESTAMP>")
                // Replace decimal numbers with placeholder
                .replaceAll("\\d+\\.\\d+", "<NUMBER>")
                // Replace integers with placeholder
                .replaceAll("\\b\\d+\\b", "<NUMBER>")
                // Replace common dynamic values
                .replaceAll("\\b(duration|time|elapsed|took)\\s*[:=]?\\s*\\d+", "$1=<TIME>")
                .trim();
    }
}
