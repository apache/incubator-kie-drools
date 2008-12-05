package org.drools.io;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;

import org.drools.builder.KnowledgeType;
import org.drools.builder.ResourceConfiguration;

public interface InternalResource extends Resource {
    void setKnowledgeType(KnowledgeType knowledgeType);
    
    KnowledgeType getKnowledgeType();
    
    ResourceConfiguration getConfiguration();

    void setConfiguration(ResourceConfiguration configuration); 
    
    URL getURL() throws IOException;
    
    boolean hasURL();
    
    boolean isDirectory();
    
    Collection<Resource> listResources();    
    
    long getLastModified();
    
    long getLastRead();    

}
