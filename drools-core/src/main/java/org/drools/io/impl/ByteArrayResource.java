package org.drools.io.impl;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.Arrays;

import org.drools.io.Resource;

public class ByteArrayResource
    implements
    Resource {

    private byte[] bytes;

    public ByteArrayResource(byte[] bytes) {
        if ( bytes == null ) {
            throw new IllegalArgumentException( "bytes cannot be null" );
        }
        this.bytes = bytes;
    }

    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream( this.bytes );
    }
    
    public Reader getReader() throws IOException {
        return new InputStreamReader( getInputStream() );
    }    
    
    public boolean hasURL() {
        return false;
    }

    public URL getURL() throws IOException {
        throw new FileNotFoundException( "byte[] cannot be resolved to URL" );
    }

    public boolean equals(Object object) {
        return (object == this || (object instanceof ByteArrayResource && Arrays.equals( ((ByteArrayResource) object).bytes,
                                                                                         this.bytes )));
    }

    public int hashCode() {
        return (byte[].class.hashCode() * 29 * this.bytes.length);
    }

}
