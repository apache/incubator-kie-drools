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
package org.jbpm.ruleflow.core.factory;

import org.jbpm.process.core.context.variable.Variable;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.process.core.datatype.DataType;
import org.jbpm.ruleflow.core.RuleFlowNodeContainerFactory;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.NodeContainer;
import org.jbpm.workflow.core.node.CompositeContextNode;

@SuppressWarnings("unchecked")
public abstract class AbstractCompositeNodeFactory<T extends RuleFlowNodeContainerFactory<T, P>, P extends RuleFlowNodeContainerFactory<P, ?>> extends RuleFlowNodeContainerFactory<T, P> {

    private long linkedIncomingNodeId = -1;
    private long linkedOutgoingNodeId = -1;

    protected AbstractCompositeNodeFactory(P nodeContainerFactory, NodeContainer nodeContainer, NodeContainer node, Object id) {
        super(nodeContainerFactory, nodeContainer, node, id);
    }

    protected CompositeContextNode getCompositeNode() {
        return (CompositeContextNode) node;
    }

    public T variable(String name, DataType type) {
        return variable(name, type, null);
    }

    public T variable(String name, DataType type, Object value) {
        return variable(name, type, value, null, null);
    }

    public T variable(String name, DataType type, String metaDataName, Object metaDataValue) {
        return variable(name, type, null, metaDataName, metaDataValue);
    }

    public T variable(String name, DataType type, Object value, String metaDataName, Object metaDataValue) {
        Variable variable = new Variable();
        variable.setName(name);
        variable.setType(type);
        variable.setValue(value);
        VariableScope variableScope = (VariableScope) getCompositeNode().getDefaultContext(VariableScope.VARIABLE_SCOPE);
        if (metaDataName != null && metaDataValue != null) {
            variable.setMetaData(metaDataName, metaDataValue);
        }
        if (variableScope == null) {
            variableScope = new VariableScope();
            getCompositeNode().addContext(variableScope);
            getCompositeNode().setDefaultContext(variableScope);
        }
        variableScope.getVariables().add(variable);
        return (T) this;
    }

    public T linkIncomingConnections(long nodeId) {
        this.linkedIncomingNodeId = nodeId;
        return (T) this;
    }

    public T autoComplete(boolean autoComplete) {
        getCompositeNode().setAutoComplete(autoComplete);
        return (T) this;
    }

    public T linkOutgoingConnections(long nodeId) {
        this.linkedOutgoingNodeId = nodeId;
        return (T) this;
    }

    @Override
    public P done() {
        if (linkedIncomingNodeId != -1) {
            getCompositeNode().linkIncomingConnections(
                    Node.CONNECTION_DEFAULT_TYPE,
                    linkedIncomingNodeId, Node.CONNECTION_DEFAULT_TYPE);
        }
        if (linkedOutgoingNodeId != -1) {
            getCompositeNode().linkOutgoingConnections(
                    linkedOutgoingNodeId, Node.CONNECTION_DEFAULT_TYPE,
                    Node.CONNECTION_DEFAULT_TYPE);
        }
        return super.done();
    }
}
