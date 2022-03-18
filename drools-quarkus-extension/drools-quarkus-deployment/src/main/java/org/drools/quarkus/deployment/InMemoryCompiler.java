/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.quarkus.deployment;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;

import io.quarkus.maven.dependency.ResolvedDependency;
import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.drools.model.project.codegen.GeneratedFile;
import org.drools.modelcompiler.builder.JavaParserCompiler;
import org.kie.memorycompiler.CompilationProblem;
import org.kie.memorycompiler.CompilationResult;
import org.kie.memorycompiler.JavaCompiler;
import org.kie.memorycompiler.JavaCompilerSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.drools.quarkus.deployment.DroolsQuarkusResourceUtils.toClassName;

/**
 * A instance of a Java compiler with a given classpath,
 * default flags, and its target directories.
 */
public class InMemoryCompiler {

    private static final Logger logger = LoggerFactory.getLogger(InMemoryCompiler.class);

    private final JavaCompiler javaCompiler;
    private final JavaCompilerSettings compilerSettings;
    private final MemoryFileSystem trgMfs = new MemoryFileSystem();

    public InMemoryCompiler(
            Collection<Path> classesPaths,
            Collection<ResolvedDependency> userDependencies,
            boolean useDebugSymbols) {
        javaCompiler = JavaParserCompiler.getCompiler();
        compilerSettings = javaCompiler.createDefaultSettings();
        compilerSettings.addOption("-proc:none"); // force disable annotation processing
        if (useDebugSymbols) {
            compilerSettings.addOption("-g");
            compilerSettings.addOption("-parameters");
        }
        for (Path classPath : classesPaths) {
            compilerSettings.addClasspath(classPath.toFile());
        }
        for (ResolvedDependency i : userDependencies) {
            compilerSettings.addClasspath(i.getResolvedPaths().getSinglePath().toFile());
        }
    }

    /**
     * Compiles the given {@link GeneratedFile}s and returns the compilation results.
     * It throws an {@link IllegalStateException} if there are errors.
     */
    public CompilationResult compile(Collection<GeneratedFile> generatedFiles) {
        MemoryFileSystem srcMfs = new MemoryFileSystem();

        String[] sources = new String[generatedFiles.size()];
        int index = 0;
        for (GeneratedFile entry : generatedFiles) {
            String relativePath = entry.relativePath();
            logger.trace("Relative path {}", relativePath);
            // verify if this is still needed https://issues.redhat.com/browse/KOGITO-3085
            String generatedClassFile = relativePath.replace("src/main/java/", "");
            logger.trace("generatedClassFile {}", generatedClassFile);
            String fileName = toRuntimeSource(toClassName(generatedClassFile));
            logger.trace("fileName {}", fileName);
            sources[index++] = fileName;

            srcMfs.write(fileName, entry.contents());
        }

        CompilationResult result = javaCompiler.compile(
                sources,
                srcMfs,
                trgMfs,
                Thread.currentThread().getContextClassLoader(),
                compilerSettings);

        if (result.getErrors().length > 0) {
            StringBuilder errorInfo = new StringBuilder();
            for (CompilationProblem compilationProblem : result.getErrors()) {
                errorInfo.append(compilationProblem.toString());
                errorInfo.append("\n");
                logger.error(compilationProblem.toString());
            }
            Arrays.stream(result.getErrors()).forEach(cp -> errorInfo.append(cp.toString()));
            throw new IllegalStateException(errorInfo.toString());
        }

        return result;
    }

    /**
     * @return the MemoryFileSystem where class files have been generated
     */
    public MemoryFileSystem getTargetFileSystem() {
        return trgMfs;
    }

    private String toRuntimeSource(String className) {
        // verify if this is still needed https://issues.redhat.com/browse/KOGITO-3085
        return "src/main/java/" + className.replace('.', '/') + ".java";
    }
}
