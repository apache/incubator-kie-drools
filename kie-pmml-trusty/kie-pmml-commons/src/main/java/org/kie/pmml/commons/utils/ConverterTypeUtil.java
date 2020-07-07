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

import org.kie.pmml.commons.exceptions.KiePMMLException;

/**
 * Class meant to provide helper methods to <b>convert</b> a given object to another one of a requested type
 */
public class ConverterTypeUtil {

    static final String FAILED_CONVERSION = "Failed to convert %s to %s";

    private ConverterTypeUtil() {
        // Avoid instantiation
    }

    /**
     * Convert the given <code>Object</code> to expected <code>Class</code>.
     * It throws <code>KiePMMLInternalException</code> if conversion has failed
     *
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
                toReturn =convertFromString(expectedClass, (String) originalObject);
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
        if (expectedClass.isAssignableFrom(Boolean.class) || expectedClass.isAssignableFrom(boolean.class)) {
            return parseBoolean(originalObject);
        } else if (expectedClass.isAssignableFrom(Integer.class) || expectedClass.isAssignableFrom(int.class)) {
            return Integer.parseInt(originalObject);
        } else if (expectedClass.isAssignableFrom(Long.class) || expectedClass.isAssignableFrom(long.class)) {
            return Long.parseLong(originalObject);
        } else if (expectedClass.isAssignableFrom(Double.class) || expectedClass.isAssignableFrom(double.class)) {
            return Double.parseDouble(originalObject);
        } else if (expectedClass.isAssignableFrom(Float.class) || expectedClass.isAssignableFrom(float.class)) {
            return Float.parseFloat(originalObject);
        } else if (expectedClass.isAssignableFrom(Character.class) || expectedClass.isAssignableFrom(char.class)) {
            return parseChar(originalObject);
        } else if (expectedClass.isAssignableFrom(Byte.class) || expectedClass.isAssignableFrom(byte.class)) {
            return Byte.parseByte(originalObject);
        } else if (expectedClass.isAssignableFrom(Short.class) || expectedClass.isAssignableFrom(short.class)) {
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
