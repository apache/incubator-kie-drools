/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jbpm.compiler.canonical;

import java.util.Collection;
import java.util.Map.Entry;

import org.jbpm.compiler.canonical.builtin.ConstraintEvaluatorBuilderService;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.ruleflow.core.factory.SplitFactory;
import org.jbpm.workflow.core.Constraint;
import org.jbpm.workflow.core.impl.ConnectionRef;
import org.jbpm.workflow.core.node.Split;

import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;

import static org.jbpm.ruleflow.core.factory.SplitFactory.METHOD_CONSTRAINT;
import static org.jbpm.ruleflow.core.factory.SplitFactory.METHOD_TYPE;

public class SplitNodeVisitor extends AbstractNodeVisitor<Split> {

    @Override
    protected String getNodeKey() {
        return "splitNode";
    }

    @Override
    public void visitNode(String factoryField, Split node, BlockStmt body, VariableScope variableScope, ProcessMetaData metadata) {
        body.addStatement(getAssignedFactoryMethod(factoryField, SplitFactory.class, getNodeId(node), getNodeKey(), getWorkflowElementConstructor(node.getId())))
                .addStatement(getNameMethod(node, "Split"))
                .addStatement(getFactoryMethod(getNodeId(node), METHOD_TYPE, new IntegerLiteralExpr(node.getType())));

        visitMetaData(node.getMetaData(), body, getNodeId(node));

        if (node.getType() == Split.TYPE_OR || node.getType() == Split.TYPE_XOR) {
            for (Entry<ConnectionRef, Collection<Constraint>> entry : node.getConstraints().entrySet()) {
                if (entry.getValue() != null) {
                    for (Constraint constraint : entry.getValue()) {
                        if (constraint != null) {
                            Expression returnValueEvaluator = ConstraintEvaluatorBuilderService.instance().build(node, constraint);
                            body.addStatement(getFactoryMethod(getNodeId(node), METHOD_CONSTRAINT,
                                    getWorkflowElementConstructor(entry.getKey().getNodeId()),
                                    new StringLiteralExpr(getOrDefault(entry.getKey().getConnectionId(), "")),
                                    new StringLiteralExpr(entry.getKey().getToType()),
                                    new StringLiteralExpr(constraint.getDialect()),
                                    returnValueEvaluator,
                                    new IntegerLiteralExpr(constraint.getPriority()),
                                    new BooleanLiteralExpr(constraint.isDefault())));
                        }
                    }
                }
            }
        }
        body.addStatement(getDoneMethod(getNodeId(node)));
    }

}
