package org.kie.submarine.rules;

import org.kie.api.runtime.rule.FactHandle;

public interface DataSource<T> {

    FactHandle add(T object );

    /**
     * Updates the fact for which the given FactHandle was assigned with the new
     * fact set as the second parameter in this method.
     * It is also possible to optionally specify the set of properties that have been modified.
     *
     * @param handle the FactHandle for the fact to be updated.
     * @param object the new value for the fact being updated.
     */
    void update(FactHandle handle, T object);

    /**
     * Deletes the fact for which the given FactHandle was assigned
     *
     * @param handle the handle whose fact is to be retracted.
     */
    void remove(FactHandle handle);

}
