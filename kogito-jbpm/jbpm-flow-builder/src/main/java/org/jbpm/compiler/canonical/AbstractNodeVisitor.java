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
package org.jbpm.compiler.canonical;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.jbpm.process.builder.action.ActionCompilerRegistry;
import org.jbpm.process.builder.transformation.DataTransformerCompilerRegistry;
import org.jbpm.process.core.ContextContainer;
import org.jbpm.process.core.context.variable.Mappable;
import org.jbpm.process.core.context.variable.Variable;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.process.instance.impl.actions.ProduceEventAction;
import org.jbpm.process.instance.impl.actions.SignalProcessInstanceAction;
import org.jbpm.ruleflow.core.Metadata;
import org.jbpm.ruleflow.core.factory.MappableNodeFactory;
import org.jbpm.workflow.core.impl.ConnectionImpl;
import org.jbpm.workflow.core.impl.DataAssociation;
import org.jbpm.workflow.core.impl.DataDefinition;
import org.jbpm.workflow.core.impl.DroolsConsequenceAction;
import org.jbpm.workflow.core.impl.ExtendedNodeImpl;
import org.jbpm.workflow.core.node.Assignment;
import org.jbpm.workflow.core.node.HumanTaskNode;
import org.jbpm.workflow.core.node.StartNode;
import org.jbpm.workflow.core.node.Transformation;
import org.kie.api.definition.process.Connection;
import org.kie.api.definition.process.Node;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.ClassExpr;
import com.github.javaparser.ast.expr.EnclosedExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.LambdaExpr;
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
import static java.util.Collections.singletonList;
import static org.drools.util.StringUtils.ucFirst;
import static org.jbpm.ruleflow.core.Metadata.CUSTOM_AUTO_START;
import static org.jbpm.ruleflow.core.Metadata.HIDDEN;
import static org.jbpm.ruleflow.core.factory.NodeFactory.METHOD_DONE;
import static org.jbpm.ruleflow.core.factory.NodeFactory.METHOD_NAME;
import static org.kie.kogito.internal.utils.ConversionUtils.sanitizeString;

public abstract class AbstractNodeVisitor<T extends Node> extends AbstractVisitor {

    protected abstract String getNodeKey();

    public void visitNode(T node, BlockStmt body, VariableScope variableScope, ProcessMetaData metadata) {
        visitNode(FACTORY_FIELD_NAME, node, body, variableScope, metadata);
        if (isAdHocNode(node) && !(node instanceof HumanTaskNode)) {
            metadata.addSignal(node.getName(), null);
        }
        if (isExtendedNode(node)) {
            ExtendedNodeImpl extendedNodeImpl = (ExtendedNodeImpl) node;
            addScript(extendedNodeImpl, body, ON_ACTION_SCRIPT_METHOD, ExtendedNodeImpl.EVENT_NODE_ENTER);
            addScript(extendedNodeImpl, body, ON_ACTION_SCRIPT_METHOD, ExtendedNodeImpl.EVENT_NODE_EXIT);
        }
    }

    private void addScript(ExtendedNodeImpl extendedNodeImpl, BlockStmt body, String factoryMethod, String actionType) {
        if (!extendedNodeImpl.hasActions(actionType)) {
            return;
        }
        List<DroolsConsequenceAction> scripts = extendedNodeImpl.getActions(actionType).stream()
                .filter(Predicate.not(Objects::isNull))
                .filter(DroolsConsequenceAction.class::isInstance)
                .map(DroolsConsequenceAction.class::cast)
                .filter(e -> e.getConsequence() != null && !e.getConsequence().isBlank())
                .toList();

        for (DroolsConsequenceAction script : scripts) {
            body.addStatement(getFactoryMethod(getNodeId((T) extendedNodeImpl), factoryMethod,
                    new StringLiteralExpr(actionType),
                    new StringLiteralExpr(script.getDialect()),
                    new StringLiteralExpr(sanitizeString(script.getConsequence())),
                    buildDroolsConsequenceAction(extendedNodeImpl, script.getDialect(), script.getConsequence())));
            ;
        }
    }

    private Expression buildDroolsConsequenceAction(ExtendedNodeImpl extendedNodeImpl, String dialect, String script) {
        if (script == null) {
            return new NullLiteralExpr();
        }
        return ActionCompilerRegistry.instance().find(dialect).buildAction(extendedNodeImpl, script);
    }

    private boolean isExtendedNode(T node) {
        return node instanceof ExtendedNodeImpl;
    }

    private boolean isAdHocNode(Node node) {
        return (node.getIncomingConnections() == null || node.getIncomingConnections().isEmpty())
                && !(node instanceof StartNode)
                && !Boolean.parseBoolean((String) node.getMetaData().get(CUSTOM_AUTO_START));
    }

    protected String getNodeId(T node) {
        return getNodeKey() + node.getId().toSanitizeString();
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
        return new MethodCallExpr(null, "org.jbpm.process.core.datatype.DataTypeResolver.fromClass",
                new NodeList<>(new ClassExpr(parseClassOrInterfaceType(type))));
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
        return toDataAssociation(toDataDef(sourceExpr), toDataDef(targetExpr), toAssignmentExpr(assignments), toTransformation(sourceExpr, singletonList(targetExpr), transformation));
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

    protected Expression toTransformation(List<DataDefinition> inputs, List<DataDefinition> outputs, Transformation transformation) {
        if (transformation == null) {
            return new NullLiteralExpr();
        }
        Expression lang = new StringLiteralExpr(transformation.getLanguage());
        Expression expression = new StringLiteralExpr(transformation.getExpression());
        Expression compiledExpression = DataTransformerCompilerRegistry.instance().find(transformation.getLanguage()).compile(inputs, outputs, transformation);
        ClassOrInterfaceType clazz = new ClassOrInterfaceType(null, "org.jbpm.workflow.core.node.Transformation");
        return new ObjectCreationExpr(null, clazz, NodeList.nodeList(lang, expression, compiledExpression));

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

        body.addStatement(getFactoryMethod(factoryField, "connection", getWorkflowElementConstructor(connection.getFrom().getId()),
                getWorkflowElementConstructor(connection.getTo().getId()),
                new StringLiteralExpr(getOrDefault(connection.getUniqueId(), ""))));
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

    protected ObjectCreationExpr buildProducerAction(Node node, ProcessMetaData metadata) {
        TriggerMetaData trigger = TriggerMetaData.of(node);
        return buildProducerAction(parseClassOrInterfaceType(ProduceEventAction.class.getCanonicalName()).setTypeArguments(NodeList.nodeList(parseClassOrInterfaceType(trigger.getDataType()))),
                trigger, metadata);

    }

    public static ObjectCreationExpr buildProducerAction(ClassOrInterfaceType actionClass, TriggerMetaData trigger, ProcessMetaData metadata) {
        metadata.addTrigger(trigger);
        return new ObjectCreationExpr(null, actionClass, NodeList.nodeList(
                new StringLiteralExpr(trigger.getName()), new StringLiteralExpr(trigger.getModelRef()),
                new LambdaExpr(NodeList.nodeList(), new NameExpr("producer_" + trigger.getOwnerId()))));
    }

    protected void visitCompensationScope(ContextContainer process, BlockStmt body) {
        visitCompensationScope(process, body, getNodeId((T) process));
    }
}
