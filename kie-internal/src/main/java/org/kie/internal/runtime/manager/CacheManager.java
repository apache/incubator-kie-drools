/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.internal.runtime.manager;

import org.kie.internal.runtime.Cacheable;

/**
 * Maintains a cache of various items that are long lived or expensive to be created
 * frequently so they can benefit from being cached.
 *
 */
public interface CacheManager {

    /**
     * Adds a given value into the cache under given key
     * @param key unique key for the item to be stored
     * @param value actual value to be cached
     */
    void add(String key, Object value);

    /**
     * Retrieves item from a cache if exists
     * @param key key for the item in the cache
     * @return actual value if found otherwise null
     */
    Object get(String key);

    /**
     * Removes the item from cache if exists
     * @param key key for the item in the cache
     * @return actual value if found otherwise null
     */
    Object remove(String key);

    /**
     * Disposes cache and cleans up/closes its stored resources of applicable
     * @see Cacheable for details
     */
    void dispose();
}
