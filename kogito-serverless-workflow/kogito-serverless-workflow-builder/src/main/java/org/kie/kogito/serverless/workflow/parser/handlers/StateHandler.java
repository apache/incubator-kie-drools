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
import java.util.Map;
import java.util.Optional;

import org.jbpm.process.instance.impl.actions.HandleMessageAction;
import org.jbpm.ruleflow.core.Metadata;
import org.jbpm.ruleflow.core.RuleFlowNodeContainerFactory;
import org.jbpm.ruleflow.core.factory.ActionNodeFactory;
import org.jbpm.ruleflow.core.factory.BoundaryEventNodeFactory;
import org.jbpm.ruleflow.core.factory.EndNodeFactory;
import org.jbpm.ruleflow.core.factory.JoinFactory;
import org.jbpm.ruleflow.core.factory.NodeFactory;
import org.jbpm.ruleflow.core.factory.StartNodeFactory;
import org.jbpm.workflow.core.node.Join;
import org.kie.kogito.serverless.workflow.parser.NodeIdGenerator;
import org.kie.kogito.serverless.workflow.parser.ServerlessWorkflowParser;
import org.kie.kogito.serverless.workflow.parser.util.ServerlessWorkflowUtils;

import io.serverlessworkflow.api.Workflow;
import io.serverlessworkflow.api.error.Error;
import io.serverlessworkflow.api.interfaces.State;
import io.serverlessworkflow.api.produce.ProduceEvent;
import io.serverlessworkflow.api.transitions.Transition;

public abstract class StateHandler<S extends State, T extends NodeFactory<T, P>, P extends RuleFlowNodeContainerFactory<P, ?>> {

    protected final S state;
    protected final Workflow workflow;
    protected final RuleFlowNodeContainerFactory<P, ?> factory;
    protected final NodeIdGenerator idGenerator;

    private StartNodeFactory<P> startNodeFactory;
    private EndNodeFactory<P> endNodeFactory;
    private T node;
    private JoinFactory<P> join;
    private List<Long> incomingConnections = new ArrayList<>();

    protected StateHandler(S state, Workflow workflow, RuleFlowNodeContainerFactory<P, ?> factory,
            NodeIdGenerator idGenerator) {
        this.workflow = workflow;
        this.factory = factory;
        this.state = state;
        this.idGenerator = idGenerator;
    }

    public void handleStart(String startState) {
        if (state.getName().equals(startState)) {
            startNodeFactory = factory.startNode(idGenerator.getId()).name(ServerlessWorkflowParser.NODE_START_NAME);
            startNodeFactory.done();
        }
    }

    public void handleEnd() {
        if (state.getEnd() != null) {
            endNodeFactory = factory.endNode(idGenerator.getId()).name(ServerlessWorkflowParser.NODE_END_NAME);
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

    public void handleState() {
        node = makeNode();
        node.done();
        connectStart();
        connectEnd();
    }

    public void connect(long sourceId) {
        incomingConnections.add(sourceId);
    }

    public void handleConnections() {
        NodeFactory<?, P> incoming = getIncomingNode();
        for (long sourceId : incomingConnections) {
            factory.connection(sourceId, incoming.getNode().getId());
        }
    }

    public void handleErrors(Map<String, StateHandler<?, ?, ?>> stateConnection) {
        for (Error error : state.getOnErrors()) {
            String eventType = "Error-" + node.getNode().getMetaData().get("UniqueId");
            BoundaryEventNodeFactory<P> boundaryNode =
                    factory.boundaryEventNode(idGenerator.getId()).attachedTo(node.getNode().getId()).metaData(
                            "EventType", Metadata.EVENT_TYPE_ERROR).metaData("HasErrorEvent", true);
            if (error.getCode() != null) {
                boundaryNode.metaData("ErrorEvent", error.getCode());
                eventType += "-" + error.getCode();
            }
            boundaryNode.eventType(eventType).name("Error-" + node.getNode().getName() + "-" + error.getCode());
            factory.exceptionHandler(eventType, error.getCode());
            handleTransition(error.getTransition(), boundaryNode.getNode().getId(), stateConnection);
        }
    }

    public void handleTransitions(Map<String, StateHandler<?, ?, ?>> stateConnection) {
        handleTransition(state.getTransition(), getOutgoingNode().getNode().getId(), stateConnection);

    }

    protected void connectStart() {
        if (startNodeFactory != null) {
            factory.connection(startNodeFactory.getNode().getId(), node.getNode().getId());
        }
    }

    protected void connectEnd() {
        if (endNodeFactory != null) {
            factory.connection(getOutgoingNode().getNode().getId(), endNodeFactory.getNode().getId());
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
            join = factory.joinNode(idGenerator.getId()).type(Join.TYPE_OR).name("Join-" + node.getNode()
                    .getName());
            join.done().connection(join.getNode().getId(), node.getNode().getId());
            return (N) join;
        } else {
            return (N) getNode();
        }
    }

    protected abstract T makeNode();

    protected final void handleTransition(Transition transition,
            long sourceId,
            Map<String, StateHandler<?, ?, ?>> stateConnection) {
        handleTransition(transition, sourceId, stateConnection, Optional.empty());
    }

    protected final void handleTransition(Transition transition,
            long sourceId,
            Map<String, StateHandler<?, ?, ?>> stateConnection,
            Optional<HandleTransitionCallBack> callback) {
        if (transition != null && transition.getNextState() != null) {
            StateHandler<?, ?, ?> targetState = stateConnection.get(transition.getNextState());
            List<ProduceEvent> produceEvents = transition.getProduceEvents();
            if (produceEvents.isEmpty()) {
                targetState.connect(sourceId);
                callback.ifPresent(c -> c.onStateTarget(targetState));
            } else {
                final ActionNodeFactory<P> actionNode = factory.actionNode(idGenerator.getId());
                ActionNodeFactory<P> endNode = actionNode;
                ServerlessWorkflowParser.sendEventNode(actionNode, ServerlessWorkflowUtils.getWorkflowEventFor(workflow,
                        produceEvents.get(0).getEventRef()));
                if (produceEvents.size() > 1) {
                    ListIterator<ProduceEvent> iter = produceEvents.listIterator(1);
                    while (iter.hasNext()) {
                        ProduceEvent produceEvent = iter.next();
                        ActionNodeFactory<P> newNode = factory.actionNode(idGenerator.getId());
                        ServerlessWorkflowParser.sendEventNode(newNode, ServerlessWorkflowUtils.getWorkflowEventFor(
                                workflow, produceEvent.getEventRef())).done().connection(endNode.getNode().getId(),
                                        newNode.getNode().getId());
                        endNode = newNode;
                    }
                }
                factory.connection(sourceId, actionNode.getNode().getId());
                targetState.connect(endNode.getNode().getId());
                callback.ifPresent(c -> c.onIdTarget(actionNode.getNode().getId()));
            }
        } else {
            callback.ifPresent(HandleTransitionCallBack::onEmptyTarget);
        }
    }

    protected interface HandleTransitionCallBack {
        void onStateTarget(StateHandler<?, ?, ?> targetState);

        void onIdTarget(long targetId);

        void onEmptyTarget();
    }
}
