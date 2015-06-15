/*
 * Copyright 2013 JBoss Inc
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

package org.optaplanner.core.impl.domain.common;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

public final class BeanPropertyMemberAccessor implements MemberAccessor {

    private final Class<?> propertyType;
    private final String propertyName;
    private final Method readMethod;
    private final Method writeMethod;

    public BeanPropertyMemberAccessor(Method readMethod) {
        this.readMethod = readMethod;
        readMethod.setAccessible(true); // Performance hack by avoiding security checks
        Class declaringClass = readMethod.getDeclaringClass();
        propertyType = readMethod.getReturnType();
        propertyName = ReflectionHelper.getGetterPropertyName(readMethod);
        writeMethod = ReflectionHelper.getSetterMethod(declaringClass, readMethod.getReturnType(), propertyName);
        if (writeMethod != null) {
            writeMethod.setAccessible(true); // Performance hack by avoiding security checks
        }
    }

    public String getName() {
        return propertyName;
    }

    @Override
    public Class<?> getType() {
        return propertyType;
    }

    @Override
    public Type getGenericType() {
        return readMethod.getGenericReturnType();
    }

    public Object executeGetter(Object bean) {
        try {
            return readMethod.invoke(bean);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Cannot call property (" + propertyName
                    + ") getter method (" + readMethod + ") on bean of class (" + bean.getClass() + ").", e);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException("The property (" + propertyName
                    + ") getter method (" + readMethod + ") on bean of class (" + bean.getClass()
                    + ") throws an exception.",
                    e.getCause());
        }
    }

    @Override
    public boolean supportSetter() {
        return writeMethod != null;
    }

    public void executeSetter(Object bean, Object value) {
        try {
            writeMethod.invoke(bean, value);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Cannot call property (" + propertyName
                    + ") setter method (" + writeMethod + ") on bean of class (" + bean.getClass()
                    + ") for value (" + value + ").", e);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException("The property (" + propertyName
                    + ") setter method (" + writeMethod + ") on bean of class (" + bean.getClass()
                    + ") throws an exception for value (" + value + ").",
                    e.getCause());
        }
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

}
