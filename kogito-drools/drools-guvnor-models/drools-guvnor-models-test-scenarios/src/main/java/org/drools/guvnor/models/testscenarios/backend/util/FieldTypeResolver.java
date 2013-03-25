package org.drools.guvnor.models.testscenarios.backend.util;

import java.lang.reflect.Method;

public class FieldTypeResolver {

    public static Class<?> getFieldType(String fieldName, Object factObject) {
        for (Method method : factObject.getClass().getDeclaredMethods()) {
            if (hasMutator(fieldName, method)) {
                return method.getParameterTypes()[0];
            }
        }
        throw new IllegalArgumentException("No field named: " + fieldName);
    }

    public static boolean isDate(String fieldName, Object factObject) {
        for (Method method : factObject.getClass().getDeclaredMethods()) {
            if (hasMutator(fieldName, method)) {
                if (java.util.Date.class.isAssignableFrom(method.getParameterTypes()[0])) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean hasMutator(String fieldName, Method method) {
        if (method.getName().equals(fieldName) || method.getName().equals("set" + capitalize(fieldName))) {
            if (method.getParameterTypes().length == 1) {
                return true;
            }
        }
        return false;
    }

    private static String capitalize(String fieldName) {
        if (fieldName.length() == 0) {
            return "";
        } else if (fieldName.length() == 1) {
            return fieldName.toUpperCase();
        } else {
            String firstLetter = fieldName.substring(0, 1);
            String tail = fieldName.substring(1);
            return firstLetter.toUpperCase() + tail;
        }
    }
}
