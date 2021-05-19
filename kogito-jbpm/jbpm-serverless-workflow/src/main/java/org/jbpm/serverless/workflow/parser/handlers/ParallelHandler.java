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
import org.jbpm.ruleflow.core.factory.JoinFactory;
import org.jbpm.ruleflow.core.factory.SplitFactory;
import org.jbpm.serverless.workflow.parser.NodeIdGenerator;
import org.jbpm.serverless.workflow.parser.ServerlessWorkflowParser;
import org.jbpm.workflow.core.node.Split;

import io.serverlessworkflow.api.Workflow;
import io.serverlessworkflow.api.branches.Branch;
import io.serverlessworkflow.api.states.ParallelState;

public class ParallelHandler<P extends RuleFlowNodeContainerFactory<P, ?>> extends StateHandler<ParallelState, SplitFactory<P>, P> {

    private JoinFactory<P> connectionNode;

    protected ParallelHandler(ParallelState state, Workflow workflow, RuleFlowNodeContainerFactory<P, ?> factory,
            NodeIdGenerator idGenerator) {
        super(state, workflow, factory, idGenerator);

    }

    @Override
    public SplitFactory<P> makeNode() {
        SplitFactory<P> nodeFactory = factory.splitNode(idGenerator.getId()).name(state.getName() + ServerlessWorkflowParser.NODE_START_NAME).type(Split.TYPE_AND);
        connectionNode = factory.joinNode(idGenerator.getId()).name(state.getName() + ServerlessWorkflowParser.NODE_END_NAME).type(Split.TYPE_AND);
        for (Branch branch : state.getBranches()) {
            long branchId = idGenerator.getId();
            if (branch.getWorkflowId() == null || branch.getWorkflowId().isEmpty()) {
                throw new IllegalStateException("Currently  supporting only branches with workflowid. Check branch " + branch.getName());
            }
            ServerlessWorkflowParser.subprocessNode(factory.subProcessNode(branchId).name(branch.getName()).processId(branch
                    .getWorkflowId()).waitForCompletion(true)).done().connection(nodeFactory.getNode().getId(), branchId).connection(branchId, connectionNode.getNode().getId());
        }
        return nodeFactory;
    }

    @SuppressWarnings("unchecked")
    @Override
    public JoinFactory<P> getConnectionNode() {
        return connectionNode;
    }
}
