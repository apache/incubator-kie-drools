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
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.ruleflow.core.factory.SubProcessNodeFactory;
import org.jbpm.workflow.core.impl.DataDefinition;
import org.jbpm.workflow.core.node.SubProcessNode;
import org.jbpm.workflow.instance.impl.NodeInstanceImpl;
import org.kie.kogito.internal.process.runtime.KogitoWorkflowProcess;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

import static com.github.javaparser.StaticJavaParser.parse;
import static com.github.javaparser.ast.NodeList.nodeList;
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

        Map<String, String> inputTypes = node.getIoSpecification().getInputTypes();

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
        addNodeMappings(node, body, getNodeId(node));
        visitMetaData(node.getMetaData(), body, getNodeId(node));
        body.addStatement(getDoneMethod(getNodeId(node)));
    }

    private BlockStmt bind(VariableScope variableScope, SubProcessNode subProcessNode, ModelMetaData subProcessModel) {
        BlockStmt actionBody = new BlockStmt();
        actionBody.addStatement(subProcessModel.newInstance("model"));

        // process the inputs of the task
        ClassOrInterfaceType nodeInstanceType = new ClassOrInterfaceType(null, NodeInstanceImpl.class.getCanonicalName());
        ClassOrInterfaceType objectType = new ClassOrInterfaceType(null, Object.class.getCanonicalName());
        ClassOrInterfaceType stringType = new ClassOrInterfaceType(null, String.class.getCanonicalName());
        ClassOrInterfaceType type = new ClassOrInterfaceType(null, Map.class.getCanonicalName()).setTypeArguments(nodeList(stringType, objectType));
        VariableDeclarationExpr expr = new VariableDeclarationExpr(type, "inputs");

        BlockStmt lambdaBody = new BlockStmt();
        MethodCallExpr getVariableExpr = new MethodCallExpr(new NameExpr(KCONTEXT_VAR), "getVariable").addArgument(new NameExpr("name"));
        Expression getNodeInstance = new CastExpr(nodeInstanceType, new MethodCallExpr(new NameExpr(KCONTEXT_VAR), "getNodeInstance"));
        lambdaBody.addStatement(new ReturnStmt(getVariableExpr));
        Parameter varName = new Parameter(stringType, "name");
        LambdaExpr sourceResolverExpr = new LambdaExpr(nodeList(varName), lambdaBody);

        MethodCallExpr processInputsExpr = new MethodCallExpr(null, "org.jbpm.workflow.core.impl.NodeIoHelper.processInputs", nodeList(getNodeInstance, sourceResolverExpr));
        AssignExpr inputs = new AssignExpr(expr, processInputsExpr, AssignExpr.Operator.ASSIGN);
        actionBody.addStatement(inputs);

        // do the actual assignments
        for (DataDefinition inputDefinition : subProcessNode.getIoSpecification().getDataInput().values()) {
            // remove multiinstance data. It does not belong to this model it is just for calculations with
            // data associations
            String collectionInput = (String) subProcessNode.getMetaData().get("MICollectionInput");
            if (collectionInput != null && collectionInput.equals(inputDefinition.getLabel())) {
                continue;
            }
            DataDefinition multiInstance = subProcessNode.getMultiInstanceSpecification().getInputDataItem();
            if (multiInstance != null && multiInstance.getLabel().equals(inputDefinition.getLabel())) {
                continue;
            }

            Expression getValueExpr = new MethodCallExpr(new NameExpr("inputs"), "get", nodeList(new StringLiteralExpr(inputDefinition.getLabel())));
            actionBody.addStatement(subProcessModel.callSetter("model", inputDefinition.getLabel(), getValueExpr));
        }

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
        BlockStmt actionBody = new BlockStmt();

        // process the outputs of the task
        ClassOrInterfaceType nodeInstanceType = new ClassOrInterfaceType(null, NodeInstanceImpl.class.getCanonicalName());
        ClassOrInterfaceType objectType = new ClassOrInterfaceType(null, Object.class.getCanonicalName());
        ClassOrInterfaceType stringType = new ClassOrInterfaceType(null, String.class.getCanonicalName());
        ClassOrInterfaceType type = new ClassOrInterfaceType(null, Map.class.getCanonicalName()).setTypeArguments(nodeList(stringType, objectType));
        ClassOrInterfaceType hashMapType = new ClassOrInterfaceType(null, HashMap.class.getCanonicalName()).setTypeArguments(nodeList(stringType, objectType));

        // we get the outputs from model
        VariableDeclarationExpr expr = new VariableDeclarationExpr(type, "outputs");
        actionBody.addStatement(new AssignExpr(expr, new ObjectCreationExpr(null, hashMapType, nodeList()), AssignExpr.Operator.ASSIGN));
        // do the actual assignments
        for (DataDefinition outputDefinition : subProcessNode.getIoSpecification().getDataOutput().values()) {
            // remove multiinstance data. It does not belong to this model it is just for calculations with
            // data associations
            String collectionOutput = (String) subProcessNode.getMetaData().get("MICollectionOutput");
            if (collectionOutput != null && collectionOutput.equals(outputDefinition.getLabel())) {
                continue;
            }
            DataDefinition multiInstance = subProcessNode.getMultiInstanceSpecification().getOutputDataItem();
            if (multiInstance != null && multiInstance.getLabel().equals(outputDefinition.getLabel())) {
                continue;
            }

            Expression getValueExpr = new MethodCallExpr(new NameExpr("model"), "get" + ucFirst(outputDefinition.getLabel()));
            Expression setValueExpr = new MethodCallExpr(new NameExpr("outputs"), "put", nodeList(new StringLiteralExpr(outputDefinition.getLabel()), getValueExpr));
            actionBody.addStatement(setValueExpr);
        }

        // source resolver
        BlockStmt lambdaSourceBody = new BlockStmt();
        Expression getSourceVariableExpr = new MethodCallExpr(new NameExpr("outputs"), "get", nodeList(new NameExpr("name")));
        lambdaSourceBody.addStatement(new ReturnStmt(getSourceVariableExpr));
        LambdaExpr sourceResolverExpr = new LambdaExpr(nodeList(new Parameter(stringType, "name")), lambdaSourceBody);

        // target resolver
        BlockStmt lambdaTargetBody = new BlockStmt();
        Expression getTargetVariableExpr = new MethodCallExpr(new NameExpr(KCONTEXT_VAR), "getVariable").addArgument(new NameExpr("name"));
        lambdaTargetBody.addStatement(new ReturnStmt(getTargetVariableExpr));
        LambdaExpr targetResolverExpr = new LambdaExpr(nodeList(new Parameter(stringType, "name")), lambdaTargetBody);

        Expression getNodeInstance = new CastExpr(nodeInstanceType, new MethodCallExpr(new NameExpr(KCONTEXT_VAR), "getNodeInstance"));
        MethodCallExpr processOutputsExpr = new MethodCallExpr(null, "org.jbpm.workflow.core.impl.NodeIoHelper.processOutputs", nodeList(getNodeInstance, sourceResolverExpr, targetResolverExpr));

        actionBody.addStatement(processOutputsExpr);

        return actionBody;
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
