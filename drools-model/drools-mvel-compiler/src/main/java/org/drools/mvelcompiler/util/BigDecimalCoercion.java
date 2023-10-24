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
package org.drools.mvelcompiler.util;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import org.drools.mvelcompiler.ast.MethodCallExprT;
import org.drools.mvelcompiler.ast.TypedExpression;

import static com.github.javaparser.ast.NodeList.nodeList;

public final class BigDecimalCoercion {

    private BigDecimalCoercion() {
        // It is forbidden to create instances of util classes.
    }

    public static TypedExpression coerceBigDecimalMethod(final Class<?> targetType, final TypedExpression bigDecimalExpression) {
        if (targetType == BigDecimal.class) {
            return bigDecimalExpression;
        } else if (isInteger(targetType)) {
            return bigDecimalToPrimitive(bigDecimalExpression, "intValue", targetType);
        } else if (isLong(targetType)) {
            return bigDecimalToPrimitive(bigDecimalExpression, "longValue", targetType);
        } else if (isShort(targetType)) {
            return bigDecimalToPrimitive(bigDecimalExpression, "shortValue", targetType);
        } else if (isDouble(targetType)) {
            return bigDecimalToPrimitive(bigDecimalExpression, "doubleValue", targetType);
        } else if (isFloat(targetType)) {
            return bigDecimalToPrimitive(bigDecimalExpression, "floatValue", targetType);
        } else {
            return bigDecimalExpression;
        }
    }

    public static Expression coercedArgument(Class<?> argumentType, Class<?> actualType, Expression argument) {
        boolean argumentTypeIsBigDecimal = BigDecimal.class.isAssignableFrom(argumentType);

        if (isInteger(actualType) && argumentTypeIsBigDecimal) {
            return bigDecimalToPrimitive(argument, "intValue");
        } else if (isLong(actualType) && argumentTypeIsBigDecimal) {
            return bigDecimalToPrimitive(argument, "longValue");
        } else if (isShort(actualType) && argumentTypeIsBigDecimal) {
            return bigDecimalToPrimitive(argument, "shortValue");
        } else if (isDouble(actualType) && argumentTypeIsBigDecimal) {
            return bigDecimalToPrimitive(argument, "doubleValue");
        } else if (isFloat(actualType) && argumentTypeIsBigDecimal) {
            return bigDecimalToPrimitive(argument, "floatValue");
        } else {
            return argument;
        }
    }

    private static MethodCallExpr bigDecimalToPrimitive(Expression argumentExpression, String typeConversionMethodName) {
        return new MethodCallExpr(argumentExpression, typeConversionMethodName, nodeList());
    }

    private static MethodCallExprT bigDecimalToPrimitive(final TypedExpression bigDecimalExpression,
            final String typeConversionMethodName, final Class<?> targetType) {
        return new MethodCallExprT(typeConversionMethodName,
                Optional.ofNullable(bigDecimalExpression),
                Collections.emptyList(),
                Collections.emptyList(),
                Optional.ofNullable(targetType));
    }

    private static boolean isInteger(Class<?> actualType) {
        return actualType == int.class || actualType == Integer.class;
    }

    private static boolean isLong(Class<?> actualType) {
        return actualType == long.class || actualType == Long.class;
    }

    private static boolean isFloat(Class<?> actualType) {
        return actualType == float.class || actualType == Float.class;
    }

    private static boolean isDouble(Class<?> actualType) {
        return actualType == double.class || actualType == Double.class;
    }

    private static boolean isShort(Class<?> actualType) {
        return actualType == short.class || actualType == Short.class;
    }
}
