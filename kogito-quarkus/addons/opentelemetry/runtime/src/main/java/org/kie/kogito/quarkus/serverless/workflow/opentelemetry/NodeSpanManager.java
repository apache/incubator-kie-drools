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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

import org.kie.kogito.quarkus.serverless.workflow.opentelemetry.config.SonataFlowOtelConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.VisibleForTesting;

import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;

import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import static org.kie.kogito.quarkus.serverless.workflow.opentelemetry.SonataFlowOtelAttributes.EVENT_DESCRIPTION;
import static org.kie.kogito.quarkus.serverless.workflow.opentelemetry.SonataFlowOtelAttributes.RequestProperties;
import static org.kie.kogito.quarkus.serverless.workflow.opentelemetry.SonataFlowOtelAttributes.SERVICE_NAME;
import static org.kie.kogito.quarkus.serverless.workflow.opentelemetry.SonataFlowOtelAttributes.SERVICE_VERSION;
import static org.kie.kogito.quarkus.serverless.workflow.opentelemetry.SonataFlowOtelAttributes.SONATAFLOW_PROCESS_ID;
import static org.kie.kogito.quarkus.serverless.workflow.opentelemetry.SonataFlowOtelAttributes.SONATAFLOW_PROCESS_INSTANCE_ID;
import static org.kie.kogito.quarkus.serverless.workflow.opentelemetry.SonataFlowOtelAttributes.SONATAFLOW_PROCESS_INSTANCE_STATE;
import static org.kie.kogito.quarkus.serverless.workflow.opentelemetry.SonataFlowOtelAttributes.SONATAFLOW_PROCESS_VERSION;
import static org.kie.kogito.quarkus.serverless.workflow.opentelemetry.SonataFlowOtelAttributes.SONATAFLOW_TRANSACTION_ID;
import static org.kie.kogito.quarkus.serverless.workflow.opentelemetry.SonataFlowOtelAttributes.SONATAFLOW_WORKFLOW_STATE;
import static org.kie.kogito.quarkus.serverless.workflow.opentelemetry.SonataFlowOtelAttributes.SpanNames;
import static org.kie.kogito.quarkus.serverless.workflow.opentelemetry.SonataFlowOtelAttributes.TrackerAttributes;

@ApplicationScoped
public class NodeSpanManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(NodeSpanManager.class);
    private final Tracer tracer;
    private final SonataFlowOtelConfig config;
    private final Map<String, SpanInfo> activeSpans = new ConcurrentHashMap<>();
    private final Map<String, Span> lastActiveNodeSpan = new ConcurrentHashMap<>();

    @Inject
    public NodeSpanManager(Tracer tracer, SonataFlowOtelConfig config) {
        this.tracer = tracer;
        this.config = config;
    }

    record SpanInfo(Span span, String spanKey, Context spanContext) {

        void endWithStatus(StatusCode statusCode, String description) {
            try {
                if (description != null) {
                    span.setStatus(statusCode, description);
                } else {
                    span.setStatus(statusCode);
                }
                span.end();
                if (span != null && span.equals(OtelContextHolder.getCurrentWorkflowSpan())) {
                    OtelContextHolder.clearCurrentWorkflowSpan();
                }
            } catch (Exception e) {
                LOGGER.error("Error ending span for {}", spanKey, e);
            }
        }

        void close() {
            if (span != null) {
                if (span.equals(OtelContextHolder.getCurrentWorkflowSpan())) {
                    OtelContextHolder.clearCurrentWorkflowSpan();
                }
                span.end();
            }
        }
    }

    private Context getOrCaptureRootContext(String processInstanceId, String parentProcessInstanceId) {
        SpanContext rootSpanContext = OtelContextHolder.getRootSpanContext(processInstanceId);
        if (rootSpanContext != null && rootSpanContext.isValid()) {
            LOGGER.debug("Using stored root span context for process {}", processInstanceId);
            return createParentContext(rootSpanContext);
        }

        if (parentProcessInstanceId != null && !parentProcessInstanceId.isEmpty()) {
            SpanContext parentRoot = OtelContextHolder.getRootSpanContext(parentProcessInstanceId);
            if (parentRoot != null && parentRoot.isValid()) {
                OtelContextHolder.setRootSpanContext(processInstanceId, parentRoot);
                LOGGER.debug("Inheriting root span context from parent {} for subflow {}",
                        parentProcessInstanceId, processInstanceId);
                return createParentContext(parentRoot);
            }
        }

        SpanContext httpSpanContext = OtelContextHolder.getHttpRequestSpanContext();
        if (httpSpanContext != null && httpSpanContext.isValid()) {
            OtelContextHolder.setRootSpanContext(processInstanceId, httpSpanContext);
            LOGGER.debug("Using HTTP request span context for process {}", processInstanceId);
            return createParentContext(httpSpanContext);
        }

        Span current = Span.current();
        if (current.getSpanContext().isValid()) {
            OtelContextHolder.setRootSpanContext(processInstanceId, current.getSpanContext());
            LOGGER.debug("Captured current span context as root for process {}", processInstanceId);
            return createParentContext(current.getSpanContext());
        }

        LOGGER.debug("No valid span context available, using current context");
        return Context.current();
    }

    private Context createParentContext(SpanContext spanContext) {
        Span parentSpan = Span.wrap(spanContext);
        return Context.current().with(parentSpan);
    }

    private boolean isSpanCreationEnabled() {
        return config.enabled() && config.spans().enabled();
    }

    private void registerSpanInfo(SpanInfo spanInfo, String spanKey) {
        SpanInfo previousSpanInfo = activeSpans.put(spanKey, spanInfo);
        if (previousSpanInfo != null) {
            previousSpanInfo.close();
            LOGGER.debug("Replaced previous span for {}", spanKey);
        }
        LOGGER.debug("Registered span for {}", spanKey);
    }

    public void addProcessEvent(Span span, String eventName, String description) {
        if (span != null) {
            if (description != null) {
                Attributes eventAttributes = Attributes.of(
                        EVENT_DESCRIPTION, description);
                span.addEvent(eventName, eventAttributes);
            } else {
                span.addEvent(eventName);
            }
            LOGGER.debug("Added event {} to span", eventName);
        } else {
            LOGGER.debug("Cannot add event {} - span is null", eventName);
        }
    }

    public void addProcessEvent(Span span, String eventName, Attributes attributes) {
        if (span != null) {
            span.addEvent(eventName, attributes);
            LOGGER.debug("Added process event {} to span", eventName);
        } else {
            LOGGER.debug("Cannot add event {} - span is null", eventName);
        }
    }

    public Span getLastActiveSpan(String processInstanceId) {
        return lastActiveNodeSpan.get(processInstanceId);
    }

    public Span getAnyActiveSpan(String processInstanceId) {
        return activeSpans.entrySet().stream()
                .filter(forProcessInstance(processInstanceId))
                .map(entry -> entry.getValue().span())
                .findFirst()
                .orElse(null);
    }

    public void endRemainingSpans(String processInstanceId) {
        endRemainingSpansWithStatus(processInstanceId, StatusCode.OK, null);
    }

    public void endRemainingSpansWithError(String processInstanceId) {
        endRemainingSpansWithStatus(processInstanceId, StatusCode.ERROR, "Process failed with error");
    }

    private void endRemainingSpansWithStatus(String processInstanceId, StatusCode statusCode, String description) {
        String statePrefix = processInstanceId + ":state:";
        activeSpans.entrySet().stream()
                .filter(forProcessInstance(processInstanceId))
                .forEach(entry -> {
                    SpanInfo spanInfo = entry.getValue();
                    String spanKey = spanInfo.spanKey();

                    String stateName = spanKey.substring(statePrefix.length());
                    String eventName = SonataFlowOtelAttributes.Events.STATE_COMPLETED;
                    String eventDescription = SonataFlowOtelAttributes.EventDescriptions.STATE_COMPLETED_PREFIX + stateName;

                    addProcessEvent(spanInfo.span(), eventName, eventDescription);
                    spanInfo.endWithStatus(statusCode, description);
                    LOGGER.debug("Ended span for {} with status {}", spanKey, statusCode);
                });

        activeSpans.entrySet().removeIf(forProcessInstance(processInstanceId));
        lastActiveNodeSpan.remove(processInstanceId);
        OtelContextHolder.clearRootContext(processInstanceId);
        OtelContextHolder.clearRootSpanContext(processInstanceId);
    }

    private Predicate<Map.Entry<String, SpanInfo>> forProcessInstance(String processInstanceId) {
        String prefix = processInstanceId + ":";
        return entry -> entry.getKey().startsWith(prefix);
    }

    public void setSpanError(Span span, Throwable exception, String description) {
        if (span != null) {
            span.setStatus(StatusCode.ERROR, description);
            if (exception != null) {
                span.recordException(exception);
            }
        }
    }

    public Span createStateSpan(String processInstanceId, String processId, String processVersion,
            String processState, String stateName) {
        return createStateSpan(processInstanceId, processId, processVersion, processState, stateName, null);
    }

    public Span createStateSpan(String processInstanceId, String processId, String processVersion,
            String processState, String stateName, String parentProcessInstanceId) {
        if (!isSpanCreationEnabled()) {
            LOGGER.debug("Span creation disabled");
            return null;
        }

        SpanInfo spanInfo = null;
        String spanKey = null;
        try {
            Context parentContext = getOrCaptureRootContext(processInstanceId, parentProcessInstanceId);

            spanKey = buildStateSpanKey(processInstanceId, stateName);
            Span span = buildStateSpan(processInstanceId, processId, processVersion, processState, stateName, parentContext);
            Context spanContext = Context.current().with(span);

            spanInfo = new SpanInfo(span, spanKey, spanContext);
            registerSpanInfo(spanInfo, spanKey);
            OtelContextHolder.setCurrentWorkflowSpan(span);

            lastActiveNodeSpan.put(processInstanceId, span);
            return span;
        } catch (Exception e) {
            if (spanInfo != null) {
                spanInfo.close();
                if (spanKey != null) {
                    activeSpans.remove(spanKey);
                    lastActiveNodeSpan.remove(processInstanceId);
                }
            }
            LOGGER.error("Failed to create state span for {}:{}", processInstanceId, stateName, e);
            return null;
        }
    }

    private String buildStateSpanKey(String processInstanceId, String stateName) {
        return processInstanceId + ":state:" + stateName;
    }

    private Span buildStateSpan(String processInstanceId, String processId, String processVersion,
            String processState, String stateName, Context parentContext) {
        String spanName = SpanNames.createProcessSpanName(processId);

        return tracer.spanBuilder(spanName)
                .setParent(parentContext)
                .setSpanKind(SpanKind.INTERNAL)
                .setAttribute(SONATAFLOW_PROCESS_INSTANCE_ID, processInstanceId)
                .setAttribute(SONATAFLOW_PROCESS_ID, processId)
                .setAttribute(SONATAFLOW_PROCESS_VERSION, processVersion)
                .setAttribute(SONATAFLOW_PROCESS_INSTANCE_STATE, processState)
                .setAttribute(SERVICE_NAME, config.serviceName())
                .setAttribute(SERVICE_VERSION, config.serviceVersion())
                .setAttribute(SONATAFLOW_WORKFLOW_STATE, stateName)
                .startSpan();
    }

    public Span createStateSpanWithContext(String processInstanceId, String processId, String processVersion,
            String processState, String stateName, Map<String, String> headerContext, String parentProcessInstanceId) {
        Span span = createStateSpan(processInstanceId, processId, processVersion, processState, stateName, parentProcessInstanceId);

        if (span != null) {
            String transactionId = null;

            if (headerContext != null && !headerContext.isEmpty()) {
                transactionId = headerContext.get(RequestProperties.TRANSACTION_ID);

                for (Map.Entry<String, String> entry : headerContext.entrySet()) {
                    if (entry.getKey().startsWith(RequestProperties.TRACKER_PREFIX)) {
                        String attributeKey = TrackerAttributes.createTrackerAttributeKey(entry.getKey());
                        span.setAttribute(attributeKey, entry.getValue());
                    }
                }
            }

            if (transactionId == null) {
                transactionId = processInstanceId;
            }

            span.setAttribute(SONATAFLOW_TRANSACTION_ID, transactionId);
        }

        return span;
    }

    public Span getActiveStateSpan(String processInstanceId, String stateName) {
        String spanKey = buildStateSpanKey(processInstanceId, stateName);
        SpanInfo spanInfo = activeSpans.get(spanKey);
        return spanInfo != null ? spanInfo.span() : null;
    }

    public void endStateSpan(String processInstanceId, String stateName) {
        String spanKey = buildStateSpanKey(processInstanceId, stateName);
        SpanInfo spanInfo = activeSpans.remove(spanKey);
        if (spanInfo != null) {
            spanInfo.endWithStatus(StatusCode.OK, null);
            LOGGER.debug("Ended state span for {}", spanKey);
        }
    }

    @VisibleForTesting
    int getActiveScopeCount() {
        return activeSpans.size();
    }

    @VisibleForTesting
    int getActiveSpanCount() {
        return lastActiveNodeSpan.size();
    }

    @PreDestroy
    public void cleanup() {
        int spanCount = activeSpans.size();
        if (spanCount > 0) {
            LOGGER.debug("Cleaning up {} active spans during shutdown", spanCount);
            activeSpans.values().forEach(spanInfo -> {
                try {
                    spanInfo.close();
                } catch (Exception e) {
                    LOGGER.warn("Error ending span for {}", spanInfo.spanKey(), e);
                }
            });
        }

        activeSpans.clear();
        lastActiveNodeSpan.clear();
    }
}
