/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.maven.plugin;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.util.List;

/**
 * Compiles and serializes knowledge packages.
 */
@Mojo(name = "touch",
      requiresProject = true,
      defaultPhase = LifecyclePhase.GENERATE_RESOURCES)
public class TouchResourcesMojo extends AbstractKieMojo {

    /**
     * DRL rule package
     */
    @Parameter(property = "kie.ruleFiles",required = true)
    private List<String> ruleFiles;

    /**
     * KnowledgeBases to serialize
     */
    @Parameter(property = "kie.kiebases",required = true)
    private List<String> kiebases;

    /**
     * Output folder
     */
    @Parameter(property = "kie.resDirectory", defaultValue = "${project.basedir}/res/raw" )
    private String resDirectory;

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            File outputFolder = new File(resDirectory);
            outputFolder.mkdirs();

            for(String kbase : kiebases) {
                getLog().info("Touching KBase: " + kbase);
                File file = new File(outputFolder, kbase.replace('.', '_').toLowerCase());
                file.createNewFile();
            }
        } catch (Exception e) {
            throw new MojoExecutionException("error", e);
        }
    }
}
