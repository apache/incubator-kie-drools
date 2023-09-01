/**
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
package org.drools.mvelcompiler.ast;

import java.lang.reflect.Type;
import java.util.Optional;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.ForEachStmt;
import com.github.javaparser.ast.stmt.Statement;

public class ForEachStmtT implements TypedExpression {

    private TypedExpression variableDeclaratorExpr;
    private TypedExpression iterable;
    private TypedExpression body;

    public ForEachStmtT(TypedExpression variableDeclaratorExpr, TypedExpression iterable, TypedExpression body) {
        this.variableDeclaratorExpr = variableDeclaratorExpr;
        this.iterable = iterable;
        this.body = body;
    }

    @Override
    public Optional<Type> getType() {
        return Optional.empty();
    }

    @Override
    public Node toJavaExpression() {
        return new ForEachStmt((VariableDeclarationExpr) variableDeclaratorExpr.toJavaExpression(),
                               (Expression) iterable.toJavaExpression(),
                               (Statement) body.toJavaExpression());
    }
}
