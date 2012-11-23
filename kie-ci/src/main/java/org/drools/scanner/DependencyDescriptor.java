package org.drools.scanner;

import org.apache.maven.model.Dependency;
import org.sonatype.aether.artifact.Artifact;

import java.util.Properties;

class DependencyDescriptor {
    private final String groupId;
    private final String artifactId;
    private final String version;
    private final String type;

    public DependencyDescriptor(Dependency dependency, Properties projectProperties) {
        groupId = dependency.getGroupId();
        artifactId = dependency.getArtifactId();
        version = resolve(dependency.getVersion(), projectProperties);
        type = dependency.getType();
    }

    public DependencyDescriptor(Artifact artifact) {
        groupId = artifact.getGroupId();
        artifactId = artifact.getArtifactId();
        version = artifact.isSnapshot() ? artifact.getBaseVersion() : artifact.getVersion();
        type = artifact.getExtension();
    }

    public DependencyDescriptor(String groupId, String artifactId, String version, String type) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.type = type;
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

    public String getType() {
        return type;
    }

    public boolean isFixedVersion() {
        return !isSnapshot() && !version.equals("LATEST") && !version.equals(")");
    }

    public boolean isSnapshot() {
        return version.endsWith("SNAPSHOT");
    }

    public boolean isValid() {
        return version != null;
    }

    private String resolve(String value, Properties projectProperties) {
        if (value == null) {
            return null;
        }
        return value.startsWith("${") ? (String)projectProperties.get(value.substring(2, value.length()-1)) : value;
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
}
