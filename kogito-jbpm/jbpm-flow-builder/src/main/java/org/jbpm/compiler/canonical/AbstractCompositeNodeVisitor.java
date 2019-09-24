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

import org.jbpm.process.core.context.variable.VariableScope;
import org.kie.api.definition.process.Node;

import com.github.javaparser.ast.stmt.BlockStmt;

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
}
