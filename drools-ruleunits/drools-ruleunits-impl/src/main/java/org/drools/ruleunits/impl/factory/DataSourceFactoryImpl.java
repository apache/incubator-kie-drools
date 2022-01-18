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
package org.drools.ruleunits.impl.factory;

import org.drools.ruleunits.impl.FieldDataStore;
import org.drools.ruleunits.impl.ListDataStore;
import org.drools.ruleunits.impl.ListDataStream;
import org.kie.kogito.rules.DataSource;
import org.kie.kogito.rules.DataStore;
import org.kie.kogito.rules.DataStream;
import org.kie.kogito.rules.SingletonStore;

public class DataSourceFactoryImpl implements DataSource.Factory {

    public <T> DataStream<T> createStream() {
        return new ListDataStream<>();
    }

    public <T> DataStore<T> createStore() {
        return new ListDataStore<>();
    }

    public <T> SingletonStore<T> createSingleton() {
        return new FieldDataStore<>();
    }
}
