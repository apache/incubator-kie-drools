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
package org.jbpm.process.builder.action;

import java.util.HashSet;
import java.util.Set;

import org.jbpm.compiler.canonical.AbstractNodeVisitor;
import org.jbpm.process.core.context.variable.Variable;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.workflow.core.impl.NodeImpl;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.AssignExpr.Operator;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;

public class JavaActionCompiler implements ActionCompiler {

    @Override
    public String[] dialects() {
        return new String[] { "java" };
    }

    @Override
    public boolean accept(String dialect) {
        return dialect.toLowerCase().contains("java");
    }

    @Override
    public Expression buildAction(NodeImpl nodeImpl, String script) {
        BlockStmt newDroolsConsequenceActionExpression = new BlockStmt();
        newDroolsConsequenceActionExpression = StaticJavaParser.parseBlock("{" + script + "}");
        Set<NameExpr> identifiers = new HashSet<>(newDroolsConsequenceActionExpression.findAll(NameExpr.class));
        for (NameExpr identifier : identifiers) {
            VariableScope scope = (VariableScope) nodeImpl.resolveContext(VariableScope.VARIABLE_SCOPE, identifier.getNameAsString());
            if (scope == null) {
                continue;
            }
            Variable var = scope.findVariable(identifier.getNameAsString());
            if (var == null) {
                continue;
            }
            Type type = StaticJavaParser.parseType(var.getType().getStringType());
            VariableDeclarationExpr target = new VariableDeclarationExpr(type, var.getName());
            Expression source = new MethodCallExpr(new NameExpr(AbstractNodeVisitor.KCONTEXT_VAR), "getVariable", NodeList.nodeList(new StringLiteralExpr(var.getName())));
            source = new CastExpr(type, source);
            AssignExpr assign = new AssignExpr(target, source, Operator.ASSIGN);
            newDroolsConsequenceActionExpression.addStatement(0, assign);
        }
        ClassOrInterfaceType type = StaticJavaParser.parseClassOrInterfaceType(org.kie.kogito.internal.process.runtime.KogitoProcessContext.class.getName());
        return new LambdaExpr(NodeList.nodeList(new Parameter(type, AbstractNodeVisitor.KCONTEXT_VAR)), newDroolsConsequenceActionExpression, true);
    }

}
