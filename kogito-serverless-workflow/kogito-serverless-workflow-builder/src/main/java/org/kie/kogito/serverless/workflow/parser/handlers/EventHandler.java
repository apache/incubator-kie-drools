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

import org.jbpm.ruleflow.core.RuleFlowNodeContainerFactory;
import org.jbpm.ruleflow.core.factory.CompositeContextNodeFactory;
import org.jbpm.ruleflow.core.factory.NodeFactory;
import org.jbpm.ruleflow.core.factory.StartNodeFactory;
import org.jbpm.workflow.core.node.Join;
import org.kie.kogito.serverless.workflow.parser.ParserContext;
import org.kie.kogito.serverless.workflow.parser.ServerlessWorkflowParser;
import org.kie.kogito.serverless.workflow.parser.util.ServerlessWorkflowUtils;

import io.serverlessworkflow.api.Workflow;
import io.serverlessworkflow.api.events.OnEvents;
import io.serverlessworkflow.api.states.EventState;

public class EventHandler<P extends RuleFlowNodeContainerFactory<P, ?>> extends CompositeContextNodeHandler<EventState, P, CompositeContextNodeFactory<P>> {

    protected EventHandler(EventState state, Workflow workflow, RuleFlowNodeContainerFactory<P, ?> factory,
            ParserContext parserContext) {
        super(state, workflow, factory, parserContext);
    }

    private NodeFactory<?, P> startFactory;

    @Override
    public void handleStart() {
        if (!state.getName().equals(workflow.getStart().getStateName())) {
            throw new IllegalStateException("Event state " + state.getName() + "should be a start state");
        }
    }

    @Override
    public CompositeContextNodeFactory<P> makeNode(RuleFlowNodeContainerFactory<?, ?> factory) {
        OnEvents onEvent = state.getOnEvents().get(0);
        CompositeContextNodeFactory<P> nodeFactory = handleActions(onEvent.getActions());
        List<String> onEventRefs = onEvent.getEventRefs();
        if (onEventRefs.size() == 1) {
            startFactory = (NodeFactory<?, P>) ServerlessWorkflowParser.messageStartNode(factory.startNode(parserContext.newId()), ServerlessWorkflowUtils
                    .getWorkflowEventFor(workflow, onEventRefs.get(0)));
        } else {
            startFactory = (NodeFactory<?, P>) factory.joinNode(parserContext.newId()).name(state.getName() + "Split").type(Join.TYPE_XOR);
            for (String onEventRef : onEventRefs) {
                StartNodeFactory<P> newStartNode = (StartNodeFactory<P>) factory.startNode(parserContext.newId());
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
