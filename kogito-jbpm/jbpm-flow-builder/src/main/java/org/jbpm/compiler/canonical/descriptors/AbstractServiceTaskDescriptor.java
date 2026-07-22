/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jbpm.compiler.canonical.descriptors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.jbpm.compiler.canonical.NodeValidator;
import org.jbpm.workflow.core.impl.DataAssociation;
import org.jbpm.workflow.core.node.WorkItemNode;
import org.kie.kogito.internal.process.workitem.KogitoWorkItem;
import org.kie.kogito.internal.process.workitem.KogitoWorkItemHandler;
import org.kie.kogito.internal.process.workitem.KogitoWorkItemManager;
import org.kie.kogito.internal.process.workitem.WorkItemExecutionException;
import org.kie.kogito.internal.process.workitem.WorkItemTransition;
import org.kie.kogito.process.workitems.impl.DefaultKogitoWorkItemHandler;

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

import static com.github.javaparser.StaticJavaParser.parseClassOrInterfaceType;

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

    private BlockStmt tryStmt(BlockStmt blockStmt, Collection<Class<?>> exceptions) {
        if (exceptions.isEmpty()) {
            return blockStmt;
        } else {
            Expression newExceptionExpression = new ObjectCreationExpr()
                    .setType(WorkItemExecutionException.class)
                    .addArgument(new StringLiteralExpr("500"))
                    .addArgument(new NameExpr(EXCEPTION_NAME));

            BlockStmt throwBlockStmt = new BlockStmt().addStatement(new ThrowStmt(newExceptionExpression));
            CatchClause catchClause = new CatchClause(new Parameter(processException(exceptions), new SimpleName(EXCEPTION_NAME)), throwBlockStmt);
            NodeList<CatchClause> clauses = NodeList.nodeList(catchClause);
            return new BlockStmt().addStatement(new TryStmt().setTryBlock(blockStmt).setCatchClauses(clauses));
        }

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

    protected final ClassOrInterfaceDeclaration classDeclaration() {
        String unqualifiedName = StaticJavaParser.parseName(getName()).removeQualifier().asString();
        ClassOrInterfaceDeclaration cls = new ClassOrInterfaceDeclaration()
                .setName(unqualifiedName)
                .setModifiers(Modifier.Keyword.PUBLIC)
                .addExtendedType(DefaultKogitoWorkItemHandler.class.getCanonicalName());
        ClassOrInterfaceType serviceType = new ClassOrInterfaceType(null, interfaceName);

        String serviceName = "service";
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

        Type workItemTransitionType = parseClassOrInterfaceType(WorkItemTransition.class.getName());
        ClassOrInterfaceType optionalClass = parseClassOrInterfaceType(Optional.class.getName()).setTypeArguments(workItemTransitionType);

        // executeWorkItem method
        BlockStmt executeWorkItemBody = new BlockStmt();
        MethodCallExpr callService = new MethodCallExpr(new NameExpr("service"), operationName);
        this.handleParametersForServiceCall(executeWorkItemBody, callService);

        // execute work item handler
        getWorkItemOutput(callService).forEach(executeWorkItemBody::addStatement);

        Expression completeExpression = new ThisExpr();
        completeExpression = new FieldAccessExpr(completeExpression, "workItemLifeCycle");
        NodeList<Expression> arguments = new NodeList<>();
        arguments.add(new StringLiteralExpr("complete"));
        arguments.add(new MethodCallExpr(new NameExpr("workItem"), "getPhaseStatus"));
        arguments.add(new NameExpr(RESULT_NAME));

        completeExpression = new MethodCallExpr(completeExpression, "newTransition", arguments);
        Statement completeWorkItem = new ReturnStmt(new MethodCallExpr(new NameExpr(Optional.class.getName()), "of", NodeList.nodeList(completeExpression)));
        executeWorkItemBody.addStatement(completeWorkItem);

        executeWorkItemBody = tryStmt(executeWorkItemBody, getCompleteWorkItemExceptionTypes());

        MethodDeclaration executeWorkItem = new MethodDeclaration()
                .setModifiers(Modifier.Keyword.PUBLIC)
                .setType(optionalClass)
                .setName("activateWorkItemHandler")
                .setBody(executeWorkItemBody)
                .addParameter(KogitoWorkItemManager.class.getCanonicalName(), "workItemManager")
                .addParameter(KogitoWorkItemHandler.class.getCanonicalName(), "workItemHandler")
                .addParameter(KogitoWorkItem.class.getCanonicalName(), "workItem")
                .addParameter(WorkItemTransition.class.getCanonicalName(), "transition");

        // abortWorkItem method
        BlockStmt abortWorkItemBody = new BlockStmt();
        MethodDeclaration abortWorkItem = new MethodDeclaration()
                .setModifiers(Modifier.Keyword.PUBLIC)
                .setType(optionalClass)
                .setName("abortWorkItemHandler")
                .setBody(abortWorkItemBody)
                .addParameter(KogitoWorkItemManager.class.getCanonicalName(), "workItemManager")
                .addParameter(KogitoWorkItemHandler.class.getCanonicalName(), "workItemHandler")
                .addParameter(KogitoWorkItem.class.getCanonicalName(), "workItem")
                .addParameter(WorkItemTransition.class.getCanonicalName(), "transition");

        abortWorkItemBody.addStatement(new ReturnStmt(new MethodCallExpr(new NameExpr(Optional.class.getName()), "empty")));

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

    protected List<Statement> getWorkItemOutput(Expression callServiceExpression) {
        List<Statement> statements = new ArrayList<>();

        ClassOrInterfaceType stringType = new ClassOrInterfaceType(null, String.class.getCanonicalName());
        ClassOrInterfaceType objectType = new ClassOrInterfaceType(null, Object.class.getCanonicalName());
        ClassOrInterfaceType map = new ClassOrInterfaceType(null, Map.class.getCanonicalName()).setTypeArguments(stringType, objectType);
        VariableDeclarationExpr resultField = new VariableDeclarationExpr(new VariableDeclarator(map, RESULT_NAME));
        statements.add(new ExpressionStmt(resultField));

        Expression resultExpression = null;
        List<DataAssociation> outAssociations = workItemNode.getOutAssociations();
        if (outAssociations.isEmpty() || isEmptyResult()) {
            statements.add(new ExpressionStmt(callServiceExpression));
            resultExpression = new MethodCallExpr(new NameExpr("java.util.Collections"), "emptyMap");
            resultExpression = new AssignExpr(new NameExpr(RESULT_NAME), resultExpression, AssignExpr.Operator.ASSIGN);
            statements.add(new ExpressionStmt(resultExpression));
        } else {
            resultExpression = new MethodCallExpr(new NameExpr("java.util.Collections"), "singletonMap")
                    .addArgument(new StringLiteralExpr(outAssociations.get(0).getSources().get(0).getLabel()))
                    .addArgument(callServiceExpression);
            resultExpression = new AssignExpr(new NameExpr(RESULT_NAME), resultExpression, AssignExpr.Operator.ASSIGN);
            statements.add(new ExpressionStmt(resultExpression));
        }

        return statements;
    }
}
