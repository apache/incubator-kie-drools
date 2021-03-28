/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.dmn.feel.codegen.feel11;

import org.kie.dmn.feel.lang.ast.InfixOpNode;
import org.kie.dmn.feel.util.AssignableFromUtil;

/**
 * The purpose of this class is to offer import .* methods to compiled FEEL classes compiling expressions.
 * Implementing DMN FEEL spec chapter 10.3.2.12 Semantic mappings
 */
public abstract class CompiledFEELSemanticMappings {

    private static boolean compatible(final Comparable left,
                                      final Comparable right) {
        final Class<?> leftClass = left.getClass();
        final Class<?> rightClass = right.getClass();
        return AssignableFromUtil.isAssignableFrom(leftClass, rightClass)
                || AssignableFromUtil.isAssignableFrom(rightClass, leftClass);
    }

    public static <T> T coerceTo(final Class<?> paramType,
                                 final Object value) {
        final Object actual;
        if (AssignableFromUtil.isAssignableFrom(paramType, value.getClass())) {
            actual = value;
        } else {
            if (value instanceof Number) {
                if (paramType == byte.class || paramType == Byte.class) {
                    actual = ((Number) value).byteValue();
                } else if (paramType == short.class || paramType == Short.class) {
                    actual = ((Number) value).shortValue();
                } else if (paramType == int.class || paramType == Integer.class) {
                    actual = ((Number) value).intValue();
                } else if (paramType == long.class || paramType == Long.class) {
                    actual = ((Number) value).longValue();
                } else if (paramType == float.class || paramType == Float.class) {
                    actual = ((Number) value).floatValue();
                } else if (paramType == double.class || paramType == Double.class) {
                    actual = ((Number) value).doubleValue();
                } else if (paramType == Object[].class) {
                    actual = new Object[]{value};
                } else {
                    throw new IllegalArgumentException("Unable to coerce parameter " + value + ". Expected " + paramType + " but found " + value.getClass());
                }
            } else if (value instanceof String
                    && ((String) value).length() == 1
                    && (paramType == char.class || paramType == Character.class)) {
                actual = ((String) value).charAt(0);
            } else if (value instanceof Boolean && paramType == boolean.class) {
                // Because Boolean can be also null, boolean.class is not assignable from Boolean.class. So we must coerce this.
                actual = value;
            } else {
                throw new IllegalArgumentException("Unable to coerce parameter " + value + ". Expected " + paramType + " but found " + value.getClass());
            }
        }
        return (T) actual;
    }

    public static Object pow(final Object left,
                             final Object right) {
        return InfixOpNode.math(left, right, null, (l, r) -> l.pow(r.intValue()));
    }
}
