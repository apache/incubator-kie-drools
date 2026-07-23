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
package org.kie.kogito.trusty.service.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.kie.kogito.event.cloudevents.utils.CloudEventUtils;
import org.kie.kogito.tracing.event.model.ModelEvent;
import org.kie.kogito.tracing.event.trace.TraceEvent;
import org.kie.kogito.tracing.event.trace.TraceHeader;
import org.kie.kogito.trusty.storage.api.model.decision.DMNModelMetadata;
import org.kie.kogito.trusty.storage.api.model.decision.Decision;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.cloudevents.CloudEvent;
import io.cloudevents.jackson.JsonFormat;

public class TrustyServiceTestUtils {

    public static final String CLOUDEVENT_SOURCE = "http://localhost:8080/Traffic+Violation/model/service";
    public static final String CLOUDEVENT_SERVICE = "http://localhost:8080/Traffic+Violation";

    public static final String CORRECT_CLOUDEVENT_ID = "correct-cloud-event-id";
    public static final String CLOUDEVENT_WITH_ERRORS_ID = "cloud-event-with-errors-id";

    public static final ObjectMapper MAPPER =
            new ObjectMapper().registerModule(JsonFormat.getCloudEventJacksonModule());

    public static CloudEvent buildCloudEvent(TraceEvent traceEvent) {
        return CloudEventUtils.build(
                traceEvent.getHeader().getExecutionId(),
                URI.create(CLOUDEVENT_SOURCE),
                traceEvent,
                TraceEvent.class).get();
    }

    public static CloudEvent buildCloudEvent(ModelEvent modelEvent) {
        return CloudEventUtils.build("id",
                URI.create(URLEncoder.encode(ModelEvent.class.getName(), StandardCharsets.UTF_8)),
                modelEvent,
                ModelEvent.class).get();
    }

    public static String buildCloudEventJsonString(TraceEvent traceEvent) {
        return CloudEventUtils.encode(buildCloudEvent(traceEvent)).orElseThrow(IllegalStateException::new);
    }

    public static String buildCloudEventWithoutDataJsonString() {
        return CloudEventUtils.encode(buildCloudEventWithoutData()).orElseThrow(IllegalStateException::new);
    }

    public static String buildCloudEventJsonString(ModelEvent modelEvent) {
        return CloudEventUtils.encode(buildCloudEvent(modelEvent)).orElseThrow(IllegalStateException::new);
    }

    public static CloudEvent buildCloudEventWithoutData() {
        return readResource("/events/cloudEventWithoutData.json", CloudEvent.class);
    }

    public static TraceEvent buildCorrectTraceEvent(String executionId) {
        TraceEvent traceEvent = readResource("/events/correctTraceEvent.json", TraceEvent.class);
        setExecutionId(traceEvent, executionId);
        return traceEvent;
    }

    public static TraceEvent buildTraceEventWithNullType(String cloudEventId) {
        TraceEvent traceEvent = readResource("/events/traceEventWithNullType.json", TraceEvent.class);
        setExecutionId(traceEvent, cloudEventId);
        return traceEvent;
    }

    public static Decision buildCorrectDecision(String cloudEventId) {
        Decision decision = readResource("/events/correctDecision.json", Decision.class);
        decision.setExecutionId(cloudEventId);
        return decision;
    }

    public static TraceEvent buildTraceEventWithErrors() {
        return readResource("/events/traceEventWithErrors.json", TraceEvent.class);
    }

    public static Decision buildDecisionWithErrors() {
        return readResource("/events/decisionWithErrors.json", Decision.class);
    }

    public static TraceEvent buildTraceEventWithNullFields() {
        return readResource("/events/traceEventWithNullFields.json", TraceEvent.class);
    }

    public static Decision buildDecisionWithNullFields() {
        return readResource("/events/decisionWithNullFields.json", Decision.class);
    }

    public static ModelEvent buildCorrectModelEvent() {
        return readResource("/events/correctModelEvent.json", ModelEvent.class);
    }

    public static String getCounterfactualJsonRequest() {
        return readResourceAsString("/requests/counterfactualRequest.json");
    }

    public static String getCounterfactualWithStructuredModelJsonRequest() {
        return readResourceAsString("/requests/counterfactualWithStructuredModelRequest.json");
    }

    public static DMNModelMetadata getModelIdentifier() {
        return new DMNModelMetadata("groupId", "artifactId", "version", "dmnVersion", "name", "namespace");
    }

    private static void setExecutionId(TraceEvent traceEvent, String executionId) {
        try {
            TraceHeader traceHeader = traceEvent.getHeader();
            Field member_name = traceHeader.getClass().getDeclaredField("executionId");
            member_name.setAccessible(true);
            member_name.set(traceHeader, executionId);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException("can't inject execution id in trace header");
        }
    }

    private static <T> T readResource(String name, Class<T> clazz) {
        try {
            return MAPPER.readValue(TrustyServiceTestUtils.class.getResource(name), clazz);
        } catch (IOException e) {
            throw new RuntimeException("Can't read test resource " + name, e);
        }
    }

    private static String readResourceAsString(String name) {
        try {
            return readFromInputStream(TrustyServiceTestUtils.class.getResourceAsStream(name));
        } catch (IOException e) {
            throw new RuntimeException("Can't read test resource " + name, e);
        }
    }

    private static String readFromInputStream(InputStream inputStream) throws IOException {
        StringBuilder resultStringBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                resultStringBuilder.append(line).append("\n");
            }
        }
        return resultStringBuilder.toString();
    }
}
