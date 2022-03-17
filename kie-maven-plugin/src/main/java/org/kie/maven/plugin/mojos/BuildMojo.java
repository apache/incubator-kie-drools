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

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.kie.maven.plugin.PluginDTO;
import org.kie.maven.plugin.helpers.DMNModelModeHelper;

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
        final PluginDTO pluginDTO = getPluginDTO();
        executeGenerateModel(pluginDTO);
        executeGenerateDMNModel(pluginDTO);
        executeGeneratePMMLModel(pluginDTO);
        executeGenerateANC(pluginDTO);
        executeBuildDRL(pluginDTO);
    }

    private void executeGenerateModel(final PluginDTO pluginDTO) throws MojoExecutionException, MojoFailureException {
        pluginDTO.getLog().info("GenerateModel");
        // GenerateModelMojo is executed when BuildMojo isn't and vice-versa
        boolean modelParameterEnabled = pluginDTO.isModelParameterEnabled();
        boolean modelCompilerInClassPath = pluginDTO.isModelCompilerInClass();
        if (modelParameterEnabled && modelCompilerInClassPath) {
            generateModel(pluginDTO);
        } else if (modelParameterEnabled) { // !modelCompilerInClassPath
            getLog().warn("You're trying to build rule assets in a project from an executable rule model, but you did" +
                                  " not provide the required dependency on the project classpath.\n" +
                                  "To enable executable rule models for your project, add the `drools-model-compiler`" +
                                  " dependency in the `pom.xml` file of your project.\n");
        }
    }

    private void executeGenerateDMNModel(final PluginDTO pluginDTO) throws MojoExecutionException,
            MojoFailureException {
        pluginDTO.getLog().info("GenerateDMNModel");
        boolean dmnModelParameterEnabled = DMNModelModeHelper.dmnModelParameterEnabled(pluginDTO.getGenerateDMNModel());
        boolean modelCompilerInClassPath = pluginDTO.isModelCompilerInClass();
        if (dmnModelParameterEnabled && modelCompilerInClassPath) {
            generateDMN(pluginDTO);
        }
    }

    private void executeGeneratePMMLModel(final PluginDTO pluginDTO) throws MojoExecutionException,
            MojoFailureException {
        pluginDTO.getLog().info("GeneratePMMLModel");
        boolean modelCompilerInClassPath = pluginDTO.isModelCompilerInClass();
        if (!modelCompilerInClassPath) {
            getLog().warn("Skipping `generatePMMLModel` because you did" +
                                  " not provide the required dependency on the project classpath.\n" +
                                  "To enable it for your project, add the `drools-model-compiler`" +
                                  " dependency in the `pom.xml` file of your project.\n");
        } else {
            generatePMMLModel(pluginDTO);
        }
    }

    private void executeGenerateANC(final PluginDTO pluginDTO) throws MojoExecutionException, MojoFailureException {
        pluginDTO.getLog().info("GenerateANC");
        final String generateModel = pluginDTO.getGenerateModel();

        // GenerateModelMojo is executed when BuildMojo isn't and vice-versa
        boolean ancParameterEnabled = ancEnabled(generateModel);
        boolean modelCompilerInClassPath = pluginDTO.isModelCompilerInClass();
        if (ancParameterEnabled && modelCompilerInClassPath) {
            generateANC(pluginDTO);
        } else if (ancParameterEnabled) { // !modelCompilerInClassPath
            getLog().warn("You're trying to build rule assets in a project from an executable rule model, but you did" +
                                  " not provide the required dependency on the project classpath.\n" +
                                  "To enable executable rule models for your project, add the `drools-model-compiler`" +
                                  " dependency in the `pom.xml` file of your project.\n");
        }
    }

    private void executeBuildDRL(final PluginDTO pluginDTO) throws MojoExecutionException, MojoFailureException {
        pluginDTO.getLog().info("BuildDRL");
        // BuildMojo is executed when GenerateModelMojo isn't and vice-versa
        boolean modelParameterEnabled = pluginDTO.isModelParameterEnabled();
        boolean modelCompilerInClassPath = pluginDTO.isModelCompilerInClass();
        if (!(modelParameterEnabled && modelCompilerInClassPath)) {
            buildDrl(pluginDTO);
        }
    }
}