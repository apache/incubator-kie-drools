/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.modelcompiler.util;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import org.drools.base.util.PropertyReactivityUtil;

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

}
