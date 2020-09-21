/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.quarkus.deployment;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MemoryClassLoaderTest  {

    @Test
    public void testMemoryClassLoader() throws IOException, ReflectiveOperationException, URISyntaxException {
        final String className = "/" + MemoryClassLoaderTest.class.getName().replace('.', '/');
        URL url = MemoryClassLoaderTest.class.getResource(className.concat(".class"));
        MemoryFileSystem fs = new MemoryFileSystem();
        try(ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            InputStream input = Files.newInputStream(Paths.get(url.toURI()))) {
            transferTo(input, bytes);
            fs.write(className.concat(".class"), bytes.toByteArray());
        }
        MemoryClassLoader cl = new MemoryClassLoader(fs,null);
        Class<?> clazz = cl.loadClass(MemoryClassLoaderTest.class.getName());
        assertTrue(Modifier.isPublic(clazz.getMethod("testMemoryClassLoader").getModifiers()));
        assertThrows(NoSuchFieldException.class, () -> clazz.getField("otherField"));
    }

    private void transferTo(InputStream in, OutputStream out) throws IOException {
        // from java 9
        //in.transferTo(out);
        byte[] buffer = new byte[1000];
        int size;
        while ((size = in.read(buffer)) != -1) {
            out.write(buffer, 0, size);
        }
    }
}
