/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.maven.plugin;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
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

import com.google.common.io.Files;

@Mojo(name = "package-dependencies-kjar",
      defaultPhase = LifecyclePhase.PREPARE_PACKAGE,
      threadSafe = true,
      requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class PackageKjarDependenciesMojo extends AbstractKieMojo {

    private static final Pattern ALT_REPO_SYNTAX_PATTERN = Pattern.compile("(.+)::(.*)::(.+)");

    @Parameter(defaultValue = "${project.build.outputDirectory}", required = true, readonly = true)
    private String outputDirectory;

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    @Parameter
    private String classifier;

    @Parameter(defaultValue = "${session}", readonly = true, required = true)
    private MavenSession session;

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
        try {
            if (artifactItems == null || artifactItems.isEmpty()) {
                getLog().info("Skipping plugin execution");
                return;
            }

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
                    repoList.add(parseRepository(repo, always));
                }
            }

            File outputFolder = new File(outputDirectory + "/KIE-INF/lib");
            if (!outputFolder.exists()) {
                outputFolder.mkdirs();
            }
            getLog().info("Create directory: " + outputFolder);

            Set<Artifact> artifacts = new HashSet<>();
            for (ArtifactItem artifactItem : artifactItems) {
                Artifact kjar = resolveArtifact(toArtifactCoordinate(artifactItem), repoList);
                getLog().info("Resolved kjar " + kjar + " dependency");
                artifacts.add(kjar);

                ProjectBuildingRequest buildingRequest = buildMavenRequest(repoList);
                Iterable<ArtifactResult> results = dependencyResolver.resolveDependencies(buildingRequest, toDefaultDependableCoordinate(kjar), null);
                for (ArtifactResult result : results) {
                    Artifact kjarDependency = resolveArtifact(toArtifactCoordinate(result.getArtifact()), repoList);
                    artifacts.add(kjarDependency);
                }
            }

            for(Artifact artifact : artifacts) {
                getLog().info("Copying artifact and creating effective pom: " + artifact);
                writeEffectivePom(projectBuilder.build(artifact, session.getProjectBuildingRequest()).getProject(), new File(outputFolder, toFile(artifact)));
                File local = artifact.getFile();
                Files.copy(local, new File(outputFolder, local.getName()));
            }
        } catch (IOException | ArtifactResolverException | DependencyResolverException | ProjectBuildingException e) {
            throw new MojoExecutionException("Couldn't download artifact: " + e.getMessage(), e);
        }

    }

    private String toFile(Artifact kjarDependency) {
        return kjarDependency.getArtifactId() + "-" + kjarDependency.getVersion() + ".pom";
    }

    private File writeEffectivePom(MavenProject mavenProject, File output) throws MojoExecutionException {
        Model m = mavenProject.getModel();
        Writer writer = null;
        try {
            writer = WriterFactory.newXmlWriter(output);
            new MavenXpp3Writer().write(writer, m);
            getLog().debug("Written effective pom at:" + output.getAbsolutePath());
            return output;
        } catch (IOException e) {
            throw new MojoExecutionException("Error writing file: " + e.getMessage(), e);
        } finally {
            IOUtil.close(writer);
        }
    }


    private Artifact resolveArtifact(ArtifactCoordinate artifact, List<ArtifactRepository> repoList) throws ArtifactResolverException {
        ProjectBuildingRequest buildingRequest = buildMavenRequest(repoList);
        getLog().debug("resolving kjar dependency: " + artifact);
        ArtifactResult artifactResolverResult = artifactResolver.resolveArtifact(buildingRequest, artifact);
        Artifact artifactResolved = artifactResolverResult.getArtifact();
        return artifactResolved;
    }


    private ProjectBuildingRequest buildMavenRequest(List<ArtifactRepository> repoList) {
        ProjectBuildingRequest buildingRequest = new DefaultProjectBuildingRequest(session.getProjectBuildingRequest());
        Settings settings = session.getSettings();
        repositorySystem.injectMirror(repoList, settings.getMirrors());
        repositorySystem.injectProxy(repoList, settings.getProxies());
        repositorySystem.injectAuthentication(repoList, settings.getServers());
        buildingRequest.setRemoteRepositories(repoList);
        return buildingRequest;
    }

    private ArtifactCoordinate toArtifactCoordinate(Artifact artifact) {
        ArtifactHandler artifactHandler = artifactHandlerManager.getArtifactHandler(artifact.getType());
        DefaultArtifactCoordinate gav = new DefaultArtifactCoordinate();
        gav.setGroupId(artifact.getGroupId());
        gav.setArtifactId(artifact.getArtifactId());
        gav.setVersion(artifact.getVersion());
        gav.setClassifier(artifact.getClassifier());
        gav.setExtension(artifactHandler.getExtension());
        return gav;
    }

    private ArtifactCoordinate toArtifactCoordinate(ArtifactItem artifact) {
        ArtifactHandler artifactHandler = artifactHandlerManager.getArtifactHandler(artifact.getType());
        DefaultArtifactCoordinate gav = new DefaultArtifactCoordinate();
        gav.setGroupId(artifact.getGroupId());
        gav.setArtifactId(artifact.getArtifactId());
        gav.setVersion(artifact.getVersion());
        gav.setClassifier(artifact.getClassifier());
        gav.setExtension(artifactHandler.getExtension());
        return gav;
    }

    private DefaultDependableCoordinate toDefaultDependableCoordinate(Artifact artifact){
        DefaultDependableCoordinate gav = new DefaultDependableCoordinate();
        gav.setArtifactId(artifact.getArtifactId());
        gav.setGroupId(artifact.getGroupId());
        gav.setVersion(artifact.getVersion());
        gav.setClassifier(artifact.getClassifier());
        gav.setType(artifact.getType());
        return gav;
    }

    private ArtifactRepository parseRepository(String repo, ArtifactRepositoryPolicy policy) throws MojoFailureException {
        // if it's a simple url
        String id = "temp";
        ArtifactRepositoryLayout layout = getLayout("default");
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
                layout = getLayout(matcher.group(2).trim());
            }
            url = matcher.group(3).trim();
        }
        return new MavenArtifactRepository(id, url, layout, policy, policy);
    }

    private ArtifactRepositoryLayout getLayout(String id) throws MojoFailureException {
        ArtifactRepositoryLayout layout = repositoryLayouts.get(id);

        if (layout == null) {
            throw new MojoFailureException(id, "Invalid repository layout", "Invalid repository layout: " + id);
        }

        return layout;
    }


}