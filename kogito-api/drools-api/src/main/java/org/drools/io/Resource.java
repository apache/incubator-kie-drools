package org.drools.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;

public interface Resource {
    InputStream getInputStream() throws IOException;
    public Reader getReader() throws IOException;
    URL getURL() throws IOException;
    boolean hasURL();
    long getLastModified();
    public long getLastRead();
}
