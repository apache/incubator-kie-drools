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

/**
 * A {@link DataSource} of mutable data.
 * @param <T> The type of objects managed by this DataSource.
 */
public interface DataStore<T> extends DataSource<T> {

    /**
     * Add an object to this DataStore.
     * @return The {@link DataHandle} to be further modified
     */
    DataHandle add(T object);

    /**
     * Updates the fact for which the given {@link DataHandle} was assigned with the new
     * fact set as the second parameter in this method.
     * It is also possible to optionally specify the set of properties that have been modified.
     *
     * @param handle the FactHandle for the fact to be updated.
     * @param object the new value for the fact being updated.
     */
    void update(DataHandle handle, T object);

    /**
     * Deletes the fact for which the given {@link DataHandle} was assigned.
     *
     * @param handle the handle whose fact is to be retracted.
     */
    void remove(DataHandle handle);

    /**
     * Deletes a fact from this DataStore.
     */
    void remove(T object);
}
