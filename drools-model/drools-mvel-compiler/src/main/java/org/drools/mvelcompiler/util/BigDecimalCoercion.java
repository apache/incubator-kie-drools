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

import org.drools.mvelcompiler.ast.MethodCallExprT;
import org.drools.mvelcompiler.ast.TypedExpression;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

public final class BigDecimalCoercion {

    private BigDecimalCoercion() {
        // It is forbidden to create instances of util classes.
    }

    public static TypedExpression coerceBigDecimalMethod(final Class<?> targetType, final TypedExpression bigDecimalExpression) {
        if (targetType == BigDecimal.class) {
            return bigDecimalExpression;
        } else if (TypeUtils.isInteger(targetType)) {
            return bigDecimalToPrimitive(bigDecimalExpression, "intValue", targetType);
        } else if (TypeUtils.isLong(targetType)) {
            return bigDecimalToPrimitive(bigDecimalExpression, "longValue", targetType);
        } else if (TypeUtils.isShort(targetType)) {
            return bigDecimalToPrimitive(bigDecimalExpression, "shortValue", targetType);
        } else if (TypeUtils.isDouble(targetType)) {
            return bigDecimalToPrimitive(bigDecimalExpression, "doubleValue", targetType);
        } else if (TypeUtils.isFloat(targetType)) {
            return bigDecimalToPrimitive(bigDecimalExpression, "floatValue", targetType);
        } else {
            return bigDecimalExpression;
        }
    }

    private static MethodCallExprT bigDecimalToPrimitive(final TypedExpression bigDecimalExpression,
            final String typeConversionMethodName, final Class<?> targetType) {
        return new MethodCallExprT(typeConversionMethodName,
                Optional.ofNullable(bigDecimalExpression),
                Collections.emptyList(),
                Collections.emptyList(),
                Optional.ofNullable(targetType));
    }
}
