/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.workbench.models.testscenarios.backend.util;

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
