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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Stream;

import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.ruleflow.core.factory.EventSubProcessNodeFactory;
import org.jbpm.workflow.core.node.EventSubProcessNode;
import org.kie.api.definition.process.Node;

import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;

import static org.jbpm.ruleflow.core.factory.EventSubProcessNodeFactory.METHOD_EVENT;
import static org.jbpm.ruleflow.core.factory.EventSubProcessNodeFactory.METHOD_KEEP_ACTIVE;

public class EventSubProcessNodeVisitor extends CompositeContextNodeVisitor<EventSubProcessNode> {

    public EventSubProcessNodeVisitor(Map<Class<?>, AbstractNodeVisitor<? extends Node>> nodesVisitors) {
        super(nodesVisitors);
    }

    @Override
    protected Class<?> factoryClass() {
        return EventSubProcessNodeFactory.class;
    }

    @Override
    protected String getNodeKey() {
        return "eventSubProcessNode";
    }

    @Override
    protected String getDefaultName() {
        return "EventSubProcess";
    }

    @Override
    public Stream<MethodCallExpr> visitCustomFields(EventSubProcessNode node, VariableScope variableScope) {
        Collection<MethodCallExpr> methods = new ArrayList<>();
        methods.add(getFactoryMethod(getNodeId(node), METHOD_KEEP_ACTIVE, new BooleanLiteralExpr(node.isKeepActive())));
        node.getEvents()
                .forEach(e -> methods.add(getFactoryMethod(getNodeId(node), METHOD_EVENT, new StringLiteralExpr(e))));
        return methods.stream();
    }
}
