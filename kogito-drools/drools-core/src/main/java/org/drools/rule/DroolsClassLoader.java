package org.drools.rule;

import java.io.InputStream;
import java.io.Externalizable;

public interface DroolsClassLoader {

    InputStream getResourceAsStream(final String name);

    public Class fastFindClass(final String name);
}
