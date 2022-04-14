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

package org.kie.maven.plugin.executors;

import java.io.File;
import java.io.IOException;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.kie.builder.impl.KieBuilderImpl;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.drools.core.impl.RuleBase;
import org.drools.util.io.FileSystemResource;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.kiesession.rulebase.SessionsAwareKnowledgeBase;
import org.drools.modelcompiler.builder.GeneratedFile;
import org.kie.api.KieServices;
import org.kie.api.io.Resource;
import org.kie.maven.plugin.PMMLResource;
import org.kie.maven.plugin.KieMavenPluginContext;
import org.kie.maven.plugin.ProjectPomModel;
import org.kie.memorycompiler.JavaCompilerSettings;
import org.kie.memorycompiler.JavaConfiguration;
import org.kie.pmml.commons.model.HasNestedModels;
import org.kie.pmml.commons.model.HasSourcesMap;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.maven.plugin.helpers.GenerateCodeHelper.compileAndWriteClasses;
import static org.kie.maven.plugin.helpers.GenerateCodeHelper.createJavaCompilerSettings;
import static org.kie.maven.plugin.helpers.GenerateCodeHelper.getProjectClassLoader;
import static org.kie.maven.plugin.helpers.GenerateCodeHelper.toClassName;
import static org.kie.pmml.evaluator.assembler.service.PMMLCompilerService.getKiePMMLModelsFromResourceWithSources;

public class GeneratePMMLModelExecutor {

    private static final Logger logger = LoggerFactory.getLogger(GeneratePMMLModelExecutor.class);

    private static final String PMML = "pmml";

    public static void generatePMMLModel(final KieMavenPluginContext kieMavenPluginContext) throws MojoExecutionException {
        final MavenProject project = kieMavenPluginContext.getProject();
        final MavenSession mavenSession = kieMavenPluginContext.getMavenSession();
        final File outputDirectory = kieMavenPluginContext.getOutputDirectory();
        final File projectDir = kieMavenPluginContext.getProjectDir();
        final File targetDirectory = kieMavenPluginContext.getTargetDirectory();
        final String dumpKieSourcesFolder = kieMavenPluginContext.getDumpKieSourcesFolder();
        final List<org.apache.maven.model.Resource> resourcesDirectories =
                kieMavenPluginContext.getResourcesDirectories();
        final JavaConfiguration.CompilerType compilerType = kieMavenPluginContext.getCompilerType();
        final Log log = kieMavenPluginContext.getLog();

        JavaCompilerSettings javaCompilerSettings = createJavaCompilerSettings();
        URLClassLoader projectClassLoader = getProjectClassLoader(project, outputDirectory, javaCompilerSettings);

        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(projectClassLoader);

        try {
            Map<String, String> generatedFilesMap = generateFiles(mavenSession, projectDir, resourcesDirectories, log);
            compileAndWriteClasses(targetDirectory, projectClassLoader, javaCompilerSettings, compilerType,
                                   generatedFilesMap, dumpKieSourcesFolder);
        } finally {
            Thread.currentThread().setContextClassLoader(contextClassLoader);
            if (projectClassLoader != null) {
                try {
                    projectClassLoader.close();
                } catch (IOException e) {
                    log.warn(e);
                }
            }
        }

        log.info("PMML model successfully generated");
    }

    private static Map<String, String> generateFiles(final MavenSession mavenSession,
                                                     final File projectDir,
                                                     final List<org.apache.maven.model.Resource> resourcesDirectories,
                                                     final Log log) throws MojoExecutionException {
        final List<GeneratedFile> generatedFiles = getGeneratedFiles(resourcesDirectories, log);
        KieServices ks = KieServices.Factory.get();
        final KieBuilderImpl kieBuilder = (KieBuilderImpl) ks.newKieBuilder(projectDir);
        kieBuilder.setPomModel(new ProjectPomModel(mavenSession));

        Map<String, String> classNameSourceMap = new HashMap<>();
        for (GeneratedFile generatedFile : generatedFiles) {
            String className = toClassName(generatedFile.getPath());
            classNameSourceMap.put(className, new String(generatedFile.getData()));
            log.info("Generating " + className);
        }
        return classNameSourceMap;
    }

    private static List<GeneratedFile> getGeneratedFiles(final List<org.apache.maven.model.Resource> resourcesDirectories,
                                                         final Log log) throws MojoExecutionException {
        List<GeneratedFile> toReturn = new ArrayList<>();
        for (org.apache.maven.model.Resource resourceDirectory : resourcesDirectories) {
            File directoryFile = new File(resourceDirectory.getDirectory());
            log.info("Looking for PMML models in " + directoryFile.getPath());
            String errorMessageTemplate = null;
            if (!directoryFile.exists()) {
                errorMessageTemplate = "Resource path %s does not exists";
            } else if (!directoryFile.canRead()) {
                errorMessageTemplate = "Resource path %s is not readable";
            } else if (!directoryFile.isDirectory()) {
                errorMessageTemplate = "Resource path %s is not a directory";
            }
            if (errorMessageTemplate != null) {
                throw new MojoExecutionException(String.format(errorMessageTemplate, resourceDirectory));
            }
            toReturn.addAll(getGeneratedFiles(directoryFile));
        }
        if (toReturn.isEmpty()) {
            log.info("No PMML Models found.");
        } else {
            log.info(String.format("Found %s PMML models", toReturn.size()));
        }
        return toReturn;
    }

    private static List<GeneratedFile> getGeneratedFiles(File resourceDirectory) throws MojoExecutionException {
        final List<GeneratedFile> toReturn = new ArrayList<>();
        try (Stream<Path> stream = Files
                .walk(resourceDirectory.toPath(), Integer.MAX_VALUE)
                .filter(path -> path.toFile().isFile() && path.toString().endsWith(PMML))) {
            return stream
                    .map(Path::toFile)
                    .map(FileSystemResource::new)
                    .map(GeneratePMMLModelExecutor::parseResource)
                    .map(GeneratePMMLModelExecutor::getGenerateFiles)
                    .reduce(toReturn, (previous, toAdd) -> {
                        previous.addAll(toAdd);
                        return previous;
                    });
        } catch (Exception e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }

    private static List<GeneratedFile> getGenerateFiles(final PMMLResource pmmlResources) {
        final List<GeneratedFile> toReturn = new ArrayList<>();
        List<KiePMMLModel> kiepmmlModels = pmmlResources.getKiePmmlModels();
        addModels(kiepmmlModels, pmmlResources, toReturn);
        return toReturn;
    }

    private static void addModels(final List<KiePMMLModel> kiepmmlModels,
                           final PMMLResource resource,
                           final List<GeneratedFile> generatedFiles) {
        for (KiePMMLModel model : kiepmmlModels) {
            if (model.getName() == null || model.getName().isEmpty()) {
                String errorMessage = String.format("Model name should not be empty inside %s",
                                                    resource.getModelPath());
                throw new RuntimeException(errorMessage);
            }
            if (!(model instanceof HasSourcesMap)) {
                String errorMessage = String.format("Expecting HasSourcesMap instance, retrieved %s inside %s",
                                                    model.getClass().getName(),
                                                    resource.getModelPath());
                throw new RuntimeException(errorMessage);
            }
            Map<String, String> sourceMap = ((HasSourcesMap) model).getSourcesMap();
            for (Map.Entry<String, String> sourceMapEntry : sourceMap.entrySet()) {
                String path = sourceMapEntry.getKey().replace('.', File.separatorChar) + ".java";
                generatedFiles.add(new GeneratedFile(GeneratedFile.Type.PMML, path, sourceMapEntry.getValue()));
            }
            Map<String, String> rulesSourceMap = ((HasSourcesMap) model).getRulesSourcesMap();
            if (rulesSourceMap != null) {
                for (Map.Entry<String, String> rulesSourceMapEntry : rulesSourceMap.entrySet()) {
                    String path = rulesSourceMapEntry.getKey().replace('.', File.separatorChar) + ".java";
                    generatedFiles.add(new GeneratedFile(GeneratedFile.Type.RULE, path,
                                                         rulesSourceMapEntry.getValue()));
                }
            }
            if (model instanceof HasNestedModels) {
                addModels(((HasNestedModels) model).getNestedModels(), resource, generatedFiles);
            }
        }
    }

    private static PMMLResource parseResource(Resource resource) {
        final RuleBase ruleBase = new KnowledgeBaseImpl("PMML", null);
        final InternalKnowledgeBase knowledgeBase = new SessionsAwareKnowledgeBase(ruleBase);
        KnowledgeBuilderImpl kbuilderImpl = new KnowledgeBuilderImpl(knowledgeBase);
        List<KiePMMLModel> kiePMMLModels = getKiePMMLModelsFromResourceWithSources(kbuilderImpl, resource);
        String modelPath = resource.getSourcePath();
        return new PMMLResource(kiePMMLModels, new File(resource.getSourcePath()).toPath(), modelPath);
    }
}
