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
package org.jbpm.compiler.canonical.builtin;

import java.util.HashSet;
import java.util.Set;

import org.jbpm.compiler.canonical.AbstractNodeVisitor;
import org.jbpm.process.core.ContextResolver;
import org.jbpm.process.core.context.variable.Variable;
import org.jbpm.process.core.context.variable.VariableScope;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.UnknownType;

public class JavaConstraintEvaluatorBuilder implements ReturnValueEvaluatorBuilder {

    @Override
    public boolean accept(String dialect) {
        return dialect.toLowerCase().contains("java");
    }

    @Override
    public Expression build(ContextResolver resolver, String expression, Class<?> type, String rootName) {
        BlockStmt actionBody = new BlockStmt();
        LambdaExpr lambda = new LambdaExpr(
                new Parameter(new UnknownType(), KCONTEXT_VAR), // (kcontext) ->
                actionBody);

        BlockStmt blockStmt = StaticJavaParser.parseBlock("{" + expression + "}");
        Set<NameExpr> identifiers = new HashSet<>(blockStmt.findAll(NameExpr.class));

        for (NameExpr v : identifiers) {
            VariableScope variableScope = (VariableScope) resolver.resolveContext(VariableScope.VARIABLE_SCOPE, v.getNameAsString());
            if (variableScope == null) {
                continue;
            }
            Variable variable = variableScope.findVariable(v.getNameAsString());
            actionBody.addStatement(AbstractNodeVisitor.makeAssignment(variable));
        }

        blockStmt.getStatements().forEach(actionBody::addStatement);

        return lambda;
    }

}
