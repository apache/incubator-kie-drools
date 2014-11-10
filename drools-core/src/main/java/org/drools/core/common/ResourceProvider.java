package org.drools.core.common;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public interface ResourceProvider {
    URL getResource(String name);
    InputStream getResourceAsStream(String name) throws IOException;
}
