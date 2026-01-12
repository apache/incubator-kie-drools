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

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import io.opentelemetry.sdk.trace.data.SpanData;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.quarkus.serverless.workflow.opentelemetry.SonataFlowOtelAttributes.LOG_MESSAGE;
import static org.kie.kogito.quarkus.serverless.workflow.opentelemetry.SonataFlowOtelAttributes.SONATAFLOW_PROCESS_INSTANCE_ID;
import static org.kie.kogito.quarkus.serverless.workflow.opentelemetry.SonataFlowOtelAttributes.SONATAFLOW_WORKFLOW_STATE;

public final class RestartSpanValidator {

    private RestartSpanValidator() {
    }

    public static void validateFlatSpanHierarchy(List<SpanData> workflowSpans) {
        assertThat(workflowSpans)
                .as("Workflow spans list must not be empty")
                .isNotEmpty();

        String firstParentSpanId = workflowSpans.get(0).getParentSpanId();

        assertThat(firstParentSpanId)
                .as("First workflow span must have a parent span ID")
                .isNotNull();

        workflowSpans.forEach(span -> {
            assertThat(span.getParentSpanId())
                    .as("All workflow state spans must share the same parent span ID for flat hierarchy. " +
                            "Span '%s' has parent '%s', expected '%s'",
                            span.getName(), span.getParentSpanId(), firstParentSpanId)
                    .isEqualTo(firstParentSpanId);
        });
    }

    public static void validateOneSpanPerState(List<SpanData> workflowSpans, List<String> expectedStates) {
        Map<String, Long> stateSpanCounts = workflowSpans.stream()
                .map(span -> span.getAttributes().get(SONATAFLOW_WORKFLOW_STATE))
                .filter(stateName -> stateName != null)
                .collect(Collectors.groupingBy(stateName -> stateName, Collectors.counting()));

        assertThat(stateSpanCounts.keySet())
                .as("Workflow spans must contain all expected states. Found: %s, Expected: %s",
                        stateSpanCounts.keySet(), expectedStates)
                .containsExactlyInAnyOrderElementsOf(expectedStates);

        stateSpanCounts.forEach((stateName, count) -> {
            assertThat(count)
                    .as("State '%s' must have exactly one span, but found %d", stateName, count)
                    .isEqualTo(1L);
        });
    }

    public static void validateNewTraceAfterRestart(String preRestartTraceId, List<SpanData> postRestartSpans) {
        assertThat(postRestartSpans)
                .as("Post-restart spans list must not be empty")
                .isNotEmpty();

        assertThat(preRestartTraceId)
                .as("Pre-restart trace ID must not be null")
                .isNotNull();

        postRestartSpans.forEach(span -> {
            assertThat(span.getTraceId())
                    .as("Post-restart span '%s' must have a different trace ID than pre-restart. " +
                            "Pre-restart trace ID: '%s', Post-restart trace ID: '%s'",
                            span.getName(), preRestartTraceId, span.getTraceId())
                    .isNotEqualTo(preRestartTraceId);
        });
    }

    public static void validateLogMessagesInSpanEvents(List<SpanData> workflowSpans, List<String> expectedMessages) {
        List<String> allLogMessages = workflowSpans.stream()
                .flatMap(span -> span.getEvents().stream())
                .map(event -> event.getAttributes().get(LOG_MESSAGE))
                .filter(message -> message != null)
                .collect(Collectors.toList());

        expectedMessages.forEach(expectedMessage -> {
            assertThat(allLogMessages)
                    .as("Expected log message '%s' not found in span events. Available messages: %s",
                            expectedMessage, allLogMessages)
                    .anyMatch(logMessage -> logMessage.contains(expectedMessage));
        });
    }

    public static Set<String> extractTraceIds(List<SpanData> spans) {
        return spans.stream()
                .map(SpanData::getTraceId)
                .collect(Collectors.toSet());
    }

    public static List<SpanData> filterByProcessInstanceId(List<SpanData> spans, String processInstanceId) {
        return spans.stream()
                .filter(span -> {
                    String spanProcessInstanceId = span.getAttributes().get(SONATAFLOW_PROCESS_INSTANCE_ID);
                    return processInstanceId.equals(spanProcessInstanceId);
                })
                .collect(Collectors.toList());
    }
}
