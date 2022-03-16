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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.drools.ancompiler.CompiledNetworkSources;
import org.drools.ancompiler.ObjectTypeNodeCompiler;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.modelcompiler.CanonicalKieModule;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.memorycompiler.JavaCompilerSettings;
import org.kie.util.maven.support.ReleaseIdImpl;

import static org.kie.maven.plugin.helpers.ExecModelModeHelper.ancEnabled;
import static org.kie.maven.plugin.helpers.ExecModelModeHelper.isModelCompilerInClassPath;
import static org.kie.maven.plugin.helpers.GenerateCodeHelper.compileAndWriteClasses;
import static org.kie.maven.plugin.helpers.GenerateCodeHelper.createJavaCompilerSettings;
import static org.kie.maven.plugin.helpers.GenerateCodeHelper.getProjectClassLoader;
import static org.kie.maven.plugin.helpers.GenerateCodeHelper.toClassName;

@Mojo(name = "generateANC",
        requiresDependencyResolution = ResolutionScope.NONE,
        defaultPhase = LifecyclePhase.COMPILE)
public class GenerateANCMojo extends AbstractKieMojo {

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
    public void execute() throws MojoExecutionException {
        // GenerateModelMojo is executed when BuildMojo isn't and vice-versa
        boolean ancParameterEnabled = ancEnabled(getGenerateModelOption());
        boolean modelCompilerInClassPath = isModelCompilerInClassPath(project.getDependencies());
        if (ancParameterEnabled && modelCompilerInClassPath) {
            generateANC();
        } else if (ancParameterEnabled) { // !modelCompilerInClassPath
            getLog().warn("You're trying to build rule assets in a project from an executable rule model, but you did not provide the required dependency on the project classpath.\n" +
                                  "To enable executable rule models for your project, add the `drools-model-compiler` dependency in the `pom.xml` file of your project.\n");
        }
    }

    private void generateANC() throws MojoExecutionException {
        JavaCompilerSettings javaCompilerSettings = createJavaCompilerSettings();
        URLClassLoader projectClassLoader = getProjectClassLoader(project, outputDirectory, javaCompilerSettings);

        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(projectClassLoader);

        try {
            setSystemProperties(properties);

            KieServices ks = KieServices.Factory.get();

            KieContainer kieContainer = ks.newKieContainer(new ReleaseIdImpl(project.getGroupId(),
                                                                             project.getArtifactId(),
                                                                             project.getVersion()));

            Map<String, String> classNameSourceMap = new HashMap<>();

            for (String kbase : kieContainer.getKieBaseNames()) {
                InternalKnowledgeBase kieBase = (InternalKnowledgeBase) kieContainer.getKieBase(kbase);

                List<CompiledNetworkSources> ancSourceFiles = ObjectTypeNodeCompiler.compiledNetworkSources(kieBase.getRete());

                getLog().info(String.format("Found %d generated files in Knowledge Base %s", ancSourceFiles.size(), kbase));

                for (CompiledNetworkSources generatedFile : ancSourceFiles) {
                    String className = toClassName(generatedFile.getSourceName());
                    classNameSourceMap.put(className, generatedFile.getSource());
                    getLog().info("Generated Alpha Network class: " + className);
                }
            }

            compileAndWriteClasses(targetDirectory, projectClassLoader, javaCompilerSettings, getCompilerType(), classNameSourceMap, dumpKieSourcesFolder);

            // generate the ANC file
            String ancFile = CanonicalKieModule.getANCFile(new ReleaseIdImpl(
                    project.getGroupId(),
                    project.getArtifactId(),
                    project.getVersion()
            ));
            final Path ancFilePath = Paths.get(targetDirectory.getPath(),
                                               "classes",
                                               ancFile);

            try {
                Files.deleteIfExists(ancFilePath);
                Files.createDirectories(ancFilePath.getParent());
                Files.createFile(ancFilePath);
                getLog().info("Written ANC File: " + ancFilePath.toAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
                throw new MojoExecutionException("Unable to write file: ", e);
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

        getLog().info("Compiled Alpha Network successfully generated");
    }
}
