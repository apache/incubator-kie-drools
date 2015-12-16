/*
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
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
import org.apache.maven.execution.MavenExecutionRequest;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.eclipse.aether.RepositorySystemSession;

import java.io.File;

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
