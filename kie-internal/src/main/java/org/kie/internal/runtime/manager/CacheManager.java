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
