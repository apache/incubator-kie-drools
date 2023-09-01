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
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.DefaultModelReader;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.kie.api.builder.ReleaseId;
import org.kie.util.maven.support.DependencyFilter;
import org.kie.util.maven.support.ReleaseIdImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

public class InJarArtifactResolver extends ArtifactResolver {

    private static final Logger log = LoggerFactory.getLogger(InJarArtifactResolver.class);

    private ClassLoader classLoader;
    private List<URL> jarRepository;
    private List<URL> effectivePoms;
    private PomParser pomParser;


    InJarArtifactResolver(ClassLoader classLoader, ReleaseId releaseId) {
        this.classLoader = classLoader;
        this.jarRepository = new ArrayList<>();
        this.effectivePoms = new ArrayList<>();
        init(releaseId);
    }

    public boolean isLoaded() {
        return pomParser != null;
    }
    // initialize in jar repository
    private void init(ReleaseId releaseId) {
        jarRepository = buildResources(name -> isInJarFolder(name, "jar"));
        effectivePoms = buildResources(name -> isInJarFolder(name, "pom"));
        pomParser = buildPomParser(releaseId);
    }

    private List<URL> buildResources(Predicate<String> predicate) {
        URL resourceURL = this.classLoader.getResource("");
        if (resourceURL == null) {
            return emptyList();
        }
        List<URL> resources = new ArrayList<>();
        try (InputStream is = resourceURL.openStream();
                ZipInputStream stream = new ZipInputStream(is);) {

            ZipEntry entry;
            while ((entry = stream.getNextEntry()) != null) {
                if (predicate.test(entry.getName())) {
                    resources.add(classLoader.getResource(entry.getName()));
                }
            }

            log.debug("Found in jar repository {}", resources);
        } catch (IOException e) {
            log.error("Error trying to open URL: {}", resourceURL);
        }
        return resources;
    }

    private boolean isInJarFolder(String name, String type) {
        String[] paths = new String[]{"BOOT-INF/classes/KIE-INF/", "KIE-INF/lib/"};
        for (String path : paths) {
            if (name.startsWith(path) && name.endsWith("." + type)) {
                return true;
            }
        }
        return false;
    }

    private PomParser buildPomParser(ReleaseId releaseId) {
        List<URL> url = effectivePoms.stream().filter(e -> e.getFile().endsWith(toFile(releaseId, "pom"))).collect(toList());
        if (url.isEmpty()) {
            return null;
        }
        String path = url.get(0).toExternalForm();
        URL pomFile = classLoader.getResource(path);
        if (pomFile == null) {
            log.warn("Maven pom not found in path {}", path);
            return null;
        }
        try (InputStream pomStream = pomFile.openStream()) {
            DefaultModelReader reader = new DefaultModelReader();
            Model model = reader.read(pomStream, Collections.emptyMap());
            // dependencies were resolved already creating the effective pom during kjar creation
            return new PomParser() {

                @Override
                public List<DependencyDescriptor> getPomDirectDependencies(DependencyFilter filter) {
                    List<DependencyDescriptor> deps = new ArrayList<>();
                    for (Dependency dep : model.getDependencies()) {
                        DependencyDescriptor depDescr = new DependencyDescriptor(dep);
                        if (depDescr.isValid() && filter.accept(depDescr.getReleaseId(), depDescr.getScope())) {
                            deps.add(depDescr);
                        }
                    }
                    return deps;
                }
                
            };

        } catch (Exception e) {
            log.error("Could not read pom in jar {}", pomFile);
            return null;
        }

    }


    @Override
    public ArtifactLocation resolveArtifactLocation(ReleaseId releaseId) {
        log.debug("resolve location {}", releaseId);
        Optional<URL> url = tryInJar(releaseId);
        if (url.isPresent()) {
            DefaultArtifact artifact = new DefaultArtifact(releaseId.toExternalForm());
            return new ArtifactLocation(artifact.setFile(new File(url.get().toString())), url.get(), true);
        }
        return null;

    }

    @Override
    public Artifact resolveArtifact(ReleaseId releaseId) {
        Optional<URL> url = tryInJar(releaseId);
        if (url.isPresent()) {
            log.info("Resolved in jar repository {}", url);
            DefaultArtifact artifact = new DefaultArtifact(releaseId.toExternalForm());
            return artifact.setFile(new File(url.get().toString()));
        }
        return null;
    }

    private Optional<URL> tryInJar(String artifactName) {
        for (URL inJarURL : jarRepository) {
            if (inJarURL.getFile().endsWith(artifactName)) {
                return Optional.of(inJarURL);
            }
        }
        return Optional.empty();
    }

    private Optional<URL> tryInJar(ReleaseId releaseId) {
        return tryInJar(toFile(releaseId, "jar"));
    }

    private String toFile(ReleaseId releaseId, String type) {
        return releaseId.getArtifactId() + "-" + releaseId.getVersion() + "." + type;
    }

    @Override
    public List<DependencyDescriptor> getArtifactDependecies(String artifactName) {
        ReleaseId releaseId = new ReleaseIdImpl(artifactName);
        PomParser pomParser = buildPomParser(releaseId);
        return pomParser != null ? pomParser.getPomDirectDependencies(DependencyFilter.COMPILE_FILTER) : emptyList();
    }

    @Override
    public List<DependencyDescriptor> getPomDirectDependencies(DependencyFilter dependencyFilter) {
        return (pomParser != null) ? pomParser.getPomDirectDependencies(dependencyFilter) : emptyList();
    }

}
