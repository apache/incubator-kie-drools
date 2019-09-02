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

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import org.optaplanner.core.impl.domain.common.ReflectionHelper;

/**
 * A {@link MemberAccessor} based on a getter and optionally a setter.
 */
public final class ReflectionBeanPropertyMemberAccessor implements MemberAccessor {

    private final Class<?> propertyType;
    private final String propertyName;
    private final Method getterMethod;
    private final Method setterMethod;

    public ReflectionBeanPropertyMemberAccessor(Method getterMethod) {
        this(getterMethod, false);
    }

    public ReflectionBeanPropertyMemberAccessor(Method getterMethod, boolean getterOnly) {
        this.getterMethod = getterMethod;
        getterMethod.setAccessible(true); // Performance hack by avoiding security checks
        Class<?> declaringClass = getterMethod.getDeclaringClass();
        if (!ReflectionHelper.isGetterMethod(getterMethod)) {
            throw new IllegalArgumentException("The getterMethod (" + getterMethod + ") is not a valid getter.");
        }
        propertyType = getterMethod.getReturnType();
        propertyName = ReflectionHelper.getGetterPropertyName(getterMethod);
        if (getterOnly) {
            setterMethod = null;
        } else {
            setterMethod = ReflectionHelper.getSetterMethod(declaringClass, getterMethod.getReturnType(), propertyName);
            if (setterMethod != null) {
                setterMethod.setAccessible(true); // Performance hack by avoiding security checks
            }
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
        try {
            return getterMethod.invoke(bean);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Cannot call property (" + propertyName
                    + ") getterMethod (" + getterMethod + ") on bean of class (" + bean.getClass() + ").\n" +
                    MemberAccessorFactory.CLASSLOADER_NUDGE_MESSAGE, e);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException("The property (" + propertyName
                    + ") getterMethod (" + getterMethod + ") on bean of class (" + bean.getClass()
                    + ") throws an exception.",
                    e.getCause());
        }
    }

    @Override
    public boolean supportSetter() {
        return setterMethod != null;
    }

    @Override
    public void executeSetter(Object bean, Object value) {
        try {
            setterMethod.invoke(bean, value);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Cannot call property (" + propertyName
                    + ") setterMethod (" + setterMethod + ") on bean of class (" + bean.getClass()
                    + ") for value (" + value + ").", e);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException("The property (" + propertyName
                    + ") setterMethod (" + setterMethod + ") on bean of class (" + bean.getClass()
                    + ") throws an exception for value (" + value + ").",
                    e.getCause());
        }
    }

    @Override
    public String getSpeedNote() {
        return "slow access with reflection";
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
