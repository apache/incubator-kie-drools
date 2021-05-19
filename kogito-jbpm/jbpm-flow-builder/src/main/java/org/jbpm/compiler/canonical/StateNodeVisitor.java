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
package org.jbpm.compiler.canonical;

import java.util.Map;
import java.util.stream.Stream;

import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.ruleflow.core.factory.StateNodeFactory;
import org.jbpm.workflow.core.node.StateNode;
import org.kie.api.definition.process.Node;

import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.utils.StringEscapeUtils;

import static org.jbpm.ruleflow.core.factory.StateNodeFactory.METHOD_CONSTRAINT;

public class StateNodeVisitor extends CompositeContextNodeVisitor<StateNode> {

    public StateNodeVisitor(Map<Class<?>, AbstractNodeVisitor<? extends Node>> nodesVisitors) {
        super(nodesVisitors);
    }

    @Override
    protected Class<?> factoryClass() {
        return StateNodeFactory.class;
    }

    @Override
    protected String getNodeKey() {
        return "stateNode";
    }

    @Override
    protected String getDefaultName() {
        return "State";
    }

    @Override
    public Stream<MethodCallExpr> visitCustomFields(StateNode node, VariableScope variableScope) {
        if (node.getConstraints() == null) {
            return Stream.empty();
        }
        return node.getConstraints()
                .entrySet()
                .stream()
                .map((e -> getFactoryMethod(getNodeId(node), METHOD_CONSTRAINT,
                        getOrNullExpr(e.getKey().getConnectionId()),
                        new LongLiteralExpr(e.getKey().getNodeId()),
                        new StringLiteralExpr(e.getKey().getToType()),
                        new StringLiteralExpr(e.getValue().getDialect()),
                        new StringLiteralExpr(StringEscapeUtils.escapeJava(e.getValue().getConstraint())),
                        new IntegerLiteralExpr(e.getValue().getPriority()))));
    }
}
