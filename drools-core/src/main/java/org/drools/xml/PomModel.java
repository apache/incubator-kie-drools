package org.drools.xml;

import org.drools.core.util.StringUtils;

public class PomModel {
    private String parentGroupId;
    private String parentArtifactId;
    private String parentVersion;
    
    private String groupId;
    private String artifactId;
    private String version;
    
    public String getParentGroupId() {
        return parentGroupId;
    }
    
    public void setParentGroupId(String parentGroupId) {
        this.parentGroupId = parentGroupId;
    }
    
    public String getParentArtifactId() {
        return parentArtifactId;
    }
    
    public void setParentArtifactId(String parentArtifactId) {
        this.parentArtifactId = parentArtifactId;
    }
    
    public String getParentVersion() {
        return parentVersion;
    }
    
    public void setParentVersion(String parentVersion) {
        this.parentVersion = parentVersion;
    }
    
    public String getGroupId() {
        if ( StringUtils.isEmpty( groupId )) {
            return parentGroupId;
        }        
        return groupId;
    }
    
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
    
    public String getArtifactId() {
        return artifactId;
    }
    
    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }
    
    public String getVersion() {
        if ( StringUtils.isEmpty( version )) {
            return parentVersion;
        }
        return version;
    }
    
    public void setVersion(String version) {
        this.version = version;
    }
    
    
}
