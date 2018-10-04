/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.domain.common.accessor;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * Fast and easy access to a {@link Member} of a bean,
 * which is a property (with a getter and optional setter {@link Method}) or a {@link Field}.
 * @see ReflectionBeanPropertyMemberAccessor
 * @see ReflectionFieldMemberAccessor
 * @see ReflectionMethodMemberAccessor
 */
public interface MemberAccessor extends AnnotatedElement {

    Class<?> getDeclaringClass();

    String getName();

    Class<?> getType();

    /**
     * As defined by {@link Method#getGenericReturnType()} and {@link Field#getGenericType()}.
     * @return never null
     */
    Type getGenericType();

    Object executeGetter(Object bean);

    boolean supportSetter();

    void executeSetter(Object bean, Object value);

    String getSpeedNote();

}
