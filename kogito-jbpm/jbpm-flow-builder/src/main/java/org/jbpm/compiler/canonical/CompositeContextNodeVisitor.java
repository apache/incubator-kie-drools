/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

import java.util.HashSet;
import java.util.Map;
import java.util.stream.Stream;

import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.ruleflow.core.factory.CompositeContextNodeFactory;
import org.jbpm.workflow.core.node.CompositeContextNode;
import org.kie.api.definition.process.Node;

import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.BlockStmt;

public class CompositeContextNodeVisitor<T extends CompositeContextNode> extends AbstractCompositeNodeVisitor<T> {

    public CompositeContextNodeVisitor(Map<Class<?>, AbstractNodeVisitor<? extends Node>> nodesVisitors) {
        super(nodesVisitors);
    }

    @Override
    protected String getNodeKey() {
        return "compositeContextNode";
    }

    protected Class<?> factoryClass() {
        return CompositeContextNodeFactory.class;
    }

    protected String factoryMethod() {
        return getNodeKey();
    }

    protected String getDefaultName() {
        return "Composite";
    }

    @Override
    public void visitNode(String factoryField, T node, BlockStmt body, VariableScope variableScope, ProcessMetaData metadata) {
        body.addStatement(getAssignedFactoryMethod(factoryField, factoryClass(), getNodeId(node), factoryMethod(), new LongLiteralExpr(node.getId())))
                .addStatement(getNameMethod(node, getDefaultName()));
        visitMetaData(node.getMetaData(), body, getNodeId(node));
        VariableScope variableScopeNode = (VariableScope) node.getDefaultContext(VariableScope.VARIABLE_SCOPE);

        if (variableScope != null) {
            visitVariableScope(getNodeId(node), variableScopeNode, body, new HashSet<>(), node.getClass().getName());
        }

        visitCustomFields(node, variableScope).forEach(body::addStatement);

        // composite context node might not have variable scope
        // in that case inherit it from parent
        VariableScope scope = variableScope;
        if (node.getDefaultContext(VariableScope.VARIABLE_SCOPE) != null && !((VariableScope) node.getDefaultContext(VariableScope.VARIABLE_SCOPE)).getVariables().isEmpty()) {
            scope = (VariableScope) node.getDefaultContext(VariableScope.VARIABLE_SCOPE);
        }
        body.addStatement(getFactoryMethod(getNodeId(node), CompositeContextNodeFactory.METHOD_AUTO_COMPLETE, new BooleanLiteralExpr(node.isAutoComplete())));
        addNodeMappings(node, body, getNodeId(node));
        visitNodes(getNodeId(node), node.getNodes(), body, scope, metadata);
        visitConnections(getNodeId(node), node.getNodes(), body);
        body.addStatement(getDoneMethod(getNodeId(node)));
    }

    protected Stream<MethodCallExpr> visitCustomFields(T compositeContextNode, VariableScope variableScope) {
        return Stream.empty();
    }
}