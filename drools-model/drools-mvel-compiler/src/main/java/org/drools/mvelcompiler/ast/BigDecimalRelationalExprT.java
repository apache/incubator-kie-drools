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
import java.math.BigDecimal;
import java.util.Optional;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;

import static com.github.javaparser.ast.NodeList.nodeList;

public class BigDecimalRelationalExprT implements TypedExpression {

    private final BinaryExpr.Operator operator;
    private final TypedExpression argument;
    private final TypedExpression scope;
    private final Type type = BigDecimal.class;

    public BigDecimalRelationalExprT(BinaryExpr.Operator operator,
                                     TypedExpression scope,
                                     TypedExpression argument) {
        this.operator = operator;
        this.scope = scope;
        this.argument = argument;
    }

    @Override
    public Optional<Type> getType() {
        return Optional.of(type);
    }

    @Override
    public Node toJavaExpression() {
        MethodCallExpr methodCallExpr = new MethodCallExpr((Expression) scope.toJavaExpression(), "compareTo",
                                                           nodeList((Expression) argument.toJavaExpression()));
        return new BinaryExpr(methodCallExpr, new IntegerLiteralExpr("0"), operator);
    }

    @Override
    public String toString() {
        return "BigDecimalRelationalExprT{" +
                "operator=" + operator +
                ", argument=" + argument +
                ", scope=" + scope +
                ", type=" + type +
                '}';
    }
}
