/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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

import org.drools.compiler.compiler.io.memory.MemoryFile;
import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.drools.compiler.compiler.io.memory.MemoryFolder;

public class MemoryClassLoader extends ClassLoader {

    private MemoryFileSystem fs;

    public MemoryClassLoader(MemoryFileSystem fs, ClassLoader parent) {
        super(parent);
        this.fs = fs;
    }

    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] ba = fs.getFileContents(new MemoryFile(fs, name.replace('.', '/').concat(".class"), new MemoryFolder(fs, "")));
        return ba != null ? defineClass(name, ba, 0, ba.length) : super.findClass(name);
    }
}
