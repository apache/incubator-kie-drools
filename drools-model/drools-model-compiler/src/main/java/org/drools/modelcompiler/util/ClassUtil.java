/*
 * Copyright 2005 JBoss Inc
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

package org.drools.modelcompiler.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import org.drools.core.util.MethodUtils;

public class ClassUtil {

    private static final Map<Class<?>, List<String>> ACCESSIBLE_PROPS_CACHE = Collections.synchronizedMap( new WeakHashMap<>() );
    private static final Map<Class<?>, List<String>> PRIMITIVE_WRAPPER_PROPS_CACHE = Collections.synchronizedMap( new WeakHashMap<>() );

    static {
        List<String> numberValueMethods = new ArrayList<>();
        numberValueMethods.add("byteValue");
        numberValueMethods.add("shortValue");
        numberValueMethods.add("intValue");
        numberValueMethods.add("longValue");
        numberValueMethods.add("floatValue");
        numberValueMethods.add("doubleValue");
        PRIMITIVE_WRAPPER_PROPS_CACHE.put(Integer.class, numberValueMethods);
        PRIMITIVE_WRAPPER_PROPS_CACHE.put(Long.class, numberValueMethods);
        PRIMITIVE_WRAPPER_PROPS_CACHE.put(Double.class, numberValueMethods);
        PRIMITIVE_WRAPPER_PROPS_CACHE.put(Float.class, numberValueMethods);
        PRIMITIVE_WRAPPER_PROPS_CACHE.put(Short.class, numberValueMethods);
        PRIMITIVE_WRAPPER_PROPS_CACHE.put(Byte.class, numberValueMethods);
        PRIMITIVE_WRAPPER_PROPS_CACHE.put(Character.class, Arrays.asList("charValue"));
        PRIMITIVE_WRAPPER_PROPS_CACHE.put(Boolean.class, Arrays.asList("booleanValue"));
    }

    public static String asJavaSourceName( Class<?> clazz ) {
        return clazz.getCanonicalName().replace( '.', '_' );
    }

    public static boolean isAccessibleProperties( Class<?> clazz, String prop ) {
        return getAccessibleProperties( clazz ).contains( prop );
    }

    // Used for property reactivity. Primitive wrapper class doesn't return immutable property (e.g. "intValue")
    public static List<String> getAccessibleProperties( Class<?> clazz ) {
        return ACCESSIBLE_PROPS_CACHE.computeIfAbsent( clazz, org.drools.core.util.ClassUtils::getAccessibleProperties );
    }

    public static boolean isAccessiblePropertiesIncludingPrimitiveWrapper( Class<?> clazz, String prop ) {
        return getAccessiblePropertiesIncludingPrimitiveWrapper( clazz ).contains( prop );
    }

    // Used for DomainClassMetadata and index. Primitive wrapper class' immutable property (e.g. "intValue") can be indexed
    public static List<String> getAccessiblePropertiesIncludingPrimitiveWrapper( Class<?> clazz ) {
        if (isPrimitiveWrapper(clazz)) {
            return PRIMITIVE_WRAPPER_PROPS_CACHE.get(clazz);
        } else {
            return getAccessibleProperties(clazz);
        }
    }

    private static boolean isPrimitiveWrapper(Class<?> clazz) {
        return clazz == Integer.class ||
               clazz == Long.class ||
               clazz == Double.class ||
               clazz == Float.class ||
               clazz == Short.class ||
               clazz == Byte.class ||
               clazz == Character.class ||
               clazz == Boolean.class;
    }

    public static Type boxTypePrimitive(Type type) {
        if (type instanceof Class<?>) {
            return MethodUtils.boxPrimitive((Class<?>)type);
        } else {
            return type;
        }
    }

    public static boolean isAssignableFrom(Type from, Type to) {
        Class<?> fromClass = toRawClass( from );
        Class<?> toClass = toRawClass( to );
        return fromClass.isAssignableFrom(toClass) || MethodUtils.areBoxingCompatible(fromClass, toClass);
    }

    public static Class<?> toNonPrimitiveType(Class<?> c) {
        if (!c.isPrimitive()) return c;
        if (c == int.class) return Integer.class;
        if (c == long.class) return Long.class;
        if (c == double.class) return Double.class;
        if (c == float.class) return Float.class;
        if (c == short.class) return Short.class;
        if (c == byte.class) return Byte.class;
        if (c == char.class) return Character.class;
        if (c == boolean.class) return Boolean.class;
        return c;
    }

    public static Class<?> toRawClass(Type type) {
        if (type == null) {
            return null;
        }
        if (type instanceof Class<?>) {
            return ( Class ) type;
        }
        if (type instanceof ParameterizedType ) {
            return toRawClass( (( ParameterizedType ) type).getRawType() );
        }
        if (type instanceof TypeVariable ) {
            return Object.class;
        }
        throw new UnsupportedOperationException( "Unknown type " + type );
    }

    public static Type getTypeArgument(Type genericType, int index) {
        return genericType instanceof ParameterizedType ? (( ParameterizedType ) genericType).getActualTypeArguments()[index] : Object.class;
    }
}
