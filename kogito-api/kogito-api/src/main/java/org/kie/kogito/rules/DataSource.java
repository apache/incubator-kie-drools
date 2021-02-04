/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.rules;

import java.util.Iterator;

import org.kie.api.internal.utils.ServiceRegistry;

public interface DataSource<T> extends Iterable<T> {

    default Iterator<T> iterator() {
        throw new UnsupportedOperationException();
    }

    void subscribe(DataProcessor<T> subscriber);

    interface Factory {
        <T> DataStream<T> createStream();
        <T> DataStore<T> createStore();
        <T> SingletonStore<T> createSingleton();
    }

    static <T> DataStream<T> createStream() {
        return FactoryHolder.get().createStream();
    }

    static <T> DataStore<T> createStore() {
        return FactoryHolder.get().createStore();
    }

    static <T> SingletonStore<T> createSingleton() {
        return FactoryHolder.get().createSingleton();
    }

    class FactoryHolder {

        private static class LazyHolder {
            private static Factory INSTANCE = ServiceRegistry.getInstance().get(Factory.class);
        }

        public static Factory get() {
            return LazyHolder.INSTANCE;
        }
    }
}
