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

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.process.core.datatype.impl.type.ObjectDataType;
import org.jbpm.ruleflow.core.factory.ForEachNodeFactory;
import org.jbpm.workflow.core.node.ForEachNode;
import org.kie.api.definition.process.Node;

import java.util.Map;

import static org.jbpm.ruleflow.core.factory.CompositeContextNodeFactory.METHOD_LINK_INCOMING_CONNECTIONS;
import static org.jbpm.ruleflow.core.factory.CompositeContextNodeFactory.METHOD_LINK_OUTGOING_CONNECTIONS;
import static org.jbpm.ruleflow.core.factory.CompositeContextNodeFactory.METHOD_VARIABLE;
import static org.jbpm.ruleflow.core.factory.ForEachNodeFactory.METHOD_COLLECTION_EXPRESSION;
import static org.jbpm.ruleflow.core.factory.ForEachNodeFactory.METHOD_OUTPUT_COLLECTION_EXPRESSION;
import static org.jbpm.ruleflow.core.factory.ForEachNodeFactory.METHOD_OUTPUT_VARIABLE;

public class ForEachNodeVisitor extends AbstractCompositeNodeVisitor {

    private static final String NODE_KEY = "forEachNode";

    public ForEachNodeVisitor(Map<Class<?>, AbstractNodeVisitor> nodesVisitors) {
        super(nodesVisitors);
    }

    @Override
    protected String getNodeKey() {
        return NODE_KEY;
    }

    @Override
    public void visitNode(String factoryField, Node node, BlockStmt body, VariableScope variableScope, ProcessMetaData metadata) {
        ForEachNode forEachNode = (ForEachNode) node;

        body.addStatement(getAssignedFactoryMethod(factoryField, ForEachNodeFactory.class, getNodeId(node), NODE_KEY, new LongLiteralExpr(forEachNode.getId())))
                .addStatement(getNameMethod(node, "ForEach"));
        visitMetaData(forEachNode.getMetaData(), body, getNodeId(node));

        body.addStatement(getFactoryMethod(getNodeId(node), METHOD_COLLECTION_EXPRESSION, new StringLiteralExpr(stripExpression(forEachNode.getCollectionExpression()))))
                .addStatement(getFactoryMethod(getNodeId(node), METHOD_VARIABLE, new StringLiteralExpr(forEachNode.getVariableName()),
                        new ObjectCreationExpr(null, new ClassOrInterfaceType(null, ObjectDataType.class.getSimpleName()), NodeList.nodeList(
                                new StringLiteralExpr(forEachNode.getVariableType().getStringType())
                        ))));

        if (forEachNode.getOutputCollectionExpression() != null) {
            body.addStatement(getFactoryMethod(getNodeId(node), METHOD_OUTPUT_COLLECTION_EXPRESSION, new StringLiteralExpr(stripExpression(forEachNode.getOutputCollectionExpression()))))
                    .addStatement(getFactoryMethod(getNodeId(node), METHOD_OUTPUT_VARIABLE, new StringLiteralExpr(forEachNode.getOutputVariableName()),
                            new ObjectCreationExpr(null, new ClassOrInterfaceType(null, ObjectDataType.class.getSimpleName()), NodeList.nodeList(
                                    new StringLiteralExpr(forEachNode.getOutputVariableType().getStringType())
                            ))));
        }
        // visit nodes
        visitNodes(getNodeId(node), forEachNode.getNodes(), body, ((VariableScope) forEachNode.getCompositeNode().getDefaultContext(VariableScope.VARIABLE_SCOPE)), metadata);
        body.addStatement(getFactoryMethod(getNodeId(node), METHOD_LINK_INCOMING_CONNECTIONS, new LongLiteralExpr(forEachNode.getLinkedIncomingNode(org.jbpm.workflow.core.Node.CONNECTION_DEFAULT_TYPE).getNodeId())))
                .addStatement(getFactoryMethod(getNodeId(node), METHOD_LINK_OUTGOING_CONNECTIONS, new LongLiteralExpr(forEachNode.getLinkedOutgoingNode(org.jbpm.workflow.core.Node.CONNECTION_DEFAULT_TYPE).getNodeId())))
                .addStatement(getDoneMethod(getNodeId(node)));

    }

}
