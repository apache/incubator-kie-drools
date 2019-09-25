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

import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.process.core.timer.Timer;
import org.jbpm.ruleflow.core.factory.TimerNodeFactory;
import org.jbpm.workflow.core.node.TimerNode;
import org.kie.api.definition.process.Node;

import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;

public class TimerNodeVisitor extends AbstractVisitor {

    @Override
    public void visitNode(String factoryField, Node node, BlockStmt body, VariableScope variableScope, ProcessMetaData metadata) {
        TimerNode timerNode = (TimerNode) node;
        
        addFactoryMethodWithArgsWithAssignment(factoryField, body, TimerNodeFactory.class, "timerNode" + node.getId(), "timerNode", new LongLiteralExpr(timerNode.getId()));
        addFactoryMethodWithArgs(body, "timerNode" + node.getId(), "name", new StringLiteralExpr(getOrDefault(timerNode.getName(), "End")));
        
        Timer timer = timerNode.getTimer();
        addFactoryMethodWithArgs(body, "timerNode" + node.getId(), "type", new IntegerLiteralExpr(timer.getTimeType()));
        
        if (timer.getTimeType() == Timer.TIME_CYCLE) {           
            addFactoryMethodWithArgs(body, "timerNode" + node.getId(), "delay", new StringLiteralExpr(timer.getDelay()));
            
            if (timer.getPeriod() != null && !timer.getPeriod().isEmpty()) {
                addFactoryMethodWithArgs(body, "timerNode" + node.getId(), "period", new StringLiteralExpr(timer.getPeriod()));
            }
        } else if (timer.getTimeType() == Timer.TIME_DURATION) {           
            addFactoryMethodWithArgs(body, "timerNode" + node.getId(), "delay", new StringLiteralExpr(timer.getDelay()));
            
        } else if (timer.getTimeType() == Timer.TIME_DATE) {           
            addFactoryMethodWithArgs(body, "timerNode" + node.getId(), "date", new StringLiteralExpr(timer.getDate()));
        }
        
               
        visitMetaData(timerNode.getMetaData(), body, "timerNode" + node.getId());
        
        addFactoryMethodWithArgs(body, "timerNode" + node.getId(), "done");
    }
}
