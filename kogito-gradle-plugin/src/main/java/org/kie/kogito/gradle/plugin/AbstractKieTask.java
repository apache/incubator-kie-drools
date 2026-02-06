/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
package org.kie.kogito.gradle.plugin;

import javax.inject.Inject;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.compile.CompileOptions;
import org.kie.kogito.codegen.manager.BuilderManager;
import org.kie.kogito.codegen.manager.util.CodeGenManagerUtil;
import org.kie.kogito.gradle.plugin.util.TaskUtil;

public abstract class AbstractKieTask extends DefaultTask {

    @Input
    private Property<String> jsonSchemaVersion;

    @OutputDirectory
    protected Property<File> generatedSourcesDir;

    @Internal
    protected Property<File> projectBaseDir;

    @Internal
    protected String gradleCompilerJavaVersion;


    @Internal
    protected Property<File> projectBuildOutputDirectory;

    @Internal
    private Project gradleProject;

    @Internal
    protected Map<String, String> properties;

    @Inject
    protected AbstractKieTask(KogitoGradleExtension kogitoGradleExtension) {
        projectBaseDir = kogitoGradleExtension.getProjectBaseDir();
        generatedSourcesDir = kogitoGradleExtension.getGeneratedSourcesDir();
        projectBuildOutputDirectory = kogitoGradleExtension.getProjectBuildOutputDirectory();
        jsonSchemaVersion = kogitoGradleExtension.getJsonSchemaVersion();
        gradleProject = kogitoGradleExtension.getGradleProject();
    }

    protected void buildProject() {
        getLogger().lifecycle("buildProject");
        executionLog();
        try {
            Set<URI> projectFilesUris = TaskUtil.getProjectFiles(gradleProject, null);
            List<String> classpathElements = TaskUtil.getRuntimeClasspathElements(gradleProject);
            classpathElements.forEach(classpathElement -> getLogger().lifecycle("classpath element: " + classpathElement));
            String jsonSchemaString = jsonSchemaVersion.isPresent() ? jsonSchemaVersion.get() : null;
            String encoding = ((CompileOptions) Objects.requireNonNull(gradleProject.getTasks().findByName("compileJava")).property("options")).getEncoding();
            String projectSourceEncoding = encoding != null ? encoding : "UTF-8";
            BuilderManager.BuildInfo buildInfo = new BuilderManager.BuildInfo(projectFilesUris,
                    projectBaseDir.get().toPath(),
                    projectBuildOutputDirectory.get().toPath(),
                    gradleProject.getGroup().toString(),
                    gradleProject.getName(),
                    gradleProject.getVersion().toString(),
                    projectSourceEncoding,
                    gradleCompilerJavaVersion,
                    jsonSchemaString,
                    false,
                    false,
                    false,
                    classpathElements,
                    discoverFramework(),
                    properties);
            BuilderManager.build(buildInfo);
        } catch (IOException e) {
            throw new GradleException("Error building project", e);
        }
    }

    public Property<File> getProjectBaseDir() {
        return projectBaseDir;
    }

    public Property<File> getGeneratedSourcesDir() {
        return generatedSourcesDir;
    }

    public Property<File> getProjectBuildOutputDirectory() {
        return projectBuildOutputDirectory;
    }

    public Property<String> getJsonSchemaVersion() {
        return jsonSchemaVersion;
    }

    public String getGradleCompilerJavaVersion() {
        return gradleCompilerJavaVersion;
    }

    public void setGradleCompilerJavaVersion(String gradleCompilerJavaVersion) {
        this.gradleCompilerJavaVersion = gradleCompilerJavaVersion;
    }

    protected void executionLog() {
        getLogger().lifecycle("Project base directory: " + projectBaseDir.get());
        getLogger().lifecycle("Build output directory: " + projectBuildOutputDirectory.get());
        getLogger().lifecycle("Generated sources directory: " + generatedSourcesDir.get());
        String jsonSchemaString = jsonSchemaVersion.isPresent() ? jsonSchemaVersion.get() : null;
        getLogger().lifecycle("Json schema version: " + jsonSchemaString);
        getLogger().lifecycle("===================================");
    }

    CodeGenManagerUtil.Framework discoverFramework() {
        if (TaskUtil.hasDependency(gradleProject, CodeGenManagerUtil.Framework.QUARKUS)) {
            return CodeGenManagerUtil.Framework.QUARKUS;
        }

        if (TaskUtil.hasDependency(gradleProject, CodeGenManagerUtil.Framework.SPRING)) {
            return CodeGenManagerUtil.Framework.SPRING;
        }

        return CodeGenManagerUtil.Framework.NONE;
    }

}   