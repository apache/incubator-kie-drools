/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.scanner;

import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.model.Dependency;
import org.drools.compiler.kproject.ReleaseIdImpl;
import org.kie.api.builder.ReleaseId;
import org.eclipse.aether.artifact.Artifact;

public class DependencyDescriptor {
    private final String groupId;
    private final String artifactId;
    private final String version;
    private final String type;
    private final String scope;

    private ArtifactVersion artifactVersion;

    private long artifactTimestamp = 0L;

    public DependencyDescriptor(Dependency dependency) {
        this(dependency.getGroupId(),
             dependency.getArtifactId(),
             dependency.getVersion(),
             dependency.getType(),
             dependency.getVersion(),
             dependency.getScope());
    }

    public DependencyDescriptor(Artifact artifact) {
        this(artifact.getGroupId(),
             artifact.getArtifactId(),
             artifact.isSnapshot() ? artifact.getBaseVersion() : artifact.getVersion(),
             artifact.getExtension(),
             artifact.getVersion(),
             "");
        if (artifact.getFile() != null) {
            artifactTimestamp = artifact.getFile().lastModified();
        }
    }

    public DependencyDescriptor(ReleaseId releaseId) {
        this(releaseId.getGroupId(),
             releaseId.getArtifactId(),
             releaseId.getVersion(),
             "jar",
             releaseId.getVersion(),
             "");
    }

    public DependencyDescriptor(ReleaseId releaseId, long artifactTimestamp) {
        this(releaseId);
        this.artifactTimestamp = artifactTimestamp;
    }

    private DependencyDescriptor(String groupId, String artifactId, String version, String type, String currentVersion, String scope) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.type = type;
        this.scope = scope;
        setArtifactVersion(currentVersion);
    }

    public String getGroupId() {
        return groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public String getVersion() {
        return version;
    }

    public ReleaseId getReleaseIdWithoutVersion() {
        return new ReleaseIdImpl(groupId, artifactId, "0");
    }

    public ReleaseId getReleaseId() {
        return new ReleaseIdImpl(groupId, artifactId, version);
    }

    public ReleaseId getArtifactReleaseId() {
        return new ReleaseIdImpl(groupId, artifactId, artifactVersion.toString());
    }

    public String getType() {
        return type;
    }

    public String getScope() {
        return scope;
    }

    public boolean isFixedVersion() {
        return isFixedVersion(version);
    }

    public static boolean isFixedVersion(String version) {
        return !isSnapshot(version) && !isRangedVersion(version)
               && !version.equals("LATEST") && !version.equals("RELEASE");
    }

    public static boolean isRangedVersion(String version) {
        return version.indexOf('(') >= 0 || version.indexOf(')') >= 0 ||
               version.indexOf('[') >= 0 || version.indexOf(']') >= 0;
    }

    public boolean isSameArtifact(ReleaseId releaseId) {
        return groupId.equals(releaseId.getGroupId()) && artifactId.equals(releaseId.getArtifactId());
    }

    public boolean isSnapshot() {
        return isSnapshot(version);
    }

    public static boolean isSnapshot(String version) {
        return version.endsWith("SNAPSHOT");
    }

    public boolean isValid() {
        return version != null;
    }

    @Override
    public String toString() {
        return groupId + ":" + artifactId + ":" + version;
    }

    public String toResolvableString() {
        return isSnapshot() ? toString() : groupId + ":" + artifactId + ":LATEST";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DependencyDescriptor that = (DependencyDescriptor) o;

        if (artifactId != null ? !artifactId.equals(that.artifactId) : that.artifactId != null) return false;
        if (groupId != null ? !groupId.equals(that.groupId) : that.groupId != null) return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        if (version != null ? !version.equals(that.version) : that.version != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = groupId != null ? groupId.hashCode() : 0;
        result = 31 * result + (artifactId != null ? artifactId.hashCode() : 0);
        result = 31 * result + (version != null ? version.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }

    public void setArtifactVersion(String version) {
        artifactVersion = new DefaultArtifactVersion(version);
    }

    public boolean isNewerThan(DependencyDescriptor o) {
        int comparison = artifactVersion.compareTo(o.artifactVersion);
        return comparison > 0 || ( comparison == 0 && artifactTimestamp > o.artifactTimestamp );
    }
}
