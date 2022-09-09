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
package org.kie.kogito.drools.core.data;

import org.drools.ruleunits.api.DataStore;
import org.drools.ruleunits.api.DataStream;
import org.drools.ruleunits.api.SingletonStore;

public class DataSourceFactoryImpl extends org.drools.ruleunits.impl.datasources.DataSourceFactoryImpl {

    @Override
    public <T> DataStream<T> createStream() {
        return new ListDataStream<>();
    }

    @Override
    public <T> DataStore<T> createStore() {
        return new ListDataStore<>();
    }

    @Override
    public <T> SingletonStore<T> createSingleton() {
        return new FieldDataStore<>();
    }

    @Override
    public int servicePriority() {
        return 1;
    }
}
