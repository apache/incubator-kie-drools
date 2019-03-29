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

import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.ruleflow.core.factory.EndNodeFactory;
import org.kie.api.definition.process.Node;
import org.jbpm.workflow.core.node.EndNode;

import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;

public class EndNodeVisitor extends AbstractVisitor {

    @Override
    public void visitNode(Node node, BlockStmt body, VariableScope variableScope) {
        EndNode endNode = (EndNode) node;
        
        addFactoryMethodWithArgsWithAssignment(body, EndNodeFactory.class, "endNode" + node.getId(), "endNode", new LongLiteralExpr(endNode.getId()));
        addFactoryMethodWithArgs(body, "endNode" + node.getId(), "name", new StringLiteralExpr(getOrDefault(endNode.getName(), "End")));
        addFactoryMethodWithArgs(body, "endNode" + node.getId(), "terminate", new BooleanLiteralExpr(endNode.isTerminate()));
        addFactoryMethodWithArgs(body, "endNode" + node.getId(), "done");
        
    }
}
