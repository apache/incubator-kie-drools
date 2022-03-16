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
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.kie.maven.plugin.helpers.DMNModelModeHelper;

import static org.kie.maven.plugin.executors.GenerateDMNModelExecutor.generateDMN;
import static org.kie.maven.plugin.helpers.ExecModelModeHelper.isModelCompilerInClassPath;

@Mojo(name = "generateDMNModel",
        requiresDependencyResolution = ResolutionScope.NONE,
        requiresProject = true,
        defaultPhase = LifecyclePhase.COMPILE)
public class GenerateDMNModelMojo extends AbstractKieMojo {

    @Parameter(property = "generateDMNModel", defaultValue = "no")
    private String generateDMNModel;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        boolean dmnModelParameterEnabled = DMNModelModeHelper.dmnModelParameterEnabled(generateDMNModel);
        boolean modelCompilerInClassPath = isModelCompilerInClassPath(project.getDependencies());

        if (dmnModelParameterEnabled && modelCompilerInClassPath) {
            generateDMN(projectDir,
                        properties,
                        targetDirectory,
                        dumpKieSourcesFolder,
                        getCompilerType(),
                        getLog());
        }
    }

}

