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

import java.util.HashMap;
import java.util.Map;

public final class StringGeneratedClassLoader extends ClassLoader {

    private final Map<String, StringGeneratedClassFileObject> fileObjectMap = new HashMap<>();

    public StringGeneratedClassLoader(ClassLoader parent) {
        super(parent);
    }

    @Override
    protected Class<?> findClass(String fullClassName) throws ClassNotFoundException {
        StringGeneratedClassFileObject fileObject = fileObjectMap.get(fullClassName);
        if (fileObject != null) {
            byte[] classBytes = fileObject.getClassBytes();
            return defineClass(fullClassName, classBytes, 0, classBytes.length);
        }
        return super.findClass(fullClassName);
    }

    public void addJavaFileObject(String qualifiedName, StringGeneratedClassFileObject fileObject) {
        fileObjectMap.put(qualifiedName, fileObject);
    }

}
