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
    public MakeNodeResult makeNode(RuleFlowNodeContainerFactory<?, ?> factory) {
        CompositeContextNodeFactory<?> embeddedSubProcess = factory.compositeContextNode(parserContext.newId()).name(state.getName()).autoComplete(true);
        NodeFactory<?, ?> currentNode = embeddedSubProcess.startNode(parserContext.newId()).name("EmbeddedStart");
        if (state.getAction() != null) {
            currentNode = connect(currentNode, getActionNode(embeddedSubProcess, state.getAction()));
        }
        currentNode = connect(currentNode, makeTimeoutNode(embeddedSubProcess,
                filterAndMergeNode(embeddedSubProcess, state.getEventDataFilter(), (f, inputVar, outputVar) -> consumeEventNode(f, state.getEventRef(), inputVar, outputVar))));
        connect(currentNode, embeddedSubProcess.endNode(parserContext.newId()).name("EmbeddedEnd").terminate(true)).done();
        handleErrors(factory, embeddedSubProcess);
        return new MakeNodeResult(embeddedSubProcess);
    }
}
