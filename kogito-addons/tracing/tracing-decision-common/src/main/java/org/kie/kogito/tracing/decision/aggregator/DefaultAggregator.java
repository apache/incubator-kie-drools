/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.tracing.decision.aggregator;

import java.net.URI;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.JsonNode;
import io.cloudevents.CloudEvent;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.api.core.ast.DMNNode;
import org.kie.dmn.api.core.ast.DecisionNode;
import org.kie.dmn.api.core.ast.InputDataNode;
import org.kie.dmn.core.ast.DMNBaseNode;
import org.kie.dmn.core.ast.DecisionServiceNodeImpl;
import org.kie.dmn.feel.util.Pair;
import org.kie.kogito.conf.ConfigBean;
import org.kie.kogito.tracing.decision.event.CloudEventUtils;
import org.kie.kogito.tracing.decision.event.EventUtils;
import org.kie.kogito.tracing.decision.event.evaluate.EvaluateDecisionResult;
import org.kie.kogito.tracing.decision.event.evaluate.EvaluateEvent;
import org.kie.kogito.tracing.decision.event.evaluate.EvaluateEventType;
import org.kie.kogito.tracing.decision.event.message.InternalMessageType;
import org.kie.kogito.tracing.decision.event.message.Message;
import org.kie.kogito.tracing.decision.event.trace.TraceEvent;
import org.kie.kogito.tracing.decision.event.trace.TraceEventType;
import org.kie.kogito.tracing.decision.event.trace.TraceExecutionStep;
import org.kie.kogito.tracing.decision.event.trace.TraceExecutionStepType;
import org.kie.kogito.tracing.decision.event.trace.TraceHeader;
import org.kie.kogito.tracing.decision.event.trace.TraceInputValue;
import org.kie.kogito.tracing.decision.event.trace.TraceOutputValue;
import org.kie.kogito.tracing.typedvalue.TypedValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.kogito.tracing.decision.event.evaluate.EvaluateEventType.AFTER_EVALUATE_DECISION_SERVICE;
import static org.kie.kogito.tracing.decision.event.evaluate.EvaluateEventType.BEFORE_EVALUATE_DECISION_SERVICE;

public class DefaultAggregator implements Aggregator {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultAggregator.class);
    private static final String UNKNOWN_SOURCE_URI_STRING = CloudEventUtils.urlEncodedStringFrom("__UNKNOWN_SOURCE__")
            .orElseThrow(IllegalStateException::new);

    private static final String EXPRESSION_ID_KEY = "expressionId";
    private static final String MATCHES_KEY = "matches";
    private static final String NODE_ID_KEY = "nodeId";
    private static final String NODE_NAME_KEY = "nodeName";
    private static final String SELECTED_KEY = "selected";
    private static final String VARIABLE_ID_KEY = "variableId";

    @Override
    public Optional<CloudEvent> aggregate(DMNModel model, String executionId, List<EvaluateEvent> events, ConfigBean configBean) {
        return events == null || events.isEmpty()
                ? buildNotEnoughDataCloudEvent(model, executionId, configBean)
                : buildDefaultCloudEvent(model, executionId, events, configBean);
    }

    private static Optional<CloudEvent> buildNotEnoughDataCloudEvent(DMNModel model, String executionId, ConfigBean configBean) {
        TraceHeader header = new TraceHeader(
                TraceEventType.DMN,
                executionId,
                null,
                null,
                null,
                EventUtils.traceResourceIdFrom(configBean.getServiceUrl(), model),
                Stream.of(
                        EventUtils.messageFrom(InternalMessageType.NOT_ENOUGH_DATA),
                        model == null ? EventUtils.messageFrom(InternalMessageType.DMN_MODEL_NOT_FOUND) : null
                ).filter(Objects::nonNull).collect(Collectors.toList())
        );

        TraceEvent event = new TraceEvent(header, Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
        return CloudEventUtils
                .build(executionId, buildSource(configBean.getServiceUrl(), null), event, TraceEvent.class);
    }

    private static Optional<CloudEvent> buildDefaultCloudEvent(DMNModel model, String executionId, List<EvaluateEvent> events, ConfigBean configBean) {
        EvaluateEvent firstEvent = events.get(0);
        EvaluateEvent lastEvent = events.get(events.size() - 1);

        List<TraceInputValue> inputs = buildTraceInputValues(model, firstEvent);

        List<TraceOutputValue> outputs = buildTraceOutputValues(model, lastEvent);

        Pair<List<TraceExecutionStep>, List<Message>> executionStepsPair = buildTraceExecutionSteps(model, executionId, events);

        TraceHeader header = new TraceHeader(
                TraceEventType.DMN,
                executionId,
                firstEvent.getTimestamp(),
                lastEvent.getTimestamp(),
                computeDurationMillis(firstEvent, lastEvent),
                firstEvent.toTraceResourceId(configBean.getServiceUrl()),
                Stream.of(
                        model == null ? Stream.of(EventUtils.messageFrom(InternalMessageType.DMN_MODEL_NOT_FOUND)) : Stream.<Message>empty(),
                        executionStepsPair.getRight().stream(),
                        lastEvent.getResult().getMessages().stream()
                                .filter(m -> m.getSourceId() == null || m.getSourceId().isEmpty())
                ).flatMap(Function.identity()).collect(Collectors.toList())
        );

        // complete event
        TraceEvent event = new TraceEvent(header, inputs, outputs, executionStepsPair.getLeft());
        return CloudEventUtils
                .build(executionId, buildSource(configBean.getServiceUrl(), firstEvent), event, TraceEvent.class);
    }

    private static URI buildSource(String serviceUrl, EvaluateEvent event) {
        String modelChunk = Optional.ofNullable(event)
                .map(EvaluateEvent::getModelName)
                .flatMap(CloudEventUtils::urlEncodedStringFrom)
                .orElse(null);

        String decisionChunk = Optional.ofNullable(event)
                .filter(e -> e.getType() == BEFORE_EVALUATE_DECISION_SERVICE || e.getType() == AFTER_EVALUATE_DECISION_SERVICE)
                .map(EvaluateEvent::getNodeName)
                .flatMap(CloudEventUtils::urlEncodedStringFrom)
                .orElse(null);

        String fullUrl = Stream.of(serviceUrl, modelChunk, decisionChunk)
                .filter(s -> s != null && !s.isEmpty())
                .collect(Collectors.joining("/"));

        return URI.create(Optional.of(fullUrl)
                .filter(s -> !s.isEmpty())
                .orElse(UNKNOWN_SOURCE_URI_STRING)
        );
    }

    private static List<TraceInputValue> buildTraceInputValues(DMNModel model, EvaluateEvent firstEvent) {
        Map<String, InputDataNode> inputNodesMap = inputDataNodesFromFirstEvent(model, firstEvent).stream()
                .collect(Collectors.toMap(DMNNode::getName, Function.identity()));

        return Stream.concat(
                streamInputsFromInitialContext(firstEvent, inputNodesMap),
                streamKnownInputsNotInInitialContext(firstEvent, inputNodesMap)
        ).collect(Collectors.toList());
    }

    private static Collection<InputDataNode> inputDataNodesFromFirstEvent(DMNModel model, EvaluateEvent firstEvent) {
        if (model == null || firstEvent == null) {
            return Collections.emptyList();
        }
        if (firstEvent.getType() == EvaluateEventType.BEFORE_EVALUATE_DECISION_SERVICE) {
            // cast to DecisionServiceNodeImpl here is required to have access to getInputParameters method
            Optional<DecisionServiceNodeImpl> optNode = model.getDecisionServices().stream()
                    .filter(ds -> ds.getId().equals(firstEvent.getNodeId()))
                    .findFirst()
                    .filter(DecisionServiceNodeImpl.class::isInstance)
                    .map(DecisionServiceNodeImpl.class::cast);

            if (optNode.isPresent()) {
                return optNode.get().getInputParameters().values().stream()
                        .filter(InputDataNode.class::isInstance)
                        .map(InputDataNode.class::cast)
                        .collect(Collectors.toList());
            }
        }
        return model.getInputs();
    }

    private static Stream<TraceInputValue> streamInputsFromInitialContext(EvaluateEvent firstEvent, Map<String, InputDataNode> inputNodesMap) {
        return firstEvent.getContext().entrySet().stream()
                .map(entry -> buildTraceInputValue(entry.getKey(), entry.getValue(), inputNodesMap));
    }

    private static TraceInputValue buildTraceInputValue(String name, Object value, Map<String, InputDataNode> inputNodesMap) {
        return inputNodesMap.containsKey(name)
                ? traceInputFrom(inputNodesMap.get(name), value)
                : traceInputFrom(name, value);
    }

    private static Stream<TraceInputValue> streamKnownInputsNotInInitialContext(EvaluateEvent firstEvent, Map<String, InputDataNode> inputNodesMap) {
        return inputNodesMap.entrySet().stream()
                .filter(entry -> !firstEvent.getContext().containsKey(entry.getKey()))
                .map(entry -> traceInputFrom(entry.getValue(), null));
    }

    private static List<TraceOutputValue> buildTraceOutputValues(DMNModel model, EvaluateEvent lastEvent) {
        return lastEvent.getResult().getDecisionResults().stream()
                .map(dr -> traceOutputFrom(dr, model, lastEvent.getContext()))
                .collect(Collectors.toList());
    }

    private static Pair<List<TraceExecutionStep>, List<Message>> buildTraceExecutionSteps(DMNModel model, String executionId, List<EvaluateEvent> events) {
        try {
            return new Pair<>(buildTraceExecutionStepsHierarchy(model, events), Collections.emptyList());
        } catch (IllegalStateException e) {
            LOG.error(String.format("IllegalStateException during aggregation of evaluation %s", executionId), e);
            return new Pair<>(buildTraceExecutionStepsList(model, events), Arrays.asList(EventUtils.messageFrom(InternalMessageType.NO_EXECUTION_STEP_HIERARCHY, e)));
        }
    }

    private static List<TraceExecutionStep> buildTraceExecutionStepsHierarchy(DMNModel model, List<EvaluateEvent> events) {
        List<TraceExecutionStep> executionSteps = new ArrayList<>(events.size() / 2);
        Deque<DefaultAggregatorStackEntry> stack = new ArrayDeque<>(events.size() / 2);
        for (int i = 1; i < events.size() - 1; i++) {
            processEvaluateEventInHierarchy(model, stack, executionSteps, events.get(i));
        }
        if (!stack.isEmpty()) {
            throw new IllegalStateException("Can't match all after events with corresponding before events");
        }
        return executionSteps;
    }

    private static void processEvaluateEventInHierarchy(DMNModel model, Deque<DefaultAggregatorStackEntry> stack, List<TraceExecutionStep> executionSteps, EvaluateEvent event) {
        LOG.trace("Started aggregating event {} (execution steps: {}, stack size: {})", event.getType(), executionSteps.size(), stack.size());
        if (event.getType().isBefore()) {
            stack.push(new DefaultAggregatorStackEntry(event));
        } else {
            if (stack.isEmpty() || !stack.peek().isValidAfterEvent(event)) {
                throw new IllegalStateException(String.format("Can't match %s after event with corresponding before event", event.getType()));
            }
            DefaultAggregatorStackEntry stackEntry = stack.pop();
            TraceExecutionStep step = buildTraceExecutionStep(model, stackEntry, event);
            if (step == null) {
                throw new IllegalStateException(String.format("Can't build TraceExecutionStep for a %s event", event.getType()));
            }
            if (stack.isEmpty()) {
                executionSteps.add(step);
            } else {
                stack.peek().addChild(step);
            }
        }
        LOG.trace("Finished aggregating event {} (execution steps: {}, stack size: {})", event.getType(), executionSteps.size(), stack.size());
    }

    private static List<TraceExecutionStep> buildTraceExecutionStepsList(DMNModel model, List<EvaluateEvent> events) {
        return events.stream()
                .filter(e -> e.getType().isAfter())
                .map(e -> buildTraceExecutionStep(model, null, e))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private static TraceExecutionStep buildTraceExecutionStep(DMNModel model, DefaultAggregatorStackEntry stackEntry, EvaluateEvent afterEvent) {
        TraceExecutionStepType type = Optional.ofNullable(afterEvent.getType()).map(EvaluateEventType::toTraceExecutionStepType).orElse(null);
        if (type == null) {
            return null;
        }

        long duration = Optional.ofNullable(stackEntry)
                .map(DefaultAggregatorStackEntry::getBeforeEvent)
                .map(beforeEvent -> computeDurationMillis(beforeEvent, afterEvent))
                .orElse(0L);

        List<TraceExecutionStep> children = Optional.ofNullable(stackEntry)
                .map(DefaultAggregatorStackEntry::getChildren)
                .orElse(Collections.emptyList());

        switch (type) {
            case DMN_BKM_EVALUATION:
            case DMN_DECISION_SERVICE:
            case DMN_BKM_INVOCATION:
                return buildDefaultTraceExecutionStep(duration, afterEvent, children, type);
            case DMN_CONTEXT_ENTRY:
                return buildDmnContextEntryTraceExecutionStep(duration, afterEvent, children, model);
            case DMN_DECISION:
                return buildDmnDecisionTraceExecutionStep(duration, afterEvent, children);
            case DMN_DECISION_TABLE:
                return buildDmnDecisionTableTraceExecutionStep(duration, afterEvent, children, model);
            default:
                return null;
        }
    }

    private static TraceExecutionStep buildDefaultTraceExecutionStep(long duration, EvaluateEvent afterEvent, List<TraceExecutionStep> children, TraceExecutionStepType type) {
        Map<String, String> additionalData = new HashMap<>();
        additionalData.put(NODE_ID_KEY, afterEvent.getNodeId());

        return new TraceExecutionStep(type, duration, afterEvent.getNodeName(), null, Collections.emptyList(), additionalData, children);
    }

    private static TraceExecutionStep buildDmnContextEntryTraceExecutionStep(long duration, EvaluateEvent afterEvent, List<TraceExecutionStep> children, DMNModel model) {
        JsonNode result = EventUtils.jsonNodeFrom(afterEvent.getContextEntryResult().getExpressionResult());

        Map<String, String> additionalData = new HashMap<>();
        additionalData.put(EXPRESSION_ID_KEY, afterEvent.getContextEntryResult().getExpressionId());
        additionalData.put(VARIABLE_ID_KEY, afterEvent.getContextEntryResult().getVariableId());

        Optional<String> optDecisionNodeId = Optional.ofNullable(model)
                .map(m -> m.getDecisionByName(afterEvent.getNodeName()))
                .map(DecisionNode::getId);

        if (optDecisionNodeId.isPresent()) {
            additionalData.put(NODE_ID_KEY, optDecisionNodeId.get());
        } else {
            additionalData.put(NODE_NAME_KEY, afterEvent.getNodeName());
        }

        return new TraceExecutionStep(TraceExecutionStepType.DMN_CONTEXT_ENTRY, duration, afterEvent.getContextEntryResult().getVariableName(), result, Collections.emptyList(), additionalData, children);
    }

    private static TraceExecutionStep buildDmnDecisionTraceExecutionStep(long duration, EvaluateEvent afterEvent, List<TraceExecutionStep> children) {
        List<Message> messages = afterEvent.getResult().getMessages().stream()
                .filter(m -> afterEvent.getNodeId().equals(m.getSourceId()))
                .collect(Collectors.toList());

        JsonNode result = afterEvent.getResult().getDecisionResults().stream()
                .filter(dr -> dr.getDecisionId().equals(afterEvent.getNodeId()))
                .findFirst()
                .map(EvaluateDecisionResult::getResult)
                .<JsonNode>map(EventUtils::jsonNodeFrom)
                .orElse(null);

        Map<String, String> additionalData = new HashMap<>();
        additionalData.put(NODE_ID_KEY, afterEvent.getNodeId());

        return new TraceExecutionStep(TraceExecutionStepType.DMN_DECISION, duration, afterEvent.getNodeName(), result, messages, additionalData, children);
    }

    private static TraceExecutionStep buildDmnDecisionTableTraceExecutionStep(long duration, EvaluateEvent afterEvent, List<TraceExecutionStep> children, DMNModel model) {
        Map<String, String> additionalData = new HashMap<>();
        additionalData.put(MATCHES_KEY, afterEvent.getDecisionTableResult().getMatches().stream().map(Object::toString).collect(Collectors.joining(",")));
        additionalData.put(SELECTED_KEY, afterEvent.getDecisionTableResult().getSelected().stream().map(Object::toString).collect(Collectors.joining(",")));

        Optional<String> optDecisionNodeId = Optional.ofNullable(model)
                .map(m -> m.getDecisionByName(afterEvent.getNodeName()))
                .map(DecisionNode::getId);

        if (optDecisionNodeId.isPresent()) {
            additionalData.put(NODE_ID_KEY, optDecisionNodeId.get());
        } else {
            additionalData.put(NODE_NAME_KEY, afterEvent.getNodeName());
        }

        return new TraceExecutionStep(TraceExecutionStepType.DMN_DECISION_TABLE, duration, afterEvent.getDecisionTableResult().getDecisionTableName(), null, Collections.emptyList(), additionalData, children);
    }

    private static long computeDurationMillis(EvaluateEvent beginEvent, EvaluateEvent endEvent) {
        return Math.round((endEvent.getNanoTime() - beginEvent.getNanoTime()) / 1000000.0);
    }

    private static TraceInputValue traceInputFrom(String name, Object value) {
        return new TraceInputValue(null, name, EventUtils.typedValueFrom(value), Collections.emptyList());
    }

    private static TraceInputValue traceInputFrom(InputDataNode node, Object value) {
        return new TraceInputValue(
                node.getId(),
                node.getName(),
                EventUtils.typedValueFrom(node.getType(), value),
                Collections.emptyList()
        );
    }

    private static TraceOutputValue traceOutputFrom(EvaluateDecisionResult decisionResult, DMNModel model, Map<String, Object> context) {
        DMNType type = Optional.ofNullable(model)
                .map(m -> m.getDecisionById(decisionResult.getDecisionId()))
                .map(DecisionNode::getResultType)
                .orElse(null);

        // cast to DMNBaseNode here is required to have access to getDependencies method
        Map<String, DMNType> decisionInputTypes = Optional.ofNullable(model)
                .map(m -> m.getDecisionById(decisionResult.getDecisionId()))
                .filter(DMNBaseNode.class::isInstance)
                .map(DMNBaseNode.class::cast)
                .map(DMNBaseNode::getDependencies)
                .map(deps -> deps.values().stream().map(DMNNode::getId).collect(Collectors.toList()))
                .map(ids -> ids.stream()
                        .map(id -> typeAndNameOf(id, model))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toMap(Pair::getRight, Pair::getLeft))
                )
                .orElseGet(HashMap::new);

        Map<String, TypedValue> decisionInputs = decisionInputTypes.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> EventUtils.typedValueFrom(e.getValue(), context.get(e.getKey()))));

        return new TraceOutputValue(
                decisionResult.getDecisionId(),
                decisionResult.getDecisionName(),
                decisionResult.getEvaluationStatus().name(),
                EventUtils.typedValueFrom(type, decisionResult.getResult()),
                decisionInputs,
                decisionResult.getMessages()
        );
    }

    private static Pair<DMNType, String> typeAndNameOf(String nodeId, DMNModel model) {
        InputDataNode input = model.getInputById(nodeId);
        if (input != null) {
            return new Pair<>(input.getType(), input.getName());
        }
        DecisionNode decision = model.getDecisionById(nodeId);
        if (decision != null) {
            return new Pair<>(decision.getResultType(), decision.getName());
        }
        return null;
    }

}
