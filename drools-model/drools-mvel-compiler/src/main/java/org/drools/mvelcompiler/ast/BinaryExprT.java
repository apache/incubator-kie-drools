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
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.Expression;

public class BinaryExprT implements TypedExpression {

    public static BinaryExpr.Operator compoundToArithmeticOperation(AssignExpr.Operator operator) {
        switch (operator) {
            case PLUS: // +=
                return BinaryExpr.Operator.PLUS;
            case MINUS: // -=
                return BinaryExpr.Operator.MINUS;
            case MULTIPLY: // *=
                return BinaryExpr.Operator.MULTIPLY;
            case DIVIDE: // /=
                return BinaryExpr.Operator.DIVIDE;
        }
        throw new RuntimeException("Unknown operator");
    }

    private static final List<Type> PRIORITIZED_TYPES = Arrays.asList( String.class,
            BigDecimal.class, BigInteger.class,
            Double.class, double.class,
            Long.class, long.class,
            Float.class, Float.class,
            Integer.class, int.class );

    private final TypedExpression left;
    private final TypedExpression right;
    private final BinaryExpr.Operator operator;

    public BinaryExprT(TypedExpression left, TypedExpression right, BinaryExpr.Operator operator) {
        this.left = left;
        this.right = right;
        this.operator = operator;
    }

    public TypedExpression getLeft() {
        return left;
    }

    public TypedExpression getRight() {
        return right;
    }

    public BinaryExpr.Operator getOperator() {
        return operator;
    }

    @Override
    public Optional<Type> getType() {
        Optional<Type> leftType = left.getType();
        Optional<Type> rightType = right.getType();
        if (!leftType.isPresent()) {
            return rightType;
        }
        if (!rightType.isPresent()) {
            return leftType;
        }
        return Optional.of( combine(leftType.get(), rightType.get()) );
    }

    @Override
    public Node toJavaExpression() {
        return new BinaryExpr((Expression) left.toJavaExpression(), (Expression) right.toJavaExpression(), operator);
    }

    private static Type combine(Type t1, Type t2) {
        for (Type t : PRIORITIZED_TYPES) {
            if (t1 == t || t2 == t) {
                return t;
            }
        }
        return t2;
    }


}
