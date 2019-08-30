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

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.drools.core.util.StringUtils;
import org.jbpm.process.core.context.variable.Variable;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.ruleflow.core.factory.SubProcessNodeFactory;
import org.jbpm.workflow.core.WorkflowProcess;
import org.jbpm.workflow.core.node.SubProcessNode;
import org.kie.api.definition.process.Node;

import static com.github.javaparser.StaticJavaParser.parse;

public class LambdaSubProcessNodeVisitor extends AbstractVisitor {

    private final Map<String, ModelMetaData> processToModel;

    public LambdaSubProcessNodeVisitor(Map<String, ModelMetaData> processToModel) {
        this.processToModel = processToModel;
    }

    @Override
    public void visitNode(String factoryField, Node node, BlockStmt body, VariableScope variableScope, ProcessMetaData metadata) {

        InputStream resourceAsStream = this.getClass().getResourceAsStream("/class-templates/SubProcessFactoryTemplate.java");
        Expression retValue = parse(resourceAsStream).findFirst(Expression.class).get();

        SubProcessNode subProcessNode = (SubProcessNode) node;
        String subProcessId = subProcessNode.getProcessId();
        String nodeVar = "subProcessNode" + node.getId();

        addFactoryMethodWithArgsWithAssignment(factoryField, body, SubProcessNodeFactory.class, nodeVar, "subProcessNode", new LongLiteralExpr(subProcessNode.getId()));
        addFactoryMethodWithArgs(body, nodeVar, "name", new StringLiteralExpr(getOrDefault(subProcessNode.getName(), "Call Activity")));
        addFactoryMethodWithArgs(body, nodeVar, "processId", new StringLiteralExpr(subProcessId));
        addFactoryMethodWithArgs(body, nodeVar, "processName", new StringLiteralExpr(getOrDefault(subProcessNode.getProcessName(), "")));
        addFactoryMethodWithArgs(body, nodeVar, "waitForCompletion", new BooleanLiteralExpr(subProcessNode.isWaitForCompletion()));
        addFactoryMethodWithArgs(body, nodeVar, "independent", new BooleanLiteralExpr(subProcessNode.isIndependent()));

        Map<String, String> inputTypes = (Map<String, String>) subProcessNode.getMetaData("BPMN.InputTypes");
        Map<String, String> outputTypes = (Map<String, String>) subProcessNode.getMetaData("BPMN.OutputTypes");

        String subProcessModelClassName = ProcessToExecModelGenerator.extractModelClassName(subProcessId);
        ModelMetaData subProcessModel = new ModelMetaData(subProcessId, metadata.getPackageName(), subProcessModelClassName, WorkflowProcess.PRIVATE_VISIBILITY, VariableDeclarations.of(inputTypes));

        retValue.findAll(ClassOrInterfaceType.class)
                .stream()
                .filter(t -> t.getNameAsString().equals("$Type$"))
                .forEach(t -> t.setName(subProcessModelClassName));

        retValue.findFirst(MethodDeclaration.class, m -> m.getNameAsString().equals("bind"))
                .ifPresent(m -> m.setBody(bind(variableScope, subProcessNode, subProcessModel)));
        retValue.findFirst(MethodDeclaration.class, m -> m.getNameAsString().equals("createInstance"))
                .ifPresent(m -> m.setBody(createInstance(subProcessNode, metadata)));
        retValue.findFirst(MethodDeclaration.class, m -> m.getNameAsString().equals("unbind"))
                .ifPresent(m -> m.setBody(unbind(variableScope, subProcessNode, subProcessModel)));

        addFactoryMethodWithArgs(body, nodeVar, "subProcessFactory", retValue);
        
        visitMetaData(subProcessNode.getMetaData(), body, "subProcessNode" + node.getId());
        
        addFactoryMethodWithArgs(body, nodeVar, "done");
    }

    private BlockStmt bind(VariableScope variableScope, SubProcessNode subProcessNode, ModelMetaData subProcessModel) {
        BlockStmt actionBody = new BlockStmt();
        actionBody.addStatement(subProcessModel.newInstance("model"));

        for (Map.Entry<String, String> e : subProcessNode.getInMappings().entrySet()) {
            Variable v = variableScope.findVariable(extractVariableFromExpression(e.getValue()));
            if (v != null) {
                actionBody.addStatement(makeAssignment(v));
                actionBody.addStatement(subProcessModel.callSetter("model", e.getKey(), e.getValue()));
            }
        }

        actionBody.addStatement(new ReturnStmt(new NameExpr("model")));
        return actionBody;
    }

    private BlockStmt createInstance(SubProcessNode subProcessNode, ProcessMetaData metadata) {

        String processId = ProcessToExecModelGenerator.extractProcessId(subProcessNode.getProcessId());
        String processFielName = "process" + processId;

        MethodCallExpr processInstanceSupplier = new MethodCallExpr(new NameExpr(processFielName), "createInstance").addArgument("model");

        metadata.getSubProcesses().put(processId, subProcessNode.getProcessId());
        
        return new BlockStmt().addStatement(new ReturnStmt(processInstanceSupplier));
    }

    private BlockStmt unbind(VariableScope variableScope, SubProcessNode subProcessNode, ModelMetaData subProcessModel) {
        BlockStmt stmts = new BlockStmt();

        for (Map.Entry<String, String> e : subProcessNode.getOutMappings().entrySet()) {
            stmts.addStatement(makeAssignmentFromModel(variableScope.findVariable(e.getValue()), e.getKey()));
            stmts.addStatement(new MethodCallExpr()
                                       .setScope(new NameExpr("kcontext"))
                                       .setName("setVariable")
                                       .addArgument(new StringLiteralExpr(e.getValue()))
                                       .addArgument(e.getKey()));
        }

        return stmts;
    }
    
}
