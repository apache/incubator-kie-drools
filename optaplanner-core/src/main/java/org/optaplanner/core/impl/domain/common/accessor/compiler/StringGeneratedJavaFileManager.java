/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.domain.common.accessor.compiler;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;

public final class StringGeneratedJavaFileManager extends ForwardingJavaFileManager<JavaFileManager> {

    private final StringGeneratedClassLoader classLoader;

    public StringGeneratedJavaFileManager(JavaFileManager fileManager, StringGeneratedClassLoader classLoader) {
        super(fileManager);
        this.classLoader = classLoader;
    }

    @Override
    public JavaFileObject getJavaFileForOutput(Location location, String qualifiedName, Kind kind, FileObject sibling) {
        if (kind != Kind.CLASS) {
            throw new IllegalArgumentException("Unsupported kind (" + kind + ") for class (" + qualifiedName + ").");
        }
        StringGeneratedClassFileObject fileObject = new StringGeneratedClassFileObject(qualifiedName);
        classLoader.addJavaFileObject(qualifiedName, fileObject);
        return fileObject;
    }

    @Override
    public ClassLoader getClassLoader(Location location) {
        return classLoader;
    }

}
