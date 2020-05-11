/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.drools.core.util.StringUtils;
import org.jbpm.process.core.context.variable.Mappable;
import org.jbpm.process.core.context.variable.Variable;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.workflow.core.impl.ConnectionImpl;
import org.kie.api.definition.process.Connection;
import org.kie.api.definition.process.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import static com.github.javaparser.StaticJavaParser.parseClassOrInterfaceType;
import static org.jbpm.ruleflow.core.Metadata.HIDDEN;
import static org.jbpm.ruleflow.core.factory.MappableNodeFactory.METHOD_IN_MAPPING;
import static org.jbpm.ruleflow.core.factory.MappableNodeFactory.METHOD_OUT_MAPPING;
import static org.jbpm.ruleflow.core.factory.NodeFactory.METHOD_DONE;
import static org.jbpm.ruleflow.core.factory.NodeFactory.METHOD_NAME;

public abstract class AbstractNodeVisitor<T extends Node> extends AbstractVisitor {

    protected static final String EVENT_TYPE_SIGNAL = "signal";
    protected static final String EVENT_TYPE_MESSAGE = "message";

    protected abstract String getNodeKey();

    public void visitNode(T node, BlockStmt body, VariableScope variableScope, ProcessMetaData metadata) {
        visitNode(FACTORY_FIELD_NAME, node, body, variableScope, metadata);
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
        ClassOrInterfaceType type = new ClassOrInterfaceType(null, typeClass.getCanonicalName());

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
                                "get" + StringUtils.capitalize(name))),
                AssignExpr.Operator.ASSIGN);

        return new ExpressionStmt(assignExpr);
    }

    protected void addNodeMappings(Mappable node, BlockStmt body, String variableName) {
        for (Entry<String, String> entry : node.getInMappings().entrySet()) {
            body.addStatement(getFactoryMethod(variableName, METHOD_IN_MAPPING, new StringLiteralExpr(entry.getKey()), new StringLiteralExpr(entry.getValue())));
        }
        for (Entry<String, String> entry : node.getOutMappings().entrySet()) {
            body.addStatement(getFactoryMethod(variableName, METHOD_OUT_MAPPING, new StringLiteralExpr(entry.getKey()), new StringLiteralExpr(entry.getValue())));
        }
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
}
