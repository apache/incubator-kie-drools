package org.drools.util;

public interface FastClassLoader {
    public Class<?> fastFindClass(String name);
}
