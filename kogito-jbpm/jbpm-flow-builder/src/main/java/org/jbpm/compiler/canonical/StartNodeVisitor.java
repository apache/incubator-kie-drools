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

import java.util.Map;
import java.util.Optional;

import org.jbpm.process.core.context.variable.Variable;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.process.core.timer.Timer;
import org.jbpm.ruleflow.core.Metadata;
import org.jbpm.ruleflow.core.factory.StartNodeFactory;
import org.jbpm.workflow.core.node.StartNode;

import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;

import static org.jbpm.ruleflow.core.Metadata.EVENT_TYPE;
import static org.jbpm.ruleflow.core.Metadata.MESSAGE_TYPE;
import static org.jbpm.ruleflow.core.Metadata.TRIGGER_MAPPING;
import static org.jbpm.ruleflow.core.Metadata.TRIGGER_REF;
import static org.jbpm.ruleflow.core.factory.StartNodeFactory.METHOD_INTERRUPTING;
import static org.jbpm.ruleflow.core.factory.StartNodeFactory.METHOD_TIMER;
import static org.jbpm.ruleflow.core.factory.StartNodeFactory.METHOD_TRIGGER;

public class StartNodeVisitor extends AbstractNodeVisitor<StartNode> {

    @Override
    protected String getNodeKey() {
        return "startNode";
    }

    @Override
    public void visitNode(String factoryField, StartNode startNode, BlockStmt body, VariableScope variableScope, ProcessMetaData metadata) {
        body.addStatement(getAssignedFactoryMethod(factoryField, StartNodeFactory.class, getNodeId(startNode), getNodeKey(), getWorkflowElementConstructor(startNode.getId())))
                .addStatement(getNameMethod(startNode, "Start"))
                .addStatement(getFactoryMethod(getNodeId(startNode), METHOD_INTERRUPTING, new BooleanLiteralExpr(startNode.isInterrupting())));
        visitMetaData(startNode.getMetaData(), body, getNodeId(startNode));
        addNodeMappings(startNode, body, getNodeId(startNode));
        Map<String, Object> nodeMetaData = startNode.getMetaData();
        String eventType = (String) startNode.getMetaData(EVENT_TYPE);
        switch (eventType) {
            case Metadata.EVENT_TYPE_TIMER: {
                Timer timer = startNode.getTimer();
                body.addStatement(getFactoryMethod(getNodeId(startNode), METHOD_TIMER, getOrNullExpr(timer.getDelay()),
                        getOrNullExpr(timer.getPeriod()),
                        getOrNullExpr(timer.getDate()),
                        new IntegerLiteralExpr(startNode.getTimer().getTimeType())));
                break;
            }
            case Metadata.EVENT_TYPE_SIGNAL:
            case Metadata.EVENT_TYPE_MESSAGE: {
                TriggerMetaData triggerMetaData = buildTriggerMetadata(startNode);
                metadata.addTrigger(triggerMetaData);
                handleIO(startNode, startNode.getMetaData(), body, variableScope, metadata);
                metadata.addSignal((String) nodeMetaData.get(TRIGGER_REF), computePayloadType(startNode, variableScope).orElse(null));
                break;
            }
            case Metadata.EVENT_TYPE_ERROR:
            case Metadata.EVENT_TYPE_ESCALATION:
            case Metadata.EVENT_TYPE_COMPENSATION:
            case Metadata.EVENT_TYPE_CONDITIONAL:
                handleIO(startNode, startNode.getMetaData(), body, variableScope, metadata);
                metadata.addSignal((String) nodeMetaData.get(TRIGGER_REF), null);
                break;
            default:
                // since there is start node without trigger then make sure it is startable
                metadata.setStartable(true);
                break;
        }
        body.addStatement(getDoneMethod(getNodeId(startNode)));
    }

    private TriggerMetaData buildTriggerMetadata(StartNode node) {
        return TriggerMetaData.of(node, (String) node.getMetaData(TRIGGER_MAPPING));
    }

    protected void handleIO(StartNode startNode, Map<String, Object> nodeMetaData, BlockStmt body, VariableScope variableScope, ProcessMetaData metadata) {
        if (nodeMetaData.containsKey(MESSAGE_TYPE)) {
            body.addStatement(getFactoryMethod(getNodeId(startNode), METHOD_TRIGGER,
                    new StringLiteralExpr((String) nodeMetaData.get(TRIGGER_REF)),
                    buildDataAssociationsExpression(startNode, startNode.getIoSpecification().getDataOutputAssociation())));
        } else if (nodeMetaData.containsKey(TRIGGER_REF)) {
            body.addStatement(getFactoryMethod(getNodeId(startNode), METHOD_TRIGGER,
                    new StringLiteralExpr((String) nodeMetaData.get(TRIGGER_REF)),
                    buildDataAssociationsExpression(startNode, startNode.getIoSpecification().getDataOutputAssociation())));
        }
    }

    public Optional<String> computePayloadType(StartNode startNode, VariableScope variableScope) {

        String triggerMapping = (String) startNode.getMetaData(TRIGGER_MAPPING);
        if (triggerMapping == null) {
            return Optional.empty();
        }

        Variable variable = null;
        variable = variableScope.findVariable(triggerMapping);

        if (variable == null) {
            // check parent node container
            VariableScope vscope = (VariableScope) startNode.resolveContext(VariableScope.VARIABLE_SCOPE, triggerMapping);
            variable = vscope.findVariable(triggerMapping);
        }
        return Optional.ofNullable(variable != null ? variable.getType().getStringType() : null);
    }

}
