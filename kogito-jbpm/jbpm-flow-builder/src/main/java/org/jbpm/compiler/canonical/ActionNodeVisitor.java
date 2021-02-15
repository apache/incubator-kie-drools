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

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.UnknownType;
import org.jbpm.process.core.context.exception.CompensationScope;
import org.jbpm.process.core.context.variable.Variable;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.process.instance.impl.actions.ProcessInstanceCompensationAction;
import org.jbpm.ruleflow.core.Metadata;
import org.jbpm.ruleflow.core.factory.ActionNodeFactory;
import org.jbpm.workflow.core.DroolsAction;
import org.jbpm.workflow.core.impl.DroolsConsequenceAction;
import org.jbpm.workflow.core.node.ActionNode;

import static org.jbpm.ruleflow.core.Metadata.CUSTOM_SCOPE;
import static org.jbpm.ruleflow.core.Metadata.EVENT_TYPE;
import static org.jbpm.ruleflow.core.Metadata.EVENT_TYPE_SIGNAL;
import static org.jbpm.ruleflow.core.Metadata.REF;
import static org.jbpm.ruleflow.core.Metadata.TRIGGER_REF;
import static org.jbpm.ruleflow.core.Metadata.VARIABLE;
import static org.jbpm.ruleflow.core.factory.ActionNodeFactory.METHOD_ACTION;

public class ActionNodeVisitor extends AbstractNodeVisitor<ActionNode> {

    private static final String INTERMEDIATE_COMPENSATION_TYPE = "IntermediateThrowEvent-None";

    @Override
    protected String getNodeKey() {
        return "actionNode";
    }

    @Override
    public void visitNode(String factoryField, ActionNode node, BlockStmt body, VariableScope variableScope, ProcessMetaData metadata) {
        body.addStatement(getAssignedFactoryMethod(factoryField, ActionNodeFactory.class, getNodeId(node), getNodeKey(), new LongLiteralExpr(node.getId())))
                .addStatement(getNameMethod(node, "Script"));

        if (isIntermediateCompensation(node)) {
            ProcessInstanceCompensationAction action = (ProcessInstanceCompensationAction) node.getAction().getMetaData(Metadata.ACTION);
            String compensateNode = CompensationScope.IMPLICIT_COMPENSATION_PREFIX + metadata.getProcessId();
            if (action != null) {
                compensateNode = action.getActivityRef();
            }
            LambdaExpr lambda = TriggerMetaData.buildCompensationLambdaExpr(compensateNode);
            body.addStatement(getFactoryMethod(getNodeId(node), METHOD_ACTION, lambda));
        } else if (node.getMetaData(TRIGGER_REF) != null) { // if there is trigger defined on end event create TriggerMetaData for it
            LambdaExpr lambda = TriggerMetaData.buildLambdaExpr(node, metadata);
            body.addStatement(getFactoryMethod(getNodeId(node), METHOD_ACTION, lambda));
        } else if (node.getMetaData(REF) != null && EVENT_TYPE_SIGNAL.equals(node.getMetaData(EVENT_TYPE))) {
            body.addStatement(getFactoryMethod(getNodeId(node), METHOD_ACTION, TriggerMetaData.buildAction((String)node.getMetaData(REF),
                (String)node.getMetaData(VARIABLE), (String) node.getMetaData(CUSTOM_SCOPE))));
        } else {
            String consequence = getActionConsequence(node.getAction());
            if (consequence == null || consequence.trim().isEmpty()) {
                throw new IllegalStateException("Action node " + node.getId() + " name " + node.getName() + " has no action defined");
            }
            BlockStmt actionBody = new BlockStmt();
            List<Variable> variables = variableScope.getVariables();
            variables.stream()
                    .filter(v -> consequence.contains(v.getName()))
                    .map(ActionNodeVisitor::makeAssignment)
                    .forEach(actionBody::addStatement);

            BlockStmt blockStmt = StaticJavaParser.parseBlock("{" + consequence + "}");
            blockStmt.getStatements().forEach(actionBody::addStatement);

            LambdaExpr lambda = new LambdaExpr(
                    new Parameter(new UnknownType(), KCONTEXT_VAR), // (kcontext) ->
                    actionBody
            );
            body.addStatement(getFactoryMethod(getNodeId(node), METHOD_ACTION, lambda));
        }
        visitMetaData(node.getMetaData(), body, getNodeId(node));
        body.addStatement(getDoneMethod(getNodeId(node)));
    }

    private boolean isIntermediateCompensation(ActionNode node) {
        return INTERMEDIATE_COMPENSATION_TYPE.equals(node.getMetaData(Metadata.NODE_TYPE));
    }

    private String getActionConsequence(DroolsAction action) {
        if (!(action instanceof DroolsConsequenceAction)) {
            return null;
        }
        return ((DroolsConsequenceAction) action).getConsequence();
    }
}
