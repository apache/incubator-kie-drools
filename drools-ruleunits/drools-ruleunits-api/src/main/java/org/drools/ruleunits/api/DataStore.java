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
