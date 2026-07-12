/*
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

package org.optaplanner.core.impl.domain.solution.cloner.gizmo;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.optaplanner.core.impl.domain.solution.cloner.DeepCloningUtils;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;

public final class GizmoCloningUtils {

    public static Set<Class<?>> getDeepClonedClasses(SolutionDescriptor<?> solutionDescriptor,
            Collection<Class<?>> entitySubclasses) {
        Set<Class<?>> deepClonedClassSet = new HashSet<>();
        Set<Class<?>> classesToProcess = new LinkedHashSet<>(solutionDescriptor.getEntityClassSet());
        classesToProcess.add(solutionDescriptor.getSolutionClass());
        classesToProcess.addAll(entitySubclasses);
        for (Class<?> clazz : classesToProcess) {
            deepClonedClassSet.add(clazz);
            for (Field field : getAllFields(clazz)) {
                deepClonedClassSet.addAll(getDeepClonedTypeArguments(solutionDescriptor, field.getGenericType()));
                if (DeepCloningUtils.isFieldDeepCloned(solutionDescriptor, field, clazz)) {
                    deepClonedClassSet.add(field.getType());
                }
            }
        }
        return deepClonedClassSet;
    }

    /**
     * @return never null
     */
    private static Set<Class<?>> getDeepClonedTypeArguments(SolutionDescriptor<?> solutionDescriptor, Type genericType) {
        // Check the generic type arguments of the field.
        // It is possible for fields and methods, but not instances.
        if (!(genericType instanceof ParameterizedType)) {
            return Collections.emptySet();
        }

        Set<Class<?>> deepClonedTypeArguments = new HashSet<>();
        ParameterizedType parameterizedType = (ParameterizedType) genericType;
        for (Type actualTypeArgument : parameterizedType.getActualTypeArguments()) {
            if (actualTypeArgument instanceof Class
                    && DeepCloningUtils.isClassDeepCloned(solutionDescriptor, (Class) actualTypeArgument)) {
                deepClonedTypeArguments.add((Class) actualTypeArgument);
            }
            deepClonedTypeArguments.addAll(getDeepClonedTypeArguments(solutionDescriptor, actualTypeArgument));
        }
        return deepClonedTypeArguments;
    }

    private static List<Field> getAllFields(Class<?> baseClass) {
        Class<?> clazz = baseClass;
        Stream<Field> memberStream = Stream.empty();
        while (clazz != null) {
            Stream<Field> fieldStream = Stream.of(clazz.getDeclaredFields());
            memberStream = Stream.concat(memberStream, fieldStream);
            clazz = clazz.getSuperclass();
        }
        return memberStream.collect(Collectors.toList());
    }

    private GizmoCloningUtils() {
        // No external instances.
    }

}
