/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.serverless.workflow.parser.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;

import org.jbpm.process.core.context.exception.CompensationScope;
import org.jbpm.process.instance.impl.actions.HandleMessageAction;
import org.jbpm.ruleflow.core.Metadata;
import org.jbpm.ruleflow.core.RuleFlowNodeContainerFactory;
import org.jbpm.ruleflow.core.factory.ActionNodeFactory;
import org.jbpm.ruleflow.core.factory.BoundaryEventNodeFactory;
import org.jbpm.ruleflow.core.factory.CompositeContextNodeFactory;
import org.jbpm.ruleflow.core.factory.EndNodeFactory;
import org.jbpm.ruleflow.core.factory.JoinFactory;
import org.jbpm.ruleflow.core.factory.NodeFactory;
import org.jbpm.ruleflow.core.factory.StartNodeFactory;
import org.jbpm.workflow.core.node.Join;
import org.kie.kogito.serverless.workflow.parser.ParserContext;
import org.kie.kogito.serverless.workflow.parser.ServerlessWorkflowParser;
import org.kie.kogito.serverless.workflow.suppliers.CompensationActionSupplier;
import org.kie.kogito.serverless.workflow.suppliers.ExpressionActionSupplier;
import org.kie.kogito.serverless.workflow.utils.ServerlessWorkflowUtils;

import io.serverlessworkflow.api.Workflow;
import io.serverlessworkflow.api.error.Error;
import io.serverlessworkflow.api.filters.StateDataFilter;
import io.serverlessworkflow.api.interfaces.State;
import io.serverlessworkflow.api.produce.ProduceEvent;
import io.serverlessworkflow.api.transitions.Transition;

public abstract class StateHandler<S extends State> {

    protected final S state;
    protected final Workflow workflow;
    protected final ParserContext parserContext;

    private StartNodeFactory<?> startNodeFactory;
    private EndNodeFactory<?> endNodeFactory;

    private NodeFactory<?, ?> node;
    private NodeFactory<?, ?> outgoingNode;

    private JoinFactory<?> join;
    private List<Long> incomingConnections = new ArrayList<>();

    protected StateHandler(S state, Workflow workflow, ParserContext parserContext) {
        this.workflow = workflow;
        this.state = state;
        this.parserContext = parserContext;
    }

    public boolean usedForCompensation() {
        return false;
    }

    public void handleStart() {
        if (state.getName().equals(workflow.getStart().getStateName())) {
            startNodeFactory = parserContext.factory().startNode(parserContext.newId()).name(ServerlessWorkflowParser.NODE_START_NAME);
            startNodeFactory.done();
        }
    }

    public void handleEnd() {
        if (state.getEnd() != null) {
            endNodeFactory = parserContext.factory().endNode(parserContext.newId()).name(ServerlessWorkflowParser.NODE_END_NAME);
            List<ProduceEvent> produceEvents = state.getEnd().getProduceEvents();
            if (produceEvents == null || produceEvents.isEmpty()) {
                endNodeFactory.terminate(true);
            } else {
                ServerlessWorkflowParser.sendEventNode(
                        endNodeFactory.terminate(false).action(new HandleMessageAction(
                                ServerlessWorkflowParser.JSON_NODE, ServerlessWorkflowParser.DEFAULT_WORKFLOW_VAR)),
                        ServerlessWorkflowUtils.getWorkflowEventFor(workflow, produceEvents.get(0).getEventRef()));
            }
            endNodeFactory.done();
        }
    }

    private void handleCompensation(RuleFlowNodeContainerFactory<?, ?> factory) {
        StateHandler<?> compensation = parserContext.getStateHandler(state.getCompensatedBy());
        if (compensation == null) {
            throw new IllegalArgumentException("State " + getState().getName() + " refers to a compensation " + state.getCompensatedBy() + " which cannot be found");
        }
        parserContext.setCompensation();
        long eventCompensationId = parserContext.newId();
        long subprocessCompensationId = parserContext.newId();
        long startCompensationId = parserContext.newId();
        String uniqueId = (String) outgoingNode.getNode().getMetaData().get(Metadata.UNIQUE_ID);
        factory.boundaryEventNode(eventCompensationId).addCompensationHandler(uniqueId).attachedTo(uniqueId).eventType("Compensation").metaData(Metadata.EVENT_TYPE, "compensation");
        CompositeContextNodeFactory<?> embeddedSubProcess =
                factory.compositeContextNode(subprocessCompensationId).autoComplete(true).metaData("isForCompensation", true).startNode(startCompensationId).interrupting(true).done();
        factory.association(eventCompensationId, subprocessCompensationId, null);
        long lastNodeId = handleCompensation(embeddedSubProcess, compensation);
        embeddedSubProcess.connection(startCompensationId, lastNodeId);
        compensation = parserContext.getStateHandler(compensation);
        while (compensation != null) {
            if (!compensation.usedForCompensation()) {
                throw new IllegalArgumentException("compesation node can only have transition to other compensation node. Node " + compensation.getState().getName() + " is not used for compensation");
            }
            lastNodeId = handleCompensation(embeddedSubProcess, compensation);
            compensation = parserContext.getStateHandler(compensation);
        }
        long endCompensationId = parserContext.newId();
        embeddedSubProcess.endNode(endCompensationId).terminate(false).done().connection(lastNodeId, endCompensationId);
    }

    private long handleCompensation(RuleFlowNodeContainerFactory<?, ?> embeddedSubProcess,
            StateHandler<?> compensation) {
        if (compensation.getState().getCompensatedBy() != null) {
            throw new IllegalArgumentException("Serverless workflow specification forbids nested compensations, hence state " + compensation.getState().getName() + " is not valid");
        }
        compensation.handleState(embeddedSubProcess);
        Transition transition = compensation.getState().getTransition();
        long lastNodeId = compensation.getNode().getNode().getId();
        compensation.handleTransitions(embeddedSubProcess, transition, lastNodeId);
        compensation.handleErrors(embeddedSubProcess);
        compensation.handleConnections(embeddedSubProcess);
        return lastNodeId;
    }

    public void handleState() {
        handleState(parserContext.factory());
    }

    protected void handleState(RuleFlowNodeContainerFactory<?, ?> factory) {
        MakeNodeResult result = makeNode(factory);
        node = result.getIncomingNode();
        outgoingNode = result.getOutgoingNode();
        if (state.getCompensatedBy() != null) {
            handleCompensation(factory);
        }
        node.done();
        StateDataFilter stateFilter = state.getStateDataFilter();
        if (stateFilter != null) {
            String input = stateFilter.getInput();

            if (input != null) {
                ActionNodeFactory<?> actionNode = handleStateFilter(factory, input);
                factory.connection(actionNode.getNode().getId(), node.getNode().getId());
                node = actionNode;
            }
            String output = stateFilter.getOutput();
            if (output != null) {
                ActionNodeFactory<?> actionNode = handleStateFilter(factory, output);
                factory.connection(outgoingNode.getNode().getId(), actionNode.getNode().getId());
                outgoingNode = actionNode;
            }
        }
        connectStart(factory);
        connectEnd(factory);
    }

    private ActionNodeFactory<?> handleStateFilter(RuleFlowNodeContainerFactory<?, ?> factory, String filter) {
        ActionNodeFactory<?> result =
                factory.actionNode(parserContext.newId()).action(new ExpressionActionSupplier(workflow.getExpressionLang(), filter, ServerlessWorkflowParser.DEFAULT_WORKFLOW_VAR));
        result.done();
        return result;
    }

    public void connect(RuleFlowNodeContainerFactory<?, ?> factory, long sourceId) {
        incomingConnections.add(sourceId);
    }

    public void handleConnections() {
        handleConnections(parserContext.factory());
    }

    protected void handleConnections(RuleFlowNodeContainerFactory<?, ?> factory) {
        NodeFactory<?, ?> incoming = getIncomingNode(factory);
        for (long sourceId : incomingConnections) {
            factory.connection(sourceId, incoming.getNode().getId());
        }
    }

    public void handleErrors() {
        handleErrors(parserContext.factory());
    }

    protected void handleErrors(RuleFlowNodeContainerFactory<?, ?> factory) {
        for (Error error : state.getOnErrors()) {
            String eventType = "Error-" + node.getNode().getMetaData().get("UniqueId");
            BoundaryEventNodeFactory<?> boundaryNode =
                    factory.boundaryEventNode(parserContext.newId()).attachedTo(node.getNode().getId()).metaData(
                            "EventType", Metadata.EVENT_TYPE_ERROR).metaData("HasErrorEvent", true);
            if (error.getCode() != null) {
                boundaryNode.metaData("ErrorEvent", error.getCode());
                eventType += "-" + error.getCode();
            }
            boundaryNode.eventType(eventType).name("Error-" + node.getNode().getName() + "-" + error.getCode());
            factory.exceptionHandler(eventType, error.getCode());
            handleTransitions(factory, error.getTransition(), boundaryNode.getNode().getId());
        }
    }

    public void handleTransitions() {
        handleTransitions(parserContext.factory(), state.getTransition(), outgoingNode.getNode().getId());
    }

    protected void handleTransitions(RuleFlowNodeContainerFactory<?, ?> factory,
            Transition transition,
            long sourceId) {
        handleTransition(factory, transition, sourceId, Optional.empty());
    }

    protected void connectStart(RuleFlowNodeContainerFactory<?, ?> factory) {
        if (startNodeFactory != null) {
            factory.connection(startNodeFactory.getNode().getId(), node.getNode().getId());
        }
    }

    private void connectEnd(RuleFlowNodeContainerFactory<?, ?> factory) {
        if (endNodeFactory != null) {
            if (state.getEnd().isCompensate()) {
                endNodeFactory.done().connection(compensationEvent(factory, outgoingNode.getNode().getId()), endNodeFactory.getNode().getId());
            } else {
                factory.connection(outgoingNode.getNode().getId(), endNodeFactory.getNode().getId());
            }
        }
    }

    public final NodeFactory<?, ?> getNode() {
        return node;
    }

    public S getState() {
        return state;
    }

    public NodeFactory<?, ?> getIncomingNode(RuleFlowNodeContainerFactory<?, ?> factory) {
        if (join != null) {
            return join;
        } else if (incomingConnections.size() > 1) {
            join = factory.joinNode(parserContext.newId()).type(Join.TYPE_OR).name("Join-" + node.getNode()
                    .getName());
            join.done().connection(join.getNode().getId(), node.getNode().getId());
            return join;
        } else {
            return getNode();
        }
    }

    protected abstract MakeNodeResult makeNode(RuleFlowNodeContainerFactory<?, ?> factory);

    protected final void handleTransition(RuleFlowNodeContainerFactory<?, ?> factory,
            Transition transition,
            long sourceId,
            Optional<HandleTransitionCallBack> callback) {
        StateHandler<?> targetState = parserContext.getStateHandler(transition);
        if (targetState != null) {
            List<ProduceEvent> produceEvents = transition.getProduceEvents();
            if (produceEvents.isEmpty()) {
                if (transition.isCompensate()) {
                    long eventId = compensationEvent(factory, sourceId);
                    targetState.connect(factory, eventId);
                    callback.ifPresent(c -> c.onIdTarget(eventId));
                } else {
                    targetState.connect(factory, sourceId);
                    callback.ifPresent(c -> c.onStateTarget(targetState));
                }
            } else {
                final ActionNodeFactory<?> actionNode = factory.actionNode(parserContext.newId());
                ActionNodeFactory<?> endNode = actionNode;
                ServerlessWorkflowParser.sendEventNode(actionNode, ServerlessWorkflowUtils.getWorkflowEventFor(workflow,
                        produceEvents.get(0).getEventRef()));
                if (produceEvents.size() > 1) {
                    ListIterator<ProduceEvent> iter = produceEvents.listIterator(1);
                    while (iter.hasNext()) {
                        ProduceEvent produceEvent = iter.next();
                        ActionNodeFactory<?> newNode = factory.actionNode(parserContext.newId());
                        ServerlessWorkflowParser.sendEventNode(newNode, ServerlessWorkflowUtils.getWorkflowEventFor(
                                workflow, produceEvent.getEventRef())).done().connection(endNode.getNode().getId(),
                                        newNode.getNode().getId());
                        endNode = newNode;
                    }
                }
                factory.connection(sourceId, actionNode.getNode().getId());
                if (transition.isCompensate()) {
                    long eventId = compensationEvent(factory, sourceId);
                    callback.ifPresent(c -> c.onIdTarget(eventId));
                } else {
                    callback.ifPresent(c -> c.onIdTarget(actionNode.getNode().getId()));
                }
                targetState.connect(factory, endNode.getNode().getId());
            }
        } else {
            callback.ifPresent(HandleTransitionCallBack::onEmptyTarget);
        }
    }

    private long compensationEvent(RuleFlowNodeContainerFactory<?, ?> factory, long sourceId) {
        long eventId = parserContext.newId();
        factory.actionNode(eventId).name(state.getName() + "-" + eventId).action(new CompensationActionSupplier(CompensationScope.IMPLICIT_COMPENSATION_PREFIX + workflow.getId())).done()
                .connection(sourceId, eventId);
        return eventId;
    }

    protected interface HandleTransitionCallBack {
        void onStateTarget(StateHandler<?> targetState);

        void onIdTarget(long targetId);

        void onEmptyTarget();
    }
}
