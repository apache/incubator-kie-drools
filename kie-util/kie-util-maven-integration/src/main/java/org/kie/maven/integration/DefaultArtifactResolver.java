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

import java.net.MalformedURLException;
import java.util.List;

import org.apache.maven.project.MavenProject;
import org.eclipse.aether.artifact.Artifact;
import org.kie.api.builder.ReleaseId;
import org.kie.maven.integration.embedder.EmbeddedPomParser;
import org.kie.util.maven.support.DependencyFilter;

public class DefaultArtifactResolver extends ArtifactResolver {

    private final MavenRepository mavenRepository;

    protected final PomParser pomParser;

    DefaultArtifactResolver() {
        this.pomParser = new EmbeddedPomParser();
        this.mavenRepository = MavenRepository.getMavenRepository();
    }

    DefaultArtifactResolver(MavenProject mavenProject) {
        this.pomParser = new EmbeddedPomParser(mavenProject);
        this.mavenRepository = MavenRepository.getMavenRepository(mavenProject);
    }

    DefaultArtifactResolver(PomParser pomParser) {
        this.pomParser = pomParser;
        this.mavenRepository = MavenRepository.getMavenRepository();
    }

    public Artifact resolveArtifact(ReleaseId releaseId) {
        return mavenRepository.resolveArtifact(releaseId);
    }

    public List<DependencyDescriptor> getArtifactDependecies(String artifactName) {
        return mavenRepository.getArtifactDependecies(artifactName);
    }

    public List<DependencyDescriptor> getPomDirectDependencies(DependencyFilter dependencyFilter) {
        return pomParser.getPomDirectDependencies(dependencyFilter);
    }

    @Override
    public ArtifactLocation resolveArtifactLocation(ReleaseId releaseId) {
        try {
            Artifact artifact = resolveArtifact(releaseId);
            if (artifact == null) {
                return null;
            }
            return new ArtifactLocation(artifact, artifact.getFile().toURL(), false);
        } catch (MalformedURLException e) {
            return null;
        }
    }
}
