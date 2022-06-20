/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.internal.utils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Objects;
import java.util.function.Function;

public class ConversionUtils {

    private ConversionUtils() {
    }

    public static <T> T convert(Object value, Class<T> clazz) {
        return convert(value, clazz, Object::toString);
    }

    /**
     * Converts an object to an instance of the provided class
     * 
     * @param <T>
     * @param value
     * @param clazz
     * @param stringConverter
     * @return
     */
    public static <T> T convert(Object value, Class<T> clazz, Function<Object, String> stringConverter) {
        if (value == null || clazz.isAssignableFrom(value.getClass())) {
            return clazz.cast(value);
        } else {
            for (Method method : clazz.getMethods()) {
                int modifiers = method.getModifiers();
                if (Modifier.isStatic(modifiers) && method.getParameterCount() == 1 && method.getParameterTypes()[0].equals(String.class) && clazz.isAssignableFrom(method.getReturnType())) {
                    try {
                        return clazz.cast(method.invoke(null, stringConverter.apply(value)));
                    } catch (ReflectiveOperationException e) {
                        // see end method
                    }
                }
            }
            try {
                return clazz.getConstructor(String.class).newInstance(stringConverter.apply(value));
            } catch (ReflectiveOperationException e) {
                // see end method
            }
        }
        throw new IllegalArgumentException(value + " cannot be converted to " + clazz.getName());
    }

    /**
     * Convert to camel case
     * 
     * @param text
     * @return
     */
    public static String toCamelCase(String text) {
        if (text == null) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        boolean convertNextCharToUpper = false;

        for (int i = 0; i < text.length(); i++) {
            char currentChar = text.charAt(i);
            if (!Character.isLetterOrDigit(currentChar)) {
                convertNextCharToUpper = true;
            } else if (convertNextCharToUpper) {
                builder.append(Character.toUpperCase(currentChar));
                convertNextCharToUpper = false;
            } else {
                builder.append(currentChar);
                convertNextCharToUpper = false;
            }
        }
        if (builder.length() > 0) {
            builder.setCharAt(0, Character.toLowerCase(builder.charAt(0)));
        }
        return builder.toString();
    }

    /**
     * Concatenate two paths using / as separator
     * 
     * @param onePath
     * @param anotherPath
     * @return
     */
    public static String concatPaths(String onePath, String anotherPath) {
        return concatPaths(onePath, anotherPath, "/");
    }

    /**
     * Concatenate two paths using a separator
     * 
     * @param onePath
     * @param anotherPath
     * @param concatChars separator to use
     * @return
     */
    public static String concatPaths(String onePath, String anotherPath, String concatChars) {
        if (anotherPath.startsWith(concatChars)) {
            if (onePath.endsWith(concatChars)) {
                return onePath.concat(anotherPath.substring(concatChars.length()));
            } else {
                return onePath.concat(anotherPath);
            }
        } else {
            if (onePath.endsWith(concatChars)) {
                return onePath.concat(anotherPath);
            } else {
                return onePath.concat(concatChars).concat(anotherPath);
            }
        }
    }

    /**
     * Check empty string
     * 
     * @param value
     * @return
     */
    public static boolean isEmpty(String value) {
        return Objects.isNull(value) || value.isBlank();
    }
}
