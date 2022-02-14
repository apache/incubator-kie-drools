/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.event;

import java.util.Optional;

/*
 * Currently we are not discriminating on type, 
 * but an implementation not based on smallrye might want to do that
 */
public class SubscriptionInfo<S, T> {

    private EventUnmarshaller<S> converter;
    private Class<T> outputClass;
    private Class<?>[] parametrizedClasses;
    private Optional<String> type;

    private SubscriptionInfo(EventUnmarshaller<S> converter, Class<T> outputClass, Class<?>[] parametrizedClasses, String type) {
        this.converter = converter;
        this.outputClass = outputClass;
        this.parametrizedClasses = parametrizedClasses;
        this.type = Optional.ofNullable(type);
    }

    public Optional<String> getType() {
        return type;
    }

    public EventUnmarshaller<S> getConverter() {
        return converter;
    }

    public Class<T> getOutputClass() {
        return outputClass;
    }

    public Class<?>[] getParametrizedClasses() {
        return parametrizedClasses;
    }

    @Override
    public String toString() {
        return "SubscriptionInfo [type=" + type + ", converter=" + converter + "]";
    }

    public static <S, T> SubscriptionInfoBuilder<S, T> builder() {
        return new SubscriptionInfoBuilder<>();
    }

    public static class SubscriptionInfoBuilder<S, T> {

        private EventUnmarshaller<S> converter;
        private Class<T> outputClass;
        private Class<?>[] parametrizedClasses = null;
        private String type = null;

        public SubscriptionInfoBuilder converter(EventUnmarshaller<S> converter) {
            this.converter = converter;
            return this;
        }

        public SubscriptionInfoBuilder outputClass(Class<T> outputClass) {
            this.outputClass = outputClass;
            return this;
        }

        public SubscriptionInfoBuilder parametrizedClasses(Class<?>... parametrizedClasses) {
            this.parametrizedClasses = parametrizedClasses;
            return this;
        }

        public SubscriptionInfoBuilder type(String type) {
            this.type = type;
            return this;
        }

        public SubscriptionInfo createSubscriptionInfo() {
            return new SubscriptionInfo(converter, outputClass, parametrizedClasses, type);
        }
    }
}
