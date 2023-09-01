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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.ObjectCreationExpr;

public class ObjectCreationExpressionT implements TypedExpression {

    private final Class<?> type;
    private List<TypedExpression> constructorArguments;

    public ObjectCreationExpressionT(List<TypedExpression> constructorArguments, Class<?> type) {
        this.constructorArguments = constructorArguments;
        this.type = type;
    }

    @Override
    public Optional<Type> getType() {
        return Optional.of(type);
    }

    @Override
    public Node toJavaExpression() {
        ObjectCreationExpr objectCreationExpr = new ObjectCreationExpr();
        objectCreationExpr.setType(type.getCanonicalName());
        List<Expression> arguments = this.constructorArguments.stream()
                .map(typedExpression -> (Expression)typedExpression.toJavaExpression())
                .collect(Collectors.toList());
        objectCreationExpr.setArguments(NodeList.nodeList(arguments));
        return objectCreationExpr;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ObjectCreationExpressionT{");
        sb.append("arguments=").append(constructorArguments);
        sb.append("type=").append(type);
        sb.append('}');
        return sb.toString();
    }
}
