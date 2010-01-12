package org.drools.io.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.Collection;

import org.drools.io.Resource;
import org.drools.io.internal.InternalResource;

public class InputStreamResource  extends BaseResource implements InternalResource {
    private InputStream stream;
    
    public InputStreamResource(InputStream stream) {
        if ( stream == null ) {
            throw new IllegalArgumentException( "stream cannot be null" );
        }
        this.stream = stream;
    }

    public InputStream getInputStream() throws IOException {
        return stream;
    }
    
    public Reader getReader() throws IOException {
        return new InputStreamReader( getInputStream() );
    }    

    public URL getURL() throws IOException {
        throw new FileNotFoundException( "InputStream cannot be resolved to URL" );
    }
    
    public boolean hasURL() {
        return false;
    }    
    
    public long getLastModified() {
        throw new IllegalStateException( "InputStream does have a modified date" );
    }    
    
    public long getLastRead() {
        throw new IllegalStateException( "InputStream does have a modified date" );
    }      
    
    public boolean isDirectory() {
        return false;
    }

    public Collection<Resource> listResources() {
        throw new RuntimeException( "This Resource cannot be listed, or is not a directory" );
    }  

}
