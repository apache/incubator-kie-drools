/*
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
package org.kie.kogito.gradle.plugin;

import java.io.File;
import org.gradle.api.Project;
import org.gradle.api.provider.Property;

public class KogitoGradleExtension {

    private final Property<File> generatedSourcesDir;

    private final Property<File> projectBaseDir;

    private final Property<File> projectBuildOutputDirectory;

    private final Property<String> jsonSchemaVersion;

    private final Project gradleProject;

    public KogitoGradleExtension(Project gradleProject) {
        projectBaseDir = gradleProject.getObjects().property(File.class);
        projectBaseDir.set(gradleProject.getLayout().getProjectDirectory().getAsFile());

        projectBuildOutputDirectory = gradleProject.getObjects().property(File.class);
        projectBuildOutputDirectory.set(gradleProject.getLayout().getBuildDirectory().getAsFile());

        generatedSourcesDir = gradleProject.getObjects().property(File.class);
        generatedSourcesDir.set(projectBuildOutputDirectory.get().toPath().resolve("generated").resolve("sources").resolve("kogito").toFile());

        jsonSchemaVersion = gradleProject.getObjects().property(String.class);
        this.gradleProject = gradleProject;

    }

    public Property<File> getGeneratedSourcesDir() {
        return generatedSourcesDir;
    }

    public Property<File> getProjectBaseDir() {
        return projectBaseDir;
    }

    public Property<File> getProjectBuildOutputDirectory() {
        return projectBuildOutputDirectory;
    }

    public Property<String> getJsonSchemaVersion() {
        return jsonSchemaVersion;
    }

    public Project getGradleProject() {
        return gradleProject;
    }
}