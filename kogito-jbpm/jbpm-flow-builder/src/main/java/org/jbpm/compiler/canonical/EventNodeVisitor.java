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
import org.jbpm.ruleflow.core.factory.EventNodeFactory;
import org.jbpm.workflow.core.node.EventNode;
import org.kie.api.definition.process.Node;

import java.text.MessageFormat;
import java.util.Map;

import static org.jbpm.ruleflow.core.factory.EventNodeFactory.METHOD_EVENT_TYPE;
import static org.jbpm.ruleflow.core.factory.EventNodeFactory.METHOD_VARIABLE_NAME;

public class EventNodeVisitor extends AbstractNodeVisitor {

    private static final String NODE_KEY = "eventNode";

    @Override
    protected String getNodeKey() {
        return NODE_KEY;
    }

    @Override
    public void visitNode(String factoryField, Node node, BlockStmt body, VariableScope variableScope, ProcessMetaData metadata) {
        EventNode eventNode = (EventNode) node;

        body.addStatement(getAssignedFactoryMethod(factoryField, EventNodeFactory.class, getNodeId(node), NODE_KEY, new LongLiteralExpr(eventNode.getId())))
                .addStatement(getNameMethod(node, "Event"))
                .addStatement(getFactoryMethod(getNodeId(node), METHOD_EVENT_TYPE, new StringLiteralExpr(eventNode.getType())));

        Variable variable = null;
        if (eventNode.getVariableName() != null) {
            body.addStatement(getFactoryMethod(getNodeId(node), METHOD_VARIABLE_NAME, new StringLiteralExpr(eventNode.getVariableName())));
            variable = variableScope.findVariable(eventNode.getVariableName());
        }

        if (EVENT_TYPE_SIGNAL.equals(eventNode.getMetaData(METADATA_EVENT_TYPE))) {
            metadata.getSignals().put(eventNode.getType(), variable != null ? variable.getType().getStringType() : null);
        } else if (EVENT_TYPE_MESSAGE.equals(eventNode.getMetaData(METADATA_EVENT_TYPE))) {
            Map<String, Object> nodeMetaData = eventNode.getMetaData();
            try {
                TriggerMetaData triggerMetaData = new TriggerMetaData((String) nodeMetaData.get(METADATA_TRIGGER_REF),
                        (String) nodeMetaData.get(METADATA_TRIGGER_TYPE),
                        (String) nodeMetaData.get(METADATA_MESSAGE_TYPE),
                        eventNode.getVariableName(),
                        String.valueOf(node.getId())).validate();
                metadata.getTriggers().add(triggerMetaData);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(
                        MessageFormat.format(
                                "Invalid parameters for event node \"{0}\": {1}",
                                eventNode.getName(),
                                e.getMessage()), e);
            }
        }
        visitMetaData(eventNode.getMetaData(), body, getNodeId(node));
        body.addStatement(getDoneMethod(getNodeId(node)));
    }
}
