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
package org.kie.kogito.maven.plugin;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.Set;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.kie.kogito.codegen.manager.BuilderManager;
import org.kie.kogito.codegen.manager.util.CodeGenManagerUtil;
import org.kie.kogito.maven.plugin.util.MojoUtil;

public abstract class AbstractKieMojo extends AbstractMojo {

    @Parameter(property = "kogito.codegen.decisions")
    protected String generateDecisions;

    /**
     * Partial generation can be used when reprocessing a pre-compiled project
     * for faster code-generation. It only generates code for rules and processes,
     * and does not generate extra meta-classes (etc. Application).
     * Use only when doing recompilation and for development purposes
     */
    @Parameter(property = "kogito.codegen.partial", defaultValue = "false")
    protected boolean generatePartial;

    @Parameter(property = "kogito.codegen.predictions")
    protected String generatePredictions;

    @Parameter(property = "kogito.codegen.processes")
    protected String generateProcesses;

    @Parameter(property = "kogito.codegen.rules")
    protected String generateRules;

    @Parameter
    protected Map<String, String> properties;

    @Parameter(defaultValue = "17", property = "maven.compiler.release")
    protected String mavenCompilerJavaVersion;

    @Parameter(property = "kogito.codegen.persistence")
    protected boolean persistence;

    @Parameter(required = true, defaultValue = "${project.basedir}")
    protected File projectBaseDir;

    @Parameter(required = true, defaultValue = "${project.build.outputDirectory}")
    protected File projectBuildOutputDirectory;

    @Parameter(required = true, defaultValue = "${project.build.sourceEncoding}")
    protected String projectSourceEncoding;

    @Parameter(property = "kogito.jsonSchema.version", required = false) //TODO double check this required false
    protected String jsonSchemaVersion;

    @Parameter(property = "kogito.codegen.ondemand", defaultValue = "false")
    protected boolean onDemand;

    @Parameter(property = "kogito.sources.keep", defaultValue = "false")
    protected boolean keepSources;

    @Parameter(required = true, defaultValue = "${project}")
    protected MavenProject project;

    @Component
    protected MavenProject mavenProject;

    public void buildProject() throws MojoExecutionException {
        getLog().info("buildProject");
        executionLog();
        try {
            Set<URI> projectFilesUris = MojoUtil.getProjectFiles(mavenProject, null);
            BuilderManager.BuildInfo buildInfo = new BuilderManager.BuildInfo(projectFilesUris,
                    projectBaseDir.toPath(),
                    projectBuildOutputDirectory.toPath(),
                    mavenProject.getGroupId(),
                    mavenProject.getArtifactId(),
                    mavenProject.getVersion(),
                    projectSourceEncoding,
                    mavenCompilerJavaVersion,
                    jsonSchemaVersion,
                    generatePartial,
                    persistence,
                    onDemand,
                    keepSources,
                    mavenProject.getRuntimeClasspathElements(),
                    discoverFramework(),
                    properties);
            BuilderManager.build(buildInfo);
        } catch (DependencyResolutionRequiredException | IOException e) {
            throw new MojoExecutionException("Error building project", e);
        }
    }

    protected void executionLog() {
        getLog().info("Compiler Java Version: " + mavenCompilerJavaVersion);
        getLog().info("Compiler Source Encoding: " + projectSourceEncoding);
        getLog().info("Project base directory: " + projectBaseDir.getAbsolutePath());
        getLog().info("Build output directory: " + projectBuildOutputDirectory);
        getLog().info("Partial generation is enabled: " + generatePartial);
        getLog().info("Json schema version: " + jsonSchemaVersion);
        getLog().info("===================================");
    }

    CodeGenManagerUtil.Framework discoverFramework() {
        if (MojoUtil.hasDependency(mavenProject, CodeGenManagerUtil.Framework.QUARKUS)) {
            return CodeGenManagerUtil.Framework.QUARKUS;
        }

        if (MojoUtil.hasDependency(mavenProject, CodeGenManagerUtil.Framework.SPRING)) {
            return CodeGenManagerUtil.Framework.SPRING;
        }

        return CodeGenManagerUtil.Framework.NONE;
    }

}
