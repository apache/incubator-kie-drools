package org.kie.maven.integration.embedder;

import org.apache.maven.execution.MavenExecutionRequest;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.eclipse.aether.RepositorySystemSession;

public interface ComponentProvider {
    <T> T lookup( Class<T> clazz ) throws ComponentLookupException;

    RepositorySystemSession getRepositorySystemSession(MavenExecutionRequest mavenExecutionRequest) throws ComponentLookupException;

    PlexusContainer getPlexusContainer();

    ClassLoader getSystemClassLoader();
}