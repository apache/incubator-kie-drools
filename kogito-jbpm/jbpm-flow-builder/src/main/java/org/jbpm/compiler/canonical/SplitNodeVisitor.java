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

import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.UnknownType;
import org.jbpm.process.core.context.variable.Variable;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.ruleflow.core.factory.SplitFactory;
import org.jbpm.workflow.core.Constraint;
import org.jbpm.workflow.core.impl.ConnectionRef;
import org.jbpm.workflow.core.node.Split;

import java.util.Map.Entry;

import static org.jbpm.ruleflow.core.factory.SplitFactory.METHOD_CONSTRAINT;
import static org.jbpm.ruleflow.core.factory.SplitFactory.METHOD_TYPE;

public class SplitNodeVisitor extends AbstractNodeVisitor<Split> {

    @Override
    protected String getNodeKey() {
        return "splitNode";
    }

    @Override
    public void visitNode(String factoryField, Split node, BlockStmt body, VariableScope variableScope, ProcessMetaData metadata) {
        body.addStatement(getAssignedFactoryMethod(factoryField, SplitFactory.class, getNodeId(node), getNodeKey(), new LongLiteralExpr(node.getId())))
                .addStatement(getNameMethod(node, "Split"))
                .addStatement(getFactoryMethod(getNodeId(node), METHOD_TYPE, new IntegerLiteralExpr(node.getType())));

        visitMetaData(node.getMetaData(), body, getNodeId(node));

        if (node.getType() == Split.TYPE_OR || node.getType() == Split.TYPE_XOR) {
            for (Entry<ConnectionRef, Constraint> entry : node.getConstraints().entrySet()) {
                if (entry.getValue() != null) {
                    BlockStmt actionBody = new BlockStmt();
                    LambdaExpr lambda = new LambdaExpr(
                            new Parameter(new UnknownType(), KCONTEXT_VAR), // (kcontext) ->
                            actionBody
                    );

                    for (Variable v : variableScope.getVariables()) {
                        actionBody.addStatement(makeAssignment(v));
                    }
                    BlockStmt constraintBody = new BlockStmt();
                    constraintBody.addStatement(entry.getValue().getConstraint());

                    actionBody.addStatement(constraintBody);

                    body.addStatement(getFactoryMethod(getNodeId(node), METHOD_CONSTRAINT,
                            new LongLiteralExpr(entry.getKey().getNodeId()),
                            new StringLiteralExpr(getOrDefault(entry.getKey().getConnectionId(), "")),
                            new StringLiteralExpr(entry.getKey().getToType()),
                            new StringLiteralExpr(entry.getValue().getDialect()),
                            lambda,
                            new IntegerLiteralExpr(entry.getValue().getPriority())));
                }
            }
        }
        body.addStatement(getDoneMethod(getNodeId(node)));
    }
}
