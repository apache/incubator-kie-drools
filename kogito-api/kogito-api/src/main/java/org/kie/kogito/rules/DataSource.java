/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.rules;

public interface DataSource<T> extends org.drools.ruleunits.api.DataSource<T> {

    static <T> DataStream<T> createStream() {
        return (DataStream<T>) org.drools.ruleunits.api.DataSource.FactoryHolder.get().createStream();
    }

    static <T> DataStore<T> createStore() {
        return (DataStore<T>) org.drools.ruleunits.api.DataSource.FactoryHolder.get().createStore();
    }

    static <T> SingletonStore<T> createSingleton() {
        return (SingletonStore<T>) org.drools.ruleunits.api.DataSource.FactoryHolder.get().createSingleton();
    }
}
