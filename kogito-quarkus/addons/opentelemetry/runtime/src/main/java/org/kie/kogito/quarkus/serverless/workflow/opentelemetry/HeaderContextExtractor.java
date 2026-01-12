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

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.MultivaluedMap;

import static org.kie.kogito.quarkus.serverless.workflow.opentelemetry.SonataFlowOtelAttributes.Headers;
import static org.kie.kogito.quarkus.serverless.workflow.opentelemetry.SonataFlowOtelAttributes.RequestProperties;

/**
 * Extracts OpenTelemetry context from HTTP headers.
 * Converts X-TRANSACTION-ID and X-TRACKER-* headers into context
 * properties for span enrichment.
 */
@ApplicationScoped
public class HeaderContextExtractor {

    private static final int MAX_HEADER_LENGTH = 100;
    private static final String HEADER_SANITIZATION_PATTERN = "[\\r\\n\\t]";

    private String sanitizeHeaderValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        String sanitized = value.replaceAll(HEADER_SANITIZATION_PATTERN, "").trim();

        if (sanitized.length() > MAX_HEADER_LENGTH) {
            sanitized = sanitized.substring(0, MAX_HEADER_LENGTH);
        }

        return sanitized.isEmpty() ? null : sanitized;
    }

    /**
     * Extract OpenTelemetry context from HTTP headers.
     *
     * This method processes X-TRANSACTION-ID and X-TRACKER-* headers from HTTP requests
     * and converts them to OpenTelemetry span attributes according to the design specification.
     *
     * @param headers the HTTP headers from the request
     * @return a map containing extracted context with transaction.id and tracker.* keys
     */
    public Map<String, String> extractHeaders(MultivaluedMap<String, String> headers) {
        Map<String, String> context = new HashMap<>();

        // Extract X-TRANSACTION-ID header
        List<String> transactionIds = headers.get(Headers.TRANSACTION_ID);
        if (transactionIds != null && !transactionIds.isEmpty()) {
            String sanitized = sanitizeHeaderValue(transactionIds.get(0));
            if (sanitized != null) {
                context.put(RequestProperties.TRANSACTION_ID, sanitized);
            }
        }

        // Extract X-TRACKER-* headers
        headers.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(Headers.TRACKER_PREFIX))
                .forEach(entry -> {
                    String key = RequestProperties.TRACKER_PREFIX + entry.getKey().substring(Headers.TRACKER_PREFIX.length()).toLowerCase().replace('-', '.');
                    String sanitized = sanitizeHeaderValue(String.join(",", entry.getValue()));
                    if (sanitized != null) {
                        context.put(key, sanitized);
                    }
                });

        return context;
    }

    /**
     * Extract OpenTelemetry context from simple string headers (for testing).
     *
     * @param headers simple map of header name to value
     * @return a map containing extracted context
     */
    public Map<String, String> extractContextFromHeaders(Map<String, String> headers) {
        Map<String, String> context = new HashMap<>();

        for (Map.Entry<String, String> entry : headers.entrySet()) {
            String headerName = entry.getKey();
            String headerValue = entry.getValue();

            if (Headers.TRANSACTION_ID.equals(headerName)) {
                context.put(RequestProperties.TRANSACTION_ID, headerValue);
            } else if (headerName.startsWith(Headers.TRACKER_PREFIX)) {
                String trackerKey = headerName.substring(Headers.TRACKER_PREFIX.length()).toLowerCase().replace('-', '.');
                context.put(RequestProperties.TRACKER_PREFIX + trackerKey, headerValue);
            }
        }

        return context;
    }

    /**
     * Extract OpenTelemetry context from process instance headers.
     * Process headers format: Map<String, List<String>>
     *
     * @param processHeaders the headers from the process instance
     * @return a map containing extracted context with transaction.id and tracker.* keys
     */
    public Map<String, String> extractFromProcessHeaders(Map<String, List<String>> processHeaders) {
        Map<String, String> context = new HashMap<>();

        if (processHeaders == null) {
            return context;
        }

        List<String> transactionIds = processHeaders.get(Headers.TRANSACTION_ID);
        if (transactionIds != null && !transactionIds.isEmpty()) {
            String sanitized = sanitizeHeaderValue(transactionIds.get(0));
            if (sanitized != null) {
                context.put(RequestProperties.TRANSACTION_ID, sanitized);
            }
        }

        processHeaders.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(Headers.TRACKER_PREFIX))
                .forEach(entry -> {
                    String key = RequestProperties.TRACKER_PREFIX + entry.getKey().substring(Headers.TRACKER_PREFIX.length()).toLowerCase().replace('-', '.');
                    List<String> values = entry.getValue();
                    if (values != null && !values.isEmpty()) {
                        String sanitized = sanitizeHeaderValue(String.join(",", values));
                        if (sanitized != null) {
                            context.put(key, sanitized);
                        }
                    }
                });

        return context;
    }
}