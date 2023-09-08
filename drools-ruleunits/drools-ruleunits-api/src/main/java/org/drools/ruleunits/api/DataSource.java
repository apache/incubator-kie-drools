/**
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
package org.drools.ruleunits.api;

import org.kie.api.internal.utils.KieService;

/**
 * A strongly typed source of data for a {@link RuleUnit}.
 * @param <T> The type of objects managed by this DataSource.
 */
public interface DataSource<T> {

    /**
     * Subscribes this DataSource to a {@link DataProcessor} that will be notified of all the changes occurred
     * to the facts going through the DataSource.
     */
    void subscribe(DataProcessor<T> subscriber);

    interface Factory extends KieService {
        <T> DataStream<T> createStream();

        <T> DataStream<T> createBufferedStream(int bufferSize);

        <T> DataStore<T> createStore();

        <T> SingletonStore<T> createSingleton();
    }

    /**
     * Creates a {@link DataStream}, a DataSource of immutable facts, without any buffer.
     */
    static <T> DataStream<T> createStream() {
        return FactoryHolder.get().createStream();
    }

    /**
     * Creates a {@link DataStream}, a DataSource of immutable facts, without a buffer retaining at most the number of facts defined in bufferSize.
     */
    static <T> DataStream<T> createBufferedStream(int bufferSize) {
        return FactoryHolder.get().createBufferedStream(bufferSize);
    }

    /**
     * Creates a {@link DataStore}, a DataSource of mutable facts.
     */
    static <T> DataStore<T> createStore() {
        return FactoryHolder.get().createStore();
    }

    /**
     * Creates a {@link SingletonStore}, a data store that contains at most one value.
     */
    static <T> SingletonStore<T> createSingleton() {
        return FactoryHolder.get().createSingleton();
    }

    class FactoryHolder {

        private static class LazyHolder {
            private static Factory INSTANCE = KieService.load(Factory.class);
        }

        public static Factory get() {
            return LazyHolder.INSTANCE;
        }
    }
}
