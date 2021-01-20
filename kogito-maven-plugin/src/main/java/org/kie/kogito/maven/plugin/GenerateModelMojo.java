/*
 *  Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
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
import java.util.stream.Stream;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.kie.kogito.codegen.ApplicationGenerator;
import org.kie.kogito.codegen.GeneratedFile;
import org.kie.kogito.codegen.utils.ApplicationGeneratorDiscovery;

@Mojo(name = "generateModel",
        requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME,
        requiresProject = true,
        defaultPhase = LifecyclePhase.COMPILE,
        threadSafe = true)
public class GenerateModelMojo extends AbstractKieMojo {

    public static final PathMatcher drlFileMatcher = FileSystems.getDefault().getPathMatcher("glob:**.drl");

    @Parameter(property = "kogito.codegen.sources.directory", defaultValue = "${project.build.directory}/generated-sources/kogito")
    private File customizableSourcesPath;

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
        addCompileSourceRoots();
        if (isOnDemand()) {
            getLog().info("On-Demand Mode is On. Use mvn compile kogito:scaffold");
        } else {
            generateModel();
        }
    }

    protected boolean isOnDemand() {
        return onDemand;
    }

    @Override
    protected File getSourcesPath() {
        return customizableSourcesPath;
    }

    protected void addCompileSourceRoots() {
        project.addCompileSourceRoot(getSourcesPath().getPath());
        project.addCompileSourceRoot(generatedSources.getPath());
    }

    protected void generateModel() throws MojoExecutionException {

        setSystemProperties(properties);

        ApplicationGenerator appGen = ApplicationGeneratorDiscovery
                .discover(discoverKogitoRuntimeContext(projectClassLoader()));

        Collection<GeneratedFile> generatedFiles;
        if (generatePartial) {
            generatedFiles = appGen.generateComponents();
        } else {
            generatedFiles = appGen.generate();
        }

        writeGeneratedFiles(generatedFiles);

        if (!keepSources) {
            deleteDrlFiles();
        }
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
