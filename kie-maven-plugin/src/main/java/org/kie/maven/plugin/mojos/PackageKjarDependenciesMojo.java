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

import java.util.List;
import java.util.Map;

import org.apache.maven.artifact.handler.manager.ArtifactHandlerManager;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.layout.ArtifactRepositoryLayout;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.repository.RepositorySystem;
import org.apache.maven.shared.artifact.resolve.ArtifactResolver;
import org.apache.maven.shared.dependencies.resolve.DependencyResolver;
import org.kie.maven.plugin.ArtifactItem;

import static org.kie.maven.plugin.executors.PackageKjarDependenciesExecutor.packageKJarDependencies;

@Mojo(name = "package-dependencies-kjar",
      defaultPhase = LifecyclePhase.PREPARE_PACKAGE,
      threadSafe = true,
      requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class PackageKjarDependenciesMojo extends AbstractKieMojo {

    @Parameter
    private String classifier;

    @Component
    private ProjectBuilder projectBuilder;

    @Component
    private ArtifactResolver artifactResolver;

    @Component
    private DependencyResolver dependencyResolver;

    @Parameter(defaultValue = "${project.remoteArtifactRepositories}", readonly = true, required = true)
    private List<ArtifactRepository> pomRemoteRepositories;

    @Parameter(property = "remoteRepositories")
    private String remoteRepositories;

    @Component
    private RepositorySystem repositorySystem;

    @Component
    private ArtifactHandlerManager artifactHandlerManager;

    @Component(role = ArtifactRepositoryLayout.class)
    private Map<String, ArtifactRepositoryLayout> repositoryLayouts;

    @Parameter
    private List<ArtifactItem> artifactItems;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (artifactItems == null || artifactItems.isEmpty()) {
            getLog().info("Skipping plugin execution");
            return;
        }

        packageKJarDependencies(mavenSession,
                                outputDirectory,
                                pomRemoteRepositories,
                                artifactItems,
                                artifactResolver,
                                artifactHandlerManager,
                                dependencyResolver,
                                projectBuilder,
                                repositorySystem,
                                repositoryLayouts,
                                remoteRepositories,
                                getLog());
    }

}