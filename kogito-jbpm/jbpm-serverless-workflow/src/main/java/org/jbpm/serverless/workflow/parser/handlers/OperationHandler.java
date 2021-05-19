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
import org.jbpm.ruleflow.core.factory.CompositeContextNodeFactory;
import org.jbpm.serverless.workflow.parser.NodeIdGenerator;

import io.serverlessworkflow.api.Workflow;
import io.serverlessworkflow.api.states.OperationState;

public class OperationHandler<P extends RuleFlowNodeContainerFactory<P, ?>> extends CompositeContextNodeHandler<OperationState, P> {

    protected OperationHandler(OperationState state, Workflow workflow, RuleFlowNodeContainerFactory<P, ?> factory, NodeIdGenerator idGenerator) {
        super(state, workflow, factory, idGenerator);
    }

    @Override
    public CompositeContextNodeFactory<P> makeNode() {
        return handleActions(factory.compositeContextNode(idGenerator.getId()).name(state.getName()).autoComplete(true), workflow.getFunctions(), state.getActions());
    }
}
