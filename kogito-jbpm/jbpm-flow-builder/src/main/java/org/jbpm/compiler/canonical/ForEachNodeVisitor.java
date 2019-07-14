/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.compiler.canonical;

import java.util.Map;

import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.process.core.datatype.impl.type.ObjectDataType;
import org.jbpm.ruleflow.core.factory.ForEachNodeFactory;
import org.jbpm.workflow.core.node.ForEachNode;
import org.kie.api.definition.process.Node;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

public class ForEachNodeVisitor extends AbstractVisitor {
    
    private Map<Class<?>, AbstractVisitor> nodesVisitors;

    public ForEachNodeVisitor(Map<Class<?>, AbstractVisitor> nodesVisitors) {
        this.nodesVisitors = nodesVisitors;
    }

    @Override
    public void visitNode(String factoryField, Node node, BlockStmt body, VariableScope variableScope, ProcessMetaData metadata) {
        ForEachNode forEachNode = (ForEachNode) node;
        
        addFactoryMethodWithArgsWithAssignment(factoryField, body, ForEachNodeFactory.class, "forEachNode" + node.getId(), "forEachNode", new LongLiteralExpr(forEachNode.getId()));
        visitMetaData(forEachNode.getMetaData(), body, "forEachNode" + node.getId());
        
        addFactoryMethodWithArgs(body, "forEachNode" + node.getId(), "collectionExpression", new StringLiteralExpr(stripExpression(forEachNode.getCollectionExpression())));
        addFactoryMethodWithArgs(body, "forEachNode" + node.getId(), "variable", new StringLiteralExpr(forEachNode.getVariableName()), 
                                                                                 new ObjectCreationExpr(null, new ClassOrInterfaceType(null, ObjectDataType.class.getSimpleName()), NodeList.nodeList(
                                                                                                                                                                                                      new StringLiteralExpr(forEachNode.getVariableType().getStringType())
                                                                                         )));
        
        if (forEachNode.getOutputCollectionExpression() != null) {
            addFactoryMethodWithArgs(body, "forEachNode" + node.getId(), "outputCollectionExpression", new StringLiteralExpr(stripExpression(forEachNode.getOutputCollectionExpression())));
            addFactoryMethodWithArgs(body, "forEachNode" + node.getId(), "outputVariable", new StringLiteralExpr(forEachNode.getOutputVariableName()), 
                                     new ObjectCreationExpr(null, new ClassOrInterfaceType(null, ObjectDataType.class.getSimpleName()), NodeList.nodeList(
                                                                                                                                                          new StringLiteralExpr(forEachNode.getOutputVariableType().getStringType())
                                             )));
        }
        // visit nodes
        visitNodes("forEachNode" + node.getId(), forEachNode.getNodes(), body, ((VariableScope) forEachNode.getCompositeNode().getDefaultContext(VariableScope.VARIABLE_SCOPE)), metadata);      
        addFactoryMethodWithArgs(body, "forEachNode" + node.getId(), "linkIncomingConnections", new LongLiteralExpr(forEachNode.getLinkedIncomingNode(org.jbpm.workflow.core.Node.CONNECTION_DEFAULT_TYPE).getNodeId()));
        addFactoryMethodWithArgs(body, "forEachNode" + node.getId(), "linkOutgoingConnections", new LongLiteralExpr(forEachNode.getLinkedOutgoingNode(org.jbpm.workflow.core.Node.CONNECTION_DEFAULT_TYPE).getNodeId()));
        
        addFactoryMethodWithArgs(body, "forEachNode" + node.getId(), "done");
        
    }
    
    protected void visitNodes(String factoryField, Node[] nodes, BlockStmt body, VariableScope variableScope, ProcessMetaData metadata) {

        for (Node node: nodes) {
            AbstractVisitor visitor = nodesVisitors.get(node.getClass());

            if (visitor == null) {
                continue;
            }

            visitor.visitNode(factoryField, node, body, variableScope, metadata);
        }

    }
    
    protected String stripExpression(String expression) {
        if (expression.startsWith("#{")) {
            return expression.substring(2, expression.length() -1);
        }
        
        return expression;
    }
}
