/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.api.runtime.rule;

/**
 * A DataSource is a source of the data processed by a given {@link RuleUnit}.
 */
public interface DataSource<T> extends Iterable<T> {

    /**
     * Inserts a new fact into this DataSource
     *
     * @param object
     *        the fact to be inserted
     *
     * @return the fact handle created for the given fact
     */
    FactHandle insert( T object );

    /**
     * Updates the fact for which the given FactHandle was assigned with the new
     * fact set as the second parameter in this method.
     * It is also possible to optionally specify the set of properties that have been modified.
     *
     * @param handle the FactHandle for the fact to be updated.
     * @param object the new value for the fact being updated.
     * @param modifiedProperties the list of the names of the object's properties modified by this update.
     */
    void update(FactHandle handle, T object, String... modifiedProperties);

    /**
     * Deletes the fact for which the given FactHandle was assigned
     *
     * @param handle the handle whose fact is to be retracted.
     */
    void delete(FactHandle handle);

    /**
     * Creates a DataSource, optionally containing some items
     *
     * @param items the items contained in the DataSource.
     */
    static <T> DataSource<T> create(T... items) {
        try {
            DataSource<T> dataSource = (DataSource<T>) Class.forName( "org.drools.core.datasources.CursoredDataSource" ).newInstance();
            for (T item : items) {
                dataSource.insert( item );
            }
            return dataSource;
        } catch (Exception e) {
            throw new RuntimeException("Unable to instance KieServices", e);
        }
    }
}
