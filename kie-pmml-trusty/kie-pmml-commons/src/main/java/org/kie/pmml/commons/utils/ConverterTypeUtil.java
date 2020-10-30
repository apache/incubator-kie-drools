/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.pmml.commons.utils;

import java.util.function.Predicate;

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
        Object toReturn;
        if (currentClass.equals(String.class)) {
            try {
                toReturn = convertFromString(expectedClass, (String) originalObject);
            } catch (Exception e) {
                throw new KiePMMLException(String.format(FAILED_CONVERSION, originalObject,
                                                         expectedClass.getName()), e);
            }
        } else if (expectedClass == String.class) {
            toReturn = originalObject.toString();
        } else {
            throw new KiePMMLException(String.format(FAILED_CONVERSION, originalObject,
                                                     expectedClass.getName()));
        }
        return toReturn;
    }

    static Object convertFromString(Class<?> expectedClass, String originalObject) {
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
