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

import org.jbpm.process.core.context.exception.ExceptionHandler;
import org.jbpm.process.core.datatype.DataType;
import org.jbpm.ruleflow.core.RuleFlowNodeContainerFactory;
import org.jbpm.workflow.core.NodeContainer;
import org.jbpm.workflow.core.node.CompositeContextNode;
import org.jbpm.workflow.core.node.DynamicNode;

public class DynamicNodeFactory extends CompositeContextNodeFactory {

	public static final String METHOD_LANGUAGE = "language";
	public static final String METHOD_ACTIVATION_EXPRESSION = "activationExpression";
	public static final String METHOD_COMPLETION_EXPRESSION = "completionExpression";

    public DynamicNodeFactory(RuleFlowNodeContainerFactory nodeContainerFactory, NodeContainer nodeContainer, long id) {
        super(nodeContainerFactory, nodeContainer, id);
    }

    @Override
    protected CompositeContextNode createNode() {
        return new DynamicNode();
    }

    protected DynamicNode getDynamicNode() {
        return (DynamicNode) getNodeContainer();
    }

    @Override
    protected CompositeContextNode getCompositeNode() {
        return (CompositeContextNode) getNodeContainer();
    }

    @Override
    public DynamicNodeFactory name(String name) {
        super.name(name);
        return this;
    }

    @Override
    public DynamicNodeFactory variable(String name, DataType type) {
        super.variable(name, type);
        return this;
    }

    @Override
    public DynamicNodeFactory variable(String name, DataType type, Object value) {
        super.variable(name, type, value);
        return this;
    }

    @Override
    public DynamicNodeFactory exceptionHandler(String exception, ExceptionHandler exceptionHandler) {
        super.exceptionHandler(exception, exceptionHandler);
        return this;
    }

    @Override
    public DynamicNodeFactory exceptionHandler(String exception, String dialect, String action) {
        super.exceptionHandler(exception, dialect, action);
        return this;
    }

    @Override
    public DynamicNodeFactory autoComplete(boolean autoComplete) {
        super.autoComplete(autoComplete);
        return this;
    }

    @Override
    public DynamicNodeFactory linkIncomingConnections(long nodeId) {
        super.linkIncomingConnections(nodeId);
        return this;
    }

    @Override
    public DynamicNodeFactory linkOutgoingConnections(long nodeId) {
        super.linkOutgoingConnections(nodeId);
        return this;
    }

    @Override
    public DynamicNodeFactory metaData(String name, Object value) {
        super.metaData(name, value);
        return this;
    }

    public DynamicNodeFactory language(String language) {
        getDynamicNode().setLanguage(language);
        return this;
    }

    public DynamicNodeFactory activationExpression(String activationExpression) {
        getDynamicNode().setActivationExpression(activationExpression);
        return this;
    }

    public DynamicNodeFactory completionExpression(String completionExpression) {
        getDynamicNode().setCompletionExpression(completionExpression);
        return this;
    }
}
