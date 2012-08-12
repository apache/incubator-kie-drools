package org.drools.kproject.memory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;


public class MemoryURLConnection extends URLConnection {

    private URL url;
    
    private byte[] bytes;

    protected MemoryURLConnection(URL url, byte[] bytes) {
        super( url );
        this.url = url;
        this.bytes = bytes;
    }

    @Override
    public void connect() throws IOException {
        
    }
    
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream( bytes );
                
    }

}
