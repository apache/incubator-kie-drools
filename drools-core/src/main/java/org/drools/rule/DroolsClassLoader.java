package org.drools.rule;

import java.io.InputStream;
import java.io.Externalizable;

public interface DroolsClassLoader {

    public InputStream getResourceAsStream(final String name);

    public Class<?> fastFindClass(final String name);
    
    public Class<?> loadClass(final String name,
                           final boolean resolve) throws ClassNotFoundException;
}
