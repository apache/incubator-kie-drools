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
 *
 */

package org.kie.kogito.maven.plugin;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

/**
 * Maven Mojo for generating Kogito model code from various asset types.
 * <p>
 * This goal generates Java code from Kogito assets including:
 * <ul>
 * <li>Business Process Models (BPMN)</li>
 * <li>Decision Models (DMN)</li>
 * <li>Business Rules (DRL)</li>
 * <li>Predictive Models (PMML)</li>
 * </ul>
 * <p>
 * The generated code includes process implementations, decision services, rule units,
 * and prediction models that are required for runtime execution of Kogito applications.
 * <p>
 * This mojo is bound to the {@code process-classes} phase. This is required to ensure that
 * the project's static classes written by the end user in the src directory are compiled
 * prior to code generation.
 * 
 */
@Mojo(name = "generateModel",
        requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME,
        defaultPhase = LifecyclePhase.PROCESS_CLASSES,
        threadSafe = true)
public class GenerateModelMojo extends AbstractKieMojo {

    @Override
    public void execute() throws MojoExecutionException {
        buildProject();
    }
}
