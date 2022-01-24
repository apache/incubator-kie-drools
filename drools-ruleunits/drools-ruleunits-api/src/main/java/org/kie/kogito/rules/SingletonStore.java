/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

/**
 * A data store that contains at most one value
 */
public interface SingletonStore<T> extends DataSource<T> {
    /**
     * Set the value in this singleton data store
     */
    DataHandle set(T value);

    /**
     * Notifies the store that the contained value has changed
     */
    void update();

    /**
     * Clear the value in this singleton data store
     */
    void clear();

}
