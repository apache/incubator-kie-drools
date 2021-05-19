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
package org.jbpm.serverless.workflow.parser.handlers;

import java.util.List;

import org.jbpm.ruleflow.core.RuleFlowNodeContainerFactory;
import org.jbpm.ruleflow.core.factory.CompositeContextNodeFactory;
import org.jbpm.ruleflow.core.factory.NodeFactory;
import org.jbpm.ruleflow.core.factory.StartNodeFactory;
import org.jbpm.serverless.workflow.parser.NodeIdGenerator;
import org.jbpm.serverless.workflow.parser.ServerlessWorkflowParser;
import org.jbpm.serverless.workflow.parser.util.ServerlessWorkflowUtils;
import org.jbpm.workflow.core.node.Join;

import io.serverlessworkflow.api.Workflow;
import io.serverlessworkflow.api.events.OnEvents;
import io.serverlessworkflow.api.states.EventState;
import io.serverlessworkflow.api.workflow.Functions;

public class EventHandler<P extends RuleFlowNodeContainerFactory<P, ?>> extends CompositeContextNodeHandler<EventState, P> {

    protected EventHandler(EventState state, Workflow workflow, RuleFlowNodeContainerFactory<P, ?> factory,
            NodeIdGenerator idGenerator) {
        super(state, workflow, factory, idGenerator);
    }

    private NodeFactory<?, P> startFactory;

    @Override
    public void handleStart(String startState) {
        if (!state.getName().equals(startState)) {
            throw new IllegalStateException("Event state " + state.getName() + "should be a start state");
        }
    }

    @Override
    public CompositeContextNodeFactory<P> makeNode() {
        Functions workflowFunctions = workflow.getFunctions();
        OnEvents onEvent = state.getOnEvents().get(0);
        CompositeContextNodeFactory<P> nodeFactory = handleActions(factory.compositeContextNode(idGenerator.getId()).name(state.getName()).autoComplete(true), workflowFunctions, onEvent.getActions());
        List<String> onEventRefs = onEvent.getEventRefs();
        if (onEventRefs.size() == 1) {
            startFactory = ServerlessWorkflowParser.messageStartNode(factory.startNode(idGenerator.getId()), ServerlessWorkflowUtils
                    .getWorkflowEventFor(workflow, onEventRefs.get(0)));
        } else {
            startFactory = factory.joinNode(idGenerator.getId()).name(state.getName() + "Split").type(Join.TYPE_XOR);
            for (String onEventRef : onEventRefs) {
                StartNodeFactory<P> newStartNode = factory.startNode(idGenerator.getId());
                ServerlessWorkflowParser.messageStartNode(newStartNode, ServerlessWorkflowUtils.getWorkflowEventFor(workflow, onEventRef)).done().connection(newStartNode.getNode().getId(),
                        startFactory.getNode().getId());
            }
        }
        return nodeFactory;
    }

    @Override
    protected void connectStart() {
        factory.connection(startFactory.getNode().getId(), getNode().getNode().getId());
    }
}
