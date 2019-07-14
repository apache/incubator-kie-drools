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

import org.jbpm.process.core.context.variable.Variable;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.ruleflow.core.factory.ActionNodeFactory;
import org.kie.api.definition.process.Node;
import org.jbpm.workflow.core.node.ActionNode;

import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.UnknownType;

public class ActionNodeVisitor extends AbstractVisitor {

    @Override
    public void visitNode(String factoryField, Node node, BlockStmt body, VariableScope variableScope, ProcessMetaData metadata) {
        ActionNode actionNode = (ActionNode) node;
        
        addFactoryMethodWithArgsWithAssignment(factoryField, body, ActionNodeFactory.class, "actionNode" + node.getId(), "actionNode", new LongLiteralExpr(actionNode.getId()));
        addFactoryMethodWithArgs(body, "actionNode" + node.getId(), "name", new StringLiteralExpr(getOrDefault(actionNode.getName(), "Script")));
        
        
        BlockStmt actionBody = new BlockStmt();
        LambdaExpr lambda = new LambdaExpr(
                new Parameter(new UnknownType(), "kcontext"), // (kcontext) ->
                actionBody
        );

        for (Variable v : variableScope.getVariables()) {
            actionBody.addStatement(makeAssignment(v));
        }
        actionBody.addStatement(new NameExpr(actionNode.getAction().toString()));
        
        addFactoryMethodWithArgs(body, "actionNode" + node.getId(), "action", lambda);
        addFactoryMethodWithArgs(body, "actionNode" + node.getId(), "done");
    }
}
