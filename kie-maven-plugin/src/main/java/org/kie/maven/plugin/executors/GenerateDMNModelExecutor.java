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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
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
import org.kie.maven.plugin.KieMavenPluginContext;
import org.kie.memorycompiler.JavaCompilerSettings;
import org.kie.memorycompiler.JavaConfiguration;

import static org.kie.maven.plugin.helpers.ExecutorHelper.getFilesByType;
import static org.kie.maven.plugin.helpers.ExecutorHelper.setSystemProperties;
import static org.kie.maven.plugin.helpers.GenerateCodeHelper.compileAndWriteClasses;
import static org.kie.maven.plugin.helpers.GenerateCodeHelper.createJavaCompilerSettings;

public class GenerateDMNModelExecutor {

    public static final String RULE_CLASS_FILE_NAME = "META-INF/kie/dmn";

    private GenerateDMNModelExecutor() {
    }

    public static void generateDMN(final KieMavenPluginContext kieMavenPluginContext) throws MojoExecutionException {
        final MavenProject project = kieMavenPluginContext.getProject();
        final File projectDir = kieMavenPluginContext.getProjectDir();
        final Map<String, String> properties = kieMavenPluginContext.getProperties();
        final File targetDirectory = kieMavenPluginContext.getTargetDirectory();
        final String dumpKieSourcesFolder = kieMavenPluginContext.getDumpKieSourcesFolder();
        final JavaConfiguration.CompilerType compilerType = kieMavenPluginContext.getCompilerType();
        final Log log = kieMavenPluginContext.getLog();

        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        JavaCompilerSettings javaCompilerSettings = createJavaCompilerSettings(project);

        KieServices ks = KieServices.Factory.get();

        try {
            setSystemProperties(properties, log);

            final KieBuilderImpl kieBuilder = (KieBuilderImpl) ks.newKieBuilder(projectDir);

            DMNCompilerConfigurationImpl dmnCompilerConfiguration =
                    (DMNCompilerConfigurationImpl) DMNFactory.newCompilerConfiguration();

            Map<String, String> classNameSourceMap = new HashMap<>();

            dmnCompilerConfiguration.setDeferredCompilation(true);
            dmnCompilerConfiguration.addListener(generatedSource -> {
                for (GeneratedSource generatedFile : generatedSource) {
                    final Path fileNameRelative = transformPathToMavenPath(generatedFile);
                    log.info("Generating new DMN file: " + generatedFile);
                    classNameSourceMap.put(getCompiledClassName(fileNameRelative), generatedFile.getSourceContent());
                }
            });

            InternalKieModule kieModule = (InternalKieModule) kieBuilder.getKieModuleIgnoringErrors();
            List<String> dmnFiles = getDMNFIles(kieModule);
            log.info("dmnFiles to process: " + dmnFiles);

            DMNAssemblerService assemblerService = new DMNAssemblerService(dmnCompilerConfiguration);
            KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

            for (String dmnFile : dmnFiles) {
                compileDMNFile(kieModule, assemblerService, knowledgeBuilder, dmnFile);
            }

            createDMNFile(targetDirectory, classNameSourceMap.keySet());

            compileAndWriteClasses(targetDirectory, contextClassLoader,
                                   javaCompilerSettings,compilerType, classNameSourceMap, dumpKieSourcesFolder);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            Thread.currentThread().setContextClassLoader(contextClassLoader);
        }

        log.info("DMN Model successfully generated");
    }

    private static void createDMNFile( File targetDirectory, Collection<String> compiledClassNames) {
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

    private static List<String> getDMNFIles(InternalKieModule kieModule) {
        return getFilesByType(kieModule, "dmn");
    }

    private static void compileDMNFile(InternalKieModule kieModule, DMNAssemblerService assemblerService,
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

    private static String getCompiledClassName(Path fileNameRelative) {
        return fileNameRelative.toString()
                .replace("/", ".")
                .replace(".java", "");
    }

    private static Path transformPathToMavenPath(GeneratedSource generatedFile) {
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

