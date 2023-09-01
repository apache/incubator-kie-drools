package org.kie.maven.integration.embedder;

import java.io.File;

import org.apache.maven.DefaultMaven;
import org.apache.maven.Maven;
import org.apache.maven.execution.MavenExecutionRequest;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.eclipse.aether.RepositorySystemSession;

public class PlexusComponentProvider implements ComponentProvider {

    private final PlexusContainer plexusContainer;

    public PlexusComponentProvider(File mavenHome, MavenRequest mavenRequest) throws MavenEmbedderException {
        plexusContainer = MavenEmbedderUtils.buildPlexusContainer(mavenHome, mavenRequest);
    }

    public PlexusComponentProvider(ClassLoader mavenClassLoader, ClassLoader parent, MavenRequest mavenRequest) throws MavenEmbedderException {
        plexusContainer = MavenEmbedderUtils.buildPlexusContainer(mavenClassLoader, parent, mavenRequest);
    }

    @Override
    public <T> T lookup( Class<T> clazz ) throws ComponentLookupException {
        return plexusContainer.lookup( clazz );
    }

    @Override
    public RepositorySystemSession getRepositorySystemSession(MavenExecutionRequest mavenExecutionRequest) throws ComponentLookupException {
        DefaultMaven defaultMaven = (DefaultMaven) lookup( Maven.class );
        return defaultMaven.newRepositorySession( mavenExecutionRequest );
    }

    @Override
    public PlexusContainer getPlexusContainer() {
        return plexusContainer;
    }

    @Override
    public ClassLoader getSystemClassLoader() {
        return plexusContainer.getContainerRealm();
    }
}
