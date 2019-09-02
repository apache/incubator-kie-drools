/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;

/**
 * A {@link MemberAccessor} based on a single read {@link Method}.
 * Do not confuse with {@link ReflectionBeanPropertyMemberAccessor} which is richer.
 */
public final class ReflectionMethodMemberAccessor implements MemberAccessor {

    private final Class<?> returnType;
    private final String methodName;
    private final Method readMethod;

    public ReflectionMethodMemberAccessor(Method readMethod) {
        this.readMethod = readMethod;
        readMethod.setAccessible(true); // Performance hack by avoiding security checks
        returnType = readMethod.getReturnType();
        methodName = readMethod.getName();
        if (readMethod.getParameterTypes().length != 0) {
            throw new IllegalArgumentException("The readMethod (" + readMethod + ") must not have any parameters ("
                    + Arrays.toString(readMethod.getParameterTypes()) + ").");
        }
        if (readMethod.getReturnType() == void.class) {
            throw new IllegalArgumentException("The readMethod (" + readMethod + ") must have a return type ("
                    + readMethod.getReturnType() + ").");
        }
    }

    @Override
    public Class<?> getDeclaringClass() {
        return readMethod.getDeclaringClass();
    }

    @Override
    public String getName() {
        return methodName;
    }

    @Override
    public Class<?> getType() {
        return returnType;
    }

    @Override
    public Type getGenericType() {
        return readMethod.getGenericReturnType();
    }

    @Override
    public Object executeGetter(Object bean) {
        try {
            return readMethod.invoke(bean);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Cannot call property (" + methodName
                    + ") getterMethod (" + readMethod + ") on bean of class (" + bean.getClass() + ").\n" +
                    MemberAccessorFactory.CLASSLOADER_NUDGE_MESSAGE, e);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException("The property (" + methodName
                    + ") getterMethod (" + readMethod + ") on bean of class (" + bean.getClass()
                    + ") throws an exception.",
                    e.getCause());
        }
    }

    @Override
    public String getSpeedNote() {
        return "slow access with reflection";
    }

    @Override
    public boolean supportSetter() {
        return  false;
    }

    @Override
    public void executeSetter(Object bean, Object value) {
        throw new UnsupportedOperationException();
    }

    // ************************************************************************
    // AnnotatedElement methods
    // ************************************************************************

    @Override
    public boolean isAnnotationPresent(Class<? extends Annotation> annotationClass) {
        return readMethod.isAnnotationPresent(annotationClass);
    }

    @Override
    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
        return readMethod.getAnnotation(annotationClass);
    }

    @Override
    public Annotation[] getAnnotations() {
        return readMethod.getAnnotations();
    }

    @Override
    public Annotation[] getDeclaredAnnotations() {
        return readMethod.getDeclaredAnnotations();
    }

    @Override
    public String toString() {
        return "method " + methodName + " on " + readMethod.getDeclaringClass();
    }

}
