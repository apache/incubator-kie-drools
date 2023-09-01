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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Dependency;
import org.apache.maven.project.MavenProject;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;
import org.kie.api.builder.ReleaseId;
import org.kie.util.maven.support.ReleaseIdImpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ProjectPomModelTest {

    private static final String GROUP_ID = "org.kie.test.groupid";
    private static final String ARTIFACT_ID = "test-artifact";
    private static final String PARENT_ARTIFACT_ID = "parent-artifact";
    private static final String VERSION = "1.0";
    private static final String PACKAGING = "kjar";

    private static final String DEPENDENCY_GROUP_ID = "org.kie.test.dependency.groupid";
    private static final String DEPENDENCY_ARTIFACT_ID1 = "dependency-artifact1";
    private static final String DEPENDENCY_ARTIFACT_ID2 = "dependency-artifact2";
    private static final String DEPENDENCY_VERSION = "1.0";
    private static final String DEPENDENCY_PACKAGING = "jar";
    private static final String DEPENDENCY_SCOPE1 = "compile";
    private static final String DEPENDENCY_SCOPE2 = "test";

    @Test
    public void getReleaseId() {
        final MavenSession mavenSession = mockMavenSession(false);
        final ProjectPomModel pomModel = new ProjectPomModel(mavenSession);
        final ReleaseId releaseId = pomModel.getReleaseId();
        assertReleaseId(releaseId, ARTIFACT_ID);
    }

    @Test
    public void getParentReleaseId() {
        final MavenSession mavenSession = mockMavenSession(true);
        final ProjectPomModel pomModel = new ProjectPomModel(mavenSession);
        final ReleaseId releaseId = pomModel.getParentReleaseId();
        assertReleaseId(releaseId, PARENT_ARTIFACT_ID);
    }

    @Test
    public void getParentReleaseIdNull() {
        final MavenSession mavenSession = mockMavenSession(false);
        final ProjectPomModel pomModel = new ProjectPomModel(mavenSession);
        assertThat(pomModel.getParentReleaseId()).isNull();
    }

    @Test
    public void getDependencies() {
        final MavenSession mavenSession = mockMavenSession(false);
        mockDependencies(mavenSession.getCurrentProject());
        final ProjectPomModel pomModel = new ProjectPomModel(mavenSession);
        final Collection<ReleaseId> dependencies = pomModel.getDependencies();
        assertThat(dependencies).isNotNull().hasSize(2);
        assertThat(dependencies).areExactly(1, new Condition<>(
                releaseId -> releaseId instanceof ReleaseIdImpl
                        && DEPENDENCY_GROUP_ID.equals(releaseId.getGroupId())
                        && DEPENDENCY_ARTIFACT_ID1.equals(releaseId.getArtifactId())
                        && DEPENDENCY_VERSION.equals(releaseId.getVersion())
                        && DEPENDENCY_PACKAGING.equals(((ReleaseIdImpl) releaseId).getType()),
                "Is dependency 1"));
        assertThat(dependencies).areExactly(1, new Condition<>(
                releaseId -> releaseId instanceof ReleaseIdImpl
                        && DEPENDENCY_GROUP_ID.equals(releaseId.getGroupId())
                        && DEPENDENCY_ARTIFACT_ID2.equals(releaseId.getArtifactId())
                        && DEPENDENCY_VERSION.equals(releaseId.getVersion())
                        && DEPENDENCY_PACKAGING.equals(((ReleaseIdImpl) releaseId).getType()),
                "Is dependency 2"));
    }

    @Test
    public void getDependenciesEmpty() {
        final MavenSession mavenSession = mockMavenSession(false);
        final ProjectPomModel pomModel = new ProjectPomModel(mavenSession);
        final Collection<ReleaseId> dependencies = pomModel.getDependencies();
        assertThat(dependencies).isNotNull().isEmpty();
    }

    @Test
    public void getDependenciesWithFilter() {
        final MavenSession mavenSession = mockMavenSession(false);
        mockDependencies(mavenSession.getCurrentProject());
        final ProjectPomModel pomModel = new ProjectPomModel(mavenSession);
        final Collection<ReleaseId> dependencies = pomModel.getDependencies(
                (releaseId, scope) -> DEPENDENCY_SCOPE1.equals(scope));
        assertThat(dependencies).isNotNull().hasSize(1);
        assertThat(dependencies).areExactly(1, new Condition<>(
                releaseId -> releaseId instanceof ReleaseIdImpl
                        && DEPENDENCY_GROUP_ID.equals(releaseId.getGroupId())
                        && DEPENDENCY_ARTIFACT_ID1.equals(releaseId.getArtifactId())
                        && DEPENDENCY_VERSION.equals(releaseId.getVersion())
                        && DEPENDENCY_PACKAGING.equals(((ReleaseIdImpl) releaseId).getType()),
                "Is dependency 1"));
    }

    private MavenSession mockMavenSession(final boolean addParentProject) {
        final MavenSession mavenSession = mock(MavenSession.class);
        final MavenProject currentProject = mockMavenProject(ARTIFACT_ID);
        if (addParentProject) {
            final MavenProject parentProject = mockMavenProject(PARENT_ARTIFACT_ID);
            when(currentProject.getParent()).thenReturn(parentProject);
        }

        when(mavenSession.getCurrentProject()).thenReturn(currentProject);

        return mavenSession;
    }

    private MavenProject mockMavenProject(final String artifactId) {
        final MavenProject mavenProject = mock(MavenProject.class);
        when(mavenProject.getGroupId()).thenReturn(GROUP_ID);
        when(mavenProject.getArtifactId()).thenReturn(artifactId);
        when(mavenProject.getVersion()).thenReturn(VERSION);
        when(mavenProject.getPackaging()).thenReturn(PACKAGING);
        return mavenProject;
    }

    private void mockDependencies(final MavenProject mavenProject) {
        final List<Dependency> dependencies = new ArrayList<>();
        dependencies.add(mockDependency(DEPENDENCY_ARTIFACT_ID1, DEPENDENCY_SCOPE1));
        dependencies.add(mockDependency(DEPENDENCY_ARTIFACT_ID2, DEPENDENCY_SCOPE2));
        when(mavenProject.getDependencies()).thenReturn(dependencies);
    }

    private Dependency mockDependency(final String artifactId, final String scope) {
        final Dependency dependency = mock(Dependency.class);
        when(dependency.getGroupId()).thenReturn(DEPENDENCY_GROUP_ID);
        when(dependency.getArtifactId()).thenReturn(artifactId);
        when(dependency.getVersion()).thenReturn(VERSION);
        when(dependency.getType()).thenReturn(DEPENDENCY_PACKAGING);
        when(dependency.getScope()).thenReturn(scope);
        return dependency;
    }

    private void assertReleaseId(final ReleaseId releaseId, final String artifactId) {
        assertThat(releaseId).isNotNull().isInstanceOf(ReleaseIdImpl.class);
        assertThat(releaseId.getGroupId()).isEqualTo(GROUP_ID);
        assertThat(releaseId.getArtifactId()).isEqualTo(artifactId);
        assertThat(releaseId.getVersion()).isEqualTo(VERSION);
        assertThat(((ReleaseIdImpl) releaseId).getType()).isEqualTo(PACKAGING);
    }
}