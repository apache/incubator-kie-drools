/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.kogito.codegen.manager;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.drools.codegen.common.GeneratedFile;
import org.drools.codegen.common.GeneratedFileType;
import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.kie.memorycompiler.CompilationProblem;
import org.kie.memorycompiler.CompilationResult;
import org.kie.memorycompiler.JavaCompiler;
import org.kie.memorycompiler.JavaCompilerFactory;
import org.kie.memorycompiler.JavaCompilerSettings;
import org.kie.memorycompiler.JavaConfiguration;

public class CompilerHelper {

    private static final JavaCompiler JAVA_COMPILER = JavaCompilerFactory.loadCompiler(JavaConfiguration.CompilerType.NATIVE, "17");
    public static final String SOURCES = "SOURCES";
    public static final String RESOURCES = "RESOURCES";

    private CompilerHelper() {
    }

    public record CompileInfo(Collection<GeneratedFile> generatedSources,
            Collection<GeneratedFile> resources,
            ClassLoader projectClassLoader,
            List<String> runtimeClassPathElements,
            File baseDir,
            String javaSourceEncoding,
            String javaVersion) {

        public CompileInfo(Collection<GeneratedFile> generatedSources, Collection<GeneratedFile> resources, GenerateModelHelper.GenerateModelInfo generateModelInfo) {
            this(generatedSources,
                    resources,
                    generateModelInfo.projectClassLoader(),
                    generateModelInfo.runtimeClassPathElements(),
                    generateModelInfo.baseDir(),
                    generateModelInfo.javaSourceEncoding(),
                    generateModelInfo.javaVersion());
        }
    }

    public static void compileAndDump(CompileInfo compileInfo) {
        compileAndWriteClasses(compileInfo.generatedSources(),
                compileInfo.projectClassLoader,
                buildJavaCompilerSettings(compileInfo.runtimeClassPathElements,
                        compileInfo.javaSourceEncoding,
                        compileInfo.javaVersion),
                compileInfo.baseDir().toPath());
    }

    static void compileAndWriteClasses(Collection<GeneratedFile> generatedClasses,
            ClassLoader classLoader,
            JavaCompilerSettings javaCompilerSettings,
            Path baseDir) {
        MemoryFileSystem srcMfs = new MemoryFileSystem();
        MemoryFileSystem trgMfs = new MemoryFileSystem();

        String[] sources = new String[generatedClasses.size()];
        int index = 0;
        for (GeneratedFile entry : generatedClasses) {
            String fileName = entry.relativePath();
            sources[index++] = fileName;
            srcMfs.write(fileName, entry.contents());
        }

        if (sources.length > 0) {
            CompilationResult result = JAVA_COMPILER.compile(sources, srcMfs, trgMfs, classLoader, javaCompilerSettings);

            if (result.getErrors().length > 0) {
                throw new IllegalStateException(Arrays.stream(result.getErrors())
                        .map(CompilationProblem::getMessage)
                        .collect(Collectors.joining(",")));
            }

            Collection<GeneratedFile> compiledClasses = trgMfs.getFilePaths().stream()
                    .map(path -> new GeneratedFile(
                            GeneratedFileType.COMPILED_CLASS,
                            path.asString(),
                            trgMfs.getBytes(path)))
                    .toList();

            GeneratedFileManager.dumpGeneratedFiles(compiledClasses, baseDir);
        }
    }

    static JavaCompilerSettings buildJavaCompilerSettings(List<String> runtimeClassPathElements,
            String sourceEncoding,
            String javaVersion) {
        JavaCompilerSettings settings = new JavaCompilerSettings();
        for (String path : runtimeClassPathElements) {
            settings.addClasspath(new File(path));
        }
        settings.setSourceEncoding(sourceEncoding);
        settings.setSourceVersion(javaVersion);
        settings.setTargetVersion(javaVersion);
        return settings;
    }
}
