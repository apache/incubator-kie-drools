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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.artifact.handler.manager.ArtifactHandlerManager;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.ArtifactRepositoryPolicy;
import org.apache.maven.artifact.repository.MavenArtifactRepository;
import org.apache.maven.artifact.repository.layout.ArtifactRepositoryLayout;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.DefaultProjectBuildingRequest;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.project.ProjectBuildingRequest;
import org.apache.maven.repository.RepositorySystem;
import org.apache.maven.settings.Settings;
import org.apache.maven.shared.artifact.ArtifactCoordinate;
import org.apache.maven.shared.artifact.DefaultArtifactCoordinate;
import org.apache.maven.shared.artifact.resolve.ArtifactResolver;
import org.apache.maven.shared.artifact.resolve.ArtifactResolverException;
import org.apache.maven.shared.artifact.resolve.ArtifactResult;
import org.apache.maven.shared.dependencies.DefaultDependableCoordinate;
import org.apache.maven.shared.dependencies.resolve.DependencyResolver;
import org.apache.maven.shared.dependencies.resolve.DependencyResolverException;
import org.apache.maven.shared.utils.StringUtils;
import org.apache.maven.shared.utils.WriterFactory;
import org.apache.maven.shared.utils.io.IOUtil;
import org.kie.maven.plugin.ArtifactItem;
import org.kie.maven.plugin.KieMavenPluginContext;

import static org.kie.maven.plugin.KieMavenPluginContext.getKieMavenPluginContext;

@Mojo(name = "package-dependencies-kjar",
        defaultPhase = LifecyclePhase.PREPARE_PACKAGE,
        threadSafe = true,
        requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class PackageKjarDependenciesMojo extends AbstractKieMojo {

    private static final Pattern ALT_REPO_SYNTAX_PATTERN = Pattern.compile("(.+)::(.*)::(.+)");

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
        Log log = getLog();
        if (artifactItems == null || artifactItems.isEmpty()) {
            log.info("Skipping plugin execution");
            return;
        }
        final KieMavenPluginContext kieMavenPluginContext = getKieMavenPluginContext(this);
        final MavenSession mavenSession = kieMavenPluginContext.getMavenSession();
        final File outputDirectory = kieMavenPluginContext.getOutputDirectory();
        try {

            ArtifactRepositoryPolicy always =
                    new ArtifactRepositoryPolicy(true, ArtifactRepositoryPolicy.UPDATE_POLICY_ALWAYS,
                                                 ArtifactRepositoryPolicy.CHECKSUM_POLICY_WARN);

            List<ArtifactRepository> repoList = new ArrayList<>();

            if (pomRemoteRepositories != null) {
                repoList.addAll(pomRemoteRepositories);
            }

            if (remoteRepositories != null) {
                // Use the same format as in the deploy plugin id::layout::url
                String[] repos = StringUtils.split(remoteRepositories, ",");
                for (String repo : repos) {
                    repoList.add(parseRepository(repo, always, repositoryLayouts));
                }
            }

            File outputFolder = new File(outputDirectory + "/KIE-INF/lib");
            if (!outputFolder.exists()) {
                outputFolder.mkdirs();
            }
            log.info("Create directory: " + outputFolder);

            Set<Artifact> artifacts = new HashSet<>();
            for (ArtifactItem artifactItem : artifactItems) {
                ArtifactCoordinate coordinate = toArtifactCoordinate(artifactItem, artifactHandlerManager);
                Artifact kjar = resolveArtifact(coordinate, artifactResolver, repoList, mavenSession,
                                                repositorySystem, log);
                log.info("Resolved kjar " + kjar + " dependency");
                artifacts.add(kjar);
                ProjectBuildingRequest buildingRequest = buildMavenRequest(repoList, mavenSession, repositorySystem);
                Iterable<ArtifactResult> results = dependencyResolver.resolveDependencies(buildingRequest,
                                                                                          toDefaultDependableCoordinate(kjar), null);
                for (ArtifactResult result : results) {
                    coordinate = toArtifactCoordinate(result.getArtifact(), artifactHandlerManager);
                    Artifact kjarDependency = resolveArtifact(coordinate, artifactResolver, repoList, mavenSession,
                                                              repositorySystem,
                                                              log);
                    artifacts.add(kjarDependency);
                }
            }

            for (Artifact artifact : artifacts) {
                log.info("Copying artifact and creating effective pom: " + artifact);
                writeEffectivePom(projectBuilder.build(artifact, mavenSession.getProjectBuildingRequest()).getProject(),
                                  new File(outputFolder, toFile(artifact)),
                                  log);
                File local = artifact.getFile();
                try (FileOutputStream fos = new FileOutputStream(new File(outputFolder, local.getName()))) {
                    Files.copy(local.toPath(), fos);
                }
            }
        } catch (IOException | ArtifactResolverException | DependencyResolverException | ProjectBuildingException e) {
            throw new MojoExecutionException("Couldn't download artifact: " + e.getMessage(), e);
        }
    }

    private static String toFile(Artifact kjarDependency) {
        return kjarDependency.getArtifactId() + "-" + kjarDependency.getVersion() + ".pom";
    }

    private static File writeEffectivePom(MavenProject mavenProject, File output, Log log) throws MojoExecutionException {
        Model m = mavenProject.getModel();
        Writer writer = null;
        try {
            writer = WriterFactory.newXmlWriter(output);
            new MavenXpp3Writer().write(writer, m);
            log.debug("Written effective pom at:" + output.getAbsolutePath());
            return output;
        } catch (IOException e) {
            throw new MojoExecutionException("Error writing file: " + e.getMessage(), e);
        } finally {
            IOUtil.close(writer);
        }
    }

    private static Artifact resolveArtifact(ArtifactCoordinate artifact, ArtifactResolver artifactResolver,
                                            List<ArtifactRepository> repoList, MavenSession mavenSession,
                                            RepositorySystem repositorySystem, Log log) throws ArtifactResolverException {
        ProjectBuildingRequest buildingRequest = buildMavenRequest(repoList, mavenSession, repositorySystem);
        log.debug("resolving kjar dependency: " + artifact);
        ArtifactResult artifactResolverResult = artifactResolver.resolveArtifact(buildingRequest, artifact);
        return artifactResolverResult.getArtifact();
    }

    private static ProjectBuildingRequest buildMavenRequest(List<ArtifactRepository> repoList,
                                                            MavenSession mavenSession,
                                                            RepositorySystem repositorySystem) {
        ProjectBuildingRequest buildingRequest =
                new DefaultProjectBuildingRequest(mavenSession.getProjectBuildingRequest());
        Settings settings = mavenSession.getSettings();
        repositorySystem.injectMirror(repoList, settings.getMirrors());
        repositorySystem.injectProxy(repoList, settings.getProxies());
        repositorySystem.injectAuthentication(repoList, settings.getServers());
        buildingRequest.setRemoteRepositories(repoList);
        return buildingRequest;
    }

    private static ArtifactCoordinate toArtifactCoordinate(Artifact artifact,
                                                           ArtifactHandlerManager artifactHandlerManager) {
        ArtifactHandler artifactHandler = artifactHandlerManager.getArtifactHandler(artifact.getType());
        DefaultArtifactCoordinate gav = new DefaultArtifactCoordinate();
        gav.setGroupId(artifact.getGroupId());
        gav.setArtifactId(artifact.getArtifactId());
        gav.setVersion(artifact.getVersion());
        gav.setClassifier(artifact.getClassifier());
        gav.setExtension(artifactHandler.getExtension());
        return gav;
    }

    private static ArtifactCoordinate toArtifactCoordinate(ArtifactItem artifact,
                                                           ArtifactHandlerManager artifactHandlerManager) {
        ArtifactHandler artifactHandler = artifactHandlerManager.getArtifactHandler(artifact.getType());
        DefaultArtifactCoordinate gav = new DefaultArtifactCoordinate();
        gav.setGroupId(artifact.getGroupId());
        gav.setArtifactId(artifact.getArtifactId());
        gav.setVersion(artifact.getVersion());
        gav.setClassifier(artifact.getClassifier());
        gav.setExtension(artifactHandler.getExtension());
        return gav;
    }

    private static DefaultDependableCoordinate toDefaultDependableCoordinate(Artifact artifact) {
        DefaultDependableCoordinate gav = new DefaultDependableCoordinate();
        gav.setArtifactId(artifact.getArtifactId());
        gav.setGroupId(artifact.getGroupId());
        gav.setVersion(artifact.getVersion());
        gav.setClassifier(artifact.getClassifier());
        gav.setType(artifact.getType());
        return gav;
    }

    private static ArtifactRepository parseRepository(String repo, ArtifactRepositoryPolicy policy, Map<String,
            ArtifactRepositoryLayout> repositoryLayouts) throws MojoFailureException {
        // if it's a simple url
        String id = "temp";
        ArtifactRepositoryLayout layout = getLayout("default", repositoryLayouts);
        String url = repo;

        // if it's an extended repo URL of the form id::layout::url
        if (repo.contains("::")) {
            Matcher matcher = ALT_REPO_SYNTAX_PATTERN.matcher(repo);
            if (!matcher.matches()) {
                throw new MojoFailureException(repo, "Invalid syntax for repository: " + repo,
                                               "Invalid syntax for repository. Use \"id::layout::url\" or \"URL\".");
            }

            id = matcher.group(1).trim();
            if (!StringUtils.isEmpty(matcher.group(2))) {
                layout = getLayout(matcher.group(2).trim(), repositoryLayouts);
            }
            url = matcher.group(3).trim();
        }
        return new MavenArtifactRepository(id, url, layout, policy, policy);
    }

    private static ArtifactRepositoryLayout getLayout(String id,
                                                      Map<String, ArtifactRepositoryLayout> repositoryLayouts) throws MojoFailureException {
        ArtifactRepositoryLayout layout = repositoryLayouts.get(id);

        if (layout == null) {
            throw new MojoFailureException(id, "Invalid repository layout", "Invalid repository layout: " + id);
        }

        return layout;
    }
}