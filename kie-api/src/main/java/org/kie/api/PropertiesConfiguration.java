package org.kie.api;

/**
 * Base class for other Configuration classes.
 */
public interface PropertiesConfiguration {

    /**
     * Sets a property value
     *
     * @param name name
     * @param value value
     */
    public boolean setProperty(String name,
                               String value);

    /**
     * Gets a property value
     *
     * @param name name
     * @return property
     */
    public String getProperty(String name);
}
