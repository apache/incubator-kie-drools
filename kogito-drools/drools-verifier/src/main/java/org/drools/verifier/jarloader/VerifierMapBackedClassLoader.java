package org.drools.verifier.jarloader;

import org.drools.rule.MapBackedClassLoader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;


/**
 * @author Toni Rikkola trikkola@redhat.com (C) 2011 Red Hat Inc
 */
public class VerifierMapBackedClassLoader extends MapBackedClassLoader {
    public VerifierMapBackedClassLoader(Collection<JarInputStream> jarInputStreams) throws IOException {
        super(createClassLoader(null));

        initialize(jarInputStreams);
    }

    private static ClassLoader createClassLoader(ClassLoader classLoader) {
        if (classLoader == null) {
            classLoader = Thread.currentThread().getContextClassLoader();
            if (classLoader == null) {
                classLoader = VerifierMapBackedClassLoader.class.getClassLoader();
            }
        }
        return classLoader;
    }

    private void initialize(Collection<JarInputStream> jarInputStreams) throws IOException {
        for (JarInputStream jarInputStream : jarInputStreams) {
            readJarEntriesToClassLoader(jarInputStream);
        }
    }

    private void readJarEntriesToClassLoader(JarInputStream jarInputStream) throws IOException {
        byte[] buf = new byte[1024];
        int len;
        JarEntry entry;
        while ((entry = jarInputStream.getNextJarEntry()) != null) {
            if (isValidClassEntry(entry)) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                while ((len = jarInputStream.read(buf)) >= 0) {
                    out.write(buf,
                            0,
                            len);
                }

                addResource(entry.getName(),
                        out.toByteArray());

                out.close();
            }
        }
    }

    private boolean isValidClassEntry(JarEntry entry) {
        return !entry.isDirectory() && entry.getName().endsWith(".class");
    }

}