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

package org.optaplanner.quarkus.nativeimage;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.BiConsumer;
import java.util.function.Function;

import com.oracle.svm.core.annotate.Alias;
import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;

/**
 * LambdaBeanPropertyMemberAccessor works by creating a new class during runtime (via LambdaMetafactory) to delegate to
 * the provided getter/setter methods. This is not supported in GraalVM, so we need to use Method reflection
 * (i.e. {@link Method#invoke(Object, Object...)}) instead.
 */
@TargetClass(className = "org.optaplanner.core.impl.domain.common.accessor.LambdaBeanPropertyMemberAccessor")
public final class Substitute_LambdaBeanPropertyMemberAccessor {

    @Alias
    Method getterMethod;

    @Alias
    Method setterMethod;

    @Substitute
    private Function<Object, Object> createGetterFunction(MethodHandles.Lookup lookup) {
        return new GetterDelegationFunction(getterMethod);
    }

    @Substitute
    private BiConsumer<Object, Object> createSetterFunction(MethodHandles.Lookup lookup) {
        if (setterMethod == null) {
            return null;
        }

        return new SetterDelegationBiConsumer(setterMethod);
    }

    private static final class GetterDelegationFunction implements Function<Object, Object> {
        private final Method method;

        public GetterDelegationFunction(Method method) {
            this.method = method;
        }

        @Override
        public Object apply(Object object) {
            try {
                return method.invoke(object);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static final class SetterDelegationBiConsumer implements BiConsumer<Object, Object> {
        private final Method method;

        public SetterDelegationBiConsumer(Method method) {
            this.method = method;
        }

        @Override
        public void accept(Object object, Object value) {
            try {
                method.invoke(object, value);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
