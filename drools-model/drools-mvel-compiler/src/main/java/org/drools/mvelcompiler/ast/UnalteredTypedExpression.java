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
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.UnaryExpr;

public class UnalteredTypedExpression implements TypedExpression {

    private final Node originalExpression;
    private Type type;

    public UnalteredTypedExpression(Node originalExpression) {
        this(originalExpression, null);
    }

    public UnalteredTypedExpression(Node originalExpression, Type type) {
        this.originalExpression = originalExpression;
        this.type = type;

        if (originalExpression instanceof UnaryExpr) {
            Expression innerExpression = ((UnaryExpr) originalExpression).getExpression();
            if (innerExpression instanceof IntegerLiteralExpr) {
                this.type = int.class;
            } else if (innerExpression instanceof LongLiteralExpr) {
                this.type = long.class;
            }
        }
    }

    @Override
    public Optional<Type> getType() {
        return Optional.ofNullable(type);
    }

    @Override
    public Node toJavaExpression() {
        return (originalExpression instanceof SimpleName)
                ? originalExpression.getParentNode().orElseThrow(() -> new IllegalStateException("Expression has no parent node!"))
                : originalExpression;
    }

    @Override
    public String toString() {
        return "UnalteredTypedExpression{originalExpression=" + originalExpression + '}';
    }
}
