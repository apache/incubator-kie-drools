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
package org.kie.maven.plugin;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Dependency;
import org.apache.maven.project.MavenProject;
import org.kie.api.builder.ReleaseId;
import org.kie.util.maven.support.DependencyFilter;
import org.kie.util.maven.support.PomModel;
import org.kie.util.maven.support.ReleaseIdImpl;

public class ProjectPomModel implements PomModel {

    private final ReleaseId releaseId;
    private final ReleaseId parentReleaseId;
    private final Map<String, Set<ReleaseId>> dependenciesByScope;

    public ProjectPomModel(final MavenSession mavenSession) {
        this.releaseId = getReleaseIdFromMavenProject(mavenSession.getCurrentProject());
        final MavenProject parentProject = mavenSession.getCurrentProject().getParent();
        if (parentProject != null) {
            this.parentReleaseId = getReleaseIdFromMavenProject(parentProject);
        } else {
            this.parentReleaseId = null;
        }
        this.dependenciesByScope = getDirectDependenciesFromMavenSession(mavenSession);
    }

    @Override
    public ReleaseId getReleaseId() {
        return releaseId;
    }

    @Override
    public ReleaseId getParentReleaseId() {
        return parentReleaseId;
    }

    @Override
    public Collection<ReleaseId> getDependencies() {
        return dependenciesByScope.values()
                .stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ReleaseId> getDependencies(final DependencyFilter filter) {
        final Set<ReleaseId> filteredDependencies = new HashSet<>();
        for (Map.Entry<String, Set<ReleaseId>> entry : dependenciesByScope.entrySet()) {
            for (ReleaseId dependencyReleaseId : entry.getValue()) {
                if (filter.accept(dependencyReleaseId, entry.getKey())) {
                    filteredDependencies.add(dependencyReleaseId);
                }
            }
        }
        return filteredDependencies;
    }

    private ReleaseId getReleaseIdFromMavenProject(final MavenProject mavenProject) {
        return new ReleaseIdImpl(mavenProject.getGroupId(),
                                 mavenProject.getArtifactId(),
                                 mavenProject.getVersion(),
                                 mavenProject.getPackaging());
    }

    private ReleaseId getReleaseIdFromDependency(final Dependency dependency) {
        return new ReleaseIdImpl(dependency.getGroupId(),
                             dependency.getArtifactId(),
                             dependency.getVersion(),
                             dependency.getType());
    }

    private Map<String, Set<ReleaseId>> getDirectDependenciesFromMavenSession(final MavenSession mavenSession) {
        final List<Dependency> dependencies = mavenSession.getCurrentProject().getDependencies();
        final Map<String, Set<ReleaseId>> result = new HashMap<>();
        for (Dependency dependency : dependencies) {
            final Set<ReleaseId> scopeDependencies = result.computeIfAbsent(dependency.getScope(), s -> new HashSet<>());
            scopeDependencies.add(getReleaseIdFromDependency(dependency));
        }
        return result;
    }
}
