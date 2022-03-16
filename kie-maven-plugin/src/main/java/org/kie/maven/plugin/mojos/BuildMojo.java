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
import java.util.Map;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import static org.kie.maven.plugin.executors.BuildDrlExecutor.buildDrl;
import static org.kie.maven.plugin.helpers.ExecModelModeHelper.isModelCompilerInClassPath;

/**
 * This goal builds the Drools files belonging to the kproject.
 */
@Mojo(name = "build",
        requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME,
        requiresProject = true,
        defaultPhase = LifecyclePhase.COMPILE)
public class BuildMojo extends AbstractKieMojo {

    @Parameter(defaultValue = "${session}", required = true, readonly = true)
    private MavenSession mavenSession;

    /**
     * Directory containing the generated JAR.
     */
    @Parameter(required = true, defaultValue = "${project.build.outputDirectory}")
    private File outputDirectory;

    /**
     * Project resources folder.
     */
    @Parameter(required = true, defaultValue = "src/main/resources")
    private File resourceFolder;

    @Parameter(required = true, defaultValue = "${project}")
    private MavenProject project;

    @Parameter
    private Map<String, String> properties;

    public void execute() throws MojoExecutionException, MojoFailureException {
        // BuildMojo is executed when GenerateModelMojo isn't and vice-versa
        boolean modelParameterEnabled = isModelParameterEnabled();
        boolean modelCompilerInClassPath = isModelCompilerInClassPath(project.getDependencies());

        if (!(modelParameterEnabled && modelCompilerInClassPath)) {
            buildDrl(project,
            outputDirectory,
            properties,
            mavenSession,
            resourceFolder,
            resources,
            validateDMN,
            getLog());
        }
    }

}