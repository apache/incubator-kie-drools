package org.drools.io;

import java.io.Serializable;
import java.util.Properties;

import org.kie.api.io.ResourceConfiguration;
import org.kie.api.io.ResourceType;

import static org.kie.api.io.ResourceType.determineResourceType;

public class ResourceConfigurationImpl implements Serializable, ResourceConfiguration {

    public static final String RESOURCE_TYPE = "resource.type";

    private ResourceType resourceType;

    public ResourceType getResourceType() {
        return resourceType;
    }

    public void setResourceType(ResourceType resourceType) {
        this.resourceType = resourceType;
    }

    public ResourceConfigurationImpl merge(ResourceConfigurationImpl other) {
        other.setResourceType(resourceType);
        return other;
    }

    public Properties toProperties() {
        Properties prop = new Properties();
        if (resourceType != null) {
            prop.setProperty( RESOURCE_TYPE, resourceType.getDefaultExtension() );
        }
        return prop;
    }

    public ResourceConfiguration fromProperties(Properties prop) {
        String extension = prop.getProperty( RESOURCE_TYPE );
        if (extension != null) {
            resourceType = determineResourceType("." + extension);
        }
        return this;
    }
}
