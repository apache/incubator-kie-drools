/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import org.jbpm.process.core.context.exception.ExceptionHandler;
import org.jbpm.process.core.datatype.DataType;
import org.jbpm.ruleflow.core.RuleFlowNodeContainerFactory;
import org.jbpm.workflow.core.NodeContainer;
import org.jbpm.workflow.core.impl.ConnectionRef;
import org.jbpm.workflow.core.impl.ConstraintImpl;
import org.jbpm.workflow.core.node.CompositeContextNode;
import org.jbpm.workflow.core.node.StateNode;

import static org.jbpm.workflow.core.Node.CONNECTION_DEFAULT_TYPE;

/**
 *
 */
public class StateNodeFactory extends CompositeContextNodeFactory {

    public static final String METHOD_CONSTRAINT = "constraint";

    public StateNodeFactory(RuleFlowNodeContainerFactory nodeContainerFactory, NodeContainer nodeContainer, long id) {
        super(nodeContainerFactory, nodeContainer, id);
    }

    @Override
    protected CompositeContextNode createNode() {
        return new StateNode();
    }

    protected StateNode getStateNode() {
        return (StateNode) getNodeContainer();
    }

    @Override
    protected CompositeContextNode getCompositeNode() {
        return (CompositeContextNode) getNodeContainer();
    }

    @Override
    public StateNodeFactory variable(String name, DataType type) {
        super.variable(name, type);
        return this;
    }

    @Override
    public StateNodeFactory variable(String name, DataType type, Object value) {
        super.variable(name, type, value);
        return this;
    }

    @Override
    public StateNodeFactory exceptionHandler(String exception, ExceptionHandler exceptionHandler) {
        super.exceptionHandler(exception, exceptionHandler);
        return this;
    }

    @Override
    public StateNodeFactory exceptionHandler(String exception, String dialect, String action) {
        super.exceptionHandler(exception, dialect, action);
        return this;
    }

    @Override
    public StateNodeFactory autoComplete(boolean autoComplete) {
        super.autoComplete(autoComplete);
        return this;
    }

    @Override
    public StateNodeFactory linkIncomingConnections(long nodeId) {
        super.linkIncomingConnections(nodeId);
        return this;
    }

    @Override
    public StateNodeFactory linkOutgoingConnections(long nodeId) {
        super.linkOutgoingConnections(nodeId);
        return this;
    }

    public StateNodeFactory constraint(String connectionId, long nodeId, String type, String dialect, String constraint, int priority) {
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
