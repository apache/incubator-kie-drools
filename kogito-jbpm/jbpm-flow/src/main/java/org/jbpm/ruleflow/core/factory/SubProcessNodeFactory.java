/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.ruleflow.core.factory;

import org.jbpm.process.core.ContextContainer;
import org.jbpm.process.core.context.variable.Mappable;
import org.jbpm.ruleflow.core.RuleFlowNodeContainerFactory;
import org.jbpm.workflow.core.NodeContainer;
import org.jbpm.workflow.core.node.SubProcessFactory;
import org.jbpm.workflow.core.node.SubProcessNode;
import org.kie.kogito.Model;

public class SubProcessNodeFactory<T extends RuleFlowNodeContainerFactory<T, ?>> extends StateBasedNodeFactory<SubProcessNodeFactory<T>, T>
        implements MappableNodeFactory<SubProcessNodeFactory<T>>, ContextContainerFactory<SubProcessNodeFactory<T>> {

    public static final String METHOD_PROCESS_ID = "processId";
    public static final String METHOD_PROCESS_NAME = "processName";
    public static final String METHOD_WAIT_FOR_COMPLETION = "waitForCompletion";
    public static final String METHOD_INDEPENDENT = "independent";

    public SubProcessNodeFactory(T nodeContainerFactory, NodeContainer nodeContainer, long id) {
        super(nodeContainerFactory, nodeContainer, new SubProcessNode(), id);
    }

    protected SubProcessNode getSubProcessNode() {
        return (SubProcessNode) getNode();
    }

    @Override
    public Mappable getMappableNode() {
        return getSubProcessNode();
    }

    public SubProcessNodeFactory<T> processId(final String processId) {
        getSubProcessNode().setProcessId(processId);
        return this;
    }

    public SubProcessNodeFactory<T> processName(final String processName) {
        getSubProcessNode().setProcessName(processName);
        return this;
    }

    public SubProcessNodeFactory<T> waitForCompletion(boolean waitForCompletion) {
        getSubProcessNode().setWaitForCompletion(waitForCompletion);
        return this;
    }

    public SubProcessNodeFactory<T> independent(boolean independent) {
        getSubProcessNode().setIndependent(independent);
        return this;
    }

    public SubProcessNodeFactory<T> subProcessNode(SubProcessFactory<? extends Model> factory) {
        getSubProcessNode().setSubProcessFactory(factory);
        return this;
    }

    @Override
    public ContextContainer getContextNode() {
        return getSubProcessNode();
    }
}
