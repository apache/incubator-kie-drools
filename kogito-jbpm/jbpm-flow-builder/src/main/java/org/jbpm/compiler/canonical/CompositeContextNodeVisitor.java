/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.compiler.canonical;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.ruleflow.core.factory.CompositeNodeFactory;
import org.jbpm.workflow.core.impl.ConnectionImpl;
import org.jbpm.workflow.core.node.CompositeContextNode;
import org.kie.api.definition.process.Connection;
import org.kie.api.definition.process.Node;

import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;

public class CompositeContextNodeVisitor extends AbstractCompositeNodeVisitor {
    
    protected static final String C_C_NODE_VAR = "compositeContextNode";
    private static final String FACTORY_METHOD_NAME = "compositeNode";
    
    public CompositeContextNodeVisitor(Map<Class<?>, AbstractVisitor> nodesVisitors) {
        super(nodesVisitors);
    }
    
    protected Class<? extends CompositeNodeFactory> factoryClass() {
        return CompositeNodeFactory.class;
    }
    
    protected String factoryMethod() {
        return FACTORY_METHOD_NAME;
    }

    @Override
    public void visitNode(String factoryField, Node node, BlockStmt body, VariableScope variableScope, ProcessMetaData metadata) {
        CompositeContextNode compositeContextNode = (CompositeContextNode) node;        
        
        addFactoryMethodWithArgsWithAssignment(factoryField, body, factoryClass(), C_C_NODE_VAR + node.getId(), factoryMethod(), new LongLiteralExpr(compositeContextNode.getId()));
        visitMetaData(compositeContextNode.getMetaData(), body, C_C_NODE_VAR + node.getId());
        VariableScope variableScopeNode = (VariableScope) compositeContextNode.getDefaultContext(VariableScope.VARIABLE_SCOPE);
        
        if (variableScope != null) {
            visitVariableScope(C_C_NODE_VAR + node.getId(), variableScopeNode, body, new HashSet<>());
        }
        
        // visit nodes
        visitNodes(C_C_NODE_VAR + node.getId(), compositeContextNode.getNodes(), body, ((VariableScope) compositeContextNode.getDefaultContext(VariableScope.VARIABLE_SCOPE)), metadata);      
        visitConnections(C_C_NODE_VAR + node.getId(), compositeContextNode.getNodes(), body);

        addFactoryMethodWithArgs(body, C_C_NODE_VAR + node.getId(), "done");
        
    }
    
    protected void visitConnections(String factoryField, Node[] nodes, BlockStmt body) {

        List<Connection> connections = new ArrayList<>();
        for (Node node : nodes) {
            for (List<Connection> connectionList : node.getIncomingConnections().values()) {
                connections.addAll(connectionList);
            }
        }
        for (Connection connection : connections) {
            visitConnection(factoryField, connection, body);
        }
    }
    
    protected void visitConnection(String factoryField, Connection connection, BlockStmt body) {
        // if the connection is a hidden one (compensations), don't dump
        Object hidden = ((ConnectionImpl) connection).getMetaData("hidden");
        if (hidden != null && ((Boolean) hidden)) {
            return;
        }

        addFactoryMethodWithArgs(factoryField, body, "connection", new LongLiteralExpr(connection.getFrom().getId()),
                                 new LongLiteralExpr(connection.getTo().getId()),
                                 new StringLiteralExpr(getOrDefault((String) ((ConnectionImpl) connection).getMetaData().get("UniqueId"), "")));
    }

    
}
