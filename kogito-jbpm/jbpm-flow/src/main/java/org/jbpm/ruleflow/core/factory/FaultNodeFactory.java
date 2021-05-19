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

import org.jbpm.ruleflow.core.RuleFlowNodeContainerFactory;
import org.jbpm.workflow.core.NodeContainer;
import org.jbpm.workflow.core.node.FaultNode;

public class FaultNodeFactory<T extends RuleFlowNodeContainerFactory<T, ?>> extends ExtendedNodeFactory<FaultNodeFactory<T>, T> {

    public static final String METHOD_FAULT_NAME = "faultName";
    public static final String METHOD_FAULT_VARIABLE = "faultVariable";
    public static final String METHOD_TERMINATE_PARENT = "terminateParent";

    public FaultNodeFactory(T nodeContainerFactory, NodeContainer nodeContainer, long id) {
        super(nodeContainerFactory, nodeContainer, new FaultNode(), id);
    }

    protected FaultNode getFaultNode() {
        return (FaultNode) getNode();
    }

    public FaultNodeFactory<T> faultVariable(String faultVariable) {
        ((FaultNode) getNode()).setFaultVariable(faultVariable);
        return this;
    }

    public FaultNodeFactory<T> faultName(String faultName) {
        ((FaultNode) getNode()).setFaultName(faultName);
        return this;
    }

    public FaultNodeFactory<T> terminateParent(Boolean terminateParent) {
        getFaultNode().setTerminateParent(terminateParent);
        return this;
    }
}
