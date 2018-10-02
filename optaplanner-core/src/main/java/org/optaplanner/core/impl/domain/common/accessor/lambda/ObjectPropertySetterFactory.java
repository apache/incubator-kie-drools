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

public final class ObjectPropertySetterFactory extends PropertySetterFactory<BiConsumer> {

    protected ObjectPropertySetterFactory(Method setterMethod,
                                          Class<?> propertyType,
                                          MethodHandles.Lookup lookup) {
        super(setterMethod, propertyType, lookup);
    }

    @Override
    protected BiConsumer functionFromCallSite(CallSite setterSite) throws Throwable {
        return (BiConsumer) setterSite.getTarget().invokeExact();
    }

    @Override
    protected Class<?> getSetterParameterType() {
        return Object.class;
    }

    @Override
    protected Class<BiConsumer> getConsumerType() {
        return BiConsumer.class;
    }

    @Override
    public BiConsumer wrapSetterFunction() {
        return setterFunction;
    }
}
