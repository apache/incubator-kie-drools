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

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.CharLiteralExpr;
import com.github.javaparser.ast.expr.DoubleLiteralExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.LiteralStringValueExpr;
import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.type.PrimitiveType;
import org.drools.util.MethodUtils;
import org.drools.model.codegen.execmodel.errors.InvalidExpressionErrorResult;
import org.drools.model.codegen.execmodel.generator.TypedExpression;
import org.drools.model.codegen.execmodel.generator.UnificationTypedExpression;

import static org.drools.model.codegen.execmodel.generator.DrlxParseUtil.toClassOrInterfaceType;
import static org.drools.model.codegen.execmodel.generator.DrlxParseUtil.toJavaParserType;
import static org.drools.model.codegen.execmodel.generator.DrlxParseUtil.toStringLiteral;
import static org.drools.util.ClassUtils.toNonPrimitiveType;

public class CoercedExpression {

    public static final String STRING_TO_DATE_FIELD_START = "org_drools_modelcompiler_util_EvaluationUtil_convertDate";
    private static final String STRING_TO_DATE_METHOD = "org.drools.modelcompiler.util.EvaluationUtil.convertDate";
    private static final String STRING_TO_LOCAL_DATE_METHOD = "org.drools.modelcompiler.util.EvaluationUtil.convertDateLocal";
    private static final String STRING_TO_LOCAL_DATE_TIME_METHOD = "org.drools.modelcompiler.util.EvaluationUtil.convertDateTimeLocal";

    private static final List<Class<?>> LITERAL_NUMBER_CLASSES = Arrays.asList(int.class, long.class, double.class, Integer.class, Long.class, Double.class);

    private final TypedExpression left;
    private final TypedExpression right;
    private final boolean equalityExpr;

    private static final Map<Class, List<Class<?>>> narrowingTypes = new HashMap<>();

    static {
        // https://docs.oracle.com/javase/specs/jls/se7/html/jls-5.html#jls-5.1.3
        narrowingTypes.put(short.class, Arrays.asList(Byte.class, Character.class));
        narrowingTypes.put(char.class, Arrays.asList(Byte.class, Short.class));
        narrowingTypes.put(int.class, Arrays.asList(Byte.class, Short.class, Character.class));
        narrowingTypes.put(long.class, Arrays.asList(Byte.class, Short.class, Character.class, Integer.class));
        narrowingTypes.put(float.class, Arrays.asList(Byte.class, Short.class, Character.class, Integer.class, Long.class));
        narrowingTypes.put(double.class, Arrays.asList(Byte.class, Short.class, Character.class, Integer.class, Long.class, Float.class));
    }

    public CoercedExpression(TypedExpression left, TypedExpression right, boolean equalityExpr) {
        this.left = left;
        this.right = right;
        this.equalityExpr = equalityExpr;
    }

    public CoercedExpressionResult coerce() {

        final Class<?> leftClass = left.getRawClass();
        final Class<?> nonPrimitiveLeftClass = toNonPrimitiveType(leftClass);
        final Class<?> rightClass = right.getRawClass();
        final Class<?> nonPrimitiveRightClass = toNonPrimitiveType(rightClass);

        boolean sameClass = leftClass == rightClass;
        boolean isUnificationExpression = left instanceof UnificationTypedExpression || right instanceof UnificationTypedExpression;

        if (sameClass || isUnificationExpression) {
            return new CoercedExpressionResult(left, right);
        }

        if (!canCoerce()) {
            throw new CoercedExpressionException(new InvalidExpressionErrorResult("Comparison operation requires compatible types. Found " + leftClass + " and " + rightClass));
        }

        if ((nonPrimitiveLeftClass == Integer.class || nonPrimitiveLeftClass == Long.class) && nonPrimitiveRightClass == Double.class) {
            CastExpr castExpression = new CastExpr(PrimitiveType.doubleType(), this.left.getExpression());
            return new CoercedExpressionResult(
                    new TypedExpression(castExpression, double.class, left.getType()),
                    right,
                    false);
        }

        final boolean leftIsPrimitive = leftClass.isPrimitive() || Number.class.isAssignableFrom( leftClass );
        final boolean canCoerceLiteralNumberExpr = canCoerceLiteralNumberExpr(leftClass);

        boolean rightAsStaticField = false;
        final Expression rightExpression = right.getExpression();
        final TypedExpression coercedRight;

        if (leftIsPrimitive && canCoerceLiteralNumberExpr && rightExpression instanceof LiteralStringValueExpr) {
            final Expression coercedLiteralNumberExprToType = coerceLiteralNumberExprToType((LiteralStringValueExpr) right.getExpression(), leftClass);
            coercedRight = right.cloneWithNewExpression(coercedLiteralNumberExprToType);
            coercedRight.setType( leftClass );
        } else if (shouldCoerceBToString(left, right)) {
            coercedRight = coerceToString(right);
        } else if (isNotBinaryExpression(right) && canBeNarrowed(leftClass, rightClass) && right.isNumberLiteral()) {
            coercedRight = castToClass(leftClass);
        } else if (leftClass == long.class && rightClass == int.class) {
            coercedRight = right.cloneWithNewExpression(new CastExpr(PrimitiveType.longType(), right.getExpression()));
        } else if (leftClass == Date.class && rightClass == String.class) {
            coercedRight = coerceToDate(right);
            rightAsStaticField = true;
        } else if (leftClass == LocalDate.class && rightClass == String.class) {
            coercedRight = coerceToLocalDate(right);
            rightAsStaticField = true;
        } else if (leftClass == LocalDateTime.class && rightClass == String.class) {
            coercedRight = coerceToLocalDateTime(right);
            rightAsStaticField = true;
        } else if (shouldCoerceBToMap()) {
            coercedRight = castToClass(toNonPrimitiveType(leftClass));
        } else if (isBoolean(leftClass) && !isBoolean(rightClass)) {
            coercedRight = coerceBoolean(right);
        } else {
            coercedRight = right;
        }

        final TypedExpression coercedLeft;
        if (nonPrimitiveLeftClass == Character.class && shouldCoerceBToString(right, left)) {
            coercedLeft = coerceToString(left);
        } else {
            coercedLeft = left;
        }

        return new CoercedExpressionResult(coercedLeft, coercedRight, rightAsStaticField);
    }

    private boolean isBoolean(Class<?> leftClass) {
        return Boolean.class.isAssignableFrom(leftClass) || boolean.class.isAssignableFrom(leftClass);
    }

    private boolean shouldCoerceBToMap() {
        return isNotBinaryExpression(right) && Map.class.isAssignableFrom(right.getRawClass());
    }

    private boolean canCoerce() {
        final Class<?> leftClass = left.getRawClass();
        if (!leftClass.isPrimitive() || !canCoerceLiteralNumberExpr(leftClass)) {
            return true;
        }

        final Class<?> rightClass = right.getRawClass();
        return rightClass.isPrimitive()
                || Number.class.isAssignableFrom(rightClass)
                || Boolean.class == rightClass
                || String.class == rightClass
                || Object.class == rightClass
                || (Map.class.isAssignableFrom(leftClass) || Map.class.isAssignableFrom(rightClass));
    }

    private TypedExpression castToClass(Class<?> clazz) {
        return right.cloneWithNewExpression(new CastExpr(toJavaParserType(clazz, right.isPrimitive()), right.getExpression()));
    }

    private static TypedExpression coerceToString(TypedExpression typedExpression) {
        final Expression expression = typedExpression.getExpression();
        TypedExpression coercedExpression;
        if (expression instanceof CharLiteralExpr) {
            coercedExpression = typedExpression.cloneWithNewExpression(toStringLiteral(((CharLiteralExpr) expression).getValue()));
        } else if (typedExpression.isPrimitive()) {
            coercedExpression = typedExpression.cloneWithNewExpression(new MethodCallExpr(new NameExpr("String"), "valueOf", NodeList.nodeList(expression)));
        } else if (typedExpression.getType() == Object.class) {
            coercedExpression = typedExpression.cloneWithNewExpression(new MethodCallExpr(expression, "toString"));
        } else if (expression instanceof NameExpr) {
            coercedExpression = typedExpression.cloneWithNewExpression(new CastExpr(toClassOrInterfaceType(String.class), expression));
        } else {
            coercedExpression = typedExpression.cloneWithNewExpression(toStringLiteral(expression.toString()));
        }
        return coercedExpression.setType(String.class);
    }

    private static TypedExpression coerceToDate(TypedExpression typedExpression) {
        MethodCallExpr methodCallExpr = new MethodCallExpr(null, STRING_TO_DATE_METHOD);
        methodCallExpr.addArgument(typedExpression.getExpression());
        return new TypedExpression(methodCallExpr, Date.class);
    }

    private static TypedExpression coerceToLocalDate(TypedExpression typedExpression) {
        MethodCallExpr methodCallExpr = new MethodCallExpr(null, STRING_TO_LOCAL_DATE_METHOD);
        methodCallExpr.addArgument(typedExpression.getExpression());
        return new TypedExpression(methodCallExpr, LocalDate.class);
    }

    private static TypedExpression coerceToLocalDateTime(TypedExpression typedExpression) {
        MethodCallExpr methodCallExpr = new MethodCallExpr(null, STRING_TO_LOCAL_DATE_TIME_METHOD);
        methodCallExpr.addArgument(typedExpression.getExpression());
        return new TypedExpression(methodCallExpr, LocalDateTime.class);
    }

    private static TypedExpression coerceBoolean(TypedExpression typedExpression) {
        if (typedExpression.getType() == MethodUtils.NullType.class) {
            return typedExpression;
        }

        final Expression expression = typedExpression.getExpression();
        if (expression instanceof BooleanLiteralExpr) {
            return typedExpression;
        } else if (expression instanceof StringLiteralExpr) {
            final String expressionValue = ((StringLiteralExpr) expression).getValue();
            if (Boolean.TRUE.toString().equals(expressionValue) || Boolean.FALSE.toString().equals(expressionValue)) {
                final TypedExpression coercedExpression = typedExpression.cloneWithNewExpression(new BooleanLiteralExpr(Boolean.parseBoolean(expressionValue)));
                return coercedExpression.setType(Boolean.class);
            } else {
                throw new CoercedExpressionException(new InvalidExpressionErrorResult("Cannot coerce String " + expressionValue + " to boolean!"));
            }
        } else {
            throw new CoercedExpressionException(new InvalidExpressionErrorResult("Cannot coerce " + typedExpression.getType() + " to boolean!"));
        }
    }

    private static boolean canCoerceLiteralNumberExpr(Class<?> type) {
        return LITERAL_NUMBER_CLASSES.contains(type);
    }

    private static boolean shouldCoerceBToString(TypedExpression a, TypedExpression b) {
        return b.getExpression() != null &&
                isNotBinaryExpression(b) &&
                a.getType() == String.class &&
                b.getType() != String.class &&
                b.getType() != Object.class &&
                b.getType() != BigDecimal.class &&
                !(Map.class.isAssignableFrom(b.getRawClass())) &&
                !(b.getExpression() instanceof NullLiteralExpr) &&
                b.getType() != Serializable.class;
    }

    private static boolean isNotBinaryExpression(TypedExpression e) {
        return !(e.getExpression() != null && e.getExpression().isBinaryExpr());
    }

    private Expression coerceLiteralNumberExprToType(LiteralStringValueExpr expr, Class<?> type) {
        if (type == int.class || type == Integer.class) {
            return new IntegerLiteralExpr( stringToIntArgument( expr.getValue() ) ) ;
        }
        if (type == long.class || type == Long.class) {
            String value = expr.getValue();
            return new LongLiteralExpr(isLongLiteral(value) ? expr.getValue() : expr.getValue() + "l");
        }
        if (type == double.class || type == Double.class) {
            String doubleExpr = expr.getValue();
            try {
                doubleExpr = Double.valueOf( doubleExpr ).toString();
            } catch (NumberFormatException nfe) {
                // safe to ignore
            }
            return new DoubleLiteralExpr(doubleExpr);
        }
        throw new CoercedExpressionException(new InvalidExpressionErrorResult("Unknown literal: " + expr));
    }

    private static String stringToIntArgument(String value) {
        return value.startsWith( "0x" ) ? value : "" + Integer.valueOf( value );
    }

    private boolean isLongLiteral(String value) {
        return value.endsWith("l") || value.endsWith("L");
    }

    private boolean canBeNarrowed(Class<?> leftType, Class<?> rightType) {
        return Optional.ofNullable(narrowingTypes.get(rightType)).map(a -> a.contains(toNonPrimitiveType(leftType))).orElse(false);
    }

    public static class CoercedExpressionResult {

        private final TypedExpression coercedLeft;
        private final TypedExpression coercedRight;
        private final boolean rightAsStaticField;

        CoercedExpressionResult(TypedExpression left, TypedExpression coercedRight) {
            this(left, coercedRight, false);
        }

        CoercedExpressionResult(TypedExpression left, TypedExpression coercedRight, boolean rightAsStaticField) {
            this.coercedLeft = left;
            this.coercedRight = coercedRight;
            this.rightAsStaticField = rightAsStaticField;
        }

        TypedExpression getCoercedLeft() {
            return coercedLeft;
        }

        public TypedExpression getCoercedRight() {
            return coercedRight;
        }

        public boolean isRightAsStaticField() {
            return rightAsStaticField;
        }
    }

    static class CoercedExpressionException extends RuntimeException {

        private final transient InvalidExpressionErrorResult invalidExpressionErrorResult;

        CoercedExpressionException(InvalidExpressionErrorResult invalidExpressionErrorResult) {
            this.invalidExpressionErrorResult = invalidExpressionErrorResult;
        }

        InvalidExpressionErrorResult getInvalidExpressionErrorResult() {
            return invalidExpressionErrorResult;
        }
    }
}
