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

import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.kie.api.event.process.ProcessCompletedEvent;
import org.kie.api.event.process.ProcessNodeLeftEvent;
import org.kie.api.event.process.ProcessNodeTriggeredEvent;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.kogito.internal.process.event.DefaultKogitoProcessEventListener;
import org.kie.kogito.internal.process.runtime.KogitoNodeInstance;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.quarkus.serverless.workflow.opentelemetry.config.SonataFlowOtelConfig;
import org.kie.kogito.serverless.workflow.SWFConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.opentelemetry.api.trace.Span;

import static org.kie.kogito.quarkus.serverless.workflow.opentelemetry.SonataFlowOtelAttributes.EventDescriptions;
import static org.kie.kogito.quarkus.serverless.workflow.opentelemetry.SonataFlowOtelAttributes.Events;
import static org.kie.kogito.quarkus.serverless.workflow.opentelemetry.SonataFlowOtelAttributes.ProcessStates;

public class NodeOtelEventListener extends DefaultKogitoProcessEventListener {

    record NodeProcessDetails(String processInstanceId, String processId, String processVersion, String processState, String nodeId, String stateName) {
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(NodeOtelEventListener.class);

    private final NodeSpanManager spanManager;
    private final SonataFlowOtelConfig config;
    private final ProcessEventHandler processEventHandler;
    private final HeaderContextExtractor headerExtractor;
    private final Set<String> processedErrors = ConcurrentHashMap.newKeySet();

    public NodeOtelEventListener(NodeSpanManager spanManager, SonataFlowOtelConfig config, HeaderContextExtractor headerExtractor) {
        this.spanManager = spanManager;
        this.config = config;
        this.headerExtractor = headerExtractor;
        this.processEventHandler = new ProcessEventHandler(spanManager, config);
        LOGGER.debug("NodeOtelEventListener initialized");
    }

    @Override
    public void afterProcessStarted(ProcessStartedEvent event) {
        LOGGER.debug("Process started event received - start event will be added in beforeNodeTriggered");
    }

    @Override
    public void beforeNodeTriggered(ProcessNodeTriggeredEvent event) {
        try {
            NodeProcessDetails details = extractNodeAndProcessDetails(event);
            String stateName = details.stateName();

            if (stateName == null) {
                LOGGER.trace("Skipping node with no state metadata: {}", details.nodeId());
                return;
            }

            KogitoProcessInstance processInstance = (KogitoProcessInstance) event.getProcessInstance();
            String parentProcessInstanceId = processInstance.getParentProcessInstanceId();
            String currentActiveState = OtelContextHolder.getActiveState(details.processInstanceId());

            LOGGER.debug("Node triggered: {} (state: {}) for process instance {} (current active state: {})",
                    details.nodeId(), stateName, details.processInstanceId(), currentActiveState);

            if (stateName.equals(currentActiveState)) {
                LOGGER.trace("Node {} in same state {}, reusing span", details.nodeId(), stateName);
                return;
            }

            Map<String, String> extractedContext = OtelContextHolder.getExtractedContext();
            if (extractedContext.isEmpty()) {
                extractedContext = handleContextReestablishment(processInstance, details.processInstanceId());
            }

            if (currentActiveState != null) {
                endStateSpan(details.processInstanceId(), currentActiveState);
            }

            createAndConfigureStateSpan(details, stateName, extractedContext, parentProcessInstanceId);

            OtelContextHolder.setActiveState(details.processInstanceId(), stateName);

        } catch (Exception e) {
            LOGGER.error("Error in beforeNodeTriggered: {}", e.getMessage(), e);
        }
    }

    private boolean isFirstStateOfProcess(String currentActiveState) {
        return currentActiveState == null;
    }

    private void endStateSpan(String processInstanceId, String stateName) {
        Span span = spanManager.getActiveStateSpan(processInstanceId, stateName);
        if (span != null) {
            spanManager.addProcessEvent(span, Events.STATE_COMPLETED,
                    EventDescriptions.STATE_COMPLETED_PREFIX + stateName);
            spanManager.endStateSpan(processInstanceId, stateName);
            LOGGER.debug("Ended state span for state: {} in process: {}", stateName, processInstanceId);
        }
    }

    private void createAndConfigureStateSpan(NodeProcessDetails details, String stateName,
            Map<String, String> extractedContext, String parentProcessInstanceId) {
        Span span = spanManager.createStateSpanWithContext(
                details.processInstanceId(),
                details.processId(),
                details.processVersion(),
                details.processState(),
                stateName,
                extractedContext,
                parentProcessInstanceId);

        if (span != null) {
            spanManager.addProcessEvent(span, Events.STATE_STARTED,
                    EventDescriptions.STATE_STARTED_PREFIX + stateName);

            if (isFirstStateOfProcess(OtelContextHolder.getActiveState(details.processInstanceId()))) {
                processEventHandler.handleProcessStartEvent(span, details.processInstanceId());
            }
        }
    }

    private String extractStateName(org.kie.api.runtime.process.NodeInstance nodeInstance) {
        if (nodeInstance instanceof org.jbpm.workflow.instance.NodeInstance jbpmNodeInstance) {
            org.kie.api.definition.process.Node node = jbpmNodeInstance.getNode();
            if (node != null) {
                Object stateName = node.getMetaData().get(SWFConstants.STATE_NAME);
                if (stateName instanceof String) {
                    return (String) stateName;
                }
            }
        }
        return null;
    }

    private NodeProcessDetails extractNodeAndProcessDetails(ProcessNodeTriggeredEvent event) {
        KogitoNodeInstance nodeInstance = (KogitoNodeInstance) event.getNodeInstance();
        KogitoProcessInstance processInstance = (KogitoProcessInstance) event.getProcessInstance();

        String processInstanceId = processInstance.getId();
        String processId = processInstance.getProcessId();
        String processVersion = processInstance.getProcessVersion();
        String processState = getProcessState(processInstance.getState());
        String nodeId = nodeInstance.getNodeName();
        String stateName = extractStateName(event.getNodeInstance());

        return new NodeProcessDetails(processInstanceId, processId, processVersion, processState, nodeId, stateName);
    }

    private Map<String, String> handleContextReestablishment(KogitoProcessInstance processInstance, String processInstanceId) {
        try {
            Map<String, java.util.List<String>> processHeaders = processInstance.getHeaders();
            if (processHeaders != null && !processHeaders.isEmpty()) {
                Map<String, String> headerContext = headerExtractor.extractFromProcessHeaders(processHeaders);
                if (!headerContext.isEmpty()) {
                    populateMDCFromContext(headerContext);
                    LOGGER.debug("Re-established context from process headers for process instance {}: {}",
                            processInstanceId, headerContext.keySet());
                    return headerContext;
                }
            }
        } catch (Exception e) {
            LOGGER.debug("Failed to extract context from process headers: {}", e.getMessage());
        }
        return Map.of();
    }

    @Override
    public void afterProcessCompleted(ProcessCompletedEvent event) {
        try {
            KogitoProcessInstance processInstance = (KogitoProcessInstance) event.getProcessInstance();
            String processInstanceId = processInstance.getId();
            String outcome = getProcessState(processInstance.getState());

            long startTime = processInstance.getStartDate().getTime();
            long endTime = System.currentTimeMillis();
            long durationMs = endTime - startTime;

            LOGGER.debug("Process completed: {} with outcome {} in {}ms", processInstanceId, outcome, durationMs);

            processedErrors.remove(processInstanceId);

            handleProcessLevelCompletionEvents(processInstance, processInstanceId, durationMs, outcome);

        } catch (Exception e) {
            LOGGER.error("Error in afterProcessCompleted: {}", e.getMessage(), e);
        }
    }

    private void handleProcessLevelCompletionEvents(KogitoProcessInstance processInstance,
            String processInstanceId,
            long durationMs,
            String outcome) {

        if (config.events().enabled()) {
            String activeState = OtelContextHolder.getActiveState(processInstanceId);
            if (activeState != null) {
                Span stateSpan = spanManager.getActiveStateSpan(processInstanceId, activeState);
                if (stateSpan != null) {
                    spanManager.addProcessEvent(stateSpan, Events.STATE_COMPLETED,
                            EventDescriptions.STATE_COMPLETED_PREFIX + activeState);
                }
            }

            Span targetSpan = spanManager.getLastActiveSpan(processInstanceId);

            if (targetSpan == null) {
                targetSpan = spanManager.getAnyActiveSpan(processInstanceId);
            }

            if (targetSpan != null) {
                if (processInstance.getState() == ProcessInstance.STATE_ERROR) {
                    processEventHandler.handleProcessErrorEvent(targetSpan, processInstanceId, processInstance);
                }

                processEventHandler.addProcessCompleteEvent(targetSpan, processInstanceId, durationMs, outcome);
                LOGGER.debug("Added process.instance.complete event for {} with outcome {}", processInstanceId, outcome);

                spanManager.endRemainingSpans(processInstanceId);

            } else if (processInstance.getState() == ProcessInstance.STATE_ERROR) {
                processEventHandler.handleProcessErrorWithoutSpans(processInstance, durationMs);
            }
        }

        OtelContextHolder.clearActiveState(processInstanceId);
        OtelContextHolder.clearProcessContexts(processInstanceId);
    }

    @Override
    public void afterNodeLeft(ProcessNodeLeftEvent event) {
        try {
            KogitoNodeInstance nodeInstance = (KogitoNodeInstance) event.getNodeInstance();
            KogitoProcessInstance processInstance = (KogitoProcessInstance) event.getProcessInstance();

            String processInstanceId = processInstance.getId();
            String nodeId = nodeInstance.getNodeName();
            String stateName = extractStateName(event.getNodeInstance());

            LOGGER.trace("Node {} left in state {}", nodeId, stateName);

            if (processInstance.getState() == ProcessInstance.STATE_ERROR) {
                handleProcessErrorAfterNodeLeft(processInstance, processInstanceId);
            }
        } catch (Exception e) {
            LOGGER.error("Error in afterNodeLeft: {}", e.getMessage(), e);
        }
    }

    private String getProcessState(int state) {
        switch (state) {
            case ProcessInstance.STATE_PENDING:
                return ProcessStates.PENDING;
            case ProcessInstance.STATE_ACTIVE:
                return ProcessStates.ACTIVE;
            case ProcessInstance.STATE_COMPLETED:
                return ProcessStates.COMPLETED;
            case ProcessInstance.STATE_ABORTED:
                return ProcessStates.ABORTED;
            case ProcessInstance.STATE_SUSPENDED:
                return ProcessStates.SUSPENDED;
            case ProcessInstance.STATE_ERROR:
                return ProcessStates.ERROR;
            default:
                return ProcessStates.UNKNOWN;
        }
    }

    private void populateMDCFromContext(Map<String, String> context) {
        if (context == null || context.isEmpty()) {
            return;
        }

        for (Map.Entry<String, String> entry : context.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            if (SonataFlowOtelAttributes.RequestProperties.TRANSACTION_ID.equals(key)) {
                OtelContextHolder.setTransactionId(value);
            } else if (key.startsWith(SonataFlowOtelAttributes.RequestProperties.TRACKER_PREFIX)) {
                String trackerKey = key.substring(SonataFlowOtelAttributes.RequestProperties.TRACKER_PREFIX.length());
                OtelContextHolder.setTrackerAttribute(trackerKey, value);
            }
        }
    }

    private void handleProcessErrorAfterNodeLeft(KogitoProcessInstance processInstance, String processInstanceId) {
        if (processInstance == null || processInstanceId == null) {
            LOGGER.error("Cannot handle process error: null processInstance or processInstanceId");
            return;
        }

        if (!processedErrors.add(processInstanceId)) {
            LOGGER.debug("Error already handled for process instance {}", processInstanceId);
            return;
        }

        try {
            long durationMs = calculateProcessDuration(processInstance);

            if (config.events().enabled()) {
                Span targetSpan = spanManager.getLastActiveSpan(processInstanceId);

                if (targetSpan != null) {
                    processEventHandler.handleProcessErrorEvent(targetSpan, processInstanceId, processInstance);

                    processEventHandler.addProcessCompleteEvent(targetSpan, processInstanceId, durationMs, ProcessStates.ERROR);

                    spanManager.endRemainingSpansWithError(processInstanceId);
                } else {
                    processEventHandler.handleProcessErrorWithoutSpans(processInstance, durationMs);
                }
            }

        } catch (Exception e) {
            LOGGER.error("Error handling process error for {}: {}", processInstanceId, e.getMessage(), e);
        } finally {
            processedErrors.remove(processInstanceId);
            OtelContextHolder.clearProcessContexts(processInstanceId);
        }
    }

    private long calculateProcessDuration(KogitoProcessInstance processInstance) {
        try {
            Date startDate = processInstance.getStartDate();
            if (startDate != null) {
                return System.currentTimeMillis() - startDate.getTime();
            }
        } catch (Exception e) {
            LOGGER.debug("Could not calculate process duration: {}", e.getMessage());
        }
        return 0L;
    }
}
