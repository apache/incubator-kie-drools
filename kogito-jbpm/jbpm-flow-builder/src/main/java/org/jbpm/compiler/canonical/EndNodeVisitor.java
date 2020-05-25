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
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.UnknownType;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.ruleflow.core.factory.EndNodeFactory;
import org.jbpm.workflow.core.node.EndNode;

import static org.jbpm.ruleflow.core.Metadata.EVENT_TYPE;
import static org.jbpm.ruleflow.core.Metadata.EVENT_TYPE_SIGNAL;
import static org.jbpm.ruleflow.core.Metadata.REF;
import static org.jbpm.ruleflow.core.Metadata.TRIGGER_REF;
import static org.jbpm.ruleflow.core.factory.EndNodeFactory.METHOD_ACTION;
import static org.jbpm.ruleflow.core.factory.EndNodeFactory.METHOD_TERMINATE;

public class EndNodeVisitor extends AbstractNodeVisitor<EndNode> {

    @Override
    protected String getNodeKey() {
        return "endNode";
    }

    @Override
    public void visitNode(String factoryField, EndNode node, BlockStmt body, VariableScope variableScope, ProcessMetaData metadata) {
        body.addStatement(getAssignedFactoryMethod(factoryField, EndNodeFactory.class, getNodeId(node), getNodeKey(), new LongLiteralExpr(node.getId())))
                .addStatement(getNameMethod(node, "End"))
                .addStatement(getFactoryMethod(getNodeId(node), METHOD_TERMINATE, new BooleanLiteralExpr(node.isTerminate())));

        // if there is trigger defined on end event create TriggerMetaData for it
        if (node.getMetaData(TRIGGER_REF) != null) {
            LambdaExpr lambda = TriggerMetaData.buildLambdaExpr(node, metadata);
            body.addStatement(getFactoryMethod(getNodeId(node), METHOD_ACTION, lambda));
        } else if (node.getMetaData(REF) != null && EVENT_TYPE_SIGNAL.equals(node.getMetaData(EVENT_TYPE))) {
            MethodCallExpr getProcessInstance = getFactoryMethod(KCONTEXT_VAR, "getProcessInstance");
            MethodCallExpr signalEventMethod = new MethodCallExpr(getProcessInstance, "signalEvent")
                    .addArgument(new StringLiteralExpr((String) node.getMetaData(REF)))
                    .addArgument(new NullLiteralExpr());
            BlockStmt actionBody = new BlockStmt();
            actionBody.addStatement(signalEventMethod);
            LambdaExpr lambda = new LambdaExpr(new Parameter(new UnknownType(), KCONTEXT_VAR), actionBody);
            body.addStatement(getFactoryMethod(getNodeId(node), METHOD_ACTION, lambda));
        }

        visitMetaData(node.getMetaData(), body, getNodeId(node));
        body.addStatement(getDoneMethod(getNodeId(node)));
    }
}
