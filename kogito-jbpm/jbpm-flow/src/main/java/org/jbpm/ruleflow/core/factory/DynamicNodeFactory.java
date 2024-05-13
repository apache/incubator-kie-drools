/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jbpm.ruleflow.core.factory;

import java.util.function.Predicate;

import org.jbpm.ruleflow.core.RuleFlowNodeContainerFactory;
import org.jbpm.workflow.core.NodeContainer;
import org.jbpm.workflow.core.node.DynamicNode;
import org.kie.api.definition.process.WorkflowElementIdentifier;
import org.kie.api.runtime.process.ProcessContext;

public class DynamicNodeFactory<T extends RuleFlowNodeContainerFactory<T, ?>> extends AbstractCompositeNodeFactory<DynamicNodeFactory<T>, T> {

    public static final String METHOD_LANGUAGE = "language";
    public static final String METHOD_ACTIVATION_EXPRESSION = "activationExpression";
    public static final String METHOD_COMPLETION_EXPRESSION = "completionExpression";

    public DynamicNodeFactory(T nodeContainerFactory, NodeContainer nodeContainer, WorkflowElementIdentifier id) {
        super(nodeContainerFactory, nodeContainer, new DynamicNode(), id);
    }

    protected DynamicNode getDynamicNode() {
        return (DynamicNode) node;
    }

    public DynamicNodeFactory<T> language(String language) {
        getDynamicNode().setLanguage(language);
        return this;
    }

    public DynamicNodeFactory<T> activationExpression(Predicate<ProcessContext> activationExpression) {
        getDynamicNode().setActivationExpression(activationExpression);
        return this;
    }

    public DynamicNodeFactory<T> completionExpression(Predicate<ProcessContext> completionExpression) {
        getDynamicNode().setCompletionExpression(completionExpression);
        return this;
    }

}
