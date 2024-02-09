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
package org.kie.maven.plugin.mojos;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.kie.memorycompiler.JavaConfiguration;

import static org.kie.maven.plugin.helpers.ExecModelModeHelper.execModelParameterEnabled;

public abstract class AbstractKieMojo extends AbstractMojo {

    @Parameter(property = "dumpKieSourcesFolder", defaultValue = "")
    private String dumpKieSourcesFolder;

    @Parameter(property = "generateModel", defaultValue = "YES_WITHDRL")
    // DROOLS-5663 align kie-maven-plugin default value for generateModel configuration flag
    private String generateModel;

    @Parameter(property = "generateDMNModel", defaultValue = "no")
    private String generateDMNModel;

    @Parameter(required = true, defaultValue = "${project.build.resources}")
    protected List<Resource> resources;

    @Parameter(property = "validateDMN", defaultValue = "VALIDATE_SCHEMA,VALIDATE_MODEL,ANALYZE_DECISION_TABLE")
    protected String validateDMN;

    @Parameter(required = true, defaultValue = "${project.basedir}")
    private File projectDir;

    @Parameter(required = true, defaultValue = "${project.build.directory}")
    private File targetDirectory;

    @Parameter
    private Map<String, String> properties;

    @Parameter(required = true, defaultValue = "${project}")
    private MavenProject project;

    @Parameter(defaultValue = "${session}", required = true, readonly = true)
    private MavenSession mavenSession;

    @Parameter(defaultValue = "${project.resources}", required = true)
    private List<org.apache.maven.model.Resource> resourcesDirectories;

    /**
     * Directory containing the generated JAR.
     */
    @Parameter(required = true, defaultValue = "${project.build.outputDirectory}")
    private File outputDirectory;

    @Parameter(required = true, defaultValue = "${project.build.testSourceDirectory}")
    private File testDir;

    /**
     * Project resources folder.
     */
    @Parameter(required = true, defaultValue = "src/main/resources")
    private File resourceFolder;

    @Parameter(property = "javaCompiler", defaultValue = "ecj")
    private String javaCompiler;

    public String getDumpKieSourcesFolder() {
        return dumpKieSourcesFolder;
    }

    public String getGenerateModel() {
        return generateModel;
    }

    public String getGenerateDMNModel() {
        return generateDMNModel;
    }

    public List<Resource> getResources() {
        return resources;
    }

    public String getValidateDMN() {
        return validateDMN;
    }

    public File getProjectDir() {
        return projectDir;
    }

    public File getTargetDirectory() {
        return targetDirectory;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public MavenProject getProject() {
        return project;
    }

    public MavenSession getMavenSession() {
        return mavenSession;
    }

    public List<Resource> getResourcesDirectories() {
        return resourcesDirectories;
    }

    public File getOutputDirectory() {
        return outputDirectory;
    }

    public File getTestDir() {
        return testDir;
    }

    public File getResourceFolder() {
        return resourceFolder;
    }

    public String getJavaCompiler() {
        return javaCompiler;
    }

    public boolean isModelParameterEnabled() {
        return execModelParameterEnabled(generateModel);
    }

    public JavaConfiguration.CompilerType getCompilerType() {
        return javaCompiler.equalsIgnoreCase("native") ? JavaConfiguration.CompilerType.NATIVE :
                JavaConfiguration.CompilerType.ECLIPSE;
    }
}
