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
package org.drools.ruleunits.impl.datasources;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

import org.drools.ruleunits.api.DataProcessor;
import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.impl.EntryPointDataProcessor;

public abstract class AbstractDataSource<T> implements DataSource<T> {

    protected final List<DataProcessor> subscribers = new CopyOnWriteArrayList<>();

    protected final List<EntryPointDataProcessor> entryPointSubscribers = new CopyOnWriteArrayList<>();

    @Override
    public void subscribe(DataProcessor<T> processor) {
        if (processor instanceof EntryPointDataProcessor) {
            entryPointSubscribers.add((EntryPointDataProcessor) processor);
        } else {
            subscribers.add(processor);
        }
    }

    protected void forEachSubscriber(Consumer<DataProcessor> consumer) {
        subscribers.forEach(consumer);
        entryPointSubscribers.forEach(consumer);
    }
}
