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

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;

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
        if(accessor.getParameterTypes().length == 1) {
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
    public Node toJavaExpression() {
        List<Expression> expressionArguments = this.arguments.stream()
                .map(a -> (Expression) (a.toJavaExpression()))
                .collect(Collectors.toList());

        return new MethodCallExpr((Expression) scope.toJavaExpression(), accessor.getName(), nodeList(expressionArguments));
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