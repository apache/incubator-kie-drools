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
package org.kie.pmml.models.regression.compiler.utils;

import java.util.Map;
import java.util.stream.Collectors;

import org.drools.compiler.commons.jci.compilers.CompilationResult;
import org.drools.compiler.commons.jci.compilers.JavaCompiler;
import org.drools.compiler.commons.jci.compilers.NativeJavaCompiler;
import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.kie.internal.jci.CompilationProblem;

public class KiePMMLRegressionCompiler {

    private static final MemoryFileSystem SRC_MFS = new MemoryFileSystem();
    private static final MemoryFileSystem TGT_MFS = new MemoryFileSystem();
    private static final JavaCompiler JAVA_COMPILER = new NativeJavaCompiler();

    /**
     * Compile the given sources and add compiled classes to the given <code>ClassLoader</code>
     * <b>classNameSourceMap</b>' key must be the <b>FQDN</b> of the class to compile
     * @param classNameSourceMap
     * @param classLoader
     * @return
     */
    public static Map<String, Class<?>> compile(Map<String, String> classNameSourceMap, ClassLoader classLoader) {
        classNameSourceMap.forEach((className, source) -> {
            String fileNameBase = className.contains(".") ? className.substring(className.lastIndexOf('.') + 1) : className;
            SRC_MFS.write(fileNameBase + ".java", source.getBytes());
        });
        String[] sourceFiles = classNameSourceMap.keySet().stream().map(className -> {
            String fileNameBase = className.contains(".") ? className.substring(className.lastIndexOf('.') + 1) : className;
            return fileNameBase + ".java";
        }).toArray(String[]::new);
        InternalClassLoader internalClassLoader = new InternalClassLoader(classLoader);
        CompilationResult res = JAVA_COMPILER.compile(sourceFiles,
                                                      SRC_MFS,
                                                      TGT_MFS,
                                                      internalClassLoader);
        if (res.getErrors().length != 0) {
            StringBuilder errorBuilder = new StringBuilder();
            for (CompilationProblem compilationProblem : res.getErrors()) {
                errorBuilder.append(compilationProblem.getMessage() + "; ");
            }
            throw new RuntimeException("Compilation failed due to: " + errorBuilder.toString());
        }
        return classNameSourceMap.keySet().stream()
                .collect(Collectors.toMap(fullClassName -> fullClassName,
                                          fullClassName -> {
                                              String classFile = fullClassName.replace(".", "/") + ".class";
                                              byte[] byteCode = TGT_MFS.getMap().get(classFile);
                                              return internalClassLoader.addClass(fullClassName, byteCode);
                                          }));
    }

    private static class InternalClassLoader extends ClassLoader {

        InternalClassLoader(ClassLoader parent) {
            super(parent);
        }

        private Class<?> addClass(String className, byte[] byteCode) {
            return defineClass(className, byteCode, 0, byteCode.length);
        }
    }
}
