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
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;

import static org.drools.mvelcompiler.util.CoercionUtils.PUT_CALL;
import static org.drools.mvelcompiler.util.CoercionUtils.coerceMapValueToString;

public class MapPutExprT implements TypedExpression {

    private final TypedExpression name;
    private final Expression key;
    private final TypedExpression value;
    private final Optional<Type> type;

    public MapPutExprT(TypedExpression name, Expression key, TypedExpression value, Optional<Type> type) {
        this.name = name;
        this.key = key;
        this.value = value;
        this.type = type;
    }

    @Override
    public Optional<Type> getType() {
        return Optional.empty();
    }

    @Override
    public Node toJavaExpression() {
        final Expression originalValue = (Expression) this.value.toJavaExpression();
        final Expression coercedValue = coerceMapValueToString(type, originalValue);

        return new MethodCallExpr((Expression) name.toJavaExpression(),
                                  PUT_CALL,
                                  NodeList.nodeList(key, coercedValue));
    }
}
