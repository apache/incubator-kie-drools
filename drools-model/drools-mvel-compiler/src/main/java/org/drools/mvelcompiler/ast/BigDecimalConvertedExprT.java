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
import java.math.BigDecimal;
import java.util.Optional;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.UnaryExpr;

import static com.github.javaparser.ast.NodeList.nodeList;

public class BigDecimalConvertedExprT implements TypedExpression {

    private final TypedExpression value;
    private final Type type = BigDecimal.class;

    private final Optional<UnaryExpr> unaryExpr;

    public BigDecimalConvertedExprT(TypedExpression value) {
        this(value, Optional.empty());
    }

    public BigDecimalConvertedExprT(TypedExpression value, Optional<UnaryExpr> unaryExpr) {
        this.value = value;
        this.unaryExpr = unaryExpr;
    }

    @Override
    public Optional<Type> getType() {
        return Optional.of(type);
    }

    @Override
    public Node toJavaExpression() {
        Expression expr = (Expression) value.toJavaExpression();
        Expression arg = unaryExpr.map(u -> (Expression) new UnaryExpr(expr, u.getOperator())).orElse(expr);
        return new ObjectCreationExpr(null, StaticJavaParser.parseClassOrInterfaceType(type.getTypeName()), nodeList(arg));
    }

    @Override
    public String toString() {
        return "BigDecimalConstantExprT{" +
                "value=" + value +
                '}';
    }
}
