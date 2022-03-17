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
import org.apache.maven.project.MavenProject;
import org.kie.maven.plugin.PluginDTO;

import static org.kie.maven.plugin.executors.GeneratePMMLModelExecutor.generatePMMLModel;
import static org.kie.maven.plugin.helpers.ExecModelModeHelper.isModelCompilerInClassPath;

@Mojo(name = "generatePMMLModel",
        requiresDependencyResolution = ResolutionScope.NONE,
        requiresProject = true,
        defaultPhase = LifecyclePhase.COMPILE)
public class GeneratePMMLModelMojo extends AbstractKieMojo {

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        final PluginDTO pluginDTO = getPluginDTO();
        MavenProject project = pluginDTO.getProject();
        boolean modelCompilerInClassPath = isModelCompilerInClassPath(project.getDependencies());
        if (!modelCompilerInClassPath) {
            getLog().warn("Skipping `generatePMMLModel` because you did" +
                                  " not provide the required dependency on the project classpath.\n" +
                                  "To enable it for your project, add the `drools-model-compiler`" +
                                  " dependency in the `pom.xml` file of your project.\n");
        } else {
            generatePMMLModel(pluginDTO);
        }
    }

}
