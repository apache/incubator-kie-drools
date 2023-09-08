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
package org.drools.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.drools.util.StringUtils.splitArgumentsList;

public interface TypeResolver {
    Set<String> getImports();

    void addImport( String importEntry );

    void addImplicitImport( String importEntry );

    Class<?> resolveType( String className ) throws ClassNotFoundException;

    Class<?> resolveType( String className, ClassFilter classFilter ) throws ClassNotFoundException;

    void registerClass( String className, Class<?> clazz );

    /**
     * This will return the fully qualified type name (including the namespace).
     * Eg, if it was a pojo org.drools.core.test.model.Cheese, then if you passed in "Cheese" you should get back
     * "org.drools.core.test.model.Cheese"
     */
    String getFullTypeName( String shortName ) throws ClassNotFoundException;

    ClassLoader getClassLoader();

    interface ClassFilter {
        boolean accept( Class<?> clazz );
    }

    AcceptAllClassFilter ACCEPT_ALL_CLASS_FILTER = new AcceptAllClassFilter();
    class AcceptAllClassFilter implements ClassFilter {
        @Override
        public boolean accept(Class<?> clazz) {
            return true;
        }
    }

    ExcludeAnnotationClassFilter EXCLUDE_ANNOTATION_CLASS_FILTER = new ExcludeAnnotationClassFilter();
    class ExcludeAnnotationClassFilter implements ClassFilter {
        @Override
        public boolean accept(Class<?> clazz) {
            return !Annotation.class.isAssignableFrom(clazz);
        }
    }

    OnlyAnnotationClassFilter ONLY_ANNOTATION_CLASS_FILTER = new OnlyAnnotationClassFilter();
    class OnlyAnnotationClassFilter implements ClassFilter {
        @Override
        public boolean accept(Class<?> clazz) {
            return Annotation.class.isAssignableFrom(clazz);
        }
    }

    default Type resolveParametrizedType( String typeName ) throws ClassNotFoundException {
        int genericsStart = typeName.indexOf('<');
        if (typeName.indexOf('<') < 0) {
            return resolveType(typeName);
        }
        String rawName = typeName.substring(0, genericsStart).trim();
        Type rawType = resolveType(rawName);

        String typeArguments = typeName.substring(genericsStart+1, typeName.lastIndexOf('>')).trim();
        List<String> args = splitArgumentsList(typeArguments);
        Type[] types = new Type[args.size()];
        for (int i = 0; i < types.length; i++) {
            try {
                types[i] = resolveParametrizedType(args.get(i));
            } catch (ClassNotFoundException cnfe) {
                // parametric types with wildcards are not managed, so fallback to the rawType if it meets one
                return rawType;
            }
        }
        return new ParsedParameterizedType(rawType, types);
    }

    class ParsedParameterizedType implements ParameterizedType {

        private final Type rawType;
        private final Type[] typeArguments;

        public ParsedParameterizedType(Type rawType, Type[] typeArguments) {
            this.rawType = rawType;
            this.typeArguments = typeArguments;
        }

        @Override
        public Type[] getActualTypeArguments() {
            return typeArguments;
        }

        @Override
        public Type getRawType() {
            return rawType;
        }

        @Override
        public Type getOwnerType() {
            return null;
        }

        @Override
        public String toString() {
            String argsString = Stream.of(typeArguments).map(this::getCanonicalTypeName).collect(Collectors.joining(", "));
            return getCanonicalTypeName(rawType) + "<" + argsString + ">";
        }

        private String getCanonicalTypeName(Type type) {
            return type instanceof Class ? ((Class<?>) type).getCanonicalName() : type.getTypeName();
        }
    }
}
