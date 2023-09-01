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
package org.kie.dmn.feel.lang.impl;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZonedDateTime;
import java.time.chrono.ChronoPeriod;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Stream;

import org.kie.dmn.feel.lang.CompositeType;
import org.kie.dmn.feel.lang.FEELProperty;
import org.kie.dmn.feel.lang.FEELType;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.feel.runtime.FEELFunction;
import org.kie.dmn.feel.runtime.Range;
import org.kie.dmn.feel.runtime.UnaryTest;
import org.kie.dmn.feel.util.EvalHelper;

import static java.util.stream.Collectors.toMap;

public class JavaBackedType implements CompositeType {

    private static Map<Class<?>, JavaBackedType> cache = new ConcurrentHashMap<>();
    
    private static Set<Method> javaObjectMethods = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(Object.class.getMethods())));
    
    private Class<?> wrapped;
    private Map<String, Type> properties;

    private JavaBackedType(Class<?> class1) {
        this.wrapped = class1;
    }

    /**
     * If method m is annotated with FEELProperty, will return FEELProperty.value, otherwise empty.
     */
    private static Optional<String> methodToCustomProperty(Method m) {
        return Optional.ofNullable(m.getAnnotation(FEELProperty.class)).map(a->a.value());
    }
    
    /**
     * If clazz can be represented as a JavaBackedType, returns a JavaBackedType for representing clazz.
     * If clazz can not be represented as a JavaBackedType, returns BuiltInType.UNKNOWN.
     * This method performs memoization when necessary.
     * @param clazz the class to be represented as JavaBackedType
     * @return JavaBackedType representing clazz or BuiltInType.UNKNOWN
     */
    public static Type of(Class<?> clazz) {
        return Optional.ofNullable( (Type) cache.computeIfAbsent( clazz, JavaBackedType::createIfAnnotated ) ).orElse( BuiltInType.UNKNOWN );
    }
    
    /**
     * For internal use, returns a new JavaBackedType if clazz can be represented as such, returns null otherwise.
     */
    private static JavaBackedType createIfAnnotated(Class<?> clazz) {
        if (clazz.isAnnotationPresent(FEELType.class) || Stream.of(clazz.getMethods()).anyMatch(m->m.getAnnotation(FEELProperty.class)!=null)) {
            return new JavaBackedType(clazz) ;
        }
        return null;
    }

    @Override
    public String getName() {
        return wrapped.getName();
    }

    public Class<?> getWrapped() {
        return wrapped;
    }

    @Override
    public Map<String, Type> getFields() {
        if (properties == null) {
            // make properties init lazy instead of constructor-time, so to avoid reentrant write lock on `cache`, which is hidden behind the call of ParserHelper.determineTypeFromClass.
            properties = Stream.of(wrapped.getMethods())
                               .filter(m -> Modifier.isPublic(m.getModifiers()) || Modifier.isProtected(m.getModifiers()))
                               .filter(m -> !javaObjectMethods.contains(m))
                               .flatMap(m -> Stream.<Function<Method, Optional<String>>> of(JavaBackedType::methodToCustomProperty, EvalHelper::propertyFromAccessor)
                                                   .map(f -> f.apply(m))
                                                   .filter(Optional::isPresent)
                                                   .map(p -> new Property(p.get(), determineTypeFromClass(m.getReturnType()))))
                               .collect(toMap((Property p) -> p.name, p -> p.type, (p1, p2) -> { throw new IllegalArgumentException(); }, LinkedHashMap::new));
        }
        return this.properties;
    }

    private static class Property {
        public final String name;
        public final Type type;

        public Property( String name, Type type ) {
            this.name = name;
            this.type = type;
        }
    }

    @Override
    public boolean isInstanceOf(Object o) {
        return wrapped.getClass().isInstance(o);
    }

    @Override
    public boolean isAssignableValue(Object value) {
        return value == null || wrapped.getClass().isAssignableFrom(value.getClass());
    }

    public static Type determineTypeFromClass( Class<?> clazz ) {
        if( clazz == null ) {
            return BuiltInType.UNKNOWN;
        } else if( Number.class.isAssignableFrom(clazz) ) {
            return BuiltInType.NUMBER;
        } else if( String.class.isAssignableFrom(clazz) || Character.class.isAssignableFrom(clazz) ) {
            return BuiltInType.STRING;
        } else if( LocalDate.class.isAssignableFrom(clazz) ) {
            return BuiltInType.DATE;
        } else if( LocalTime.class.isAssignableFrom(clazz) || OffsetTime.class.isAssignableFrom(clazz) ) {
            return BuiltInType.TIME;
        } else if( ZonedDateTime.class.isAssignableFrom(clazz) || OffsetDateTime.class.isAssignableFrom(clazz) || LocalDateTime.class.isAssignableFrom(clazz) || java.util.Date.class.isAssignableFrom(clazz) ) {
            return BuiltInType.DATE_TIME;
        } else if (Duration.class.isAssignableFrom(clazz) || ChronoPeriod.class.isAssignableFrom(clazz)) {
            return BuiltInType.DURATION;
        } else if( Boolean.class.isAssignableFrom(clazz) ) {
            return BuiltInType.BOOLEAN;
        } else if( UnaryTest.class.isAssignableFrom(clazz) ) {
            return BuiltInType.UNARY_TEST;
        } else if( Range.class.isAssignableFrom(clazz) ) {
            return BuiltInType.RANGE;
        } else if( FEELFunction.class.isAssignableFrom(clazz) ) {
            return BuiltInType.FUNCTION;
        } else if( List.class.isAssignableFrom(clazz) ) {
            return BuiltInType.LIST;
        } else if( Map.class.isAssignableFrom(clazz) ) {     // TODO not so sure about this one..
            return BuiltInType.CONTEXT;
        } 
        return of( clazz ); 
    }
}
