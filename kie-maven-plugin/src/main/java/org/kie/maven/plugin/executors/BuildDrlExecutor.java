/**
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
package org.kie.maven.plugin.executors;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.artifact.resolver.filter.CumulativeScopeArtifactFilter;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.drools.compiler.compiler.io.memory.MemoryFile;
import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.drools.compiler.kie.builder.impl.CompilationCacheProvider;
import org.drools.compiler.kie.builder.impl.DrlProject;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.compiler.kie.builder.impl.KieBuilderImpl;
import org.drools.compiler.kie.builder.impl.MemoryKieModule;
import org.drools.compiler.kie.builder.impl.ResultsImpl;
import org.kie.api.KieServices;
import org.kie.api.builder.Message;
import org.kie.maven.plugin.DiskResourceStore;
import org.kie.maven.plugin.KieMavenPluginContext;
import org.kie.maven.plugin.ProjectPomModel;

import static org.kie.maven.plugin.helpers.DMNValidationHelper.performDMNDTAnalysis;
import static org.kie.maven.plugin.helpers.DMNValidationHelper.shallPerformDMNDTAnalysis;
import static org.kie.maven.plugin.helpers.ExecutorHelper.setSystemProperties;

/**
 * This executor builds the Drools files belonging to the kproject.
 */
public class BuildDrlExecutor {

    private BuildDrlExecutor() {
    }

    public static void buildDrl(final KieMavenPluginContext kieMavenPluginContext) throws MojoFailureException, MojoExecutionException {
        final MavenProject project = kieMavenPluginContext.getProject();
        final MavenSession mavenSession = kieMavenPluginContext.getMavenSession();
        final File outputDirectory = kieMavenPluginContext.getOutputDirectory();
        final Map<String, String> properties = kieMavenPluginContext.getProperties();
        final File resourceFolder = kieMavenPluginContext.getResourceFolder();
        final List<Resource> resources = kieMavenPluginContext.getResources();
        final String validateDMN = kieMavenPluginContext.getValidateDMN();
        final Log log = kieMavenPluginContext.getLog();

        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Set<URL> urls = new HashSet<>();
            for (String element : project.getCompileClasspathElements()) {
                urls.add(new File(element).toURI().toURL());
            }

            project.setArtifactFilter(new CumulativeScopeArtifactFilter(Arrays.asList("compile",
                                                                                      "runtime")));
            for (Artifact artifact : project.getArtifacts()) {
                File file = artifact.getFile();
                if (file != null) {
                    urls.add(file.toURI().toURL());
                }
            }
            urls.add(outputDirectory.toURI().toURL());

            ClassLoader projectClassLoader = URLClassLoader.newInstance(urls.toArray(new URL[0]),
                                                                        BuildDrlExecutor.class.getClassLoader());

            Thread.currentThread().setContextClassLoader(projectClassLoader);
        } catch (DependencyResolutionRequiredException | MalformedURLException e) {
            throw new RuntimeException(e);
        }

        try {
            setSystemProperties(properties, log);

            KieServices ks = KieServices.Factory.get();
            KieBuilderImpl kieBuilder = (KieBuilderImpl) ks.newKieBuilder(project.getBasedir());
            kieBuilder.setPomModel(new ProjectPomModel(mavenSession));
            kieBuilder.buildAll(DrlProject.SUPPLIER, s -> s.contains(resourceFolder.getAbsolutePath()) || s.endsWith(
                    "pom.xml"));
            InternalKieModule kModule = (InternalKieModule) kieBuilder.getKieModule();
            ResultsImpl messages = (ResultsImpl)kieBuilder.getResults();

            List<Message> errors = messages != null ? messages.filterMessages( Message.Level.ERROR): Collections.emptyList();

            CompilationCacheProvider.get().writeKieModuleMetaInfo(kModule, new DiskResourceStore(outputDirectory));

            if (!errors.isEmpty()) {
                for (Message error : errors) {
                    log.error(error.toString());
                }
                throw new MojoFailureException("Build failed!");
            } else {
                writeClassFiles(kModule, outputDirectory);
            }

            if (shallPerformDMNDTAnalysis(validateDMN, log)) {
                performDMNDTAnalysis(kModule, resources, log);
            }
        } finally {
            Thread.currentThread().setContextClassLoader(contextClassLoader);
        }
        log.info("KieModule successfully built!");
    }

    private static void writeClassFiles( InternalKieModule kModule, File outputDirectory ) throws MojoFailureException {
        MemoryFileSystem mfs = ((MemoryKieModule )kModule).getMemoryFileSystem();
        kModule.getFileNames()
                .stream()
                .filter(name -> name.endsWith(".class")
                        && !name.contains("target/classes") && !name.contains("target\\classes")
                        && !name.contains("target/test-classes") && !name.contains("target\\test-classes"))
                .forEach( fileName -> {
                    try {
                        saveFile( mfs, fileName, outputDirectory );
                    } catch (MojoFailureException e) {
                        throw new RuntimeException( e );
                    }
                } );
    }

    private static void saveFile(MemoryFileSystem mfs, String fileName, File outputDirectory) throws MojoFailureException {
        MemoryFile memFile = (MemoryFile)mfs.getFile(fileName);
        final Path path = Paths.get(outputDirectory.getPath(), memFile.getPath().asString());

        try {
            Files.deleteIfExists(path);
            Files.createDirectories(path);
            Files.copy(memFile.getContents(), path, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException iox) {
            throw new MojoFailureException("Unable to write file", iox);
        }
    }

}