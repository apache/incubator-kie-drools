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

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;

@Mojo(name = "scaffold",
        requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME,
        requiresProject = true,
        threadSafe = true)
public class ScaffoldMojo extends GenerateModelMojo {

    @Parameter(property = "kogito.codegen.ondemand", defaultValue = "true")
    private boolean onDemand;

    @Parameter(property = "kogito.codegen.sources.directory", defaultValue = "${project.build.sourceDirectory}")
    private File customizableSources;

    @Override
    public void execute() throws MojoExecutionException {
        addCompileSourceRoots();
        ClassLoader projectClassLoader = projectClassLoader();
        KogitoBuildContext kogitoBuildContext = getKogitoBuildContext(projectClassLoader);
        generateModel(kogitoBuildContext);
    }

    @Override
    public boolean isOnDemand() {
        return onDemand;
    }

}
