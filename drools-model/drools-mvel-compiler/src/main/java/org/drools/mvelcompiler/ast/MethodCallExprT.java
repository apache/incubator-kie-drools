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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import org.drools.mvelcompiler.util.BigDecimalArgumentCoercion;

import static com.github.javaparser.ast.NodeList.nodeList;
import static org.drools.mvelcompiler.util.CoercionUtils.PUT_CALL;
import static org.drools.mvelcompiler.util.CoercionUtils.coerceMapValueToString;

public class MethodCallExprT implements TypedExpression {

    private final String name;
    private final Optional<TypedExpression> scope;
    private final List<TypedExpression> arguments;
    private List<Class<?>> actualMethodArgumentType;
    private final Optional<Type> type;

    public MethodCallExprT(String name, Optional<TypedExpression> scope,
                           List<TypedExpression> arguments,
                           List<Class<?>> actualMethodArgumentType,
                           Optional<Type> type) {
        this.name = name;
        this.scope = scope;
        this.arguments = arguments;
        this.actualMethodArgumentType = actualMethodArgumentType;
        this.type = type;
    }

    @Override
    public Optional<Type> getType() {
        return type;
    }

    @Override
    public Optional<Type> getScopeType() {
        return scope.flatMap(TypedExpression::getScopeType);
    }

    @Override
    public Node toJavaExpression() {
        Node scopeE = scope.map(TypedExpression::toJavaExpression).orElse(null);

        List<Expression> methodArguments;
        // MVEL forces a to string on each String value in map
        if (PUT_CALL.equals(name) && arguments.size() == 2) {
            methodArguments = coercedMapArguments();
        } else {
            methodArguments = toJavaExpressionArgument();
        }

        return new MethodCallExpr((Expression) scopeE, name, nodeList(methodArguments));
    }

    private List<Expression> toJavaExpressionArgument() {
        List<Expression> list = new ArrayList<>();
        List<TypedExpression> typedArguments = this.arguments;
        for (int i = 0; i < typedArguments.size(); i++) {
            Expression expression = toJavaArgument(typedArguments, i);
            list.add(expression);
        }
        return list;
    }

    private Expression toJavaArgument(List<TypedExpression> typedExpressions, int i) {
        TypedExpression a = typedExpressions.get(i);

        Optional<Class<?>> optionalActualType = Optional.empty();
        if(actualMethodArgumentType.size() == typedExpressions.size()) {
            optionalActualType = Optional.ofNullable(actualMethodArgumentType.get(i));
        }

        if(optionalActualType.isPresent() && a.getType().isPresent()) {
            Type argumentTypeOrig = a.getType().get();
            if (argumentTypeOrig instanceof Class) {
                Class<?> argumentType = (Class<?>) argumentTypeOrig;
                Class<?> actualType = optionalActualType.get();
                if(argumentType != actualType) {
                    return new BigDecimalArgumentCoercion().coercedArgument(argumentType, actualType, (Expression) a.toJavaExpression());
                }
            }
        }

        return (Expression) a.toJavaExpression();
    }

    private List<Expression> coercedMapArguments() {
        Expression key = (Expression) arguments.get(0).toJavaExpression();

        Expression originalValue = (Expression) arguments.get(1).toJavaExpression();
        Optional<Type> scopeType = scope.flatMap(TypedExpression::getType);
        Expression coercedValue = coerceMapValueToString (scopeType, originalValue);

        return Arrays.asList(key, coercedValue);
    }

    @Override
    public String toString() {
        return "MethodCallExprT{" +
                "name='" + name + '\'' +
                ", scope=" + scope +
                ", arguments=" + arguments +
                ", type=" + type +
                '}';
    }
}
