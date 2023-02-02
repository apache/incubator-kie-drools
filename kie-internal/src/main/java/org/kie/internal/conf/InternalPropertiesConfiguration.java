package org.kie.internal.conf;

public interface InternalPropertiesConfiguration {
    public boolean setInternalProperty(String name,
                                       String value);

    /**
     * Gets a property value
     *
     * @param name name
     * @return property
     */
    public String getInternalProperty(String name);
}
