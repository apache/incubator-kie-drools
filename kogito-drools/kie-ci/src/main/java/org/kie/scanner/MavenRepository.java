/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.kie.scanner;

import org.apache.maven.project.MavenProject;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
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
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.resolution.VersionRangeRequest;
import org.eclipse.aether.resolution.VersionRangeResolutionException;
import org.eclipse.aether.resolution.VersionRangeResult;
import org.eclipse.aether.util.artifact.SubArtifact;
import org.eclipse.aether.version.Version;
import org.kie.api.builder.ReleaseId;
import org.kie.scanner.embedder.MavenSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import static org.kie.scanner.DependencyDescriptor.isRangedVersion;

public class MavenRepository {

    private static final Logger log = LoggerFactory.getLogger( MavenRepository.class );

    private static MavenRepository defaultMavenRepository;

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
        Collection<RemoteRepository> remoteRepos = new HashSet<RemoteRepository>();
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

        List<DependencyDescriptor> descriptors = new ArrayList<DependencyDescriptor>();
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
        if ( isRangedVersion( releaseId.getVersion() ) ) {
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
        try {
            ArtifactResult artifactResult = aether.getSystem().resolveArtifact( aether.getSession(), artifactRequest );
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

    public void deployArtifact( ReleaseId releaseId,
                                InternalKieModule kieModule,
                                File pomfile ) {
        File jarFile = bytesToFile( releaseId, kieModule.getBytes(), ".jar" );
        deployArtifact( releaseId, jarFile, pomfile );
    }

    public void deployArtifact( ReleaseId releaseId,
                                byte[] jarContent,
                                byte[] pomContent ) {
        File jarFile = bytesToFile( releaseId, jarContent, ".jar" );
        File pomFile = bytesToFile( releaseId, pomContent, ".pom" );
        deployArtifact( releaseId, jarFile, pomFile );
    }

    private File bytesToFile( ReleaseId releaseId, byte[] bytes, String extension ) {
        File file = new File( System.getProperty( "java.io.tmpdir" ), toFileName( releaseId, null ) + extension );
        try {
            FileOutputStream fos = new FileOutputStream( file );
            fos.write( bytes );
            fos.flush();
            fos.close();
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }
        return file;
    }

    public void deployArtifact( ReleaseId releaseId,
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
                .setRepository( aether.getLocalRepository() );

        try {
            aether.getSystem().deploy( aether.getSession(), deployRequest );
        } catch ( DeploymentException e ) {
            throw new RuntimeException( e );
        }
    }

    public void installArtifact( ReleaseId releaseId,
                                InternalKieModule kieModule,
                                File pomfile ) {
        File jarFile = bytesToFile( releaseId, kieModule.getBytes(), ".jar" );
        installArtifact( releaseId, jarFile, pomfile );
    }

    public void installArtifact( ReleaseId releaseId,
                                 byte[] jarContent,
                                 byte[] pomContent ) {
        File jarFile = bytesToFile( releaseId, jarContent, ".jar" );
        File pomFile = bytesToFile( releaseId, pomContent, ".pom" );
        installArtifact( releaseId, jarFile, pomFile );
    }

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

        private final List<DependencyNode> dependencies = new ArrayList<DependencyNode>();

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
}
