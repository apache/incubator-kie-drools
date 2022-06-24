/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.maven.plugin.executors;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.apache.maven.DefaultMaven;
import org.apache.maven.Maven;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.layout.DefaultRepositoryLayout;
import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.execution.DefaultMavenExecutionResult;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.descriptor.MojoDescriptor;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.project.ProjectBuildingRequest;
import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.apache.maven.settings.Settings;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.internal.impl.SimpleLocalRepositoryManagerFactory;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.LocalRepositoryManager;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.transport.file.FileTransporterFactory;
import org.eclipse.aether.transport.http.HttpTransporterFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.kie.maven.plugin.KieMavenPluginContext;
import org.kie.maven.plugin.mojos.BuildMojo;

import static org.kie.maven.plugin.KieMavenPluginContext.getKieMavenPluginContext;

class GeneratePMMLModelExecutorTest extends AbstractMojoTestCase {

//    protected MavenProjectBuilder mavenProjectBuilder;

//    private BuildMojo mojo;

    /**
     * {@inheritDoc}
     */
    @BeforeEach
    protected void setUp() throws Exception {
        // required
        super.setUp();
//        mavenProjectBuilder = (MavenProjectBuilder) getContainer().lookup(
//                MavenProjectBuilder.class);

//        ClassLoader classLoader = getClass().getClassLoader();
//        URL url = classLoader.getResource("src/test/resources/unit/pmml/pom.xml");
//        if (url == null) {
//            throw new MojoExecutionException(String.format(
//                    "Cannot locate %s", "src/test/resources/unit/pmml/pom.xml"));
//        }
//        File pom = new File(url.getFile());

//        File pom = getTestFile("src/test/resources/unit/pmml/pom.xml");
//        assertNotNull(pom);
//
//        Settings settings = getMavenSettings();
//        if (settings.getLocalRepository() == null) {
//            settings.setLocalRepository(
//                    org.apache.maven.repository.RepositorySystem
//                            .defaultUserLocalRepository.getAbsolutePath());
//        }
//        MavenExecutionRequest request = new DefaultMavenExecutionRequest();
//        request.setPom(pom);
//        ArtifactRepository artifactRepository =
//                new org.apache.maven.artifact.repository.
//                        DefaultArtifactRepository(
//                        "id", settings.getLocalRepository(),
//                        new DefaultRepositoryLayout());
//        request.setLocalRepository(artifactRepository);
//        MavenExecutionRequestPopulator populator =
//                getContainer().lookup(MavenExecutionRequestPopulator.class);
//        populator.populateFromSettings(request, settings);
//        DefaultMaven maven = (DefaultMaven)
//                getContainer().lookup(Maven.class);
//        DefaultRepositorySystemSession repositorySystemSession =
//                (DefaultRepositorySystemSession)
//                        maven.newRepositorySession(request);
//        SimpleLocalRepositoryManagerFactory factory =
//                new SimpleLocalRepositoryManagerFactory();
//        LocalRepositoryManager localRepositoryManager =
//                factory.newInstance(repositorySystemSession,
//                                    new LocalRepository(settings.getLocalRepository()));
//        repositorySystemSession.setLocalRepositoryManager(
//                localRepositoryManager);
//        ProjectBuildingRequest buildingRequest =
//                request.getProjectBuildingRequest()
//                        .setRepositorySession(repositorySystemSession)
//                        .setResolveDependencies(true);
//        ProjectBuilder projectBuilder = lookup(ProjectBuilder.class);
//        ProjectBuildingResult projectBuildingResult =
//                projectBuilder.build(pom, buildingRequest);
//        MavenProject project = projectBuildingResult.getProject();
//        MavenSession session = new MavenSession(getContainer(),
//                                                repositorySystemSession, request,
//                                                new DefaultMavenExecutionResult());
//        session.setCurrentProject(project);
//        session.setProjects(Collections.singletonList(project));
//        request.setSystemProperties(System.getProperties());
//
//        mojo = (BuildMojo) lookupConfiguredMojo(session,
//                                                newMojoExecution("build"));
//        assertNotNull(mojo);
//        mojo.getLog().debug(String.format("localRepo = %s",
//                                          request.getLocalRepository()));
//        copyTestProjectResourcesToTarget(getContainer(), project, session);
//        resolveConfigurationFromRepo(repositorySystemSession, project);
    }

    /**
     * {@inheritDoc}
     */
    @AfterEach
    protected void tearDown() throws Exception {
        // required
        super.tearDown();
    }

    @Disabled
    @Test
    public void generatePMMLModel() throws Exception {
        File pom = getTestFile("src/test/resources/unit/pmml/pom.xml");
        assertNotNull(pom);

        Settings settings = getMavenSettings();
        if (settings.getLocalRepository() == null) {
            settings.setLocalRepository(
                    org.apache.maven.repository.RepositorySystem
                            .defaultUserLocalRepository.getAbsolutePath());
        }

        MavenExecutionRequest executionRequest = new DefaultMavenExecutionRequest();
        executionRequest.setPom(pom);
        ArtifactRepository artifactRepository =
                new org.apache.maven.artifact.repository.
                        DefaultArtifactRepository(
                        "id", settings.getLocalRepository(),
                        new DefaultRepositoryLayout());
        executionRequest.setLocalRepository(artifactRepository);

        DefaultMaven maven = (DefaultMaven)
                getContainer().lookup(Maven.class);
        DefaultRepositorySystemSession repositorySystemSession =
                (DefaultRepositorySystemSession)
                        maven.newRepositorySession(executionRequest);
        SimpleLocalRepositoryManagerFactory factory =
                new SimpleLocalRepositoryManagerFactory();
        LocalRepositoryManager localRepositoryManager =
                factory.newInstance(repositorySystemSession,
                                    new LocalRepository(settings.getLocalRepository()));
        repositorySystemSession.setLocalRepositoryManager(
                localRepositoryManager);
        ProjectBuildingRequest buildingRequest = executionRequest.getProjectBuildingRequest();
        buildingRequest.setLocalRepository(artifactRepository);
        buildingRequest.setResolveDependencies(true);
        DefaultRepositorySystemSession session = new DefaultRepositorySystemSession();
        session.setLocalRepositoryManager(localRepositoryManager);
        buildingRequest.setRepositorySession(session);
        ProjectBuilder projectBuilder = lookup(ProjectBuilder.class);
        MavenProject project = projectBuilder.build(pom, buildingRequest).getProject();

        MojoDescriptor mojoDescriptor = new MojoDescriptor();
        mojoDescriptor.setGoal("build");

        MavenSession mavenSession = new MavenSession(getContainer(),
                                                     repositorySystemSession, executionRequest,
                                                     new DefaultMavenExecutionResult());
        mavenSession.setCurrentProject(project);
        mavenSession.setProjects(Collections.singletonList(project));

        BuildMojo mojo = (BuildMojo) lookupConfiguredMojo(mavenSession, new MojoExecution(mojoDescriptor));
        assertNotNull(mojo);
        resolveConfigurationFromRepo(repositorySystemSession, project, mojo);
        KieMavenPluginContext kieMavenPluginContext = getKieMavenPluginContext(mojo);
        assertNotNull(kieMavenPluginContext);
        GeneratePMMLModelExecutor.generatePMMLModel(kieMavenPluginContext);
        System.out.println("done");
    }

    @Disabled
    @Test
    public void generatePMMLModelB() throws Exception {
        File pom = getTestFile("src/test/resources/unit/pmml/pom.xml");
        assertNotNull(pom);
        assertTrue(pom.exists());
        MavenExecutionRequest executionRequest = new DefaultMavenExecutionRequest();
        ProjectBuildingRequest buildingRequest = executionRequest.getProjectBuildingRequest();
        buildingRequest.setResolveDependencies(true);
        DefaultRepositorySystemSession session = new DefaultRepositorySystemSession();

        DefaultMaven maven = (DefaultMaven)
                getContainer().lookup(Maven.class);
        DefaultRepositorySystemSession repositorySystemSession =
                (DefaultRepositorySystemSession)
                        maven.newRepositorySession(executionRequest);
        SimpleLocalRepositoryManagerFactory factory =
                new SimpleLocalRepositoryManagerFactory();

        Settings settings = getMavenSettings();
        if (settings.getLocalRepository() == null) {
            settings.setLocalRepository(
                    org.apache.maven.repository.RepositorySystem
                            .defaultUserLocalRepository.getAbsolutePath());
        }

        LocalRepositoryManager localRepositoryManager =
                factory.newInstance(repositorySystemSession,
                                    new LocalRepository(settings.getLocalRepository()));
        session.setLocalRepositoryManager(localRepositoryManager);
        buildingRequest.setRepositorySession(session);


        ArtifactRepository artifactRepository =
                new org.apache.maven.artifact.repository.
                        DefaultArtifactRepository(
                        "id", settings.getLocalRepository(),
                        new DefaultRepositoryLayout());
        buildingRequest.setLocalRepository(artifactRepository);


        ProjectBuilder projectBuilder = this.lookup(ProjectBuilder.class);
        MavenProject project = projectBuilder.build(pom, buildingRequest).getProject();

        BuildMojo mojo = (BuildMojo)  lookupConfiguredMojo(project, "build");
        KieMavenPluginContext kieMavenPluginContext = getKieMavenPluginContext(mojo);
        assertNotNull(kieMavenPluginContext);
        GeneratePMMLModelExecutor.generatePMMLModel(kieMavenPluginContext);
        System.out.println("done");
    }


    private Settings getMavenSettings()
            throws ComponentLookupException,
            IOException,
            XmlPullParserException {
        org.apache.maven.settings.MavenSettingsBuilder mavenSettingsBuilder
                = (org.apache.maven.settings.MavenSettingsBuilder)
                getContainer().lookup(
                        org.apache.maven.settings.MavenSettingsBuilder.ROLE);
        return mavenSettingsBuilder.buildSettings();
    }

    /**
     * This is ugly but there seems to be no other way to accomplish it. The
     * artifact that the mojo finds on its own will not resolve to a jar file
     * on its own in the test harness. So we use aether to resolve it, by
     * cloning the maven default artifact into an aether artifact and feeding
     * an artifact request to the repo system obtained by the aether service
     * locator.
     */
    private void resolveConfigurationFromRepo(DefaultRepositorySystemSession repositorySystemSession,
                                              MavenProject project,
                                              BuildMojo mojo)
            throws ArtifactResolutionException, MojoExecutionException {
        org.apache.maven.artifact.Artifact defaultArtifact =
                mojo.getProject().getArtifact();
        Artifact artifact = new DefaultArtifact(
                defaultArtifact.getGroupId(),
                defaultArtifact.getArtifactId(),
                null,
                defaultArtifact.getType(),
                defaultArtifact.getVersion());
        List<RemoteRepository> remoteArtifactRepositories =
                project.getRemoteProjectRepositories();
        DefaultServiceLocator locator =
                MavenRepositorySystemUtils.newServiceLocator();
        locator.addService(RepositoryConnectorFactory.class,
                           BasicRepositoryConnectorFactory.class);
        locator.addService(TransporterFactory.class,
                           FileTransporterFactory.class);
        locator.addService(TransporterFactory.class,
                           HttpTransporterFactory.class);
        RepositorySystem repositorySystem = locator.getService(
                RepositorySystem.class);
        ArtifactRequest artifactRequest = new ArtifactRequest();
        artifactRequest.setArtifact(artifact);
        artifactRequest.setRepositories(remoteArtifactRepositories);
        ArtifactResult result = repositorySystem.resolveArtifact(
                repositorySystemSession, artifactRequest);
        defaultArtifact.setFile(result.getArtifact().getFile());
//        testMojo.getLog().debug("Resolved artifact " + artifact + " to " +
//                                        result.getArtifact().getFile() + " from "
//                                        + result.getRepository());
    }

//    /**
//     * Need manual copy of resources because only parts of the maven lifecycle
//     * happen automatically with this test harness.
//     */
//    private void copyTestProjectResourcesToTarget(PlexusContainer container,
//                                                  MavenProject project,
//                                                  MavenSession session)
//            throws ComponentLookupException, MojoExecutionException {
//        Optional<Dependency> resourcesPluginDepOpt =
//                project.getDependencies().stream()
//                        .filter(d -> Objects.equals(d.getArtifactId(),
//                                                    MAVEN_RESOURCES_ARTIFACT_ID))
//                        .findFirst();
//        // don't want to define the version here so we read it from what we have
//        if (!resourcesPluginDepOpt.isPresent()) {
//            throw new MojoExecutionException("Require " +
//                                                     MAVEN_RESOURCES_ARTIFACT_ID);
//        }
//        Plugin resourcePlugin = MojoExecutor.plugin(
//                MojoExecutor.groupId(MAVEN_PLUGINS_GROUP_ID),
//                MojoExecutor.artifactId(MAVEN_RESOURCES_ARTIFACT_ID),
//                MojoExecutor.version(resourcesPluginDepOpt.get().getVersion()));
//        MojoExecutor.executeMojo(resourcePlugin,
//                                 MojoExecutor.goal("resources"),
//                                 MojoExecutor.configuration(),
//                                 MojoExecutor.executionEnvironment(
//                                         project, session,
//                                         container.lookup(BuildPluginManager.class)));
//    }
}