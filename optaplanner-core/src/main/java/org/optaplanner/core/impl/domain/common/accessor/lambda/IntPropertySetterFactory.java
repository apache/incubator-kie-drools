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
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.function.BiConsumer;

import org.optaplanner.core.impl.domain.common.accessor.lambda.consumer.ObjectIntConsumer;

public final class IntPropertySetterFactory extends PropertySetterFactory<ObjectIntConsumer> {

    protected IntPropertySetterFactory(Method setterMethod,
                                       Class<?> propertyType,
                                       MethodHandles.Lookup lookup) {
        super(setterMethod, propertyType, lookup);
    }

    @Override
    protected ObjectIntConsumer functionFromCallSite(CallSite setterSite) throws Throwable {
        return (ObjectIntConsumer) setterSite.getTarget().invokeExact();
    }

    @Override
    protected Class<?> getSetterParameterType() {
        return Integer.TYPE;
    }

    @Override
    protected Class<ObjectIntConsumer> getConsumerType() {
        return ObjectIntConsumer.class;
    }

    @Override
    protected BiConsumer wrapSetterFunction() {
        return new IntUnboxingSetterFunction();
    }

    protected class IntUnboxingSetterFunction implements BiConsumer<Object, Object> {

        public void accept(Object bean, Object value) {
            setterFunction.accept(bean, (int) value);
        }
    }
}
