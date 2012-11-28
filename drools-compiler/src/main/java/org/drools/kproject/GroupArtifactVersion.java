package org.drools.kproject;

import org.kie.builder.GAV;

public class GroupArtifactVersion implements GAV {
    private final String groupId;
    private final String artifactId;
    private final String version;

    public GroupArtifactVersion(String groupId,
                                String artifactId,
                                String version) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
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
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GroupArtifactVersion that = (GroupArtifactVersion) o;

        if (artifactId != null ? !artifactId.equals(that.artifactId) : that.artifactId != null) return false;
        if (groupId != null ? !groupId.equals(that.groupId) : that.groupId != null) return false;
        if (version != null ? !version.equals(that.version) : that.version != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = groupId != null ? groupId.hashCode() : 0;
        result = 31 * result + (artifactId != null ? artifactId.hashCode() : 0);
        result = 31 * result + (version != null ? version.hashCode() : 0);
        return result;
    }
}