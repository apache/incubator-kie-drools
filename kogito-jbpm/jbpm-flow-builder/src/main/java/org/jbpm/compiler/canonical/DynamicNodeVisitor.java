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
package org.jbpm.compiler.canonical;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Stream;

import org.jbpm.compiler.canonical.builtin.ReturnValueEvaluatorBuilderService;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.ruleflow.core.factory.DynamicNodeFactory;
import org.jbpm.workflow.core.node.DynamicNode;

import com.github.javaparser.ast.expr.MethodCallExpr;

import static org.jbpm.ruleflow.core.factory.DynamicNodeFactory.METHOD_ACTIVATION_EXPRESSION;
import static org.jbpm.ruleflow.core.factory.DynamicNodeFactory.METHOD_COMPLETION_EXPRESSION;
import static org.jbpm.ruleflow.core.factory.DynamicNodeFactory.METHOD_LANGUAGE;

public class DynamicNodeVisitor extends CompositeContextNodeVisitor<DynamicNode> {

    private ReturnValueEvaluatorBuilderService builder;

    public DynamicNodeVisitor(ClassLoader classLoader) {
        super(classLoader);
        this.builder = ReturnValueEvaluatorBuilderService.instance(classLoader);
    }

    @Override
    protected Class<?> factoryClass() {
        return DynamicNodeFactory.class;
    }

    @Override
    protected String factoryMethod() {
        return "dynamicNode";
    }

    @Override
    protected String getDefaultName() {
        return "Dynamic";
    }

    @Override
    public Stream<MethodCallExpr> visitCustomFields(DynamicNode node, VariableScope variableScope) {

        Collection<MethodCallExpr> methods = new ArrayList<>();
        methods.add(getFactoryMethod(getNodeId(node), METHOD_LANGUAGE, getOrNullExpr(node.getLanguage())));
        if (node.getActivationCondition() != null && !node.getActivationCondition().isBlank()) {
            methods.add(getActivationConditionStatement(node));
        }
        if (!node.isAutoComplete() && node.getCompletionCondition() != null && !node.getCompletionCondition().isBlank()) {
            methods.add(getCompletionConditionStatement(node));
        }
        return methods.stream();
    }

    private MethodCallExpr getActivationConditionStatement(DynamicNode node) {
        return getFactoryMethod(getNodeId(node), METHOD_ACTIVATION_EXPRESSION, builder.build(node, node.getLanguage(), node.getActivationCondition(), Boolean.class, (String) null));
    }

    private MethodCallExpr getCompletionConditionStatement(DynamicNode node) {
        return getFactoryMethod(getNodeId(node), METHOD_COMPLETION_EXPRESSION, builder.build(node, node.getLanguage(), node.getCompletionCondition(), Boolean.class, (String) null));
    }
}
