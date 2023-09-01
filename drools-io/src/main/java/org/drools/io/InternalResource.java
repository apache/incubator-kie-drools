package org.drools.io;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.List;

import org.kie.api.io.Resource;
import org.kie.api.io.ResourceConfiguration;
import org.kie.api.io.ResourceType;

public interface InternalResource extends Resource {
    InternalResource setResourceType(ResourceType resourceType);
    
    ResourceType getResourceType();
    
    ResourceConfiguration getConfiguration();

    InternalResource setConfiguration(ResourceConfiguration configuration);
    
    URL getURL() throws IOException;
    
    boolean hasURL();
    
    boolean isDirectory();
    
    Collection<Resource> listResources();
    
    /**
     * Returns the description of the resource. This is just a text description
     * of the resource used to add more information about it.
     * This is not a mandatory attribute
     * 
     * @return the name of the resource, or null if is not set.
     */
    String getDescription();

    void setDescription(String description); 

    List<String> getCategories();

    void setCategories( String categories );

    void addCategory( String category );

    byte[] getBytes();

    String getEncoding();
}
