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

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.jbpm.process.core.context.variable.Variable;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.process.core.datatype.impl.type.ObjectDataType;
import org.jbpm.ruleflow.core.factory.CompositeContextNodeFactory;
import org.jbpm.workflow.core.node.CompositeContextNode;
import org.kie.api.definition.process.Node;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.jbpm.ruleflow.core.factory.CompositeContextNodeFactory.METHOD_VARIABLE;

public class CompositeContextNodeVisitor extends AbstractCompositeNodeVisitor {

    private static final String NODE_KEY = "compositeContextNode";
    private static final String FACTORY_METHOD_NAME = "compositeNode";
    private static final String DEFAULT_NAME = "Composite";

    @Override
    protected String getNodeKey() {
        return NODE_KEY;
    }

    public CompositeContextNodeVisitor(Map<Class<?>, AbstractNodeVisitor> nodesVisitors) {
        super(nodesVisitors);
    }

    protected Class<? extends CompositeContextNodeFactory> factoryClass() {
        return CompositeContextNodeFactory.class;
    }

    protected String factoryMethod() {
        return FACTORY_METHOD_NAME;
    }

    @Override
    public void visitNode(String factoryField, Node node, BlockStmt body, VariableScope variableScope, ProcessMetaData metadata) {
        CompositeContextNode compositeContextNode = (CompositeContextNode) node;

        body.addStatement(getAssignedFactoryMethod(factoryField, factoryClass(), getNodeId(node), factoryMethod(), new LongLiteralExpr(compositeContextNode.getId())))
                .addStatement(getNameMethod(node, getDefaultName()));
        visitMetaData(compositeContextNode.getMetaData(), body, getNodeId(node));
        VariableScope variableScopeNode = (VariableScope) compositeContextNode.getDefaultContext(VariableScope.VARIABLE_SCOPE);

        if (variableScope != null) {
            visitVariableScope(getNodeId(node), variableScopeNode, body, new HashSet<>());
        }

        // visit nodes
        visitNodes(getNodeId(node), compositeContextNode.getNodes(), body, ((VariableScope) compositeContextNode.getDefaultContext(VariableScope.VARIABLE_SCOPE)), metadata);
        visitConnections(getNodeId(node), compositeContextNode.getNodes(), body);
        body.addStatement(getDoneMethod(getNodeId(node)));
    }

    protected String getDefaultName() {
        return DEFAULT_NAME;
    }

    protected void visitVariableScope(String contextNode, VariableScope variableScope, BlockStmt body, Set<String> visitedVariables) {
        if (variableScope != null && !variableScope.getVariables().isEmpty()) {
            for (Variable variable : variableScope.getVariables()) {
                if (!visitedVariables.add(variable.getName())) {
                    continue;
                }
                String tags = (String) variable.getMetaData(Variable.VARIABLE_TAGS);
                ClassOrInterfaceType variableType = new ClassOrInterfaceType(null, ObjectDataType.class.getSimpleName());
                ObjectCreationExpr variableValue = new ObjectCreationExpr(null, variableType, new NodeList<>(new StringLiteralExpr(variable.getType().getStringType())));
                body.addStatement(getFactoryMethod(contextNode, METHOD_VARIABLE,
                        new StringLiteralExpr(variable.getName()), variableValue,
                        new StringLiteralExpr(Variable.VARIABLE_TAGS), (tags != null ? new StringLiteralExpr(tags) : new NullLiteralExpr())));
            }
        }
    }
}
