/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.compiler.canonical.descriptors;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

import org.jbpm.compiler.canonical.NodeValidator;
import org.jbpm.workflow.core.node.DataAssociation;
import org.jbpm.workflow.core.node.WorkItemNode;
import org.kie.kogito.internal.process.runtime.KogitoWorkItem;
import org.kie.kogito.internal.process.runtime.KogitoWorkItemHandler;
import org.kie.kogito.internal.process.runtime.KogitoWorkItemManager;
import org.kie.kogito.process.workitem.WorkItemExecutionException;
import org.kie.kogito.process.workitems.impl.expr.ExpressionWorkItemResolver;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
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

public abstract class AbstractServiceTaskDescriptor implements TaskDescriptor {

    static final String PARAM_INTERFACE = "Interface";
    static final String PARAM_OPERATION = "Operation";
    private static final String RESULT_NAME = "result";
    private static final String EXCEPTION_NAME = "ex";
    protected final String interfaceName;
    protected final String operationName;

    protected final WorkItemNode workItemNode;

    protected AbstractServiceTaskDescriptor(final WorkItemNode workItemNode) {
        interfaceName = (String) workItemNode.getWork().getParameter(PARAM_INTERFACE);
        operationName = (String) workItemNode.getWork().getParameter(PARAM_OPERATION);
        NodeValidator.of("workItemNode", workItemNode.getName())
                .notEmpty("interfaceName", interfaceName)
                .notEmpty("operationName", operationName)
                .validate();
        this.workItemNode = workItemNode;
    }

    protected abstract void handleParametersForServiceCall(final BlockStmt executeWorkItemBody, final MethodCallExpr callService);

    protected Expression handleServiceCallResult(final BlockStmt executeWorkItemBody, final MethodCallExpr callService) {
        return callService;
    }

    protected abstract Collection<Class<?>> getCompleteWorkItemExceptionTypes();

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

    protected boolean isEmptyResult() {
        return false;
    }

    protected MethodCallExpr completeWorkItem(BlockStmt executeWorkItemBody, MethodCallExpr callService, Collection<Class<?>> exceptions) {
        Expression results;

        List<DataAssociation> outAssociations = workItemNode.getOutAssociations();
        if (outAssociations.isEmpty() || isEmptyResult()) {
            executeWorkItemBody.addStatement(tryStmt(callService, exceptions));
            results = new MethodCallExpr(new NameExpr("java.util.Collections"), "emptyMap");
        } else {
            VariableDeclarationExpr resultField = new VariableDeclarationExpr()
                    .addVariable(new VariableDeclarator(
                            new ClassOrInterfaceType(null, Object.class.getCanonicalName()),
                            RESULT_NAME));
            final Expression callServiceResult = this.handleServiceCallResult(executeWorkItemBody, callService);
            executeWorkItemBody.addStatement(resultField);
            executeWorkItemBody
                    .addStatement(
                            tryStmt(
                                    new AssignExpr(
                                            new NameExpr(RESULT_NAME),
                                            callServiceResult,
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

    protected final ClassOrInterfaceDeclaration classDeclaration() {
        String unqualifiedName = StaticJavaParser.parseName(getName()).removeQualifier().asString();
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
                .addConstructor(Modifier.Keyword.PUBLIC)
                .setBody(
                        new BlockStmt()
                                .addStatement(new MethodCallExpr("this", new ObjectCreationExpr().setType(serviceType))));
        cls
                .addConstructor(Modifier.Keyword.PUBLIC)
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
        this.handleParametersForServiceCall(executeWorkItemBody, callService);

        MethodCallExpr completeWorkItem = completeWorkItem(
                executeWorkItemBody,
                callService,
                getCompleteWorkItemExceptionTypes());
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
                .setBody(new BlockStmt().addStatement(new ReturnStmt(new StringLiteralExpr(getName()))));
        cls.addMember(executeWorkItem)
                .addMember(abortWorkItem)
                .addMember(getName);

        return cls;
    }

    public static Object processWorkItemValue(String exprLang, Object object, String paramName, Class<? extends ExpressionWorkItemResolver> clazz, Predicate<String> isExpression) {
        return object instanceof CharSequence && isExpression.test(object.toString())
                ? new WorkItemParamResolverSupplier(clazz, () -> new StringLiteralExpr(exprLang), () -> new StringLiteralExpr(object.toString()), () -> new StringLiteralExpr(paramName))
                : object;
    }

}
