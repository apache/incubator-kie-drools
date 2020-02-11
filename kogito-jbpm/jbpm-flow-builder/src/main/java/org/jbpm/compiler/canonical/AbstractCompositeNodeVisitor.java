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

import java.util.Map;
import java.util.Set;

import org.jbpm.process.core.context.variable.Variable;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.process.core.datatype.impl.type.ObjectDataType;
import org.kie.api.definition.process.Node;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

public class AbstractCompositeNodeVisitor extends AbstractVisitor {

    protected Map<Class<?>, AbstractVisitor> nodesVisitors;

    public AbstractCompositeNodeVisitor(Map<Class<?>, AbstractVisitor> nodesVisitors) {
        this.nodesVisitors = nodesVisitors;
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
    
    
    protected void visitVariableScope(String contextNode, VariableScope variableScope, BlockStmt body, Set<String> visitedVariables) {
        if (variableScope != null && !variableScope.getVariables().isEmpty()) {
            for (Variable variable : variableScope.getVariables()) {

                if (!visitedVariables.add(variable.getName())) {
                    continue;
                }
                String tags = (String) variable.getMetaData(Variable.VARIABLE_TAGS);
                ClassOrInterfaceType variableType = new ClassOrInterfaceType(null, ObjectDataType.class.getSimpleName());
                ObjectCreationExpr variableValue = new ObjectCreationExpr(null, variableType, new NodeList<>(new StringLiteralExpr(variable.getType().getStringType())));
                addFactoryMethodWithArgs(contextNode, body, "variable", new StringLiteralExpr(variable.getName()), variableValue, new StringLiteralExpr(Variable.VARIABLE_TAGS), (tags != null ? new StringLiteralExpr(tags) : new NullLiteralExpr()));
            }
        }
    }
}
