package org.drools.compiler.compiler.io;

import java.io.IOException;
import java.io.InputStream;

import org.drools.util.PortablePath;


public interface File extends FileSystemItem {
    String getName();
    
    boolean exists();    

    InputStream getContents() throws IOException;
    
    void setContents(InputStream is) throws IOException;

    void create(InputStream is) throws IOException;
    
    PortablePath getPath();
}
