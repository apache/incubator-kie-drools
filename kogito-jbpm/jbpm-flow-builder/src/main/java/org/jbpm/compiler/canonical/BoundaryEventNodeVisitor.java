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

import java.util.Map;

import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import org.jbpm.process.core.context.variable.Variable;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.ruleflow.core.factory.BoundaryEventNodeFactory;
import org.jbpm.workflow.core.node.BoundaryEventNode;

import static org.jbpm.ruleflow.core.Metadata.EVENT_TYPE;
import static org.jbpm.ruleflow.core.Metadata.EVENT_TYPE_COMPENSATION;
import static org.jbpm.ruleflow.core.Metadata.EVENT_TYPE_MESSAGE;
import static org.jbpm.ruleflow.core.Metadata.EVENT_TYPE_SIGNAL;
import static org.jbpm.ruleflow.core.Metadata.MESSAGE_TYPE;
import static org.jbpm.ruleflow.core.Metadata.TRIGGER_REF;
import static org.jbpm.ruleflow.core.Metadata.TRIGGER_TYPE;
import static org.jbpm.ruleflow.core.factory.BoundaryEventNodeFactory.METHOD_ADD_COMPENSATION_HANDLER;
import static org.jbpm.ruleflow.core.factory.BoundaryEventNodeFactory.METHOD_ATTACHED_TO;
import static org.jbpm.ruleflow.core.factory.EventNodeFactory.METHOD_EVENT_TYPE;
import static org.jbpm.ruleflow.core.factory.EventNodeFactory.METHOD_SCOPE;
import static org.jbpm.ruleflow.core.factory.EventNodeFactory.METHOD_VARIABLE_NAME;

public class BoundaryEventNodeVisitor extends AbstractNodeVisitor<BoundaryEventNode> {

    @Override
    protected String getNodeKey() {
        return "boundaryEventNode";
    }

    @Override
    public void visitNode(String factoryField, BoundaryEventNode node, BlockStmt body, VariableScope variableScope, ProcessMetaData metadata) {
        body.addStatement(getAssignedFactoryMethod(factoryField, BoundaryEventNodeFactory.class, getNodeId(node), getNodeKey(), new LongLiteralExpr(node.getId())))
                .addStatement(getNameMethod(node, "BoundaryEvent"))
                .addStatement(getFactoryMethod(getNodeId(node), METHOD_EVENT_TYPE, new StringLiteralExpr(node.getType())))
                .addStatement(getFactoryMethod(getNodeId(node), METHOD_ATTACHED_TO, new StringLiteralExpr(node.getAttachedToNodeId())))
                .addStatement(getFactoryMethod(getNodeId(node), METHOD_SCOPE, getOrNullExpr(node.getScope())));

        Variable variable = null;
        if (node.getVariableName() != null) {
            body.addStatement(getFactoryMethod(getNodeId(node), METHOD_VARIABLE_NAME, new StringLiteralExpr(node.getVariableName())));
            variable = variableScope.findVariable(node.getVariableName());
        }

        if (EVENT_TYPE_SIGNAL.equals(node.getMetaData(EVENT_TYPE))) {
            metadata.addSignal(node.getType(), variable != null ? variable.getType().getStringType() : null);
        } else if (EVENT_TYPE_MESSAGE.equals(node.getMetaData(EVENT_TYPE))) {
            Map<String, Object> nodeMetaData = node.getMetaData();
            metadata.addTrigger(new TriggerMetaData((String) nodeMetaData.get(TRIGGER_REF),
                    (String) nodeMetaData.get(TRIGGER_TYPE),
                    (String) nodeMetaData.get(MESSAGE_TYPE),
                    node.getVariableName(),
                    String.valueOf(node.getId())).validate());
        } else if (EVENT_TYPE_COMPENSATION.equalsIgnoreCase((String) node.getMetaData(EVENT_TYPE)) && node.getAttachedToNodeId() != null) {
            body.addStatement(getFactoryMethod(getNodeId(node), METHOD_ADD_COMPENSATION_HANDLER, new StringLiteralExpr(node.getAttachedToNodeId())));
        }

        visitMetaData(node.getMetaData(), body, getNodeId(node));
        body.addStatement(getDoneMethod(getNodeId(node)));
    }
}
