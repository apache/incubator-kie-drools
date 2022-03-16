/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.maven.plugin.mojos;

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
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.artifact.resolver.filter.CumulativeScopeArtifactFilter;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
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
import org.kie.maven.plugin.ProjectPomModel;
import org.kie.maven.plugin.helpers.CompilerHelper;

import static org.kie.maven.plugin.helpers.ExecModelModeHelper.isModelCompilerInClassPath;
import static org.kie.maven.plugin.helpers.DMNValidationHelper.performDMNDTAnalysis;
import static org.kie.maven.plugin.helpers.DMNValidationHelper.shallPerformDMNDTAnalysis;

/**
 * This goal builds the Drools files belonging to the kproject.
 */
@Mojo(name = "build",
        requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME,
        requiresProject = true,
        defaultPhase = LifecyclePhase.COMPILE)
public class BuildMojo extends AbstractKieMojo {

    @Parameter(defaultValue = "${session}", required = true, readonly = true)
    private MavenSession mavenSession;

    /**
     * Directory containing the generated JAR.
     */
    @Parameter(required = true, defaultValue = "${project.build.outputDirectory}")
    private File outputDirectory;

    /**
     * Project resources folder.
     */
    @Parameter(required = true, defaultValue = "src/main/resources")
    private File sourceFolder;

    @Parameter(required = true, defaultValue = "${project}")
    private MavenProject project;

    @Parameter
    private Map<String, String> properties;

    @Parameter(required = false, defaultValue = "no")
    private String usesPMML;

    /**
     * This container is the same accessed in the KieMavenCli in the kie-wb-common
     */
    @Inject
    private PlexusContainer container;


    public void execute() throws MojoExecutionException, MojoFailureException {
        // BuildMojo is executed when GenerateModelMojo isn't and vice-versa
        boolean modelParameterEnabled = isModelParameterEnabled();
        boolean modelCompilerInClassPath = isModelCompilerInClassPath(project.getDependencies());

        if (!(modelParameterEnabled && modelCompilerInClassPath)) {
            buildDrl();
        }
    }

    private void buildDrl() throws MojoFailureException, MojoExecutionException {
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
                                                                        getClass().getClassLoader());

            Thread.currentThread().setContextClassLoader(projectClassLoader);
        } catch (DependencyResolutionRequiredException | MalformedURLException e) {
            throw new RuntimeException(e);
        }

        try {
            setSystemProperties(properties);

            KieServices ks = KieServices.Factory.get();
            KieBuilderImpl kieBuilder = (KieBuilderImpl) ks.newKieBuilder(project.getBasedir());
            kieBuilder.setPomModel(new ProjectPomModel(mavenSession));
            kieBuilder.buildAll(DrlProject.SUPPLIER,
                                s -> s.contains(sourceFolder.getAbsolutePath()) || s.endsWith("pom.xml"));
            InternalKieModule kModule = (InternalKieModule) kieBuilder.getKieModule();
            ResultsImpl messages = (ResultsImpl)kieBuilder.getResults();

            List<Message> errors = messages != null ? messages.filterMessages( Message.Level.ERROR): Collections.emptyList();

            Map<String, Object> kieMap = getKieMap();
            if (container != null && !kieMap.isEmpty()) {
                CompilerHelper helper = new CompilerHelper();
                helper.share(kieMap, kModule, getLog());
            } else {
                CompilationCacheProvider.get().writeKieModuleMetaInfo(kModule, new DiskResourceStore(outputDirectory));
            }

            if (!errors.isEmpty()) {
                for (Message error : errors) {
                    getLog().error(error.toString());
                }
                throw new MojoFailureException("Build failed!");
            } else {
                writeClassFiles(kModule);
            }

            if (shallPerformDMNDTAnalysis(getValidateDMN(), getLog())) {
                performDMNDTAnalysis(kModule, resources, getLog());
            }
        } finally {
            Thread.currentThread().setContextClassLoader(contextClassLoader);
        }
        getLog().info("KieModule successfully built!");
    }

    private void writeClassFiles( InternalKieModule kModule ) throws MojoFailureException {
        MemoryFileSystem mfs = ((MemoryKieModule )kModule).getMemoryFileSystem();
        kModule.getFileNames()
                .stream()
                .filter(name -> name.endsWith(".class")
                        && !name.contains("target/classes") && !name.contains("target\\classes")
                        && !name.contains("target/test-classes") && !name.contains("target\\test-classes"))
                .forEach( fileName -> {
                    try {
                        saveFile( mfs, fileName );
                    } catch (MojoFailureException e) {
                        throw new RuntimeException( e );
                    }
                } );
    }

    private void saveFile(MemoryFileSystem mfs, String fileName) throws MojoFailureException {
        MemoryFile memFile = (MemoryFile)mfs.getFile(fileName);
        final Path path = Paths.get(outputDirectory.getPath(), memFile.getPath().asString());

        try {
            Files.deleteIfExists(path);
            Files.createDirectories(path);
            Files.copy(memFile.getContents(), path, StandardCopyOption.REPLACE_EXISTING);
        } catch(IOException iox) {
            iox.printStackTrace();
            throw new MojoFailureException("Unable to write file", iox);
        }
    }

    private Map<String, Object> getKieMap() {
        try {
            /**
             * Retrieve the map passed into the Plexus container by the MavenEmbedder from the MavenIncrementalCompiler in the kie-wb-common
             */
            Map<String, Object> kieMap = (Map) container.lookup(Map.class,
                                                                "java.util.HashMap",
                                                                "kieMap");
            return Optional.ofNullable(kieMap).orElse(Collections.emptyMap());
        } catch (ComponentLookupException cle) {
            getLog().info("kieMap not present");
            return Collections.emptyMap();
        }
    }
}