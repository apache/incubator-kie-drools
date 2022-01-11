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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.drools.core.common.InternalKnowledgeRuntime;
import org.jbpm.process.core.ContextContainer;
import org.jbpm.process.core.context.variable.Mappable;
import org.jbpm.process.core.context.variable.Variable;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.process.instance.InternalProcessRuntime;
import org.jbpm.process.instance.impl.actions.SignalProcessInstanceAction;
import org.jbpm.ruleflow.core.Metadata;
import org.jbpm.ruleflow.core.factory.MappableNodeFactory;
import org.jbpm.workflow.core.impl.ConnectionImpl;
import org.jbpm.workflow.core.impl.DataAssociation;
import org.jbpm.workflow.core.impl.DataDefinition;
import org.jbpm.workflow.core.node.Assignment;
import org.jbpm.workflow.core.node.HumanTaskNode;
import org.jbpm.workflow.core.node.StartNode;
import org.jbpm.workflow.core.node.Transformation;
import org.kie.api.definition.process.Connection;
import org.kie.api.definition.process.Node;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.EnclosedExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.type.UnknownType;
import com.github.javaparser.ast.type.WildcardType;

import static com.github.javaparser.StaticJavaParser.parseClassOrInterfaceType;
import static com.github.javaparser.StaticJavaParser.parseType;
import static org.drools.core.util.StringUtils.ucFirst;
import static org.jbpm.ruleflow.core.Metadata.CUSTOM_AUTO_START;
import static org.jbpm.ruleflow.core.Metadata.HIDDEN;
import static org.jbpm.ruleflow.core.factory.NodeFactory.METHOD_DONE;
import static org.jbpm.ruleflow.core.factory.NodeFactory.METHOD_NAME;

public abstract class AbstractNodeVisitor<T extends Node> extends AbstractVisitor {

    protected abstract String getNodeKey();

    public void visitNode(T node, BlockStmt body, VariableScope variableScope, ProcessMetaData metadata) {
        visitNode(FACTORY_FIELD_NAME, node, body, variableScope, metadata);
        if (isAdHocNode(node) && !(node instanceof HumanTaskNode)) {
            metadata.addSignal(node.getName(), null);
        }
    }

    private boolean isAdHocNode(Node node) {
        return (node.getIncomingConnections() == null || node.getIncomingConnections().isEmpty())
                && !(node instanceof StartNode)
                && !Boolean.parseBoolean((String) node.getMetaData().get(CUSTOM_AUTO_START));
    }

    protected String getNodeId(T node) {
        return getNodeKey() + node.getId();
    }

    public void visitNode(String factoryField, T node, BlockStmt body, VariableScope variableScope, ProcessMetaData metadata) {
    }

    protected MethodCallExpr getNameMethod(T node, String defaultName) {
        return getFactoryMethod(getNodeId(node), METHOD_NAME, new StringLiteralExpr(getOrDefault(node.getName(), defaultName)));
    }

    protected MethodCallExpr getDoneMethod(String object) {
        return getFactoryMethod(object, METHOD_DONE);
    }

    protected AssignExpr getAssignedFactoryMethod(String factoryField, Class<?> typeClass, String variableName, String methodName, Expression... args) {
        return getAssignedFactoryMethod(factoryField, typeClass, variableName, methodName, new WildcardType(), args);
    }

    public Expression buildDataResolver(String type) {
        MethodCallExpr currentThread = new MethodCallExpr(null, "java.lang.Thread.currentThread");
        MethodCallExpr classLoaderMethodCallExpr = new MethodCallExpr(currentThread, "getContextClassLoader");
        return new MethodCallExpr(null, "org.jbpm.process.core.datatype.DataTypeResolver.fromType",
                new NodeList<>(new StringLiteralExpr(type), classLoaderMethodCallExpr));
    }

    protected AssignExpr getAssignedFactoryMethod(String factoryField, Class<?> typeClass, String variableName, String methodName, Type parentType, Expression... args) {
        ClassOrInterfaceType type = new ClassOrInterfaceType(null, typeClass.getCanonicalName());

        type.setTypeArguments(parentType);

        MethodCallExpr variableMethod = new MethodCallExpr(new NameExpr(factoryField), methodName);

        for (Expression arg : args) {
            variableMethod.addArgument(arg);
        }

        return new AssignExpr(
                new VariableDeclarationExpr(type, variableName),
                variableMethod,
                AssignExpr.Operator.ASSIGN);

    }

    public static Statement makeAssignment(Variable v) {
        String name = v.getSanitizedName();
        return makeAssignment(name, v);
    }

    public static Statement makeAssignment(String targetLocalVariable, Variable processVariable) {
        ClassOrInterfaceType type = parseClassOrInterfaceType(processVariable.getType().getStringType());
        // `type` `name` = (`type`) `kcontext.getVariable
        AssignExpr assignExpr = new AssignExpr(
                new VariableDeclarationExpr(type, targetLocalVariable),
                new CastExpr(
                        type,
                        new MethodCallExpr(
                                new NameExpr(KCONTEXT_VAR),
                                "getVariable")
                                        .addArgument(new StringLiteralExpr(targetLocalVariable))),
                AssignExpr.Operator.ASSIGN);
        return new ExpressionStmt(assignExpr);
    }

    protected Statement makeAssignmentFromModel(Variable v) {
        return makeAssignmentFromModel(v, v.getSanitizedName());
    }

    protected Statement makeAssignmentFromModel(Variable v, String name) {
        ClassOrInterfaceType type = parseClassOrInterfaceType(v.getType().getStringType());
        // `type` `name` = (`type`) `model.get<Name>
        AssignExpr assignExpr = new AssignExpr(
                new VariableDeclarationExpr(type, name),
                new CastExpr(
                        type,
                        new MethodCallExpr(
                                new NameExpr("model"),
                                "get" + ucFirst(name))),
                AssignExpr.Operator.ASSIGN);

        return new ExpressionStmt(assignExpr);
    }

    protected void addNodeMappings(Mappable node, BlockStmt body, String variableName) {
        for (DataAssociation entry : node.getInAssociations()) {
            body.addStatement(getFactoryMethod(variableName, MappableNodeFactory.METHOD_IN_ASSOCIATION, buildDataAssociationExpression(entry)));
        }
        for (DataAssociation entry : node.getOutAssociations()) {
            body.addStatement(getFactoryMethod(variableName, MappableNodeFactory.METHOD_OUT_ASSOCIATION, buildDataAssociationExpression(entry)));
        }
    }

    protected Expression buildDataAssociationsExpression(List<DataAssociation> dataAssociations) {
        NodeList<Expression> expressions = NodeList.nodeList(dataAssociations.stream().map(this::buildDataAssociationExpression).collect(Collectors.toList()));
        return new MethodCallExpr(null, "java.util.Arrays.asList", NodeList.nodeList(expressions));
    }

    protected Expression buildDataAssociationExpression(DataAssociation dataAssociation) {
        List<DataDefinition> sourceExpr = dataAssociation.getSources();
        DataDefinition targetExpr = dataAssociation.getTarget();
        Transformation transformation = dataAssociation.getTransformation();
        List<Assignment> assignments = dataAssociation.getAssignments();
        return toDataAssociation(toDataDef(sourceExpr), toDataDef(targetExpr), toAssignmentExpr(assignments), toTransformation(transformation));
    }

    private Expression toAssignmentExpr(List<Assignment> assignments) {
        if (assignments == null || assignments.isEmpty()) {
            return new NullLiteralExpr();
        }

        List<Expression> expressions = new ArrayList<>();
        for (Assignment assignment : assignments) {
            Expression lang = assignment.getDialect() != null ? new StringLiteralExpr(assignment.getDialect()) : new NullLiteralExpr();
            Expression from = toDataDef(assignment.getFrom());
            Expression to = toDataDef(assignment.getTo());
            ClassOrInterfaceType clazz = new ClassOrInterfaceType(null, "org.jbpm.workflow.core.node.Assignment");
            expressions.add(new ObjectCreationExpr(null, clazz, NodeList.nodeList(lang, from, to)));
        }

        return new MethodCallExpr(null, "java.util.Arrays.asList", NodeList.nodeList(expressions));
    }

    protected Expression toTransformation(Transformation transformation) {
        if (transformation == null) {
            return new NullLiteralExpr();
        }
        Expression lang = new StringLiteralExpr(transformation.getLanguage());
        Expression expression = new StringLiteralExpr(transformation.getExpression());
        Expression source = new StringLiteralExpr(transformation.getSource());
        ClassOrInterfaceType clazz = new ClassOrInterfaceType(null, "org.jbpm.workflow.core.node.Transformation");
        return new ObjectCreationExpr(null, clazz, NodeList.nodeList(lang, expression, source));

    }

    protected Expression toDataAssociation(Expression sourceExprs, Expression target, Expression transformation, Expression assignments) {
        ClassOrInterfaceType clazz = new ClassOrInterfaceType(null, "org.jbpm.workflow.core.impl.DataAssociation");
        return new ObjectCreationExpr(null, clazz, NodeList.nodeList(sourceExprs, target, transformation, assignments));
    }

    private Expression toDataDef(List<DataDefinition> sourceExpr) {
        List<Expression> expressions = sourceExpr.stream().map(this::toDataDef).collect(Collectors.toList());
        return new MethodCallExpr(null, "java.util.Arrays.asList", NodeList.nodeList(expressions));
    }

    private Expression toDataDef(DataDefinition sourceExpr) {
        if (sourceExpr == null) {
            return new NullLiteralExpr();
        }
        Expression id = new StringLiteralExpr(sourceExpr.getId());
        Expression label = new StringLiteralExpr(escape(sourceExpr.getLabel()));
        Expression type = new StringLiteralExpr(sourceExpr.getType());
        Expression expression = sourceExpr.getExpression() != null ? new StringLiteralExpr(escape(sourceExpr.getExpression())) : new NullLiteralExpr();
        ClassOrInterfaceType clazz = new ClassOrInterfaceType(null, "org.jbpm.workflow.core.impl.DataDefinition");
        return new ObjectCreationExpr(null, clazz, NodeList.nodeList(id, label, type, expression));
    }

    private String escape(String escape) {
        return escape.replace("\"", "\\\"");
    }

    protected String extractVariableFromExpression(String variableExpression) {
        if (variableExpression.startsWith("#{")) {
            return variableExpression.substring(2, variableExpression.indexOf('.'));
        }
        return variableExpression;
    }

    protected void visitConnections(String factoryField, Node[] nodes, BlockStmt body) {
        List<Connection> connections = new ArrayList<>();
        for (Node node : nodes) {
            for (List<Connection> connectionList : node.getIncomingConnections().values()) {
                connections.addAll(connectionList);
            }
        }
        for (Connection connection : connections) {
            visitConnection(factoryField, connection, body);
        }
    }

    protected void visitConnection(String factoryField, Connection connection, BlockStmt body) {
        // if the connection is a hidden one (compensations), don't dump
        Object hidden = ((ConnectionImpl) connection).getMetaData(HIDDEN);
        if (hidden != null && ((Boolean) hidden)) {
            return;
        }

        body.addStatement(getFactoryMethod(factoryField, "connection", new LongLiteralExpr(connection.getFrom().getId()),
                new LongLiteralExpr(connection.getTo().getId()),
                new StringLiteralExpr(getOrDefault((String) connection.getMetaData().get("UniqueId"), ""))));
    }

    protected static LambdaExpr createLambdaExpr(String consequence, VariableScope scope) {
        BlockStmt conditionBody = new BlockStmt();
        List<Variable> variables = scope.getVariables();
        variables.stream()
                .map(ActionNodeVisitor::makeAssignment)
                .forEach(conditionBody::addStatement);

        conditionBody.addStatement(new ReturnStmt(new EnclosedExpr(new NameExpr(consequence))));

        return new LambdaExpr(
                new Parameter(new UnknownType(), KCONTEXT_VAR), // (kcontext) ->
                conditionBody);
    }

    public static ObjectCreationExpr buildAction(String signalName, String variable, String inputVariable, String scope) {
        return new ObjectCreationExpr(null,
                parseClassOrInterfaceType(SignalProcessInstanceAction.class.getCanonicalName()),
                new NodeList<>(new StringLiteralExpr(signalName), variable != null ? new StringLiteralExpr(variable.replace("\"", "\\\""))
                        : new CastExpr(
                                parseClassOrInterfaceType(String.class.getCanonicalName()), new NullLiteralExpr()),
                        inputVariable != null ? new StringLiteralExpr(inputVariable) : new NullLiteralExpr(),
                        scope != null ? new StringLiteralExpr(scope)
                                : new CastExpr(
                                        parseClassOrInterfaceType(String.class.getCanonicalName()), new NullLiteralExpr())));
    }

    public static LambdaExpr buildCompensationLambdaExpr(String compensationRef) {
        BlockStmt actionBody = new BlockStmt();
        MethodCallExpr getProcessInstance = new MethodCallExpr(new NameExpr(KCONTEXT_VAR), "getProcessInstance");
        MethodCallExpr signalEvent = new MethodCallExpr(getProcessInstance, "signalEvent")
                .addArgument(new StringLiteralExpr(Metadata.EVENT_TYPE_COMPENSATION))
                .addArgument(new StringLiteralExpr(compensationRef));
        actionBody.addStatement(signalEvent);
        return new LambdaExpr(
                new Parameter(new UnknownType(), KCONTEXT_VAR), // (kcontext) ->
                actionBody);
    }

    public static LambdaExpr buildLambdaExpr(Node node, ProcessMetaData metadata) {
        TriggerMetaData triggerMetaData = TriggerMetaData.of(node);
        metadata.addTrigger(triggerMetaData);
        NameExpr kExpr = new NameExpr(KCONTEXT_VAR);
        BlockStmt actionBody = new BlockStmt();
        final String objectName = "object";
        final String runtimeName = "runtime";
        final String processName = "process";
        final String piName = "pi";
        NameExpr object = new NameExpr(objectName);
        NameExpr runtime = new NameExpr(runtimeName);
        NameExpr pi = new NameExpr(piName);
        Type objectType = new ClassOrInterfaceType(null, triggerMetaData.getDataType());
        Type processRuntime = parseClassOrInterfaceType(InternalProcessRuntime.class.getCanonicalName());
        Type kieRuntime = parseClassOrInterfaceType(InternalKnowledgeRuntime.class.getCanonicalName());
        Type processInstance = parseClassOrInterfaceType(KogitoProcessInstance.class.getCanonicalName());
        AssignExpr objectExpr = new AssignExpr(
                new VariableDeclarationExpr(objectType, objectName),
                new CastExpr(objectType, new MethodCallExpr(kExpr, "getVariable").addArgument(new StringLiteralExpr(
                        triggerMetaData.getModelRef()))),
                AssignExpr.Operator.ASSIGN);
        AssignExpr runtimeExpr = new AssignExpr(
                new VariableDeclarationExpr(kieRuntime, runtimeName),
                new CastExpr(kieRuntime, new MethodCallExpr(kExpr, "getKieRuntime")),
                AssignExpr.Operator.ASSIGN);
        AssignExpr processExpr = new AssignExpr(
                new VariableDeclarationExpr(processRuntime, processName),
                new CastExpr(processRuntime, new MethodCallExpr(runtime, "getProcessRuntime")),
                AssignExpr.Operator.ASSIGN);
        AssignExpr processInstanceAssignment = new AssignExpr(
                new VariableDeclarationExpr(processInstance, piName),
                new CastExpr(parseType(KogitoProcessInstance.class.getCanonicalName()), new MethodCallExpr(new NameExpr("kcontext"), "getProcessInstance")),
                AssignExpr.Operator.ASSIGN);
        // add onMessage listener call
        MethodCallExpr listenerMethodCall = new MethodCallExpr(
                new MethodCallExpr(new NameExpr(processName), "getProcessEventSupport"), "fireOnMessage")
                        .addArgument(pi)
                        .addArgument(new MethodCallExpr(kExpr, "getNodeInstance"))
                        .addArgument(runtime)
                        .addArgument(new StringLiteralExpr(triggerMetaData.getName())).addArgument(object);
        // add producer call
        MethodCallExpr producerMethodCall = new MethodCallExpr(new NameExpr("producer_" + node.getId()), "produce")
                .addArgument(pi).addArgument(object);
        actionBody.addStatement(objectExpr);
        actionBody.addStatement(runtimeExpr);
        actionBody.addStatement(processInstanceAssignment);
        actionBody.addStatement(processExpr);
        actionBody.addStatement(listenerMethodCall);
        actionBody.addStatement(producerMethodCall);
        return new LambdaExpr(new Parameter(new UnknownType(), KCONTEXT_VAR), actionBody);
    }

    protected void visitCompensationScope(ContextContainer process, BlockStmt body) {
        visitCompensationScope(process, body, getNodeId((T) process));
    }
}
