/*
 * Copied from the Hibernate Validator project
 * Original authors: Hardy Ferentschik, Gunnar Morling and Kevin Pollet.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.core.impl.domain.common;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Avoids the usage of Introspector to work on Android too.
 */
public final class ReflectionHelper {

    private static final String PROPERTY_ACCESSOR_PREFIX_GET = "get";
    private static final String PROPERTY_ACCESSOR_PREFIX_IS = "is";
    private static final String[] PROPERTY_ACCESSOR_PREFIXES = {
            PROPERTY_ACCESSOR_PREFIX_GET,
            PROPERTY_ACCESSOR_PREFIX_IS
    };

    private static final String PROPERTY_MUTATOR_PREFIX = "set";

    /**
     * Returns the JavaBeans property name of the given member.
     * @param member never null
     * @return null if the member is neither a field nor a getter method according to the JavaBeans standard
     */
    public static String getGetterPropertyName(Member member) {
        if (member instanceof Field) {
            return member.getName();
        } else if (member instanceof Method) {
            String methodName = member.getName();
            for (String prefix : PROPERTY_ACCESSOR_PREFIXES) {
                if (methodName.startsWith(prefix)) {
                    return decapitalizePropertyName(methodName.substring(prefix.length()));
                }
            }
        }
        return null;
    }

    private static String decapitalizePropertyName(String propertyName) {
        if (propertyName.isEmpty() || startsWithSeveralUpperCaseLetters(propertyName)) {
            return propertyName;
        } else {
            return propertyName.substring(0, 1).toLowerCase() + propertyName.substring(1);
        }
    }

    private static boolean startsWithSeveralUpperCaseLetters(String propertyName) {
        return propertyName.length() > 1 &&
                Character.isUpperCase(propertyName.charAt(0)) &&
                Character.isUpperCase(propertyName.charAt(1));
    }

    /**
     * Checks whether the given method is a valid getter method according to the JavaBeans standard.
     * @param method never null
     * @return true if the given method is a getter method
     */
    public static boolean isGetterMethod(Method method) {
        if (method.getParameterTypes().length != 0) {
            return false;
        }
        String methodName = method.getName();
        if (methodName.startsWith(PROPERTY_ACCESSOR_PREFIX_GET) && method.getReturnType() != void.class) {
            return true;
        } else if (methodName.startsWith(PROPERTY_ACCESSOR_PREFIX_IS) && method.getReturnType() == boolean.class) {
            return true;
        }
        return false;
    }

    /**
     * @param containingClass never null
     * @param propertyName never null
     * @return true if that getter exists
     */
    public static boolean hasGetterMethod(Class<?> containingClass, String propertyName) {
        return getGetterMethod(containingClass, propertyName) != null;
    }

    /**
     * @param containingClass never null
     * @param propertyName never null
     * @return sometimes null
     */
    public static Method getGetterMethod(Class<?> containingClass, String propertyName) {
        String getterName = PROPERTY_ACCESSOR_PREFIX_GET
                + (propertyName.isEmpty() ? "" : propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1));
        try {
            return containingClass.getMethod(getterName);
        } catch (NoSuchMethodException e) {
            // intentionally empty
        }
        String isserName = PROPERTY_ACCESSOR_PREFIX_IS
                + (propertyName.isEmpty() ? "" : propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1));
        try {
            Method method = containingClass.getMethod(isserName);
            if (method.getReturnType() == boolean.class) {
                return method;
            }
        } catch (NoSuchMethodException e) {
            // intentionally empty
        }
        return null;
    }

    /**
     * @param containingClass never null
     * @param fieldName never null
     * @return true if that field exists
     */
    public static boolean hasField(Class<?> containingClass, String fieldName) {
        return getField(containingClass, fieldName) != null;
    }

    /**
     * @param containingClass never null
     * @param fieldName never null
     * @return sometimes null
     */
    public static Field getField(Class<?> containingClass, String fieldName) {
        try {
            return containingClass.getField(fieldName);
        } catch (NoSuchFieldException e) {
            return null;
        }
    }

    /**
     * @param containingClass never null
     * @param propertyType never null
     * @param propertyName never null
     * @return null if it doesn't exist
     */
    public static Method getSetterMethod(Class<?> containingClass, Class<?> propertyType, String propertyName) {
        String setterName = PROPERTY_MUTATOR_PREFIX
                + (propertyName.isEmpty() ? "" : propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1));
        try {
            return containingClass.getMethod(setterName, propertyType);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    /**
     * @param containingClass never null
     * @param propertyName never null
     * @return null if it doesn't exist
     */
    public static Method getSetterMethod(Class<?> containingClass, String propertyName) {
        String setterName = PROPERTY_MUTATOR_PREFIX
                + (propertyName.isEmpty() ? "" : propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1));
        Method[] methods = Arrays.stream(containingClass.getMethods())
                .filter(method -> method.getName().equals(setterName))
                .toArray(Method[]::new);
        if (methods.length == 0) {
            return null;
        }
        if (methods.length > 1) {
            throw new IllegalStateException("The containingClass (" + containingClass
                    + ") has multiple setter methods (" + Arrays.toString(methods)
                    + ") with the propertyName (" + propertyName + ").");
        }
        return methods[0];
    }

    public static boolean isMethodOverwritten(Method parentMethod, Class<?> childClass) {
        Method leafMethod;
        try {
            leafMethod = childClass.getDeclaredMethod(parentMethod.getName(), parentMethod.getParameterTypes());
        } catch (NoSuchMethodException e) {
            return false;
        }
        return !leafMethod.getDeclaringClass().equals(parentMethod.getClass());
    }

    public static void assertGetterMethod(Method getterMethod, Class<? extends Annotation> annotationClass) {
        if (getterMethod.getParameterTypes().length != 0) {
            throw new IllegalStateException("The getterMethod (" + getterMethod + ") with a "
                    + annotationClass.getSimpleName() + " annotation must not have any parameters ("
                    + Arrays.toString(getterMethod.getParameterTypes()) + ").");
        }
        String methodName = getterMethod.getName();
        if (methodName.startsWith(PROPERTY_ACCESSOR_PREFIX_GET)) {
            if (getterMethod.getReturnType() == void.class) {
                throw new IllegalStateException("The getterMethod (" + getterMethod + ") with a "
                        + annotationClass.getSimpleName() + " annotation must have a non-void return type ("
                        + getterMethod.getReturnType() + ").");
            }
        } else if (methodName.startsWith(PROPERTY_ACCESSOR_PREFIX_IS)) {
            if (getterMethod.getReturnType() != boolean.class) {
                throw new IllegalStateException("The getterMethod (" + getterMethod + ") with a "
                        + annotationClass.getSimpleName() + " annotation must have a primitive boolean return type ("
                        + getterMethod.getReturnType() + ") or use another prefix in its methodName ("
                        + methodName + ").");
            }
        } else {
            throw new IllegalStateException("The getterMethod (" + getterMethod + ") with a "
                    + annotationClass.getSimpleName() + " annotation has a methodName ("
                    + methodName + ") that does not start with a valid prefix ("
                    + Arrays.toString(PROPERTY_ACCESSOR_PREFIXES) + ").");
        }
    }

    public static void assertReadMethod(Method readMethod, Class<? extends Annotation> annotationClass) {
        if (readMethod.getParameterTypes().length != 0) {
            throw new IllegalStateException("The readMethod (" + readMethod + ") with a "
                    + annotationClass.getSimpleName() + " annotation must not have any parameters ("
                    + Arrays.toString(readMethod.getParameterTypes()) + ").");
        }
        if (readMethod.getReturnType() == void.class) {
            throw new IllegalStateException("The readMethod (" + readMethod + ") with a "
                    + annotationClass.getSimpleName() + " annotation must have a non-void return type ("
                    + readMethod.getReturnType() + ").");
        }
    }

    /**
     * @param type never null
     * @return true if it is a {@link Map}
     */
    public static boolean isMap(Type type) {
        if (type instanceof Class && Map.class.isAssignableFrom((Class<?>) type)) {
            return true;
        }
        if (type instanceof ParameterizedType) {
            return isMap(((ParameterizedType) type).getRawType());
        }
        if (type instanceof WildcardType) {
            Type[] upperBounds = ((WildcardType) type).getUpperBounds();
            return upperBounds.length != 0 && isMap(upperBounds[0]);
        }
        return false;
    }

    /**
     * @param type never null
     * @return true if it is a {@link List}
     */
    public static boolean isList(Type type) {
        if (type instanceof Class && List.class.isAssignableFrom((Class<?>) type)) {
            return true;
        }
        if (type instanceof ParameterizedType) {
            return isList(((ParameterizedType) type).getRawType());
        }
        if (type instanceof WildcardType) {
            Type[] upperBounds = ((WildcardType) type).getUpperBounds();
            return upperBounds.length != 0 && isList(upperBounds[0]);
        }
        return false;
    }

    public static List<Object> transformArrayToList(Object arrayObject) {
        if (arrayObject == null) {
            return null;
        }
        int arrayLength = Array.getLength(arrayObject);
        List<Object> list = new ArrayList<>(arrayLength);
        for (int i = 0; i < arrayLength; i++) {
            list.add(Array.get(arrayObject, i));
        }
        return list;
    }

    private ReflectionHelper() {
    }

}

