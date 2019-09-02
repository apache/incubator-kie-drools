/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaConversionException;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.optaplanner.core.impl.domain.common.ReflectionHelper;

/**
 * A {@link MemberAccessor} based on a getter and optionally a setter.
 */
public final class LambdaBeanPropertyMemberAccessor implements MemberAccessor {

    private final Class<?> propertyType;
    private final String propertyName;
    private final Method getterMethod;
    private final Function getterFunction;
    private final Method setterMethod;
    private final BiConsumer setterFunction;

    public LambdaBeanPropertyMemberAccessor(Method getterMethod) {
        this(getterMethod, false);
    }

    public LambdaBeanPropertyMemberAccessor(Method getterMethod, boolean getterOnly) {
        this.getterMethod = getterMethod;
        Class<?> declaringClass = getterMethod.getDeclaringClass();
        if (!ReflectionHelper.isGetterMethod(getterMethod)) {
            throw new IllegalArgumentException("The getterMethod (" + getterMethod + ") is not a valid getter.");
        }
        propertyType = getterMethod.getReturnType();
        propertyName = ReflectionHelper.getGetterPropertyName(getterMethod);
        // TODO In JDK 9 switch to (and remove workaround from MemberAccessorFactory)
        // MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(declaringClass, MethodHandles.lookup())
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        getterFunction = createGetterFunction(lookup);
        if (getterOnly) {
            setterMethod = null;
            setterFunction = null;
        } else {
            setterMethod = ReflectionHelper.getSetterMethod(declaringClass, getterMethod.getReturnType(), propertyName);
            setterFunction = createSetterFunction(lookup);
        }
    }

    private Function createGetterFunction(MethodHandles.Lookup lookup) {
        Class<?> declaringClass = getterMethod.getDeclaringClass();
        CallSite getterSite;
        try {
            getterSite = LambdaMetafactory.metafactory(lookup,
                    "apply",
                    MethodType.methodType(Function.class),
                    MethodType.methodType(Object.class, Object.class),
                    lookup.findVirtual(declaringClass, getterMethod.getName(), MethodType.methodType(propertyType)),
                    MethodType.methodType(propertyType, declaringClass));
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Lambda creation failed for getterMethod (" + getterMethod + ").\n" +
                    MemberAccessorFactory.CLASSLOADER_NUDGE_MESSAGE, e);
        } catch (LambdaConversionException | NoSuchMethodException e) {
            throw new IllegalArgumentException("Lambda creation failed for getterMethod (" + getterMethod + ").", e);
        }
        try {
            return (Function) getterSite.getTarget().invokeExact();
        } catch (Throwable e) {
            throw new IllegalArgumentException("Lambda creation failed for getterMethod (" + getterMethod + ").", e);
        }
    }

    private BiConsumer createSetterFunction(MethodHandles.Lookup lookup) {
        if (setterMethod == null) {
            return null;
        }
        Class<?> declaringClass = setterMethod.getDeclaringClass();
        CallSite setterSite;
        try {
            setterSite = LambdaMetafactory.metafactory(lookup,
                    "accept",
                    MethodType.methodType(BiConsumer.class),
                    MethodType.methodType(void.class, Object.class, Object.class),
                    lookup.findVirtual(declaringClass, setterMethod.getName(), MethodType.methodType(void.class, propertyType)),
                    MethodType.methodType(void.class, declaringClass, propertyType));
        } catch (LambdaConversionException | NoSuchMethodException | IllegalAccessException e) {
            throw new IllegalArgumentException("Lambda creation failed for setterMethod (" + setterMethod + ").", e);
        }
        try {
            return (BiConsumer) setterSite.getTarget().invokeExact();
        } catch (Throwable e) {
            throw new IllegalArgumentException("Lambda creation failed for setterMethod (" + setterMethod + ").", e);
        }
    }

    @Override
    public Class<?> getDeclaringClass() {
        return getterMethod.getDeclaringClass();
    }

    @Override
    public String getName() {
        return propertyName;
    }

    @Override
    public Class<?> getType() {
        return propertyType;
    }

    @Override
    public Type getGenericType() {
        return getterMethod.getGenericReturnType();
    }

    @Override
    public Object executeGetter(Object bean) {
        return getterFunction.apply(bean);
    }

    @Override
    public boolean supportSetter() {
        return setterMethod != null;
    }

    @Override
    public void executeSetter(Object bean, Object value) {
        setterFunction.accept(bean, value);
    }

    @Override
    public String getSpeedNote() {
        return "pretty fast access with LambdaMetafactory";
    }

    // ************************************************************************
    // AnnotatedElement methods
    // ************************************************************************

    @Override
    public boolean isAnnotationPresent(Class<? extends Annotation> annotationClass) {
        return getterMethod.isAnnotationPresent(annotationClass);
    }

    @Override
    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
        return getterMethod.getAnnotation(annotationClass);
    }

    @Override
    public Annotation[] getAnnotations() {
        return getterMethod.getAnnotations();
    }

    @Override
    public Annotation[] getDeclaredAnnotations() {
        return getterMethod.getDeclaredAnnotations();
    }

    @Override
    public String toString() {
        return "bean property " + propertyName + " on " + getterMethod.getDeclaringClass();
    }
}
