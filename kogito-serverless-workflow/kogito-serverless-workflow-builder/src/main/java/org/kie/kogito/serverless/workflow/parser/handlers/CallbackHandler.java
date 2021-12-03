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

import org.jbpm.ruleflow.core.RuleFlowNodeContainerFactory;
import org.jbpm.ruleflow.core.factory.CompositeContextNodeFactory;
import org.jbpm.ruleflow.core.factory.NodeFactory;
import org.kie.kogito.serverless.workflow.parser.ParserContext;
import org.kie.kogito.serverless.workflow.parser.ServerlessWorkflowParser;
import org.kie.kogito.serverless.workflow.utils.ServerlessWorkflowUtils;

import io.serverlessworkflow.api.Workflow;
import io.serverlessworkflow.api.states.CallbackState;

public class CallbackHandler extends CompositeContextNodeHandler<CallbackState> {

    protected CallbackHandler(CallbackState state, Workflow workflow, ParserContext parserContext) {
        super(state, workflow, parserContext);
    }

    @Override
    public boolean usedForCompensation() {
        return state.isUsedForCompensation();
    }

    @Override
    public CompositeContextNodeFactory<?> makeNode(RuleFlowNodeContainerFactory<?, ?> factory) {
        CompositeContextNodeFactory<?> embeddedSubProcess = factory.compositeContextNode(parserContext.newId()).name(state.getName()).autoComplete(true);
        NodeFactory<?, ?> startNode = embeddedSubProcess.startNode(parserContext.newId()).name("EmbeddedStart");
        NodeFactory<?, ?> currentNode;
        if (state.getAction() != null) {
            currentNode = getActionNode(embeddedSubProcess, state.getAction());
            embeddedSubProcess.connection(startNode.getNode().getId(), currentNode.getNode().getId());
            startNode = currentNode;
        }
        currentNode = ServerlessWorkflowParser.messageEventNode(embeddedSubProcess.eventNode(parserContext.newId()), ServerlessWorkflowUtils
                .getWorkflowEventFor(workflow, state.getEventRef()));
        embeddedSubProcess.connection(startNode.getNode().getId(), currentNode.getNode().getId());
        long endId = parserContext.newId();
        embeddedSubProcess.endNode(endId).name("EmbeddedEnd").terminate(true).done().connection(currentNode
                .getNode().getId(), endId);
        return embeddedSubProcess;
    }
}
