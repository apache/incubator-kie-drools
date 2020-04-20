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

import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import org.jbpm.process.core.context.variable.Variable;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.ruleflow.core.factory.BoundaryEventNodeFactory;
import org.jbpm.workflow.core.node.BoundaryEventNode;
import org.kie.api.definition.process.Node;

import java.util.Map;

import static org.jbpm.ruleflow.core.factory.BoundaryEventNodeFactory.METHOD_ATTACHED_TO;
import static org.jbpm.ruleflow.core.factory.EventNodeFactory.METHOD_EVENT_TYPE;
import static org.jbpm.ruleflow.core.factory.EventNodeFactory.METHOD_SCOPE;
import static org.jbpm.ruleflow.core.factory.EventNodeFactory.METHOD_VARIABLE_NAME;

public class BoundaryEventNodeVisitor extends AbstractNodeVisitor {

    private static final String NODE_KEY = "boundaryEventNode";

    @Override
    protected String getNodeKey() {
        return NODE_KEY;
    }

    @Override
    public void visitNode(String factoryField, Node node, BlockStmt body, VariableScope variableScope, ProcessMetaData metadata) {
        BoundaryEventNode boundaryEventNode = (BoundaryEventNode) node;

        body.addStatement(getAssignedFactoryMethod(factoryField, BoundaryEventNodeFactory.class, getNodeId(node), NODE_KEY, new LongLiteralExpr(boundaryEventNode.getId())))
                .addStatement(getNameMethod(node, "BoundaryEvent"))
                .addStatement(getFactoryMethod(getNodeId(node), METHOD_EVENT_TYPE, new StringLiteralExpr(boundaryEventNode.getType())))
                .addStatement(getFactoryMethod(getNodeId(node), METHOD_ATTACHED_TO, new StringLiteralExpr(boundaryEventNode.getAttachedToNodeId())))
                .addStatement(getFactoryMethod(getNodeId(node), METHOD_SCOPE, getOrNullExpr(boundaryEventNode.getScope())));

        Variable variable = null;
        if (boundaryEventNode.getVariableName() != null) {
            body.addStatement(getFactoryMethod(getNodeId(node), METHOD_VARIABLE_NAME, new StringLiteralExpr(boundaryEventNode.getVariableName())));
            variable = variableScope.findVariable(boundaryEventNode.getVariableName());
        }

        if (EVENT_TYPE_SIGNAL.equals(boundaryEventNode.getMetaData(METADATA_EVENT_TYPE))) {
            metadata.getSignals().put(boundaryEventNode.getType(), variable != null ? variable.getType().getStringType() : null);
        } else if (EVENT_TYPE_MESSAGE.equals(boundaryEventNode.getMetaData(METADATA_EVENT_TYPE))) {
            Map<String, Object> nodeMetaData = boundaryEventNode.getMetaData();
            metadata.getTriggers().add(new TriggerMetaData((String) nodeMetaData.get(METADATA_TRIGGER_REF),
                    (String) nodeMetaData.get(METADATA_TRIGGER_TYPE),
                    (String) nodeMetaData.get(METADATA_MESSAGE_TYPE),
                    boundaryEventNode.getVariableName(),
                    String.valueOf(node.getId())).validate());
        }

        visitMetaData(boundaryEventNode.getMetaData(), body, getNodeId(node));
        body.addStatement(getDoneMethod(getNodeId(node)));
    }
}
