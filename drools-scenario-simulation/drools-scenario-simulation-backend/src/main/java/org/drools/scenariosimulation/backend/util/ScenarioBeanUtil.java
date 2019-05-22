/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.drools.scenariosimulation.backend.util;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.scenariosimulation.backend.runner.ScenarioException;

public class ScenarioBeanUtil {

    private static final Map<String, Class<?>> primitiveMap = new HashMap<>();

    static {
        primitiveMap.put("boolean", boolean.class);
        primitiveMap.put("int", int.class);
        primitiveMap.put("long", long.class);
        primitiveMap.put("double", double.class);
        primitiveMap.put("float", float.class);
        primitiveMap.put("char", char.class);
        primitiveMap.put("byte", byte.class);
        primitiveMap.put("short", short.class);
    }

    private ScenarioBeanUtil() {
    }

    @SuppressWarnings("unchecked")
    public static <T> T fillBean(String className, Map<List<String>, Object> params, ClassLoader classLoader) {

        Class<T> clazz = loadClass(className, classLoader);
        T beanToFill = newInstance(clazz);

        for (Map.Entry<List<String>, Object> param : params.entrySet()) {
            try {
                fillProperty(beanToFill, param.getKey(), param.getValue());
            } catch (ReflectiveOperationException e) {
                throw new ScenarioException(new StringBuilder().append("Impossible to fill ").append(className)
                                                    .append(" with the provided properties").toString(), e);
            }
        }

        return beanToFill;
    }

    private static <T> void fillProperty(T beanToFill, List<String> steps, Object propertyValue) throws ReflectiveOperationException {
        List<String> pathToProperty = steps.subList(0, steps.size() - 1);
        String lastStep = steps.get(steps.size() - 1);

        Object currentObject = beanToFill;
        if (pathToProperty.size() > 0) {
            ScenarioBeanWrapper<?> scenarioBeanWrapper = navigateToObject(beanToFill, pathToProperty, true);
            currentObject = scenarioBeanWrapper.getBean();
        }

        Field last = currentObject.getClass().getDeclaredField(lastStep);
        last.setAccessible(true);
        last.set(currentObject, propertyValue);
    }

    public static ScenarioBeanWrapper<?> navigateToObject(Object rootObject, List<String> steps) {
        return navigateToObject(rootObject, steps, true);
    }

    public static ScenarioBeanWrapper<?> navigateToObject(Object rootObject, List<String> steps, boolean createIfNull) {
        Class<?> currentClass = rootObject != null ? rootObject.getClass() : null;
        Object currentObject = rootObject;

        for (String step : steps) {
            Field declaredField = null;
            try {
                if (currentObject == null) {
                    throw new ScenarioException(new StringBuilder().append("Impossible to reach field ")
                                                        .append(step).append(" because a step is not instantiated")
                                                        .toString());
                }
                declaredField = currentClass.getDeclaredField(step);
            } catch (NoSuchFieldException e) {
                throw new ScenarioException(new StringBuilder().append("Impossible to find field with name '")
                                                    .append(step).append("' in class ")
                                                    .append(currentClass.getCanonicalName()).toString(), e);
            }
            declaredField.setAccessible(true);
            currentClass = declaredField.getType();
            try {
                currentObject = getFieldValue(declaredField, currentObject, createIfNull);
            } catch (ReflectiveOperationException e) {
                throw new ScenarioException(new StringBuilder().append("Impossible to get or create class ")
                                                    .append(currentClass.getCanonicalName()).toString());
            }
        }

        return new ScenarioBeanWrapper<>(currentObject, currentClass);
    }

    private static Object getFieldValue(Field declaredField, Object currentObject, boolean createIfNull) throws ReflectiveOperationException {
        Object value = declaredField.get(currentObject);
        if (value == null && createIfNull) {
            value = newInstance(declaredField.getType());
            declaredField.set(currentObject, value);
        }
        return value;
    }

    private static <T> T newInstance(Class<T> clazz) {
        try {
            return clazz.newInstance();
        } catch (ReflectiveOperationException e) {
            throw new ScenarioException(new StringBuilder().append("Class ").append(clazz.getCanonicalName())
                                                .append(" has no empty constructor").toString(), e);
        }
    }

    public static Object convertValue(String className, Object cleanValue, ClassLoader classLoader) {
        if (!isPrimitive(className) && cleanValue == null) {
            return null;
        }

        Class<?> clazz = loadClass(className, classLoader);

        // if it is not a String, it has to be an instance of the desired type
        if (!(cleanValue instanceof String)) {
            if (clazz.isInstance(cleanValue)) {
                return cleanValue;
            }
            throw new IllegalArgumentException(new StringBuilder().append("Object ").append(cleanValue)
                                                       .append(" is not a String or an instance of ").append(className).toString());
        }

        String value = (String) cleanValue;

        if (clazz.isAssignableFrom(String.class)) {
            return value;
        } else if (clazz.isAssignableFrom(Boolean.class) || clazz.isAssignableFrom(boolean.class)) {
            return parseBoolean(value);
        } else if (clazz.isAssignableFrom(Integer.class) || clazz.isAssignableFrom(int.class)) {
            return Integer.parseInt(cleanStringForNumberParsing(value));
        } else if (clazz.isAssignableFrom(Long.class) || clazz.isAssignableFrom(long.class)) {
            return Long.parseLong(cleanStringForNumberParsing(value));
        } else if (clazz.isAssignableFrom(Double.class) || clazz.isAssignableFrom(double.class)) {
            return Double.parseDouble(cleanStringForNumberParsing(value));
        } else if (clazz.isAssignableFrom(Float.class) || clazz.isAssignableFrom(float.class)) {
            return Float.parseFloat(cleanStringForNumberParsing(value));
        } else if (clazz.isAssignableFrom(Character.class) || clazz.isAssignableFrom(char.class)) {
            return parseChar(value);
        } else if (clazz.isAssignableFrom(Byte.class) || clazz.isAssignableFrom(byte.class)) {
            return parseByte(value);
        } else if (clazz.isAssignableFrom(Short.class) || clazz.isAssignableFrom(short.class)) {
            return Short.parseShort(cleanStringForNumberParsing(value));
        } else if (clazz.isAssignableFrom(LocalDate.class)) {
            return LocalDate.parse(value, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }

        throw new IllegalArgumentException(new StringBuilder().append("Class ").append(className)
                                                   .append(" is not supported").toString());
    }

    public static String revertValue(Object cleanValue) {
        if (cleanValue == null) {
            return "null";
        }

        Class<?> clazz = cleanValue.getClass();

        if (clazz.isAssignableFrom(String.class)) {
            return String.valueOf(cleanValue);
        } else if (clazz.isAssignableFrom(Boolean.class) || clazz.isAssignableFrom(boolean.class)) {
            return Boolean.toString((Boolean) cleanValue);
        } else if (clazz.isAssignableFrom(Integer.class) || clazz.isAssignableFrom(int.class)) {
            return Integer.toString((Integer) cleanValue);
        } else if (clazz.isAssignableFrom(Long.class) || clazz.isAssignableFrom(long.class)) {
            return cleanValue + "L";
        } else if (clazz.isAssignableFrom(Double.class) || clazz.isAssignableFrom(double.class)) {
            return cleanValue + "D";
        } else if (clazz.isAssignableFrom(Float.class) || clazz.isAssignableFrom(float.class)) {
            return cleanValue + "F";
        } else if (clazz.isAssignableFrom(Character.class) || clazz.isAssignableFrom(char.class)) {
            return String.valueOf(cleanValue);
        } else if (clazz.isAssignableFrom(Byte.class) || clazz.isAssignableFrom(byte.class)) {
            return String.valueOf(cleanValue);
        } else if (clazz.isAssignableFrom(Short.class) || clazz.isAssignableFrom(short.class)) {
            return String.valueOf(cleanValue);
        } else if (clazz.isAssignableFrom(LocalDate.class)) {
            LocalDate localDate = (LocalDate) cleanValue;
            return String.format("%04d-%02d-%02d", localDate.getYear(), localDate.getMonthValue(), localDate.getDayOfMonth());
        } else {
            return String.valueOf(cleanValue);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> Class<T> loadClass(String className, ClassLoader classLoader) {
        if (isPrimitive(className)) {
            return (Class<T>) primitiveMap.get(className);
        }
        try {
            return (Class<T>) classLoader.loadClass(className);
        } catch (ClassNotFoundException | NullPointerException e) {
            throw new ScenarioException(new StringBuilder().append("Impossible to load class ").append(className).toString(), e);
        }
    }

    private static boolean isPrimitive(String className) {
        return primitiveMap.containsKey(className);
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
            throw new IllegalArgumentException("Impossible to transform " + value + "as char");
        }
        return value.charAt(0);
    }

    private static byte parseByte(String value) {
        if (value == null || value.length() != 1) {
            throw new IllegalArgumentException("Impossible to transform " + value + "as byte");
        }
        return value.getBytes()[0];
    }

    private static String cleanStringForNumberParsing(String rawValue) {
        return rawValue.replaceAll("(-)\\s*([0-9])", "$1$2");
    }
}
