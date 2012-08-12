package org.drools.kproject;

import java.io.IOException;
import java.io.InputStream;

public interface File extends Resource {      
    String getName();
    
    boolean exists();    

    InputStream getContents() throws IOException;
    
    void setContents(InputStream is) throws IOException;

    void create(InputStream is) throws IOException;
    
    Path getPath();
}
