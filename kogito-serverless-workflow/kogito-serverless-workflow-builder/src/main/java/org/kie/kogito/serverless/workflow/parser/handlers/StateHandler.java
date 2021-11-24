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
import org.kie.kogito.serverless.workflow.parser.util.ServerlessWorkflowUtils;
import org.kie.kogito.serverless.workflow.suppliers.CompensationActionSupplier;

import io.serverlessworkflow.api.Workflow;
import io.serverlessworkflow.api.error.Error;
import io.serverlessworkflow.api.interfaces.State;
import io.serverlessworkflow.api.produce.ProduceEvent;
import io.serverlessworkflow.api.transitions.Transition;

public abstract class StateHandler<S extends State, T extends NodeFactory<T, P>, P extends RuleFlowNodeContainerFactory<P, ?>> {

    protected final S state;
    protected final Workflow workflow;
    protected final RuleFlowNodeContainerFactory<P, ?> factory;
    protected final ParserContext parserContext;

    private StartNodeFactory<P> startNodeFactory;
    private EndNodeFactory<P> endNodeFactory;

    private T node;
    private JoinFactory<P> join;
    private List<Long> incomingConnections = new ArrayList<>();

    protected StateHandler(S state, Workflow workflow, RuleFlowNodeContainerFactory<P, ?> factory, ParserContext parserContext) {
        this.workflow = workflow;
        this.state = state;
        this.factory = factory;
        this.parserContext = parserContext;
    }

    public boolean usedForCompensation() {
        return false;
    }

    public void handleStart() {
        if (state.getName().equals(workflow.getStart().getStateName())) {
            startNodeFactory = factory.startNode(parserContext.newId()).name(ServerlessWorkflowParser.NODE_START_NAME);
        }
    }

    public void handleEnd() {
        if (state.getEnd() != null) {
            endNodeFactory = factory.endNode(parserContext.newId()).name(ServerlessWorkflowParser.NODE_END_NAME);
            List<ProduceEvent> produceEvents = state.getEnd().getProduceEvents();
            if (produceEvents == null || produceEvents.isEmpty()) {
                endNodeFactory.terminate(true);
            } else {
                ServerlessWorkflowParser.sendEventNode(
                        endNodeFactory.terminate(false).action(new HandleMessageAction(
                                ServerlessWorkflowParser.JSON_NODE, ServerlessWorkflowParser.DEFAULT_WORKFLOW_VAR)),
                        ServerlessWorkflowUtils.getWorkflowEventFor(workflow, produceEvents.get(0).getEventRef()));
            }
        }
    }

    private void handleCompensation() {
        StateHandler<?, ?, ?> compensation = parserContext.getStateHandler(state.getCompensatedBy());
        if (compensation == null) {
            throw new IllegalArgumentException("State " + getState().getName() + " refers to a compensation " + state.getCompensatedBy() + " which cannot be found");
        }
        parserContext.setCompensation();
        long eventCompensationId = parserContext.newId();
        long subprocessCompensationId = parserContext.newId();
        long startCompensationId = parserContext.newId();
        String uniqueId = (String) getOutgoingNode().getNode().getMetaData().get(Metadata.UNIQUE_ID);
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

    private <N extends RuleFlowNodeContainerFactory<N, ?>> long handleCompensation(RuleFlowNodeContainerFactory<N, ?> embeddedSubProcess,
            StateHandler<?, ?, ?> compensation) {
        if (compensation.getState().getCompensatedBy() != null) {
            throw new IllegalArgumentException("Serverless workflow specification forbids nested compensations, hence state " + compensation.getState().getName() + " is not valid");
        }
        compensation.handleState(embeddedSubProcess);
        Transition transition = compensation.getState().getTransition();
        long lastNodeId = compensation.getNode().getNode().getId();
        compensation.handleTransitions(embeddedSubProcess, transition, lastNodeId);
        compensation.handleConnections(embeddedSubProcess);
        return lastNodeId;
    }

    public void handleState() {
        handleState(factory);
    }

    protected <N extends RuleFlowNodeContainerFactory<N, ?>> void handleState(RuleFlowNodeContainerFactory<N, ?> factory) {
        node = makeNode(factory);
        connectStart();
        connectEnd();
        if (state.getCompensatedBy() != null) {
            handleCompensation();
        }
    }

    public void connect(long sourceId) {
        incomingConnections.add(sourceId);
    }

    public void handleConnections() {
        handleConnections(factory);
    }

    protected <N extends RuleFlowNodeContainerFactory<N, ?>> void handleConnections(RuleFlowNodeContainerFactory<N, ?> factory) {
        NodeFactory<?, P> incoming = getIncomingNode();
        for (long sourceId : incomingConnections) {
            factory.connection(sourceId, incoming.getNode().getId());
        }
    }

    public void handleErrors() {
        for (Error error : state.getOnErrors()) {
            String eventType = "Error-" + node.getNode().getMetaData().get("UniqueId");
            BoundaryEventNodeFactory<P> boundaryNode =
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
        handleTransitions(factory, state.getTransition(), getOutgoingNode().getNode().getId());
    }

    protected <N extends RuleFlowNodeContainerFactory<N, ?>> void handleTransitions(RuleFlowNodeContainerFactory<N, ?> factory,
            Transition transition,
            long sourceId) {
        handleTransition(factory, transition, sourceId, Optional.empty());
    }

    protected void connectStart() {
        if (startNodeFactory != null) {
            factory.connection(startNodeFactory.getNode().getId(), node.getNode().getId());
        }
    }

    protected void connectEnd() {
        if (endNodeFactory != null) {
            if (state.getEnd().isCompensate()) {
                endNodeFactory.done().connection(compensationEvent(getOutgoingNode().getNode().getId()), endNodeFactory.getNode().getId());
            } else {
                factory.connection(getOutgoingNode().getNode().getId(), endNodeFactory.getNode().getId());
            }
        }
    }

    public T getNode() {
        return node;
    }

    public S getState() {
        return state;
    }

    @SuppressWarnings("unchecked")
    public <N extends NodeFactory<N, P>> N getOutgoingNode() {
        return (N) getNode();
    }

    @SuppressWarnings("unchecked")
    public <N extends NodeFactory<N, P>> N getIncomingNode() {
        if (join != null) {
            return (N) join;
        } else if (incomingConnections.size() > 1) {
            join = factory.joinNode(parserContext.newId()).type(Join.TYPE_OR).name("Join-" + node.getNode()
                    .getName());
            join.done().connection(join.getNode().getId(), node.getNode().getId());
            return (N) join;
        } else {
            return (N) getNode();
        }
    }

    protected abstract T makeNode(RuleFlowNodeContainerFactory<?, ?> factory);

    protected final <N extends RuleFlowNodeContainerFactory<N, ?>> void handleTransition(RuleFlowNodeContainerFactory<N, ?> factory,
            Transition transition,
            long sourceId,
            Optional<HandleTransitionCallBack> callback) {
        StateHandler<?, ?, ?> targetState = parserContext.getStateHandler(transition);
        if (targetState != null) {
            List<ProduceEvent> produceEvents = transition.getProduceEvents();
            if (produceEvents.isEmpty()) {
                if (transition.isCompensate()) {
                    long eventId = compensationEvent(sourceId);
                    targetState.connect(eventId);
                    callback.ifPresent(c -> c.onIdTarget(eventId));
                } else {
                    targetState.connect(sourceId);
                    callback.ifPresent(c -> c.onStateTarget(targetState));
                }
            } else {
                final ActionNodeFactory<N> actionNode = factory.actionNode(parserContext.newId());
                ActionNodeFactory<N> endNode = actionNode;
                ServerlessWorkflowParser.sendEventNode(actionNode, ServerlessWorkflowUtils.getWorkflowEventFor(workflow,
                        produceEvents.get(0).getEventRef()));
                if (produceEvents.size() > 1) {
                    ListIterator<ProduceEvent> iter = produceEvents.listIterator(1);
                    while (iter.hasNext()) {
                        ProduceEvent produceEvent = iter.next();
                        ActionNodeFactory<N> newNode = factory.actionNode(parserContext.newId());
                        ServerlessWorkflowParser.sendEventNode(newNode, ServerlessWorkflowUtils.getWorkflowEventFor(
                                workflow, produceEvent.getEventRef())).done().connection(endNode.getNode().getId(),
                                        newNode.getNode().getId());
                        endNode = newNode;
                    }
                }
                factory.connection(sourceId, actionNode.getNode().getId());
                if (transition.isCompensate()) {
                    long eventId = compensationEvent(sourceId);
                    callback.ifPresent(c -> c.onIdTarget(eventId));
                } else {
                    callback.ifPresent(c -> c.onIdTarget(actionNode.getNode().getId()));
                }
                targetState.connect(endNode.getNode().getId());
            }
        } else {
            callback.ifPresent(HandleTransitionCallBack::onEmptyTarget);
        }
    }

    private long compensationEvent(long sourceId) {
        long eventId = parserContext.newId();
        factory.actionNode(eventId).name(state.getName() + "-" + eventId).action(new CompensationActionSupplier(CompensationScope.IMPLICIT_COMPENSATION_PREFIX + workflow.getId())).done()
                .connection(sourceId, eventId);
        return eventId;
    }

    protected interface HandleTransitionCallBack {
        void onStateTarget(StateHandler<?, ?, ?> targetState);

        void onIdTarget(long targetId);

        void onEmptyTarget();
    }
}
