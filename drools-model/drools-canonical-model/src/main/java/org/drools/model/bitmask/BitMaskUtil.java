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

package org.drools.model.bitmask;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.drools.model.BitMask;
import org.drools.model.DomainClassMetadata;

public class BitMaskUtil {
    public static final int TRAITABLE_BIT = 0;
    public static final int CUSTOM_BITS_OFFSET = 1;
    public static final String TRAITSET_FIELD_NAME = "__$$dynamic_traits_map$$";

    private static final Map<Class<?>, List<String>> accessiblePropertiesCache = new HashMap<>();

    public static BitMask calculatePatternMask(DomainClassMetadata metadata, boolean isPositive, String... listenedProperties) {
        if (listenedProperties == null) {
            return EmptyBitMask.get();
        }

        BitMask mask = getEmptyPropertyReactiveMask( metadata.getPropertiesSize() );
        for (String propertyName : listenedProperties) {
            if (propertyName.equals(isPositive ? "*" : "!*")) {
                return AllSetBitMask.get();
            }
            if (propertyName.startsWith("!") ^ !isPositive) {
                continue;
            }
            if (propertyName.equals( TRAITSET_FIELD_NAME )) {
                mask = mask.set( TRAITABLE_BIT );
                continue;
            }
            if (!isPositive) {
                propertyName = propertyName.substring(1);
            }

            mask = setPropertyOnMask(mask, metadata.getPropertyIndex( propertyName ));
        }
        return mask;
    }

    public static BitMask calculatePatternMask( Class<?> clazz, Collection<String> listenedProperties ) {
        List<String> accessibleProperties = getAccessibleProperties( clazz );
        if (listenedProperties == null) {
            return EmptyBitMask.get();
        }

        BitMask mask = getEmptyPropertyReactiveMask(accessibleProperties.size());
        if (listenedProperties.contains( TRAITSET_FIELD_NAME )) {
            mask = mask.set(TRAITABLE_BIT);
        }
        for (String propertyName : listenedProperties) {
            if (propertyName.equals("*")) {
                return AllSetBitMask.get();
            }
            mask = setPropertyOnMask(mask, accessibleProperties, propertyName);
        }
        return mask;
    }

    private static BitMask getEmptyPropertyReactiveMask(int settablePropertiesSize) {
        return BitMask.getEmpty(settablePropertiesSize + CUSTOM_BITS_OFFSET);
    }

    private static BitMask setPropertyOnMask(BitMask mask, List<String> settableProperties, String propertyName) {
        int index = settableProperties.indexOf(propertyName);
        if (index < 0) {
            throw new RuntimeException("Unknown property: " + propertyName);
        }
        return setPropertyOnMask(mask, index);
    }

    private static BitMask setPropertyOnMask(BitMask mask, int index) {
        return mask.set(index + CUSTOM_BITS_OFFSET);
    }

    public static boolean isAccessibleProperties( Class<?> clazz, String prop ) {
        return getAccessibleProperties( clazz ).contains( prop );
    }

    public static List<String> getAccessibleProperties( Class<?> clazz ) {
        return accessiblePropertiesCache.computeIfAbsent( clazz, BitMaskUtil::findAccessibleProperties );
    }

    private static List<String> findAccessibleProperties( Class<?> clazz ) {
        Set<PropertyInClass> props = new TreeSet<>();
        for (Method m : clazz.getMethods()) {
            if (m.getParameterTypes().length == 0) {
                String propName = getter2property(m.getName());
                if (propName != null && !propName.equals( "class" )) {
                    props.add( new PropertyInClass( propName, m.getDeclaringClass() ) );
                }
            }
        }

        for (Field f : clazz.getFields()) {
            if ( Modifier.isPublic( f.getModifiers() ) && !Modifier.isStatic( f.getModifiers() ) ) {
                props.add( new PropertyInClass( f.getName(), f.getDeclaringClass() ) );
            }
        }

        List<String> accessibleProperties = new ArrayList<>();
        for ( PropertyInClass setter : props ) {
            accessibleProperties.add(setter.setter);
        }
        return accessibleProperties;
    }

    private static String getter2property(String methodName) {
        if (methodName.startsWith("get") && methodName.length() > 3) {
            return Character.toLowerCase(methodName.charAt(3)) + methodName.substring(4);
        }
        if (methodName.startsWith("is") && methodName.length() > 2) {
            return Character.toLowerCase(methodName.charAt(2)) + methodName.substring(3);
        }
        return null;
    }

    private static class PropertyInClass implements Comparable {
        private final String setter;
        private final Class<?> clazz;

        private PropertyInClass( String setter, Class<?> clazz ) {
            this.setter = setter;
            this.clazz = clazz;
        }

        public int compareTo(Object o) {
            PropertyInClass other = (PropertyInClass) o;
            if (clazz == other.clazz) {
                return setter.compareTo(other.setter);
            }
            return clazz.isAssignableFrom(other.clazz) ? -1 : 1;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof PropertyInClass)) {
                return false;
            }
            PropertyInClass other = (PropertyInClass) obj;
            return clazz == other.clazz && setter.equals(other.setter);
        }

        @Override
        public int hashCode() {
            return 29 * clazz.hashCode() + 31 * setter.hashCode();
        }
    }

    private BitMaskUtil() {
        // It is not allowed to create instances of util classes.
    }
}
