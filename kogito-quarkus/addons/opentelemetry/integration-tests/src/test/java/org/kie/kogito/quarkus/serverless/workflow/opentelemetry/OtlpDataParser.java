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
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tomakehurst.wiremock.stubbing.ServeEvent;
import com.google.protobuf.InvalidProtocolBufferException;

import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.common.AttributesBuilder;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest;
import io.opentelemetry.proto.trace.v1.ResourceSpans;
import io.opentelemetry.proto.trace.v1.ScopeSpans;
import io.opentelemetry.proto.trace.v1.Span;
import io.opentelemetry.sdk.common.InstrumentationScopeInfo;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.data.EventData;
import io.opentelemetry.sdk.trace.data.LinkData;
import io.opentelemetry.sdk.trace.data.SpanData;
import io.opentelemetry.sdk.trace.data.StatusData;

/**
 * Helper class to parse OTLP protobuf data from WireMock ServeEvents.
 * Converts OTLP proto messages into OpenTelemetry SDK SpanData objects for test validation.
 */
public class OtlpDataParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(OtlpDataParser.class);

    public static List<SpanData> extractSpansFromTraceRequests(List<ServeEvent> traceRequests) {
        List<SpanData> allSpans = new ArrayList<>();

        for (ServeEvent event : traceRequests) {
            try {
                byte[] requestBody = event.getRequest().getBody();
                ExportTraceServiceRequest request = ExportTraceServiceRequest.parseFrom(requestBody);

                for (ResourceSpans resourceSpans : request.getResourceSpansList()) {
                    for (ScopeSpans scopeSpans : resourceSpans.getScopeSpansList()) {
                        for (Span span : scopeSpans.getSpansList()) {
                            allSpans.add(convertToSpanData(span, resourceSpans, scopeSpans));
                        }
                    }
                }
            } catch (InvalidProtocolBufferException e) {
                LOGGER.warn("Failed to parse OTLP trace request", e);
            }
        }

        return allSpans;
    }

    private static SpanData convertToSpanData(Span protoSpan, ResourceSpans resourceSpans, ScopeSpans scopeSpans) {
        return new SpanData() {
            @Override
            public String getName() {
                return protoSpan.getName();
            }

            @Override
            public String getTraceId() {
                return bytesToHex(protoSpan.getTraceId().toByteArray());
            }

            @Override
            public String getSpanId() {
                return bytesToHex(protoSpan.getSpanId().toByteArray());
            }

            @Override
            public io.opentelemetry.api.trace.SpanContext getSpanContext() {
                return null;
            }

            @Override
            public String getParentSpanId() {
                if (protoSpan.getParentSpanId().isEmpty()) {
                    return null;
                }
                return bytesToHex(protoSpan.getParentSpanId().toByteArray());
            }

            @Override
            public io.opentelemetry.api.trace.SpanContext getParentSpanContext() {
                return io.opentelemetry.api.trace.SpanContext.getInvalid();
            }

            @Override
            public SpanKind getKind() {
                return convertSpanKind(protoSpan.getKind());
            }

            @Override
            public long getStartEpochNanos() {
                return protoSpan.getStartTimeUnixNano();
            }

            @Override
            public Attributes getAttributes() {
                return convertAttributes(protoSpan.getAttributesList());
            }

            @Override
            public List<EventData> getEvents() {
                return protoSpan.getEventsList().stream()
                        .map(OtlpDataParser::convertEvent)
                        .collect(Collectors.toList());
            }

            @Override
            public List<LinkData> getLinks() {
                return List.of();
            }

            @Override
            public StatusData getStatus() {
                return StatusData.create(
                        convertStatusCode(protoSpan.getStatus().getCode()),
                        protoSpan.getStatus().getMessage());
            }

            @Override
            public long getEndEpochNanos() {
                return protoSpan.getEndTimeUnixNano();
            }

            @Override
            public boolean hasEnded() {
                return protoSpan.getEndTimeUnixNano() > 0;
            }

            @Override
            public int getTotalRecordedEvents() {
                return protoSpan.getEventsCount();
            }

            @Override
            public int getTotalRecordedLinks() {
                return protoSpan.getLinksCount();
            }

            @Override
            public int getTotalAttributeCount() {
                return protoSpan.getAttributesCount();
            }

            @Override
            public Resource getResource() {
                return Resource.getDefault();
            }

            @Override
            public InstrumentationScopeInfo getInstrumentationScopeInfo() {
                return InstrumentationScopeInfo.create(scopeSpans.getScope().getName());
            }

            @Override
            @Deprecated
            public io.opentelemetry.sdk.common.InstrumentationLibraryInfo getInstrumentationLibraryInfo() {
                return io.opentelemetry.sdk.common.InstrumentationLibraryInfo.create(
                        scopeSpans.getScope().getName(),
                        scopeSpans.getScope().getVersion());
            }
        };
    }

    private static EventData convertEvent(Span.Event protoEvent) {
        return new EventData() {
            @Override
            public String getName() {
                return protoEvent.getName();
            }

            @Override
            public Attributes getAttributes() {
                return convertAttributes(protoEvent.getAttributesList());
            }

            @Override
            public long getEpochNanos() {
                return protoEvent.getTimeUnixNano();
            }

            @Override
            public int getTotalAttributeCount() {
                return protoEvent.getAttributesCount();
            }
        };
    }

    private static Attributes convertAttributes(List<io.opentelemetry.proto.common.v1.KeyValue> protoAttributes) {
        AttributesBuilder builder = Attributes.builder();

        for (io.opentelemetry.proto.common.v1.KeyValue kv : protoAttributes) {
            String key = kv.getKey();
            io.opentelemetry.proto.common.v1.AnyValue value = kv.getValue();

            switch (value.getValueCase()) {
                case STRING_VALUE:
                    builder.put(AttributeKey.stringKey(key), value.getStringValue());
                    break;
                case BOOL_VALUE:
                    builder.put(AttributeKey.booleanKey(key), value.getBoolValue());
                    break;
                case INT_VALUE:
                    builder.put(AttributeKey.longKey(key), value.getIntValue());
                    break;
                case DOUBLE_VALUE:
                    builder.put(AttributeKey.doubleKey(key), value.getDoubleValue());
                    break;
                case ARRAY_VALUE:
                case KVLIST_VALUE:
                case BYTES_VALUE:
                case VALUE_NOT_SET:
                default:
                    break;
            }
        }

        return builder.build();
    }

    private static SpanKind convertSpanKind(Span.SpanKind protoKind) {
        switch (protoKind) {
            case SPAN_KIND_INTERNAL:
                return SpanKind.INTERNAL;
            case SPAN_KIND_SERVER:
                return SpanKind.SERVER;
            case SPAN_KIND_CLIENT:
                return SpanKind.CLIENT;
            case SPAN_KIND_PRODUCER:
                return SpanKind.PRODUCER;
            case SPAN_KIND_CONSUMER:
                return SpanKind.CONSUMER;
            default:
                return SpanKind.INTERNAL;
        }
    }

    private static io.opentelemetry.api.trace.StatusCode convertStatusCode(io.opentelemetry.proto.trace.v1.Status.StatusCode protoCode) {
        switch (protoCode) {
            case STATUS_CODE_OK:
                return io.opentelemetry.api.trace.StatusCode.OK;
            case STATUS_CODE_ERROR:
                return io.opentelemetry.api.trace.StatusCode.ERROR;
            default:
                return io.opentelemetry.api.trace.StatusCode.UNSET;
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
