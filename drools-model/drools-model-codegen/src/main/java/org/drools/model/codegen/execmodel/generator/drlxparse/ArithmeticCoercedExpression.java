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
package org.drools.model.codegen.execmodel.generator.drlxparse;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import com.github.javaparser.ast.expr.BinaryExpr.Operator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import org.drools.model.codegen.execmodel.errors.InvalidExpressionErrorResult;
import org.drools.model.codegen.execmodel.generator.TypedExpression;

import static com.github.javaparser.ast.NodeList.nodeList;
import static com.github.javaparser.ast.expr.BinaryExpr.Operator.DIVIDE;
import static com.github.javaparser.ast.expr.BinaryExpr.Operator.MINUS;
import static com.github.javaparser.ast.expr.BinaryExpr.Operator.MULTIPLY;
import static com.github.javaparser.ast.expr.BinaryExpr.Operator.PLUS;
import static com.github.javaparser.ast.expr.BinaryExpr.Operator.REMAINDER;
import static java.util.Arrays.asList;
import static org.drools.util.ClassUtils.isNumericClass;

public class ArithmeticCoercedExpression {

    private final TypedExpression left;
    private final TypedExpression right;
    private final Operator operator;

    private static final Set<Operator> arithmeticOperators = new HashSet<>(asList(PLUS, MINUS, MULTIPLY, DIVIDE, REMAINDER));

    public ArithmeticCoercedExpression(TypedExpression left, TypedExpression right, Operator operator) {
        this.left = left;
        this.right = right;
        this.operator = operator;
    }

    public ArithmeticCoercedExpressionResult coerce() {

        if (!requiresCoercion()) {
            return new ArithmeticCoercedExpressionResult(left, right); // do not coerce
        }

        final Class<?> leftClass = left.getRawClass();
        final Class<?> rightClass = right.getRawClass();

        if (!canCoerce(leftClass, rightClass)) {
            throw new ArithmeticCoercedExpressionException(new InvalidExpressionErrorResult("Arithmetic operation requires compatible types. Found " + leftClass + " and " + rightClass));
        }

        TypedExpression coercedLeft = left;
        TypedExpression coercedRight = right;
        if (leftClass == String.class) {
            if (operator == Operator.PLUS) {
                // String concatenation : Compatibility with mvel
                coercedRight = coerceToString(right);
            } else {
                // We may coerce to BigDecimal but Mvel MathProcessor uses double so followed the same.
                coercedLeft = coerceToDouble(left);
            }
        }
        if (rightClass == String.class) {
            if (operator == Operator.PLUS) {
                // String concatenation : Compatibility with mvel
                coercedLeft = coerceToString(left);
            } else {
                // We may coerce to BigDecimal but Mvel MathProcessor uses double so followed the same.
                coercedRight = coerceToDouble(right);
            }
        }

        return new ArithmeticCoercedExpressionResult(coercedLeft, coercedRight);
    }

    private boolean requiresCoercion() {
        if (!arithmeticOperators.contains(operator)) {
            return false;
        }
        final Class<?> leftClass = left.getRawClass();
        final Class<?> rightClass = right.getRawClass();
        if (leftClass == rightClass) {
            return false;
        }
        if (isNumericClass(leftClass) && isNumericClass(rightClass)) {
            return false;
        }
        return true;
    }

    private boolean canCoerce(Class<?> leftClass, Class<?> rightClass) {
        return leftClass == String.class && isNumericClass(rightClass) ||
               rightClass == String.class && isNumericClass(leftClass);
    }

    private TypedExpression coerceToDouble(TypedExpression typedExpression) {
        final Expression expression = typedExpression.getExpression();
        TypedExpression coercedExpression = typedExpression.cloneWithNewExpression(new MethodCallExpr(new NameExpr("Double"), "valueOf", nodeList(expression)));
        return coercedExpression.setType(BigDecimal.class);
    }

    private TypedExpression coerceToString(TypedExpression typedExpression) {
        final Expression expression = typedExpression.getExpression();
        TypedExpression coercedExpression = typedExpression.cloneWithNewExpression(new MethodCallExpr(new NameExpr("String"), "valueOf", nodeList(expression)));
        return coercedExpression.setType(String.class);
    }

    public static class ArithmeticCoercedExpressionResult {

        private final TypedExpression coercedLeft;
        private final TypedExpression coercedRight;

        public ArithmeticCoercedExpressionResult(TypedExpression left, TypedExpression coercedRight) {
            this.coercedLeft = left;
            this.coercedRight = coercedRight;
        }

        public TypedExpression getCoercedLeft() {
            return coercedLeft;
        }

        public TypedExpression getCoercedRight() {
            return coercedRight;
        }
    }

    public static class ArithmeticCoercedExpressionException extends RuntimeException {

        private final transient InvalidExpressionErrorResult invalidExpressionErrorResult;

        ArithmeticCoercedExpressionException(InvalidExpressionErrorResult invalidExpressionErrorResult) {
            this.invalidExpressionErrorResult = invalidExpressionErrorResult;
        }

        public InvalidExpressionErrorResult getInvalidExpressionErrorResult() {
            return invalidExpressionErrorResult;
        }
    }

}
