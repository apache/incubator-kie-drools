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
package org.kie.pmml.api.utils;

import java.math.BigDecimal;
import java.util.function.Predicate;

import org.apache.commons.math3.util.Precision;
import org.kie.pmml.api.exceptions.KiePMMLException;

/**
 * Class meant to provide helper methods to <b>convert</b> a given object to another one of a requested type
 */
public class ConverterTypeUtil {

    static final String FAILED_CONVERSION = "Failed to convert %s to %s";
    private static Predicate<Class<?>> IS_BOOLEAN =
            expectedClass -> expectedClass.isAssignableFrom(Boolean.class) || expectedClass.isAssignableFrom(boolean.class);
    private static Predicate<Class<?>> IS_INTEGER =
            expectedClass -> expectedClass.isAssignableFrom(Integer.class) || expectedClass.isAssignableFrom(int.class);
    private static Predicate<Class<?>> IS_LONG =
            expectedClass -> expectedClass.isAssignableFrom(Long.class) || expectedClass.isAssignableFrom(long.class);
    private static Predicate<Class<?>> IS_DOUBLE =
            expectedClass -> expectedClass.isAssignableFrom(Double.class) || expectedClass.isAssignableFrom(double.class);
    private static Predicate<Class<?>> IS_FLOAT =
            expectedClass -> expectedClass.isAssignableFrom(Float.class) || expectedClass.isAssignableFrom(float.class);
    private static Predicate<Class<?>> IS_CHARACTER =
            expectedClass -> expectedClass.isAssignableFrom(Character.class) || expectedClass.isAssignableFrom(char.class);
    private static Predicate<Class<?>> IS_BYTE =
            expectedClass -> expectedClass.isAssignableFrom(Byte.class) || expectedClass.isAssignableFrom(byte.class);
    private static Predicate<Class<?>> IS_SHORT =
            expectedClass -> expectedClass.isAssignableFrom(Short.class) || expectedClass.isAssignableFrom(short.class);

    private ConverterTypeUtil() {
        // Avoid instantiation
    }

    /**
     * Convert the given <code>Object</code> to expected <code>Class</code>.
     * It throws <code>KiePMMLInternalException</code> if conversion has failed
     * @param expectedClass
     * @param originalObject
     * @return
     */
    public static Object convert(Class<?> expectedClass, Object originalObject) {
        if (originalObject == null) {
            return null;
        }
        Class<?> currentClass = originalObject.getClass();
        if (expectedClass.isAssignableFrom(currentClass)) {
            return originalObject;
        }
        if (PrimitiveBoxedUtils.areSameWithBoxing(expectedClass, originalObject.getClass())) {
            // No cast/transformation originalObject
            return originalObject;
        }
        if (expectedClass == String.class) {
            return originalObject.toString();
        }
        Object toReturn;
        String currentClassName = currentClass.getName();
        switch (currentClassName) {
            case "java.lang.String":
                toReturn = convertFromString(expectedClass, (String) originalObject);
                break;
            case "int":
            case "java.lang.Integer":
                toReturn = convertFromInteger(expectedClass, (Integer) originalObject);
                break;
            case "double":
            case "java.lang.Double":
                toReturn = convertFromDouble(expectedClass, (Double) originalObject);
                break;
            case "float":
            case "java.lang.Float":
                toReturn = convertFromFloat(expectedClass, (Float) originalObject);
                break;
            default:
                throw new KiePMMLException(String.format(FAILED_CONVERSION, originalObject,
                                                         expectedClass.getName()));
        }
        return toReturn;
    }

    static Object convertFromInteger(Class<?> expectedClass, Integer originalObject) {
        if (IS_DOUBLE.test(expectedClass)) {
            return originalObject.doubleValue();
        } else if (IS_LONG.test(expectedClass)) {
            return originalObject.longValue();
        } else if (IS_FLOAT.test(expectedClass)) {
            return originalObject.floatValue();
        } else if (IS_BYTE.test(expectedClass)) {
            return originalObject.byteValue();
        } else if (IS_SHORT.test(expectedClass)) {
            return originalObject.shortValue();
        } else {
            throw new KiePMMLException(String.format(FAILED_CONVERSION, originalObject,
                                                     expectedClass.getName()));
        }
    }

    static Object convertFromDouble(Class<?> expectedClass, Double originalObject) {
        if (IS_INTEGER.test(expectedClass)) {
            return  (int) Precision.round(originalObject, 0, BigDecimal.ROUND_HALF_UP);
        } else if (IS_LONG.test(expectedClass)) {
            return  (long) Precision.round(originalObject, 0, BigDecimal.ROUND_HALF_UP);
        } else if (IS_FLOAT.test(expectedClass)) {
            return originalObject.floatValue();
        } else if (IS_BYTE.test(expectedClass)) {
            return (byte) Precision.round(originalObject, 0, BigDecimal.ROUND_HALF_UP);
        } else if (IS_SHORT.test(expectedClass)) {
            return (short) Precision.round(originalObject, 0, BigDecimal.ROUND_HALF_UP);
        } else {
            throw new KiePMMLException(String.format(FAILED_CONVERSION, originalObject,
                                                     expectedClass.getName()));
        }
    }

    static Object convertFromFloat(Class<?> expectedClass, Float originalObject) {
        if (IS_INTEGER.test(expectedClass)) {
            return  (int) Precision.round(originalObject, 0, BigDecimal.ROUND_HALF_UP);
        } else if (IS_LONG.test(expectedClass)) {
            return  (long) Precision.round(originalObject, 0, BigDecimal.ROUND_HALF_UP);
        } else if (IS_DOUBLE.test(expectedClass)) {
            return originalObject.doubleValue();
        } else if (IS_BYTE.test(expectedClass)) {
            return (byte) Precision.round(originalObject, 0, BigDecimal.ROUND_HALF_UP);
        } else if (IS_SHORT.test(expectedClass)) {
            return (short) Precision.round(originalObject, 0, BigDecimal.ROUND_HALF_UP);
        } else {
            throw new KiePMMLException(String.format(FAILED_CONVERSION, originalObject,
                                                     expectedClass.getName()));
        }
    }

    static Object convertFromString(Class<?> expectedClass, String originalObject) {
        try {
            if (IS_BOOLEAN.test(expectedClass)) {
                return parseBoolean(originalObject);
            } else if (IS_INTEGER.test(expectedClass)) {
                return Integer.parseInt(originalObject);
            } else if (IS_LONG.test(expectedClass)) {
                return Long.parseLong(originalObject);
            } else if (IS_DOUBLE.test(expectedClass)) {
                return Double.parseDouble(originalObject);
            } else if (IS_FLOAT.test(expectedClass)) {
                return Float.parseFloat(originalObject);
            } else if (IS_CHARACTER.test(expectedClass)) {
                return parseChar(originalObject);
            } else if (IS_BYTE.test(expectedClass)) {
                return Byte.parseByte(originalObject);
            } else if (IS_SHORT.test(expectedClass)) {
                return Short.parseShort(originalObject);
            } else {
                throw new KiePMMLException(String.format(FAILED_CONVERSION, originalObject,
                                                         expectedClass.getName()));
            }
        } catch (Exception e) {
            throw new KiePMMLException(String.format(FAILED_CONVERSION, originalObject,
                                                     expectedClass.getName()));
        }
    }

    private static boolean parseBoolean(String value) {
        if ("true".equalsIgnoreCase(value)) {
            return true;
        } else if ("false".equalsIgnoreCase(value)) {
            return false;
        } else {
            throw new IllegalArgumentException("Impossible to parse as boolean " + value);
        }
    }

    private static char parseChar(String value) {
        if (value == null || value.length() != 1) {
            throw new IllegalArgumentException("Impossible to transform " + value + " as char");
        }
        return value.charAt(0);
    }
}
