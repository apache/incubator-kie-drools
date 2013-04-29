package org.jbpm.kie.services.impl;

import org.jbpm.kie.services.api.DeploymentUnit;

public class KModuleDeploymentUnit implements DeploymentUnit {

    private String artifactId;
    private String groupId;
    private String version;
    private String kbaseName;
    private String ksessionName;
    
    private RuntimeStrategy strategy = RuntimeStrategy.SINGLETON;
    
    
    public KModuleDeploymentUnit(String groupId, String artifactId, String version) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
    }

    public KModuleDeploymentUnit(String groupId, String artifactId, 
            String version, String kbaseName, String ksessionName) {
        this.artifactId = artifactId;
        this.groupId = groupId;
        this.version = version;
        this.kbaseName = kbaseName;
        this.ksessionName = ksessionName;
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

    public String getKsessionName() {
        return ksessionName;
    }

    public void setKsessionName(String ksessionName) {
        this.ksessionName = ksessionName;
    }

    public String getKbaseName() {
        return kbaseName;
    }

    public void setKbaseName(String kbaseName) {
        this.kbaseName = kbaseName;
    }

}
