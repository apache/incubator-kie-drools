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

import java.io.InputStream;
import java.util.Map;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.drools.core.util.StringUtils;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.ruleflow.core.factory.SubProcessNodeFactory;
import org.jbpm.workflow.core.node.SubProcessNode;
import org.kie.api.definition.process.Node;

public class LambdaSubProcessNodeVisitor extends AbstractVisitor {

    private final Map<String, ModelMetaData> processToModel;

    public LambdaSubProcessNodeVisitor(Map<String, ModelMetaData> processToModel) {
        this.processToModel = processToModel;
    }

    @Override
    public void visitNode(Node node, BlockStmt body, VariableScope variableScope, ProcessMetaData metadata) {

        InputStream resourceAsStream = this.getClass().getResourceAsStream("/class-templates/SubProcessFactoryTemplate.java");
        Expression retValue = JavaParser.parse(resourceAsStream).findFirst(Expression.class).get();

        SubProcessNode subProcessNode = (SubProcessNode) node;
        String nodeVar = "subProcessNode" + node.getId();
        addFactoryMethodWithArgsWithAssignment(body, SubProcessNodeFactory.class, nodeVar, "subProcessNode", new LongLiteralExpr(subProcessNode.getId()));
        addFactoryMethodWithArgs(body, nodeVar, "name", new StringLiteralExpr(getOrDefault(subProcessNode.getName(), "Call Activity")));
        addFactoryMethodWithArgs(body, nodeVar, "processId", new StringLiteralExpr(subProcessNode.getProcessId()));
        addFactoryMethodWithArgs(body, nodeVar, "processName", new StringLiteralExpr(getOrDefault(subProcessNode.getProcessName(), "")));
        addFactoryMethodWithArgs(body, nodeVar, "waitForCompletion", new BooleanLiteralExpr(subProcessNode.isWaitForCompletion()));
        addFactoryMethodWithArgs(body, nodeVar, "independent", new BooleanLiteralExpr(subProcessNode.isIndependent()));

        ModelMetaData subProcessModel = processToModel.get(subProcessNode.getProcessId());

        retValue.findAll(ClassOrInterfaceType.class)
                .stream()
                .filter(t -> t.getNameAsString().equals("$Type$"))
                .forEach(t -> t.setName(subProcessModel.getModelClassName()));

        retValue.findFirst(MethodDeclaration.class, m -> m.getNameAsString().equals("bind"))
                .ifPresent(m -> m.setBody(bind(variableScope, subProcessNode, subProcessModel)));
        retValue.findFirst(MethodDeclaration.class, m -> m.getNameAsString().equals("createInstance"))
                .ifPresent(m -> m.setBody(createInstance(subProcessNode)));
        retValue.findFirst(MethodDeclaration.class, m -> m.getNameAsString().equals("unbind"))
                .ifPresent(m -> m.setBody(unbind(variableScope, subProcessNode, subProcessModel)));

        addFactoryMethodWithArgs(body, nodeVar, "subProcessFactory", retValue);
        addFactoryMethodWithArgs(body, nodeVar, "done");
    }

    private BlockStmt bind(VariableScope variableScope, SubProcessNode subProcessNode, ModelMetaData subProcessModel) {
        BlockStmt actionBody = new BlockStmt();
        actionBody.addStatement(subProcessModel.newInstance("model"));

        for (Map.Entry<String, String> e : subProcessNode.getInMappings().entrySet()) {
            actionBody.addStatement(makeAssignment(variableScope.findVariable(e.getKey())));
            actionBody.addStatement(subProcessModel.callSetter("model", e.getValue(), new NameExpr(e.getKey())));
        }

        actionBody.addStatement(new ReturnStmt(new NameExpr("model")));
        return actionBody;
    }

    private BlockStmt createInstance(SubProcessNode subProcessNode) {

        String processId = ProcessToExecModelGenerator.extractProcessId(subProcessNode.getProcessId());
        String factoryMethodName = String.format("create%sProcess", StringUtils.capitalize(processId));

        MethodCallExpr processSupplier = new MethodCallExpr(new NameExpr("app"), factoryMethodName);
        MethodCallExpr processInstanceSupplier = new MethodCallExpr(processSupplier, "createInstance").addArgument("model");

        return new BlockStmt().addStatement(new ReturnStmt(processInstanceSupplier));
    }

    private BlockStmt unbind(VariableScope variableScope, SubProcessNode subProcessNode, ModelMetaData subProcessModel) {
        BlockStmt stmts = new BlockStmt();

        for (Map.Entry<String, String> e : subProcessNode.getOutMappings().entrySet()) {
            stmts.addStatement(makeAssignmentFromModel(variableScope.findVariable(e.getKey())));
            stmts.addStatement(new MethodCallExpr()
                                       .setScope(new NameExpr("kcontext"))
                                       .setName("setVariable")
                                       .addArgument(new StringLiteralExpr(e.getValue()))
                                       .addArgument(e.getKey()));
        }

        return stmts;
    }
}
