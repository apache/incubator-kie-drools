package org.drools.rule;

import java.io.InputStream;

public interface DroolsClassLoader {
    
    InputStream getResourceAsStream(final String name);
    
    public Class fastFindClass(final String name);
}
