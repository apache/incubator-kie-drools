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
import org.drools.codegen.common.GeneratedFileWriter;
import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.drools.util.PortablePath;
import org.kie.memorycompiler.CompilationProblem;
import org.kie.memorycompiler.CompilationResult;
import org.kie.memorycompiler.JavaCompiler;
import org.kie.memorycompiler.JavaCompilerFactory;
import org.kie.memorycompiler.JavaCompilerSettings;
import org.kie.memorycompiler.JavaConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CompilerHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(CompilerHelper.class);

    private static final JavaCompiler JAVA_COMPILER = JavaCompilerFactory.loadCompiler(JavaConfiguration.CompilerType.NATIVE, "17");
    private static final GeneratedFileWriter.Builder GENERATED_FILE_WRITER_BUILDER =
            GeneratedFileWriter.builder("kogito", "kogito.codegen.resources.directory", "kogito.codegen.sources.directory");
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
        compileAndDumpGeneratedSources(compileInfo);
        dumpResources(compileInfo.resources(), compileInfo.baseDir());
    }

    public static void compileAndDumpGeneratedSources(CompileInfo compileInfo) {
        // Compile and write files
        compileAndWriteClasses(compileInfo.generatedSources(),
                compileInfo.projectClassLoader,
                buildJavaCompilerSettings(compileInfo.runtimeClassPathElements,
                        compileInfo.javaSourceEncoding,
                        compileInfo.javaVersion),
                getGeneratedFileWriter(compileInfo.baseDir));
        // Dump resources
        writeFiles(compileInfo.generatedSources, compileInfo.baseDir);
    }

    public static void dumpResources(Collection<GeneratedFile> resources, Path baseDir) {
        dumpResources(resources, baseDir.toFile());
    }

    public static void dumpResources(Collection<GeneratedFile> resources, File baseDir) {
        writeFiles(resources, baseDir);
    }

    static void writeFiles(Collection<GeneratedFile> toWrite, File baseDir) {
        GeneratedFileWriter writer = getGeneratedFileWriter(baseDir);
        toWrite.forEach(generatedFile -> writeGeneratedFile(generatedFile, writer));
    }

    static void writeFiles(Collection<GeneratedFile> toWrite, Path baseDir) {
        GeneratedFileWriter writer = getGeneratedFileWriter(baseDir);
        toWrite.forEach(generatedFile -> writeGeneratedFile(generatedFile, writer));
    }

    static GeneratedFileWriter getGeneratedFileWriter(Path baseDir) {
        return GENERATED_FILE_WRITER_BUILDER.build(baseDir);
    }

    static void writeGeneratedFile(GeneratedFile generatedFile, GeneratedFileWriter writer) {
        LOGGER.info("Writing compiled class: {}", generatedFile.relativePath());
        writer.write(generatedFile);
    }

    static void compileAndWriteClasses(Collection<GeneratedFile> generatedClasses,
            ClassLoader classLoader,
            JavaCompilerSettings javaCompilerSettings,
            GeneratedFileWriter fileWriter) {
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

            for (PortablePath path : trgMfs.getFilePaths()) {
                byte[] data = trgMfs.getBytes(path);
                writeGeneratedFile(new GeneratedFile(GeneratedFileType.COMPILED_CLASS, path.asString(), data), fileWriter);
            }
        }
    }

    static GeneratedFileWriter getGeneratedFileWriter(File baseDir) {
        return GENERATED_FILE_WRITER_BUILDER.build(Path.of(baseDir.getAbsolutePath()));
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
