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

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.jbpm.process.core.ParameterDefinition;
import org.jbpm.process.core.Work;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.ruleflow.core.factory.WorkItemNodeFactory;
import org.jbpm.workflow.core.node.DataAssociation;
import org.jbpm.workflow.core.node.WorkItemNode;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static org.jbpm.ruleflow.core.factory.WorkItemNodeFactory.METHOD_WORK_NAME;
import static org.jbpm.ruleflow.core.factory.WorkItemNodeFactory.METHOD_WORK_PARAMETER;

public class WorkItemNodeVisitor<T extends WorkItemNode> extends AbstractNodeVisitor<T> {

    private final ClassLoader contextClassLoader;

    public WorkItemNodeVisitor(ClassLoader contextClassLoader) {
        this.contextClassLoader = contextClassLoader;
    }

    @Override
    protected String getNodeKey() {
        return "workItemNode";
    }

    @Override
    public void visitNode(String factoryField, T node, BlockStmt body, VariableScope variableScope, ProcessMetaData metadata) {
        Work work = node.getWork();
        String workName = workItemName(node, metadata);
        body.addStatement(getAssignedFactoryMethod(factoryField, WorkItemNodeFactory.class, getNodeId(node), getNodeKey(), new LongLiteralExpr(node.getId())))
        .addStatement(getNameMethod(node, work.getName()))
        .addStatement(getFactoryMethod(getNodeId(node), METHOD_WORK_NAME, new StringLiteralExpr(workName)));

        addWorkItemParameters(work, body, getNodeId(node));
        addNodeMappings(node, body, getNodeId(node));

        body.addStatement(getDoneMethod(getNodeId(node)));

        visitMetaData(node.getMetaData(), body, getNodeId(node));

        metadata.getWorkItems().add(workName);
    }

    protected void addWorkItemParameters(Work work, BlockStmt body, String variableName) {
        for (Entry<String, Object> entry : work.getParameters().entrySet()) {
            if (entry.getValue() == null) {
                continue; // interfaceImplementationRef ?
            }
            body.addStatement(getFactoryMethod(variableName, METHOD_WORK_PARAMETER, new StringLiteralExpr(entry.getKey()), new StringLiteralExpr(entry.getValue().toString())));
        }
    }

    protected String workItemName(WorkItemNode workItemNode, ProcessMetaData metadata) {
        String workName = workItemNode.getWork().getName();

        if (workName.equals("Service Task")) {
            String interfaceName = (String) workItemNode.getWork().getParameter("Interface");
            String operationName = (String) workItemNode.getWork().getParameter("Operation");
            String type = (String) workItemNode.getWork().getParameter("ParameterType");

            NodeValidator.of(getNodeKey(), workItemNode.getName())
                    .notEmpty("interfaceName", interfaceName)
                    .notEmpty("operationName", operationName)
                    .validate();

            workName = interfaceName + "." + operationName;

            Map<String, String> parameters = null;
            if (type != null) {
                if (isDefaultParameterType(type)) {
                    type = inferParameterType(workItemNode.getName(), interfaceName, operationName, type);
                }

                parameters = Collections.singletonMap("Parameter", type);
            } else {
                parameters = new LinkedHashMap<>();

                for (ParameterDefinition def : workItemNode.getWork().getParameterDefinitions()) {
                    parameters.put(def.getName(), def.getType().getStringType());
                }
            }

            CompilationUnit handlerClass = generateHandlerClassForService(interfaceName, operationName, parameters, workItemNode.getOutAssociations());

            metadata.getGeneratedHandlers().put(workName, handlerClass);
        }

        return workName;
    }

    // assume 1 single arg as above
    private String inferParameterType(String nodeName, String interfaceName, String operationName, String defaultType) {
        try {
            Class<?> i = contextClassLoader.loadClass(interfaceName);
            for (Method m : i.getMethods()) {
                if (m.getName().equals(operationName) && m.getParameterCount() == 1) {
                    return m.getParameterTypes()[0].getCanonicalName();
                }
            }
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(MessageFormat.format("Invalid work item \"{0}\": class not found for interfaceName \"{1}\"", nodeName, interfaceName));
        }
        throw new IllegalArgumentException(MessageFormat.format("Invalid work item \"{0}\": could not find a method called \"{1}\" in class \"{2}\"", nodeName, operationName, interfaceName));
    }

    private boolean isDefaultParameterType(String type) {
        return type.equals("java.lang.Object") || type.equals("Object");
    }

    protected CompilationUnit generateHandlerClassForService(String interfaceName, String operation, Map<String, String> parameters, List<DataAssociation> outAssociations) {
        CompilationUnit compilationUnit = new CompilationUnit("org.kie.kogito.handlers");

        compilationUnit.getTypes().add(classDeclaration(interfaceName, operation, parameters, outAssociations));

        return compilationUnit;
    }

    public ClassOrInterfaceDeclaration classDeclaration(String interfaceName, String operation, Map<String, String> parameters, List<DataAssociation> outAssociations) {
        ClassOrInterfaceDeclaration cls = new ClassOrInterfaceDeclaration()
                .setName(interfaceName.substring(interfaceName.lastIndexOf(".") + 1) + "_" + operation + "Handler")
                .setModifiers(Modifier.Keyword.PUBLIC)
                .addImplementedType(WorkItemHandler.class.getCanonicalName());
        ClassOrInterfaceType serviceType = new ClassOrInterfaceType(null, interfaceName);
        FieldDeclaration serviceField = new FieldDeclaration()
                .addVariable(new VariableDeclarator(serviceType, "service"));
        cls.addMember(serviceField);

        // executeWorkItem method
        BlockStmt executeWorkItemBody = new BlockStmt();
        MethodDeclaration executeWorkItem = new MethodDeclaration()
                .setModifiers(Modifier.Keyword.PUBLIC)
                .setType(void.class)
                .setName("executeWorkItem")
                .setBody(executeWorkItemBody)
                .addParameter(WorkItem.class.getCanonicalName(), "workItem")
                .addParameter(WorkItemManager.class.getCanonicalName(), "workItemManager");


        MethodCallExpr callService = new MethodCallExpr(new NameExpr("service"), operation);


        for (Entry<String, String> paramEntry : parameters.entrySet()) {
            MethodCallExpr getParamMethod = new MethodCallExpr(new NameExpr("workItem"), "getParameter").addArgument(new StringLiteralExpr(paramEntry.getKey()));
            callService.addArgument(new CastExpr(new ClassOrInterfaceType(null, paramEntry.getValue()), getParamMethod));
        }
        Expression results = null;
        if (outAssociations.isEmpty()) {

            executeWorkItemBody.addStatement(callService);
            results = new NullLiteralExpr();

        } else {
            VariableDeclarationExpr resultField = new VariableDeclarationExpr()
                    .addVariable(new VariableDeclarator(new ClassOrInterfaceType(null, Object.class.getCanonicalName()), "result", callService));

            executeWorkItemBody.addStatement(resultField);

            results = new MethodCallExpr(new NameExpr("java.util.Collections"), "singletonMap")
                    .addArgument(new StringLiteralExpr(outAssociations.get(0).getSources().get(0)))
                    .addArgument(new NameExpr("result"));
        }

        MethodCallExpr completeWorkItem = new MethodCallExpr(new NameExpr("workItemManager"), "completeWorkItem")
                .addArgument(new MethodCallExpr(new NameExpr("workItem"), "getId"))
                .addArgument(results);

        executeWorkItemBody.addStatement(completeWorkItem);

        // abortWorkItem method
        BlockStmt abortWorkItemBody = new BlockStmt();
        MethodDeclaration abortWorkItem = new MethodDeclaration()
                .setModifiers(Modifier.Keyword.PUBLIC)
                .setType(void.class)
                .setName("abortWorkItem")
                .setBody(abortWorkItemBody)
                .addParameter(WorkItem.class.getCanonicalName(), "workItem")
                .addParameter(WorkItemManager.class.getCanonicalName(), "workItemManager");


        // getName method
        MethodDeclaration getName = new MethodDeclaration()
                .setModifiers(Modifier.Keyword.PUBLIC)
                .setType(String.class)
                .setName("getName")
                .setBody(new BlockStmt().addStatement(new ReturnStmt(new StringLiteralExpr(interfaceName + "." + operation))));
        cls.addMember(executeWorkItem)
                .addMember(abortWorkItem)
                .addMember(getName);

        return cls;
    }
}
