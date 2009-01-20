package org.drools;

/**
 * Base class for other Configuration classes.
 *
 */
public interface PropertiesConfiguration {

    /**
     * Sets a property value
     * 
     * @param name
     * @param value
     */
    public void setProperty(String name,
                            String value);

    /**
     * Gets a property value
     * 
     * @param name
     * @return
     */
    public String getProperty(String name);
}
