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

import org.jbpm.process.core.context.exception.ExceptionHandler;
import org.jbpm.process.core.datatype.DataType;
import org.jbpm.ruleflow.core.RuleFlowNodeContainerFactory;
import org.jbpm.workflow.core.NodeContainer;
import org.jbpm.workflow.core.node.CompositeContextNode;
import org.jbpm.workflow.core.node.ForEachNode;

public class ForEachNodeFactory extends CompositeContextNodeFactory {

    public static final String METHOD_COLLECTION_EXPRESSION = "collectionExpression";
    public static final String METHOD_OUTPUT_COLLECTION_EXPRESSION = "outputCollectionExpression";
    public static final String METHOD_OUTPUT_VARIABLE = "outputVariable";

    public ForEachNodeFactory(RuleFlowNodeContainerFactory nodeContainerFactory, NodeContainer nodeContainer, long id) {
        super(nodeContainerFactory, nodeContainer, id);
    }

    protected ForEachNode getForEachNode() {
        return (ForEachNode) getNodeContainer();
    }

    @Override
    protected CompositeContextNode createNode() {
        return new ForEachNode();
    }

    @Override
    public ForEachNodeFactory name(String name) {
        super.name(name);
        return this;
    }

    public ForEachNodeFactory collectionExpression(String collectionExpression) {
        getForEachNode().setCollectionExpression(collectionExpression);
        return this;
    }

    @Override
    public ForEachNodeFactory exceptionHandler(String exception, ExceptionHandler exceptionHandler) {
        super.exceptionHandler(exception, exceptionHandler);
        return this;
    }

    @Override
    public ForEachNodeFactory exceptionHandler(String exception, String dialect, String action) {
        super.exceptionHandler(exception, dialect, action);
        return this;
    }

    @Override
    public ForEachNodeFactory autoComplete(boolean autoComplete) {
        super.autoComplete(autoComplete);
        return this;
    }

    @Override
    public ForEachNodeFactory linkIncomingConnections(long nodeId) {
        super.linkIncomingConnections(nodeId);
        return this;
    }

    @Override
    public ForEachNodeFactory linkOutgoingConnections(long nodeId) {
        super.linkOutgoingConnections(nodeId);
        return this;
    }

    @Override
    public ForEachNodeFactory variable(String name, DataType type) {
        getForEachNode().setVariable(name, type);
        return this;
    }

    public ForEachNodeFactory outputCollectionExpression(String collectionExpression) {
        getForEachNode().setOutputCollectionExpression(collectionExpression);
        return this;
    }

    public ForEachNodeFactory outputVariable(String variableName, DataType dataType) {
        getForEachNode().setOutputVariable(variableName, dataType);
        return this;
    }

    public ForEachNodeFactory waitForCompletion(boolean waitForCompletion) {
        getForEachNode().setWaitForCompletion(waitForCompletion);
        return this;
    }
}
