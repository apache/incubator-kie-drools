package org.drools.util;

import java.io.InputStream;

public interface DroolsClassLoader {

    public InputStream getResourceAsStream(final String name);

    public Class<?> fastFindClass(final String name);
    
    public Class<?> loadClass(final String name,
                           final boolean resolve) throws ClassNotFoundException;
}
