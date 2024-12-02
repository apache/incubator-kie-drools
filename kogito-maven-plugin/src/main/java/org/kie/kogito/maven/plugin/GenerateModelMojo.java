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

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.drools.codegen.common.GeneratedFile;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.maven.plugin.util.CompilerHelper;
import org.kie.kogito.maven.plugin.util.GenerateModelHelper;
import org.kie.kogito.maven.plugin.util.PersistenceGenerationHelper;
import org.reflections.Reflections;

import static org.kie.efesto.common.api.constants.Constants.INDEXFILE_DIRECTORY_PROPERTY;
import static org.kie.kogito.maven.plugin.util.CompilerHelper.RESOURCES;
import static org.kie.kogito.maven.plugin.util.CompilerHelper.SOURCES;

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

    @Parameter(property = "kogito.jsonSchema.version", required = false)
    String schemaVersion;

    @Parameter(defaultValue = "${mojoExecution}")
    private MojoExecution mojoExecution;

    /**
     * The <code>maven-compiler-plugin</code> version to use.
     * Default to <b>3.8.1</b>
     */
    @Parameter(defaultValue = "3.8.1", property = "version.compiler.plugin")
    private String compilerPluginVersion;

    @Component
    private MavenProject mavenProject;

    @Component
    private MavenSession mavenSession;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
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
        Map<String, Collection<GeneratedFile>> generatedModelFiles;
        ClassLoader projectClassLoader = projectClassLoader();
        KogitoBuildContext kogitoBuildContext = discoverKogitoRuntimeContext(projectClassLoader);
        if (isOnDemand()) {
            getLog().info("On-Demand Mode is On. Use mvn compile kogito:scaffold");
            generatedModelFiles = new HashMap<>();
        } else {
            generatedModelFiles = generateModel(kogitoBuildContext);
        }
        // TODO to be removed with DROOLS-7090
        if (indexFileDirectorySet) {
            System.clearProperty(INDEXFILE_DIRECTORY_PROPERTY);
        }

        // Compile and write model files
        compileAndDump(generatedModelFiles, projectClassLoader, getLog());

        Map<String, Collection<GeneratedFile>> generatedPersistenceFiles = generatePersistence(kogitoBuildContext, getReflections(projectClassLoader));

        compileAndDump(generatedPersistenceFiles, projectClassLoader, getLog());

        if (!keepSources) {
            deleteDrlFiles();
        }
    }

    protected boolean isOnDemand() {
        return onDemand;
    }

    protected void addCompileSourceRoots() {
        project.addCompileSourceRoot(getGeneratedFileWriter().getScaffoldedSourcesDir().toString());
    }

    protected Map<String, Collection<GeneratedFile>> generateModel(KogitoBuildContext kogitoBuildContext) {
        setSystemProperties(properties);
        return GenerateModelHelper.generateModelFiles(kogitoBuildContext, generatePartial);
    }

    protected Map<String, Collection<GeneratedFile>> generatePersistence(KogitoBuildContext kogitoBuildContext, Reflections reflections) throws MojoExecutionException {
        return PersistenceGenerationHelper.generatePersistenceFiles(kogitoBuildContext, reflections, schemaVersion);
    }

    protected void compileAndDump(Map<String, Collection<GeneratedFile>> generatedFiles, ClassLoader classloader, Log log) throws MojoExecutionException {
        // Compile and write files
        CompilerHelper.dumpAndCompileGeneratedSources(generatedFiles.get(SOURCES), classloader, project, baseDir, log);
        // Dump resources
        CompilerHelper.dumpResources(generatedFiles.get(RESOURCES), baseDir, log);
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
