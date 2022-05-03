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

import java.util.List;
import java.util.stream.Collectors;

import org.jbpm.ruleflow.core.RuleFlowNodeContainerFactory;
import org.jbpm.ruleflow.core.factory.CompositeContextNodeFactory;
import org.jbpm.ruleflow.core.factory.NodeFactory;
import org.jbpm.ruleflow.core.factory.StartNodeFactory;
import org.jbpm.workflow.core.node.Join;
import org.jbpm.workflow.core.node.Split;
import org.kie.kogito.serverless.workflow.parser.ParserContext;
import org.kie.kogito.serverless.workflow.parser.ServerlessWorkflowParser;

import io.serverlessworkflow.api.Workflow;
import io.serverlessworkflow.api.events.OnEvents;
import io.serverlessworkflow.api.states.EventState;

import static org.kie.kogito.serverless.workflow.parser.handlers.NodeFactoryUtils.messageNode;

public class EventHandler extends CompositeContextNodeHandler<EventState> {

    protected EventHandler(EventState state, Workflow workflow, ParserContext parserContext) {
        super(state, workflow, parserContext);
    }

    @Override
    public void handleStart() {
        // disable standard procedure
    }

    @Override
    public MakeNodeResult makeNode(RuleFlowNodeContainerFactory<?, ?> factory) {
        return joinNodes(factory, state.getOnEvents().stream().map(onEvent -> processOnEvent(factory, onEvent)).collect(Collectors.toList()));
    }

    private MakeNodeResult processOnEvent(RuleFlowNodeContainerFactory<?, ?> factory, OnEvents onEvent) {
        MakeNodeResult result = joinNodes(factory,
                onEvent.getEventRefs().stream().map(onEventRef -> filterAndMergeNode(factory, onEvent.getEventDataFilter(), isStartState ? ServerlessWorkflowParser.DEFAULT_WORKFLOW_VAR : getVarName(),
                        (f, inputVar, outputVar) -> buildEventNode(f, onEventRef, inputVar, outputVar))).collect(Collectors.toList()));
        CompositeContextNodeFactory<?> embeddedSubProcess = handleActions(makeCompositeNode(factory), onEvent.getActions());
        connect(result.getOutgoingNode(), embeddedSubProcess);
        return new MakeNodeResult(result.getIncomingNode(), embeddedSubProcess);
    }

    private MakeNodeResult joinNodes(RuleFlowNodeContainerFactory<?, ?> factory, List<MakeNodeResult> nodes) {
        NodeFactory<?, ?> incomingNode = null;
        NodeFactory<?, ?> outgoingNode;
        if (nodes.size() == 1) {
            incomingNode = nodes.get(0).getIncomingNode();
            outgoingNode = nodes.get(0).getOutgoingNode();
        } else {
            if (!isStartState) {
                incomingNode = factory.splitNode(parserContext.newId()).name(state.getName() + "Split").type(Split.TYPE_AND);
            }
            outgoingNode = factory.joinNode(parserContext.newId()).name(state.getName() + "Join").type(state.isExclusive() ? Join.TYPE_XOR : Join.TYPE_AND);
            for (MakeNodeResult node : nodes) {
                connectNode(node, incomingNode, outgoingNode);
            }
        }
        return isStartState ? new MakeNodeResult(outgoingNode) : new MakeNodeResult(incomingNode, outgoingNode);
    }

    private void connectNode(MakeNodeResult node, NodeFactory<?, ?> incomingNode, NodeFactory<?, ?> outgoingNode) {
        if (!isStartState) {
            connect(incomingNode, node.getIncomingNode());
        }
        connect(node.getOutgoingNode(), outgoingNode);
    }

    private NodeFactory<?, ?> buildEventNode(RuleFlowNodeContainerFactory<?, ?> factory, String eventRef, String inputVar, String outputVar) {
        return isStartState ? messageStartNode(factory, eventRef, inputVar, outputVar) : consumeEventNode(factory, eventRef, inputVar, outputVar);
    }

    private StartNodeFactory<?> messageStartNode(RuleFlowNodeContainerFactory<?, ?> factory, String eventRef, String inputVar, String outputVar) {
        return messageNode(factory.startNode(parserContext.newId()), eventDefinition(eventRef), inputVar).trigger(ServerlessWorkflowParser.JSON_NODE, outputVar);
    }
}
