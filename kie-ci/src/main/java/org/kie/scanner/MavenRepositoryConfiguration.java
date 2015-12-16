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

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.ArtifactRepositoryPolicy;
import org.apache.maven.artifact.repository.Authentication;
import org.apache.maven.artifact.repository.MavenArtifactRepository;
import org.apache.maven.artifact.repository.layout.ArtifactRepositoryLayout;
import org.apache.maven.artifact.repository.layout.DefaultRepositoryLayout;
import org.apache.maven.settings.Mirror;
import org.apache.maven.settings.Profile;
import org.apache.maven.settings.Repository;
import org.apache.maven.settings.RepositoryPolicy;
import org.apache.maven.settings.Server;
import org.apache.maven.settings.Settings;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.util.repository.AuthenticationBuilder;

import java.util.Collection;
import java.util.HashSet;

public class MavenRepositoryConfiguration {

    private final Settings settings;
    private final Collection<RemoteRepository> extraRepositories;
    private final Collection<RemoteRepository> remoteRepositoriesForRequest;
    private final Collection<ArtifactRepository> artifactRepositoriesForRequest;

    public MavenRepositoryConfiguration( Settings settings ) {
        this.settings = settings;
        this.extraRepositories = initExtraRepositories();
        this.remoteRepositoriesForRequest = initRemoteRepositoriesForRequest();
        this.artifactRepositoriesForRequest = initArtifactRepositories();
    }

    public Collection<RemoteRepository> getExtraRepositories() {
        return extraRepositories;
    }

    public Collection<RemoteRepository> getRemoteRepositoriesForRequest() {
        return remoteRepositoriesForRequest;
    }

    public Collection<ArtifactRepository> getArtifactRepositoriesForRequest() {
        return artifactRepositoriesForRequest;
    }

    private Collection<RemoteRepository> initExtraRepositories() {
        Collection<RemoteRepository> extraRepositories = new HashSet<RemoteRepository>();
        for ( Profile profile : settings.getProfiles() ) {
            if ( isProfileActive( profile ) ) {
                for ( Repository repository : profile.getRepositories() ) {
                    extraRepositories.add( toRemoteRepositoryBuilder( settings,
                                                                      repository ).build() );
                }
                for ( Repository repository : profile.getPluginRepositories() ) {
                    extraRepositories.add( toRemoteRepositoryBuilder( settings,
                                                                      repository ).build() );
                }
            }
        }
        return extraRepositories;
    }

    private Collection<RemoteRepository> initRemoteRepositoriesForRequest() {
        Collection<RemoteRepository> remoteRepos = new HashSet<RemoteRepository>();
        for ( RemoteRepository repo : extraRepositories ) {
            remoteRepos.add( resolveMirroredRepo( repo ) );
        }
        return remoteRepos;
    }

    private Collection<ArtifactRepository> initArtifactRepositories() {
        Collection<ArtifactRepository> artifactRepos = new HashSet<ArtifactRepository>();
        for ( RemoteRepository remoteRepository : remoteRepositoriesForRequest ) {
            artifactRepos.add( toArtifactRepository( remoteRepository ) );
        }
        return artifactRepos;
    }

    public RemoteRepository resolveMirroredRepo( RemoteRepository repo ) {
        for ( Mirror mirror : settings.getMirrors() ) {
            if ( isMirror( repo, mirror.getMirrorOf() ) ) {
                return toRemoteRepositoryBuilder( settings,
                                                  mirror.getId(),
                                                  mirror.getLayout(),
                                                  mirror.getUrl() ).build();
            }
        }
        return repo;
    }

    private boolean isMirror( RemoteRepository repo,
                              String mirrorOf ) {
        return mirrorOf.equals( "*" ) ||
                ( mirrorOf.equals( "external:*" ) && !repo.getUrl().startsWith( "file:" ) ) ||
                ( mirrorOf.contains( "external:*" ) && !repo.getUrl().startsWith( "file:" ) && !mirrorOf.contains( "!" + repo.getId() ) ) ||
                ( mirrorOf.startsWith( "*" ) && !mirrorOf.contains( "!" + repo.getId() ) ) ||
                ( !mirrorOf.startsWith( "*" ) && !mirrorOf.contains( "external:*" ) && mirrorOf.contains( repo.getId() ) );
    }

    private boolean isProfileActive( Profile profile ) {
        return settings.getActiveProfiles().contains( profile.getId() ) ||
                ( profile.getActivation() != null && profile.getActivation().isActiveByDefault() );
    }

    private static RemoteRepository.Builder toRemoteRepositoryBuilder( Settings settings,
                                                                       Repository repository ) {
        RemoteRepository.Builder remoteBuilder = toRemoteRepositoryBuilder( settings,
                                                                            repository.getId(),
                                                                            repository.getLayout(),
                                                                            repository.getUrl() );
        setPolicy( remoteBuilder, repository.getSnapshots(),
                   true );
        setPolicy( remoteBuilder, repository.getReleases(),
                   false );
        return remoteBuilder;
    }

    private static RemoteRepository.Builder toRemoteRepositoryBuilder( Settings settings,
                                                                       String id,
                                                                       String layout,
                                                                       String url ) {
        RemoteRepository.Builder remoteBuilder = new RemoteRepository.Builder( id,
                                                                               layout,
                                                                               url );
        Server server = settings.getServer( id );
        if ( server != null ) {
            remoteBuilder.setAuthentication( new AuthenticationBuilder().addUsername( server.getUsername() )
                                                     .addPassword( server.getPassword() )
                                                     .build() );
        }
        return remoteBuilder;

    }

    private static void setPolicy( RemoteRepository.Builder builder,
                                   RepositoryPolicy policy,
                                   boolean snapshot ) {
        if ( policy != null ) {
            org.eclipse.aether.repository.RepositoryPolicy repoPolicy =
                    new org.eclipse.aether.repository.RepositoryPolicy( policy.isEnabled(),
                                                                        policy.getUpdatePolicy(),
                                                                        policy.getChecksumPolicy() );
            if ( snapshot ) {
                builder.setSnapshotPolicy( repoPolicy );
            } else {
                builder.setReleasePolicy( repoPolicy );
            }
        }
    }

    private ArtifactRepository toArtifactRepository( RemoteRepository remoteRepository ) {
        final String id = remoteRepository.getId();
        final String url = remoteRepository.getUrl();
        final ArtifactRepositoryLayout layout = new DefaultRepositoryLayout();
        ArtifactRepositoryPolicy snapshots = new ArtifactRepositoryPolicy();
        ArtifactRepositoryPolicy releases = new ArtifactRepositoryPolicy();
        if ( remoteRepository.getPolicy( true ) != null ) {
            snapshots = new ArtifactRepositoryPolicy( remoteRepository.getPolicy( true ).isEnabled(),
                                                      remoteRepository.getPolicy( true ).getUpdatePolicy(),
                                                      remoteRepository.getPolicy( true ).getChecksumPolicy() );
        }
        if ( remoteRepository.getPolicy( false ) != null ) {
            releases = new ArtifactRepositoryPolicy( remoteRepository.getPolicy( false ).isEnabled(),
                                                     remoteRepository.getPolicy( false ).getUpdatePolicy(),
                                                     remoteRepository.getPolicy( false ).getChecksumPolicy() );
        }
        final ArtifactRepository artifactRepository = new MavenArtifactRepository( id,
                                                                                   url,
                                                                                   layout,
                                                                                   snapshots,
                                                                                   releases );

        final Server server = settings.getServer( id );
        if ( server != null ) {
            artifactRepository.setAuthentication( new Authentication( server.getUsername(),
                                                                      server.getPassword() ) );
        }
        return artifactRepository;
    }
}
