package org.kie.internal.conf;

public interface InternalPropertiesConfiguration {
    boolean setInternalProperty(String name, String value);

    /**
     * Gets a property value
     *
     * @param name name
     * @return property
     */
    String getInternalProperty(String name);
}
