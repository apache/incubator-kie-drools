/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.maven.plugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;

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
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.compiler.kie.builder.impl.KieBuilderImpl;
import org.drools.compiler.kie.builder.impl.KieMetaInfoBuilder;
import org.drools.compiler.kie.builder.impl.MemoryKieModule;
import org.drools.compiler.kie.builder.impl.ResultsImpl;
import org.kie.api.KieServices;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.internal.io.ResourceFactory;
import org.kie.kogito.maven.plugin.util.MojoUtil;

/**
 * This goal builds the Drools files belonging to the kproject.
 */
@Mojo(name = "build",
        requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME,
        requiresProject = true,
        defaultPhase = LifecyclePhase.COMPILE)
public class BuildMojo extends AbstractKieMojo {

    /**
     * Directory containing the generated JAR.
     */
    @Parameter(required = true, defaultValue = "${project.build.outputDirectory}")
    private File outputDirectory;
    @Parameter(required = true, defaultValue = "${project.build.directory}")
    private File targetDirectory;

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


    @Parameter(property = "generateModel", defaultValue = "no")
    private String generateModel;

    public void execute() throws MojoExecutionException, MojoFailureException {
        if(!ExecModelMode.shouldGenerateModel(generateModel)) {
            buildDrl();
        }
    }

    private void buildDrl() throws MojoFailureException, MojoExecutionException {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            List<InternalKieModule> kmoduleDeps = new ArrayList<>();
            ClassLoader projectClassLoader = MojoUtil.createProjectClassLoader(this.getClass().getClassLoader(),
                                                                               project,
                                                                               outputDirectory,
                                                                               kmoduleDeps);
            Thread.currentThread().setContextClassLoader(projectClassLoader);

            KieServices ks = KieServices.Factory.get();
            setSystemProperties(properties);

            KieFileSystem kfs = ks.newKieFileSystem();
            for (File file : getResourceFiles(sourceFolder)) {
                if (!file.getPath().contains("META-INF")) {
                    kfs.write( ResourceFactory.newFileResource(file) );
                }
            }

            KieBuilderImpl kieBuilder = new KieBuilderImpl(kfs);
            InternalKieModule kModule = (InternalKieModule)kieBuilder.getKieModule();
            for (InternalKieModule kmoduleDep : kmoduleDeps) {
                kModule.addKieDependency(kmoduleDep);
            }

            kieBuilder.buildAll();
            ResultsImpl messages = (ResultsImpl)kieBuilder.getResults();

            List<Message> errors = messages != null ? messages.filterMessages( Message.Level.ERROR): Collections.emptyList();

            if (container != null) {
                Map<String, Object> kieMap = getKieMap();
                if (!kieMap.isEmpty()) {
                    CompilerHelper helper = new CompilerHelper();
                    helper.share(kieMap, kModule, getLog());
                }else{
                    getLog().info("Kie Map empty");
                }
            } else {
                new KieMetaInfoBuilder(kModule).writeKieModuleMetaInfo(new DiskResourceStore(outputDirectory));
            }

            if (!errors.isEmpty()) {
                for (Message error : errors) {
                    getLog().error(error.toString());
                }
                throw new MojoFailureException("Build failed!");
            } else {
                writeClassFiles( kModule );
            }
        } finally {
            Thread.currentThread().setContextClassLoader(contextClassLoader);
        }
        getLog().info("KieModule successfully built!");
    }

    private List<File> getResourceFiles(File parent) {
        List<File> files = new ArrayList<>();
        if (parent.isDirectory()) {
            File[] children = parent.listFiles();
            if (children != null) {
                for (File child: children) {
                    if (child.isDirectory()) {
                        files.addAll(getResourceFiles(child));
                    } else {
                        files.add(child);
                    }
                }
            }
        }
        return files;
    }

    private void writeClassFiles( InternalKieModule kModule ) throws MojoFailureException {
        MemoryFileSystem mfs = ((MemoryKieModule )kModule).getMemoryFileSystem();
        for (String name : kModule.getFileNames()) {
            if (name.endsWith(".class")) {
                saveFile(mfs, name);
            }
        }
    }

    private void saveFile(MemoryFileSystem mfs, String fileName) throws MojoFailureException {
        MemoryFile memFile = (MemoryFile)mfs.getFile(fileName);
        final Path path = Paths.get(outputDirectory.getPath(), memFile.getPath().toPortableString());

        try {
            Files.deleteIfExists(path);
            Files.createDirectories(path);
            Files.copy(memFile.getContents(), path, StandardCopyOption.REPLACE_EXISTING);
        } catch(IOException iox) {
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