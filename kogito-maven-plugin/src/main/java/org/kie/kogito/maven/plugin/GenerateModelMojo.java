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
package org.kie.kogito.maven.plugin;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.drools.codegen.common.GeneratedFile;
import org.drools.codegen.common.GeneratedFileType;
import org.kie.kogito.codegen.core.ApplicationGenerator;
import org.kie.kogito.codegen.core.utils.ApplicationGeneratorDiscovery;

import static org.drools.codegen.common.GeneratedFileType.COMPILED_CLASS;
import static org.kie.efesto.common.api.constants.Constants.INDEXFILE_DIRECTORY_PROPERTY;

@Mojo(name = "generateModel",
        requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME,
        requiresProject = true,
        defaultPhase = LifecyclePhase.COMPILE,
        threadSafe = true)
public class GenerateModelMojo extends AbstractKieMojo {

    public static final PathMatcher drlFileMatcher = FileSystems.getDefault().getPathMatcher("glob:**.drl");

    /**
     * Partial generation can be used when reprocessing a pre-compiled project
     * for faster code-generation. It only generates code for rules and processes,
     * and does not generate extra meta-classes (etc. Application).
     * Use only when doing recompilation and for development purposes
     */
    @Parameter(property = "kogito.codegen.partial", defaultValue = "false")
    private boolean generatePartial;

    @Parameter(property = "kogito.codegen.ondemand", defaultValue = "false")
    private boolean onDemand;

    @Parameter(property = "kogito.sources.keep", defaultValue = "false")
    private boolean keepSources;

    @Override
    public void execute() throws MojoExecutionException {
        // TODO to be removed with DROOLS-7090
        boolean indexFileDirectorySet = false;
        getLog().debug("execute -> " + outputDirectory);
        if (outputDirectory == null) {
            throw new MojoExecutionException("${project.build.directory} is null");
        }
        if (System.getProperty(INDEXFILE_DIRECTORY_PROPERTY) == null) {
            System.setProperty(INDEXFILE_DIRECTORY_PROPERTY, outputDirectory.toString());
            indexFileDirectorySet = true;
        }
        addCompileSourceRoots();
        if (isOnDemand()) {
            getLog().info("On-Demand Mode is On. Use mvn compile kogito:scaffold");
        } else {
            generateModel();
        }
        // TODO to be removed with DROOLS-7090
        if (indexFileDirectorySet) {
            System.clearProperty(INDEXFILE_DIRECTORY_PROPERTY);
        }
    }

    protected boolean isOnDemand() {
        return onDemand;
    }

    protected void addCompileSourceRoots() {
        project.addCompileSourceRoot(getGeneratedFileWriter().getScaffoldedSourcesDir().toString());
    }

    protected void generateModel() throws MojoExecutionException {

        setSystemProperties(properties);

        ClassLoader projectClassLoader = projectClassLoader();
        ApplicationGenerator appGen = ApplicationGeneratorDiscovery.discover(discoverKogitoRuntimeContext(projectClassLoader));

        Collection<GeneratedFile> generatedFiles;
        if (generatePartial) {
            generatedFiles = appGen.generateComponents();
        } else {
            generatedFiles = appGen.generate();
        }

        Map<GeneratedFileType, List<GeneratedFile>> mappedGeneratedFiles = generatedFiles.stream()
                .collect(Collectors.groupingBy(GeneratedFile::type));
        List<GeneratedFile> generatedUncompiledFiles = mappedGeneratedFiles.entrySet().stream()
                .filter(entry -> !entry.getKey().equals(COMPILED_CLASS))
                .flatMap(entry -> entry.getValue().stream())
                .toList();
        writeGeneratedFiles(generatedUncompiledFiles);

        List<GeneratedFile> generatedCompiledFiles = mappedGeneratedFiles.getOrDefault(COMPILED_CLASS,
                Collections.emptyList())
                .stream().map(originalGeneratedFile -> new GeneratedFile(COMPILED_CLASS, convertPath(originalGeneratedFile.path().toString()), originalGeneratedFile.contents()))
                .collect(Collectors.toList());

        writeGeneratedFiles(generatedCompiledFiles);

        if (!keepSources) {
            deleteDrlFiles();
        }
    }

    private String convertPath(String toConvert) {
        return toConvert.replace('.', File.separatorChar) + ".class";
    }

    private void deleteDrlFiles() throws MojoExecutionException {
        // Remove drl files
        try (final Stream<Path> drlFiles = Files.find(outputDirectory.toPath(), Integer.MAX_VALUE,
                (p, f) -> drlFileMatcher.matches(p))) {
            drlFiles.forEach(p -> {
                try {
                    Files.delete(p);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            });
        } catch (IOException e) {
            throw new MojoExecutionException("Unable to find .drl files");
        }
    }
}
