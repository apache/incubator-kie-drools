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
