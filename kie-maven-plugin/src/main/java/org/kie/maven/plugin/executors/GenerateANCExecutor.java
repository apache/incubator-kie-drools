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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.drools.ancompiler.CompiledNetworkSources;
import org.drools.ancompiler.ObjectTypeNodeCompiler;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.modelcompiler.CanonicalKieModule;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.maven.plugin.KieMavenPluginContext;
import org.kie.memorycompiler.JavaCompilerSettings;
import org.kie.memorycompiler.JavaConfiguration;
import org.kie.util.maven.support.ReleaseIdImpl;

import static org.kie.maven.plugin.helpers.ExecutorHelper.setSystemProperties;
import static org.kie.maven.plugin.helpers.GenerateCodeHelper.compileAndWriteClasses;
import static org.kie.maven.plugin.helpers.GenerateCodeHelper.createJavaCompilerSettings;
import static org.kie.maven.plugin.helpers.GenerateCodeHelper.getProjectClassLoader;
import static org.kie.maven.plugin.helpers.GenerateCodeHelper.toClassName;

public class GenerateANCExecutor {

    private GenerateANCExecutor() {
    }

    public static void generateANC(final KieMavenPluginContext kieMavenPluginContext) throws MojoExecutionException {
        final MavenProject project = kieMavenPluginContext.getProject();
        final File outputDirectory = kieMavenPluginContext.getOutputDirectory();
        final Map<String, String> properties = kieMavenPluginContext.getProperties();
        final File targetDirectory = kieMavenPluginContext.getTargetDirectory();
        final String dumpKieSourcesFolder = kieMavenPluginContext.getDumpKieSourcesFolder();
        final JavaConfiguration.CompilerType compilerType = kieMavenPluginContext.getCompilerType();
        final Log log = kieMavenPluginContext.getLog();

        JavaCompilerSettings javaCompilerSettings = createJavaCompilerSettings(project);
        URLClassLoader projectClassLoader = getProjectClassLoader(project, outputDirectory, javaCompilerSettings);

        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(projectClassLoader);

        try {
            setSystemProperties(properties, log);

            KieServices ks = KieServices.Factory.get();

            KieContainer kieContainer = ks.newKieContainer(new ReleaseIdImpl(project.getGroupId(),
                                                                             project.getArtifactId(),
                                                                             project.getVersion()));

            Map<String, String> classNameSourceMap = new HashMap<>();

            for (String kbase : kieContainer.getKieBaseNames()) {
                InternalKnowledgeBase kieBase = (InternalKnowledgeBase) kieContainer.getKieBase(kbase);

                List<CompiledNetworkSources> ancSourceFiles =
                        ObjectTypeNodeCompiler.compiledNetworkSources(kieBase.getRete());

                log.info(String.format("Found %d generated files in Knowledge Base %s", ancSourceFiles.size(),
                                       kbase));

                for (CompiledNetworkSources generatedFile : ancSourceFiles) {
                    String className = toClassName(generatedFile.getSourceName());
                    classNameSourceMap.put(className, generatedFile.getSource());
                    log.info("Generated Alpha Network class: " + className);
                }
            }

            compileAndWriteClasses(targetDirectory, projectClassLoader, javaCompilerSettings, compilerType,
                                   classNameSourceMap, dumpKieSourcesFolder);

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
                log.info("Written ANC File: " + ancFilePath.toAbsolutePath());
            } catch (IOException e) {
                throw new MojoExecutionException("Unable to write file: ", e);
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

        log.info("Compiled Alpha Network successfully generated");
    }
}
