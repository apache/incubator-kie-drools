package org.kie.api.conf;

import java.io.Serializable;

/**
 * A base interface for type safe options in configuration objects
 */
public interface Option extends Serializable {

    /**
     * @return the property name for this option
     */
    @Deprecated // immutable properties should not need getter/setter, this is common now for "value" types.
    String getPropertyName();

    default String propertyName() {
        return getPropertyName();
    }

    String type();

}
