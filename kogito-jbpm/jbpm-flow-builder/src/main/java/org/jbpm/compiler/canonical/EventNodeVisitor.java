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
import org.jbpm.ruleflow.core.factory.EventNodeFactory;
import org.jbpm.workflow.core.node.EventNode;
import org.kie.api.definition.process.Node;

import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;

public class EventNodeVisitor extends AbstractVisitor {

    @Override
    public void visitNode(String factoryField, Node node, BlockStmt body, VariableScope variableScope, ProcessMetaData metadata) {
        EventNode eventNode = (EventNode) node;
        
        addFactoryMethodWithArgsWithAssignment(factoryField, body, EventNodeFactory.class, "eventNode" + node.getId(), "eventNode", new LongLiteralExpr(eventNode.getId()));
        addFactoryMethodWithArgs(body, "eventNode" + node.getId(), "name", new StringLiteralExpr(getOrDefault(eventNode.getName(), "Event")));
        addFactoryMethodWithArgs(body, "eventNode" + node.getId(), "eventType", new StringLiteralExpr(eventNode.getType()));
        
        Variable variable = null;
        if (eventNode.getVariableName() != null) {
            addFactoryMethodWithArgs(body, "eventNode" + node.getId(), "variableName", new StringLiteralExpr(eventNode.getVariableName()));
            variable = variableScope.findVariable(eventNode.getVariableName());
        }
        
        if ("signal".equals(eventNode.getMetaData("EventType"))) {
            metadata.getSignals().put(eventNode.getType(), variable != null ? variable.getType().getStringType() : null);
        } else if ("message".equals(eventNode.getMetaData("EventType"))) {
            Map<String, Object> nodeMetaData = eventNode.getMetaData();
            metadata.getTriggers().add(new TriggerMetaData((String)nodeMetaData.get("TriggerRef"), 
                                                           (String)nodeMetaData.get("TriggerType"), 
                                                           (String)nodeMetaData.get("MessageType"), 
                                                           eventNode.getVariableName(),
                                                           String.valueOf(node.getId())).validate());
        }

        visitMetaData(eventNode.getMetaData(), body, "eventNode" + node.getId());
        
        addFactoryMethodWithArgs(body, "eventNode" + node.getId(), "done");
        
    }
}
