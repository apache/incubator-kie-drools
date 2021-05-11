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
import java.math.BigDecimal;
import java.util.Optional;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;

import static com.github.javaparser.ast.NodeList.nodeList;

public class BigDecimalArithmeticExprT implements TypedExpression {

    private final String name;
    private final TypedExpression argument;
    private final TypedExpression scope;
    private final Type type = BigDecimal.class;

    public static String toBigDecimalMethod(BinaryExpr.Operator operator) {
        switch (operator) {
            case PLUS: // +
                return "add";
            case MINUS: // -
                return "subtract";
            case MULTIPLY: // *
                return "multiply";
            case DIVIDE: // /
                return "divide";
            case REMAINDER: // %
                return "remainder";
        }
        throw new RuntimeException("Unknown operator");
    }

    public static String toBigDecimalMethod(AssignExpr.Operator operator) {
        switch (operator) {
            case PLUS: // +=
                return "add";
            case MINUS: // -=
                return "subtract";
            case MULTIPLY: // *=
                return "multiply";
            case DIVIDE: // /=
                return "divide";
            case ASSIGN: // =
                return "valueOf";
        }
        throw new RuntimeException("Unknown operator");
    }

    public BigDecimalArithmeticExprT(String bigDecimalMethod,
                                     TypedExpression scope,
                                     TypedExpression argument) {
        this.name = bigDecimalMethod;
        this.scope = scope;
        this.argument = argument;
    }

    @Override
    public Optional<Type> getType() {
        return Optional.of(type);
    }

    @Override
    public Node toJavaExpression() {

        return new MethodCallExpr((Expression) scope.toJavaExpression(),
                                  name,
                                  nodeList((Expression) argument.toJavaExpression()));
    }

    @Override
    public String toString() {
        return "BigDecimalExprT{" +
                "name='" + name + '\'' +
                ", argument=" + argument +
                ", type=" + type +
                '}';
    }
}
