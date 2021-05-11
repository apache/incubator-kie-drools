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
import com.github.javaparser.ast.expr.ObjectCreationExpr;

public class ObjectCreationExpressionT implements TypedExpression {

    private final Class<?> type;

    ObjectCreationExpr originalExpression;

    public ObjectCreationExpressionT(ObjectCreationExpr originalExpression, Class<?> type) {
        this.originalExpression = originalExpression;
        this.type = type;
    }

    @Override
    public Optional<Type> getType() {
        return Optional.of(type);
    }

    @Override
    public Node toJavaExpression() {
        return originalExpression;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ObjectCreationExpressionT{");
        sb.append("originalExpression=").append(originalExpression);
        sb.append("type=").append(type);
        sb.append('}');
        return sb.toString();
    }
}
