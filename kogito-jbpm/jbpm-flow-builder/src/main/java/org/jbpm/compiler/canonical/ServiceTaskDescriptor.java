/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.process.core.ParameterDefinition;
import org.jbpm.workflow.core.node.DataAssociation;
import org.jbpm.workflow.core.node.WorkItemNode;
import org.kie.kogito.internal.process.runtime.KogitoWorkItem;
import org.kie.kogito.internal.process.runtime.KogitoWorkItemHandler;
import org.kie.kogito.internal.process.runtime.KogitoWorkItemManager;
import org.kie.kogito.process.workitem.WorkItemExecutionException;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Modifier.Keyword;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.ThrowStmt;
import com.github.javaparser.ast.stmt.TryStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.type.UnionType;

public class ServiceTaskDescriptor {

    private static final String RESULT_NAME = "result";
    private static final String EXCEPTION_NAME = "ex";

    private final ClassLoader contextClassLoader;
    private final String interfaceName;
    private final String operationName;
    private final Map<String, String> parameters;
    private final WorkItemNode workItemNode;
    private final String mangledName;
    private Class<?> cls;

    ServiceTaskDescriptor(WorkItemNode workItemNode, ClassLoader contextClassLoader) {
        this.workItemNode = workItemNode;
        interfaceName = (String) workItemNode.getWork().getParameter("Interface");
        operationName = (String) workItemNode.getWork().getParameter("Operation");
        this.contextClassLoader = contextClassLoader;

        NodeValidator.of("workItemNode", workItemNode.getName())
                .notEmpty("interfaceName", interfaceName)
                .notEmpty("operationName", operationName)
                .validate();

        parameters = serviceTaskParameters();

        mangledName = mangledHandlerName(interfaceName, operationName, String.valueOf(workItemNode.getId()));
    }

    public String mangledName() {
        return mangledName;
    }

    private Map<String, String> serviceTaskParameters() {
        String type = (String) workItemNode.getWork().getParameter("ParameterType");
        Map<String, String> parameters = null;
        if (type != null) {
            if (isDefaultParameterType(type)) {
                type = inferParameterType();
            }

            parameters = Collections.singletonMap("Parameter", type);
        } else {
            parameters = new LinkedHashMap<>();

            for (ParameterDefinition def : workItemNode.getWork().getParameterDefinitions()) {
                parameters.put(def.getName(), def.getType().getStringType());
            }
        }
        return parameters;
    }

    // assume 1 single arg as above
    private String inferParameterType() {
        loadClass();
        for (Method m : cls.getMethods()) {
            if (m.getName().equals(operationName) && m.getParameterCount() == 1) {
                return m.getParameterTypes()[0].getCanonicalName();
            }
        }
        throw new IllegalArgumentException(
                MessageFormat
                        .format(
                                "Invalid work item \"{0}\": could not find a method called \"{1}\" in class \"{2}\"",
                                workItemNode.getName(),
                                operationName,
                                interfaceName));
    }

    private void loadClass() {
        if (cls != null) {
            return;
        }
        try {
            cls = contextClassLoader.loadClass(interfaceName);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(
                    MessageFormat
                            .format(
                                    "Invalid work item \"{0}\": class not found for interfaceName \"{1}\"",
                                    workItemNode.getName(),
                                    interfaceName));
        }
    }

    private boolean isDefaultParameterType(String type) {
        return type.equals("java.lang.Object") || type.equals("Object");
    }

    private String mangledHandlerName(String interfaceName, String operationName, String nodeName) {
        return String.format("%s_%s_%s_Handler", interfaceName, operationName, nodeName);
    }

    public CompilationUnit generateHandlerClassForService() {
        CompilationUnit compilationUnit = new CompilationUnit("org.kie.kogito.handlers");
        compilationUnit.getTypes().add(classDeclaration());
        compilationUnit.addImport(WorkItemExecutionException.class);
        return compilationUnit;
    }

    public ClassOrInterfaceDeclaration classDeclaration() {
        String unqualifiedName = StaticJavaParser.parseName(mangledName).removeQualifier().asString();
        ClassOrInterfaceDeclaration cls = new ClassOrInterfaceDeclaration()
                .setName(unqualifiedName)
                .setModifiers(Modifier.Keyword.PUBLIC)
                .addImplementedType(KogitoWorkItemHandler.class.getCanonicalName());
        ClassOrInterfaceType serviceType = new ClassOrInterfaceType(null, interfaceName);

        final String serviceName = "service";
        FieldDeclaration serviceField = new FieldDeclaration()
                .addVariable(new VariableDeclarator(serviceType, serviceName));
        cls.addMember(serviceField);
        cls
                .addConstructor(Keyword.PUBLIC)
                .setBody(
                        new BlockStmt()
                                .addStatement(new MethodCallExpr("this", new ObjectCreationExpr().setType(serviceType))));
        cls
                .addConstructor(Keyword.PUBLIC)
                .addParameter(serviceType, serviceName)
                .setBody(
                        new BlockStmt()
                                .addStatement(
                                        new AssignExpr(
                                                new FieldAccessExpr(
                                                        new ThisExpr(),
                                                        serviceName),
                                                new NameExpr(serviceName),
                                                AssignExpr.Operator.ASSIGN)));

        // executeWorkItem method
        BlockStmt executeWorkItemBody = new BlockStmt();
        MethodDeclaration executeWorkItem = new MethodDeclaration()
                .setModifiers(Modifier.Keyword.PUBLIC)
                .setType(void.class)
                .setName("executeWorkItem")
                .setBody(executeWorkItemBody)
                .addParameter(KogitoWorkItem.class.getCanonicalName(), "workItem")
                .addParameter(KogitoWorkItemManager.class.getCanonicalName(), "workItemManager");

        MethodCallExpr callService = new MethodCallExpr(new NameExpr("service"), operationName);

        for (Map.Entry<String, String> paramEntry : parameters.entrySet()) {
            MethodCallExpr getParamMethod = new MethodCallExpr(new NameExpr("workItem"), "getParameter").addArgument(new StringLiteralExpr(paramEntry.getKey()));
            callService.addArgument(new CastExpr(new ClassOrInterfaceType(null, paramEntry.getValue()), getParamMethod));
        }

        MethodCallExpr completeWorkItem = completeWorkItem(
                executeWorkItemBody,
                callService,
                Arrays.asList(getOperationMethod().getExceptionTypes()));
        executeWorkItemBody.addStatement(completeWorkItem);

        // abortWorkItem method
        BlockStmt abortWorkItemBody = new BlockStmt();
        MethodDeclaration abortWorkItem = new MethodDeclaration()
                .setModifiers(Modifier.Keyword.PUBLIC)
                .setType(void.class)
                .setName("abortWorkItem")
                .setBody(abortWorkItemBody)
                .addParameter(KogitoWorkItem.class.getCanonicalName(), "workItem")
                .addParameter(KogitoWorkItemManager.class.getCanonicalName(), "workItemManager");

        // getName method
        MethodDeclaration getName = new MethodDeclaration()
                .setModifiers(Modifier.Keyword.PUBLIC)
                .setType(String.class)
                .setName("getName")
                .setBody(new BlockStmt().addStatement(new ReturnStmt(new StringLiteralExpr(mangledName))));
        cls.addMember(executeWorkItem)
                .addMember(abortWorkItem)
                .addMember(getName);

        return cls;
    }

    private Method getOperationMethod() {
        loadClass();
        try {
            return ReflectionUtils.getMethod(contextClassLoader, cls, operationName, parameters.values());
        } catch (ReflectiveOperationException ex) {
            throw new IllegalArgumentException(
                    MessageFormat
                            .format(
                                    "Invalid work item \"{0}\": could not find a method called \"{1}\" in class \"{2}\" with proper arguments \"{3}\", error \"{4}\"",
                                    workItemNode.getName(),
                                    operationName,
                                    interfaceName,
                                    parameters,
                                    ex));
        }

    }

    private Statement tryStmt(Expression expr, Collection<Class<?>> exceptions) {
        return exceptions.isEmpty() ? new ExpressionStmt(expr)
                : new TryStmt(
                        new BlockStmt().addStatement(expr),
                        NodeList
                                .nodeList(
                                        new CatchClause(
                                                new Parameter(processException(exceptions), new SimpleName(EXCEPTION_NAME)),
                                                new BlockStmt()
                                                        .addStatement(
                                                                new ThrowStmt(
                                                                        new ObjectCreationExpr()
                                                                                .setType(WorkItemExecutionException.class)
                                                                                .addArgument(new StringLiteralExpr("500"))
                                                                                .addArgument(new NameExpr(EXCEPTION_NAME)))))),
                        null);
    }

    private Type processException(Collection<Class<?>> exceptions) {
        return new UnionType(
                exceptions
                        .stream()
                        .map(Class::getName)
                        .map(StaticJavaParser::parseClassOrInterfaceType)
                        .collect(NodeList.toNodeList()));
    }

    private MethodCallExpr
            completeWorkItem(BlockStmt executeWorkItemBody, MethodCallExpr callService, Collection<Class<?>> exceptions) {
        Expression results = null;

        List<DataAssociation> outAssociations = workItemNode.getOutAssociations();
        if (outAssociations.isEmpty()) {
            executeWorkItemBody.addStatement(tryStmt(callService, exceptions));
            results = new NullLiteralExpr();
        } else {
            VariableDeclarationExpr resultField = new VariableDeclarationExpr()
                    .addVariable(
                            new VariableDeclarator(
                                    new ClassOrInterfaceType(null, Object.class.getCanonicalName()),
                                    RESULT_NAME));
            executeWorkItemBody.addStatement(resultField);
            executeWorkItemBody
                    .addStatement(
                            tryStmt(
                                    new AssignExpr(
                                            new NameExpr(RESULT_NAME),
                                            callService,
                                            AssignExpr.Operator.ASSIGN),
                                    exceptions));
            results = new MethodCallExpr(new NameExpr("java.util.Collections"), "singletonMap")
                    .addArgument(new StringLiteralExpr(outAssociations.get(0).getSources().get(0)))
                    .addArgument(new NameExpr(RESULT_NAME));
        }

        return new MethodCallExpr(new NameExpr("workItemManager"), "completeWorkItem")
                .addArgument(new MethodCallExpr(new NameExpr("workItem"), "getStringId"))
                .addArgument(results);
    }

}
