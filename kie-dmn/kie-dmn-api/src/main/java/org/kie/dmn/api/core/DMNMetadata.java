package org.kie.dmn.api.core;

import java.util.Map;

public interface DMNMetadata {

    /**
     * Sets or changes the value of an attribute
     *
     * @param name the attribute name
     * @param value the attribute value
     * @return the previous value associated with the name if present, null otherwise
     */
    Object set(String name, Object value);

    /**
     * Returns an attribute associated with this meta data by name.
     *
     * @param name the attribute name
     * @return the attribute object
     */
    Object get(String name);

    /**
     * @return a read-only list of attributes.
     */
    Map<String, Object> getAll();

    /**
     * Tells if an attribute with the specified name is defined or not
     *
     * @param name the attribute name
     * @return true if the attribute exists, false otherwise
     */
    boolean isDefined(String name);

}
