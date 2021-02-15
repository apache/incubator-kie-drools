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

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.jbpm.process.core.context.variable.Variable;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.process.core.datatype.impl.type.ObjectDataType;
import org.jbpm.ruleflow.core.factory.SubProcessNodeFactory;
import org.jbpm.util.PatternConstants;
import org.jbpm.workflow.core.node.SubProcessNode;
import org.kie.kogito.internal.process.runtime.KogitoWorkflowProcess;

import static com.github.javaparser.StaticJavaParser.parse;
import static org.drools.core.util.StringUtils.ucFirst;
import static org.jbpm.ruleflow.core.factory.SubProcessNodeFactory.METHOD_INDEPENDENT;
import static org.jbpm.ruleflow.core.factory.SubProcessNodeFactory.METHOD_PROCESS_ID;
import static org.jbpm.ruleflow.core.factory.SubProcessNodeFactory.METHOD_PROCESS_NAME;
import static org.jbpm.ruleflow.core.factory.SubProcessNodeFactory.METHOD_WAIT_FOR_COMPLETION;

public class LambdaSubProcessNodeVisitor extends AbstractNodeVisitor<SubProcessNode> {

    @Override
    protected String getNodeKey() {
        return "subProcessNode";
    }

    @Override
    public void visitNode(String factoryField, SubProcessNode node, BlockStmt body, VariableScope variableScope, ProcessMetaData metadata) {
        Optional<Expression> retValue;
        try (InputStream resourceAsStream = this.getClass().getResourceAsStream("/class-templates/SubProcessFactoryTemplate.java")) {
            retValue = parse(resourceAsStream).findFirst(Expression.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String name = node.getName();
        String subProcessId = node.getProcessId();

        NodeValidator.of(getNodeKey(), name)
                .notEmpty("subProcessId", subProcessId)
                .validate();


        body.addStatement(getAssignedFactoryMethod(factoryField, SubProcessNodeFactory.class, getNodeId(node), getNodeKey(), new LongLiteralExpr(node.getId())))
                .addStatement(getNameMethod(node, "Call Activity"))
                .addStatement(getFactoryMethod(getNodeId(node), METHOD_PROCESS_ID, new StringLiteralExpr(subProcessId)))
                .addStatement(getFactoryMethod(getNodeId(node), METHOD_PROCESS_NAME, new StringLiteralExpr(getOrDefault(node.getProcessName(), ""))))
                .addStatement(getFactoryMethod(getNodeId(node), METHOD_WAIT_FOR_COMPLETION, new BooleanLiteralExpr(node.isWaitForCompletion())))
                .addStatement(getFactoryMethod(getNodeId(node), METHOD_INDEPENDENT, new BooleanLiteralExpr(node.isIndependent())));

        Map<String, String> inputTypes = (Map<String, String>) node.getMetaData("BPMN.InputTypes");

        String subProcessModelClassName = ProcessToExecModelGenerator.extractModelClassName(subProcessId);
        ModelMetaData subProcessModel = new ModelMetaData(subProcessId,
                metadata.getPackageName(),
                subProcessModelClassName,
                KogitoWorkflowProcess.PRIVATE_VISIBILITY,
                VariableDeclarations.ofRawInfo(inputTypes),
                false);

        retValue.ifPresent(retValueExpression -> {
            retValueExpression.findAll(ClassOrInterfaceType.class)
                    .stream()
                    .filter(t -> t.getNameAsString().equals("$Type$"))
                    .forEach(t -> t.setName(subProcessModelClassName));

            retValueExpression.findFirst(MethodDeclaration.class, m -> m.getNameAsString().equals("bind"))
                    .ifPresent(m -> m.setBody(bind(variableScope, node, subProcessModel)));
            retValueExpression.findFirst(MethodDeclaration.class, m -> m.getNameAsString().equals("createInstance"))
                    .ifPresent(m -> m.setBody(createInstance(node, metadata)));
            retValueExpression.findFirst(MethodDeclaration.class, m -> m.getNameAsString().equals("unbind"))
                    .ifPresent(m -> m.setBody(unbind(variableScope, node)));
        });

        if (retValue.isPresent()) {
            body.addStatement(getFactoryMethod(getNodeId(node), getNodeKey(), retValue.get()));
        } else {
            body.addStatement(getFactoryMethod(getNodeId(node), getNodeKey()));
        }

        visitMetaData(node.getMetaData(), body, getNodeId(node));
        body.addStatement(getDoneMethod(getNodeId(node)));
    }

    private BlockStmt bind(VariableScope variableScope, SubProcessNode subProcessNode, ModelMetaData subProcessModel) {
        BlockStmt actionBody = new BlockStmt();
        actionBody.addStatement(subProcessModel.newInstance("model"));

        for (Map.Entry<String, String> e : subProcessNode.getInMappings().entrySet()) {
            // check if given mapping is an expression
            Matcher matcher = PatternConstants.PARAMETER_MATCHER.matcher(e.getValue());
            if (matcher.find()) {
                String expression = matcher.group(1);
                String topLevelVariable = expression.split("\\.")[0];
                Variable v = variableScope.findVariable(topLevelVariable);

                actionBody.addStatement(makeAssignment(v));
                actionBody.addStatement(subProcessModel.callSetter("model", e.getKey(), dotNotationToGetExpression(expression)));
            } else {
                Variable v = variableScope.findVariable(e.getValue());
                if (v != null) {
                    actionBody.addStatement(makeAssignment(v));
                    actionBody.addStatement(subProcessModel.callSetter("model", e.getKey(), e.getValue()));
                }
            }
        }

        subProcessNode.getInAssociations().stream().filter(da -> da.getAssignments() != null && !da.getAssignments().isEmpty()).forEach(da -> {
            if (da.getTransformation() == null && da.getSources().size() == 1) {
                actionBody.addStatement(subProcessModel.callSetter("model", da.getTarget(), new StringLiteralExpr(da.getSources().get(0))));
            }
        });

        actionBody.addStatement(new ReturnStmt(new NameExpr("model")));
        return actionBody;
    }

    private BlockStmt createInstance(SubProcessNode subProcessNode, ProcessMetaData metadata) {

        String processId = ProcessToExecModelGenerator.extractProcessId(subProcessNode.getProcessId());
        String processFieldName = "process" + processId;

        MethodCallExpr processInstanceSupplier = new MethodCallExpr(new NameExpr(processFieldName), "createInstance").addArgument("model");

        metadata.addSubProcess(processId, subProcessNode.getProcessId());

        return new BlockStmt().addStatement(new ReturnStmt(processInstanceSupplier));
    }

    private BlockStmt unbind(VariableScope variableScope, SubProcessNode subProcessNode) {
        BlockStmt stmts = new BlockStmt();

        for (Map.Entry<String, String> e : subProcessNode.getOutMappings().entrySet()) {

            // check if given mapping is an expression
            Matcher matcher = PatternConstants.PARAMETER_MATCHER.matcher(e.getValue());
            if (matcher.find()) {

                String expression = matcher.group(1);
                String topLevelVariable = expression.split("\\.")[0];
                Map<String, String> dataOutputs = (Map<String, String>) subProcessNode.getMetaData("BPMN.OutputTypes");
                Variable variable = new Variable();
                variable.setName(topLevelVariable);
                variable.setType(new ObjectDataType(dataOutputs.get(e.getKey())));

                stmts.addStatement(makeAssignment(variableScope.findVariable(topLevelVariable)));
                stmts.addStatement(makeAssignmentFromModel(variable, e.getKey()));

                stmts.addStatement(dotNotationToSetExpression(expression, e.getKey()));

                stmts.addStatement(new MethodCallExpr()
                        .setScope(new NameExpr(KCONTEXT_VAR))
                        .setName("setVariable")
                        .addArgument(new StringLiteralExpr(topLevelVariable))
                        .addArgument(topLevelVariable));
            } else {

                stmts.addStatement(makeAssignmentFromModel(variableScope.findVariable(e.getValue()), e.getKey()));
                stmts.addStatement(new MethodCallExpr()
                        .setScope(new NameExpr(KCONTEXT_VAR))
                        .setName("setVariable")
                        .addArgument(new StringLiteralExpr(e.getValue()))
                        .addArgument(e.getKey()));
            }

        }

        return stmts;
    }

    protected Expression dotNotationToSetExpression(String dotNotation, String value) {
        String[] elements = dotNotation.split("\\.");
        Expression scope = new NameExpr(elements[0]);
        if (elements.length == 1) {
            return new AssignExpr(
                    scope,
                    new NameExpr(value),
                    AssignExpr.Operator.ASSIGN);
        }
        for (int i = 1; i < elements.length - 1; i++) {
            scope = new MethodCallExpr()
                    .setScope(scope)
                    .setName("get" + ucFirst(elements[i]));
        }

        return new MethodCallExpr()
                .setScope(scope)
                .setName("set" + ucFirst(elements[elements.length - 1]))
                .addArgument(value);
    }

    protected Expression dotNotationToGetExpression(String dotNotation) {
        String[] elements = dotNotation.split("\\.");
        Expression scope = new NameExpr(elements[0]);

        if (elements.length == 1) {
            return scope;
        }

        for (int i = 1; i < elements.length; i++) {
            scope = new MethodCallExpr()
                    .setScope(scope)
                    .setName("get" + ucFirst(elements[i]));
        }

        return scope;
    }
}
