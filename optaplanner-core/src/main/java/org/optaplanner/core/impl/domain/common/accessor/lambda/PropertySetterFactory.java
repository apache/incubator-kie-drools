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

package org.optaplanner.core.impl.domain.common.accessor.lambda;

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaConversionException;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.function.BiConsumer;

public abstract class PropertySetterFactory<T> {

    private Method setterMethod;
    private Class<?> propertyType;
    private MethodHandles.Lookup lookup;

    protected T setterFunction;

    public static BiConsumer createSetterFunction(Method setterMethod,
                                                  Class<?> propertyType,
                                                  MethodHandles.Lookup lookup) {
        final PropertySetterFactory propertySetterFactory;
        if (Object.class.isAssignableFrom(propertyType)) {
            propertySetterFactory = new ObjectPropertySetterFactory(setterMethod, propertyType, lookup);
        } else if (propertyType.equals(Boolean.TYPE)) {
            propertySetterFactory = new BooleanPropertySetterFactory(setterMethod, propertyType, lookup);
        } else if (propertyType.equals(Integer.TYPE)) {
            propertySetterFactory = new IntPropertySetterFactory(setterMethod, propertyType, lookup);
        } else if (propertyType.equals(Long.TYPE)) {
            propertySetterFactory = new LongPropertySetterFactory(setterMethod, propertyType, lookup);
        } else if (propertyType.equals(Float.TYPE)) {
            propertySetterFactory = new FloatPropertySetterFactory(setterMethod, propertyType, lookup);
        } else if (propertyType.equals(Double.TYPE)) {
            propertySetterFactory = new DoublePropertySetterFactory(setterMethod, propertyType, lookup);
        } else if (propertyType.equals(Character.TYPE)) {
            propertySetterFactory = new CharPropertySetterFactory(setterMethod, propertyType, lookup);
        } else if (propertyType.equals(Byte.TYPE)) {
            propertySetterFactory = new BytePropertySetterFactory(setterMethod, propertyType, lookup);
        } else if (propertyType.equals(Short.TYPE)) {
            propertySetterFactory = new ShortPropertySetterFactory(setterMethod, propertyType, lookup);
        } else {
            throw new IllegalArgumentException("Unsupported property type (" + propertyType + ").");
        }

        return propertySetterFactory.wrapSetterFunction();
    }

    protected PropertySetterFactory(Method setterMethod, Class<?> propertyType, MethodHandles.Lookup lookup) {
        this.setterMethod = setterMethod;
        this.propertyType = propertyType;
        this.lookup = lookup;
        this.setterFunction = createSetterFunction();
    }

    private T createSetterFunction() {
        if (setterMethod == null) {
            return null;
        }
        Class<?> declaringClass = setterMethod.getDeclaringClass();
        CallSite setterSite;

        try {
            setterSite = LambdaMetafactory.metafactory(lookup,
                                                       "accept",
                                                       MethodType.methodType(getConsumerType()),
                                                       MethodType.methodType(void.class, Object.class, getSetterParameterType()),
                                                       lookup.findVirtual(declaringClass, setterMethod.getName(), MethodType.methodType(void.class, propertyType)),
                                                       MethodType.methodType(void.class, declaringClass, propertyType));
        } catch (LambdaConversionException | NoSuchMethodException | IllegalAccessException e) {
            throw new IllegalArgumentException("Lambda creation failed for setterMethod (" + setterMethod + ").", e);
        }
        try {
            return functionFromCallSite(setterSite);
        } catch (Throwable e) {
            throw new IllegalArgumentException("Lambda creation failed for setterMethod (" + setterMethod + ").", e);
        }
    }

    protected abstract T functionFromCallSite(CallSite setterSite) throws Throwable;

    protected abstract Class<?> getSetterParameterType();

    protected abstract Class<T> getConsumerType();

    protected abstract BiConsumer wrapSetterFunction();
}
