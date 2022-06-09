package org.optaplanner.core.api.solver;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;

public class DivertingClassLoader extends ClassLoader {

    private final String divertedPrefix = "divertThroughClassLoader";

    public DivertingClassLoader(ClassLoader parent) {
        super(parent);
    }

    @Override
    public Class<?> loadClass(String className) throws ClassNotFoundException {
        if (className.startsWith(divertedPrefix + ".")) {
            className = className.substring(divertedPrefix.length() + 1);
        }
        return super.loadClass(className);
    }

    @Override
    public URL getResource(String resourceName) {
        if (resourceName.startsWith(divertedPrefix + "/")) {
            resourceName = resourceName.substring(divertedPrefix.length() + 1);
        }
        return super.getResource(resourceName);
    }

    @Override
    public InputStream getResourceAsStream(String resourceName) {
        if (resourceName.startsWith(divertedPrefix + "/")) {
            resourceName = resourceName.substring(divertedPrefix.length() + 1);
        }
        return super.getResourceAsStream(resourceName);
    }

    @Override
    public Enumeration<URL> getResources(String resourceName) throws IOException {
        if (resourceName.startsWith(divertedPrefix + "/")) {
            resourceName = resourceName.substring(divertedPrefix.length() + 1);
        }
        return super.getResources(resourceName);
    }

}
