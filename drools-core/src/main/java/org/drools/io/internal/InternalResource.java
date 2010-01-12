package org.drools.io.internal;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;

import org.drools.builder.ResourceType;
import org.drools.builder.ResourceConfiguration;
import org.drools.io.Resource;

public interface InternalResource extends Resource {
    void setResourceType(ResourceType resourceType);
    
    ResourceType getResourceType();
    
    ResourceConfiguration getConfiguration();

    void setConfiguration(ResourceConfiguration configuration); 
    
    URL getURL() throws IOException;
    
    boolean hasURL();
    
    boolean isDirectory();
    
    Collection<Resource> listResources();    
    
    long getLastModified();
    
    long getLastRead();    

}
