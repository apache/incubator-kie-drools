/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.compiler.canonical;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.jbpm.process.core.context.exception.CompensationScope;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.process.instance.impl.actions.ProcessInstanceCompensationAction;
import org.jbpm.ruleflow.core.Metadata;
import org.jbpm.ruleflow.core.factory.ActionNodeFactory;
import org.jbpm.ruleflow.core.factory.EndNodeFactory;
import org.jbpm.workflow.core.DroolsAction;
import org.jbpm.workflow.core.impl.DroolsConsequenceAction;
import org.jbpm.workflow.core.impl.ExtendedNodeImpl;
import org.jbpm.workflow.core.node.EndNode;

import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;

import static org.jbpm.ruleflow.core.Metadata.CUSTOM_SCOPE;
import static org.jbpm.ruleflow.core.Metadata.EVENT_TYPE;
import static org.jbpm.ruleflow.core.Metadata.EVENT_TYPE_SIGNAL;
import static org.jbpm.ruleflow.core.Metadata.REF;
import static org.jbpm.ruleflow.core.Metadata.TRIGGER_REF;
import static org.jbpm.ruleflow.core.Metadata.VARIABLE;
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
        Optional<ProcessInstanceCompensationAction> compensationAction = getCompensationAction(node);
        if (compensationAction.isPresent()) {
            String compensateNode = CompensationScope.IMPLICIT_COMPENSATION_PREFIX + metadata.getProcessId();
            if (compensationAction.get().getActivityRef() != null) {
                compensateNode = compensationAction.get().getActivityRef();
            }
            LambdaExpr lambda = buildCompensationLambdaExpr(compensateNode);
            body.addStatement(getFactoryMethod(getNodeId(node), ActionNodeFactory.METHOD_ACTION, lambda));
        } else if (node.getMetaData(TRIGGER_REF) != null) {
            LambdaExpr lambda = buildLambdaExpr(node, metadata);
            body.addStatement(getFactoryMethod(getNodeId(node), METHOD_ACTION, lambda));
        } else if (node.getMetaData(REF) != null && EVENT_TYPE_SIGNAL.equals(node.getMetaData(EVENT_TYPE))) {
            body.addStatement(getFactoryMethod(getNodeId(node), METHOD_ACTION, buildAction((String) node.getMetaData(REF),
                    (String) node.getMetaData(VARIABLE), (String) node.getMetaData(CUSTOM_SCOPE))));
        }
        visitMetaData(node.getMetaData(), body, getNodeId(node));
        body.addStatement(getDoneMethod(getNodeId(node)));
    }

    private Optional<ProcessInstanceCompensationAction> getCompensationAction(EndNode node) {
        List<DroolsAction> actions = node.getActions(ExtendedNodeImpl.EVENT_NODE_ENTER);
        if (actions == null || actions.isEmpty()) {
            return Optional.empty();
        }
        return actions.stream()
                .filter(a -> a instanceof DroolsConsequenceAction)
                .map(d -> d.getMetaData(Metadata.ACTION))
                .filter(Objects::nonNull)
                .filter(a -> a instanceof ProcessInstanceCompensationAction)
                .map(a -> (ProcessInstanceCompensationAction) a)
                .findFirst();
    }
}
