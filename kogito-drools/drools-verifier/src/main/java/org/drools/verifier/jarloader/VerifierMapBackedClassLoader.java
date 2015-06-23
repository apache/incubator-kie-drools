/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.verifier.jarloader;

import org.drools.core.rule.MapBackedClassLoader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;


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
