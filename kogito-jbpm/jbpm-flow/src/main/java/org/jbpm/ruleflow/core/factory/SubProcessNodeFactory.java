/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.ruleflow.core.factory;

import org.jbpm.process.core.context.variable.Mappable;
import org.jbpm.ruleflow.core.RuleFlowNodeContainerFactory;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.NodeContainer;
import org.jbpm.workflow.core.node.SubProcessFactory;
import org.jbpm.workflow.core.node.SubProcessNode;

public class SubProcessNodeFactory extends StateBasedNodeFactory implements MappableNodeFactory {

    public static final String METHOD_PROCESS_ID = "processId";
    public static final String METHOD_PROCESS_NAME = "processName";
    public static final String METHOD_WAIT_FOR_COMPLETION = "waitForCompletion";
    public static final String METHOD_INDEPENDENT = "independent";

    public SubProcessNodeFactory(RuleFlowNodeContainerFactory nodeContainerFactory, NodeContainer nodeContainer, long id) {
        super(nodeContainerFactory, nodeContainer, id);
    }

    protected Node createNode() {
        return new SubProcessNode();
    }

    protected SubProcessNode getSubProcessNode() {
        return (SubProcessNode) getNode();
    }

    @Override
    public SubProcessNodeFactory name(String name) {
        super.name(name);
        return this;
    }

    @Override
    public SubProcessNodeFactory onEntryAction(String dialect, String action) {
        super.onEntryAction(dialect, action);
        return this;
    }

    @Override
    public SubProcessNodeFactory onExitAction(String dialect, String action) {
        super.onExitAction(dialect, action);
        return this;
    }

    @Override
    public SubProcessNodeFactory timer(String delay, String period, String dialect, String action) {
        super.timer(delay, period, dialect, action);
        return this;
    }

    @Override
    public Mappable getMappableNode() {
        return getSubProcessNode();
    }

    @Override
    public SubProcessNodeFactory inMapping(String parameterName, String variableName) {
        MappableNodeFactory.super.inMapping(parameterName, variableName);
        return this;
    }

    @Override
    public SubProcessNodeFactory outMapping(String parameterName, String variableName) {
        MappableNodeFactory.super.outMapping(parameterName, variableName);
        return this;
    }

    public SubProcessNodeFactory processId(final String processId) {
        getSubProcessNode().setProcessId(processId);
        return this;
    }

    public SubProcessNodeFactory processName(final String processName) {
        getSubProcessNode().setProcessName(processName);
        return this;
    }

    public SubProcessNodeFactory waitForCompletion(boolean waitForCompletion) {
        getSubProcessNode().setWaitForCompletion(waitForCompletion);
        return this;
    }

    public SubProcessNodeFactory independent(boolean independent) {
        getSubProcessNode().setIndependent(independent);
        return this;
    }

    public <T> SubProcessNodeFactory subProcessFactory(SubProcessFactory<T> factory) {
        getSubProcessNode().setSubProcessFactory(factory);
        return this;
    }
}
