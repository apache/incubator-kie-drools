package org.drools.kproject.memory;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

public class MemorytURLStreamHandler extends URLStreamHandler {
    
    private byte[] bytes;
    
    public MemorytURLStreamHandler(byte[] bytes) {
        this.bytes = bytes;
    }

    @Override
    protected URLConnection openConnection(URL url) throws IOException {
        String protocol = url.getProtocol();
        if ( !"memory".equals( protocol ) ) {
            throw new RuntimeException( "Memory protocol unable to handle:" + url.toExternalForm() );
        }
        
        return new MemoryURLConnection( url, bytes );
    }

}
