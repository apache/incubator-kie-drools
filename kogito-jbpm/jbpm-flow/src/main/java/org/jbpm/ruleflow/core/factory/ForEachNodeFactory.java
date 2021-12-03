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

import org.jbpm.process.core.datatype.DataType;
import org.jbpm.process.instance.impl.Action;
import org.jbpm.ruleflow.core.RuleFlowNodeContainerFactory;
import org.jbpm.workflow.core.NodeContainer;
import org.jbpm.workflow.core.node.ForEachNode;

public class ForEachNodeFactory<T extends RuleFlowNodeContainerFactory<T, ?>> extends AbstractCompositeNodeFactory<ForEachNodeFactory<T>, T> {

    public static final String METHOD_COLLECTION_EXPRESSION = "collectionExpression";
    public static final String METHOD_OUTPUT_COLLECTION_EXPRESSION = "outputCollectionExpression";
    public static final String METHOD_OUTPUT_VARIABLE = "outputVariable";
    public static final String METHOD_SEQUENTIAL = "sequential";

    public ForEachNodeFactory(T nodeContainerFactory, NodeContainer nodeContainer, long id) {
        super(nodeContainerFactory, nodeContainer, new ForEachNode(), id);
    }

    protected ForEachNode getForEachNode() {
        return (ForEachNode) node;
    }

    public ForEachNodeFactory<T> collectionExpression(String collectionExpression) {
        getForEachNode().setCollectionExpression(collectionExpression);
        return this;
    }

    @Override
    public ForEachNodeFactory<T> variable(String variableName, DataType dataType) {
        getForEachNode().setVariable(variableName, dataType);
        return this;
    }

    public ForEachNodeFactory<T> outputCollectionExpression(String collectionExpression) {
        getForEachNode().setOutputCollectionExpression(collectionExpression);
        return this;
    }

    public ForEachNodeFactory<T> expressionLanguage(String exprLanguage) {
        getForEachNode().setExpressionLanguage(exprLanguage);
        return this;
    }

    public ForEachNodeFactory<T> completionAction(Action completionAction) {
        getForEachNode().setCompletionAction(completionAction);
        return this;
    }

    public ForEachNodeFactory<T> outputVariable(String variableName, DataType dataType) {
        getForEachNode().setOutputVariable(variableName, dataType);
        return this;
    }

    public ForEachNodeFactory<T> waitForCompletion(boolean waitForCompletion) {
        getForEachNode().setWaitForCompletion(waitForCompletion);
        return this;
    }

    public ForEachNodeFactory<T> sequential(boolean sequential) {
        getForEachNode().setSequential(sequential);
        return this;
    }
}
