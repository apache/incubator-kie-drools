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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.maven.project.MavenProject;
import org.eclipse.aether.artifact.Artifact;
import org.kie.api.builder.ReleaseId;
import org.kie.util.maven.support.DependencyFilter;
import org.kie.util.maven.support.MinimalPomParser;
import org.kie.util.maven.support.PomModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.maven.integration.embedder.MavenProjectLoader.parseMavenPom;

public abstract class ArtifactResolver {

    private static final Logger log = LoggerFactory.getLogger(ArtifactResolver.class);

    public static ArtifactResolver getResolverFor(ClassLoader classLoader, ReleaseId releaseId, boolean allowDefaultPom) {
        InJarArtifactResolver inJarResolver = new InJarArtifactResolver(classLoader, releaseId);
        if (inJarResolver.isLoaded()) {
            return inJarResolver;
        }

        ArtifactResolver resolver = getResolverFor(releaseId, allowDefaultPom);
        if (resolver != null) {
            return resolver;
        }

        return null;
    }

    public static ArtifactResolver getResolverFor(ReleaseId releaseId, boolean allowDefaultPom) {
        File pomFile = getPomFileForGAV( releaseId, allowDefaultPom );
        if (pomFile != null) {
            ArtifactResolver artifactResolver = getResolverFor(pomFile);
            if (artifactResolver != null) {
                return artifactResolver;
            }
        }
        return allowDefaultPom ? new DefaultArtifactResolver() : null;
    }

    public static ArtifactResolver getResolverFor(URI uri) {
        return getResolverFor(new File(uri));
    }

    public static ArtifactResolver getResolverFor(File pomFile) {
        try {
            return new DefaultArtifactResolver(parseMavenPom(pomFile));
        } catch (RuntimeException e) {
            log.warn("Cannot use native maven pom parser, fall back to the internal one", e);
            PomParser pomParser = createInternalPomParser(pomFile);
            if (pomParser != null) {
                return new DefaultArtifactResolver(pomParser);
            }
        }
        return null;
    }

    public static ArtifactResolver getResolverFor(InputStream pomStream) {
        MavenProject mavenProject = parseMavenPom(pomStream);
        return new DefaultArtifactResolver(mavenProject);
    }

    private static File getPomFileForGAV(ReleaseId releaseId, boolean allowDefaultPom) {
        String artifactName = releaseId.getGroupId() + ":" + releaseId.getArtifactId() + ":pom:" + releaseId.getVersion();
        Artifact artifact = MavenRepository.getMavenRepository().resolveArtifact(artifactName, !allowDefaultPom);
        return artifact != null ? artifact.getFile() : null;
    }

    public static ArtifactResolver getResolverFor(InputStream pomStream, ReleaseId releaseId, boolean allowDefaultPom ) {
        if (pomStream != null) {
            ArtifactResolver artifactResolver = getResolverFor(pomStream);
            return artifactResolver;
        }
        return getResolverFor(releaseId, allowDefaultPom);
    }

    public static ArtifactResolver getResolverFor(PomModel pomModel ) {
        return pomModel instanceof MavenPomModelGenerator.MavenModel ?
                new DefaultArtifactResolver(((MavenPomModelGenerator.MavenModel) pomModel).getMavenProject()) : new DefaultArtifactResolver();
    }

    private static InternalPomParser createInternalPomParser(File pomFile) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(pomFile);
            return new InternalPomParser(MinimalPomParser.parse(pomFile.getAbsolutePath(), fis));
        } catch (FileNotFoundException e) {
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    log.error("Cannot create internal pom parser", e);
                }
            }
        }
        return null;
    }

    private static class InternalPomParser implements PomParser {

        private final PomModel pomModel;

        private InternalPomParser(PomModel pomModel) {
            this.pomModel = pomModel;
        }

        @Override
        public List<DependencyDescriptor> getPomDirectDependencies(DependencyFilter filter) {
            List<DependencyDescriptor> deps = new ArrayList<>();
            for (ReleaseId rId : pomModel.getDependencies(filter)) {
                deps.add(new DependencyDescriptor(rId));
            }
            return deps;
        }
    }

    public static ArtifactResolver create() {
        return new DefaultArtifactResolver();
    }

    public Collection<DependencyDescriptor> getAllDependecies(DependencyFilter dependencyFilter) {
        Set<DependencyDescriptor> dependencies = new HashSet<>();
        for (DependencyDescriptor dep : getPomDirectDependencies(dependencyFilter)) {
            dependencies.add(dep);
            for (DependencyDescriptor transitiveDep : getArtifactDependecies(dep.toString())) {
                if (dependencyFilter.accept(dep.getReleaseId(), dep.getScope())) {
                    dependencies.add(transitiveDep);
                }
            }
        }
        return dependencies;
    }

    public Collection<DependencyDescriptor> getAllDependecies() {
        return getAllDependecies((releaseId, scope) -> true);
    }

    public abstract List<DependencyDescriptor> getPomDirectDependencies(DependencyFilter dependencyFilter);



    public abstract Artifact resolveArtifact(ReleaseId releaseId);

    public abstract List<DependencyDescriptor> getArtifactDependecies(String artifactName);

    public static class ArtifactLocation {

        private boolean classPath;
        private URL url;
        private Artifact artifact;

        public ArtifactLocation(Artifact artifact, URL url, boolean classPath) {
            this.url = url;
            this.artifact = artifact;
            this.classPath = classPath;
        }

        public Artifact getArtifact() {
            return artifact;
        }


        public URL toURL() {
            return url;
        }

        public boolean isClassPath() {
            return classPath;
        }

    }

    public abstract ArtifactLocation resolveArtifactLocation(ReleaseId releaseId);
}
