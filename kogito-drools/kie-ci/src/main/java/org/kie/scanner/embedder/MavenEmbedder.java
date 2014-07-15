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

import org.apache.maven.DefaultMaven;
import org.apache.maven.Maven;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.InvalidRepositoryException;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.ArtifactRepositoryPolicy;
import org.apache.maven.artifact.repository.layout.ArtifactRepositoryLayout;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
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
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.project.ProjectBuildingRequest;
import org.apache.maven.project.ProjectBuildingResult;
import org.apache.maven.repository.RepositorySystem;
import org.apache.maven.settings.Settings;
import org.apache.maven.settings.building.DefaultSettingsBuildingRequest;
import org.apache.maven.settings.building.SettingsBuilder;
import org.apache.maven.settings.building.SettingsBuildingException;
import org.apache.maven.settings.building.SettingsBuildingRequest;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.util.DirectoryScanner;
import org.codehaus.plexus.util.Os;
import org.eclipse.aether.RepositorySystemSession;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;


public class MavenEmbedder {

    public static final File DEFAULT_GLOBAL_SETTINGS_FILE =
            new File( System.getProperty( "maven.home", System.getProperty( "user.dir", "" ) ), "conf/settings.xml" );

    private final File mavenHome;
    
    private final PlexusContainer plexusContainer;
    
    private final MavenRequest mavenRequest;
    
    private MavenExecutionRequest mavenExecutionRequest;
    private final MavenSession mavenSession;

    public MavenEmbedder(File mavenHome, MavenRequest mavenRequest) throws MavenEmbedderException {
        this(mavenHome,mavenRequest,MavenEmbedderUtils.buildPlexusContainer(mavenHome, mavenRequest));
    }

    public MavenEmbedder(ClassLoader mavenClassLoader, ClassLoader parent, MavenRequest mavenRequest) throws MavenEmbedderException {
        this(null,mavenRequest,MavenEmbedderUtils.buildPlexusContainer(mavenClassLoader, parent, mavenRequest));
    }

    private MavenEmbedder(File mavenHome, MavenRequest mavenRequest, PlexusContainer plexusContainer)
        throws MavenEmbedderException
    {
        this.mavenHome = mavenHome;
        this.mavenRequest = mavenRequest;
        this.plexusContainer = plexusContainer;

        try {
            this.mavenExecutionRequest = this.buildMavenExecutionRequest(mavenRequest);

            RepositorySystemSession rss = ((DefaultMaven) lookup(Maven.class)).newRepositorySession(mavenExecutionRequest);
            
            mavenSession = new MavenSession( plexusContainer, rss, mavenExecutionRequest, new DefaultMavenExecutionResult() );
                        
            lookup(LegacySupport.class).setSession(mavenSession);
        } catch (MavenEmbedderException e) {
            throw new MavenEmbedderException(e.getMessage(), e);
        } catch (ComponentLookupException e) {
            throw new MavenEmbedderException(e.getMessage(), e);
        }
    }


    public MavenEmbedder(ClassLoader mavenClassLoader, MavenRequest mavenRequest) throws MavenEmbedderException {
        this(mavenClassLoader, null, mavenRequest);
    }


    public PlexusContainer getPlexusContainer() {
        return plexusContainer;
    }

    protected MavenExecutionRequest buildMavenExecutionRequest(MavenRequest mavenRequest)
        throws MavenEmbedderException, ComponentLookupException {
        MavenExecutionRequest mavenExecutionRequest = new DefaultMavenExecutionRequest();

        if ( mavenRequest.getGlobalSettingsFile() != null ) {
            mavenExecutionRequest.setGlobalSettingsFile( new File( mavenRequest.getGlobalSettingsFile() ) );
        }

        if ( mavenExecutionRequest.getUserSettingsFile() != null ) {
            mavenExecutionRequest.setUserSettingsFile( new File( mavenRequest.getUserSettingsFile() ) );
        }

        try {
            lookup( MavenExecutionRequestPopulator.class ).populateFromSettings( mavenExecutionRequest,
                                                                                 getSettings() );
            
            lookup( MavenExecutionRequestPopulator.class ).populateDefaults( mavenExecutionRequest );
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

        if ( this.mavenHome != null ) {
            mavenExecutionRequest.getSystemProperties().put( "maven.home", this.mavenHome.getAbsolutePath() );
        }
       
        if (mavenRequest.getProfiles() != null && !mavenRequest.getProfiles().isEmpty()) {
            for (String id : mavenRequest.getProfiles()) {
                Profile p = new Profile();
                p.setId( id );
                p.setSource( "cli" );
                mavenExecutionRequest.addProfile( p );
                mavenExecutionRequest.addActiveProfile( id );
            }
        }


        mavenExecutionRequest.setLoggingLevel( mavenRequest.getLoggingLevel() );

        lookup( Logger.class ).setThreshold( mavenRequest.getLoggingLevel() );

        mavenExecutionRequest.setExecutionListener( mavenRequest.getExecutionListener() )
            .setInteractiveMode( mavenRequest.isInteractive() )
            .setGlobalChecksumPolicy( mavenRequest.getGlobalChecksumPolicy() )
            .setGoals( mavenRequest.getGoals() );

        if ( mavenRequest.getPom() != null ) {
            mavenExecutionRequest.setPom( new File( mavenRequest.getPom() ) );
        }
        
        if (mavenRequest.getWorkspaceReader() != null) {
            mavenExecutionRequest.setWorkspaceReader( mavenRequest.getWorkspaceReader() );
        }

        return  mavenExecutionRequest;
    }

    private Properties getEnvVars( ) {
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
        if ( this.mavenRequest.getUserSettingsFile() != null ) {
            settingsBuildingRequest.setUserSettingsFile( new File( this.mavenRequest.getUserSettingsFile() ) );
        } else {
            File userSettingsFile = MavenSettings.getUserSettingsFile();
            if (userSettingsFile != null) {
                settingsBuildingRequest.setUserSettingsFile( userSettingsFile );
            }
        }

        settingsBuildingRequest.setUserProperties( this.mavenRequest.getUserProperties() );
        settingsBuildingRequest.getSystemProperties().putAll( System.getProperties() );
        settingsBuildingRequest.getSystemProperties().putAll( this.mavenRequest.getSystemProperties() );
        settingsBuildingRequest.getSystemProperties().putAll( getEnvVars() );

        try {
            return lookup( SettingsBuilder.class ).build( settingsBuildingRequest ).getEffectiveSettings();
        } catch ( SettingsBuildingException e ) {
            throw new MavenEmbedderException( e.getMessage(), e );
        }
    }

    public ArtifactRepository getLocalRepository() throws ComponentLookupException {
        try {
            String localRepositoryPath = getLocalRepositoryPath();
            if ( localRepositoryPath != null ) {
                return lookup( RepositorySystem.class ).createLocalRepository( new File( localRepositoryPath ) );
            }
            return lookup( RepositorySystem.class ).createLocalRepository( RepositorySystem.defaultUserLocalRepository );
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
            path =  this.mavenRequest.getLocalRepositoryPath();
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
            Thread.currentThread().setContextClassLoader( this.plexusContainer.getContainerRealm() );
            ProjectBuilder projectBuilder = lookup( ProjectBuilder.class );
            // BZ-1007894: Check if added dependencies are resolvable.
            ProjectBuildingResult result = projectBuilder.build( modelSource, getProjectBuildingRequest() );
            if (result != null && result.getDependencyResolutionResult() != null && !result.getDependencyResolutionResult().getCollectionErrors().isEmpty()) {
                // A dependency resolution error has been produced. It can contains some error. Throw the first one to the client, so the user will fix every one sequentially.
                Exception depedencyResolutionException = result.getDependencyResolutionResult().getCollectionErrors().get(0);
                if (depedencyResolutionException != null) throw new MavenEmbedderException(depedencyResolutionException.getMessage(), depedencyResolutionException);
            }
            return result.getProject();
        } catch(ComponentLookupException e) {
            throw new MavenEmbedderException(e.getMessage(), e);
        } finally {
            Thread.currentThread().setContextClassLoader( originalCl );
            try {
                mavenProjectStream.close();
            } catch (IOException e) { }
        }
    }

    public MavenProject readProject( File mavenProject ) throws ProjectBuildingException, MavenEmbedderException {
        List<MavenProject> projects = readProjects( mavenProject, false );
        return projects == null || projects.isEmpty() ? null : projects.get( 0 );
    }

    public List<MavenProject> readProjects( File mavenProject, boolean recursive ) throws ProjectBuildingException, MavenEmbedderException {
        ClassLoader originalCl = Thread.currentThread().getContextClassLoader();
        try {
            List<ProjectBuildingResult> results = buildProjects( mavenProject, recursive );
            List<MavenProject> projects = new ArrayList<MavenProject>(results.size());
            for (ProjectBuildingResult result : results) {
                projects.add( result.getProject() );
            }
            return projects;
        } finally {
            Thread.currentThread().setContextClassLoader( originalCl );
        }
    }

    public List<ProjectBuildingResult> buildProjects( File mavenProject, boolean recursive ) throws ProjectBuildingException, MavenEmbedderException {
        ClassLoader originalCl = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader( this.plexusContainer.getContainerRealm() );
            ProjectBuilder projectBuilder = lookup(ProjectBuilder.class);
            return projectBuilder.build( Arrays.asList(mavenProject), recursive, getProjectBuildingRequest() );
        } catch(ComponentLookupException e) {
            throw new MavenEmbedderException(e.getMessage(), e);
        } finally {
            Thread.currentThread().setContextClassLoader( originalCl );
        }
    }

    private ProjectBuildingRequest getProjectBuildingRequest() throws ComponentLookupException {
        ProjectBuildingRequest projectBuildingRequest = this.mavenExecutionRequest.getProjectBuildingRequest();
        projectBuildingRequest.setValidationLevel( this.mavenRequest.getValidationLevel() );
        RepositorySystemSession repositorySystemSession = buildRepositorySystemSession();
        projectBuildingRequest.setRepositorySession( repositorySystemSession );
        projectBuildingRequest.setProcessPlugins( this.mavenRequest.isProcessPlugins() );
        projectBuildingRequest.setResolveDependencies( this.mavenRequest.isResolveDependencies() );
        return projectBuildingRequest;
    }

    private RepositorySystemSession buildRepositorySystemSession() throws ComponentLookupException {
        DefaultMaven defaultMaven = (DefaultMaven) plexusContainer.lookup( Maven.class );
        return defaultMaven.newRepositorySession( mavenExecutionRequest );
    }

    public List<MavenProject> collectProjects( File basedir, String[] includes, String[] excludes )
        throws MojoExecutionException, MavenEmbedderException {
        List<MavenProject> projects = new ArrayList<MavenProject>();

        List<File> poms = getPomFiles( basedir, includes, excludes );

        for ( File pom : poms ) {
            try {
                MavenProject p = readProject( pom );

                projects.add( p );

            } catch ( ProjectBuildingException e ) {
                throw new MojoExecutionException( "Error loading " + pom, e );
            }
        }

        return projects;
    }

    // ----------------------------------------------------------------------
    // Artifacts
    // ----------------------------------------------------------------------

    public Artifact createArtifact( String groupId, String artifactId, String version, String scope, String type )
        throws MavenEmbedderException
    {
        try {
            RepositorySystem repositorySystem = lookup( RepositorySystem.class );
            return repositorySystem.createArtifact( groupId, artifactId, version, scope, type );
        } catch ( ComponentLookupException e ) {
            throw new MavenEmbedderException(e.getMessage(), e);
        }

    }

    public Artifact createArtifactWithClassifier( String groupId, String artifactId, String version, String type, String classifier )
        throws MavenEmbedderException
    {
        try {
            RepositorySystem repositorySystem = lookup( RepositorySystem.class );
            return repositorySystem.createArtifactWithClassifier( groupId, artifactId, version, type, classifier );
        } catch ( ComponentLookupException e ) {
            throw new MavenEmbedderException(e.getMessage(), e);
        }
    }

    public void resolve( Artifact artifact, List remoteRepositories, ArtifactRepository localRepository )
        throws ArtifactResolutionException, ArtifactNotFoundException {
        // FIXME ?
    }

    // ----------------------------------------------------------------------
    // Execution of phases/goals
    // ----------------------------------------------------------------------

    public MavenExecutionResult execute( MavenRequest mavenRequest ) throws MavenEmbedderException {
        ClassLoader original = Thread.currentThread().getContextClassLoader();
        try {
            Maven maven = lookup( Maven.class );
            Thread.currentThread().setContextClassLoader( this.plexusContainer.getContainerRealm() );
            return maven.execute( buildMavenExecutionRequest( mavenRequest ) );
        }
        catch ( MavenEmbedderException e )
        {
            throw new MavenEmbedderException(e.getMessage(),e);
        }
        catch ( ComponentLookupException e )
        {
            throw new MavenEmbedderException(e.getMessage(),e);
        }
        finally {
            Thread.currentThread().setContextClassLoader( original );
        }
    }
    // ----------------------------------------------------------------------
    // Local MavenRepository
    // ----------------------------------------------------------------------

    public static final String DEFAULT_LOCAL_REPO_ID = "local";

    public static final String DEFAULT_LAYOUT_ID = "default";

    public ArtifactRepository createLocalRepository( File localRepository ) throws ComponentLookupException {
        return createLocalRepository( localRepository.getAbsolutePath(), DEFAULT_LOCAL_REPO_ID );
    }

    public ArtifactRepository createLocalRepository( Settings settings ) throws ComponentLookupException {
        return createLocalRepository( settings.getLocalRepository(), DEFAULT_LOCAL_REPO_ID );
    }

    public ArtifactRepository createLocalRepository( String url, String repositoryId ) throws ComponentLookupException {
        if ( !url.startsWith( "file:" ) ) {
            url = "file://" + url;
        }

        return createRepository( url, repositoryId );
    }

    public ArtifactRepository createRepository( String url, String repositoryId ) throws ComponentLookupException {
        String updatePolicyFlag = ArtifactRepositoryPolicy.UPDATE_POLICY_ALWAYS;
        String checksumPolicyFlag = ArtifactRepositoryPolicy.CHECKSUM_POLICY_WARN;
        ArtifactRepositoryPolicy snapshotsPolicy = new ArtifactRepositoryPolicy( true, updatePolicyFlag, checksumPolicyFlag );
        ArtifactRepositoryPolicy releasesPolicy = new ArtifactRepositoryPolicy( true, updatePolicyFlag, checksumPolicyFlag );
        RepositorySystem repositorySystem = lookup( RepositorySystem.class );
        ArtifactRepositoryLayout repositoryLayout = lookup( ArtifactRepositoryLayout.class, "default" );
        return repositorySystem.createArtifactRepository( repositoryId, url, repositoryLayout, snapshotsPolicy, releasesPolicy );
    }

    // ----------------------------------------------------------------------
    // Internal utility code
    // ----------------------------------------------------------------------

    private List<File> getPomFiles( File basedir, String[] includes, String[] excludes ) {
        DirectoryScanner scanner = new DirectoryScanner();

        scanner.setBasedir( basedir );

        scanner.setIncludes( includes );

        scanner.setExcludes( excludes );

        scanner.scan();

        List<File> poms = new ArrayList<File>();

        for ( int i = 0; i < scanner.getIncludedFiles().length; i++ ) {
            poms.add( new File( basedir, scanner.getIncludedFiles()[i] ) );
        }

        return poms;
    }

    public <T> T lookup( Class<T> clazz ) throws ComponentLookupException {
        return plexusContainer.lookup( clazz );
    }

    public <T> T lookup( Class<T> clazz, String hint ) throws ComponentLookupException {
        return plexusContainer.lookup( clazz, hint );
    }

    public Object lookup( String role, String hint ) throws ComponentLookupException {
        return plexusContainer.lookup( role, hint );
    }

    public Object lookup( String role ) throws ComponentLookupException {
        return plexusContainer.lookup( role );
    }
    
    private Map<String,String> propertiesToMap(Properties properties) {
        if ( properties == null || properties.isEmpty() ) {
            return new HashMap<String, String>( 0 );
        }
        Map<String, String> result = new HashMap<String, String>( properties.size() );
        for ( Entry<Object, Object> entry : properties.entrySet() ) {
            result.put( (String) entry.getKey(), (String) entry.getValue() );
        }
        return result;
    }

    public MavenRequest getMavenRequest()
    {
        return mavenRequest;
    }
}
