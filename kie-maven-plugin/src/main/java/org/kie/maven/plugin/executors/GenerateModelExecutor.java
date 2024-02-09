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
import java.net.URLClassLoader;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.drools.compiler.compiler.io.Folder;
import org.drools.compiler.compiler.io.memory.MemoryFile;
import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.drools.compiler.kie.builder.impl.BuildContext;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.compiler.kie.builder.impl.KieBuilderImpl;
import org.drools.compiler.kie.builder.impl.KieModuleKieProject;
import org.drools.compiler.kie.builder.impl.MemoryKieModule;
import org.drools.model.codegen.execmodel.CanonicalModelKieProject;
import org.drools.model.codegen.execmodel.ModelBuilderImpl;
import org.drools.model.codegen.execmodel.ModelSourceClass;
import org.drools.model.codegen.execmodel.ModelWriter;
import org.drools.modelcompiler.CanonicalKieModule;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.maven.plugin.KieMavenPluginContext;
import org.kie.maven.plugin.ProjectPomModel;
import org.kie.memorycompiler.JavaCompilerSettings;
import org.kie.memorycompiler.JavaConfiguration;

import static org.kie.maven.plugin.helpers.DMNValidationHelper.performDMNDTAnalysis;
import static org.kie.maven.plugin.helpers.DMNValidationHelper.shallPerformDMNDTAnalysis;
import static org.kie.maven.plugin.helpers.ExecModelModeHelper.shouldDeleteFile;
import static org.kie.maven.plugin.helpers.ExecutorHelper.setSystemProperties;
import static org.kie.maven.plugin.helpers.GenerateCodeHelper.compileAndWriteClasses;
import static org.kie.maven.plugin.helpers.GenerateCodeHelper.createJavaCompilerSettings;
import static org.kie.maven.plugin.helpers.GenerateCodeHelper.getProjectClassLoader;
import static org.kie.maven.plugin.helpers.GenerateCodeHelper.toClassName;

public class GenerateModelExecutor {

    private GenerateModelExecutor() {
    }

    public static PathMatcher drlFileMatcher = FileSystems.getDefault().getPathMatcher("glob:**.drl");

    public static void generateModel(final KieMavenPluginContext kieMavenPluginContext) throws MojoExecutionException, MojoFailureException {
        final MavenProject project = kieMavenPluginContext.getProject();
        final MavenSession mavenSession = kieMavenPluginContext.getMavenSession();
        final File outputDirectory = kieMavenPluginContext.getOutputDirectory();
        final File projectDir = kieMavenPluginContext.getProjectDir();
        final Map<String, String> properties = kieMavenPluginContext.getProperties();
        final File targetDirectory = kieMavenPluginContext.getTargetDirectory();
        final String dumpKieSourcesFolder = kieMavenPluginContext.getDumpKieSourcesFolder();
        final List<Resource> resources = kieMavenPluginContext.getResources();
        final JavaConfiguration.CompilerType compilerType = kieMavenPluginContext.getCompilerType();
        final String validateDMN = kieMavenPluginContext.getValidateDMN();
        final String generateModel = kieMavenPluginContext.getGenerateModel();
        final Log log = kieMavenPluginContext.getLog();

        JavaCompilerSettings javaCompilerSettings = createJavaCompilerSettings(project);
        URLClassLoader projectClassLoader = getProjectClassLoader(project, outputDirectory, javaCompilerSettings);

        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(projectClassLoader);

        try {
            setSystemProperties(properties, log);

            KieServices ks = KieServices.Factory.get();
            final KieBuilderImpl kieBuilder = (KieBuilderImpl) ks.newKieBuilder(projectDir);
            kieBuilder.setPomModel(new ProjectPomModel(mavenSession));
            kieBuilder.buildAll(ExecutableModelMavenProject.SUPPLIER,
                                s -> !s.contains("src/test/java") && !s.contains("src\\test\\java"));

            InternalKieModule kieModule = (InternalKieModule) kieBuilder.getKieModule();
            List<String> generatedFiles = kieModule.getFileNames()
                    .stream()
                    .filter(f -> f.endsWith("java"))
                    .collect(Collectors.toList());

            log.info(String.format("Found %d generated files in Canonical Model", generatedFiles.size()));

            MemoryFileSystem mfs = kieModule instanceof CanonicalKieModule ?
                    ((MemoryKieModule) ((CanonicalKieModule) kieModule).getInternalKieModule()).getMemoryFileSystem() :
                    ((MemoryKieModule) kieModule).getMemoryFileSystem();

            Map<String, String> classNameSourceMap = new HashMap<>();

            for (String generatedFile : generatedFiles) {
                MemoryFile f = (MemoryFile) mfs.getFile(generatedFile);
                String className = toClassName(generatedFile);
                classNameSourceMap.put(className, new String(mfs.getFileContents(f)));
                log.info("Generating " + className);
            }

            compileAndWriteClasses(targetDirectory, projectClassLoader, javaCompilerSettings, compilerType,
                                   classNameSourceMap, dumpKieSourcesFolder);

            // copy the META-INF packages files
            copyMetaInfFile(targetDirectory, mfs, CanonicalKieModule.getModelFileWithGAV(kieModule.getReleaseId()));
            copyMetaInfFile(targetDirectory, mfs, CanonicalKieModule.RULE_UNIT_SERVICES_FILE);

            if (shallPerformDMNDTAnalysis(validateDMN, log)) {
                performDMNDTAnalysis(kieModule, resources, log);
            }

            if (shouldDeleteFile(generateModel)) {
                Set<String> drlFiles = kieModule.getFileNames()
                        .stream()
                        .filter(f -> f.endsWith("drl"))
                        .collect(Collectors.toSet());
                deleteDrlFiles(outputDirectory,
                               projectDir,
                               drlFiles,
                               log);
            }
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

        log.info("DSL successfully generated");
    }

    private static void copyMetaInfFile(File targetDirectory, MemoryFileSystem mfs, String path) throws MojoExecutionException {
        final MemoryFile memoryFile = (MemoryFile) mfs.getFile(path);
        if (!memoryFile.exists()) {
            return;
        }
        final String packagesMemoryFilePath = memoryFile.getFolder().getPath().asString();
        final Path packagesDestinationPath = Paths.get(targetDirectory.getPath(), "classes",
                                                       packagesMemoryFilePath, memoryFile.getName());

        try {
            if (!Files.exists(packagesDestinationPath)) {
                Files.createDirectories(packagesDestinationPath.getParent());
            }
            Files.copy(memoryFile.getContents(), packagesDestinationPath,
                       StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new MojoExecutionException("Unable to write file", e);
        }
    }

    private static void deleteDrlFiles(final File outputDirectory,
                                       final File projectDir,
                                       final Set<String> actualDrlFiles,
                                       final Log log) throws MojoExecutionException {
        // Remove drl files
        try (final Stream<Path> drlFilesToDeleted = Files.find(outputDirectory.toPath(), Integer.MAX_VALUE,
                                                               (p, f) -> drlFileMatcher.matches(p))) {
            Set<String> deletedFiles = new HashSet<>();
            drlFilesToDeleted.forEach(p -> {
                try {
                    Files.delete(p);
                    deletedFiles.add(p.toString());
                } catch (IOException e) {
                    throw new RuntimeException("Unable to delete file " + p);
                }
            });
            actualDrlFiles.retainAll(deletedFiles);
            if (!actualDrlFiles.isEmpty()) {
                String actualDrlFiles1 = String.join(",", actualDrlFiles);
                log.warn("Base directory: " + projectDir);
                log.warn("Files not deleted: " + actualDrlFiles1);
            }
        } catch (IOException e) {
            throw new MojoExecutionException("Unable to find .drl files");
        }
    }

    public static class ExecutableModelMavenProject implements KieBuilder.ProjectType {

        private ExecutableModelMavenProject() {
        }

        public static final BiFunction<InternalKieModule, ClassLoader, KieModuleKieProject> SUPPLIER =
                ExecutableModelMavenPluginKieProject::new;

        public static class ExecutableModelMavenPluginKieProject extends CanonicalModelKieProject {

            public ExecutableModelMavenPluginKieProject(InternalKieModule kieModule, ClassLoader classLoader) {
                super(kieModule, classLoader);
            }

            @Override
            public void writeProjectOutput(MemoryFileSystem trgMfs, BuildContext buildContext) {
                MemoryFileSystem srcMfs = new MemoryFileSystem();
                Folder sourceFolder = srcMfs.getFolder("src/main/java");

                List<String> modelFiles = new ArrayList<>();
                List<String> ruleUnitClassNames = new ArrayList<>();
                ModelWriter modelWriter = new ModelWriter();

                Map<String, List<String>> modelsByKBase = new HashMap<>();
                for (Map.Entry<String, ModelBuilderImpl> modelBuilder : modelBuilders.entrySet()) {
                    ModelWriter.Result result = modelWriter.writeModel(srcMfs,
                                                                       modelBuilder.getValue().getPackageSources());
                    modelFiles.addAll(result.getModelFiles());
                    ruleUnitClassNames.addAll( result.getRuleUnitClassNames() );
                    modelsByKBase.put(modelBuilder.getKey(), result.getModelFiles());
                }

                InternalKieModule kieModule = getInternalKieModule();
                ModelSourceClass modelSourceClass = new ModelSourceClass(kieModule.getReleaseId(),
                                                                         kieModule.getKieModuleModel().getKieBaseModels(), modelsByKBase, hasDynamicClassLoader());
                String projectSourcePath = modelSourceClass.getName();
                srcMfs.write(projectSourcePath, modelSourceClass.generate().getBytes());

                Folder targetFolder = trgMfs.getFolder(".");
                srcMfs.copyFolder(sourceFolder, trgMfs, targetFolder);
                modelWriter.writeModelFile(modelFiles, trgMfs, getInternalKieModule().getReleaseId());
                modelWriter.writeRuleUnitServiceFile(ruleUnitClassNames, trgMfs);
            }
        }
    }
}
