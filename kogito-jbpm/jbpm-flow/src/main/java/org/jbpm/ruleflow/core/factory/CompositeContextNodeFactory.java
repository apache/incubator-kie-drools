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

import org.jbpm.process.core.context.exception.ActionExceptionHandler;
import org.jbpm.process.core.context.exception.ExceptionHandler;
import org.jbpm.process.core.context.exception.ExceptionScope;
import org.jbpm.process.core.context.variable.Variable;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.process.core.datatype.DataType;
import org.jbpm.ruleflow.core.RuleFlowNodeContainerFactory;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.NodeContainer;
import org.jbpm.workflow.core.impl.DroolsConsequenceAction;
import org.jbpm.workflow.core.node.CompositeContextNode;

public class CompositeContextNodeFactory extends RuleFlowNodeContainerFactory {

    public static final String METHOD_VARIABLE = "variable";
    public static final String METHOD_LINK_INCOMING_CONNECTIONS = "linkIncomingConnections";
    public static final String METHOD_LINK_OUTGOING_CONNECTIONS = "linkOutgoingConnections";
    public static final String METHOD_AUTO_COMPLETE = "autoComplete";

    private RuleFlowNodeContainerFactory nodeContainerFactory;
    private NodeContainer nodeContainer;
    private long linkedIncomingNodeId = -1;
    private long linkedOutgoingNodeId = -1;

    public CompositeContextNodeFactory(RuleFlowNodeContainerFactory nodeContainerFactory, NodeContainer nodeContainer, long id) {
        this.nodeContainerFactory = nodeContainerFactory;
        this.nodeContainer = nodeContainer;
        CompositeContextNode node = createNode();
        node.setId(id);
        setNodeContainer(node);
    }

    protected CompositeContextNode createNode() {
        return new CompositeContextNode();
    }

    protected CompositeContextNode getCompositeNode() {
        return (CompositeContextNode) getNodeContainer();
    }

    public CompositeContextNodeFactory name(String name) {
        getCompositeNode().setName(name);
        return this;
    }

    public CompositeContextNodeFactory variable(String name, DataType type) {
        return variable(name, type, null);
    }

    public CompositeContextNodeFactory variable(String name, DataType type, Object value) {
        return variable(name, type, value, null, null);
    }

    public CompositeContextNodeFactory variable(String name, DataType type, String metaDataName, Object metaDataValue) {
        return variable(name, type, null, metaDataName, metaDataValue);
    }

    public CompositeContextNodeFactory variable(String name, DataType type, Object value, String metaDataName, Object metaDataValue) {
        Variable variable = new Variable();
        variable.setName(name);
        variable.setType(type);
        variable.setValue(value);
        if (metaDataName != null && metaDataValue != null) {
            variable.setMetaData(metaDataName, metaDataValue);
        }
        VariableScope variableScope = (VariableScope) getCompositeNode().getDefaultContext(VariableScope.VARIABLE_SCOPE);
        if (variableScope == null) {
            variableScope = new VariableScope();
            getCompositeNode().addContext(variableScope);
            getCompositeNode().setDefaultContext(variableScope);
        }
        variableScope.getVariables().add(variable);
        return this;
    }

    public CompositeContextNodeFactory exceptionHandler(String exception, ExceptionHandler exceptionHandler) {
        ExceptionScope exceptionScope = (ExceptionScope) getCompositeNode().getDefaultContext(ExceptionScope.EXCEPTION_SCOPE);
        if (exceptionScope == null) {
            exceptionScope = new ExceptionScope();
            getCompositeNode().addContext(exceptionScope);
            getCompositeNode().setDefaultContext(exceptionScope);
        }
        exceptionScope.setExceptionHandler(exception, exceptionHandler);
        return this;
    }

    public CompositeContextNodeFactory exceptionHandler(String exception, String dialect, String action) {
        ActionExceptionHandler exceptionHandler = new ActionExceptionHandler();
        exceptionHandler.setAction(new DroolsConsequenceAction(dialect, action));
        return exceptionHandler(exception, exceptionHandler);
    }

    public CompositeContextNodeFactory autoComplete(boolean autoComplete) {
        getCompositeNode().setAutoComplete(autoComplete);
        return this;
    }

    public CompositeContextNodeFactory linkIncomingConnections(long nodeId) {
        this.linkedIncomingNodeId = nodeId;
        return this;
    }

    public CompositeContextNodeFactory linkOutgoingConnections(long nodeId) {
        this.linkedOutgoingNodeId = nodeId;
        return this;
    }

    public CompositeContextNodeFactory metaData(String name, Object value) {
        getCompositeNode().setMetaData(name, value);
        return this;
    }

    public RuleFlowNodeContainerFactory done() {
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
        nodeContainer.addNode(getCompositeNode());
        return nodeContainerFactory;
    }
}
