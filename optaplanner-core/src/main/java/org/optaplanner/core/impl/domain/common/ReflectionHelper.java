/*
 * Copied from the Hibernate Validator project
 * Original authors: Hardy Ferentschik, Gunnar Morling and Kevin Pollet.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.core.impl.domain.common;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
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

    /**
     * Returns the JavaBeans property name of the given member.
     * @param member never null
     * @return null if the member is neither a field nor a getter method according to the JavaBeans standard
     */
    public static String getPropertyName(Member member) {
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

    private ReflectionHelper() {
    }

}

