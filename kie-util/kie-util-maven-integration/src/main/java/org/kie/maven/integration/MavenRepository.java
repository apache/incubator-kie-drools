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
package org.kie.maven.integration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.maven.model.DeploymentRepository;
import org.apache.maven.model.DistributionManagement;
import org.apache.maven.project.MavenProject;
import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.apache.maven.settings.Server;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.collection.CollectResult;
import org.eclipse.aether.collection.DependencyCollectionException;
import org.eclipse.aether.deployment.DeployRequest;
import org.eclipse.aether.deployment.DeploymentException;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.graph.DependencyVisitor;
import org.eclipse.aether.installation.InstallRequest;
import org.eclipse.aether.installation.InstallationException;
import org.eclipse.aether.repository.Authentication;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.LocalRepositoryManager;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.repository.RepositoryPolicy;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.resolution.VersionRangeRequest;
import org.eclipse.aether.resolution.VersionRangeResolutionException;
import org.eclipse.aether.resolution.VersionRangeResult;
import org.eclipse.aether.util.artifact.SubArtifact;
import org.eclipse.aether.version.Version;
import org.kie.api.builder.ReleaseId;
import org.kie.maven.integration.embedder.MavenEmbedder;
import org.kie.maven.integration.embedder.MavenProjectLoader;
import org.kie.maven.integration.embedder.MavenSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.maven.integration.embedder.MavenProjectLoader.parseMavenPom;

public class MavenRepository {

    private static final String SESSION_CHECKS = "updateCheckManager.checks";

    private static final Logger log = LoggerFactory.getLogger( MavenRepository.class );

    public static MavenRepository defaultMavenRepository;

    private final Aether aether;
    private final Collection<RemoteRepository> remoteRepositoriesForRequest;

    protected MavenRepository( Aether aether ) {
        this.aether = aether;
        remoteRepositoriesForRequest = initRemoteRepositoriesForRequest();
    }

    protected MavenRepositoryConfiguration getMavenRepositoryConfiguration() {
        return MavenSettings.getMavenRepositoryConfiguration();
    }

    Collection<RemoteRepository> getRemoteRepositoriesForRequest() {
        return remoteRepositoriesForRequest;
    }

    public static synchronized MavenRepository getMavenRepository() {
        if ( defaultMavenRepository == null ) {
            Aether defaultAether = Aether.getAether();
            defaultMavenRepository = new MavenRepository( defaultAether );
        }
        return defaultMavenRepository;
    }

    private Collection<RemoteRepository> initRemoteRepositoriesForRequest() {
        final MavenRepositoryConfiguration repositoryUtils = getMavenRepositoryConfiguration();
        Collection<RemoteRepository> remoteRepos = new HashSet<>();
        remoteRepos.addAll( repositoryUtils.getRemoteRepositoriesForRequest() );

        for ( RemoteRepository repo : aether.getRepositories() ) {
            remoteRepos.add( repositoryUtils.resolveMirroredRepo( repo ) );
        }
        return remoteRepos;
    }

    public static MavenRepository getMavenRepository( MavenProject mavenProject ) {
        return new MavenRepository( new Aether( mavenProject ) );
    }

    public List<DependencyDescriptor> getArtifactDependecies( String artifactName ) {
        Artifact artifact = new DefaultArtifact( artifactName );
        CollectRequest collectRequest = new CollectRequest();
        Dependency root = new Dependency( artifact, "" );
        collectRequest.setRoot( root );
        for ( RemoteRepository repo : remoteRepositoriesForRequest ) {
            collectRequest.addRepository( repo );
        }
        CollectResult collectResult;
        try {
            collectResult = aether.getSystem().collectDependencies( aether.getSession(), collectRequest );
        } catch ( DependencyCollectionException e ) {
            throw new RuntimeException( e );
        }
        CollectDependencyVisitor visitor = new CollectDependencyVisitor();
        collectResult.getRoot().accept( visitor );

        List<DependencyDescriptor> descriptors = new ArrayList<>();
        for ( DependencyNode node : visitor.getDependencies() ) {
            // skip root to not add artifact as dependency
            if ( node.getDependency().equals( root ) ) {
                continue;
            }
            descriptors.add( new DependencyDescriptor( node.getDependency().getArtifact() ) );
        }
        return descriptors;
    }

    public Artifact resolveArtifact( ReleaseId releaseId ) {
        String artifactName = releaseId.toString();
        if ( DependencyDescriptor.isRangedVersion( releaseId.getVersion() ) ) {
            Version v = resolveVersion( artifactName );
            if ( v == null ) {
                return null;
            }
            artifactName = releaseId.getGroupId() + ":" + releaseId.getArtifactId() + ":" + v;
        }
        return resolveArtifact( artifactName );
    }

    public Artifact resolveArtifact( String artifactName ) {
        return resolveArtifact( artifactName, true );
    }

    public Artifact resolveArtifact( String artifactName,
                                     boolean logUnresolvedArtifact ) {
        Artifact artifact = new DefaultArtifact( artifactName );
        ArtifactRequest artifactRequest = new ArtifactRequest();
        artifactRequest.setArtifact( artifact );
        for ( RemoteRepository repo : remoteRepositoriesForRequest ) {
            artifactRequest.addRepository( repo );
        }

        RepositorySystemSession session = aether.getSession();
        Object sessionChecks = null;
        boolean isSnapshot = artifactName.endsWith( "-SNAPSHOT" );
        if (artifactName.endsWith( "-SNAPSHOT" )) {
            // ensure to always update snapshots
            sessionChecks = session.getData().get( SESSION_CHECKS );
            session.getData().set( SESSION_CHECKS, null );
        }

        try {
            ArtifactResult artifactResult = aether.getSystem().resolveArtifact( session, artifactRequest );
            return artifactResult.getArtifact();
        } catch ( ArtifactResolutionException e ) {
            if ( logUnresolvedArtifact ) {
                if ( log.isDebugEnabled() ) {
                    log.debug( "Unable to resolve artifact: " + artifactName, e );
                } else {
                    log.warn( "Unable to resolve artifact: " + artifactName );
                }
            }
            return null;
        } finally {
            if (sessionChecks != null) {
                session.getData().set( SESSION_CHECKS, sessionChecks );
            }
        }
    }

    public Version resolveVersion( String artifactName ) {
        Artifact artifact = new DefaultArtifact( artifactName );
        VersionRangeRequest versionRequest = new VersionRangeRequest();
        versionRequest.setArtifact( artifact );
        for ( RemoteRepository repo : remoteRepositoriesForRequest ) {
            versionRequest.addRepository( repo );
        }
        try {
            VersionRangeResult versionRangeResult = aether.getSystem().resolveVersionRange( aether.getSession(), versionRequest );
            return versionRangeResult.getHighestVersion();
        } catch ( VersionRangeResolutionException e ) {
            if ( log.isDebugEnabled() ) {
                log.debug( "Unable to resolve version range for artifact: " + artifactName, e );
            } else {
                log.warn( "Unable to resolve version range for artifact: " + artifactName );
            }
            return null;
        }
    }

    protected RemoteRepository getRemoteRepositoryFromDistributionManagement( File pomfile ) {
        MavenProject mavenProject = parseMavenPom( pomfile );
        DistributionManagement distMan = mavenProject.getDistributionManagement();
        if (distMan == null) {
            return null;
        }
        DeploymentRepository deployRepo = distMan.getSnapshotRepository() != null && mavenProject.getVersion().endsWith( "SNAPSHOT" ) ?
                                          distMan.getSnapshotRepository() :
                                          distMan.getRepository();
        if (deployRepo == null) {
            return null;
        }

        RemoteRepository.Builder remoteRepoBuilder = new RemoteRepository.Builder( deployRepo.getId(), deployRepo.getLayout(), deployRepo.getUrl() )
                .setSnapshotPolicy( new RepositoryPolicy( true,
                                                          RepositoryPolicy.UPDATE_POLICY_DAILY,
                                                          RepositoryPolicy.CHECKSUM_POLICY_WARN ) )
                .setReleasePolicy( new RepositoryPolicy( true,
                                                         RepositoryPolicy.UPDATE_POLICY_ALWAYS,
                                                         RepositoryPolicy.CHECKSUM_POLICY_WARN ) );

        Server server = MavenSettings.getSettings().getServer( deployRepo.getId() );
        if ( server != null ) {
            MavenEmbedder embedder = MavenProjectLoader.newMavenEmbedder( false );
            try {
                Authentication authentication = embedder.getMavenSession().getRepositorySession()
                                                        .getAuthenticationSelector()
                                                        .getAuthentication( remoteRepoBuilder.build() );
                remoteRepoBuilder.setAuthentication( authentication );
            } finally {
                embedder.dispose();
            }
        }

        return remoteRepoBuilder.build();
    }

    /**
     * Deploys a jar on a remote repository.
     *
     * @param repository The remote repository where the kjar will be deployed
     * @param releaseId The releaseId with which the deployment will be made
     * @param jar The jar to be deployed
     * @param pomfile The pom file to be deployed together with the kjar
     */
    public void deployArtifact( RemoteRepository repository,
                                ReleaseId releaseId,
                                File jar,
                                File pomfile ) {
        Artifact jarArtifact = new DefaultArtifact( releaseId.getGroupId(), releaseId.getArtifactId(), "jar", releaseId.getVersion() );
        jarArtifact = jarArtifact.setFile( jar );

        Artifact pomArtifact = new SubArtifact( jarArtifact, "", "pom" );
        pomArtifact = pomArtifact.setFile( pomfile );

        DeployRequest deployRequest = new DeployRequest();
        deployRequest
                .addArtifact( jarArtifact )
                .addArtifact( pomArtifact )
                .setRepository( repository );

        try {
            aether.getSystem().deploy( aether.getSession(), deployRequest );
        } catch ( DeploymentException e ) {
            throw new RuntimeException( e );
        }
    }

    protected File bytesToFile(ReleaseId releaseId, byte[] bytes, String extension ) {
        File file = new File( System.getProperty( "java.io.tmpdir" ), toFileName( releaseId, null ) + extension );
        try (FileOutputStream fos = new FileOutputStream( file )) {
            fos.write( bytes );
            fos.flush();
        } catch ( IOException e ) {
            log.error("Error while converting bytes to file for releaseId: {} and extensions {}.", releaseId, extension);
            throw new RuntimeException( e );
        }
        return file;
    }


    /**
     * Installs the given jar into the local repository.
     *
     * @param releaseId The releaseId with which the kjar will be installed
     * @param jarContent A byte array containing the kjar to be installed
     * @param pomContent A byte array containing the pom file to be installed together with the kjar
     */
    public void installArtifact( ReleaseId releaseId,
                                 byte[] jarContent,
                                 byte[] pomContent ) {
        File jarFile = bytesToFile( releaseId, jarContent, ".jar" );
        File pomFile = bytesToFile( releaseId, pomContent, ".pom" );
        installArtifact( releaseId, jarFile, pomFile );
    }

    /**
     * Installs the given jar into the local repository.
     *
     * @param releaseId The releaseId with which the kjar will be installed
     * @param jar The jar to be installed
     * @param pomfile The pom file to be installed together with the kjar
     */
    public void installArtifact( ReleaseId releaseId,
                                 File jar,
                                 File pomfile ) {
        Artifact jarArtifact = new DefaultArtifact( releaseId.getGroupId(), releaseId.getArtifactId(), "jar", releaseId.getVersion() );
        jarArtifact = jarArtifact.setFile( jar );

        Artifact pomArtifact = new SubArtifact( jarArtifact, "", "pom" );
        pomArtifact = pomArtifact.setFile( pomfile );

        InstallRequest installRequest = new InstallRequest();
        installRequest
                .addArtifact( jarArtifact )
                .addArtifact( pomArtifact );

        try {
            aether.getSystem().install( aether.getSession(), installRequest );
        } catch (InstallationException e) {
            throw new RuntimeException( e );
        }
    }

    public void deployPomArtifact( String groupId,
                                   String artifactId,
                                   String version,
                                   File pomfile ) {
        Artifact pomArtifact = new DefaultArtifact( groupId, artifactId, "pom", version );
        pomArtifact = pomArtifact.setFile( pomfile );

        DeployRequest deployRequest = new DeployRequest();
        deployRequest
                .addArtifact( pomArtifact )
                .setRepository( aether.getLocalRepository() );

        try {
            aether.getSystem().deploy( aether.getSession(), deployRequest );
        } catch ( DeploymentException e ) {
            throw new RuntimeException( e );
        }
    }

    private static class CollectDependencyVisitor implements DependencyVisitor {

        private final List<DependencyNode> dependencies = new ArrayList<>();

        public boolean visitEnter( DependencyNode node ) {
            dependencies.add( node );
            return true;
        }

        public boolean visitLeave( DependencyNode node ) {
            return true;
        }

        public List<DependencyNode> getDependencies() {
            return dependencies;
        }
    }

    public static String toFileName( ReleaseId releaseId,
                                     String classifier ) {
        if ( classifier != null ) {
            return releaseId.getArtifactId() + "-" + releaseId.getVersion() + "-" + classifier;
        }

        return releaseId.getArtifactId() + "-" + releaseId.getVersion();
    }
    
    /**
     * Utility method specifically suggested for testing purposes only.
     */
    public void removeLocalArtifact(ReleaseId releaseId) {
        // Taken by analogy of build-helper-maven-plugin
        Artifact artifact = new DefaultArtifact(releaseId.getGroupId(), releaseId.getArtifactId(), null, releaseId.getVersion());
        
        LocalRepository localRepo = new LocalRepository( getMavenRepositoryConfiguration().getLocalRepository() );
        
        DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession();
        LocalRepositoryManager localRepositoryManager = aether.getSystem().newLocalRepositoryManager( session, localRepo );
        session.setLocalRepositoryManager( localRepositoryManager );
        session.setOffline( true );
        
        String pathForLocalArtifact = localRepositoryManager.getPathForLocalArtifact(artifact);
        
        File localArtifactDir = new File( localRepo.getBasedir(), pathForLocalArtifact )    // File .jar
                                .getParentFile()                                            // Directory of specific version, corresponding in ReleaseId
                                ;

        if (!localArtifactDir.exists()) {
            log.warn("The expected local maven repo dir for {} does not exist {}, nothing to delete.", releaseId, localArtifactDir);
            return;
        }
        log.info("Erasing directory from local maven repository {}", localArtifactDir);
        try {
            FileUtils.deleteDirectory(localArtifactDir);
        } catch (Exception e) {
            log.error("Error while trying to erase directory from local maven repository {}", localArtifactDir);
        }
    }
}
