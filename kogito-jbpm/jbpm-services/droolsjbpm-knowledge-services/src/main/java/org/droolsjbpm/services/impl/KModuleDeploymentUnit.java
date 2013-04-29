package org.droolsjbpm.services.impl;

import org.droolsjbpm.services.api.DeploymentUnit;

public class KModuleDeploymentUnit implements DeploymentUnit {

    private String artifactId;
    private String groupId;
    private String version;
    
    private RuntimeStrategy strategy = RuntimeStrategy.SINGLETON;
    
    
    public KModuleDeploymentUnit(String artifactId, String groupId, String version) {
        this.artifactId = artifactId;
        this.groupId = groupId;
        this.version = version;
    }

    @Override
    public String getIdentifier() {
        return getGroupId()+":"+getArtifactId()+":"+getVersion();
    }

    @Override
    public RuntimeStrategy getStrategy() {
        return strategy;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

}
