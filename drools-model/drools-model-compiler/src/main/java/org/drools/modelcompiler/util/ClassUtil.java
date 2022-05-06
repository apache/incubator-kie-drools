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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import org.drools.core.util.PropertyReactivityUtil;
import org.drools.util.MethodUtils;

public class ClassUtil {

    private static final Map<Class<?>, List<String>> ACCESSIBLE_PROPS_CACHE = Collections.synchronizedMap( new WeakHashMap<>() );

    private static final Map<Class<?>, List<String>> ACCESSIBLE_PROPS_CACHE_INCLUDING_NON_GETTER = Collections.synchronizedMap( new WeakHashMap<>() );

    public static String asJavaSourceName( Class<?> clazz ) {
        return clazz.getCanonicalName().replace( '.', '_' );
    }

    public static Class<?> javaSourceNameToClass(String javaSourceName) throws ClassNotFoundException {
        String fqcn = javaSourceName.replace('_', '.');
        return Class.forName(fqcn);
    }

    public static List<String> getAccessibleProperties( Class<?> clazz ) {
        return ACCESSIBLE_PROPS_CACHE.computeIfAbsent( clazz, PropertyReactivityUtil::getAccessibleProperties );
    }

    public static boolean isAccessiblePropertiesIncludingNonGetterValueMethod( Class<?> clazz, String prop ) {
        return getAccessiblePropertiesIncludingNonGetterValueMethod( clazz ).contains( prop );
    }

    // ACCESSIBLE_PROPS_CACHE_INCLUDING_NON_GETTER must contain the same order of props in ClassUtils.getAccessibleProperties() first. Then NON_GETTER methods are listed at the end.
    // So index and property reactivity can share the same ACCESSIBLE_PROPS_CACHE_INCLUDING_NON_GETTER in DamainClassMetadata.getPropertyIndex()
    public static List<String> getAccessiblePropertiesIncludingNonGetterValueMethod( Class<?> clazz ) {
        return ACCESSIBLE_PROPS_CACHE_INCLUDING_NON_GETTER.computeIfAbsent( clazz, PropertyReactivityUtil::getAccessiblePropertiesIncludingNonGetterValueMethod );
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
