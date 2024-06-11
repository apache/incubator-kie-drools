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

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.EnclosedExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import org.drools.mvelcompiler.util.BigDecimalArgumentCoercion;
import org.drools.util.MethodUtils;

import static com.github.javaparser.ast.NodeList.nodeList;

public class FieldToAccessorTExpr implements TypedExpression {

    private final TypedExpression scope;
    private final Type type;
    private final Method accessor;

    private final List<TypedExpression> arguments;

    public FieldToAccessorTExpr(TypedExpression scope, Method accessor, List<TypedExpression> arguments) {
        this.scope = scope;
        this.accessor = accessor;
        this.type = parseType(accessor);
        this.arguments = arguments;
    }

    private Type parseType(Method accessor) {
        if (accessor.getParameterTypes().length == 1) {
            return accessor.getParameterTypes()[0]; // setter
        } else {
            return accessor.getGenericReturnType(); // getter
        }
    }

    public FieldToAccessorTExpr(TypedExpression scope, Method accessor) {
        this(scope, accessor, Collections.emptyList());
    }

    @Override
    public Optional<Type> getType() {
        return Optional.of(type);
    }

    @Override
    public Optional<Type> getScopeType() {
        return scope.getType();
    }

    @Override
    public Node toJavaExpression() {
        List<Expression> expressionArguments = this.arguments.stream()
                .map(this::convertToStringIfNeeded)
                .collect(Collectors.toList());

        Optional<Type> rhsType = this.arguments.stream()
                .findFirst()
                .flatMap(TypedExpression::getType);

        // Right is BigDecimal, left is other, coerce
        if(rhsType.isPresent() && rhsType.get().equals(BigDecimal.class) && !type.equals(BigDecimal.class)) {
            Expression coercedExpression = new BigDecimalArgumentCoercion().coercedArgument(BigDecimal.class, (Class<?>)type, expressionArguments.get(0));
            return new MethodCallExpr((Expression) scope.toJavaExpression(), accessor.getName(), nodeList(coercedExpression));
        } else {
            return new MethodCallExpr((Expression) scope.toJavaExpression(), accessor.getName(), nodeList(expressionArguments));
        }
    }

    private Expression convertToStringIfNeeded(TypedExpression argumentExpression) {
        boolean argumentCanBeToStringed = argumentExpression.getType().filter(this::typeCanBeToStringed).isPresent();
        boolean fieldIsString = String.class.equals(this.type);
        if (fieldIsString && argumentCanBeToStringed) {
            return new MethodCallExpr(new EnclosedExpr((Expression) argumentExpression.toJavaExpression()), "toString");
        } else {
            return (Expression) argumentExpression.toJavaExpression();
        }
    }

    private Boolean typeCanBeToStringed(Type t) {
        if(t instanceof ParameterizedType) {
            return false;
        }

        Class<?> clazz = (Class<?>) t;
        boolean isNotPrimitive = !clazz.isPrimitive();
        boolean isNotString = !t.equals(String.class); // no need to toString a String
        boolean isNotNull = !t.equals(MethodUtils.NullType.class);

        return isNotString && isNotPrimitive && isNotNull;
    }

    @Override
    public String toString() {
        return "FieldToAccessorTExpr{" +
                " scope=" + scope.toString() +
                ", type=" + type +
                ", accessor=" + accessor +
                '}';
    }
}