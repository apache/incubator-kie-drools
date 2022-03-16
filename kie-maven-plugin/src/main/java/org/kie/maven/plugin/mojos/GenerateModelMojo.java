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
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.drools.compiler.compiler.io.Folder;
import org.drools.compiler.compiler.io.memory.MemoryFile;
import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.drools.compiler.kie.builder.impl.BuildContext;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.compiler.kie.builder.impl.KieBuilderImpl;
import org.drools.compiler.kie.builder.impl.KieModuleKieProject;
import org.drools.compiler.kie.builder.impl.MemoryKieModule;
import org.drools.modelcompiler.CanonicalKieModule;
import org.drools.modelcompiler.builder.CanonicalModelKieProject;
import org.drools.modelcompiler.builder.ModelBuilderImpl;
import org.drools.modelcompiler.builder.ModelSourceClass;
import org.drools.modelcompiler.builder.ModelWriter;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.maven.plugin.ProjectPomModel;
import org.kie.memorycompiler.JavaCompilerSettings;

import static org.kie.maven.plugin.helpers.DMNValidationHelper.performDMNDTAnalysis;
import static org.kie.maven.plugin.helpers.DMNValidationHelper.shallPerformDMNDTAnalysis;
import static org.kie.maven.plugin.helpers.ExecModelModeHelper.isModelCompilerInClassPath;
import static org.kie.maven.plugin.helpers.ExecModelModeHelper.shouldDeleteFile;
import static org.kie.maven.plugin.helpers.GenerateCodeHelper.compileAndWriteClasses;
import static org.kie.maven.plugin.helpers.GenerateCodeHelper.createJavaCompilerSettings;
import static org.kie.maven.plugin.helpers.GenerateCodeHelper.getProjectClassLoader;
import static org.kie.maven.plugin.helpers.GenerateCodeHelper.toClassName;

@Mojo(name = "generateModel",
        requiresDependencyResolution = ResolutionScope.NONE,
        requiresProject = true,
        defaultPhase = LifecyclePhase.COMPILE)
public class GenerateModelMojo extends AbstractKieMojo {

    public static PathMatcher drlFileMatcher = FileSystems.getDefault().getPathMatcher("glob:**.drl");

    @Parameter(defaultValue = "${session}", required = true, readonly = true)
    private MavenSession mavenSession;

    @Parameter(required = true, defaultValue = "${project.build.directory}")
    private File targetDirectory;

    @Parameter(required = true, defaultValue = "${project.basedir}")
    private File projectDir;

    @Parameter(required = true, defaultValue = "${project.build.testSourceDirectory}")
    private File testDir;

    @Parameter
    private Map<String, String> properties;

    @Parameter(required = true, defaultValue = "${project}")
    private MavenProject project;

    @Parameter(required = true, defaultValue = "${project.build.outputDirectory}")
    private File outputDirectory;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        // GenerateModelMojo is executed when BuildMojo isn't and vice-versa
        boolean modelParameterEnabled = isModelParameterEnabled();
        boolean modelCompilerInClassPath = isModelCompilerInClassPath(project.getDependencies());
        if (modelParameterEnabled && modelCompilerInClassPath) {
            generateModel();
        } else if (modelParameterEnabled) { // !modelCompilerInClassPath
            getLog().warn("You're trying to build rule assets in a project from an executable rule model, but you did" +
                                  " not provide the required dependency on the project classpath.\n" +
                                  "To enable executable rule models for your project, add the `drools-model-compiler`" +
                                  " dependency in the `pom.xml` file of your project.\n");
        }
    }

    private void generateModel() throws MojoExecutionException, MojoFailureException {
        JavaCompilerSettings javaCompilerSettings = createJavaCompilerSettings();
        URLClassLoader projectClassLoader = getProjectClassLoader(project, outputDirectory, javaCompilerSettings);

        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(projectClassLoader);

        try {
            setSystemProperties(properties);

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

            getLog().info(String.format("Found %d generated files in Canonical Model", generatedFiles.size()));

            MemoryFileSystem mfs = kieModule instanceof CanonicalKieModule ?
                    ((MemoryKieModule) ((CanonicalKieModule) kieModule).getInternalKieModule()).getMemoryFileSystem() :
                    ((MemoryKieModule) kieModule).getMemoryFileSystem();

            Map<String, String> classNameSourceMap = new HashMap<>();

            for (String generatedFile : generatedFiles) {
                MemoryFile f = (MemoryFile) mfs.getFile(generatedFile);
                String className = toClassName(generatedFile);
                classNameSourceMap.put(className, new String(mfs.getFileContents(f)));
                getLog().info("Generating " + className);
            }

            compileAndWriteClasses(targetDirectory, projectClassLoader, javaCompilerSettings, getCompilerType(),
                                   classNameSourceMap, dumpKieSourcesFolder);

            // copy the META-INF packages file
            final String path = CanonicalKieModule.getModelFileWithGAV(kieModule.getReleaseId());
            final MemoryFile packagesMemoryFile = (MemoryFile) mfs.getFile(path);
            final String packagesMemoryFilePath = packagesMemoryFile.getFolder().getPath().asString();
            final Path packagesDestinationPath = Paths.get(targetDirectory.getPath(), "classes",
                                                           packagesMemoryFilePath, packagesMemoryFile.getName());

            try {
                if (!Files.exists(packagesDestinationPath)) {
                    Files.createDirectories(packagesDestinationPath.getParent());
                }
                Files.copy(packagesMemoryFile.getContents(), packagesDestinationPath,
                           StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
                throw new MojoExecutionException("Unable to write file", e);
            }

            if (shallPerformDMNDTAnalysis(getValidateDMN(), getLog())) {
                performDMNDTAnalysis(kieModule, resources, getLog());
            }

            if (shouldDeleteFile(getGenerateModelOption())) {
                Set<String> drlFiles = kieModule.getFileNames()
                        .stream()
                        .filter(f -> f.endsWith("drl"))
                        .collect(Collectors.toSet());
                deleteDrlFiles(drlFiles);
            }
        } finally {
            Thread.currentThread().setContextClassLoader(contextClassLoader);
            if (projectClassLoader != null) {
                try {
                    projectClassLoader.close();
                } catch (IOException e) {
                    getLog().warn(e);
                }
            }
        }

        getLog().info("DSL successfully generated");
    }

    private void deleteDrlFiles(Set<String> actualDrlFiles) throws MojoExecutionException {
        // Remove drl files
        try (final Stream<Path> drlFilesToDeleted = Files.find(outputDirectory.toPath(), Integer.MAX_VALUE,
                                                               (p, f) -> drlFileMatcher.matches(p))) {
            Set<String> deletedFiles = new HashSet<>();
            drlFilesToDeleted.forEach(p -> {
                try {
                    Files.delete(p);
                    deletedFiles.add(p.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException("Unable to delete file " + p);
                }
            });
            actualDrlFiles.retainAll(deletedFiles);
            if (!actualDrlFiles.isEmpty()) {
                String actualDrlFiles1 = String.join(",", actualDrlFiles);
                getLog().warn("Base directory: " + projectDir);
                getLog().warn("Files not deleted: " + actualDrlFiles1);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new MojoExecutionException("Unable to find .drl files");
        }
    }

    public static class ExecutableModelMavenProject implements KieBuilder.ProjectType {

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
                ModelWriter modelWriter = new ModelWriter();

                Map<String, List<String>> modelsByKBase = new HashMap<>();
                for (Map.Entry<String, ModelBuilderImpl> modelBuilder : modelBuilders.entrySet()) {
                    ModelWriter.Result result = modelWriter.writeModel(srcMfs,
                                                                       modelBuilder.getValue().getPackageSources());
                    modelFiles.addAll(result.getModelFiles());
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
            }
        }
    }
}
