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

import org.kie.api.runtime.rule.FactHandle;

/**
 * The interface to implement in order to be notified of all the changes occurred to the facts managed by a {@link DataSource}
 * @param <T> The type of objects observed by this DataProcessor.
 */
public interface DataProcessor<T> {

    /**
     * Notifies this DataProcessor that an object has been inserted in the observed {@link DataSource}.
     */
    default void insert(T object) {
        insert(null, object);
    }

    /**
     * Notifies this DataProcessor that an object with the given {@link DataHandle} has been inserted in the observed {@link DataSource}.
     */
    FactHandle insert(DataHandle handle, T object);

    /**
     * Notifies this DataProcessor that an object with the given {@link DataHandle} has been updated in the observed {@link DataSource}.
     */
    void update(DataHandle handle, T object);

    /**
     * Notifies this DataProcessor that an object with the given {@link DataHandle} has been deleted from the observed {@link DataSource}.
     */
    void delete(DataHandle handle);
}
