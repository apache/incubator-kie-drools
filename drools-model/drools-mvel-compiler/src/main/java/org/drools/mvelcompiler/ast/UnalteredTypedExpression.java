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
import com.github.javaparser.ast.expr.SimpleName;

public class UnalteredTypedExpression implements TypedExpression {

    private final Node originalExpression;
    private Type type;

    public UnalteredTypedExpression(Node originalExpression) {
        this(originalExpression, null);
    }

    public UnalteredTypedExpression(Node originalExpression, Type type) {
        this.originalExpression = originalExpression;
        this.type = type;
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
