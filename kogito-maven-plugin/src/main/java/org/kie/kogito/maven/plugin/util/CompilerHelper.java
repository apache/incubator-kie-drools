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
package org.kie.kogito.maven.plugin.util;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.drools.codegen.common.GeneratedFile;
import org.drools.codegen.common.GeneratedFileType;
import org.drools.codegen.common.GeneratedFileWriter;
import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.drools.util.PortablePath;
import org.kie.memorycompiler.CompilationResult;
import org.kie.memorycompiler.JavaCompiler;
import org.kie.memorycompiler.JavaCompilerFactory;
import org.kie.memorycompiler.JavaCompilerSettings;
import org.kie.memorycompiler.JavaConfiguration;

public class CompilerHelper {

    private static final JavaCompiler JAVA_COMPILER = JavaCompilerFactory.loadCompiler(JavaConfiguration.CompilerType.NATIVE, "1.8");
    private static final GeneratedFileWriter.Builder generatedFileWriterBuilder = GeneratedFileWriter.builder("kogito", "kogito.codegen.resources.directory", "kogito.codegen.sources.directory");
    public static final String SOURCES = "SOURCES";
    public static final String RESOURCES = "RESOURCES";

    private CompilerHelper() {
    }

    public static void dumpAndCompileGeneratedSources(Collection<GeneratedFile> generatedSources,
            ClassLoader classLoader,
            MavenProject project,
            File baseDir, Log log) throws MojoExecutionException {
        try {
            JavaCompilerSettings settings = new JavaCompilerSettings();
            for (String path : project.getRuntimeClasspathElements()) {
                File pathFile = new File(path);
                settings.addClasspath(pathFile);
            }
            // Compile and write persistence files
            compileAndWriteClasses(generatedSources, classLoader, settings, getGeneratedFileWriter(baseDir), log);
        } catch (Exception e) {
            throw new MojoExecutionException("Error during processing model classes", e);
        }
    }

    public static void dumpResources(Collection<GeneratedFile> generatedFiles, File baseDir, Log log) {
        GeneratedFileWriter writer = getGeneratedFileWriter(baseDir);
        generatedFiles.forEach(generatedFile -> writeGeneratedFile(generatedFile, writer, log));
    }

    static void writeGeneratedFile(GeneratedFile generatedFile, GeneratedFileWriter writer, Log log) {
        log.info("Generating: " + generatedFile.relativePath());
        writer.write(generatedFile);
    }

    static void compileAndWriteClasses(Collection<GeneratedFile> generatedClasses, ClassLoader cl, JavaCompilerSettings settings, GeneratedFileWriter fileWriter, Log log)
            throws MojoFailureException {
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

            CompilationResult result = JAVA_COMPILER.compile(sources, srcMfs, trgMfs, cl, settings);
            if (result.getErrors().length > 0) {
                throw new MojoFailureException(Arrays.toString(result.getErrors()));
            }

            for (PortablePath path : trgMfs.getFilePaths()) {
                byte[] data = trgMfs.getBytes(path);
                writeGeneratedFile(new GeneratedFile(GeneratedFileType.COMPILED_CLASS, path.asString(), data), fileWriter, log);
            }
        }
    }

    private static GeneratedFileWriter getGeneratedFileWriter(File baseDir) {
        return generatedFileWriterBuilder
                .build(Path.of(baseDir.getAbsolutePath()));
    }
}
