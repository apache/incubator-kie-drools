package org.drools.ruleunits.api;

/**
 * An handle to an object inserted into a {@link DataStore}.
 */
public interface DataHandle {

    /**
     * The object referred by this handle.
     */
    Object getObject();
}
