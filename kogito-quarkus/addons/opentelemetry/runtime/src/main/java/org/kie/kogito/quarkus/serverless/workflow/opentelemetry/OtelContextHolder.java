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

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;
import io.opentelemetry.context.Context;

import static org.kie.kogito.quarkus.serverless.workflow.opentelemetry.SonataFlowOtelAttributes.MDCKeys;
import static org.kie.kogito.quarkus.serverless.workflow.opentelemetry.SonataFlowOtelAttributes.RequestProperties;

/**
 * MDC-based context holder for OpenTelemetry context extracted from HTTP headers.
 *
 * This utility class provides thread-safe storage for transaction IDs and tracker attributes
 * extracted from X-TRANSACTION-ID and X-TRACKER-* headers. The context is stored using SLF4J MDC,
 * which properly propagates across thread boundaries in Kogito workflow executions.
 *
 * This follows the same pattern as ProcessInstanceContext for MDC-based context management.
 */
public class OtelContextHolder {

    private static final Logger LOGGER = LoggerFactory.getLogger(OtelContextHolder.class);
    private static final int MAX_CONTEXT_SIZE = 100;
    private static final int TTL_MINUTES = 60;

    private static final Map<String, TimestampedValue<String>> processStartContexts = new ConcurrentHashMap<>();
    private static final Map<String, TimestampedValue<ProcessCompletionContext>> processCompletionContexts = new ConcurrentHashMap<>();
    private static final Map<String, TimestampedValue<Context>> rootContexts = new ConcurrentHashMap<>();
    private static final Map<String, TimestampedValue<String>> activeStateContexts = new ConcurrentHashMap<>();
    private static final Map<String, TimestampedValue<SpanContext>> rootSpanContexts = new ConcurrentHashMap<>();
    private static final ThreadLocal<SpanContext> httpRequestSpanContext = new ThreadLocal<>();
    private static final ThreadLocal<Span> currentWorkflowSpan = new ThreadLocal<>();

    public record ProcessCompletionContext(long durationMs, String outcome) {
    }

    private record TimestampedValue<T> (T value, LocalDateTime timestamp) {
    }

    /**
     * Set the transaction ID in MDC.
     * This is typically extracted from the X-TRANSACTION-ID header.
     *
     * @param transactionId the transaction ID to store
     */
    public static void setTransactionId(String transactionId) {
        if (transactionId != null) {
            MDC.put(MDCKeys.TRANSACTION_ID, transactionId);
        }
    }

    /**
     * Get the transaction ID from MDC.
     *
     * @return the transaction ID, or null if not set
     */
    public static String getTransactionId() {
        return MDC.get(MDCKeys.TRANSACTION_ID);
    }

    /**
     * Set a tracker attribute in MDC.
     * These are typically extracted from X-TRACKER-* headers.
     *
     * @param key the tracker attribute key (e.g., "tracker.customer.id")
     * @param value the tracker attribute value
     */
    public static void setTrackerAttribute(String key, String value) {
        if (key != null && value != null && !value.isEmpty()) {
            MDC.put(MDCKeys.TRACKER_PREFIX + key, value);
        }
    }

    /**
     * Get all tracker attributes from MDC.
     *
     * @return a map of tracker attributes, or empty map if none set
     */
    public static Map<String, String> getTrackerAttributes() {
        Map<String, String> attributes = new HashMap<>();
        Map<String, String> mdcContext = MDC.getCopyOfContextMap();

        if (mdcContext != null) {
            for (Map.Entry<String, String> entry : mdcContext.entrySet()) {
                if (entry.getKey().startsWith(MDCKeys.TRACKER_PREFIX)) {
                    String key = entry.getKey().substring(MDCKeys.TRACKER_PREFIX.length());
                    attributes.put(key, entry.getValue());
                }
            }
        }

        return attributes;
    }

    /**
     * Get the complete extracted context from MDC.
     * This includes both transaction ID and tracker attributes.
     *
     * @return a map containing all extracted context
     */
    public static Map<String, String> getExtractedContext() {
        Map<String, String> context = new HashMap<>();

        String transactionId = getTransactionId();
        if (transactionId != null) {
            context.put(RequestProperties.TRANSACTION_ID, transactionId);
        }

        context.putAll(getTrackerAttributes());
        return context;
    }

    /**
     * Populate MDC with OpenTelemetry context from extracted context map.
     * Used to establish context for subflows that have headers but no MDC context.
     *
     * @param extractedContext map with transaction.id and tracker.* keys (RequestProperties format)
     */
    public static void populateFromExtractedContext(Map<String, String> extractedContext) {
        if (extractedContext == null || extractedContext.isEmpty()) {
            return;
        }

        for (Map.Entry<String, String> entry : extractedContext.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            if (value == null || value.isEmpty()) {
                continue;
            }

            if (RequestProperties.TRANSACTION_ID.equals(key)) {
                setTransactionId(value);
            } else if (key.startsWith(RequestProperties.TRACKER_PREFIX)) {
                String trackerKey = key.substring(RequestProperties.TRACKER_PREFIX.length());
                setTrackerAttribute(trackerKey, value);
            }
        }
    }

    /**
     * Clear all OpenTelemetry context from MDC.
     * This should be called at the end of HTTP request processing.
     * Note: Process contexts are managed separately via clearProcessContexts(processInstanceId).
     */
    public static void clear() {
        MDC.remove(MDCKeys.TRANSACTION_ID);

        Map<String, String> mdcContext = MDC.getCopyOfContextMap();
        if (mdcContext != null) {
            mdcContext.keySet().stream()
                    .filter(key -> key.startsWith(MDCKeys.TRACKER_PREFIX))
                    .forEach(MDC::remove);
        }

        // Process contexts are process-scoped, not request-scoped.
        // They are cleaned via clearProcessContexts(processInstanceId) when processes complete.
    }

    /**
     * Clear process-specific contexts for a single process instance.
     * This is useful for cleanup when a process completes.
     *
     * @param processInstanceId the process instance ID
     */
    public static void clearProcessContexts(String processInstanceId) {
        if (processInstanceId != null) {
            processStartContexts.remove(processInstanceId);
            processCompletionContexts.remove(processInstanceId);
            rootContexts.remove(processInstanceId);
            activeStateContexts.remove(processInstanceId);
            rootSpanContexts.remove(processInstanceId);
        }
    }

    public static void setProcessStartContext(String processInstanceId, String transactionId) {
        if (processInstanceId != null && transactionId != null) {
            processStartContexts.put(processInstanceId, new TimestampedValue<>(transactionId, LocalDateTime.now()));
            enforceMaxSize();
        }
    }

    public static String getProcessStartContext(String processInstanceId) {
        TimestampedValue<String> timestamped = processStartContexts.get(processInstanceId);
        return timestamped != null ? timestamped.value() : null;
    }

    public static void clearProcessStartContext(String processInstanceId) {
        processStartContexts.remove(processInstanceId);
    }

    public static void setProcessCompletionContext(String processInstanceId, long durationMs, String outcome) {
        if (processInstanceId != null) {
            processCompletionContexts.put(processInstanceId,
                    new TimestampedValue<>(new ProcessCompletionContext(durationMs, outcome), LocalDateTime.now()));
            enforceMaxSize();
        }
    }

    public static ProcessCompletionContext getProcessCompletionContext(String processInstanceId) {
        TimestampedValue<ProcessCompletionContext> timestamped = processCompletionContexts.get(processInstanceId);
        return timestamped != null ? timestamped.value() : null;
    }

    public static void clearProcessCompletionContext(String processInstanceId) {
        processCompletionContexts.remove(processInstanceId);
    }

    /**
     * Set the root OpenTelemetry context for a process instance.
     * This captures the HTTP request span context that all regular nodes will use as parent.
     *
     * @param processInstanceId the process instance ID
     * @param context the OpenTelemetry context (typically containing the HTTP request span)
     */
    public static void setRootContext(String processInstanceId, Context context) {
        if (processInstanceId != null && context != null) {
            rootContexts.put(processInstanceId, new TimestampedValue<>(context, LocalDateTime.now()));
            enforceMaxSize();
        }
    }

    /**
     * Get the stored root OpenTelemetry context for a process instance.
     *
     * @param processInstanceId the process instance ID
     * @return the stored context, or null if not set
     */
    public static Context getRootContext(String processInstanceId) {
        TimestampedValue<Context> timestamped = rootContexts.get(processInstanceId);
        return timestamped != null ? timestamped.value() : null;
    }

    /**
     * Clear the root context for a specific process instance.
     *
     * @param processInstanceId the process instance ID
     */
    public static void clearRootContext(String processInstanceId) {
        rootContexts.remove(processInstanceId);
    }

    public static void setActiveState(String processInstanceId, String stateName) {
        if (processInstanceId != null && stateName != null) {
            activeStateContexts.put(processInstanceId, new TimestampedValue<>(stateName, LocalDateTime.now()));
            enforceMaxSize();
        }
    }

    public static String getActiveState(String processInstanceId) {
        TimestampedValue<String> timestamped = activeStateContexts.get(processInstanceId);
        return timestamped != null ? timestamped.value() : null;
    }

    public static void clearActiveState(String processInstanceId) {
        activeStateContexts.remove(processInstanceId);
    }

    public static void setRootSpanContext(String processInstanceId, SpanContext spanContext) {
        if (processInstanceId != null && spanContext != null) {
            rootSpanContexts.put(processInstanceId, new TimestampedValue<>(spanContext, LocalDateTime.now()));
            enforceMaxSize();
        }
    }

    public static SpanContext getRootSpanContext(String processInstanceId) {
        if (processInstanceId == null) {
            return null;
        }
        TimestampedValue<SpanContext> timestamped = rootSpanContexts.get(processInstanceId);
        return timestamped != null ? timestamped.value() : null;
    }

    public static void clearRootSpanContext(String processInstanceId) {
        rootSpanContexts.remove(processInstanceId);
    }

    public static void setHttpRequestContext(Context context) {
        if (context != null) {
            Span span = Span.fromContext(context);
            if (span != null && span.getSpanContext().isValid()) {
                httpRequestSpanContext.set(span.getSpanContext());
            }
        }
    }

    public static SpanContext getHttpRequestSpanContext() {
        return httpRequestSpanContext.get();
    }

    public static void clearHttpRequestContext() {
        httpRequestSpanContext.remove();
    }

    public static void setCurrentWorkflowSpan(Span span) {
        currentWorkflowSpan.set(span);
    }

    public static Span getCurrentWorkflowSpan() {
        return currentWorkflowSpan.get();
    }

    public static void clearCurrentWorkflowSpan() {
        currentWorkflowSpan.remove();
    }

    public static void cleanupExpiredProcessContexts() {
        LocalDateTime cutoff = LocalDateTime.now().minus(TTL_MINUTES, ChronoUnit.MINUTES);

        int removedStart = removeExpiredEntries(processStartContexts, cutoff);
        int removedCompletion = removeExpiredEntries(processCompletionContexts, cutoff);
        int removedRoot = removeExpiredEntries(rootContexts, cutoff);
        int removedRootSpan = removeExpiredEntries(rootSpanContexts, cutoff);

        if (removedStart > 0 || removedCompletion > 0 || removedRoot > 0 || removedRootSpan > 0) {
            LOGGER.debug("Cleaned up {} expired process start contexts, {} completion contexts, {} root contexts, and {} root span contexts",
                    removedStart, removedCompletion, removedRoot, removedRootSpan);
        }
    }

    public static void enforceMaxSize() {
        enforceMapMaxSize(processStartContexts, "start");
        enforceMapMaxSize(processCompletionContexts, "completion");
        enforceMapMaxSize(rootContexts, "root");
        enforceMapMaxSize(activeStateContexts, "activeState");
        enforceMapMaxSize(rootSpanContexts, "rootSpan");
    }

    private static <T> int removeExpiredEntries(Map<String, TimestampedValue<T>> map, LocalDateTime cutoff) {
        int initialSize = map.size();
        map.entrySet().removeIf(entry -> entry.getValue().timestamp().isBefore(cutoff));
        return initialSize - map.size();
    }

    private static <T> void enforceMapMaxSize(Map<String, TimestampedValue<T>> map, String mapName) {
        if (map.size() > MAX_CONTEXT_SIZE) {
            int toRemove = map.size() - MAX_CONTEXT_SIZE;
            var keysToRemove = map.entrySet().stream()
                    .sorted((e1, e2) -> e1.getValue().timestamp().compareTo(e2.getValue().timestamp()))
                    .limit(toRemove)
                    .map(Map.Entry::getKey)
                    .toList();

            keysToRemove.forEach(map::remove);

            LOGGER.warn("Removed {} oldest entries from {} context map due to size limit", toRemove, mapName);
        }
    }
}