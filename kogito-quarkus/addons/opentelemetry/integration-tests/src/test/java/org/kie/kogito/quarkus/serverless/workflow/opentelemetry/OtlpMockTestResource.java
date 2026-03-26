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

import com.github.tomakehurst.wiremock.stubbing.ServeEvent;

import io.opentelemetry.sdk.trace.data.SpanData;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

/**
 * QuarkusTestResource that manages OTLP mock collector lifecycle.
 * Configures the application to send telemetry to the mock collector instead of real endpoints.
 */
public class OtlpMockTestResource implements QuarkusTestResourceLifecycleManager {

    private static OtlpMockCollector otlpCollector;

    @Override
    public Map<String, String> start() {
        otlpCollector = new OtlpMockCollector();
        otlpCollector.start();

        Map<String, String> config = new HashMap<>();

        // Configure Quarkus OpenTelemetry to use OTLP exporter pointing to our mock
        config.put("quarkus.otel.enabled", "true");

        config.put("quarkus.otel.exporter.otlp.endpoint", otlpCollector.getBaseUrl());

        // Disable other exporters
        config.put("quarkus.otel.exporter.jaeger.enabled", "false");
        config.put("quarkus.otel.exporter.zipkin.enabled", "false");

        // Use HTTP/protobuf protocol (standard OTLP)
        config.put("quarkus.otel.exporter.otlp.protocol", "http/protobuf");

        config.put("otel.metrics.exporter", "none");

        // Immediate export for testing (use simple span processor)
        config.put("quarkus.otel.traces.sampler", "always_on");

        return config;
    }

    @Override
    public void stop() {
        if (otlpCollector != null) {
            otlpCollector.stop();
            otlpCollector = null;
        }
    }

    /**
     * Get the mock collector instance for test validation.
     * Only available during test execution when the resource is active.
     */
    public static OtlpMockCollector getCollector() {
        if (otlpCollector == null) {
            throw new IllegalStateException("OTLP mock collector is not active. Ensure OtlpMockTestResource is configured on your test class.");
        }
        return otlpCollector;
    }

    /**
     * Convenience methods for test validation
     */
    public static List<ServeEvent> getReceivedTracesRequests() {
        return getCollector().getReceivedTracesRequests();
    }

    public static List<ServeEvent> getReceivedMetricsRequests() {
        return getCollector().getReceivedMetricsRequests();
    }

    public static List<ServeEvent> getReceivedLogsRequests() {
        return getCollector().getReceivedLogsRequests();
    }

    public static int getTracesRequestCount() {
        return getCollector().getTracesRequestCount();
    }

    public static int getMetricsRequestCount() {
        return getCollector().getMetricsRequestCount();
    }

    public static int getLogsRequestCount() {
        return getCollector().getLogsRequestCount();
    }

    public static void clearRequests() {
        getCollector().clearRequests();
    }

    public static boolean isCollectorRunning() {
        return otlpCollector != null && otlpCollector.isRunning();
    }

    public static String getCollectorBaseUrl() {
        return getCollector().getBaseUrl();
    }

    /**
     * Extract and parse spans from received OTLP trace requests.
     * This provides compatibility with the old test infrastructure that accessed spans directly.
     */
    public static List<SpanData> getSpans() {
        List<ServeEvent> traceRequests = getReceivedTracesRequests();
        return OtlpDataParser.extractSpansFromTraceRequests(traceRequests);
    }

    /**
     * Get the count of spans from received OTLP trace requests.
     */
    public static int getSpanCount() {
        return getSpans().size();
    }
}
