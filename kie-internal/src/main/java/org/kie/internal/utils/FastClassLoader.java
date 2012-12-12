package org.kie.internal.utils;

public interface FastClassLoader {
    public Class<?> fastFindClass(String name);
}
