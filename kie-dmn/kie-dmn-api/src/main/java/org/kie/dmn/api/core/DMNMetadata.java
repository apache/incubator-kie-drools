package org.kie.dmn.api.core;

import java.util.Map;

public interface DMNMetadata {

    /**
     * Sets or changes the value of an attribute
     *
     * @param name the attribute name
     * @param value the attribute value
     */
    void setAttribute(String name, Object value);

    /**
     * Returns an attribute associated with this meta data by name.
     *
     * @param name the attribute name
     * @return the attribute
     */
    Object getAttribute(String name);

    /**
     * @return a read-only list of attributes.
     */
    Map<String, Object> getAttributes();

}
