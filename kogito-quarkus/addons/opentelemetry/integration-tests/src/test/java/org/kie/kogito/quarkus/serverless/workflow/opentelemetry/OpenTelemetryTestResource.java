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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.common.AttributesBuilder;
import io.opentelemetry.api.trace.SpanContext;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.sdk.common.InstrumentationLibraryInfo;
import io.opentelemetry.sdk.common.InstrumentationScopeInfo;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.data.EventData;
import io.opentelemetry.sdk.trace.data.LinkData;
import io.opentelemetry.sdk.trace.data.SpanData;
import io.opentelemetry.sdk.trace.data.StatusData;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import io.restassured.RestAssured;
import io.restassured.response.Response;

/**
 * Test resource to configure OpenTelemetry for integration tests.
 * For @QuarkusIntegrationTest, this uses HTTP calls to access spans from the application JVM.
 */
public class OpenTelemetryTestResource implements QuarkusTestResourceLifecycleManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(OpenTelemetryTestResource.class);

    @Override
    public Map<String, String> start() {
        // Configuration is handled by application.properties at build-time
        // Quarkus will use the CDI SpanExporter from TestSpanExporterProducer
        return Map.of();
    }

    @Override
    public void stop() {
        // Clear spans via HTTP call to application JVM
        try {
            RestAssured.delete("/test/spans");
        } catch (Exception e) {
            // Ignore cleanup errors
        }
    }

    /**
     * Get the collected spans for testing via HTTP call to application JVM.
     */
    public static List<SpanData>

            getSpans() {
        try {
            Response response = RestAssured.get("/test/spans");
            if (response.getStatusCode() == 200) {
                List<Map<String, Object>> spanMaps = response.jsonPath().getList("$");
                return deserializeSpans(spanMaps);
            }
        } catch (Exception e) {
            // Return empty list if HTTP call fails
        }
        return Collections.emptyList();
    }

    /**
     * Get the count of collected spans via HTTP call to application JVM.
     */
    public static int getSpanCount() {
        try {
            Response response = RestAssured.get("/test/spans/count");
            return response.as(Integer.class);
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * Clear collected spans via HTTP call to application JVM.
     */
    public static void clearSpans() {
        try {
            // loop to clean late spans
            for (int attempt = 0; attempt < 30; attempt++) {
                try {
                    RestAssured.delete("/test/spans");
                    Thread.sleep(100);
                    if (getSpanCount() == 0) {
                        return;
                    }
                } catch (Exception e) {
                    Thread.sleep(100);
                }
            }

            int finalCount = getSpanCount();
            if (finalCount > 0) {
                LOGGER.warn("clearSpans() did not clear all spans after 3 seconds. Remaining: {}", finalCount);
            }

        } catch (Exception e) {
            LOGGER.error("clearSpans() failed", e);
        }
    }

    private static List<SpanData> deserializeSpans(List<Map<String, Object>> spanMaps) {
        List<SpanData> spans = new ArrayList<>();
        for (Map<String, Object> spanMap : spanMaps) {
            spans.add(createSpanDataFromMap(spanMap));
        }
        return spans;
    }

    /**
     * Helper method to safely extract long values from JSON deserialization.
     * Handles Integer, Long, and String representations.
     */
    private static long extractLongValue(Object value) {
        if (value == null) {
            return 0L;
        }
        if (value instanceof Integer) {
            return ((Integer) value).longValue();
        }
        if (value instanceof Long) {
            return (Long) value;
        }
        if (value instanceof String) {
            try {
                return Long.parseLong((String) value);
            } catch (NumberFormatException e) {
                return 0L;
            }
        }
        return 0L;
    }

    @SuppressWarnings("unchecked")
    private static SpanData createSpanDataFromMap(Map<String, Object> spanMap) {
        String name = spanMap.get("name") != null ? (String) spanMap.get("name") : "";
        String traceId = spanMap.get("traceId") != null ? (String) spanMap.get("traceId") : "";
        String spanId = spanMap.get("spanId") != null ? (String) spanMap.get("spanId") : "";

        String parentSpanId = spanMap.get("parentSpanId") != null ? (String) spanMap.get("parentSpanId") : null;
        long startEpochNanos = extractLongValue(spanMap.get("startEpochNanos"));
        long endEpochNanos = extractLongValue(spanMap.get("endEpochNanos"));

        Map<String, Object> attributesMap = spanMap.get("attributes") != null
                ? (Map<String, Object>) spanMap.get("attributes")
                : Collections.emptyMap();

        Attributes attributes = convertToAttributes(attributesMap);

        List<EventData> events = new ArrayList<>();
        if (spanMap.get("events") != null) {
            List<Map<String, Object>> eventMaps = (List<Map<String, Object>>) spanMap.get("events");
            for (Map<String, Object> eventMap : eventMaps) {
                String eventName = eventMap.get("name") != null ? (String) eventMap.get("name") : "";
                Object epochNanosObj = eventMap.get("epochNanos");
                long epochNanos = 0;
                if (epochNanosObj instanceof Integer) {
                    epochNanos = ((Integer) epochNanosObj).longValue();
                } else if (epochNanosObj instanceof Long) {
                    epochNanos = (Long) epochNanosObj;
                }
                Map<String, Object> eventAttrsMap = eventMap.get("attributes") != null
                        ? (Map<String, Object>) eventMap.get("attributes")
                        : Collections.emptyMap();
                Attributes eventAttributes = convertToAttributes(eventAttrsMap);

                final long finalEpochNanos = epochNanos;
                events.add(new EventData() {
                    @Override
                    public String getName() {
                        return eventName;
                    }

                    @Override
                    public Attributes getAttributes() {
                        return eventAttributes;
                    }

                    @Override
                    public long getEpochNanos() {
                        return finalEpochNanos;
                    }

                    @Override
                    public int getTotalAttributeCount() {
                        return eventAttributes.size();
                    }
                });
            }
        }

        final List<EventData> finalEvents = events;
        final String finalParentSpanId = parentSpanId;
        final long finalStartEpochNanos = startEpochNanos;
        final long finalEndEpochNanos = endEpochNanos;

        return new SpanData() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public String getTraceId() {
                return traceId;
            }

            @Override
            public String getSpanId() {
                return spanId;
            }

            @Override
            public Attributes getAttributes() {
                return attributes;
            }

            @Override
            public SpanKind getKind() {
                return SpanKind.INTERNAL;
            }

            @Override
            public SpanContext getSpanContext() {
                return null;
            }

            @Override
            public String getParentSpanId() {
                return finalParentSpanId;
            }

            @Override
            public SpanContext getParentSpanContext() {
                return SpanContext.getInvalid();
            }

            @Override
            public StatusData getStatus() {
                return StatusData.create(StatusCode.OK, "");
            }

            @Override
            public long getStartEpochNanos() {
                return finalStartEpochNanos;
            }

            @Override
            public long getEndEpochNanos() {
                return finalEndEpochNanos;
            }

            @Override
            public List<EventData> getEvents() {
                return finalEvents;
            }

            @Override
            public List<LinkData> getLinks() {
                return Collections.emptyList();
            }

            @Override
            public int getTotalRecordedEvents() {
                return finalEvents.size();
            }

            @Override
            public int getTotalRecordedLinks() {
                return 0;
            }

            @Override
            public int getTotalAttributeCount() {
                return attributes.size();
            }

            @Override
            public InstrumentationLibraryInfo getInstrumentationLibraryInfo() {
                return null;
            }

            @Override
            public boolean hasEnded() {
                return true;
            }

            @Override
            public Resource getResource() {
                return Resource.getDefault();
            }

            @Override
            public InstrumentationScopeInfo getInstrumentationScopeInfo() {
                return InstrumentationScopeInfo.create("test");
            }
        };
    }

    private static Attributes convertToAttributes(Map<String, Object> attributesMap) {
        if (attributesMap == null || attributesMap.isEmpty()) {
            return Attributes.empty();
        }

        AttributesBuilder builder = Attributes.builder();
        for (Map.Entry<String, Object> entry : attributesMap.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (value instanceof String) {
                builder.put(AttributeKey.stringKey(key), (String) value);
            } else if (value instanceof Long) {
                builder.put(AttributeKey.longKey(key), (Long) value);
            } else if (value instanceof Integer) {
                builder.put(AttributeKey.longKey(key), ((Integer) value).longValue());
            } else if (value instanceof Double) {
                builder.put(AttributeKey.doubleKey(key), (Double) value);
            } else if (value instanceof Boolean) {
                builder.put(AttributeKey.booleanKey(key), (Boolean) value);
            }
        }
        return builder.build();
    }
}
