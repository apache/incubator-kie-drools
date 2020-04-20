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

import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.process.core.timer.Timer;
import org.jbpm.ruleflow.core.factory.TimerNodeFactory;
import org.jbpm.workflow.core.node.TimerNode;
import org.kie.api.definition.process.Node;

import static org.jbpm.ruleflow.core.factory.TimerNodeFactory.METHOD_DATE;
import static org.jbpm.ruleflow.core.factory.TimerNodeFactory.METHOD_DELAY;
import static org.jbpm.ruleflow.core.factory.TimerNodeFactory.METHOD_PERIOD;
import static org.jbpm.ruleflow.core.factory.TimerNodeFactory.METHOD_TYPE;

public class TimerNodeVisitor extends AbstractNodeVisitor {

    protected static final String NODE_KEY = "timerNode";

    @Override
    protected String getNodeKey() {
        return NODE_KEY;
    }

    @Override
    public void visitNode(String factoryField, Node node, BlockStmt body, VariableScope variableScope, ProcessMetaData metadata) {
        TimerNode timerNode = (TimerNode) node;

        body.addStatement(getAssignedFactoryMethod(factoryField, TimerNodeFactory.class, getNodeId(node), NODE_KEY, new LongLiteralExpr(timerNode.getId())))
        .addStatement(getNameMethod(node, "End"));

        Timer timer = timerNode.getTimer();
        body.addStatement(getFactoryMethod(getNodeId(node), METHOD_TYPE, new IntegerLiteralExpr(timer.getTimeType())));

        if (timer.getTimeType() == Timer.TIME_CYCLE) {
            body.addStatement(getFactoryMethod(getNodeId(node), METHOD_DELAY, new StringLiteralExpr(timer.getDelay())));
            if (timer.getPeriod() != null && !timer.getPeriod().isEmpty()) {
                body.addStatement(getFactoryMethod(getNodeId(node), METHOD_PERIOD, new StringLiteralExpr(timer.getPeriod())));
            }
        } else if (timer.getTimeType() == Timer.TIME_DURATION) {
            body.addStatement(getFactoryMethod(getNodeId(node), METHOD_DELAY, new StringLiteralExpr(timer.getDelay())));
        } else if (timer.getTimeType() == Timer.TIME_DATE) {
            body.addStatement(getFactoryMethod(getNodeId(node), METHOD_DATE, new StringLiteralExpr(timer.getDate())));
        }

        visitMetaData(timerNode.getMetaData(), body, getNodeId(node));
        body.addStatement(getDoneMethod(getNodeId(node)));
    }
}
