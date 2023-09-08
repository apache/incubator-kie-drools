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

import java.util.function.Consumer;

import org.kie.api.runtime.rule.FactHandle;

public interface DataObserver {

    static <T> DataProcessor<T> of(Consumer<T> consumer) {
        return new DataProcessor<T>() {
            @Override
            public FactHandle insert(DataHandle handle, T object) {
                consumer.accept(object);
                return null;
            }

            @Override
            public void update(DataHandle handle, T object) {

            }

            @Override
            public void delete(DataHandle handle) {

            }
        };
    }

    static <T> DataProcessor<T> ofUpdatable(Consumer<T> consumer) {
        return new DataProcessor<T>() {
            @Override
            public FactHandle insert(DataHandle handle, T object) {
                consumer.accept(object);
                return null;
            }

            @Override
            public void update(DataHandle handle, T object) {
                consumer.accept(object);
            }

            @Override
            public void delete(DataHandle handle) {
                consumer.accept(null);
            }
        };
    }
}
