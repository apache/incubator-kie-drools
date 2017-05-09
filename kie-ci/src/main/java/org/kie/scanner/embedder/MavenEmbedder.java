/*
 * Copyright 2001-2005 The Apache Software Foundation.
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
package org.kie.scanner.embedder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.maven.DefaultMaven;
import org.apache.maven.Maven;
import org.apache.maven.artifact.InvalidRepositoryException;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.execution.DefaultMavenExecutionResult;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionRequestPopulationException;
import org.apache.maven.execution.MavenExecutionRequestPopulator;
import org.apache.maven.execution.MavenExecutionResult;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Profile;
import org.apache.maven.model.building.ModelSource;
import org.apache.maven.plugin.LegacySupport;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.project.ProjectBuildingRequest;
import org.apache.maven.project.ProjectBuildingResult;
import org.apache.maven.repository.RepositorySystem;
import org.apache.maven.settings.Settings;
import org.apache.maven.settings.building.DefaultSettingsBuildingRequest;
import org.apache.maven.settings.building.FileSettingsSource;
import org.apache.maven.settings.building.SettingsBuilder;
import org.apache.maven.settings.building.SettingsBuildingException;
import org.apache.maven.settings.building.SettingsBuildingRequest;
import org.apache.maven.settings.building.SettingsSource;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.util.Os;
import org.eclipse.aether.RepositorySystemSession;
import org.kie.scanner.MavenRepositoryConfiguration;
import org.slf4j.LoggerFactory;

import static org.drools.core.util.IoUtils.*;

public class MavenEmbedder {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger( MavenEmbedder.class );

    public static final File DEFAULT_GLOBAL_SETTINGS_FILE =
            new File( System.getProperty( "maven.home", System.getProperty( "user.dir", "" ) ), "conf/settings.xml" );

    private final MavenRequest mavenRequest;
    private final ComponentProvider componentProvider;

    private MavenExecutionRequest mavenExecutionRequest;

    private MavenSession mavenSession;

    public MavenEmbedder( MavenRequest mavenRequest ) throws MavenEmbedderException {
        this( Thread.currentThread().getContextClassLoader(), null, mavenRequest );
    }

    public MavenEmbedder( ClassLoader mavenClassLoader,
                          ClassLoader parent,
                          MavenRequest mavenRequest ) throws MavenEmbedderException {
        this( mavenRequest, MavenEmbedderUtils.buildComponentProvider( mavenClassLoader, parent, mavenRequest ) );
    }

    private MavenEmbedder( MavenRequest mavenRequest,
                           ComponentProvider componentProvider ) throws MavenEmbedderException {
        this.mavenRequest = mavenRequest;
        this.componentProvider = componentProvider;

        try {
            this.mavenExecutionRequest = this.buildMavenExecutionRequest( mavenRequest );

            RepositorySystemSession rss = ( (DefaultMaven) componentProvider.lookup( Maven.class ) ).newRepositorySession( mavenExecutionRequest );

            mavenSession = new MavenSession( componentProvider.getPlexusContainer(), rss, mavenExecutionRequest, new DefaultMavenExecutionResult() );

            componentProvider.lookup( LegacySupport.class ).setSession( mavenSession );
        } catch ( MavenEmbedderException e ) {
            log.error( "Unable to build MavenEmbedder", e );
            throw e;
        } catch ( ComponentLookupException e ) {
            log.error( "Unable to build MavenEmbedder", e );
            throw new MavenEmbedderException( e.getMessage(), e );
        }
    }

    protected MavenExecutionRequest buildMavenExecutionRequest( MavenRequest mavenRequest )
            throws MavenEmbedderException, ComponentLookupException {
        MavenExecutionRequest mavenExecutionRequest = new DefaultMavenExecutionRequest();

        if ( mavenRequest.getGlobalSettingsFile() != null ) {
            mavenExecutionRequest.setGlobalSettingsFile( new File( mavenRequest.getGlobalSettingsFile() ) );
        }

        SettingsSource userSettings = mavenRequest.getUserSettingsSource();
        if ( userSettings != null ) {
            if ( userSettings instanceof FileSettingsSource ) {
                mavenExecutionRequest.setUserSettingsFile( ( (FileSettingsSource) userSettings ).getSettingsFile() );
            } else {
                try {
                    mavenExecutionRequest.setUserSettingsFile( copyInTempFile( userSettings.getInputStream(), "xml" ) );
                } catch ( IOException ioe ) {
                    log.warn( "Unable to use maven settings defined in " + userSettings, ioe );
                }
            }
        }

        try {
            componentProvider.lookup( MavenExecutionRequestPopulator.class ).populateFromSettings( mavenExecutionRequest, getSettings() );
            componentProvider.lookup( MavenExecutionRequestPopulator.class ).populateDefaults( mavenExecutionRequest );
        } catch ( MavenExecutionRequestPopulationException e ) {
            throw new MavenEmbedderException( e.getMessage(), e );
        }

        ArtifactRepository localRepository = getLocalRepository();
        mavenExecutionRequest.setLocalRepository( localRepository );
        mavenExecutionRequest.setLocalRepositoryPath( localRepository.getBasedir() );
        mavenExecutionRequest.setOffline( mavenRequest.isOffline() );

        mavenExecutionRequest.setUpdateSnapshots( mavenRequest.isUpdateSnapshots() );

        // TODO check null and create a console one ?
        mavenExecutionRequest.setTransferListener( mavenRequest.getTransferListener() );

        mavenExecutionRequest.setCacheNotFound( mavenRequest.isCacheNotFound() );
        mavenExecutionRequest.setCacheTransferError( true );

        mavenExecutionRequest.setUserProperties( mavenRequest.getUserProperties() );
        mavenExecutionRequest.getSystemProperties().putAll( System.getProperties() );
        if ( mavenRequest.getSystemProperties() != null ) {
            mavenExecutionRequest.getSystemProperties().putAll( mavenRequest.getSystemProperties() );
        }
        mavenExecutionRequest.getSystemProperties().putAll( getEnvVars() );

        if ( mavenRequest.getProfiles() != null && !mavenRequest.getProfiles().isEmpty() ) {
            for ( String id : mavenRequest.getProfiles() ) {
                Profile p = new Profile();
                p.setId( id );
                p.setSource( "cli" );
                mavenExecutionRequest.addProfile( p );
                mavenExecutionRequest.addActiveProfile( id );
            }
        }

        MavenRepositoryConfiguration mavenRepoConf = getMavenRepositoryConfiguration();

        //DROOLS-899: Copy repositories defined in settings to execution request
        for ( ArtifactRepository artifactRepository : mavenRepoConf.getArtifactRepositoriesForRequest() ) {
            mavenExecutionRequest.addRemoteRepository( artifactRepository );
        }

        mavenExecutionRequest.setProxies( mavenRepoConf.getProxies() );

        mavenExecutionRequest.setLoggingLevel( mavenRequest.getLoggingLevel() );

        componentProvider.lookup( Logger.class ).setThreshold( mavenRequest.getLoggingLevel() );

        mavenExecutionRequest.setExecutionListener( mavenRequest.getExecutionListener() )
                .setInteractiveMode( mavenRequest.isInteractive() )
                .setGlobalChecksumPolicy( mavenRequest.getGlobalChecksumPolicy() )
                .setGoals( mavenRequest.getGoals() );

        if ( mavenRequest.getPom() != null ) {
            mavenExecutionRequest.setPom( new File( mavenRequest.getPom() ) );
        }

        if ( mavenRequest.getWorkspaceReader() != null ) {
            mavenExecutionRequest.setWorkspaceReader( mavenRequest.getWorkspaceReader() );
        }

        return mavenExecutionRequest;
    }

    protected MavenRepositoryConfiguration getMavenRepositoryConfiguration() {
        return MavenSettings.getMavenRepositoryConfiguration();
    }

    private Properties getEnvVars() {
        Properties envVars = new Properties();
        boolean caseSensitive = !Os.isFamily( Os.FAMILY_WINDOWS );
        for ( Entry<String, String> entry : System.getenv().entrySet() ) {
            String key = "env." + ( caseSensitive ? entry.getKey() : entry.getKey().toUpperCase( Locale.ENGLISH ) );
            envVars.setProperty( key, entry.getValue() );
        }
        return envVars;
    }

    public Settings getSettings() throws MavenEmbedderException, ComponentLookupException {
        SettingsBuildingRequest settingsBuildingRequest = new DefaultSettingsBuildingRequest();
        if ( this.mavenRequest.getGlobalSettingsFile() != null ) {
            settingsBuildingRequest.setGlobalSettingsFile( new File( this.mavenRequest.getGlobalSettingsFile() ) );
        } else {
            settingsBuildingRequest.setGlobalSettingsFile( DEFAULT_GLOBAL_SETTINGS_FILE );
        }
        if ( this.mavenRequest.getUserSettingsSource() != null ) {
            settingsBuildingRequest.setUserSettingsSource( this.mavenRequest.getUserSettingsSource() );
        } else {
            SettingsSource userSettingsSource = MavenSettings.getUserSettingsSource();
            if ( userSettingsSource != null ) {
                settingsBuildingRequest.setUserSettingsSource( userSettingsSource );
            }
        }

        settingsBuildingRequest.setUserProperties( this.mavenRequest.getUserProperties() );
        settingsBuildingRequest.getSystemProperties().putAll( System.getProperties() );
        settingsBuildingRequest.getSystemProperties().putAll( this.mavenRequest.getSystemProperties() );
        settingsBuildingRequest.getSystemProperties().putAll( getEnvVars() );

        try {
            return componentProvider.lookup( SettingsBuilder.class ).build( settingsBuildingRequest ).getEffectiveSettings();
        } catch ( SettingsBuildingException e ) {
            throw new MavenEmbedderException( e.getMessage(), e );
        }
    }

    public ArtifactRepository getLocalRepository() throws ComponentLookupException {
        try {
            String localRepositoryPath = getLocalRepositoryPath();
            if ( localRepositoryPath != null ) {
                return componentProvider.lookup( RepositorySystem.class ).createLocalRepository( new File( localRepositoryPath ) );
            }
            return componentProvider.lookup( RepositorySystem.class ).createLocalRepository( RepositorySystem.defaultUserLocalRepository );
        } catch ( InvalidRepositoryException e ) {
            // never happened
            throw new IllegalStateException( e );
        }
    }

    public String getLocalRepositoryPath() {
        String path = null;

        try {
            Settings settings = getSettings();
            path = settings.getLocalRepository();
        } catch ( MavenEmbedderException e ) {
            // ignore
        } catch ( ComponentLookupException e ) {
            // ignore
        }

        if ( this.mavenRequest.getLocalRepositoryPath() != null ) {
            path = this.mavenRequest.getLocalRepositoryPath();
        }

        if ( path == null ) {
            path = RepositorySystem.defaultUserLocalRepository.getAbsolutePath();
        }
        return path;
    }

    // ----------------------------------------------------------------------
    // Project
    // ----------------------------------------------------------------------

    public MavenProject readProject( final InputStream mavenProjectStream ) throws ProjectBuildingException, MavenEmbedderException {
        ModelSource modelSource = new ModelSource() {
            @Override
            public InputStream getInputStream() throws IOException {
                return mavenProjectStream;
            }

            @Override
            public String getLocation() {
                return "";
            }
        };

        ClassLoader originalCl = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader( componentProvider.getSystemClassLoader() );
            ProjectBuilder projectBuilder = componentProvider.lookup( ProjectBuilder.class );
            // BZ-1007894: Check if added dependencies are resolvable.
            ProjectBuildingResult result = projectBuilder.build( modelSource, getProjectBuildingRequest() );
            if ( result != null && result.getDependencyResolutionResult() != null && !result.getDependencyResolutionResult().getCollectionErrors().isEmpty() ) {
                // A dependency resolution error has been produced. It can contains some error. Throw the first one to the client, so the user will fix every one sequentially.
                Exception depedencyResolutionException = result.getDependencyResolutionResult().getCollectionErrors().get( 0 );
                if ( depedencyResolutionException != null ) {
                    throw new MavenEmbedderException( depedencyResolutionException.getMessage(), depedencyResolutionException );
                }
            }
            return result.getProject();
        } catch ( ComponentLookupException e ) {
            throw new MavenEmbedderException( e.getMessage(), e );
        } finally {
            Thread.currentThread().setContextClassLoader( originalCl );
            try {
                mavenProjectStream.close();
            } catch ( IOException e ) {
            }
        }
    }

    public MavenProject readProject( File mavenProject ) throws ProjectBuildingException, MavenEmbedderException {
        List<MavenProject> projects = readProjects( mavenProject, false );
        return projects == null || projects.isEmpty() ? null : projects.get( 0 );
    }

    public List<MavenProject> readProjects( File mavenProject,
                                            boolean recursive ) throws ProjectBuildingException, MavenEmbedderException {
        ClassLoader originalCl = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader( componentProvider.getSystemClassLoader() );
            List<ProjectBuildingResult> results = buildProjects( mavenProject, recursive );
            List<MavenProject> projects = new ArrayList<MavenProject>( results.size() );
            for ( ProjectBuildingResult result : results ) {
                projects.add( result.getProject() );
            }
            return projects;
        } finally {
            Thread.currentThread().setContextClassLoader( originalCl );
        }
    }

    public List<ProjectBuildingResult> buildProjects( File mavenProject,
                                                      boolean recursive ) throws ProjectBuildingException, MavenEmbedderException {
        ClassLoader originalCl = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader( componentProvider.getSystemClassLoader() );
            ProjectBuilder projectBuilder = componentProvider.lookup( ProjectBuilder.class );
            return projectBuilder.build( Collections.singletonList( mavenProject ), recursive, getProjectBuildingRequest() );
        } catch ( ComponentLookupException e ) {
            throw new MavenEmbedderException( e.getMessage(), e );
        } finally {
            Thread.currentThread().setContextClassLoader( originalCl );
        }
    }

    private ProjectBuildingRequest getProjectBuildingRequest() throws ComponentLookupException {
        ProjectBuildingRequest projectBuildingRequest = this.mavenExecutionRequest.getProjectBuildingRequest();
        projectBuildingRequest.setValidationLevel( this.mavenRequest.getValidationLevel() );
        RepositorySystemSession repositorySystemSession = componentProvider.getRepositorySystemSession( mavenExecutionRequest );
        projectBuildingRequest.setRepositorySession( repositorySystemSession );
        projectBuildingRequest.setProcessPlugins( this.mavenRequest.isProcessPlugins() );
        projectBuildingRequest.setResolveDependencies( this.mavenRequest.isResolveDependencies() );
        return projectBuildingRequest;
    }

    public MavenSession getMavenSession() {
        return mavenSession;
    }

    public MavenExecutionRequest getMavenExecutionRequest() {
        return mavenExecutionRequest;
    }

    public void dispose() {
        PlexusContainer plexusContainer = componentProvider.getPlexusContainer();
        if ( plexusContainer != null ) {
            plexusContainer.dispose();
        }
    }

    public MavenExecutionResult execute( final MavenRequest mavenRequest )
            throws MavenEmbedderException {
        final ClassLoader originalCl = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader( componentProvider.getSystemClassLoader() );
            final Maven maven = componentProvider.lookup( Maven.class );
            return maven.execute( buildMavenExecutionRequest( mavenRequest ) );
        } catch ( final MavenEmbedderException e ) {
            log.error( "An MavenEmbedderException occurred during maven execution.", e );
            throw e;
        } catch ( final Throwable e ) {
            log.error( "An exception occurred during maven execution.", e );
            throw new MavenEmbedderException( e.getMessage(), e );
        } finally {
            Thread.currentThread().setContextClassLoader( originalCl );
        }
    }
}
