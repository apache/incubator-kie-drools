/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.mvelcompiler.ast;

import java.lang.reflect.Type;
import java.util.Optional;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;

public class IfStmtT implements TypedExpression {

    private final TypedExpression typedCondition;
    private final TypedExpression typedThen;
    private final Optional<TypedExpression> typedElse;

    public IfStmtT(TypedExpression typedCondition, TypedExpression typedThen, Optional<TypedExpression> typedElse) {
        this.typedCondition = typedCondition;
        this.typedThen = typedThen;
        this.typedElse = typedElse;
    }

    @Override
    public Optional<Type> getType() {
        return Optional.empty();
    }

    @Override
    public Node toJavaExpression() {
        IfStmt stmt = new IfStmt();
        stmt.setCondition((Expression) typedCondition.toJavaExpression());
        stmt.setThenStmt((Statement) typedThen.toJavaExpression());
        typedElse.ifPresent(e -> stmt.setElseStmt((Statement) e.toJavaExpression()));
        return stmt;
    }
}
