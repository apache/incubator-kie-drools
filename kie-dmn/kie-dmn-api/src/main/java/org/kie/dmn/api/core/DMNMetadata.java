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
     * @return a read-only map of attributes.
     */
    Map<String, Object> asMap();

}
