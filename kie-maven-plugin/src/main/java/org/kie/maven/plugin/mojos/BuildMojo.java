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

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.kie.maven.plugin.KieMavenPluginContext;
import org.kie.maven.plugin.helpers.DMNModelModeHelper;

import static org.kie.maven.plugin.KieMavenPluginContext.getKieMavenPluginContext;
import static org.kie.maven.plugin.executors.BuildDrlExecutor.buildDrl;
import static org.kie.maven.plugin.executors.GenerateANCExecutor.generateANC;
import static org.kie.maven.plugin.executors.GenerateDMNModelExecutor.generateDMN;
import static org.kie.maven.plugin.executors.GenerateModelExecutor.generateModel;
import static org.kie.maven.plugin.executors.GeneratePMMLModelExecutor.generatePMMLModel;
import static org.kie.maven.plugin.helpers.ExecModelModeHelper.ancEnabled;

/**
 * This goal builds the Drools files belonging to the kproject.
 */
@Mojo(name = "build",
        requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME,
        requiresProject = true,
        defaultPhase = LifecyclePhase.COMPILE)
public class BuildMojo extends AbstractKieMojo {


    public void execute() throws MojoExecutionException, MojoFailureException {
        KieMavenPluginContext kieMavenPluginContext = getKieMavenPluginContext(this);
        executeGenerateModel(kieMavenPluginContext);
        executeGenerateDMNModel(kieMavenPluginContext);
        executeGeneratePMMLModel(kieMavenPluginContext);
        executeGenerateANC(kieMavenPluginContext);
        executeBuildDRL(kieMavenPluginContext);
    }

    private void executeGenerateModel(final KieMavenPluginContext kieMavenPluginContext) throws MojoExecutionException, MojoFailureException {
        getLog().info("GenerateModel");
        // GenerateModelMojo is executed when BuildMojo isn't and vice-versa
        boolean modelParameterEnabled = kieMavenPluginContext.isModelParameterEnabled();
        boolean modelCompilerInClassPath = kieMavenPluginContext.isModelCompilerInClass();
        if (modelParameterEnabled && modelCompilerInClassPath) {
            generateModel(kieMavenPluginContext);
        } else if (modelParameterEnabled) { // !modelCompilerInClassPath
            getLog().warn("You're trying to build rule assets in a project from an executable rule model, but you did" +
                                  " not provide the required dependency on the project classpath.\n" +
                                  "To enable executable rule models for your project, add the `drools-model-compiler`" +
                                  " dependency in the `pom.xml` file of your project.\n");
        }
    }

    private void executeGenerateDMNModel(final KieMavenPluginContext kieMavenPluginContext) throws MojoExecutionException,
            MojoFailureException {
        getLog().info("GenerateDMNModel");
        boolean dmnModelParameterEnabled = DMNModelModeHelper.dmnModelParameterEnabled(kieMavenPluginContext.getGenerateDMNModel());
        boolean modelCompilerInClassPath = kieMavenPluginContext.isModelCompilerInClass();
        if (dmnModelParameterEnabled && modelCompilerInClassPath) {
            generateDMN(kieMavenPluginContext);
        }
    }

    private void executeGeneratePMMLModel(final KieMavenPluginContext kieMavenPluginContext) throws MojoExecutionException,
            MojoFailureException {
        getLog().info("GeneratePMMLModel");
        boolean modelCompilerInClassPath = kieMavenPluginContext.isModelCompilerInClass();
        if (!modelCompilerInClassPath) {
            getLog().warn("Skipping `generatePMMLModel` because you did" +
                                  " not provide the required dependency on the project classpath.\n" +
                                  "To enable it for your project, add the `drools-model-compiler`" +
                                  " dependency in the `pom.xml` file of your project.\n");
        } else {
            generatePMMLModel(kieMavenPluginContext);
        }
    }

    private void executeGenerateANC(final KieMavenPluginContext kieMavenPluginContext) throws MojoExecutionException, MojoFailureException {
        getLog().info("GenerateANC");
        final String generateModel = kieMavenPluginContext.getGenerateModel();

        // GenerateModelMojo is executed when BuildMojo isn't and vice-versa
        boolean ancParameterEnabled = ancEnabled(generateModel);
        boolean modelCompilerInClassPath = kieMavenPluginContext.isModelCompilerInClass();
        if (ancParameterEnabled && modelCompilerInClassPath) {
            generateANC(kieMavenPluginContext);
        } else if (ancParameterEnabled) { // !modelCompilerInClassPath
            getLog().warn("You're trying to build rule assets in a project from an executable rule model, but you did" +
                                  " not provide the required dependency on the project classpath.\n" +
                                  "To enable executable rule models for your project, add the `drools-model-compiler`" +
                                  " dependency in the `pom.xml` file of your project.\n");
        }
    }

    private void executeBuildDRL(final KieMavenPluginContext kieMavenPluginContext) throws MojoExecutionException, MojoFailureException {
        getLog().info("BuildDRL");
        // BuildMojo is executed when GenerateModelMojo isn't and vice-versa
        boolean modelParameterEnabled = kieMavenPluginContext.isModelParameterEnabled();
        boolean modelCompilerInClassPath = kieMavenPluginContext.isModelCompilerInClass();
        if (!(modelParameterEnabled && modelCompilerInClassPath)) {
            buildDrl(kieMavenPluginContext);
        }
    }
}