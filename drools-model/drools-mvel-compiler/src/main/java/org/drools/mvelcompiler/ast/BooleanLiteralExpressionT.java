/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
import com.github.javaparser.ast.expr.BooleanLiteralExpr;

public class BooleanLiteralExpressionT implements TypedExpression {

    private final BooleanLiteralExpr booleanLiteralExpr;

    public BooleanLiteralExpressionT(BooleanLiteralExpr booleanLiteralExpr) {
        this.booleanLiteralExpr = booleanLiteralExpr;
    }

    @Override
    public Optional<Type> getType() {
        return Optional.of(boolean.class);
    }

    @Override
    public Node toJavaExpression() {
        return booleanLiteralExpr;
    }

    @Override
    public String toString() {
        return "BooleanLiteralExpressionT{" +
               "originalExpression=" + booleanLiteralExpr +
               '}';
    }
}
