package org.drools.io.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

import org.drools.io.Resource;

public class ReaderResource implements Resource {
    private Reader reader;
    private String encoding;
    
    public ReaderResource(Reader reader) {
        this(reader, null);
    }
    
    public ReaderResource(Reader reader, String encoding) {
        if ( reader == null ) {
            throw new IllegalArgumentException( "reader cannot be null" );
        }
        if ( encoding == null && reader instanceof InputStreamReader ) {
            this.encoding = ((InputStreamReader)reader).getEncoding();
        }
        this.reader = reader;

        this.encoding = encoding;
    }
    
    public URL getURL() throws IOException {
        throw new FileNotFoundException( "byte[] cannot be resolved to URL");
    }

    public InputStream getInputStream() throws IOException {
        if ( this.encoding != null ) {
            return new ReaderInputStream( this.reader, this.encoding);         
        } else {
            return new ReaderInputStream( this.reader);
        }
    } 
    
    public Reader getReader() {
        return this.reader;
    }
    
    public String getEncoding() {
        return this.encoding;
    }
    
    public boolean hasURL() {
        return false;
    }        
    
}
