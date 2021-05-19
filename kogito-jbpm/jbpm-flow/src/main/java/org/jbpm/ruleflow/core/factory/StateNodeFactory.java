/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
import org.jbpm.workflow.core.impl.ConnectionRef;
import org.jbpm.workflow.core.impl.ConstraintImpl;
import org.jbpm.workflow.core.node.StateNode;

import static org.jbpm.workflow.core.Node.CONNECTION_DEFAULT_TYPE;

public class StateNodeFactory<T extends RuleFlowNodeContainerFactory<T, ?>> extends AbstractCompositeNodeFactory<StateNodeFactory<T>, T> {

    public static final String METHOD_CONSTRAINT = "constraint";

    public StateNodeFactory(T nodeContainerFactory, NodeContainer nodeContainer, long id) {
        super(nodeContainerFactory, nodeContainer, new StateNode(), id);
    }

    protected StateNode getStateNode() {
        return (StateNode) node;
    }

    public StateNodeFactory<T> constraint(String connectionId, long nodeId, String type, String dialect, String constraint, int priority) {
        ConstraintImpl constraintImpl = new ConstraintImpl();
        constraintImpl.setName(connectionId);
        constraintImpl.setType(type);
        constraintImpl.setDialect(dialect);
        constraintImpl.setConstraint(constraint);
        constraintImpl.setPriority(priority);
        getStateNode().addConstraint(
                new ConnectionRef(connectionId, nodeId, CONNECTION_DEFAULT_TYPE),
                constraintImpl);
        return this;
    }
}
