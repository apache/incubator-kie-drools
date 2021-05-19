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

import org.jbpm.ruleflow.core.RuleFlowNodeContainerFactory;
import org.jbpm.ruleflow.core.factory.SubProcessNodeFactory;
import org.jbpm.serverless.workflow.parser.NodeIdGenerator;
import org.jbpm.serverless.workflow.parser.ServerlessWorkflowParser;

import io.serverlessworkflow.api.Workflow;
import io.serverlessworkflow.api.states.SubflowState;

public class SubflowHandler<P extends RuleFlowNodeContainerFactory<P, ?>> extends StateHandler<SubflowState, SubProcessNodeFactory<P>, P> {

    protected SubflowHandler(SubflowState state, Workflow workflow, RuleFlowNodeContainerFactory<P, ?> factory,
            NodeIdGenerator idGenerator) {
        super(state, workflow, factory, idGenerator);
    }

    @Override
    public SubProcessNodeFactory<P> makeNode() {
        return ServerlessWorkflowParser.subprocessNode(factory.subProcessNode(idGenerator.getId()).name(state.getName()).processId(state
                .getWorkflowId()).waitForCompletion(state.isWaitForCompletion()));

    }

}
