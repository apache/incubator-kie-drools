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
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

import static org.kie.maven.plugin.executors.GenerateANCExecutor.generateANC;
import static org.kie.maven.plugin.helpers.ExecModelModeHelper.ancEnabled;
import static org.kie.maven.plugin.helpers.ExecModelModeHelper.isModelCompilerInClassPath;

@Mojo(name = "generateANC",
        requiresDependencyResolution = ResolutionScope.NONE,
        defaultPhase = LifecyclePhase.COMPILE)
public class GenerateANCMojo extends AbstractKieMojo {

    @Override
    public void execute() throws MojoExecutionException {
        // GenerateModelMojo is executed when BuildMojo isn't and vice-versa
        boolean ancParameterEnabled = ancEnabled(generateModel);
        boolean modelCompilerInClassPath = isModelCompilerInClassPath(project.getDependencies());
        if (ancParameterEnabled && modelCompilerInClassPath) {
            generateANC(project,
                        outputDirectory,
                        properties,
                        targetDirectory,
                        dumpKieSourcesFolder,
                        getCompilerType(),
                        getLog());
        } else if (ancParameterEnabled) { // !modelCompilerInClassPath
            getLog().warn("You're trying to build rule assets in a project from an executable rule model, but you did not provide the required dependency on the project classpath.\n" +
                                  "To enable executable rule models for your project, add the `drools-model-compiler` dependency in the `pom.xml` file of your project.\n");
        }
    }

}
