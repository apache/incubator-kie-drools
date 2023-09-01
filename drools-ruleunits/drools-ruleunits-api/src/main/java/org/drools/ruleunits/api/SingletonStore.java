package org.drools.ruleunits.api;

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
