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
package org.kie.dmn.typesafe.compilation;

import java.util.ArrayList;
import java.util.List;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;

public class FileManager extends ForwardingJavaFileManager<JavaFileManager> {

    private List<Code> compiledCode = new ArrayList<>();
    private DMNClassLoader DMNClassLoader;

    public FileManager(JavaFileManager fileManager, DMNClassLoader DMNClassLoader) {
        super(fileManager);
        this.DMNClassLoader = DMNClassLoader;
    }

    @Override
    public JavaFileObject getJavaFileForOutput(
            Location location, String className,
            JavaFileObject.Kind kind, FileObject sibling) {

        try {
            Code innerClass = new Code(className);
            compiledCode.add(innerClass);
            DMNClassLoader.addCode(innerClass);
            return innerClass;
        } catch (Exception e) {
            throw new RuntimeException(
                    "Error while creating in-memory output file for "
                            + className, e);
        }
    }

    @Override
    public java.lang.ClassLoader getClassLoader(Location location) {
        return DMNClassLoader;
    }
}
