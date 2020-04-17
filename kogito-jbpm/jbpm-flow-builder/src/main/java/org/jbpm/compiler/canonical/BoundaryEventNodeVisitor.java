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

import org.jbpm.process.core.context.variable.Variable;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.ruleflow.core.factory.BoundaryEventNodeFactory;
import org.jbpm.workflow.core.node.BoundaryEventNode;
import org.kie.api.definition.process.Node;

import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;

public class BoundaryEventNodeVisitor extends AbstractVisitor {

    @Override
    public void visitNode(String factoryField, Node node, BlockStmt body, VariableScope variableScope, ProcessMetaData metadata) {
        BoundaryEventNode boundaryEventNode = (BoundaryEventNode) node;
        
        addFactoryMethodWithArgsWithAssignment(factoryField, body, BoundaryEventNodeFactory.class, "boundaryEventNode" + node.getId(), "boundaryEventNode", new LongLiteralExpr(boundaryEventNode.getId()));
        addFactoryMethodWithArgs(body, "boundaryEventNode" + node.getId(), "name", new StringLiteralExpr(getOrDefault(boundaryEventNode.getName(), "BoundaryEvent")));
        addFactoryMethodWithArgs(body, "boundaryEventNode" + node.getId(), "eventType", new StringLiteralExpr(boundaryEventNode.getType()));
        addFactoryMethodWithArgs(body, "boundaryEventNode" + node.getId(), "attachedTo", new StringLiteralExpr(boundaryEventNode.getAttachedToNodeId()));
        addFactoryMethodWithArgs(body, "boundaryEventNode" + node.getId(), "scope", getOrNullExpr(boundaryEventNode.getScope()));

        Variable variable = null;
        if (boundaryEventNode.getVariableName() != null) {
            addFactoryMethodWithArgs(body, "boundaryEventNode" + node.getId(), "variableName", new StringLiteralExpr(boundaryEventNode.getVariableName()));
            variable = variableScope.findVariable(boundaryEventNode.getVariableName());
        }
        
        if ("signal".equals(boundaryEventNode.getMetaData("EventType"))) {
            metadata.getSignals().put(boundaryEventNode.getType(), variable != null ? variable.getType().getStringType() : null);
        } else if ("message".equals(boundaryEventNode.getMetaData("EventType"))) {
            Map<String, Object> nodeMetaData = boundaryEventNode.getMetaData();
            metadata.getTriggers().add(new TriggerMetaData((String)nodeMetaData.get("TriggerRef"), 
                                                           (String)nodeMetaData.get("TriggerType"), 
                                                           (String)nodeMetaData.get("MessageType"), 
                                                           boundaryEventNode.getVariableName(),
                                                           String.valueOf(node.getId())).validate());
        }

        visitMetaData(boundaryEventNode.getMetaData(), body, "boundaryEventNode" + node.getId());
        
        addFactoryMethodWithArgs(body, "boundaryEventNode" + node.getId(), "done");
        
    }
}
