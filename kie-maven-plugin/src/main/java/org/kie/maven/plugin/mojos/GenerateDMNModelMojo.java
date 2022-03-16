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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.compiler.kie.builder.impl.KieBuilderImpl;
import org.kie.api.KieServices;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceConfiguration;
import org.kie.api.io.ResourceType;
import org.kie.api.io.ResourceWithConfiguration;
import org.kie.dmn.api.core.GeneratedSource;
import org.kie.dmn.core.api.DMNFactory;
import org.kie.dmn.core.assembler.DMNAssemblerService;
import org.kie.dmn.core.compiler.DMNCompilerConfigurationImpl;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceWithConfigurationImpl;
import org.kie.maven.plugin.helpers.DMNModelModeHelper;
import org.kie.memorycompiler.JavaCompilerSettings;

import static org.kie.maven.plugin.helpers.ExecModelModeHelper.isModelCompilerInClassPath;
import static org.kie.maven.plugin.helpers.GenerateCodeHelper.compileAndWriteClasses;
import static org.kie.maven.plugin.helpers.GenerateCodeHelper.createJavaCompilerSettings;

@Mojo(name = "generateDMNModel",
        requiresDependencyResolution = ResolutionScope.NONE,
        requiresProject = true,
        defaultPhase = LifecyclePhase.COMPILE)
public class GenerateDMNModelMojo extends AbstractKieMojo {

    public static final String RULE_CLASS_FILE_NAME = "META-INF/kie/dmn";

    @Parameter(required = true, defaultValue = "${project.build.directory}")
    private File targetDirectory;

    @Parameter(required = true, defaultValue = "${project.basedir}")
    private File projectDir;

    @Parameter
    private Map<String, String> properties;

    @Parameter(required = true, defaultValue = "${project}")
    private MavenProject project;

    @Parameter(property = "generateDMNModel", defaultValue = "no")
    private String generateDMNModel;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        boolean DMNmodelParameterEnabled = DMNModelModeHelper.dmnModelParameterEnabled(generateDMNModel);
        boolean modelCompilerInClassPath = isModelCompilerInClassPath(project.getDependencies());

        if (DMNmodelParameterEnabled && modelCompilerInClassPath) {
            generateDMNModel();
        }
    }

    private void generateDMNModel() throws MojoExecutionException {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        JavaCompilerSettings javaCompilerSettings = createJavaCompilerSettings();

        KieServices ks = KieServices.Factory.get();

        try {
            setSystemProperties(properties);

            final KieBuilderImpl kieBuilder = (KieBuilderImpl) ks.newKieBuilder(projectDir);

            DMNCompilerConfigurationImpl dmnCompilerConfiguration =
                    (DMNCompilerConfigurationImpl) DMNFactory.newCompilerConfiguration();

            Map<String, String> classNameSourceMap = new HashMap<>();

            dmnCompilerConfiguration.setDeferredCompilation(true);
            dmnCompilerConfiguration.addListener(generatedSource -> {
                for (GeneratedSource generatedFile : generatedSource) {
                    final Path fileNameRelative = transformPathToMavenPath(generatedFile);
                    getLog().info("Generating new DMN file: " + generatedFile);
                    classNameSourceMap.put(getCompiledClassName(fileNameRelative), generatedFile.getSourceContent());
                }
            });

            InternalKieModule kieModule = (InternalKieModule) kieBuilder.getKieModuleIgnoringErrors();
            List<String> dmnFiles = getDMNFIles(kieModule);
            getLog().info("dmnFiles to process: " + dmnFiles);

            DMNAssemblerService assemblerService = new DMNAssemblerService(dmnCompilerConfiguration);
            KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

            for (String dmnFile : dmnFiles) {
                compileDMNFile(kieModule, assemblerService, knowledgeBuilder, dmnFile);
            }

            createDMNFile(classNameSourceMap.keySet());

            compileAndWriteClasses(targetDirectory, contextClassLoader,
                                   javaCompilerSettings, getCompilerType(), classNameSourceMap, dumpKieSourcesFolder);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            Thread.currentThread().setContextClassLoader(contextClassLoader);
        }

        getLog().info("DMN Model successfully generated");
    }

    private void createDMNFile(Collection<String> compiledClassNames) {
        final Path dmnCompiledClassFile = Paths.get(targetDirectory.getPath(), "classes", RULE_CLASS_FILE_NAME);

        try {
            if (!Files.exists(dmnCompiledClassFile)) {
                Files.createDirectories(dmnCompiledClassFile.getParent());
            }
            Files.write(dmnCompiledClassFile, compiledClassNames);
        } catch (IOException e) {
            throw new RuntimeException("Unable to write file", e);
        }
    }

    private List<String> getDMNFIles(InternalKieModule kieModule) {
        return getFilesByType(kieModule, "dmn");
    }

    private void compileDMNFile(InternalKieModule kieModule, DMNAssemblerService assemblerService,
                                KnowledgeBuilder knowledgeBuilder, String dmnFile) throws Exception {
        Resource resource = kieModule.getResource(dmnFile);
        ResourceConfiguration resourceConfiguration = kieModule.getResourceConfiguration(dmnFile);

        ResourceWithConfiguration resourceWithConfiguration =
                new ResourceWithConfigurationImpl(resource, resourceConfiguration, a -> {
                }, b -> {
                });

        assemblerService.addResourcesAfterRules(knowledgeBuilder,
                                                Collections.singletonList(resourceWithConfiguration), ResourceType.DMN);
    }

    private String getCompiledClassName(Path fileNameRelative) {
        return fileNameRelative.toString()
                .replace("/", ".")
                .replace(".java", "");
    }

    private Path transformPathToMavenPath(GeneratedSource generatedFile) {
        Path fileName = Paths.get(generatedFile.getFileName());
        Path originalFilePath = Paths.get("src/main/java");
        final Path fileNameRelative;
        if (fileName.startsWith(originalFilePath)) {
            fileNameRelative = originalFilePath.relativize(fileName);
        } else {
            fileNameRelative = fileName;
        }
        return fileNameRelative;
    }
}

