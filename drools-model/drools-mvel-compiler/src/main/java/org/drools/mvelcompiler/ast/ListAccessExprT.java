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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;

public class ListAccessExprT implements TypedExpression {

    private final TypedExpression name;
    private final Expression index;
    private final Type type;

    public ListAccessExprT(TypedExpression name, Expression index, Type type) {
        this.name = name;
        this.index = index;
        this.type = type;
    }

    @Override
    public Optional<Type> getType() {
        return Optional.of(type);
    }

    public Optional<Type> getElementType() {
        return getType().filter(ParameterizedType.class::isInstance)
                        .map(ParameterizedType.class::cast)
                        .map(parameterizedType -> {
                            Class<?> rawType = (Class<?>) parameterizedType.getRawType();
                            if (List.class.isAssignableFrom(rawType)) {
                                return parameterizedType.getActualTypeArguments()[0];
                            } else if (Map.class.isAssignableFrom(rawType)) {
                                return parameterizedType.getActualTypeArguments()[1];
                            } else {
                                throw new IllegalStateException("ListAccessExprT is not applicable to " + rawType);
                            }
                        });
    }

    @Override
    public Node toJavaExpression() {
        return new MethodCallExpr((Expression) name.toJavaExpression(), "get", NodeList.nodeList(index));
    }
}
