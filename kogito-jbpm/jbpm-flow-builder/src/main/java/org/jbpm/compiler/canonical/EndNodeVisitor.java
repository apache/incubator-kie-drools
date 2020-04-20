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
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.UnknownType;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.ruleflow.core.factory.EndNodeFactory;
import org.jbpm.workflow.core.node.EndNode;
import org.kie.api.definition.process.Node;

import java.util.Map;

import static org.jbpm.ruleflow.core.factory.EndNodeFactory.METHOD_ACTION;
import static org.jbpm.ruleflow.core.factory.EndNodeFactory.METHOD_TERMINATE;

public class EndNodeVisitor extends AbstractNodeVisitor {

    private static final String NODE_KEY = "endNode";

    @Override
    protected String getNodeKey() {
        return NODE_KEY;
    }

    @Override
    public void visitNode(String factoryField, Node node, BlockStmt body, VariableScope variableScope, ProcessMetaData metadata) {
        EndNode endNode = (EndNode) node;

        body.addStatement(getAssignedFactoryMethod(factoryField, EndNodeFactory.class, getNodeId(node), NODE_KEY, new LongLiteralExpr(endNode.getId())))
                .addStatement(getNameMethod(node, "End"))
                .addStatement(getFactoryMethod(getNodeId(node), METHOD_TERMINATE, new BooleanLiteralExpr(endNode.isTerminate())));

        // if there is trigger defined on end event create TriggerMetaData for it
        if (endNode.getMetaData(METADATA_TRIGGER_REF) != null) {
            Map<String, Object> nodeMetaData = endNode.getMetaData();
            TriggerMetaData triggerMetaData = new TriggerMetaData((String) nodeMetaData.get(METADATA_TRIGGER_REF),
                    (String) nodeMetaData.get(METADATA_TRIGGER_TYPE),
                    (String) nodeMetaData.get(METADATA_MESSAGE_TYPE),
                    (String) nodeMetaData.get(METADATA_MAPPING_VARIABLE),
                    String.valueOf(node.getId()))
                    .validate();
            metadata.getTriggers().add(triggerMetaData);

            // and add trigger action
            BlockStmt actionBody = new BlockStmt();
            LambdaExpr lambda = new LambdaExpr(
                    new Parameter(new UnknownType(), KCONTEXT_VAR), // (kcontext) ->
                    actionBody
            );

            CastExpr variable = new CastExpr(
                    new ClassOrInterfaceType(null, triggerMetaData.getDataType()),
                    new MethodCallExpr(new NameExpr(KCONTEXT_VAR), "getVariable")
                            .addArgument(new StringLiteralExpr(triggerMetaData.getModelRef())));

            MethodCallExpr producerMethodCall = new MethodCallExpr(new NameExpr("producer_" + node.getId()), "produce").addArgument(new MethodCallExpr(new NameExpr("kcontext"), "getProcessInstance")).addArgument(variable);
            actionBody.addStatement(producerMethodCall);

            body.addStatement(getFactoryMethod(getNodeId(node), METHOD_ACTION, lambda));
        }

        visitMetaData(endNode.getMetaData(), body, getNodeId(node));
        body.addStatement(getDoneMethod(getNodeId(node)));
    }
}
