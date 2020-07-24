/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.memorycompiler;

import java.util.ArrayList;
import java.util.List;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;

public class KieMemoryCompilerFileManager extends ForwardingJavaFileManager<JavaFileManager> {

    private List<KieMemoryCompilerByteCode> compiledCode = new ArrayList<>();
    private KieMemoryCompilerClassLoader classLoader;

    public KieMemoryCompilerFileManager(JavaFileManager fileManager, KieMemoryCompilerClassLoader classLoader) {
        super(fileManager);
        this.classLoader = classLoader;
    }

    @Override
    public JavaFileObject getJavaFileForOutput(
            JavaFileManager.Location location, String className,
            JavaFileObject.Kind kind, FileObject sibling) {

        try {
            KieMemoryCompilerByteCode innerClass = new KieMemoryCompilerByteCode(className);
            compiledCode.add(innerClass);
            classLoader.addCode(innerClass);
            return innerClass;
        } catch (Exception e) {
            throw new KieMemoryCompilerException(
                    "Error while creating in-memory output file for "
                            + className, e);
        }
    }

    @Override
    public ClassLoader getClassLoader(JavaFileManager.Location location) {
        return classLoader;
    }
}
