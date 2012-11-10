package org.kie.util;

public interface FastClassLoader {
    public Class<?> fastFindClass(String name);
}
