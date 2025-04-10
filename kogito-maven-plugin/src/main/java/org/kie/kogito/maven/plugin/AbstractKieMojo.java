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
import java.nio.file.Path;
import java.util.Map;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.drools.codegen.common.GeneratedFileWriter;
import org.kie.kogito.codegen.manager.util.CodeGenManagerUtil;
import org.kie.kogito.maven.plugin.util.MojoUtil;

public abstract class AbstractKieMojo extends AbstractMojo {

    protected static final GeneratedFileWriter.Builder GENERATED_FILE_WRITER_BUILDER = GeneratedFileWriter.builder("kogito", "kogito.codegen.resources.directory", "kogito.codegen.sources.directory");

    @Parameter(required = true, defaultValue = "${project.basedir}")
    protected File projectBaseDir;

    @Parameter
    protected Map<String, String> properties;

    @Parameter(required = true, defaultValue = "${project}")
    protected MavenProject project;

    @Parameter(required = true, defaultValue = "${project.build.sourceEncoding}")
    protected String projectSourceEncoding;

    @Parameter(required = true, defaultValue = "${project.build.outputDirectory}")
    protected File outputDirectory;

    @Parameter(required = true, defaultValue = "${project.basedir}")
    protected File baseDir;

    @Parameter(property = "kogito.codegen.persistence")
    protected boolean persistence;

    @Parameter(property = "kogito.codegen.rules")
    protected String generateRules;

    @Parameter(property = "kogito.codegen.processes")
    protected String generateProcesses;

    @Parameter(property = "kogito.codegen.decisions")
    protected String generateDecisions;

    @Parameter(property = "kogito.codegen.predictions")
    protected String generatePredictions;

    protected void setSystemProperties(Map<String, String> properties) {
        if (properties != null) {
            getLog().debug("Additional system properties: " + properties);
            for (Map.Entry<String, String> property : properties.entrySet()) {
                System.setProperty(property.getKey(), property.getValue());
            }
            getLog().debug("Configured system properties were successfully set.");
        }
    }

    protected ClassLoader projectClassLoader() throws MojoExecutionException {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        return MojoUtil.createProjectClassLoader(contextClassLoader,
                project,
                outputDirectory,
                null);
    }

    protected CodeGenManagerUtil.Framework discoverFramework() {
        if (hasDependency("quarkus")) {
            return CodeGenManagerUtil.Framework.QUARKUS;
        }

        if (hasDependency("spring")) {
            return CodeGenManagerUtil.Framework.SPRING;
        }

        return CodeGenManagerUtil.Framework.NONE;
    }

    private boolean hasDependency(String dependency) {
        return project.getDependencies().stream().anyMatch(d -> d.getArtifactId().contains(dependency));
    }

    protected GeneratedFileWriter getGeneratedFileWriter() {
        return GENERATED_FILE_WRITER_BUILDER.build(Path.of(baseDir.getAbsolutePath()));
    }
}
